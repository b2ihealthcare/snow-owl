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
package com.b2international.snowowl.snomed.datastore;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * This class is for building the <b>SNOMED CT source code to target map code correlation value</b> and its all subtypes 
 * manually since the 0531 NEHTA (AU) RF2 SNOMED CT release does not contain is and it is required for the SNOMED CT complex 
 * map type reference sets. 
 *
 */
public class SnomedCorrelationConceptBuilder {

	private static final String EN = "en";

	private final SnomedEditingContext context;
	
	/**
	 * Creates a builder instance.
	 * @param context the underlying editing context for the SNOMED CT terminology.
	 */
	public SnomedCorrelationConceptBuilder(final SnomedEditingContext context) {
		this.context = context;
	}
	
	/**
	 * Check the root correlation concept if it does not exist then creates it and returns with its SNOMED CT concept identifier,
	 * if the concept already exists in the store it returns with {@code null}. 
	 * @return the concept identifier or {@code null}.
	 */
	public String checkCorrelationRoot() {
		final Concept concept = new SnomedConceptLookupService().getComponent(CorrelationConcept.CORRELATION_ROOT.getConceptId(), context.getTransaction());
		if (null == concept) {
			return create(CorrelationConcept.CORRELATION_ROOT).getId();
		} 
		return null;
	}
	
	/**
	 * Builds all the required correlation subtypes.
	 * @return returns with a list of the SNOMED CT concept identifiers.
	 */
	public Iterable<String> buildCorrentationTypes() {
		final List<String> ids = Lists.newArrayList();
		for (final CorrelationConcept correlationConcept : CorrelationConcept.getSubTypes()) {
			ids.add(create(correlationConcept).getId());
		}
		return ids;
	}
	
	private void addToResource(final EObject object) {
		context.add(object);
	}
	
