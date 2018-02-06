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

import static com.b2international.commons.StringUtils.EMPTY_STRING;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.datastore.BranchPathUtils.createVersionPath;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REPOSITORY_UUID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.Collections.unmodifiableMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.time.TimeUtil;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.server.index.IndexBranchService;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

/**
 * Export configuration for the SNOMED CT export process.
 */
public class SnomedExportConfigurationImpl implements SnomedExportConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedExportConfiguration.class);
	private static final String SEGMENT_INFO_EXTENSION = ".si";
	
	private final IBranchPath currentBranchPath;
	private final ContentSubType contentSubType;
	private final String unsetEffectiveTimeLabel;
	private final String countryAndNameSpaceElement;

	private final Date deltaExportStartEffectiveTime;
	private final Date deltaExportEndEffectiveTime;
	
	private final Map<String, ReferenceManager<IndexSearcher>> branchManagerMap = newHashMap();
	private final Map<String, IndexSearcher> indexSearcherMap = newHashMap();

	private final Supplier<Map<IBranchPath, Collection<String>>> versionPathToSegmentNameMappingSupplier = 
			memoize(new Supplier<Map<IBranchPath, Collection<String>>>() {
				public Map<IBranchPath, Collection<String>> get() {
					
					//try to collect all segment file names for each individual version
					final IndexServerService<?> indexService = (IndexServerService<?>) getServiceForClass(SnomedIndexService.class);
					final IndexBranchService branchService = indexService.getBranchService(createMainPath());
					
					final List<IBranchPath> versionPaths = newArrayList();
					for (final ICodeSystemVersion version : getAllVersion()) {
						versionPaths.add(createVersionPath(version.getVersionId()));
					}
					versionPaths.add(createMainPath());
					
					final Map<IBranchPath, Collection<String>> branchPathToSegmentNamesMapping = newLinkedHashMap();
					for (final IBranchPath branchPath : versionPaths) {
						try {
							final IndexCommit commit = branchService.getIndexCommit(branchPath);
							if (null == commit) {
								branchPathToSegmentNamesMapping.put(branchPath, Collections.<String>emptySet());
							} else {
								
								final Collection<String> segmentNames = FluentIterable.from(commit.getFileNames())
										.filter(new Predicate<String>() { @Override public boolean apply(final String fileName) {
											return fileName.endsWith(SEGMENT_INFO_EXTENSION);
										}})
										.transform(new Function<String, String>() { @Override public String apply(final String fileName) {
											return fileName.replace(SEGMENT_INFO_EXTENSION, EMPTY_STRING);
										}})
										.toSet();
								
								branchPathToSegmentNamesMapping.put(branchPath, segmentNames);
							}
						} catch (final IOException e) {
							throw new SnowowlRuntimeException("Error while initializing SNOMED CT full export.", e);
						}
					}
					
					return unmodifiableMap(branchPathToSegmentNamesMapping);
				}
		});

	public SnomedExportConfigurationImpl(final IBranchPath currentBranchPath,
			final ContentSubType contentSubType,
			final String unsetEffectiveTimeLabel,
			final String countryAndNameSpaceElement,
			@Nullable final Date deltaExportStartEffectiveTime, 
			@Nullable final Date deltaExportEndEffectiveTime) {

		this.currentBranchPath = checkNotNull(currentBranchPath, "currentBranchPath");
		this.contentSubType = checkNotNull(contentSubType, "contentSubType");
		this.unsetEffectiveTimeLabel = checkNotNull(unsetEffectiveTimeLabel, "unsetEffectiveTimeLabel");
		this.countryAndNameSpaceElement = checkNotNull(countryAndNameSpaceElement, "countryAndNameSpaceElement");
		this.deltaExportStartEffectiveTime = deltaExportStartEffectiveTime;
		this.deltaExportEndEffectiveTime = deltaExportEndEffectiveTime;
		initializeMaps();
	}

	private void initializeMaps() {
		try {
			
			Stopwatch watch = Stopwatch.createStarted();
			
			if (ContentSubType.FULL == contentSubType) {
				
				for (ICodeSystemVersion version : getAllVersion()) {
					
					IBranchPath versionPath = BranchPathUtils.createVersionPath(version.getVersionId());
					
					Stopwatch stopwatch = Stopwatch.createStarted();
					
					LOGGER.info("Initializing branch manager and index searcher for {}...", versionPath.getPath());

					ReferenceManager<IndexSearcher> manager = getIndexServerService().getManager(versionPath);
					branchManagerMap.put(versionPath.getPath(), manager);
					
					IndexSearcher searcher = manager.acquire();
					indexSearcherMap.put(versionPath.getPath(), searcher);
					
					LOGGER.info("Branch manager and index searcher initialization for {} took {}", versionPath.getPath(), TimeUtil.toString(stopwatch));
				}
				
				if (!branchManagerMap.containsKey(BranchPathUtils.createMainPath())) {
					
					Stopwatch stopwatch = Stopwatch.createStarted();
					
					LOGGER.info("Initializing branch manager and index searcher for {}...", currentBranchPath.getPath());
					
					ReferenceManager<IndexSearcher> manager = getIndexServerService().getManager(currentBranchPath);
					branchManagerMap.put(currentBranchPath.getPath(), manager);
					
					IndexSearcher searcher = manager.acquire();
					indexSearcherMap.put(currentBranchPath.getPath(), searcher);
					
					LOGGER.info("Branch manager and index searcher initialization for {} took {}", currentBranchPath.getPath(), TimeUtil.toString(stopwatch));
					
				}
				
			} else {
				
				ReferenceManager<IndexSearcher> manager = getIndexServerService().getManager(currentBranchPath);
				branchManagerMap.put(currentBranchPath.getPath(), manager);
				
				IndexSearcher searcher = manager.acquire();
				indexSearcherMap.put(currentBranchPath.getPath(), searcher);
				
			}
			
			LOGGER.info("Branch manager and index searcher initialization for export type {} took {}", contentSubType, TimeUtil.toString(watch));
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void dispose() {
		
		try {
			
			for (Entry<String, ReferenceManager<IndexSearcher>> entry : branchManagerMap.entrySet()) {
				
				String branchPath = entry.getKey();
				ReferenceManager<IndexSearcher> manager = entry.getValue();
				
				if (manager != null && indexSearcherMap.containsKey(branchPath)) {
					LOGGER.info("Releasing index searcher for {}", branchPath);
					manager.release(indexSearcherMap.get(branchPath));
				}
				
			}
		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/*returns with the server side index service.*/
	@SuppressWarnings("rawtypes")
	private final IndexServerService<?> getIndexServerService() {
		return (IndexServerService) ApplicationContext.getInstance().getService(SnomedIndexService.class);
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
	
	@Override
	public Map<IBranchPath, Collection<String>> getVersionPathToSegmentNameMappings() {
		return versionPathToSegmentNameMappingSupplier.get();
	}
	
	private List<ICodeSystemVersion> getAllVersion() {
		return getServiceForClass(CodeSystemService.class).getAllTagsWithHead(REPOSITORY_UUID);
	}

	@Override
	public String getCountryAndNamespaceElement() {
		return countryAndNameSpaceElement;
	}
	
	@Override
	public Map<String, IndexSearcher> getIndexSearcherMap() {
		return indexSearcherMap;
	}
	
	
}
