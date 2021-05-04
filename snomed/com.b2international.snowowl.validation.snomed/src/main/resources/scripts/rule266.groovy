package scripts

import static com.google.common.collect.Lists.newArrayList

import java.util.stream.Collectors

import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.SnomedDescription
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.base.Charsets
import com.google.common.io.Files

File resourceFile = params.resourcesDir.resolve("scripts/Non-Acceptable_Semantic_Tags.txt").toFile()
List<String> nonAcceptableSematicTags = newArrayList()
nonAcceptableSematicTags = Files.readLines(resourceFile, , Charsets.UTF_8)

List<ComponentIdentifier> issues = newArrayList()

List<String> semanticTagsWithoutParentheses = nonAcceptableSematicTags.stream()
	.map{semanticTag -> semanticTag.substring(1, semanticTag.length() -1)}
	.collect(Collectors.toList())

SnomedDescriptionSearchRequestBuilder descRequestBuilder = SnomedRequests.prepareSearchDescription()
	.filterByType(String.format("<<%s", Concepts.SYNONYM))
	.filterBySemanticTags(semanticTagsWithoutParentheses)
	.filterByModule(params.workingModules)
	.filterByActive(true)
	.all()
	.setFields(SnomedDescriptionIndexEntry.Fields.ID, SnomedDescriptionIndexEntry.Fields.EFFECTIVE_TIME)
	
	if (params.isUnpublishedOnly) {
		descRequestBuilder.filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
	}

SnomedDescriptions results = descRequestBuilder
	.build()
	.execute(ctx)

for (SnomedDescription desc : results) {
	ComponentIdentifier affectedComponent = ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, desc.getId());
	issues.add(affectedComponent)
}
	
return issues;
