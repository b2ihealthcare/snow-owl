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

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.ConstraintForm;
import com.b2international.snowowl.snomed.mrcm.ConstraintStrength;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;

/**
 * The component representation of an MRCM constraint.
 * 
 * @since 6.5
 */
public final class SnomedConstraint extends SnomedConceptModelComponent implements IComponent {

	public static final String PROP_STRENGTH = "strength";
	public static final String PROP_FORM = "form";
	public static final String PROP_VALIDATION_MESSAGE = "validationMessage";
	public static final String PROP_DESCRIPTION = "description";

	private ConstraintStrength strength;
	private String validationMessage;
	private String description;
	private ConstraintForm form;
	private SnomedConceptSetDefinition domain;
	private SnomedPredicate predicate;

	public ConstraintStrength getStrength() {
		return strength;
	}

	public void setStrength(final ConstraintStrength strength) {
		this.strength = strength;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage(final String validationMessage) {
		this.validationMessage = validationMessage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public ConstraintForm getForm() {
		return form;
	}

	public void setForm(final ConstraintForm form) {
		this.form = form;
	}

	public SnomedConceptSetDefinition getDomain() {
		return domain;
	}

	public void setDomain(final SnomedConceptSetDefinition domain) {
		this.domain = domain;
	}

	public SnomedPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(final SnomedPredicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public AttributeConstraint createModel() {
		return MrcmFactory.eINSTANCE.createAttributeConstraint();
	}

	@Override
	public AttributeConstraint applyChangesTo(final ConceptModelComponent existingModel) {
		final AttributeConstraint updatedModel = (existingModel instanceof AttributeConstraint)
				? (AttributeConstraint) existingModel
				: createModel();

		updatedModel.setActive(isActive());
		updatedModel.setAuthor(getAuthor());
		updatedModel.setDescription(getDescription());
		updatedModel.setDomain(getDomain().applyChangesTo(updatedModel.getDomain()));
		updatedModel.setEffectiveTime(EffectiveTimes.toDate(getEffectiveTime()));
		updatedModel.setForm(getForm());
		updatedModel.setPredicate(getPredicate().applyChangesTo(updatedModel.getPredicate()));
		updatedModel.setStrength(getStrength());
		updatedModel.setUuid(getId());
		updatedModel.setValidationMessage(getValidationMessage());

		return updatedModel;
	}

	@Override
	public SnomedConstraint deepCopy(final Date date, final String userName) {
		final SnomedConstraint copy = new SnomedConstraint();

		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setDescription(getDescription());
		if (getDomain() != null) { copy.setDomain(getDomain().deepCopy(date, userName)); }
		copy.setEffectiveTime(date.getTime());
		copy.setForm(getForm());
		copy.setId(UUID.randomUUID().toString());
		if (getPredicate() != null) { copy.setPredicate(getPredicate().deepCopy(date, userName)); }
		copy.setStrength(getStrength());
		copy.setValidationMessage(getValidationMessage());

		return copy;
	}

	@Override
	public void collectConceptIds(final Collection<String> conceptIds) {
		if (getDomain() != null) { getDomain().collectConceptIds(conceptIds); }
		if (getPredicate() != null) { getPredicate().collectConceptIds(conceptIds); }
	}

	@Override
	public String validate() {
		final String parentMessage = super.validate();
		if (parentMessage != null) { return parentMessage; }

		if (getStrength() == null) { return String.format("Strength should be set on %s with UUID %s.", displayName(), getId()); }
		if (getForm() == null) { return String.format("Applicable form should be set on %s with UUID %s.", displayName(), getId()); }
		if (getDomain() == null) { return String.format("A domain should be specified for %s with UUID %s.", displayName(), getId()); }
		if (getPredicate() == null) { return String.format("A predicate should be specified for %s with UUID %s.", displayName(), getId()); }

		final String domainMessage = getDomain().validate();
		if (domainMessage != null) { return domainMessage; }
		final String predicateMessage = getPredicate().validate();
		if (predicateMessage != null) { return predicateMessage; }

		return null;
	}

	@Override
	public int structuralHashCode() {
		return 31 * super.hashCode() + Objects.hash(description, domain, form, predicate, strength, validationMessage);
	}

	@Override
	public boolean structurallyEquals(final Object obj) {
		if (this == obj) { return true; }
		if (!super.structurallyEquals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedConstraint other = (SnomedConstraint) obj;

		if (!Objects.equals(description, other.description)) { return false; }
		if (structurallyEquals(domain, other.domain)) { return false; }
		if (form != other.form) { return false; }
		if (structurallyEquals(predicate, other.predicate)) { return false; }
		if (strength != other.strength) { return false; }
		if (!Objects.equals(validationMessage, other.validationMessage)) { return false; }
		return true;
	}
}
