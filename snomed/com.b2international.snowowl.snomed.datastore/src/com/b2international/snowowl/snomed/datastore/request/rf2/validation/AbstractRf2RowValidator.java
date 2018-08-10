/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.validation;

import java.util.Arrays;
import java.util.List;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * @since 7.0
 */
public abstract class AbstractRf2RowValidator {

	private Rf2ValidationResponseEntity validationEntity;
	private String[] values;
	private List<String> validatableConceptIds = Lists.newArrayList();

	public AbstractRf2RowValidator(Rf2ValidationResponseEntity validationEntity, String[] values) {
		this.validationEntity = validationEntity;
		this.values = values;
	}
	
	public void validateRows(String[] headerColumns) {
		final String isActive = values[2];
		final String moduleId = values[3];
		if (values.length != headerColumns.length) {
			validationEntity.put(Rf2ValidationType.ERROR, Rf2ValidationDefects.INCORRECT_COLUMN_NUMBER.getLabel());
		}
		if (Strings.isNullOrEmpty(isActive)) {
			validationEntity.put(Rf2ValidationType.ERROR, "Missing active flag from release file");
		}
		validateId(moduleId, ComponentCategory.CONCEPT);
		validate(values);
		validatableConceptIds.forEach(id -> validateId(id, ComponentCategory.CONCEPT));
	}
	
	protected abstract void validate(String[] values);
	
	protected void reportIssue(Rf2ValidationType validationType, String validationMessage) {
		validationEntity.put(validationType, validationMessage);
	}
	
	protected void addValidatableConcept(String conceptId) {
		validatableConceptIds.add(conceptId);
	}
	
	protected void addValidatableConcepts(List<String> conceptIds) {
		validatableConceptIds.addAll(conceptIds);
	}
		
	protected void validateId(String id, ComponentCategory expectedCategory) {
		boolean issuesFound = validateIds(id);
		if (!issuesFound) {
			final ComponentCategory componentCategory = SnomedIdentifiers.getComponentCategory(id);
			if (componentCategory != expectedCategory) {
				validationEntity.put(Rf2ValidationType.ERROR, Rf2ValidationDefects.UNEXPECTED_COMPONENT_CATEGORY.getLabel());
			}
		}

	}

	protected boolean validateIds(String...ids) {
		boolean issuesFound = false;
		final List<String> idsToValidate = Arrays.asList(ids);
		for (String id : idsToValidate) {
			if (Strings.isNullOrEmpty(id)) {
				issuesFound = true;
				validationEntity.put(Rf2ValidationType.ERROR, "Missing id from release file");
				break;
			}
			
			try {
				SnomedIdentifiers.validate(id);
			} catch (IllegalArgumentException e) {
				issuesFound = true;
				validationEntity.put(Rf2ValidationType.ERROR, String.format("%s %s", id, Rf2ValidationDefects.INVALID_ID.getLabel()));
				
			}
		}
		return issuesFound;
	}
	
	
	
}
