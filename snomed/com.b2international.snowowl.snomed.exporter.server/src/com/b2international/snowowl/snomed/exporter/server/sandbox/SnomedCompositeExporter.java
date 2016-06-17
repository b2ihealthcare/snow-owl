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
import static com.b2international.snowowl.datastore.BranchPathUtils.bottomToTopIterator;
import static com.b2international.snowowl.datastore.BranchPathUtils.convertIntoBasePath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createVersionPath;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.effectiveTime;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterators.concat;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.reverse;
import static java.util.Collections.unmodifiableMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.b2international.commons.collections.CloseableList;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf1Exporter;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf2Exporter;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

/**
 * Abstract SNOMED CT composite exporter. The exporter relies on the underlying index store without 
 * directly accessing it.
 */
public abstract class SnomedCompositeExporter<T extends SnomedDocument> implements SnomedIndexExporter<T> {

	private final Iterator<String> itr;
	//private final CloseableList<SnomedSubExporter> closeables;
	private final SnomedExportConfiguration configuration;
	private final Map<IBranchPath, Long> branchPathWithEffectiveTimeMap;

	public SnomedCompositeExporter(final SnomedExportConfiguration configuration) {
		this.configuration = checkNotNull(configuration, "configuration");
		//closeables = new CloseableList<SnomedSubExporter>();
		branchPathWithEffectiveTimeMap = createBranchPathMap();
		itr = createSubExporters(this.configuration);
	}
	
	@Override
	public String next() {
		return itr.next();
	}
	
