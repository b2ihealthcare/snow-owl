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
package com.b2international.snowowl.datastore.index.diff;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.b2international.commons.hierarchy.Derivation;
import com.b2international.snowowl.core.api.IBranchPath;

/**
 * {@link CompareResult} implementation.
 *
 */
public class CompareResultImpl implements CompareResult {

	private static final long serialVersionUID = -3113949964737709280L;

	private final Derivation<NodeDiff> changes;
	private final VersionCompareConfiguration configuration;
	private final CompareStatistics statistics;

	public CompareResultImpl(final CompareResult result, final Iterable<NodeDiff> filteredNodes) {
		this(checkNotNull(result, "result").getConfiguration(), createFilteredDerivation(result.getChanges(), checkNotNull(filteredNodes, "filteredNodes")));
	}

	public CompareResultImpl(final VersionCompareConfiguration configuration, final Derivation<NodeDiff> changes) {
		this.configuration = checkNotNull(configuration, "configuration");
		this.changes = checkNotNull(changes, "changes");
		statistics = createStatistics();
	}

	@Override
	public Derivation<NodeDiff> getChanges() {
		return changes;
	}

	@Override
	public IBranchPath getSourcePath() {
		return configuration.getSourcePath();
	}

	@Override
	public IBranchPath getTargetPath() {
		return configuration.getTargetPath();
	}

	@Override
	public String getRepositoryUuid() {
		return configuration.getRepositoryUuid();
	}
	
	@Override
	public VersionCompareConfiguration getConfiguration() {
		return configuration;
	}
	
	@Override
	public Iterator<NodeDiff> iterator() {
		return changes.iterator();
	}

	@Override
	public int size() {
		return changes.size();
	}

	@Override
	public CompareStatistics getStatistics() {
		return statistics;
	}
	
	private static Derivation<NodeDiff> createFilteredDerivation(final Derivation<NodeDiff> changes, final Iterable<NodeDiff> filteredNodes) {
		final Set<NodeDiff> nodesToRemove = collectNodesToRemove(filteredNodes);
		final Collection<NodeDiff> allElements = newArrayList(changes.getAllElements());
		for (final Iterator<NodeDiff> itr = allElements.iterator(); itr.hasNext(); /**/) {
			if (nodesToRemove.contains(itr.next())) {
				itr.remove();
			}
		}
		return new NodeDiffDerivation(allElements);
	}

	private static Set<NodeDiff> collectNodesToRemove(final Iterable<NodeDiff> filteredNodes) {
		final Set<NodeDiff> nodesToRemove = newHashSet(filteredNodes);
		for (final NodeDiff diff : filteredNodes) {
			nodesToRemove.addAll(collectNodesToRemove(diff.getChildren()));
		}
		return nodesToRemove;
	}

	private CompareStatistics createStatistics() {
		int newCount = 0;
		int dirtyCount = 0;
		int detachedCount = 0;
		for (final NodeDiff diff : changes.getAllElements()) {
			if (!diff.hasChanged()) {
				continue;
			}
			if (diff.isNew()) {
				newCount++;
			} else if (diff.isDeleted()) {
				detachedCount++;
			} else {
				dirtyCount++;
			}
		}
		return new CompareStatisticsImpl(newCount, dirtyCount, detachedCount);
	}

}