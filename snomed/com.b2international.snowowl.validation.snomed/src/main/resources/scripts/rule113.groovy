package scripts;

import static com.google.common.collect.Lists.newArrayList

import com.b2international.index.Hits
import com.b2international.index.aggregations.Aggregation
import com.b2international.index.aggregations.AggregationBuilder
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry

final RevisionSearcher searcher = ctx.service(RevisionSearcher.class);

final List<ComponentIdentifier> issues = newArrayList();

final Query<String> conceptIdQuery = Query.select(String.class)
	.from(SnomedConceptDocument.class)
	.fields(SnomedConceptDocument.Fields.ID)
	.where(SnomedConceptDocument.Expressions.active(true))
	.limit(10_000)
	.scroll("2m")
	.build()
	
searcher.scroll(conceptIdQuery).each({ conceptIds -> 

	final ExpressionBuilder expression = com.b2international.index.query.Expressions
		.builder()
		.filter(SnomedRelationshipIndexEntry.Expressions.active())
		.filter(SnomedRelationshipIndexEntry.Expressions.sourceIds(conceptIds.collect()))
		.filter(SnomedRelationshipIndexEntry.Expressions.group(1, Integer.MAX_VALUE))
		.mustNot(SnomedRelationshipIndexEntry.Expressions.characteristicTypeId(Concepts.ADDITIONAL_RELATIONSHIP))
	
	final String script = String.format("return %s + '_' + %s + '_' + %s",
			"doc.sourceId.value",
			"doc.characteristicTypeId.value",
			"doc.group.value");
	
	final Aggregation<SnomedRelationshipIndexEntry> relationshipAggregation = searcher
		.aggregate(AggregationBuilder.bucket("rule113", SnomedRelationshipIndexEntry.class)
		.query(expression.build())
		.onScriptValue(script)
		.setBucketHitsLimit(0))
	
	final Set<String> invalidConceptIds = new HashSet();
	
	relationshipAggregation.getBuckets().entrySet().each({entry ->
		if (entry.getValue().getHits().getTotal() < 2) {
			String key = entry.getKey()
			int indexOfUnderscore = key.indexOf('_')
			String sourceId = key.substring(0, indexOfUnderscore)
			invalidConceptIds.add(sourceId);
		} 
	})
	
	ExpressionBuilder queryBuilder = Expressions.builder().filter(SnomedConceptDocument.Expressions.ids(invalidConceptIds));
	
	if (params.isUnpublishedOnly) {
		queryBuilder.filter(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
	}
	
	Hits<String> hits = searcher.search(
			Query.select(String.class)
			.from(SnomedConceptDocument.class)
			.fields(SnomedConceptDocument.Fields.ID)
			.where(queryBuilder.build())
			.build()
		)
	
	hits.each({id -> 
		ComponentIdentifier affectedComponent = ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, id);
		issues.add(affectedComponent)
	})
})

return issues