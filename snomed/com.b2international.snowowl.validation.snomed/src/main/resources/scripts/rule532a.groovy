package scripts

import com.b2international.commons.options.Options
import com.b2international.index.aggregations.Aggregation
import com.b2international.index.aggregations.AggregationBuilder
import com.b2international.index.query.Expressions
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedRf2Headers
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.SnomedConcept
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.collect.Lists

//FSN terms should not duplicate other FSN terms, regardless of case

RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

List<ComponentIdentifier> issues = Lists.newArrayList()

Set<String> extensionModules = SnomedRequests.prepareSearchConcept()
	.filterByEcl(params.workingModules)
	.all()
	.build()
	.execute(ctx)
	.collect({ SnomedConcept c -> c.getId() })
	
def pendingMoveDescriptions = SnomedRequests.prepareSearchMember()
	.filterByRefSet(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR)
	.filterByProps(Options.builder()
			.put(SnomedRf2Headers.FIELD_VALUE_ID, Concepts.PENDING_MOVE)
			.build())
	.filterByActive(true)
	.all()
	.build()
	.execute(ctx)
	.collect({SnomedReferenceSetMember member -> member.getReferencedComponent().getId()})

ExpressionBuilder filterExpressionBuilder = Expressions.builder()
		.filter(SnomedDescriptionIndexEntry.Expressions.active())
		.filter(SnomedDescriptionIndexEntry.Expressions.type(Concepts.FULLY_SPECIFIED_NAME))
		.should(SnomedDescriptionIndexEntry.Expressions.ids(pendingMoveDescriptions)) // either pending move or no description inactivity indicator
		.should(Expressions.builder()
			.mustNot(SnomedDescriptionIndexEntry.Expressions.activeMemberOf(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR))
			.build())

Aggregation<String[]> activeDescriptionsByOriginalTerm = searcher.aggregate(
		AggregationBuilder.bucket("rule532a", String[].class, SnomedDescriptionIndexEntry.class)
			.query(filterExpressionBuilder.build())
			.onFieldValue(SnomedDescriptionIndexEntry.Fields.TERM_EXACT)
			.fields(SnomedDescriptionIndexEntry.Fields.ID,
				SnomedDescriptionIndexEntry.Fields.EFFECTIVE_TIME,
				SnomedDescriptionIndexEntry.Fields.MODULE_ID)
			.minBucketSize(2))

activeDescriptionsByOriginalTerm.getBuckets()
		.values()
		.each({ bucket ->
			def shouldReport = bucket.any({ hit ->
				def descEffectiveTime = hit[1]
				def moduleId = hit[2]
				if (!params.isUnpublishedOnly || Long.valueOf(descEffectiveTime).equals(EffectiveTimes.UNSET_EFFECTIVE_TIME)) {
					if (extensionModules.contains(moduleId)) {
						return true
					}
				}
				return false
			})
			
			if (shouldReport) {
				bucket.each({ hit ->
					def descId = hit[0]
					issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, descId))
				})
			}
		})

return issues
