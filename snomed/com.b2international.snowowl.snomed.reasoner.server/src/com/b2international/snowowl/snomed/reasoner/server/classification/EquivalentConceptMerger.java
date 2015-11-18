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
package com.b2international.snowowl.snomed.reasoner.server.classification;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDeletionPlan;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan.InactivationReason;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

import bak.pcj.LongIterator;
import bak.pcj.set.LongSet;


/**
 *
 */
public class EquivalentConceptMerger {

	private final SnomedEditingContext editingContext;
	private final List<LongSet> equivalenciesToFix;
	private final SnomedConceptLookupService lookupService = new SnomedConceptLookupService();
	private final SnomedRefSetBrowser refSetBrowser = ApplicationContext.getServiceForClass(SnomedRefSetBrowser.class);
	
	public EquivalentConceptMerger(final SnomedEditingContext editingContext, final List<LongSet> equivalenciesToFix) {
		this.editingContext = editingContext;
		this.equivalenciesToFix = equivalenciesToFix;
	}
	
	public void fixEquivalencies() {
		// First resolve the equivalencies Map, to find the equivalent concept instances
		final Multimap<Concept, Concept> equivalentConcepts = resolveEquivalencies();
		// iterate over the sorted concepts and switch to the equivalent using
		// the resolved Map
		final SnomedDeletionPlan deletionPlan = new SnomedDeletionPlan();
		for (final Concept conceptToKeep : equivalentConcepts.keySet()) {
			final Collection<Concept> conceptsToRemove = equivalentConcepts.get(conceptToKeep);
			switchToEquivalentConcept(conceptToKeep, conceptsToRemove);
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
					this.editingContext.canDelete(concept, deletionPlan);
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
				final Concept concept = lookupService.getComponent(Long.toString(conceptId), editingContext.getTransaction());
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
	
	private void switchToEquivalentConcept(final Concept conceptToKeep, final Collection<Concept> conceptsToRemove) {
		removeDeprecatedRelationships(conceptToKeep, conceptsToRemove);
		for (final Concept conceptToRemove : conceptsToRemove) {
			switchInboundRelationships(conceptToKeep, conceptsToRemove, conceptToRemove);
			switchOutboundRelationships(conceptToKeep, conceptsToRemove, conceptToRemove);
			switchRefSetMembers(conceptToKeep, conceptToRemove);
		}
	}
	
	private void switchOutboundRelationships(final Concept conceptToKeep, final Collection<Concept> conceptsToRemove,
			final Concept conceptToRemove) {
		
		for (final Relationship outboundRelationship : newArrayList(conceptToRemove.getOutboundRelationships())) {
			
			if (!outboundRelationship.isActive()) {
				continue;
			}
			
			if (conceptsToRemove.contains(outboundRelationship.getDestination())) {
				SnomedModelExtensions.removeOrDeactivate(outboundRelationship);
				continue;
			}
				
			boolean found = false;
			for (final Relationship replacementOutboundRelationship : conceptToKeep.getOutboundRelationships()) {
				if (replacementOutboundRelationship.isActive()
						&& outboundRelationship.getType().equals(replacementOutboundRelationship.getType())
						&& outboundRelationship.getDestination().equals(replacementOutboundRelationship.getDestination())
						&& outboundRelationship.getModifier().equals(replacementOutboundRelationship.getModifier())) {
					found = true;
					break;
				}
			}

			if (found) {
				SnomedModelExtensions.removeOrDeactivate(outboundRelationship);
			} else {
				final Relationship relationshipToKeep = editingContext.buildDefaultRelationship(conceptToKeep, 
						outboundRelationship.getType(), 
						outboundRelationship.getDestination(),
						outboundRelationship.getCharacteristicType(),
						outboundRelationship.getModule(),
						editingContext.getNamespace()); // FIXME: get namespace from outbound relationship

				switchRelationship(relationshipToKeep, outboundRelationship);
			}
		}
	}

	private void switchInboundRelationships(final Concept conceptToKeep, final Collection<Concept> conceptsToRemove,
			final Concept conceptToRemove) {
		
		for (final Relationship inboundRelationship : newArrayList(conceptToRemove.getInboundRelationships())) {
			
			if (!inboundRelationship.isActive()) {
				continue;
			}
			
			if (conceptsToRemove.contains(inboundRelationship.getSource())) {
				SnomedModelExtensions.removeOrDeactivate(inboundRelationship);
				continue;
			}
			
			boolean found = false;
			for (final Relationship replacementInboundRelationship : conceptToKeep.getInboundRelationships()) {
				if (replacementInboundRelationship.isActive()
						&& inboundRelationship.getType().equals(replacementInboundRelationship.getType())
						&& inboundRelationship.getSource().equals(replacementInboundRelationship.getSource())
						&& inboundRelationship.getModifier().equals(replacementInboundRelationship.getModifier())) {
					found = true;
					break;
				}
			}

			if (found) {
				SnomedModelExtensions.removeOrDeactivate(inboundRelationship);
			} else {
				final Relationship relationshipToKeep = editingContext.buildDefaultRelationship(inboundRelationship.getSource(), 
						inboundRelationship.getType(), 
						conceptToKeep,
						inboundRelationship.getCharacteristicType(),
						inboundRelationship.getModule(),
						editingContext.getNamespace()); // FIXME: get namespace from inbound relationship

				switchRelationship(relationshipToKeep, inboundRelationship);
			}
		}
	}

	private void switchRelationship(Relationship relationshipToKeep, Relationship relationshipToRemove) {
		relationshipToKeep.setCharacteristicType(relationshipToRemove.getCharacteristicType());
		switchConcreteDomains(relationshipToKeep, relationshipToRemove);
		SnomedModelExtensions.removeOrDeactivate(relationshipToRemove);
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
				
				if (!refSetBrowser.hasMapping(branchPath, 
						simpleMapMember.getRefSetIdentifierId(), 
						id, 
						simpleMapMember.getMapTargetComponentId())) {
					
					final SnomedSimpleMapRefSetMember newMember = refSetEditingContext.createSimpleMapRefSetMember(
							SnomedRefSetEditingContext.createConceptTypePair(id), 
							ComponentIdentifierPair.create(
									simpleMapMember.getMapTargetComponentType(), 
									simpleMapMember.getMapTargetComponentId()),
							simpleMapMember.getModuleId(),
							(SnomedMappingRefSet) simpleMapMember.getRefSet());
					
					newMember.setMapTargetComponentDescription(simpleMapMember.getMapTargetComponentDescription());
					((SnomedMappingRefSet) simpleMapMember.getRefSet()).getMembers().add(newMember);

				} else {
					SnomedModelExtensions.removeOrDeactivate(referringMemberToRemove);
				}
				
			} else {
				
				if (!refSetBrowser.isActiveMemberOf(branchPath, 
						Long.parseLong(referringMemberToRemove.getRefSetIdentifierId()), 
						Long.parseLong(id))) {
					
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
	
	private void removeDeprecatedRelationships(final Concept conceptToKeep, final Collection<Concept> conceptToRemove) {

		for (final Relationship inboundRelationship : newArrayList(conceptToKeep.getInboundRelationships())) {
			if (conceptToRemove.contains(inboundRelationship.getSource())) {
				SnomedModelExtensions.removeOrDeactivate(inboundRelationship);
			}
		}

		for (final Relationship outboundRelationship : newArrayList(conceptToKeep.getOutboundRelationships())) {
			if (conceptToRemove.contains(outboundRelationship.getDestination())) {
				SnomedModelExtensions.removeOrDeactivate(outboundRelationship);
			}
		}
	}
	
}