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
package com.b2international.snowowl.dsl.validation;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.dsl.escg.Concept;
import com.b2international.snowowl.dsl.escg.EscgPackage;
import com.b2international.snowowl.dsl.escg.NumericalAssignment;
import com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup;
import com.b2international.snowowl.dsl.escg.RefSet;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Java validator to register custom validation rules to be checked.
 * 
 * 
 */
public class ESCGJavaValidator extends AbstractESCGJavaValidator {

	public static final String INVALID_CONCEPT_ID_LENGTH = "invalidConceptId";
	public static final String NON_EXISTING_CONCEPT_ID = "nonexistentConcept";
	public static final String NON_MATCHING_TERM = "nomMatchingTerm";
	public static final String INACTIVE_CONCEPT = "inactiveConcept";
	public static final String INVALID_NUMERICAL_CONCEPT = "invalidNumericalConcept";
	public static final String INVALID_INGREDIENT_CONCEPT = "invalidIngredientConcept";
	
	private final Provider<IEventBus> bus;

	@Inject
	public ESCGJavaValidator(Provider<IEventBus> bus) {
		this.bus = bus;
	}
	
	@Override
	protected boolean isResponsible(Map<Object, Object> context, EObject eObject) {
		// context must have the associated activeBranch key, otherwise we cannot validate the target
		return super.isResponsible(context, eObject) && context.containsKey("activeBranch");
	}
	
	/**
	 * Check if the concept id length is between 6 and 18.
	 * 
	 * @param concept
	 */
	@Check(CheckType.FAST)
	public void checkSnomedConceptId(Concept concept) {
		
		if (null == concept || concept.getId() == null) {
			return;
		}

		checkSnomedConceptId(concept.getId(), EscgPackage.Literals.CONCEPT__ID);
	}

	/**
	 * Check if the reference set id length is between 6 and 18.
	 * 
	 * @param refSet
	 */
	@Check(CheckType.FAST)
	public void checkSnomedConceptId(RefSet refSet) {
		
		if (null == refSet || refSet.getId() == null) {
			return;
		}

		checkSnomedConceptId(refSet.getId(), EscgPackage.Literals.REF_SET__ID);
	}
	
	private void checkSnomedConceptId(String id, EAttribute attribute) {
		
		if (id.length() < 6 || id.length() > 18) {
			error("ID length should be between 6 and 18 characters", attribute, INVALID_CONCEPT_ID_LENGTH);
		}
	}

