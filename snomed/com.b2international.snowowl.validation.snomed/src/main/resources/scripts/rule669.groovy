package scripts

import com.b2international.index.aggregations.Aggregation
import com.b2international.index.aggregations.AggregationBuilder
import com.b2international.index.query.Expressions
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.core.terminology.ComponentCategory
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry
import com.google.common.collect.Lists
import com.google.common.collect.ImmutableList

final RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

final List<ComponentIdentifier> issues = Lists.newArrayList()

final String script = String.format("return %s + '_' + %s",
					"doc.referenceSetId.value",
					"doc.referencedComponentId.value")

final ExpressionBuilder queryBuilder = Expressions.builder()
		.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes(ImmutableList.of(SnomedRefSetType.SIMPLE, SnomedRefSetType.LANGUAGE, SnomedRefSetType.ATTRIBUTE_VALUE)))
		.filter(SnomedRefSetMemberIndexEntry.Expressions.active(true))

if (params.isUnpublishedOnly) {
	queryBuilder.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

final Aggregation<String> memberAggregation = searcher
		.aggregate(AggregationBuilder.bucket("rule669", String.class, SnomedRefSetMemberIndexEntry.class)
		.fields(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID)
		.query(queryBuilder.build())
		.onScriptValue(script)
		.minBucketSize(2)
		.setBucketHitsLimit(100))
	
		
memberAggregation.getBuckets().entrySet().each({entry ->
	entry.getValue().getHits().forEach({referencedComponentId -> 
		final ComponentCategory referencedComponentCategory = SnomedIdentifiers.getComponentCategory(referencedComponentId)
		ComponentIdentifier affectedComponent = ComponentIdentifier.UNKOWN
		switch(referencedComponentCategory) {
			case ComponentCategory.CONCEPT:
				affectedComponent = ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, referencedComponentId);
				break;
			case ComponentCategory.DESCRIPTION:
				affectedComponent = ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, referencedComponentId);
				break;
				
			case ComponentCategory.RELATIONSHIP:
				affectedComponent = ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, referencedComponentId);
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
		