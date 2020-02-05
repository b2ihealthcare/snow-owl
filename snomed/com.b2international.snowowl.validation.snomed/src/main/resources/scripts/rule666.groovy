package scripts

import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.core.request.SearchResourceRequestIterator
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.Acceptability
import com.b2international.snowowl.snomed.core.domain.SnomedConcept
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts
import com.b2international.snowowl.snomed.core.domain.SnomedDescription
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.collect.HashMultiset
import com.google.common.collect.Lists
import com.google.common.collect.Multiset
import com.google.common.collect.Sets

final List<ComponentIdentifier> issues = Lists.newArrayList()

final Set<String> synonymTypes = Sets.newHashSet()

SnomedRequests.prepareSearchConcept()
		.all()
		.filterByActive(true)
		.filterByEcl("<<${Concepts.SYNONYM}")
		.build()
		.execute(ctx)
		.forEach({concept -> synonymTypes.add(concept.getId())})

SnomedConceptSearchRequestBuilder conceptsRequestBuilder = SnomedRequests.prepareSearchConcept()
		.filterByActive(true)
		.setLimit(50_000)
		.setScroll("5m")
		.setExpand("preferredDescriptions()")
				
if (params.isUnpublishedOnly) {

	def descriptionsIdsWithUnpublishedLanguageMembers = SnomedRequests.prepareSearchMember()
		.filterByReferencedComponentType(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER)
		.filterByRefSetType(SnomedRefSetType.LANGUAGE)
		.filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
		.filterByActive(true)
		.all()
		.build()
		.execute(ctx)
		.collect({ it.referencedComponent.id})	
	
	def conceptsWithUnpublishedLanguageMembers = SnomedRequests.prepareSearchDescription()
				.filterByIds(descriptionsIdsWithUnpublishedLanguageMembers)
				.setLimit(descriptionsIdsWithUnpublishedLanguageMembers.size())
				.setFields(SnomedDescriptionIndexEntry.Fields.ID,
					SnomedDescriptionIndexEntry.Fields.CONCEPT_ID)
				.build()
				.execute(ctx)
				.collect({SnomedDescription d -> d.getConceptId()})
	
	if (!conceptsWithUnpublishedLanguageMembers.isEmpty()) {
		conceptsRequestBuilder.filterByIds(conceptsWithUnpublishedLanguageMembers)
	} else {
		return issues
	}
	
}

final SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> iterator = new SearchResourceRequestIterator<>(conceptsRequestBuilder, {scrolledBuilder ->
	return scrolledBuilder.build().execute(ctx)
});

while(iterator.hasNext()) {
	for (SnomedConcept concept : iterator.next()) {
		def shouldReport = false
		
		Multiset<String> ptLanguageRefsets = HashMultiset.create()
		Multiset<String> fsnLanguageRefsets = HashMultiset.create()
		
		for (SnomedDescription description : concept.getPreferredDescriptions()) {
			for (String realmRefsetId : description.getAcceptabilityMap().keySet()) {
				Acceptability descriptionAcceptability = description.getAcceptabilityMap().get(realmRefsetId)
				if (descriptionAcceptability != null && descriptionAcceptability.equals(Acceptability.PREFERRED)) {
					if (description.getTypeId().equals(Concepts.FULLY_SPECIFIED_NAME)) {
						fsnLanguageRefsets.add(realmRefsetId)
					} else if (synonymTypes.contains(description.getTypeId())) {
						ptLanguageRefsets.add(realmRefsetId)
					}
				}
			}	
		}
		
		for (String realmRefsetId : ptLanguageRefsets) {
			if (ptLanguageRefsets.count(realmRefsetId) > 1) {
				shouldReport = true
				break
			}
		}
		
		for (String realmRefsetId : fsnLanguageRefsets) {
			if (fsnLanguageRefsets.count(realmRefsetId) > 1) {
				shouldReport = true
				break
			}
		}
		
		if (shouldReport) {
			issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, concept.getId()))
		}
		
	}
}

return issues