	/**
	 * Check if the concept is in the database based on the given concept id
	 * 
	 * @param concept
	 */
	@Check(CheckType.FAST)
	public void checkSnomedConceptValidity(Concept concept) {
		
		if (null == concept || concept.getId() == null) {
			return;
		}
		
		try {
			
			boolean conceptIdExists = SnomedRequests.prepareSearchConcept()
					.setLimit(0)
					.filterById(concept.getId())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
					.execute(bus.get())
					.getSync().getTotal() > 0;

			// Concept id is not valid if the concept id length is less then 6 or longer then 18 -> should't be existed at all -> don't show 2 error messages
			if (concept.getId().length() < 6 || concept.getId().length() > 18) {
				return;
			}

			if (conceptIdExists == false) {
				error("Concept ID does not exist in the database", EscgPackage.Literals.CONCEPT__ID, NON_EXISTING_CONCEPT_ID);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check if the refset is in the database based on the given concept id
	 * 
	 * @param concept
	 */
	@Check(CheckType.FAST)
	public void checkSnomedRefSetValidity(RefSet refSet) {
		
		if (null == refSet || refSet.getId() == null) {
			return;
		}
		
		try {
			
			boolean exists = SnomedRequests.prepareSearchConcept()
					.setLimit(0)
					.filterById(refSet.getId())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
					.execute(bus.get())
					.getSync().getTotal() > 0;
			
			// Concept id is not valid if the concept id length is less then 6 or longer then 18 -> should't be existed at all -> don't show 2 error messages
			if (refSet.getId().length() < 6 || refSet.getId().length() > 18) {
				return;
			}

			if (!exists) {
				error("Regular reference set with this ID does not exist in the database", EscgPackage.Literals.REF_SET__ID, NON_EXISTING_CONCEPT_ID);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Check if the concept id matches the subsequent term declaration. 
	 * Shows no error if it matches the preferred term, a warning if the term is a synonym, error otherwise.
	 * 
	 * @param concept
	 */
	@Check(CheckType.FAST)
	public void checkNonMatchingTermForConcept(Concept concept) {
		
		if (null == concept || null == concept.getId() || null == concept.getTerm()) {
			return;
		}
		
		checkNonMatchingTerm(concept.getId(), concept.getTerm(), EscgPackage.Literals.CONCEPT__TERM);
	}

	/**
	 * Check if the identifier concept of the reference set matches the subsequent term declaration. 
	 * Shows no error if it matches the preferred term, a warning if the term is a synonym, error otherwise.
	 * 
	 * @param refSet
	 */
	@Check(CheckType.FAST)
	public void checkNonMatchingTermForRefSet(RefSet refSet) {
		
		if (null == refSet || null == refSet.getId() || null == refSet.getTerm()) {
			return;
		}
		
		checkNonMatchingTerm(refSet.getId(), refSet.getTerm(), EscgPackage.Literals.REF_SET__TERM);
	}

	private void checkNonMatchingTerm(String id, String term, EAttribute termAttribute) {
		
		final SnomedConcept concept = Iterables.getOnlyElement(SnomedRequests.prepareSearchConcept()
				.setLimit(1)
				.filterById(id)
				.setExpand("descriptions(),pt()")
				.setLocales(ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(bus.get())
				.getSync(), null);
		
		if (concept == null || (concept.getPt() != null && concept.getPt().getTerm().equals(term))) {
			return;
		}
		
		for (SnomedDescription description : concept.getDescriptions()) {
			if (description.getTerm().equals(term)) {
				if (Concepts.FULLY_SPECIFIED_NAME.equals(description.getTypeId())) {
					warning("This is the fully specified name, not the preferred term.", termAttribute, NON_MATCHING_TERM);
					return;
				} else if (Concepts.SYNONYM.equals(description.getTypeId())) {
					warning("This is a synonym, not the preferred term.", termAttribute, NON_MATCHING_TERM);
					return;
				}
			}
		}
		
		error("This term is not a description for the specified component ID.", termAttribute, NON_MATCHING_TERM);
	}

	@Check(CheckType.FAST) 
	public void checkNonActiveConcepts(Concept concept) {
		if (null == concept || concept.getId() == null) {
			return;
		}
		
		final SnomedConcept entry = Iterables.getOnlyElement(SnomedRequests.prepareSearchConcept()
				.setLimit(1)
				.filterById(concept.getId())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getBranch())
				.execute(bus.get())
				.getSync(), null);
		if (entry != null && !entry.isActive()) {
			warning("Concept is inactive", EscgPackage.eINSTANCE.getConcept_Id(), INACTIVE_CONCEPT);
		}
	}
	
	@Check(CheckType.FAST)
	public void checkNumericalAssignment(NumericalAssignment numericalAssignment){
		if (null == numericalAssignment || null == numericalAssignment.getName() || null == numericalAssignment.getName().getId()) {
			return;
		}
		
		if(!Concepts.HAS_STRENGTH.equals(numericalAssignment.getName().getId())){
			error("The left side of a numerical attribute must be the concept: |" + Concepts.HAS_STRENGTH + "|Has strength|.", EscgPackage.eINSTANCE.getConceptAssignment_Value(),
					INVALID_NUMERICAL_CONCEPT);
		}
	}
	
	@Check(CheckType.FAST)
	public void checkNumericalAssignmentGroup(NumericalAssignmentGroup numericalAssignmentGroup){
		if (null == numericalAssignmentGroup || null == numericalAssignmentGroup.getIngredientConcept()) {
			return;
		}
		
		Concept ingredientConcept = numericalAssignmentGroup.getIngredientConcept();
		
		if (null == ingredientConcept || null == ingredientConcept.getId()) {
			return;
		}
		
		if(!Concepts.HAS_ACTIVE_INGREDIENT.equals(ingredientConcept.getId())){
			error("The first member of a numerical attribute group must be the concept: |" + Concepts.HAS_ACTIVE_INGREDIENT + "|Has active ingredient|.",
					EscgPackage.eINSTANCE.getNumericalAssignmentGroup_IngredientConcept(), INVALID_INGREDIENT_CONCEPT);
		}
	}
	
	private String getBranch() {
		checkArgument(getContext().containsKey("activeBranch"), "Active branch scope is required to execute this validator");
		return (String) getContext().get("activeBranch");
	}
	
}