package scripts;

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

def checkDescriptions = { boolean active , Set<String> inactiveConceptIds ->
	ExpressionBuilder filterSnomedDescriptions = Expressions.builder()
			.filter(SnomedDescriptionIndexEntry.Expressions.concepts(inactiveConceptIds))
			.filter(SnomedDescriptionIndexEntry.Expressions.active(active))

	final Set<String> descriptionIds = Sets.newHashSet()

	searcher.scroll(Query.select(String.class)
			.from(SnomedDescriptionIndexEntry.class)
			.fields(SnomedDescriptionIndexEntry.Fields.ID)
			.where(filterSnomedDescriptions.build())
			.limit(10_000)
			.build())
			.each({hits ->
				hits.each({ id ->
					descriptionIds.add(id)
				})
			})

	ExpressionBuilder filterDescriptionsExpressionBuilder = Expressions.builder()
			.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR))
			.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
			.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(descriptionIds))
			
	if (params.isUnpublishedOnly) {
		filterDescriptionsExpressionBuilder.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
	}

	if(active) {
		filterDescriptionsExpressionBuilder
				.mustNot(SnomedRefSetMemberIndexEntry.Expressions.valueIds(activeDescriptionIndicatorIds))
	} else {
		filterDescriptionsExpressionBuilder
				.filter(SnomedRefSetMemberIndexEntry.Expressions.valueIds(activeDescriptionIndicatorIds))
	}
	searcher.scroll(Query.select(SnomedRefSetMemberIndexEntry.class)
		.from(SnomedRefSetMemberIndexEntry.class)
		.where(filterDescriptionsExpressionBuilder.build())
		.limit(10_000)
		.build())
		.each({hits ->
			hits.each({ hit ->
				issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, hit.getReferencedComponentId()))
			})
		})
}

ExpressionBuilder filterInactiveConceptsExpressionBuilder = Expressions.builder()
		.filter(SnomedConceptDocument.Expressions.inactive())

Set<String> inactiveConceptIds = Sets.newHashSet()

searcher.scroll(Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(filterInactiveConceptsExpressionBuilder.build())
		.limit(10_000)
		.build())
		.each({hits ->
			hits.each({ id ->
				inactiveConceptIds.add(id)
			})
		})
		
checkDescriptions(true, inactiveConceptIds)
checkDescriptions(false, inactiveConceptIds)

return issues.toList()
