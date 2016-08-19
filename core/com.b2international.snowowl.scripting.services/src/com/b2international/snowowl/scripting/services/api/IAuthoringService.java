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
package com.b2international.snowowl.scripting.services.api;

import java.util.Date;

import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * This service provides limited access to Snow Owl's authoring capabilities.
 * 
 * This class evolves as Snow Owl extension requirements arise.
 * 
 * 
 */
public interface IAuthoringService {

	/**
	 * The unique identifier of the MAIN branch.
	 */
	public static final String MAIN_TASK_ID = "MAIN";
	
	/**
	 * Adds a concrete domain data type to a SNOMED&nbsp;CT concept.
	 * @param conceptId
	 *            SNOMED&nbsp;CT concept identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}.
	 *            Example: {@code isVaccine}.
	 * @param concreteDomainAttributeType
	 *            the type of the attribute. Cannot be {@code null}.
	 * @param value
	 *            the value associated with the concrete domain. Cannot be {@code null}.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)           
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addConcreteDomainDataTypeToConcept(final long conceptId, final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, 
			final Object value, final String characteristicTypeId, final String taskId);

	/**
	 * Adds a boolean concrete domain data type to a SNOMED&nbsp;CT concept.
	 * @param conceptId
	 *            SNOMED&nbsp;CT concept identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}.
	 *            Example: {@code isVaccine}.
	 * @param booleanValue boolean value of the concrete domain.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addBooleanConcreteDomainTypeToConcept(final long conceptId, final String concreteDomainAttributeName, 
			boolean booleanValue, final String characteristicTypeId, final String taskId);

	/**
	 * Adds an integer concrete domain data type to a SNOMED&nbsp;CT concept. 
	 * @param conceptId
	 *            SNOMED&nbsp;CT concept identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}
	 *            Example: {@code numberOfTablets}.
	 * @param intValue the integer value of the concrete domain.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addIntegerConcreteDomainTypeToConcept(final long conceptId, final String concreteDomainAttributeName, 
			int intValue, final String characteristicTypeId, final String taskId);

	/**
	 * Adds a float concrete domain data type to a SNOMED&nbsp;CT concept.
	 * @param conceptId
	 *            SNOMED&nbsp;CT concept identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}
	 *            Example: {@code stengthInFractions}.
	 * @param floatValue decimal value of the concrete domain.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)	
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addFloatConcreteDomainTypeToConcept(final long conceptId, final String concreteDomainAttributeName, 
			float floatValue, final String characteristicTypeId, final String taskId);

	/**
	 * Adds a date concrete domain data type to a SNOMED&nbsp;CT concept.
	 * @param conceptId
	 *            SNOMED&nbsp;CT concept identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}
	 *            Example: {@code startDate}.
	 * @param dateValue datetime value of the concrete domain.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)	
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addDateConcreteDomainTypeToConcept(final long conceptId, final String concreteDomainAttributeName, 
			Date dateValue, final String characteristicTypeId, final String taskId);

	/**
	 * Adds a string type concrete domain data type to a SNOMED&nbsp;CT concept.
	 * @param conceptId
	 *            SNOMED&nbsp;CT concept identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}
	 *            Example: {@code startDate}.
	 * @param stringValue string value of the concrete domain.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)	
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addStringDomainDateTypeToConcept(final long conceptId, final String concreteDomainAttributeName, 
			String stringValue, final String characteristicTypeId, final String taskId);

	/**
	 * Adds a concrete domain data type to a SNOMED&nbsp;CT relationship.
	 * @param relationshipId
	 *            SNOMED&nbsp;CT relationship identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}.
	 *            Example: {@code isVaccine}.
	 * @param concreteDomainAttributeType
	 *            the type of the attribute. Cannot be {@code null}.
	 * @param value
	 *            the value associated with the concrete domain. Cannot be {@code null}.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)	
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addConcreteDomainDataTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, final Object value, 
			final String characteristicTypeId, final String taskId);

	/**
	 * Adds a boolean concrete domain data type to a SNOMED&nbsp;CT relationship.
	 * @param relationshipId
	 *            SNOMED&nbsp;CT relationship identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}.
	 *            Example: {@code isVaccine}.
	 * @param booleanValue boolean value of the concrete domain.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)	
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addBooleanConcreteDomainTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName, 
			boolean booleanValue, final String characteristicTypeId, final String taskId);

	/**
	 * Adds an integer concrete domain data type to a SNOMED&nbsp;CT relationship. 
	 * @param relationshipId
	 *            SNOMED&nbsp;CT relationship identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}
	 *            Example: {@code numberOfTablets}.
	 * @param intValue the integer value of the concrete domain.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)	
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addIntegerConcreteDomainTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName, int intValue, 
			final String characteristicTypeId, final String taskId);

	/**
	 * Adds a float concrete domain data type to a SNOMED&nbsp;CT relationship.
	 * @param relationshipId
	 *            SNOMED&nbsp;CT relationship identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}
	 *            Example: {@code stengthInFractions}.
	 * @param floatValue decimal value of the concrete domain.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.) 
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addFloatConcreteDomainTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName, float floatValue, 
			final String characteristicTypeId, final String taskId);

	/**
	 * Adds a date concrete domain data type to a SNOMED&nbsp;CT relationship.
	 * @param relationshipId
	 *            SNOMED&nbsp;CT relationship identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}
	 *            Example: {@code startDate}.
	 * @param dateValue datetime value of the concrete domain.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)	
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addDateConcreteDomainTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName, 
			Date dateValue, final String characteristicTypeId, final String taskId);

	/**
	 * Adds a string type concrete domain data type to a SNOMED&nbsp;CT relationship.
	 * @param relationshipId
	 *            SNOMED&nbsp;CT relationship identifier to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null}
	 *            Example: {@code startDate}.
	 * @param stringValue string value of the concrete domain.
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)	
	 * @param taskId
	 *            the unique identifier of the task where the changes should be performed. Can be {@code null}. If task identifier is {@code null} the changes will be performed on the MAIN branch.
	 *            Also for performing the concrete domain data type creation on the MAIN branch, {@link #MAIN_TASK_ID} can be used as the MAIN task ID.
	 */
	void addStringDomainDateTypeToRelationship(final long relationshipId, final String concreteDomainAttributeName, String stringValue, 
			final String characteristicTypeId, final String taskId);
	
