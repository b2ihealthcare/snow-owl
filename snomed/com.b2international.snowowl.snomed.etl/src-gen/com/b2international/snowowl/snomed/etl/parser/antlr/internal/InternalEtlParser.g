/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
parser grammar InternalEtlParser;

options {
	tokenVocab=InternalEtlLexer;
	superClass=AbstractInternalAntlrParser;
	backtrack=true;
}

@header {
package com.b2international.snowowl.snomed.etl.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import com.b2international.snowowl.snomed.etl.services.EtlGrammarAccess;

}

@members {

/*
  This grammar contains a lot of empty actions to work around a bug in ANTLR.
  Otherwise the ANTLR tool will create synpreds that cannot be compiled in some rare cases.
*/

 	private EtlGrammarAccess grammarAccess;

    public InternalEtlParser(TokenStream input, EtlGrammarAccess grammarAccess) {
        this(input);
        this.grammarAccess = grammarAccess;
        registerRules(grammarAccess.getGrammar());
    }

    @Override
    protected String getFirstRuleName() {
    	return "ExpressionTemplate";
   	}

   	@Override
   	protected EtlGrammarAccess getGrammarAccess() {
   		return grammarAccess;
   	}

}

@rulecatch {
    catch (RecognitionException re) {
        recover(input,re);
        appendSkippedTokens();
    }
}

// Entry rule entryRuleExpressionTemplate
entryRuleExpressionTemplate returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getExpressionTemplateRule()); }
	iv_ruleExpressionTemplate=ruleExpressionTemplate
	{ $current=$iv_ruleExpressionTemplate.current; }
	EOF;

// Rule ExpressionTemplate
ruleExpressionTemplate returns [EObject current=null]
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
					grammarAccess.getExpressionTemplateAccess().getExpressionTemplateAction_0(),
					$current);
			}
		)
		(
			(
				(
					(
						(
							lv_primitive_1_0=RULE_SUBTYPE_OF
							{
								newLeafNode(lv_primitive_1_0, grammarAccess.getExpressionTemplateAccess().getPrimitiveSUBTYPE_OFTerminalRuleCall_1_0_0_0_0());
							}
							{
								if ($current==null) {
									$current = createModelElement(grammarAccess.getExpressionTemplateRule());
								}
								setWithLastConsumed(
									$current,
									"primitive",
									lv_primitive_1_0 != null,
									"com.b2international.snowowl.snomed.etl.Etl.SUBTYPE_OF");
							}
						)
					)
					    |
					this_EQUIVALENT_TO_2=RULE_EQUIVALENT_TO
					{
						newLeafNode(this_EQUIVALENT_TO_2, grammarAccess.getExpressionTemplateAccess().getEQUIVALENT_TOTerminalRuleCall_1_0_0_1());
					}
				)
				    |
				(
					(
						{
							newCompositeNode(grammarAccess.getExpressionTemplateAccess().getSlotTokenReplacementSlotParserRuleCall_1_0_1_0());
						}
						lv_slot_3_0=ruleTokenReplacementSlot
						{
							if ($current==null) {
								$current = createModelElementForParent(grammarAccess.getExpressionTemplateRule());
							}
							set(
								$current,
								"slot",
								lv_slot_3_0,
								"com.b2international.snowowl.snomed.etl.Etl.TokenReplacementSlot");
							afterParserOrEnumRuleCall();
						}
					)
				)
			)?
			(
				(
					{
						newCompositeNode(grammarAccess.getExpressionTemplateAccess().getExpressionSubExpressionParserRuleCall_1_1_0());
					}
					lv_expression_4_0=ruleSubExpression
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getExpressionTemplateRule());
						}
						set(
							$current,
							"expression",
							lv_expression_4_0,
							"com.b2international.snowowl.snomed.etl.Etl.SubExpression");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)?
	)
;

// Entry rule entryRuleSubExpression
entryRuleSubExpression returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSubExpressionRule()); }
	iv_ruleSubExpression=ruleSubExpression
	{ $current=$iv_ruleSubExpression.current; }
	EOF;

// Rule SubExpression
ruleSubExpression returns [EObject current=null]
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
					newCompositeNode(grammarAccess.getSubExpressionAccess().getFocusConceptsFocusConceptParserRuleCall_0_0());
				}
				lv_focusConcepts_0_0=ruleFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getSubExpressionRule());
					}
					add(
						$current,
						"focusConcepts",
						lv_focusConcepts_0_0,
						"com.b2international.snowowl.snomed.etl.Etl.FocusConcept");
					afterParserOrEnumRuleCall();
				}
			)
		)
		(
			this_PLUS_1=RULE_PLUS
			{
				newLeafNode(this_PLUS_1, grammarAccess.getSubExpressionAccess().getPLUSTerminalRuleCall_1_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getSubExpressionAccess().getFocusConceptsFocusConceptParserRuleCall_1_1_0());
					}
					lv_focusConcepts_2_0=ruleFocusConcept
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getSubExpressionRule());
						}
						add(
							$current,
							"focusConcepts",
							lv_focusConcepts_2_0,
							"com.b2international.snowowl.snomed.etl.Etl.FocusConcept");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)*
		(
			this_COLON_3=RULE_COLON
			{
				newLeafNode(this_COLON_3, grammarAccess.getSubExpressionAccess().getCOLONTerminalRuleCall_2_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getSubExpressionAccess().getRefinementRefinementParserRuleCall_2_1_0());
					}
					lv_refinement_4_0=ruleRefinement
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getSubExpressionRule());
						}
						set(
							$current,
							"refinement",
							lv_refinement_4_0,
							"com.b2international.snowowl.snomed.etl.Etl.Refinement");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)?
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
		(
			(
				{
					newCompositeNode(grammarAccess.getFocusConceptAccess().getSlotTemplateInformationSlotParserRuleCall_0_0());
				}
				lv_slot_0_0=ruleTemplateInformationSlot
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getFocusConceptRule());
					}
					set(
						$current,
						"slot",
						lv_slot_0_0,
						"com.b2international.snowowl.snomed.etl.Etl.TemplateInformationSlot");
					afterParserOrEnumRuleCall();
				}
			)
		)?
		(
			(
				{
					newCompositeNode(grammarAccess.getFocusConceptAccess().getConceptConceptReferenceParserRuleCall_1_0());
				}
				lv_concept_1_0=ruleConceptReference
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getFocusConceptRule());
					}
					set(
						$current,
						"concept",
						lv_concept_1_0,
						"com.b2international.snowowl.snomed.etl.Etl.ConceptReference");
					afterParserOrEnumRuleCall();
				}
			)
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
	(
		(
			(
				(
					(
						{
							newCompositeNode(grammarAccess.getRefinementAccess().getAttributesAttributeParserRuleCall_0_0_0_0());
						}
						lv_attributes_0_0=ruleAttribute
						{
							if ($current==null) {
								$current = createModelElementForParent(grammarAccess.getRefinementRule());
							}
							add(
								$current,
								"attributes",
								lv_attributes_0_0,
								"com.b2international.snowowl.snomed.etl.Etl.Attribute");
							afterParserOrEnumRuleCall();
						}
					)
				)
				(
					this_COMMA_1=RULE_COMMA
					{
						newLeafNode(this_COMMA_1, grammarAccess.getRefinementAccess().getCOMMATerminalRuleCall_0_0_1_0());
					}
					(
						(
							{
								newCompositeNode(grammarAccess.getRefinementAccess().getAttributesAttributeParserRuleCall_0_0_1_1_0());
							}
							lv_attributes_2_0=ruleAttribute
							{
								if ($current==null) {
									$current = createModelElementForParent(grammarAccess.getRefinementRule());
								}
								add(
									$current,
									"attributes",
									lv_attributes_2_0,
									"com.b2international.snowowl.snomed.etl.Etl.Attribute");
								afterParserOrEnumRuleCall();
							}
						)
					)
				)*
			)
			    |
			(
				(
					{
						newCompositeNode(grammarAccess.getRefinementAccess().getGroupsAttributeGroupParserRuleCall_0_1_0());
					}
					lv_groups_3_0=ruleAttributeGroup
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getRefinementRule());
						}
						add(
							$current,
							"groups",
							lv_groups_3_0,
							"com.b2international.snowowl.snomed.etl.Etl.AttributeGroup");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)
		(
			(
				this_COMMA_4=RULE_COMMA
				{
					newLeafNode(this_COMMA_4, grammarAccess.getRefinementAccess().getCOMMATerminalRuleCall_1_0());
				}
			)?
			(
				(
					{
						newCompositeNode(grammarAccess.getRefinementAccess().getGroupsAttributeGroupParserRuleCall_1_1_0());
					}
					lv_groups_5_0=ruleAttributeGroup
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getRefinementRule());
						}
						add(
							$current,
							"groups",
							lv_groups_5_0,
							"com.b2international.snowowl.snomed.etl.Etl.AttributeGroup");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)*
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
					newCompositeNode(grammarAccess.getAttributeGroupAccess().getSlotTemplateInformationSlotParserRuleCall_0_0());
				}
				lv_slot_0_0=ruleTemplateInformationSlot
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
					}
					set(
						$current,
						"slot",
						lv_slot_0_0,
						"com.b2international.snowowl.snomed.etl.Etl.TemplateInformationSlot");
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
					newCompositeNode(grammarAccess.getAttributeGroupAccess().getAttributesAttributeParserRuleCall_2_0());
				}
				lv_attributes_2_0=ruleAttribute
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
					}
					add(
						$current,
						"attributes",
						lv_attributes_2_0,
						"com.b2international.snowowl.snomed.etl.Etl.Attribute");
					afterParserOrEnumRuleCall();
				}
			)
		)
		(
			this_COMMA_3=RULE_COMMA
			{
				newLeafNode(this_COMMA_3, grammarAccess.getAttributeGroupAccess().getCOMMATerminalRuleCall_3_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getAttributeGroupAccess().getAttributesAttributeParserRuleCall_3_1_0());
					}
					lv_attributes_4_0=ruleAttribute
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
						}
						add(
							$current,
							"attributes",
							lv_attributes_4_0,
							"com.b2international.snowowl.snomed.etl.Etl.Attribute");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)*
		this_CURLY_CLOSE_5=RULE_CURLY_CLOSE
		{
			newLeafNode(this_CURLY_CLOSE_5, grammarAccess.getAttributeGroupAccess().getCURLY_CLOSETerminalRuleCall_4());
		}
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
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeAccess().getSlotTemplateInformationSlotParserRuleCall_0_0());
				}
				lv_slot_0_0=ruleTemplateInformationSlot
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeRule());
					}
					set(
						$current,
						"slot",
						lv_slot_0_0,
						"com.b2international.snowowl.snomed.etl.Etl.TemplateInformationSlot");
					afterParserOrEnumRuleCall();
				}
			)
		)?
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeAccess().getNameConceptReferenceParserRuleCall_1_0());
				}
				lv_name_1_0=ruleConceptReference
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeRule());
					}
					set(
						$current,
						"name",
						lv_name_1_0,
						"com.b2international.snowowl.snomed.etl.Etl.ConceptReference");
					afterParserOrEnumRuleCall();
				}
			)
		)
		this_EQUAL_2=RULE_EQUAL
		{
			newLeafNode(this_EQUAL_2, grammarAccess.getAttributeAccess().getEQUALTerminalRuleCall_2());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeAccess().getValueAttributeValueParserRuleCall_3_0());
				}
				lv_value_3_0=ruleAttributeValue
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeRule());
					}
					set(
						$current,
						"value",
						lv_value_3_0,
						"com.b2international.snowowl.snomed.etl.Etl.AttributeValue");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleAttributeValue
