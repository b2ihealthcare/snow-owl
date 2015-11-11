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
package com.b2international.snowowl.dsl.validation;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.dsl.escg.Concept;
import com.b2international.snowowl.dsl.escg.EscgPackage;
import com.b2international.snowowl.dsl.escg.NumericalAssignment;
import com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup;
import com.b2international.snowowl.dsl.escg.RefSet;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.SnomedClientRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptFullQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;

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
		
		final SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		
		if (null == terminologyBrowser) {
			return;
		}
		
		try {
			
			boolean conceptIdExists = terminologyBrowser.getConcept(concept.getId()) != null;

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
		
		final SnomedClientRefSetBrowser browser  = ApplicationContext.getInstance().getService(SnomedClientRefSetBrowser.class);
		
		if (null == browser) {
			return;
		}
		
		try {
			
			boolean refSetExists = browser.getRefSet(refSet.getId()) != null;

			// Concept id is not valid if the concept id length is less then 6 or longer then 18 -> should't be existed at all -> don't show 2 error messages
			if (refSet.getId().length() < 6 || refSet.getId().length() > 18) {
				return;
			}

			if (refSetExists == false) {
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
		
		SnomedClientIndexService indexSearcher = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
		
		if (null == indexSearcher) {
			return;
		}

		String conceptPreferredTerm = ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), id);
		
		if (term.equals(conceptPreferredTerm)) {
			return;
		}
		
		SnomedDescriptionIndexQueryAdapter queryAdapter = SnomedDescriptionIndexQueryAdapter.createFindByConceptIds(id);
		List<SnomedDescriptionIndexEntry> result = indexSearcher.search(queryAdapter);
		
		for (SnomedDescriptionIndexEntry snomedDescriptionIndexEntry : result) {
			if (snomedDescriptionIndexEntry.getLabel().equals(term) && Concepts.FULLY_SPECIFIED_NAME.equals(snomedDescriptionIndexEntry.getTypeId())) {
				warning("This is the fully specified name, not the preferred term.", termAttribute, NON_MATCHING_TERM);
				return;
			} else if (snomedDescriptionIndexEntry.getLabel().equals(term) && Concepts.SYNONYM.equals(snomedDescriptionIndexEntry.getTypeId())) {
				warning("This is a synonym, not the preferred term.", termAttribute, NON_MATCHING_TERM);
				return;
			}
		}
		
		error("This term is not a description for the specified component ID.", termAttribute, NON_MATCHING_TERM);
	}

	@Check(CheckType.FAST) 
	public void checkNonActiveConcepts(Concept concept) {
		if (null == concept || concept.getId() == null) {
			return;
		}
		
		SnomedClientIndexService indexSearcher = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
		SnomedConceptFullQueryAdapter queryBuilder = new SnomedConceptFullQueryAdapter(concept.getId(), SnomedConceptIndexQueryAdapter.SEARCH_BY_CONCEPT_ID);
		
		if (null == indexSearcher) {
			return;
		}
		
		List<SnomedConceptIndexEntry> result = indexSearcher.search(queryBuilder, 1);
		if (result != null && result.size() > 0) {
			if (!result.get(0).isActive()) {	// there shouldn't be more than 1 concept id in the search result
				warning("Concept is not active", EscgPackage.eINSTANCE.getConcept_Id(), INACTIVE_CONCEPT);
			}
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
}