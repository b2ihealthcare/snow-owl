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
package com.b2international.snowowl.snomed.exporter.server.sandbox;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.core.date.EffectiveTimes.UNSET_EFFECTIVE_TIME;
import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createVersionPath;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.Collections.unmodifiableMap;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;
import static org.apache.lucene.search.NumericRangeQuery.newLongRange;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf1Exporter;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf2Exporter;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.primitives.Longs;

/**
 *
 */
public abstract class SnomedCompositeExporter implements SnomedIndexExporter {

	private static final long SG_RELEASE_20140801 = EffectiveTimes.parse("2014-08-01").getTime();
	private SnomedSubExporter subExporter;
	
	private final SnomedExportConfiguration configuration;
	private final Iterator<IBranchPath> branchesToExport;
	private final Map<IBranchPath, Long> branchesToEffectiveTimeMap;

	private final LoadingCache<Long, String> dateCache;
	
	public SnomedCompositeExporter(final SnomedExportConfiguration configuration) {
		this.configuration = checkNotNull(configuration, "configuration");
		
		branchesToEffectiveTimeMap = createBranchPathMap();
		branchesToExport = getBranchesToExport();
		
		subExporter = createSubExporter(this.configuration);
		
		dateCache = initDateCache(configuration);
	}

	@Override
	public String next() {
		return subExporter.next();
	}
	