entryRuleAttributeValue returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getAttributeValueRule()); }
	iv_ruleAttributeValue=ruleAttributeValue
	{ $current=$iv_ruleAttributeValue.current; }
	EOF;

// Rule AttributeValue
ruleAttributeValue returns [EObject current=null]
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
			newCompositeNode(grammarAccess.getAttributeValueAccess().getConceptReferenceParserRuleCall_0());
		}
		this_ConceptReference_0=ruleConceptReference
		{
			$current = $this_ConceptReference_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		(
			this_ROUND_OPEN_1=RULE_ROUND_OPEN
			{
				newLeafNode(this_ROUND_OPEN_1, grammarAccess.getAttributeValueAccess().getROUND_OPENTerminalRuleCall_1_0());
			}
			{
				/* */
			}
			{
				newCompositeNode(grammarAccess.getAttributeValueAccess().getSubExpressionParserRuleCall_1_1());
			}
			this_SubExpression_2=ruleSubExpression
			{
				$current = $this_SubExpression_2.current;
				afterParserOrEnumRuleCall();
			}
			this_ROUND_CLOSE_3=RULE_ROUND_CLOSE
			{
				newLeafNode(this_ROUND_CLOSE_3, grammarAccess.getAttributeValueAccess().getROUND_CLOSETerminalRuleCall_1_2());
			}
		)
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getAttributeValueAccess().getStringValueParserRuleCall_2());
		}
		this_StringValue_4=ruleStringValue
		{
			$current = $this_StringValue_4.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getAttributeValueAccess().getIntegerValueParserRuleCall_3());
		}
		this_IntegerValue_5=ruleIntegerValue
		{
			$current = $this_IntegerValue_5.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getAttributeValueAccess().getDecimalValueParserRuleCall_4());
		}
		this_DecimalValue_6=ruleDecimalValue
		{
			$current = $this_DecimalValue_6.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getAttributeValueAccess().getConcreteValueReplacementSlotParserRuleCall_5());
		}
		this_ConcreteValueReplacementSlot_7=ruleConcreteValueReplacementSlot
		{
			$current = $this_ConcreteValueReplacementSlot_7.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleConceptIdReplacementSlot
entryRuleConceptIdReplacementSlot returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getConceptIdReplacementSlotRule()); }
	iv_ruleConceptIdReplacementSlot=ruleConceptIdReplacementSlot
	{ $current=$iv_ruleConceptIdReplacementSlot.current; }
	EOF;

// Rule ConceptIdReplacementSlot
ruleConceptIdReplacementSlot returns [EObject current=null]
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
					grammarAccess.getConceptIdReplacementSlotAccess().getConceptIdReplacementSlotAction_0(),
					$current);
			}
		)
		this_DOUBLE_SQUARE_OPEN_1=RULE_DOUBLE_SQUARE_OPEN
		{
			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getConceptIdReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
		}
		this_PLUS_2=RULE_PLUS
		{
			newLeafNode(this_PLUS_2, grammarAccess.getConceptIdReplacementSlotAccess().getPLUSTerminalRuleCall_2());
		}
		this_ID_3=RULE_ID
		{
			newLeafNode(this_ID_3, grammarAccess.getConceptIdReplacementSlotAccess().getIDTerminalRuleCall_3());
		}
		(
			this_ROUND_OPEN_4=RULE_ROUND_OPEN
			{
				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getConceptIdReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getConceptIdReplacementSlotAccess().getConstraintExpressionConstraintParserRuleCall_4_1_0());
					}
					lv_constraint_5_0=ruleExpressionConstraint
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getConceptIdReplacementSlotRule());
						}
						set(
							$current,
							"constraint",
							lv_constraint_5_0,
							"com.b2international.snomed.ecl.Ecl.ExpressionConstraint");
						afterParserOrEnumRuleCall();
					}
				)
			)
			this_ROUND_CLOSE_6=RULE_ROUND_CLOSE
			{
				newLeafNode(this_ROUND_CLOSE_6, grammarAccess.getConceptIdReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_2());
			}
		)?
		(
			(
				lv_name_7_0=RULE_SLOTNAME_STRING
				{
					newLeafNode(lv_name_7_0, grammarAccess.getConceptIdReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getConceptIdReplacementSlotRule());
					}
					setWithLastConsumed(
						$current,
						"name",
						lv_name_7_0,
						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
				}
			)
		)?
		this_DOUBLE_SQUARE_CLOSE_8=RULE_DOUBLE_SQUARE_CLOSE
		{
			newLeafNode(this_DOUBLE_SQUARE_CLOSE_8, grammarAccess.getConceptIdReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
		}
	)
