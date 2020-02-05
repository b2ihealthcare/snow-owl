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
parser grammar InternalScgParser;

options {
	tokenVocab=InternalScgLexer;
	superClass=AbstractInternalAntlrParser;
	backtrack=true;
}

@header {
package com.b2international.snowowl.snomed.scg.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import com.b2international.snowowl.snomed.scg.services.ScgGrammarAccess;

}

@members {

/*
  This grammar contains a lot of empty actions to work around a bug in ANTLR.
  Otherwise the ANTLR tool will create synpreds that cannot be compiled in some rare cases.
*/

 	private ScgGrammarAccess grammarAccess;

    public InternalScgParser(TokenStream input, ScgGrammarAccess grammarAccess) {
        this(input);
        this.grammarAccess = grammarAccess;
        registerRules(grammarAccess.getGrammar());
    }

    @Override
    protected String getFirstRuleName() {
    	return "Expression";
   	}

   	@Override
   	protected ScgGrammarAccess getGrammarAccess() {
   		return grammarAccess;
   	}

}

@rulecatch {
    catch (RecognitionException re) {
        recover(input,re);
        appendSkippedTokens();
    }
}

// Entry rule entryRuleExpression
entryRuleExpression returns [EObject current=null]:
	{ newCompositeNode(grammarAccess.getExpressionRule()); }
	iv_ruleExpression=ruleExpression
	{ $current=$iv_ruleExpression.current; }
	EOF;