	@Override
	public boolean hasNext() {
		
		boolean hasNext = subExporter.hasNext();
		
		try {
			
			if (!hasNext) {
				
				while (branchesToExport.hasNext()) {
					// close current sub exporter
					subExporter.close();
					IBranchPath nextBranchPath = branchesToExport.next();
					// open sub exporter for the next branch path
					subExporter = new SnomedSubExporter(nextBranchPath, this);
					// check if there are any elements on the next branch
					if (subExporter.hasNext()) {
						return true;
					}
				}
				
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return hasNext;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws Exception {
		subExporter.close();
	}
	
	@Override
	public Query getExportQuery(final IBranchPath branchPath) {
		final ContentSubType contentSubType = configuration.getContentSubType();
		switch (contentSubType) {
			case DELTA:
				return getDeltaQuery();
			case SNAPSHOT: //$FALL-THROUGH$
				return getSnapshotQuery();
			case FULL:
				return getFullQuery(branchPath);
			default:
				throw new IllegalArgumentException("Implementation error. Unknown content subtype: " + contentSubType);
		}
	}
	
	@Override
	public String getRelativeDirectory() {
		checkInstance();
		if (this instanceof SnomedRf1Exporter) {
			return RF1_CORE_RELATIVE_DIRECTORY;
		} else if (this instanceof SnomedRf2Exporter) {
			return RF2_CORE_RELATIVE_DIRECTORY;
		} else {
			return raiseError();
		}
	}
	
	@Override
	public String getFileName() {
		checkInstance();
		if (this instanceof SnomedRf1Exporter) {
			return SnomedRfFileNameBuilder.buildCoreRf1FileName(getType(), configuration);
		} else if (this instanceof SnomedRf2Exporter) {
			return SnomedRfFileNameBuilder.buildCoreRf2FileName(getType(), configuration);
		} else {
			return raiseError();
		}
	}
	
	@Override
	public Iterator<String> iterator() {
		return this;
	}
	
	@Override
	public SnomedExportConfiguration getConfiguration() {
		return configuration;
	}
	
	protected Query getDeltaQuery() {
		
		final Date startDate = configuration.getDeltaExportStartEffectiveTime();
		final Date endDate = configuration.getDeltaExportEndEffectiveTime();
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(getSnapshotQuery(), MUST);

		//no start no end -> export unpublished ones
		if (null == startDate && null == endDate) {
			query.add(getUnpublishedQuery(), MUST);
			return query;
		}
		
		final String effectiveTimeField = SnomedMappings.effectiveTime().fieldName();
		
		//end effective time is specified so we need everything 
		//where the effective time is less or equal than the given end date one
		if (null == startDate) {
			query.add(newLongRange(effectiveTimeField, UNSET_EFFECTIVE_TIME, endDate.getTime(), false, true), MUST);
			return query;
		}
		
		//end date is not set. so effective time can be greater/equals than start
		//end does not matter but consider unpublished ones.
		if (null == endDate) {
			final BooleanQuery effectiveTimeQuery = new BooleanQuery(true);
			effectiveTimeQuery.add(getUnpublishedQuery(), SHOULD);
			effectiveTimeQuery.add(newLongRange(effectiveTimeField, startDate.getTime(), null, true, true), SHOULD);
			query.add(effectiveTimeQuery, MUST);
			return query;
		}
		
		//both start and end is specified. create a range
		query.add(newLongRange(effectiveTimeField, startDate.getTime(), endDate.getTime(), true, true), MUST);
		
		return query;
	}
	
	private Query getFullQuery(IBranchPath branchPath) {
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(getSnapshotQuery(), MUST);
		
		final BooleanQuery effectiveTimeQuery = new BooleanQuery(true);
		effectiveTimeQuery.add(getUnpublishedQuery(), SHOULD);
		
		if (!BranchPathUtils.isMain(branchPath)) {
			Long versionBranchEffectiveDate = branchesToEffectiveTimeMap.get(BranchPathUtils.createPath(branchPath.getPath()));
			
			// restrict RF2 export lowerbound effective time filter to first SG version for each version entry
			if (versionBranchEffectiveDate >= SG_RELEASE_20140801) {
				versionBranchEffectiveDate = SG_RELEASE_20140801;
			}
			
			effectiveTimeQuery.add(newLongRange(SnomedMappings.effectiveTime().fieldName(), versionBranchEffectiveDate, null, true, false), SHOULD);
		}

		query.add(effectiveTimeQuery, MUST);
		
		return query;
	}
	
	protected abstract Query getSnapshotQuery();
	
	private Query getUnpublishedQuery() {
		return SnomedMappings.newQuery().effectiveTime(UNSET_EFFECTIVE_TIME).matchAll();
	}

	protected final String formatEffectiveTime(final Long effectiveTime) {
		return dateCache.getUnchecked(effectiveTime);
	}
	
	/**
	 * Throws an illegal state exception.
	 */
	protected <T> T raiseError() throws IllegalStateException {
		throw new IllegalStateException(
			new StringBuilder("Unknown SNOMED CT release format exporter implementation. Expected either ")
			.append(SnomedRf1Exporter.class.getName())
			.append( "or" )
			.append(SnomedRf2Exporter.class.getName())
			.append(". Was ")
			.append(getClass().getName())
			.append(".")
			.toString());
	}
	
	/**
	 * Checks the current instance. <br>Throws {@link IllegalStateException} if the current instance implements 
	 * SnomedRf1Exporter and SnomedRf2Exporter interfaces in the same time.
	 */
	protected void checkInstance() {
		Preconditions.checkState(
				!(this instanceof SnomedRf2Exporter && this instanceof SnomedRf1Exporter), 
				"SNOMED CT exporter implementation cannot be RF1 and RF2 specific in the same time.");
	}
	
	private LoadingCache<Long, String> initDateCache(final SnomedExportConfiguration configuration) {
		return CacheBuilder.newBuilder().build(new CacheLoader<Long, String>() {
			@Override
			public String load(Long key) throws Exception {
				return EffectiveTimes.format(key, DateFormats.SHORT, configuration.getUnsetEffectiveTimeLabel());
			}
		});
	}

	private Map<IBranchPath, Long> createBranchPathMap() {
		
		final Map<IBranchPath, Long> branchPathMap = newLinkedHashMap();
		
		List<ICodeSystemVersion> allCodeSystemVersions = getAllVersion();
		
		long lowerBound = configuration.getDeltaExportStartEffectiveTime() != null ? configuration.getDeltaExportStartEffectiveTime().getTime() : 0L;
		long upperBound = configuration.getDeltaExportEndEffectiveTime() != null ? configuration.getDeltaExportEndEffectiveTime().getTime() : Long.MAX_VALUE;
		
		for (final ICodeSystemVersion version : allCodeSystemVersions) {
			if (version.getEffectiveDate() > lowerBound && version.getEffectiveDate() < upperBound) {
				branchPathMap.put(createVersionPath(version.getVersionId()), version.getEffectiveDate());
			}
		}
		
		branchPathMap.put(createMainPath(), UNSET_EFFECTIVE_TIME);
		
		return unmodifiableMap(branchPathMap);
	}

	private Iterator<IBranchPath> getBranchesToExport() {
		return FluentIterable.from(branchesToEffectiveTimeMap.keySet()).transform(new Function<IBranchPath, IBranchPath>() {
			@Override
			public IBranchPath apply(IBranchPath input) {
				if (BranchPathUtils.isMain(input)) {
					return input;
				}
				return BranchPathUtils.convertIntoBasePath(input);
			}
		}).toList().iterator();
	}

	private SnomedSubExporter createSubExporter(final SnomedExportConfiguration configuration) {
		
		final ContentSubType contentSubType = configuration.getContentSubType();
		final IBranchPath currentBranchPath = configuration.getCurrentBranchPath();
		
		switch (contentSubType) {
			
			case DELTA: // fall through
			case SNAPSHOT:
				return new SnomedSubExporter(currentBranchPath, this);
				
			case FULL:
				return new SnomedSubExporter(branchesToExport.next(), this);
			
			default:
				throw new IllegalArgumentException("Implementation error. Unknown content subtype: " + contentSubType);
			
		}
		
	}

	private List<ICodeSystemVersion> getAllVersion() {
		List<ICodeSystemVersion> codeSystemVersions = getServiceForClass(CodeSystemService.class).getAllTagsWithHead(SnomedDatastoreActivator.REPOSITORY_UUID);
		return FluentIterable.from(codeSystemVersions).toSortedList(new Comparator<ICodeSystemVersion>() {
			@Override
			public int compare(ICodeSystemVersion o1, ICodeSystemVersion o2) {
				return Longs.compare(o1.getEffectiveDate(), o2.getEffectiveDate());
			}
		});
	}

}