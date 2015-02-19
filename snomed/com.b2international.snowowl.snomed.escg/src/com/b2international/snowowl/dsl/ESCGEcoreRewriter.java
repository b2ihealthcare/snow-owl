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
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.parser.IParseResult;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.dsl.escg.And;
import com.b2international.snowowl.dsl.escg.Attribute;
import com.b2international.snowowl.dsl.escg.AttributeGroup;
import com.b2international.snowowl.dsl.escg.AttributeSet;
import com.b2international.snowowl.dsl.escg.ConceptAssignment;
import com.b2international.snowowl.dsl.escg.ConceptGroup;
import com.b2international.snowowl.dsl.escg.Expression;
import com.b2international.snowowl.dsl.escg.LValue;
import com.b2international.snowowl.dsl.escg.NegatableSubExpression;
import com.b2international.snowowl.dsl.escg.Or;
import com.b2international.snowowl.dsl.escg.RefSet;
import com.b2international.snowowl.dsl.parser.antlr.ESCGParser;
import com.b2international.snowowl.snomed.dsl.query.QueryParser;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.b2international.snowowl.snomed.dsl.query.queryast.AndClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.NotClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.OrClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastFactory;
import com.google.common.collect.Lists;

/**
 * Rewrites an ESCG parse tree and produces to "Query AST" form. The rewrite makes
 * implicit "is_a" relationships explicit from the "concepts" part of the ESCG expression
 * (i.e. the terms before the ':').
 * 
 *
 */
public class ESCGEcoreRewriter implements QueryParser {

	private final ESCGParser parser;

	public ESCGEcoreRewriter(final ESCGParser parser) {
		this.parser = parser;
	}
	
	public RValue rewrite(EObject ast) {

		Expression expression = (Expression) ast;
		final List<RValue> rValues = Lists.newArrayList();
		
		
		for (com.b2international.snowowl.dsl.escg.SubExpression subExpression : expression.getSubExpression()) {
		
			if(subExpression.getLValues().size() < 1) {
				throw new IllegalArgumentException("At least one concept is required");
			}
					
			RValue rootClause = toRValue(subExpression.getLValues().get(0));
			
			for(int i = 1; i < subExpression.getLValues().size(); i++) {
				rootClause = createAndClause(rootClause, toRValue(subExpression.getLValues().get(i)));
			}
			
			if(subExpression.getRefinements() != null && subExpression.getRefinements().getAttributeGroups() != null) {
				for(AttributeGroup attributeGroup: subExpression.getRefinements().getAttributeGroups()) {
					if(attributeGroup instanceof AttributeSet) {
						rootClause = handleAttributeSet(rootClause, (AttributeSet) attributeGroup);
					} else {
						throw new RuntimeException("attributeGroup was not an attributeSet: " + attributeGroup);
					}
				}
				rootClause = handleAttributeSet(rootClause, subExpression.getRefinements().getAttributeSet());
			}
			rValues.add(rootClause);
			
		}
		
		if (CompareUtils.isEmpty(rValues)) {
			throw new IllegalArgumentException("RValues was empty for expression: " + ast);
		}
		
		
		if (1 == rValues.size()) {
			return rValues.get(0);
			
		} else {
			
			OrClause orClause = createOrClause(rValues.get(0), rValues.get(1));
			for (int i = 2; i < rValues.size(); i++) {
				orClause = createOrClause(orClause, rValues.get(i));
			}
			return orClause;
		}
	
		
	}
	
	protected RValue handleAttributeGroup(RValue rootClause, AttributeSet attributeGroup) {
		AttributeClauseGroup attributeClauseGroup = ecoreastFactory.eINSTANCE.createAttributeClauseGroup();
		
		if(attributeGroup != null && attributeGroup.getAttributes() != null) {
			Attribute firstAttribute = attributeGroup.getAttributes().get(0);
			RValue localRootClause = null;
			if(firstAttribute.getAssignment() instanceof ConceptAssignment){
				localRootClause = toAttributeClause((ConceptAssignment)firstAttribute.getAssignment());
			}
			
			for(int i = 1; i < attributeGroup.getAttributes().size(); i++) {
				Attribute attribute = attributeGroup.getAttributes().get(i);
				
				if(attribute.getAssignment() instanceof ConceptAssignment){
					ConceptAssignment conceptAssignment = (ConceptAssignment) attribute.getAssignment();
					// TODO: handle RefSet
					if(conceptAssignment.getName() instanceof ConceptGroup){
						RValue rValue = toAttributeClause(conceptAssignment);
						localRootClause = createAndClause(localRootClause, rValue);
					}
				}
				
				//TODO: handle numerical and unit type attributes
			}
			attributeClauseGroup.setValue(localRootClause);
			return createAndClause(rootClause, attributeClauseGroup);
		}
		return rootClause;
	}

	protected RValue toAttributeClause(ConceptAssignment conceptAssignment) {
		ConceptGroup conceptGroup = (ConceptGroup) conceptAssignment.getName();
		RValue predicate = conceptGroup.isNegated() ? createNotClause(toConceptRef(conceptGroup)) : toConceptRef(conceptGroup);
		com.b2international.snowowl.dsl.escg.RValue logicalExpression = conceptAssignment.getValue();
		RValue rValue = toRValue(predicate, logicalExpression);
		return rValue;
	}
	
