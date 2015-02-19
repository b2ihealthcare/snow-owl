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

import com.google.common.base.Preconditions;

/**
 * Composite component delta fusing a {@link ComponentDelta source} and a target component.
 * @see ComponentDelta
 */
public class CompositeComponentDelta extends HierarchicalComponentDelta implements Serializable {

	private static final long serialVersionUID = -2874126116190648942L;

	private final ComponentDelta target;

	public CompositeComponentDelta(final ComponentDelta source, final ComponentDelta target) {
		
		super(
				Preconditions.checkNotNull(source, "Source component delta argument cannot be null.").getId(), 
				source.getCdoId(),
				source.getBranchPath(),
				source.getLabel(), 
				source.getIconId(),
				source.getTerminologyComponentId(),
				source.getCodeSystemOID(),
				source.getChange());
		
		this.target = Preconditions.checkNotNull(target, "Target component delta argument cannot be null.");
		
	}

	/**
	 * Returns with the target {@link ComponentDelta delta}.
	 */
	public ComponentDelta getTarget() {
		return target;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.ComponentDelta#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("Target: ");
		sb.append(target.toString());
		return sb.toString();
	}
	

}