;

// Entry rule entryRuleExpressionReplacementSlot
entryRuleExpressionReplacementSlot returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getExpressionReplacementSlotRule()); }
	iv_ruleExpressionReplacementSlot=ruleExpressionReplacementSlot
	{ $current=$iv_ruleExpressionReplacementSlot.current; }
	EOF;

// Rule ExpressionReplacementSlot
ruleExpressionReplacementSlot returns [EObject current=null]
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
					grammarAccess.getExpressionReplacementSlotAccess().getExpressionReplacementSlotAction_0(),
					$current);
			}
		)
		this_DOUBLE_SQUARE_OPEN_1=RULE_DOUBLE_SQUARE_OPEN
		{
			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getExpressionReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
		}
		this_PLUS_2=RULE_PLUS
		{
			newLeafNode(this_PLUS_2, grammarAccess.getExpressionReplacementSlotAccess().getPLUSTerminalRuleCall_2());
		}
		(
			this_SCG_3=RULE_SCG
			{
				newLeafNode(this_SCG_3, grammarAccess.getExpressionReplacementSlotAccess().getSCGTerminalRuleCall_3());
			}
		)?
		(
			this_ROUND_OPEN_4=RULE_ROUND_OPEN
			{
				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getExpressionReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getExpressionReplacementSlotAccess().getConstraintExpressionConstraintParserRuleCall_4_1_0());
					}
					lv_constraint_5_0=ruleExpressionConstraint
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getExpressionReplacementSlotRule());
						}
						set(
							$current,
							"constraint",
							lv_constraint_5_0,
							"com.b2international.snomed.ecl.Ecl.ExpressionConstraint");
						afterParserOrEnumRuleCall();
					}
				)
			)
			this_ROUND_CLOSE_6=RULE_ROUND_CLOSE
			{
				newLeafNode(this_ROUND_CLOSE_6, grammarAccess.getExpressionReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_2());
			}
		)?
		(
			(
				lv_name_7_0=RULE_SLOTNAME_STRING
				{
					newLeafNode(lv_name_7_0, grammarAccess.getExpressionReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getExpressionReplacementSlotRule());
					}
					setWithLastConsumed(
						$current,
						"name",
						lv_name_7_0,
						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
				}
			)
		)?
		this_DOUBLE_SQUARE_CLOSE_8=RULE_DOUBLE_SQUARE_CLOSE
		{
			newLeafNode(this_DOUBLE_SQUARE_CLOSE_8, grammarAccess.getExpressionReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
		}
	)
;

// Entry rule entryRuleTokenReplacementSlot
entryRuleTokenReplacementSlot returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getTokenReplacementSlotRule()); }
	iv_ruleTokenReplacementSlot=ruleTokenReplacementSlot
	{ $current=$iv_ruleTokenReplacementSlot.current; }
	EOF;

// Rule TokenReplacementSlot
ruleTokenReplacementSlot returns [EObject current=null]
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
					grammarAccess.getTokenReplacementSlotAccess().getTokenReplacementSlotAction_0(),
					$current);
			}
		)
		this_DOUBLE_SQUARE_OPEN_1=RULE_DOUBLE_SQUARE_OPEN
		{
			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getTokenReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
		}
		this_PLUS_2=RULE_PLUS
		{
			newLeafNode(this_PLUS_2, grammarAccess.getTokenReplacementSlotAccess().getPLUSTerminalRuleCall_2());
		}
		this_TOK_3=RULE_TOK
		{
			newLeafNode(this_TOK_3, grammarAccess.getTokenReplacementSlotAccess().getTOKTerminalRuleCall_3());
		}
		(
			this_ROUND_OPEN_4=RULE_ROUND_OPEN
			{
				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getTokenReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getTokenReplacementSlotAccess().getTokensSlotTokenParserRuleCall_4_1_0());
					}
					lv_tokens_5_0=ruleSlotToken
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getTokenReplacementSlotRule());
						}
						add(
							$current,
							"tokens",
							lv_tokens_5_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotToken");
						afterParserOrEnumRuleCall();
					}
				)
			)
			(
				(
					{
						newCompositeNode(grammarAccess.getTokenReplacementSlotAccess().getTokensSlotTokenParserRuleCall_4_2_0());
					}
					lv_tokens_6_0=ruleSlotToken
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getTokenReplacementSlotRule());
						}
						add(
							$current,
							"tokens",
							lv_tokens_6_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotToken");
						afterParserOrEnumRuleCall();
					}
				)
			)*
			this_ROUND_CLOSE_7=RULE_ROUND_CLOSE
			{
				newLeafNode(this_ROUND_CLOSE_7, grammarAccess.getTokenReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_3());
			}
		)?
		(
			(
				lv_name_8_0=RULE_SLOTNAME_STRING
				{
					newLeafNode(lv_name_8_0, grammarAccess.getTokenReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getTokenReplacementSlotRule());
					}
					setWithLastConsumed(
						$current,
						"name",
						lv_name_8_0,
						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
				}
			)
		)?
		this_DOUBLE_SQUARE_CLOSE_9=RULE_DOUBLE_SQUARE_CLOSE
		{
			newLeafNode(this_DOUBLE_SQUARE_CLOSE_9, grammarAccess.getTokenReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
		}
	)
;

// Entry rule entryRuleTemplateInformationSlot
entryRuleTemplateInformationSlot returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getTemplateInformationSlotRule()); }
	iv_ruleTemplateInformationSlot=ruleTemplateInformationSlot
	{ $current=$iv_ruleTemplateInformationSlot.current; }
	EOF;

// Rule TemplateInformationSlot
ruleTemplateInformationSlot returns [EObject current=null]
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
					grammarAccess.getTemplateInformationSlotAccess().getTemplateInformationSlotAction_0(),
					$current);
			}
		)
		this_DOUBLE_SQUARE_OPEN_1=RULE_DOUBLE_SQUARE_OPEN
		{
			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getTemplateInformationSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getTemplateInformationSlotAccess().getCardinalityEtlCardinalityParserRuleCall_2_0());
				}
				lv_cardinality_2_0=ruleEtlCardinality
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getTemplateInformationSlotRule());
					}
					set(
						$current,
						"cardinality",
						lv_cardinality_2_0,
						"com.b2international.snowowl.snomed.etl.Etl.EtlCardinality");
					afterParserOrEnumRuleCall();
				}
			)
		)?
		(
			(
				lv_name_3_0=RULE_SLOTNAME_STRING
				{
					newLeafNode(lv_name_3_0, grammarAccess.getTemplateInformationSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_3_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getTemplateInformationSlotRule());
					}
					setWithLastConsumed(
						$current,
						"name",
						lv_name_3_0,
						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
				}
			)
		)?
		this_DOUBLE_SQUARE_CLOSE_4=RULE_DOUBLE_SQUARE_CLOSE
		{
			newLeafNode(this_DOUBLE_SQUARE_CLOSE_4, grammarAccess.getTemplateInformationSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_4());
		}
	)
;

// Entry rule entryRuleConcreteValueReplacementSlot
entryRuleConcreteValueReplacementSlot returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getConcreteValueReplacementSlotRule()); }
	iv_ruleConcreteValueReplacementSlot=ruleConcreteValueReplacementSlot
	{ $current=$iv_ruleConcreteValueReplacementSlot.current; }
	EOF;

