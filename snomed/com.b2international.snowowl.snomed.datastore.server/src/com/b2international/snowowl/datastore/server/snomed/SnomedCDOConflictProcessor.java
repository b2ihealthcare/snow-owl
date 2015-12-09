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
package com.b2international.snowowl.datastore.server.snomed;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta.Type;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.cdo.AbstractCDOConflictProcessor;
import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndDetachedInTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.snomed.*;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * An {@link ICDOConflictProcessor} implementation handling conflicts specific to the SNOMED CT terminology model.
 */
public class SnomedCDOConflictProcessor extends AbstractCDOConflictProcessor implements ICDOConflictProcessor {

	private static final Set<EClass> COMPONENT_CLASSES = ImmutableSet.of(
			SnomedPackage.Literals.CONCEPT, 
			SnomedPackage.Literals.DESCRIPTION, 
			SnomedPackage.Literals.RELATIONSHIP);

	private static final Multimap<EClass, EStructuralFeature> DETACHED_FEATURE_MAP = ImmutableMultimap.<EClass, EStructuralFeature>builder()
			.put(SnomedPackage.Literals.RELATIONSHIP, SnomedPackage.Literals.RELATIONSHIP__SOURCE)
			.put(SnomedPackage.Literals.RELATIONSHIP, SnomedPackage.Literals.RELATIONSHIP__TYPE)
			.put(SnomedPackage.Literals.RELATIONSHIP, SnomedPackage.Literals.RELATIONSHIP__DESTINATION)
			.put(SnomedPackage.Literals.DESCRIPTION, SnomedPackage.Literals.DESCRIPTION__CONCEPT)
			.put(SnomedPackage.Literals.DESCRIPTION, SnomedPackage.Literals.DESCRIPTION__TYPE)
			.build();

