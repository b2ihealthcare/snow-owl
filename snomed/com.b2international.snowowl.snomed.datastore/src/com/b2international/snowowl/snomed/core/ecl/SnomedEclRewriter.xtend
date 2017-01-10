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
package com.b2international.snowowl.snomed.core.ecl

import com.b2international.snowowl.snomed.ecl.ecl.AncestorOf
import com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf
import com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint
import com.b2international.snowowl.snomed.ecl.ecl.AndRefinement
import com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint
import com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals
import com.b2international.snowowl.snomed.ecl.ecl.ChildOf
import com.b2international.snowowl.snomed.ecl.ecl.Comparison
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOf
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf
import com.b2international.snowowl.snomed.ecl.ecl.EclFactory
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint
import com.b2international.snowowl.snomed.ecl.ecl.NestedExpression
import com.b2international.snowowl.snomed.ecl.ecl.NestedRefinement
import com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint
import com.b2international.snowowl.snomed.ecl.ecl.OrRefinement
import com.b2international.snowowl.snomed.ecl.ecl.ParentOf
import com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint
import com.b2international.snowowl.snomed.ecl.ecl.Refinement
import org.eclipse.emf.ecore.EObject
import com.b2international.snowowl.snomed.ecl.ecl.MemberOf
import com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint
import com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint

/** 
 * @since 5.4
 */
final class SnomedEclRewriter {
	
	package new() {}

	/**
	 * Rewrites recognizable ECL subtrees for better/easier evaluation.
	 */
	def <T extends EObject> T rewrite(T eclObject) {
		if (eclObject instanceof ExpressionConstraint) {
			return eclObject.rewriteExpression as T
		} else if (eclObject instanceof Refinement) {
			return eclObject.rewriteRefinement as T 
		} else if (eclObject instanceof Comparison) {
			return eclObject.rewriteComparison as T
		} else {
			return eclObject
		}
	}

	// EXPRESSIONCONSTRAINT hierarchy

	def private dispatch ExpressionConstraint rewriteExpression(ExpressionConstraint it) {
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(ParentOf it) {
		constraint = rewrite(constraint)
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(AncestorOf it) {
		constraint = rewrite(constraint)
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(AncestorOrSelfOf it) {
		constraint = rewrite(constraint)
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(ChildOf it) {
		constraint = rewrite(constraint)
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(DescendantOf it) {
		constraint = rewrite(constraint)
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(DescendantOrSelfOf it) {
		constraint = rewrite(constraint)
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(MemberOf it) {
		constraint = rewrite(constraint)
		it
	}

	def private dispatch ExpressionConstraint rewriteExpression(OrExpressionConstraint it) {
		left = rewrite(left)
		right = rewrite(right)
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(AndExpressionConstraint it) {
		left = rewrite(left)
		right = rewrite(right)
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(ExclusionExpressionConstraint it) {
		left = rewrite(left)
		right = rewrite(right)
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(DottedExpressionConstraint it) {
		constraint = rewrite(constraint)
		attribute = rewrite(attribute)
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(RefinedExpressionConstraint it) {
		constraint = rewrite(constraint)
		refinement = rewrite(refinement)
		it
	}
	
	def private dispatch ExpressionConstraint rewriteExpression(NestedExpression it) {
		nested = rewrite(nested)
		it
	}
	
	// REFINEMENT hierarchy
	
	def private dispatch Refinement rewriteRefinement(Refinement it) {
		it
	}
	
	def private dispatch Refinement rewriteRefinement(AndRefinement it) {
		left = rewrite(left)
		right = rewrite(right)
		it
	}
	
	def private dispatch Refinement rewriteRefinement(OrRefinement it) {
		left = rewrite(left)
		right = rewrite(right)
		it
	}
	
	def private dispatch Refinement rewriteRefinement(NestedRefinement it) {
		nested = rewrite(nested)
		it
	}
	
	def private dispatch Refinement rewriteRefinement(AttributeGroup it) {
		refinement = rewriteRefinement(refinement)
		it
	}
	
	def private dispatch Refinement rewriteRefinement(AttributeConstraint it) {
		comparison = rewrite(comparison)
		it
	}
	
	// COMPARISON hierarchy
	
	def private dispatch Comparison rewriteComparison(Comparison it) {
		it
	}
	
	def private dispatch Comparison rewriteComparison(AttributeValueNotEquals notEquals) {
		EclFactory.eINSTANCE.createAttributeValueEquals => [
			constraint = EclFactory.eINSTANCE.createNestedExpression => [
				nested = EclFactory.eINSTANCE.createExclusionExpressionConstraint => [
					left = EclFactory.eINSTANCE.createAny
					right = notEquals.constraint
				]
			] 
		]
	}
	
}
