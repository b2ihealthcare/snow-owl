package scripts

import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.SortBy
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.query.SortBy.Order
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.core.terminology.ComponentCategory
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers
import com.b2international.snowowl.snomed.core.domain.SnomedConcept
import com.b2international.snowowl.snomed.core.domain.SnomedDescription
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry

def RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

def Set<ComponentIdentifier> issues = []

// common query part
final ExpressionBuilder queryBuilder = Expressions.builder()
		.filter(SnomedRefSetMemberIndexEntry.Expressions.active(true))
		.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes([SnomedRefSetType.SIMPLE, SnomedRefSetType.LANGUAGE, SnomedRefSetType.ATTRIBUTE_VALUE]))

// use an alternative method when running in unpublished mode
if (params.isUnpublishedOnly) {
	// extract all unpublished active SIMPLE, LANG, ATTR VAL member refSet + refComp IDs in two filters and perform another search with them
	def Set<String> unpublishedRefSetIds = []
	def Set<String> unpublishedReferencedComponentIds = []
	
	searcher.stream(Query.select(String[].class)
		.from(SnomedRefSetMemberIndexEntry.class)
		.fields(SnomedRefSetMemberIndexEntry.Fields.REFSET_ID, SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID)
		.where(
			Expressions.builder()
				.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
				.filter(queryBuilder.build())
			.build()
		)
		.limit(10_000)
		.build()
	).forEachOrdered({ unpublishedMembers ->
		unpublishedMembers.each { unpublishedMember ->
			unpublishedRefSetIds.add(unpublishedMember[0])
			unpublishedReferencedComponentIds.add(unpublishedMember[1])
		}
	})
	
	// if there are no candidate members to test, then return early
	if (unpublishedRefSetIds.isEmpty() && unpublishedReferencedComponentIds.isEmpty()) {
		return issues.toList()
	}
	
	// attach refset and refComp filter to reduce visibility of rule to unpublished member references
	queryBuilder
		.filter(SnomedRefSetMemberIndexEntry.Expressions.refsetIds(unpublishedRefSetIds))
		.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(unpublishedReferencedComponentIds))
	
} 

def String previousMemberKey

// search ALL relevant members by the current query and sort them by refset and refComp and moduleId
searcher.stream(Query.select(String[].class)
	.from(SnomedRefSetMemberIndexEntry.class)
	.fields(SnomedRefSetMemberIndexEntry.Fields.REFSET_ID, SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID, SnomedDocument.Fields.MODULE_ID)
	.where(queryBuilder.build())
	.sortBy(
		SortBy.builder()
			.sortByField(SnomedRefSetMemberIndexEntry.Fields.REFSET_ID, Order.ASC)
			.sortByField(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID, Order.ASC)
			.sortByField(SnomedDocument.Fields.MODULE_ID, Order.ASC)
		.build()
	)
	.limit(10_000)
	.build()
).forEachOrdered({ members ->
	members.each { member ->
		def refSetId = member[0]
		def referencedComponentId = member[1]
		def moduleId = member[2]
		def currentMemberKey = String.join("_", refSetId, referencedComponentId, moduleId)
		if (!Objects.equals(previousMemberKey, currentMemberKey)) {
			previousMemberKey = currentMemberKey
		} else {
			switch (SnomedIdentifiers.getComponentCategory(referencedComponentId)) {
				case ComponentCategory.CONCEPT:
					issues.add(ComponentIdentifier.of(SnomedConcept.TYPE, referencedComponentId));
					break;
				case ComponentCategory.DESCRIPTION:
					issues.add(ComponentIdentifier.of(SnomedDescription.TYPE, referencedComponentId));
					break;
				case ComponentCategory.RELATIONSHIP:
					issues.add(ComponentIdentifier.of(SnomedRelationship.TYPE, referencedComponentId))
					break;
				default: // ignore
					break;
			}
		}
	}
})

return issues.toList()
		