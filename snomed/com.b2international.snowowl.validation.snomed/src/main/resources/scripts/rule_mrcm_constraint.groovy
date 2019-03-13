package scripts

import com.b2international.index.Hits
import com.b2international.index.query.Expression
import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedCardinalityPredicate
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConceptSetDefinition
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedRelationshipPredicate
import com.b2international.snowowl.snomed.core.ecl.EclExpression
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipSearchRequestBuilder
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.base.Strings
import com.google.common.collect.Lists
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
		
def getApplicableConcepts = { SnomedConceptSetDefinition conceptSetDefinition ->
	def conceptSetExpression = conceptSetDefinition.toEcl()
	def expression = Expressions.builder()
		.filter(EclExpression.of(conceptSetExpression).resolveToExpression(ctx).getSync())
		.build()
	
	Query<String[]> conceptSetQuery = Query.select(String[].class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(expression)
		.limit(Integer.MAX_VALUE)
		.build()
	
	Set<String> conceptIds = Sets.newHashSet()
	searcher.search(conceptSetQuery)
		.each({ids -> conceptIds.addAll(ids)})
	return conceptIds	
}.memoize()
		
def getApplicableRules = { String conceptId ->
	final Set<SnomedConstraint> applicableConstraints = Sets.newHashSet()
	for (SnomedConstraint constraint: mrcmRules) {
		
		SnomedConceptSetDefinition domain = constraint.domain
		if (getApplicableConcepts(domain).contains(conceptId)) {
			applicableConstraints.add(constraint)
		}
	}
	
	return applicableConstraints
}.memoize()

SnomedRelationshipSearchRequestBuilder  requestBuilder = SnomedRequests.prepareSearchRelationship()
		.filterByActive(true)
		.all()
		.setFields(SnomedRelationshipIndexEntry.Fields.SOURCE_ID,
			SnomedRelationshipIndexEntry.Fields.TYPE_ID,
			SnomedRelationshipIndexEntry.Fields.DESTINATION_ID,
			SnomedRelationshipIndexEntry.Fields.ID)

if (params.isUnpublishedOnly) {
	requestBuilder.filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
}
		
Expression where = requestBuilder.build().prepareQuery(ctx);

final Query<String[]> relationshipQuery = Query.select(String[].class)
	.from(SnomedRelationshipIndexEntry.class)
	.fields(SnomedRelationshipIndexEntry.Fields.SOURCE_ID,
		SnomedRelationshipIndexEntry.Fields.TYPE_ID,
		SnomedRelationshipIndexEntry.Fields.DESTINATION_ID,
		SnomedRelationshipIndexEntry.Fields.ID,
		SnomedRelationshipIndexEntry.Fields.CHARACTERISTIC_TYPE_ID)
	.where(where)
	.limit(Integer.MAX_VALUE)
	.build();

Iterable<Hits<String[]>> queryResult = ctx.service(RevisionSearcher.class).search(relationshipQuery);

queryResult.each { hit ->
	def sourceId = hit[0]
	def typeId = hit[1]
	def destinationId = hit[2]
	def relationshipId = hit[3]
	def charTypeId = hit[4]
	
	def applicableRules = getApplicableRules(sourceId)
	
	for (SnomedConstraint constraint : applicableRules) {
		SnomedRelationshipPredicate predicate
		if (constraint.getPredicate() instanceof SnomedCardinalityPredicate) {
			predicate = ((SnomedCardinalityPredicate) constraint.getPredicate()).getPredicate()
		} else {
			predicate = constraint.getPredicate()
		}

		if (predicate.getAttributeExpression().equals(typeId)) {
			 if (getApplicableConcepts(predicate.getRange()).contains(destinationId)) {
				 def predicateCharType = predicate.getCharacteristicTypeId()
				 if (!Strings.isNullOrEmpty(predicateCharType) && !charTypeId.equals(predicateCharType)) {
					 issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipId))
				 }
			 } else {
				 issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipId))				 
			 }
		}
	}
}

return issues