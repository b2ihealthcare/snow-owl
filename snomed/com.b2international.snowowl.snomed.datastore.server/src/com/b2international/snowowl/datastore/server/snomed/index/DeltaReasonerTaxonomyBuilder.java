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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.util.BytesRef;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.ecore.EClass;

import bak.pcj.map.LongKeyMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.collect.ImmutableSet;

/**
 * Stores the state of the SNOMED CT ontology after change processing. This class should be used for retracting OWL axioms and creating new ones
 * if reasoner change processing is enabled.
 */
public final class DeltaReasonerTaxonomyBuilder extends AbstractReasonerTaxonomyBuilder {

	private static final Set<EClass> TRACKED_ECLASSES = ImmutableSet.of(
			SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER,
			SnomedPackage.Literals.RELATIONSHIP,
			SnomedPackage.Literals.CONCEPT);

	private final LongSet conceptIdsToRemove = new LongOpenHashSet();
	private final LongSet conceptIdsToAdd = new LongOpenHashSet();
	
	/**
	 * Creates a new {@link DeltaReasonerTaxonomyBuilder} instance with the specified arguments.
	 * 
	 * @param source the taxonomy builder used as a baseline for changes (may not be {@code null})
	 * @param type the requested mode of operation
	 * @param changeSet the change set to apply (may not be {@code null})
	 */
	public DeltaReasonerTaxonomyBuilder(final AbstractReasonerTaxonomyBuilder source, final Type type, final ICDOCommitChangeSet changeSet) {
		
		super(checkNotNull(source, "source"), type);
		checkNotNull(changeSet, "changeSet");

		for (final Entry<CDOID, EClass> detachedComponent : changeSet.getDetachedComponents().entrySet()) {
			if (TRACKED_ECLASSES.contains(detachedComponent.getValue())) {
				registerRemove(detachedComponent.getKey(), detachedComponent.getValue(), true);
			}
		}

		for (final CDOObject dirtyObject : changeSet.getDirtyComponents()) {
			final EClass dirtyEClass = dirtyObject.eClass();
			if (TRACKED_ECLASSES.contains(dirtyEClass)) {
				registerRemove(dirtyObject.cdoID(), dirtyEClass, false);
			}
		}
	
		for (final CDOObject dirtyObject : changeSet.getDirtyComponents()) {
			final EClass dirtyEClass = dirtyObject.eClass();
			if (TRACKED_ECLASSES.contains(dirtyEClass)) {
				registerAdd(dirtyObject, dirtyEClass);
			}
		}

		for (final CDOObject newObject : changeSet.getNewComponents()) {
			final EClass newEClass = newObject.eClass();
			if (TRACKED_ECLASSES.contains(newEClass)) {
				registerAdd(newObject, newEClass);
			}
		}

		rebuildTaxonomy();
	}
	
	public LongSet getConceptIdsToRemove() {
		return conceptIdsToRemove;
	}
	
	public LongSet getConceptIdsToAdd() {
		return conceptIdsToAdd;
	}
	
	private void registerRemove(final CDOID id, final EClass eClass, final boolean detached) {

		final long storageKey = CDOIDUtils.asLong(id);
		final long conceptId = componentStorageKeyToConceptId.get(storageKey);
		
		if (0L == conceptId) {
			// TODO: log this?
			return;
		}
		
		conceptIdsToRemove.add(conceptId);
		
		// If we got here via a dirty component, the definition will have to be regenerated
		if (!detached) {
			conceptIdsToAdd.add(conceptId);
		}
		
		if (SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER.equals(eClass)) {
			removeConcreteDomainMember(conceptId, storageKey);
		} else if (SnomedPackage.Literals.RELATIONSHIP.equals(eClass)) {
			removeSourceRelationship(conceptId, storageKey);
		} else if (SnomedPackage.Literals.CONCEPT.equals(eClass)) {
			removeConcept(conceptId, detached);
		} else {
			throw new IllegalStateException(MessageFormat.format("Unhandled EClass {0}''.", eClass));
		}
		
		componentStorageKeyToConceptId.remove(storageKey);
	}

	private void removeConcreteDomainMember(final long conceptId, final long storageKey) {
		removeConceptConcreteDomainMember(conceptId, storageKey);
		removeStatementConcreteDomainMember(conceptId, storageKey);
	}

	private void removeConceptConcreteDomainMember(final long conceptId, final long storageKey) {
		final Collection<ConcreteDomainFragment> conceptFragments = getConceptConcreteDomainFragments(conceptId);

		removeConcreteDomainFragment(conceptFragments, storageKey);
		if (conceptFragments.isEmpty()) {
			conceptIdToStatedConcreteDomains.remove(conceptId);
		}
	}

	private void removeStatementConcreteDomainMember(final long conceptId, final long storageKey) {
		final Collection<StatementFragment> statementFragments = getStatedStatementFragments(conceptId);
		
		for (final StatementFragment fragment : statementFragments) {
			
			final long statementId = fragment.getStatementId();
			final Collection<ConcreteDomainFragment> statementConcreteDomainFragments = getStatementConcreteDomainFragments(statementId);
			
			removeConcreteDomainFragment(statementConcreteDomainFragments, storageKey);
			if (statementConcreteDomainFragments.isEmpty()) {
				statementIdToConcreteDomain.remove(statementId);
			}
		}
	}