	@Override
	public boolean hasNext() {
		return itr.hasNext();
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Expression getExportQuery(final IBranchPath branchPath) {
		final ContentSubType contentSubType = configuration.getContentSubType();
		switch (contentSubType) {
			case DELTA:
				return getDeltaQuery();
			case SNAPSHOT: //$FALL-THROUGH$
			case FULL:
				return getSnapshotQuery();
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
			 throw createException();
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
			throw createException();
		}
	}
	
	@Override
	public void close() throws Exception {
		//closeables.close();
	}
	
	@Override
	public Iterator<String> iterator() {
		return this;
	}
	
	@Override
	public SnomedExportConfiguration getConfiguration() {
		return configuration;
	}
	
	protected Expression getDeltaQuery() {
		
		final Date startDate = configuration.getDeltaExportStartEffectiveTime();
		final Date endDate = configuration.getDeltaExportEndEffectiveTime();
		
		ExpressionBuilder builder = Expressions.builder();
		builder.must(getSnapshotQuery());

		//no start, no end -> export unpublished ones
		if (null == startDate && null == endDate) {
			builder.must(effectiveTime(UNSET_EFFECTIVE_TIME));
			return builder.build();
		}
		
		//end effective time is specified so we need everything 
		//where the effective time is less or equal than the given end date one
		if (null == startDate) {
			//TODO: replace this condition with exclusive start interval (now it is true, true)
			builder.must(effectiveTime(UNSET_EFFECTIVE_TIME, endDate.getTime()));
			return builder.build();
		}
		
		//end date is not set. so effective time can be greater/equals than start
		//end does not matter but consider unpublished ones.
		if (null == endDate) {
			ExpressionBuilder noEndDateExpressionBuilder = Expressions.builder();
			noEndDateExpressionBuilder.should(effectiveTime(UNSET_EFFECTIVE_TIME));
			//TODO: no end, this should be part of the builder as 'after', for now, Long.MAX_VALUE
			noEndDateExpressionBuilder.should(effectiveTime(startDate.getTime(), Long.MAX_VALUE));
			builder.must(noEndDateExpressionBuilder.build());
			return builder.build();
		}
		//both start and end is specified. create a range
		builder.must(SnomedDocument.Expressions.effectiveTime(startDate.getTime(), endDate.getTime()));
		
		return builder.build();
	}
	
	protected Expression getSnapshotQuery() {
		return Expressions.matchAll();
	}
	
//	protected SnomedSubExporter createSubExporter(final IBranchPath branchPath, final SnomedIndexExporter<T> exporter) {
//		return createSubExporter(branchPath, exporter, Collections.<String>emptySet());
//	}
	
//	protected SnomedSubExporter createSubExporter(final IBranchPath branchPath, final SnomedIndexExporter<T> exporter, final Collection<String> ignoredSegmentNames) {
//		final SnomedSubExporter subExporter = new SnomedSubExporter(branchPath, exporter);//, ignoredSegmentNames);
//		closeables.add(subExporter);
//		return subExporter;
//	}
	
	protected final String formatEffectiveTime(final Long effectiveTime) {
		return EffectiveTimes.format(effectiveTime, DateFormats.SHORT, configuration.getUnsetEffectiveTimeLabel());
	}
	
	/**
	 * Throws an illegal state exception.
	 */
	protected RuntimeException createException() {
		return new IllegalStateException(
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
	
	private Map<IBranchPath, Long> createBranchPathMap() {
		final Map<IBranchPath, Long> branchPathMap = newLinkedHashMap();
		for (final ICodeSystemVersion version : getAllVersion()) {
			branchPathMap.put(createVersionPath(version.getVersionId()), version.getEffectiveDate());
		}
		branchPathMap.put(createMainPath(), UNSET_EFFECTIVE_TIME);
		return unmodifiableMap(branchPathMap);
	}

	private Iterator<String> createSubExporters(final SnomedExportConfiguration configuration) {
		
		final ContentSubType contentSubType = configuration.getContentSubType();
		final IBranchPath currentBranchPath = configuration.getCurrentBranchPath();
		
		switch (contentSubType) {
			
			case DELTA:
				return createSubExporter(currentBranchPath, this);
				
			case SNAPSHOT:
				return createSubExporter(currentBranchPath, this);
				
			case FULL:
				
				//the first is always the current one no matter we are on task or not
				final List<IBranchPath> branchPaths = newArrayList(currentBranchPath);

				//gather all branch paths for all versions
				final List<IBranchPath> allVersionsBranchPaths = newArrayList(branchPathWithEffectiveTimeMap.keySet());

				//let's figure out if are we on task, version or MAIN. let's find the closest branch path
				int closestBranchPathIndex = -1;
				final Iterator<IBranchPath> bottomToTopIterator = bottomToTopIterator(currentBranchPath);
				while (bottomToTopIterator.hasNext()) {
					final IBranchPath branchPath = bottomToTopIterator.next();
					closestBranchPathIndex = allVersionsBranchPaths.indexOf(branchPath); 
					if (closestBranchPathIndex > -1) {
						break;
					}
				}
				
				checkState(closestBranchPathIndex > -1, "Cannot find closest version or MAIN for branch path '" + currentBranchPath + "'.");
				
				//now add the BASE of all previous versions (exclude the one that is the closest to current branch)
				for (int i = closestBranchPathIndex + 1; i < allVersionsBranchPaths.size(); i++) {
					branchPaths.add(convertIntoBasePath(allVersionsBranchPaths.get(i)));
				}
				
				//start with the oldest one then traverse to the current one from bottom to top
				reverse(branchPaths);
				
				Iterator<String> subExportersIterator = concat(Iterators.transform(branchPaths.iterator(), new Function<IBranchPath, SnomedSubExporter>() {
					@Override public SnomedSubExporter apply(final IBranchPath branchPath) {
						
						final List<IBranchPath> branchPaths = newArrayList(branchPathWithEffectiveTimeMap.keySet());
						reverse(branchPaths);
						
						//final Collection<String> ignoredSegmentNames = newHashSet();
//						for (final IBranchPath path : branchPaths) {
//							if (createPath(path).equals(createPath(branchPath))) {
//								break;
//							}
//							//ignoredSegmentNames.addAll(configuration.getVersionPathToSegmentNameMappings().get(path));
//						}
						
						return createSubExporter(branchPath, SnomedCompositeExporter.this); //, ignoredSegmentNames);
					}
				}));
				
				return subExportersIterator;
			
			default:
				throw new IllegalArgumentException("Implementation error. Unknown content subtype: " + contentSubType);
			
		}
		
	}

	private List<ICodeSystemVersion> getAllVersion() {
		return getServiceForClass(CodeSystemService.class).getAllTagsWithHead(SnomedDatastoreActivator.REPOSITORY_UUID);
	}

}