	protected RValue handleAttributeSet(RValue rootClause, AttributeSet attributeSet) {
		if(attributeSet != null && attributeSet.getAttributes() != null) {
			for(Attribute attribute: attributeSet.getAttributes()) {
				
				if(attribute.getAssignment() instanceof ConceptAssignment){
					ConceptAssignment conceptAssignment = (ConceptAssignment) attribute.getAssignment();
					// TODO: handle RefSet
					if(conceptAssignment.getName() instanceof ConceptGroup){
						RValue rValue = toAttributeClause(conceptAssignment);
						rootClause = createAndClause(rootClause, rValue);
					}
				}
			}
		}
		
		return rootClause;
	}
	
	protected RValue toConceptRef(String conceptId) {
		return toConceptRef(conceptId, "");
	}
	
	protected RValue toConceptRef(String conceptId, String label) {
		ConceptRef conceptRef = ecoreastFactory.eINSTANCE.createConceptRef();
		conceptRef.setConceptId(conceptId);
		conceptRef.setLabel(label);
		return conceptRef;
	}
	
	protected RValue toConceptRef(ConceptGroup conceptGroup) {
		SubsumptionQuantifier quantifier = SubsumptionQuantifier.SELF; 
		if("<".equals(conceptGroup.getConstraint())) {
			quantifier = SubsumptionQuantifier.ANY_SUBTYPE;
		} else if("<<".equals(conceptGroup.getConstraint())) {
			quantifier = SubsumptionQuantifier.SELF_AND_ANY_SUBTYPE;
		}
		
		ConceptRef conceptRef = ecoreastFactory.eINSTANCE.createConceptRef();
		conceptRef.setConceptId(conceptGroup.getConcept().getId());
		conceptRef.setLabel(conceptGroup.getConcept().getTerm());
		conceptRef.setQuantifier(quantifier);
		return conceptRef;
	}
	
	protected RValue toRValue(LValue lValue) {
		
		if(lValue instanceof ConceptGroup) {
			ConceptGroup conceptGroup = (ConceptGroup) lValue;
			RValue conceptRef = toConceptRef(conceptGroup);
			
			return conceptGroup.isNegated() ? createNotClause(conceptRef) : conceptRef;
		}
		
		if(lValue instanceof RefSet) {
			RefSet refSet = (RefSet) lValue;
			com.b2international.snowowl.snomed.dsl.query.queryast.RefSet clause =
					ecoreastFactory.eINSTANCE.createRefSet();
			clause.setId(refSet.getId());
			return refSet.isNegated() ? createNotClause(clause) : clause;
		}
		throw new IllegalArgumentException("Don't know how to evaluate lValue: " + lValue);
	}
	
	protected RValue toRValue(RValue predicate, com.b2international.snowowl.dsl.escg.RValue rValue) {
		
		if(rValue instanceof NegatableSubExpression) {
			NegatableSubExpression nse = (NegatableSubExpression) rValue;
			
			SubExpression subExpression = ecoreastFactory.eINSTANCE.createSubExpression();
			subExpression.setValue(rewrite(nse.getExpression()));
			AttributeClause clause = ecoreastFactory.eINSTANCE.createAttributeClause();
			clause.setLeft(predicate);
			clause.setRight(subExpression);
			return nse.isNegated() ? createNotClause(clause) : clause;
		}
		if(rValue instanceof ConceptGroup) {
			ConceptGroup cg = (ConceptGroup) rValue;
			AttributeClause clause = ecoreastFactory.eINSTANCE.createAttributeClause();
			clause.setLeft(predicate);
			clause.setRight(toConceptRef(cg));
			return cg.isNegated() ? createNotClause(clause) : clause;
			
		}
		if(rValue instanceof And) {
			RValue left = toRValue(EcoreUtil.copy(predicate), ((And) rValue).getLeft());
			RValue right = toRValue(EcoreUtil.copy(predicate), ((And) rValue).getRight());
			return createAndClause(left, right);
		}
		if(rValue instanceof Or) {
			RValue left = toRValue(EcoreUtil.copy(predicate), ((Or) rValue).getLeft());
			RValue right = toRValue(EcoreUtil.copy(predicate), ((Or) rValue).getRight());
			OrClause orClause = ecoreastFactory.eINSTANCE.createOrClause();
			orClause.setLeft(left);
			orClause.setRight(right);
			return orClause;
		}
		if(rValue instanceof RefSet) {
			RefSet refSet = (RefSet) rValue;
			AttributeClause clause = ecoreastFactory.eINSTANCE.createAttributeClause();
			com.b2international.snowowl.snomed.dsl.query.queryast.RefSet refset = ecoreastFactory.eINSTANCE.createRefSet();
			refset.setId(refSet.getId());
			clause.setLeft(predicate);
			clause.setRight(refset);
			
			return refSet.isNegated() ? createNotClause(clause) : clause;
		}
		throw new IllegalArgumentException("Don't know how to evaluate logical expression: " + rValue);
	}
	
	public RValue parse(Reader reader) {
		final IParseResult parseResult = parser.parse(reader);
		
		if (parseResult.hasSyntaxErrors()) {
			throw new SyntaxErrorException(parseResult.getSyntaxErrors());
		}
		
		return rewrite(parseResult.getRootASTElement());
	}
	
	protected NotClause createNotClause(RValue value) {
		NotClause clause = ecoreastFactory.eINSTANCE.createNotClause();
		clause.setValue(value);
		return clause;
	}
	
	protected AndClause createAndClause(RValue left, RValue right) {
		AndClause clause = ecoreastFactory.eINSTANCE.createAndClause();
		clause.setLeft(left);
		clause.setRight(right);
		return clause;
	}
	
	protected OrClause createOrClause(final RValue left, RValue right) {
		OrClause orClause = ecoreastFactory.eINSTANCE.createOrClause();
		orClause.setRight(right);
		orClause.setLeft(left);
		return orClause;
	}
}