/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.index.diff.impl;

import static com.b2international.commons.StringUtils.EMPTY_STRING;
import static com.b2international.commons.collect.ByteCollections.filter;
import static com.b2international.commons.collect.ByteCollections.getLast;
import static com.b2international.commons.collect.LongSets.forEach;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.hash.Hashing.murmur3_32;
import static java.text.MessageFormat.format;
import static org.apache.lucene.index.DirectoryReader.open;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SegmentReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.bytes.ByteList;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.collect.ByteCollections.BytePredicate;
import com.b2international.commons.collect.LongSets;
import com.b2international.snowowl.index.diff.IndexDiff;
import com.b2international.snowowl.index.diff.IndexDiffException;
import com.b2international.snowowl.index.diff.IndexDiffer;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;

/**
 * Index differ implementation to compare index differences between index commits.
 *
 */
public class IndexDifferImpl implements IndexDiffer {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexDifferImpl.class);
	
	private static final String LOG_TEMPLATE = "Registered index change. ID: {} {} Segment name: {}";
	private static final String SEGMENT_INFO_EXTENSION = ".si";
	private static final String DEFAULT_INDEX_COMPARE_FIELD = "component_compare_unique_key";
	private static final String DEFAULT_IGNORE_INDEX_COMPARE_KEY = "component_ignore_compare_unique_key";
	
	private static final int INTERSECTION = 1 << 0;
	private static final int RELEVANT = 1 << 1;
	private static final int LIVE_IN_SOURCE = 1 << 2; 
	private static final int LIVE_IN_TARGET = 1 << 3;
	
	@Override
	public IndexDiff calculateDiff(final IndexCommit sourceCommit, final IndexCommit targetCommit) {
		
		checkNotNull(sourceCommit, "sourceCommit");
		checkNotNull(targetCommit, "targetCommit");
		
		final byte[] spare = new byte[1];		
		final LongKeyMap<ByteList> indexChanges = PrimitiveMaps.newLongKeyOpenHashMap(murmur3_32());
		final LongSet newIds = PrimitiveSets.newLongOpenHashSet(murmur3_32());
		final LongSet changedIds = PrimitiveSets.newLongOpenHashSet(murmur3_32());
		final LongSet detachedIds = PrimitiveSets.newLongOpenHashSet(murmur3_32());
		
		final Stopwatch stopwatch = Stopwatch.createStarted();
		
		DirectoryReader sourceReader = null;
		DirectoryReader targetReader = null;
		
		try {
			
			sourceReader = open(sourceCommit);
			targetReader = open(targetCommit); 
			
			final Collection<String> intersectionSegmentFileNames = getIntersectionSegmentFileNames(sourceCommit, targetCommit);
			final Map<String, SegmentReader> sourceReaders = createSourceReadersMap(sourceReader);
			
			for (final LeafReaderContext context : targetReader.leaves()) {
				
				final SegmentReader segmentReader = (SegmentReader) context.reader();
				final NumericDocValues docValues = getCompareDocValues(context);
				
				if (null == docValues) {
					continue; //no compare related changes in the current segment
				}
				
				final boolean intersection = intersectionSegmentFileNames.contains(segmentReader.getSegmentName());
				
				for (int docId = 0; docId < segmentReader.maxDoc(); docId++) {
					
					long id = docValues.get(docId);
					
					if (0 != id) {

						final String segmentName = segmentReader.getSegmentName();
						final boolean liveInTarget = isLiveInReader(segmentReader, docId);
						final boolean liveInSource = null == sourceReaders.get(segmentName) ? false : isLiveInReader(sourceReaders.get(segmentName), docId);
						final boolean relevant = id > 0;
						
						if (!relevant) {
							id = getIgnoreCompareDocValues(context).get(docId);
						}

						final byte indexChangeFlag = createIndexChangeFlag(intersection, relevant, liveInSource, liveInTarget);
						registerIndexChange(id, indexChangeFlag, indexChanges, spare);
						trace(LOG_TEMPLATE, id, toString(indexChangeFlag), segmentName);
						
					}
					
				}
				
			}
			
			forEach(indexChanges.keySet(), new LongSets.LongCollectionProcedure() {
				
				@Override
				public void apply(final long id) {

					final ByteList allChanges = indexChanges.get(id);
					final ByteList intersectionChanges = PrimitiveLists.newByteArrayListWithExpectedSize(allChanges.size());
					final ByteList differenceChanges = PrimitiveLists.newByteArrayList(filter(allChanges, new BytePredicate() {
						@Override public boolean apply(final byte input) {
							final boolean inIntersection = isIntersection(input);
							if (inIntersection) {
								intersectionChanges.add(input);
							}
							return !inIntersection;
						}
					}));

					final boolean notChangedInIntersection = differenceChanges.size() == allChanges.size();
					final byte lastIndexChange = getLast(allChanges);
					
					if (notChangedInIntersection) {
						
						for (int i = 0; i < differenceChanges.size(); i++) {
							final byte diffIndexChange = differenceChanges.get(i);
							if (isRelevant(diffIndexChange)) {
								if (isLiveInTarget(lastIndexChange)) {
									newIds.add(id);
									break;
								}
							}
						}
						
					} else {
						
						final boolean hasOutOfIntersectionChanges = CompareUtils.isEmpty(differenceChanges);
						if (hasOutOfIntersectionChanges) {

							for (int i = 0; i < allChanges.size(); i++) {
								final byte indexChange = allChanges.get(i);
								if (isRelevant(indexChange)) {
									if (!isLiveInTarget(lastIndexChange) && isLiveInSource(lastIndexChange)) {
										detachedIds.add(id);
										break;
									}
								}
							}
							
						} else {

							for (int i = 0; i < differenceChanges.size(); i++) {
								final byte diffIndexChange = differenceChanges.get(i);
								if (isRelevant(diffIndexChange)) {
									if (isLiveInTarget(lastIndexChange)) {
										changedIds.add(id);
									} else {
										detachedIds.add(id);
									}
									break;
								} 
							}
						
							final byte lastDifferenceIndexChange = getLast(differenceChanges);
							if (!isLiveInTarget(lastDifferenceIndexChange)) {
								for (int i = (allChanges.size() - 1); i >= 0; i--) {
									final byte indexChange = allChanges.get(i);
									if (isRelevant(indexChange) && (isLiveInSource(indexChange) || !isLiveInTarget(indexChange))) {
										detachedIds.add(id);
									}
								}
							}
							
						}
						
					}
				}
			});
			
			final IndexDiffImpl diff = new IndexDiffImpl(newIds, changedIds, detachedIds);
			
			LOGGER.info(format("Index difference calculation successfully finished. [{0}]", stopwatch));
			
			return diff;
			
		} catch (final IOException e) {
			
			LOGGER.error("Failed to calculate index difference.", e);
			throw new IndexDiffException("Failed to calculate index difference.", e);
			
		} finally {
			
			Exception e = null;
			e = closeReader(sourceReader);
			if (null == e) {
				e = closeReader(targetReader);
			} else {
				e.addSuppressed(closeReader(targetReader));
			}
			
			if (null == e) {
				trace("Index readers successfully closed.");
			} else {
				throw new IndexDiffException("Error while closing index readers.", e);
			}
			
		}
	}

	@Override
	public IndexDiff calculateDiff(final IndexCommit ancestorCommit, final IndexCommit sourceCommit, final IndexCommit targetCommit) {
		
		checkNotNull(ancestorCommit, "ancestorCommit");
		checkNotNull(sourceCommit, "sourceCommit");
		checkNotNull(targetCommit, "targetCommit");
		
		final IndexDiff sourceDiff = calculateDiff(ancestorCommit, sourceCommit);
		final IndexDiff targetDiff = calculateDiff(ancestorCommit, targetCommit);

		return new ThreeWayIndexDiffImpl(sourceDiff, targetDiff);
		
	}
	
	/**Returns with the compare field name.*/
	protected String getCompareField() {
		return DEFAULT_INDEX_COMPARE_FIELD;
	}
	
	/**Returns with the field name for identifying an ignored change in the {@link IndexDiff index difference}.*/
	protected String getIgnoredCompareField() {
		return DEFAULT_IGNORE_INDEX_COMPARE_KEY;
	}
	
	private NumericDocValues getIgnoreCompareDocValues(final LeafReaderContext context) throws IOException {
		return getNumericDocValues(context, getIgnoredCompareField());
	}

	private NumericDocValues getCompareDocValues(final LeafReaderContext context) throws IOException {
		return getNumericDocValues(context, getCompareField());
	}

	private NumericDocValues getNumericDocValues(final LeafReaderContext context, final String field) throws IOException {
		return context.reader().getNumericDocValues(field);
	}

	private Map<String, SegmentReader> createSourceReadersMap(final DirectoryReader sourceReader) {
		return uniqueIndex(transform(sourceReader.leaves(), new Function<LeafReaderContext, SegmentReader>() {
			@Override public SegmentReader apply(final LeafReaderContext context) {
				return (SegmentReader) context.reader();
			}
		}), new Function<SegmentReader, String>() {
			@Override public String apply(final SegmentReader reader) {
				return reader.getSegmentName();
			}
		});
	}

	private void registerIndexChange(final long id, final byte indexChangeFlag, final LongKeyMap<ByteList> indexChanges, final byte[] spare) {
		
		final ByteList indexChangeFlags = indexChanges.get(id);
		if (null == indexChangeFlags) {
			spare[0] = indexChangeFlag;
			indexChanges.put(id, PrimitiveLists.newByteArrayList(spare));
		} else {
			indexChangeFlags.add(indexChangeFlag);
		}
		
	}

	private boolean isLiveInReader(final SegmentReader segmentReader, final int docId) {
		return null == segmentReader.getLiveDocs() || segmentReader.getLiveDocs().get(docId);
	}

	private Collection<String> getIntersectionSegmentFileNames(final IndexCommit sourceCommit, final IndexCommit targetCommit) throws IOException {
		
		final Collection<String> sourceFileNames = sourceCommit.getFileNames();
		final Collection<String> targetFileNames = targetCommit.getFileNames();
		final Collection<String> intersectionFileNames = intersection(newHashSet(sourceFileNames), newHashSet(targetFileNames));
		
		final Collection<String> intersectionSegmentFileNames = newHashSet(transform(filter(intersectionFileNames, new Predicate<String>() {
			@Override public boolean apply(final String fileName) {
				return fileName.endsWith(SEGMENT_INFO_EXTENSION);
			}
		}), new Function<String, String>() {
			@Override public String apply(final String fileName) {
				return fileName.replace(SEGMENT_INFO_EXTENSION, EMPTY_STRING);
			}
		}));
		
		trace("Source commit segment file names: " + sourceFileNames);
		trace("Target commit segment file names: " + targetFileNames);
		trace("Intersection segment file names: " + intersectionSegmentFileNames);
		
		return intersectionSegmentFileNames;
		
	}
	
	private void trace(final String msg, final Object... arg) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(msg, arg);
		}
	}
	
	private IOException closeReader(final Closeable closeable) {

		if (null != closeable) {
			try {
				closeable.close();
			} catch (final IOException e) {
				try {
					closeable.close();
				} catch (final IOException e1) {
					e.addSuppressed(e1);
				}
				LOGGER.error("Error while closing index reader.", e);
				return e;
			}
		}
		
		return null;
		
	}
	
