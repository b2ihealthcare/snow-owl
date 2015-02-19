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

/**
 * The widget model of a concept, serving as a central hub from which associated description and relationship models can
 * be accessed.
 * 
 */
public class ConceptWidgetModel extends WidgetModel {

	private static final long serialVersionUID = 8170524232028520318L;

	private DescriptionContainerWidgetModel descriptionContainerModel;
	private RelationshipGroupContainerWidgetModel relationshipGroupContainerModel;
	private DataTypeContainerWidgetModel dataTypeContainerWidgetModel;
	
	private MappingContainerWidgetModel mappingContainerWidgetModel;

	/**
	 * Default constructor for serialization.
	 */
	public ConceptWidgetModel() {
		super();
	}
	
	/**
	 * Creates a new concept model instance with the specified sub-models.
	 * 
	 * @param descriptionContainerModel the container model collecting description models (may not be {@code null})
	 * @param relationshipGroupWidgetModel the container model collecting relationship groups (may not be {@code null})
	 * @param dataTypeContainerWidgetModel the container model collecting concrete domain elements (may not be {@code null})
	 */
	public ConceptWidgetModel(final DescriptionContainerWidgetModel descriptionContainerModel,
			final RelationshipGroupContainerWidgetModel relationshipGroupWidgetModel,
			final DataTypeContainerWidgetModel dataTypeContainerWidgetModel) {
		
		super(LowerBound.REQUIRED, UpperBound.SINGLE, ModelType.INFRASTRUCTURE);
		this.descriptionContainerModel = checkNotNull(descriptionContainerModel, "descriptionContainerModel");
		this.relationshipGroupContainerModel = checkNotNull(relationshipGroupWidgetModel, "relationshipGroupWidgetModel");
		this.dataTypeContainerWidgetModel = checkNotNull(dataTypeContainerWidgetModel, "dataTypeContainerWidgetModel");
	}
	
	public DescriptionContainerWidgetModel getDescriptionContainerModel() {
		return descriptionContainerModel;
	}
	
	public RelationshipGroupContainerWidgetModel getRelationshipGroupContainerModel() {
		return relationshipGroupContainerModel;
	}
	
	public DataTypeContainerWidgetModel getDataTypeContainerWidgetModel() {
		return dataTypeContainerWidgetModel;
	}
	
	public void setMappingContainerWidgetModel(MappingContainerWidgetModel mappingContainerWidgetModel) {
		this.mappingContainerWidgetModel = mappingContainerWidgetModel;
	}
	
	public MappingContainerWidgetModel getMappingContainerWidgetModel() {
		return mappingContainerWidgetModel;
	}
	
	@Override
	public String toString() {

		return String.format(
				"ConceptWidgetModel [\n"
				+ "    descriptionContainerModel=%s,\n"
				+ "    relationshipGroupContainerModel=%s,\n"
				+ "    dataTypeWidgetModel=%s\n"
				+ "]",
				descriptionContainerModel, relationshipGroupContainerModel, dataTypeContainerWidgetModel);
	}
}