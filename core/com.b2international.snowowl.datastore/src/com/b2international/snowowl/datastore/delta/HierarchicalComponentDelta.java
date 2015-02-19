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
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Class representing a {@link ComponentDelta} with additional parentage information.
 * @see ComponentDelta
 * @see Serializable
 */
public class HierarchicalComponentDelta extends ComponentDelta implements Serializable, HierarchicalElement<HierarchicalComponentDelta> {

	private static final long serialVersionUID = -6346847096632018833L;

	private final Collection<HierarchicalComponentDelta> children;
	private HierarchicalComponentDelta parent;

	public HierarchicalComponentDelta(final ComponentDelta delta) {
		
		this(
				Preconditions.checkNotNull(Preconditions.checkNotNull(delta, "Component delta argument cannot be null").getId(), "ID argument cannot be null."),
				delta.getCdoId(),
				Preconditions.checkNotNull(delta.getBranchPath(), "Branch path argument cannot be null."),
				Preconditions.checkNotNull(delta.getLabel(), "Label argument cannot be null."),
				Preconditions.checkNotNull(delta.getIconId(), "Component icon ID argument cannot be null."),
				delta.getTerminologyComponentId(),
				delta.getCodeSystemOID(),
				Preconditions.checkNotNull(delta.getChange(), "Component change kind argument cannot be null."));
		
	}
	
	public HierarchicalComponentDelta(final String id, final long cdoId, final IBranchPath branchPath, final String label, final String iconId, final short terminologyComponentId, final String codeSystemOID) {
		
		this(
				Preconditions.checkNotNull(id, "ID argument cannot be null."),
				cdoId,
				Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null."),
				Preconditions.checkNotNull(label, "Label argument cannot be null."),
				Preconditions.checkNotNull(iconId, "Component icon ID argument cannot be null."),
				terminologyComponentId,
				codeSystemOID,
				ChangeKind.UNCHANGED);
		
	}
	
	public HierarchicalComponentDelta(final String id, final long cdoId, final IBranchPath branchPath, final String label, final String iconId, short terminologyComponentId, final String codeSystemOID, final ChangeKind change) {
		
		super(
				Preconditions.checkNotNull(id, "ID argument cannot be null."),
				cdoId,
				Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null."),
				Preconditions.checkNotNull(label, "Label argument cannot be null."),
				Preconditions.checkNotNull(iconId, "Component icon ID argument cannot be null."),
				terminologyComponentId,
				codeSystemOID,
				Preconditions.checkNotNull(change, "Component change kind argument cannot be null."));
		
		children = Lists.newArrayList();
		
	}
	
	/**
	 * Returns with a collection of descendants.
	 */
	public Collection<HierarchicalComponentDelta> getChildren() {
		return children;
	}
	
	/**
	 * Returns with the ancestor of the current component.
	 */
	public HierarchicalComponentDelta getParent() {
		return parent;
	}
	
	/**
	 * Sets the ancestor of the current component.
	 */
	public void setParent(HierarchicalComponentDelta parent) {
		this.parent = parent;
	}
	
}