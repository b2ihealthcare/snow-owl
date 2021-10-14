package scripts;

import java.util.stream.Collectors

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Multimap

def RevisionSearcher searcher = ctx.service(RevisionSearcher.class)
def Set<String> relationshipIdsToReport = []

if (params.isUnpublishedOnly) {
	// unpublished only
	searcher
		.stream(Query.select(String[].class)
		.from(SnomedRelationshipIndexEntry.class)
		.fields(SnomedRelationshipIndexEntry.Fields.ID, SnomedRelationshipIndexEntry.Fields.SOURCE_ID, SnomedRelationshipIndexEntry.Fields.TYPE_ID, SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
		.where(
			Expressions.builder()
				.filter(SnomedRelationshipIndexEntry.Expressions.active())
				.filter(SnomedRelationshipIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
			.build()
		)
		.limit(10_000)
		.build())
		.forEachOrdered({ Hits<String[]> relationships ->
			
			def Multimap<String, String> relationshipsBySource = HashMultimap.create() 
			def Multimap<String, String> relationshipsByType = HashMultimap.create()
			def Multimap<String, String> relationshipsByDestination = HashMultimap.create()
			
			relationships.each { relationship -> 
				relationshipsBySource.put(relationship[1], relationship[0])
				relationshipsByType.put(relationship[2], relationship[0])
				relationshipsByDestination.put(relationship[3], relationship[0])
			}
			
			def Set<String> ids = []
			ids.addAll(relationshipsBySource.keySet())
			ids.addAll(relationshipsByType.keySet())
			ids.addAll(relationshipsByDestination.keySet())
			
			searcher
				.search(Query.select(String.class)
					.from(SnomedConceptDocument.class)
					.fields(SnomedConceptDocument.Fields.ID)
					.where(
						Expressions.builder()
							.filter(SnomedConceptDocument.Expressions.ids(ids))
							.filter(SnomedConceptDocument.Expressions.inactive())
						.build()
					)
					.limit(ids.size())
					.build()
				)
				.each { inactiveConceptId ->
					relationshipIdsToReport.addAll(relationshipsBySource.get(inactiveConceptId))
					relationshipIdsToReport.addAll(relationshipsByType.get(inactiveConceptId))
					relationshipIdsToReport.addAll(relationshipsByDestination.get(inactiveConceptId))
				}
		})
} else {
	// published + unpublished
	searcher
		.stream(Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(SnomedConceptDocument.Expressions.inactive())
		.limit(30_000)
		.build())
		.forEachOrdered({ Hits<String> conceptBatch ->
			def inactiveConceptIds = ImmutableSet.copyOf(conceptBatch.getHits())
			
			searcher
				.stream(Query.select(String.class)
				.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedRelationshipIndexEntry.Fields.ID)
				.where(
					Expressions.builder()
						.filter(SnomedRelationshipIndexEntry.Expressions.active())
						.should(SnomedRelationshipIndexEntry.Expressions.sourceIds(inactiveConceptIds))
						.should(SnomedRelationshipIndexEntry.Expressions.typeIds(inactiveConceptIds))
						.should(SnomedRelationshipIndexEntry.Expressions.destinationIds(inactiveConceptIds))
					.build()
				)
				.limit(10_000)
				.build())
				.forEachOrdered({ Hits<String> relationshipBatch ->
					relationshipIdsToReport.addAll(relationshipBatch.getHits())
				})
		})
}

return relationshipIdsToReport.stream().map({id -> ComponentIdentifier.of(SnomedRelationship.TYPE, id)}).collect(Collectors.toList())
