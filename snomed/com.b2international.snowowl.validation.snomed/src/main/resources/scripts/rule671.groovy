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
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import com.google.common.collect.Sets

final Set<ComponentIdentifier> issues = Sets.newHashSet()
final RevisionSearcher searcher = ctx.service(RevisionSearcher.class)
final List<String> activeDescriptionIndicatorIds = ImmutableList.of(Concepts.PENDING_MOVE, Concepts.LIMITED, Concepts.CONCEPT_NON_CURRENT)

def checkDescriptions = { boolean active , List<String> inactiveConceptIds ->
	ExpressionBuilder filterSnomedDescriptions = Expressions.builder()
			.filter(SnomedDescriptionIndexEntry.Expressions.concepts(inactiveConceptIds))
			.filter(SnomedDescriptionIndexEntry.Expressions.active(active))

	final List<String> descriptionIds = []

//	println "Searching ${active ? 'active' : 'inactive'} descriptions on inactive concepts..."
	
	searcher.scroll(Query.select(String.class)
			.from(SnomedDescriptionIndexEntry.class)
			.fields(SnomedDescriptionIndexEntry.Fields.ID)
			.where(filterSnomedDescriptions.build())
			.withScores(false)
			.limit(100_000)
			.build())
			.each({Hits<String> hits ->
				descriptionIds.addAll(hits.getHits())
			})
			
//	println "Found ${descriptionIds.size()} ${active ? 'active' : 'inactive'} descriptions on inactive concepts..."

	ExpressionBuilder filterDescriptionsExpressionBuilder = Expressions.builder()
			.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR))
			.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
			.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(descriptionIds))
			
	if (params.isUnpublishedOnly) {
		filterDescriptionsExpressionBuilder.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
	}

	if (active) {
		filterDescriptionsExpressionBuilder
				.mustNot(SnomedRefSetMemberIndexEntry.Expressions.valueIds(activeDescriptionIndicatorIds))
	} else {
		filterDescriptionsExpressionBuilder
				.filter(SnomedRefSetMemberIndexEntry.Expressions.valueIds(activeDescriptionIndicatorIds))
	}
	
//	println "Searching matching reference set members..."
	
	searcher.scroll(Query.select(String.class)
		.from(SnomedRefSetMemberIndexEntry.class)
		.fields(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID)
		.where(filterDescriptionsExpressionBuilder.build())
		.withScores(false)
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
		.withScores(false)
		.limit(100_000)
		.build())
		.each({Hits<String> hits ->
			inactiveConceptIds.addAll(hits.getHits())
		})
//println "Loaded ${inactiveConceptIds.size()} inactive concepts"
		
checkDescriptions(true, inactiveConceptIds)
checkDescriptions(false, inactiveConceptIds)

return issues.toList()