// Rule Expression
ruleExpression returns [EObject current=null]
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
					grammarAccess.getExpressionAccess().getExpressionAction_0(),
					$current);
			}
		)
		(
			(
				(
					(
						lv_primitive_1_0=RULE_SUBTYPE_OF
						{
							newLeafNode(lv_primitive_1_0, grammarAccess.getExpressionAccess().getPrimitiveSUBTYPE_OFTerminalRuleCall_1_0_0_0());
						}
						{
							if ($current==null) {
								$current = createModelElement(grammarAccess.getExpressionRule());
							}
							setWithLastConsumed(
								$current,
								"primitive",
								true,
								"com.b2international.snowowl.snomed.scg.Scg.SUBTYPE_OF");
						}
					)
				)
				    |
				this_EQUIVALENT_TO_2=RULE_EQUIVALENT_TO
				{
					newLeafNode(this_EQUIVALENT_TO_2, grammarAccess.getExpressionAccess().getEQUIVALENT_TOTerminalRuleCall_1_0_1());
				}
			)?
			(
				(
					{
						newCompositeNode(grammarAccess.getExpressionAccess().getExpressionSubExpressionParserRuleCall_1_1_0());
					}
					lv_expression_3_0=ruleSubExpression
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getExpressionRule());
						}
						set(
							$current,
							"expression",
							lv_expression_3_0,
							"com.b2international.snowowl.snomed.scg.Scg.SubExpression");
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
					newCompositeNode(grammarAccess.getSubExpressionAccess().getFocusConceptsConceptReferenceParserRuleCall_0_0());
				}
				lv_focusConcepts_0_0=ruleConceptReference
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getSubExpressionRule());
					}
					add(
						$current,
						"focusConcepts",
						lv_focusConcepts_0_0,
						"com.b2international.snowowl.snomed.scg.Scg.ConceptReference");
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
						newCompositeNode(grammarAccess.getSubExpressionAccess().getFocusConceptsConceptReferenceParserRuleCall_1_1_0());
					}
					lv_focusConcepts_2_0=ruleConceptReference
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getSubExpressionRule());
						}
						add(
							$current,
							"focusConcepts",
							lv_focusConcepts_2_0,
							"com.b2international.snowowl.snomed.scg.Scg.ConceptReference");
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
							"com.b2international.snowowl.snomed.scg.Scg.Refinement");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)?
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
								"com.b2international.snowowl.snomed.scg.Scg.Attribute");
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
									"com.b2international.snowowl.snomed.scg.Scg.Attribute");
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
							"com.b2international.snowowl.snomed.scg.Scg.AttributeGroup");
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
							"com.b2international.snowowl.snomed.scg.Scg.AttributeGroup");
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
		this_CURLY_OPEN_0=RULE_CURLY_OPEN
		{
			newLeafNode(this_CURLY_OPEN_0, grammarAccess.getAttributeGroupAccess().getCURLY_OPENTerminalRuleCall_0());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeGroupAccess().getAttributesAttributeParserRuleCall_1_0());
				}
				lv_attributes_1_0=ruleAttribute
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
					}
					add(
						$current,
						"attributes",
						lv_attributes_1_0,
						"com.b2international.snowowl.snomed.scg.Scg.Attribute");
					afterParserOrEnumRuleCall();
				}
			)
		)
		(
			this_COMMA_2=RULE_COMMA
			{
				newLeafNode(this_COMMA_2, grammarAccess.getAttributeGroupAccess().getCOMMATerminalRuleCall_2_0());
			}
			(
				(
					{
						newCompositeNode(grammarAccess.getAttributeGroupAccess().getAttributesAttributeParserRuleCall_2_1_0());
					}
					lv_attributes_3_0=ruleAttribute
					{
						if ($current==null) {
							$current = createModelElementForParent(grammarAccess.getAttributeGroupRule());
						}
						add(
							$current,
							"attributes",
							lv_attributes_3_0,
							"com.b2international.snowowl.snomed.scg.Scg.Attribute");
						afterParserOrEnumRuleCall();
					}
				)
			)
		)*
		this_CURLY_CLOSE_4=RULE_CURLY_CLOSE
		{
			newLeafNode(this_CURLY_CLOSE_4, grammarAccess.getAttributeGroupAccess().getCURLY_CLOSETerminalRuleCall_3());
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
					newCompositeNode(grammarAccess.getAttributeAccess().getNameConceptReferenceParserRuleCall_0_0());
				}
				lv_name_0_0=ruleConceptReference
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeRule());
					}
					set(
						$current,
						"name",
						lv_name_0_0,
						"com.b2international.snowowl.snomed.scg.Scg.ConceptReference");
					afterParserOrEnumRuleCall();
				}
			)
		)
		this_EQUAL_1=RULE_EQUAL
		{
			newLeafNode(this_EQUAL_1, grammarAccess.getAttributeAccess().getEQUALTerminalRuleCall_1());
		}
		(
			(
				{
					newCompositeNode(grammarAccess.getAttributeAccess().getValueAttributeValueParserRuleCall_2_0());
				}
				lv_value_2_0=ruleAttributeValue
				{
					if ($current==null) {
						$current = createModelElementForParent(grammarAccess.getAttributeRule());
					}
					set(
						$current,
						"value",
						lv_value_2_0,
						"com.b2international.snowowl.snomed.scg.Scg.AttributeValue");
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
					"com.b2international.snowowl.snomed.scg.Scg.STRING");
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
						"com.b2international.snowowl.snomed.scg.Scg.Integer");
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
						"com.b2international.snowowl.snomed.scg.Scg.Decimal");
					afterParserOrEnumRuleCall();
				}
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
						"com.b2international.snowowl.snomed.scg.Scg.SnomedIdentifier");
					afterParserOrEnumRuleCall();
				}
			)
		)
		(
			(
				lv_term_1_0=RULE_TERM_STRING
				{
					newLeafNode(lv_term_1_0, grammarAccess.getConceptReferenceAccess().getTermTERM_STRINGTerminalRuleCall_1_0());
				}
				{
					if ($current==null) {
						$current = createModelElement(grammarAccess.getConceptReferenceRule());
					}
					setWithLastConsumed(
						$current,
						"term",
						lv_term_1_0,
						"com.b2international.snowowl.snomed.scg.Scg.TERM_STRING");
				}
			)
		)?
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
