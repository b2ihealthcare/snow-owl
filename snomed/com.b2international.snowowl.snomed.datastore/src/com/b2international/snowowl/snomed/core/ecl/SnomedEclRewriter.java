/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.b2international.snomed.ecl.ecl.*;
import com.b2international.snomed.ecl.ecl.util.EclSwitch;

/** 
 * @since 5.4
 */
@SuppressWarnings("unchecked")
public class SnomedEclRewriter {

	private static final class RewriterImpl extends EclSwitch<EObject> {
		private static final RewriterImpl INSTANCE = new RewriterImpl();

		@Override
		public EObject caseScript(Script object) {
			object.setConstraint(rewrite(object.getConstraint()));
			return object;
		}

		@Override
		public EObject caseChildOf(ChildOf object) {
			object.setConstraint(rewrite(object.getConstraint()));
			return object;
		}

		@Override
		public EObject caseChildOrSelfOf(ChildOrSelfOf object) {
			object.setConstraint(rewrite(object.getConstraint()));
			return object;
		}

		@Override
		public EObject caseDescendantOf(DescendantOf object) {
			object.setConstraint(rewrite(object.getConstraint()));
			return object;
		}

		@Override
		public EObject caseDescendantOrSelfOf(DescendantOrSelfOf object) {
			object.setConstraint(rewrite(object.getConstraint()));
			return object;
		}

		@Override
		public EObject caseParentOf(ParentOf object) {
			object.setConstraint(rewrite(object.getConstraint()));
			return object;
		}

		@Override
		public EObject caseParentOrSelfOf(ParentOrSelfOf object) {
			object.setConstraint(rewrite(object.getConstraint()));
			return object;
		}

		@Override
		public EObject caseAncestorOf(AncestorOf object) {
			object.setConstraint(rewrite(object.getConstraint()));
			return object;
		}

		@Override
		public EObject caseAncestorOrSelfOf(AncestorOrSelfOf object) {
			object.setConstraint(rewrite(object.getConstraint()));
			return object;
		}

		@Override
		public EObject caseMemberOf(MemberOf object) {
			object.setConstraint(rewrite(object.getConstraint()));
			return object;
		}

		@Override
		public EObject caseEclConceptReference(EclConceptReference object) {
			// Remove term from reference
			object.setTerm(null);
			return object;
		}

		@Override
		public EObject caseEclConceptReferenceSet(EclConceptReferenceSet object) {
			// Make referenced SCTIDs unique (remove reference if its SCTID was already in the set)
			final List<EclConceptReference> conceptReferences = object.getConcepts();
			final Set<String> conceptIds = newHashSet();
			conceptReferences.removeIf(ref -> !conceptIds.add(ref.getId()));
			
			// Rewrite remaining references
			for (int i = 0; i < conceptReferences.size(); i++) {
				conceptReferences.set(i, rewrite(conceptReferences.get(i)));
			}
			
			return object;
		}

		@Override
		public EObject caseAny(Any object) {
			// Nothing to rewrite on "Any"
			return object;
		}

		@Override
		public EObject caseNestedRefinement(NestedRefinement object) {
			object.setNested(rewrite(object.getNested()));
			return object;
		}

		@Override
		public EObject caseEclAttributeGroup(EclAttributeGroup object) {
			object.setRefinement(rewrite(object.getRefinement()));
			return object;
		}

		@Override
		public EObject caseAttributeConstraint(AttributeConstraint object) {
			object.setAttribute(rewrite(object.getAttribute()));
			object.setComparison(rewrite(object.getComparison()));
			return object;
		}

		@Override
		public EObject caseAttributeComparison(AttributeComparison object) {
			final String op = object.getOp();
			final Operator operator = Operator.fromString(op);
			if (!Operator.NOT_EQUALS.equals(operator)) {
				return object;
			}
			
			// replace "!= XYZ" with "= (* MINUS XYZ)"
			final ExclusionExpressionConstraint newExclusion = EclFactory.eINSTANCE.createExclusionExpressionConstraint();
			newExclusion.setLeft(EclFactory.eINSTANCE.createAny());
			newExclusion.setRight(rewrite(object.getValue()));
			
			final NestedExpression newNestedExpression = EclFactory.eINSTANCE.createNestedExpression();
			newNestedExpression.setNested(newExclusion);
			
			object.setOp(Operator.EQUALS.toString());
			object.setValue(newNestedExpression);
			return object;
		}

		@Override
		public EObject caseNestedExpression(NestedExpression object) {
			object.setNested(rewrite(object.getNested()));
			return object;
		}

