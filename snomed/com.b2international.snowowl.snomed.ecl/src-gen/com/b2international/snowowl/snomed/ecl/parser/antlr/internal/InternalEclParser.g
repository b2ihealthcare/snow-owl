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
parser grammar InternalEclParser;

options {
	tokenVocab=InternalEclLexer;
	superClass=AbstractInternalAntlrParser;
	backtrack=true;
}

@header {
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

@members {

/*
  This grammar contains a lot of empty actions to work around a bug in ANTLR.
  Otherwise the ANTLR tool will create synpreds that cannot be compiled in some rare cases.
*/

 	private EclGrammarAccess grammarAccess;

    public InternalEclParser(TokenStream input, EclGrammarAccess grammarAccess) {
        this(input);
        this.grammarAccess = grammarAccess;
        registerRules(grammarAccess.getGrammar());
    }

    @Override
    protected String getFirstRuleName() {
    	return "Script";
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

// Entry rule entryRuleScript
entryRuleScript returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getScriptRule()); }
	iv_ruleScript=ruleScript
	{ $current=$iv_ruleScript.current; }
	EOF;

// Rule Script
ruleScript returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			{
				/* */
			}
			{
				$current = forceCreateModelElement(
					grammarAccess.getScriptAccess().getScriptAction_0(),
					$current);
			}
		)
		(
			(
				{
					newCompositeNode(grammarAccess.getScriptAccess().getConstraintExpressionConstraintParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleExpressionConstraint
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getScriptRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.ExpressionConstraint");
					afterParserOrEnumRuleCall();
				}
			)
		)?
	)
;

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
		/* */
	}
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
			/* */
		}
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
					/* */
				}
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getOrExpressionConstraintAccess().getOrExpressionConstraintLeftAction_1_0(),
						$current);
				}
			)
			this_DISJUNCTION_2=RULE_DISJUNCTION
			{
				newLeafNode(this_DISJUNCTION_2, grammarAccess.getOrExpressionConstraintAccess().getDISJUNCTIONTerminalRuleCall_1_1());
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
			/* */
		}
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
					/* */
				}
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getAndExpressionConstraintAccess().getAndExpressionConstraintLeftAction_1_0(),
						$current);
				}
			)
			(
				this_CONJUNCTION_2=RULE_CONJUNCTION
				{
					newLeafNode(this_CONJUNCTION_2, grammarAccess.getAndExpressionConstraintAccess().getCONJUNCTIONTerminalRuleCall_1_1_0());
				}
				    |
				this_COMMA_3=RULE_COMMA
				{
					newLeafNode(this_COMMA_3, grammarAccess.getAndExpressionConstraintAccess().getCOMMATerminalRuleCall_1_1_1());
				}
			)
			(
				(
					{
						newCompositeNode(grammarAccess.getAndExpressionConstraintAccess().getRightExclusionExpressionConstraintParserRuleCall_1_2_0());
					}
					lv_right_4_0=ruleExclusionExpressionConstraint
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getAndExpressionConstraintRule());
						}
						set(
							$current,
							"right",
							lv_right_4_0,
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
			/* */
		}
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
					/* */
				}
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getExclusionExpressionConstraintAccess().getExclusionExpressionConstraintLeftAction_1_0(),
						$current);
				}
			)
			this_EXCLUSION_2=RULE_EXCLUSION
			{
				newLeafNode(this_EXCLUSION_2, grammarAccess.getExclusionExpressionConstraintAccess().getEXCLUSIONTerminalRuleCall_1_1());
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
			/* */
		}
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
					/* */
				}
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
						newCompositeNode(grammarAccess.getRefinedExpressionConstraintAccess().getRefinementEclRefinementParserRuleCall_1_2_0());
					}
					lv_refinement_3_0=ruleEclRefinement
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getRefinedExpressionConstraintRule());
						}
						set(
							$current,
							"refinement",
							lv_refinement_3_0,
							"com.b2international.snowowl.snomed.ecl.Ecl.EclRefinement");
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
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getSubExpressionConstraintParserRuleCall_0());
		}
		this_SubExpressionConstraint_0=ruleSubExpressionConstraint
		{
			$current = $this_SubExpressionConstraint_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(
				{
					/* */
				}
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
						newCompositeNode(grammarAccess.getDottedExpressionConstraintAccess().getAttributeSubExpressionConstraintParserRuleCall_1_2_0());
					}
					lv_attribute_3_0=ruleSubExpressionConstraint
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getDottedExpressionConstraintRule());
						}
						set(
							$current,
							"attribute",
							lv_attribute_3_0,
							"com.b2international.snowowl.snomed.ecl.Ecl.SubExpressionConstraint");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)*
	)
