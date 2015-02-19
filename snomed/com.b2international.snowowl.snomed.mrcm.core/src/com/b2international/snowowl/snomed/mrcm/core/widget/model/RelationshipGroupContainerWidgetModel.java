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
package com.b2international.snowowl.snomed.mrcm.core.widget.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.List;

import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupWidgetModel.GroupFlag;
import com.google.common.collect.Iterables;

/**
 * A container model carrying relationship group models.
 * 
 */
public class RelationshipGroupContainerWidgetModel extends DataTypeContainerWidgetModel {

	private static final long serialVersionUID = -663705562436368091L;

	/**
	 * Default constructor for serialization.
	 */
	protected RelationshipGroupContainerWidgetModel() {
	}
	
	/**
	 * Creates a new infrastructure relationship group container with the given relationship group widget models.
	 * 
	 * @param children the list of contained relationship group models (may not be {@code null}; elements may not be
	 * {@code null})
	 */
	public RelationshipGroupContainerWidgetModel(final List<? extends WidgetModel> children) {
		super("Properties", children);
	}
	
	/**
	 * Returns the first matching relationship group widget model for the specified group flag.
	 * 
	 * @param groupFlag the group flag to look for
	 * @return the first matching relationship group widget model which has the same group flag as the argument
	 * @throws IllegalStateException if no matching model can be found
	 */
	public RelationshipGroupWidgetModel getFirstMatching(final GroupFlag groupFlag) {
		
		checkNotNull(groupFlag, "groupFlag");
		
		for (final RelationshipGroupWidgetModel candidate : Iterables.filter(getChildren(), RelationshipGroupWidgetModel.class)) {
			if (candidate.matches(groupFlag)) {
				return candidate;
			}
		}
		
		throw new IllegalStateException(MessageFormat.format("No relationship group widget model found for group flag ''{0}''.", groupFlag));
	}
}