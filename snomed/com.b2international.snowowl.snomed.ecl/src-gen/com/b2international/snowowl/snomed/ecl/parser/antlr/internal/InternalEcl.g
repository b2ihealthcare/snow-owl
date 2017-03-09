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
grammar InternalEcl;

options {
	superClass=AbstractInternalAntlrParser;
}

@lexer::header {
package com.b2international.snowowl.snomed.ecl.parser.antlr.internal;

// Hack: Use our own Lexer superclass by means of import. 
// Currently there is no other way to specify the superclass for the lexer.
import org.eclipse.xtext.parser.antlr.Lexer;
}

@parser::header {
package com.b2international.snowowl.snomed.ecl.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import com.b2international.snowowl.snomed.ecl.services.EclGrammarAccess;

}

@parser::members {

 	private EclGrammarAccess grammarAccess;

    public InternalEclParser(TokenStream input, EclGrammarAccess grammarAccess) {
        this(input);
        this.grammarAccess = grammarAccess;
        registerRules(grammarAccess.getGrammar());
    }

    @Override
    protected String getFirstRuleName() {
    	return "ExpressionConstraint";
   	}

   	@Override
   	protected EclGrammarAccess getGrammarAccess() {
   		return grammarAccess;
   	}

}

@rulecatch {
    catch (RecognitionException re) {
        recover(input,re);
        appendSkippedTokens();
    }
}

// Entry rule entryRuleExpressionConstraint
entryRuleExpressionConstraint returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getExpressionConstraintRule()); }
	iv_ruleExpressionConstraint=ruleExpressionConstraint
	{ $current=$iv_ruleExpressionConstraint.current; }
	EOF;

// Rule ExpressionConstraint
ruleExpressionConstraint returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	{
		newCompositeNode(grammarAccess.getExpressionConstraintAccess().getOrExpressionConstraintParserRuleCall());
	}
	this_OrExpressionConstraint_0=ruleOrExpressionConstraint
	{
		$current = $this_OrExpressionConstraint_0.current;
		afterParserOrEnumRuleCall();
	}
;

// Entry rule entryRuleOrExpressionConstraint
entryRuleOrExpressionConstraint returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getOrExpressionConstraintRule()); }
	iv_ruleOrExpressionConstraint=ruleOrExpressionConstraint
	{ $current=$iv_ruleOrExpressionConstraint.current; }
	EOF;

// Rule OrExpressionConstraint
ruleOrExpressionConstraint returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getOrExpressionConstraintAccess().getAndExpressionConstraintParserRuleCall_0());
		}
		this_AndExpressionConstraint_0=ruleAndExpressionConstraint
		{
			$current = $this_AndExpressionConstraint_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getOrExpressionConstraintAccess().getOrExpressionConstraintLeftAction_1_0(),
						$current);
				}
			)
			this_OR_2=RULE_OR
			{
				newLeafNode(this_OR_2, grammarAccess.getOrExpressionConstraintAccess().getORTerminalRuleCall_1_1());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getOrExpressionConstraintAccess().getRightAndExpressionConstraintParserRuleCall_1_2_0());
					}
					lv_right_3_0=ruleAndExpressionConstraint
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getOrExpressionConstraintRule());
						}
						set(
							$current,
							"right",
							lv_right_3_0,
							"com.b2international.snowowl.snomed.ecl.Ecl.AndExpressionConstraint");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)*
	)
;

// Entry rule entryRuleAndExpressionConstraint
entryRuleAndExpressionConstraint returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAndExpressionConstraintRule()); }
	iv_ruleAndExpressionConstraint=ruleAndExpressionConstraint
	{ $current=$iv_ruleAndExpressionConstraint.current; }
	EOF;

// Rule AndExpressionConstraint
ruleAndExpressionConstraint returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getExclusionExpressionConstraintParserRuleCall_0());
		}
		this_ExclusionExpressionConstraint_0=ruleExclusionExpressionConstraint
		{
			$current = $this_ExclusionExpressionConstraint_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getAndExpressionConstraintAccess().getAndExpressionConstraintLeftAction_1_0(),
						$current);
				}
			)
			{
				newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getAndOperatorParserRuleCall_1_1());
			}
			ruleAndOperator
			{
				afterParserOrEnumRuleCall();
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getRightExclusionExpressionConstraintParserRuleCall_1_2_0());
					}
					lv_right_3_0=ruleExclusionExpressionConstraint
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getAndExpressionConstraintRule());
						}
						set(
							$current,
							"right",
							lv_right_3_0,
							"com.b2international.snowowl.snomed.ecl.Ecl.ExclusionExpressionConstraint");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)*
	)
;

// Entry rule entryRuleExclusionExpressionConstraint
entryRuleExclusionExpressionConstraint returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getExclusionExpressionConstraintRule()); }
	iv_ruleExclusionExpressionConstraint=ruleExclusionExpressionConstraint
	{ $current=$iv_ruleExclusionExpressionConstraint.current; }
	EOF;

// Rule ExclusionExpressionConstraint
ruleExclusionExpressionConstraint returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRefinedExpressionConstraintParserRuleCall_0());
		}
		this_RefinedExpressionConstraint_0=ruleRefinedExpressionConstraint
		{
			$current = $this_RefinedExpressionConstraint_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getExclusionExpressionConstraintAccess().getExclusionExpressionConstraintLeftAction_1_0(),
						$current);
				}
			)
			this_MINUS_2=RULE_MINUS
			{
				newLeafNode(this_MINUS_2, grammarAccess.getExclusionExpressionConstraintAccess().getMINUSTerminalRuleCall_1_1());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getExclusionExpressionConstraintAccess().getRightRefinedExpressionConstraintParserRuleCall_1_2_0());
					}
					lv_right_3_0=ruleRefinedExpressionConstraint
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getExclusionExpressionConstraintRule());
						}
						set(
							$current,
							"right",
							lv_right_3_0,
							"com.b2international.snowowl.snomed.ecl.Ecl.RefinedExpressionConstraint");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)?
	)
;

// Entry rule entryRuleRefinedExpressionConstraint
entryRuleRefinedExpressionConstraint returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getRefinedExpressionConstraintRule()); }
	iv_ruleRefinedExpressionConstraint=ruleRefinedExpressionConstraint
	{ $current=$iv_ruleRefinedExpressionConstraint.current; }
	EOF;

// Rule RefinedExpressionConstraint
ruleRefinedExpressionConstraint returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getDottedExpressionConstraintParserRuleCall_0());
		}
		this_DottedExpressionConstraint_0=ruleDottedExpressionConstraint
		{
			$current = $this_DottedExpressionConstraint_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getRefinedExpressionConstraintAccess().getRefinedExpressionConstraintConstraintAction_1_0(),
						$current);
				}
			)
			this_COLON_2=RULE_COLON
			{
				newLeafNode(this_COLON_2, grammarAccess.getRefinedExpressionConstraintAccess().getCOLONTerminalRuleCall_1_1());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getRefinementRefinementParserRuleCall_1_2_0());
					}
					lv_refinement_3_0=ruleRefinement
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getRefinedExpressionConstraintRule());
						}
						set(
							$current,
							"refinement",
							lv_refinement_3_0,
							"com.b2international.snowowl.snomed.ecl.Ecl.Refinement");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)?
	)
;

// Entry rule entryRuleDottedExpressionConstraint
entryRuleDottedExpressionConstraint returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDottedExpressionConstraintRule()); }
	iv_ruleDottedExpressionConstraint=ruleDottedExpressionConstraint
	{ $current=$iv_ruleDottedExpressionConstraint.current; }
	EOF;

// Rule DottedExpressionConstraint
ruleDottedExpressionConstraint returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getSimpleExpressionConstraintParserRuleCall_0());
		}
		this_SimpleExpressionConstraint_0=ruleSimpleExpressionConstraint
		{
			$current = $this_SimpleExpressionConstraint_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getDottedExpressionConstraintAccess().getDottedExpressionConstraintConstraintAction_1_0(),
						$current);
				}
			)
			this_DOT_2=RULE_DOT
			{
				newLeafNode(this_DOT_2, grammarAccess.getDottedExpressionConstraintAccess().getDOTTerminalRuleCall_1_1());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getAttributeAttributeParserRuleCall_1_2_0());
					}
					lv_attribute_3_0=ruleAttribute
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getDottedExpressionConstraintRule());
						}
						set(
							$current,
							"attribute",
							lv_attribute_3_0,
							"com.b2international.snowowl.snomed.ecl.Ecl.Attribute");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)*
	)
;

// Entry rule entryRuleSimpleExpressionConstraint
entryRuleSimpleExpressionConstraint returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSimpleExpressionConstraintRule()); }
	iv_ruleSimpleExpressionConstraint=ruleSimpleExpressionConstraint
	{ $current=$iv_ruleSimpleExpressionConstraint.current; }
	EOF;

// Rule SimpleExpressionConstraint
ruleSimpleExpressionConstraint returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getChildOfParserRuleCall_0());
		}
		this_ChildOf_0=ruleChildOf
		{
			$current = $this_ChildOf_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getDescendantOfParserRuleCall_1());
		}
		this_DescendantOf_1=ruleDescendantOf
		{
			$current = $this_DescendantOf_1.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getDescendantOrSelfOfParserRuleCall_2());
		}
		this_DescendantOrSelfOf_2=ruleDescendantOrSelfOf
		{
			$current = $this_DescendantOrSelfOf_2.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getParentOfParserRuleCall_3());
		}
		this_ParentOf_3=ruleParentOf
		{
			$current = $this_ParentOf_3.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getAncestorOfParserRuleCall_4());
		}
		this_AncestorOf_4=ruleAncestorOf
		{
			$current = $this_AncestorOf_4.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getAncestorOrSelfOfParserRuleCall_5());
		}
		this_AncestorOrSelfOf_5=ruleAncestorOrSelfOf
		{
			$current = $this_AncestorOrSelfOf_5.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getSimpleExpressionConstraintAccess().getFocusConceptParserRuleCall_6());
		}
		this_FocusConcept_6=ruleFocusConcept
		{
			$current = $this_FocusConcept_6.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleFocusConcept
entryRuleFocusConcept returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getFocusConceptRule()); }
	iv_ruleFocusConcept=ruleFocusConcept
	{ $current=$iv_ruleFocusConcept.current; }
	EOF;

