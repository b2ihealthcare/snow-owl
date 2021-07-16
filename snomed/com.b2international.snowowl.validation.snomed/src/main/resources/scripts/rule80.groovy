package scripts;

import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.REFSET_OWL_AXIOM

import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.SnomedConcept
import com.b2international.snowowl.snomed.datastore.index.entry.*
import com.google.common.collect.Lists


RevisionSearcher searcher = ctx.service(RevisionSearcher.class)
	
List<ComponentIdentifier> issues = Lists.newArrayList();

ExpressionBuilder filterExpressionBuilder = Expressions.builder()
	.filter(SnomedComponentDocument.Expressions.active())
	.filter(SnomedComponentDocument.Expressions.modules([Concepts.MODULE_SCT_CORE, Concepts.MODULE_SCT_MODEL_COMPONENT]))


if (params.isUnpublishedOnly) {
	filterExpressionBuilder.filter(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}
Set<String> coreConceptIds = searcher
		.search(
			Query.select(String.class)
				.from(SnomedConceptDocument.class)
				.fields(SnomedConceptDocument.Fields.ID)
				.where(filterExpressionBuilder.build())
				.limit(Integer.MAX_VALUE)
			.build()
		).toSet()
		
Set<String> coreConceptsWithCoreParent = searcher.search(Query.select(String.class)
	.from(SnomedRelationshipIndexEntry.class)
	.fields(SnomedRelationshipIndexEntry.Fields.SOURCE_ID)
	.where(Expressions.builder()
		.filter(SnomedRelationshipIndexEntry.Expressions.active())
		.filter(SnomedRelationshipIndexEntry.Expressions.typeId(Concepts.IS_A))
		.filter(SnomedRelationshipIndexEntry.Expressions.sourceIds(coreConceptIds))
		.filter(SnomedRelationshipIndexEntry.Expressions.destinationIds(coreConceptIds))
		.build()
	)
	.limit(Integer.MAX_VALUE)
	.build()
).toSet()

searcher.search(Query.select(String.class)
	.from(SnomedRefSetMemberIndexEntry.class)
	.fields(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID)
	.where(Expressions.builder()
		.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
		.filter(SnomedRefSetMemberIndexEntry.Expressions.refsetIds([REFSET_OWL_AXIOM]))
		.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(coreConceptIds))
		.filter(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionType([Concepts.IS_A]))
		.filter(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionDestination(coreConceptIds))
		.build())
	.limit(Integer.MAX_VALUE)
	.build()
).each { coreConceptsWithCoreParent.add(it) }

coreConceptIds.remove(Concepts.ROOT_CONCEPT) // do NOT report ROOT concept without any ISA
coreConceptIds.each({ coreConceptId ->
	if (!coreConceptsWithCoreParent.contains(coreConceptId)) {
		issues.add(ComponentIdentifier.of(SnomedConcept.TYPE, coreConceptId))
	} 
})

return issues