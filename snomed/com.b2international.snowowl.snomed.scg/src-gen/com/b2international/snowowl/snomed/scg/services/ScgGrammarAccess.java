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
package com.b2international.snowowl.snomed.scg.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import org.eclipse.xtext.Action;
import org.eclipse.xtext.Alternatives;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.Group;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.service.AbstractElementFinder.AbstractGrammarElementFinder;
import org.eclipse.xtext.service.GrammarProvider;

@Singleton
public class ScgGrammarAccess extends AbstractGrammarElementFinder {
	
	public class ExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.Expression");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cExpressionAction_0 = (Action)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Alternatives cAlternatives_1_0 = (Alternatives)cGroup_1.eContents().get(0);
		private final Assignment cPrimitiveAssignment_1_0_0 = (Assignment)cAlternatives_1_0.eContents().get(0);
		private final RuleCall cPrimitiveSUBTYPE_OFTerminalRuleCall_1_0_0_0 = (RuleCall)cPrimitiveAssignment_1_0_0.eContents().get(0);
		private final RuleCall cEQUIVALENT_TOTerminalRuleCall_1_0_1 = (RuleCall)cAlternatives_1_0.eContents().get(1);
		private final Assignment cExpressionAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cExpressionSubExpressionParserRuleCall_1_1_0 = (RuleCall)cExpressionAssignment_1_1.eContents().get(0);
		
		//Expression:
		//	{Expression} ((primitive?=SUBTYPE_OF | EQUIVALENT_TO)? expression=SubExpression)?;
		@Override public ParserRule getRule() { return rule; }
		
		//{Expression} ((primitive?=SUBTYPE_OF | EQUIVALENT_TO)? expression=SubExpression)?
		public Group getGroup() { return cGroup; }
		
		//{Expression}
		public Action getExpressionAction_0() { return cExpressionAction_0; }
		
		//((primitive?=SUBTYPE_OF | EQUIVALENT_TO)? expression=SubExpression)?
		public Group getGroup_1() { return cGroup_1; }
		
		//(primitive?=SUBTYPE_OF | EQUIVALENT_TO)?
		public Alternatives getAlternatives_1_0() { return cAlternatives_1_0; }
		
		//primitive?=SUBTYPE_OF
		public Assignment getPrimitiveAssignment_1_0_0() { return cPrimitiveAssignment_1_0_0; }
		
		//SUBTYPE_OF
		public RuleCall getPrimitiveSUBTYPE_OFTerminalRuleCall_1_0_0_0() { return cPrimitiveSUBTYPE_OFTerminalRuleCall_1_0_0_0; }
		
		//EQUIVALENT_TO
		public RuleCall getEQUIVALENT_TOTerminalRuleCall_1_0_1() { return cEQUIVALENT_TOTerminalRuleCall_1_0_1; }
		
		//expression=SubExpression
		public Assignment getExpressionAssignment_1_1() { return cExpressionAssignment_1_1; }
		
