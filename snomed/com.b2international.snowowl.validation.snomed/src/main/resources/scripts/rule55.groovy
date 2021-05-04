package scripts;

import java.util.regex.Pattern

import com.b2international.index.Hits
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.collect.Lists

import groovy.transform.Field

List<ComponentIdentifier> issues = Lists.newArrayList()

@Field 
static final Pattern SYMBOL_PATTERN = Pattern.compile("[^A-Za-z0-9]")

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
	filterExpressionBuilder.filter(SnomedDescriptionIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
}

Iterable<Hits<String[]>> queryResult = ctx.service(RevisionSearcher.class)
	.scroll(Query.select(String[].class)
		.from(SnomedDescriptionIndexEntry.class)
		.fields(
			SnomedDescriptionIndexEntry.Fields.ID,
			SnomedDescriptionIndexEntry.Fields.TERM
		)
		.where(filterExpressionBuilder.build())
		.limit(50_000)
		.build())
	queryResult.each( { hits -> 
		for(String[] hit: hits) {
			if(!isValid(hit[1])) {
				def descId = hit[0]
				ComponentIdentifier affectedComponent = ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, descId);
				issues.add(affectedComponent)
			}
		}
	})

	def isValid(String term) {
		char[] letters = term.toCharArray();
		for (int i = 1; i < letters.length - 1; i++) {
			int whiteSpaceBeforeCount = 0;
			int whiteSpaceAfterCount = 0;
			if(letters[i] == '-') {
				
				//if there is a symbol after a hyphen, just ignore it, and step for the next hyphen if there is
				if (SYMBOL_PATTERN.matcher(String.valueOf(letters[i + 1])).matches() && !Character.isWhitespace(letters[i + 1])) {
					continue;
				}

				//if there is a symbol before a hyphen, just ignore it, and step for the next hyphen if there is
				if (SYMBOL_PATTERN.matcher(String.valueOf(letters[i - 1])).matches() && !Character.isWhitespace(letters[i - 1])) {
					continue
				}				
				
				if(Character.isWhitespace(letters[i - 1])) {
					for (int j = i - 1; Character.isWhitespace(letters[j]); j--) {
						whiteSpaceBeforeCount++;
					}
				}
				
				if(Character.isWhitespace(letters[i + 1])) {
					for (int j = i + 1; Character.isWhitespace(letters[j]); j++) {
						whiteSpaceAfterCount++;
					}
				}
				if(whiteSpaceBeforeCount != whiteSpaceAfterCount) {
					return false;
				}
			}
		}
		return true;
	}
	
return issues
