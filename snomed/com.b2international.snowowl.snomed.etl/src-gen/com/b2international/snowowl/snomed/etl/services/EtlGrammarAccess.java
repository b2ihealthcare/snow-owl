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
package com.b2international.snowowl.snomed.etl.services;

import com.b2international.snowowl.snomed.ecl.services.EclGrammarAccess;
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
public class EtlGrammarAccess extends AbstractGrammarElementFinder {
	
	public class ExpressionTemplateElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.ExpressionTemplate");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cExpressionTemplateAction_0 = (Action)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Alternatives cAlternatives_1_0 = (Alternatives)cGroup_1.eContents().get(0);
		private final Alternatives cAlternatives_1_0_0 = (Alternatives)cAlternatives_1_0.eContents().get(0);
		private final Assignment cPrimitiveAssignment_1_0_0_0 = (Assignment)cAlternatives_1_0_0.eContents().get(0);
		private final RuleCall cPrimitiveSUBTYPE_OFTerminalRuleCall_1_0_0_0_0 = (RuleCall)cPrimitiveAssignment_1_0_0_0.eContents().get(0);
		private final RuleCall cEQUIVALENT_TOTerminalRuleCall_1_0_0_1 = (RuleCall)cAlternatives_1_0_0.eContents().get(1);
		private final Assignment cSlotAssignment_1_0_1 = (Assignment)cAlternatives_1_0.eContents().get(1);
		private final RuleCall cSlotTokenReplacementSlotParserRuleCall_1_0_1_0 = (RuleCall)cSlotAssignment_1_0_1.eContents().get(0);
		private final Assignment cExpressionAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cExpressionSubExpressionParserRuleCall_1_1_0 = (RuleCall)cExpressionAssignment_1_1.eContents().get(0);
		
		//ExpressionTemplate:
		//	{ExpressionTemplate} (((primitive?=SUBTYPE_OF | EQUIVALENT_TO) | slot=TokenReplacementSlot)?
		//	expression=SubExpression)?;
		@Override public ParserRule getRule() { return rule; }
		
		//{ExpressionTemplate} (((primitive?=SUBTYPE_OF | EQUIVALENT_TO) | slot=TokenReplacementSlot)? expression=SubExpression)?
		public Group getGroup() { return cGroup; }
		
		//{ExpressionTemplate}
		public Action getExpressionTemplateAction_0() { return cExpressionTemplateAction_0; }
		
		//(((primitive?=SUBTYPE_OF | EQUIVALENT_TO) | slot=TokenReplacementSlot)? expression=SubExpression)?
		public Group getGroup_1() { return cGroup_1; }
		
		//((primitive?=SUBTYPE_OF | EQUIVALENT_TO) | slot=TokenReplacementSlot)?
		public Alternatives getAlternatives_1_0() { return cAlternatives_1_0; }
		
		//(primitive?=SUBTYPE_OF | EQUIVALENT_TO)
		public Alternatives getAlternatives_1_0_0() { return cAlternatives_1_0_0; }
		
		//primitive?=SUBTYPE_OF
		public Assignment getPrimitiveAssignment_1_0_0_0() { return cPrimitiveAssignment_1_0_0_0; }
		
		//SUBTYPE_OF
		public RuleCall getPrimitiveSUBTYPE_OFTerminalRuleCall_1_0_0_0_0() { return cPrimitiveSUBTYPE_OFTerminalRuleCall_1_0_0_0_0; }
		
		//EQUIVALENT_TO
		public RuleCall getEQUIVALENT_TOTerminalRuleCall_1_0_0_1() { return cEQUIVALENT_TOTerminalRuleCall_1_0_0_1; }
		
		//slot=TokenReplacementSlot
		public Assignment getSlotAssignment_1_0_1() { return cSlotAssignment_1_0_1; }
		
		//TokenReplacementSlot
		public RuleCall getSlotTokenReplacementSlotParserRuleCall_1_0_1_0() { return cSlotTokenReplacementSlotParserRuleCall_1_0_1_0; }
		
		//expression=SubExpression
		public Assignment getExpressionAssignment_1_1() { return cExpressionAssignment_1_1; }
		
