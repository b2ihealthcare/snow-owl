import com.b2international.index.Hits
import com.b2international.index.query.Expression
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.google.common.collect.Sets

Iterable<Hits<String>> inactiveConceptHits = ctx.service(RevisionSearcher.class)
		.scroll(Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(SnomedConceptDocument.Expressions.active(false))
		.limit(100_099)
		.build())
		
Collection<String> inactiveConceptIds = Sets.newHashSet();
inactiveConceptHits.each( { hits -> 
	for (String hit : hits) {
		inactiveConceptIds.add(hit);
	}
})
		
Expression shouldrelationshipExpression = Expressions.builder()
		.should(SnomedRelationshipIndexEntry.Expressions.sourceIds(inactiveConceptIds))
		.should(SnomedRelationshipIndexEntry.Expressions.typeIds(inactiveConceptIds))
		.should(SnomedRelationshipIndexEntry.Expressions.destinationIds(inactiveConceptIds))
		.build()
		
Expression expression = Expressions.builder()
		.should(shouldrelationshipExpression)
		.filter(SnomedRelationshipIndexEntry.Expressions.active())
		.build()

Iterable<Hits<String>> invalidRelationshipHits = ctx.service(RevisionSearcher.class)
		.scroll(Query.select(String.class)
		.from(SnomedRelationshipIndexEntry.class)
		.fields(SnomedRelationshipIndexEntry.Fields.ID)
		.where(expression)
		.limit(10_000)
		.build())
		
Collection<ComponentIdentifier> invalidRelationshipIds = Sets.newHashSet();
invalidRelationshipHits.each( { hits ->
	for (String hit : hits) {
		invalidRelationshipIds.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, hit));
	}
})
		
return new ArrayList<>(invalidRelationshipIds)


		
