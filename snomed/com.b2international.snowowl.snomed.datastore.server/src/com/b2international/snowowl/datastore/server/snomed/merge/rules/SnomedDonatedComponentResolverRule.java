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
package com.b2international.snowowl.datastore.server.snomed.merge.rules;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOStaleReferencePolicy;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.time.TimeUtil;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * @since 6.1.0
 */
public class SnomedDonatedComponentResolverRule extends AbstractSnomedMergeConflictRule {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedDonatedComponentResolverRule.class);

	private final Map<CDOID, CDOID> newDonatedComponents;
	private final Set<CDOID> changedDonatedComponents;

	public SnomedDonatedComponentResolverRule(final Map<CDOID,CDOID> newDonatedComponents, final Set<CDOID> changedDonatedComponents) {
		this.newDonatedComponents = newDonatedComponents;
		this.changedDonatedComponents = changedDonatedComponents;
	}

	@Override
	public Collection<MergeConflict> validate(final CDOTransaction transaction) {

		Stopwatch stopwatch = Stopwatch.createStarted();
		
		// XXX This is important to avoid ObjectNotFoundExceptions due to the removal of extension concepts
		transaction.options().setStaleReferencePolicy(CDOStaleReferencePolicy.PROXY);
		
		Map<CDOID, Component> newComponentsMap = StreamSupport
				.stream(ComponentUtils2.getNewObjects(transaction, Component.class).spliterator(), false)
				.collect(toMap(CDOObject::cdoID, Function.identity()));
		
		Iterable<Relationship> allNewAndDirtyRelationships = Iterables.concat(ComponentUtils2.getNewObjects(transaction, Relationship.class),
				ComponentUtils2.getDirtyObjects(transaction, Relationship.class));
		
		Multimap<CDOID, Relationship> destinationToRelationshipsMap = HashMultimap.create();
		
		StreamSupport.stream(allNewAndDirtyRelationships.spliterator(), false)
			.filter(r -> !newDonatedComponents.containsKey(r.getSource().cdoID()) && newDonatedComponents.containsKey(r.getDestination().cdoID()))
			.forEach(r -> destinationToRelationshipsMap.put(r.getDestination().cdoID(), r));
		
		for (final Entry<CDOID, CDOID> entry : newDonatedComponents.entrySet()) {

			final CDOID sourceCDOID = entry.getKey();
			final CDOID targetCDOID = entry.getValue();

			final Optional<CDOObject> sourceComponent = Optional.ofNullable(newComponentsMap.get(sourceCDOID));
			final Optional<CDOObject> targetComponent = Optional.ofNullable(CDOUtils.getObjectIfExists(transaction, targetCDOID));

			if (sourceComponent.isPresent() && targetComponent.isPresent()) {

				if (sourceComponent.get() instanceof Concept && targetComponent.get() instanceof Concept) {
					
					final Concept extensionConcept = (Concept) sourceComponent.get();
					final Concept donatedConcept = (Concept) targetComponent.get();
					
					LOGGER.info(">>> Processing donated concept with id '{}'", donatedConcept.getId());
					
					unfreezeRevision(extensionConcept);
					
					final List<Description> additionalExtensionDescriptions = extensionConcept.getDescriptions().stream()
							.filter(extension -> !donatedConcept.getDescriptions().stream()
													.anyMatch(donated -> donated.getId().equals(extension.getId())))
							.collect(toList());

					final List<Relationship> additionalExtensionRelationships = extensionConcept.getOutboundRelationships().stream()
							.filter(extension -> !donatedConcept.getOutboundRelationships().stream()
													.anyMatch(donated -> donated.getId().equals(extension.getId())))
							.collect(toList());

					// association refset members?
					// inactivation indicator refset members?
					// concrete domain refset members?
					
					// handle inbound relationships
					if (destinationToRelationshipsMap.containsKey(extensionConcept.cdoID())) {
						
						Collection<Relationship> inboundRelationships = destinationToRelationshipsMap.get(extensionConcept.cdoID());
						
						for (Relationship relationship : inboundRelationships) {
							
							Concept relationshipSourceConcept = relationship.getSource();
							
							LOGGER.info("Replacing inbound reference from '{}' to '{}' with id '{}'", relationshipSourceConcept.getId(),
									donatedConcept.getId(), relationship.getId());

							unfreezeRevision(relationshipSourceConcept);
							relationship.setDestination(donatedConcept);
							
						}
						
					}
					
					EcoreUtil.remove(extensionConcept);

					for (final Description extensionDescription : additionalExtensionDescriptions) {
						LOGGER.info("Adding extension description to the donated version '{}' - '{}'", extensionDescription.getId(),
								extensionDescription.getTerm());
						donatedConcept.getDescriptions().add(extensionDescription);
					}

					for (final Relationship extensionRelationship : additionalExtensionRelationships) {
						
						if (newDonatedComponents.containsKey(extensionRelationship.getDestination().cdoID())) {
							
							Optional<Concept> newDestinationConcept = Optional.ofNullable(CDOUtils.getObjectIfExists(transaction,
									newDonatedComponents.get(extensionRelationship.getDestination().cdoID())));

							if (newDestinationConcept.isPresent()) {
								
								LOGGER.info("Replacing outbound reference from '{}' to '{}' with id '{}'", donatedConcept.getId(),
										newDestinationConcept.get().getId(), extensionRelationship.getId());

								extensionRelationship.setDestination(newDestinationConcept.get());
							}
						}
						
						LOGGER.info("Adding extension relationship to the donated version with id '{}'", extensionRelationship.getId());
						
						donatedConcept.getOutboundRelationships().add(extensionRelationship);
					}

					LOGGER.info("<<< Processed donated concept with id '{}'", extensionConcept.getId());

				} else if (sourceComponent.get() instanceof Description && targetComponent.get() instanceof Description) {

					final Description extensionDescription = (Description) sourceComponent.get();
					final Description donatedDescription = (Description) targetComponent.get();

					LOGGER.info(">>> Processing donated description with id '{}'", extensionDescription.getId());
					
					EcoreUtil.remove(extensionDescription);

					// association refset members?
					// inactivation indicator refset members?

					donatedDescription.getLanguageRefSetMembers().addAll(extensionDescription.getLanguageRefSetMembers());

					LOGGER.info("<<< Processed donated description with id '{}'", extensionDescription.getId());

				} else if (sourceComponent.get() instanceof Relationship && targetComponent.get() instanceof Relationship) {

					final Relationship sourceRelationship = (Relationship) sourceComponent.get();

					LOGGER.info(">>> Processing donated relationship with id '{}'", sourceRelationship.getId());
					
					EcoreUtil.remove(sourceRelationship);

					// concrete domain members?

					LOGGER.info("<<< Processed donated relationship with id '{}'", sourceRelationship.getId());
				}

			}

		}
		
		for (final CDOID id : changedDonatedComponents) {

			final Optional<CDOObject> object = Optional.ofNullable(CDOUtils.getObjectIfExists(transaction, id));

			if (object.isPresent()) {

				if (object.get() instanceof Component) {

					final Component component = (Component) object.get();

					transaction.getLastSavepoint().getDirtyObjects().remove(id);
					transaction.getLastSavepoint().getRevisionDeltas().remove(id);

					LOGGER.info("Keeping latest ({}) version of donated component '{}' with id '{}'",
							EffectiveTimes.format(component.getEffectiveTime()), component.eClass().getName(), component.getId());
				}
			}
		}

		LOGGER.info("Donated component resolution finished in {}", TimeUtil.toString(stopwatch));
		
		return emptySet();
	}
	
	private void unfreezeRevision(Concept concept) {
		copyRevision(concept);
		TreeIterator<EObject> allContents = concept.eAllContents();
		while (allContents.hasNext()) {
			copyRevision(allContents.next());
		}
	}

	private void copyRevision(EObject eObject) {
		if (eObject instanceof InternalCDOObject) {
			InternalCDOObject internalCDOObject = (InternalCDOObject) eObject;
			InternalCDORevision revision = internalCDOObject.cdoRevision().copy();
			internalCDOObject.cdoInternalSetRevision(revision);
		}
	}
	
}