	private void removeConcreteDomainFragment(final Collection<ConcreteDomainFragment> fragments, final long storageKey) {
		for (final Iterator<ConcreteDomainFragment> it = fragments.iterator(); it.hasNext(); /* empty */) {
			final ConcreteDomainFragment fragment = it.next();
			if (storageKey == fragment.getStorageKey()) {
				it.remove();
				break;
			}
		}
	}

	private void removeSourceRelationship(final long conceptId, final long storageKey) {
		final Collection<StatementFragment> statementFragments = getStatedStatementFragments(conceptId);
		
		removeStatement(statementFragments, storageKey);
		if (statementFragments.isEmpty()) {
			conceptIdToStatedStatements.remove(conceptId);
		}
	}

	private void removeStatement(final Collection<StatementFragment> statementFragments, final long storageKey) {
		for (final Iterator<StatementFragment> it = statementFragments.iterator(); it.hasNext(); /* empty */) {
			final StatementFragment fragment = it.next();
			if (storageKey == fragment.getStorageKey()) {
				statementIdToConcreteDomain.remove(fragment.getStatementId());
				it.remove();
				break;
			}
		}
	}

	private void removeConcept(final long conceptId, final boolean detached) {
		exhaustiveConceptIds.remove(conceptId);
		fullyDefinedConceptIds.remove(conceptId);
		
		if (detached) {
			conceptIdToStatedStatements.remove(conceptId);
			conceptIdToStatedConcreteDomains.remove(conceptId);

			// Leave an empty spot instead of renumbering everything
			final int internalId = getInternalId(conceptId);
			internalIdToconceptId.set(internalId, -1L);
			conceptIdToInternalId.remove(conceptId);
			
			// If we thought that this definition should be regenerated, we have to reconsider
			conceptIdsToAdd.remove(conceptId);
		}
	}
	
	private void registerAdd(final CDOObject object, final EClass eClass) {

		if (SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER.equals(eClass)) {
			
			final SnomedConcreteDataTypeRefSetMember member = (SnomedConcreteDataTypeRefSetMember) object;
			if (member.isActive()) {
				final short referencedComponentType = member.getReferencedComponentType();
				
				if (SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER == referencedComponentType) {
					addRelationshipConcreteDomainMember(member);
				} else {
					addConceptConcreteDomainMember(member);
				}
			}
			
		} else if (SnomedPackage.Literals.RELATIONSHIP.equals(eClass)) {
			
			final Relationship relationship = (Relationship) object;
			if (relationship.isActive()) {			
				addRelationship(relationship);
			}
			
		} else if (SnomedPackage.Literals.CONCEPT.equals(eClass)) {
			
			final Concept concept = (Concept) object;
			addConcept(concept);
						
		} else {
			throw new IllegalStateException(MessageFormat.format("Unhandled EClass {0}''.", eClass));
		}
	}

	private boolean isCharacteristicTypeApplicable (final String characteristicTypeId) {
		
		if (isReasonerMode()) {
			return Concepts.STATED_RELATIONSHIP.equals(characteristicTypeId);
		} else {
			return Concepts.DEFINING_CHARACTERISTIC_TYPES.contains(characteristicTypeId);
		}
	}

	private void addRelationshipConcreteDomainMember(final SnomedConcreteDataTypeRefSetMember member) {

		final String characteristicTypeId = member.getCharacteristicTypeId();
		
		if (!isCharacteristicTypeApplicable(characteristicTypeId) && !Concepts.ADDITIONAL_RELATIONSHIP.equals(characteristicTypeId)) {
			return;
		}
		
		final long statementId = Long.valueOf(member.getReferencedComponentId());
		final ConcreteDomainFragment fragment = createFragment(member);
		addToMultimap(statementIdToConcreteDomain, statementId, fragment);
	}

	private void addConceptConcreteDomainMember(final SnomedConcreteDataTypeRefSetMember member) {
		
		if (!isCharacteristicTypeApplicable(member.getCharacteristicTypeId())) {
			return;
		}

		final long conceptId = Long.valueOf(member.getReferencedComponentId());
		final ConcreteDomainFragment fragment = createFragment(member);
		addToMultimap(conceptIdToStatedConcreteDomains, conceptId, fragment);
	}

	private ConcreteDomainFragment createFragment(final SnomedConcreteDataTypeRefSetMember member) {
		
		final long uomId = (null == member.getUomComponentId()) 
				? ConcreteDomainFragment.UNSET_UOM_ID 
				: Long.valueOf(member.getUomComponentId());
		
		final byte ordinal = (byte) member.getDataType().ordinal();
		final long storageKey = CDOIDUtils.asLong(member.cdoID());
		final long refSetId = Long.valueOf(member.getRefSetIdentifierId());
		
		return new ConcreteDomainFragment(new BytesRef(member.getSerializedValue()), 
				new BytesRef(member.getLabel()), 
				ordinal, 
				uomId, 
				storageKey, 
				refSetId);
	}