// Rule FocusConcept
ruleFocusConcept returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getFocusConceptAccess().getMemberOfParserRuleCall_0());
		}
		this_MemberOf_0=ruleMemberOf
		{
			$current = $this_MemberOf_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getFocusConceptAccess().getConceptReferenceParserRuleCall_1());
		}
		this_ConceptReference_1=ruleConceptReference
		{
			$current = $this_ConceptReference_1.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getFocusConceptAccess().getAnyParserRuleCall_2());
		}
		this_Any_2=ruleAny
		{
			$current = $this_Any_2.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getFocusConceptAccess().getNestedExpressionParserRuleCall_3());
		}
		this_NestedExpression_3=ruleNestedExpression
		{
			$current = $this_NestedExpression_3.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleChildOf
entryRuleChildOf returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getChildOfRule()); }
	iv_ruleChildOf=ruleChildOf
	{ $current=$iv_ruleChildOf.current; }
	EOF;

// Rule ChildOf
ruleChildOf returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_LT_EM_0=RULE_LT_EM
		{
			newLeafNode(this_LT_EM_0, grammarAccess.getChildOfAccess().getLT_EMTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getChildOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getChildOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleDescendantOf
entryRuleDescendantOf returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDescendantOfRule()); }
	iv_ruleDescendantOf=ruleDescendantOf
	{ $current=$iv_ruleDescendantOf.current; }
	EOF;

// Rule DescendantOf
ruleDescendantOf returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_LT_0=RULE_LT
		{
			newLeafNode(this_LT_0, grammarAccess.getDescendantOfAccess().getLTTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getDescendantOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getDescendantOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleDescendantOrSelfOf
entryRuleDescendantOrSelfOf returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDescendantOrSelfOfRule()); }
	iv_ruleDescendantOrSelfOf=ruleDescendantOrSelfOf
	{ $current=$iv_ruleDescendantOrSelfOf.current; }
	EOF;

// Rule DescendantOrSelfOf
ruleDescendantOrSelfOf returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_DBL_LT_0=RULE_DBL_LT
		{
			newLeafNode(this_DBL_LT_0, grammarAccess.getDescendantOrSelfOfAccess().getDBL_LTTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getDescendantOrSelfOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getDescendantOrSelfOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleParentOf
entryRuleParentOf returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getParentOfRule()); }
	iv_ruleParentOf=ruleParentOf
	{ $current=$iv_ruleParentOf.current; }
	EOF;

// Rule ParentOf
ruleParentOf returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_GT_EM_0=RULE_GT_EM
		{
			newLeafNode(this_GT_EM_0, grammarAccess.getParentOfAccess().getGT_EMTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getParentOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getParentOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleAncestorOf
entryRuleAncestorOf returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAncestorOfRule()); }
	iv_ruleAncestorOf=ruleAncestorOf
	{ $current=$iv_ruleAncestorOf.current; }
	EOF;

// Rule AncestorOf
ruleAncestorOf returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_GT_0=RULE_GT
		{
			newLeafNode(this_GT_0, grammarAccess.getAncestorOfAccess().getGTTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getAncestorOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAncestorOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleAncestorOrSelfOf
entryRuleAncestorOrSelfOf returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAncestorOrSelfOfRule()); }
	iv_ruleAncestorOrSelfOf=ruleAncestorOrSelfOf
	{ $current=$iv_ruleAncestorOrSelfOf.current; }
	EOF;

// Rule AncestorOrSelfOf
ruleAncestorOrSelfOf returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_DBL_GT_0=RULE_DBL_GT
		{
			newLeafNode(this_DBL_GT_0, grammarAccess.getAncestorOrSelfOfAccess().getDBL_GTTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getAncestorOrSelfOfAccess().getConstraintFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAncestorOrSelfOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleMemberOf
entryRuleMemberOf returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getMemberOfRule()); }
	iv_ruleMemberOf=ruleMemberOf
	{ $current=$iv_ruleMemberOf.current; }
	EOF;

// Rule MemberOf
ruleMemberOf returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_CARET_0=RULE_CARET
		{
			newLeafNode(this_CARET_0, grammarAccess.getMemberOfAccess().getCARETTerminalRuleCall_0());
		}
		(
			(
				(
					{
						newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintConceptReferenceParserRuleCall_1_0_0());
					}
					lv_constraint_1_1=ruleConceptReference
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getMemberOfRule());
						}
						set(
							$current,
							"constraint",
							lv_constraint_1_1,
							"com.b2international.snowowl.snomed.ecl.Ecl.ConceptReference");
						afterParserOrEnumRuleCall();
					}
					    |
					{
						newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintAnyParserRuleCall_1_0_1());
					}
					lv_constraint_1_2=ruleAny
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getMemberOfRule());
						}
						set(
							$current,
							"constraint",
							lv_constraint_1_2,
							"com.b2international.snowowl.snomed.ecl.Ecl.Any");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)
	)
;

// Entry rule entryRuleConceptReference
entryRuleConceptReference returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getConceptReferenceRule()); }
	iv_ruleConceptReference=ruleConceptReference
	{ $current=$iv_ruleConceptReference.current; }
	EOF;

// Rule ConceptReference
ruleConceptReference returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			(
				{
					newCompositeNode(grammarAccess.getConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_0_0());
				}
				lv_id_0_0=ruleSnomedIdentifier
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getConceptReferenceRule());
					}
					set(
						$current,
						"id",
						lv_id_0_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.SnomedIdentifier");
					afterParserOrEnumRuleCall();
				}
			)
		)
		(
			this_PIPE_1=RULE_PIPE
			{
				newLeafNode(this_PIPE_1, grammarAccess.getConceptReferenceAccess().getPIPETerminalRuleCall_1_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getConceptReferenceAccess().getTermTermParserRuleCall_1_1_0());
					}
					lv_term_2_0=ruleTerm
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getConceptReferenceRule());
						}
						set(
							$current,
							"term",
							lv_term_2_0,
							"com.b2international.snowowl.snomed.ecl.Ecl.Term");
						afterParserOrEnumRuleCall();
					}
				)
			)
			this_PIPE_3=RULE_PIPE
			{
				newLeafNode(this_PIPE_3, grammarAccess.getConceptReferenceAccess().getPIPETerminalRuleCall_1_2());
			}
		)?
	)
;

// Entry rule entryRuleAny
entryRuleAny returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAnyRule()); }
	iv_ruleAny=ruleAny
	{ $current=$iv_ruleAny.current; }
	EOF;

// Rule Any
ruleAny returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_WILDCARD_0=RULE_WILDCARD
		{
			newLeafNode(this_WILDCARD_0, grammarAccess.getAnyAccess().getWILDCARDTerminalRuleCall_0());
		}
		(
			{
				$current = forceCreateModelElement(
					grammarAccess.getAnyAccess().getAnyAction_1(),
					$current);
			}
		)
	)
;

// Entry rule entryRuleRefinement
entryRuleRefinement returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getRefinementRule()); }
	iv_ruleRefinement=ruleRefinement
	{ $current=$iv_ruleRefinement.current; }
	EOF;

// Rule Refinement
ruleRefinement returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	{
		newCompositeNode(grammarAccess.getRefinementAccess().getOrRefinementParserRuleCall());
	}
	this_OrRefinement_0=ruleOrRefinement
	{
		$current = $this_OrRefinement_0.current;
		afterParserOrEnumRuleCall();
	}
;

// Entry rule entryRuleOrRefinement
entryRuleOrRefinement returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getOrRefinementRule()); }
	iv_ruleOrRefinement=ruleOrRefinement
	{ $current=$iv_ruleOrRefinement.current; }
	EOF;

// Rule OrRefinement
ruleOrRefinement returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getOrRefinementAccess().getAndRefinementParserRuleCall_0());
		}
		this_AndRefinement_0=ruleAndRefinement
		{
			$current = $this_AndRefinement_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(RULE_OR)=>
			(
				(
					{
						$current = forceCreateModelElementAndSet(
							grammarAccess.getOrRefinementAccess().getOrRefinementLeftAction_1_0_0(),
							$current);
					}
				)
				this_OR_2=RULE_OR
				{
					newLeafNode(this_OR_2, grammarAccess.getOrRefinementAccess().getORTerminalRuleCall_1_0_1());
				}
				(
					(
						{
							newCompositeNode(grammarAccess.getOrRefinementAccess().getRightAndRefinementParserRuleCall_1_0_2_0());
						}
						lv_right_3_0=ruleAndRefinement
						{
							if ($current==null) {
								$current = createModelElementForParent(grammarAccess.getOrRefinementRule());
							}
							set(
								$current,
								"right",
								lv_right_3_0,
								"com.b2international.snowowl.snomed.ecl.Ecl.AndRefinement");
							afterParserOrEnumRuleCall();
						}
					)
				)
			)
		)*
	)
;

// Entry rule entryRuleAndRefinement
entryRuleAndRefinement returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAndRefinementRule()); }
	iv_ruleAndRefinement=ruleAndRefinement
	{ $current=$iv_ruleAndRefinement.current; }
	EOF;

