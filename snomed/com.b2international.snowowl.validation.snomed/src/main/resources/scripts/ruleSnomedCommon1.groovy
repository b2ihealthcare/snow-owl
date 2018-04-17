import com.b2international.index.Hits
import com.b2international.index.query.Expression
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.validation.issue.IssueDetail
import com.b2international.snowowl.snomed.common.SnomedRf2Headers
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Sets

Hits<String> inactiveConceptHits = ctx.service(RevisionSearcher.class)
		.search(Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(SnomedConceptDocument.Expressions.active(false))
		.limit(Integer.MAX_VALUE)
		.build())
		
Set<String> inactiveConceptIds = Sets.newHashSet(inactiveConceptHits);
		
Expression expression = Expressions.builder()
		.should(SnomedRelationshipIndexEntry.Expressions.sourceIds(inactiveConceptIds))
		.should(SnomedRelationshipIndexEntry.Expressions.typeIds(inactiveConceptIds))
		.should(SnomedRelationshipIndexEntry.Expressions.destinationIds(inactiveConceptIds))
		.filter(SnomedRelationshipIndexEntry.Expressions.active())
		.build()
		

Iterable<Hits<String[]>> invalidRelationshipHits = ctx.service(RevisionSearcher.class)
		.scroll(Query.select(String[].class)
		.from(SnomedRelationshipIndexEntry.class)
		.fields(SnomedRelationshipIndexEntry.Fields.ID, SnomedRelationshipIndexEntry.Fields.MODULE_ID)
		.where(expression)
		.limit(10_000)
		.build())
		
Collection<IssueDetail> invalidRelationshipIds = Sets.newHashSet();
invalidRelationshipHits.each( { hits ->
	for (String[] hit : hits) {
		invalidRelationshipIds.add(
			new IssueDetail(
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, hit[0]),
				ImmutableMap.of(
					SnomedRf2Headers.FIELD_ACTIVE, true,
					SnomedRf2Headers.FIELD_MODULE_ID, hit[1]
				) 
			)
		);
	}
})
		
return new ArrayList<>(invalidRelationshipIds)


		
