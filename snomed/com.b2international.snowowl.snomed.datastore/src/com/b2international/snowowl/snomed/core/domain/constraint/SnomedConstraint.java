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
package com.b2international.snowowl.snomed.core.domain.constraint;

import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.mrcm.ConstraintForm;
import com.b2international.snowowl.snomed.mrcm.ConstraintStrength;

/**
 * The component representation of an MRCM constraint.
 * 
 * @since 6.5
 */
public final class SnomedConstraint extends SnomedConceptModelComponent implements IComponent {

	private ConstraintStrength strength;
	private String validationMessage;
	private String description;
	private ConstraintForm form;
	private SnomedConceptSetDefinition domain;
	private SnomedPredicate predicate;
	
	public ConstraintStrength getStrength() {
		return strength;
	}
	
	public void setStrength(ConstraintStrength strength) {
		this.strength = strength;
	}
	
	public String getValidationMessage() {
		return validationMessage;
	}
	
	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ConstraintForm getForm() {
		return form;
	}
	
	public void setForm(ConstraintForm form) {
		this.form = form;
	}
	
	public SnomedConceptSetDefinition getDomain() {
		return domain;
	}
	
	public void setDomain(SnomedConceptSetDefinition domain) {
		this.domain = domain;
	}
	
	public SnomedPredicate getPredicate() {
		return predicate;
	}
	
	public void setPredicate(SnomedPredicate predicate) {
		this.predicate = predicate;
	}
}