;

// Entry rule entryRuleSubExpressionConstraint
entryRuleSubExpressionConstraint returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSubExpressionConstraintRule()); }
	iv_ruleSubExpressionConstraint=ruleSubExpressionConstraint
	{ $current=$iv_ruleSubExpressionConstraint.current; }
	EOF;

// Rule SubExpressionConstraint
ruleSubExpressionConstraint returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getChildOfParserRuleCall_0());
		}
		this_ChildOf_0=ruleChildOf
		{
			$current = $this_ChildOf_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getDescendantOfParserRuleCall_1());
		}
		this_DescendantOf_1=ruleDescendantOf
		{
			$current = $this_DescendantOf_1.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getDescendantOrSelfOfParserRuleCall_2());
		}
		this_DescendantOrSelfOf_2=ruleDescendantOrSelfOf
		{
			$current = $this_DescendantOrSelfOf_2.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getParentOfParserRuleCall_3());
		}
		this_ParentOf_3=ruleParentOf
		{
			$current = $this_ParentOf_3.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getAncestorOfParserRuleCall_4());
		}
		this_AncestorOf_4=ruleAncestorOf
		{
			$current = $this_AncestorOf_4.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getAncestorOrSelfOfParserRuleCall_5());
		}
		this_AncestorOrSelfOf_5=ruleAncestorOrSelfOf
		{
			$current = $this_AncestorOrSelfOf_5.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getEclFocusConceptParserRuleCall_6());
		}
		this_EclFocusConcept_6=ruleEclFocusConcept
		{
			$current = $this_EclFocusConcept_6.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleEclFocusConcept
entryRuleEclFocusConcept returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getEclFocusConceptRule()); }
	iv_ruleEclFocusConcept=ruleEclFocusConcept
	{ $current=$iv_ruleEclFocusConcept.current; }
	EOF;

// Rule EclFocusConcept
ruleEclFocusConcept returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getEclFocusConceptAccess().getMemberOfParserRuleCall_0());
		}
		this_MemberOf_0=ruleMemberOf
		{
			$current = $this_MemberOf_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getEclFocusConceptAccess().getEclConceptReferenceParserRuleCall_1());
		}
		this_EclConceptReference_1=ruleEclConceptReference
		{
			$current = $this_EclConceptReference_1.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getEclFocusConceptAccess().getAnyParserRuleCall_2());
		}
		this_Any_2=ruleAny
		{
			$current = $this_Any_2.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getEclFocusConceptAccess().getNestedExpressionParserRuleCall_3());
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
					newCompositeNode(grammarAccess.getChildOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleEclFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getChildOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.EclFocusConcept");
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
					newCompositeNode(grammarAccess.getDescendantOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleEclFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getDescendantOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.EclFocusConcept");
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
					newCompositeNode(grammarAccess.getDescendantOrSelfOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleEclFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getDescendantOrSelfOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.EclFocusConcept");
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
					newCompositeNode(grammarAccess.getParentOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleEclFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getParentOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.EclFocusConcept");
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
					newCompositeNode(grammarAccess.getAncestorOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleEclFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAncestorOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.EclFocusConcept");
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
					newCompositeNode(grammarAccess.getAncestorOrSelfOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleEclFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAncestorOrSelfOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.EclFocusConcept");
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
						newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintEclConceptReferenceParserRuleCall_1_0_0());
					}
					lv_constraint_1_1=ruleEclConceptReference
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getMemberOfRule());
						}
						set(
							$current,
							"constraint",
							lv_constraint_1_1,
							"com.b2international.snowowl.snomed.ecl.Ecl.EclConceptReference");
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
					    |
					{
						newCompositeNode(grammarAccess.getMemberOfAccess().getConstraintNestedExpressionParserRuleCall_1_0_2());
					}
					lv_constraint_1_3=ruleNestedExpression
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getMemberOfRule());
						}
						set(
							$current,
							"constraint",
							lv_constraint_1_3,
							"com.b2international.snowowl.snomed.ecl.Ecl.NestedExpression");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)
	)
