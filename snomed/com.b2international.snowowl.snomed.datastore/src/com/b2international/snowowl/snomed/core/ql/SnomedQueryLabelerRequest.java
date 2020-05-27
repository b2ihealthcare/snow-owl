/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.core.ql;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.tree.NoopTreeVisitor;
import com.b2international.commons.tree.emf.EObjectTreeNode;
import com.b2international.commons.tree.emf.EObjectWalker;
import com.b2international.snowowl.core.authorization.BranchAccessControl;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.ecl.EclConceptReference;
import com.b2international.snowowl.snomed.ql.ql.Query;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 7.6.0
 */
final class SnomedQueryLabelerRequest extends ResourceRequest<BranchContext, Expressions> implements BranchAccessControl {

	private static final long serialVersionUID = 1L;

	@NotNull
	private List<String> expressions;

	@JsonProperty
	@NotEmpty
	private String descriptionType;
	
	void setExpressions(List<String> expression) {
		this.expressions = expression;
	}
	
	void setDescriptionType(String descriptionType) {
		this.descriptionType = descriptionType;
	}
	
	@Override
	public String getOperation() {
		return Permission.BROWSE;
	}

	@Override
	public Expressions execute(BranchContext context) {
		SnomedQuerySerializer querySerializer = context.service(SnomedQuerySerializer.class);
		final Set<String> conceptIdsToLabel = Sets.newHashSetWithExpectedSize(expressions.size());
		final Map<String, Query> queries = Maps.newHashMapWithExpectedSize(expressions.size());
		
		for (String expression : expressions) {
			if (Strings.isNullOrEmpty(expression)) {
				continue;
			}
			Query query = queries.computeIfAbsent(expression, (key) -> context.service(SnomedQueryParser.class).parse(key));
			conceptIdsToLabel.addAll(collect(query));
		}
		
		// fetch all concept labels
		final Map<String, String> labels = SnomedRequests.prepareSearchConcept()
				.filterByIds(conceptIdsToLabel)
				.setLimit(conceptIdsToLabel.size())
				.setExpand(descriptionType.toLowerCase() + "()")
				.setLocales(locales())
				.build()
				.execute(context)
				.stream()
				.collect(Collectors.toMap(SnomedConcept::getId, this::extractLabel));
		
		// expand all queries with labels
		List<String> results = expressions.stream()
			.map(expression -> {
				if (Strings.isNullOrEmpty(expression)) {
					return expression;
				} else {
					Query query = queries.get(expression);
					expand(query, labels);
					return querySerializer.serialize(query);
				}
			})
			.collect(Collectors.toList());
		
		
		return new Expressions(results);
	}
	
	private String extractLabel(SnomedConcept concept) {
		switch (descriptionType) {
		case SnomedConcept.Expand.FULLY_SPECIFIED_NAME:
			if (concept.getFsn() != null) {
				return concept.getFsn().getTerm();
			}
			break;
		case SnomedConcept.Expand.PREFERRED_TERM:
			if (concept.getPt() != null) {
				return concept.getPt().getTerm();
			}
			break;
		default: break;
		}
		return concept.getId(); 
	}

	private Set<String> collect(Query query) {
		final Set<String> conceptIds = Sets.newHashSet();
		
		EObjectWalker.createContainmentWalker(new NoopTreeVisitor<EObjectTreeNode>() {
			@Override
			protected void doVisit(EObjectTreeNode node) {
				if (node.getEObject() instanceof EclConceptReference) {
					EclConceptReference ref = (EclConceptReference) node.getEObject();
					conceptIds.add(ref.getId());
				}
			}
		}).walk(query);
		
		return conceptIds;
	}
	
	private void expand(Query query, final Map<String, String> labels) {
		EObjectWalker.createContainmentWalker(new NoopTreeVisitor<EObjectTreeNode>() {
			@Override
			protected void doVisit(EObjectTreeNode node) {
				if (node.getEObject() instanceof EclConceptReference) {
					EclConceptReference ref = (EclConceptReference) node.getEObject();
					String label = labels.get(ref.getId());
					// if the given label is not the concept's ID then set the label
					if (!Objects.equals(ref.getId(), label)) {
						ref.setTerm(label);
					}
					// TODO report missing labels?
				}
			}
		}).walk(query);
	}

}