	private Concept create(final CorrelationConcept correlationConcept) {
		Concept parentConcept = new SnomedConceptLookupService().getComponent(correlationConcept.getParentId(), context.getTransaction());
		
		if (null == parentConcept) { //if concept does not exist lookup in the transaction 
			parentConcept = getConceptFromTransaction(correlationConcept.getParentId());
		}
		
		Preconditions.checkNotNull(parentConcept, "Parent concept argument cannot be null.");
		final Concept concept = SnomedFactory.eINSTANCE.createConcept();
		addToResource(concept);
		
		final ComponentIdentifierPair<String> acceptabilityPair = 
				SnomedRefSetEditingContext.createConceptTypePair(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
		
		final SnomedStructuralRefSet languageRefSet = getLanguageRefSet();
		
		// set concept properties
		concept.setId(correlationConcept.getConceptId());
		concept.setActive(true);
		concept.setDefinitionStatus(new SnomedConceptLookupService().getComponent(Concepts.PRIMITIVE, context.getTransaction()));
		concept.setModule(context.getDefaultModuleConcept());
		
		// add FSN
		final Description fsn = SnomedFactory.eINSTANCE.createDescription();
		fsn.setId(correlationConcept.getFsnId());
		fsn.setActive(true);
		fsn.setCaseSignificance(new SnomedConceptLookupService().getComponent(Concepts.ENTIRE_TERM_CASE_SENSITIVE, context.getTransaction()));
		fsn.setType(new SnomedConceptLookupService().getComponent(Concepts.FULLY_SPECIFIED_NAME, context.getTransaction()));
		fsn.setTerm(correlationConcept.getLabel());
		fsn.setLanguageCode(EN);
		fsn.setModule(context.getDefaultModuleConcept());
		fsn.setConcept(concept);
		
		//create language reference set membership
		final ComponentIdentifierPair<String> fsnReferencedComponentPair = SnomedRefSetEditingContext.createDescriptionTypePair(fsn.getId());
		
		//create language reference set member for FSN
		final SnomedLanguageRefSetMember fsnMember = context.getRefSetEditingContext().createLanguageRefSetMember(
				fsnReferencedComponentPair, 
				acceptabilityPair, 
				context.getDefaultModuleConcept().getId(), 
				languageRefSet);
		
		fsn.getLanguageRefSetMembers().add(fsnMember);
		
		//add synonym
		final Description synonym = SnomedFactory.eINSTANCE.createDescription();
		synonym.setId(correlationConcept.getSynonymId());
		synonym.setActive(true);
		synonym.setCaseSignificance(new SnomedConceptLookupService().getComponent(Concepts.ENTIRE_TERM_CASE_SENSITIVE, context.getTransaction()));
		synonym.setType(new SnomedConceptLookupService().getComponent(Concepts.SYNONYM, context.getTransaction()));
		synonym.setTerm(correlationConcept.getLabel());
		synonym.setLanguageCode(EN);
		synonym.setModule(context.getDefaultModuleConcept());
		synonym.setConcept(concept);
		
		//create language reference set membership
		final ComponentIdentifierPair<String> synonymReferencedComponentPair = SnomedRefSetEditingContext.createDescriptionTypePair(fsn.getId());
		
		//create language reference set member for PT
		final SnomedLanguageRefSetMember ptMember = context.getRefSetEditingContext().createLanguageRefSetMember(
				synonymReferencedComponentPair, 
				acceptabilityPair, 
				context.getDefaultModuleConcept().getId(), 
				languageRefSet);
		
		synonym.getLanguageRefSetMembers().add(ptMember);
		
		final Relationship relationship = context.buildDefaultRelationship(concept,new SnomedConceptLookupService().getComponent(SnomedConstants.Concepts.IS_A, context.getTransaction()), 
				parentConcept, new SnomedConceptLookupService().getComponent(SnomedConstants.Concepts.STATED_RELATIONSHIP, context.getTransaction()));
		relationship.setModule(concept.getModule());
		
		return concept;
	}
	
	private Concept getConceptFromTransaction(final String parentId) {
		final Collection<CDOObject> newobjects = context.getTransaction().getNewObjects().values();
		final Iterable<Concept> newConcepts = Iterables.filter(newobjects, Concept.class);
		final List<Concept> searchedConcept = Lists.newArrayList(Iterables.filter(newConcepts, new Predicate<Concept>() {
			@Override public boolean apply(final Concept newConcept) {
				return parentId.equals(newConcept.getId());
			}
		}));
		return searchedConcept.isEmpty() ? null : searchedConcept.get(0);
	}

	/*returns with the currently used language type reference set*/
	private SnomedStructuralRefSet getLanguageRefSet() {
		final String languageRefSetId = getLanguageRefSetId();
		return (SnomedStructuralRefSet) new SnomedRefSetLookupService().getComponent(languageRefSetId, context.getTransaction());
	}

	private String getLanguageRefSetId() {
		return ApplicationContext.getInstance().getServiceChecked(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId(BranchPathUtils.createPath(context.getTransaction()));
	}
	
	private static enum CorrelationConcept {
		
		CORRELATION_ROOT(
			"SNOMED CT source code to target map code correlation value",
			"447247004",
			"2879493018",
			"2882284011",
			"900000000000457003"),
		CORRELATION_TYPE_1(
			"Broad to narrow map from SNOMED CT source code to target code",
			"447559001",
			"2879497017",
			"2882287016",
			"447247004"),
		CORRELATION_TYPE_2(
			"Exact match map from SNOMED CT source code to target code",
			"447557004",
			"2879495013",
			"2884215017",
			"447247004"),
		CORRELATION_TYPE_3(
			"Narrow to broad map from SNOMED CT source code to target code",
			"447558009",
			"2879496014",
			"2882286013",
			"447247004"),
		CORRELATION_TYPE_4(
			"Partial overlap between SNOMED CT source code and target code",
			"447560006",
			"2879498010",
			"2882970014",
			"447247004"),
		CORRELATION_TYPE_5(
			"SNOMED CT source code not mappable to target coding scheme",
			"447556008",
			"2879494012",
			"2882285012",
			"447247004"),
		CORRELATION_TYPE_6(
			"SNOMED CT source code to target map code correlation not specified",
			"447561005",
			"2879499019",
			"2882968017",
			"447247004");
				
				
		private static Iterable<CorrelationConcept> getSubTypes() {
			return Iterables.filter(Lists.newArrayList(CorrelationConcept.values()), new Predicate<CorrelationConcept>() {
				@Override public boolean apply(final CorrelationConcept input) {
					return !CORRELATION_ROOT.equals(input);
				}
			});
		}
		
		private String label;
		private String conceptId;
		private String fsnId;
		private String synonymId;
		private String parentId;
		
		private CorrelationConcept(final String label, final String conceptId, final String fsnId, final String synonymId, final String parentId) {
			this.label = label;
			this.conceptId = conceptId;
			this.fsnId = fsnId;
			this.synonymId = synonymId;
			this.parentId = parentId;
		}

		public String getLabel() {
			return label;
		}

		public String getConceptId() {
			return conceptId;
		}

		public String getFsnId() {
			return fsnId;
		}

		public String getSynonymId() {
			return synonymId;
		}

		public String getParentId() {
			return parentId;
		}
		
	}
	
}