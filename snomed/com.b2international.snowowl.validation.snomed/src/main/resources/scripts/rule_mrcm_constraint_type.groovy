package scripts

import java.util.concurrent.TimeUnit

import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.snomed.common.SnomedRf2Headers
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember
import com.b2international.snowowl.snomed.core.ecl.EclExpression
import com.b2international.snowowl.snomed.core.tree.Trees
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.base.Joiner
import com.google.common.base.Strings
import com.google.common.collect.HashMultimap
import com.google.common.collect.Maps
import com.google.common.collect.Multimap
import com.google.common.collect.Sets

import groovy.transform.Field

RevisionSearcher searcher = ctx.service(RevisionSearcher.class);
Set<ComponentIdentifier> issues = Sets.newHashSet();
Set<ComponentIdentifier> potentialIssues = Sets.newHashSet();

List<String> moduleIds = SnomedRequests.prepareSearchConcept()
	.filterByEcl(params.modules)
	.filterByActive(true)
	.all()
	.build()
	.execute(ctx)
	.collect({it.getId()})

List<String> inScopeRefSets = SnomedRequests.prepareSearchMember()
	.all()
	.filterByActive(true)
	.filterByRefSet(Concepts.REFSET_MRCM_MODULE_SCOPE)
	.filterByReferencedComponent(moduleIds)
	.build()
	.execute(ctx)
	.collect { SnomedReferenceSetMember m -> m.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID)}

def getApplicableConcepts = { String conceptSetExpression ->
	def expression = EclExpression.of(conceptSetExpression, Trees.INFERRED_FORM).resolveToExpression(ctx).getSync(1, TimeUnit.MINUTES);
	
	Query<String> conceptSetQuery = Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(expression)
		.limit(Integer.MAX_VALUE)
		.build();
	
	Set<String> conceptIds = Sets.newHashSet();
	searcher.search(conceptSetQuery).each({id -> conceptIds.add(id)})
	return conceptIds;
}

@Field
Map<String, String> parentDomains = Maps.newHashMap();
@Field
Multimap<String, String> allowedTypeIds = new HashMultimap();
Multimap<String, String> childDomains = new HashMultimap();
Map<String, SnomedReferenceSetMember> domainMembers = Maps.newHashMap();

SnomedRequests.prepareSearchMember()
	.filterByActive(true)
	.filterByRefSet(inScopeRefSets)
	.filterByRefSetType(SnomedRefSetType.MRCM_DOMAIN)
	.all()
	.setFields(SnomedRefSetMemberIndexEntry.Fields.ID,
		SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID,
		SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT,
		SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN)
	.build()
	.execute(ctx)
	.forEach({ SnomedReferenceSetMember member ->
		final String parentDomain = member.getProperties().get(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN);
		if (!Strings.isNullOrEmpty(parentDomain)) {
			int lastChar = parentDomain.contains('|') ? parentDomain.indexOf('|') : parentDomain.length();
			String parentDomainId = parentDomain.substring(0, lastChar).trim();
			parentDomains.put(member.getReferencedComponentId(), parentDomainId);
			childDomains.put(parentDomainId, member.getReferencedComponentId());				
		}
		domainMembers.put(member.getReferencedComponentId(), member);	
	});

domainMembers.keySet().forEach({ String domainId ->
	final Query<String> attributeQuery = Query.select(String.class)
		.from(SnomedRefSetMemberIndexEntry.class)
		.fields(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID)
		.where(Expressions.builder()
			.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
			.filter(SnomedRefSetMemberIndexEntry.Expressions.refsetIds(inScopeRefSets))
			.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes([SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN]))
			.filter(SnomedRefSetMemberIndexEntry.Expressions.domainIds(Collections.singleton(domainId)))
			.build())
		.limit(Integer.MAX_VALUE)
		.build();
	
	Set<String> attributes = searcher.search(attributeQuery).collect() as Set;
	allowedTypeIds.putAll(domainId, attributes);
})

def getConstraint = { String domain ->
	final SnomedReferenceSetMember member = domainMembers.get(domain);
	final String domainConstraint = member.getProperties().get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT);
}

def getDomainConstraint = { String domain ->
	final String domainConstraint = getConstraint(domain);
	if (childDomains.containsKey(domain)) {
		List<String> childDomainConstraints = childDomains.get(domain).collect({ String domainId -> "(${getConstraint(domainId)})" });
		String openBrace = childDomainConstraints.size() == 1 ? "" : "(";
		String closeBrace = childDomainConstraints.size() == 1 ? "" : ")";
		return "${domainConstraint} MINUS (${Joiner.on(" OR ").join(childDomainConstraints)})"
	}
	return domainConstraint;
}

def getAllowedTypeIds (String domain) {
	final Set<String> typeIds = allowedTypeIds.get(domain);
	
	if (parentDomains.containsKey(domain)) {
		final String parentDomain = parentDomains.get(domain);		
		typeIds.addAll(getAllowedTypeIds(parentDomain));
	}
	
	return typeIds;
}

