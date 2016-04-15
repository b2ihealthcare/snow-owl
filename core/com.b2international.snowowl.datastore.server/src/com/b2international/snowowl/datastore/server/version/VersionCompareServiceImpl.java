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
package com.b2international.snowowl.datastore.server.version;

import static com.b2international.commons.collect.LongSets.parallelForEach;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.server.version.VersionCompareHierarchyBuilderCache.INSTANCE;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Maps.newConcurrentMap;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Change;
import com.b2international.commons.collect.LongSets.LongCollectionProcedure;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.index.diff.CompareResult;
import com.b2international.snowowl.datastore.index.diff.CompareResultImpl;
import com.b2international.snowowl.datastore.index.diff.IndexDifferService;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.NodeDiffDerivation;
import com.b2international.snowowl.datastore.index.diff.NodeDiffImpl;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;
import com.b2international.snowowl.datastore.version.VersionCompareService;
import com.b2international.snowowl.index.diff.IndexDiff;
import com.b2international.snowowl.index.diff.ThreeWayIndexDiff;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets.SetView;

/**
 * Basic implementation of the {@link VersionCompareService} interface.
 *
 */
public class VersionCompareServiceImpl implements VersionCompareService {

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionCompareServiceImpl.class);
	
	@Override
	public CompareResult compare(final VersionCompareConfiguration configuration, final IProgressMonitor monitor) {
		
		try {

			checkNotNull(configuration, "configuration");
			final IProgressMonitor subMonitor = SubMonitor.convert(monitor, 5);
			
			final Stopwatch overallStopwatch = Stopwatch.createStarted();
			
			final String sourcePath = configuration.getSourcePath().getPath() + (configuration.isSourcePatched() ? "*" : "");
			final String targetPath = configuration.getTargetPath().getPath() + (configuration.isTargetPatched() ? "*" : "");
			subMonitor.setTaskName("Comparing changes between '" + sourcePath + "' and '" + targetPath + " for '" + configuration.getToolingName() + "'...");
			LOGGER.info("Comparing changes between '" + sourcePath + "' and '" + targetPath + " for '" + configuration.getToolingName() + "'...");
			final VersionCompareHierarchyBuilder builder = INSTANCE.getBuilder(configuration.getRepositoryUuid());
			subMonitor.worked(1);
			
			
			final Stopwatch stopwatch = Stopwatch.createStarted();
			subMonitor.setTaskName("Collecting changed components from semantic cache [1 of 5]...");
			LOGGER.info("Collecting changed components from semantic cache [1 of 5]...");
			final IndexDiff diff = collectChangedComponent(configuration);
			LOGGER.info("Changed components have been successfully collected. [" + stopwatch + "]");
			subMonitor.worked(1);
			
			
			if (subMonitor.isCanceled()) {
				logUserAbort();
				return createEmptyResult(configuration);
			}
			resetAndRestart(stopwatch);
			LOGGER.info("Resolving changed components [2 of 5]...");
			subMonitor.setTaskName("Resolving changed components [2 of 5]...");
			final Map<String, NodeDiff> nodes = resolveNodes(configuration, builder, diff);
			LOGGER.info("Changed components have been successfully resolved. [" + stopwatch + "]");
			subMonitor.worked(1);
			
			
			if (subMonitor.isCanceled()) {
				logUserAbort();
				return createEmptyResult(configuration);
			}
			resetAndRestart(stopwatch);
			LOGGER.info("Building hierarchy among the changed components [3 of 5]...");
			subMonitor.setTaskName("Building hierarchy among the changed components [3 of 5]...");
			buildHierarchy(configuration, builder, nodes, diff);
			
			LOGGER.info("Hierarchy has been successfully built among the changed components. [" + stopwatch + "]");
			subMonitor.worked(1);
			
			
			if (subMonitor.isCanceled()) {
				logUserAbort();
				return createEmptyResult(configuration);
			}
			resetAndRestart(stopwatch);
			LOGGER.info("Collapsing hierarchy [4 of 5]...");
			subMonitor.setTaskName("Compacting hierarchy [4 of 5]...");
			collapseHierarchy(builder, nodes);
			LOGGER.info("Hierarchy has been successfully collapsed. [" + stopwatch + "]");
			subMonitor.worked(1);
		
			final Multimap<IBranchPath, String> nodesByBranch = ArrayListMultimap.create();
			for (NodeDiff node : nodes.values()) {
				nodesByBranch.put(getBranchPath(configuration, diff, node.getStorageKey(), node), node.getId());
			}
			final Map<String, String> labels = builder.resolveLabels(nodesByBranch);
			for (NodeDiff node : nodes.values()) {
				((NodeDiffImpl)node).setLabel(labels.get(node.getId()));
			}
			
			if (subMonitor.isCanceled()) {
				logUserAbort();
				return createEmptyResult(configuration);
			}
			resetAndRestart(stopwatch);
			LOGGER.info("Finalizing version compare. Building version compare result [5 of 5]...");
			subMonitor.setTaskName("Finalizing version compare. Building version compare result [5 of 5]...");
			final CompareResult result = createCompareResult(configuration, builder, nodes.values());
			LOGGER.info("Version compare successfully built. [" + stopwatch + "]");
			subMonitor.worked(1);
			
			
			LOGGER.info("Changes between '" + sourcePath + "' and '" + targetPath + "' for '" + configuration.getToolingName() + "' have been successfully calculated. [" + overallStopwatch + "]");
			return result;
			
		} finally {
			if (null != monitor) {
				monitor.done();
			}
		}
		
	}

	private void collapseHierarchy(final VersionCompareHierarchyBuilder builder, final Map<String, NodeDiff> nodes) {
		builder.collapseHierarchy(nodes.values());
	}

	private IndexDiff collectChangedComponent(final VersionCompareConfiguration configuration) {
		return getServiceForClass(IndexDifferService.class).calculateDiff(checkNotNull(configuration, "configuration"));
	}

	private void buildHierarchy(final VersionCompareConfiguration configuration, final VersionCompareHierarchyBuilder builder, final Map<String, NodeDiff> nodes, final IndexDiff diff) {
		
		for (final Iterator<Entry<String, NodeDiff>> itr = newHashSet(nodes.entrySet()).iterator(); itr.hasNext(); /* nothing */) {
		
			final Entry<String, NodeDiff> entry = itr.next();
			NodeDiff currentNode = entry.getValue();
			
			boolean stop = false;
			
			while (!stop) {
				
				stop = null != currentNode.getParent();
				
				if (!stop) {
					
					stop = builder.isRoot(currentNode);
					
					if (!stop) {
						
						final IBranchPath branchPath = getBranchPath(configuration, diff, currentNode.getStorageKey(), currentNode);
						final Set<String> superTypeStorageKeys = builder.getSuperTypeIds(branchPath, currentNode.getId());
						
						if (isEmpty(superTypeStorageKeys)) {
							
							stop = true;
							
						} else {
							
							
							final Set<String> intersectionSuperTypeIds = getIntersectionSuperTypeIds(nodes, superTypeStorageKeys);
							
							NodeDiff ancestorNode = null;
							
							if (isEmpty(intersectionSuperTypeIds)) {
								
								final String ancestorId = superTypeStorageKeys.iterator().next();
								ancestorNode = builder.createUnchangedNode(branchPath, ancestorId);
								nodes.put(ancestorId, ancestorNode);
								
							} else {
								
								ancestorNode = (NodeDiff) nodes.get(intersectionSuperTypeIds.iterator().next());
								
							}
							
							((NodeDiffImpl) ancestorNode).addChild(currentNode);
							((NodeDiffImpl) currentNode).setParent(ancestorNode);
							
							currentNode = ancestorNode;
							stop = false;
							
							
						}
						
					}
					
				}
				
			}
			
		}
	}

	private CompareResult createCompareResult(final VersionCompareConfiguration configuration, final VersionCompareHierarchyBuilder builder, final Collection<NodeDiff> changedNodes) {
		return builder.createCompareResult(configuration, newHashSet(changedNodes));
	}

	private Map<String, NodeDiff> resolveNodes(final VersionCompareConfiguration configuration, final VersionCompareHierarchyBuilder builder, final IndexDiff diff) {
		
		final Map<String, NodeDiff> nodes = newConcurrentMap();
		parallelForEach(diff.iterator(), new LongCollectionProcedure() {
			public void apply(final long storageKey) {
				final Change change = diff.getChange(storageKey);
				final IBranchPath branchPath = getBranchPath(configuration, diff, storageKey, change);
				final NodeDiff node = builder.createNode(branchPath, storageKey, change);
				nodes.put(node.getId(), node);
			}
		});
		
		return newHashMap(nodes);
	}

	private SetView<String> getIntersectionSuperTypeIds(final Map<String, NodeDiff> nodes, final Set<String> superTypeStorageKeys) {
		return superTypeStorageKeys.size() < nodes.keySet().size() 
				? intersection(superTypeStorageKeys, nodes.keySet()) 
				: intersection(nodes.keySet(), superTypeStorageKeys);
	}
	
	private CompareResult createEmptyResult(final VersionCompareConfiguration configuration) {
		return new CompareResultImpl(configuration, new NodeDiffDerivation(Collections.<NodeDiff>emptySet()));
	}
	
	private IBranchPath getBranchPath(final VersionCompareConfiguration configuration, final IndexDiff diff, final long storageKey, final Change change) {
		final IBranchPath branchPath;
		if (configuration.isThreeWay()) {
			
			if (change.isNew()) {
				
				if (isNewInTarget(storageKey, diff)) {
					branchPath = configuration.getTargetPath();
				} else {
					branchPath = configuration.getSourcePath();
				}
				
			} else if (change.isDeleted()) {
				
				if (isNewInSource(storageKey, diff)) {
					branchPath = createPath(configuration.getSourcePath());
				} else {
					branchPath = configuration.getSourcePath();
				}
				
			} else {
				branchPath = configuration.getTargetPath();
			}
			
		} else {
			branchPath = change.isDeleted() ? configuration.getSourcePath() : configuration.getTargetPath();
		}
		return branchPath;
	}

	private boolean isNewInSource(final long storageKey, final IndexDiff diff) {
		return toThreeWay(diff).getSourceDiff().getNewIds().contains(storageKey);
	}

	private boolean isNewInTarget(final long storageKey, final IndexDiff diff) {
		return toThreeWay(diff).getTargetDiff().getNewIds().contains(storageKey);
	}

	private ThreeWayIndexDiff toThreeWay(final IndexDiff diff) {
		return (ThreeWayIndexDiff) diff;
	}

	private Stopwatch resetAndRestart(final Stopwatch stopwatch) {
		stopwatch.reset();
		stopwatch.start();
		return stopwatch;
	}

	private void logUserAbort() {
		LOGGER.info("User abort.");
	}
	
}