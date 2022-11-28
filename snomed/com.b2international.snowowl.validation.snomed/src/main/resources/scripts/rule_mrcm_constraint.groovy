package scripts 

import static com.b2international.index.query.Expressions.*;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.ALL_PRECOORDINATED_CONTENT;

import com.b2international.index.query.Expressions
import com.b2international.index.query.Query
import com.b2international.index.query.Expressions.ExpressionBuilder
import com.b2international.index.revision.RevisionSearcher
import com.b2international.snowowl.core.ComponentIdentifier
import com.b2international.snowowl.core.date.EffectiveTimes
import com.b2international.snowowl.core.ecl.EclParser
import com.b2international.snowowl.snomed.common.SnomedConstants
import com.b2international.snowowl.snomed.common.SnomedRf2Headers
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember
import com.b2international.snowowl.snomed.core.ecl.EclExpression
import com.b2international.snowowl.snomed.core.tree.Trees
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberCreateRequestBuilder
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests
import com.google.common.collect.Maps
import com.google.common.collect.Sets

RevisionSearcher searcher = ctx.service(RevisionSearcher.class);
Set<ComponentIdentifier> issues = Sets.newHashSet();

final String integerTypeRangePrefix = "int";
final String decimalTypeRangePrefix = "dec";

Map<String, String> allowedRanges = Maps.newHashMap();

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

final ExpressionBuilder mrcmRangeMemberQueryBuilder = Expressions.builder()
	.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
	.filter(SnomedRefSetMemberIndexEntry.Expressions.refsetIds(inScopeRefSets))
	.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes([SnomedRefSetType.MRCM_ATTRIBUTE_RANGE]))	

final Query<String[]> mrcmRangeMemberQuery = Query.select(String[].class)
	.from(SnomedRefSetMemberIndexEntry.class)
	.fields(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID,
			SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID,
			SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT)
	.where(mrcmRangeMemberQueryBuilder.build())
	.limit(Integer.MAX_VALUE)
	.build();

searcher.search(mrcmRangeMemberQuery).each { hit ->
	String contentType = hit[0];
	String typeId = hit[1];
	String rangeConstraint = hit[2];
	
	if (rangeConstraint.startsWith(integerTypeRangePrefix) || rangeConstraint.startsWith(decimalTypeRangePrefix)) {
		//Do nothing, skip concrete value type range validation for now
	} else {
		if (allowedRanges.containsKey(typeId)) {
			if (ALL_PRECOORDINATED_CONTENT.equals(contentType)) {
				allowedRanges.put(typeId, rangeConstraint);
			} else {
				//Do nothing, the already mapped member should be chosen over this
			}
		} else {
			allowedRanges.put(typeId, rangeConstraint);
		}
	}
}

EclParser eclParser = ctx.service(EclParser.class);

