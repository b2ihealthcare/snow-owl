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
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.nodemodel.impl.LeafNodeWithSyntaxError;
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
import com.b2international.snowowl.dsl.escg.NumericalAssignment;
import com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup;
import com.b2international.snowowl.dsl.escg.Or;
import com.b2international.snowowl.dsl.escg.RefSet;
import com.b2international.snowowl.dsl.parser.antlr.ESCGParser;
import com.b2international.snowowl.snomed.dsl.query.QueryParser;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.b2international.snowowl.snomed.dsl.query.ast.AndClause;
import com.b2international.snowowl.snomed.dsl.query.ast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.ast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.ast.NotClause;
import com.b2international.snowowl.snomed.dsl.query.ast.NumericDataClause;
import com.b2international.snowowl.snomed.dsl.query.ast.NumericDataClause.Operator;
import com.b2international.snowowl.snomed.dsl.query.ast.NumericDataGroupClause;
import com.b2international.snowowl.snomed.dsl.query.ast.OrClause;
import com.b2international.snowowl.snomed.dsl.query.ast.RValue;
import com.b2international.snowowl.snomed.dsl.query.ast.SubExpression;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


/**
 * Rewrites an ESCG parse tree and produces to "Query AST" form. The rewrite makes
 * implicit "is_a" relationships explicit from the "concepts" part of the CGS expression
 * (i.e. the terms before the ':').
 * 
 *
 */
public class ESCGRewriter implements QueryParser {
	
	private final ESCGParser parser;

	public ESCGRewriter(final ESCGParser parser) {
		this.parser = parser;
	}
	
	//the expression computed from the LValue-s of the query
	private RValue leftValue;

