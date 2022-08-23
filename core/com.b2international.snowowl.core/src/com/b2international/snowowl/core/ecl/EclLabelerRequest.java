/*
 * Copyright 2020-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.ecl;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.ApiException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.SyntaxException;
import com.b2international.commons.tree.NoopTreeVisitor;
import com.b2international.snomed.ecl.ecl.EclConceptReference;
import com.b2international.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snomed.ecl.validation.EclValidator;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.emf.EObjectTreeNode;
import com.b2international.snowowl.core.emf.EObjectWalker;
import com.b2international.snowowl.core.request.ResourceRequest;
import com.b2international.snowowl.core.request.ecl.EclRewriter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 7.6.0
 */
final class EclLabelerRequest extends ResourceRequest<ServiceProvider, LabeledEclExpressions> {

	private static final long serialVersionUID = 2L;

	@NotEmpty
	private String codeSystemUri;
	
	@NotNull
	private List<String> expressions;

	@JsonProperty
	@NotEmpty
	private String descriptionType;
	
	void setCodeSystemUri(String codeSystemUri) {
		this.codeSystemUri = codeSystemUri;
	}
	
	void setExpressions(List<String> expression) {
		this.expressions = expression;
	}
	
	void setDescriptionType(String descriptionType) {
		this.descriptionType = descriptionType;
	}
	
	@Override
	public LabeledEclExpressions execute(ServiceProvider context) {
		final EclSerializer eclSerializer = context.service(EclSerializer.class);
		final EclParser eclParser = context.service(EclParser.class);
		final Set<String> conceptIdsToLabel = Sets.newHashSetWithExpectedSize(expressions.size());
		final Map<String, ExpressionConstraint> queries = Maps.newHashMapWithExpectedSize(expressions.size());
		final LinkedHashMap <String, Object> errors = Maps.newLinkedHashMap();
		
		// retrieve Ecl Rewriter implementation for the selected CodeSystem
		CodeSystem codeSystem = CodeSystemRequests.prepareSearchCodeSystem()
			.one()
			.filterById(CodeSystem.uri(codeSystemUri).getResourceId())
			.buildAsync()
			.execute(context)
			.first()
			.orElseThrow(() -> new BadRequestException("codeSystemUri '%s' cannot be resolved to an existing Code System.", codeSystemUri));
		
		EclRewriter rewriter = context.service(RepositoryManager.class)
				.get(codeSystem.getToolingId())
				.service(EclRewriter.class);
		
		for (String expression : expressions) {
			if (Strings.isNullOrEmpty(expression)) {
				continue;
			}
			try {
				// always ignore the SCT ID validation error, so this API can be used with any Code System 
				// also, rewrite the ECL query to properly parse certain tooling specific syntaxes
				ExpressionConstraint query = queries.computeIfAbsent(expression, (key) -> rewriter.rewrite(eclParser.parse(key, Set.of(EclValidator.SCTID_ERROR_CODE))));
				conceptIdsToLabel.addAll(collect(query));
			} catch (ApiException e) {
				if (e instanceof SyntaxException) {
					errors.put(expression, List.copyOf(((SyntaxException)e).getAdditionalInfo().values()));
				} else if (e instanceof BadRequestException) {
					errors.put(expression, e.getMessage());
				} else {
					throw e;
				}
			}
		}
		
		if (!errors.isEmpty()) {
			BadRequestException badRequestException = new BadRequestException("One or more ECL syntax errors");
			badRequestException.withAdditionalInfo("erroneousExpressions", errors);

			throw badRequestException;
		}
		
		// fetch all concept labels
		final Map<String, String> labels = CodeSystemRequests.prepareSearchConcepts()
				.filterByIds(conceptIdsToLabel)
				.filterByCodeSystemUri(CodeSystem.uri(codeSystemUri))
				.setPreferredDisplay(descriptionType)
				.setLimit(conceptIdsToLabel.size())
				.setLocales(locales())
				.build()
				.execute(context)
				.stream()
				.collect(Collectors.toMap(Concept::getId, Concept::getTerm));
		
		// expand all queries with labels
		List<String> results = expressions.stream()
			.map(expression -> {
				if (Strings.isNullOrEmpty(expression)) {
					return expression;
				} else {
					ExpressionConstraint query = queries.get(expression);
					expand(query, labels);
					return eclSerializer.serialize(query);
				}
			})
			.collect(Collectors.toList());
		
		return new LabeledEclExpressions(results);
	}
	
	private Set<String> collect(ExpressionConstraint constraint) {
		final Set<String> conceptIds = Sets.newHashSet();
		
		EObjectWalker.createContainmentWalker(new NoopTreeVisitor<EObjectTreeNode>() {
			@Override
			protected void doVisit(EObjectTreeNode node) {
				if (node.getEObject() instanceof EclConceptReference) {
					EclConceptReference ref = (EclConceptReference) node.getEObject();
					conceptIds.add(ref.getId());
				}
			}
		}).walk(constraint);
		
		return conceptIds;
	}
	
	private void expand(ExpressionConstraint constraint, final Map<String, String> labels) {
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
		}).walk(constraint);
	}
}