// Rule AndRefinement
ruleAndRefinement returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getAndRefinementAccess().getSubRefinementParserRuleCall_0());
		}
		this_SubRefinement_0=ruleSubRefinement
		{
			$current = $this_SubRefinement_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(RULE_AND | RULE_COMMA)=>
			(
				(
					{
						$current = forceCreateModelElementAndSet(
							grammarAccess.getAndRefinementAccess().getAndRefinementLeftAction_1_0_0(),
							$current);
					}
				)
				{
					newCompositeNode(grammarAccess.getAndRefinementAccess().getAndOperatorParserRuleCall_1_0_1());
				}
				ruleAndOperator
				{
					afterParserOrEnumRuleCall();
				}
				(
					(
						{
							newCompositeNode(grammarAccess.getAndRefinementAccess().getRightSubRefinementParserRuleCall_1_0_2_0());
						}
						lv_right_3_0=ruleSubRefinement
						{
							if ($current==null) {
								$current = createModelElementForParent(grammarAccess.getAndRefinementRule());
							}
							set(
								$current,
								"right",
								lv_right_3_0,
								"com.b2international.snowowl.snomed.ecl.Ecl.SubRefinement");
							afterParserOrEnumRuleCall();
						}
					)
				)
			)
		)*
	)
;

// Entry rule entryRuleSubRefinement
entryRuleSubRefinement returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSubRefinementRule()); }
	iv_ruleSubRefinement=ruleSubRefinement
	{ $current=$iv_ruleSubRefinement.current; }
	EOF;

// Rule SubRefinement
ruleSubRefinement returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getSubRefinementAccess().getAttributeConstraintParserRuleCall_0());
		}
		this_AttributeConstraint_0=ruleAttributeConstraint
		{
			$current = $this_AttributeConstraint_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getSubRefinementAccess().getAttributeGroupParserRuleCall_1());
		}
		this_AttributeGroup_1=ruleAttributeGroup
		{
			$current = $this_AttributeGroup_1.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getSubRefinementAccess().getNestedRefinementParserRuleCall_2());
		}
		this_NestedRefinement_2=ruleNestedRefinement
		{
			$current = $this_NestedRefinement_2.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleNestedRefinement
entryRuleNestedRefinement returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getNestedRefinementRule()); }
	iv_ruleNestedRefinement=ruleNestedRefinement
	{ $current=$iv_ruleNestedRefinement.current; }
	EOF;

// Rule NestedRefinement
ruleNestedRefinement returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_ROUND_OPEN_0=RULE_ROUND_OPEN
		{
			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedRefinementAccess().getROUND_OPENTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getNestedRefinementAccess().getNestedRefinementParserRuleCall_1_0());
				}
				lv_nested_1_0=ruleRefinement
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getNestedRefinementRule());
					}
					set(
						$current,
						"nested",
						lv_nested_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Refinement");
					afterParserOrEnumRuleCall();
				}
			)
		)
		this_ROUND_CLOSE_2=RULE_ROUND_CLOSE
		{
			newLeafNode(this_ROUND_CLOSE_2, grammarAccess.getNestedRefinementAccess().getROUND_CLOSETerminalRuleCall_2());
		}
	)
;

// Entry rule entryRuleAttributeGroup
entryRuleAttributeGroup returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAttributeGroupRule()); }
	iv_ruleAttributeGroup=ruleAttributeGroup
	{ $current=$iv_ruleAttributeGroup.current; }
	EOF;

// Rule AttributeGroup
ruleAttributeGroup returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeGroupAccess().getCardinalityCardinalityParserRuleCall_0_0());
				}
				lv_cardinality_0_0=ruleCardinality
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
					}
					set(
						$current,
						"cardinality",
						lv_cardinality_0_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Cardinality");
					afterParserOrEnumRuleCall();
				}
			)
		)?
		this_CURLY_OPEN_1=RULE_CURLY_OPEN
		{
			newLeafNode(this_CURLY_OPEN_1, grammarAccess.getAttributeGroupAccess().getCURLY_OPENTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeGroupAccess().getRefinementAttributeSetParserRuleCall_2_0());
				}
				lv_refinement_2_0=ruleAttributeSet
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
					}
					set(
						$current,
						"refinement",
						lv_refinement_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.AttributeSet");
					afterParserOrEnumRuleCall();
				}
			)
		)
		this_CURLY_CLOSE_3=RULE_CURLY_CLOSE
		{
			newLeafNode(this_CURLY_CLOSE_3, grammarAccess.getAttributeGroupAccess().getCURLY_CLOSETerminalRuleCall_3());
		}
	)
;

// Entry rule entryRuleAttributeSet
entryRuleAttributeSet returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAttributeSetRule()); }
	iv_ruleAttributeSet=ruleAttributeSet
	{ $current=$iv_ruleAttributeSet.current; }
	EOF;

// Rule AttributeSet
ruleAttributeSet returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	{
		newCompositeNode(grammarAccess.getAttributeSetAccess().getOrAttributeSetParserRuleCall());
	}
	this_OrAttributeSet_0=ruleOrAttributeSet
	{
		$current = $this_OrAttributeSet_0.current;
		afterParserOrEnumRuleCall();
	}
;

// Entry rule entryRuleOrAttributeSet
entryRuleOrAttributeSet returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getOrAttributeSetRule()); }
	iv_ruleOrAttributeSet=ruleOrAttributeSet
	{ $current=$iv_ruleOrAttributeSet.current; }
	EOF;

// Rule OrAttributeSet
ruleOrAttributeSet returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getOrAttributeSetAccess().getAndAttributeSetParserRuleCall_0());
		}
		this_AndAttributeSet_0=ruleAndAttributeSet
		{
			$current = $this_AndAttributeSet_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getOrAttributeSetAccess().getOrRefinementLeftAction_1_0(),
						$current);
				}
			)
			this_OR_2=RULE_OR
			{
				newLeafNode(this_OR_2, grammarAccess.getOrAttributeSetAccess().getORTerminalRuleCall_1_1());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getOrAttributeSetAccess().getRightAndAttributeSetParserRuleCall_1_2_0());
					}
					lv_right_3_0=ruleAndAttributeSet
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getOrAttributeSetRule());
						}
						set(
							$current,
							"right",
							lv_right_3_0,
							"com.b2international.snowowl.snomed.ecl.Ecl.AndAttributeSet");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)*
	)
;

// Entry rule entryRuleAndAttributeSet
entryRuleAndAttributeSet returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAndAttributeSetRule()); }
	iv_ruleAndAttributeSet=ruleAndAttributeSet
	{ $current=$iv_ruleAndAttributeSet.current; }
	EOF;

// Rule AndAttributeSet
ruleAndAttributeSet returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getAndAttributeSetAccess().getSubAttributeSetParserRuleCall_0());
		}
		this_SubAttributeSet_0=ruleSubAttributeSet
		{
			$current = $this_SubAttributeSet_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getAndAttributeSetAccess().getAndRefinementLeftAction_1_0(),
						$current);
				}
			)
			{
				newCompositeNode(grammarAccess.getAndAttributeSetAccess().getAndOperatorParserRuleCall_1_1());
			}
			ruleAndOperator
			{
				afterParserOrEnumRuleCall();
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getAndAttributeSetAccess().getRightSubAttributeSetParserRuleCall_1_2_0());
					}
					lv_right_3_0=ruleSubAttributeSet
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getAndAttributeSetRule());
						}
						set(
							$current,
							"right",
							lv_right_3_0,
							"com.b2international.snowowl.snomed.ecl.Ecl.SubAttributeSet");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)*
	)
;

// Entry rule entryRuleSubAttributeSet
entryRuleSubAttributeSet returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSubAttributeSetRule()); }
	iv_ruleSubAttributeSet=ruleSubAttributeSet
	{ $current=$iv_ruleSubAttributeSet.current; }
	EOF;

// Rule SubAttributeSet
ruleSubAttributeSet returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getSubAttributeSetAccess().getAttributeConstraintParserRuleCall_0());
		}
		this_AttributeConstraint_0=ruleAttributeConstraint
		{
			$current = $this_AttributeConstraint_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getSubAttributeSetAccess().getNestedAttributeSetParserRuleCall_1());
		}
		this_NestedAttributeSet_1=ruleNestedAttributeSet
		{
			$current = $this_NestedAttributeSet_1.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleNestedAttributeSet
entryRuleNestedAttributeSet returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getNestedAttributeSetRule()); }
	iv_ruleNestedAttributeSet=ruleNestedAttributeSet
	{ $current=$iv_ruleNestedAttributeSet.current; }
	EOF;

// Rule NestedAttributeSet
ruleNestedAttributeSet returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_ROUND_OPEN_0=RULE_ROUND_OPEN
		{
			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedAttributeSetAccess().getROUND_OPENTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getNestedAttributeSetAccess().getNestedAttributeSetParserRuleCall_1_0());
				}
				lv_nested_1_0=ruleAttributeSet
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getNestedAttributeSetRule());
					}
					set(
						$current,
						"nested",
						lv_nested_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.AttributeSet");
					afterParserOrEnumRuleCall();
				}
			)
		)
		this_ROUND_CLOSE_2=RULE_ROUND_CLOSE
		{
			newLeafNode(this_ROUND_CLOSE_2, grammarAccess.getNestedAttributeSetAccess().getROUND_CLOSETerminalRuleCall_2());
		}
	)
;

// Entry rule entryRuleAttributeConstraint
entryRuleAttributeConstraint returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAttributeConstraintRule()); }
	iv_ruleAttributeConstraint=ruleAttributeConstraint
	{ $current=$iv_ruleAttributeConstraint.current; }
	EOF;