for (String typeId : allowedRanges.keySet()) {
	
	final String rangeConstraint = allowedRanges.get(typeId);	
	final String[] rangeClauses = rangeConstraint.split("OR");
	final Set<String> clauses = Sets.newHashSet();
	
	for (String rangeClause : rangeClauses) {
		String clause = rangeClause;
		
		if (clause.contains("|")) {
			clause = rangeClause.substring(0, rangeClause.indexOf('|')).strip();
		}
		
		try {
			eclParser.parse(clause);
		} catch (Exception e) {
			ctx.log().warn("Could not parse clause ${clause}");
			ctx.log().warn(e.getMessage());
			continue;
		}
		
		clauses.add(clause);
	}
	
	Set<String> destinationIds = Sets.newHashSet();
	//Populate possible destination ids from relationships
	ExpressionBuilder relevantRelationshipQueryBuilder = Expressions.builder()
		.filter(SnomedRelationshipIndexEntry.Expressions.active())
		.filter(SnomedRelationshipIndexEntry.Expressions.typeId(typeId));
	
	if (params.isUnpublishedOnly) {
		relevantRelationshipQueryBuilder.filter(SnomedRelationshipIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
	}
	
	final Query<String> relevantRelationshipQuery = Query.select(String.class)
		.from(SnomedRelationshipIndexEntry.class)
		.fields(SnomedRelationshipIndexEntry.Fields.DESTINATION_ID)
		.where(relevantRelationshipQueryBuilder.build())
		.limit(50_000)
		.build();
	
	searcher.stream(relevantRelationshipQuery).each { hits -> hits.forEach({ String destinationId -> destinationIds.add(destinationId)}) }
	
	//Populate possible destination ids from OWL axiom relationships
	final ExpressionBuilder relevantOwlMemberExpressionBuilder = Expressions.builder()
		.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
		.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes([SnomedRefSetType.OWL_AXIOM]))
		.filter(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionType([typeId]))
	
	if (params.isUnpublishedOnly) {
		relevantOwlMemberExpressionBuilder.filter(SnomedRefSetMemberIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME));
	};
	
	final Query<SnomedRefSetMemberIndexEntry> relevantOwlMemberQuery = Query.select(SnomedRefSetMemberIndexEntry.class)
		.fields(SnomedRefSetMemberIndexEntry.Fields.CLASS_AXIOM_RELATIONSHIP,
			SnomedRefSetMemberIndexEntry.Fields.GCI_AXIOM_RELATIONSHIP,
			SnomedRefSetMemberIndexEntry.Fields.ID)
		.where(relevantOwlMemberExpressionBuilder.build())
		.limit(50_000)
		.build();
	
	searcher.stream(relevantOwlMemberQuery).forEach({ axioms -> 
		axioms.forEach({SnomedRefSetMemberIndexEntry axiom -> 
			axiom.getGciAxiomRelationships()?.forEach({ SnomedOWLRelationshipDocument relationship -> 
				if (relationship.getTypeId().equals(typeId)) {
					destinationIds.add(relationship.getDestinationId());					
				}
			});
			axiom.getClassAxiomRelationships()?.forEach({ SnomedOWLRelationshipDocument relationship -> 
				if (relationship.getTypeId().equals(typeId)) {
					destinationIds.add(relationship.getDestinationId());
				}
			});
		})
	});
	
	Set<String> conceptIds = Sets.newHashSet();
	Set<String> ancestorIds = Sets.newHashSet();
	Set<String> parentIds = Sets.newHashSet();
	Set<String> refsetIds = Sets.newHashSet();
	
	for (String clause : clauses) {
		if (clause.startsWith("<<")) {
			String ancestorId = clause.replaceAll("<<", "").strip();
			ancestorIds.add(ancestorId);
			conceptIds.add(ancestorId);
		} else if (clause.startsWith("<!")) {
			String parentId = clause.replaceAll("<!", "").strip();
			parentIds.add(parentId);
		} else if (clause.startsWith("<")) {
			String ancestorId = clause.replaceAll("<", "").strip();
			ancestorIds.add(ancestorId);
		} else if (clause.startsWith("^")) {
			String refsetId = clause.replaceAll("^", "").strip();
			refsetIds.add(refsetId);
		} else {
			conceptIds.add(clause.strip());
		}
	}
	
	destinationIds.removeAll(conceptIds);
	
	ExpressionBuilder destinationQueryBuilder = Expressions.builder()
		.filter(SnomedConceptDocument.Expressions.active())
		.filter(SnomedConceptDocument.Expressions.ids(destinationIds));
	
	if (!ancestorIds.isEmpty()) {
		destinationQueryBuilder
			.mustNot(SnomedConceptDocument.Expressions.ancestors(ancestorIds))
			.mustNot(SnomedConceptDocument.Expressions.parents(ancestorIds));
	}
	
	if (!parentIds.isEmpty()) {
		destinationQueryBuilder.mustNot(SnomedConceptDocument.Expressions.parents(parentIds));
	}
	
	if (!refsetIds.isEmpty()) {
		destinationQueryBuilder.mustNot(SnomedConceptDocument.Expressions.activeMemberOf(refsetIds));
	}
	
	final Query<String> destinationQuery = Query.select(String.class)
		.from(SnomedConceptDocument.class)
		.fields(SnomedConceptDocument.Fields.ID)
		.where(destinationQueryBuilder.build())
		.limit(50_000)
		.build();
	
	Set<String> incorrectDestinationIds = Sets.newHashSet();
	searcher.stream(destinationQuery).each { hits -> hits.forEach({ String id -> incorrectDestinationIds.add(id)}) }
		
	if (incorrectDestinationIds.isEmpty()) {
		continue;
	}
	
	//Find relationships that have destinations out of the allowed range
	ExpressionBuilder relationshipQueryBuilder = Expressions.builder()
		.filter(SnomedRelationshipIndexEntry.Expressions.active())
		.filter(SnomedRelationshipIndexEntry.Expressions.typeId(typeId))
		.filter(SnomedRelationshipIndexEntry.Expressions.destinationIds(incorrectDestinationIds));
	
	if (params.isUnpublishedOnly) {
		relationshipQueryBuilder.filter(SnomedRelationshipIndexEntry.Expressions.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME))
	}
		
	final Query<String> relationshipQuery = Query.select(String.class)
		.from(SnomedRelationshipIndexEntry.class)
		.fields(SnomedRelationshipIndexEntry.Fields.ID)
		.where(relationshipQueryBuilder.build())
		.limit(50_000)
		.build();
		
	searcher.stream(relationshipQuery).each { hits ->
		hits.forEach({ String relationshipId ->
			issues.add(ComponentIdentifier.of(SnomedRelationship.TYPE, relationshipId));
		})
	}
	
	//Find OWL axiom members with relationships that have destinations out of the allowed range
	final ExpressionBuilder owlMemberExpressionBuilder = Expressions.builder()
		.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
		.filter(SnomedRefSetMemberIndexEntry.Expressions.refSetTypes([SnomedRefSetType.OWL_AXIOM]))
		.filter(Expressions.builder()
			.should(nestedMatch("classAxiomRelationships", Expressions.builder()
				.filter(matchAny("typeId", [typeId]))
				.filter(matchAny("destinationId", incorrectDestinationIds))
				.build()))
			.should(nestedMatch("gciAxiomRelationships", Expressions.builder()
				.filter(matchAny("typeId", [typeId]))
				.filter(matchAny("destinationId", incorrectDestinationIds))
				.build()))
			.build()
			);
	
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
		hits.forEach({ id ->
			issues.add(ComponentIdentifier.of(SnomedReferenceSetMember.TYPE, id))
		})
	})
}

return issues as List
