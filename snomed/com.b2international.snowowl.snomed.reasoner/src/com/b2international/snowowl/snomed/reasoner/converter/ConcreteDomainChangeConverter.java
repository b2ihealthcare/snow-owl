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
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
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
 * @since 7.0
 */
public final class ConcreteDomainChangeConverter 
		extends BaseResourceConverter<ConcreteDomainChangeDocument, ConcreteDomainChange, ConcreteDomainChanges> {

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
		 * New members: ID refers to the "origin" member ID 
		 * Updated members: ID refers to the member that should be updated in place
		 * Redundant members: ID refers to the member that should be removed or deactivated
		 */
		final ReasonerConcreteDomainMember concreteDomainMember = new ReasonerConcreteDomainMember(entry.getMemberId());
		
		// Released flag is the "origin" member's released state for updated and redundant members, false for new members
		concreteDomainMember.setReleased(entry.isReleased());
		concreteDomainMember.setReferencedComponentId(entry.getReferencedComponentId());

		switch (entry.getNature()) {
			case NEW:
				/*
				 * New members are referring to: 
				 * - a different component 
				 * - in another relationship group
				 * - with a potentially different characteristic type 
				 */
				concreteDomainMember.setGroup(entry.getGroup());
				concreteDomainMember.setCharacteristicTypeId(entry.getCharacteristicTypeId());
				break;
				
			case UPDATED:
				// Updates change the serialized value on an existing member
				concreteDomainMember.setSerializedValue(entry.getSerializedValue());
				break;
				
			case REDUNDANT:
				// Redundant CD members only need the UUID and released flag populated to do the delete/inactivation
				break;
				
			default:
				throw new IllegalStateException(String.format("Unexpected CD member change '%s' found with UUID '%s'.", 
						entry.getNature(), 
						entry.getMemberId()));
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
		final Options referencedComponentOptions = cdMemberExpandOptions.getOptions(SnomedReferenceSetMember.Expand.REFERENCED_COMPONENT);
		
		/*
		 * Remove this option from the member expand options map, so that member search
		 * does not expand the referenced component again
		 */
		final boolean needsReferencedComponent = cdMemberExpandOptions.keySet().remove(SnomedReferenceSetMember.Expand.REFERENCED_COMPONENT);

		for (final String branch : itemsByBranch.keySet()) {
			final Collection<ConcreteDomainChange> itemsForCurrentBranch = itemsByBranch.get(branch);

			/*
			 * Expand referenced component on members via a separate search request, as they
			 * can be different from the referenced component on the "origin" member
			 */
			if (needsReferencedComponent) {
				final List<ReasonerConcreteDomainMember> blankMembers = itemsForCurrentBranch.stream()
						.filter(c -> !inferredOnly || ChangeNature.NEW.equals(c.getChangeNature()))
						.map(ConcreteDomainChange::getConcreteDomainMember)
						.collect(Collectors.toList());

				final Multimap<String, ReasonerConcreteDomainMember> membersByReferencedComponent = Multimaps.index(blankMembers, 
						ReasonerConcreteDomainMember::getReferencedComponentId);
				
				final Multimap<ComponentCategory, String> referencedComponentsByCategory = Multimaps.index(membersByReferencedComponent.keySet(), 
						SnomedIdentifiers::getComponentCategory);

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
			 * member might be used for multiple eg. "new" counterparts.
			 */
			final Set<String> cdMemberUuids = itemsForCurrentBranch.stream()
					.filter(c -> !inferredOnly || ChangeNature.NEW.equals(c.getChangeNature()))
					.map(c -> c.getConcreteDomainMember().getOriginMemberId())
					.collect(Collectors.toSet());

			final Request<BranchContext, SnomedReferenceSetMembers> cdMemberSearchRequest = SnomedRequests.prepareSearchMember()
					.filterByIds(cdMemberUuids)
					.setLimit(cdMemberUuids.size())
					.setExpand(cdMemberExpandOptions)
					.setLocales(locales())
					.build();

			final SnomedReferenceSetMembers cdMembers = new BranchRequest<>(branch, 
				new RevisionIndexReadRequest<>(cdMemberSearchRequest))
					.execute(context());
			
			final Map<String, SnomedReferenceSetMember> cdMembersByUuid = Maps.uniqueIndex(cdMembers, SnomedReferenceSetMember::getId);

			/*
			 * Finally, set the member on the change item, but preserve the properties that
			 * were already set in "toResource"
			 */
			for (final ConcreteDomainChange item : itemsForCurrentBranch) {
				final ReasonerConcreteDomainMember reasonerMember = item.getConcreteDomainMember();
				final String memberUuid = reasonerMember.getOriginMemberId();

				switch (item.getChangeNature()) {
					case NEW: {
							final SnomedReferenceSetMember expandedMember = cdMembersByUuid.get(memberUuid);
							final Map<String, Object> expandedProperties = expandedMember.getProperties();

							// reasonerMember.setCharacteristicTypeId(...) is already set
							// reasonerMember.setGroup(...) is already set
							// reasonerMember.setReferencedComponent(...) is already set (or expanded)
							reasonerMember.setReferenceSetId(expandedMember.getReferenceSetId());
							// reasonerMember.setReleased(...) is already set
							reasonerMember.setSerializedValue((String) expandedProperties.get(SnomedRf2Headers.FIELD_VALUE));
							reasonerMember.setTypeId((String) expandedProperties.get(SnomedRf2Headers.FIELD_TYPE_ID));
						}
						break;
						
					case UPDATED:
						if (!inferredOnly) {
							final SnomedReferenceSetMember expandedMember = cdMembersByUuid.get(memberUuid);
							final Map<String, Object> expandedProperties = expandedMember.getProperties();
							
							reasonerMember.setCharacteristicTypeId((String) expandedProperties.get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID));
							reasonerMember.setGroup((Integer) expandedProperties.get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP));
							// reasonerMember.setReferencedComponent(...) is already set (or expanded)
							reasonerMember.setReferenceSetId(expandedMember.getReferenceSetId());
							// reasonerMember.setReleased(...) is already set
							// reasonerMember.setSerializedValue(...) is already set
							reasonerMember.setTypeId((String) expandedProperties.get(SnomedRf2Headers.FIELD_TYPE_ID)); 
						}
						break;
						
					case REDUNDANT:
						if (!inferredOnly) {
							final SnomedReferenceSetMember expandedMember = cdMembersByUuid.get(memberUuid);
							final Map<String, Object> expandedProperties = expandedMember.getProperties();
							
							reasonerMember.setCharacteristicTypeId((String) expandedProperties.get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID));
							reasonerMember.setGroup((Integer) expandedProperties.get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP));
							// reasonerMember.setReferencedComponent(...) is already set (or expanded)
							reasonerMember.setReferenceSetId(expandedMember.getReferenceSetId());
							// reasonerMember.setReleased(...) is already set
							reasonerMember.setSerializedValue((String) expandedProperties.get(SnomedRf2Headers.FIELD_VALUE));
							reasonerMember.setTypeId((String) expandedProperties.get(SnomedRf2Headers.FIELD_TYPE_ID)); 
						}
						break;
						
					default:
						throw new IllegalStateException(String.format("Unexpected CD member change '%s' found with UUID '%s'.", 
								item.getChangeNature(), 
								item.getConcreteDomainMember().getOriginMemberId()));
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
				.setLimit(classificationTaskIds.size())
				.build()
				.execute(context())
				.stream()
				.collect(Collectors.toMap(
						ClassificationTask::getId, 
						ClassificationTask::getBranch));

		final Multimap<String, ConcreteDomainChange> itemsByBranch = Multimaps.index(results, 
				r -> branchesByClassificationIdMap.get(r.getClassificationId()));
		
		return itemsByBranch;
	}

	// Copied from SnomedReferenceSetMemberConverter
	private void expandComponentCategory(final String branch,
			final ComponentCategory category,
			final Collection<String> componentIds,
			final Options componentOptions, 
			final Multimap<String, ReasonerConcreteDomainMember> membersByReferencedComponent) {

		final SearchResourceRequestBuilder<?, BranchContext, ? extends CollectionResource<? extends SnomedCoreComponent>> searchRequestBuilder = 
				createSearchRequestBuilder(category);

		searchRequestBuilder.filterByIds(componentIds)
			.setLimit(componentIds.size())
			.setLocales(locales())
			.setExpand(componentOptions.get("expand", Options.class));

		final CollectionResource<? extends SnomedCoreComponent> referencedComponents = new BranchRequest<>(branch, 
			new RevisionIndexReadRequest<>(searchRequestBuilder.build()))
				.execute(context());

		for (final SnomedCoreComponent referencedComponent : referencedComponents) {
			for (final ReasonerConcreteDomainMember member : membersByReferencedComponent.get(referencedComponent.getId())) {
				member.setReferencedComponent(referencedComponent);
			}
		}
	}

	private SearchResourceRequestBuilder<?, BranchContext, ? extends CollectionResource<? extends SnomedCoreComponent>> createSearchRequestBuilder(final ComponentCategory category) {
		switch (category) {
			case CONCEPT: return SnomedRequests.prepareSearchConcept();
			case DESCRIPTION: return SnomedRequests.prepareSearchDescription();
			case RELATIONSHIP: return SnomedRequests.prepareSearchRelationship();
			default: throw new UnsupportedOperationException("Category '" + category + "' is not supported in referenced component expansion.");
		}
	}
}