// Rule ConcreteValueReplacementSlot
ruleConcreteValueReplacementSlot returns [EObject current=null]
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
			newCompositeNode(grammarAccess.getConcreteValueReplacementSlotAccess().getStringReplacementSlotParserRuleCall_0());
		}
		this_StringReplacementSlot_0=ruleStringReplacementSlot
		{
			$current = $this_StringReplacementSlot_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getConcreteValueReplacementSlotAccess().getIntegerReplacementSlotParserRuleCall_1());
		}
		this_IntegerReplacementSlot_1=ruleIntegerReplacementSlot
		{
			$current = $this_IntegerReplacementSlot_1.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getConcreteValueReplacementSlotAccess().getDecimalReplacementSlotParserRuleCall_2());
		}
		this_DecimalReplacementSlot_2=ruleDecimalReplacementSlot
		{
			$current = $this_DecimalReplacementSlot_2.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleStringReplacementSlot
entryRuleStringReplacementSlot returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getStringReplacementSlotRule()); }
	iv_ruleStringReplacementSlot=ruleStringReplacementSlot
	{ $current=$iv_ruleStringReplacementSlot.current; }
	EOF;

// Rule StringReplacementSlot
ruleStringReplacementSlot returns [EObject current=null]
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
					grammarAccess.getStringReplacementSlotAccess().getStringReplacementSlotAction_0(),
					$current);
			}
		)
		this_DOUBLE_SQUARE_OPEN_1=RULE_DOUBLE_SQUARE_OPEN
		{
			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getStringReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
		}
		this_PLUS_2=RULE_PLUS
		{
			newLeafNode(this_PLUS_2, grammarAccess.getStringReplacementSlotAccess().getPLUSTerminalRuleCall_2());
		}
		this_STR_3=RULE_STR
		{
			newLeafNode(this_STR_3, grammarAccess.getStringReplacementSlotAccess().getSTRTerminalRuleCall_3());
		}
		(
			this_ROUND_OPEN_4=RULE_ROUND_OPEN
			{
				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getStringReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
			}
			(
				(
					lv_values_5_0=RULE_STRING
					{
						newLeafNode(lv_values_5_0, grammarAccess.getStringReplacementSlotAccess().getValuesSTRINGTerminalRuleCall_4_1_0());
					}
					{
						if ($current==null) {
							$current = createModelElement(grammarAccess.getStringReplacementSlotRule());
						}
						addWithLastConsumed(
							$current,
							"values",
							lv_values_5_0,
							"com.b2international.snomed.ecl.Ecl.STRING");
					}
				)
			)
			(
				(
					lv_values_6_0=RULE_STRING
					{
						newLeafNode(lv_values_6_0, grammarAccess.getStringReplacementSlotAccess().getValuesSTRINGTerminalRuleCall_4_2_0());
					}
					{
						if ($current==null) {
							$current = createModelElement(grammarAccess.getStringReplacementSlotRule());
						}
						addWithLastConsumed(
							$current,
							"values",
							lv_values_6_0,
							"com.b2international.snomed.ecl.Ecl.STRING");
					}
				)
			)*
			this_ROUND_CLOSE_7=RULE_ROUND_CLOSE
			{
				newLeafNode(this_ROUND_CLOSE_7, grammarAccess.getStringReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_3());
			}
		)?
		(
			(
				lv_name_8_0=RULE_SLOTNAME_STRING
				{
					newLeafNode(lv_name_8_0, grammarAccess.getStringReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getStringReplacementSlotRule());
					}
					setWithLastConsumed(
						$current,
						"name",
						lv_name_8_0,
						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
				}
			)
		)?
		this_DOUBLE_SQUARE_CLOSE_9=RULE_DOUBLE_SQUARE_CLOSE
		{
			newLeafNode(this_DOUBLE_SQUARE_CLOSE_9, grammarAccess.getStringReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
		}
	)
;

// Entry rule entryRuleIntegerReplacementSlot
entryRuleIntegerReplacementSlot returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getIntegerReplacementSlotRule()); }
	iv_ruleIntegerReplacementSlot=ruleIntegerReplacementSlot
	{ $current=$iv_ruleIntegerReplacementSlot.current; }
	EOF;

// Rule IntegerReplacementSlot
ruleIntegerReplacementSlot returns [EObject current=null]
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
					grammarAccess.getIntegerReplacementSlotAccess().getIntegerReplacementSlotAction_0(),
					$current);
			}
		)
		this_DOUBLE_SQUARE_OPEN_1=RULE_DOUBLE_SQUARE_OPEN
		{
			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getIntegerReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
		}
		this_PLUS_2=RULE_PLUS
		{
			newLeafNode(this_PLUS_2, grammarAccess.getIntegerReplacementSlotAccess().getPLUSTerminalRuleCall_2());
		}
		this_INT_3=RULE_INT
		{
			newLeafNode(this_INT_3, grammarAccess.getIntegerReplacementSlotAccess().getINTTerminalRuleCall_3());
		}
		(
			this_ROUND_OPEN_4=RULE_ROUND_OPEN
			{
				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getIntegerReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getIntegerReplacementSlotAccess().getValuesSlotIntegerParserRuleCall_4_1_0());
					}
					lv_values_5_0=ruleSlotInteger
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getIntegerReplacementSlotRule());
						}
						add(
							$current,
							"values",
							lv_values_5_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotInteger");
						afterParserOrEnumRuleCall();
					}
				)
			)
			(
				(
					{
						newCompositeNode(grammarAccess.getIntegerReplacementSlotAccess().getValuesSlotIntegerParserRuleCall_4_2_0());
					}
					lv_values_6_0=ruleSlotInteger
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getIntegerReplacementSlotRule());
						}
						add(
							$current,
							"values",
							lv_values_6_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotInteger");
						afterParserOrEnumRuleCall();
					}
				)
			)*
			this_ROUND_CLOSE_7=RULE_ROUND_CLOSE
			{
				newLeafNode(this_ROUND_CLOSE_7, grammarAccess.getIntegerReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_3());
			}
		)?
		(
			(
				lv_name_8_0=RULE_SLOTNAME_STRING
				{
					newLeafNode(lv_name_8_0, grammarAccess.getIntegerReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getIntegerReplacementSlotRule());
					}
					setWithLastConsumed(
						$current,
						"name",
						lv_name_8_0,
						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
				}
			)
		)?
		this_DOUBLE_SQUARE_CLOSE_9=RULE_DOUBLE_SQUARE_CLOSE
		{
			newLeafNode(this_DOUBLE_SQUARE_CLOSE_9, grammarAccess.getIntegerReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
		}
	)
;

// Entry rule entryRuleDecimalReplacementSlot
entryRuleDecimalReplacementSlot returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDecimalReplacementSlotRule()); }
	iv_ruleDecimalReplacementSlot=ruleDecimalReplacementSlot
	{ $current=$iv_ruleDecimalReplacementSlot.current; }
	EOF;

// Rule DecimalReplacementSlot
ruleDecimalReplacementSlot returns [EObject current=null]
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
					grammarAccess.getDecimalReplacementSlotAccess().getDecimalReplacementSlotAction_0(),
					$current);
			}
		)
		this_DOUBLE_SQUARE_OPEN_1=RULE_DOUBLE_SQUARE_OPEN
		{
			newLeafNode(this_DOUBLE_SQUARE_OPEN_1, grammarAccess.getDecimalReplacementSlotAccess().getDOUBLE_SQUARE_OPENTerminalRuleCall_1());
		}
		this_PLUS_2=RULE_PLUS
		{
			newLeafNode(this_PLUS_2, grammarAccess.getDecimalReplacementSlotAccess().getPLUSTerminalRuleCall_2());
		}
		this_DEC_3=RULE_DEC
		{
			newLeafNode(this_DEC_3, grammarAccess.getDecimalReplacementSlotAccess().getDECTerminalRuleCall_3());
		}
		(
			this_ROUND_OPEN_4=RULE_ROUND_OPEN
			{
				newLeafNode(this_ROUND_OPEN_4, grammarAccess.getDecimalReplacementSlotAccess().getROUND_OPENTerminalRuleCall_4_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getDecimalReplacementSlotAccess().getValuesSlotDecimalParserRuleCall_4_1_0());
					}
					lv_values_5_0=ruleSlotDecimal
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getDecimalReplacementSlotRule());
						}
						add(
							$current,
							"values",
							lv_values_5_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotDecimal");
						afterParserOrEnumRuleCall();
					}
				)
			)
			(
				(
					{
						newCompositeNode(grammarAccess.getDecimalReplacementSlotAccess().getValuesSlotDecimalParserRuleCall_4_2_0());
					}
					lv_values_6_0=ruleSlotDecimal
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getDecimalReplacementSlotRule());
						}
						add(
							$current,
							"values",
							lv_values_6_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotDecimal");
						afterParserOrEnumRuleCall();
					}
				)
			)*
			this_ROUND_CLOSE_7=RULE_ROUND_CLOSE
			{
				newLeafNode(this_ROUND_CLOSE_7, grammarAccess.getDecimalReplacementSlotAccess().getROUND_CLOSETerminalRuleCall_4_3());
			}
		)?
		(
			(
				lv_name_8_0=RULE_SLOTNAME_STRING
				{
					newLeafNode(lv_name_8_0, grammarAccess.getDecimalReplacementSlotAccess().getNameSLOTNAME_STRINGTerminalRuleCall_5_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getDecimalReplacementSlotRule());
					}
					setWithLastConsumed(
						$current,
						"name",
						lv_name_8_0,
						"com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
				}
			)
		)?
		this_DOUBLE_SQUARE_CLOSE_9=RULE_DOUBLE_SQUARE_CLOSE
		{
			newLeafNode(this_DOUBLE_SQUARE_CLOSE_9, grammarAccess.getDecimalReplacementSlotAccess().getDOUBLE_SQUARE_CLOSETerminalRuleCall_6());
		}
	)
