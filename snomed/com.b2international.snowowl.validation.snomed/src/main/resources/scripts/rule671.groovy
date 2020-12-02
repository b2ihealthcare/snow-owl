package scripts;

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.SnomedDescription
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import com.google.common.collect.Multimap
import com.google.common.collect.Sets

def Set<ComponentIdentifier> issues = []
def RevisionSearcher searcher = ctx.service(RevisionSearcher.class)
def activeDescriptionIndicatorIds = [Concepts.PENDING_MOVE, Concepts.LIMITED, Concepts.CONCEPT_NON_CURRENT]

if (params.isUnpublishedOnly) {
	
	def reportDescriptions = { Set<String> descriptionIds, boolean active ->
		if (descriptionIds.isEmpty()) {
			return
		}
		
		searcher.search(Query.select(String.class)
			.from(SnomedDescriptionIndexEntry.class)
			.fields(SnomedRefSetMemberIndexEntry.Fields.ID)
			.where(
				Expressions.builder()
					.filter(SnomedDescriptionIndexEntry.Expressions.ids(descriptionIds))
					.filter(SnomedDescriptionIndexEntry.Expressions.active(active))
				.build()
			)
			.limit(descriptionIds.size())
			.build()
		).each { String descriptionToReport -> 
			issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, descriptionToReport))
		}
		
	}
	
	// report descriptions with incorrect unpublished inactivation indicator members 
	searcher.scroll(Query.select(String[].class)
			.from(SnomedRefSetMemberIndexEntry.class)
			.fields(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID, SnomedRefSetMemberIndexEntry.Fields.VALUE_ID)
			.where(
				// active, unpublished description inactivation refset members only
				Expressions.builder()
					.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
					.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR))
					.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
				.build()
			)
			.limit(10_000)
			.build())
			.each { Hits<String[]> members ->
				def Set<String> descriptionsWithActiveDescriptionIndicator = Sets.newHashSet()
				def Set<String> descriptionsWithInactiveDescriptionIndicator = Sets.newHashSet()
				members.each { String[] member ->
					if (activeDescriptionIndicatorIds.contains(member[1])) {
						descriptionsWithActiveDescriptionIndicator.add(member[0])
					} else {
						descriptionsWithInactiveDescriptionIndicator.add(member[0])
					}
				}
				
				// run two queries per 10k batch
				// active descriptions referencing indicator values that should be added to inactive descriptions
				reportDescriptions(descriptionsWithActiveDescriptionIndicator, false /* description should be active, searching for inactive ones to find errors */)
				// inactive descriptions referencing indicator values that should be added to active descriptions
				reportDescriptions(descriptionsWithInactiveDescriptionIndicator, true /* description should be inactive, searching for active ones to find errors */)
			}
	
} else {
	// report descriptions with incorrect unpublished or published inactivation indicator members
	def checkDescriptions = { boolean active , List<String> inactiveConceptIds ->
		final List<String> descriptionIds = []
		
		//	println "Searching ${active ? 'active' : 'inactive'} descriptions on inactive concepts..."
			
		searcher.scroll(Query.select(String.class)
				.from(SnomedDescriptionIndexEntry.class)
				.fields(SnomedDescriptionIndexEntry.Fields.ID)
				.where(
					Expressions.builder()
						.filter(SnomedDescriptionIndexEntry.Expressions.concepts(inactiveConceptIds))
						.filter(SnomedDescriptionIndexEntry.Expressions.active(active))
					.build()
				)
				.limit(100_000)
				.build())
				.each({Hits<String> hits ->
					descriptionIds.addAll(hits.getHits())
				})
					
		//	println "Found ${descriptionIds.size()} ${active ? 'active' : 'inactive'} descriptions on inactive concepts..."
		
		ExpressionBuilder memberQuery = Expressions.builder()
				.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
				.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR))
				.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(descriptionIds))
				
		if (params.isUnpublishedOnly) {
			memberQuery.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
		}
	
		if (active) {
			memberQuery
					.mustNot(SnomedRefSetMemberIndexEntry.Expressions.valueIds(activeDescriptionIndicatorIds))
		} else {
			memberQuery
					.filter(SnomedRefSetMemberIndexEntry.Expressions.valueIds(activeDescriptionIndicatorIds))
		}
	
		//	println "Searching matching reference set members..."
			
		searcher.scroll(Query.select(String.class)
			.from(SnomedRefSetMemberIndexEntry.class)
			.fields(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID)
			.where(memberQuery.build())
			.limit(10_000)
			.build())
			.each({hits ->
				hits.each({ hit ->
					issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, hit))
				})
			})
			
		//	println "Found ${issues.size()} issues"
	}
	
	ExpressionBuilder filterInactiveConceptsExpressionBuilder = Expressions.builder()
			.filter(SnomedConceptDocument.Expressions.inactive())
	
	List<String> inactiveConceptIds = []
	
	//println "Loading inactive concepts"
	searcher.scroll(Query.select(String.class)
			.from(SnomedConceptDocument.class)
			.fields(SnomedConceptDocument.Fields.ID)
			.where(filterInactiveConceptsExpressionBuilder.build())
			.limit(100_000)
			.build())
			.each({Hits<String> hits ->
				inactiveConceptIds.addAll(hits.getHits())
			})
	//println "Loaded ${inactiveConceptIds.size()} inactive concepts"
			
	checkDescriptions(true, inactiveConceptIds)
	checkDescriptions(false, inactiveConceptIds)
}

return issues.toList()