	private void addRelationship(final Relationship relationship) {
		
		if (!isCharacteristicTypeApplicable(relationship.getCharacteristicType().getId())) {
			return;
		}
		
		final long sourceId = Long.valueOf(relationship.getSource().getId());
		
		final long statementId = Long.valueOf(relationship.getId());
		final long storageKey = CDOIDUtils.asLong(relationship.cdoID());
		final long destinationId = Long.valueOf(relationship.getDestination().getId());
		final long typeId = Long.valueOf(relationship.getType().getId());
		final boolean destinationNegated = relationship.isDestinationNegated();
		final boolean universal = Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(relationship.getModifier().getId());
		final byte group = (byte) relationship.getGroup();
		final byte unionGroup = (byte) relationship.getUnionGroup();
		
		final StatementFragment fragment = new StatementFragment(
				typeId, 
				destinationId, 
				destinationNegated, 
				group, 
				unionGroup, 
				universal, 
				statementId, 
				storageKey
		);

		addToMultimap(conceptIdToStatedStatements, sourceId, fragment);
	}

	private void addConcept(final Concept concept) {

		final long conceptId = Long.valueOf(concept.getId());
		final long storageKey = CDOIDUtils.asLong(concept.cdoID());
		
		if (!concept.isActive()) {
			// Remove all traces from concept which is either new and already inactive, or dirty and has been inactivated
			removeConcept(conceptId, true);
			return;
		}
		
		if (concept.isExhaustive()) {
			exhaustiveConceptIds.add(conceptId);
		}
		
		if (!concept.isPrimitive()) {
			fullyDefinedConceptIds.add(conceptId);
		}
		
		internalIdToconceptId.add(conceptId);
		conceptIdToInternalId.put(conceptId, internalIdToconceptId.size() - 1);
		componentStorageKeyToConceptId.put(storageKey, conceptId);
		conceptIdsToAdd.add(conceptId);
	}

	private <T> void addToMultimap(final LongKeyMap multimap, final long key, final T fragment) {
		@SuppressWarnings("unchecked")
		List<T> fragments = (List<T>) multimap.get(key);
		
		if (null == fragments) {
			fragments = newArrayList();
			multimap.put(key, fragments);
		}
			
		fragments.add(fragment);
	}

	private void rebuildTaxonomy() {

		// Upper bound estimate, exact if there were no removals
		final int conceptCount = internalIdToconceptId.size();
		final int[] outboundIsACount = new int[conceptCount];
		final int[] inboundIsACount = new int[conceptCount];

		superTypes = new int[conceptCount][];
		subTypes = new int[conceptCount][];

		for (int sourceInternalId = 0; sourceInternalId < internalIdToconceptId.size(); sourceInternalId++) {
			
			final long sourceId = internalIdToconceptId.get(sourceInternalId);
			if (-1L == sourceId) {
				continue;
			}
			
			final Collection<StatementFragment> fragments = getStatedStatementFragments(sourceId);
			for (final StatementFragment fragment : fragments) {
				
				final long typeId = fragment.getTypeId();
				if (IS_A_ID != typeId) {
					continue;
				}
				
				final long destinationId = fragment.getDestinationId();
				final int destinationInternalId = getInternalId(destinationId);
				if (-1L == destinationInternalId) {
					continue;
				}
				
				outboundIsACount[sourceInternalId]++;
				inboundIsACount[destinationInternalId]++;
			}
		}

		for (int i = 0; i < conceptCount; i++) {
			superTypes[i] = new int[outboundIsACount[i]];
			subTypes[i] = new int[inboundIsACount[i]];
		}

		// Create last used index matrices for IS A relationships (initialized to 0 for all concepts)
		final int[] lastSuperTypeIdx = new int[conceptCount];
		final int[] lastSubTypeIdx = new int[conceptCount];

		// Register IS A relationships as subtype and supertype internal IDs
		for (int sourceInternalId = 0; sourceInternalId < internalIdToconceptId.size(); sourceInternalId++) {
			
			final long sourceId = internalIdToconceptId.get(sourceInternalId);
			if (-1L == sourceId) {
				continue;
			}
			
			final Collection<StatementFragment> fragments = getStatedStatementFragments(sourceId);
			for (final StatementFragment fragment : fragments) {
				
				final long typeId = fragment.getTypeId();
				if (IS_A_ID != typeId) {
					continue;
				}
				
				final long destinationId = fragment.getDestinationId();
				final int destinationInternalId = getInternalId(destinationId);
				if (-1L == destinationInternalId) {
					continue;
				}
				
				superTypes[sourceInternalId][lastSuperTypeIdx[sourceInternalId]++] = destinationInternalId;
				subTypes[destinationInternalId][lastSubTypeIdx[destinationInternalId]++] = sourceInternalId;
			}
		}
	}
}