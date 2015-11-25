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

import static com.b2international.commons.ChangeKind.UNCHANGED;
import static com.b2international.commons.collections.Collections3.toSet;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.NO_STORAGE_KEY;
import static com.b2international.snowowl.datastore.version.DefaultNodeDiffFilter.DEFAULT;
import static com.b2international.snowowl.datastore.version.NodeDiffPredicate.HAS_CHANGED_PREDICATE;
import static com.b2international.snowowl.datastore.version.NodeDiffPredicate.HAS_PARENT_PREDICATE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import com.b2international.commons.Change;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.api.ExtendedComponent;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentIconIdProvider;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.core.api.browser.ExtendedComponentProvider;
import com.b2international.snowowl.core.api.browser.SuperTypeIdProvider;
import com.b2international.snowowl.core.api.component.IdProvider;
import com.b2international.snowowl.core.api.component.LabelProvider;
import com.b2international.snowowl.datastore.index.diff.CompareResult;
import com.b2international.snowowl.datastore.index.diff.CompareResultImpl;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.b2international.snowowl.datastore.index.diff.NodeDiffDerivation;
import com.b2international.snowowl.datastore.index.diff.NodeDiffImpl;
import com.b2international.snowowl.datastore.index.diff.VersionCompareConfiguration;
import com.google.common.base.Predicate;

/**
 * Basic component hierarchy builder.
 */
public abstract class VersionCompareHierarchyBuilderImpl implements VersionCompareHierarchyBuilder {

	/**
	 * This method is a shortcut in recursion when hierarchy has to be built among the nodes.
	 * Always return with {@code false} by default. Clients may override to ensure better performance.
	 * <p> {@inheritDoc} 
	 */
	@Override
	public boolean isRoot(final NodeDiff node) {
		return false;
	}
	
	@Override
	public NodeDiff createNode(final IBranchPath branchPath, final long storageKey, final Change change) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(change, "changeKind");
		checkArgument(storageKey > NO_STORAGE_KEY, "Storage key should be a non negative long value.");
		
		final ExtendedComponent component = getExtendedComponentProvider().getExtendedComponent(branchPath, storageKey);
		return new NodeDiffImpl(storageKey, component, null, change);
	}
	
	@Override
	public NodeDiff createUnchangedNode(final IBranchPath branchPath, final String componentId) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(componentId, "componentId");
		
		final String iconId = getIconIdProvider().getIconId(branchPath, componentId);
		final String label = getNameProvider().getComponentLabel(branchPath, componentId);
		return new NodeDiffImpl(getTerminologyComponentId(), NO_STORAGE_KEY, componentId, label, iconId, null, UNCHANGED);
	}

	protected abstract IComponentIconIdProvider<String> getIconIdProvider();
	
	protected abstract IComponentNameProvider getNameProvider();
	
	protected abstract short getTerminologyComponentId();

	@Override
	public Set<String> getSuperTypeIds(final IBranchPath branchPath, final String componentId) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(componentId, "componentId");
		
		return toSet(getSuperTypeIdProvider().getSuperTypeIds(branchPath, componentId));
	}
	
	@Override
	public CompareResult createCompareResult(final VersionCompareConfiguration configuration, final Collection<NodeDiff> changedNodes) {

		for (final NodeDiff diff : checkNotNull(changedNodes, "changedNodes")) {
			sortChildNodes((NodeDiffImpl) diff, getComparator());
		}

		return new CompareResultImpl(configuration, new NodeDiffDerivation(changedNodes, getComparator()));
		
	}

	@Override
	public void collapseHierarchy(Iterable<NodeDiff> nodeDiffs) {
		DEFAULT.filter(nodeDiffs, getNodeFilterPredicate());
	}
	
	/**Returns with the {@link ExtendedComponentProvider extended component provider}.*/
	protected abstract ExtendedComponentProvider getExtendedComponentProvider();
	
	/**Returns with the {@link SuperTypeIdProvider} for building the parentage among the nodes.*/
	protected abstract SuperTypeIdProvider<String> getSuperTypeIdProvider();

	/**
	 * Returns with the predicate for collapsing the hierarchy after resolving the components.
	 * @return the predicate to filter and collapse the resolved component hierarchy.
	 */
	protected Predicate<NodeDiff> getNodeFilterPredicate() {
		return and(HAS_PARENT_PREDICATE, not(HAS_CHANGED_PREDICATE));
	}
	
	/**Returns with the comparator for sorting the nodes in the hierarchy.*/
	protected Comparator<NodeDiff> getComparator() {
		return LABEL_COMPARATOR;
	}

	/**Sorts the child nodes of the node diff argument using the given comparator*/
	private void sortChildNodes(final NodeDiffImpl diff, final Comparator<NodeDiff> comparator) {
		diff.sortChildren(comparator);
	}
	
	/**Label comparator.*/
	protected static final Comparator<NodeDiff> LABEL_COMPARATOR = new Comparator<NodeDiff>() {
		
		private final Comparator<LabelProvider> delegate = // 
				ComponentUtils.getLabelOrdering();
		
		@Override public int compare(final NodeDiff o1, final NodeDiff o2) {
			return delegate.compare(o1, o2);
		}
	};
	
	/**ID comparator.*/
	protected static final Comparator<NodeDiff> ID_COMPARATOR = new Comparator<NodeDiff>() {
		
		private final Comparator<IdProvider<String>> delegate = // 
				ComponentUtils.<String, IdProvider<String>>getIdOrdering();
		
		@Override public int compare(final NodeDiff o1, final NodeDiff o2) {
			return delegate.compare(o1, o2);
		}
	};
	
	
}