	/**
	 * Adds a concrete domain data type to a SNOMED&nbsp;CT concept.
	 * <p>
	 * The method uses the default moduleId, which can be set in the preferences page.
	 * The method does not persist the changes in the repository, the caller explicitely needs to call
	 * editingContext.commit() to persist the changes.
	 * 
	 * @param editingContext the editing context for the changes to be performed on
	 * @param concept SNOMED&nbsp;CT component to add the concrete domain data type to. Cannot be
	 * {@code null}.
	 * @param concreteDomainAttributeName the name of the attribute. The format should be camelcase. Cannot be
	 * {@code null} Example: {@code startDate}.
	 * @param concreteDomainAttributeType the concrete domain type, see {@link ConcreteDomainDataType}
	 * @param value value of the concrete domain
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)	
	 */
	void addConcreteDomainDataTypeToConcept(SnomedEditingContext editingContext, final Concept concept,
			final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, 
			final Object value, final String characteristicTypeId);
	
	/**
	 * Adds a concrete domain data type to a SNOMED&nbsp;CT concept using the specified moduleId as module.
	 * <p>
	 * The method does not persist the changes in the repository, the caller explicitely needs to call
	 * editingContext.commit() to persist the changes.
	 * 
	 * @param editingContext the editing context for the changes to be performed on
	 * @param concept SNOMED&nbsp;CT component to add the concrete domain data type to. Cannot be
	 * {@code null}.
	 * @param concreteDomainAttributeName the name of the attribute. The format should be camelcase. Cannot be
	 * {@code null} Example: {@code startDate}.
	 * @param concreteDomainAttributeType the concrete domain type, see {@link ConcreteDomainDataType}
	 * @param value value of the concrete domain
	 * @param moduleId the SNOMED&nbsp;CT ID of the module concept to use
	 * @param characteristicTypeId the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)	
	 */
	void addConcreteDomainDataTypeToConcept(SnomedEditingContext editingContext, final Concept concept,
			final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, 
			final Object value, final String moduleId, final String characteristicTypeId);
		
