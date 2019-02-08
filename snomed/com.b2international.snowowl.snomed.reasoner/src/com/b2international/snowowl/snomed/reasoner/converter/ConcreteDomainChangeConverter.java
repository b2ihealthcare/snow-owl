/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.converter.BaseResourceConverter;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.ConcreteDomainChange;
import com.b2international.snowowl.snomed.reasoner.domain.ConcreteDomainChanges;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerConcreteDomainMember;
import com.b2international.snowowl.snomed.reasoner.index.ConcreteDomainChangeDocument;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 6.11 (originally introduced on 7.0)
 */
public final class ConcreteDomainChangeConverter 
		extends BaseResourceConverter<ConcreteDomainChangeDocument, ConcreteDomainChange, ConcreteDomainChanges> {

	// TODO: this constant is moved to SnomedReferenceSetMember.Expand on 7.x 
	private static final String REFERENCED_COMPONENT = "referencedComponent";

	public ConcreteDomainChangeConverter(final RepositoryContext context, final Options expand, final List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected ConcreteDomainChanges createCollectionResource(final List<ConcreteDomainChange> results, 
			final String scrollId, 
			final String searchAfter, 
			final int limit, 
			final int total) {

		return new ConcreteDomainChanges(results, scrollId, searchAfter, limit, total);
	}

	@Override
	protected ConcreteDomainChange toResource(final ConcreteDomainChangeDocument entry) {
		final ConcreteDomainChange resource = new ConcreteDomainChange();
		resource.setClassificationId(entry.getClassificationId());
		resource.setChangeNature(entry.getNature());

		/*
		 * - Inferred members: ID refers to the "origin" member ID (it will point to a stated or "grouped additional" CD member) 
		 * - Redundant members: ID refers to the member that should be removed or deactivated
		 */
		final ReasonerConcreteDomainMember concreteDomainMember = new ReasonerConcreteDomainMember(entry.getMemberId());

		if (ChangeNature.INFERRED.equals(entry.getNature())) {
			/*
			 * Inferences carry information about the group which can differ from the values
			 * on the "origin" member, so make note of it here. Characteristic type is
			 * always "inferred".
			 */
			concreteDomainMember.setReferencedComponentId(entry.getReferencedComponentId());
			concreteDomainMember.setGroup(entry.getGroup());
		} else {
			// Redundant CD members only need the ID and released flag populated to do the delete/inactivation
			concreteDomainMember.setReleased(entry.isReleased());
		}
		
		resource.setConcreteDomainMember(concreteDomainMember);
		return resource;
	}

	@Override
	protected void expand(final List<ConcreteDomainChange> results) {
		if (!expand().containsKey(ConcreteDomainChange.Expand.CONCRETE_DOMAIN_MEMBER)) {
			return;
		}

		/*
		 * Depending on the CD member change search request, we might need to issue
		 * SNOMED CT searches against multiple branches; find out which ones we have.
		 */
		final Multimap<String, ConcreteDomainChange> itemsByBranch = getItemsByBranch(results);
		
		// Check if we only need to load inferred CD members in their entirety
		final Options expandOptions = expand().getOptions(ConcreteDomainChange.Expand.CONCRETE_DOMAIN_MEMBER);
		final boolean inferredOnly = expandOptions.getBoolean("inferredOnly");

		final Options cdMemberExpandOptions = expandOptions.getOptions("expand");
		
		final Options referencedComponentOptions = cdMemberExpandOptions.getOptions(REFERENCED_COMPONENT);
		final boolean needsReferencedComponent = cdMemberExpandOptions.keySet().contains(REFERENCED_COMPONENT);

		for (final String branch : itemsByBranch.keySet()) {
			final Collection<ConcreteDomainChange> itemsForCurrentBranch = itemsByBranch.get(branch);

			/*
			 * Expand referenced component on the member currently set on each item first,
			 * as it might have changed when compared to the "origin" member.
			 */
			if (needsReferencedComponent) {
				final List<ReasonerConcreteDomainMember> blankMembers = itemsForCurrentBranch.stream()
						.filter(c -> !inferredOnly || ChangeNature.INFERRED.equals(c.getChangeNature()))
						.map(ConcreteDomainChange::getConcreteDomainMember)
						.collect(Collectors.toList());

				final Multimap<String, ReasonerConcreteDomainMember> membersByReferencedComponent = Multimaps.index(blankMembers, m -> m.getReferencedComponent().getId());
				final Multimap<ComponentCategory, String> referencedComponentsByCategory = Multimaps.index(membersByReferencedComponent.keySet(), SnomedIdentifiers::getComponentCategory);

				for (final Entry<ComponentCategory, Collection<String>> categoryEntry : referencedComponentsByCategory.asMap().entrySet()) {
					expandComponentCategory(branch, 
							categoryEntry.getKey(),
							categoryEntry.getValue(),
							referencedComponentOptions, 
							membersByReferencedComponent);
				}
			}
			
			/*
			 * Then fetch all the required members (these will have a referenced component
			 * ID that should no longer be copied on inferred members). Note that the same "origin"
			 * member might be used for multiple inferred counterparts.
			 */
			final Set<String> memberIds = itemsForCurrentBranch.stream()
					.filter(c -> !inferredOnly || ChangeNature.INFERRED.equals(c.getChangeNature()))
					.map(c -> c.getConcreteDomainMember().getOriginMemberId())
					.collect(Collectors.toSet());

			final Request<BranchContext, SnomedReferenceSetMembers> memberSearchRequest = SnomedRequests.prepareSearchMember()
					.filterByIds(memberIds)
					.setLimit(memberIds.size())
					.setExpand(cdMemberExpandOptions)
					.setLocales(locales())
					.build();

			final SnomedReferenceSetMembers concreteDomainMembers = new BranchRequest<>(branch, 
				new RevisionIndexReadRequest<>(memberSearchRequest))
				.execute(context());
			
			final Map<String, SnomedReferenceSetMember> membersByUuid = Maps.uniqueIndex(concreteDomainMembers, SnomedReferenceSetMember::getId);

			/*
			 * Finally, set the member on the change item, but preserve the "adjusted" properties:
			 * 
			 * - inferred referenced component ID
			 * - inferred relationship group
			 */
			for (final ConcreteDomainChange item : itemsForCurrentBranch) {
				final ReasonerConcreteDomainMember reasonerMember = item.getConcreteDomainMember();
				final String memberUuid = reasonerMember.getOriginMemberId();
				
				if (ChangeNature.INFERRED.equals(item.getChangeNature())) {
					final SnomedReferenceSetMember expandedMember = membersByUuid.get(memberUuid);
					final Map<String, Object> expandedProperties = expandedMember.getProperties();
					
					reasonerMember.setCharacteristicTypeId(Concepts.INFERRED_RELATIONSHIP);
					// reasonerMember.setGroup(...) is already set
					// reasonerMember.setReferencedComponent(...) is already set
					reasonerMember.setReferenceSetId(expandedMember.getReferenceSetId());
					reasonerMember.setReleased(false);
					reasonerMember.setSerializedValue((String) expandedProperties.get(SnomedRf2Headers.FIELD_VALUE));
					reasonerMember.setTypeId((String) expandedProperties.get(SnomedRf2Headers.FIELD_TYPE_ID));
					
				} else if (!inferredOnly) {
					final SnomedReferenceSetMember expandedMember = membersByUuid.get(memberUuid);
					final Map<String, Object> expandedProperties = expandedMember.getProperties();

					reasonerMember.setCharacteristicTypeId((String) expandedProperties.get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID));
					reasonerMember.setGroup((Integer) expandedProperties.get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP));
					reasonerMember.setReferencedComponent(expandedMember.getReferencedComponent());
					reasonerMember.setReferenceSetId(expandedMember.getReferenceSetId());
					// reasonerMember.setReleased(...) is already set
					reasonerMember.setSerializedValue((String) expandedProperties.get(SnomedRf2Headers.FIELD_VALUE));
					reasonerMember.setTypeId((String) expandedProperties.get(SnomedRf2Headers.FIELD_TYPE_ID)); 
				}
			}
		}
	}

	private Multimap<String, ConcreteDomainChange> getItemsByBranch(final List<ConcreteDomainChange> results) {
		final Set<String> classificationTaskIds = results.stream()
				.map(ConcreteDomainChange::getClassificationId)
				.collect(Collectors.toSet());

		final Map<String, String> branchesByClassificationIdMap = ClassificationRequests.prepareSearchClassification()
				.filterByIds(classificationTaskIds)
				.all()
				.build()
				.execute(context())
				.stream()
				.collect(Collectors.toMap(ClassificationTask::getId, ClassificationTask::getBranch));

		final Multimap<String, ConcreteDomainChange> itemsByBranch = Multimaps.index(results, r -> branchesByClassificationIdMap.get(r.getClassificationId()));
		return itemsByBranch;
	}

	// Copied from SnomedReferenceSetMemberConverter
	private void expandComponentCategory(final String branch,
			final ComponentCategory category,
			final Collection<String> componentIds,
			final Options componentOptions, 
			final Multimap<String, ReasonerConcreteDomainMember> membersByReferencedComponent) {

		final SearchResourceRequestBuilder<?, BranchContext, ? extends CollectionResource<? extends SnomedCoreComponent>> search = 
				createSearchRequestBuilder(category);

		search
			.filterByIds(componentIds)
			.setLimit(componentIds.size())
			.setLocales(locales())
			.setExpand(componentOptions.get("expand", Options.class));

		final CollectionResource<? extends SnomedCoreComponent> components = new BranchRequest<>(branch, 
			new RevisionIndexReadRequest<>(search.build()))
			.execute(context());

		for (final SnomedCoreComponent component : components) {
			for (final ReasonerConcreteDomainMember member : membersByReferencedComponent.get(component.getId())) {
				member.setReferencedComponent(component);
			}
		}
	}

	private SearchResourceRequestBuilder<?, BranchContext, ? extends CollectionResource<? extends SnomedCoreComponent>> createSearchRequestBuilder(final ComponentCategory category) {
		switch (category) {
		case CONCEPT:
			return SnomedRequests.prepareSearchConcept();
		case DESCRIPTION:
			return SnomedRequests.prepareSearchDescription();
		case RELATIONSHIP:
			return SnomedRequests.prepareSearchRelationship();
		default: 
			throw new UnsupportedOperationException("Category is not supported in referenced component expansion");
		}
	}
}
