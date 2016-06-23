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
package com.b2international.snowowl.snomed.importer.rf2.indexsynchronizer;

import static com.b2international.snowowl.snomed.SnomedPackage.eINSTANCE;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.CDOStore;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Builder;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * In-memory SNOMED CT ontology that combines components from changesets with
 * the already persisted store.
 */
public class SnomedOntology {

	private CDOCommitInfo commitInfo;
	private List<InternalCDORevision> newDescriptionRevisions = Lists.newArrayList();
	private List<InternalCDORevision> newConceptRevisions = Lists.newArrayList();
	private List<InternalCDORevision> newRelationshipRevisions = Lists.newArrayList();

	// maps to hold the processed eObjects
	private Map<InternalCDORevision, Concept> newConceptsMap = Maps.newHashMap();
	private Map<InternalCDORevision, Description> newDescriptionsMap = Maps.newHashMap();
	private Map<InternalCDORevision, Relationship> newRelationshipsMap = Maps.newHashMap();
	
	private Taxonomy statedTaxonomy;
	private Taxonomy inferredTaxonomy;

	/**
	 * @param commitInfo
	 */
	public SnomedOntology(CDOCommitInfo commitInfo) {
		this.commitInfo = commitInfo;
	}

	/**
	 * 
	 */
	public void preProcessCommitInfo() {
		List<CDOIDAndVersion> newObjects = commitInfo.getNewObjects();
		System.out.println("New objects size: " + newObjects.size());

		for (CDOIDAndVersion cdoidAndVersion : newObjects) {
			InternalCDORevision cdoRevision = (InternalCDORevision) cdoidAndVersion;
			EClass eClass = cdoRevision.getEClass();

			if (eClass == SnomedPackage.Literals.DESCRIPTION) {
				newDescriptionRevisions.add(cdoRevision);
			} else if (eClass == SnomedPackage.Literals.CONCEPT) {
				newConceptRevisions.add(cdoRevision);
			} else if (eClass == SnomedPackage.Literals.RELATIONSHIP) {
				newRelationshipRevisions.add(cdoRevision);
			}
		}
	}

	public void processRevisions() {
		processNewConcepts();
		processNewDescriptions();
		processNewRelationships();
		processNewConceptDescriptions();
		processNewConceptOutgoingRelationships();

		// processRefsetMembersForDescriptions();
	}

	public void createDocuments() {
		
		
		
		// find the references
		/*
		Set<InternalCDORevision> keySet = newConceptsMap.keySet();
		for (InternalCDORevision internalCDORevision : keySet) {
			Concept concept = newConceptsMap.get(internalCDORevision);
			final Builder doc = SnomedConceptDocument.builder().id(concept.getId());
			
			doc.active(concept.isActive())
			.released(concept.isReleased())
			.effectiveTime(concept.isSetEffectiveTime() ? concept.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME)
			.moduleId(concept.getModule().getId())
			.exhaustive(concept.isExhaustive())
			.primitive(concept.isPrimitive());
			// .referringPredicates(referringPredicates.removeAll(id))
			// .relevant() // TODO register change type
			
			doc.build();
			*/
	}

	/**
	 * 
	 */
	private void processNewConcepts() {

		// create the skeleton concepts first without references
		for (InternalCDORevision cdoRevision : newConceptRevisions) {
			Concept newConcept = SnomedFactory.eINSTANCE.createConcept();
			newConcept.setId((String) cdoRevision.get(eINSTANCE.getComponent_Id(), CDOStore.NO_INDEX));
			newConcept.setActive((boolean) cdoRevision.get(eINSTANCE.getComponent_Active(), CDOStore.NO_INDEX));
			newConcept.setEffectiveTime((Date) cdoRevision.get(eINSTANCE.getComponent_EffectiveTime(), CDOStore.NO_INDEX));
			newConcept.setExhaustive((boolean) cdoRevision.get(eINSTANCE.getConcept_Exhaustive(), CDOStore.NO_INDEX));
			newConcept.setReleased((boolean) cdoRevision.get(eINSTANCE.getComponent_Released(), CDOStore.NO_INDEX));
			newConceptsMap.put(cdoRevision, newConcept);
		}

		// find the references
		Set<InternalCDORevision> keySet = newConceptsMap.keySet();

		for (InternalCDORevision cdoRevision : keySet) {
			Concept concept = newConceptsMap.get(cdoRevision);

			CDOID definitionStatusConceptCDOId = (CDOID) cdoRevision.get(eINSTANCE.getConcept_DefinitionStatus(), CDOStore.NO_INDEX);
			concept.setDefinitionStatus(findConceptByCDOId(definitionStatusConceptCDOId));

			CDOID moduleConceptCDOId = (CDOID) cdoRevision.get(eINSTANCE.getComponent_Module(), CDOStore.NO_INDEX);
			concept.setModule(findConceptByCDOId(moduleConceptCDOId));
		}
		System.out.println("Created " + newConceptsMap.size() + " concepts.");
	}

