package scripts;

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.index.Hits
import com.b2international.index.aggregations.Aggregation
import com.b2international.index.aggregations.AggregationBuilder
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.core.domain.BranchContext
import com.b2international.snowowl.snomed.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.common.SnomedRf2Headers
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument

import com.b2international.snowowl.snomed.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.common.SnomedRf2Headers
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.common.collect.Lists
import com.google.common.collect.ImmutableMap

final RevisionSearcher searcher = ctx.service(RevisionSearcher.class)

final SnomedReferenceSetMembers descriptionFormatMembers = SnomedRequests.prepareSearchMember()
        .filterByRefSet(Concepts.REFSET_DESCRIPTION_TYPE)
        .filterByActive(true)
        .setExpand("referencedComponent()")
        .all()
        .build()
        .execute(ctx)

final Multimap<Integer, String> descriptionTypesByMaxLength = HashMultimap.create();

for (SnomedReferenceSetMember member : descriptionFormatMembers) {
    final Integer descriptionLength = member.getProperties().get(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH)
    final String descriptionTypeId = member.getReferencedComponent().getId()
    descriptionTypesByMaxLength.put(descriptionLength, descriptionTypeId)
}

final List<ComponentIdentifier> issues =  new ArrayList<>();
for (Integer length : descriptionTypesByMaxLength.keySet()) {
    final Collection<String> descriptionTypes = descriptionTypesByMaxLength.get(length)

    final ExpressionBuilder activeDescriptionFilter = Expressions.builder()
            .filter(SnomedDescriptionIndexEntry.Expressions.active())
            .filter(SnomedDescriptionIndexEntry.Expressions.types(descriptionTypes))
            .filter(Expressions.scriptQuery("descriptionLength", ImmutableMap.of("length", length)))

    if (params.isUnpublishedOnly) {
        activeDescriptionFilter.filter(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
    }

    final Iterable<Hits<String>> invalidDescriptions = searcher.scroll(Query.select(String.class)
            .from(SnomedDescriptionIndexEntry.class)
            .fields(SnomedDescriptionIndexEntry.Fields.ID)
            .where(activeDescriptionFilter.build())
            .limit(20_000)
            .build())

    invalidDescriptions.each({ descriptionBatch ->
        descriptionBatch.each({ id ->
            issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, id))
        })
    })
}

return issues
