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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.constraint.CompositeDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.ConceptSetDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.EnumeratedDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.HierarchyDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.PredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.ReferenceSetDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.RelationshipDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintPredicateType;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * The component representation of an MRCM constraint.
 * 
 * @since 6.5
 */
@TerminologyComponent(
	id = SnomedTerminologyComponentConstants.CONSTRAINT,
	shortId = SnomedTerminologyComponentConstants.CONSTRAINT_NUMBER,
	componentCategory = ComponentCategory.UNKNOWN,
	name = "SNOMED CT MRCM Constraint",
	docType = SnomedConstraintDocument.class
)
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

	public boolean applyChangesTo(final SnomedConstraintDocument.Builder updatedModel) {

		// Examine the inner predicate in case it is wrapped in cardinality restrictions
		SnomedPredicate predicate = getPredicate();
		if (predicate instanceof SnomedCardinalityPredicate) {
			predicate = ((SnomedCardinalityPredicate) predicate).getPredicate();
		}
		
		final ConceptSetDefinitionFragment domainFragment = getDomain().createModel();
		final PredicateFragment predicateFragment = getPredicate().createModel();
		/* 
		 * Collect SCT identifiers and keys seen in the constraint's domain part, which will be used when we
		 * are looking for applicable constraints.
		 */
		final Set<String> selfIds = newHashSet();
		final Set<String> descendantIds = newHashSet();
		final Set<String> refSetIds = newHashSet();
		final Set<String> relationshipKeys = newHashSet(); // "typeId=destinationId" format
		collectIds(domainFragment, selfIds, descendantIds, refSetIds, relationshipKeys);
		
		updatedModel.id(getId());
		updatedModel.active(isActive());
		updatedModel.author(getAuthor());
		updatedModel.description(getDescription());
		updatedModel.effectiveTime(getEffectiveTime());
		updatedModel.form(getForm());
		updatedModel.strength(getStrength());
		updatedModel.validationMessage(getValidationMessage());
		updatedModel.domain(domainFragment);
		updatedModel.predicate(predicateFragment);
		updatedModel.predicateType(SnomedConstraintPredicateType.typeOf(predicate));
		updatedModel.selfIds(selfIds);
		updatedModel.descendantIds(descendantIds);
		updatedModel.refSetIds(refSetIds);
		updatedModel.relationshipKeys(relationshipKeys);

		return true;
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
		return 31 * super.structuralHashCode() + structuralHashCode(description, domain, form, predicate, strength, validationMessage);
	}

	@Override
	public boolean structurallyEquals(final SnomedConceptModelComponent obj) {
		if (this == obj) { return true; }
		if (!super.structurallyEquals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedConstraint other = (SnomedConstraint) obj;

		if (!Objects.equals(description, other.description)) { return false; }
		if (!structurallyEquals(domain, other.domain)) { return false; }
		if (form != other.form) { return false; }
		if (!structurallyEquals(predicate, other.predicate)) { return false; }
		if (strength != other.strength) { return false; }
		if (!Objects.equals(validationMessage, other.validationMessage)) { return false; }
		return true;
	}
	
	/**
	 * Collects key concept IDs from the specified concept set definition. Results are aggregated in the
	 * given sets.
	 * 
	 * @param definition
	 *            the definition to parse
	 * @param selfIds
	 *            the definition applies to these concepts
	 * @param descendantIds
	 *            the definition applies to the descendants of these concepts
	 * @param refSetIds
	 *            the definition applies to members of these reference sets
	 * @param relationshipKeys
	 *            the definition applies to concepts that include a relationship with these
	 *            type-destination values
	 */
	private static void collectIds(final ConceptSetDefinitionFragment definition, 
			final Set<String> selfIds, 
			final Set<String> descendantIds, 
			final Set<String> refSetIds, 
			final Set<String> relationshipKeys) {
		
		if (definition instanceof HierarchyDefinitionFragment) {
			HierarchyDefinitionFragment hierarchyDefinition = (HierarchyDefinitionFragment) definition;
			final String focusConceptId = hierarchyDefinition.getConceptId();
			final HierarchyInclusionType inclusionType = hierarchyDefinition.getInclusionType();

			switch (inclusionType) {
				case SELF:
					selfIds.add(focusConceptId);
					break;
				case DESCENDANT:
					descendantIds.add(focusConceptId);
					break;
				case SELF_OR_DESCENDANT:
					selfIds.add(focusConceptId);
					descendantIds.add(focusConceptId);
					break;
				default: 
					throw new IllegalStateException("Unexpected hierarchy inclusion type '" + inclusionType + "'.");
			}
		} else if (definition instanceof EnumeratedDefinitionFragment) {
			selfIds.addAll(((EnumeratedDefinitionFragment) definition).getConceptIds());
		} else if (definition instanceof ReferenceSetDefinitionFragment) {
			refSetIds.add(((ReferenceSetDefinitionFragment) definition).getRefSetId());			
		} else if (definition instanceof CompositeDefinitionFragment) {
			for (final ConceptSetDefinitionFragment childDefinition : ((CompositeDefinitionFragment) definition).getChildren()) {
				collectIds(childDefinition, selfIds, descendantIds, refSetIds, relationshipKeys);
			}
		} else if (definition instanceof RelationshipDefinitionFragment) {
			final String typeId = ((RelationshipDefinitionFragment) definition).getTypeId();
			final String destinationId = ((RelationshipDefinitionFragment) definition).getDestinationId();
			relationshipKeys.add(String.format("%s=%s", typeId, destinationId));			
		}
	}

	public Request<TransactionContext, String> toCreateRequest() {
		return SnomedRequests.prepareNewConstraint().setConstraint(this).build();
	}
	
	public Request<TransactionContext, Boolean> toUpdateRequest() {
		return SnomedRequests.prepareUpdateConstraint().setConstraint(this).build();
	}
	
}
