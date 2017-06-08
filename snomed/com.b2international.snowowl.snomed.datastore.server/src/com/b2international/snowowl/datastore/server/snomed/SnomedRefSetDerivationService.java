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

import static com.b2international.commons.pcj.LongSets.toStringSet;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.server.CDOServerUtils.commit;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext.createConceptTypePair;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext.createDescriptionTypePair;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext.createRelationshipTypePair;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.partition;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Long.parseLong;
import static java.text.MessageFormat.format;
import static org.apache.lucene.search.BooleanQuery.getMaxClauseCount;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.cdo.util.CommitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.derivation.SnomedRefSetDerivationModel;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionContainerQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedRefSetDerivationService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.collect.FluentIterable;

import bak.pcj.LongCollection;
import bak.pcj.map.LongKeyMap;

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

		SnomedRefSetEditingContext editingContext = null;
		
		try {

			editingContext = SnomedRefSetEditingContext.createInstance(branchPath);
			final Collection<String> conceptIds = collectComponentIds(model.getRefSetId(), false, branchPath);
			final String refSetName = model.getRefSetName();
			
			switch (model.getSimpleTypeDerivation()) {
				case DESCRIPTION:
					deriveDescriptions(refSetName, conceptIds, editingContext, branchPath);
					break;
				case RELATIONSHIP:
					deriveRelationships(refSetName, conceptIds, editingContext, branchPath);
					break;
				case DUO:
					deriveDescriptions(refSetName, conceptIds, editingContext, branchPath);
					deriveRelationships(refSetName, conceptIds, editingContext, branchPath);
					break;
			}
			
			commit(editingContext.getTransaction(), model.getUserId(), model.getCommitMessage(), null);
		} catch (final SnowowlServiceException e) {
			LOGGER.error("Error while deriving simple type reference set.", e);
			return false;
		} catch (final CommitException e) {
			LOGGER.error("Error while committing simple type reference set derivation changes.", e);
			return false;
		} finally {
			if (null != editingContext) {
				editingContext.close();
			}
		}
		
		return true;
	}

	@Override
	public boolean deriveSimpleMapTypeRefSet(final IBranchPath branchPath, final SnomedRefSetDerivationModel model) {
		
		checkNotNull(branchPath, "Branch path argument must not be null.");
		checkNotNull(model, "Derivation model argument must not be null.");

		SnomedRefSetEditingContext editingContext = null;
		
		try {

			editingContext = SnomedRefSetEditingContext.createInstance(branchPath);
			final Collection<String> conceptIds = collectComponentIds(model.getRefSetId(), false, branchPath);
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
					deriveRelationships(refSetName, conceptIds, editingContext, branchPath);
					break;
				}
			
			commit(editingContext.getTransaction(), model.getUserId(), model.getCommitMessage(), null);
		} catch (final SnowowlServiceException e) {
			LOGGER.error("Error while deriving simple map type reference set.", e);
			return false;
		} catch (final CommitException e) {
			LOGGER.error("Error while committing simple map type reference set derivation changes.", e);
			return false;
		} finally {
			if (null != editingContext) {
				editingContext.close();
			}
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
					createConceptTypePair(conceptId), 
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
		final Iterable<List<String>> partitions = partition(componentIds, getMaxClauseCount());
		
		for (final List<String> partition : partitions) {
			
			final IIndexQueryAdapter<SnomedDescriptionIndexEntry> adapter = createQueryDescriptionAdapter(newArrayList(partition));
			
			for (final String descriptionId : searchForDescriptionIds(branchPath, adapter)) {
				members.add(context.createSimpleTypeRefSetMember(
						createDescriptionTypePair(descriptionId), 
						moduleId, 
						refSet));
			}
		}
		
		refSet.getMembers().addAll(members);
	}
	
	/*
	 * Creates a new simple type reference set where the referenced component type is relationship and the members are relationships between the
	 * derived reference set member's. 
	 */
	private void deriveRelationships(final String refSetName, final Collection<String> componentIds, final SnomedRefSetEditingContext context, final IBranchPath branchPath) throws SnowowlServiceException {

		final Set<SnomedRefSetMember> members = newHashSet();
		final String moduleId = getModuleId(context);

		final SnomedRegularRefSet refSet = context.createSnomedSimpleTypeRefSet(format("{0} - relationships", refSetName), RELATIONSHIP);
		final LongKeyMap activeStatements = getServiceForClass(SnomedStatementBrowser.class).getAllActiveStatements(branchPath);

		for (final String conceptId : componentIds) {

			final Object object = activeStatements.get(parseLong(conceptId));

			if (object instanceof List) {

				@SuppressWarnings("unchecked")
				final List<StatementFragment> fragments = (List<StatementFragment>) object;

				for (final StatementFragment fragment : fragments) {

					if (componentIds.contains(Long.toString(fragment.getDestinationId()))) {

						final String relationshipId = Long.toString(fragment.getStatementId());
						members.add(context.createSimpleTypeRefSetMember(
								createRelationshipTypePair(relationshipId), 
								moduleId, 
								refSet));
					}
				}
			}
		}

		refSet.getMembers().addAll(members);
	}
	
	/*
	 * Collects the component IDs from the reference set.
	 */
	private Collection<String> collectComponentIds(final String refSetId, final boolean mapTarget, final IBranchPath branchPath) {
		final LongKeyMap refSetToConceptMappings = getServiceForClass(SnomedRefSetBrowser.class).getReferencedConceptIds(branchPath);
		final Object concepts = refSetToConceptMappings.get(Long.parseLong(refSetId));
		return concepts instanceof LongCollection ? toStringSet((LongCollection) concepts) : Collections.<String>emptySet();
	}
	
	/*
	 * Collects all the active sub type IDs of the given components.
	 */
	private Collection<String> collectSubTypeConceptIds(final IBranchPath branchPath, final Collection<String> conceptIds) {
		
		final Collection<String> allDescendantConceptIds = newHashSet(conceptIds);
		for (final String conceptId : conceptIds) {
			final SnomedConcepts descendantConcepts = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByAncestor(conceptId)
				.build(branchPath.getPath())
				.execute(getServiceForClass(IEventBus.class))
				.getSync();
			
			FluentIterable.from(descendantConcepts)
				.transform(IComponent.ID_FUNCTION)
				.copyInto(allDescendantConceptIds);
		}
		
		return allDescendantConceptIds;
	}
	
	/*
	 * Creates a query for querying descriptions.
	 */
	private SnomedDescriptionContainerQueryAdapter createQueryDescriptionAdapter(final Collection<String> componentIds) {
		return SnomedDescriptionContainerQueryAdapter.createFindByConceptIds(componentIds);
	}
	
	/*
	 * Gets the descriptions IDs.
	 */
	private Collection<String> searchForDescriptionIds(final IBranchPath branchPath, final IIndexQueryAdapter<SnomedDescriptionIndexEntry> adapter) {
		return getIndexService().searchUnsortedIds(branchPath, adapter);
	}
	
	/*
	 * Gets the index service.
	 */
	private SnomedIndexService getIndexService() {
		return ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}

}