		//SubExpression
		public RuleCall getExpressionSubExpressionParserRuleCall_1_1_0() { return cExpressionSubExpressionParserRuleCall_1_1_0; }
	}
	public class SubExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.SubExpression");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cFocusConceptsAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cFocusConceptsConceptReferenceParserRuleCall_0_0 = (RuleCall)cFocusConceptsAssignment_0.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cPLUSTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Assignment cFocusConceptsAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cFocusConceptsConceptReferenceParserRuleCall_1_1_0 = (RuleCall)cFocusConceptsAssignment_1_1.eContents().get(0);
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		private final RuleCall cCOLONTerminalRuleCall_2_0 = (RuleCall)cGroup_2.eContents().get(0);
		private final Assignment cRefinementAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		private final RuleCall cRefinementRefinementParserRuleCall_2_1_0 = (RuleCall)cRefinementAssignment_2_1.eContents().get(0);
		
		//SubExpression:
		//	focusConcepts+=ConceptReference (PLUS focusConcepts+=ConceptReference)* (COLON refinement=Refinement)?;
		@Override public ParserRule getRule() { return rule; }
		
		//focusConcepts+=ConceptReference (PLUS focusConcepts+=ConceptReference)* (COLON refinement=Refinement)?
		public Group getGroup() { return cGroup; }
		
		//focusConcepts+=ConceptReference
		public Assignment getFocusConceptsAssignment_0() { return cFocusConceptsAssignment_0; }
		
		//ConceptReference
		public RuleCall getFocusConceptsConceptReferenceParserRuleCall_0_0() { return cFocusConceptsConceptReferenceParserRuleCall_0_0; }
		
		//(PLUS focusConcepts+=ConceptReference)*
		public Group getGroup_1() { return cGroup_1; }
		
		//PLUS
		public RuleCall getPLUSTerminalRuleCall_1_0() { return cPLUSTerminalRuleCall_1_0; }
		
		//focusConcepts+=ConceptReference
		public Assignment getFocusConceptsAssignment_1_1() { return cFocusConceptsAssignment_1_1; }
		
		//ConceptReference
		public RuleCall getFocusConceptsConceptReferenceParserRuleCall_1_1_0() { return cFocusConceptsConceptReferenceParserRuleCall_1_1_0; }
		
		//(COLON refinement=Refinement)?
		public Group getGroup_2() { return cGroup_2; }
		
		//COLON
		public RuleCall getCOLONTerminalRuleCall_2_0() { return cCOLONTerminalRuleCall_2_0; }
		
		//refinement=Refinement
		public Assignment getRefinementAssignment_2_1() { return cRefinementAssignment_2_1; }
		
		//Refinement
		public RuleCall getRefinementRefinementParserRuleCall_2_1_0() { return cRefinementRefinementParserRuleCall_2_1_0; }
	}
	public class RefinementElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.Refinement");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Alternatives cAlternatives_0 = (Alternatives)cGroup.eContents().get(0);
		private final Group cGroup_0_0 = (Group)cAlternatives_0.eContents().get(0);
		private final Assignment cAttributesAssignment_0_0_0 = (Assignment)cGroup_0_0.eContents().get(0);
		private final RuleCall cAttributesAttributeParserRuleCall_0_0_0_0 = (RuleCall)cAttributesAssignment_0_0_0.eContents().get(0);
		private final Group cGroup_0_0_1 = (Group)cGroup_0_0.eContents().get(1);
		private final RuleCall cCOMMATerminalRuleCall_0_0_1_0 = (RuleCall)cGroup_0_0_1.eContents().get(0);
		private final Assignment cAttributesAssignment_0_0_1_1 = (Assignment)cGroup_0_0_1.eContents().get(1);
		private final RuleCall cAttributesAttributeParserRuleCall_0_0_1_1_0 = (RuleCall)cAttributesAssignment_0_0_1_1.eContents().get(0);
		private final Assignment cGroupsAssignment_0_1 = (Assignment)cAlternatives_0.eContents().get(1);
		private final RuleCall cGroupsAttributeGroupParserRuleCall_0_1_0 = (RuleCall)cGroupsAssignment_0_1.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cCOMMATerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Assignment cGroupsAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cGroupsAttributeGroupParserRuleCall_1_1_0 = (RuleCall)cGroupsAssignment_1_1.eContents().get(0);
		
		//Refinement:
		//	(attributes+=Attribute (COMMA attributes+=Attribute)* | groups+=AttributeGroup) (COMMA? groups+=AttributeGroup)*;
		@Override public ParserRule getRule() { return rule; }
		
		//(attributes+=Attribute (COMMA attributes+=Attribute)* | groups+=AttributeGroup) (COMMA? groups+=AttributeGroup)*
		public Group getGroup() { return cGroup; }
		
		//attributes+=Attribute (COMMA attributes+=Attribute)* | groups+=AttributeGroup
		public Alternatives getAlternatives_0() { return cAlternatives_0; }
		
		//attributes+=Attribute (COMMA attributes+=Attribute)*
		public Group getGroup_0_0() { return cGroup_0_0; }
		
		//attributes+=Attribute
		public Assignment getAttributesAssignment_0_0_0() { return cAttributesAssignment_0_0_0; }
		
		//Attribute
		public RuleCall getAttributesAttributeParserRuleCall_0_0_0_0() { return cAttributesAttributeParserRuleCall_0_0_0_0; }
		
		//(COMMA attributes+=Attribute)*
		public Group getGroup_0_0_1() { return cGroup_0_0_1; }
		
		//COMMA
		public RuleCall getCOMMATerminalRuleCall_0_0_1_0() { return cCOMMATerminalRuleCall_0_0_1_0; }
		
		//attributes+=Attribute
		public Assignment getAttributesAssignment_0_0_1_1() { return cAttributesAssignment_0_0_1_1; }
		
		//Attribute
		public RuleCall getAttributesAttributeParserRuleCall_0_0_1_1_0() { return cAttributesAttributeParserRuleCall_0_0_1_1_0; }
		
		//groups+=AttributeGroup
		public Assignment getGroupsAssignment_0_1() { return cGroupsAssignment_0_1; }
		
		//AttributeGroup
		public RuleCall getGroupsAttributeGroupParserRuleCall_0_1_0() { return cGroupsAttributeGroupParserRuleCall_0_1_0; }
		
		//(COMMA? groups+=AttributeGroup)*
		public Group getGroup_1() { return cGroup_1; }
		
		//COMMA?
		public RuleCall getCOMMATerminalRuleCall_1_0() { return cCOMMATerminalRuleCall_1_0; }
		
		//groups+=AttributeGroup
		public Assignment getGroupsAssignment_1_1() { return cGroupsAssignment_1_1; }
		
		//AttributeGroup
		public RuleCall getGroupsAttributeGroupParserRuleCall_1_1_0() { return cGroupsAttributeGroupParserRuleCall_1_1_0; }
	}
	public class AttributeGroupElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.AttributeGroup");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cCURLY_OPENTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cAttributesAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cAttributesAttributeParserRuleCall_1_0 = (RuleCall)cAttributesAssignment_1.eContents().get(0);
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		private final RuleCall cCOMMATerminalRuleCall_2_0 = (RuleCall)cGroup_2.eContents().get(0);
		private final Assignment cAttributesAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		private final RuleCall cAttributesAttributeParserRuleCall_2_1_0 = (RuleCall)cAttributesAssignment_2_1.eContents().get(0);
		private final RuleCall cCURLY_CLOSETerminalRuleCall_3 = (RuleCall)cGroup.eContents().get(3);
		
		//AttributeGroup:
		//	CURLY_OPEN attributes+=Attribute (COMMA attributes+=Attribute)* CURLY_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//CURLY_OPEN attributes+=Attribute (COMMA attributes+=Attribute)* CURLY_CLOSE
		public Group getGroup() { return cGroup; }
		
		//CURLY_OPEN
		public RuleCall getCURLY_OPENTerminalRuleCall_0() { return cCURLY_OPENTerminalRuleCall_0; }
		
		//attributes+=Attribute
		public Assignment getAttributesAssignment_1() { return cAttributesAssignment_1; }
		
		//Attribute
		public RuleCall getAttributesAttributeParserRuleCall_1_0() { return cAttributesAttributeParserRuleCall_1_0; }
		
		//(COMMA attributes+=Attribute)*
		public Group getGroup_2() { return cGroup_2; }
		
		//COMMA
		public RuleCall getCOMMATerminalRuleCall_2_0() { return cCOMMATerminalRuleCall_2_0; }
		
		//attributes+=Attribute
		public Assignment getAttributesAssignment_2_1() { return cAttributesAssignment_2_1; }
		
		//Attribute
		public RuleCall getAttributesAttributeParserRuleCall_2_1_0() { return cAttributesAttributeParserRuleCall_2_1_0; }
		
		//CURLY_CLOSE
		public RuleCall getCURLY_CLOSETerminalRuleCall_3() { return cCURLY_CLOSETerminalRuleCall_3; }
	}
	public class AttributeElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.Attribute");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cNameAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cNameConceptReferenceParserRuleCall_0_0 = (RuleCall)cNameAssignment_0.eContents().get(0);
		private final RuleCall cEQUAL_SIGNTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueAttributeValueParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//Attribute:
		//	name=ConceptReference EQUAL_SIGN value=AttributeValue;
		@Override public ParserRule getRule() { return rule; }
		
		//name=ConceptReference EQUAL_SIGN value=AttributeValue
		public Group getGroup() { return cGroup; }
		
		//name=ConceptReference
		public Assignment getNameAssignment_0() { return cNameAssignment_0; }
		
		//ConceptReference
		public RuleCall getNameConceptReferenceParserRuleCall_0_0() { return cNameConceptReferenceParserRuleCall_0_0; }
		
		//EQUAL_SIGN
		public RuleCall getEQUAL_SIGNTerminalRuleCall_1() { return cEQUAL_SIGNTerminalRuleCall_1; }
		
		//value=AttributeValue
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//AttributeValue
		public RuleCall getValueAttributeValueParserRuleCall_2_0() { return cValueAttributeValueParserRuleCall_2_0; }
	}
	public class AttributeValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.AttributeValue");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cConceptReferenceParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final RuleCall cOPENING_ROUND_BRACKETTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final RuleCall cSubExpressionParserRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final RuleCall cCLOSING_ROUND_BRACKETTerminalRuleCall_1_2 = (RuleCall)cGroup_1.eContents().get(2);
		private final RuleCall cStringValueParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cIntegerValueParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		private final RuleCall cDecimalValueParserRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		
		//AttributeValue:
		//	ConceptReference | OPENING_ROUND_BRACKET SubExpression CLOSING_ROUND_BRACKET | StringValue | IntegerValue |
		//	DecimalValue;
		@Override public ParserRule getRule() { return rule; }
		
		//ConceptReference | OPENING_ROUND_BRACKET SubExpression CLOSING_ROUND_BRACKET | StringValue | IntegerValue | DecimalValue
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//ConceptReference
		public RuleCall getConceptReferenceParserRuleCall_0() { return cConceptReferenceParserRuleCall_0; }
		
		//OPENING_ROUND_BRACKET SubExpression CLOSING_ROUND_BRACKET
		public Group getGroup_1() { return cGroup_1; }
		
		//OPENING_ROUND_BRACKET
		public RuleCall getOPENING_ROUND_BRACKETTerminalRuleCall_1_0() { return cOPENING_ROUND_BRACKETTerminalRuleCall_1_0; }
		
		//SubExpression
		public RuleCall getSubExpressionParserRuleCall_1_1() { return cSubExpressionParserRuleCall_1_1; }
		
		//CLOSING_ROUND_BRACKET
		public RuleCall getCLOSING_ROUND_BRACKETTerminalRuleCall_1_2() { return cCLOSING_ROUND_BRACKETTerminalRuleCall_1_2; }
		
		//StringValue
		public RuleCall getStringValueParserRuleCall_2() { return cStringValueParserRuleCall_2; }
		
		//IntegerValue
		public RuleCall getIntegerValueParserRuleCall_3() { return cIntegerValueParserRuleCall_3; }
		
		//DecimalValue
		public RuleCall getDecimalValueParserRuleCall_4() { return cDecimalValueParserRuleCall_4; }
	}
	public class StringValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.StringValue");
		private final Assignment cValueAssignment = (Assignment)rule.eContents().get(1);
		private final RuleCall cValueSTRINGTerminalRuleCall_0 = (RuleCall)cValueAssignment.eContents().get(0);
		
		//StringValue:
		//	value=STRING;
		@Override public ParserRule getRule() { return rule; }
		
		//value=STRING
		public Assignment getValueAssignment() { return cValueAssignment; }
		
		//STRING
		public RuleCall getValueSTRINGTerminalRuleCall_0() { return cValueSTRINGTerminalRuleCall_0; }
	}
	public class IntegerValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.IntegerValue");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cHASHTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cValueAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cValueIntegerParserRuleCall_1_0 = (RuleCall)cValueAssignment_1.eContents().get(0);
		
		//IntegerValue:
		//	HASH value=Integer;
		@Override public ParserRule getRule() { return rule; }
		
		//HASH value=Integer
		public Group getGroup() { return cGroup; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_0() { return cHASHTerminalRuleCall_0; }
		
		//value=Integer
		public Assignment getValueAssignment_1() { return cValueAssignment_1; }
		
		//Integer
		public RuleCall getValueIntegerParserRuleCall_1_0() { return cValueIntegerParserRuleCall_1_0; }
	}
	public class DecimalValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.DecimalValue");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cHASHTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cValueAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cValueDecimalParserRuleCall_1_0 = (RuleCall)cValueAssignment_1.eContents().get(0);
		
		//DecimalValue:
		//	HASH value=Decimal;
		@Override public ParserRule getRule() { return rule; }
		
		//HASH value=Decimal
		public Group getGroup() { return cGroup; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_0() { return cHASHTerminalRuleCall_0; }
		
		//value=Decimal
		public Assignment getValueAssignment_1() { return cValueAssignment_1; }
		
		//Decimal
		public RuleCall getValueDecimalParserRuleCall_1_0() { return cValueDecimalParserRuleCall_1_0; }
	}
	public class ConceptReferenceElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.ConceptReference");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cIdAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cIdSnomedIdentifierParserRuleCall_0_0 = (RuleCall)cIdAssignment_0.eContents().get(0);
		private final Assignment cTermAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cTermTERM_STRINGTerminalRuleCall_1_0 = (RuleCall)cTermAssignment_1.eContents().get(0);
		
		//ConceptReference:
		//	id=SnomedIdentifier term=TERM_STRING?;
		@Override public ParserRule getRule() { return rule; }
		
		//id=SnomedIdentifier term=TERM_STRING?
		public Group getGroup() { return cGroup; }
		
		//id=SnomedIdentifier
		public Assignment getIdAssignment_0() { return cIdAssignment_0; }
		
		//SnomedIdentifier
		public RuleCall getIdSnomedIdentifierParserRuleCall_0_0() { return cIdSnomedIdentifierParserRuleCall_0_0; }
		
		//term=TERM_STRING?
		public Assignment getTermAssignment_1() { return cTermAssignment_1; }
		
		//TERM_STRING
		public RuleCall getTermTERM_STRINGTerminalRuleCall_1_0() { return cTermTERM_STRINGTerminalRuleCall_1_0; }
	}
	public class SnomedIdentifierElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.SnomedIdentifier");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Alternatives cAlternatives_1 = (Alternatives)cGroup.eContents().get(1);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_1_0 = (RuleCall)cAlternatives_1.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_1_1 = (RuleCall)cAlternatives_1.eContents().get(1);
		private final Alternatives cAlternatives_2 = (Alternatives)cGroup.eContents().get(2);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_2_0 = (RuleCall)cAlternatives_2.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_2_1 = (RuleCall)cAlternatives_2.eContents().get(1);
		private final Alternatives cAlternatives_3 = (Alternatives)cGroup.eContents().get(3);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_3_0 = (RuleCall)cAlternatives_3.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_3_1 = (RuleCall)cAlternatives_3.eContents().get(1);
		private final Alternatives cAlternatives_4 = (Alternatives)cGroup.eContents().get(4);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_4_0 = (RuleCall)cAlternatives_4.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_4_1 = (RuleCall)cAlternatives_4.eContents().get(1);
		private final Alternatives cAlternatives_5 = (Alternatives)cGroup.eContents().get(5);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_5_0 = (RuleCall)cAlternatives_5.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_5_1 = (RuleCall)cAlternatives_5.eContents().get(1);
		
		//SnomedIdentifier hidden():
		//	DIGIT_NONZERO (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO)
		//	(DIGIT_NONZERO | ZERO)+;
		@Override public ParserRule getRule() { return rule; }
		
		//DIGIT_NONZERO (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO
		//| ZERO)+
		public Group getGroup() { return cGroup; }
		
		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_0() { return cDIGIT_NONZEROTerminalRuleCall_0; }
		
		//DIGIT_NONZERO | ZERO
		public Alternatives getAlternatives_1() { return cAlternatives_1; }
		
		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_1_0() { return cDIGIT_NONZEROTerminalRuleCall_1_0; }
		
		//ZERO
		public RuleCall getZEROTerminalRuleCall_1_1() { return cZEROTerminalRuleCall_1_1; }
		
		//DIGIT_NONZERO | ZERO
		public Alternatives getAlternatives_2() { return cAlternatives_2; }
		
		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_2_0() { return cDIGIT_NONZEROTerminalRuleCall_2_0; }
		
		//ZERO
		public RuleCall getZEROTerminalRuleCall_2_1() { return cZEROTerminalRuleCall_2_1; }
		
		//DIGIT_NONZERO | ZERO
		public Alternatives getAlternatives_3() { return cAlternatives_3; }
		
		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_3_0() { return cDIGIT_NONZEROTerminalRuleCall_3_0; }
		
		//ZERO
		public RuleCall getZEROTerminalRuleCall_3_1() { return cZEROTerminalRuleCall_3_1; }
		
		//DIGIT_NONZERO | ZERO
		public Alternatives getAlternatives_4() { return cAlternatives_4; }
		
		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_4_0() { return cDIGIT_NONZEROTerminalRuleCall_4_0; }
		
		//ZERO
		public RuleCall getZEROTerminalRuleCall_4_1() { return cZEROTerminalRuleCall_4_1; }
		
		//(DIGIT_NONZERO | ZERO)+
		public Alternatives getAlternatives_5() { return cAlternatives_5; }
		
		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_5_0() { return cDIGIT_NONZEROTerminalRuleCall_5_0; }
		
		//ZERO
		public RuleCall getZEROTerminalRuleCall_5_1() { return cZEROTerminalRuleCall_5_1; }
	}
	public class NonNegativeIntegerElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.NonNegativeInteger");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cZEROTerminalRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Alternatives cAlternatives_1_1 = (Alternatives)cGroup_1.eContents().get(1);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_1_1_0 = (RuleCall)cAlternatives_1_1.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_1_1_1 = (RuleCall)cAlternatives_1_1.eContents().get(1);
		
		//NonNegativeInteger ecore::EInt hidden():
		//	ZERO | DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*;
		@Override public ParserRule getRule() { return rule; }
		
		//ZERO | DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//ZERO
		public RuleCall getZEROTerminalRuleCall_0() { return cZEROTerminalRuleCall_0; }
		
		//DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*
		public Group getGroup_1() { return cGroup_1; }
		
		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_1_0() { return cDIGIT_NONZEROTerminalRuleCall_1_0; }
		
		//(DIGIT_NONZERO | ZERO)*
		public Alternatives getAlternatives_1_1() { return cAlternatives_1_1; }
		
		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_1_1_0() { return cDIGIT_NONZEROTerminalRuleCall_1_1_0; }
		
		//ZERO
		public RuleCall getZEROTerminalRuleCall_1_1_1() { return cZEROTerminalRuleCall_1_1_1; }
	}
	public class IntegerElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.Integer");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Alternatives cAlternatives_0 = (Alternatives)cGroup.eContents().get(0);
		private final RuleCall cPLUSTerminalRuleCall_0_0 = (RuleCall)cAlternatives_0.eContents().get(0);
		private final RuleCall cDASHTerminalRuleCall_0_1 = (RuleCall)cAlternatives_0.eContents().get(1);
		private final RuleCall cNonNegativeIntegerParserRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		
		//Integer ecore::EInt hidden():
		//	(PLUS | DASH)? NonNegativeInteger;
		@Override public ParserRule getRule() { return rule; }
		
		//(PLUS | DASH)? NonNegativeInteger
		public Group getGroup() { return cGroup; }
		
		//(PLUS | DASH)?
		public Alternatives getAlternatives_0() { return cAlternatives_0; }
		
		//PLUS
		public RuleCall getPLUSTerminalRuleCall_0_0() { return cPLUSTerminalRuleCall_0_0; }
		
		//DASH
		public RuleCall getDASHTerminalRuleCall_0_1() { return cDASHTerminalRuleCall_0_1; }
		
		//NonNegativeInteger
		public RuleCall getNonNegativeIntegerParserRuleCall_1() { return cNonNegativeIntegerParserRuleCall_1; }
	}
	public class DecimalElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.Decimal");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Alternatives cAlternatives_0 = (Alternatives)cGroup.eContents().get(0);
		private final RuleCall cPLUSTerminalRuleCall_0_0 = (RuleCall)cAlternatives_0.eContents().get(0);
		private final RuleCall cDASHTerminalRuleCall_0_1 = (RuleCall)cAlternatives_0.eContents().get(1);
		private final RuleCall cNonNegativeDecimalParserRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		
		//Decimal ecore::EBigDecimal hidden():
		//	(PLUS | DASH)? NonNegativeDecimal;
		@Override public ParserRule getRule() { return rule; }
		
		//(PLUS | DASH)? NonNegativeDecimal
		public Group getGroup() { return cGroup; }
		
		//(PLUS | DASH)?
		public Alternatives getAlternatives_0() { return cAlternatives_0; }
		
		//PLUS
		public RuleCall getPLUSTerminalRuleCall_0_0() { return cPLUSTerminalRuleCall_0_0; }
		
		//DASH
		public RuleCall getDASHTerminalRuleCall_0_1() { return cDASHTerminalRuleCall_0_1; }
		
		//NonNegativeDecimal
		public RuleCall getNonNegativeDecimalParserRuleCall_1() { return cNonNegativeDecimalParserRuleCall_1; }
	}
	public class NonNegativeDecimalElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.NonNegativeDecimal");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cNonNegativeIntegerParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cDOTTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Alternatives cAlternatives_2 = (Alternatives)cGroup.eContents().get(2);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_2_0 = (RuleCall)cAlternatives_2.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_2_1 = (RuleCall)cAlternatives_2.eContents().get(1);
		
		//NonNegativeDecimal ecore::EBigDecimal hidden():
		//	NonNegativeInteger DOT (DIGIT_NONZERO | ZERO)*;
		@Override public ParserRule getRule() { return rule; }
		
		//NonNegativeInteger DOT (DIGIT_NONZERO | ZERO)*
		public Group getGroup() { return cGroup; }
		
		//NonNegativeInteger
		public RuleCall getNonNegativeIntegerParserRuleCall_0() { return cNonNegativeIntegerParserRuleCall_0; }
		
		//DOT
		public RuleCall getDOTTerminalRuleCall_1() { return cDOTTerminalRuleCall_1; }
		
		//(DIGIT_NONZERO | ZERO)*
		public Alternatives getAlternatives_2() { return cAlternatives_2; }
		
		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_2_0() { return cDIGIT_NONZEROTerminalRuleCall_2_0; }
		
		//ZERO
		public RuleCall getZEROTerminalRuleCall_2_1() { return cZEROTerminalRuleCall_2_1; }
	}
	
	
	private final ExpressionElements pExpression;
	private final SubExpressionElements pSubExpression;
	private final RefinementElements pRefinement;
	private final AttributeGroupElements pAttributeGroup;
	private final AttributeElements pAttribute;
	private final AttributeValueElements pAttributeValue;
	private final StringValueElements pStringValue;
	private final IntegerValueElements pIntegerValue;
	private final DecimalValueElements pDecimalValue;
	private final ConceptReferenceElements pConceptReference;
	private final TerminalRule tEQUIVALENT_TO;
	private final TerminalRule tSUBTYPE_OF;
	private final TerminalRule tTERM_STRING;
	private final TerminalRule tZERO;
	private final TerminalRule tDIGIT_NONZERO;
	private final TerminalRule tCURLY_OPEN;
	private final TerminalRule tCURLY_CLOSE;
	private final TerminalRule tCOMMA;
	private final TerminalRule tEQUAL_SIGN;
	private final TerminalRule tCOLON;
	private final TerminalRule tPLUS;
	private final TerminalRule tDASH;
	private final TerminalRule tDOT;
	private final TerminalRule tQUOTATION_MARK;
	private final TerminalRule tOPENING_ROUND_BRACKET;
	private final TerminalRule tCLOSING_ROUND_BRACKET;
	private final TerminalRule tHASH;
	private final TerminalRule tWS;
	private final TerminalRule tML_COMMENT;
	private final TerminalRule tSL_COMMENT;
	private final TerminalRule tSTRING;
	private final SnomedIdentifierElements pSnomedIdentifier;
	private final NonNegativeIntegerElements pNonNegativeInteger;
	private final IntegerElements pInteger;
	private final DecimalElements pDecimal;
	private final NonNegativeDecimalElements pNonNegativeDecimal;
	
	private final Grammar grammar;

	@Inject
	public ScgGrammarAccess(GrammarProvider grammarProvider) {
		this.grammar = internalFindGrammar(grammarProvider);
		this.pExpression = new ExpressionElements();
		this.pSubExpression = new SubExpressionElements();
		this.pRefinement = new RefinementElements();
		this.pAttributeGroup = new AttributeGroupElements();
		this.pAttribute = new AttributeElements();
		this.pAttributeValue = new AttributeValueElements();
		this.pStringValue = new StringValueElements();
		this.pIntegerValue = new IntegerValueElements();
		this.pDecimalValue = new DecimalValueElements();
		this.pConceptReference = new ConceptReferenceElements();
		this.tEQUIVALENT_TO = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.EQUIVALENT_TO");
		this.tSUBTYPE_OF = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.SUBTYPE_OF");
		this.tTERM_STRING = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.TERM_STRING");
		this.tZERO = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.ZERO");
		this.tDIGIT_NONZERO = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.DIGIT_NONZERO");
		this.tCURLY_OPEN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.CURLY_OPEN");
		this.tCURLY_CLOSE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.CURLY_CLOSE");
		this.tCOMMA = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.COMMA");
		this.tEQUAL_SIGN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.EQUAL_SIGN");
		this.tCOLON = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.COLON");
		this.tPLUS = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.PLUS");
		this.tDASH = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.DASH");
		this.tDOT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.DOT");
		this.tQUOTATION_MARK = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.QUOTATION_MARK");
		this.tOPENING_ROUND_BRACKET = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.OPENING_ROUND_BRACKET");
		this.tCLOSING_ROUND_BRACKET = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.CLOSING_ROUND_BRACKET");
		this.tHASH = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.HASH");
		this.tWS = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.WS");
		this.tML_COMMENT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.ML_COMMENT");
		this.tSL_COMMENT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.SL_COMMENT");
		this.tSTRING = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.scg.Scg.STRING");
		this.pSnomedIdentifier = new SnomedIdentifierElements();
		this.pNonNegativeInteger = new NonNegativeIntegerElements();
		this.pInteger = new IntegerElements();
		this.pDecimal = new DecimalElements();
		this.pNonNegativeDecimal = new NonNegativeDecimalElements();
	}
	
	protected Grammar internalFindGrammar(GrammarProvider grammarProvider) {
		Grammar grammar = grammarProvider.getGrammar(this);
		while (grammar != null) {
			if ("com.b2international.snowowl.snomed.scg.Scg".equals(grammar.getName())) {
				return grammar;
			}
			List<Grammar> grammars = grammar.getUsedGrammars();
			if (!grammars.isEmpty()) {
				grammar = grammars.iterator().next();
			} else {
				return null;
			}
		}
		return grammar;
	}
	
	@Override
	public Grammar getGrammar() {
		return grammar;
	}
	

	
	//Expression:
	//	{Expression} ((primitive?=SUBTYPE_OF | EQUIVALENT_TO)? expression=SubExpression)?;
	public ExpressionElements getExpressionAccess() {
		return pExpression;
	}
	
	public ParserRule getExpressionRule() {
		return getExpressionAccess().getRule();
	}
	
	//SubExpression:
	//	focusConcepts+=ConceptReference (PLUS focusConcepts+=ConceptReference)* (COLON refinement=Refinement)?;
	public SubExpressionElements getSubExpressionAccess() {
		return pSubExpression;
	}
	
	public ParserRule getSubExpressionRule() {
		return getSubExpressionAccess().getRule();
	}
	
	//Refinement:
	//	(attributes+=Attribute (COMMA attributes+=Attribute)* | groups+=AttributeGroup) (COMMA? groups+=AttributeGroup)*;
	public RefinementElements getRefinementAccess() {
		return pRefinement;
	}
	
	public ParserRule getRefinementRule() {
		return getRefinementAccess().getRule();
	}
	
	//AttributeGroup:
	//	CURLY_OPEN attributes+=Attribute (COMMA attributes+=Attribute)* CURLY_CLOSE;
	public AttributeGroupElements getAttributeGroupAccess() {
		return pAttributeGroup;
	}
	
	public ParserRule getAttributeGroupRule() {
		return getAttributeGroupAccess().getRule();
	}
	
	//Attribute:
	//	name=ConceptReference EQUAL_SIGN value=AttributeValue;
	public AttributeElements getAttributeAccess() {
		return pAttribute;
	}
	
	public ParserRule getAttributeRule() {
		return getAttributeAccess().getRule();
	}
	
	//AttributeValue:
	//	ConceptReference | OPENING_ROUND_BRACKET SubExpression CLOSING_ROUND_BRACKET | StringValue | IntegerValue |
	//	DecimalValue;
	public AttributeValueElements getAttributeValueAccess() {
		return pAttributeValue;
	}
	
	public ParserRule getAttributeValueRule() {
		return getAttributeValueAccess().getRule();
	}
	
	//StringValue:
	//	value=STRING;
	public StringValueElements getStringValueAccess() {
		return pStringValue;
	}
	
	public ParserRule getStringValueRule() {
		return getStringValueAccess().getRule();
	}
	
	//IntegerValue:
	//	HASH value=Integer;
	public IntegerValueElements getIntegerValueAccess() {
		return pIntegerValue;
	}
	
	public ParserRule getIntegerValueRule() {
		return getIntegerValueAccess().getRule();
	}
	
	//DecimalValue:
	//	HASH value=Decimal;
	public DecimalValueElements getDecimalValueAccess() {
		return pDecimalValue;
	}
	
	public ParserRule getDecimalValueRule() {
		return getDecimalValueAccess().getRule();
	}
	
	//ConceptReference:
	//	id=SnomedIdentifier term=TERM_STRING?;
	public ConceptReferenceElements getConceptReferenceAccess() {
		return pConceptReference;
	}
	
	public ParserRule getConceptReferenceRule() {
		return getConceptReferenceAccess().getRule();
	}
	
	//terminal EQUIVALENT_TO:
	//	'===';
	public TerminalRule getEQUIVALENT_TORule() {
		return tEQUIVALENT_TO;
	}
	
	//terminal SUBTYPE_OF:
	//	'<<<';
	public TerminalRule getSUBTYPE_OFRule() {
		return tSUBTYPE_OF;
	}
	
	//terminal TERM_STRING:
	//	"|" !"|"* "|";
	public TerminalRule getTERM_STRINGRule() {
		return tTERM_STRING;
	}
	
	//terminal ZERO:
	//	'0';
	public TerminalRule getZERORule() {
		return tZERO;
	}
	
	//terminal DIGIT_NONZERO:
	//	'1'..'9';
	public TerminalRule getDIGIT_NONZERORule() {
		return tDIGIT_NONZERO;
	}
	
	//terminal CURLY_OPEN:
	//	'{';
	public TerminalRule getCURLY_OPENRule() {
		return tCURLY_OPEN;
	}
	
	//terminal CURLY_CLOSE:
	//	'}';
	public TerminalRule getCURLY_CLOSERule() {
		return tCURLY_CLOSE;
	}
	
	//terminal COMMA:
	//	',';
	public TerminalRule getCOMMARule() {
		return tCOMMA;
	}
	
	//terminal EQUAL_SIGN:
	//	'=';
	public TerminalRule getEQUAL_SIGNRule() {
		return tEQUAL_SIGN;
	}
	
	//terminal COLON:
	//	':';
	public TerminalRule getCOLONRule() {
		return tCOLON;
	}
	
	//terminal PLUS:
	//	'+';
	public TerminalRule getPLUSRule() {
		return tPLUS;
	}
	
	//terminal DASH:
	//	'-';
	public TerminalRule getDASHRule() {
		return tDASH;
	}
	
	//terminal DOT:
	//	'.';
	public TerminalRule getDOTRule() {
		return tDOT;
	}
	
	//terminal QUOTATION_MARK:
	//	'"';
	public TerminalRule getQUOTATION_MARKRule() {
		return tQUOTATION_MARK;
	}
	
	//terminal OPENING_ROUND_BRACKET:
	//	'(';
	public TerminalRule getOPENING_ROUND_BRACKETRule() {
		return tOPENING_ROUND_BRACKET;
	}
	
	//terminal CLOSING_ROUND_BRACKET:
	//	')';
	public TerminalRule getCLOSING_ROUND_BRACKETRule() {
		return tCLOSING_ROUND_BRACKET;
	}
	
	//terminal HASH:
	//	'#';
	public TerminalRule getHASHRule() {
		return tHASH;
	}
	
	//terminal WS:
	//	' ' | '\t' | '\n' | '\r';
	public TerminalRule getWSRule() {
		return tWS;
	}
	
	//terminal ML_COMMENT:
	//	'/*'->'*/';
	public TerminalRule getML_COMMENTRule() {
		return tML_COMMENT;
	}
	
	//terminal SL_COMMENT:
	//	'//' !('\n' | '\r')* ('\r'? '\n')?;
	public TerminalRule getSL_COMMENTRule() {
		return tSL_COMMENT;
	}
	
	//terminal STRING:
	//	'"' ('\\' . | !('\\' | '"'))* '"' |
	//	"'" ('\\' . | !('\\' | "'"))* "'";
	public TerminalRule getSTRINGRule() {
		return tSTRING;
	}
	
	//SnomedIdentifier hidden():
	//	DIGIT_NONZERO (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO)
	//	(DIGIT_NONZERO | ZERO)+;
	public SnomedIdentifierElements getSnomedIdentifierAccess() {
		return pSnomedIdentifier;
	}
	
	public ParserRule getSnomedIdentifierRule() {
		return getSnomedIdentifierAccess().getRule();
	}
	
	//NonNegativeInteger ecore::EInt hidden():
	//	ZERO | DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*;
	public NonNegativeIntegerElements getNonNegativeIntegerAccess() {
		return pNonNegativeInteger;
	}
	
	public ParserRule getNonNegativeIntegerRule() {
		return getNonNegativeIntegerAccess().getRule();
	}
	
	//Integer ecore::EInt hidden():
	//	(PLUS | DASH)? NonNegativeInteger;
	public IntegerElements getIntegerAccess() {
		return pInteger;
	}
	
	public ParserRule getIntegerRule() {
		return getIntegerAccess().getRule();
	}
	
	//Decimal ecore::EBigDecimal hidden():
	//	(PLUS | DASH)? NonNegativeDecimal;
	public DecimalElements getDecimalAccess() {
		return pDecimal;
	}
	
	public ParserRule getDecimalRule() {
		return getDecimalAccess().getRule();
	}
	
	//NonNegativeDecimal ecore::EBigDecimal hidden():
	//	NonNegativeInteger DOT (DIGIT_NONZERO | ZERO)*;
	public NonNegativeDecimalElements getNonNegativeDecimalAccess() {
		return pNonNegativeDecimal;
	}
	
	public ParserRule getNonNegativeDecimalRule() {
		return getNonNegativeDecimalAccess().getRule();
	}
}