	private static final Map<EClass, EAttribute> RELEASED_ATTRIBUTE_MAP = ImmutableMap.of(
			SnomedPackage.Literals.COMPONENT, SnomedPackage.Literals.COMPONENT__RELEASED,
			SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__RELEASED);

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedCDOConflictProcessor.class);

	public SnomedCDOConflictProcessor() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID, RELEASED_ATTRIBUTE_MAP);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * SNOMED CT-specific conflict processing will report a conflict if:
	 * <ul>
	 * <li>a new object on target has the same SNOMED CT component identifier as the new object on source 
	 * <li>a detached object on target is referenced by the new object on source
	 * </ul>
	 * The addition is allowed through in all other cases.
	 */
	@Override
	public Object addedInSource(final CDORevision sourceRevision, final Map<CDOID, Object> targetMap) {

		Conflict conflict = checkDuplicateComponentIds(sourceRevision, targetMap);
		if (conflict != null) {
			return conflict;
		}

		conflict = checkDetachedReferences(sourceRevision, targetMap);
		if (conflict != null) {
			return conflict;
		}
		
		return super.addedInSource(sourceRevision, targetMap);
	}
	
	@Override
	public CDOFeatureDelta changedInSourceAndTargetSingleValued(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta) {
		final EStructuralFeature feature = targetFeatureDelta.getFeature();
		
		if (SnomedPackage.Literals.COMPONENT__EFFECTIVE_TIME.equals(feature) 
				|| SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME.equals(feature)
				|| SnomedRefSetPackage.Literals.SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__SOURCE_EFFECTIVE_TIME.equals(feature)
				|| SnomedRefSetPackage.Literals.SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__TARGET_EFFECTIVE_TIME.equals(feature)) {
		
			if (Type.UNSET.equals(targetFeatureDelta.getType())) {
				return targetFeatureDelta;
			} else if (Type.UNSET.equals(sourceFeatureDelta.getType())) {
				return sourceFeatureDelta;
			}
			
			// Fall-through
		}
		
		return super.changedInSourceAndTargetSingleValued(targetFeatureDelta, sourceFeatureDelta);
	}

	private Conflict checkDuplicateComponentIds(final CDORevision sourceRevision, final Map<CDOID, Object> targetMap) {

		if (!isComponent(sourceRevision)) {
			return null;
		}

		final String newComponentIdInSource = getComponentId((InternalCDORevision) sourceRevision);
		final Map<String, CDOID> newComponentIdsInTarget = newHashMap();
		final Iterable<InternalCDORevision> newRevisionsInTarget = getNewRevisionsInTarget(targetMap);

		for (final InternalCDORevision targetRevision : newRevisionsInTarget) {
			if (isComponent(targetRevision)) {
				newComponentIdsInTarget.put(getComponentId(targetRevision), targetRevision.getID());
			}
		}

		final CDOID conflictingNewInTarget = newComponentIdsInTarget.get(newComponentIdInSource);
		if (null != conflictingNewInTarget) {
			return new AddedInSourceAndTargetConflict(sourceRevision.getID(), conflictingNewInTarget);
		} else {
			return null;
		}
	}

	private boolean isComponent(final CDORevision revision) {
		return isComponent(revision.getEClass());
	}

	private boolean isComponent(final EClass eClass) {
		return COMPONENT_CLASSES.contains(eClass);
	}

	private String getComponentId(final InternalCDORevision revision) {
		return (String) revision.getValue(SnomedPackage.Literals.COMPONENT__ID);
	}

	private Conflict checkDetachedReferences(final CDORevision sourceRevision, final Map<CDOID, Object> targetMap) {

		final InternalCDORevision internalSourceRevision = (InternalCDORevision) sourceRevision;
		final EClass eClass = internalSourceRevision.getEClass();
		final Set<CDOID> detachedTargetIds = getDetachedIdsInTarget(targetMap);

		final Conflict conflict;

		if (isComponent(eClass)) {
			conflict = checkDetachedComponentReferences(internalSourceRevision, detachedTargetIds, DETACHED_FEATURE_MAP.get(eClass));
		} else if (SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER.isSuperTypeOf(eClass)) {
			conflict = checkDetachedRefSetReferences(internalSourceRevision, eClass, detachedTargetIds);
		} else {
			conflict = null;
		}

		return conflict;
	}

	private Conflict checkDetachedComponentReferences(final InternalCDORevision internalSourceRevision, final Set<CDOID> detachedTargetIds, final Collection<EStructuralFeature> featuresToCheck) {

		for (final EStructuralFeature feature : featuresToCheck) {
			final CDOID targetId = (CDOID) internalSourceRevision.getValue(feature);
			if (detachedTargetIds.contains(targetId)) {
				return new AddedInSourceAndDetachedInTargetConflict(internalSourceRevision.getID(), targetId);
			}
		}

		return null;
	}

	private Conflict checkDetachedRefSetReferences(final InternalCDORevision internalSourceRevision, final EClass sourceEClass, final Set<CDOID> detachedTargetIds) {

		final String referencedComponentId = (String) internalSourceRevision.getValue(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID);
		final short referencedComponentType = getReferencedComponentType(sourceEClass, referencedComponentId);

		// Unspecified or non-SNOMED CT components can not be checked this way
		if (referencedComponentType == CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT) {
			return null;
		}

		final String terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(referencedComponentType);
		final long referencedComponentStorageKey = getReferencedComponentStorageKey(internalSourceRevision.getBranch(), referencedComponentId, terminologyComponentId); 

		// Not found components are OK as well
		if (referencedComponentStorageKey == -1L) {
			return null;
		}

		final CDOID targetId = CDOIDUtil.createLong(referencedComponentStorageKey);
		if (detachedTargetIds.contains(targetId)) {
			return new AddedInSourceAndDetachedInTargetConflict(internalSourceRevision.getID(), targetId);
		} else {
			return null;
		}
	}

	private short getReferencedComponentType(final EClass sourceEClass, final String referencedComponentId) {

		if (SnomedRefSetPackage.Literals.SNOMED_QUERY_REF_SET_MEMBER.equals(sourceEClass)) {
			// Query reference set members need to be special cases so that they don't return CONCEPT as the type
			return SnomedTerminologyComponentConstants.REFSET_NUMBER;
		} else {
			return SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(referencedComponentId);
		}
	}

	private long getReferencedComponentStorageKey(final CDOBranch branch, final String referencedComponentId, final String terminologyComponentId) {
		final IBranchPath branchPath = BranchPathUtils.createPath(branch);
		return CoreTerminologyBroker.getInstance().getLookupService(terminologyComponentId).getStorageKey(branchPath, referencedComponentId);
	}

	@Override
	public Conflict postProcess(CDOTransaction transaction) {
		super.postProcess(transaction);
		
		final IBranchPath branchPath = BranchPathUtils.createPath(transaction);
		final Set<String> synonymAndDescendantIds = ApplicationContext.getServiceForClass(ISnomedComponentService.class).getSynonymAndDescendantIds(branchPath);
		final Set<SnomedLanguageRefSetMember> membersToRemove = newHashSet();
		
		label:
		for (CDOObject newObject : transaction.getNewObjects().values()) {
			
			
			if (!(newObject instanceof SnomedLanguageRefSetMember)) {
				continue;
			}
			
			SnomedLanguageRefSetMember newLanguageRefSetMember = (SnomedLanguageRefSetMember) newObject;
			
			if (!newLanguageRefSetMember.isActive()) {
				continue;
			}
			
			Description description = (Description) newObject.eContainer();
			
			if (!description.isActive()) {
				continue;
			}
			
			String acceptabilityId = newLanguageRefSetMember.getAcceptabilityId();
			String typeId = description.getType().getId();
			String languageRefSetId = newLanguageRefSetMember.getRefSetIdentifierId(); 
			
			Concept concept = description.getConcept();
			
			for (Description conceptDescription : concept.getDescriptions()) {
				
				if (!conceptDescription.isActive()) {
					continue;
				}
				
				String conceptDescriptionTypeId = conceptDescription.getType().getId();
				
				if (!typeId.equals(conceptDescriptionTypeId) && !(synonymAndDescendantIds.contains(typeId) && synonymAndDescendantIds.contains(conceptDescriptionTypeId))) {
					continue;
				}
				
				for (SnomedLanguageRefSetMember conceptDescriptionMember : conceptDescription.getLanguageRefSetMembers()) {
					
					if (!conceptDescriptionMember.isActive()) {
						continue;
					}
					
					if (!languageRefSetId.equals(conceptDescriptionMember.getRefSetIdentifierId())) {
						continue;
					}
					
					if (conceptDescriptionMember.equals(newLanguageRefSetMember)) {
						continue;
					}
					
					if (acceptabilityId.equals(conceptDescriptionMember.getAcceptabilityId())) {
						if (description.equals(conceptDescription)) {
							membersToRemove.add(newLanguageRefSetMember);
							continue label;
						} else if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(acceptabilityId)) {
							return new AddedInSourceAndTargetConflict(newLanguageRefSetMember.cdoID(), conceptDescriptionMember.cdoID()); 
						}
					} else {
						if (description.equals(conceptDescription)) {
							return new AddedInSourceAndTargetConflict(newLanguageRefSetMember.cdoID(), conceptDescriptionMember.cdoID());
						}
					}
				}
			}
		}
		
		for (SnomedLanguageRefSetMember memberToRemove : membersToRemove) {
			unlinkObject(memberToRemove);
		}
		
		return null;
	}
	
	@Override
	protected void unlinkObject(final CDOObject object) {

		if (object instanceof Relationship) {
			((Relationship) object).setSource(null);
			((Relationship) object).setDestination(null);
		} else if (object instanceof SnomedRefSetMember) {
			super.unlinkObject(object);
		} else {
			LOGGER.warn("Unexpected CDO object not unlinked: {}.", object);
		}
	}
}
