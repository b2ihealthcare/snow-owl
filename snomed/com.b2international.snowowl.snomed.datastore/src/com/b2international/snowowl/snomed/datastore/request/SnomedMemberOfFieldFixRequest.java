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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @since 7.21.1
 */
public class SnomedMemberOfFieldFixRequest implements Request<TransactionContext, Set<String>> {

	private static final long serialVersionUID = 7930138235619925182L;
	private static final int LIMIT = 10_000;
	
	Set<String> affectedComponentIds = new HashSet<>();
	
	@Override
	public Set<String> execute(TransactionContext context) {
		Logger log = LoggerFactory.getLogger("dataset-fix-SO-5690");
		processConcepts(context, log);
		processDescriptions(context, log);
		return affectedComponentIds;
	}
	
	private void processConcepts(TransactionContext context, Logger log) {		
		Set<String> referenceSetIds = SnomedRequests.prepareSearchRefSet()
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
				.setLimit(Integer.MAX_VALUE)
				.build()
				.execute(context)
				.stream()
				.map(SnomedReferenceSet::getId)
				.collect(Collectors.toSet());
		
		log.info("Found a total of " + referenceSetIds.size() + " concept type reference sets");

		int counter = 0;
		Multimap<String, String> missingMembersOfConcepts = HashMultimap.create();
		Multimap<String, String> missingActiveMembersOfConcepts = HashMultimap.create();
		
		for (String referenceSetId : referenceSetIds) {
			Multimap<String, String> missingMembersOf = HashMultimap.create();
			Multimap<String, String> missingActiveMembersOf = HashMultimap.create();
						
			log.info("Processing batch " + counter++);
			log.info("Processing refset " + referenceSetId);
			
			SnomedRefSetMemberSearchRequestBuilder memberRequest = SnomedRequests.prepareSearchMember()
					.filterByRefSet(referenceSetId)
					.setLimit(50_000)
					.setFields("id", "active", SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID);
			
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
			
			SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> memberOfonceptIterator = 
					new SearchResourceRequestIterator<>(
							SnomedRequests.prepareSearchConcept().isMemberOf(referenceSetId).setFields("id"),
							b -> b.setLimit(50_000).build().execute(context));
			memberOfonceptIterator.forEachRemaining(concepts -> concepts.forEach(concept -> missingMembersOf.removeAll(concept.getId())));
			missingMembersOfConcepts.putAll(missingMembersOf);
			log.info("Found " + missingMembersOf.size() + " concepts with missing member of entry for reference set " + referenceSetId);
			
			SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> activeMemberOfonceptIterator = 
					new SearchResourceRequestIterator<>(
							SnomedRequests.prepareSearchConcept().isActiveMemberOf(referenceSetId).setFields("id"),
							b -> b.setLimit(50_000).build().execute(context));
			activeMemberOfonceptIterator.forEachRemaining(concepts -> concepts.forEach(concept -> missingActiveMembersOf.removeAll(concept.getId())));
			missingActiveMembersOfConcepts.putAll(missingActiveMembersOf);
			log.info("Found " + missingActiveMembersOf.size() + " concepts with missing active member of entry for reference set " + referenceSetId);
		}
		
		int modifiedConceptCount = 0;
		
		Set<String> allAffectedConcepts = Sets.union(missingMembersOfConcepts.keySet(), missingActiveMembersOfConcepts.keySet());
		
		for (String conceptId : allAffectedConcepts) {
			try {
				
				final SnomedConceptDocument concept = context.lookup(conceptId, SnomedConceptDocument.class);				
				List<String> memberOf = new ArrayList<>(concept.getMemberOf());
				if (missingMembersOfConcepts.containsKey(conceptId)) {
					memberOf.addAll(missingMembersOfConcepts.get(conceptId));				
				}
				
				List<String> activeMemberOf = new ArrayList<>(concept.getActiveMemberOf());
				if (missingActiveMembersOfConcepts.containsKey(conceptId)) {
					activeMemberOf.addAll(missingActiveMembersOfConcepts.get(conceptId));
				}
				
				SnomedConceptDocument updatedDocument = SnomedConceptDocument.builder(concept)
						.memberOf(memberOf)
						.activeMemberOf(activeMemberOf)
						.build();
				context.update(concept, updatedDocument);
				
				modifiedConceptCount++;
				if (modifiedConceptCount%LIMIT == 0) {
					context.commit("Update memberOf/activeMemberOf fields on concepts");
				}
				
			} catch (ComponentNotFoundException exception) {
				log.error(exception.getMessage());
			}
		}
		
		//Commit any remaining concepts;
		context.commit("Update memberOf/activeMemberOf fields on concepts");
		affectedComponentIds.addAll(allAffectedConcepts);
	}
	
