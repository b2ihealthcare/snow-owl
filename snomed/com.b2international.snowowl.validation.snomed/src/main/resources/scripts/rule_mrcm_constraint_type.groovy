package scripts

import java.util.stream.Collectors

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
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember
import com.b2international.snowowl.snomed.core.ecl.EclExpression
import com.b2international.snowowl.snomed.core.tree.Trees
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipSearchRequestBuilder
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.base.Joiner
import com.google.common.base.Strings
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Lists
import com.google.common.collect.Multimap
import com.google.common.collect.Sets

RevisionSearcher searcher = ctx.service(RevisionSearcher.class)
List<ComponentIdentifier> issues = Lists.newArrayList()

def getPredicate = { SnomedConstraint constraint ->
	return constraint.getPredicate() instanceof SnomedCardinalityPredicate
			? ((SnomedCardinalityPredicate) constraint.getPredicate()).getPredicate()
			: constraint.getPredicate()
}

def mrcmRules = SnomedRequests.prepareSearchConstraint()
		.all()
		.build()
		.execute(ctx)
		.stream()
		.filter({SnomedConstraint constraint -> constraint.getPredicate() instanceof SnomedCardinalityPredicate
			? ((SnomedCardinalityPredicate) constraint.getPredicate()).getPredicate() instanceof SnomedRelationshipPredicate
			: constraint.getPredicate() instanceof SnomedRelationshipPredicate})
		.collect();

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

def typeMultimapBuilder = ImmutableMultimap.builder()
for (SnomedConstraint constraint : mrcmRules) {
	final SnomedRelationshipPredicate predicate = constraint.getPredicate() instanceof SnomedCardinalityPredicate
			? ((SnomedCardinalityPredicate) constraint.getPredicate()).getPredicate()
			: constraint.getPredicate()
	
	final String attributeExpression = predicate.getAttributeExpression()	
	if (!Concepts.IS_A.equals(attributeExpression)) {		
		getApplicableConcepts(attributeExpression).forEach({		
			typeMultimapBuilder.put(it, constraint)		
		})
	}
}
Multimap<String, SnomedConstraint> mrcmRulesByAttributeType = typeMultimapBuilder.build()

def getCachedApplicableConcepts = { String conceptSetExpression ->
	return getApplicableConcepts(conceptSetExpression)
}.memoize()

def getApplicableRules = { String conceptId, String typeId, boolean checkCharacteristicType ->
	final Set<SnomedConstraint> applicableConstraints = Sets.newHashSet()
	
	if (mrcmRulesByAttributeType.keySet().contains(typeId)) {
		for (SnomedConstraint constraint : mrcmRulesByAttributeType.get(typeId)) {
			if (getCachedApplicableConcepts(constraint.getDomain().toEcl()).contains(conceptId)) {
				if (checkCharacteristicType) {
					SnomedRelationshipPredicate predicate = getPredicate(constraint)
					String charType = predicate.getCharacteristicTypeId()
					if (Concepts.STATED_RELATIONSHIP.equals(charType) || Strings.isNullOrEmpty(charType)) {
						applicableConstraints.add(constraint)
					}
				} else {
					applicableConstraints.add(constraint)
				}
			}
		}
	}
	
	return applicableConstraints
}

def getOWLRelationships = { SnomedReferenceSetMember owlMember ->
	def owlRelationships = [] as List
	if (owlMember.getClassOWLRelationships() != null) {
		owlRelationships.addAll(owlMember.getClassOWLRelationships());
	} else if (owlMember.getEquivalentOWLRelationships() != null) {
		owlRelationships.addAll(owlMember.getEquivalentOWLRelationships());
	}
	return owlRelationships
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
		
		if (!typeId.equals(Concepts.IS_A) && getApplicableRules(sourceId, typeId, false).isEmpty()) {
			issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipId))			
		}
	}
	
	def owlAxiomMembers = SnomedRequests.prepareSearchMember()
		.filterByRefSet(Concepts.REFSET_OWL_AXIOM)
		.filterByActive(true)
		.filterByEffectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
		.all()
		.setExpand("owlRelationships()")
		.build()
		.execute(ctx)
	
	for (SnomedReferenceSetMember owlAxiomMember : owlAxiomMembers) {
		def sourceId = owlAxiomMember.getReferencedComponent().getId()
		for (SnomedOWLRelationshipDocument relationship : getOWLRelationships(owlAxiomMember)) {
			def typeId = relationship.getTypeId()
			if (!typeId.equals(Concepts.IS_A) && getApplicableRules(sourceId, typeId, true).isEmpty()) {
				issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, owlAxiomMember.getId()))
			}
		}
	}
	
} else {
	//Relationship search based checks
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
			issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, id))
		})
	})
	
	//OWL axiom member search based checks
	for (String typeExpression : mrcmRulesByAttributeType.keySet()) {
		def allowedDomainExpressions = mrcmRulesByAttributeType.get(typeExpression)
			.stream()
			.filter({ SnomedConstraint constraint -> 
				SnomedRelationshipPredicate predicate = getPredicate(constraint)
				String charType = predicate.getCharacteristicTypeId()
				return Strings.isNullOrEmpty(charType) || Concepts.STATED_RELATIONSHIP.equals(charType)})
			.map({SnomedConstraint constraint -> constraint.getDomain().toEcl()})
			.collect(Collectors.toSet())
		
		if (allowedDomainExpressions.isEmpty()) {
			continue
		}
		
		def domainExpression = Joiner.on(" OR ").join(allowedDomainExpressions);
		
		final ExpressionBuilder owlMemberExpressionBuilder = Expressions.builder()
				.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
				.filter(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionType(Collections.singleton(typeExpression)))
				.mustNot(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(getCachedApplicableConcepts(domainExpression)))
		
		final Query<String> owlMemberQuery = Query.select(String.class)
				.from(SnomedRefSetMemberIndexEntry.class)
				.fields(SnomedRefSetMemberIndexEntry.Fields.ID)
				.where(owlMemberExpressionBuilder.build())
				.limit(10_000)
				.scroll("2m")
				.build()
		
		searcher.scroll(owlMemberQuery).forEach({ hits ->
			hits.forEach({ id ->
				issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, id))
			})
		})
	}
	
	def allowedTypeIdsForOwlRelationships = mrcmRulesByAttributeType.keySet()
		.stream()
		.filter({ String typeId ->
			mrcmRulesByAttributeType.get(typeId).stream()
				.map({getPredicate(it)})
				.filter({SnomedRelationshipPredicate predicate ->
					String charType = predicate.getCharacteristicTypeId()
					return Concepts.STATED_RELATIONSHIP.equals(charType) || Strings.isNullOrEmpty(charType)
				}).findAny().isPresent()
		}).collect(Collectors.toSet())
		
	allowedTypeIdsForOwlRelationships.add(Concepts.IS_A)
	
	final ExpressionBuilder owlMemberExpressionBuilder = Expressions.builder()
		.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
		.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes([SnomedRefSetType.OWL_AXIOM]))
		.mustNot(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionType(allowedTypeIdsForOwlRelationships))
	
	final Query<String> owlMemberQuery = Query.select(String.class)
		.from(SnomedRefSetMemberIndexEntry.class)
		.fields(SnomedRefSetMemberIndexEntry.Fields.ID)
		.where(owlMemberExpressionBuilder.build())
		.limit(10_000)
		.scroll("2m")
		.build()
	
	searcher.scroll(owlMemberQuery).forEach({ hits ->
		hits.forEach({ id ->
			issues.add(ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, id))
		})
	})
}

return issues
