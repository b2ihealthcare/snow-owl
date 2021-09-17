package scripts;

import static com.google.common.collect.Sets.newHashSet

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.SortBy
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.query.SortBy.Order
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.google.common.collect.Lists

RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

ExpressionBuilder effectiveTimeExpressionBuilder =  Expressions.builder().filter(SnomedRelationshipIndexEntry.Expressions.active())
if (params.isUnpublishedOnly) {
	effectiveTimeExpressionBuilder.filter(SnomedRelationshipIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

Query<String[]> query = Query.select(String[].class)
	.from(SnomedRelationshipIndexEntry.class)
	.fields(SnomedRelationshipIndexEntry.Fields.ID, // 0 
		SnomedRelationshipIndexEntry.Fields.RELATIONSHIP_GROUP, // 1
		SnomedRelationshipIndexEntry.Fields.SOURCE_ID, // 2
		SnomedRelationshipIndexEntry.Fields.TYPE_ID, // 3
		SnomedRelationshipIndexEntry.Fields.DESTINATION_ID, // 4
		SnomedRelationshipIndexEntry.Fields.CHARACTERISTIC_TYPE_ID, // 5
		SnomedRelationshipIndexEntry.Fields.MODIFIER_ID //6
	)
	
	.where(effectiveTimeExpressionBuilder.build())
	.sortBy(SortBy.builder()
		.sortByField(SnomedRelationshipIndexEntry.Fields.SOURCE_ID, Order.ASC)
		.sortByField(SnomedRelationshipIndexEntry.Fields.RELATIONSHIP_GROUP, Order.ASC)
		.build())
	.limit(50_000)
	.build()

List<ComponentIdentifier> issues = Lists.newArrayList();
Set<String> buckets = newHashSet()
String currentSourceId = null
	
for (Hits<String[]> page : searcher.scroll(query)) {
	for (String[] relationship : page) {
		if (!relationship[2].equals(currentSourceId)) {
			buckets.clear()
			currentSourceId = relationship[2] 
		}
		String key = String.format("%s_%s_%s_%s_%s", relationship[2], relationship[3], relationship[4], relationship[5], relationship[6])
		if ("0".equals(relationship[1])) {
			buckets.add(key);
		} else if (buckets.contains(key)) { // report duplication of ungrouped relationship in a group
			issues.add(ComponentIdentifier.of(SnomedRelationship.TYPE, relationship[0]));					
		}
	}
}

return issues;