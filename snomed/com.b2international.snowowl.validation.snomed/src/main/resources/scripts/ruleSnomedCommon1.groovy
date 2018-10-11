package scripts;

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.core.domain.BranchContext
import com.b2international.snowowl.snomed.common.SnomedRf2Headers
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.google.common.collect.Lists

RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

Iterable<Hits<String>> inactiveConceptBatches = searcher.scroll(Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(SnomedConceptDocument.Expressions.inactive())
		.limit(10_000)
		.build())

List<ComponentIdentifier> issues = Lists.newArrayList()

inactiveConceptBatches.each({ conceptBatch ->
	List<String> inactiveConceptIds = conceptBatch.getHits()
	
	ExpressionBuilder invalidRelationshipExpression = Expressions.builder()
			.filter(SnomedRelationshipIndexEntry.Expressions.active())
			.should(SnomedRelationshipIndexEntry.Expressions.sourceIds(inactiveConceptIds))
			.should(SnomedRelationshipIndexEntry.Expressions.typeIds(inactiveConceptIds))
			.should(SnomedRelationshipIndexEntry.Expressions.destinationIds(inactiveConceptIds))
			
	if (params.isUnpublishedOnly) {
		invalidRelationshipExpression.filter(SnomedRelationshipIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
	}
	
	Iterable<Hits<String>> invalidRelationshipBatches = searcher.scroll(Query.select(String.class)
			.from(SnomedRelationshipIndexEntry.class)
			.fields(SnomedRelationshipIndexEntry.Fields.ID)
			.where(invalidRelationshipExpression.build())
			.limit(10_000)
			.build())

	invalidRelationshipBatches.each({ relationshipBatch ->
		relationshipBatch.each({ id ->
			issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, id))
		})
	})
})

return issues