		//SubExpression
		public RuleCall getExpressionSubExpressionParserRuleCall_1_1_0() { return cExpressionSubExpressionParserRuleCall_1_1_0; }
	}
	public class SubExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SubExpression");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cFocusConceptsAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cFocusConceptsFocusConceptParserRuleCall_0_0 = (RuleCall)cFocusConceptsAssignment_0.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cPLUSTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Assignment cFocusConceptsAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cFocusConceptsFocusConceptParserRuleCall_1_1_0 = (RuleCall)cFocusConceptsAssignment_1_1.eContents().get(0);
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		private final RuleCall cCOLONTerminalRuleCall_2_0 = (RuleCall)cGroup_2.eContents().get(0);
		private final Assignment cRefinementAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		private final RuleCall cRefinementRefinementParserRuleCall_2_1_0 = (RuleCall)cRefinementAssignment_2_1.eContents().get(0);
		
		//SubExpression:
		//	focusConcepts+=FocusConcept (PLUS focusConcepts+=FocusConcept)* (COLON refinement=Refinement)?;
		@Override public ParserRule getRule() { return rule; }
		
		//focusConcepts+=FocusConcept (PLUS focusConcepts+=FocusConcept)* (COLON refinement=Refinement)?
		public Group getGroup() { return cGroup; }
		
		//focusConcepts+=FocusConcept
		public Assignment getFocusConceptsAssignment_0() { return cFocusConceptsAssignment_0; }
		
		//FocusConcept
		public RuleCall getFocusConceptsFocusConceptParserRuleCall_0_0() { return cFocusConceptsFocusConceptParserRuleCall_0_0; }
		
		//(PLUS focusConcepts+=FocusConcept)*
		public Group getGroup_1() { return cGroup_1; }
		
		//PLUS
		public RuleCall getPLUSTerminalRuleCall_1_0() { return cPLUSTerminalRuleCall_1_0; }
		
		//focusConcepts+=FocusConcept
		public Assignment getFocusConceptsAssignment_1_1() { return cFocusConceptsAssignment_1_1; }
		
		//FocusConcept
		public RuleCall getFocusConceptsFocusConceptParserRuleCall_1_1_0() { return cFocusConceptsFocusConceptParserRuleCall_1_1_0; }
		
		//(COLON refinement=Refinement)?
		public Group getGroup_2() { return cGroup_2; }
		
		//COLON
		public RuleCall getCOLONTerminalRuleCall_2_0() { return cCOLONTerminalRuleCall_2_0; }
		
		//refinement=Refinement
		public Assignment getRefinementAssignment_2_1() { return cRefinementAssignment_2_1; }
		
		//Refinement
		public RuleCall getRefinementRefinementParserRuleCall_2_1_0() { return cRefinementRefinementParserRuleCall_2_1_0; }
	}
	public class FocusConceptElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.FocusConcept");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cSlotAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cSlotTemplateInformationSlotParserRuleCall_0_0 = (RuleCall)cSlotAssignment_0.eContents().get(0);
		private final Assignment cConceptAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConceptConceptReferenceParserRuleCall_1_0 = (RuleCall)cConceptAssignment_1.eContents().get(0);
		
		//FocusConcept:
		//	slot=TemplateInformationSlot? concept=ConceptReference;
		@Override public ParserRule getRule() { return rule; }
		
		//slot=TemplateInformationSlot? concept=ConceptReference
		public Group getGroup() { return cGroup; }
		
		//slot=TemplateInformationSlot?
		public Assignment getSlotAssignment_0() { return cSlotAssignment_0; }
		
		//TemplateInformationSlot
		public RuleCall getSlotTemplateInformationSlotParserRuleCall_0_0() { return cSlotTemplateInformationSlotParserRuleCall_0_0; }
		
		//concept=ConceptReference
		public Assignment getConceptAssignment_1() { return cConceptAssignment_1; }
		
		//ConceptReference
		public RuleCall getConceptConceptReferenceParserRuleCall_1_0() { return cConceptConceptReferenceParserRuleCall_1_0; }
	}
	public class RefinementElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.Refinement");
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
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.AttributeGroup");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cSlotAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cSlotTemplateInformationSlotParserRuleCall_0_0 = (RuleCall)cSlotAssignment_0.eContents().get(0);
		private final RuleCall cCURLY_OPENTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cAttributesAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cAttributesAttributeParserRuleCall_2_0 = (RuleCall)cAttributesAssignment_2.eContents().get(0);
		private final Group cGroup_3 = (Group)cGroup.eContents().get(3);
		private final RuleCall cCOMMATerminalRuleCall_3_0 = (RuleCall)cGroup_3.eContents().get(0);
		private final Assignment cAttributesAssignment_3_1 = (Assignment)cGroup_3.eContents().get(1);
		private final RuleCall cAttributesAttributeParserRuleCall_3_1_0 = (RuleCall)cAttributesAssignment_3_1.eContents().get(0);
		private final RuleCall cCURLY_CLOSETerminalRuleCall_4 = (RuleCall)cGroup.eContents().get(4);
		
		//AttributeGroup:
		//	slot=TemplateInformationSlot? CURLY_OPEN attributes+=Attribute (COMMA attributes+=Attribute)* CURLY_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//slot=TemplateInformationSlot? CURLY_OPEN attributes+=Attribute (COMMA attributes+=Attribute)* CURLY_CLOSE
		public Group getGroup() { return cGroup; }
		
		//slot=TemplateInformationSlot?
		public Assignment getSlotAssignment_0() { return cSlotAssignment_0; }
		
		//TemplateInformationSlot
		public RuleCall getSlotTemplateInformationSlotParserRuleCall_0_0() { return cSlotTemplateInformationSlotParserRuleCall_0_0; }
		
		//CURLY_OPEN
		public RuleCall getCURLY_OPENTerminalRuleCall_1() { return cCURLY_OPENTerminalRuleCall_1; }
		
		//attributes+=Attribute
		public Assignment getAttributesAssignment_2() { return cAttributesAssignment_2; }
		
		//Attribute
		public RuleCall getAttributesAttributeParserRuleCall_2_0() { return cAttributesAttributeParserRuleCall_2_0; }
		
		//(COMMA attributes+=Attribute)*
		public Group getGroup_3() { return cGroup_3; }
		
		//COMMA
		public RuleCall getCOMMATerminalRuleCall_3_0() { return cCOMMATerminalRuleCall_3_0; }
		
		//attributes+=Attribute
		public Assignment getAttributesAssignment_3_1() { return cAttributesAssignment_3_1; }
		
		//Attribute
		public RuleCall getAttributesAttributeParserRuleCall_3_1_0() { return cAttributesAttributeParserRuleCall_3_1_0; }
		
		//CURLY_CLOSE
		public RuleCall getCURLY_CLOSETerminalRuleCall_4() { return cCURLY_CLOSETerminalRuleCall_4; }
	}
	public class AttributeElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.Attribute");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cSlotAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cSlotTemplateInformationSlotParserRuleCall_0_0 = (RuleCall)cSlotAssignment_0.eContents().get(0);
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNameConceptReferenceParserRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		private final RuleCall cEQUALTerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		private final Assignment cValueAssignment_3 = (Assignment)cGroup.eContents().get(3);
		private final RuleCall cValueAttributeValueParserRuleCall_3_0 = (RuleCall)cValueAssignment_3.eContents().get(0);
		
		//Attribute:
		//	slot=TemplateInformationSlot? name=ConceptReference EQUAL value=AttributeValue;
		@Override public ParserRule getRule() { return rule; }
		
		//slot=TemplateInformationSlot? name=ConceptReference EQUAL value=AttributeValue
		public Group getGroup() { return cGroup; }
		
		//slot=TemplateInformationSlot?
		public Assignment getSlotAssignment_0() { return cSlotAssignment_0; }
		
		//TemplateInformationSlot
		public RuleCall getSlotTemplateInformationSlotParserRuleCall_0_0() { return cSlotTemplateInformationSlotParserRuleCall_0_0; }
		
		//name=ConceptReference
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		//ConceptReference
		public RuleCall getNameConceptReferenceParserRuleCall_1_0() { return cNameConceptReferenceParserRuleCall_1_0; }
		
		//EQUAL
		public RuleCall getEQUALTerminalRuleCall_2() { return cEQUALTerminalRuleCall_2; }
		
		//value=AttributeValue
		public Assignment getValueAssignment_3() { return cValueAssignment_3; }
		
		//AttributeValue
		public RuleCall getValueAttributeValueParserRuleCall_3_0() { return cValueAttributeValueParserRuleCall_3_0; }
	}
	public class AttributeValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.AttributeValue");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cConceptReferenceParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final RuleCall cROUND_OPENTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final RuleCall cSubExpressionParserRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final RuleCall cROUND_CLOSETerminalRuleCall_1_2 = (RuleCall)cGroup_1.eContents().get(2);
		private final RuleCall cStringValueParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cIntegerValueParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		private final RuleCall cDecimalValueParserRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		private final RuleCall cConcreteValueReplacementSlotParserRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		
		//AttributeValue:
		//	ConceptReference | ROUND_OPEN SubExpression ROUND_CLOSE | StringValue | IntegerValue | DecimalValue |
		//	ConcreteValueReplacementSlot;
		@Override public ParserRule getRule() { return rule; }
		
		//ConceptReference | ROUND_OPEN SubExpression ROUND_CLOSE | StringValue | IntegerValue | DecimalValue |
		//ConcreteValueReplacementSlot
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//ConceptReference
		public RuleCall getConceptReferenceParserRuleCall_0() { return cConceptReferenceParserRuleCall_0; }
		
		//ROUND_OPEN SubExpression ROUND_CLOSE
		public Group getGroup_1() { return cGroup_1; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_1_0() { return cROUND_OPENTerminalRuleCall_1_0; }
		
		//SubExpression
		public RuleCall getSubExpressionParserRuleCall_1_1() { return cSubExpressionParserRuleCall_1_1; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_1_2() { return cROUND_CLOSETerminalRuleCall_1_2; }
		
		//StringValue
		public RuleCall getStringValueParserRuleCall_2() { return cStringValueParserRuleCall_2; }
		
		//IntegerValue
		public RuleCall getIntegerValueParserRuleCall_3() { return cIntegerValueParserRuleCall_3; }
		
		//DecimalValue
		public RuleCall getDecimalValueParserRuleCall_4() { return cDecimalValueParserRuleCall_4; }
		
		//ConcreteValueReplacementSlot
		public RuleCall getConcreteValueReplacementSlotParserRuleCall_5() { return cConcreteValueReplacementSlotParserRuleCall_5; }
	}
	public class ConceptIdReplacementSlotElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.ConceptIdReplacementSlot");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cConceptIdReplacementSlotAction_0 = (Action)cGroup.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_OPENTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final RuleCall cPLUSTerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		private final RuleCall cIDTerminalRuleCall_3 = (RuleCall)cGroup.eContents().get(3);
		private final Group cGroup_4 = (Group)cGroup.eContents().get(4);
		private final RuleCall cROUND_OPENTerminalRuleCall_4_0 = (RuleCall)cGroup_4.eContents().get(0);
		private final Assignment cConstraintAssignment_4_1 = (Assignment)cGroup_4.eContents().get(1);
		private final RuleCall cConstraintExpressionConstraintParserRuleCall_4_1_0 = (RuleCall)cConstraintAssignment_4_1.eContents().get(0);
		private final RuleCall cROUND_CLOSETerminalRuleCall_4_2 = (RuleCall)cGroup_4.eContents().get(2);
		private final Assignment cNameAssignment_5 = (Assignment)cGroup.eContents().get(5);
		private final RuleCall cNameSLOTNAME_STRINGTerminalRuleCall_5_0 = (RuleCall)cNameAssignment_5.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_CLOSETerminalRuleCall_6 = (RuleCall)cGroup.eContents().get(6);
		
		//ConceptIdReplacementSlot:
		//	{ConceptIdReplacementSlot} DOUBLE_SQUARE_OPEN PLUS ID (ROUND_OPEN constraint=ExpressionConstraint ROUND_CLOSE)?
		//	name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//{ConceptIdReplacementSlot} DOUBLE_SQUARE_OPEN PLUS ID (ROUND_OPEN constraint=ExpressionConstraint ROUND_CLOSE)?
		//name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE
		public Group getGroup() { return cGroup; }
		
		//{ConceptIdReplacementSlot}
		public Action getConceptIdReplacementSlotAction_0() { return cConceptIdReplacementSlotAction_0; }
		
		//DOUBLE_SQUARE_OPEN
		public RuleCall getDOUBLE_SQUARE_OPENTerminalRuleCall_1() { return cDOUBLE_SQUARE_OPENTerminalRuleCall_1; }
		
		//PLUS
		public RuleCall getPLUSTerminalRuleCall_2() { return cPLUSTerminalRuleCall_2; }
		
		//ID
		public RuleCall getIDTerminalRuleCall_3() { return cIDTerminalRuleCall_3; }
		
		//(ROUND_OPEN constraint=ExpressionConstraint ROUND_CLOSE)?
		public Group getGroup_4() { return cGroup_4; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_4_0() { return cROUND_OPENTerminalRuleCall_4_0; }
		
		//constraint=ExpressionConstraint
		public Assignment getConstraintAssignment_4_1() { return cConstraintAssignment_4_1; }
		
		//ExpressionConstraint
		public RuleCall getConstraintExpressionConstraintParserRuleCall_4_1_0() { return cConstraintExpressionConstraintParserRuleCall_4_1_0; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_4_2() { return cROUND_CLOSETerminalRuleCall_4_2; }
		
		//name=SLOTNAME_STRING?
		public Assignment getNameAssignment_5() { return cNameAssignment_5; }
		
		//SLOTNAME_STRING
		public RuleCall getNameSLOTNAME_STRINGTerminalRuleCall_5_0() { return cNameSLOTNAME_STRINGTerminalRuleCall_5_0; }
		
		//DOUBLE_SQUARE_CLOSE
		public RuleCall getDOUBLE_SQUARE_CLOSETerminalRuleCall_6() { return cDOUBLE_SQUARE_CLOSETerminalRuleCall_6; }
	}
	public class ExpressionReplacementSlotElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.ExpressionReplacementSlot");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cExpressionReplacementSlotAction_0 = (Action)cGroup.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_OPENTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final RuleCall cPLUSTerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		private final RuleCall cSCGTerminalRuleCall_3 = (RuleCall)cGroup.eContents().get(3);
		private final Group cGroup_4 = (Group)cGroup.eContents().get(4);
		private final RuleCall cROUND_OPENTerminalRuleCall_4_0 = (RuleCall)cGroup_4.eContents().get(0);
		private final Assignment cConstraintAssignment_4_1 = (Assignment)cGroup_4.eContents().get(1);
		private final RuleCall cConstraintExpressionConstraintParserRuleCall_4_1_0 = (RuleCall)cConstraintAssignment_4_1.eContents().get(0);
		private final RuleCall cROUND_CLOSETerminalRuleCall_4_2 = (RuleCall)cGroup_4.eContents().get(2);
		private final Assignment cNameAssignment_5 = (Assignment)cGroup.eContents().get(5);
		private final RuleCall cNameSLOTNAME_STRINGTerminalRuleCall_5_0 = (RuleCall)cNameAssignment_5.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_CLOSETerminalRuleCall_6 = (RuleCall)cGroup.eContents().get(6);
		
		//ExpressionReplacementSlot:
		//	{ExpressionReplacementSlot} DOUBLE_SQUARE_OPEN PLUS SCG? (ROUND_OPEN constraint=ExpressionConstraint ROUND_CLOSE)?
		//	name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//{ExpressionReplacementSlot} DOUBLE_SQUARE_OPEN PLUS SCG? (ROUND_OPEN constraint=ExpressionConstraint ROUND_CLOSE)?
		//name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE
		public Group getGroup() { return cGroup; }
		
		//{ExpressionReplacementSlot}
		public Action getExpressionReplacementSlotAction_0() { return cExpressionReplacementSlotAction_0; }
		
		//DOUBLE_SQUARE_OPEN
		public RuleCall getDOUBLE_SQUARE_OPENTerminalRuleCall_1() { return cDOUBLE_SQUARE_OPENTerminalRuleCall_1; }
		
		//PLUS
		public RuleCall getPLUSTerminalRuleCall_2() { return cPLUSTerminalRuleCall_2; }
		
		//SCG?
		public RuleCall getSCGTerminalRuleCall_3() { return cSCGTerminalRuleCall_3; }
		
		//(ROUND_OPEN constraint=ExpressionConstraint ROUND_CLOSE)?
		public Group getGroup_4() { return cGroup_4; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_4_0() { return cROUND_OPENTerminalRuleCall_4_0; }
		
		//constraint=ExpressionConstraint
		public Assignment getConstraintAssignment_4_1() { return cConstraintAssignment_4_1; }
		
		//ExpressionConstraint
		public RuleCall getConstraintExpressionConstraintParserRuleCall_4_1_0() { return cConstraintExpressionConstraintParserRuleCall_4_1_0; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_4_2() { return cROUND_CLOSETerminalRuleCall_4_2; }
		
		//name=SLOTNAME_STRING?
		public Assignment getNameAssignment_5() { return cNameAssignment_5; }
		
		//SLOTNAME_STRING
		public RuleCall getNameSLOTNAME_STRINGTerminalRuleCall_5_0() { return cNameSLOTNAME_STRINGTerminalRuleCall_5_0; }
		
		//DOUBLE_SQUARE_CLOSE
		public RuleCall getDOUBLE_SQUARE_CLOSETerminalRuleCall_6() { return cDOUBLE_SQUARE_CLOSETerminalRuleCall_6; }
	}
	public class TokenReplacementSlotElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.TokenReplacementSlot");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cTokenReplacementSlotAction_0 = (Action)cGroup.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_OPENTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final RuleCall cPLUSTerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		private final RuleCall cTOKTerminalRuleCall_3 = (RuleCall)cGroup.eContents().get(3);
		private final Group cGroup_4 = (Group)cGroup.eContents().get(4);
		private final RuleCall cROUND_OPENTerminalRuleCall_4_0 = (RuleCall)cGroup_4.eContents().get(0);
		private final Assignment cTokensAssignment_4_1 = (Assignment)cGroup_4.eContents().get(1);
		private final RuleCall cTokensSlotTokenParserRuleCall_4_1_0 = (RuleCall)cTokensAssignment_4_1.eContents().get(0);
		private final Assignment cTokensAssignment_4_2 = (Assignment)cGroup_4.eContents().get(2);
		private final RuleCall cTokensSlotTokenParserRuleCall_4_2_0 = (RuleCall)cTokensAssignment_4_2.eContents().get(0);
		private final RuleCall cROUND_CLOSETerminalRuleCall_4_3 = (RuleCall)cGroup_4.eContents().get(3);
		private final Assignment cNameAssignment_5 = (Assignment)cGroup.eContents().get(5);
		private final RuleCall cNameSLOTNAME_STRINGTerminalRuleCall_5_0 = (RuleCall)cNameAssignment_5.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_CLOSETerminalRuleCall_6 = (RuleCall)cGroup.eContents().get(6);
		
		//TokenReplacementSlot:
		//	{TokenReplacementSlot} DOUBLE_SQUARE_OPEN PLUS TOK (ROUND_OPEN tokens+=SlotToken tokens+=SlotToken* ROUND_CLOSE)?
		//	name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//{TokenReplacementSlot} DOUBLE_SQUARE_OPEN PLUS TOK (ROUND_OPEN tokens+=SlotToken tokens+=SlotToken* ROUND_CLOSE)?
		//name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE
		public Group getGroup() { return cGroup; }
		
		//{TokenReplacementSlot}
		public Action getTokenReplacementSlotAction_0() { return cTokenReplacementSlotAction_0; }
		
		//DOUBLE_SQUARE_OPEN
		public RuleCall getDOUBLE_SQUARE_OPENTerminalRuleCall_1() { return cDOUBLE_SQUARE_OPENTerminalRuleCall_1; }
		
		//PLUS
		public RuleCall getPLUSTerminalRuleCall_2() { return cPLUSTerminalRuleCall_2; }
		
		//TOK
		public RuleCall getTOKTerminalRuleCall_3() { return cTOKTerminalRuleCall_3; }
		
		//(ROUND_OPEN tokens+=SlotToken tokens+=SlotToken* ROUND_CLOSE)?
		public Group getGroup_4() { return cGroup_4; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_4_0() { return cROUND_OPENTerminalRuleCall_4_0; }
		
		//tokens+=SlotToken
		public Assignment getTokensAssignment_4_1() { return cTokensAssignment_4_1; }
		
		//SlotToken
		public RuleCall getTokensSlotTokenParserRuleCall_4_1_0() { return cTokensSlotTokenParserRuleCall_4_1_0; }
		
		//tokens+=SlotToken*
		public Assignment getTokensAssignment_4_2() { return cTokensAssignment_4_2; }
		
		//SlotToken
		public RuleCall getTokensSlotTokenParserRuleCall_4_2_0() { return cTokensSlotTokenParserRuleCall_4_2_0; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_4_3() { return cROUND_CLOSETerminalRuleCall_4_3; }
		
		//name=SLOTNAME_STRING?
		public Assignment getNameAssignment_5() { return cNameAssignment_5; }
		
		//SLOTNAME_STRING
		public RuleCall getNameSLOTNAME_STRINGTerminalRuleCall_5_0() { return cNameSLOTNAME_STRINGTerminalRuleCall_5_0; }
		
		//DOUBLE_SQUARE_CLOSE
		public RuleCall getDOUBLE_SQUARE_CLOSETerminalRuleCall_6() { return cDOUBLE_SQUARE_CLOSETerminalRuleCall_6; }
	}
	public class TemplateInformationSlotElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.TemplateInformationSlot");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cTemplateInformationSlotAction_0 = (Action)cGroup.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_OPENTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cCardinalityAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cCardinalityEtlCardinalityParserRuleCall_2_0 = (RuleCall)cCardinalityAssignment_2.eContents().get(0);
		private final Assignment cNameAssignment_3 = (Assignment)cGroup.eContents().get(3);
		private final RuleCall cNameSLOTNAME_STRINGTerminalRuleCall_3_0 = (RuleCall)cNameAssignment_3.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_CLOSETerminalRuleCall_4 = (RuleCall)cGroup.eContents().get(4);
		
		//TemplateInformationSlot:
		//	{TemplateInformationSlot} DOUBLE_SQUARE_OPEN cardinality=EtlCardinality? name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//{TemplateInformationSlot} DOUBLE_SQUARE_OPEN cardinality=EtlCardinality? name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE
		public Group getGroup() { return cGroup; }
		
		//{TemplateInformationSlot}
		public Action getTemplateInformationSlotAction_0() { return cTemplateInformationSlotAction_0; }
		
		//DOUBLE_SQUARE_OPEN
		public RuleCall getDOUBLE_SQUARE_OPENTerminalRuleCall_1() { return cDOUBLE_SQUARE_OPENTerminalRuleCall_1; }
		
		//cardinality=EtlCardinality?
		public Assignment getCardinalityAssignment_2() { return cCardinalityAssignment_2; }
		
		//EtlCardinality
		public RuleCall getCardinalityEtlCardinalityParserRuleCall_2_0() { return cCardinalityEtlCardinalityParserRuleCall_2_0; }
		
		//name=SLOTNAME_STRING?
		public Assignment getNameAssignment_3() { return cNameAssignment_3; }
		
		//SLOTNAME_STRING
		public RuleCall getNameSLOTNAME_STRINGTerminalRuleCall_3_0() { return cNameSLOTNAME_STRINGTerminalRuleCall_3_0; }
		
		//DOUBLE_SQUARE_CLOSE
		public RuleCall getDOUBLE_SQUARE_CLOSETerminalRuleCall_4() { return cDOUBLE_SQUARE_CLOSETerminalRuleCall_4; }
	}
	public class ConcreteValueReplacementSlotElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.ConcreteValueReplacementSlot");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cStringReplacementSlotParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cIntegerReplacementSlotParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cDecimalReplacementSlotParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		//ConcreteValueReplacementSlot:
		//	StringReplacementSlot | IntegerReplacementSlot | DecimalReplacementSlot;
		@Override public ParserRule getRule() { return rule; }
		
		//StringReplacementSlot | IntegerReplacementSlot | DecimalReplacementSlot
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//StringReplacementSlot
		public RuleCall getStringReplacementSlotParserRuleCall_0() { return cStringReplacementSlotParserRuleCall_0; }
		
		//IntegerReplacementSlot
		public RuleCall getIntegerReplacementSlotParserRuleCall_1() { return cIntegerReplacementSlotParserRuleCall_1; }
		
		//DecimalReplacementSlot
		public RuleCall getDecimalReplacementSlotParserRuleCall_2() { return cDecimalReplacementSlotParserRuleCall_2; }
	}
	public class StringReplacementSlotElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.StringReplacementSlot");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cStringReplacementSlotAction_0 = (Action)cGroup.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_OPENTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final RuleCall cPLUSTerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		private final RuleCall cSTRTerminalRuleCall_3 = (RuleCall)cGroup.eContents().get(3);
		private final Group cGroup_4 = (Group)cGroup.eContents().get(4);
		private final RuleCall cROUND_OPENTerminalRuleCall_4_0 = (RuleCall)cGroup_4.eContents().get(0);
		private final Assignment cValuesAssignment_4_1 = (Assignment)cGroup_4.eContents().get(1);
		private final RuleCall cValuesSTRINGTerminalRuleCall_4_1_0 = (RuleCall)cValuesAssignment_4_1.eContents().get(0);
		private final Assignment cValuesAssignment_4_2 = (Assignment)cGroup_4.eContents().get(2);
		private final RuleCall cValuesSTRINGTerminalRuleCall_4_2_0 = (RuleCall)cValuesAssignment_4_2.eContents().get(0);
		private final RuleCall cROUND_CLOSETerminalRuleCall_4_3 = (RuleCall)cGroup_4.eContents().get(3);
		private final Assignment cNameAssignment_5 = (Assignment)cGroup.eContents().get(5);
		private final RuleCall cNameSLOTNAME_STRINGTerminalRuleCall_5_0 = (RuleCall)cNameAssignment_5.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_CLOSETerminalRuleCall_6 = (RuleCall)cGroup.eContents().get(6);
		
		//StringReplacementSlot:
		//	{StringReplacementSlot} DOUBLE_SQUARE_OPEN PLUS STR (ROUND_OPEN values+=STRING values+=STRING* ROUND_CLOSE)?
		//	name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//{StringReplacementSlot} DOUBLE_SQUARE_OPEN PLUS STR (ROUND_OPEN values+=STRING values+=STRING* ROUND_CLOSE)?
		//name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE
		public Group getGroup() { return cGroup; }
		
		//{StringReplacementSlot}
		public Action getStringReplacementSlotAction_0() { return cStringReplacementSlotAction_0; }
		
		//DOUBLE_SQUARE_OPEN
		public RuleCall getDOUBLE_SQUARE_OPENTerminalRuleCall_1() { return cDOUBLE_SQUARE_OPENTerminalRuleCall_1; }
		
		//PLUS
		public RuleCall getPLUSTerminalRuleCall_2() { return cPLUSTerminalRuleCall_2; }
		
		//STR
		public RuleCall getSTRTerminalRuleCall_3() { return cSTRTerminalRuleCall_3; }
		
		//(ROUND_OPEN values+=STRING values+=STRING* ROUND_CLOSE)?
		public Group getGroup_4() { return cGroup_4; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_4_0() { return cROUND_OPENTerminalRuleCall_4_0; }
		
		//values+=STRING
		public Assignment getValuesAssignment_4_1() { return cValuesAssignment_4_1; }
		
		//STRING
		public RuleCall getValuesSTRINGTerminalRuleCall_4_1_0() { return cValuesSTRINGTerminalRuleCall_4_1_0; }
		
		//values+=STRING*
		public Assignment getValuesAssignment_4_2() { return cValuesAssignment_4_2; }
		
		//STRING
		public RuleCall getValuesSTRINGTerminalRuleCall_4_2_0() { return cValuesSTRINGTerminalRuleCall_4_2_0; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_4_3() { return cROUND_CLOSETerminalRuleCall_4_3; }
		
		//name=SLOTNAME_STRING?
		public Assignment getNameAssignment_5() { return cNameAssignment_5; }
		
		//SLOTNAME_STRING
		public RuleCall getNameSLOTNAME_STRINGTerminalRuleCall_5_0() { return cNameSLOTNAME_STRINGTerminalRuleCall_5_0; }
		
		//DOUBLE_SQUARE_CLOSE
		public RuleCall getDOUBLE_SQUARE_CLOSETerminalRuleCall_6() { return cDOUBLE_SQUARE_CLOSETerminalRuleCall_6; }
	}
	public class IntegerReplacementSlotElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.IntegerReplacementSlot");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cIntegerReplacementSlotAction_0 = (Action)cGroup.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_OPENTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final RuleCall cPLUSTerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		private final RuleCall cINTTerminalRuleCall_3 = (RuleCall)cGroup.eContents().get(3);
		private final Group cGroup_4 = (Group)cGroup.eContents().get(4);
		private final RuleCall cROUND_OPENTerminalRuleCall_4_0 = (RuleCall)cGroup_4.eContents().get(0);
		private final Assignment cValuesAssignment_4_1 = (Assignment)cGroup_4.eContents().get(1);
		private final RuleCall cValuesSlotIntegerParserRuleCall_4_1_0 = (RuleCall)cValuesAssignment_4_1.eContents().get(0);
		private final Assignment cValuesAssignment_4_2 = (Assignment)cGroup_4.eContents().get(2);
		private final RuleCall cValuesSlotIntegerParserRuleCall_4_2_0 = (RuleCall)cValuesAssignment_4_2.eContents().get(0);
		private final RuleCall cROUND_CLOSETerminalRuleCall_4_3 = (RuleCall)cGroup_4.eContents().get(3);
		private final Assignment cNameAssignment_5 = (Assignment)cGroup.eContents().get(5);
		private final RuleCall cNameSLOTNAME_STRINGTerminalRuleCall_5_0 = (RuleCall)cNameAssignment_5.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_CLOSETerminalRuleCall_6 = (RuleCall)cGroup.eContents().get(6);
		
		//IntegerReplacementSlot:
		//	{IntegerReplacementSlot} DOUBLE_SQUARE_OPEN PLUS INT (ROUND_OPEN values+=SlotInteger values+=SlotInteger*
		//	ROUND_CLOSE)? name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//{IntegerReplacementSlot} DOUBLE_SQUARE_OPEN PLUS INT (ROUND_OPEN values+=SlotInteger values+=SlotInteger* ROUND_CLOSE)?
		//name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE
		public Group getGroup() { return cGroup; }
		
		//{IntegerReplacementSlot}
		public Action getIntegerReplacementSlotAction_0() { return cIntegerReplacementSlotAction_0; }
		
		//DOUBLE_SQUARE_OPEN
		public RuleCall getDOUBLE_SQUARE_OPENTerminalRuleCall_1() { return cDOUBLE_SQUARE_OPENTerminalRuleCall_1; }
		
		//PLUS
		public RuleCall getPLUSTerminalRuleCall_2() { return cPLUSTerminalRuleCall_2; }
		
		//INT
		public RuleCall getINTTerminalRuleCall_3() { return cINTTerminalRuleCall_3; }
		
		//(ROUND_OPEN values+=SlotInteger values+=SlotInteger* ROUND_CLOSE)?
		public Group getGroup_4() { return cGroup_4; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_4_0() { return cROUND_OPENTerminalRuleCall_4_0; }
		
		//values+=SlotInteger
		public Assignment getValuesAssignment_4_1() { return cValuesAssignment_4_1; }
		
		//SlotInteger
		public RuleCall getValuesSlotIntegerParserRuleCall_4_1_0() { return cValuesSlotIntegerParserRuleCall_4_1_0; }
		
		//values+=SlotInteger*
		public Assignment getValuesAssignment_4_2() { return cValuesAssignment_4_2; }
		
		//SlotInteger
		public RuleCall getValuesSlotIntegerParserRuleCall_4_2_0() { return cValuesSlotIntegerParserRuleCall_4_2_0; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_4_3() { return cROUND_CLOSETerminalRuleCall_4_3; }
		
		//name=SLOTNAME_STRING?
		public Assignment getNameAssignment_5() { return cNameAssignment_5; }
		
		//SLOTNAME_STRING
		public RuleCall getNameSLOTNAME_STRINGTerminalRuleCall_5_0() { return cNameSLOTNAME_STRINGTerminalRuleCall_5_0; }
		
		//DOUBLE_SQUARE_CLOSE
		public RuleCall getDOUBLE_SQUARE_CLOSETerminalRuleCall_6() { return cDOUBLE_SQUARE_CLOSETerminalRuleCall_6; }
	}
	public class DecimalReplacementSlotElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.DecimalReplacementSlot");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cDecimalReplacementSlotAction_0 = (Action)cGroup.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_OPENTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final RuleCall cPLUSTerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		private final RuleCall cDECTerminalRuleCall_3 = (RuleCall)cGroup.eContents().get(3);
		private final Group cGroup_4 = (Group)cGroup.eContents().get(4);
		private final RuleCall cROUND_OPENTerminalRuleCall_4_0 = (RuleCall)cGroup_4.eContents().get(0);
		private final Assignment cValuesAssignment_4_1 = (Assignment)cGroup_4.eContents().get(1);
		private final RuleCall cValuesSlotDecimalParserRuleCall_4_1_0 = (RuleCall)cValuesAssignment_4_1.eContents().get(0);
		private final Assignment cValuesAssignment_4_2 = (Assignment)cGroup_4.eContents().get(2);
		private final RuleCall cValuesSlotDecimalParserRuleCall_4_2_0 = (RuleCall)cValuesAssignment_4_2.eContents().get(0);
		private final RuleCall cROUND_CLOSETerminalRuleCall_4_3 = (RuleCall)cGroup_4.eContents().get(3);
		private final Assignment cNameAssignment_5 = (Assignment)cGroup.eContents().get(5);
		private final RuleCall cNameSLOTNAME_STRINGTerminalRuleCall_5_0 = (RuleCall)cNameAssignment_5.eContents().get(0);
		private final RuleCall cDOUBLE_SQUARE_CLOSETerminalRuleCall_6 = (RuleCall)cGroup.eContents().get(6);
		
		//DecimalReplacementSlot:
		//	{DecimalReplacementSlot} DOUBLE_SQUARE_OPEN PLUS DEC (ROUND_OPEN values+=SlotDecimal values+=SlotDecimal*
		//	ROUND_CLOSE)? name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//{DecimalReplacementSlot} DOUBLE_SQUARE_OPEN PLUS DEC (ROUND_OPEN values+=SlotDecimal values+=SlotDecimal* ROUND_CLOSE)?
		//name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE
		public Group getGroup() { return cGroup; }
		
		//{DecimalReplacementSlot}
		public Action getDecimalReplacementSlotAction_0() { return cDecimalReplacementSlotAction_0; }
		
		//DOUBLE_SQUARE_OPEN
		public RuleCall getDOUBLE_SQUARE_OPENTerminalRuleCall_1() { return cDOUBLE_SQUARE_OPENTerminalRuleCall_1; }
		
		//PLUS
		public RuleCall getPLUSTerminalRuleCall_2() { return cPLUSTerminalRuleCall_2; }
		
		//DEC
		public RuleCall getDECTerminalRuleCall_3() { return cDECTerminalRuleCall_3; }
		
		//(ROUND_OPEN values+=SlotDecimal values+=SlotDecimal* ROUND_CLOSE)?
		public Group getGroup_4() { return cGroup_4; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_4_0() { return cROUND_OPENTerminalRuleCall_4_0; }
		
		//values+=SlotDecimal
		public Assignment getValuesAssignment_4_1() { return cValuesAssignment_4_1; }
		
		//SlotDecimal
		public RuleCall getValuesSlotDecimalParserRuleCall_4_1_0() { return cValuesSlotDecimalParserRuleCall_4_1_0; }
		
		//values+=SlotDecimal*
		public Assignment getValuesAssignment_4_2() { return cValuesAssignment_4_2; }
		
		//SlotDecimal
		public RuleCall getValuesSlotDecimalParserRuleCall_4_2_0() { return cValuesSlotDecimalParserRuleCall_4_2_0; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_4_3() { return cROUND_CLOSETerminalRuleCall_4_3; }
		
		//name=SLOTNAME_STRING?
		public Assignment getNameAssignment_5() { return cNameAssignment_5; }
		
		//SLOTNAME_STRING
		public RuleCall getNameSLOTNAME_STRINGTerminalRuleCall_5_0() { return cNameSLOTNAME_STRINGTerminalRuleCall_5_0; }
		
		//DOUBLE_SQUARE_CLOSE
		public RuleCall getDOUBLE_SQUARE_CLOSETerminalRuleCall_6() { return cDOUBLE_SQUARE_CLOSETerminalRuleCall_6; }
	}
	public class EtlCardinalityElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.EtlCardinality");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cTILDETerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cMinAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cMinNonNegativeIntegerParserRuleCall_1_0 = (RuleCall)cMinAssignment_1.eContents().get(0);
		private final RuleCall cTOTerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		private final Assignment cMaxAssignment_3 = (Assignment)cGroup.eContents().get(3);
		private final RuleCall cMaxMaxValueParserRuleCall_3_0 = (RuleCall)cMaxAssignment_3.eContents().get(0);
		
		//EtlCardinality:
		//	TILDE? min=NonNegativeInteger TO max=MaxValue;
		@Override public ParserRule getRule() { return rule; }
		
		//// XXX this is different to Ecl.Cardinality -> needed to remove square brackets and to add optional tilde (-> IHTSDO has template syntax v0.2)
		//TILDE? min=NonNegativeInteger TO max=MaxValue
		public Group getGroup() { return cGroup; }
		
		//// XXX this is different to Ecl.Cardinality -> needed to remove square brackets and to add optional tilde (-> IHTSDO has template syntax v0.2)
		//TILDE?
		public RuleCall getTILDETerminalRuleCall_0() { return cTILDETerminalRuleCall_0; }
		
		//min=NonNegativeInteger
		public Assignment getMinAssignment_1() { return cMinAssignment_1; }
		
		//NonNegativeInteger
		public RuleCall getMinNonNegativeIntegerParserRuleCall_1_0() { return cMinNonNegativeIntegerParserRuleCall_1_0; }
		
		//TO
		public RuleCall getTOTerminalRuleCall_2() { return cTOTerminalRuleCall_2; }
		
		//max=MaxValue
		public Assignment getMaxAssignment_3() { return cMaxAssignment_3; }
		
		//MaxValue
		public RuleCall getMaxMaxValueParserRuleCall_3_0() { return cMaxMaxValueParserRuleCall_3_0; }
	}
	public class ConceptReplacementSlotElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.ConceptReplacementSlot");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cConceptIdReplacementSlotParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cExpressionReplacementSlotParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//ConceptReplacementSlot:
		//	ConceptIdReplacementSlot | ExpressionReplacementSlot;
		@Override public ParserRule getRule() { return rule; }
		
		//ConceptIdReplacementSlot | ExpressionReplacementSlot
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//ConceptIdReplacementSlot
		public RuleCall getConceptIdReplacementSlotParserRuleCall_0() { return cConceptIdReplacementSlotParserRuleCall_0; }
		
		//ExpressionReplacementSlot
		public RuleCall getExpressionReplacementSlotParserRuleCall_1() { return cExpressionReplacementSlotParserRuleCall_1; }
	}
	public class ConceptReferenceElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.ConceptReference");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final Assignment cSlotAssignment_0 = (Assignment)cAlternatives.eContents().get(0);
		private final RuleCall cSlotConceptReplacementSlotParserRuleCall_0_0 = (RuleCall)cSlotAssignment_0.eContents().get(0);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final Assignment cIdAssignment_1_0 = (Assignment)cGroup_1.eContents().get(0);
		private final RuleCall cIdSnomedIdentifierParserRuleCall_1_0_0 = (RuleCall)cIdAssignment_1_0.eContents().get(0);
		private final Assignment cTermAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cTermTERM_STRINGTerminalRuleCall_1_1_0 = (RuleCall)cTermAssignment_1_1.eContents().get(0);
		
		//ConceptReference:
		//	slot=ConceptReplacementSlot | id=SnomedIdentifier term=TERM_STRING?;
		@Override public ParserRule getRule() { return rule; }
		
		//slot=ConceptReplacementSlot | id=SnomedIdentifier term=TERM_STRING?
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//slot=ConceptReplacementSlot
		public Assignment getSlotAssignment_0() { return cSlotAssignment_0; }
		
		//ConceptReplacementSlot
		public RuleCall getSlotConceptReplacementSlotParserRuleCall_0_0() { return cSlotConceptReplacementSlotParserRuleCall_0_0; }
		
		//id=SnomedIdentifier term=TERM_STRING?
		public Group getGroup_1() { return cGroup_1; }
		
		//id=SnomedIdentifier
		public Assignment getIdAssignment_1_0() { return cIdAssignment_1_0; }
		
		//SnomedIdentifier
		public RuleCall getIdSnomedIdentifierParserRuleCall_1_0_0() { return cIdSnomedIdentifierParserRuleCall_1_0_0; }
		
		//term=TERM_STRING?
		public Assignment getTermAssignment_1_1() { return cTermAssignment_1_1; }
		
		//TERM_STRING
		public RuleCall getTermTERM_STRINGTerminalRuleCall_1_1_0() { return cTermTERM_STRINGTerminalRuleCall_1_1_0; }
	}
	public class SlotTokenElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SlotToken");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cEQUIVALENT_TOTerminalRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cSUBTYPE_OFTerminalRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cCOMMATerminalRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cCONJUNCTIONTerminalRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		private final RuleCall cDISJUNCTIONTerminalRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		private final RuleCall cEXCLUSIONTerminalRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		private final RuleCall cREVERSEDTerminalRuleCall_6 = (RuleCall)cAlternatives.eContents().get(6);
		private final RuleCall cCARETTerminalRuleCall_7 = (RuleCall)cAlternatives.eContents().get(7);
		private final RuleCall cLTTerminalRuleCall_8 = (RuleCall)cAlternatives.eContents().get(8);
		private final RuleCall cLTETerminalRuleCall_9 = (RuleCall)cAlternatives.eContents().get(9);
		private final RuleCall cDBL_LTTerminalRuleCall_10 = (RuleCall)cAlternatives.eContents().get(10);
		private final RuleCall cLT_EMTerminalRuleCall_11 = (RuleCall)cAlternatives.eContents().get(11);
		private final RuleCall cGTTerminalRuleCall_12 = (RuleCall)cAlternatives.eContents().get(12);
		private final RuleCall cGTETerminalRuleCall_13 = (RuleCall)cAlternatives.eContents().get(13);
		private final RuleCall cDBL_GTTerminalRuleCall_14 = (RuleCall)cAlternatives.eContents().get(14);
		private final RuleCall cGT_EMTerminalRuleCall_15 = (RuleCall)cAlternatives.eContents().get(15);
		private final RuleCall cEQUALTerminalRuleCall_16 = (RuleCall)cAlternatives.eContents().get(16);
		private final RuleCall cNOT_EQUALTerminalRuleCall_17 = (RuleCall)cAlternatives.eContents().get(17);
		
		//SlotToken:
		//	EQUIVALENT_TO | SUBTYPE_OF | COMMA | CONJUNCTION | DISJUNCTION | EXCLUSION | REVERSED | CARET | LT | LTE | DBL_LT |
		//	LT_EM | GT | GTE | DBL_GT | GT_EM | EQUAL | NOT_EQUAL;
		@Override public ParserRule getRule() { return rule; }
		
		//EQUIVALENT_TO | SUBTYPE_OF | COMMA | CONJUNCTION | DISJUNCTION | EXCLUSION | REVERSED | CARET | LT | LTE | DBL_LT |
		//LT_EM | GT | GTE | DBL_GT | GT_EM | EQUAL | NOT_EQUAL
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//EQUIVALENT_TO
		public RuleCall getEQUIVALENT_TOTerminalRuleCall_0() { return cEQUIVALENT_TOTerminalRuleCall_0; }
		
		//SUBTYPE_OF
		public RuleCall getSUBTYPE_OFTerminalRuleCall_1() { return cSUBTYPE_OFTerminalRuleCall_1; }
		
		//COMMA
		public RuleCall getCOMMATerminalRuleCall_2() { return cCOMMATerminalRuleCall_2; }
		
		//CONJUNCTION
		public RuleCall getCONJUNCTIONTerminalRuleCall_3() { return cCONJUNCTIONTerminalRuleCall_3; }
		
		//DISJUNCTION
		public RuleCall getDISJUNCTIONTerminalRuleCall_4() { return cDISJUNCTIONTerminalRuleCall_4; }
		
		//EXCLUSION
		public RuleCall getEXCLUSIONTerminalRuleCall_5() { return cEXCLUSIONTerminalRuleCall_5; }
		
		//REVERSED
		public RuleCall getREVERSEDTerminalRuleCall_6() { return cREVERSEDTerminalRuleCall_6; }
		
		//CARET
		public RuleCall getCARETTerminalRuleCall_7() { return cCARETTerminalRuleCall_7; }
		
		//LT
		public RuleCall getLTTerminalRuleCall_8() { return cLTTerminalRuleCall_8; }
		
		//LTE
		public RuleCall getLTETerminalRuleCall_9() { return cLTETerminalRuleCall_9; }
		
		//DBL_LT
		public RuleCall getDBL_LTTerminalRuleCall_10() { return cDBL_LTTerminalRuleCall_10; }
		
		//LT_EM
		public RuleCall getLT_EMTerminalRuleCall_11() { return cLT_EMTerminalRuleCall_11; }
		
		//GT
		public RuleCall getGTTerminalRuleCall_12() { return cGTTerminalRuleCall_12; }
		
		//GTE
		public RuleCall getGTETerminalRuleCall_13() { return cGTETerminalRuleCall_13; }
		
		//DBL_GT
		public RuleCall getDBL_GTTerminalRuleCall_14() { return cDBL_GTTerminalRuleCall_14; }
		
		//GT_EM
		public RuleCall getGT_EMTerminalRuleCall_15() { return cGT_EMTerminalRuleCall_15; }
		
		//EQUAL
		public RuleCall getEQUALTerminalRuleCall_16() { return cEQUALTerminalRuleCall_16; }
		
		//NOT_EQUAL
		public RuleCall getNOT_EQUALTerminalRuleCall_17() { return cNOT_EQUALTerminalRuleCall_17; }
	}
	public class StringValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.StringValue");
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
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.IntegerValue");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cHASHTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cValueAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cValueIntegerParserRuleCall_1_0 = (RuleCall)cValueAssignment_1.eContents().get(0);
		
		//IntegerValue:
		//	HASH value=Integer;
		@Override public ParserRule getRule() { return rule; }
		
		//// this is used by SCG (and ECL) and can be negative or positive 
		//HASH value=Integer
		public Group getGroup() { return cGroup; }
		
		//// this is used by SCG (and ECL) and can be negative or positive 
		//HASH
		public RuleCall getHASHTerminalRuleCall_0() { return cHASHTerminalRuleCall_0; }
		
		//value=Integer
		public Assignment getValueAssignment_1() { return cValueAssignment_1; }
		
		//Integer
		public RuleCall getValueIntegerParserRuleCall_1_0() { return cValueIntegerParserRuleCall_1_0; }
	}
	public class DecimalValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.DecimalValue");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cHASHTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cValueAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cValueDecimalParserRuleCall_1_0 = (RuleCall)cValueAssignment_1.eContents().get(0);
		
		//DecimalValue:
		//	HASH value=Decimal;
		@Override public ParserRule getRule() { return rule; }
		
		//// this is used by SCG (and ECL) and can be negative or positive
		//HASH value=Decimal
		public Group getGroup() { return cGroup; }
		
		//// this is used by SCG (and ECL) and can be negative or positive
		//HASH
		public RuleCall getHASHTerminalRuleCall_0() { return cHASHTerminalRuleCall_0; }
		
		//value=Decimal
		public Assignment getValueAssignment_1() { return cValueAssignment_1; }
		
		//Decimal
		public RuleCall getValueDecimalParserRuleCall_1_0() { return cValueDecimalParserRuleCall_1_0; }
	}
	public class SlotIntegerElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SlotInteger");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cSlotIntegerRangeParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cSlotIntegerValueParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//SlotInteger:
		//	SlotIntegerRange | SlotIntegerValue;
		@Override public ParserRule getRule() { return rule; }
		
		//// this is a slot integer value which cannot be negative, the range must preceed the simple value in this definition
		//SlotIntegerRange | SlotIntegerValue
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//// this is a slot integer value which cannot be negative, the range must preceed the simple value in this definition
		//SlotIntegerRange
		public RuleCall getSlotIntegerRangeParserRuleCall_0() { return cSlotIntegerRangeParserRuleCall_0; }
		
		//SlotIntegerValue
		public RuleCall getSlotIntegerValueParserRuleCall_1() { return cSlotIntegerValueParserRuleCall_1; }
	}
	public class SlotIntegerValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SlotIntegerValue");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cHASHTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cValueAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cValueNonNegativeIntegerParserRuleCall_1_0 = (RuleCall)cValueAssignment_1.eContents().get(0);
		
		//SlotIntegerValue:
		//	HASH value=NonNegativeInteger;
		@Override public ParserRule getRule() { return rule; }
		
		//HASH value=NonNegativeInteger
		public Group getGroup() { return cGroup; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_0() { return cHASHTerminalRuleCall_0; }
		
		//value=NonNegativeInteger
		public Assignment getValueAssignment_1() { return cValueAssignment_1; }
		
		//NonNegativeInteger
		public RuleCall getValueNonNegativeIntegerParserRuleCall_1_0() { return cValueNonNegativeIntegerParserRuleCall_1_0; }
	}
	public class SlotIntegerRangeElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SlotIntegerRange");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final Group cGroup_0 = (Group)cAlternatives.eContents().get(0);
		private final Assignment cMinimumAssignment_0_0 = (Assignment)cGroup_0.eContents().get(0);
		private final RuleCall cMinimumSlotIntegerMinimumValueParserRuleCall_0_0_0 = (RuleCall)cMinimumAssignment_0_0.eContents().get(0);
		private final RuleCall cTOTerminalRuleCall_0_1 = (RuleCall)cGroup_0.eContents().get(1);
		private final Assignment cMaximumAssignment_0_2 = (Assignment)cGroup_0.eContents().get(2);
		private final RuleCall cMaximumSlotIntegerMaximumValueParserRuleCall_0_2_0 = (RuleCall)cMaximumAssignment_0_2.eContents().get(0);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final RuleCall cTOTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Assignment cMaximumAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cMaximumSlotIntegerMaximumValueParserRuleCall_1_1_0 = (RuleCall)cMaximumAssignment_1_1.eContents().get(0);
		
		//SlotIntegerRange:
		//	minimum=SlotIntegerMinimumValue TO maximum=SlotIntegerMaximumValue? | TO maximum=SlotIntegerMaximumValue;
		@Override public ParserRule getRule() { return rule; }
		
		//minimum=SlotIntegerMinimumValue TO maximum=SlotIntegerMaximumValue? | TO maximum=SlotIntegerMaximumValue
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//minimum=SlotIntegerMinimumValue TO maximum=SlotIntegerMaximumValue?
		public Group getGroup_0() { return cGroup_0; }
		
		//minimum=SlotIntegerMinimumValue
		public Assignment getMinimumAssignment_0_0() { return cMinimumAssignment_0_0; }
		
		//SlotIntegerMinimumValue
		public RuleCall getMinimumSlotIntegerMinimumValueParserRuleCall_0_0_0() { return cMinimumSlotIntegerMinimumValueParserRuleCall_0_0_0; }
		
		//TO
		public RuleCall getTOTerminalRuleCall_0_1() { return cTOTerminalRuleCall_0_1; }
		
		//maximum=SlotIntegerMaximumValue?
		public Assignment getMaximumAssignment_0_2() { return cMaximumAssignment_0_2; }
		
		//SlotIntegerMaximumValue
		public RuleCall getMaximumSlotIntegerMaximumValueParserRuleCall_0_2_0() { return cMaximumSlotIntegerMaximumValueParserRuleCall_0_2_0; }
		
		//TO maximum=SlotIntegerMaximumValue
		public Group getGroup_1() { return cGroup_1; }
		
		//TO
		public RuleCall getTOTerminalRuleCall_1_0() { return cTOTerminalRuleCall_1_0; }
		
		//maximum=SlotIntegerMaximumValue
		public Assignment getMaximumAssignment_1_1() { return cMaximumAssignment_1_1; }
		
		//SlotIntegerMaximumValue
		public RuleCall getMaximumSlotIntegerMaximumValueParserRuleCall_1_1_0() { return cMaximumSlotIntegerMaximumValueParserRuleCall_1_1_0; }
	}
	public class SlotIntegerMinimumValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SlotIntegerMinimumValue");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cExclusiveAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cExclusiveGTTerminalRuleCall_0_0 = (RuleCall)cExclusiveAssignment_0.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueNonNegativeIntegerParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//SlotIntegerMinimumValue:
		//	exclusive?=GT? HASH value=NonNegativeInteger;
		@Override public ParserRule getRule() { return rule; }
		
		//exclusive?=GT? HASH value=NonNegativeInteger
		public Group getGroup() { return cGroup; }
		
		//exclusive?=GT?
		public Assignment getExclusiveAssignment_0() { return cExclusiveAssignment_0; }
		
		//GT
		public RuleCall getExclusiveGTTerminalRuleCall_0_0() { return cExclusiveGTTerminalRuleCall_0_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=NonNegativeInteger
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//NonNegativeInteger
		public RuleCall getValueNonNegativeIntegerParserRuleCall_2_0() { return cValueNonNegativeIntegerParserRuleCall_2_0; }
	}
	public class SlotIntegerMaximumValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SlotIntegerMaximumValue");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cExclusiveAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cExclusiveLTTerminalRuleCall_0_0 = (RuleCall)cExclusiveAssignment_0.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueNonNegativeIntegerParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//SlotIntegerMaximumValue:
		//	exclusive?=LT? HASH value=NonNegativeInteger;
		@Override public ParserRule getRule() { return rule; }
		
		//exclusive?=LT? HASH value=NonNegativeInteger
		public Group getGroup() { return cGroup; }
		
		//exclusive?=LT?
		public Assignment getExclusiveAssignment_0() { return cExclusiveAssignment_0; }
		
		//LT
		public RuleCall getExclusiveLTTerminalRuleCall_0_0() { return cExclusiveLTTerminalRuleCall_0_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=NonNegativeInteger
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//NonNegativeInteger
		public RuleCall getValueNonNegativeIntegerParserRuleCall_2_0() { return cValueNonNegativeIntegerParserRuleCall_2_0; }
	}
	public class SlotDecimalElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SlotDecimal");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cSlotDecimalRangeParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cSlotDecimalValueParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//SlotDecimal:
		//	SlotDecimalRange | SlotDecimalValue;
		@Override public ParserRule getRule() { return rule; }
		
		//// this is a slot decimal value which cannot be negative, the range must preceed the simple value in this definition
		//SlotDecimalRange | SlotDecimalValue
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//// this is a slot decimal value which cannot be negative, the range must preceed the simple value in this definition
		//SlotDecimalRange
		public RuleCall getSlotDecimalRangeParserRuleCall_0() { return cSlotDecimalRangeParserRuleCall_0; }
		
		//SlotDecimalValue
		public RuleCall getSlotDecimalValueParserRuleCall_1() { return cSlotDecimalValueParserRuleCall_1; }
	}
	public class SlotDecimalValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SlotDecimalValue");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cHASHTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cValueAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cValueNonNegativeDecimalParserRuleCall_1_0 = (RuleCall)cValueAssignment_1.eContents().get(0);
		
		//SlotDecimalValue:
		//	HASH value=NonNegativeDecimal;
		@Override public ParserRule getRule() { return rule; }
		
		//HASH value=NonNegativeDecimal
		public Group getGroup() { return cGroup; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_0() { return cHASHTerminalRuleCall_0; }
		
		//value=NonNegativeDecimal
		public Assignment getValueAssignment_1() { return cValueAssignment_1; }
		
		//NonNegativeDecimal
		public RuleCall getValueNonNegativeDecimalParserRuleCall_1_0() { return cValueNonNegativeDecimalParserRuleCall_1_0; }
	}
	public class SlotDecimalRangeElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SlotDecimalRange");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final Group cGroup_0 = (Group)cAlternatives.eContents().get(0);
		private final Assignment cMinimumAssignment_0_0 = (Assignment)cGroup_0.eContents().get(0);
		private final RuleCall cMinimumSlotDecimalMinimumValueParserRuleCall_0_0_0 = (RuleCall)cMinimumAssignment_0_0.eContents().get(0);
		private final RuleCall cTOTerminalRuleCall_0_1 = (RuleCall)cGroup_0.eContents().get(1);
		private final Assignment cMaximumAssignment_0_2 = (Assignment)cGroup_0.eContents().get(2);
		private final RuleCall cMaximumSlotDecimalMaximumValueParserRuleCall_0_2_0 = (RuleCall)cMaximumAssignment_0_2.eContents().get(0);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final RuleCall cTOTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Assignment cMaximumAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cMaximumSlotDecimalMaximumValueParserRuleCall_1_1_0 = (RuleCall)cMaximumAssignment_1_1.eContents().get(0);
		
		//SlotDecimalRange:
		//	minimum=SlotDecimalMinimumValue TO maximum=SlotDecimalMaximumValue? | TO maximum=SlotDecimalMaximumValue;
		@Override public ParserRule getRule() { return rule; }
		
		//minimum=SlotDecimalMinimumValue TO maximum=SlotDecimalMaximumValue? | TO maximum=SlotDecimalMaximumValue
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//minimum=SlotDecimalMinimumValue TO maximum=SlotDecimalMaximumValue?
		public Group getGroup_0() { return cGroup_0; }
		
		//minimum=SlotDecimalMinimumValue
		public Assignment getMinimumAssignment_0_0() { return cMinimumAssignment_0_0; }
		
		//SlotDecimalMinimumValue
		public RuleCall getMinimumSlotDecimalMinimumValueParserRuleCall_0_0_0() { return cMinimumSlotDecimalMinimumValueParserRuleCall_0_0_0; }
		
		//TO
		public RuleCall getTOTerminalRuleCall_0_1() { return cTOTerminalRuleCall_0_1; }
		
		//maximum=SlotDecimalMaximumValue?
		public Assignment getMaximumAssignment_0_2() { return cMaximumAssignment_0_2; }
		
		//SlotDecimalMaximumValue
		public RuleCall getMaximumSlotDecimalMaximumValueParserRuleCall_0_2_0() { return cMaximumSlotDecimalMaximumValueParserRuleCall_0_2_0; }
		
		//TO maximum=SlotDecimalMaximumValue
		public Group getGroup_1() { return cGroup_1; }
		
		//TO
		public RuleCall getTOTerminalRuleCall_1_0() { return cTOTerminalRuleCall_1_0; }
		
		//maximum=SlotDecimalMaximumValue
		public Assignment getMaximumAssignment_1_1() { return cMaximumAssignment_1_1; }
		
		//SlotDecimalMaximumValue
		public RuleCall getMaximumSlotDecimalMaximumValueParserRuleCall_1_1_0() { return cMaximumSlotDecimalMaximumValueParserRuleCall_1_1_0; }
	}
	public class SlotDecimalMinimumValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SlotDecimalMinimumValue");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cExclusiveAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cExclusiveGTTerminalRuleCall_0_0 = (RuleCall)cExclusiveAssignment_0.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueNonNegativeDecimalParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//SlotDecimalMinimumValue:
		//	exclusive?=GT? HASH value=NonNegativeDecimal;
		@Override public ParserRule getRule() { return rule; }
		
		//exclusive?=GT? HASH value=NonNegativeDecimal
		public Group getGroup() { return cGroup; }
		
		//exclusive?=GT?
		public Assignment getExclusiveAssignment_0() { return cExclusiveAssignment_0; }
		
		//GT
		public RuleCall getExclusiveGTTerminalRuleCall_0_0() { return cExclusiveGTTerminalRuleCall_0_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=NonNegativeDecimal
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//NonNegativeDecimal
		public RuleCall getValueNonNegativeDecimalParserRuleCall_2_0() { return cValueNonNegativeDecimalParserRuleCall_2_0; }
	}
	public class SlotDecimalMaximumValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SlotDecimalMaximumValue");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cExclusiveAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cExclusiveLTTerminalRuleCall_0_0 = (RuleCall)cExclusiveAssignment_0.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueNonNegativeDecimalParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//SlotDecimalMaximumValue:
		//	exclusive?=LT? HASH value=NonNegativeDecimal;
		@Override public ParserRule getRule() { return rule; }
		
		//exclusive?=LT? HASH value=NonNegativeDecimal
		public Group getGroup() { return cGroup; }
		
		//exclusive?=LT?
		public Assignment getExclusiveAssignment_0() { return cExclusiveAssignment_0; }
		
		//LT
		public RuleCall getExclusiveLTTerminalRuleCall_0_0() { return cExclusiveLTTerminalRuleCall_0_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=NonNegativeDecimal
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//NonNegativeDecimal
		public RuleCall getValueNonNegativeDecimalParserRuleCall_2_0() { return cValueNonNegativeDecimalParserRuleCall_2_0; }
	}
	
	
	private final ExpressionTemplateElements pExpressionTemplate;
	private final SubExpressionElements pSubExpression;
	private final FocusConceptElements pFocusConcept;
	private final RefinementElements pRefinement;
	private final AttributeGroupElements pAttributeGroup;
	private final AttributeElements pAttribute;
	private final AttributeValueElements pAttributeValue;
	private final ConceptIdReplacementSlotElements pConceptIdReplacementSlot;
	private final ExpressionReplacementSlotElements pExpressionReplacementSlot;
	private final TokenReplacementSlotElements pTokenReplacementSlot;
	private final TemplateInformationSlotElements pTemplateInformationSlot;
	private final ConcreteValueReplacementSlotElements pConcreteValueReplacementSlot;
	private final StringReplacementSlotElements pStringReplacementSlot;
	private final IntegerReplacementSlotElements pIntegerReplacementSlot;
	private final DecimalReplacementSlotElements pDecimalReplacementSlot;
	private final EtlCardinalityElements pEtlCardinality;
	private final ConceptReplacementSlotElements pConceptReplacementSlot;
	private final ConceptReferenceElements pConceptReference;
	private final SlotTokenElements pSlotToken;
	private final StringValueElements pStringValue;
	private final IntegerValueElements pIntegerValue;
	private final DecimalValueElements pDecimalValue;
	private final SlotIntegerElements pSlotInteger;
	private final SlotIntegerValueElements pSlotIntegerValue;
	private final SlotIntegerRangeElements pSlotIntegerRange;
	private final SlotIntegerMinimumValueElements pSlotIntegerMinimumValue;
	private final SlotIntegerMaximumValueElements pSlotIntegerMaximumValue;
	private final SlotDecimalElements pSlotDecimal;
	private final SlotDecimalValueElements pSlotDecimalValue;
	private final SlotDecimalRangeElements pSlotDecimalRange;
	private final SlotDecimalMinimumValueElements pSlotDecimalMinimumValue;
	private final SlotDecimalMaximumValueElements pSlotDecimalMaximumValue;
	private final TerminalRule tDOUBLE_SQUARE_OPEN;
	private final TerminalRule tDOUBLE_SQUARE_CLOSE;
	private final TerminalRule tTILDE;
	private final TerminalRule tAT;
	private final TerminalRule tID;
	private final TerminalRule tSCG;
	private final TerminalRule tTOK;
	private final TerminalRule tSTR;
	private final TerminalRule tINT;
	private final TerminalRule tDEC;
	private final TerminalRule tEQUIVALENT_TO;
	private final TerminalRule tSUBTYPE_OF;
	private final TerminalRule tSLOTNAME_STRING;
	
	private final Grammar grammar;
	
	private final EclGrammarAccess gaEcl;

	@Inject
	public EtlGrammarAccess(GrammarProvider grammarProvider,
			EclGrammarAccess gaEcl) {
		this.grammar = internalFindGrammar(grammarProvider);
		this.gaEcl = gaEcl;
		this.pExpressionTemplate = new ExpressionTemplateElements();
		this.pSubExpression = new SubExpressionElements();
		this.pFocusConcept = new FocusConceptElements();
		this.pRefinement = new RefinementElements();
		this.pAttributeGroup = new AttributeGroupElements();
		this.pAttribute = new AttributeElements();
		this.pAttributeValue = new AttributeValueElements();
		this.pConceptIdReplacementSlot = new ConceptIdReplacementSlotElements();
		this.pExpressionReplacementSlot = new ExpressionReplacementSlotElements();
		this.pTokenReplacementSlot = new TokenReplacementSlotElements();
		this.pTemplateInformationSlot = new TemplateInformationSlotElements();
		this.pConcreteValueReplacementSlot = new ConcreteValueReplacementSlotElements();
		this.pStringReplacementSlot = new StringReplacementSlotElements();
		this.pIntegerReplacementSlot = new IntegerReplacementSlotElements();
		this.pDecimalReplacementSlot = new DecimalReplacementSlotElements();
		this.pEtlCardinality = new EtlCardinalityElements();
		this.pConceptReplacementSlot = new ConceptReplacementSlotElements();
		this.pConceptReference = new ConceptReferenceElements();
		this.pSlotToken = new SlotTokenElements();
		this.pStringValue = new StringValueElements();
		this.pIntegerValue = new IntegerValueElements();
		this.pDecimalValue = new DecimalValueElements();
		this.pSlotInteger = new SlotIntegerElements();
		this.pSlotIntegerValue = new SlotIntegerValueElements();
		this.pSlotIntegerRange = new SlotIntegerRangeElements();
		this.pSlotIntegerMinimumValue = new SlotIntegerMinimumValueElements();
		this.pSlotIntegerMaximumValue = new SlotIntegerMaximumValueElements();
		this.pSlotDecimal = new SlotDecimalElements();
		this.pSlotDecimalValue = new SlotDecimalValueElements();
		this.pSlotDecimalRange = new SlotDecimalRangeElements();
		this.pSlotDecimalMinimumValue = new SlotDecimalMinimumValueElements();
		this.pSlotDecimalMaximumValue = new SlotDecimalMaximumValueElements();
		this.tDOUBLE_SQUARE_OPEN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.DOUBLE_SQUARE_OPEN");
		this.tDOUBLE_SQUARE_CLOSE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.DOUBLE_SQUARE_CLOSE");
		this.tTILDE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.TILDE");
		this.tAT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.AT");
		this.tID = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.ID");
		this.tSCG = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SCG");
		this.tTOK = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.TOK");
		this.tSTR = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.STR");
		this.tINT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.INT");
		this.tDEC = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.DEC");
		this.tEQUIVALENT_TO = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.EQUIVALENT_TO");
		this.tSUBTYPE_OF = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SUBTYPE_OF");
		this.tSLOTNAME_STRING = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.etl.Etl.SLOTNAME_STRING");
	}
	
	protected Grammar internalFindGrammar(GrammarProvider grammarProvider) {
		Grammar grammar = grammarProvider.getGrammar(this);
		while (grammar != null) {
			if ("com.b2international.snowowl.snomed.etl.Etl".equals(grammar.getName())) {
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
	
	
	public EclGrammarAccess getEclGrammarAccess() {
		return gaEcl;
	}

	
	//ExpressionTemplate:
	//	{ExpressionTemplate} (((primitive?=SUBTYPE_OF | EQUIVALENT_TO) | slot=TokenReplacementSlot)?
	//	expression=SubExpression)?;
	public ExpressionTemplateElements getExpressionTemplateAccess() {
		return pExpressionTemplate;
	}
	
	public ParserRule getExpressionTemplateRule() {
		return getExpressionTemplateAccess().getRule();
	}
	
	//SubExpression:
	//	focusConcepts+=FocusConcept (PLUS focusConcepts+=FocusConcept)* (COLON refinement=Refinement)?;
	public SubExpressionElements getSubExpressionAccess() {
		return pSubExpression;
	}
	
	public ParserRule getSubExpressionRule() {
		return getSubExpressionAccess().getRule();
	}
	
	//FocusConcept:
	//	slot=TemplateInformationSlot? concept=ConceptReference;
	public FocusConceptElements getFocusConceptAccess() {
		return pFocusConcept;
	}
	
	public ParserRule getFocusConceptRule() {
		return getFocusConceptAccess().getRule();
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
	//	slot=TemplateInformationSlot? CURLY_OPEN attributes+=Attribute (COMMA attributes+=Attribute)* CURLY_CLOSE;
	public AttributeGroupElements getAttributeGroupAccess() {
		return pAttributeGroup;
	}
	
	public ParserRule getAttributeGroupRule() {
		return getAttributeGroupAccess().getRule();
	}
	
	//Attribute:
	//	slot=TemplateInformationSlot? name=ConceptReference EQUAL value=AttributeValue;
	public AttributeElements getAttributeAccess() {
		return pAttribute;
	}
	
	public ParserRule getAttributeRule() {
		return getAttributeAccess().getRule();
	}
	
	//AttributeValue:
	//	ConceptReference | ROUND_OPEN SubExpression ROUND_CLOSE | StringValue | IntegerValue | DecimalValue |
	//	ConcreteValueReplacementSlot;
	public AttributeValueElements getAttributeValueAccess() {
		return pAttributeValue;
	}
	
	public ParserRule getAttributeValueRule() {
		return getAttributeValueAccess().getRule();
	}
	
	//ConceptIdReplacementSlot:
	//	{ConceptIdReplacementSlot} DOUBLE_SQUARE_OPEN PLUS ID (ROUND_OPEN constraint=ExpressionConstraint ROUND_CLOSE)?
	//	name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
	public ConceptIdReplacementSlotElements getConceptIdReplacementSlotAccess() {
		return pConceptIdReplacementSlot;
	}
	
	public ParserRule getConceptIdReplacementSlotRule() {
		return getConceptIdReplacementSlotAccess().getRule();
	}
	
	//ExpressionReplacementSlot:
	//	{ExpressionReplacementSlot} DOUBLE_SQUARE_OPEN PLUS SCG? (ROUND_OPEN constraint=ExpressionConstraint ROUND_CLOSE)?
	//	name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
	public ExpressionReplacementSlotElements getExpressionReplacementSlotAccess() {
		return pExpressionReplacementSlot;
	}
	
	public ParserRule getExpressionReplacementSlotRule() {
		return getExpressionReplacementSlotAccess().getRule();
	}
	
	//TokenReplacementSlot:
	//	{TokenReplacementSlot} DOUBLE_SQUARE_OPEN PLUS TOK (ROUND_OPEN tokens+=SlotToken tokens+=SlotToken* ROUND_CLOSE)?
	//	name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
	public TokenReplacementSlotElements getTokenReplacementSlotAccess() {
		return pTokenReplacementSlot;
	}
	
	public ParserRule getTokenReplacementSlotRule() {
		return getTokenReplacementSlotAccess().getRule();
	}
	
	//TemplateInformationSlot:
	//	{TemplateInformationSlot} DOUBLE_SQUARE_OPEN cardinality=EtlCardinality? name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
	public TemplateInformationSlotElements getTemplateInformationSlotAccess() {
		return pTemplateInformationSlot;
	}
	
	public ParserRule getTemplateInformationSlotRule() {
		return getTemplateInformationSlotAccess().getRule();
	}
	
	//ConcreteValueReplacementSlot:
	//	StringReplacementSlot | IntegerReplacementSlot | DecimalReplacementSlot;
	public ConcreteValueReplacementSlotElements getConcreteValueReplacementSlotAccess() {
		return pConcreteValueReplacementSlot;
	}
	
	public ParserRule getConcreteValueReplacementSlotRule() {
		return getConcreteValueReplacementSlotAccess().getRule();
	}
	
	//StringReplacementSlot:
	//	{StringReplacementSlot} DOUBLE_SQUARE_OPEN PLUS STR (ROUND_OPEN values+=STRING values+=STRING* ROUND_CLOSE)?
	//	name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
	public StringReplacementSlotElements getStringReplacementSlotAccess() {
		return pStringReplacementSlot;
	}
	
	public ParserRule getStringReplacementSlotRule() {
		return getStringReplacementSlotAccess().getRule();
	}
	
	//IntegerReplacementSlot:
	//	{IntegerReplacementSlot} DOUBLE_SQUARE_OPEN PLUS INT (ROUND_OPEN values+=SlotInteger values+=SlotInteger*
	//	ROUND_CLOSE)? name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
	public IntegerReplacementSlotElements getIntegerReplacementSlotAccess() {
		return pIntegerReplacementSlot;
	}
	
	public ParserRule getIntegerReplacementSlotRule() {
		return getIntegerReplacementSlotAccess().getRule();
	}
	
	//DecimalReplacementSlot:
	//	{DecimalReplacementSlot} DOUBLE_SQUARE_OPEN PLUS DEC (ROUND_OPEN values+=SlotDecimal values+=SlotDecimal*
	//	ROUND_CLOSE)? name=SLOTNAME_STRING? DOUBLE_SQUARE_CLOSE;
	public DecimalReplacementSlotElements getDecimalReplacementSlotAccess() {
		return pDecimalReplacementSlot;
	}
	
	public ParserRule getDecimalReplacementSlotRule() {
		return getDecimalReplacementSlotAccess().getRule();
	}
	
	//EtlCardinality:
	//	TILDE? min=NonNegativeInteger TO max=MaxValue;
	public EtlCardinalityElements getEtlCardinalityAccess() {
		return pEtlCardinality;
	}
	
	public ParserRule getEtlCardinalityRule() {
		return getEtlCardinalityAccess().getRule();
	}
	
	//ConceptReplacementSlot:
	//	ConceptIdReplacementSlot | ExpressionReplacementSlot;
	public ConceptReplacementSlotElements getConceptReplacementSlotAccess() {
		return pConceptReplacementSlot;
	}
	
	public ParserRule getConceptReplacementSlotRule() {
		return getConceptReplacementSlotAccess().getRule();
	}
	
	//ConceptReference:
	//	slot=ConceptReplacementSlot | id=SnomedIdentifier term=TERM_STRING?;
	public ConceptReferenceElements getConceptReferenceAccess() {
		return pConceptReference;
	}
	
	public ParserRule getConceptReferenceRule() {
		return getConceptReferenceAccess().getRule();
	}
	
	//SlotToken:
	//	EQUIVALENT_TO | SUBTYPE_OF | COMMA | CONJUNCTION | DISJUNCTION | EXCLUSION | REVERSED | CARET | LT | LTE | DBL_LT |
	//	LT_EM | GT | GTE | DBL_GT | GT_EM | EQUAL | NOT_EQUAL;
	public SlotTokenElements getSlotTokenAccess() {
		return pSlotToken;
	}
	
	public ParserRule getSlotTokenRule() {
		return getSlotTokenAccess().getRule();
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
	
	//SlotInteger:
	//	SlotIntegerRange | SlotIntegerValue;
	public SlotIntegerElements getSlotIntegerAccess() {
		return pSlotInteger;
	}
	
	public ParserRule getSlotIntegerRule() {
		return getSlotIntegerAccess().getRule();
	}
	
	//SlotIntegerValue:
	//	HASH value=NonNegativeInteger;
	public SlotIntegerValueElements getSlotIntegerValueAccess() {
		return pSlotIntegerValue;
	}
	
	public ParserRule getSlotIntegerValueRule() {
		return getSlotIntegerValueAccess().getRule();
	}
	
	//SlotIntegerRange:
	//	minimum=SlotIntegerMinimumValue TO maximum=SlotIntegerMaximumValue? | TO maximum=SlotIntegerMaximumValue;
	public SlotIntegerRangeElements getSlotIntegerRangeAccess() {
		return pSlotIntegerRange;
	}
	
	public ParserRule getSlotIntegerRangeRule() {
		return getSlotIntegerRangeAccess().getRule();
	}
	
	//SlotIntegerMinimumValue:
	//	exclusive?=GT? HASH value=NonNegativeInteger;
	public SlotIntegerMinimumValueElements getSlotIntegerMinimumValueAccess() {
		return pSlotIntegerMinimumValue;
	}
	
	public ParserRule getSlotIntegerMinimumValueRule() {
		return getSlotIntegerMinimumValueAccess().getRule();
	}
	
	//SlotIntegerMaximumValue:
	//	exclusive?=LT? HASH value=NonNegativeInteger;
	public SlotIntegerMaximumValueElements getSlotIntegerMaximumValueAccess() {
		return pSlotIntegerMaximumValue;
	}
	
	public ParserRule getSlotIntegerMaximumValueRule() {
		return getSlotIntegerMaximumValueAccess().getRule();
	}
	
	//SlotDecimal:
	//	SlotDecimalRange | SlotDecimalValue;
	public SlotDecimalElements getSlotDecimalAccess() {
		return pSlotDecimal;
	}
	
	public ParserRule getSlotDecimalRule() {
		return getSlotDecimalAccess().getRule();
	}
	
	//SlotDecimalValue:
	//	HASH value=NonNegativeDecimal;
	public SlotDecimalValueElements getSlotDecimalValueAccess() {
		return pSlotDecimalValue;
	}
	
	public ParserRule getSlotDecimalValueRule() {
		return getSlotDecimalValueAccess().getRule();
	}
	
	//SlotDecimalRange:
	//	minimum=SlotDecimalMinimumValue TO maximum=SlotDecimalMaximumValue? | TO maximum=SlotDecimalMaximumValue;
	public SlotDecimalRangeElements getSlotDecimalRangeAccess() {
		return pSlotDecimalRange;
	}
	
	public ParserRule getSlotDecimalRangeRule() {
		return getSlotDecimalRangeAccess().getRule();
	}
	
	//SlotDecimalMinimumValue:
	//	exclusive?=GT? HASH value=NonNegativeDecimal;
	public SlotDecimalMinimumValueElements getSlotDecimalMinimumValueAccess() {
		return pSlotDecimalMinimumValue;
	}
	
	public ParserRule getSlotDecimalMinimumValueRule() {
		return getSlotDecimalMinimumValueAccess().getRule();
	}
	
	//SlotDecimalMaximumValue:
	//	exclusive?=LT? HASH value=NonNegativeDecimal;
	public SlotDecimalMaximumValueElements getSlotDecimalMaximumValueAccess() {
		return pSlotDecimalMaximumValue;
	}
	
	public ParserRule getSlotDecimalMaximumValueRule() {
		return getSlotDecimalMaximumValueAccess().getRule();
	}
	
	//terminal DOUBLE_SQUARE_OPEN:
	//	'[[';
	public TerminalRule getDOUBLE_SQUARE_OPENRule() {
		return tDOUBLE_SQUARE_OPEN;
	}
	
	//terminal DOUBLE_SQUARE_CLOSE:
	//	']]';
	public TerminalRule getDOUBLE_SQUARE_CLOSERule() {
		return tDOUBLE_SQUARE_CLOSE;
	}
	
	//terminal TILDE:
	//	'~';
	public TerminalRule getTILDERule() {
		return tTILDE;
	}
	
	//terminal AT:
	//	'@';
	public TerminalRule getATRule() {
		return tAT;
	}
	
	//terminal ID:
	//	'id';
	public TerminalRule getIDRule() {
		return tID;
	}
	
	//terminal SCG:
	//	'scg';
	public TerminalRule getSCGRule() {
		return tSCG;
	}
	
	//terminal TOK:
	//	'tok';
	public TerminalRule getTOKRule() {
		return tTOK;
	}
	
	//terminal STR:
	//	'str';
	public TerminalRule getSTRRule() {
		return tSTR;
	}
	
	//terminal INT:
	//	'int';
	public TerminalRule getINTRule() {
		return tINT;
	}
	
	//terminal DEC:
	//	'dec';
	public TerminalRule getDECRule() {
		return tDEC;
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
	
	//terminal SLOTNAME_STRING:
	//	AT (STRING | !('\\' | '"' | "'" | WS | AT | SQUARE_OPEN | SQUARE_CLOSE)*);
	public TerminalRule getSLOTNAME_STRINGRule() {
		return tSLOTNAME_STRING;
	}
	
	//Script:
	//	{Script} constraint=ExpressionConstraint?;
	public EclGrammarAccess.ScriptElements getScriptAccess() {
		return gaEcl.getScriptAccess();
	}
	
	public ParserRule getScriptRule() {
		return getScriptAccess().getRule();
	}
	
	//ExpressionConstraint:
	//	OrExpressionConstraint;
	public EclGrammarAccess.ExpressionConstraintElements getExpressionConstraintAccess() {
		return gaEcl.getExpressionConstraintAccess();
	}
	
	public ParserRule getExpressionConstraintRule() {
		return getExpressionConstraintAccess().getRule();
	}
	
	//OrExpressionConstraint ExpressionConstraint:
	//	AndExpressionConstraint ({OrExpressionConstraint.left=current} DISJUNCTION right=AndExpressionConstraint)*;
	public EclGrammarAccess.OrExpressionConstraintElements getOrExpressionConstraintAccess() {
		return gaEcl.getOrExpressionConstraintAccess();
	}
	
	public ParserRule getOrExpressionConstraintRule() {
		return getOrExpressionConstraintAccess().getRule();
	}
	
	//AndExpressionConstraint ExpressionConstraint:
	//	ExclusionExpressionConstraint ({AndExpressionConstraint.left=current} (CONJUNCTION | COMMA)
	//	right=ExclusionExpressionConstraint)*;
	public EclGrammarAccess.AndExpressionConstraintElements getAndExpressionConstraintAccess() {
		return gaEcl.getAndExpressionConstraintAccess();
	}
	
	public ParserRule getAndExpressionConstraintRule() {
		return getAndExpressionConstraintAccess().getRule();
	}
	
	//ExclusionExpressionConstraint ExpressionConstraint:
	//	RefinedExpressionConstraint ({ExclusionExpressionConstraint.left=current} EXCLUSION
	//	right=RefinedExpressionConstraint)?;
	public EclGrammarAccess.ExclusionExpressionConstraintElements getExclusionExpressionConstraintAccess() {
		return gaEcl.getExclusionExpressionConstraintAccess();
	}
	
	public ParserRule getExclusionExpressionConstraintRule() {
		return getExclusionExpressionConstraintAccess().getRule();
	}
	
	//RefinedExpressionConstraint ExpressionConstraint:
	//	DottedExpressionConstraint ({RefinedExpressionConstraint.constraint=current} COLON refinement=EclRefinement)?;
	public EclGrammarAccess.RefinedExpressionConstraintElements getRefinedExpressionConstraintAccess() {
		return gaEcl.getRefinedExpressionConstraintAccess();
	}
	
	public ParserRule getRefinedExpressionConstraintRule() {
		return getRefinedExpressionConstraintAccess().getRule();
	}
	
	//DottedExpressionConstraint ExpressionConstraint:
	//	SubExpressionConstraint ({DottedExpressionConstraint.constraint=current} DOT attribute=SubExpressionConstraint)*;
	public EclGrammarAccess.DottedExpressionConstraintElements getDottedExpressionConstraintAccess() {
		return gaEcl.getDottedExpressionConstraintAccess();
	}
	
	public ParserRule getDottedExpressionConstraintRule() {
		return getDottedExpressionConstraintAccess().getRule();
	}
	
	//SubExpressionConstraint ExpressionConstraint:
	//	ChildOf | DescendantOf | DescendantOrSelfOf | ParentOf | AncestorOf | AncestorOrSelfOf | EclFocusConcept;
	public EclGrammarAccess.SubExpressionConstraintElements getSubExpressionConstraintAccess() {
		return gaEcl.getSubExpressionConstraintAccess();
	}
	
	public ParserRule getSubExpressionConstraintRule() {
		return getSubExpressionConstraintAccess().getRule();
	}
	
	//EclFocusConcept ExpressionConstraint:
	//	MemberOf | EclConceptReference | Any | NestedExpression;
	public EclGrammarAccess.EclFocusConceptElements getEclFocusConceptAccess() {
		return gaEcl.getEclFocusConceptAccess();
	}
	
	public ParserRule getEclFocusConceptRule() {
		return getEclFocusConceptAccess().getRule();
	}
	
	//ChildOf:
	//	LT_EM constraint=EclFocusConcept;
	public EclGrammarAccess.ChildOfElements getChildOfAccess() {
		return gaEcl.getChildOfAccess();
	}
	
	public ParserRule getChildOfRule() {
		return getChildOfAccess().getRule();
	}
	
	//DescendantOf:
	//	LT constraint=EclFocusConcept;
	public EclGrammarAccess.DescendantOfElements getDescendantOfAccess() {
		return gaEcl.getDescendantOfAccess();
	}
	
	public ParserRule getDescendantOfRule() {
		return getDescendantOfAccess().getRule();
	}
	
	//DescendantOrSelfOf:
	//	DBL_LT constraint=EclFocusConcept;
	public EclGrammarAccess.DescendantOrSelfOfElements getDescendantOrSelfOfAccess() {
		return gaEcl.getDescendantOrSelfOfAccess();
	}
	
	public ParserRule getDescendantOrSelfOfRule() {
		return getDescendantOrSelfOfAccess().getRule();
	}
	
	//ParentOf:
	//	GT_EM constraint=EclFocusConcept;
	public EclGrammarAccess.ParentOfElements getParentOfAccess() {
		return gaEcl.getParentOfAccess();
	}
	
	public ParserRule getParentOfRule() {
		return getParentOfAccess().getRule();
	}
	
	//AncestorOf:
	//	GT constraint=EclFocusConcept;
	public EclGrammarAccess.AncestorOfElements getAncestorOfAccess() {
		return gaEcl.getAncestorOfAccess();
	}
	
	public ParserRule getAncestorOfRule() {
		return getAncestorOfAccess().getRule();
	}
	
	//AncestorOrSelfOf:
	//	DBL_GT constraint=EclFocusConcept;
	public EclGrammarAccess.AncestorOrSelfOfElements getAncestorOrSelfOfAccess() {
		return gaEcl.getAncestorOrSelfOfAccess();
	}
	
	public ParserRule getAncestorOrSelfOfRule() {
		return getAncestorOrSelfOfAccess().getRule();
	}
	
	//MemberOf:
	//	CARET constraint=(EclConceptReference | Any | NestedExpression);
	public EclGrammarAccess.MemberOfElements getMemberOfAccess() {
		return gaEcl.getMemberOfAccess();
	}
	
	public ParserRule getMemberOfRule() {
		return getMemberOfAccess().getRule();
	}
	
	//EclConceptReference:
	//	id=SnomedIdentifier term=TERM_STRING?;
	public EclGrammarAccess.EclConceptReferenceElements getEclConceptReferenceAccess() {
		return gaEcl.getEclConceptReferenceAccess();
	}
	
	public ParserRule getEclConceptReferenceRule() {
		return getEclConceptReferenceAccess().getRule();
	}
	
	//Any:
	//	WILDCARD {Any};
	public EclGrammarAccess.AnyElements getAnyAccess() {
		return gaEcl.getAnyAccess();
	}
	
	public ParserRule getAnyRule() {
		return getAnyAccess().getRule();
	}
	
	//EclRefinement:
	//	OrRefinement;
	public EclGrammarAccess.EclRefinementElements getEclRefinementAccess() {
		return gaEcl.getEclRefinementAccess();
	}
	
	public ParserRule getEclRefinementRule() {
		return getEclRefinementAccess().getRule();
	}
	
	//OrRefinement EclRefinement:
	//	AndRefinement -> ({OrRefinement.left=current} DISJUNCTION right=AndRefinement)*;
	public EclGrammarAccess.OrRefinementElements getOrRefinementAccess() {
		return gaEcl.getOrRefinementAccess();
	}
	
	public ParserRule getOrRefinementRule() {
		return getOrRefinementAccess().getRule();
	}
	
	//AndRefinement EclRefinement:
	//	SubRefinement -> ({AndRefinement.left=current} (CONJUNCTION | COMMA) right=SubRefinement)*;
	public EclGrammarAccess.AndRefinementElements getAndRefinementAccess() {
		return gaEcl.getAndRefinementAccess();
	}
	
	public ParserRule getAndRefinementRule() {
		return getAndRefinementAccess().getRule();
	}
	
	//SubRefinement EclRefinement:
	//	AttributeConstraint | EclAttributeGroup | NestedRefinement;
	public EclGrammarAccess.SubRefinementElements getSubRefinementAccess() {
		return gaEcl.getSubRefinementAccess();
	}
	
	public ParserRule getSubRefinementRule() {
		return getSubRefinementAccess().getRule();
	}
	
	//NestedRefinement:
	//	ROUND_OPEN nested=EclRefinement ROUND_CLOSE;
	public EclGrammarAccess.NestedRefinementElements getNestedRefinementAccess() {
		return gaEcl.getNestedRefinementAccess();
	}
	
	public ParserRule getNestedRefinementRule() {
		return getNestedRefinementAccess().getRule();
	}
	
	//EclAttributeGroup:
	//	cardinality=Cardinality? CURLY_OPEN refinement=EclAttributeSet CURLY_CLOSE;
	public EclGrammarAccess.EclAttributeGroupElements getEclAttributeGroupAccess() {
		return gaEcl.getEclAttributeGroupAccess();
	}
	
	public ParserRule getEclAttributeGroupRule() {
		return getEclAttributeGroupAccess().getRule();
	}
	
	//EclAttributeSet EclRefinement:
	//	OrAttributeSet;
	public EclGrammarAccess.EclAttributeSetElements getEclAttributeSetAccess() {
		return gaEcl.getEclAttributeSetAccess();
	}
	
	public ParserRule getEclAttributeSetRule() {
		return getEclAttributeSetAccess().getRule();
	}
	
	//OrAttributeSet EclRefinement:
	//	AndAttributeSet ({OrRefinement.left=current} DISJUNCTION right=AndAttributeSet)*;
	public EclGrammarAccess.OrAttributeSetElements getOrAttributeSetAccess() {
		return gaEcl.getOrAttributeSetAccess();
	}
	
	public ParserRule getOrAttributeSetRule() {
		return getOrAttributeSetAccess().getRule();
	}
	
	//AndAttributeSet EclRefinement:
	//	SubAttributeSet ({AndRefinement.left=current} (CONJUNCTION | COMMA) right=SubAttributeSet)*;
	public EclGrammarAccess.AndAttributeSetElements getAndAttributeSetAccess() {
		return gaEcl.getAndAttributeSetAccess();
	}
	
	public ParserRule getAndAttributeSetRule() {
		return getAndAttributeSetAccess().getRule();
	}
	
	//SubAttributeSet EclRefinement:
	//	AttributeConstraint | NestedAttributeSet;
	public EclGrammarAccess.SubAttributeSetElements getSubAttributeSetAccess() {
		return gaEcl.getSubAttributeSetAccess();
	}
	
	public ParserRule getSubAttributeSetRule() {
		return getSubAttributeSetAccess().getRule();
	}
	
	//NestedAttributeSet NestedRefinement:
	//	ROUND_OPEN nested=EclAttributeSet ROUND_CLOSE;
	public EclGrammarAccess.NestedAttributeSetElements getNestedAttributeSetAccess() {
		return gaEcl.getNestedAttributeSetAccess();
	}
	
	public ParserRule getNestedAttributeSetRule() {
		return getNestedAttributeSetAccess().getRule();
	}
	
	//AttributeConstraint:
	//	cardinality=Cardinality? reversed?=REVERSED? attribute=SubExpressionConstraint comparison=Comparison;
	public EclGrammarAccess.AttributeConstraintElements getAttributeConstraintAccess() {
		return gaEcl.getAttributeConstraintAccess();
	}
	
	public ParserRule getAttributeConstraintRule() {
		return getAttributeConstraintAccess().getRule();
	}
	
	//Cardinality:
	//	SQUARE_OPEN min=NonNegativeInteger TO max=MaxValue SQUARE_CLOSE;
	public EclGrammarAccess.CardinalityElements getCardinalityAccess() {
		return gaEcl.getCardinalityAccess();
	}
	
	public ParserRule getCardinalityRule() {
		return getCardinalityAccess().getRule();
	}
	
	//Comparison:
	//	AttributeComparison | DataTypeComparison;
	public EclGrammarAccess.ComparisonElements getComparisonAccess() {
		return gaEcl.getComparisonAccess();
	}
	
	public ParserRule getComparisonRule() {
		return getComparisonAccess().getRule();
	}
	
	//AttributeComparison:
	//	AttributeValueEquals | AttributeValueNotEquals;
	public EclGrammarAccess.AttributeComparisonElements getAttributeComparisonAccess() {
		return gaEcl.getAttributeComparisonAccess();
	}
	
	public ParserRule getAttributeComparisonRule() {
		return getAttributeComparisonAccess().getRule();
	}
	
	//DataTypeComparison:
	//	BooleanValueEquals
	//	| BooleanValueNotEquals
	//	| StringValueEquals
	//	| StringValueNotEquals
	//	| IntegerValueEquals
	//	| IntegerValueNotEquals
	//	| IntegerValueGreaterThan
	//	| IntegerValueGreaterThanEquals
	//	| IntegerValueLessThan
	//	| IntegerValueLessThanEquals
	//	| DecimalValueEquals
	//	| DecimalValueNotEquals
	//	| DecimalValueGreaterThan
	//	| DecimalValueGreaterThanEquals
	//	| DecimalValueLessThan
	//	| DecimalValueLessThanEquals;
	public EclGrammarAccess.DataTypeComparisonElements getDataTypeComparisonAccess() {
		return gaEcl.getDataTypeComparisonAccess();
	}
	
	public ParserRule getDataTypeComparisonRule() {
		return getDataTypeComparisonAccess().getRule();
	}
	
	//AttributeValueEquals:
	//	EQUAL constraint=SubExpressionConstraint;
	public EclGrammarAccess.AttributeValueEqualsElements getAttributeValueEqualsAccess() {
		return gaEcl.getAttributeValueEqualsAccess();
	}
	
	public ParserRule getAttributeValueEqualsRule() {
		return getAttributeValueEqualsAccess().getRule();
	}
	
	//AttributeValueNotEquals:
	//	NOT_EQUAL constraint=SubExpressionConstraint;
	public EclGrammarAccess.AttributeValueNotEqualsElements getAttributeValueNotEqualsAccess() {
		return gaEcl.getAttributeValueNotEqualsAccess();
	}
	
	public ParserRule getAttributeValueNotEqualsRule() {
		return getAttributeValueNotEqualsAccess().getRule();
	}
	
	//BooleanValueEquals:
	//	EQUAL value=Boolean;
	public EclGrammarAccess.BooleanValueEqualsElements getBooleanValueEqualsAccess() {
		return gaEcl.getBooleanValueEqualsAccess();
	}
	
	public ParserRule getBooleanValueEqualsRule() {
		return getBooleanValueEqualsAccess().getRule();
	}
	
	//BooleanValueNotEquals:
	//	NOT_EQUAL value=Boolean;
	public EclGrammarAccess.BooleanValueNotEqualsElements getBooleanValueNotEqualsAccess() {
		return gaEcl.getBooleanValueNotEqualsAccess();
	}
	
	public ParserRule getBooleanValueNotEqualsRule() {
		return getBooleanValueNotEqualsAccess().getRule();
	}
	
	//StringValueEquals:
	//	EQUAL value=STRING;
	public EclGrammarAccess.StringValueEqualsElements getStringValueEqualsAccess() {
		return gaEcl.getStringValueEqualsAccess();
	}
	
	public ParserRule getStringValueEqualsRule() {
		return getStringValueEqualsAccess().getRule();
	}
	
	//StringValueNotEquals:
	//	NOT_EQUAL value=STRING;
	public EclGrammarAccess.StringValueNotEqualsElements getStringValueNotEqualsAccess() {
		return gaEcl.getStringValueNotEqualsAccess();
	}
	
	public ParserRule getStringValueNotEqualsRule() {
		return getStringValueNotEqualsAccess().getRule();
	}
	
	//IntegerValueEquals:
	//	EQUAL HASH value=Integer;
	public EclGrammarAccess.IntegerValueEqualsElements getIntegerValueEqualsAccess() {
		return gaEcl.getIntegerValueEqualsAccess();
	}
	
	public ParserRule getIntegerValueEqualsRule() {
		return getIntegerValueEqualsAccess().getRule();
	}
	
	//IntegerValueNotEquals:
	//	NOT_EQUAL HASH value=Integer;
	public EclGrammarAccess.IntegerValueNotEqualsElements getIntegerValueNotEqualsAccess() {
		return gaEcl.getIntegerValueNotEqualsAccess();
	}
	
	public ParserRule getIntegerValueNotEqualsRule() {
		return getIntegerValueNotEqualsAccess().getRule();
	}
	
	//IntegerValueGreaterThan:
	//	GT HASH value=Integer;
	public EclGrammarAccess.IntegerValueGreaterThanElements getIntegerValueGreaterThanAccess() {
		return gaEcl.getIntegerValueGreaterThanAccess();
	}
	
	public ParserRule getIntegerValueGreaterThanRule() {
		return getIntegerValueGreaterThanAccess().getRule();
	}
	
	//IntegerValueLessThan:
	//	LT HASH value=Integer;
	public EclGrammarAccess.IntegerValueLessThanElements getIntegerValueLessThanAccess() {
		return gaEcl.getIntegerValueLessThanAccess();
	}
	
	public ParserRule getIntegerValueLessThanRule() {
		return getIntegerValueLessThanAccess().getRule();
	}
	
	//IntegerValueGreaterThanEquals:
	//	GTE HASH value=Integer;
	public EclGrammarAccess.IntegerValueGreaterThanEqualsElements getIntegerValueGreaterThanEqualsAccess() {
		return gaEcl.getIntegerValueGreaterThanEqualsAccess();
	}
	
	public ParserRule getIntegerValueGreaterThanEqualsRule() {
		return getIntegerValueGreaterThanEqualsAccess().getRule();
	}
	
	//IntegerValueLessThanEquals:
	//	LTE HASH value=Integer;
	public EclGrammarAccess.IntegerValueLessThanEqualsElements getIntegerValueLessThanEqualsAccess() {
		return gaEcl.getIntegerValueLessThanEqualsAccess();
	}
	
	public ParserRule getIntegerValueLessThanEqualsRule() {
		return getIntegerValueLessThanEqualsAccess().getRule();
	}
	
	//DecimalValueEquals:
	//	EQUAL HASH value=Decimal;
	public EclGrammarAccess.DecimalValueEqualsElements getDecimalValueEqualsAccess() {
		return gaEcl.getDecimalValueEqualsAccess();
	}
	
	public ParserRule getDecimalValueEqualsRule() {
		return getDecimalValueEqualsAccess().getRule();
	}
	
	//DecimalValueNotEquals:
	//	NOT_EQUAL HASH value=Decimal;
	public EclGrammarAccess.DecimalValueNotEqualsElements getDecimalValueNotEqualsAccess() {
		return gaEcl.getDecimalValueNotEqualsAccess();
	}
	
	public ParserRule getDecimalValueNotEqualsRule() {
		return getDecimalValueNotEqualsAccess().getRule();
	}
	
	//DecimalValueGreaterThan:
	//	GT HASH value=Decimal;
	public EclGrammarAccess.DecimalValueGreaterThanElements getDecimalValueGreaterThanAccess() {
		return gaEcl.getDecimalValueGreaterThanAccess();
	}
	
	public ParserRule getDecimalValueGreaterThanRule() {
		return getDecimalValueGreaterThanAccess().getRule();
	}
	
	//DecimalValueLessThan:
	//	LT HASH value=Decimal;
	public EclGrammarAccess.DecimalValueLessThanElements getDecimalValueLessThanAccess() {
		return gaEcl.getDecimalValueLessThanAccess();
	}
	
	public ParserRule getDecimalValueLessThanRule() {
		return getDecimalValueLessThanAccess().getRule();
	}
	
	//DecimalValueGreaterThanEquals:
	//	GTE HASH value=Decimal;
	public EclGrammarAccess.DecimalValueGreaterThanEqualsElements getDecimalValueGreaterThanEqualsAccess() {
		return gaEcl.getDecimalValueGreaterThanEqualsAccess();
	}
	
	public ParserRule getDecimalValueGreaterThanEqualsRule() {
		return getDecimalValueGreaterThanEqualsAccess().getRule();
	}
	
	//DecimalValueLessThanEquals:
	//	LTE HASH value=Decimal;
	public EclGrammarAccess.DecimalValueLessThanEqualsElements getDecimalValueLessThanEqualsAccess() {
		return gaEcl.getDecimalValueLessThanEqualsAccess();
	}
	
	public ParserRule getDecimalValueLessThanEqualsRule() {
		return getDecimalValueLessThanEqualsAccess().getRule();
	}
	
	//NestedExpression:
	//	ROUND_OPEN nested=ExpressionConstraint ROUND_CLOSE;
	public EclGrammarAccess.NestedExpressionElements getNestedExpressionAccess() {
		return gaEcl.getNestedExpressionAccess();
	}
	
	public ParserRule getNestedExpressionRule() {
		return getNestedExpressionAccess().getRule();
	}
	
	//// hidden grammar rules
	//SnomedIdentifier hidden():
	//	DIGIT_NONZERO (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO)
	//	(DIGIT_NONZERO | ZERO)+;
	public EclGrammarAccess.SnomedIdentifierElements getSnomedIdentifierAccess() {
		return gaEcl.getSnomedIdentifierAccess();
	}
	
	public ParserRule getSnomedIdentifierRule() {
		return getSnomedIdentifierAccess().getRule();
	}
	
	//NonNegativeInteger ecore::EInt hidden():
	//	ZERO | DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*;
	public EclGrammarAccess.NonNegativeIntegerElements getNonNegativeIntegerAccess() {
		return gaEcl.getNonNegativeIntegerAccess();
	}
	
	public ParserRule getNonNegativeIntegerRule() {
		return getNonNegativeIntegerAccess().getRule();
	}
	
	//MaxValue ecore::EInt hidden():
	//	NonNegativeInteger | WILDCARD;
	public EclGrammarAccess.MaxValueElements getMaxValueAccess() {
		return gaEcl.getMaxValueAccess();
	}
	
	public ParserRule getMaxValueRule() {
		return getMaxValueAccess().getRule();
	}
	
	//Integer ecore::EInt hidden():
	//	(PLUS | DASH)? NonNegativeInteger;
	public EclGrammarAccess.IntegerElements getIntegerAccess() {
		return gaEcl.getIntegerAccess();
	}
	
	public ParserRule getIntegerRule() {
		return getIntegerAccess().getRule();
	}
	
	//Decimal ecore::EBigDecimal hidden():
	//	(PLUS | DASH)? NonNegativeDecimal;
	public EclGrammarAccess.DecimalElements getDecimalAccess() {
		return gaEcl.getDecimalAccess();
	}
	
	public ParserRule getDecimalRule() {
		return getDecimalAccess().getRule();
	}
	
	//NonNegativeDecimal ecore::EBigDecimal hidden():
	//	NonNegativeInteger DOT (DIGIT_NONZERO | ZERO)*;
	public EclGrammarAccess.NonNegativeDecimalElements getNonNegativeDecimalAccess() {
		return gaEcl.getNonNegativeDecimalAccess();
	}
	
	public ParserRule getNonNegativeDecimalRule() {
		return getNonNegativeDecimalAccess().getRule();
	}
	
	//Boolean ecore::EBoolean hidden():
	//	'true' | 'false';
	public EclGrammarAccess.BooleanElements getBooleanAccess() {
		return gaEcl.getBooleanAccess();
	}
	
	public ParserRule getBooleanRule() {
		return getBooleanAccess().getRule();
	}
	
	//terminal TERM_STRING:
	//	"|" !"|"* "|";
	public TerminalRule getTERM_STRINGRule() {
		return gaEcl.getTERM_STRINGRule();
	}
	
	//terminal REVERSED:
	//	'R';
	public TerminalRule getREVERSEDRule() {
		return gaEcl.getREVERSEDRule();
	}
	
	//terminal TO:
	//	'..';
	public TerminalRule getTORule() {
		return gaEcl.getTORule();
	}
	
	//terminal COMMA:
	//	',';
	public TerminalRule getCOMMARule() {
		return gaEcl.getCOMMARule();
	}
	
	//terminal CONJUNCTION:
	//	('a' | 'A') ('n' | 'N') ('d' | 'D');
	public TerminalRule getCONJUNCTIONRule() {
		return gaEcl.getCONJUNCTIONRule();
	}
	
	//terminal DISJUNCTION:
	//	('o' | 'O') ('r' | 'R');
	public TerminalRule getDISJUNCTIONRule() {
		return gaEcl.getDISJUNCTIONRule();
	}
	
	//terminal EXCLUSION:
	//	('m' | 'M') ('i' | 'I') ('n' | 'N') ('u' | 'U') ('s' | 'S');
	public TerminalRule getEXCLUSIONRule() {
		return gaEcl.getEXCLUSIONRule();
	}
	
	//terminal ZERO:
	//	'0';
	public TerminalRule getZERORule() {
		return gaEcl.getZERORule();
	}
	
	//terminal DIGIT_NONZERO:
	//	'1'..'9';
	public TerminalRule getDIGIT_NONZERORule() {
		return gaEcl.getDIGIT_NONZERORule();
	}
	
	//terminal COLON:
	//	':';
	public TerminalRule getCOLONRule() {
		return gaEcl.getCOLONRule();
	}
	
	//terminal CURLY_OPEN:
	//	'{';
	public TerminalRule getCURLY_OPENRule() {
		return gaEcl.getCURLY_OPENRule();
	}
	
	//terminal CURLY_CLOSE:
	//	'}';
	public TerminalRule getCURLY_CLOSERule() {
		return gaEcl.getCURLY_CLOSERule();
	}
	
	//terminal ROUND_OPEN:
	//	'(';
	public TerminalRule getROUND_OPENRule() {
		return gaEcl.getROUND_OPENRule();
	}
	
	//terminal ROUND_CLOSE:
	//	')';
	public TerminalRule getROUND_CLOSERule() {
		return gaEcl.getROUND_CLOSERule();
	}
	
	//terminal SQUARE_OPEN:
	//	'[';
	public TerminalRule getSQUARE_OPENRule() {
		return gaEcl.getSQUARE_OPENRule();
	}
	
	//terminal SQUARE_CLOSE:
	//	']';
	public TerminalRule getSQUARE_CLOSERule() {
		return gaEcl.getSQUARE_CLOSERule();
	}
	
	//terminal PLUS:
	//	'+';
	public TerminalRule getPLUSRule() {
		return gaEcl.getPLUSRule();
	}
	
	//terminal DASH:
	//	'-';
	public TerminalRule getDASHRule() {
		return gaEcl.getDASHRule();
	}
	
	//terminal CARET:
	//	'^';
	public TerminalRule getCARETRule() {
		return gaEcl.getCARETRule();
	}
	
	//terminal DOT:
	//	'.';
	public TerminalRule getDOTRule() {
		return gaEcl.getDOTRule();
	}
	
	//terminal WILDCARD:
	//	'*';
	public TerminalRule getWILDCARDRule() {
		return gaEcl.getWILDCARDRule();
	}
	
	//terminal EQUAL:
	//	'=';
	public TerminalRule getEQUALRule() {
		return gaEcl.getEQUALRule();
	}
	
	//terminal NOT_EQUAL:
	//	'!=';
	public TerminalRule getNOT_EQUALRule() {
		return gaEcl.getNOT_EQUALRule();
	}
	
	//terminal LT:
	//	'<';
	public TerminalRule getLTRule() {
		return gaEcl.getLTRule();
	}
	
	//terminal GT:
	//	'>';
	public TerminalRule getGTRule() {
		return gaEcl.getGTRule();
	}
	
	//terminal DBL_LT:
	//	'<<';
	public TerminalRule getDBL_LTRule() {
		return gaEcl.getDBL_LTRule();
	}
	
	//terminal DBL_GT:
	//	'>>';
	public TerminalRule getDBL_GTRule() {
		return gaEcl.getDBL_GTRule();
	}
	
	//terminal LT_EM:
	//	'<!';
	public TerminalRule getLT_EMRule() {
		return gaEcl.getLT_EMRule();
	}
	
	//terminal GT_EM:
	//	'>!';
	public TerminalRule getGT_EMRule() {
		return gaEcl.getGT_EMRule();
	}
	
	//terminal GTE:
	//	'>=';
	public TerminalRule getGTERule() {
		return gaEcl.getGTERule();
	}
	
	//terminal LTE:
	//	'<=';
	public TerminalRule getLTERule() {
		return gaEcl.getLTERule();
	}
	
	//terminal HASH:
	//	'#';
	public TerminalRule getHASHRule() {
		return gaEcl.getHASHRule();
	}
	
	//terminal WS:
	//	' ' | '\t' | '\n' | '\r';
	public TerminalRule getWSRule() {
		return gaEcl.getWSRule();
	}
	
	//terminal ML_COMMENT:
	//	'/*'->'*/';
	public TerminalRule getML_COMMENTRule() {
		return gaEcl.getML_COMMENTRule();
	}
	
	//terminal SL_COMMENT:
	//	'//' !('\n' | '\r')* ('\r'? '\n')?;
	public TerminalRule getSL_COMMENTRule() {
		return gaEcl.getSL_COMMENTRule();
	}
	
	//terminal STRING:
	//	'"' ('\\' . | !('\\' | '"'))* '"' |
	//	"'" ('\\' . | !('\\' | "'"))* "'";
	public TerminalRule getSTRINGRule() {
		return gaEcl.getSTRINGRule();
	}
}