	public RValue rewrite(EObject ast) {
		Expression expression = (Expression) ast;
		final List<RValue> rValues = Lists.newArrayList();

		for (com.b2international.snowowl.dsl.escg.SubExpression subExpression : expression.getSubExpression()) {

			if (subExpression.getLValues().size() < 1) {
				throw new IllegalArgumentException("At least one concept is required");
			}

			RValue rootClause = toRValue(subExpression.getLValues().get(0));

			for (int i = 1; i < subExpression.getLValues().size(); i++) {
				rootClause = new AndClause(rootClause, toRValue(subExpression.getLValues().get(i)));
			}

			leftValue = rootClause;

			if (subExpression.getRefinements() != null && subExpression.getRefinements().getAttributeGroups() != null) {
				for (AttributeGroup attributeGroup : subExpression.getRefinements().getAttributeGroups()) {
					if (attributeGroup instanceof AttributeSet) {
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

			OrClause orClause = new OrClause(rValues.get(0), rValues.get(1));
			for (int i = 2; i < rValues.size(); i++) {
				orClause = new OrClause(orClause, rValues.get(i));
			}
			return orClause;
		}
	}
	
	protected RValue handleAttributeSet(RValue rootClause, AttributeSet attributeSet) {
		if(attributeSet != null && attributeSet.getAttributes() != null) {
			for(Attribute attribute: attributeSet.getAttributes()) {
				
				if(attribute.getAssignment() instanceof ConceptAssignment){
					ConceptAssignment conceptAssignment = (ConceptAssignment) attribute.getAssignment();
					
					// TODO: handle RefSet
					if (conceptAssignment.getName() instanceof ConceptGroup) {
						
						ConceptGroup conceptGroup = (ConceptGroup) conceptAssignment.getName();
						RValue predicate = conceptGroup.isNegated() ? new NotClause(toConceptRef(conceptGroup)) : toConceptRef(conceptGroup);
						
						com.b2international.snowowl.dsl.escg.RValue logicalExpression = conceptAssignment.getValue();
						
						rootClause = new AndClause(rootClause, toRValue(predicate, logicalExpression));
					}
				}
				else if(attribute.getAssignment() instanceof NumericalAssignment) {
					
					NumericalAssignment numericalAssignment = (NumericalAssignment) attribute.getAssignment();
					
					NumericDataClause numericDataClause = new NumericDataClause(leftValue, Operator.get(numericalAssignment.getOperator()), numericalAssignment.getValue().doubleValue(), numericalAssignment.getUnit());
					
					rootClause = new AndClause(rootClause, numericDataClause);
				}
				else if(attribute.getAssignment() instanceof NumericalAssignmentGroup) {
					
					NumericalAssignmentGroup numericalAssignmentGroup = (NumericalAssignmentGroup) attribute.getAssignment();
					
					//substance concepts
					com.b2international.snowowl.dsl.escg.RValue substanceExpression = numericalAssignmentGroup.getSubstance();
					AttributeClause hasIngredientClause = (AttributeClause) toRValue(toConceptRef(numericalAssignmentGroup.getIngredientConcept().getId()), substanceExpression);
					//only the right side of the clause is needed
					RValue substanceClause = hasIngredientClause.getRight();					
										
					//numeric data
					NumericalAssignment numericalAssignment = numericalAssignmentGroup.getNumericValue();
					NumericDataClause numericDataClause = new NumericDataClause(leftValue, Operator.get(numericalAssignment.getOperator()), numericalAssignment.getValue().doubleValue(), numericalAssignment.getUnit());
											
					NumericDataGroupClause numericDataGroupClause = new NumericDataGroupClause(leftValue, numericDataClause, substanceClause);
					
					rootClause = new AndClause(rootClause, numericDataGroupClause);
				}
			}
		}
		
		return rootClause;
	}
	
	protected RValue toConceptRef(String conceptId) {
		return toConceptRef(conceptId, "");
	}
	
	protected RValue toConceptRef(String conceptId, String label) {
		ConceptRef conceptRef = new ConceptRef(conceptId, label);
		conceptRef.setConceptId(conceptId);
		conceptRef.setLabel(label);
		return conceptRef;
	}
	
	protected RValue toConceptRef(ConceptGroup conceptGroup) {
		ConceptRef.Quantifier quantifier = ConceptRef.Quantifier.SELF; 
		if("<".equals(conceptGroup.getConstraint())) {
			quantifier = ConceptRef.Quantifier.ANY_SUBTYPE;
		} else if("<<".equals(conceptGroup.getConstraint())) {
			quantifier = ConceptRef.Quantifier.SELF_AND_ANY_SUBTYPE;
		}
		
		ConceptRef conceptRef = new ConceptRef(conceptGroup.getConcept().getId(), conceptGroup.getConcept().getTerm(), quantifier);
		return conceptRef;
	}
	
	protected RValue toRValue(LValue lValue) {
		
		if(lValue instanceof ConceptGroup) {
			ConceptGroup conceptGroup = (ConceptGroup) lValue;
			RValue conceptRef = toConceptRef(conceptGroup);
			
			return conceptGroup.isNegated() ? new NotClause(conceptRef) : conceptRef;
		}
		
		if(lValue instanceof RefSet) {
			RefSet refSet = (RefSet) lValue;
			com.b2international.snowowl.snomed.dsl.query.ast.RefSet clause =
				new com.b2international.snowowl.snomed.dsl.query.ast.RefSet(refSet.getId());
			return refSet.isNegated() ? new NotClause(clause) : clause;
		}
		throw new IllegalArgumentException("Don't know how to evaluate lValue: " + lValue);
	}
	
	protected RValue toRValue(RValue predicate, com.b2international.snowowl.dsl.escg.RValue rValue) {
		
		if(rValue instanceof NegatableSubExpression) {
			NegatableSubExpression nse = (NegatableSubExpression) rValue;
			
			AttributeClause clause = new AttributeClause(predicate, new SubExpression(rewrite(nse.getExpression())));
			return nse.isNegated() ? new NotClause(clause) : clause;
		}
		if(rValue instanceof ConceptGroup) {
			ConceptGroup cg = (ConceptGroup) rValue;
			AttributeClause clause = new AttributeClause(predicate, toConceptRef(cg));
			return cg.isNegated() ? new NotClause(clause) : clause;
			
		}
		if(rValue instanceof And) {
			RValue left = toRValue(predicate, ((And) rValue).getLeft());
			RValue right = toRValue(predicate, ((And) rValue).getRight());
			return new AndClause(left, right);
		}
		if(rValue instanceof Or) {
			RValue left = toRValue(predicate, ((Or) rValue).getLeft());
			RValue right = toRValue(predicate, ((Or) rValue).getRight());
			return new OrClause(left, right);
		}
		if(rValue instanceof RefSet) {
			RefSet refSet = (RefSet) rValue;
			AttributeClause clause =
				new AttributeClause(predicate, new com.b2international.snowowl.snomed.dsl.query.ast.RefSet(refSet.getId()));
			
			return refSet.isNegated() ? new NotClause(clause) : clause;
		}
		throw new IllegalArgumentException("Don't know how to evaluate logical expression: " + rValue);
	}
	
	public RValue parse(Reader reader) {
		final IParseResult parseResult = parser.parse(reader);

		if (parseResult.hasSyntaxErrors()) {
			throw new SyntaxErrorException(parseResult.getSyntaxErrors());
		}

		if (parseResult.getRootASTElement() == null) {
			final INode inputEmptyError = new LeafNodeWithSyntaxError() {
				@Override protected void basicSetSyntaxErrorMessage(final SyntaxErrorMessage syntaxErrorMessage) {
					super.basicSetSyntaxErrorMessage(new SyntaxErrorMessage("Input expression is empty.", Diagnostic.SYNTAX_DIAGNOSITC));
				}
			};
			throw new SyntaxErrorException(ImmutableList.of(inputEmptyError));
		}

		return rewrite(parseResult.getRootASTElement());
	}
}