;

// Entry rule entryRuleEclConceptReference
entryRuleEclConceptReference returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getEclConceptReferenceRule()); }
	iv_ruleEclConceptReference=ruleEclConceptReference
	{ $current=$iv_ruleEclConceptReference.current; }
	EOF;

// Rule EclConceptReference
ruleEclConceptReference returns [EObject current=null]
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
					newCompositeNode(grammarAccess.getEclConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_0_0());
				}
				lv_id_0_0=ruleSnomedIdentifier
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getEclConceptReferenceRule());
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
			(
				lv_term_1_0=RULE_TERM_STRING
				{
					newLeafNode(lv_term_1_0, grammarAccess.getEclConceptReferenceAccess().getTermTERM_STRINGTerminalRuleCall_1_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getEclConceptReferenceRule());
					}
					setWithLastConsumed(
						$current,
						"term",
						lv_term_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.TERM_STRING");
				}
			)
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
				/* */
			}
			{
				$current = forceCreateModelElement(
					grammarAccess.getAnyAccess().getAnyAction_1(),
					$current);
			}
		)
	)
;

// Entry rule entryRuleEclRefinement
entryRuleEclRefinement returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getEclRefinementRule()); }
	iv_ruleEclRefinement=ruleEclRefinement
	{ $current=$iv_ruleEclRefinement.current; }
	EOF;

