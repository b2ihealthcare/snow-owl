package scripts;

import static com.google.common.collect.Maps.newHashMap

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

ExpressionBuilder effectiveTimeExpressionBuilder = Expressions.builder()

if (params.isUnpublishedOnly) {
	effectiveTimeExpressionBuilder.filter(SnomedRelationshipIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

Query<String[]> query = Query.select(String[].class)
	.from(SnomedRelationshipIndexEntry.class)
	.fields(SnomedRelationshipIndexEntry.Fields.ID, // 0 
		SnomedRelationshipIndexEntry.Fields.MODULE_ID, // 1 
		SnomedRelationshipIndexEntry.Fields.GROUP, // 2
		SnomedRelationshipIndexEntry.Fields.SOURCE_ID, // 3
		SnomedRelationshipIndexEntry.Fields.TYPE_ID, // 4
		SnomedRelationshipIndexEntry.Fields.DESTINATION_ID, // 5
		SnomedRelationshipIndexEntry.Fields.CHARACTERISTIC_TYPE_ID, // 6
		SnomedRelationshipIndexEntry.Fields.MODIFIER_ID, // 7
		SnomedRelationshipIndexEntry.Fields.UNION_GROUP // 8
		
	)
	.where(Expressions
		.builder()
		.filter(SnomedRelationshipIndexEntry.Expressions.active())
		.filter(effectiveTimeExpressionBuilder.build())
		.build())
	.sortBy(SortBy.builder()
		.sortByField(SnomedRelationshipIndexEntry.Fields.SOURCE_ID, Order.ASC)
		.sortByField(SnomedRelationshipIndexEntry.Fields.GROUP, Order.ASC)
		.build())
	.limit(50000)
	.build()

List<ComponentIdentifier> issues = Lists.newArrayList();

String[] REPORTED = new String[0]

Map<String, String[]> buckets = newHashMap()

String currentSourceId = null
String currentGroupId = null


for (Hits<String[]> page : searcher.scroll(query)) {
	for (String[] relationship : page) {
		def sourceId = relationship[3]
		if (!sourceId.equals(currentSourceId)) {
			buckets.clear()
			currentSourceId = relationship[3]
			currentGroupId = relationship[2]
		}
		
		def groupId = relationship[2]
		
		if (!groupId.equals(currentGroupId)) {
			buckets.clear()
			currentGroupId = relationship[2]
		}
		String key = String.format("%s_%s_%s_%s_%s_%s", relationship[3], relationship[4], relationship[5], relationship[6], relationship[7], relationship[8])
		
		String[] duplicate = buckets.get(key)
		if (duplicate != null) {
			// report duplication of grouped relationship in the same group
			issues.add(ComponentIdentifier.of(SnomedRelationship.TYPE, relationship[0]));
			
			if (duplicate != REPORTED) {
				issues.add(ComponentIdentifier.of(SnomedRelationship.TYPE, duplicate[0]));
				buckets.put(key, REPORTED)
			}
			
		} else {
			buckets.put(key, relationship[0..1])
		}
	}
}

return issues;
