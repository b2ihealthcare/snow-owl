/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ecl;

import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.snomed.ecl.ecl.AncestorOf;
import com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AndRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.ChildOf;
import com.b2international.snowowl.snomed.ecl.ecl.Comparison;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOf;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.EclFactory;
import com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.MemberOf;
import com.b2international.snowowl.snomed.ecl.ecl.NestedExpression;
import com.b2international.snowowl.snomed.ecl.ecl.NestedRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.OrRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.ParentOf;
import com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.Refinement;

/** 
 * @since 5.4
 */
@SuppressWarnings("unchecked")
public class SnomedEclRewriter {

	SnomedEclRewriter() {}

	/**
	 * Rewrites recognizable ECL subtrees for better/easier evaluation.
	 */
	public <T extends EObject> T rewrite(T eclObject) {
		if (eclObject instanceof ExpressionConstraint) {
			return (T) rewriteExpression((ExpressionConstraint) eclObject);
		} else if (eclObject instanceof Refinement) {
			return (T) rewriteRefinement((Refinement) eclObject);
		} else if (eclObject instanceof Comparison) {
			return (T) rewriteComparison((Comparison) eclObject);
		} else {
			return eclObject;
		}
	}

	// EXPRESSIONCONSTRAINT hierarchy

	private ExpressionConstraint rewriteExpression(ExpressionConstraint it) {
		if (it instanceof ParentOf) {
			return rewriteExpression((ParentOf) it);
		} else if (it instanceof AncestorOf) {
			return rewriteExpression((AncestorOf) it);
		} else if (it instanceof AncestorOrSelfOf) {
			return rewriteExpression((AncestorOrSelfOf) it);
		} else if (it instanceof ChildOf) {
			return rewriteExpression((ChildOf) it);
		} else if (it instanceof DescendantOf) {
			return rewriteExpression((DescendantOf) it);
		} else if (it instanceof DescendantOrSelfOf) {
			return rewriteExpression((DescendantOrSelfOf) it);
		} else if (it instanceof MemberOf) {
			return rewriteExpression((MemberOf) it);
		} else if (it instanceof OrExpressionConstraint) {
			return rewriteExpression((OrExpressionConstraint) it);
		} else if (it instanceof AndExpressionConstraint) {
			return rewriteExpression((AndExpressionConstraint) it);
		} else if (it instanceof ExclusionExpressionConstraint) {
			return rewriteExpression((ExclusionExpressionConstraint) it);
		} else if (it instanceof DottedExpressionConstraint) {
			return rewriteExpression((DottedExpressionConstraint) it);
		} else if (it instanceof RefinedExpressionConstraint) {
			return rewriteExpression((RefinedExpressionConstraint) it);
		} else if (it instanceof NestedExpression) {
			return rewriteExpression((NestedExpression) it);
		}
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(ParentOf it) {
		it.setConstraint(rewrite(it.getConstraint()));
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(AncestorOf it) {
		it.setConstraint(rewrite(it.getConstraint()));
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(AncestorOrSelfOf it) {
		it.setConstraint(rewrite(it.getConstraint()));
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(ChildOf it) {
		it.setConstraint(rewrite(it.getConstraint()));
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(DescendantOf it) {
		it.setConstraint(rewrite(it.getConstraint()));
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(DescendantOrSelfOf it) {
		it.setConstraint(rewrite(it.getConstraint()));
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(MemberOf it) {
		it.setConstraint(rewrite(it.getConstraint()));
		return it;
	}

	private ExpressionConstraint rewriteExpression(OrExpressionConstraint it) {
		it.setLeft(rewrite(it.getLeft()));
		it.setRight(rewrite(it.getRight()));
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(AndExpressionConstraint it) {
		it.setLeft(rewrite(it.getLeft()));
		it.setRight(rewrite(it.getRight()));
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(ExclusionExpressionConstraint it) {
		it.setLeft(rewrite(it.getLeft()));
		it.setRight(rewrite(it.getRight()));
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(DottedExpressionConstraint it) {
		it.setConstraint(rewrite(it.getConstraint()));
		it.setAttribute(rewrite(it.getAttribute()));
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(RefinedExpressionConstraint it) {
		it.setConstraint(rewrite(it.getConstraint()));
		it.setRefinement(rewrite(it.getRefinement()));
		return it;
	}
	
	private ExpressionConstraint rewriteExpression(NestedExpression it) {
		it.setNested(rewrite(it.getNested()));
		return it;
	}
	
	// REFINEMENT hierarchy
	
	private Refinement rewriteRefinement(Refinement it) {
		if (it instanceof AndRefinement) {
			return rewriteRefinement((AndRefinement) it);
		} else if (it instanceof OrRefinement) {
			return rewriteRefinement((OrRefinement) it);
		} else if (it instanceof NestedRefinement) {
			return rewriteRefinement((NestedRefinement) it);
		} else if (it instanceof AttributeGroup) {
			return rewriteRefinement((AttributeGroup) it);
		} else if (it instanceof AttributeConstraint) {
			return rewriteRefinement((AttributeConstraint) it);
		}
		return it;
	}
	
	private Refinement rewriteRefinement(AndRefinement it) {
		it.setLeft(rewrite(it.getLeft()));
		it.setRight(rewrite(it.getRight()));
		return it;
	}
	
	private Refinement rewriteRefinement(OrRefinement it) {
		it.setLeft(rewrite(it.getLeft()));
		it.setRight(rewrite(it.getRight()));
		return it;
	}
	
	private Refinement rewriteRefinement(NestedRefinement it) {
		it.setNested(rewrite(it.getNested()));
		return it;
	}
	
	private Refinement rewriteRefinement(AttributeGroup it) {
		it.setRefinement(rewriteRefinement(it.getRefinement()));
		return it;
	}
	
	private Refinement rewriteRefinement(AttributeConstraint it) {
		it.setComparison(rewrite(it.getComparison()));
		return it;
	}
	
	// COMPARISON hierarchy
	
	private Comparison rewriteComparison(Comparison it) {
		if (it instanceof AttributeValueNotEquals) {
			// replace != with * MINUS XYZ
			AttributeValueEquals newComparison = EclFactory.eINSTANCE.createAttributeValueEquals();
			NestedExpression newConstraint = EclFactory.eINSTANCE.createNestedExpression();
			ExclusionExpressionConstraint newNested = EclFactory.eINSTANCE.createExclusionExpressionConstraint();
			newNested.setLeft(EclFactory.eINSTANCE.createAny());
			newNested.setRight(((AttributeValueNotEquals) it).getConstraint());
			newConstraint.setNested(newNested);
			newComparison.setConstraint(newConstraint);
			return newComparison;
		}
		return it;
	}
	
}