;

// Entry rule entryRuleEtlCardinality
entryRuleEtlCardinality returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getEtlCardinalityRule()); }
	iv_ruleEtlCardinality=ruleEtlCardinality
	{ $current=$iv_ruleEtlCardinality.current; }
	EOF;

// Rule EtlCardinality
ruleEtlCardinality returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			this_TILDE_0=RULE_TILDE
			{
				newLeafNode(this_TILDE_0, grammarAccess.getEtlCardinalityAccess().getTILDETerminalRuleCall_0());
			}
		)?
		(
			(
				{
					newCompositeNode(grammarAccess.getEtlCardinalityAccess().getMinNonNegativeIntegerParserRuleCall_1_0());
				}
				lv_min_1_0=ruleNonNegativeInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getEtlCardinalityRule());
					}
					set(
						$current,
						"min",
						lv_min_1_0,
						"com.b2international.snomed.ecl.Ecl.NonNegativeInteger");
					afterParserOrEnumRuleCall();
				}
			)
		)
		this_TO_2=RULE_TO
		{
			newLeafNode(this_TO_2, grammarAccess.getEtlCardinalityAccess().getTOTerminalRuleCall_2());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getEtlCardinalityAccess().getMaxMaxValueParserRuleCall_3_0());
				}
				lv_max_3_0=ruleMaxValue
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getEtlCardinalityRule());
					}
					set(
						$current,
						"max",
						lv_max_3_0,
						"com.b2international.snomed.ecl.Ecl.MaxValue");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleConceptReplacementSlot
entryRuleConceptReplacementSlot returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getConceptReplacementSlotRule()); }
	iv_ruleConceptReplacementSlot=ruleConceptReplacementSlot
	{ $current=$iv_ruleConceptReplacementSlot.current; }
	EOF;

// Rule ConceptReplacementSlot
ruleConceptReplacementSlot returns [EObject current=null]
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
			newCompositeNode(grammarAccess.getConceptReplacementSlotAccess().getConceptIdReplacementSlotParserRuleCall_0());
		}
		this_ConceptIdReplacementSlot_0=ruleConceptIdReplacementSlot
		{
			$current = $this_ConceptIdReplacementSlot_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getConceptReplacementSlotAccess().getExpressionReplacementSlotParserRuleCall_1());
		}
		this_ExpressionReplacementSlot_1=ruleExpressionReplacementSlot
		{
			$current = $this_ExpressionReplacementSlot_1.current;
			afterParserOrEnumRuleCall();
		}
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
					newCompositeNode(grammarAccess.getConceptReferenceAccess().getSlotConceptReplacementSlotParserRuleCall_0_0());
				}
				lv_slot_0_0=ruleConceptReplacementSlot
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getConceptReferenceRule());
					}
					set(
						$current,
						"slot",
						lv_slot_0_0,
						"com.b2international.snowowl.snomed.etl.Etl.ConceptReplacementSlot");
					afterParserOrEnumRuleCall();
				}
			)
		)
		    |
		(
			(
				(
					{
						newCompositeNode(grammarAccess.getConceptReferenceAccess().getIdSnomedIdentifierParserRuleCall_1_0_0());
					}
					lv_id_1_0=ruleSnomedIdentifier
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getConceptReferenceRule());
						}
						set(
							$current,
							"id",
							lv_id_1_0,
							"com.b2international.snomed.ecl.Ecl.SnomedIdentifier");
						afterParserOrEnumRuleCall();
					}
				)
			)
			(
				(
					lv_term_2_0=RULE_TERM_STRING
					{
						newLeafNode(lv_term_2_0, grammarAccess.getConceptReferenceAccess().getTermTERM_STRINGTerminalRuleCall_1_1_0());
					}
					{
						if ($current==null) {
							$current = createModelElement(grammarAccess.getConceptReferenceRule());
						}
						setWithLastConsumed(
							$current,
							"term",
							lv_term_2_0,
							"com.b2international.snomed.ecl.Ecl.TERM_STRING");
					}
				)
			)?
		)
	)
;

// Entry rule entryRuleSlotToken
entryRuleSlotToken returns [String current=null]:
	{ newCompositeNode(grammarAccess.getSlotTokenRule()); }
	iv_ruleSlotToken=ruleSlotToken
	{ $current=$iv_ruleSlotToken.current.getText(); }
	EOF;

