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

import static com.b2international.commons.collections.Collections3.toList;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.sort;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.b2international.commons.Change;
import com.b2international.commons.ChangeKind;
import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.ExtendedComponent;

/**
 * Basic {@link NodeDiff} implementation wrapping an individual index diff.
 */
public class NodeDiffImpl implements NodeDiff, Serializable {

	private static final long serialVersionUID = -6936927614653002197L;
	
	private final short terminologyComponentId;
	private final long storageKey;
	private final String id;
	private final String label;
	@Nullable private final String iconId;
	@Nullable private NodeDiff parent;
	private final ChangeKind changeKind;
	private Collection<NodeDiff> children;
	
	/**
	 * Returns with a new {@link NodeDiff} instance as the copy of the given argument.
	 * @param nodeDiff the node difference to copy.
	 * @return the new copy instance.
	 */
	public static NodeDiff copyOf(final NodeDiff nodeDiff) {
		checkNotNull(nodeDiff, "nodeDiff");
		final NodeDiffImpl copy = new NodeDiffImpl(
				nodeDiff.getTerminologyComponentId(), 
				nodeDiff.getStorageKey(), 
				nodeDiff.getId(), 
				nodeDiff.getLabel(), 
				nodeDiff.getIconId(), 
				nodeDiff.getParent(), 
				nodeDiff);
		copy.addChildren(nodeDiff.getChildren());
		return copy;
	}
	
	public NodeDiffImpl(final long storageKey, final ExtendedComponent component, @Nullable final NodeDiff parent, final Change change) {
		this(checkNotNull(component, "component").getTerminologyComponentId(), storageKey, component.getId(), component.getLabel(), component.getIconId(), parent,  checkNotNull(change, "change"));
	}
	
	public NodeDiffImpl(final short terminologyComponentId, final long storageKey, final String id, final String label, 
			@Nullable final String iconId, @Nullable final NodeDiff parent, final Change change) {
		
		this.terminologyComponentId = terminologyComponentId;
		this.storageKey = storageKey;
		this.id = checkNotNull(id, "id");
		this.label = checkNotNull(label, "label");
		this.iconId = iconId;
		this.parent = parent;
		this.children = newHashSet();
		this.changeKind = checkNotNull(change, "change").getChange();
	}

	@Override
	public short getTerminologyComponentId() {
		return terminologyComponentId;
	}

	@Override
	public long getStorageKey() {
		return storageKey;
	}

	@Override
	public String getId() {
		return id;
	}
	
	@Override
	@Nullable
	public String getIconId() {
		return iconId;
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	@Nullable
	public NodeDiff getParent() {
		return parent;
	}

	@Override
	public Collection<NodeDiff> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	@Override
	public boolean isDirty() {
		return changeKind.isDirty();
	}

	@Override
	public boolean isNew() {
		return changeKind.isNew();
	}

	@Override
	public boolean isDeleted() {
		return changeKind.isDeleted();
	}

	@Override
	public boolean hasChanged() {
		return changeKind.hasChanged();
	}

	@Override
	public ChangeKind getChange() {
		return changeKind;
	}

	/**
	 * Removes the child argument from the current diff.
	 * @param child the child to remove.
	 * @return {@link Set#remove(Object)}
	 */
	public boolean removeChild(final NodeDiff child) {
		return children.remove(checkNotNull(child, "child"));
	}
	
	/**
	 * Registers the child argument as a descendant of the current diff.
	 * @param child the new child node to add.
	 * @return {@link Collection#add(Object)}
	 */
	public boolean addChild(final NodeDiff child) {
		return children.add(checkNotNull(child, "child"));
	}
	
	/**
	 * Registers the collection of children argument as the descendants of the current diff.
	 * @param children the new children nodes to add.
	 * @return {@link Collection#addAll(Object)}
	 */
	public boolean addChildren(final Collection<? extends NodeDiff> children) {
		return this.children.addAll(checkNotNull(children, "children"));
	}
	
	/**
	 * Sets given {@link NodeDiff node} as the parent of the current node.
	 * @param parent the parent to set.
	 */
	public void setParent(final NodeDiff parent) {
		this.parent = parent;
	}

	/**
	 * Sorts the child nodes of the current node with the given {@link Comparator} argument.
	 * @param comparator the comparator to determine the order of the child nodes.
	 */
	@SuppressWarnings("unchecked")
	public void sortChildren(@SuppressWarnings("rawtypes") final Comparator comparator) {
		children = toList(children);
		sort((List<?>) children, comparator);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (int) (storageKey ^ (storageKey >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NodeDiffImpl other = (NodeDiffImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (storageKey != other.storageKey)
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder("Label: ").append(label)
			.append(" ID: ").append(id)
			
			.append(" Change kind: ").append(changeKind)
			.append(" Icon ID: ").append(iconId)
			.append(" Component type: ").append(terminologyComponentId);

		if (ChangeKind.UNCHANGED != changeKind) {
			sb.append(" Storage key: ").append(storageKey);
		}
		
		if (null != parent) {
			sb.append("\nParent: ");
			sb.append("\n\t");
			sb.append(parent.getLabel());
			sb.append("|");
			sb.append(parent.getId());
		}

		if (!CompareUtils.isEmpty(children)) {
			sb.append("\nChildren:");
		}
		
		for (final NodeDiff child : children) {
			sb.append("\n\t");
			sb.append(child.getLabel());
			sb.append("|");
			sb.append(child.getId());
		}
		
		if (!CompareUtils.isEmpty(children)) {
			sb.append("\n");
		}
		
		return sb.toString();
	}

}