		@Override
		public EObject caseNestedFilter(NestedFilter object) {
			object.setNested(rewrite(object.getNested()));
			return object;
		}

		@Override
		public EObject caseTypeIdFilter(TypeIdFilter object) {
			object.setType(rewrite(object.getType()));
			return object;
		}

		@Override
		public EObject caseTypeTokenFilter(TypeTokenFilter object) {
			// Make referenced description type tokens unique
			final List<String> tokens = object.getTokens();
			final Set<String> uniqueTokens = newHashSet();
			tokens.removeIf(t -> !uniqueTokens.add(t));
			
			return object;
		}

		@Override
		public EObject caseOrExpressionConstraint(OrExpressionConstraint object) {
			// Consecutive OR constraints are parsed to one side, rewrite the other
			ExpressionConstraint left = object;
			while (left instanceof OrExpressionConstraint) {
				OrExpressionConstraint newLeft = (OrExpressionConstraint) left;
				newLeft.setRight(rewrite(newLeft.getRight()));
				left = newLeft.getLeft();
			}
			return object;
		}

		@Override
		public EObject caseAndExpressionConstraint(AndExpressionConstraint object) {
			// Consecutive AND constraints are parsed to one side, rewrite the other
			ExpressionConstraint left = object;
			while (left instanceof AndExpressionConstraint) {
				AndExpressionConstraint newAnd = (AndExpressionConstraint) left;
				newAnd.setRight(rewrite(newAnd.getRight()));
				left = newAnd.getLeft();
			}
			return object;
		}

		@Override
		public EObject caseExclusionExpressionConstraint(ExclusionExpressionConstraint object) {
			// Consecutive MINUS constraints are parsed to one side, rewrite the other
			ExpressionConstraint left = object;
			while (left instanceof ExclusionExpressionConstraint) {
				ExclusionExpressionConstraint newExclusion = (ExclusionExpressionConstraint) left;
				newExclusion.setRight(rewrite(newExclusion.getRight()));
				left = newExclusion.getLeft();
			}
			return object;
		}

		@Override
		public EObject caseRefinedExpressionConstraint(RefinedExpressionConstraint object) {
			object.setConstraint(rewrite(object.getConstraint()));
			object.setRefinement(rewrite(object.getRefinement()));
			return object;
		}

		@Override
		public EObject caseDottedExpressionConstraint(DottedExpressionConstraint object) {
			object.setAttribute(rewrite(object.getAttribute()));
			object.setConstraint(rewrite(object.getConstraint()));
			return object;
		}

		@Override
		public EObject caseFilteredExpressionConstraint(FilteredExpressionConstraint object) {
			object.setConstraint(rewrite(object.getConstraint()));
			object.setFilter(rewrite(object.getFilter()));
			return object;
		}

		@Override
		public EObject caseOrRefinement(OrRefinement object) {
			EclRefinement left = object;
			while (left instanceof OrRefinement) {
				OrRefinement newRefinement = (OrRefinement) left;
				newRefinement.setRight(rewrite(newRefinement.getRight()));
				left = newRefinement.getLeft();
			}
			return object;
		}

		@Override
		public EObject caseAndRefinement(AndRefinement object) {
			EclRefinement left = object;
			while (left instanceof AndRefinement) {
				AndRefinement newRefinement = (AndRefinement) left;
				newRefinement.setRight(rewrite(newRefinement.getRight()));
				left = newRefinement.getLeft();
			}
			return object;
		}

		@Override
		public EObject caseDisjunctionFilter(DisjunctionFilter object) {
			Filter left = object;
			while (left instanceof DisjunctionFilter) {
				DisjunctionFilter newFilter = (DisjunctionFilter) left;
				newFilter.setRight(rewrite(newFilter.getRight()));
				left = newFilter.getLeft();
			}
			return object;
		}

		@Override
		public EObject caseConjunctionFilter(ConjunctionFilter object) {
			Filter left = object;
			while (left instanceof ConjunctionFilter) {
				ConjunctionFilter newFilter = (ConjunctionFilter) left;
				newFilter.setRight(rewrite(newFilter.getRight()));
				left = newFilter.getLeft();
			}
			return object;
		}

		@Override
		public EObject defaultCase(EObject object) {
			return object;
		}

		// Convenience method that casts the result back to the input object's type
		public <T extends EObject> T rewrite(T object) {
			return (object == null) ? null : (T) doSwitch(object);
		}
	}
	
	SnomedEclRewriter() {}

	/**
	 * Rewrites recognizable ECL subtrees for better/easier evaluation.
	 */
	public <T extends EObject> T rewrite(T eclObject) {
		return RewriterImpl.INSTANCE.rewrite(eclObject);
	}
}
