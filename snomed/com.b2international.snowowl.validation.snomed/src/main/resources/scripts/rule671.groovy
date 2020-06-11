package scripts;

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedRf2Headers
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.SnomedDescription
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Sets

final Set<ComponentIdentifier> issues = Sets.newHashSet()
final RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

ExpressionBuilder filterExpressionBuilder = Expressions.builder()
		.filter(SnomedConceptDocument.Expressions.inactive())

if (params.isUnpublishedOnly) {
	filterExpressionBuilder.filter(SnomedConceptDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

List<String> inactiveIds = Lists.newArrayList()

searcher.scroll(Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(filterExpressionBuilder.build())
		.limit(10_000)
		.build())
		.each({id ->
			inactiveIds.add(id[0])
		})

Map<String, SnomedRefSetMemberIndexEntry> members = Maps.newHashMap()

searcher.scroll(Query.select(SnomedRefSetMemberIndexEntry.class)
		.from(SnomedRefSetMemberIndexEntry.class)
		.where(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR))
		.limit(10_000)
		.build())
		.each({hits ->
			hits.each({ hit ->
				members.put(hit.getReferencedComponentId(), hit)
			})
		})

final SnomedDescriptions descriptions = SnomedRequests.prepareSearchDescription()
		.all()
		.filterByConceptId(inactiveIds)
		.build()
		.execute(ctx)

descriptions.forEach({SnomedDescription description ->
	if (description.isActive()) {
		def inactivationIndicatorId = members.get(description.getId()).getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID)
		if (!(Concepts.PENDING_MOVE.equals(inactivationIndicatorId) || Concepts.LIMITED.equals(inactivationIndicatorId) ||
		Concepts.CONCEPT_NON_CURRENT.equals(inactivationIndicatorId))) {
			issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, description.getId()))
		}
	}

})

return issues.toList()