// Rule AttributeConstraint
ruleAttributeConstraint returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeConstraintAccess().getCardinalityCardinalityParserRuleCall_0_0());
				}
				lv_cardinality_0_0=ruleCardinality
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
					}
					set(
						$current,
						"cardinality",
						lv_cardinality_0_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Cardinality");
					afterParserOrEnumRuleCall();
				}
			)
		)?
		(
			(
				lv_reversed_1_0=RULE_REVERSED
				{
					newLeafNode(lv_reversed_1_0, grammarAccess.getAttributeConstraintAccess().getReversedREVERSEDTerminalRuleCall_1_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getAttributeConstraintRule());
					}
					setWithLastConsumed(
						$current,
						"reversed",
						true,
						"com.b2international.snowowl.snomed.ecl.Ecl.REVERSED");
				}
			)
		)?
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeConstraintAccess().getAttributeAttributeParserRuleCall_2_0());
				}
				lv_attribute_2_0=ruleAttribute
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
					}
					set(
						$current,
						"attribute",
						lv_attribute_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Attribute");
					afterParserOrEnumRuleCall();
				}
			)
		)
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeConstraintAccess().getComparisonComparisonParserRuleCall_3_0());
				}
				lv_comparison_3_0=ruleComparison
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
					}
					set(
						$current,
						"comparison",
						lv_comparison_3_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Comparison");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleAttribute
entryRuleAttribute returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAttributeRule()); }
	iv_ruleAttribute=ruleAttribute
	{ $current=$iv_ruleAttribute.current; }
	EOF;

// Rule Attribute
ruleAttribute returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getAttributeAccess().getAttributeDescendantOfParserRuleCall_0());
		}
		this_AttributeDescendantOf_0=ruleAttributeDescendantOf
		{
			$current = $this_AttributeDescendantOf_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getAttributeAccess().getAttributeDescendantOrSelfOfParserRuleCall_1());
		}
		this_AttributeDescendantOrSelfOf_1=ruleAttributeDescendantOrSelfOf
		{
			$current = $this_AttributeDescendantOrSelfOf_1.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getAttributeAccess().getConceptReferenceParserRuleCall_2());
		}
		this_ConceptReference_2=ruleConceptReference
		{
			$current = $this_ConceptReference_2.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getAttributeAccess().getAnyParserRuleCall_3());
		}
		this_Any_3=ruleAny
		{
			$current = $this_Any_3.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleAttributeDescendantOf
entryRuleAttributeDescendantOf returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAttributeDescendantOfRule()); }
	iv_ruleAttributeDescendantOf=ruleAttributeDescendantOf
	{ $current=$iv_ruleAttributeDescendantOf.current; }
	EOF;

// Rule AttributeDescendantOf
ruleAttributeDescendantOf returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_LT_0=RULE_LT
		{
			newLeafNode(this_LT_0, grammarAccess.getAttributeDescendantOfAccess().getLTTerminalRuleCall_0());
		}
		(
			(
				(
					{
						newCompositeNode(grammarAccess.getAttributeDescendantOfAccess().getConstraintConceptReferenceParserRuleCall_1_0_0());
					}
					lv_constraint_1_1=ruleConceptReference
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getAttributeDescendantOfRule());
						}
						set(
							$current,
							"constraint",
							lv_constraint_1_1,
							"com.b2international.snowowl.snomed.ecl.Ecl.ConceptReference");
						afterParserOrEnumRuleCall();
					}
					    |
					{
						newCompositeNode(grammarAccess.getAttributeDescendantOfAccess().getConstraintAnyParserRuleCall_1_0_1());
					}
					lv_constraint_1_2=ruleAny
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getAttributeDescendantOfRule());
						}
						set(
							$current,
							"constraint",
							lv_constraint_1_2,
							"com.b2international.snowowl.snomed.ecl.Ecl.Any");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)
	)
;

// Entry rule entryRuleAttributeDescendantOrSelfOf
entryRuleAttributeDescendantOrSelfOf returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAttributeDescendantOrSelfOfRule()); }
	iv_ruleAttributeDescendantOrSelfOf=ruleAttributeDescendantOrSelfOf
	{ $current=$iv_ruleAttributeDescendantOrSelfOf.current; }
	EOF;

// Rule AttributeDescendantOrSelfOf
ruleAttributeDescendantOrSelfOf returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_DBL_LT_0=RULE_DBL_LT
		{
			newLeafNode(this_DBL_LT_0, grammarAccess.getAttributeDescendantOrSelfOfAccess().getDBL_LTTerminalRuleCall_0());
		}
		(
			(
				(
					{
						newCompositeNode(grammarAccess.getAttributeDescendantOrSelfOfAccess().getConstraintConceptReferenceParserRuleCall_1_0_0());
					}
					lv_constraint_1_1=ruleConceptReference
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getAttributeDescendantOrSelfOfRule());
						}
						set(
							$current,
							"constraint",
							lv_constraint_1_1,
							"com.b2international.snowowl.snomed.ecl.Ecl.ConceptReference");
						afterParserOrEnumRuleCall();
					}
					    |
					{
						newCompositeNode(grammarAccess.getAttributeDescendantOrSelfOfAccess().getConstraintAnyParserRuleCall_1_0_1());
					}
					lv_constraint_1_2=ruleAny
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getAttributeDescendantOrSelfOfRule());
						}
						set(
							$current,
							"constraint",
							lv_constraint_1_2,
							"com.b2international.snowowl.snomed.ecl.Ecl.Any");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)
	)
;

// Entry rule entryRuleCardinality
entryRuleCardinality returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getCardinalityRule()); }
	iv_ruleCardinality=ruleCardinality
	{ $current=$iv_ruleCardinality.current; }
	EOF;

// Rule Cardinality
ruleCardinality returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_SQUARE_OPEN_0=RULE_SQUARE_OPEN
		{
			newLeafNode(this_SQUARE_OPEN_0, grammarAccess.getCardinalityAccess().getSQUARE_OPENTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getCardinalityAccess().getMinNonNegativeIntegerParserRuleCall_1_0());
				}
				lv_min_1_0=ruleNonNegativeInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getCardinalityRule());
					}
					set(
						$current,
						"min",
						lv_min_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.NonNegativeInteger");
					afterParserOrEnumRuleCall();
				}
			)
		)
		this_TO_2=RULE_TO
		{
			newLeafNode(this_TO_2, grammarAccess.getCardinalityAccess().getTOTerminalRuleCall_2());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getCardinalityAccess().getMaxMaxValueParserRuleCall_3_0());
				}
				lv_max_3_0=ruleMaxValue
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getCardinalityRule());
					}
					set(
						$current,
						"max",
						lv_max_3_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.MaxValue");
					afterParserOrEnumRuleCall();
				}
			)
		)
		this_SQUARE_CLOSE_4=RULE_SQUARE_CLOSE
		{
			newLeafNode(this_SQUARE_CLOSE_4, grammarAccess.getCardinalityAccess().getSQUARE_CLOSETerminalRuleCall_4());
		}
	)
;

// Entry rule entryRuleComparison
entryRuleComparison returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getComparisonRule()); }
	iv_ruleComparison=ruleComparison
	{ $current=$iv_ruleComparison.current; }
	EOF;

// Rule Comparison
ruleComparison returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getComparisonAccess().getAttributeComparisonParserRuleCall_0());
		}
		this_AttributeComparison_0=ruleAttributeComparison
		{
			$current = $this_AttributeComparison_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getComparisonAccess().getDataTypeComparisonParserRuleCall_1());
		}
		this_DataTypeComparison_1=ruleDataTypeComparison
		{
			$current = $this_DataTypeComparison_1.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleAttributeComparison
entryRuleAttributeComparison returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAttributeComparisonRule()); }
	iv_ruleAttributeComparison=ruleAttributeComparison
	{ $current=$iv_ruleAttributeComparison.current; }
	EOF;

// Rule AttributeComparison
ruleAttributeComparison returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getAttributeComparisonAccess().getAttributeValueEqualsParserRuleCall_0());
		}
		this_AttributeValueEquals_0=ruleAttributeValueEquals
		{
			$current = $this_AttributeValueEquals_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getAttributeComparisonAccess().getAttributeValueNotEqualsParserRuleCall_1());
		}
		this_AttributeValueNotEquals_1=ruleAttributeValueNotEquals
		{
			$current = $this_AttributeValueNotEquals_1.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleDataTypeComparison
entryRuleDataTypeComparison returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDataTypeComparisonRule()); }
	iv_ruleDataTypeComparison=ruleDataTypeComparison
	{ $current=$iv_ruleDataTypeComparison.current; }
	EOF;