// Rule EclRefinement
ruleEclRefinement returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	{
		/* */
	}
	{
		newCompositeNode(grammarAccess.getEclRefinementAccess().getOrRefinementParserRuleCall());
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
			/* */
		}
		{
			newCompositeNode(grammarAccess.getOrRefinementAccess().getAndRefinementParserRuleCall_0());
		}
		this_AndRefinement_0=ruleAndRefinement
		{
			$current = $this_AndRefinement_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(RULE_DISJUNCTION)=>
			(
				(
					{
						/* */
					}
					{
						$current = forceCreateModelElementAndSet(
							grammarAccess.getOrRefinementAccess().getOrRefinementLeftAction_1_0_0(),
							$current);
					}
				)
				this_DISJUNCTION_2=RULE_DISJUNCTION
				{
					newLeafNode(this_DISJUNCTION_2, grammarAccess.getOrRefinementAccess().getDISJUNCTIONTerminalRuleCall_1_0_1());
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
			/* */
		}
		{
			newCompositeNode(grammarAccess.getAndRefinementAccess().getSubRefinementParserRuleCall_0());
		}
		this_SubRefinement_0=ruleSubRefinement
		{
			$current = $this_SubRefinement_0.current;
			afterParserOrEnumRuleCall();
		}
		(
			(RULE_CONJUNCTION | RULE_COMMA)=>
			(
				(
					{
						/* */
					}
					{
						$current = forceCreateModelElementAndSet(
							grammarAccess.getAndRefinementAccess().getAndRefinementLeftAction_1_0_0(),
							$current);
					}
				)
				(
					this_CONJUNCTION_2=RULE_CONJUNCTION
					{
						newLeafNode(this_CONJUNCTION_2, grammarAccess.getAndRefinementAccess().getCONJUNCTIONTerminalRuleCall_1_0_1_0());
					}
					    |
					this_COMMA_3=RULE_COMMA
					{
						newLeafNode(this_COMMA_3, grammarAccess.getAndRefinementAccess().getCOMMATerminalRuleCall_1_0_1_1());
					}
				)
				(
					(
						{
							newCompositeNode(grammarAccess.getAndRefinementAccess().getRightSubRefinementParserRuleCall_1_0_2_0());
						}
						lv_right_4_0=ruleSubRefinement
						{
							if ($current==null) {
								$current = createModelElementForParent(grammarAccess.getAndRefinementRule());
							}
							set(
								$current,
								"right",
								lv_right_4_0,
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
			/* */
		}
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
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubRefinementAccess().getEclAttributeGroupParserRuleCall_1());
		}
		this_EclAttributeGroup_1=ruleEclAttributeGroup
		{
			$current = $this_EclAttributeGroup_1.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
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
					newCompositeNode(grammarAccess.getNestedRefinementAccess().getNestedEclRefinementParserRuleCall_1_0());
				}
				lv_nested_1_0=ruleEclRefinement
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getNestedRefinementRule());
					}
					set(
						$current,
						"nested",
						lv_nested_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.EclRefinement");
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

// Entry rule entryRuleEclAttributeGroup
entryRuleEclAttributeGroup returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getEclAttributeGroupRule()); }
	iv_ruleEclAttributeGroup=ruleEclAttributeGroup
	{ $current=$iv_ruleEclAttributeGroup.current; }
	EOF;

// Rule EclAttributeGroup
ruleEclAttributeGroup returns [EObject current=null]
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
					newCompositeNode(grammarAccess.getEclAttributeGroupAccess().getCardinalityCardinalityParserRuleCall_0_0());
				}
				lv_cardinality_0_0=ruleCardinality
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getEclAttributeGroupRule());
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
			newLeafNode(this_CURLY_OPEN_1, grammarAccess.getEclAttributeGroupAccess().getCURLY_OPENTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getEclAttributeGroupAccess().getRefinementEclAttributeSetParserRuleCall_2_0());
				}
				lv_refinement_2_0=ruleEclAttributeSet
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getEclAttributeGroupRule());
					}
					set(
						$current,
						"refinement",
						lv_refinement_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.EclAttributeSet");
					afterParserOrEnumRuleCall();
				}
			)
		)
		this_CURLY_CLOSE_3=RULE_CURLY_CLOSE
		{
			newLeafNode(this_CURLY_CLOSE_3, grammarAccess.getEclAttributeGroupAccess().getCURLY_CLOSETerminalRuleCall_3());
		}
	)
;

// Entry rule entryRuleEclAttributeSet
entryRuleEclAttributeSet returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getEclAttributeSetRule()); }
	iv_ruleEclAttributeSet=ruleEclAttributeSet
	{ $current=$iv_ruleEclAttributeSet.current; }
	EOF;

