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

import java.util.Set;

/**
 * A widget model that enumerates its list of allowed terminology type IDs.
 * 
 */
public abstract class AllowedTypesWidgetModel extends WidgetModel {

	private static final long serialVersionUID = 5571433119689811914L;

	protected Set<String> allowedTypeIds;

	/**
	 * Default constructor for serialization.
	 */
	protected AllowedTypesWidgetModel() {
	}
	
	/**
	 * Creates a widget model of the specified multiplicity, model type and list of allowed types.
	 * 
	 * @param lowerBound the widget model's lower bound (may not be {@code null})
	 * @param upperBound the widget model's upper bound (may not be {@code null})
	 * @param allowedTypeIds the list of allowed terminology type IDs (may not be {@code null}; may not contain {@code null}
	 * elements)
	 */
	protected AllowedTypesWidgetModel(final LowerBound lowerBound, final UpperBound upperBound,
			final ModelType modelType, final Set<String> allowedTypeIds) {
		
		super(lowerBound, upperBound, modelType);
		this.allowedTypeIds = checkNotNull(allowedTypeIds, "allowedTypeIds");
	}

	/**
	 * @return the list of allowed terminology items
	 */
	public Set<String> getAllowedTypeIds() {
		return allowedTypeIds;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This method was intentionally made abstract; subclasses have to supply a
	 * meaningful implementation.
	 */
	@Override
	public abstract String toString();
}