// Rule SlotToken
ruleSlotToken returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_EQUIVALENT_TO_0=RULE_EQUIVALENT_TO
		{
			$current.merge(this_EQUIVALENT_TO_0);
		}
		{
			newLeafNode(this_EQUIVALENT_TO_0, grammarAccess.getSlotTokenAccess().getEQUIVALENT_TOTerminalRuleCall_0());
		}
		    |
		this_SUBTYPE_OF_1=RULE_SUBTYPE_OF
		{
			$current.merge(this_SUBTYPE_OF_1);
		}
		{
			newLeafNode(this_SUBTYPE_OF_1, grammarAccess.getSlotTokenAccess().getSUBTYPE_OFTerminalRuleCall_1());
		}
		    |
		this_COMMA_2=RULE_COMMA
		{
			$current.merge(this_COMMA_2);
		}
		{
			newLeafNode(this_COMMA_2, grammarAccess.getSlotTokenAccess().getCOMMATerminalRuleCall_2());
		}
		    |
		this_CONJUNCTION_3=RULE_CONJUNCTION
		{
			$current.merge(this_CONJUNCTION_3);
		}
		{
			newLeafNode(this_CONJUNCTION_3, grammarAccess.getSlotTokenAccess().getCONJUNCTIONTerminalRuleCall_3());
		}
		    |
		this_DISJUNCTION_4=RULE_DISJUNCTION
		{
			$current.merge(this_DISJUNCTION_4);
		}
		{
			newLeafNode(this_DISJUNCTION_4, grammarAccess.getSlotTokenAccess().getDISJUNCTIONTerminalRuleCall_4());
		}
		    |
		this_EXCLUSION_5=RULE_EXCLUSION
		{
			$current.merge(this_EXCLUSION_5);
		}
		{
			newLeafNode(this_EXCLUSION_5, grammarAccess.getSlotTokenAccess().getEXCLUSIONTerminalRuleCall_5());
		}
		    |
		this_REVERSED_6=RULE_REVERSED
		{
			$current.merge(this_REVERSED_6);
		}
		{
			newLeafNode(this_REVERSED_6, grammarAccess.getSlotTokenAccess().getREVERSEDTerminalRuleCall_6());
		}
		    |
		this_CARET_7=RULE_CARET
		{
			$current.merge(this_CARET_7);
		}
		{
			newLeafNode(this_CARET_7, grammarAccess.getSlotTokenAccess().getCARETTerminalRuleCall_7());
		}
		    |
		this_LT_8=RULE_LT
		{
			$current.merge(this_LT_8);
		}
		{
			newLeafNode(this_LT_8, grammarAccess.getSlotTokenAccess().getLTTerminalRuleCall_8());
		}
		    |
		this_LTE_9=RULE_LTE
		{
			$current.merge(this_LTE_9);
		}
		{
			newLeafNode(this_LTE_9, grammarAccess.getSlotTokenAccess().getLTETerminalRuleCall_9());
		}
		    |
		this_DBL_LT_10=RULE_DBL_LT
		{
			$current.merge(this_DBL_LT_10);
		}
		{
			newLeafNode(this_DBL_LT_10, grammarAccess.getSlotTokenAccess().getDBL_LTTerminalRuleCall_10());
		}
		    |
		this_LT_EM_11=RULE_LT_EM
		{
			$current.merge(this_LT_EM_11);
		}
		{
			newLeafNode(this_LT_EM_11, grammarAccess.getSlotTokenAccess().getLT_EMTerminalRuleCall_11());
		}
		    |
		this_GT_12=RULE_GT
		{
			$current.merge(this_GT_12);
		}
		{
			newLeafNode(this_GT_12, grammarAccess.getSlotTokenAccess().getGTTerminalRuleCall_12());
		}
		    |
		this_GTE_13=RULE_GTE
		{
			$current.merge(this_GTE_13);
		}
		{
			newLeafNode(this_GTE_13, grammarAccess.getSlotTokenAccess().getGTETerminalRuleCall_13());
		}
		    |
		this_DBL_GT_14=RULE_DBL_GT
		{
			$current.merge(this_DBL_GT_14);
		}
		{
			newLeafNode(this_DBL_GT_14, grammarAccess.getSlotTokenAccess().getDBL_GTTerminalRuleCall_14());
		}
		    |
		this_GT_EM_15=RULE_GT_EM
		{
			$current.merge(this_GT_EM_15);
		}
		{
			newLeafNode(this_GT_EM_15, grammarAccess.getSlotTokenAccess().getGT_EMTerminalRuleCall_15());
		}
		    |
		this_EQUAL_16=RULE_EQUAL
		{
			$current.merge(this_EQUAL_16);
		}
		{
			newLeafNode(this_EQUAL_16, grammarAccess.getSlotTokenAccess().getEQUALTerminalRuleCall_16());
		}
		    |
		this_NOT_EQUAL_17=RULE_NOT_EQUAL
		{
			$current.merge(this_NOT_EQUAL_17);
		}
		{
			newLeafNode(this_NOT_EQUAL_17, grammarAccess.getSlotTokenAccess().getNOT_EQUALTerminalRuleCall_17());
		}
	)
;

// Entry rule entryRuleStringValue
entryRuleStringValue returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getStringValueRule()); }
	iv_ruleStringValue=ruleStringValue
	{ $current=$iv_ruleStringValue.current; }
	EOF;

// Rule StringValue
ruleStringValue returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			lv_value_0_0=RULE_STRING
			{
				newLeafNode(lv_value_0_0, grammarAccess.getStringValueAccess().getValueSTRINGTerminalRuleCall_0());
			}
			{
				if ($current==null) {
					$current = createModelElement(grammarAccess.getStringValueRule());
				}
				setWithLastConsumed(
					$current,
					"value",
					lv_value_0_0,
					"com.b2international.snomed.ecl.Ecl.STRING");
			}
		)
	)
;

// Entry rule entryRuleIntegerValue
entryRuleIntegerValue returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getIntegerValueRule()); }
	iv_ruleIntegerValue=ruleIntegerValue
	{ $current=$iv_ruleIntegerValue.current; }
	EOF;

// Rule IntegerValue
ruleIntegerValue returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_HASH_0=RULE_HASH
		{
			newLeafNode(this_HASH_0, grammarAccess.getIntegerValueAccess().getHASHTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getIntegerValueAccess().getValueIntegerParserRuleCall_1_0());
				}
				lv_value_1_0=ruleInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getIntegerValueRule());
					}
					set(
						$current,
						"value",
						lv_value_1_0,
						"com.b2international.snomed.ecl.Ecl.Integer");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleDecimalValue
entryRuleDecimalValue returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getDecimalValueRule()); }
	iv_ruleDecimalValue=ruleDecimalValue
	{ $current=$iv_ruleDecimalValue.current; }
	EOF;

// Rule DecimalValue
ruleDecimalValue returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_HASH_0=RULE_HASH
		{
			newLeafNode(this_HASH_0, grammarAccess.getDecimalValueAccess().getHASHTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getDecimalValueAccess().getValueDecimalParserRuleCall_1_0());
				}
				lv_value_1_0=ruleDecimal
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getDecimalValueRule());
					}
					set(
						$current,
						"value",
						lv_value_1_0,
						"com.b2international.snomed.ecl.Ecl.Decimal");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleSlotInteger
entryRuleSlotInteger returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSlotIntegerRule()); }
	iv_ruleSlotInteger=ruleSlotInteger
	{ $current=$iv_ruleSlotInteger.current; }
	EOF;

// Rule SlotInteger
ruleSlotInteger returns [EObject current=null]
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
			newCompositeNode(grammarAccess.getSlotIntegerAccess().getSlotIntegerRangeParserRuleCall_0());
		}
		this_SlotIntegerRange_0=ruleSlotIntegerRange
		{
			$current = $this_SlotIntegerRange_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSlotIntegerAccess().getSlotIntegerValueParserRuleCall_1());
		}
		this_SlotIntegerValue_1=ruleSlotIntegerValue
		{
			$current = $this_SlotIntegerValue_1.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleSlotIntegerValue
entryRuleSlotIntegerValue returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSlotIntegerValueRule()); }
	iv_ruleSlotIntegerValue=ruleSlotIntegerValue
	{ $current=$iv_ruleSlotIntegerValue.current; }
	EOF;

// Rule SlotIntegerValue
ruleSlotIntegerValue returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_HASH_0=RULE_HASH
		{
			newLeafNode(this_HASH_0, grammarAccess.getSlotIntegerValueAccess().getHASHTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getSlotIntegerValueAccess().getValueNonNegativeIntegerParserRuleCall_1_0());
				}
				lv_value_1_0=ruleNonNegativeInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getSlotIntegerValueRule());
					}
					set(
						$current,
						"value",
						lv_value_1_0,
						"com.b2international.snomed.ecl.Ecl.NonNegativeInteger");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleSlotIntegerRange
entryRuleSlotIntegerRange returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSlotIntegerRangeRule()); }
	iv_ruleSlotIntegerRange=ruleSlotIntegerRange
	{ $current=$iv_ruleSlotIntegerRange.current; }
	EOF;

// Rule SlotIntegerRange
ruleSlotIntegerRange returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			(
				(
					{
						newCompositeNode(grammarAccess.getSlotIntegerRangeAccess().getMinimumSlotIntegerMinimumValueParserRuleCall_0_0_0());
					}
					lv_minimum_0_0=ruleSlotIntegerMinimumValue
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getSlotIntegerRangeRule());
						}
						set(
							$current,
							"minimum",
							lv_minimum_0_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotIntegerMinimumValue");
						afterParserOrEnumRuleCall();
					}
				)
			)
			this_TO_1=RULE_TO
			{
				newLeafNode(this_TO_1, grammarAccess.getSlotIntegerRangeAccess().getTOTerminalRuleCall_0_1());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getSlotIntegerRangeAccess().getMaximumSlotIntegerMaximumValueParserRuleCall_0_2_0());
					}
					lv_maximum_2_0=ruleSlotIntegerMaximumValue
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getSlotIntegerRangeRule());
						}
						set(
							$current,
							"maximum",
							lv_maximum_2_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotIntegerMaximumValue");
						afterParserOrEnumRuleCall();
					}
				)
			)?
		)
		    |
		(
			this_TO_3=RULE_TO
			{
				newLeafNode(this_TO_3, grammarAccess.getSlotIntegerRangeAccess().getTOTerminalRuleCall_1_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getSlotIntegerRangeAccess().getMaximumSlotIntegerMaximumValueParserRuleCall_1_1_0());
					}
					lv_maximum_4_0=ruleSlotIntegerMaximumValue
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getSlotIntegerRangeRule());
						}
						set(
							$current,
							"maximum",
							lv_maximum_4_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotIntegerMaximumValue");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)
	)