// Rule DataTypeComparison
ruleDataTypeComparison returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getStringValueEqualsParserRuleCall_0());
		}
		this_StringValueEquals_0=ruleStringValueEquals
		{
			$current = $this_StringValueEquals_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getStringValueNotEqualsParserRuleCall_1());
		}
		this_StringValueNotEquals_1=ruleStringValueNotEquals
		{
			$current = $this_StringValueNotEquals_1.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueEqualsParserRuleCall_2());
		}
		this_IntegerValueEquals_2=ruleIntegerValueEquals
		{
			$current = $this_IntegerValueEquals_2.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueNotEqualsParserRuleCall_3());
		}
		this_IntegerValueNotEquals_3=ruleIntegerValueNotEquals
		{
			$current = $this_IntegerValueNotEquals_3.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueGreaterThanParserRuleCall_4());
		}
		this_IntegerValueGreaterThan_4=ruleIntegerValueGreaterThan
		{
			$current = $this_IntegerValueGreaterThan_4.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueGreaterThanEqualsParserRuleCall_5());
		}
		this_IntegerValueGreaterThanEquals_5=ruleIntegerValueGreaterThanEquals
		{
			$current = $this_IntegerValueGreaterThanEquals_5.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueLessThanParserRuleCall_6());
		}
		this_IntegerValueLessThan_6=ruleIntegerValueLessThan
		{
			$current = $this_IntegerValueLessThan_6.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueLessThanEqualsParserRuleCall_7());
		}
		this_IntegerValueLessThanEquals_7=ruleIntegerValueLessThanEquals
		{
			$current = $this_IntegerValueLessThanEquals_7.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueEqualsParserRuleCall_8());
		}
		this_DecimalValueEquals_8=ruleDecimalValueEquals
		{
			$current = $this_DecimalValueEquals_8.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueNotEqualsParserRuleCall_9());
		}
		this_DecimalValueNotEquals_9=ruleDecimalValueNotEquals
		{
			$current = $this_DecimalValueNotEquals_9.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueGreaterThanParserRuleCall_10());
		}
		this_DecimalValueGreaterThan_10=ruleDecimalValueGreaterThan
		{
			$current = $this_DecimalValueGreaterThan_10.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueGreaterThanEqualsParserRuleCall_11());
		}
		this_DecimalValueGreaterThanEquals_11=ruleDecimalValueGreaterThanEquals
		{
			$current = $this_DecimalValueGreaterThanEquals_11.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueLessThanParserRuleCall_12());
		}
		this_DecimalValueLessThan_12=ruleDecimalValueLessThan
		{
			$current = $this_DecimalValueLessThan_12.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueLessThanEqualsParserRuleCall_13());
		}
		this_DecimalValueLessThanEquals_13=ruleDecimalValueLessThanEquals
		{
			$current = $this_DecimalValueLessThanEquals_13.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleAttributeValueEquals
entryRuleAttributeValueEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAttributeValueEqualsRule()); }
	iv_ruleAttributeValueEquals=ruleAttributeValueEquals
	{ $current=$iv_ruleAttributeValueEquals.current; }
	EOF;

// Rule AttributeValueEquals
ruleAttributeValueEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_EQUAL_0=RULE_EQUAL
		{
			newLeafNode(this_EQUAL_0, grammarAccess.getAttributeValueEqualsAccess().getEQUALTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeValueEqualsAccess().getConstraintSimpleExpressionConstraintParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleSimpleExpressionConstraint
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeValueEqualsRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.SimpleExpressionConstraint");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleAttributeValueNotEquals
entryRuleAttributeValueNotEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAttributeValueNotEqualsRule()); }
	iv_ruleAttributeValueNotEquals=ruleAttributeValueNotEquals
	{ $current=$iv_ruleAttributeValueNotEquals.current; }
	EOF;

// Rule AttributeValueNotEquals
ruleAttributeValueNotEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_NOT_EQUAL_0=RULE_NOT_EQUAL
		{
			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getAttributeValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeValueNotEqualsAccess().getConstraintSimpleExpressionConstraintParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleSimpleExpressionConstraint
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeValueNotEqualsRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.SimpleExpressionConstraint");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleStringValueEquals
entryRuleStringValueEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getStringValueEqualsRule()); }
	iv_ruleStringValueEquals=ruleStringValueEquals
	{ $current=$iv_ruleStringValueEquals.current; }
	EOF;

// Rule StringValueEquals
ruleStringValueEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_EQUAL_0=RULE_EQUAL
		{
			newLeafNode(this_EQUAL_0, grammarAccess.getStringValueEqualsAccess().getEQUALTerminalRuleCall_0());
		}
		(
			(
				lv_value_1_0=RULE_STRING
				{
					newLeafNode(lv_value_1_0, grammarAccess.getStringValueEqualsAccess().getValueSTRINGTerminalRuleCall_1_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getStringValueEqualsRule());
					}
					setWithLastConsumed(
						$current,
						"value",
						lv_value_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.STRING");
				}
			)
		)
	)
;

// Entry rule entryRuleStringValueNotEquals
entryRuleStringValueNotEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getStringValueNotEqualsRule()); }
	iv_ruleStringValueNotEquals=ruleStringValueNotEquals
	{ $current=$iv_ruleStringValueNotEquals.current; }
	EOF;

// Rule StringValueNotEquals
ruleStringValueNotEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_NOT_EQUAL_0=RULE_NOT_EQUAL
		{
			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getStringValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
		}
		(
			(
				lv_value_1_0=RULE_STRING
				{
					newLeafNode(lv_value_1_0, grammarAccess.getStringValueNotEqualsAccess().getValueSTRINGTerminalRuleCall_1_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getStringValueNotEqualsRule());
					}
					setWithLastConsumed(
						$current,
						"value",
						lv_value_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.STRING");
				}
			)
		)
	)
;

// Entry rule entryRuleIntegerValueEquals
entryRuleIntegerValueEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getIntegerValueEqualsRule()); }
	iv_ruleIntegerValueEquals=ruleIntegerValueEquals
	{ $current=$iv_ruleIntegerValueEquals.current; }
	EOF;

// Rule IntegerValueEquals
ruleIntegerValueEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_EQUAL_0=RULE_EQUAL
		{
			newLeafNode(this_EQUAL_0, grammarAccess.getIntegerValueEqualsAccess().getEQUALTerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueEqualsAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getIntegerValueEqualsAccess().getValueIntegerParserRuleCall_2_0());
				}
				lv_value_2_0=ruleInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getIntegerValueEqualsRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleIntegerValueNotEquals
entryRuleIntegerValueNotEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getIntegerValueNotEqualsRule()); }
	iv_ruleIntegerValueNotEquals=ruleIntegerValueNotEquals
	{ $current=$iv_ruleIntegerValueNotEquals.current; }
	EOF;

// Rule IntegerValueNotEquals
ruleIntegerValueNotEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_NOT_EQUAL_0=RULE_NOT_EQUAL
		{
			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getIntegerValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueNotEqualsAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getIntegerValueNotEqualsAccess().getValueIntegerParserRuleCall_2_0());
				}
				lv_value_2_0=ruleInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getIntegerValueNotEqualsRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleIntegerValueGreaterThan
entryRuleIntegerValueGreaterThan returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getIntegerValueGreaterThanRule()); }
	iv_ruleIntegerValueGreaterThan=ruleIntegerValueGreaterThan
	{ $current=$iv_ruleIntegerValueGreaterThan.current; }
	EOF;

// Rule IntegerValueGreaterThan
ruleIntegerValueGreaterThan returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_GT_0=RULE_GT
		{
			newLeafNode(this_GT_0, grammarAccess.getIntegerValueGreaterThanAccess().getGTTerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueGreaterThanAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getIntegerValueGreaterThanAccess().getValueIntegerParserRuleCall_2_0());
				}
				lv_value_2_0=ruleInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getIntegerValueGreaterThanRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleIntegerValueLessThan
entryRuleIntegerValueLessThan returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getIntegerValueLessThanRule()); }
	iv_ruleIntegerValueLessThan=ruleIntegerValueLessThan
	{ $current=$iv_ruleIntegerValueLessThan.current; }
	EOF;

// Rule IntegerValueLessThan
ruleIntegerValueLessThan returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_LT_0=RULE_LT
		{
			newLeafNode(this_LT_0, grammarAccess.getIntegerValueLessThanAccess().getLTTerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueLessThanAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getIntegerValueLessThanAccess().getValueIntegerParserRuleCall_2_0());
				}
				lv_value_2_0=ruleInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getIntegerValueLessThanRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleIntegerValueGreaterThanEquals
entryRuleIntegerValueGreaterThanEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getIntegerValueGreaterThanEqualsRule()); }
	iv_ruleIntegerValueGreaterThanEquals=ruleIntegerValueGreaterThanEquals
	{ $current=$iv_ruleIntegerValueGreaterThanEquals.current; }
	EOF;

// Rule IntegerValueGreaterThanEquals
ruleIntegerValueGreaterThanEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_GTE_0=RULE_GTE
		{
			newLeafNode(this_GTE_0, grammarAccess.getIntegerValueGreaterThanEqualsAccess().getGTETerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueGreaterThanEqualsAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getIntegerValueGreaterThanEqualsAccess().getValueIntegerParserRuleCall_2_0());
				}
				lv_value_2_0=ruleInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getIntegerValueGreaterThanEqualsRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleIntegerValueLessThanEquals
entryRuleIntegerValueLessThanEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getIntegerValueLessThanEqualsRule()); }
	iv_ruleIntegerValueLessThanEquals=ruleIntegerValueLessThanEquals
	{ $current=$iv_ruleIntegerValueLessThanEquals.current; }
	EOF;

// Rule IntegerValueLessThanEquals
ruleIntegerValueLessThanEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_LTE_0=RULE_LTE
		{
			newLeafNode(this_LTE_0, grammarAccess.getIntegerValueLessThanEqualsAccess().getLTETerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getIntegerValueLessThanEqualsAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getIntegerValueLessThanEqualsAccess().getValueIntegerParserRuleCall_2_0());
				}
				lv_value_2_0=ruleInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getIntegerValueLessThanEqualsRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Integer");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleDecimalValueEquals
entryRuleDecimalValueEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDecimalValueEqualsRule()); }
	iv_ruleDecimalValueEquals=ruleDecimalValueEquals
	{ $current=$iv_ruleDecimalValueEquals.current; }
	EOF;

// Rule DecimalValueEquals
ruleDecimalValueEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_EQUAL_0=RULE_EQUAL
		{
			newLeafNode(this_EQUAL_0, grammarAccess.getDecimalValueEqualsAccess().getEQUALTerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueEqualsAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getDecimalValueEqualsAccess().getValueDecimalParserRuleCall_2_0());
				}
				lv_value_2_0=ruleDecimal
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getDecimalValueEqualsRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleDecimalValueNotEquals
entryRuleDecimalValueNotEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDecimalValueNotEqualsRule()); }
	iv_ruleDecimalValueNotEquals=ruleDecimalValueNotEquals
	{ $current=$iv_ruleDecimalValueNotEquals.current; }
	EOF;