	private void processDescriptions(TransactionContext context, Logger log) {		
		Set<String> referenceSetIds = SnomedRequests.prepareSearchRefSet()
				.filterByReferencedComponentType(SnomedTerminologyComponentConstants.DESCRIPTION)
				.setLimit(Integer.MAX_VALUE)
				.build()
				.execute(context)
				.stream()
				.map(SnomedReferenceSet::getId)
				.collect(Collectors.toSet());
		
		log.info("Found a total of " + referenceSetIds.size() + " active, description type reference sets");

		int counter = 0;
		Multimap<String, String> missingMembersOfDescriptions = HashMultimap.create();
		Multimap<String, String> missingActiveMembersOfDescriptions = HashMultimap.create();
		
		for (String referenceSetId : referenceSetIds) {
			Multimap<String, String> missingMembersOf = HashMultimap.create();
			Multimap<String, String> missingActiveMembersOf = HashMultimap.create();
						
			log.info("Processing batch " + counter++);
			log.info("Processing refset " + referenceSetId);
			
			SnomedRefSetMemberSearchRequestBuilder memberRequest = SnomedRequests.prepareSearchMember()
					.filterByRefSet(referenceSetId)
					.setLimit(50_000)
					.setFields("id", "active", SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID);
			
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
			
			SearchResourceRequestIterator<SnomedDescriptionSearchRequestBuilder, SnomedDescriptions> memberOfDescriptionIterator = new SearchResourceRequestIterator<>(
							SnomedRequests.prepareSearchDescription().isMemberOf(referenceSetId).setFields("id"),
							b -> b.setLimit(50_000).build().execute(context));
			memberOfDescriptionIterator.forEachRemaining(descriptions -> descriptions.forEach(description -> missingMembersOf.removeAll(description.getId())));
			missingMembersOfDescriptions.putAll(missingMembersOf);
			log.info("Found " + missingMembersOf.size() + " descriptions with missing member of entry for reference set " + referenceSetId);
			
			SearchResourceRequestIterator<SnomedDescriptionSearchRequestBuilder, SnomedDescriptions> activeMemberOfDescriptionIterator = 
					new SearchResourceRequestIterator<>(
							SnomedRequests.prepareSearchDescription().isActiveMemberOf(referenceSetId).setFields("id"),
							b -> b.setLimit(50_000).build().execute(context));
			activeMemberOfDescriptionIterator.forEachRemaining(descriptions -> descriptions.forEach(description -> missingActiveMembersOf.removeAll(description.getId())));
			missingActiveMembersOfDescriptions.putAll(missingActiveMembersOf);
			log.info("Found " + missingActiveMembersOf.size() + " descriptions with missing active member of entry for reference set " + referenceSetId);
		}
		
		int modifiedDescriptionCount = 0;
		
		Set<String> allAffectedDescriptions = Sets.union(missingMembersOfDescriptions.keySet(), missingActiveMembersOfDescriptions.keySet());
		
		for (String descriptionId : allAffectedDescriptions) {
			try {
				final SnomedDescriptionIndexEntry description = context.lookup(descriptionId, SnomedDescriptionIndexEntry.class);
				
				List<String> memberOf = new ArrayList<>(description.getMemberOf());
				if (missingMembersOfDescriptions.containsKey(descriptionId)) {
					memberOf.addAll(missingMembersOfDescriptions.get(descriptionId));				
				}
				
				List<String> activeMemberOf = new ArrayList<>(description.getActiveMemberOf());
				if (missingActiveMembersOfDescriptions.containsKey(descriptionId)) {
					activeMemberOf.addAll(missingActiveMembersOfDescriptions.get(descriptionId));
				}
				
				SnomedDescriptionIndexEntry updatedDocument = SnomedDescriptionIndexEntry.builder(description)
						.memberOf(memberOf)
						.activeMemberOf(activeMemberOf)
						.build();
				context.update(description, updatedDocument);
				
				modifiedDescriptionCount++;
				if (modifiedDescriptionCount%LIMIT == 0) {
					context.commit("Update memberOf/activeMemberOf fields on descriptions");
				}
				
			} catch (Exception exception) {
				log.error(exception.getMessage());
			}
		}
		
		//Commit any remaining descriptions;
		context.commit("Update memberOf/activeMemberOf fields on descriptions");
		affectedComponentIds.addAll(allAffectedDescriptions);
	}
	
}
