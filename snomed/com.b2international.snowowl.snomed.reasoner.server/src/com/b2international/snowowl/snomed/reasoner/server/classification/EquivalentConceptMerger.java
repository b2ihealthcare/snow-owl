/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.server.classification;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedDeletionPlan;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan.InactivationReason;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;


/**
 *
 */
public class EquivalentConceptMerger {

	private final SnomedEditingContext editingContext;
	private final List<LongSet> equivalenciesToFix;
	
	public EquivalentConceptMerger(final SnomedEditingContext editingContext, final List<LongSet> equivalenciesToFix) {
		this.editingContext = editingContext;
		this.equivalenciesToFix = equivalenciesToFix;
	}
	
	public void fixEquivalencies() {
		// First resolve the equivalencies Map, to find the equivalent concept instances
		final Multimap<Concept, Concept> equivalentConcepts = resolveEquivalencies();
		final Iterable<Concept> concepts = Iterables.concat(equivalentConcepts.keySet(), equivalentConcepts.values());
		final Iterable<String> destinationIds = Iterables.transform(concepts, Concept::getId);
		SnomedRelationships inboundRelationships = SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true)
				.filterByDestination(destinationIds)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, editingContext.getBranch())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
		final Multimap<String, Relationship> inboundRelationshipMap = HashMultimap.create(FluentIterable
				.from(inboundRelationships)
				.transform(relationship -> (Relationship) editingContext.lookup(relationship.getStorageKey()))
				//Exclude relationships that were already marked redundant
				.filter(relationship -> relationship.getSource() != null && relationship.getDestination() != null)
				.index( relationship -> relationship.getDestination().getId()));
		for (Relationship relationship : ComponentUtils2.getNewObjects(editingContext.getTransaction(), Relationship.class)) {
			inboundRelationshipMap.put(relationship.getDestination().getId(), relationship);
		}
		for (Relationship relationship : ComponentUtils2.getDetachedObjects(editingContext.getTransaction(), Relationship.class)) {
			inboundRelationshipMap.values().remove(relationship);
		}
		
		// iterate over the sorted concepts and switch to the equivalent using
		// the resolved Map
		final SnomedDeletionPlan deletionPlan = new SnomedDeletionPlan();
		for (final Concept conceptToKeep : equivalentConcepts.keySet()) {
			final Collection<Concept> conceptsToRemove = equivalentConcepts.get(conceptToKeep);
			switchToEquivalentConcept(conceptToKeep, conceptsToRemove, inboundRelationshipMap);
			removeOrDeactivate(conceptsToRemove, deletionPlan);
		}
		if (!deletionPlan.isEmpty()) {
			if (!deletionPlan.isRejected()) {
				this.editingContext.delete(deletionPlan);
			} else {
				throw new SnowowlRuntimeException(Joiner.on(",").join(deletionPlan.getRejectionReasons()));
			}
		}
	}

	private void removeOrDeactivate(Collection<Concept> conceptsToRemove, final SnomedDeletionPlan deletionPlan) {
		if (!Iterables.isEmpty(conceptsToRemove)) {
			final SnomedInactivationPlan plan = new SnomedInactivationPlan(this.editingContext);
			for (final Concept concept : conceptsToRemove) {
				if (concept.isReleased()) {
					this.editingContext.inactivateConcepts(plan, new NullProgressMonitor(), concept.cdoID());		
				} else {
					this.editingContext.canDelete(concept, deletionPlan, false);
				}
			}
			plan.performInactivation(InactivationReason.RETIRED, null);
		}
	}

	/**
	 * Resolves the equivalency {@link Map} to a {@link Map} that contains only
	 * {@link Concept} instances. The returned {@link Map} is contains value
	 * {@link Concept}s as replacement for the key {@link Concept}s.
	 * 
	 * @param equivalencies
	 * @param results
	 * @param helper
	 * @return
	 */
	private Multimap<Concept, Concept> resolveEquivalencies() {
		final Multimap<Concept, Concept> processedEquivalencies = HashMultimap.create();
		
		for (final LongSet equivalentSet : equivalenciesToFix) {
			
			final List<Concept> conceptsToRemove = Lists.newArrayList();
			
			for (final LongIterator itr = equivalentSet.iterator(); itr.hasNext(); /* empty */) {
				final long conceptId = itr.next();
				final Concept concept = editingContext.lookup(Long.toString(conceptId), Concept.class);
				conceptsToRemove.add(concept);
			}
			
			final Concept conceptToKeep = Ordering.natural().onResultOf(new Function<Concept, Long>() {
				@Override
				public Long apply(Concept input) {
					return CDOIDUtil.getLong(input.cdoID());
				}
			}).min(conceptsToRemove);
			
			conceptsToRemove.remove(conceptToKeep);
			processedEquivalencies.putAll(conceptToKeep, conceptsToRemove);
		}
		
		return processedEquivalencies;
	}
	
	private void switchToEquivalentConcept(final Concept conceptToKeep, final Collection<Concept> conceptsToRemove, Multimap<String, Relationship> inboundRelationshipMap) {
		removeDeprecatedRelationships(conceptToKeep, conceptsToRemove, inboundRelationshipMap);
		for (final Concept conceptToRemove : conceptsToRemove) {
			switchInboundRelationships(conceptToKeep, conceptsToRemove, conceptToRemove, inboundRelationshipMap);
			switchOutboundRelationships(conceptToKeep, conceptsToRemove, conceptToRemove, inboundRelationshipMap);
			switchRefSetMembers(conceptToKeep, conceptToRemove);
		}
	}
	
	private void switchOutboundRelationships(final Concept conceptToKeep, final Collection<Concept> conceptsToRemove, final Concept conceptToRemove, Multimap<String, Relationship> inboundRelationshipMap) {
		for (final Relationship relationshipToRemove : newArrayList(conceptToRemove.getOutboundRelationships())) {
			boolean found = false;
			for (final Relationship replacementOutboundRelationship : conceptToKeep.getOutboundRelationships()) {
				if (relationshipToRemove.getType().equals(replacementOutboundRelationship.getType())
						&& relationshipToRemove.getDestination().equals(replacementOutboundRelationship.getDestination())
						&& relationshipToRemove.getCharacteristicType().equals(replacementOutboundRelationship.getCharacteristicType())
						&& relationshipToRemove.getModifier().equals(replacementOutboundRelationship.getModifier())) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				if (!conceptsToRemove.contains(relationshipToRemove.getSource())) {
					final String namespace = SnomedIdentifiers.create(relationshipToRemove.getId()).getNamespace();
					final Relationship newRelationship = editingContext.buildDefaultRelationship(conceptToKeep, 
							relationshipToRemove.getType(), 
							relationshipToRemove.getDestination(),
							relationshipToRemove.getCharacteristicType(),
							relationshipToRemove.getModule(),
							namespace);
					
					inboundRelationshipMap.put(newRelationship.getDestination().getId(), newRelationship);
					switchRelationship(newRelationship, relationshipToRemove);
				}
			}
			if (inboundRelationshipMap.containsValue(relationshipToRemove)) {
				inboundRelationshipMap.remove(relationshipToRemove.getDestination().getId(), relationshipToRemove);
			}
			SnomedModelExtensions.removeOrDeactivate(relationshipToRemove);
		}
	}

	private void switchInboundRelationships(final Concept conceptToKeep, final Collection<Concept> conceptsToRemove, final Concept conceptToRemove, Multimap<String, Relationship> inboundRelationshipMap) {
		for (final Relationship relationshipToRemove : newArrayList(inboundRelationshipMap.get(conceptToRemove.getId()))) {
			boolean found = false;
			for (final Relationship replacementInboundRelationship : Sets.newHashSet(inboundRelationshipMap.get(conceptToKeep.getId()))) {
				if (relationshipToRemove.getType().equals(replacementInboundRelationship.getType())
						&& relationshipToRemove.getSource().equals(replacementInboundRelationship.getSource())
						&& relationshipToRemove.getCharacteristicType().equals(replacementInboundRelationship.getCharacteristicType())
						&& relationshipToRemove.getModifier().equals(replacementInboundRelationship.getModifier())) {
					found = true;
					break;
				}
			}
			if (!found) {
				if (!conceptsToRemove.contains(relationshipToRemove.getSource())) {
					final String namespace = SnomedIdentifiers.create(relationshipToRemove.getId()).getNamespace();
					final Relationship newRelationship = editingContext.buildDefaultRelationship(relationshipToRemove.getSource(), 
							relationshipToRemove.getType(), 
							conceptToKeep,
							relationshipToRemove.getCharacteristicType(),
							relationshipToRemove.getModule(),
							namespace);
					
					inboundRelationshipMap.put(newRelationship.getDestination().getId(), newRelationship);
					switchRelationship(newRelationship, relationshipToRemove);
				}
			}
			if (inboundRelationshipMap.containsValue(relationshipToRemove)) {
				inboundRelationshipMap.remove(relationshipToRemove.getDestination().getId(), relationshipToRemove);
			}
			SnomedModelExtensions.removeOrDeactivate(relationshipToRemove);
		}
	}

	private void switchRelationship(Relationship relationshipToKeep, Relationship relationshipToRemove) {
		relationshipToKeep.setCharacteristicType(relationshipToRemove.getCharacteristicType());
		switchConcreteDomains(relationshipToKeep, relationshipToRemove);
	}

	private void switchConcreteDomains(Relationship relationshipToKeep, Relationship relationshipToRemove) {
		for (SnomedConcreteDataTypeRefSetMember member : relationshipToRemove.getConcreteDomainRefSetMembers()) {
			
			if (!member.isActive()) {
				continue;
			}
			
			final SnomedConcreteDataTypeRefSetMember newMember = EcoreUtil.copy(member);
			
			newMember.setUuid(UUID.randomUUID().toString());
			newMember.unsetEffectiveTime();
			newMember.setReferencedComponentId(relationshipToKeep.getId());
			
			relationshipToKeep.getConcreteDomainRefSetMembers().add(newMember);
		}
	}
	
	private void switchRefSetMembers(final Concept conceptToKeep, final Concept conceptToRemove) {
		
		final String id = conceptToKeep.getId();
		final SnomedRefSetEditingContext refSetEditingContext = editingContext.getRefSetEditingContext();
		final IBranchPath branchPath = BranchPathUtils.createPath(editingContext.getTransaction());
		
		for (final SnomedRefSetMember referringMemberToRemove : newArrayList(editingContext.getReferringMembers(conceptToRemove))) {
		
			if (!referringMemberToRemove.isActive()) {
				continue;
			}
			
			if (referringMemberToRemove instanceof SnomedSimpleMapRefSetMember) {
				final SnomedSimpleMapRefSetMember simpleMapMember = (SnomedSimpleMapRefSetMember) referringMemberToRemove;
				
				if (!hasMapping(branchPath, simpleMapMember.getRefSetIdentifierId(), id, simpleMapMember.getMapTargetComponentId())) {
					
					final SnomedSimpleMapRefSetMember newMember = refSetEditingContext.createSimpleMapRefSetMember(
							id, 
							simpleMapMember.getMapTargetComponentId(),
							simpleMapMember.getModuleId(),
							(SnomedMappingRefSet) simpleMapMember.getRefSet());
					
					newMember.setMapTargetComponentDescription(simpleMapMember.getMapTargetComponentDescription());
					((SnomedMappingRefSet) simpleMapMember.getRefSet()).getMembers().add(newMember);

				} else {
					SnomedModelExtensions.removeOrDeactivate(referringMemberToRemove);
				}
				
			} else {
				
				if (!isActiveMemberOf(branchPath, 
						referringMemberToRemove.getRefSetIdentifierId(), 
						id)) {
					
					final SnomedRefSetMember newMember = EcoreUtil.copy(referringMemberToRemove);
					newMember.unsetEffectiveTime();
					newMember.setUuid(UUID.randomUUID().toString());
					newMember.setReferencedComponentId(id);
					
					if (newMember.getRefSet() instanceof SnomedRegularRefSet) {
						((SnomedRegularRefSet) newMember.getRefSet()).getMembers().add(newMember);
					} else if (newMember.getRefSet() instanceof SnomedStructuralRefSet) {
						
						if (newMember instanceof SnomedConcreteDataTypeRefSetMember) {
							conceptToKeep.getConcreteDomainRefSetMembers().add((SnomedConcreteDataTypeRefSetMember) newMember);
						} else if (newMember instanceof SnomedAttributeValueRefSetMember) {
							conceptToKeep.getInactivationIndicatorRefSetMembers().add((SnomedAttributeValueRefSetMember) newMember);
						} else if (newMember instanceof SnomedAssociationRefSetMember) {
							conceptToKeep.getAssociationRefSetMembers().add((SnomedAssociationRefSetMember) newMember);
						}
					}
					
				} else {
					SnomedModelExtensions.removeOrDeactivate(referringMemberToRemove);
				}
			}
		}
	}
	
	private boolean isActiveMemberOf(IBranchPath branchPath, String refSetIdentifierId, String referencedComponentId) {
		return SnomedRequests.prepareSearchMember()
				.setLimit(0)
				.filterByActive(true)
				.filterByRefSet(refSetIdentifierId)
				.filterByReferencedComponent(referencedComponentId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync().getTotal() > 0;
	}

	private boolean hasMapping(IBranchPath branchPath, String refSetIdentifierId, String referencedComponentId, String mapTargetComponentId) {
		return SnomedRequests.prepareSearchMember()
				.setLimit(0)
				.filterByActive(true)
				.filterByRefSet(refSetIdentifierId)
				.filterByReferencedComponent(referencedComponentId)
				.filterByProps(OptionsBuilder.newBuilder().put(SnomedRefSetMemberIndexEntry.Fields.MAP_TARGET, mapTargetComponentId).build())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync().getTotal() > 0;
	}

	private void removeDeprecatedRelationships(final Concept conceptToKeep, final Collection<Concept> conceptsToRemove, final Multimap<String, Relationship> inboundRelationshipMap) {
		for (final Relationship inboundRelationship : Sets.newHashSet(inboundRelationshipMap.get(conceptToKeep.getId()))) {
			if (conceptsToRemove.contains(inboundRelationship.getSource())) {
				if (inboundRelationshipMap.containsValue(inboundRelationship)) {
					inboundRelationshipMap.remove(inboundRelationship.getDestination().getId(), inboundRelationship);
				}
				SnomedModelExtensions.removeOrDeactivate(inboundRelationship);
			}
		}
		
		for (final Relationship outboundRelationship : newArrayList(conceptToKeep.getOutboundRelationships())) {
			if (conceptsToRemove.contains(outboundRelationship.getDestination())) {
				if (inboundRelationshipMap.containsValue(outboundRelationship)) {
					inboundRelationshipMap.remove(outboundRelationship.getDestination().getId(), outboundRelationship);
				}
				SnomedModelExtensions.removeOrDeactivate(outboundRelationship);
			}
		}
	}
	
}