// Rule DecimalValueNotEquals
ruleDecimalValueNotEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_NOT_EQUAL_0=RULE_NOT_EQUAL
		{
			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getDecimalValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueNotEqualsAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getDecimalValueNotEqualsAccess().getValueDecimalParserRuleCall_2_0());
				}
				lv_value_2_0=ruleDecimal
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getDecimalValueNotEqualsRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleDecimalValueGreaterThan
entryRuleDecimalValueGreaterThan returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDecimalValueGreaterThanRule()); }
	iv_ruleDecimalValueGreaterThan=ruleDecimalValueGreaterThan
	{ $current=$iv_ruleDecimalValueGreaterThan.current; }
	EOF;

// Rule DecimalValueGreaterThan
ruleDecimalValueGreaterThan returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_GT_0=RULE_GT
		{
			newLeafNode(this_GT_0, grammarAccess.getDecimalValueGreaterThanAccess().getGTTerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueGreaterThanAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getDecimalValueGreaterThanAccess().getValueDecimalParserRuleCall_2_0());
				}
				lv_value_2_0=ruleDecimal
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getDecimalValueGreaterThanRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleDecimalValueLessThan
entryRuleDecimalValueLessThan returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDecimalValueLessThanRule()); }
	iv_ruleDecimalValueLessThan=ruleDecimalValueLessThan
	{ $current=$iv_ruleDecimalValueLessThan.current; }
	EOF;

// Rule DecimalValueLessThan
ruleDecimalValueLessThan returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_LT_0=RULE_LT
		{
			newLeafNode(this_LT_0, grammarAccess.getDecimalValueLessThanAccess().getLTTerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueLessThanAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getDecimalValueLessThanAccess().getValueDecimalParserRuleCall_2_0());
				}
				lv_value_2_0=ruleDecimal
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getDecimalValueLessThanRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleDecimalValueGreaterThanEquals
entryRuleDecimalValueGreaterThanEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDecimalValueGreaterThanEqualsRule()); }
	iv_ruleDecimalValueGreaterThanEquals=ruleDecimalValueGreaterThanEquals
	{ $current=$iv_ruleDecimalValueGreaterThanEquals.current; }
	EOF;

// Rule DecimalValueGreaterThanEquals
ruleDecimalValueGreaterThanEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_GTE_0=RULE_GTE
		{
			newLeafNode(this_GTE_0, grammarAccess.getDecimalValueGreaterThanEqualsAccess().getGTETerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueGreaterThanEqualsAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getDecimalValueGreaterThanEqualsAccess().getValueDecimalParserRuleCall_2_0());
				}
				lv_value_2_0=ruleDecimal
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getDecimalValueGreaterThanEqualsRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleDecimalValueLessThanEquals
entryRuleDecimalValueLessThanEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDecimalValueLessThanEqualsRule()); }
	iv_ruleDecimalValueLessThanEquals=ruleDecimalValueLessThanEquals
	{ $current=$iv_ruleDecimalValueLessThanEquals.current; }
	EOF;

// Rule DecimalValueLessThanEquals
ruleDecimalValueLessThanEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_LTE_0=RULE_LTE
		{
			newLeafNode(this_LTE_0, grammarAccess.getDecimalValueLessThanEqualsAccess().getLTETerminalRuleCall_0());
		}
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getDecimalValueLessThanEqualsAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getDecimalValueLessThanEqualsAccess().getValueDecimalParserRuleCall_2_0());
				}
				lv_value_2_0=ruleDecimal
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getDecimalValueLessThanEqualsRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleNestedExpression
entryRuleNestedExpression returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getNestedExpressionRule()); }
	iv_ruleNestedExpression=ruleNestedExpression
	{ $current=$iv_ruleNestedExpression.current; }
	EOF;

// Rule NestedExpression
ruleNestedExpression returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_ROUND_OPEN_0=RULE_ROUND_OPEN
		{
			newLeafNode(this_ROUND_OPEN_0, grammarAccess.getNestedExpressionAccess().getROUND_OPENTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getNestedExpressionAccess().getNestedExpressionConstraintParserRuleCall_1_0());
				}
				lv_nested_1_0=ruleExpressionConstraint
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getNestedExpressionRule());
					}
					set(
						$current,
						"nested",
						lv_nested_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.ExpressionConstraint");
					afterParserOrEnumRuleCall();
				}
			)
		)
		this_ROUND_CLOSE_2=RULE_ROUND_CLOSE
		{
			newLeafNode(this_ROUND_CLOSE_2, grammarAccess.getNestedExpressionAccess().getROUND_CLOSETerminalRuleCall_2());
		}
	)
;

// Entry rule entryRuleSnomedIdentifier
entryRuleSnomedIdentifier returns [String current=null]@init {
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}:
	{ newCompositeNode(grammarAccess.getSnomedIdentifierRule()); }
	iv_ruleSnomedIdentifier=ruleSnomedIdentifier
	{ $current=$iv_ruleSnomedIdentifier.current.getText(); }
	EOF;
finally {
	myHiddenTokenState.restore();
}

// Rule SnomedIdentifier
ruleSnomedIdentifier returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()]
@init {
	enterRule();
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}
@after {
	leaveRule();
}:
	(
		this_DIGIT_NONZERO_0=RULE_DIGIT_NONZERO
		{
			$current.merge(this_DIGIT_NONZERO_0);
		}
		{
			newLeafNode(this_DIGIT_NONZERO_0, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_0());
		}
		(
			this_DIGIT_NONZERO_1=RULE_DIGIT_NONZERO
			{
				$current.merge(this_DIGIT_NONZERO_1);
			}
			{
				newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_1_0());
			}
			    |
			this_ZERO_2=RULE_ZERO
			{
				$current.merge(this_ZERO_2);
			}
			{
				newLeafNode(this_ZERO_2, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_1_1());
			}
		)
		(
			this_DIGIT_NONZERO_3=RULE_DIGIT_NONZERO
			{
				$current.merge(this_DIGIT_NONZERO_3);
			}
			{
				newLeafNode(this_DIGIT_NONZERO_3, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_2_0());
			}
			    |
			this_ZERO_4=RULE_ZERO
			{
				$current.merge(this_ZERO_4);
			}
			{
				newLeafNode(this_ZERO_4, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_2_1());
			}
		)
		(
			this_DIGIT_NONZERO_5=RULE_DIGIT_NONZERO
			{
				$current.merge(this_DIGIT_NONZERO_5);
			}
			{
				newLeafNode(this_DIGIT_NONZERO_5, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_3_0());
			}
			    |
			this_ZERO_6=RULE_ZERO
			{
				$current.merge(this_ZERO_6);
			}
			{
				newLeafNode(this_ZERO_6, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_3_1());
			}
		)
		(
			this_DIGIT_NONZERO_7=RULE_DIGIT_NONZERO
			{
				$current.merge(this_DIGIT_NONZERO_7);
			}
			{
				newLeafNode(this_DIGIT_NONZERO_7, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_4_0());
			}
			    |
			this_ZERO_8=RULE_ZERO
			{
				$current.merge(this_ZERO_8);
			}
			{
				newLeafNode(this_ZERO_8, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_4_1());
			}
		)
		(
			this_DIGIT_NONZERO_9=RULE_DIGIT_NONZERO
			{
				$current.merge(this_DIGIT_NONZERO_9);
			}
			{
				newLeafNode(this_DIGIT_NONZERO_9, grammarAccess.getSnomedIdentifierAccess().getDIGIT_NONZEROTerminalRuleCall_5_0());
			}
			    |
			this_ZERO_10=RULE_ZERO
			{
				$current.merge(this_ZERO_10);
			}
			{
				newLeafNode(this_ZERO_10, grammarAccess.getSnomedIdentifierAccess().getZEROTerminalRuleCall_5_1());
			}
		)+
	)
;
finally {
	myHiddenTokenState.restore();
}

// Entry rule entryRuleTerm
entryRuleTerm returns [String current=null]@init {
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}:
	{ newCompositeNode(grammarAccess.getTermRule()); }
	iv_ruleTerm=ruleTerm
	{ $current=$iv_ruleTerm.current.getText(); }
	EOF;
finally {
	myHiddenTokenState.restore();
}

// Rule Term
ruleTerm returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()]
@init {
	enterRule();
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}
@after {
	leaveRule();
}:
	(
		(
			{
				newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_0());
			}
			this_TermCharacter_0=ruleTermCharacter
			{
				$current.merge(this_TermCharacter_0);
			}
			{
				afterParserOrEnumRuleCall();
			}
		)+
		(
			(
				this_WS_1=RULE_WS
				{
					$current.merge(this_WS_1);
				}
				{
					newLeafNode(this_WS_1, grammarAccess.getTermAccess().getWSTerminalRuleCall_1_0());
				}
			)+
			(
				{
					newCompositeNode(grammarAccess.getTermAccess().getTermCharacterParserRuleCall_1_1());
				}
				this_TermCharacter_2=ruleTermCharacter
				{
					$current.merge(this_TermCharacter_2);
				}
				{
					afterParserOrEnumRuleCall();
				}
			)+
		)*
	)
;
finally {
	myHiddenTokenState.restore();
}

// Entry rule entryRuleTermCharacter
entryRuleTermCharacter returns [String current=null]@init {
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}:
	{ newCompositeNode(grammarAccess.getTermCharacterRule()); }
	iv_ruleTermCharacter=ruleTermCharacter
	{ $current=$iv_ruleTermCharacter.current.getText(); }
	EOF;
finally {
	myHiddenTokenState.restore();
}

