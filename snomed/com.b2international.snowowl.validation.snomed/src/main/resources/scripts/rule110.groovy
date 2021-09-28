package scripts

import static com.b2international.index.query.Expressions.*

import java.util.stream.Stream

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.collect.Sets
	
RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

Set<String> attributeHierarchyConceptIds = 
	SnomedRequests.prepareSearchConcept()
		.all()
		.filterByEcl(String.format("<<%s", Concepts.ATTRIBUTE))
		.build()
		.execute(ctx)
		.collect { c -> c.getId() } as Set		

if (attributeHierarchyConceptIds.isEmpty()) {
	return Collections.emptyList();
}

Set<ComponentIdentifier> issues = Sets.newHashSet()

ExpressionBuilder owlAxiomMemberQuery = Expressions
	.builder()
	.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
	.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes([SnomedRefSetType.OWL_AXIOM]))
	.filter(Expressions.builder()
		.should(nestedMatch("classAxiomRelationships", exists("typeId")))
		.should(nestedMatch("gciAxiomRelationships", exists("typeId")))
	.build())
	.mustNot(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionType(attributeHierarchyConceptIds))
	
if (params.isUnpublishedOnly) {
	owlAxiomMemberQuery.filter(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

Stream<Hits<String>> owlAxiomMemberQueryResult = searcher
	.stream(Query.select(String.class)
	.from(SnomedRefSetMemberIndexEntry.class)
	.fields(SnomedRefSetMemberIndexEntry.Fields.ID)
	.where(owlAxiomMemberQuery.build())
	.limit(10_000)
	.build())
	
owlAxiomMemberQueryResult.forEachOrdered({relHits ->
	for (String id: relHits) {
		ComponentIdentifier affectedComponent = ComponentIdentifier.of(SnomedReferenceSetMember.TYPE, id);
		issues.add(affectedComponent)
	}
})

ExpressionBuilder relationshipQuery = Expressions
	.builder()
	.filter(SnomedRelationshipIndexEntry.Expressions.active())
	.mustNot(SnomedRelationshipIndexEntry.Expressions.typeIds(attributeHierarchyConceptIds))

if (params.isUnpublishedOnly) {
	relationshipQuery.filter(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

Stream<Hits<String>> relationshipQueryResult = searcher
	.stream(Query.select(String.class)
		.from(SnomedRelationshipIndexEntry.class)
		.fields(SnomedRelationshipIndexEntry.Fields.ID)
		.where(relationshipQuery.build())
		.limit(10_000)
		.build())

relationshipQueryResult.forEachOrdered({relHits ->
	for (String id: relHits) {
		ComponentIdentifier affectedComponent = ComponentIdentifier.of(SnomedRelationship.TYPE, id);
		issues.add(affectedComponent)
	}
})

return issues as List
