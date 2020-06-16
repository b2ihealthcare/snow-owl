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

ExpressionBuilder filterInactiveConceptsExpressionBuilder = Expressions.builder()
		.filter(SnomedConceptDocument.Expressions.inactive())

Set<String> inactiveIds = Sets.newHashSet()

searcher.scroll(Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(filterInactiveConceptsExpressionBuilder.build())
		.limit(10_000)
		.build())
		.each({hits ->
			hits.each({ id ->
				inactiveIds.add(id)
			})
		})
		
ExpressionBuilder filterActiveSnomedDescriptions = Expressions.builder()
		.filter(SnomedDescriptionIndexEntry.Expressions.concepts(inactiveIds))
		.must(SnomedDescriptionIndexEntry.Expressions.active())
		
ExpressionBuilder filterInactiveSnomedDescriptions = Expressions.builder()
		.filter(SnomedDescriptionIndexEntry.Expressions.concepts(inactiveIds))
		.must(SnomedDescriptionIndexEntry.Expressions.inactive())

		
final Set<String> activeDescriptionIds = Sets.newHashSet()

searcher.scroll(Query.select(String.class)
		.from(SnomedDescriptionIndexEntry.class)
		.fields(SnomedDescriptionIndexEntry.Fields.ID)
		.where(filterActiveSnomedDescriptions.build())
		.limit(10_000)
		.build())
		.each({hits ->
			hits.each({ id ->
				activeDescriptionIds.add(id)
			})
		})
		
final Set<String> inactiveDescriptionIds = Sets.newHashSet()

searcher.scroll(Query.select(String.class)
		.from(SnomedDescriptionIndexEntry.class)
		.fields(SnomedDescriptionIndexEntry.Fields.ID)
		.where(filterActiveSnomedDescriptions.build())
		.limit(10_000)
		.build())
		.each({hits ->
			hits.each({ id ->
				inactiveDescriptionIds.add(id)
			})
		})

ExpressionBuilder filterActiveDescriptionsExpressionBuilder = Expressions.builder()
		.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR))
        .filter(SnomedRefSetMemberIndexEntry.Expressions.active())
        .filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(activeDescriptionIds))
		.mustNot(SnomedRefSetMemberIndexEntry.Expressions.valueIds(ImmutableList.of(Concepts.PENDING_MOVE, Concepts.LIMITED, Concepts.CONCEPT_NON_CURRENT)))


ExpressionBuilder filterInactiveDescriptionsExpressionBuilder = Expressions.builder()
		.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR))
        .filter(SnomedRefSetMemberIndexEntry.Expressions.active())
        .filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(inactiveDescriptionIds))
		.filter(SnomedRefSetMemberIndexEntry.Expressions.valueIds(ImmutableList.of(Concepts.PENDING_MOVE, Concepts.LIMITED, Concepts.CONCEPT_NON_CURRENT)))

if (params.isUnpublishedOnly) {
	filterActiveDescriptionsExpressionBuilder.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
	filterInactiveDescriptionsExpressionBuilder.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

searcher.scroll(Query.select(SnomedRefSetMemberIndexEntry.class)
		.from(SnomedRefSetMemberIndexEntry.class)
		.where(filterActiveDescriptionsExpressionBuilder.build())
		.limit(10_000)
		.build())
		.each({hits ->
			hits.each({ hit ->
				issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, hit.getReferencedComponentId()))
			})
		})

searcher.scroll(Query.select(SnomedRefSetMemberIndexEntry.class)
		.from(SnomedRefSetMemberIndexEntry.class)
		.where(filterInactiveDescriptionsExpressionBuilder.build())
		.limit(10_000)
		.build())
		.each({hits ->
			hits.each({ hit ->
				issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, hit.getReferencedComponentId()))
			})
		})

return issues.toList()
