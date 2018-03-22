import com.b2international.index.Hits
import com.b2international.index.aggregations.Aggregation
import com.b2international.index.aggregations.AggregationBuilder
import com.b2international.index.query.Expression
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.snomed.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry
import com.google.common.collect.Lists
import com.google.common.collect.Sets


Hits<String> conceptHits = ctx.service(RevisionSearcher.class)
		.search(Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(SnomedConceptDocument.Expressions.active())
		.limit(Integer.MAX_VALUE)
		.build())
		
Set<String> conceptIds = Sets.newHashSet(conceptHits)

Expression expression = Expressions
		.builder()
		.filter(SnomedDescriptionIndexEntry.Expressions.active())
		.filter(SnomedDescriptionIndexEntry.Expressions.type(Concepts.FULLY_SPECIFIED_NAME))
		.filter(SnomedDescriptionIndexEntry.Expressions.concepts(conceptIds))
		.build()

Aggregation<SnomedDescriptionIndexEntry> buckets = ctx.service(RevisionSearcher.class)
		.aggregate(AggregationBuilder
		.bucket("ruleSnomedCommon2", SnomedDescriptionIndexEntry.class)
		.query(expression)
		.onFieldValue(SnomedDescriptionIndexEntry.Fields.ORIGINAL_TERM)
		.minBucketSize(2))

List<ComponentIdentifier> invalidIds = Lists.newArrayList()

buckets.getBuckets().values().each({ bucket ->
	bucket.each({ entry ->
		invalidIds.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, entry.getId()))
	})
})

return invalidIds;