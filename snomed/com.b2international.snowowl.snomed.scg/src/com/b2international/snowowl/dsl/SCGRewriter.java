/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.dsl;

import java.io.Reader;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.IParseResult;

import com.b2international.snowowl.dsl.parser.antlr.SCGParser;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.AttributeValue;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.snomed.dsl.query.QueryParser;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.b2international.snowowl.snomed.dsl.query.queryast.AndClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastFactory;


/**
 * Rewrites an SCG parse tree and produces "Query AST" form. Probably broken for now.
 * 
 *
 */
public class SCGRewriter implements QueryParser {

	public RValue rewrite(EObject ast) {

		Expression expression = (Expression) ast;
		
		if(expression.getConcepts().size() < 1) {
			throw new IllegalArgumentException("At least one concept is required");
		}
		
		RValue rootClause = toConceptRef(expression.getConcepts().get(0));

		for(int i = 1; i < expression.getConcepts().size(); i++) {
			rootClause = createAndClause(rootClause, toConceptRef(expression.getConcepts().get(i)));
		}
		
		for(Attribute attribute: expression.getAttributes()) {
				ConceptRef predicate = toConceptRef(attribute.getName());
				rootClause = createAndClause(rootClause, toRValue(predicate, attribute.getValue()));
		} 
			
		for(Group group: expression.getGroups()) {
			if(group.getAttributes().size() > 0) {
				
				AttributeClauseGroup acGroup = ecoreastFactory.eINSTANCE.createAttributeClauseGroup();
				RValue groupRoot = toRValue(
						toConceptRef(group.getAttributes().get(0).getName()),
						group.getAttributes().get(0).getValue());
				for(int i = 1; i < group.getAttributes().size(); i++) {
					groupRoot = createAndClause(rootClause, toRValue(
							toConceptRef(group.getAttributes().get(i).getName()),
							group.getAttributes().get(i).getValue()));
				}
				rootClause = acGroup;
				acGroup.setValue(groupRoot);
			}
		}
		
		return rootClause;
	}
	
	protected ConceptRef toConceptRef(Concept concept) {
		ConceptRef conceptRef = ecoreastFactory.eINSTANCE.createConceptRef();
		conceptRef.setConceptId(concept.getId());
		conceptRef.setLabel(concept.getTerm());
		return conceptRef;
	}
	
	protected RValue toRValue(ConceptRef predicate, AttributeValue attributeValue) {

		if(attributeValue instanceof Concept) {
			Concept concept = (Concept) attributeValue;
			AttributeClause attributeClause = ecoreastFactory.eINSTANCE.createAttributeClause();
			attributeClause.setLeft(predicate);
			attributeClause.setRight(toConceptRef(concept));
			return attributeClause;
		
		} else if (attributeValue instanceof Expression) {
			Expression expression = (Expression) attributeValue;
			
			SubExpression subExpression = ecoreastFactory.eINSTANCE.createSubExpression();
			subExpression.setValue(rewrite(expression));
			AttributeClause attributeClause = ecoreastFactory.eINSTANCE.createAttributeClause();
			attributeClause.setLeft(predicate);
			attributeClause.setRight(subExpression);
			return attributeClause;
		}
		
		throw new UnsupportedOperationException("Unknown attribute value type: " + attributeValue);
	}

	public RValue parse(Reader reader) {
		SCGParser parser = SCGStandaloneSetup.createSCGParser();
		IParseResult parseResult = parser.parse(reader);
		
		if (parseResult.hasSyntaxErrors()) {
			throw new SyntaxErrorException(parseResult.getSyntaxErrors());
		}
		
		return rewrite(parseResult.getRootASTElement());
	}
	
	protected AndClause createAndClause(RValue left, RValue right) {
		AndClause clause = ecoreastFactory.eINSTANCE.createAndClause();
		clause.setLeft(left);
		clause.setRight(right);
		return clause;
	}	
}