	/**
	 * 
	 */
	private void processNewDescriptions() {

		// create the skeleton descriptions first without references
		for (InternalCDORevision cdoRevision : newDescriptionRevisions) {
			Description newDescription = SnomedFactory.eINSTANCE.createDescription();
			newDescription.setId((String) cdoRevision.get(eINSTANCE.getComponent_Id(), CDOStore.NO_INDEX));
			newDescription.setActive((boolean) cdoRevision.get(eINSTANCE.getComponent_Active(), CDOStore.NO_INDEX));
			newDescription.setEffectiveTime((Date) cdoRevision.get(eINSTANCE.getComponent_EffectiveTime(), CDOStore.NO_INDEX));
			newDescription.setLanguageCode((String) cdoRevision.get(eINSTANCE.getDescription_LanguageCode(), CDOStore.NO_INDEX));
			newDescription.setReleased((boolean) cdoRevision.get(eINSTANCE.getComponent_Released(), CDOStore.NO_INDEX));
			newDescription.setTerm((String) cdoRevision.get(eINSTANCE.getDescription_Term(), CDOStore.NO_INDEX));
			newDescriptionsMap.put(cdoRevision, newDescription);
		}

		// attach the references
		Set<InternalCDORevision> keySet = newDescriptionsMap.keySet();

		for (InternalCDORevision cdoRevision : keySet) {
			Description description = newDescriptionsMap.get(cdoRevision);

			CDOID caseSignificanceConceptCDOId = (CDOID) cdoRevision.get(eINSTANCE.getDescription_CaseSignificance(), CDOStore.NO_INDEX);
			description.setCaseSignificance(findConceptByCDOId(caseSignificanceConceptCDOId));

			CDOID conceptCDOId = (CDOID) cdoRevision.get(eINSTANCE.getDescription_Concept(), CDOStore.NO_INDEX);
			description.setConcept(findConceptByCDOId(conceptCDOId));

			CDOID moduleConceptCDOId = (CDOID) cdoRevision.get(eINSTANCE.getComponent_Module(), CDOStore.NO_INDEX);
			description.setModule(findConceptByCDOId(moduleConceptCDOId));

			CDOID typeCDOId = (CDOID) cdoRevision.get(eINSTANCE.getDescription_Type(), CDOStore.NO_INDEX);
			description.setType(findConceptByCDOId(typeCDOId));
		}
		System.out.println("Created " + newDescriptionsMap.size() + " descriptions.");

	}

	/**
	 * 
	 */
	private void processNewRelationships() {

		// create the skeleton relationship first without references
		for (InternalCDORevision cdoRevision : newRelationshipRevisions) {
			Relationship newRelationship = SnomedFactory.eINSTANCE.createRelationship();
			newRelationship.setId((String) cdoRevision.get(eINSTANCE.getComponent_Id(), CDOStore.NO_INDEX));
			newRelationship.setActive((boolean) cdoRevision.get(eINSTANCE.getComponent_Active(), CDOStore.NO_INDEX));
			newRelationship.setEffectiveTime((Date) cdoRevision.get(eINSTANCE.getComponent_EffectiveTime(), CDOStore.NO_INDEX));
			newRelationship.setReleased((boolean) cdoRevision.get(eINSTANCE.getComponent_Released(), CDOStore.NO_INDEX));
			newRelationship.setDestinationNegated((boolean) cdoRevision.get(eINSTANCE.getRelationship_DestinationNegated(), CDOStore.NO_INDEX));
			newRelationship.setGroup((int) cdoRevision.get(eINSTANCE.getRelationship_Group(), CDOStore.NO_INDEX));
			newRelationship.setUnionGroup((int) cdoRevision.get(eINSTANCE.getRelationship_UnionGroup(), CDOStore.NO_INDEX));

			newRelationshipsMap.put(cdoRevision, newRelationship);
		}

		// attach the references
		Set<InternalCDORevision> keySet = newRelationshipsMap.keySet();

		for (InternalCDORevision cdoRevision : keySet) {
			Relationship relationship = newRelationshipsMap.get(cdoRevision);

			CDOID moduleConceptCDOId = (CDOID) cdoRevision.get(eINSTANCE.getComponent_Module(), CDOStore.NO_INDEX);
			relationship.setModule(findConceptByCDOId(moduleConceptCDOId));

			CDOID characteristicTypeCDOId = (CDOID) cdoRevision.get(eINSTANCE.getRelationship_CharacteristicType(), CDOStore.NO_INDEX);
			relationship.setCharacteristicType((findConceptByCDOId(characteristicTypeCDOId)));

			CDOID destinationCDOId = (CDOID) cdoRevision.get(eINSTANCE.getRelationship_Destination(), CDOStore.NO_INDEX);
			relationship.setDestination(findConceptByCDOId(destinationCDOId));

			CDOID modifierCDOId = (CDOID) cdoRevision.get(eINSTANCE.getRelationship_Modifier(), CDOStore.NO_INDEX);
			relationship.setModifier(findConceptByCDOId(modifierCDOId));

			CDOID sourceCDOId = (CDOID) cdoRevision.get(eINSTANCE.getRelationship_Source(), CDOStore.NO_INDEX);
			relationship.setSource(findConceptByCDOId(sourceCDOId));

			CDOID typeCDOId = (CDOID) cdoRevision.get(eINSTANCE.getRelationship_Type(), CDOStore.NO_INDEX);
			relationship.setType(findConceptByCDOId(typeCDOId));

		}
		System.out.println("Created " + newRelationshipsMap.size() + " relationships.");

	}

