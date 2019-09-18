package scripts;

import com.b2international.index.Hits
import com.b2international.index.aggregations.Aggregation
import com.b2international.index.aggregations.AggregationBuilder
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.core.domain.SnomedConcept
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.b2international.snowowl.core.domain.BranchContext
import com.b2international.snowowl.snomed.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.common.SnomedRf2Headers
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument
import com.google.common.collect.Lists
import com.google.common.collect.Sets

final RevisionSearcher searcher = ctx.service(RevisionSearcher.class)


final String DEFINITION_TYPE_REGEX = "^\\w{4096,}\$"

final ExpressionBuilder activeTextDefinitionFilter = Expressions.builder()
        .filter(SnomedDescriptionIndexEntry.Expressions.active())
        .filter(SnomedDescriptionIndexEntry.Expressions.type(Concepts.TEXT_DEFINITION))
        .filter(SnomedDescriptionIndexEntry.Expressions.matchTermRegex(DEFINITION_TYPE_REGEX))

if (params.isUnpublishedOnly) {
    activeTextDefinitionFilter.filter(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

Iterable<Hits<String>> queryResult =  searcher.scroll(Query.select(String.class)
        .from(SnomedDescriptionIndexEntry.class)
        .fields(SnomedDescriptionIndexEntry.Fields.ID)
        .where(activeTextDefinitionFilter.build())
        .limit(10_000)
        .build())

List<ComponentIdentifier> issues =  new ArrayList<>();

queryResult.each({hits ->
    for (String descriptionId : hits) {
        issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, descriptionId))
    }
})

return issues
