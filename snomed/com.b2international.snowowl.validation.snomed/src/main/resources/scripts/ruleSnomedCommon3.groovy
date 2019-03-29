package scripts

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.CHARACTERISTIC_TYPE
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.INFERRED_RELATIONSHIP
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.STATED_RELATIONSHIP
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ADDITIONAL_RELATIONSHIP

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.core.domain.SnomedConcept
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests

RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

String deprecatedCharTypesEclFormat = "<%s MINUS (%s OR %s OR %S)"

Set<String> deprecatedCharacheristicTypes = SnomedRequests.prepareSearchConcept()
	.all()
	.filterByEcl(String.format(deprecatedCharTypesEclFormat, CHARACTERISTIC_TYPE, INFERRED_RELATIONSHIP, STATED_RELATIONSHIP, ADDITIONAL_RELATIONSHIP))
	.build()
	.execute(ctx)
	.collect({ SnomedConcept c -> c.getId() })
	
ExpressionBuilder fitlerExpressionBuilder = Expressions.builder()
		.filter(SnomedRelationshipIndexEntry.Expressions.active())
		.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeIds(deprecatedCharacheristicTypes))
		
if (params.isUnpublishedOnly) {
	fitlerExpressionBuilder.filter(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}
		
Iterable<Hits<String>> queryResult =  searcher.scroll(Query.select(String.class)
	.from(SnomedRelationshipIndexEntry.class)
	.fields(SnomedRelationshipIndexEntry.Fields.ID)
	.where(fitlerExpressionBuilder.build())
	.limit(10_000)
	.build())

List<ComponentIdentifier> issues =  new ArrayList<>();

queryResult.each({hits -> 
	for (String relationshipId : hits) {
		issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipId))
	}
})

return issues