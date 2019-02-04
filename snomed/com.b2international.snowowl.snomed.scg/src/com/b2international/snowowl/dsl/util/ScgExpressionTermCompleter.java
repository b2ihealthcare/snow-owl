/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.dsl.util;

import org.eclipse.emf.common.util.EList;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.AttributeValue;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;

public class ScgExpressionTermCompleter {
	
	private final Expression expression;
	private final String branch;
	
	public ScgExpressionTermCompleter(final String branch, Expression expression) {
		this.branch = branch;
		this.expression = expression;
	}

	public Expression getTermCompletedExpression() {
		populateTerm(expression);
		
		return expression;
	}
	
	private void populateTerm(Expression normalizedExpression) {
		EList<Concept> concepts = normalizedExpression.getConcepts();
		for (Concept concept : concepts) {
			addTerm(concept);
		}
		
		EList<Attribute> attributes = normalizedExpression.getAttributes();
		
		populateAttributesWithTerm(attributes);
		
		EList<Group> groups = normalizedExpression.getGroups();
		
		for (Group group : groups) {
			populateAttributesWithTerm(group.getAttributes());
		}
	}

	private void populateAttributesWithTerm(EList<Attribute> attributes) {
		for (Attribute attribute : attributes) {
			AttributeValue attributeValue = attribute.getValue();
			
			if (attributeValue instanceof Concept) {
				addTerm((Concept) attributeValue);
			}
			
			addTerm(attribute.getName());
		}
	}

	private void addTerm(Concept concept) {
		final SnomedDescription pt = getPt(concept.getId());
		concept.setTerm(pt == null ? concept.getId() : pt.getTerm());
	}

	private SnomedDescription getPt(String id) {
		return SnomedRequests.prepareGetConcept(id)
				.setExpand("pt()")
				.setLocales(ImmutableList.of(ExtendedLocale.valueOf("en-sg"), ExtendedLocale.valueOf("en-gb")))
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync().getPt();
	}
}