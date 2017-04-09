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
package com.b2international.snowowl.datastore.server.snomed;

import static com.b2international.snowowl.datastore.server.CDOServerUtils.commit;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.text.MessageFormat.format;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.cdo.util.CommitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.derivation.SnomedRefSetDerivationModel;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedRefSetDerivationService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * Service class for SNOMED&nbsp;CT simple type and simple map type reference set derivation.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public class SnomedRefSetDerivationService implements ISnomedRefSetDerivationService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedRefSetDerivationService.class);
	
	@Override
	public boolean deriveSimpleTypeRefSet(final IBranchPath branchPath, final SnomedRefSetDerivationModel model) {
		
		checkNotNull(branchPath, "Branch path argument must not be null.");
		checkNotNull(model, "Derivation model argument must not be null.");

		try (SnomedRefSetEditingContext editingContext = SnomedRefSetEditingContext.createInstance(branchPath)) {
			final Collection<String> conceptIds = collectComponentIds(branchPath, model.getRefSetId());
			final String refSetName = model.getRefSetName();
			
			switch (model.getSimpleTypeDerivation()) {
				case DESCRIPTION:
					deriveDescriptions(refSetName, conceptIds, editingContext, branchPath);
					break;
				case RELATIONSHIP:
					deriveRelationships(refSetName, conceptIds, editingContext);
					break;
				case DUO:
					deriveDescriptions(refSetName, conceptIds, editingContext, branchPath);
					deriveRelationships(refSetName, conceptIds, editingContext);
					break;
			}
			
			commit(editingContext.getTransaction(), model.getUserId(), model.getCommitMessage(), null);
		} catch (final SnowowlServiceException e) {
			LOGGER.error("Error while deriving simple type reference set.", e);
			return false;
		} catch (final CommitException e) {
			LOGGER.error("Error while committing simple type reference set derivation changes.", e);
			return false;
		}
		
		return true;
	}

	@Override
	public boolean deriveSimpleMapTypeRefSet(final IBranchPath branchPath, final SnomedRefSetDerivationModel model) {
		
		checkNotNull(branchPath, "Branch path argument must not be null.");
		checkNotNull(model, "Derivation model argument must not be null.");

		try (SnomedRefSetEditingContext editingContext = SnomedRefSetEditingContext.createInstance(branchPath)) {

			final Collection<String> conceptIds = collectComponentIds(branchPath, model.getRefSetId());
			final String refSetName = model.getRefSetName();
			
			switch (model.getSimpleMapTypeDerivation()) {
				case CONCEPT:
					deriveConcepts(refSetName, conceptIds, editingContext);
					break;
				case CONCEPT_WITH_SUBTYPES:
					deriveConcepts(refSetName, collectSubTypeConceptIds(branchPath, conceptIds), editingContext);
					break;
				case TRIO:
					deriveConcepts(refSetName, conceptIds, editingContext);
					deriveDescriptions(refSetName, conceptIds, editingContext, branchPath);
					deriveRelationships(refSetName, conceptIds, editingContext);
					break;
				}
			
			commit(editingContext.getTransaction(), model.getUserId(), model.getCommitMessage(), null);
		} catch (final SnowowlServiceException e) {
			LOGGER.error("Error while deriving simple map type reference set.", e);
			return false;
		} catch (final CommitException e) {
			LOGGER.error("Error while committing simple map type reference set derivation changes.", e);
			return false;
		}
		
		return true;
		
	}
	
	/*
	 * Creates a new reference set where the referenced component type is concept and the members are the derived simple map type reference set's 
	 * referenced components or map target.
	 */
	private void deriveConcepts(final String refSetName, final Collection<String> concpetIds, final SnomedRefSetEditingContext context) {
		
		final Collection<SnomedRefSetMember> members = newHashSet();
		final String moduleId = getModuleId(context);
		final SnomedRegularRefSet refSet = context.createSnomedSimpleTypeRefSet(refSetName, CONCEPT);
		
		for (final String conceptId : concpetIds) {
			members.add(context.createSimpleTypeRefSetMember(
					conceptId, 
					moduleId, 
					refSet));
		}
		
		refSet.getMembers().addAll(members);
	}

	private String getModuleId(final SnomedRefSetEditingContext context) {
		return context.getSnomedEditingContext().getDefaultModuleConcept().getId();
	}
	
	/*
	 * Creates a new simple type reference set where the referenced component type is description and the members are the derived 
	 * reference set member's descriptions.
	 */
	private void deriveDescriptions(final String refSetName, final Collection<String> componentIds, final SnomedRefSetEditingContext context, final IBranchPath branchPath) throws SnowowlServiceException {
		
		final Collection<SnomedRefSetMember> members = newHashSet();
		final String moduleId = getModuleId(context);
		
		final SnomedRegularRefSet refSet = context.createSnomedSimpleTypeRefSet(format("{0} - descriptions", refSetName), DESCRIPTION);
		
		final SnomedDescriptions descriptions = SnomedRequests.prepareSearchDescription()
			.all()
			.filterByActive(true)
			.filterByConceptId(componentIds)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, context.getBranch())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.getSync();
		
		for (final SnomedDescription description : descriptions) {
			members.add(context.createSimpleTypeRefSetMember(
					description.getId(), 
					moduleId, 
					refSet));
		}
		
		refSet.getMembers().addAll(members);
	}
	
	/*
	 * Creates a new simple type reference set where the referenced component type is relationship and the members are relationships between the
	 * derived reference set member's. 
	 */
	private void deriveRelationships(final String refSetName, final Collection<String> componentIds, final SnomedRefSetEditingContext context) throws SnowowlServiceException {

		final Set<SnomedRefSetMember> members = newHashSet();
		final String moduleId = getModuleId(context);

		final SnomedRegularRefSet refSet = context.createSnomedSimpleTypeRefSet(format("{0} - relationships", refSetName), RELATIONSHIP);
		
		for (SnomedRelationship relationship : getEdgesBetween(context.getBranch(), componentIds)) {
			members.add(context.createSimpleTypeRefSetMember(
					relationship.getId(), 
					moduleId, 
					refSet));
		}
		
		refSet.getMembers().addAll(members);
	}
	
	private SnomedRelationships getEdgesBetween(String branch, Collection<String> componentIds) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true)
				.filterBySource(componentIds)
				.filterByDestination(componentIds)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
	}

	/*
	 * Collects the component IDs from the reference set.
	 */
	private Collection<String> collectComponentIds(final IBranchPath branchPath, final String refSetId) {
		return SnomedRequests.prepareGetReferenceSet(refSetId)
			.setExpand("members(limit:"+Integer.MAX_VALUE+",active:true)")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.then(new Function<SnomedReferenceSet, Collection<String>>() {
				@Override
				public Collection<String> apply(SnomedReferenceSet input) {
					return FluentIterable.from(input.getMembers()).transform(new Function<SnomedReferenceSetMember, String>() {
						@Override
						public String apply(SnomedReferenceSetMember input) {
							return input.getReferencedComponent().getId();
						}
					}).toSet();
				}
			}).getSync();
	}
	
	/*
	 * Collects all the active sub type IDs of the given components.
	 */
	private Collection<String> collectSubTypeConceptIds(final IBranchPath branchPath, final Collection<String> conceptIds) {
		return SnomedRequests.prepareSearchConcept()
				.all()
				.filterByAncestors(conceptIds)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedConcepts, Set<String>>() {
					@Override
					public Set<String> apply(SnomedConcepts input) {
						final Set<String> subTypeIds = newHashSet(conceptIds);
						return FluentIterable.from(input).transform(IComponent.ID_FUNCTION).copyInto(subTypeIds);
					}
				})
				.getSync();
	}
	
}