package scripts;

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry
import com.google.common.collect.Lists

/**
 *  OWL axiom relationships must not refer to inactive concepts as its type or destination.
 */
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
	
	ExpressionBuilder invalidOWLAxiomExpression = Expressions.builder()
			.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
			.should(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionConcept(inactiveConceptIds))
			.should(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(inactiveConceptIds));
	
	if (params.isUnpublishedOnly) {
		invalidOWLAxiomExpression.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
	}
	
	Iterable<Hits<String>> invalidAxiomMemberBatches = searcher.scroll(Query.select(String.class)
			.from(SnomedRefSetMemberIndexEntry.class)
			.fields(SnomedRefSetMemberIndexEntry.Fields.ID)
			.where(invalidOWLAxiomExpression.build())
			.limit(10_000)
			.build())

	invalidAxiomMemberBatches.each({ axiomMemberBatch ->
		axiomMemberBatch.each({ id ->
			issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, id))
		})
	})
})

return issues
