/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.ConcreteDomainChange;
import com.b2international.snowowl.snomed.reasoner.domain.ConcreteDomainChanges;
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
		resource.setChangeNature(entry.getNature());
		resource.setClassificationId(entry.getClassificationId());

		final SnomedReferenceSetMember concreteDomainMember = new SnomedReferenceSetMember();
		final SnomedCoreComponent referencedComponent = createReferencedComponent(entry.getReferencedComponentId());
		concreteDomainMember.setId(entry.getMemberId());
		concreteDomainMember.setReferencedComponent(referencedComponent);

		resource.setConcreteDomainMember(concreteDomainMember);
		return resource;
	}

	private SnomedCoreComponent createReferencedComponent(final String referencedComponentId) {
		final ComponentCategory referencedComponentCategory = SnomedIdentifiers.getComponentCategory(referencedComponentId);

		switch (referencedComponentCategory) {
		case CONCEPT:
			return new SnomedConcept(referencedComponentId);
		case DESCRIPTION:
			return new SnomedDescription(referencedComponentId);
		case RELATIONSHIP:
			return new SnomedRelationship(referencedComponentId);
		default:
			throw new IllegalStateException(String.format("Unexpected referenced component category '%s' for SCTID '%s'.", referencedComponentCategory, referencedComponentId));
		}
	}

	@Override
	protected void expand(final List<ConcreteDomainChange> results) {
		if (!expand().containsKey(ConcreteDomainChange.Expand.CONCRETE_DOMAIN_MEMBER)) {
			return;
		}

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

		final Options expandOptions = expand().get(ConcreteDomainChange.Expand.CONCRETE_DOMAIN_MEMBER, Options.class);
		final Options referencedComponentOptions = expandOptions.getOptions(REFERENCED_COMPONENT);
		final boolean needsReferencedComponent = expandOptions.keySet().remove(REFERENCED_COMPONENT);

		for (final String branch : itemsByBranch.keySet()) {
			final Collection<ConcreteDomainChange> itemsForCurrentBranch = itemsByBranch.get(branch);

			// Expand referenced component on the initial, "blank" reference set member of each item
			if (needsReferencedComponent) {
				final List<SnomedReferenceSetMember> blankMembers = itemsForCurrentBranch.stream()
						.map(ConcreteDomainChange::getConcreteDomainMember)
						.collect(Collectors.toList());

				final Multimap<String, SnomedReferenceSetMember> membersByReferencedComponent = Multimaps.index(blankMembers, m -> m.getReferencedComponent().getId());
				final Multimap<ComponentCategory, String> referencedComponentsByCategory = Multimaps.index(membersByReferencedComponent.keySet(), SnomedIdentifiers::getComponentCategory);

				for (final ComponentCategory category : referencedComponentsByCategory.keySet()) {
					expandComponentCategory(branch, 
							referencedComponentOptions, 
							membersByReferencedComponent, 
							referencedComponentsByCategory, 
							category);
				}
			}
			
			// Then fetch all the members (these will have a referenced component ID that is no longer valid for inferred members)
			final Set<String> memberIds = itemsForCurrentBranch.stream()
					.map(c -> c.getConcreteDomainMember().getId())
					.collect(Collectors.toSet());

			final Request<BranchContext, SnomedReferenceSetMembers> memberSearchRequest = SnomedRequests.prepareSearchMember()
					.filterByIds(memberIds)
					.all()
					.setExpand(expandOptions.get("expand", Options.class))
					.setLocales(locales())
					.build();

			final SnomedReferenceSetMembers concreteDomainMembers = new BranchRequest<>(branch, memberSearchRequest).execute(context());
			final Map<String, SnomedReferenceSetMember> membersByUuid = Maps.uniqueIndex(concreteDomainMembers, SnomedReferenceSetMember::getId);

			// Finally, set the member on the change item, but preserve the "adjusted" version that holds the inferred component target's ID
			for (final ConcreteDomainChange item : itemsForCurrentBranch) {
				final String memberUuid = item.getConcreteDomainMember().getId();
				final SnomedCoreComponent adjustedReferencedComponent = item.getConcreteDomainMember().getReferencedComponent();
				final SnomedReferenceSetMember expandedMember = membersByUuid.get(memberUuid);

				expandedMember.setReferencedComponent(adjustedReferencedComponent);
				item.setConcreteDomainMember(expandedMember);
			}
		}
	}

	// Copied from SnomedReferenceSetMemberConverter
	private void expandComponentCategory(final String branch,
			final Options expandOptions,
			final Multimap<String, SnomedReferenceSetMember> referencedComponentIdToMemberMap,
			final Multimap<ComponentCategory, String> componentCategoryToIdMap, 
			final ComponentCategory category) {

		final Collection<String> componentIds = componentCategoryToIdMap.get(category);
		final SearchResourceRequestBuilder<?, BranchContext, ? extends CollectionResource<? extends SnomedCoreComponent>> search = 
				createSearchRequestBuilder(category);

		search
			.filterByIds(componentIds)
			.setLimit(componentIds.size())
			.setLocales(locales())
			.setExpand(expandOptions.get("expand", Options.class));

		final CollectionResource<? extends SnomedCoreComponent> components = new BranchRequest<>(branch, search.build()).execute(context());

		for (final SnomedCoreComponent component : components) {
			for (final SnomedReferenceSetMember member : referencedComponentIdToMemberMap.get(component.getId())) {
				((SnomedReferenceSetMember) member).setReferencedComponent(component);
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