// Rule TermCharacter
ruleTermCharacter returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()]
@init {
	enterRule();
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}
@after {
	leaveRule();
}:
	(
		this_LT_0=RULE_LT
		{
			$current.merge(this_LT_0);
		}
		{
			newLeafNode(this_LT_0, grammarAccess.getTermCharacterAccess().getLTTerminalRuleCall_0());
		}
		    |
		this_GT_1=RULE_GT
		{
			$current.merge(this_GT_1);
		}
		{
			newLeafNode(this_GT_1, grammarAccess.getTermCharacterAccess().getGTTerminalRuleCall_1());
		}
		    |
		this_DBL_LT_2=RULE_DBL_LT
		{
			$current.merge(this_DBL_LT_2);
		}
		{
			newLeafNode(this_DBL_LT_2, grammarAccess.getTermCharacterAccess().getDBL_LTTerminalRuleCall_2());
		}
		    |
		this_DBL_GT_3=RULE_DBL_GT
		{
			$current.merge(this_DBL_GT_3);
		}
		{
			newLeafNode(this_DBL_GT_3, grammarAccess.getTermCharacterAccess().getDBL_GTTerminalRuleCall_3());
		}
		    |
		this_LT_EM_4=RULE_LT_EM
		{
			$current.merge(this_LT_EM_4);
		}
		{
			newLeafNode(this_LT_EM_4, grammarAccess.getTermCharacterAccess().getLT_EMTerminalRuleCall_4());
		}
		    |
		this_GT_EM_5=RULE_GT_EM
		{
			$current.merge(this_GT_EM_5);
		}
		{
			newLeafNode(this_GT_EM_5, grammarAccess.getTermCharacterAccess().getGT_EMTerminalRuleCall_5());
		}
		    |
		this_GTE_6=RULE_GTE
		{
			$current.merge(this_GTE_6);
		}
		{
			newLeafNode(this_GTE_6, grammarAccess.getTermCharacterAccess().getGTETerminalRuleCall_6());
		}
		    |
		this_LTE_7=RULE_LTE
		{
			$current.merge(this_LTE_7);
		}
		{
			newLeafNode(this_LTE_7, grammarAccess.getTermCharacterAccess().getLTETerminalRuleCall_7());
		}
		    |
		this_AND_8=RULE_AND
		{
			$current.merge(this_AND_8);
		}
		{
			newLeafNode(this_AND_8, grammarAccess.getTermCharacterAccess().getANDTerminalRuleCall_8());
		}
		    |
		this_OR_9=RULE_OR
		{
			$current.merge(this_OR_9);
		}
		{
			newLeafNode(this_OR_9, grammarAccess.getTermCharacterAccess().getORTerminalRuleCall_9());
		}
		    |
		this_NOT_10=RULE_NOT
		{
			$current.merge(this_NOT_10);
		}
		{
			newLeafNode(this_NOT_10, grammarAccess.getTermCharacterAccess().getNOTTerminalRuleCall_10());
		}
		    |
		this_MINUS_11=RULE_MINUS
		{
			$current.merge(this_MINUS_11);
		}
		{
			newLeafNode(this_MINUS_11, grammarAccess.getTermCharacterAccess().getMINUSTerminalRuleCall_11());
		}
		    |
		this_ZERO_12=RULE_ZERO
		{
			$current.merge(this_ZERO_12);
		}
		{
			newLeafNode(this_ZERO_12, grammarAccess.getTermCharacterAccess().getZEROTerminalRuleCall_12());
		}
		    |
		this_DIGIT_NONZERO_13=RULE_DIGIT_NONZERO
		{
			$current.merge(this_DIGIT_NONZERO_13);
		}
		{
			newLeafNode(this_DIGIT_NONZERO_13, grammarAccess.getTermCharacterAccess().getDIGIT_NONZEROTerminalRuleCall_13());
		}
		    |
		this_LETTER_14=RULE_LETTER
		{
			$current.merge(this_LETTER_14);
		}
		{
			newLeafNode(this_LETTER_14, grammarAccess.getTermCharacterAccess().getLETTERTerminalRuleCall_14());
		}
		    |
		this_CARET_15=RULE_CARET
		{
			$current.merge(this_CARET_15);
		}
		{
			newLeafNode(this_CARET_15, grammarAccess.getTermCharacterAccess().getCARETTerminalRuleCall_15());
		}
		    |
		this_EQUAL_16=RULE_EQUAL
		{
			$current.merge(this_EQUAL_16);
		}
		{
			newLeafNode(this_EQUAL_16, grammarAccess.getTermCharacterAccess().getEQUALTerminalRuleCall_16());
		}
		    |
		this_NOT_EQUAL_17=RULE_NOT_EQUAL
		{
			$current.merge(this_NOT_EQUAL_17);
		}
		{
			newLeafNode(this_NOT_EQUAL_17, grammarAccess.getTermCharacterAccess().getNOT_EQUALTerminalRuleCall_17());
		}
		    |
		this_PLUS_18=RULE_PLUS
		{
			$current.merge(this_PLUS_18);
		}
		{
			newLeafNode(this_PLUS_18, grammarAccess.getTermCharacterAccess().getPLUSTerminalRuleCall_18());
		}
		    |
		this_CURLY_OPEN_19=RULE_CURLY_OPEN
		{
			$current.merge(this_CURLY_OPEN_19);
		}
		{
			newLeafNode(this_CURLY_OPEN_19, grammarAccess.getTermCharacterAccess().getCURLY_OPENTerminalRuleCall_19());
		}
		    |
		this_CURLY_CLOSE_20=RULE_CURLY_CLOSE
		{
			$current.merge(this_CURLY_CLOSE_20);
		}
		{
			newLeafNode(this_CURLY_CLOSE_20, grammarAccess.getTermCharacterAccess().getCURLY_CLOSETerminalRuleCall_20());
		}
		    |
		this_ROUND_OPEN_21=RULE_ROUND_OPEN
		{
			$current.merge(this_ROUND_OPEN_21);
		}
		{
			newLeafNode(this_ROUND_OPEN_21, grammarAccess.getTermCharacterAccess().getROUND_OPENTerminalRuleCall_21());
		}
		    |
		this_ROUND_CLOSE_22=RULE_ROUND_CLOSE
		{
			$current.merge(this_ROUND_CLOSE_22);
		}
		{
			newLeafNode(this_ROUND_CLOSE_22, grammarAccess.getTermCharacterAccess().getROUND_CLOSETerminalRuleCall_22());
		}
		    |
		this_SQUARE_OPEN_23=RULE_SQUARE_OPEN
		{
			$current.merge(this_SQUARE_OPEN_23);
		}
		{
			newLeafNode(this_SQUARE_OPEN_23, grammarAccess.getTermCharacterAccess().getSQUARE_OPENTerminalRuleCall_23());
		}
		    |
		this_SQUARE_CLOSE_24=RULE_SQUARE_CLOSE
		{
			$current.merge(this_SQUARE_CLOSE_24);
		}
		{
			newLeafNode(this_SQUARE_CLOSE_24, grammarAccess.getTermCharacterAccess().getSQUARE_CLOSETerminalRuleCall_24());
		}
		    |
		this_DOT_25=RULE_DOT
		{
			$current.merge(this_DOT_25);
		}
		{
			newLeafNode(this_DOT_25, grammarAccess.getTermCharacterAccess().getDOTTerminalRuleCall_25());
		}
		    |
		this_COLON_26=RULE_COLON
		{
			$current.merge(this_COLON_26);
		}
		{
			newLeafNode(this_COLON_26, grammarAccess.getTermCharacterAccess().getCOLONTerminalRuleCall_26());
		}
		    |
		this_COMMA_27=RULE_COMMA
		{
			$current.merge(this_COMMA_27);
		}
		{
			newLeafNode(this_COMMA_27, grammarAccess.getTermCharacterAccess().getCOMMATerminalRuleCall_27());
		}
		    |
		this_REVERSED_28=RULE_REVERSED
		{
			$current.merge(this_REVERSED_28);
		}
		{
			newLeafNode(this_REVERSED_28, grammarAccess.getTermCharacterAccess().getREVERSEDTerminalRuleCall_28());
		}
		    |
		this_TO_29=RULE_TO
		{
			$current.merge(this_TO_29);
		}
		{
			newLeafNode(this_TO_29, grammarAccess.getTermCharacterAccess().getTOTerminalRuleCall_29());
		}
		    |
		this_WILDCARD_30=RULE_WILDCARD
		{
			$current.merge(this_WILDCARD_30);
		}
		{
			newLeafNode(this_WILDCARD_30, grammarAccess.getTermCharacterAccess().getWILDCARDTerminalRuleCall_30());
		}
		    |
		this_HASH_31=RULE_HASH
		{
			$current.merge(this_HASH_31);
		}
		{
			newLeafNode(this_HASH_31, grammarAccess.getTermCharacterAccess().getHASHTerminalRuleCall_31());
		}
		    |
		this_DASH_32=RULE_DASH
		{
			$current.merge(this_DASH_32);
		}
		{
			newLeafNode(this_DASH_32, grammarAccess.getTermCharacterAccess().getDASHTerminalRuleCall_32());
		}
		    |
		this_OTHER_CHARACTER_33=RULE_OTHER_CHARACTER
		{
			$current.merge(this_OTHER_CHARACTER_33);
		}
		{
			newLeafNode(this_OTHER_CHARACTER_33, grammarAccess.getTermCharacterAccess().getOTHER_CHARACTERTerminalRuleCall_33());
		}
	)
;
finally {
	myHiddenTokenState.restore();
}

// Entry rule entryRuleNonNegativeInteger
entryRuleNonNegativeInteger returns [String current=null]@init {
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}:
	{ newCompositeNode(grammarAccess.getNonNegativeIntegerRule()); }
	iv_ruleNonNegativeInteger=ruleNonNegativeInteger
	{ $current=$iv_ruleNonNegativeInteger.current.getText(); }
	EOF;
finally {
	myHiddenTokenState.restore();
}