	/**
	 * 
	 */
	private void processNewConceptDescriptions() {
		// find the references
		Set<InternalCDORevision> keySet = newConceptsMap.keySet();

		for (InternalCDORevision cdoRevision : keySet) {
			Concept concept = newConceptsMap.get(cdoRevision);

			@SuppressWarnings("unchecked")
			List<CDOID> descriptionCDOIds = (List<CDOID>) cdoRevision.get(eINSTANCE.getConcept_Descriptions(), CDOStore.NO_INDEX);
			for (CDOID cdoid : descriptionCDOIds) {
				Description description = findDescriptionByCDOId(cdoid);
				System.err.println("looking for descriptions: " + description.getTerm());
				concept.getDescriptions().add(description);
			}
		}
	}

	private void processNewConceptOutgoingRelationships() {
		// find the references
		Set<InternalCDORevision> keySet = newConceptsMap.keySet();

		for (InternalCDORevision cdoRevision : keySet) {
			Concept concept = newConceptsMap.get(cdoRevision);

			@SuppressWarnings("unchecked")
			List<CDOID> outboundRelationshipCDOIds = (List<CDOID>) cdoRevision.get(eINSTANCE.getConcept_OutboundRelationships(), CDOStore.NO_INDEX);
			for (CDOID cdoid : outboundRelationshipCDOIds) {
				Relationship relationship = findRelationshipByCDOId(cdoid);
				System.err.println("looking for relationship: " + relationship.getId());
				concept.getOutboundRelationships().add(relationship);
			}
		}
	}

	/**
	 * @param cdoId
	 * @return
	 */
	private Concept findConceptByCDOId(CDOID cdoId) {

		// 1. look within the already processed new concepts
		Set<InternalCDORevision> keySet = newConceptsMap.keySet();
		for (InternalCDORevision internalCDORevision : keySet) {
			if (CDOIDUtil.equals(internalCDORevision.getID(), cdoId)) {
				// System.out.println("ConceptProcessor.findConceptByCDOId()");
				return newConceptsMap.get(internalCDORevision);
			}
		}

		// look within the changed concepts

		// log something
		System.err.println("Did not find the referenced concept.");
		return null;
	}

	/**
	 * @param cdoId
	 * @return
	 */
	private Description findDescriptionByCDOId(CDOID cdoId) {

		// 1. look within the already processed new concepts
		Set<InternalCDORevision> keySet = newDescriptionsMap.keySet();
		for (InternalCDORevision internalCDORevision : keySet) {
			if (CDOIDUtil.equals(internalCDORevision.getID(), cdoId)) {
				return newDescriptionsMap.get(internalCDORevision);
			}
		}

		// look within the changed concepts

		// log something
		System.err.println("Did not find the referenced description.");
		return null;
	}

	/**
	 * @param cdoId
	 * @return
	 */
	private Relationship findRelationshipByCDOId(CDOID cdoId) {

		// 1. look within the already processed new concepts
		Set<InternalCDORevision> keySet = newRelationshipsMap.keySet();
		for (InternalCDORevision internalCDORevision : keySet) {
			if (CDOIDUtil.equals(internalCDORevision.getID(), cdoId)) {
				return newRelationshipsMap.get(internalCDORevision);
			}
		}

		// look within the changed concepts

		// log something
		System.err.println("Did not find the referenced relationship.");
		return null;
	}

}
