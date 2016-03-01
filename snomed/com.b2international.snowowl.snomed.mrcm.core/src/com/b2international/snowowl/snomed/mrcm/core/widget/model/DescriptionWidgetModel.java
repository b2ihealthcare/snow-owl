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

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.datastore.utils.UnrestrictedStringSet;

/**
 * A widget model that provides the list of allowed description types for description widget beans.
 * 
 */
public class DescriptionWidgetModel extends AllowedTypesWidgetModel {

	private static final long serialVersionUID = -5039393330694427775L;

	/**
	 * Creates an infrastructure description model of cardinality 1..1 with the specified allowed type.
	 * 
	 * @param allowedTypeIds the allowed type IDs for this model (may not be {@code null})
	 * @return the created description model instance
	 */
	public static DescriptionWidgetModel createInfrastructureModel(final Set<String> allowedTypeIds) {
		return new DescriptionWidgetModel(LowerBound.REQUIRED, UpperBound.SINGLE, ModelType.INFRASTRUCTURE, 
				checkNotNull(allowedTypeIds, "allowedTypeIds"));
	}

	/**
	 * Creates a regular description model of the specified multiplicity and list of allowed description types.
	 * 
	 * @param lowerBound the widget model's lower bound (may not be {@code null})
	 * @param upperBound the widget model's upper bound (may not be {@code null})
	 * @param allowedTypeIds the list of allowed description type IDs (may not be {@code null}; may not contain
	 * {@code null} elements)
	 * @return the created description model instance
	 */
	public static DescriptionWidgetModel createRegularModel(final LowerBound lowerBound, final UpperBound upperBound, 
			final Set<String> allowedTypeIds) {
		return new DescriptionWidgetModel(lowerBound, upperBound, ModelType.REGULAR, 
				checkNotNull(allowedTypeIds, "allowedTypeIds"));
	}

	/**
	 * Default constructor for serialization.
	 */
	protected DescriptionWidgetModel() {
	}	
	
	/**
	 * Creates an unsanctioned description model with a list of all allowed description types.
	 * @return the created description model instance
	 */
	public static DescriptionWidgetModel createUnsanctionedModel() {
		return new DescriptionWidgetModel(LowerBound.OPTIONAL, UpperBound.MULTIPLE, ModelType.UNSANCTIONED, UnrestrictedStringSet.INSTANCE);
	}
	
	private DescriptionWidgetModel(final LowerBound lowerBound, final UpperBound upperBound, final ModelType modelType, 
			final Set<String> allowedTypeIds) {
		
		super(lowerBound, upperBound, modelType, allowedTypeIds);
	}
	
	/**
	 * Checks if the specified description type matches the list of allowed types of this model.
	 * 
	 * @param typeId the SNOMED CT ID of the description type to check
	 * @return {@code true} if this model is a match for the specified argument, {@code false} otherwise
	 */
	public boolean matches(final String typeId) {
		return allowedTypeIds.contains(typeId);
	}
	
	@Override
	public String toString() {
		
		return String.format(
				"DescriptionWidgetModel [\n"
				+ "            allowedTypeIds=%s\n"
				+ "        ]", 
				StringUtils.toString(allowedTypeIds));
	}
}