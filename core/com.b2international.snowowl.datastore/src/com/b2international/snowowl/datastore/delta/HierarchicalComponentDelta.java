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
package com.b2international.snowowl.datastore.delta;

import java.io.Serializable;
import java.util.Collection;

import com.b2international.commons.ChangeKind;
import com.b2international.commons.hierarchy.HierarchicalElement;
import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.collect.Lists;

/**
 * Class representing a {@link ComponentDelta} with additional parentage information.
 * 
 * @see ComponentDelta
 */
public class HierarchicalComponentDelta extends ComponentDelta implements Serializable, HierarchicalElement<HierarchicalComponentDelta> {

	private static final long serialVersionUID = -6346847096632018833L;

	private HierarchicalComponentDelta parent;
	private final Collection<HierarchicalComponentDelta> children = Lists.newArrayList();

	public HierarchicalComponentDelta(final String id, 
			final long cdoId, 
			final IBranchPath branchPath,
			final String label, 
			final String iconId, 
			final short terminologyComponentId, 
			final String codeSystemOID) {

		this(id, cdoId, branchPath, label, iconId, terminologyComponentId, codeSystemOID, ChangeKind.UNCHANGED);
	}

	public HierarchicalComponentDelta(final String id, 
			final long cdoId, 
			final IBranchPath branchPath, 
			final String label, 
			final String iconId, 
			final short terminologyComponentId, 
			final String codeSystemOID, 
			final ChangeKind change) {

		super(id, cdoId, branchPath, label, iconId, terminologyComponentId, codeSystemOID, change);
	}

	/**
	 * Returns a collection of direct children.
	 */
	public Collection<HierarchicalComponentDelta> getChildren() {
		return children;
	}

	/**
	 * Returns the parent of the component, if set.
	 */
	public HierarchicalComponentDelta getParent() {
		return parent;
	}

	/**
	 * Sets the parent of the component.
	 */
	public void setParent(final HierarchicalComponentDelta parent) {
		this.parent = parent;
	}
}
