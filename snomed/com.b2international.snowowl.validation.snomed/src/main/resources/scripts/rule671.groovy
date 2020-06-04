package scripts;

import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedRf2Headers
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry
import com.google.common.collect.Maps
import com.google.common.collect.Sets

final Set<ComponentIdentifier> issues = Sets.newHashSet()
final RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

ExpressionBuilder filterExpressionBuilder = Expressions.builder()
		.filter(SnomedConceptDocument.Expressions.inactive())

if (params.isUnpublishedOnly) {
	filterExpressionBuilder.filter(SnomedConceptDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

Map<String, SnomedRefSetMemberIndexEntry> members = Maps.newHashMap()

Query<String> queryRefsetMembers =Query.select(SnomedRefSetMemberIndexEntry.class)
		.from(SnomedRefSetMemberIndexEntry.class)
		.where(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR))
		.limit(Integer.MAX_VALUE)
		.build()

searcher.search(queryRefsetMembers).forEach({SnomedRefSetMemberIndexEntry member ->
	members.put(member.getReferencedComponentId(), member)

})

Query<String> queryConcepts = Query.select(SnomedConceptDocument.class)
		.from(SnomedConceptDocument.class)
		.where(filterExpressionBuilder.build())
		.limit(Integer.MAX_VALUE)
		.build()

searcher.search(queryConcepts).forEach({SnomedConceptDocument concept ->
	String inactivationIndicatorId = (String) members.get(concept.getId()).getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID);
	println(inactivationIndicatorId)
	if (!(inactivationIndicatorId.equals(Concepts.PENDING_MOVE)|| inactivationIndicatorId.equals(Concepts.LIMITED) ||
	inactivationIndicatorId.equals(Concepts.CONCEPT_NON_CURRENT))) {
		issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, concept.getId()))
	}
})

return issues.toList()
