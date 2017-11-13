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
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 6.1.0
 */
public class SnomedDonatedComponentResolverRule extends AbstractSnomedMergeConflictRule {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedDonatedComponentResolverRule.class);

	private final Collection<Pair<CDOID,CDOID>> newDonatedComponents;
	private final Set<CDOID> changedComponentCandidates;

	public SnomedDonatedComponentResolverRule(final Collection<Pair<CDOID,CDOID>> newDonatedComponents, final Set<CDOID> changedComponentCandidates) {
		this.newDonatedComponents = newDonatedComponents;
		this.changedComponentCandidates = changedComponentCandidates;
	}

	@Override
	public Collection<MergeConflict> validate(final CDOTransaction transaction) {

		for (final Pair<CDOID, CDOID> entry : newDonatedComponents) {

			final CDOID sourceCDOID = entry.getA();
			final CDOID targetCDOID = entry.getB();

			final Optional<CDOObject> sourceComponent = transaction.getNewObjects().values().stream()
					.filter(c -> CDOIDUtil.equals(c.cdoID(), sourceCDOID))
					.findFirst();

			final Optional<CDOObject> targetComponent = Optional.ofNullable(CDOUtils.getObjectIfExists(transaction, targetCDOID));

			if (sourceComponent.isPresent() && targetComponent.isPresent()) {

				if (sourceComponent.get() instanceof Concept && targetComponent.get() instanceof Concept) {

					final Concept extensionConcept = (Concept) sourceComponent.get();
					final Concept donatedConcept = (Concept) targetComponent.get();

					final List<Description> additionalExtensionDescriptions = extensionConcept.getDescriptions().stream().filter(
							extension -> !donatedConcept.getDescriptions().stream().anyMatch(donated -> donated.getId().equals(extension.getId())))
							.collect(toList());

					final List<Relationship> additionalExtensionRelationships = extensionConcept.getOutboundRelationships().stream()
							.filter(extension -> !donatedConcept.getOutboundRelationships().stream()
									.anyMatch(donated -> donated.getId().equals(extension.getId())))
							.collect(toList());

					// association refset members?
					// inactivation indicator refset members?
					// concrete domain refset members?

					EcoreUtil.remove(extensionConcept);

					for (final Description extensionDescription : additionalExtensionDescriptions) {
						donatedConcept.getDescriptions().add(extensionDescription);
					}

					for (final Relationship extensionRelationship : additionalExtensionRelationships) {
						donatedConcept.getOutboundRelationships().add(extensionRelationship);
					}

					LOGGER.info("Processed donated concept with id '{}'", extensionConcept.getId());

				} else if (sourceComponent.get() instanceof Description && targetComponent.get() instanceof Description) {

					final Description extensionDescription = (Description) sourceComponent.get();
					final Description donatedDescription = (Description) targetComponent.get();

					EcoreUtil.remove(extensionDescription);

					final Set<String> intLanguageRefsetIds = donatedDescription.getLanguageRefSetMembers().stream()
							.map(SnomedRefSetMember::getRefSetIdentifierId).collect(toSet());

					// association refset members?
					// inactivation indicator refset members?

					donatedDescription.getLanguageRefSetMembers().addAll(extensionDescription.getLanguageRefSetMembers().stream()
							.filter(member -> !intLanguageRefsetIds.contains(member.getRefSetIdentifierId()))
							.collect(toList()));

					LOGGER.info("Processed donated description with id '{}'", extensionDescription.getId());

				} else if (sourceComponent.get() instanceof Relationship && targetComponent.get() instanceof Relationship) {

					final Relationship sourceRelationship = (Relationship) sourceComponent.get();

					EcoreUtil.remove(sourceRelationship);

					// concrete domain members?

					LOGGER.info("Processed donated relationship with id '{}'", sourceRelationship.getId());
				}

			}

		}

		for (final CDOID id : changedComponentCandidates) {

			final Optional<CDOObject> object = Optional.ofNullable(CDOUtils.getObjectIfExists(transaction, id));

			if (object.isPresent()) {

				if (object.get() instanceof Component) {

					final Component component = (Component) object.get();

					transaction.getLastSavepoint().getDirtyObjects().remove(id);
					transaction.getLastSavepoint().getRevisionDeltas().remove(id);

					LOGGER.info("Keeping latest ({}) version of component '{}' with id '{}'", EffectiveTimes.format(component.getEffectiveTime()), component.eClass().getName(), component.getId());
				}
			}
		}

		return emptySet();
	}

}
