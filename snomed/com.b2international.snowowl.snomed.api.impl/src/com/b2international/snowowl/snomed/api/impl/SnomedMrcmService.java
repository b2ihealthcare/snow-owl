package com.b2international.snowowl.snomed.api.impl;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.impl.domain.Predicate;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.SnomedPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTaxonomyService;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry.PredicateType;

public class SnomedMrcmService {
	
	@Resource
	private IEventBus bus;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	public List<Predicate> getPredicates(String conceptId) {
		IBranchPath mainPath = BranchPathUtils.createMainPath();
		Collection<PredicateIndexEntry> indexEntries = getPredicateBrowser().getPredicates(mainPath, conceptId, null);
		List<Predicate> predicates = new ArrayList<>();
		for (PredicateIndexEntry predicateIndexEntry : indexEntries) {
			PredicateType type = predicateIndexEntry.getType();
			if (type == PredicateType.RELATIONSHIP) {
				Predicate predicate = new Predicate();
				predicate.setType(type);
				predicate.setRelationshipTypeExpression(predicateIndexEntry.getRelationshipTypeExpression());
				predicate.setRelationshipValueExpression(predicateIndexEntry.getRelationshipValueExpression());
				predicates.add(predicate);
			}
		}
		return predicates;
	}

	public SnomedConcepts getDomainAttributes(String branchPath, List<String> parentIds, 
			int offset, int limit, final List<ExtendedLocale> locales, final String expand) {
		
		StringBuilder builder = new StringBuilder();
		if (!parentIds.isEmpty()) {
			Collection<PredicateIndexEntry> predicates = getPredicateBrowser().getPredicates(getBranch(branchPath), parentIds, null);
			Set<String> typeExpressions = new HashSet<>();
			for (PredicateIndexEntry predicateIndexEntry : predicates) {
				if (predicateIndexEntry.getType() == PredicateType.RELATIONSHIP) {
					typeExpressions.add(predicateIndexEntry.getRelationshipTypeExpression());
				}
			}
			if (typeExpressions.isEmpty()) {
				return new SnomedConcepts(offset, limit, 0);
			}
			for (String typeExpression : typeExpressions) {
				if (builder.length() > 0) {
					builder.append(" UNION ");
				}
				builder.append(typeExpression);
			}
		} else {
			builder.append(Concepts.IS_A);
		}
		
		return SnomedRequests
			.prepareSearchConcept()
			.setLimit(limit)
			.setOffset(offset)
			.filterByEscg(builder.toString())
			.filterByActive(true)
			.setExpand(expand)
			.setLocales(locales)
			.build(branchPath)
			.executeSync(bus);
	}

	public SnomedConcepts getAttributeValues(String branchPath, String attributeId, String termPrefix, 
			int offset, int limit, List<ExtendedLocale> locales, String expand) {
		
		IBranchPath branch = getBranch(branchPath);
		final Collection<String> ancestorIds = getServiceForClass(SnomedTaxonomyService.class).getAllSupertypes(branch, attributeId);
		
		String relationshipValueExpression = null;
		String relationshipTypeExpression = null;
		Collection<PredicateIndexEntry> predicates = getPredicateBrowser().getAllPredicates(getBranch(branchPath));
		for (PredicateIndexEntry predicateIndexEntry : predicates) {
			if (predicateIndexEntry.getType() == PredicateType.RELATIONSHIP) {
				relationshipTypeExpression = predicateIndexEntry.getRelationshipTypeExpression();
				if (relationshipTypeExpression.startsWith("<")) {
					String relationshipTypeId = relationshipTypeExpression.replace("<", "");
					if ((relationshipTypeExpression.startsWith("<<") && 
							(relationshipTypeId.equals(attributeId) || ancestorIds.contains(relationshipTypeId)))
							|| ancestorIds.contains(relationshipTypeId)) {
						relationshipValueExpression = predicateIndexEntry.getRelationshipValueExpression();
						break;
					}
				} else if (relationshipTypeExpression.equals(attributeId)) {
					relationshipValueExpression = predicateIndexEntry.getRelationshipValueExpression();
					break;
				}
			}
		}
		if (relationshipValueExpression == null) {
			logger.error("No MRCM predicate found for attribute {}", attributeId);
			throw new ComponentNotFoundException("MRCM predicate for attribute", attributeId);
		}
		logger.info("Matched attribute predicate for attribute {}, type expression '{}', value expression '{}'", attributeId, relationshipTypeExpression, relationshipValueExpression);
		
		return SnomedRequests
				.prepareSearchConcept()
				.setLimit(limit)
				.setOffset(offset)
				.filterByEscg(relationshipValueExpression)
				.filterByTerm(termPrefix)
				.filterByActive(true)
				.setExpand(expand)
				.setLocales(locales)
				.build(branchPath)
				.executeSync(bus);
	}
	
	private IBranchPath getBranch(String branchPath) {
		IBranchPath path = BranchPathUtils.createPath(branchPath);
		return path;
	}

	private SnomedPredicateBrowser getPredicateBrowser() {
		SnomedPredicateBrowser predicateBrowser = ApplicationContext.getInstance().getService(SnomedPredicateBrowser.class);
		return predicateBrowser;
	}

}
