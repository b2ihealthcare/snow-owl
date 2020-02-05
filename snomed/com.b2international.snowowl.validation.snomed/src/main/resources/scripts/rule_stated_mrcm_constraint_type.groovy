package scripts

import java.util.stream.Collectors

import javax.validation.Constraint

import com.b2international.index.Hits
import com.b2international.index.query.Expression
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedCardinalityPredicate
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedRelationshipPredicate
import com.b2international.snowowl.snomed.core.ecl.EclExpression
import com.b2international.snowowl.snomed.core.tree.Trees
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipSearchRequestBuilder
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.base.Joiner
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Lists
import com.google.common.collect.Multimap
import com.google.common.collect.Sets

RevisionSearcher searcher = ctx.service(RevisionSearcher.class)
List<ComponentIdentifier> issues = Lists.newArrayList()

def mrcmRules = SnomedRequests.prepareSearchConstraint()
		.all()
		.build()
		.execute(ctx)
		.stream()
		.filter({SnomedConstraint constraint -> constraint.getPredicate() instanceof SnomedCardinalityPredicate
			? ((SnomedCardinalityPredicate) constraint.getPredicate()).getPredicate() instanceof SnomedRelationshipPredicate
			: constraint.getPredicate() instanceof SnomedRelationshipPredicate})
		.collect();
		
def typeMultimapBuilder = ImmutableMultimap.builder()

for (SnomedConstraint constraint : mrcmRules) {
	SnomedRelationshipPredicate predicate = constraint.getPredicate() instanceof SnomedCardinalityPredicate
		? ((SnomedCardinalityPredicate) constraint.getPredicate()).getPredicate()
		: constraint.getPredicate()
	String typeId = predicate.getAttributeExpression()
	
	if (!typeId.equals(Concepts.IS_A)) {
		typeMultimapBuilder.put(typeId, constraint)
	}
}

Multimap<String, SnomedConstraint> mrcmRulesByAttributeType = typeMultimapBuilder.build()

def getApplicableConcepts = { String conceptSetExpression ->
	def expression = Expressions.builder()
		.filter(EclExpression.of(conceptSetExpression, Trees.STATED_FORM).resolveToExpression(ctx).getSync())
		.build()
	
	Query<String> conceptSetQuery = Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(expression)
		.limit(Integer.MAX_VALUE)
		.build()
	
	Set<String> conceptIds = Sets.newHashSet()
	searcher.search(conceptSetQuery)
		.each({id -> conceptIds.add(id)})
	return conceptIds
}

def getCachedApplicableConcepts = { String conceptSetExpression ->
	return getApplicableConcepts(conceptSetExpression)
}.memoize()

def getApplicableRules = { String conceptId, String typeId ->
	final Set<SnomedConstraint> applicableConstraints = Sets.newHashSet()
	
	if (mrcmRulesByAttributeType.keySet().contains(typeId)) {
		for (SnomedConstraint constraint : mrcmRulesByAttributeType.get(typeId)) {
			if (getCachedApplicableConcepts(constraint.getDomain().toEcl()).contains(conceptId)) {
				applicableConstraints.add(Constraint)
			}
		}
	}
	
	return applicableConstraints
}

if (params.isUnpublishedOnly) {
	SnomedRelationshipSearchRequestBuilder requestBuilder = SnomedRequests.prepareSearchRelationship()
			.filterByActive(true)
			.filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
			.all()

	Expression where = requestBuilder.build().prepareQuery(ctx);

	final Query<String[]> relationshipQuery = Query.select(String[].class)
			.from(SnomedRelationshipIndexEntry.class)
			.fields(SnomedRelationshipIndexEntry.Fields.ID,
					SnomedRelationshipIndexEntry.Fields.SOURCE_ID,
					SnomedRelationshipIndexEntry.Fields.TYPE_ID)
			.where(where)
			.limit(Integer.MAX_VALUE)
			.build();
	
	Iterable<Hits<String[]>> queryResult = ctx.service(RevisionSearcher.class).search(relationshipQuery);
	
	queryResult.each { hit ->
		def relationshipId = hit[0]
		def sourceId = hit[1]
		def typeId = hit[2]
		
		if (!typeId.equals(Concepts.IS_A) && getApplicableRules(sourceId, typeId).isEmpty()) {
			issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipId))			
		}
	}
	
} else {
	for (String typeExpression : mrcmRulesByAttributeType.keySet()) {
		
		def allowedDomainExpressions = mrcmRulesByAttributeType.get(typeExpression)
			.stream()
			.map({SnomedConstraint constraint -> constraint.getDomain().toEcl()})
			.collect(Collectors.toSet())			
		def domainExpression = Joiner.on(" OR ").join(allowedDomainExpressions);
		
		final ExpressionBuilder expressionBuilder = Expressions.builder()
				.filter(SnomedRelationshipIndexEntry.Expressions.active())
				.filter(SnomedRelationshipIndexEntry.Expressions.typeId(typeExpression)) //Assuming single id attribute expressions
				.mustNot(SnomedRelationshipIndexEntry.Expressions.sourceIds(getCachedApplicableConcepts(domainExpression)))
		
		final Query<String> query = Query.select(String.class)
				.from(SnomedRelationshipIndexEntry.class)
				.fields(SnomedRelationshipIndexEntry.Fields.ID)
				.where(expressionBuilder.build())
				.limit(10_000)
				.scroll("2m")
				.build()
		
		searcher.scroll(query).forEach({ hits ->
			hits.forEach({ id ->
				println(id + " type yes source no")
				issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, id))
			})
		})
	}
	
	def allowedTypeIds = new HashSet(mrcmRulesByAttributeType.keySet())
	allowedTypeIds.add(Concepts.IS_A)
	
	final ExpressionBuilder expressionBuilder = Expressions.builder()
		.filter(SnomedRelationshipIndexEntry.Expressions.active())
		.mustNot(SnomedRelationshipIndexEntry.Expressions.typeIds(allowedTypeIds))
	
	final Query<String> query = Query.select(String.class)
		.from(SnomedRelationshipIndexEntry.class)
		.fields(SnomedRelationshipIndexEntry.Fields.ID)
		.where(expressionBuilder.build())
		.limit(10_000)
		.scroll("2m")
		.build()
	
	searcher.scroll(query).forEach({ hits ->
		hits.forEach({ id ->
			println(id + " type not found")
			issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, id))
		})
	})
}

return issues
