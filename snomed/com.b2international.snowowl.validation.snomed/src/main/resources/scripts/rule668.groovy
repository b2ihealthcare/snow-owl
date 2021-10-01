package scripts;

import java.util.function.Supplier

import com.b2international.commons.CompareUtils
import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry
import com.google.common.base.Suppliers

/**
 *  OWL axiom relationships must not refer to inactive concepts as its type or destination.
 */
def UNPUBLISHED_THRESHOLD = 2_000
def RevisionSearcher searcher = ctx.service(RevisionSearcher.class)
def List<ComponentIdentifier> issues = []

def Supplier<Set<String>> inactiveConceptIds = Suppliers.memoize({
	def Set<String> ids = []
	searcher.stream(Query.select(String.class)
			.from(SnomedConceptDocument.class)
			.fields(SnomedConceptDocument.Fields.ID)
			.where(SnomedConceptDocument.Expressions.inactive())
			.limit(100_000)
			.build())
	.forEachOrdered({ Hits<String> conceptIds ->
		ids.addAll(conceptIds.getHits())
	})
	return ids
})

if (params.isUnpublishedOnly) {
	// load the first threshold number of unpublished OWL Axioms, 
	// if there are more than that fall back to the original algorithm
	// otherwise iterate over them and check the invalid ID reference
	Hits<SnomedRefSetMemberIndexEntry> unpublishedFirstHits = searcher.search(Query.select(SnomedRefSetMemberIndexEntry.class)
		.where(
			Expressions.builder()
				.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
				.filter(SnomedRefSetMemberIndexEntry.Expressions.refsetId(Concepts.REFSET_OWL_AXIOM))
				.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
			.build()
		)
		.limit(UNPUBLISHED_THRESHOLD)
		.build()
	)
	
	// process only if there are less than the configured threshold available
	if (unpublishedFirstHits.total <= UNPUBLISHED_THRESHOLD) {
		def Set<String> inactiveConcepts = inactiveConceptIds.get()
		unpublishedFirstHits.each { hit ->
			if (inactiveConcepts.contains(hit.getReferencedComponentId())) {
				issues.add(ComponentIdentifier.of(SnomedReferenceSetMember.TYPE, hit.getId()))
			} else if (!CompareUtils.isEmpty(hit.getClassAxiomRelationships())) {
				for (SnomedOWLRelationshipDocument classAxiomRelationship : hit.getClassAxiomRelationships()) {
					if (inactiveConcepts.contains(classAxiomRelationship.getTypeId()) || inactiveConcepts.contains(classAxiomRelationship.getDestinationId())) {
						issues.add(ComponentIdentifier.of(SnomedReferenceSetMember.TYPE, hit.getId()))
					}
				}
			} else if (!CompareUtils.isEmpty(hit.getGciAxiomRelationships())) {
				for (SnomedOWLRelationshipDocument classAxiomRelationship : hit.getGciAxiomRelationships()) {
					if (inactiveConcepts.contains(classAxiomRelationship.getTypeId()) || inactiveConcepts.contains(classAxiomRelationship.getDestinationId())) {
						issues.add(ComponentIdentifier.of(SnomedReferenceSetMember.TYPE, hit.getId()))
					}
				}
			}
		}
		return issues
	}
}

ExpressionBuilder invalidOWLAxiomExpression = Expressions.builder()
		.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
		.filter(SnomedRefSetMemberIndexEntry.Expressions.refsetId(Concepts.REFSET_OWL_AXIOM))
		.should(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionConcept(inactiveConceptIds.get()))
		.should(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(inactiveConceptIds.get()))

if (params.isUnpublishedOnly) {
	invalidOWLAxiomExpression.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

searcher
	.stream(Query.select(String.class)
	.from(SnomedRefSetMemberIndexEntry.class)
	.fields(SnomedRefSetMemberIndexEntry.Fields.ID)
	.where(invalidOWLAxiomExpression.build())
	.limit(10_000)
	.build())
	.forEachOrdered({ memberIds ->
		memberIds.each { memberId ->
			issues.add(ComponentIdentifier.of(SnomedReferenceSetMember.TYPE, memberId))
		}
	})

return issues