// Rule EclAttributeSet
ruleEclAttributeSet returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	{
		/* */
	}
	{
		newCompositeNode(grammarAccess.getEclAttributeSetAccess().getOrAttributeSetParserRuleCall());
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
			/* */
		}
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
					/* */
				}
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getOrAttributeSetAccess().getOrRefinementLeftAction_1_0(),
						$current);
				}
			)
			this_DISJUNCTION_2=RULE_DISJUNCTION
			{
				newLeafNode(this_DISJUNCTION_2, grammarAccess.getOrAttributeSetAccess().getDISJUNCTIONTerminalRuleCall_1_1());
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
			/* */
		}
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
					/* */
				}
				{
					$current = forceCreateModelElementAndSet(
						grammarAccess.getAndAttributeSetAccess().getAndRefinementLeftAction_1_0(),
						$current);
				}
			)
			(
				this_CONJUNCTION_2=RULE_CONJUNCTION
				{
					newLeafNode(this_CONJUNCTION_2, grammarAccess.getAndAttributeSetAccess().getCONJUNCTIONTerminalRuleCall_1_1_0());
				}
				    |
				this_COMMA_3=RULE_COMMA
				{
					newLeafNode(this_COMMA_3, grammarAccess.getAndAttributeSetAccess().getCOMMATerminalRuleCall_1_1_1());
				}
			)
			(
				(
					{
						newCompositeNode(grammarAccess.getAndAttributeSetAccess().getRightSubAttributeSetParserRuleCall_1_2_0());
					}
					lv_right_4_0=ruleSubAttributeSet
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getAndAttributeSetRule());
						}
						set(
							$current,
							"right",
							lv_right_4_0,
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
			/* */
		}
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
			/* */
		}
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
					newCompositeNode(grammarAccess.getNestedAttributeSetAccess().getNestedEclAttributeSetParserRuleCall_1_0());
				}
				lv_nested_1_0=ruleEclAttributeSet
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getNestedAttributeSetRule());
					}
					set(
						$current,
						"nested",
						lv_nested_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.EclAttributeSet");
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
					newCompositeNode(grammarAccess.getAttributeConstraintAccess().getAttributeSubExpressionConstraintParserRuleCall_2_0());
				}
				lv_attribute_2_0=ruleSubExpressionConstraint
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeConstraintRule());
					}
					set(
						$current,
						"attribute",
						lv_attribute_2_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.SubExpressionConstraint");
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
			/* */
		}
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
			/* */
		}
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
			/* */
		}
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
			/* */
		}
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
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getBooleanValueEqualsParserRuleCall_0());
		}
		this_BooleanValueEquals_0=ruleBooleanValueEquals
		{
			$current = $this_BooleanValueEquals_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getBooleanValueNotEqualsParserRuleCall_1());
		}
		this_BooleanValueNotEquals_1=ruleBooleanValueNotEquals
		{
			$current = $this_BooleanValueNotEquals_1.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getStringValueEqualsParserRuleCall_2());
		}
		this_StringValueEquals_2=ruleStringValueEquals
		{
			$current = $this_StringValueEquals_2.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getStringValueNotEqualsParserRuleCall_3());
		}
		this_StringValueNotEquals_3=ruleStringValueNotEquals
		{
			$current = $this_StringValueNotEquals_3.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueEqualsParserRuleCall_4());
		}
		this_IntegerValueEquals_4=ruleIntegerValueEquals
		{
			$current = $this_IntegerValueEquals_4.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueNotEqualsParserRuleCall_5());
		}
		this_IntegerValueNotEquals_5=ruleIntegerValueNotEquals
		{
			$current = $this_IntegerValueNotEquals_5.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueGreaterThanParserRuleCall_6());
		}
		this_IntegerValueGreaterThan_6=ruleIntegerValueGreaterThan
		{
			$current = $this_IntegerValueGreaterThan_6.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueGreaterThanEqualsParserRuleCall_7());
		}
		this_IntegerValueGreaterThanEquals_7=ruleIntegerValueGreaterThanEquals
		{
			$current = $this_IntegerValueGreaterThanEquals_7.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueLessThanParserRuleCall_8());
		}
		this_IntegerValueLessThan_8=ruleIntegerValueLessThan
		{
			$current = $this_IntegerValueLessThan_8.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getIntegerValueLessThanEqualsParserRuleCall_9());
		}
		this_IntegerValueLessThanEquals_9=ruleIntegerValueLessThanEquals
		{
			$current = $this_IntegerValueLessThanEquals_9.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueEqualsParserRuleCall_10());
		}
		this_DecimalValueEquals_10=ruleDecimalValueEquals
		{
			$current = $this_DecimalValueEquals_10.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueNotEqualsParserRuleCall_11());
		}
		this_DecimalValueNotEquals_11=ruleDecimalValueNotEquals
		{
			$current = $this_DecimalValueNotEquals_11.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueGreaterThanParserRuleCall_12());
		}
		this_DecimalValueGreaterThan_12=ruleDecimalValueGreaterThan
		{
			$current = $this_DecimalValueGreaterThan_12.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueGreaterThanEqualsParserRuleCall_13());
		}
		this_DecimalValueGreaterThanEquals_13=ruleDecimalValueGreaterThanEquals
		{
			$current = $this_DecimalValueGreaterThanEquals_13.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueLessThanParserRuleCall_14());
		}
		this_DecimalValueLessThan_14=ruleDecimalValueLessThan
		{
			$current = $this_DecimalValueLessThan_14.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getDataTypeComparisonAccess().getDecimalValueLessThanEqualsParserRuleCall_15());
		}
		this_DecimalValueLessThanEquals_15=ruleDecimalValueLessThanEquals
		{
			$current = $this_DecimalValueLessThanEquals_15.current;
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
					newCompositeNode(grammarAccess.getAttributeValueEqualsAccess().getConstraintSubExpressionConstraintParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleSubExpressionConstraint
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeValueEqualsRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.SubExpressionConstraint");
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
					newCompositeNode(grammarAccess.getAttributeValueNotEqualsAccess().getConstraintSubExpressionConstraintParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleSubExpressionConstraint
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeValueNotEqualsRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.SubExpressionConstraint");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleBooleanValueEquals
entryRuleBooleanValueEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getBooleanValueEqualsRule()); }
	iv_ruleBooleanValueEquals=ruleBooleanValueEquals
	{ $current=$iv_ruleBooleanValueEquals.current; }
	EOF;

// Rule BooleanValueEquals
ruleBooleanValueEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_EQUAL_0=RULE_EQUAL
		{
			newLeafNode(this_EQUAL_0, grammarAccess.getBooleanValueEqualsAccess().getEQUALTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getBooleanValueEqualsAccess().getValueBooleanParserRuleCall_1_0());
				}
				lv_value_1_0=ruleBoolean
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getBooleanValueEqualsRule());
					}
					set(
						$current,
						"value",
						lv_value_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Boolean");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleBooleanValueNotEquals
entryRuleBooleanValueNotEquals returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getBooleanValueNotEqualsRule()); }
	iv_ruleBooleanValueNotEquals=ruleBooleanValueNotEquals
	{ $current=$iv_ruleBooleanValueNotEquals.current; }
	EOF;

