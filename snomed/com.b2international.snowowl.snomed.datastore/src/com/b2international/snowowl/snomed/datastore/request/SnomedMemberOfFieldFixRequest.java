/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import static com.b2international.index.revision.Revision.Fields.ID;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Fields.ACTIVE;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @since 7.21.1
 */
public class SnomedMemberOfFieldFixRequest implements Request<TransactionContext, Set<String>> {

	private static final long serialVersionUID = 7930138235619925182L;
	private static final int LIMIT = 10_000;
	
	@Override
	@SuppressWarnings("deprecation")
	public Set<String> execute(TransactionContext context) {
		Logger log = LoggerFactory.getLogger("dataset-fix-SO-5690");
		
		Set<String> referenceSetIds = SnomedRequests.prepareSearchRefSet()
				.all()
				.setFields(SnomedComponentDocument.Fields.ID)
				.build()
				.execute(context)
				.stream()
				.map(SnomedReferenceSet::getId)
				.collect(Collectors.toSet());
		
		log.info("Found a total of {} reference sets", referenceSetIds.size());

		int counter = 0;
		Multimap<String, String> missingMembersOfComponents = HashMultimap.create();
		Multimap<String, String> missingActiveMembersOfComponents = HashMultimap.create();
		
		for (String referenceSetId : referenceSetIds) {
			Multimap<String, String> missingMembersOf = HashMultimap.create();
			Multimap<String, String> missingActiveMembersOf = HashMultimap.create();
						
			log.info("Processing batch {}", ++counter);
			log.info("Processing refset {}", referenceSetId);
			
			SnomedRefSetMemberSearchRequestBuilder memberRequest = SnomedRequests.prepareSearchMember()
					.filterByRefSet(referenceSetId)
					.setLimit(50_000)
					.setFields(ID, ACTIVE, REFERENCED_COMPONENT_ID);
			
			SearchResourceRequestIterator<SnomedRefSetMemberSearchRequestBuilder, SnomedReferenceSetMembers> iterator = 
					new SearchResourceRequestIterator<>(memberRequest, b -> b.build().execute(context));
			
			iterator.forEachRemaining(refsetMembers ->
				refsetMembers.forEach( m -> {
					if (m.isActive()) {
						missingActiveMembersOf.put(m.getReferencedComponentId(), referenceSetId);
					}
					missingMembersOf.put(m.getReferencedComponentId(), referenceSetId);
				})
			);
			
			// Find concepts/descriptions with incomplete member of fields
			SnomedConceptSearchRequestBuilder conceptMemberOfRequest = SnomedRequests.prepareSearchConcept().isMemberOf(referenceSetId).setFields(ID).setLimit(50_000);
			SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> memberOfConceptIterator = 
					new SearchResourceRequestIterator<>(conceptMemberOfRequest, b -> b.build().execute(context));
			memberOfConceptIterator.forEachRemaining(concepts -> concepts.forEach(concept -> missingMembersOf.removeAll(concept.getId())));
			
			SnomedDescriptionSearchRequestBuilder descriptionMemberOfRequest = SnomedRequests.prepareSearchDescription().isMemberOf(referenceSetId).setFields(ID).setLimit(50_000);
			SearchResourceRequestIterator<SnomedDescriptionSearchRequestBuilder, SnomedDescriptions> memberOfDescriptionIterator = 
					new SearchResourceRequestIterator<>(descriptionMemberOfRequest, b -> b.setLimit(50_000).build().execute(context));
			memberOfDescriptionIterator.forEachRemaining(descriptions -> descriptions.forEach(description -> missingMembersOf.removeAll(description.getId())));
			
			missingMembersOfComponents.putAll(missingMembersOf);
			log.info("Found {} components with missing member of entry for reference set {}", missingMembersOf.size(), referenceSetId);
			
			// Find concepts/descriptions with incomplete active member of fields
			SnomedConceptSearchRequestBuilder conceptActiveMemberOfRequest = SnomedRequests.prepareSearchConcept().isActiveMemberOf(referenceSetId).setFields(ID).setLimit(50_000);
			SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> activeMemberOfConceptIterator = 
					new SearchResourceRequestIterator<>(conceptActiveMemberOfRequest, b -> b.build().execute(context));
			activeMemberOfConceptIterator.forEachRemaining(concepts -> concepts.forEach(concept -> missingActiveMembersOf.removeAll(concept.getId())));
	
			SnomedDescriptionSearchRequestBuilder descriptionActiveMemberOfRequest = SnomedRequests.prepareSearchDescription().isActiveMemberOf(referenceSetId).setFields(ID).setLimit(50_000);
			SearchResourceRequestIterator<SnomedDescriptionSearchRequestBuilder, SnomedDescriptions> activeMemberOfDescriptionIterator = 
					new SearchResourceRequestIterator<>(descriptionActiveMemberOfRequest, b -> b.build().execute(context));
			activeMemberOfDescriptionIterator.forEachRemaining(descriptions -> descriptions.forEach(description -> missingActiveMembersOf.removeAll(description.getId())));
			
			missingActiveMembersOfComponents.putAll(missingActiveMembersOf);
			log.info("Found {} components with missing active member of entry for reference set {}",  missingActiveMembersOf.size(), referenceSetId);

		}
		
		int modifiedComponentCount = 0;
		
		Set<String> allAffectedComponents = Sets.union(missingMembersOfComponents.keySet(), missingActiveMembersOfComponents.keySet());
		
		for (String componentId : allAffectedComponents) {
			try {
				
				if (SnomedIdentifiers.isConceptIdentifier(componentId)) {
					
					final SnomedConceptDocument concept = context.lookup(componentId, SnomedConceptDocument.class);				
					List<String> memberOf = new ArrayList<>(concept.getMemberOf());
					if (missingMembersOfComponents.containsKey(componentId)) {
						memberOf.addAll(missingMembersOfComponents.get(componentId));				
					}
					
					List<String> activeMemberOf = new ArrayList<>(concept.getActiveMemberOf());
					if (missingActiveMembersOfComponents.containsKey(componentId)) {
						activeMemberOf.addAll(missingActiveMembersOfComponents.get(componentId));
					}
					
					SnomedConceptDocument updatedDocument = SnomedConceptDocument.builder(concept)
							.memberOf(memberOf)
							.activeMemberOf(activeMemberOf)
							.build();
					context.update(concept, updatedDocument);
					
				} else {
					
					final SnomedDescriptionIndexEntry description = context.lookup(componentId, SnomedDescriptionIndexEntry.class);
					
					List<String> memberOf = new ArrayList<>(description.getMemberOf());
					if (missingMembersOfComponents.containsKey(componentId)) {
						memberOf.addAll(missingMembersOfComponents.get(componentId));				
					}
					
					List<String> activeMemberOf = new ArrayList<>(description.getActiveMemberOf());
					if (missingActiveMembersOfComponents.containsKey(componentId)) {
						activeMemberOf.addAll(missingActiveMembersOfComponents.get(componentId));
					}
					
					SnomedDescriptionIndexEntry updatedDocument = SnomedDescriptionIndexEntry.builder(description)
							.memberOf(memberOf)
							.activeMemberOf(activeMemberOf)
							.build();
					context.update(description, updatedDocument);
				}
				
				modifiedComponentCount++;
				if (modifiedComponentCount % LIMIT == 0) {
					context.commit("Update memberOf/activeMemberOf fields on concepts");
				}
				
			} catch (ComponentNotFoundException exception) {
				log.error(exception.getMessage());
			}
		}
		
		//Commit any remaining concepts;
		context.commit("Update memberOf/activeMemberOf fields on concepts");
		return Set.copyOf(allAffectedComponents);
	}
		
}
