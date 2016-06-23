/*******************************************************************************
 * Copyright (c) 2016 B2i Healthcare. All rights reserved.
 *******************************************************************************/
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
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.spi.cdo.CDOStore;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Builder;
import com.google.common.collect.Maps;

/**
 *
 */
public class ConceptProcessor implements CDOComponentProcessor {

	private CDOCommitInfo commitInfo;
	private Map<InternalCDORevision, Concept> newSkeletonConceptsMap = Maps.newHashMap();

	/**
	 * @param commitInfo
	 */
	public ConceptProcessor(final CDOCommitInfo commitInfo) {
		this.commitInfo = commitInfo;
	}
	
	@Override
	public void createConcepts(List<InternalCDORevision> conceptRevisions) {
		
		//create the skeleton concepts first without references
		for (InternalCDORevision cdoRevision : conceptRevisions) {
			Concept skeletonConcept = createSkeletonConcept(cdoRevision);
			newSkeletonConceptsMap.put(cdoRevision, skeletonConcept);
		}
		
		//find the references
		Set<InternalCDORevision> keySet = newSkeletonConceptsMap.keySet();
		
		for (InternalCDORevision cdoRevision : keySet) {
			Concept concept = newSkeletonConceptsMap.get(cdoRevision);
			
			CDOID definitionStatusConceptCDOId = (CDOID) cdoRevision.get(eINSTANCE.getConcept_DefinitionStatus(), CDOStore.NO_INDEX);
			concept.setDefinitionStatus(findConceptByCDOId(definitionStatusConceptCDOId));
			
			CDOID moduleConceptCDOId = (CDOID) cdoRevision.get(eINSTANCE.getComponent_Module(), CDOStore.NO_INDEX);
			concept.setModule(findConceptByCDOId(moduleConceptCDOId));
			
			//concept.getDescriptions().add(d);
			
		}
		System.out.println("Created " + newSkeletonConceptsMap.size() + " concepts.");
	}

	/**
	 * @param cdoId
	 * @return
	 */
	private Concept findConceptByCDOId(CDOID cdoId) {
		
		//1. look within the already processed new concepts
		Set<InternalCDORevision> keySet = newSkeletonConceptsMap.keySet();
		for (InternalCDORevision internalCDORevision : keySet) {
			if (CDOIDUtil.equals(internalCDORevision.getID(), cdoId)) {
				//System.out.println("ConceptProcessor.findConceptByCDOId()");
				return newSkeletonConceptsMap.get(internalCDORevision);
			}
		}
		
		//look within the changed concepts
		
		//log something
		System.err.println("Did not find the referenced concept");
		return null;
	}

	@Override
	public SnomedConceptDocument createDocument(InternalCDORevision cdoRevision) {
		Concept newConcept = createSkeletonConcept(cdoRevision);
		//return createDocument(newConcept);
		return null;
	}
	
	/**
	 * @param cdoRevision
	 * @return 
	 */
	private Concept createSkeletonConcept(InternalCDORevision cdoRevision) {
		Concept newConcept = SnomedFactory.eINSTANCE.createConcept();
		
		//System.out.println("Creating a concept for the revision with id: " + cdoRevision.getID());
		newConcept.setId((String) cdoRevision.get(eINSTANCE.getComponent_Id(), CDOStore.NO_INDEX));
		//System.out.println("Concept id: " + newConcept.getId());
		newConcept.setActive((boolean) cdoRevision.get(eINSTANCE.getComponent_Active(), CDOStore.NO_INDEX));
		
//		CDOID definitionStatusConceptId = (CDOID) cdoRevision.get(eINSTANCE.getConcept_DefinitionStatus(), CDOStore.NO_INDEX);
//		
//		if (CDOIDUtil.equals(cdoRevision.getID(), definitionStatusConceptId)) {
//			newConcept.setDefinitionStatus(newConcept);
//		} else {
//			Concept definitionStatusConcept = findReferencedConcept(definitionStatusConceptId);
//			newConcept.setDefinitionStatus(definitionStatusConcept);
//		}
		newConcept.setEffectiveTime((Date) cdoRevision.get(eINSTANCE.getComponent_EffectiveTime(), CDOStore.NO_INDEX));
		newConcept.setExhaustive((boolean) cdoRevision.get(eINSTANCE.getConcept_Exhaustive(), CDOStore.NO_INDEX));
		
//		CDOID moduleId = (CDOID)cdoRevision.get(eINSTANCE.getComponent_Module(), CDOStore.NO_INDEX);
//		if (CDOIDUtil.equals(cdoRevision.getID(), moduleId)) {
//			newConcept.setModule(newConcept);
//		} else {
//			Concept moduleConcept = findReferencedConcept(moduleId);
//			newConcept.setModule(moduleConcept);
//		}
		newConcept.setReleased((boolean) cdoRevision.get(eINSTANCE.getComponent_Released(), CDOStore.NO_INDEX));
		
		//System.out.println("Concept: " + newConcept.getId());
		//SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_ReferencedComponentId(), CDOStore.NO_INDEX));
		return newConcept;
	}
	
	/**
	 * @param cdoRevision 
	 * @param cdoId
	 * @return
	 */
	private Concept findReferencedConcept(CDOID cdoId) {
		
		//is it new?
		List<CDOIDAndVersion> newObjects = commitInfo.getNewObjects();
		for (CDOIDAndVersion cdoidAndVersion : newObjects) {
			if (CDOIDUtil.equals(cdoidAndVersion.getID(), cdoId)) {
				System.out.println("   Found the definition status as new object: " + cdoId);
				return createSkeletonConcept((InternalCDORevision) cdoidAndVersion);
			}
		}
		
		//check the changed objects
		List<CDORevisionKey> changedObjects = commitInfo.getChangedObjects();
		for (CDOIDAndVersion cdoidAndVersion : changedObjects) {
			if (cdoidAndVersion.getID().equals(cdoId)) {
				System.out.println("   Found the definition status as changed object: " + cdoId);
				return createSkeletonConcept((InternalCDORevision) cdoidAndVersion);
			}
		}
		
		//check the deleted objects
		List<CDOIDAndVersion> detachedObjects = commitInfo.getDetachedObjects();
		for (CDOIDAndVersion cdoidAndVersion : detachedObjects) {
			if (cdoidAndVersion.getID().equals(cdoId)) {
				System.out.println("   Found the definition status as deleted object: " + cdoId);
				return createSkeletonConcept((InternalCDORevision) cdoidAndVersion);
			}
		}
		
		//check the index store or CDO store
		
		System.out.println("      NOT FOUND");
		return null;
	}

	private SnomedConceptDocument createDocument(Concept concept) {
		final Builder doc = SnomedConceptDocument.builder().id(concept.getId());
		
		doc.active(concept.isActive())
		.released(concept.isReleased())
		.effectiveTime(concept.isSetEffectiveTime() ? concept.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME)
		//.moduleId(concept.getModule().getId())
		.exhaustive(concept.isExhaustive())
		.primitive(concept.isPrimitive())
		//.referringPredicates(referringPredicates.removeAll(id))
//		.relevant() // TODO register change type
		;
		return doc.build();
	}

}
