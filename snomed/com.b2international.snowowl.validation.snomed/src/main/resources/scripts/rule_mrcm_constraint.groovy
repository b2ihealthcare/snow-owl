package scripts

import groovy.transform.Immutable

import com.b2international.index.Hits
import com.b2international.index.query.Expression
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedCardinalityPredicate
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConceptSetDefinition
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedRelationshipPredicate
import com.b2international.snowowl.snomed.core.ecl.EclExpression
import com.b2international.snowowl.snomed.core.tree.Trees
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipSearchRequestBuilder
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.base.Strings
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Lists
import com.google.common.collect.Multimap
import com.google.common.collect.Sets

RevisionSearcher searcher = ctx.service(RevisionSearcher.class)
List<ComponentIdentifier> issues = Lists.newArrayList()
String oneMandatoryIsAPerConceptRuleId = "b93819e3-2679-46f6-a35d-3a749842d83e"

def mrcmRules = SnomedRequests.prepareSearchConstraint()
		.all()
		.build()
		.execute(ctx)
		.stream()
		.filter({SnomedConstraint constraint -> constraint.getPredicate() instanceof SnomedCardinalityPredicate
			? ((SnomedCardinalityPredicate) constraint.getPredicate()).getPredicate() instanceof SnomedRelationshipPredicate
			: constraint.getPredicate() instanceof SnomedRelationshipPredicate})
		.filter({SnomedConstraint constraint -> !constraint.getId().equals(oneMandatoryIsAPerConceptRuleId)})
		.collect();
		
def multimapBuilder = ImmutableMultimap.builder()
for (SnomedConstraint constraint : mrcmRules) {
	multimapBuilder.put(constraint.getDomain().toEcl(), constraint)	
}

Multimap<String, SnomedConstraint> mrcmRulesByDomain = multimapBuilder.build()

def getApplicableConcepts = { String conceptSetExpression ->
	def expression = Expressions.builder()
		.filter(EclExpression.of(conceptSetExpression, Trees.INFERRED_FORM).resolveToExpression(ctx).getSync())
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
		
def getApplicableRules = { String conceptId ->
	final Set<SnomedConstraint> applicableConstraints = Sets.newHashSet()
	for (SnomedConstraint constraint: mrcmRules) {
		
		SnomedConceptSetDefinition domain = constraint.domain
		if (getCachedApplicableConcepts(domain.toEcl()).contains(conceptId)) {
			applicableConstraints.add(constraint)
		}
	}
	
	return applicableConstraints
}.memoize()

if (params.isUnpublishedOnly) {
	SnomedRelationshipSearchRequestBuilder  requestBuilder = SnomedRequests.prepareSearchRelationship()
			.filterByActive(true)
			.filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
			.all()

	Expression where = requestBuilder.build().prepareQuery(ctx);

	final Query<String[]> relationshipQuery = Query.select(String[].class)
	.from(SnomedRelationshipIndexEntry.class)
	.fields(SnomedRelationshipIndexEntry.Fields.ID,
		SnomedRelationshipIndexEntry.Fields.SOURCE_ID,
		SnomedRelationshipIndexEntry.Fields.DESTINATION_ID,
		SnomedRelationshipIndexEntry.Fields.TYPE_ID,
		SnomedRelationshipIndexEntry.Fields.CHARACTERISTIC_TYPE_ID)
	.where(where)
	.limit(Integer.MAX_VALUE)
	.build();

	Iterable<Hits<String[]>> queryResult = ctx.service(RevisionSearcher.class).search(relationshipQuery);

	queryResult.each { hit ->
		def relationshipId = hit[0]
		def sourceId = hit[1]
		def destinationId = hit[2]
		def typeId = hit[3]
		def charTypeId = hit[4]

		def applicableRules = getApplicableRules(sourceId)

		for (SnomedConstraint constraint : applicableRules) {
			SnomedRelationshipPredicate predicate
			if (constraint.getPredicate() instanceof SnomedCardinalityPredicate) {
				predicate = ((SnomedCardinalityPredicate) constraint.getPredicate()).getPredicate()
			} else {
				predicate = constraint.getPredicate()
			}

			def predicateCharType = predicate.getCharacteristicTypeId()
			if (predicate.getAttributeExpression().equals(typeId) && (Strings.isNullOrEmpty(predicateCharType) || charTypeId.equals(predicateCharType))) {
				if (!getCachedApplicableConcepts(predicate.getRangeExpression()).contains(destinationId)) {
					issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipId))
				}
			}
		}
	}
} else {
	for (String domainExpression : mrcmRulesByDomain.keySet()) {
		Set<String> domain = getApplicableConcepts(domainExpression)
		
		for (SnomedConstraint constraint : mrcmRulesByDomain.get(domainExpression)) {
			SnomedRelationshipPredicate predicate = constraint.getPredicate() instanceof SnomedCardinalityPredicate
					? ((SnomedCardinalityPredicate) constraint.getPredicate()).getPredicate()
					: constraint.getPredicate()

			final String attributeExpression = predicate.getAttributeExpression()
			final String rangeExpression = predicate.getRangeExpression()
			final String charType = predicate.getCharacteristicTypeId()

			final ExpressionBuilder expressionBuilder = Expressions.builder()
					.filter(SnomedRelationshipIndexEntry.Expressions.active())
					.filter(SnomedRelationshipIndexEntry.Expressions.sourceIds(domain))
					.filter(SnomedRelationshipIndexEntry.Expressions.typeId(attributeExpression))
					.mustNot(SnomedRelationshipIndexEntry.Expressions.destinationIds(getCachedApplicableConcepts(rangeExpression)))

			if (!Strings.isNullOrEmpty(charType)) {
				expressionBuilder.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeId(charType))
			}

			final Query<String> query = Query.select(String.class)
					.from(SnomedRelationshipIndexEntry.class)
					.fields(SnomedRelationshipIndexEntry.Fields.ID)
					.where(expressionBuilder.build())
					.limit(10000)
					.build()

			searcher.scroll(query).forEach({ hits ->
				hits.forEach({ id ->
					issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, id))
				})
			})
		}
	}
}

return issues