;

// Entry rule entryRuleSlotIntegerMinimumValue
entryRuleSlotIntegerMinimumValue returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSlotIntegerMinimumValueRule()); }
	iv_ruleSlotIntegerMinimumValue=ruleSlotIntegerMinimumValue
	{ $current=$iv_ruleSlotIntegerMinimumValue.current; }
	EOF;

// Rule SlotIntegerMinimumValue
ruleSlotIntegerMinimumValue returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			(
				lv_exclusive_0_0=RULE_GT
				{
					newLeafNode(lv_exclusive_0_0, grammarAccess.getSlotIntegerMinimumValueAccess().getExclusiveGTTerminalRuleCall_0_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getSlotIntegerMinimumValueRule());
					}
					setWithLastConsumed(
						$current,
						"exclusive",
						lv_exclusive_0_0 != null,
						"com.b2international.snomed.ecl.Ecl.GT");
				}
			)
		)?
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getSlotIntegerMinimumValueAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getSlotIntegerMinimumValueAccess().getValueNonNegativeIntegerParserRuleCall_2_0());
				}
				lv_value_2_0=ruleNonNegativeInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getSlotIntegerMinimumValueRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snomed.ecl.Ecl.NonNegativeInteger");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleSlotIntegerMaximumValue
entryRuleSlotIntegerMaximumValue returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSlotIntegerMaximumValueRule()); }
	iv_ruleSlotIntegerMaximumValue=ruleSlotIntegerMaximumValue
	{ $current=$iv_ruleSlotIntegerMaximumValue.current; }
	EOF;

// Rule SlotIntegerMaximumValue
ruleSlotIntegerMaximumValue returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			(
				lv_exclusive_0_0=RULE_LT
				{
					newLeafNode(lv_exclusive_0_0, grammarAccess.getSlotIntegerMaximumValueAccess().getExclusiveLTTerminalRuleCall_0_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getSlotIntegerMaximumValueRule());
					}
					setWithLastConsumed(
						$current,
						"exclusive",
						lv_exclusive_0_0 != null,
						"com.b2international.snomed.ecl.Ecl.LT");
				}
			)
		)?
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getSlotIntegerMaximumValueAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getSlotIntegerMaximumValueAccess().getValueNonNegativeIntegerParserRuleCall_2_0());
				}
				lv_value_2_0=ruleNonNegativeInteger
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getSlotIntegerMaximumValueRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snomed.ecl.Ecl.NonNegativeInteger");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleSlotDecimal
entryRuleSlotDecimal returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSlotDecimalRule()); }
	iv_ruleSlotDecimal=ruleSlotDecimal
	{ $current=$iv_ruleSlotDecimal.current; }
	EOF;

// Rule SlotDecimal
ruleSlotDecimal returns [EObject current=null]
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
			newCompositeNode(grammarAccess.getSlotDecimalAccess().getSlotDecimalRangeParserRuleCall_0());
		}
		this_SlotDecimalRange_0=ruleSlotDecimalRange
		{
			$current = $this_SlotDecimalRange_0.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSlotDecimalAccess().getSlotDecimalValueParserRuleCall_1());
		}
		this_SlotDecimalValue_1=ruleSlotDecimalValue
		{
			$current = $this_SlotDecimalValue_1.current;
			afterParserOrEnumRuleCall();
		}
	)
;

// Entry rule entryRuleSlotDecimalValue
entryRuleSlotDecimalValue returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSlotDecimalValueRule()); }
	iv_ruleSlotDecimalValue=ruleSlotDecimalValue
	{ $current=$iv_ruleSlotDecimalValue.current; }
	EOF;

// Rule SlotDecimalValue
ruleSlotDecimalValue returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_HASH_0=RULE_HASH
		{
			newLeafNode(this_HASH_0, grammarAccess.getSlotDecimalValueAccess().getHASHTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getSlotDecimalValueAccess().getValueNonNegativeDecimalParserRuleCall_1_0());
				}
				lv_value_1_0=ruleNonNegativeDecimal
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getSlotDecimalValueRule());
					}
					set(
						$current,
						"value",
						lv_value_1_0,
						"com.b2international.snomed.ecl.Ecl.NonNegativeDecimal");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleSlotDecimalRange
entryRuleSlotDecimalRange returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSlotDecimalRangeRule()); }
	iv_ruleSlotDecimalRange=ruleSlotDecimalRange
	{ $current=$iv_ruleSlotDecimalRange.current; }
	EOF;

// Rule SlotDecimalRange
ruleSlotDecimalRange returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			(
				(
					{
						newCompositeNode(grammarAccess.getSlotDecimalRangeAccess().getMinimumSlotDecimalMinimumValueParserRuleCall_0_0_0());
					}
					lv_minimum_0_0=ruleSlotDecimalMinimumValue
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getSlotDecimalRangeRule());
						}
						set(
							$current,
							"minimum",
							lv_minimum_0_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotDecimalMinimumValue");
						afterParserOrEnumRuleCall();
					}
				)
			)
			this_TO_1=RULE_TO
			{
				newLeafNode(this_TO_1, grammarAccess.getSlotDecimalRangeAccess().getTOTerminalRuleCall_0_1());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getSlotDecimalRangeAccess().getMaximumSlotDecimalMaximumValueParserRuleCall_0_2_0());
					}
					lv_maximum_2_0=ruleSlotDecimalMaximumValue
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getSlotDecimalRangeRule());
						}
						set(
							$current,
							"maximum",
							lv_maximum_2_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotDecimalMaximumValue");
						afterParserOrEnumRuleCall();
					}
				)
			)?
		)
		    |
		(
			this_TO_3=RULE_TO
			{
				newLeafNode(this_TO_3, grammarAccess.getSlotDecimalRangeAccess().getTOTerminalRuleCall_1_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getSlotDecimalRangeAccess().getMaximumSlotDecimalMaximumValueParserRuleCall_1_1_0());
					}
					lv_maximum_4_0=ruleSlotDecimalMaximumValue
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getSlotDecimalRangeRule());
						}
						set(
							$current,
							"maximum",
							lv_maximum_4_0,
							"com.b2international.snowowl.snomed.etl.Etl.SlotDecimalMaximumValue");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)
	)
;

// Entry rule entryRuleSlotDecimalMinimumValue
entryRuleSlotDecimalMinimumValue returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSlotDecimalMinimumValueRule()); }
	iv_ruleSlotDecimalMinimumValue=ruleSlotDecimalMinimumValue
	{ $current=$iv_ruleSlotDecimalMinimumValue.current; }
	EOF;

// Rule SlotDecimalMinimumValue
ruleSlotDecimalMinimumValue returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			(
				lv_exclusive_0_0=RULE_GT
				{
					newLeafNode(lv_exclusive_0_0, grammarAccess.getSlotDecimalMinimumValueAccess().getExclusiveGTTerminalRuleCall_0_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getSlotDecimalMinimumValueRule());
					}
					setWithLastConsumed(
						$current,
						"exclusive",
						lv_exclusive_0_0 != null,
						"com.b2international.snomed.ecl.Ecl.GT");
				}
			)
		)?
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getSlotDecimalMinimumValueAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getSlotDecimalMinimumValueAccess().getValueNonNegativeDecimalParserRuleCall_2_0());
				}
				lv_value_2_0=ruleNonNegativeDecimal
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getSlotDecimalMinimumValueRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snomed.ecl.Ecl.NonNegativeDecimal");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleSlotDecimalMaximumValue
entryRuleSlotDecimalMaximumValue returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getSlotDecimalMaximumValueRule()); }
	iv_ruleSlotDecimalMaximumValue=ruleSlotDecimalMaximumValue
	{ $current=$iv_ruleSlotDecimalMaximumValue.current; }
	EOF;