// Rule BooleanValueNotEquals
ruleBooleanValueNotEquals returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_NOT_EQUAL_0=RULE_NOT_EQUAL
		{
			newLeafNode(this_NOT_EQUAL_0, grammarAccess.getBooleanValueNotEqualsAccess().getNOT_EQUALTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getBooleanValueNotEqualsAccess().getValueBooleanParserRuleCall_1_0());
				}
				lv_value_1_0=ruleBoolean
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getBooleanValueNotEqualsRule());
					}
					set(
						$current,
						"value",
						lv_value_1_0,
						"com.b2international.snowowl.snomed.ecl.Ecl.Boolean");
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

// Entry rule entryRuleBoolean
entryRuleBoolean returns [String current=null]@init {
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}:
	{ newCompositeNode(grammarAccess.getBooleanRule()); }
	iv_ruleBoolean=ruleBoolean
	{ $current=$iv_ruleBoolean.current.getText(); }
	EOF;
finally {
	myHiddenTokenState.restore();
}

// Rule Boolean
ruleBoolean returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()]
@init {
	enterRule();
	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
}
@after {
	leaveRule();
}:
	(
		kw=True
		{
			$current.merge(kw);
			newLeafNode(kw, grammarAccess.getBooleanAccess().getTrueKeyword_0());
		}
		    |
		kw=False
		{
			$current.merge(kw);
			newLeafNode(kw, grammarAccess.getBooleanAccess().getFalseKeyword_1());
		}
	)
;
finally {
	myHiddenTokenState.restore();
}