Set<String> reportedRelationshipIds;
def searchRelationships = { boolean isValidationRun ->
	domainMembers.keySet().forEach({ String domain ->
		final SnomedReferenceSetMember domainMember = domainMembers.get(domain); 
		
		final String domainConstraint = getDomainConstraint(domain);
		final Set<String> domainConcepts = getApplicableConcepts(domainConstraint);
		final Set<String> attributes = getAllowedTypeIds(domain);
		attributes.add(Concepts.IS_A);
		
		//Find relationships that have no applicable MRCM rules for their types
		ExpressionBuilder relationshipQueryBuilder = Expressions.builder()
				.filter(SnomedRelationshipIndexEntry.Expressions.active())
				.filter(SnomedRelationshipIndexEntry.Expressions.sourceIds(domainConcepts));

		if (isValidationRun) {
			relationshipQueryBuilder
				.filter(SnomedRelationshipIndexEntry.Expressions.typeIds(attributes))
				.filter(SnomedRelationshipIndexEntry.Expressions.ids(reportedRelationshipIds));

		} else {
			relationshipQueryBuilder
				.mustNot(SnomedRelationshipIndexEntry.Expressions.typeIds(attributes));
		}
		
		if (params.isUnpublishedOnly) {
			relationshipQueryBuilder
				.filter(SnomedRelationshipIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
		}
		
		final Query<String> relationshipQuery = Query.select(String.class)
			.from(SnomedRelationshipIndexEntry.class)
			.fields(SnomedRelationshipIndexEntry.Fields.ID)
			.where(relationshipQueryBuilder.build())
			.limit(50_000)
			.build();
		
		searcher.stream(relationshipQuery).each { hits ->
			hits.forEach({ String relationshipId ->
				if (isValidationRun) {
					issues.remove(ComponentIdentifier.of(SnomedRelationship.TYPE, relationshipId));
				} else {
					issues.add(ComponentIdentifier.of(SnomedRelationship.TYPE, relationshipId));
				}
			})
		}
		
		//Find OWL axiom members with relationships that have no applicable MRCM rules for their types
		final ExpressionBuilder owlMemberExpressionBuilder = Expressions.builder()
			.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
			.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes([SnomedRefSetType.OWL_AXIOM]))
			.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(domainConcepts));

		if (isValidationRun) {
			owlMemberExpressionBuilder
				.filter(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionType(attributes))
				.filter(SnomedRefSetMemberIndexEntry.Expressions.ids(reportedRelationshipIds));
		} else {
			owlMemberExpressionBuilder
				.mustNot(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionType(attributes));
		}

		if (params.isUnpublishedOnly) {
			owlMemberExpressionBuilder
				.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
		}
		
		final Query<String> owlMemberQuery = Query.select(String.class)
			.from(SnomedRefSetMemberIndexEntry.class)
			.fields(SnomedRefSetMemberIndexEntry.Fields.ID)
			.where(owlMemberExpressionBuilder.build())
			.limit(50_000)
			.build();
		
		searcher.stream(owlMemberQuery).forEach({ hits ->
			hits.forEach({ id ->
				if (isValidationRun) {
					issues.remove(ComponentIdentifier.of(SnomedReferenceSetMember.TYPE, id))
				} else {
					issues.add(ComponentIdentifier.of(SnomedReferenceSetMember.TYPE, id))
				}
			})
		})
	})
}

def searchRelationshipsWithUnregulatedTypeIds =  {
	Set<String> typeIdsInMrcmRules = Sets.newHashSet(allowedTypeIds.values());
	typeIdsInMrcmRules.add(Concepts.IS_A);
	
	//Find relationships that have no MRCM rules with this type
	ExpressionBuilder relationshipQueryBuilder = Expressions.builder()
			.filter(SnomedRelationshipIndexEntry.Expressions.active())
			.mustNot(SnomedRelationshipIndexEntry.Expressions.typeIds(typeIdsInMrcmRules));
	
	if (params.isUnpublishedOnly) {
		relationshipQueryBuilder.filter(SnomedRelationshipIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
	}
	
	final Query<String> relationshipQuery = Query.select(String.class)
		.from(SnomedRelationshipIndexEntry.class)
		.fields(SnomedRelationshipIndexEntry.Fields.ID)
		.where(relationshipQueryBuilder.build())
		.limit(50_000)
		.build();
	
	searcher.stream(relationshipQuery).each { hits -> hits.each { id -> 
		issues.add(ComponentIdentifier.of(SnomedRelationship.TYPE, id))
	}}
	
	//Find OWL Axiom relationships that have no MRCM rules with this type
	final ExpressionBuilder owlMemberExpressionBuilder = Expressions.builder()
		.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
		.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes([SnomedRefSetType.OWL_AXIOM]))
		.mustNot(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionType(typeIdsInMrcmRules));
	
	if (params.isUnpublishedOnly) {
		owlMemberExpressionBuilder.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
	}
	
	final Query<String> owlMemberQuery = Query.select(String.class)
		.from(SnomedRefSetMemberIndexEntry.class)
		.fields(SnomedRefSetMemberIndexEntry.Fields.ID)
		.where(owlMemberExpressionBuilder.build())
		.limit(50_000)
		.build();
	
	searcher.stream(owlMemberQuery).forEach({ hits ->
		hits.each { hit ->
			def id = hit[0];
			def owlExpression = hit[1];
			if (!owlExpression.contains("TransitiveObjectProperty") &&
				!owlExpression.contains("ReflexiveObjectProperty") &&
				!owlExpression.contains("SubDataPropertyOf") &&
				!owlExpression.contains("SubObjectPropertyOf") &&
				!owlExpression.contains("ObjectPropertyChain")) {
				//Skip axiom member with no generated axiom relationships
				issues.add(ComponentIdentifier.of(SnomedReferenceSetMember.TYPE, id))
			}
		}
	});
}

//On the first run find potential MRCM type violations
searchRelationships(false);
//Collect the relationship/owl axiom member ids of the potential violations
reportedRelationshipIds = issues.collect{ ComponentIdentifier identifier -> identifier.getComponentId()} as Set
//Rerun validation against these ids to double check if a different rules allows for these relationships (useful for concepts with ancestors in multiple hierarchies) 
searchRelationships(true);
//Find all concepts whith type ids not accounted for in MRCM rules, irrespective of what domain they are in
searchRelationshipsWithUnregulatedTypeIds();

return issues as List;
