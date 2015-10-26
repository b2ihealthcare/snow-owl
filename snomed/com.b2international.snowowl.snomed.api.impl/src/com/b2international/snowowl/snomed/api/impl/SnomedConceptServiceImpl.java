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
package com.b2international.snowowl.snomed.api.impl;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.api.exception.ComponentStatusConflictException;
import com.b2international.snowowl.api.exception.IllegalQueryParameterException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponentList;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.domain.InternalComponentRef;
import com.b2international.snowowl.datastore.server.domain.InternalStorageRef;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.ISnomedConceptService;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedConceptList;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedConceptUpdate;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SearchKind;
import com.b2international.snowowl.snomed.core.domain.SnomedConceptCreateRequest;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptionCreateRequest;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptReducedQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedDOIQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.datastore.server.events.DefaultSnomedDescriptionCreateRequest;
import com.b2international.snowowl.snomed.datastore.server.events.SnomedConceptConverter;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class SnomedConceptServiceImpl 
	extends AbstractSnomedComponentServiceImpl<SnomedConceptCreateRequest, ISnomedConcept, ISnomedConceptUpdate, Concept>
	implements ISnomedConceptService {

	private SnomedDescriptionServiceImpl descriptionService;

	public SnomedConceptServiceImpl() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID, ComponentCategory.CONCEPT);
	}

	@Resource
	public void setDescriptionService(final SnomedDescriptionServiceImpl descriptionService) {
		this.descriptionService = descriptionService;
	}

	private static SnomedIndexService getIndexService() {
		return ApplicationContext.getServiceForClass(SnomedIndexService.class);
	}

	private static IEscgQueryEvaluatorService getQueryEvaluatorService() {
		return getServiceForClass(IEscgQueryEvaluatorService.class);
	}

	private static ISnomedComponentService getSnomedComponentService() {
		return ApplicationContext.getServiceForClass(ISnomedComponentService.class);
	}

	private SnomedConceptConverter getConceptConverter(final IBranchPath branchPath) {
		return new SnomedConceptConverter(getMembershipLookupService(branchPath));
	}

	@Override
	public boolean componentExists(final IComponentRef ref) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(ref, InternalComponentRef.class);
		return snomedConceptLookupService.exists(internalRef.getBranch().branchPath(), internalRef.getComponentId());
	}

	@Override
	protected Concept convertAndRegister(final SnomedConceptCreateRequest conceptInput, final SnomedEditingContext editingContext) {
		final Concept concept = convertConcept(conceptInput, editingContext);
		concept.getOutboundRelationships().add(convertParentIsARelationship(conceptInput, editingContext));
		editingContext.add(concept);

		final Set<String> requiredDescriptionTypes = newHashSet(Concepts.FULLY_SPECIFIED_NAME, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
		final Multiset<String> preferredLanguageRefSetIds = HashMultiset.create();
		final Set<String> synonymAndDescendantIds = getSnomedComponentService().getSynonymAndDescendantIds(BranchPathUtils.createPath(editingContext.getTransaction()));

		for (final SnomedDescriptionCreateRequest descriptionInput : conceptInput.getDescriptions()) {
			final DefaultSnomedDescriptionCreateRequest internalDescriptionInput = ClassUtils.checkAndCast(descriptionInput, DefaultSnomedDescriptionCreateRequest.class);

			internalDescriptionInput.setConceptId(concept.getId());

			if (null == internalDescriptionInput.getModuleId()) {
				internalDescriptionInput.setModuleId(conceptInput.getModuleId());
			}

			descriptionService.convertAndRegister(descriptionInput, editingContext);

			final String typeId = descriptionInput.getTypeId();

			if (synonymAndDescendantIds.contains(typeId)) {
				for (final Entry<String, Acceptability> acceptability : descriptionInput.getAcceptability().entrySet()) {
					if (Acceptability.PREFERRED.equals(acceptability.getValue())) {
						preferredLanguageRefSetIds.add(acceptability.getKey());
						requiredDescriptionTypes.remove(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
					}
				}
			}

			requiredDescriptionTypes.remove(typeId);
		}

		if (!requiredDescriptionTypes.isEmpty()) {
			throw new BadRequestException("At least one fully specified name and one preferred term must be supplied with the concept.");
		}

		for (final com.google.common.collect.Multiset.Entry<String> languageRefSetIdOccurence : preferredLanguageRefSetIds.entrySet()) {
			if (languageRefSetIdOccurence.getCount() > 1) {
				throw new BadRequestException("More than one preferred term has been added for language reference set %s.", languageRefSetIdOccurence.getElement());				
			}
		}

		return concept;
	}

	private Concept convertConcept(final SnomedConceptCreateRequest input, final SnomedEditingContext context) {
		try {
			return SnomedComponents.newConcept()
					.withId(input.getIdGenerationStrategy())
					.withModule(input.getModuleId())
					.build(context);
		} catch (final ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	private Relationship convertParentIsARelationship(final SnomedConceptCreateRequest input, final SnomedEditingContext context) {
		try {
			final Relationship parentIsARelationship = SnomedComponents.newRelationship()
					.withId(input.getIsAIdGenerationStrategy())
					.withModule(input.getModuleId())
					.withDestination(input.getParentId())
					.withType(IS_A)
					.build(context);
			return parentIsARelationship;
		} catch (final ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	@Override
	protected ISnomedConcept doRead(final IComponentRef ref) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(ref, InternalComponentRef.class);
		final IBranchPath branch = internalRef.getBranch().branchPath();
		final SnomedConceptIndexEntry conceptIndexEntry = snomedConceptLookupService.getComponent(branch, internalRef.getComponentId());
		return getConceptConverter(branch).apply(conceptIndexEntry); 
	}

	@Override
	protected void doUpdate(final IComponentRef ref, final ISnomedConceptUpdate update, final SnomedEditingContext editingContext) {
		final Concept concept = editingContext.getConcept(ref.getComponentId());

		boolean changed = false;
		changed |= updateModule(update.getModuleId(), concept, editingContext);
		changed |= updateDefinitionStatus(update.getDefinitionStatus(), concept, editingContext);
		changed |= updateSubclassDefinitionStatus(update.getSubclassDefinitionStatus(), concept, editingContext);
		changed |= processInactivation(update.isActive(), update.getInactivationIndicator(), concept, editingContext);

		updateAssociationTargets(update.getAssociationTargets(), concept, editingContext);

		if (changed) {
			concept.unsetEffectiveTime();
		}
	}

	private boolean updateDefinitionStatus(final DefinitionStatus newDefinitionStatus, final Concept concept, final SnomedEditingContext context) {

		if (null == newDefinitionStatus) {
			return false;
		}

		final String existingDefinitionStatusId = concept.getDefinitionStatus().getId();
		final String newDefinitionStatusId = newDefinitionStatus.getConceptId();
		if (!existingDefinitionStatusId.equals(newDefinitionStatusId)) {
			concept.setDefinitionStatus(context.getConcept(newDefinitionStatusId));
			return true;
		} else {
			return false;
		}
	}

	private boolean updateSubclassDefinitionStatus(final SubclassDefinitionStatus newSubclassDefinitionStatus, final Concept concept, 
			final SnomedEditingContext editingContext) {

		if (null == newSubclassDefinitionStatus) {
			return false;
		}

		final boolean currentExhaustive = concept.isExhaustive();
		final boolean newExhaustive = newSubclassDefinitionStatus.isExhaustive();
		if (currentExhaustive != newExhaustive) {
			concept.setExhaustive(newExhaustive);
			return true;
		} else {
			return false;
		}
	}

	private boolean processInactivation(final Boolean newActive, final InactivationIndicator newInactivationIndicator, 
			final Concept concept, 
			final SnomedEditingContext editingContext) {

		if (null != newInactivationIndicator) {
			
			if (null == newActive || !newActive) {
				inactivateConcept(concept, newInactivationIndicator, editingContext);
			} else {
				throw new BadRequestException("Bad");
			}
			
		} else {
			
			if (null == newActive) {
				return false;
			} else if (!newActive) {
				inactivateConcept(concept, InactivationIndicator.RETIRED, editingContext);
			} else {
				reactivateConcept(concept, editingContext);
			}
		}

		return true;
	}

	private void inactivateConcept(final Concept concept, final InactivationIndicator newInactivationIndicator, final SnomedEditingContext editingContext) {

		if (!concept.isActive()) {
			throw new ComponentStatusConflictException(concept.getId(), concept.isActive());
		}
		
		final SnomedInactivationPlan inactivationPlan = editingContext.inactivateConcept(new NullProgressMonitor(), concept.getId());
		inactivationPlan.performInactivation(newInactivationIndicator.toInactivationReason(), null);
	}

	private void reactivateConcept(final Concept concept, final SnomedEditingContext editingContext) {

		if (concept.isActive()) {
			throw new ComponentStatusConflictException(concept.getId(), concept.isActive());
		}
		
		concept.setActive(true);
		
		for (final SnomedAssociationRefSetMember associationMember : ImmutableList.copyOf(concept.getAssociationRefSetMembers())) {
			SnomedModelExtensions.removeOrDeactivate(associationMember);
		}
		
		removeOrDeactivateInactivationIndicators(concept);
		
		for (final Description description : concept.getDescriptions()) {
			removeOrDeactivateInactivationIndicators(description);
		}
		
		reactivateRelationships(concept.getOutboundRelationships());
		reactivateRelationships(concept.getInboundRelationships());
	}

	private void reactivateRelationships(EList<Relationship> relationships) {
		for (final Relationship relationship : relationships) {
			if (!relationship.isActive()) {
				relationship.setActive(true);
				relationship.unsetEffectiveTime();
			}
		}
	}

	private void removeOrDeactivateInactivationIndicators(Inactivatable component) {
		for (final SnomedAttributeValueRefSetMember attributeValueMember : ImmutableList.copyOf(component.getInactivationIndicatorRefSetMembers())) {
			SnomedModelExtensions.removeOrDeactivate(attributeValueMember);
		}
	}

	@Override
	protected void doDelete(final IComponentRef ref, final SnomedEditingContext editingContext) {
		final Concept concept = editingContext.getConcept(ref.getComponentId());
		editingContext.delete(concept);
	}

	@Override
	public IComponentList<ISnomedConcept> getAllConcepts(final String branchPath, final int offset, final int limit) {
		final InternalStorageRef internalRef = createStorageRef(branchPath);
		final IBranchPath branch = internalRef.getBranch().branchPath();
		final SnomedConceptReducedQueryAdapter queryAdapter = new SnomedConceptReducedQueryAdapter("", SnomedConceptReducedQueryAdapter.SEARCH_DEFAULT);

		return search(offset, limit, queryAdapter, branch);
	}

	@Override
	public IComponentList<ISnomedConcept> search(final String branchPath, final Map<SearchKind, String> queryParams, final int offset, final int limit) {
		final InternalStorageRef internalRef = createStorageRef(branchPath);
		final IBranchPath branch = internalRef.getBranch().branchPath();
		final Query restrictionQuery;

		if (queryParams.containsKey(SearchKind.ESCG)) {

			try {
				restrictionQuery = getQueryEvaluatorService().evaluateBooleanQuery(branch, queryParams.get(SearchKind.ESCG));
			} catch (final SyntaxErrorException e) {
				throw new IllegalQueryParameterException(e.getMessage());
			}

		} else {
			restrictionQuery = new MatchAllDocsQuery();
		}

		final String label = Strings.nullToEmpty(queryParams.get(SearchKind.LABEL));
		final SnomedDOIQueryAdapter queryAdapter = new SnomedDOIQueryAdapter(label, "", restrictionQuery);
		return search(offset, limit, queryAdapter, branch);
	}

	private IComponentList<ISnomedConcept> search(final int offset, final int limit, final SnomedConceptIndexQueryAdapter queryAdapter, final IBranchPath branchPath) {
		final int totalComponents = getIndexService().getHitCount(branchPath, queryAdapter);
		final List<SnomedConceptIndexEntry> conceptIndexEntries = getIndexService().search(branchPath, queryAdapter, offset, limit);
		final List<ISnomedConcept> concepts = Lists.transform(conceptIndexEntries, getConceptConverter(branchPath));

		final SnomedConceptList result = new SnomedConceptList();
		result.setTotalMembers(totalComponents);
		result.setMembers(ImmutableList.copyOf(concepts));

		return result;
	}
}
