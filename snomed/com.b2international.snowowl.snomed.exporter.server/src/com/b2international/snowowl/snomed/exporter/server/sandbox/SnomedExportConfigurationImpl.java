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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.annotation.Nullable;

import com.b2international.index.Searcher;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.common.ContentSubType;

/**
 * Export configuration for the SNOMED CT export process.
 */
public class SnomedExportConfigurationImpl implements SnomedExportConfiguration {

	private static final String SEGMENT_INFO_EXTENSION = ".si";
	
	private final IBranchPath currentBranchPath;
	private final ContentSubType contentSubType;
	private final String unsetEffectiveTimeLabel;

	private final Date deltaExportStartEffectiveTime;
	private final Date deltaExportEndEffectiveTime;
	
	private RevisionSearcher revisionSearcher;
	private Searcher searcher;

	
//	private final Supplier<Map<IBranchPath, Collection<String>>> versionPathToSegmentNameMappingSupplier = 
//			memoize(new Supplier<Map<IBranchPath, Collection<String>>>() {
//				public Map<IBranchPath, Collection<String>> get() {
//					
//					//try to collect all segment file names for each individual version
//					final IndexServerService<?> indexService = (IndexServerService<?>) getServiceForClass(SnomedIndexService.class);
//					final IndexBranchService branchService = indexService.getBranchService(createMainPath());
//					
//					final List<IBranchPath> versionPaths = newArrayList();
//					for (final ICodeSystemVersion version : getAllVersion()) {
//						versionPaths.add(createVersionPath(version.getVersionId()));
//					}
//					versionPaths.add(createMainPath());
//					
//					final Map<IBranchPath, Collection<String>> branchPathToSegmentNamesMapping = newLinkedHashMap();
//					for (final IBranchPath branchPath : versionPaths) {
//						try {
//							final IndexCommit commit = branchService.getIndexCommit(branchPath);
//							if (null == commit) {
//								branchPathToSegmentNamesMapping.put(branchPath, Collections.<String>emptySet());
//							} else {
//								
//								final Collection<String> segmentNames = FluentIterable.from(commit.getFileNames())
//										.filter(new Predicate<String>() { @Override public boolean apply(final String fileName) {
//											return fileName.endsWith(SEGMENT_INFO_EXTENSION);
//										}})
//										.transform(new Function<String, String>() { @Override public String apply(final String fileName) {
//											return fileName.replace(SEGMENT_INFO_EXTENSION, EMPTY_STRING);
//										}})
//										.toSet();
//								
//								branchPathToSegmentNamesMapping.put(branchPath, segmentNames);
//							}
//						} catch (final IOException e) {
//							throw new SnowowlRuntimeException("Error while initializing SNOMED CT full export.", e);
//						}
//					}
//					
//					return unmodifiableMap(branchPathToSegmentNamesMapping);
//				}
//		});

	public SnomedExportConfigurationImpl(final IBranchPath currentBranchPath,
			final ContentSubType contentSubType,
			final String unsetEffectiveTimeLabel,
			@Nullable final Date deltaExportStartEffectiveTime, 
			@Nullable final Date deltaExportEndEffectiveTime) {

		this.currentBranchPath = checkNotNull(currentBranchPath, "currentBranchPath");
		this.contentSubType = checkNotNull(contentSubType, "contentSubType");
		this.unsetEffectiveTimeLabel = checkNotNull(unsetEffectiveTimeLabel, "unsetEffectiveTimeLabel");
		this.deltaExportStartEffectiveTime = deltaExportStartEffectiveTime;
		this.deltaExportEndEffectiveTime = deltaExportEndEffectiveTime;
	}

	@Override
	public IBranchPath getCurrentBranchPath() {
		return currentBranchPath;
	}

	@Override
	public ContentSubType getContentSubType() {
		return contentSubType;
	}

	@Override
	public String getUnsetEffectiveTimeLabel() {
		return unsetEffectiveTimeLabel;
	}
	
	@Override
	@Nullable public Date getDeltaExportStartEffectiveTime() {
		return deltaExportStartEffectiveTime;
	}

	@Override
	@Nullable public Date getDeltaExportEndEffectiveTime() {
		return deltaExportEndEffectiveTime;
	}
	
//	@Override
//	public Map<IBranchPath, Collection<String>> getVersionPathToSegmentNameMappings() {
//		return versionPathToSegmentNameMappingSupplier.get();
//	}
	
	public RevisionSearcher getRevisionSearcher() {
		return revisionSearcher;
	}

	public void setRevisionSearcher(RevisionSearcher revisionSearcher) {
		this.revisionSearcher = revisionSearcher;
	}
	
	public Searcher getSearcher() {
		return searcher;
	}

	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
}