// Rule NonNegativeInteger
ruleNonNegativeInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()]
@init {
	enterRule();
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}
@after {
	leaveRule();
}:
	(
		this_ZERO_0=RULE_ZERO
		{
			$current.merge(this_ZERO_0);
		}
		{
			newLeafNode(this_ZERO_0, grammarAccess.getNonNegativeIntegerAccess().getZEROTerminalRuleCall_0());
		}
		    |
		(
			this_DIGIT_NONZERO_1=RULE_DIGIT_NONZERO
			{
				$current.merge(this_DIGIT_NONZERO_1);
			}
			{
				newLeafNode(this_DIGIT_NONZERO_1, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_0());
			}
			(
				this_DIGIT_NONZERO_2=RULE_DIGIT_NONZERO
				{
					$current.merge(this_DIGIT_NONZERO_2);
				}
				{
					newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getNonNegativeIntegerAccess().getDIGIT_NONZEROTerminalRuleCall_1_1_0());
				}
				    |
				this_ZERO_3=RULE_ZERO
				{
					$current.merge(this_ZERO_3);
				}
				{
					newLeafNode(this_ZERO_3, grammarAccess.getNonNegativeIntegerAccess().getZEROTerminalRuleCall_1_1_1());
				}
			)*
		)
	)
;
finally {
	myHiddenTokenState.restore();
}

// Entry rule entryRuleMaxValue
entryRuleMaxValue returns [String current=null]@init {
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}:
	{ newCompositeNode(grammarAccess.getMaxValueRule()); }
	iv_ruleMaxValue=ruleMaxValue
	{ $current=$iv_ruleMaxValue.current.getText(); }
	EOF;
finally {
	myHiddenTokenState.restore();
}

// Rule MaxValue
ruleMaxValue returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()]
@init {
	enterRule();
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getMaxValueAccess().getNonNegativeIntegerParserRuleCall_0());
		}
		this_NonNegativeInteger_0=ruleNonNegativeInteger
		{
			$current.merge(this_NonNegativeInteger_0);
		}
		{
			afterParserOrEnumRuleCall();
		}
		    |
		this_WILDCARD_1=RULE_WILDCARD
		{
			$current.merge(this_WILDCARD_1);
		}
		{
			newLeafNode(this_WILDCARD_1, grammarAccess.getMaxValueAccess().getWILDCARDTerminalRuleCall_1());
		}
	)
;
finally {
	myHiddenTokenState.restore();
}

// Entry rule entryRuleAndOperator
entryRuleAndOperator returns [String current=null]@init {
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}:
	{ newCompositeNode(grammarAccess.getAndOperatorRule()); }
	iv_ruleAndOperator=ruleAndOperator
	{ $current=$iv_ruleAndOperator.current.getText(); }
	EOF;
finally {
	myHiddenTokenState.restore();
}

// Rule AndOperator
ruleAndOperator returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()]
@init {
	enterRule();
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}
@after {
	leaveRule();
}:
	(
		this_AND_0=RULE_AND
		{
			$current.merge(this_AND_0);
		}
		{
			newLeafNode(this_AND_0, grammarAccess.getAndOperatorAccess().getANDTerminalRuleCall_0());
		}
		    |
		this_COMMA_1=RULE_COMMA
		{
			$current.merge(this_COMMA_1);
		}
		{
			newLeafNode(this_COMMA_1, grammarAccess.getAndOperatorAccess().getCOMMATerminalRuleCall_1());
		}
	)
;
finally {
	myHiddenTokenState.restore();
}

// Entry rule entryRuleInteger
entryRuleInteger returns [String current=null]@init {
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}:
	{ newCompositeNode(grammarAccess.getIntegerRule()); }
	iv_ruleInteger=ruleInteger
	{ $current=$iv_ruleInteger.current.getText(); }
	EOF;
finally {
	myHiddenTokenState.restore();
}

// Rule Integer
ruleInteger returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()]
@init {
	enterRule();
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}
@after {
	leaveRule();
}:
	(
		(
			this_PLUS_0=RULE_PLUS
			{
				$current.merge(this_PLUS_0);
			}
			{
				newLeafNode(this_PLUS_0, grammarAccess.getIntegerAccess().getPLUSTerminalRuleCall_0_0());
			}
			    |
			this_DASH_1=RULE_DASH
			{
				$current.merge(this_DASH_1);
			}
			{
				newLeafNode(this_DASH_1, grammarAccess.getIntegerAccess().getDASHTerminalRuleCall_0_1());
			}
		)?
		{
			newCompositeNode(grammarAccess.getIntegerAccess().getNonNegativeIntegerParserRuleCall_1());
		}
		this_NonNegativeInteger_2=ruleNonNegativeInteger
		{
			$current.merge(this_NonNegativeInteger_2);
		}
		{
			afterParserOrEnumRuleCall();
		}
	)
;
finally {
	myHiddenTokenState.restore();
}

// Entry rule entryRuleDecimal
entryRuleDecimal returns [String current=null]@init {
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}:
	{ newCompositeNode(grammarAccess.getDecimalRule()); }
	iv_ruleDecimal=ruleDecimal
	{ $current=$iv_ruleDecimal.current.getText(); }
	EOF;
finally {
	myHiddenTokenState.restore();
}

// Rule Decimal
ruleDecimal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()]
@init {
	enterRule();
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}
@after {
	leaveRule();
}:
	(
		(
			this_PLUS_0=RULE_PLUS
			{
				$current.merge(this_PLUS_0);
			}
			{
				newLeafNode(this_PLUS_0, grammarAccess.getDecimalAccess().getPLUSTerminalRuleCall_0_0());
			}
			    |
			this_DASH_1=RULE_DASH
			{
				$current.merge(this_DASH_1);
			}
			{
				newLeafNode(this_DASH_1, grammarAccess.getDecimalAccess().getDASHTerminalRuleCall_0_1());
			}
		)?
		{
			newCompositeNode(grammarAccess.getDecimalAccess().getNonNegativeDecimalParserRuleCall_1());
		}
		this_NonNegativeDecimal_2=ruleNonNegativeDecimal
		{
			$current.merge(this_NonNegativeDecimal_2);
		}
		{
			afterParserOrEnumRuleCall();
		}
	)
;
finally {
	myHiddenTokenState.restore();
}

// Entry rule entryRuleNonNegativeDecimal
entryRuleNonNegativeDecimal returns [String current=null]@init {
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}:
	{ newCompositeNode(grammarAccess.getNonNegativeDecimalRule()); }
	iv_ruleNonNegativeDecimal=ruleNonNegativeDecimal
	{ $current=$iv_ruleNonNegativeDecimal.current.getText(); }
	EOF;
finally {
	myHiddenTokenState.restore();
}

// Rule NonNegativeDecimal
ruleNonNegativeDecimal returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()]
@init {
	enterRule();
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}
@after {
	leaveRule();
}:
	(
		{
			newCompositeNode(grammarAccess.getNonNegativeDecimalAccess().getNonNegativeIntegerParserRuleCall_0());
		}
		this_NonNegativeInteger_0=ruleNonNegativeInteger
		{
			$current.merge(this_NonNegativeInteger_0);
		}
		{
			afterParserOrEnumRuleCall();
		}
		this_DOT_1=RULE_DOT
		{
			$current.merge(this_DOT_1);
		}
		{
			newLeafNode(this_DOT_1, grammarAccess.getNonNegativeDecimalAccess().getDOTTerminalRuleCall_1());
		}
		(
			this_DIGIT_NONZERO_2=RULE_DIGIT_NONZERO
			{
				$current.merge(this_DIGIT_NONZERO_2);
			}
			{
				newLeafNode(this_DIGIT_NONZERO_2, grammarAccess.getNonNegativeDecimalAccess().getDIGIT_NONZEROTerminalRuleCall_2_0());
			}
			    |
			this_ZERO_3=RULE_ZERO
			{
				$current.merge(this_ZERO_3);
			}
			{
				newLeafNode(this_ZERO_3, grammarAccess.getNonNegativeDecimalAccess().getZEROTerminalRuleCall_2_1());
			}
		)*
	)
;
finally {
	myHiddenTokenState.restore();
}

RULE_REVERSED : 'R';

RULE_TO : '..';

RULE_AND : 'AND';

RULE_OR : 'OR';

RULE_MINUS : 'MINUS';

RULE_ZERO : '0';

RULE_DIGIT_NONZERO : '1'..'9';

RULE_LETTER : ('a'..'z'|'A'..'Z');

RULE_PIPE : '|';

RULE_COLON : ':';

RULE_CURLY_OPEN : '{';

RULE_CURLY_CLOSE : '}';

RULE_COMMA : ',';

RULE_ROUND_OPEN : '(';

RULE_ROUND_CLOSE : ')';

RULE_SQUARE_OPEN : '[';

RULE_SQUARE_CLOSE : ']';

RULE_PLUS : '+';

RULE_DASH : '-';

RULE_CARET : '^';

RULE_NOT : '!';

RULE_DOT : '.';

RULE_WILDCARD : '*';

RULE_EQUAL : '=';

RULE_NOT_EQUAL : '!=';

RULE_LT : '<';

RULE_GT : '>';

RULE_DBL_LT : '<<';

RULE_DBL_GT : '>>';

RULE_LT_EM : '<!';

RULE_GT_EM : '>!';

RULE_GTE : '>=';

RULE_LTE : '<=';

RULE_HASH : '#';

RULE_WS : (' '|'\t'|'\n'|'\r');

RULE_ML_COMMENT : '/*' ( options {greedy=false;} : . )*'*/';

RULE_SL_COMMENT : '//' ~(('\n'|'\r'))* ('\r'? '\n')?;

RULE_OTHER_CHARACTER : ~('|');

RULE_STRING : ('"' ('\\' .|~(('\\'|'"')))* '"'|'\'' ('\\' .|~(('\\'|'\'')))* '\'');