// Rule SlotDecimalMaximumValue
ruleSlotDecimalMaximumValue returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		(
			(
				lv_exclusive_0_0=RULE_LT
				{
					newLeafNode(lv_exclusive_0_0, grammarAccess.getSlotDecimalMaximumValueAccess().getExclusiveLTTerminalRuleCall_0_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getSlotDecimalMaximumValueRule());
					}
					setWithLastConsumed(
						$current,
						"exclusive",
						lv_exclusive_0_0 != null,
						"com.b2international.snomed.ecl.Ecl.LT");
				}
			)
		)?
		this_HASH_1=RULE_HASH
		{
			newLeafNode(this_HASH_1, grammarAccess.getSlotDecimalMaximumValueAccess().getHASHTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getSlotDecimalMaximumValueAccess().getValueNonNegativeDecimalParserRuleCall_2_0());
				}
				lv_value_2_0=ruleNonNegativeDecimal
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getSlotDecimalMaximumValueRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snomed.ecl.Ecl.NonNegativeDecimal");
					afterParserOrEnumRuleCall();
				}
			)
		)
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
							"com.b2international.snomed.ecl.Ecl.AndExpressionConstraint");
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
							"com.b2international.snomed.ecl.Ecl.ExclusionExpressionConstraint");
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
							"com.b2international.snomed.ecl.Ecl.RefinedExpressionConstraint");
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
							"com.b2international.snomed.ecl.Ecl.EclRefinement");
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
							"com.b2international.snomed.ecl.Ecl.SubExpressionConstraint");
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
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getChildOrSelfOfParserRuleCall_1());
		}
		this_ChildOrSelfOf_1=ruleChildOrSelfOf
		{
			$current = $this_ChildOrSelfOf_1.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getDescendantOfParserRuleCall_2());
		}
		this_DescendantOf_2=ruleDescendantOf
		{
			$current = $this_DescendantOf_2.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getDescendantOrSelfOfParserRuleCall_3());
		}
		this_DescendantOrSelfOf_3=ruleDescendantOrSelfOf
		{
			$current = $this_DescendantOrSelfOf_3.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getParentOfParserRuleCall_4());
		}
		this_ParentOf_4=ruleParentOf
		{
			$current = $this_ParentOf_4.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getParentOrSelfOfParserRuleCall_5());
		}
		this_ParentOrSelfOf_5=ruleParentOrSelfOf
		{
			$current = $this_ParentOrSelfOf_5.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getAncestorOfParserRuleCall_6());
		}
		this_AncestorOf_6=ruleAncestorOf
		{
			$current = $this_AncestorOf_6.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getAncestorOrSelfOfParserRuleCall_7());
		}
		this_AncestorOrSelfOf_7=ruleAncestorOrSelfOf
		{
			$current = $this_AncestorOrSelfOf_7.current;
			afterParserOrEnumRuleCall();
		}
		    |
		{
			/* */
		}
		{
			newCompositeNode(grammarAccess.getSubExpressionConstraintAccess().getEclFocusConceptParserRuleCall_8());
		}
		this_EclFocusConcept_8=ruleEclFocusConcept
		{
			$current = $this_EclFocusConcept_8.current;
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
						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleChildOrSelfOf
entryRuleChildOrSelfOf returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getChildOrSelfOfRule()); }
	iv_ruleChildOrSelfOf=ruleChildOrSelfOf
	{ $current=$iv_ruleChildOrSelfOf.current; }
	EOF;

// Rule ChildOrSelfOf
ruleChildOrSelfOf returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_DBL_LT_EM_0=RULE_DBL_LT_EM
		{
			newLeafNode(this_DBL_LT_EM_0, grammarAccess.getChildOrSelfOfAccess().getDBL_LT_EMTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getChildOrSelfOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleEclFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getChildOrSelfOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
					afterParserOrEnumRuleCall();
				}
			)
		)
	)
;

// Entry rule entryRuleParentOrSelfOf
entryRuleParentOrSelfOf returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getParentOrSelfOfRule()); }
	iv_ruleParentOrSelfOf=ruleParentOrSelfOf
	{ $current=$iv_ruleParentOrSelfOf.current; }
	EOF;

// Rule ParentOrSelfOf
ruleParentOrSelfOf returns [EObject current=null]
@init {
	enterRule();
}
@after {
	leaveRule();
}:
	(
		this_DBL_GT_EM_0=RULE_DBL_GT_EM
		{
			newLeafNode(this_DBL_GT_EM_0, grammarAccess.getParentOrSelfOfAccess().getDBL_GT_EMTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getParentOrSelfOfAccess().getConstraintEclFocusConceptParserRuleCall_1_0());
				}
				lv_constraint_1_0=ruleEclFocusConcept
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getParentOrSelfOfRule());
					}
					set(
						$current,
						"constraint",
						lv_constraint_1_0,
						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
						"com.b2international.snomed.ecl.Ecl.EclFocusConcept");
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
							"com.b2international.snomed.ecl.Ecl.EclConceptReference");
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
							"com.b2international.snomed.ecl.Ecl.Any");
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
							"com.b2international.snomed.ecl.Ecl.NestedExpression");
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
						"com.b2international.snomed.ecl.Ecl.SnomedIdentifier");
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
						"com.b2international.snomed.ecl.Ecl.TERM_STRING");
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
								"com.b2international.snomed.ecl.Ecl.AndRefinement");
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
								"com.b2international.snomed.ecl.Ecl.SubRefinement");
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
						"com.b2international.snomed.ecl.Ecl.EclRefinement");
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
						"com.b2international.snomed.ecl.Ecl.Cardinality");
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
						"com.b2international.snomed.ecl.Ecl.EclAttributeSet");
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
							"com.b2international.snomed.ecl.Ecl.AndAttributeSet");
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
							"com.b2international.snomed.ecl.Ecl.SubAttributeSet");
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
						"com.b2international.snomed.ecl.Ecl.EclAttributeSet");
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
						"com.b2international.snomed.ecl.Ecl.Cardinality");
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
						lv_reversed_1_0 != null,
						"com.b2international.snomed.ecl.Ecl.REVERSED");
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
						"com.b2international.snomed.ecl.Ecl.SubExpressionConstraint");
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
						"com.b2international.snomed.ecl.Ecl.Comparison");
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
						"com.b2international.snomed.ecl.Ecl.NonNegativeInteger");
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
						"com.b2international.snomed.ecl.Ecl.MaxValue");
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
						"com.b2international.snomed.ecl.Ecl.SubExpressionConstraint");
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
						"com.b2international.snomed.ecl.Ecl.SubExpressionConstraint");
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
						"com.b2international.snomed.ecl.Ecl.Boolean");
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
						"com.b2international.snomed.ecl.Ecl.Boolean");
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
						"com.b2international.snomed.ecl.Ecl.STRING");
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
						"com.b2international.snomed.ecl.Ecl.STRING");
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
						"com.b2international.snomed.ecl.Ecl.Integer");
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
						"com.b2international.snomed.ecl.Ecl.Integer");
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
						"com.b2international.snomed.ecl.Ecl.Integer");
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
						"com.b2international.snomed.ecl.Ecl.Integer");
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
						"com.b2international.snomed.ecl.Ecl.Integer");
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
						"com.b2international.snomed.ecl.Ecl.Integer");
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
						"com.b2international.snomed.ecl.Ecl.Decimal");
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
						"com.b2international.snomed.ecl.Ecl.Decimal");
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
						"com.b2international.snomed.ecl.Ecl.Decimal");
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
						"com.b2international.snomed.ecl.Ecl.Decimal");
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
						"com.b2international.snomed.ecl.Ecl.Decimal");
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
						"com.b2international.snomed.ecl.Ecl.Decimal");
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
						"com.b2international.snomed.ecl.Ecl.ExpressionConstraint");
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
