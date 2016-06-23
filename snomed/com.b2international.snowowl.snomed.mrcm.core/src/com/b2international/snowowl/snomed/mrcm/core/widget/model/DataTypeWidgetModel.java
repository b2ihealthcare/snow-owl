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

import java.util.Collection;
import java.util.Set;

import com.b2international.snowowl.datastore.utils.UnrestrictedStringSet;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.collect.ImmutableSet;

/**
 * A widget model for data types. The model limits the allowed label and type of a data type widget bean.
 * 
 */
public class DataTypeWidgetModel extends WidgetModel {
	
	private static final long serialVersionUID = 1116493981379628335L;
	private Set<String> allowedLabels;
	private DataType allowedType;
	
	/**
	 * Creates a regular data type model of the specified multiplicity, data type and the only allowed label.
	 * 
	 * @param lowerBound the widget model's lower bound (may not be {@code null})
	 * @param upperBound the widget model's upper bound (may not be {@code null})
	 * @param allowedLabel the label described by the originating rule (may not be {@code null})
	 * @param allowedType the allowed data type (may not be {@code null}
	 * @return the created data type model instance
	 */
	public static DataTypeWidgetModel createRegularModel(final LowerBound lowerBound, final UpperBound upperBound, 
			final String allowedLabel, final DataType allowedType) {
		
		return new DataTypeWidgetModel(lowerBound, upperBound, ModelType.REGULAR, 
				ImmutableSet.of(checkNotNull(allowedLabel, "allowedLabel")), allowedType);
	}

	/**
	 * Creates an unsanctioned data type model with 0..* cardinality and the corresponding type.
	 * @param allowedType the allowed data type (may not be {@code null}
	 * @param allowedDataTypeLabels allowed labels appropriate for the given allowedType parameter 
	 * @return the created data type model instance
	 */
	public static DataTypeWidgetModel createUnsanctionedModel(final DataType allowedType, final Collection<String> allowedDataTypeLabels) {
		return new DataTypeWidgetModel(LowerBound.OPTIONAL, UpperBound.MULTIPLE, ModelType.UNSANCTIONED, 
				allowedDataTypeLabels, allowedType); // FIXME: in the future the set of allowed IDs could be UNRESTRICTED
	}
	
	/**
	 * Creates an infrastructure data type model with 0..* cardinality and the corresponding type. Infrastructure model elements
	 * are not displayed with a warning.
	 * 
	 * @param allowedType the allowed data type (may not be {@code null}
	 * @return the created data type model instance
	 */
	public static DataTypeWidgetModel createInfrastructureModel(final DataType allowedType) {
		return new DataTypeWidgetModel(LowerBound.OPTIONAL, UpperBound.MULTIPLE, ModelType.INFRASTRUCTURE, UnrestrictedStringSet.INSTANCE, allowedType);
	}
	
	/**
	 * Default constructor for serialization.
	 */
	protected DataTypeWidgetModel() {
	}
	
	private DataTypeWidgetModel(final LowerBound lowerBound, final UpperBound upperBound, final ModelType modelType, 
			final Collection<String> allowedLabels, final DataType type) {

		super(lowerBound, upperBound, modelType);
		
		this.allowedLabels = ImmutableSet.copyOf(allowedLabels);
		this.allowedType = checkNotNull(type, "type");
	}

	public Set<String> getAllowedLabels() {
		return allowedLabels;
	}
	
	public DataType getAllowedType() {
		return allowedType;
	}

	/**
	 * Checks if the specified label-data type pair matches the list of allowed labels and type of this model,
	 * respectively.
	 * 
	 * @param label the label to check
	 * @param dataType the datatype to check
	 * @return {@code true} if this model is a match for the specified arguments, {@code false} otherwise
	 */
	public boolean matches(final String label, final DataType dataType) {
		return allowedLabels.contains(label) && allowedType.equals(dataType);
	}

	@Override
	public String toString() {
		
		return String.format(
				"DataTypeWidgetModel [\n"
				+ "            allowedLabels=%s,\n"
				+ "            allowedType=%s\n"
				+ "        ]", 
				allowedLabels, allowedType);
	}
	
}