	/**
	 * Adds a concrete domain data type to a SNOMED&nbsp;CT relationship. The method uses the default moduleId, which can be set in the preferences
	 * page. The method does not persist the changes in the repository, the caller explicitly needs to call editingContext.commit() to persist the
	 * changes.
	 * 
	 * @param editingContext
	 *            the editing context for the changes to be performed on
	 * @param relationship
	 *            SNOMED&nbsp;CT relationship to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null} Example: {@code startDate}.
	 * @param concreteDomainAttributeType
	 *            the concrete domain type, see {@link ConcreteDomainDataType}
	 * @param value
	 *            value of the concrete domain
	 * @param characteristicTypeId
	 *            the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)
	 * 
	 */
	void addConcreteDomainDataTypeToRelationship(SnomedEditingContext editingContext, final Relationship relationship,
			final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, final Object value,
			final String characteristicTypeId);

	/**
	 * Adds a concrete domain data type to a SNOMED&nbsp;CT relationship, the method uses the given moduleId as module. The method does not persist
	 * the changes in the repository, the caller explicitly needs to call editingContext.commit() to persist the changes.
	 * 
	 * @param editingContext
	 *            the editing context for the changes to be performed on
	 * @param relationship
	 *            SNOMED&nbsp;CT relationship to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null} Example: {@code startDate}.
	 * @param concreteDomainAttributeType
	 *            the concrete domain type, see {@link ConcreteDomainDataType}
	 * @param value
	 *            value of the concrete domain
	 * @param moduleId
	 *            the SNOMED&nbsp;CT ID of the module concept to use
	 * @param characteristicTypeId
	 *            the characteristic type SNOMED CT id of the concrete domain element (Defining, etc.)
	 * 
	 */
	void addConcreteDomainDataTypeToRelationship(SnomedEditingContext editingContext, final Relationship relationship,
			final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, final Object value,
			final String moduleId, final String characteristicTypeId);

	/**
	 * Adds a concrete domain data type to a SNOMED&nbsp;CT relationship with additional operator and unit of measurement information. The method uses
	 * the default moduleId, which can be set in the preferences page. The method does not persist the changes in the repository, the caller
	 * explicitly needs to call editingContext.commit() to persist the changes.
	 * 
	 * @param editingContext
	 *            the editing context for the changes to be performed on
	 * @param relationship
	 *            SNOMED&nbsp;CT relationship to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null} Example: {@code startDate}.
	 * @param concreteDomainAttributeType
	 *            the concrete domain type, see {@link ConcreteDomainDataType}
	 * @param value
	 *            the value of the concrete domain
	 * @param uomId
	 *            the SNOMED CT id of the Unit Of Measurement
	 * @param operatorId
	 *            the SNOMED CT id of the operator (e.g. =, <=, <>, etc.)
	 * @param characteristicTypeId
	 *            the characteristic type SNOMED CT id of the concrete domain element (defining, etc.)
	 * 
	 */
	void addConcreteDomainDataTypeToRelationship(SnomedEditingContext editingContext, final Relationship relationship,
			final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, final Object value,
			final String uomId, final String operatorId, final String characteristicTypeId);

	/**
	 * Adds a concrete domain data type to a SNOMED&nbsp;CT relationship with additional operator and unit of measurement information and uses the
	 * given moduleId as module. The method does not persist the changes in the repository, the caller explicitly needs to call
	 * editingContext.commit() to persist the changes.
	 * 
	 * @param editingContext
	 *            the editing context for the changes to be performed on
	 * @param relationship
	 *            SNOMED&nbsp;CT relationship to add the concrete domain data type to. Cannot be {@code null}.
	 * @param concreteDomainAttributeName
	 *            the name of the attribute. The format should be camelcase. Cannot be {@code null} Example: {@code startDate}.
	 * @param concreteDomainAttributeType
	 *            the concrete domain type, see {@link ConcreteDomainDataType}
	 * @param value
	 *            the value of the concrete domain
	 * @param uomId
	 *            the SNOMED CT id of the Unit Of Measurement
	 * @param operatorId
	 *            the SNOMED CT id of the operator (e.g. =, <=, <>, etc.)
	 * @param moduleId
	 *            the SNOMED&nbsp;CT ID of the module concept to use
	 * @param characteristicTypeId
	 *            the characteristic type SNOMED CT id of the concrete domain element (defining, etc.)
	 * 
	 */
	void addConcreteDomainDataTypeToRelationship(SnomedEditingContext editingContext, final Relationship relationship,
			final String concreteDomainAttributeName, final DataType concreteDomainAttributeType, final Object value,
			final String uomId, final String operatorId, final String moduleId, final String characteristicTypeId);
	
}