//	0  DIF IRR DFS DFT
//	1  INT IRR DFS DFT
//	2  DIF REL DFS DFT
//	3  INT REL DFS DFT
//	4  DIF IRR LIS DFT
//	5  INT IRR LIS DFT
//	6  DIF REL LIS DFT
//	7  INT REL LIS DFT
//	8  DIF IRR DFS LIT
//	9  INT IRR DFS LIT
//	10 DIF REL DFS LIT
//	11 INT REL DFS LIT
//	12 DIF IRR LIS LIT
//	13 INT IRR LIS LIT
//	14 DIF REL LIS LIT
//	15 INT REL LIS LIT
	
	private byte createIndexChangeFlag(final boolean intersection, final boolean relevant, final boolean liveInSource, final boolean liveInTarget) {
		
		byte indexChangeFlag = 0;
		if (intersection) {
			indexChangeFlag |= INTERSECTION;
		}
		if (relevant) {
			indexChangeFlag |= RELEVANT;
		}
		if (liveInSource) {
			indexChangeFlag |= LIVE_IN_SOURCE;
		}
		if (liveInTarget) {
			indexChangeFlag |= LIVE_IN_TARGET;
		}
		return indexChangeFlag;
		
	}
	
	private boolean isIntersection(final byte indexChangeFlag) {
		return isFlagSet(indexChangeFlag, INTERSECTION);
	}
	
	private boolean isRelevant(final byte indexChangeFlag) {
		return isFlagSet(indexChangeFlag, RELEVANT);
	}
	
	private boolean isLiveInSource(final byte indexChangeFlag) {
		return isFlagSet(indexChangeFlag, LIVE_IN_SOURCE);
	}
	
	private boolean isLiveInTarget(final byte indexChangeFlag) {
		return isFlagSet(indexChangeFlag, LIVE_IN_TARGET);
	}
	
	private boolean isFlagSet(final byte indexChangeFlag, final int mask) {
		return (indexChangeFlag & mask) != 0;
	}
	
	private String toString(final byte indexChangeFlag) {
		return new StringBuilder("Index change:")
			.append(isIntersection(indexChangeFlag) ? " INT" : " DIF")
			.append(isRelevant(indexChangeFlag) ? " REL" : " IRR")
			.append(isLiveInSource(indexChangeFlag) ? " LIS" : " DFS")
			.append(isLiveInTarget(indexChangeFlag) ? " LIT" : " DFT")
			.toString();
	}

	
}