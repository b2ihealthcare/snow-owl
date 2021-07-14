package scripts

import com.b2international.index.aggregations.Aggregation
import com.b2international.index.aggregations.AggregationBuilder
import com.b2international.index.query.Expressions
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.core.terminology.ComponentCategory
import com.b2international.snowowl.core.terminology.TerminologyRegistry
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers
import com.b2international.snowowl.snomed.core.domain.SnomedConcept
import com.b2international.snowowl.snomed.core.domain.SnomedDescription
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry
import com.google.common.collect.Lists

final RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

final List<ComponentIdentifier> issues = Lists.newArrayList()

final String script = String.format("return %s + '_' + %s + '_' + %s",
					"doc.referenceSetId.value",
					"doc.referencedComponentId.value",
					"doc.targetComponent.value")

final ExpressionBuilder queryBuilder = Expressions.builder()
	.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes(Collections.singleton(SnomedRefSetType.ASSOCIATION)))
	.filter(SnomedRefSetMemberIndexEntry.Expressions.active(true))
	
if (params.isUnpublishedOnly) {
	queryBuilder.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

final Aggregation<String> memberAggregation = searcher
	.aggregate(AggregationBuilder.bucket("rule670", String.class, SnomedRefSetMemberIndexEntry.class)
	.fields(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID)
	.query(queryBuilder.build())
	.onScriptValue(script)
	.minBucketSize(2)
	.setBucketHitsLimit(100))
	
memberAggregation.getBuckets().entrySet().each({entry ->
		entry.getValue().getHits().forEach({referencedComponentId ->
			final ComponentCategory referencedComponentCategory = SnomedIdentifiers.getComponentCategory(referencedComponentId)
			ComponentIdentifier affectedComponent = ComponentIdentifier.unknown(referencedComponentId)
			switch(referencedComponentCategory) {
				case ComponentCategory.CONCEPT:
					affectedComponent = ComponentIdentifier.of(SnomedConcept.TYPE, referencedComponentId);
					break;
				case ComponentCategory.DESCRIPTION:
					affectedComponent = ComponentIdentifier.of(SnomedDescription.TYPE, referencedComponentId);
					break;
					
				case ComponentCategory.RELATIONSHIP:
					affectedComponent = ComponentIdentifier.of(SnomedRelationship.TYPE, referencedComponentId);
					break;
					
				default:
					// ignore
					break;
			}
			if (!issues.contains(affectedComponent)) {
				issues.add(affectedComponent)
			}
		})
	})
	
	return issues