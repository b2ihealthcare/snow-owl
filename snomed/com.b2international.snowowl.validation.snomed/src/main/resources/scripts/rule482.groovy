package scripts;

import java.util.Map.Entry
import java.util.stream.Stream

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.core.domain.SnomedDescription
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.base.Strings
import com.google.common.collect.ImmutableMap

import groovy.transform.Field

List<ComponentIdentifier> issues = new ArrayList<>()

def modules = SnomedRequests.prepareSearchConcept()
	.filterByEcl(params.modules)
	.filterByActive(true)
	.all()
	.build()
	.execute(ctx)
	.collect({it.getId()})

ExpressionBuilder filterExpressionBuilder = Expressions.builder()
	.filter(SnomedDescriptionIndexEntry.Expressions.active())
	.filter(SnomedDescriptionIndexEntry.Expressions.modules(modules))
	
if (params.isUnpublishedOnly) {
	filterExpressionBuilder.filter(SnomedDocument.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

Stream<Hits<String[]>> queryResult = ctx.service(RevisionSearcher.class)
		.stream(Query.select(String[].class)
		.from(SnomedDescriptionIndexEntry.class)
		.fields(
			SnomedDescriptionIndexEntry.Fields.ID,
			SnomedDescriptionIndexEntry.Fields.TERM
		)
		.where(filterExpressionBuilder.build())
		.limit(50000)
		.build())
		
queryResult.forEachOrdered({ hits ->
	for (String[] hit : hits) {
		def descId = hit[0]
		def descTerm = hit[1]
		if (!isValid(descTerm)) {
			ComponentIdentifier affectedComponent = ComponentIdentifier.of(SnomedDescription.TYPE, descId);
			issues.add(affectedComponent)
		}
	}
})
return issues

// Brackets to check in isValid method
@Field final Map<String, String> BRACKETS = ImmutableMap.of(
	"(", ")",
	"[", "]",
	"{", "}"
);

def isValid(String item) {
	if (Strings.isNullOrEmpty(item) || !containsBrackets(item)) {
		return true
	}

	final Stack<String> brackets = new Stack<>()

	for (char i : item.toCharArray()) {
		final String character = i.toString()
		if (isOpeningBracket(character)) {
			brackets.push(character)
		} else if (isClosingBracket(character)) {
			if (brackets.empty() || !isPaired(brackets.peek(), character)) {
				return false
			} else {
				brackets.pop()
			}
		}
	}
	return brackets.empty()
}

def isOpeningBracket(String c) {
	BRACKETS.containsKey(c)
}

def isClosingBracket(String c) {
	BRACKETS.containsValue(c)
}

def isPaired(String opening, String closing) {
	BRACKETS.containsKey(opening) && BRACKETS.get(opening).equals(closing)
}

def containsBrackets(String item) {
	for (Entry<String, String> bracketPair : BRACKETS.entrySet()) {
		if (item.contains(bracketPair.key) || item.contains(bracketPair.value)) {
			return true
		}
	}
	return false
}
