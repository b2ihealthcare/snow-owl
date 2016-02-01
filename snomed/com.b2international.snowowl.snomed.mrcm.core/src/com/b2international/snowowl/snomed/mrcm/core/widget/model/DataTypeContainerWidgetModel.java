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

import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.collect.Iterables;

/**
 * A container model carrying data type models (possibly other models as well).
 * 
 * @see DescriptionContainerWidgetModel
 * @see RelationshipGroupContainerWidgetModel
 * 
 */
public class DataTypeContainerWidgetModel extends ContainerWidgetModel {

	private static final long serialVersionUID = -4038624197620498824L;

	/**
	 * Default constructor for serialization.
	 */
	protected DataTypeContainerWidgetModel() {
	}
	
	public DataTypeContainerWidgetModel(final List<? extends WidgetModel> children) {
		this("Concrete domain elements", children);
	}
	
	/**
	 * Creates a new infrastructure data type container with the given data type widget models.
	 * 
	 * @param children the list of contained data type models (may not be {@code null}; elements may not be {@code null})
	 */
	public DataTypeContainerWidgetModel(final String label, final List<? extends WidgetModel> children) {
		super(label, children);
	}
	
	/**
	 * Returns the first matching child for the specified label and data type.
	 * 
	 * @param label the label to look for (may not be {@code null})
	 * @param dataType the data type to look for (may not be {@code null})
	 * @return the first matching widget model which accepts both arguments
	 * @throws IllegalStateException if no matching model can be found
	 */
	public DataTypeWidgetModel getFirstMatching(final String label, final DataType dataType) {
		
		checkNotNull(label, "label");
		checkNotNull(dataType, "dataType");
		
		for (final DataTypeWidgetModel candidate : Iterables.filter(getChildren(), DataTypeWidgetModel.class)) {
			if (candidate.matches(label, dataType)) {
				return candidate;
			}
		}
		
		throw new IllegalStateException(MessageFormat.format("No matching concrete domain model found for label ''{0}'', datatype ''{1}''.", 
				label, dataType));
	}
}