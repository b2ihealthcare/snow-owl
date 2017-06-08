/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.datastore.EscgExpressionConstants.UNRESTRICTED_EXPRESSION;

import java.io.Serializable;
import java.util.Set;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.utils.UnrestrictedStringSet;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;

/**
 * The model that drives the rendering of the relationship selector widget.
 * 
 */
public class RelationshipWidgetModel extends AllowedTypesWidgetModel implements Serializable {

	private static final long serialVersionUID = -545148663567380435L;

	/**
	 * Creates a regular relationship model of the specified multiplicity and list of allowed relationship types, values
	 * and characteristic types.
	 * 
	 * @param lowerBound the widget model's lower bound (may not be {@code null})
	 * @param upperBound the widget model's upper bound (may not be {@code null})
	 * @param allowedTypeIds the set of allowed relationship type IDs (may not be {@code null}; may not contain
	 * {@code null} elements)
	 * @param allowedValueIdsExpression expression representing a set of allowed relationship value IDs (may not be {@code null}).
	 * @param allowedCharacteristicTypeIds the set of allowed relationship characteristic type IDs (may not be
	 * {@code null}; may not contain {@code null} elements)
	 * @return the created relationship model instance
	 */
	public static RelationshipWidgetModel createRegularModel(final LowerBound lowerBound, final UpperBound upperBound, final IBranchPath branchPath,
			final Set<String> allowedTypeIds, final String allowedValueIdsExpression, 
			final Set<String> allowedCharacteristicTypeIds) {

		return new RelationshipWidgetModel(lowerBound, upperBound, ModelType.REGULAR, branchPath, allowedTypeIds, allowedValueIdsExpression,
				allowedCharacteristicTypeIds);
	}

	/**
	 * Creates an unsanctioned relationship model of cardinality 0..*.
	 * @return the created relationship model instance
	 */
	public static RelationshipWidgetModel createUnsanctionedModel(final IBranchPath branchPath) {
		return new RelationshipWidgetModel(LowerBound.OPTIONAL, UpperBound.MULTIPLE, ModelType.UNSANCTIONED, branchPath, 
				UnrestrictedStringSet.INSTANCE, UNRESTRICTED_EXPRESSION, UnrestrictedStringSet.INSTANCE);
	}
	
	private String allowedValueIdsExpression;
	private Set<String> allowedCharacteristicTypeIds;
	private IBranchPath branchPath;

	/**
	 * Default constructor for serialization.
	 */
	protected RelationshipWidgetModel() {
		super();
	}
	
	private RelationshipWidgetModel(final LowerBound lowerBound, final UpperBound upperBound, final ModelType modelType, final IBranchPath branchPath,
			final Set<String> allowedTypeIds, final String allowedValueIdsExpression, 
			final Set<String> allowedCharacteristicTypeIds) { 

		super(lowerBound, upperBound, modelType, allowedTypeIds);
		this.branchPath = branchPath;
		this.allowedValueIdsExpression = allowedValueIdsExpression/*copySet(checkNotNull(allowedValueIds, "allowedValueIds"))*/;
		this.allowedCharacteristicTypeIds = allowedCharacteristicTypeIds/*copySet(checkNotNull(allowedCharacteristicTypeIds, "allowedCharacteristicTypeIds"))*/;
	}
	
	public String getAllowedValueIdsExpression() {
		return allowedValueIdsExpression;
	}
	
	public Set<String> getAllowedCharacteristicTypeIds() {
		return allowedCharacteristicTypeIds;
	}
	
	/**
	 * Checks if the specified identifiers fit into the list of the types and values allowed by this model.
	 * 
	 * @param typeId the type identifier to match (may not be {@code null} or empty)
	 * @param valueId the value identifier to match (may not be {@code null} or empty)
	 * @param characteristicTypeId the characteristic type identifier to match (may not be {@code null} or empty)
	 * @return {@code true} on a positive match, {@code false} otherwise
	 */
	public boolean matches(final String typeId, final String valueId, final String characteristicTypeId) {
		return allowedTypeIds.contains(typeId) && matchesWithValueId(valueId) && allowedCharacteristicTypeIds.contains(characteristicTypeId);
	}

	@Override
	public String toString() {
		
		return String.format(
				"RelationshipWidgetModel [\n"
				+ "                allowedTypeIds=%s,\n"
				+ "                allowedValueIdsExpression=%s\n"
				+ "                allowedCharacteristicTypeIds=%s,\n"
				+ "            ]",
				StringUtils.toString(allowedTypeIds), 
				String.valueOf(allowedValueIdsExpression),
				StringUtils.toString(allowedCharacteristicTypeIds)); 
	}
	
	/*instead of storing a set of concept IDs, we convert the expression to index query and add a concept ID boolean query with occur MUST
	 *if we got a positive integer hit count that means the component ID is in the subset of allowed concept IDs*/
	private boolean matchesWithValueId(final String valueId) {
		// XXX: Method on terminology browser handles REJECT_ALL and UNRESTRICTED expressions
		return getServiceForClass(SnomedTerminologyBrowser.class).contains(branchPath, allowedValueIdsExpression, valueId);
	}
}
