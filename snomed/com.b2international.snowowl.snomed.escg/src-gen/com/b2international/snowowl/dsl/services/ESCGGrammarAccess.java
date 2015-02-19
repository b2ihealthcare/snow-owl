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
package com.b2international.snowowl.dsl.services;

import com.google.inject.Singleton;
import com.google.inject.Inject;

import java.util.List;

import org.eclipse.xtext.*;
import org.eclipse.xtext.service.GrammarProvider;
import org.eclipse.xtext.service.AbstractElementFinder.*;


@Singleton
public class ESCGGrammarAccess extends AbstractGrammarElementFinder {
	
	
	public class ExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Expression");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cSubExpressionAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cSubExpressionSubExpressionParserRuleCall_0_0 = (RuleCall)cSubExpressionAssignment_0.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cUNION_TOKENTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Assignment cSubExpressionAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cSubExpressionSubExpressionParserRuleCall_1_1_0 = (RuleCall)cSubExpressionAssignment_1_1.eContents().get(0);
		
		//// parser rules
		// Expression hidden(WS, SL_COMMENT, ML_COMMENT):
		//
		//	(subExpression+=SubExpression (UNION_TOKEN subExpression+=SubExpression)*)?;
		public ParserRule getRule() { return rule; }

		//(subExpression+=SubExpression (UNION_TOKEN subExpression+=SubExpression)*)?
		public Group getGroup() { return cGroup; }

		//subExpression+=SubExpression
		public Assignment getSubExpressionAssignment_0() { return cSubExpressionAssignment_0; }

		//SubExpression
		public RuleCall getSubExpressionSubExpressionParserRuleCall_0_0() { return cSubExpressionSubExpressionParserRuleCall_0_0; }

		//(UNION_TOKEN subExpression+=SubExpression)*
		public Group getGroup_1() { return cGroup_1; }

		//UNION_TOKEN
		public RuleCall getUNION_TOKENTerminalRuleCall_1_0() { return cUNION_TOKENTerminalRuleCall_1_0; }

		//subExpression+=SubExpression
		public Assignment getSubExpressionAssignment_1_1() { return cSubExpressionAssignment_1_1; }

		//SubExpression
		public RuleCall getSubExpressionSubExpressionParserRuleCall_1_1_0() { return cSubExpressionSubExpressionParserRuleCall_1_1_0; }
	}

	public class SubExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "SubExpression");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cLValuesAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cLValuesLValueParserRuleCall_0_0 = (RuleCall)cLValuesAssignment_0.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cPLUS_SIGNTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Assignment cLValuesAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cLValuesLValueParserRuleCall_1_1_0 = (RuleCall)cLValuesAssignment_1_1.eContents().get(0);
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		private final RuleCall cCOLONTerminalRuleCall_2_0 = (RuleCall)cGroup_2.eContents().get(0);
		private final Assignment cRefinementsAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		private final RuleCall cRefinementsRefinementsParserRuleCall_2_1_0 = (RuleCall)cRefinementsAssignment_2_1.eContents().get(0);
		
		//SubExpression:
		//
		//	lValues+=LValue (PLUS_SIGN lValues+=LValue)* (COLON refinements=Refinements)?;
		public ParserRule getRule() { return rule; }

		//lValues+=LValue (PLUS_SIGN lValues+=LValue)* (COLON refinements=Refinements)?
		public Group getGroup() { return cGroup; }

		//lValues+=LValue
		public Assignment getLValuesAssignment_0() { return cLValuesAssignment_0; }

		//LValue
		public RuleCall getLValuesLValueParserRuleCall_0_0() { return cLValuesLValueParserRuleCall_0_0; }

		//(PLUS_SIGN lValues+=LValue)*
		public Group getGroup_1() { return cGroup_1; }

		//PLUS_SIGN
		public RuleCall getPLUS_SIGNTerminalRuleCall_1_0() { return cPLUS_SIGNTerminalRuleCall_1_0; }

		//lValues+=LValue
		public Assignment getLValuesAssignment_1_1() { return cLValuesAssignment_1_1; }

		//LValue
		public RuleCall getLValuesLValueParserRuleCall_1_1_0() { return cLValuesLValueParserRuleCall_1_1_0; }

		//(COLON refinements=Refinements)?
		public Group getGroup_2() { return cGroup_2; }

		//COLON
		public RuleCall getCOLONTerminalRuleCall_2_0() { return cCOLONTerminalRuleCall_2_0; }

		//refinements=Refinements
		public Assignment getRefinementsAssignment_2_1() { return cRefinementsAssignment_2_1; }

		//Refinements
		public RuleCall getRefinementsRefinementsParserRuleCall_2_1_0() { return cRefinementsRefinementsParserRuleCall_2_1_0; }
	}

	public class LValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "LValue");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cConceptGroupParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cRefSetParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//LValue:
		//
		//	ConceptGroup | RefSet;
		public ParserRule getRule() { return rule; }

		//ConceptGroup | RefSet
		public Alternatives getAlternatives() { return cAlternatives; }

		//ConceptGroup
		public RuleCall getConceptGroupParserRuleCall_0() { return cConceptGroupParserRuleCall_0; }

		//RefSet
		public RuleCall getRefSetParserRuleCall_1() { return cRefSetParserRuleCall_1; }
	}

	public class RefSetElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "RefSet");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cNegatedAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cNegatedNOT_TOKENTerminalRuleCall_0_0 = (RuleCall)cNegatedAssignment_0.eContents().get(0);
		private final RuleCall cCARETTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cIdAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cIdConceptIdParserRuleCall_2_0 = (RuleCall)cIdAssignment_2.eContents().get(0);
		private final Group cGroup_3 = (Group)cGroup.eContents().get(3);
		private final RuleCall cPIPETerminalRuleCall_3_0 = (RuleCall)cGroup_3.eContents().get(0);
		private final Assignment cTermAssignment_3_1 = (Assignment)cGroup_3.eContents().get(1);
		private final RuleCall cTermTermParserRuleCall_3_1_0 = (RuleCall)cTermAssignment_3_1.eContents().get(0);
		private final RuleCall cPIPETerminalRuleCall_3_2 = (RuleCall)cGroup_3.eContents().get(2);
		
		//RefSet:
		//
		//	negated?=NOT_TOKEN? CARET id=ConceptId (PIPE term=Term PIPE)?;
		public ParserRule getRule() { return rule; }

		//negated?=NOT_TOKEN? CARET id=ConceptId (PIPE term=Term PIPE)?
		public Group getGroup() { return cGroup; }

		//negated?=NOT_TOKEN?
		public Assignment getNegatedAssignment_0() { return cNegatedAssignment_0; }

		//NOT_TOKEN
		public RuleCall getNegatedNOT_TOKENTerminalRuleCall_0_0() { return cNegatedNOT_TOKENTerminalRuleCall_0_0; }

		//CARET
		public RuleCall getCARETTerminalRuleCall_1() { return cCARETTerminalRuleCall_1; }

		//id=ConceptId
		public Assignment getIdAssignment_2() { return cIdAssignment_2; }

		//ConceptId
		public RuleCall getIdConceptIdParserRuleCall_2_0() { return cIdConceptIdParserRuleCall_2_0; }

		//(PIPE term=Term PIPE)?
		public Group getGroup_3() { return cGroup_3; }

		//PIPE
		public RuleCall getPIPETerminalRuleCall_3_0() { return cPIPETerminalRuleCall_3_0; }

		//term=Term
		public Assignment getTermAssignment_3_1() { return cTermAssignment_3_1; }

		//Term
		public RuleCall getTermTermParserRuleCall_3_1_0() { return cTermTermParserRuleCall_3_1_0; }

		//PIPE
		public RuleCall getPIPETerminalRuleCall_3_2() { return cPIPETerminalRuleCall_3_2; }
	}

	public class ConceptGroupElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "ConceptGroup");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cNegatedAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cNegatedNOT_TOKENTerminalRuleCall_0_0 = (RuleCall)cNegatedAssignment_0.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final Alternatives cConstraintAlternatives_1_0 = (Alternatives)cConstraintAssignment_1.eContents().get(0);
		private final RuleCall cConstraintSUBTYPETerminalRuleCall_1_0_0 = (RuleCall)cConstraintAlternatives_1_0.eContents().get(0);
		private final RuleCall cConstraintINCLUSIVE_SUBTYPETerminalRuleCall_1_0_1 = (RuleCall)cConstraintAlternatives_1_0.eContents().get(1);
		private final Assignment cConceptAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cConceptConceptParserRuleCall_2_0 = (RuleCall)cConceptAssignment_2.eContents().get(0);
		
		////  ! << 1234567|Left hand|
		// ConceptGroup:
		//
		//	negated?=NOT_TOKEN? constraint=(SUBTYPE | INCLUSIVE_SUBTYPE)? concept=Concept;
		public ParserRule getRule() { return rule; }

		//negated?=NOT_TOKEN? constraint=(SUBTYPE | INCLUSIVE_SUBTYPE)? concept=Concept
		public Group getGroup() { return cGroup; }

		//negated?=NOT_TOKEN?
		public Assignment getNegatedAssignment_0() { return cNegatedAssignment_0; }

		//NOT_TOKEN
		public RuleCall getNegatedNOT_TOKENTerminalRuleCall_0_0() { return cNegatedNOT_TOKENTerminalRuleCall_0_0; }

		//constraint=(SUBTYPE | INCLUSIVE_SUBTYPE)?
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }

		//SUBTYPE | INCLUSIVE_SUBTYPE
		public Alternatives getConstraintAlternatives_1_0() { return cConstraintAlternatives_1_0; }

		//SUBTYPE
		public RuleCall getConstraintSUBTYPETerminalRuleCall_1_0_0() { return cConstraintSUBTYPETerminalRuleCall_1_0_0; }

		//INCLUSIVE_SUBTYPE
		public RuleCall getConstraintINCLUSIVE_SUBTYPETerminalRuleCall_1_0_1() { return cConstraintINCLUSIVE_SUBTYPETerminalRuleCall_1_0_1; }

		//concept=Concept
		public Assignment getConceptAssignment_2() { return cConceptAssignment_2; }

		//Concept
		public RuleCall getConceptConceptParserRuleCall_2_0() { return cConceptConceptParserRuleCall_2_0; }
	}

	public class ConceptElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Concept");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cIdAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cIdConceptIdParserRuleCall_0_0 = (RuleCall)cIdAssignment_0.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cPIPETerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final RuleCall cWSTerminalRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final Assignment cTermAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cTermTermParserRuleCall_1_2_0 = (RuleCall)cTermAssignment_1_2.eContents().get(0);
		private final RuleCall cWSTerminalRuleCall_1_3 = (RuleCall)cGroup_1.eContents().get(3);
		private final RuleCall cPIPETerminalRuleCall_1_4 = (RuleCall)cGroup_1.eContents().get(4);
		
		////  1234567|Left hand|
		// Concept:
		//
		//	id=ConceptId (PIPE WS* term=Term WS* PIPE)?;
		public ParserRule getRule() { return rule; }

		//id=ConceptId (PIPE WS* term=Term WS* PIPE)?
		public Group getGroup() { return cGroup; }

		//id=ConceptId
		public Assignment getIdAssignment_0() { return cIdAssignment_0; }

		//ConceptId
		public RuleCall getIdConceptIdParserRuleCall_0_0() { return cIdConceptIdParserRuleCall_0_0; }

		//(PIPE WS* term=Term WS* PIPE)?
		public Group getGroup_1() { return cGroup_1; }

		//PIPE
		public RuleCall getPIPETerminalRuleCall_1_0() { return cPIPETerminalRuleCall_1_0; }

		//WS*
		public RuleCall getWSTerminalRuleCall_1_1() { return cWSTerminalRuleCall_1_1; }

		//term=Term
		public Assignment getTermAssignment_1_2() { return cTermAssignment_1_2; }

		//Term
		public RuleCall getTermTermParserRuleCall_1_2_0() { return cTermTermParserRuleCall_1_2_0; }

		//WS*
		public RuleCall getWSTerminalRuleCall_1_3() { return cWSTerminalRuleCall_1_3; }

		//PIPE
		public RuleCall getPIPETerminalRuleCall_1_4() { return cPIPETerminalRuleCall_1_4; }
	}

	public class RefinementsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Refinements");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final Group cGroup_0 = (Group)cAlternatives.eContents().get(0);
		private final Assignment cAttributeSetAssignment_0_0 = (Assignment)cGroup_0.eContents().get(0);
		private final RuleCall cAttributeSetAttributeSetParserRuleCall_0_0_0 = (RuleCall)cAttributeSetAssignment_0_0.eContents().get(0);
		private final Assignment cAttributeGroupsAssignment_0_1 = (Assignment)cGroup_0.eContents().get(1);
		private final RuleCall cAttributeGroupsAttributeGroupParserRuleCall_0_1_0 = (RuleCall)cAttributeGroupsAssignment_0_1.eContents().get(0);
		private final Assignment cAttributeGroupsAssignment_1 = (Assignment)cAlternatives.eContents().get(1);
		private final RuleCall cAttributeGroupsAttributeGroupParserRuleCall_1_0 = (RuleCall)cAttributeGroupsAssignment_1.eContents().get(0);
		
		//Refinements:
		//
		//	attributeSet=AttributeSet attributeGroups+=AttributeGroup* | attributeGroups+=AttributeGroup+;
		public ParserRule getRule() { return rule; }

		//attributeSet=AttributeSet attributeGroups+=AttributeGroup* | attributeGroups+=AttributeGroup+
		public Alternatives getAlternatives() { return cAlternatives; }

		//attributeSet=AttributeSet attributeGroups+=AttributeGroup*
		public Group getGroup_0() { return cGroup_0; }

		//attributeSet=AttributeSet
		public Assignment getAttributeSetAssignment_0_0() { return cAttributeSetAssignment_0_0; }

		//AttributeSet
		public RuleCall getAttributeSetAttributeSetParserRuleCall_0_0_0() { return cAttributeSetAttributeSetParserRuleCall_0_0_0; }

		//attributeGroups+=AttributeGroup*
		public Assignment getAttributeGroupsAssignment_0_1() { return cAttributeGroupsAssignment_0_1; }

		//AttributeGroup
		public RuleCall getAttributeGroupsAttributeGroupParserRuleCall_0_1_0() { return cAttributeGroupsAttributeGroupParserRuleCall_0_1_0; }

		//attributeGroups+=AttributeGroup+
		public Assignment getAttributeGroupsAssignment_1() { return cAttributeGroupsAssignment_1; }

		//AttributeGroup
		public RuleCall getAttributeGroupsAttributeGroupParserRuleCall_1_0() { return cAttributeGroupsAttributeGroupParserRuleCall_1_0; }
	}

	public class AttributeGroupElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "AttributeGroup");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cOPENING_CURLY_BRACKETTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cAttributeSetParserRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final RuleCall cCLOSING_CURLY_BRACKETTerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		
		//AttributeGroup:
		//
		//	OPENING_CURLY_BRACKET AttributeSet CLOSING_CURLY_BRACKET;
		public ParserRule getRule() { return rule; }

		//OPENING_CURLY_BRACKET AttributeSet CLOSING_CURLY_BRACKET
		public Group getGroup() { return cGroup; }

		//OPENING_CURLY_BRACKET
		public RuleCall getOPENING_CURLY_BRACKETTerminalRuleCall_0() { return cOPENING_CURLY_BRACKETTerminalRuleCall_0; }

		//AttributeSet
		public RuleCall getAttributeSetParserRuleCall_1() { return cAttributeSetParserRuleCall_1; }

		//CLOSING_CURLY_BRACKET
		public RuleCall getCLOSING_CURLY_BRACKETTerminalRuleCall_2() { return cCLOSING_CURLY_BRACKETTerminalRuleCall_2; }
	}

	public class AttributeSetElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "AttributeSet");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cAttributesAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cAttributesAttributeParserRuleCall_0_0 = (RuleCall)cAttributesAssignment_0.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cCOMMATerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Assignment cAttributesAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cAttributesAttributeParserRuleCall_1_1_0 = (RuleCall)cAttributesAssignment_1_1.eContents().get(0);
		
		//AttributeSet:
		//
		//	attributes+=Attribute (COMMA attributes+=Attribute)*;
		public ParserRule getRule() { return rule; }

		//attributes+=Attribute (COMMA attributes+=Attribute)*
		public Group getGroup() { return cGroup; }

		//attributes+=Attribute
		public Assignment getAttributesAssignment_0() { return cAttributesAssignment_0; }

		//Attribute
		public RuleCall getAttributesAttributeParserRuleCall_0_0() { return cAttributesAttributeParserRuleCall_0_0; }

		//(COMMA attributes+=Attribute)*
		public Group getGroup_1() { return cGroup_1; }

		//COMMA
		public RuleCall getCOMMATerminalRuleCall_1_0() { return cCOMMATerminalRuleCall_1_0; }

		//attributes+=Attribute
		public Assignment getAttributesAssignment_1_1() { return cAttributesAssignment_1_1; }

		//Attribute
		public RuleCall getAttributesAttributeParserRuleCall_1_1_0() { return cAttributesAttributeParserRuleCall_1_1_0; }
	}

	public class AttributeElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Attribute");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cOptionalAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cOptionalOPTIONALTerminalRuleCall_0_0 = (RuleCall)cOptionalAssignment_0.eContents().get(0);
		private final Assignment cAssignmentAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cAssignmentAttributeAssignmentParserRuleCall_1_0 = (RuleCall)cAssignmentAssignment_1.eContents().get(0);
		
		//Attribute:
		//
		//	optional?=OPTIONAL? assignment=AttributeAssignment;
		public ParserRule getRule() { return rule; }

		//optional?=OPTIONAL? assignment=AttributeAssignment
		public Group getGroup() { return cGroup; }

		//optional?=OPTIONAL?
		public Assignment getOptionalAssignment_0() { return cOptionalAssignment_0; }

		//OPTIONAL
		public RuleCall getOptionalOPTIONALTerminalRuleCall_0_0() { return cOptionalOPTIONALTerminalRuleCall_0_0; }

		//assignment=AttributeAssignment
		public Assignment getAssignmentAssignment_1() { return cAssignmentAssignment_1; }

		//AttributeAssignment
		public RuleCall getAssignmentAttributeAssignmentParserRuleCall_1_0() { return cAssignmentAttributeAssignmentParserRuleCall_1_0; }
	}

	public class AttributeAssignmentElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "AttributeAssignment");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cConceptAssignmentParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cNumericalAssignmentParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cNumericalAssignmentGroupParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		//AttributeAssignment:
		//
		//	ConceptAssignment | NumericalAssignment | NumericalAssignmentGroup;
		public ParserRule getRule() { return rule; }

		//ConceptAssignment | NumericalAssignment | NumericalAssignmentGroup
		public Alternatives getAlternatives() { return cAlternatives; }

		//ConceptAssignment
		public RuleCall getConceptAssignmentParserRuleCall_0() { return cConceptAssignmentParserRuleCall_0; }

		//NumericalAssignment
		public RuleCall getNumericalAssignmentParserRuleCall_1() { return cNumericalAssignmentParserRuleCall_1; }

		//NumericalAssignmentGroup
		public RuleCall getNumericalAssignmentGroupParserRuleCall_2() { return cNumericalAssignmentGroupParserRuleCall_2; }
	}

	public class ConceptAssignmentElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "ConceptAssignment");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cNameAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cNameLValueParserRuleCall_0_0 = (RuleCall)cNameAssignment_0.eContents().get(0);
		private final RuleCall cEQUAL_SIGNTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueRValueParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//ConceptAssignment:
		//
		//	name=LValue EQUAL_SIGN value=RValue;
		public ParserRule getRule() { return rule; }

		//name=LValue EQUAL_SIGN value=RValue
		public Group getGroup() { return cGroup; }

		//name=LValue
		public Assignment getNameAssignment_0() { return cNameAssignment_0; }

		//LValue
		public RuleCall getNameLValueParserRuleCall_0_0() { return cNameLValueParserRuleCall_0_0; }

		//EQUAL_SIGN
		public RuleCall getEQUAL_SIGNTerminalRuleCall_1() { return cEQUAL_SIGNTerminalRuleCall_1; }

		//value=RValue
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }

		//RValue
		public RuleCall getValueRValueParserRuleCall_2_0() { return cValueRValueParserRuleCall_2_0; }
	}

	public class NumericalAssignmentElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "NumericalAssignment");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cNameAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cNameConceptParserRuleCall_0_0 = (RuleCall)cNameAssignment_0.eContents().get(0);
		private final Assignment cOperatorAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cOperatorOperatorParserRuleCall_1_0 = (RuleCall)cOperatorAssignment_1.eContents().get(0);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueDecimalNumberParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		private final Assignment cUnitAssignment_3 = (Assignment)cGroup.eContents().get(3);
		private final RuleCall cUnitUnitTypeParserRuleCall_3_0 = (RuleCall)cUnitAssignment_3.eContents().get(0);
		
		//NumericalAssignment:
		//
		//	name=Concept operator=Operator value=DecimalNumber unit=UnitType;
		public ParserRule getRule() { return rule; }

		//name=Concept operator=Operator value=DecimalNumber unit=UnitType
		public Group getGroup() { return cGroup; }

		//name=Concept
		public Assignment getNameAssignment_0() { return cNameAssignment_0; }

		//Concept
		public RuleCall getNameConceptParserRuleCall_0_0() { return cNameConceptParserRuleCall_0_0; }

		//operator=Operator
		public Assignment getOperatorAssignment_1() { return cOperatorAssignment_1; }

		//Operator
		public RuleCall getOperatorOperatorParserRuleCall_1_0() { return cOperatorOperatorParserRuleCall_1_0; }

		//value=DecimalNumber
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }

		//DecimalNumber
		public RuleCall getValueDecimalNumberParserRuleCall_2_0() { return cValueDecimalNumberParserRuleCall_2_0; }

		//unit=UnitType
		public Assignment getUnitAssignment_3() { return cUnitAssignment_3; }

		//UnitType
		public RuleCall getUnitUnitTypeParserRuleCall_3_0() { return cUnitUnitTypeParserRuleCall_3_0; }
	}

	public class NumericalAssignmentGroupElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "NumericalAssignmentGroup");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cOPENING_SQUARE_BRACKETTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cIngredientConceptAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cIngredientConceptConceptParserRuleCall_1_0 = (RuleCall)cIngredientConceptAssignment_1.eContents().get(0);
		private final RuleCall cEQUAL_SIGNTerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		private final Assignment cSubstanceAssignment_3 = (Assignment)cGroup.eContents().get(3);
		private final RuleCall cSubstanceRValueParserRuleCall_3_0 = (RuleCall)cSubstanceAssignment_3.eContents().get(0);
		private final RuleCall cCOMMATerminalRuleCall_4 = (RuleCall)cGroup.eContents().get(4);
		private final Assignment cNumericValueAssignment_5 = (Assignment)cGroup.eContents().get(5);
		private final RuleCall cNumericValueNumericalAssignmentParserRuleCall_5_0 = (RuleCall)cNumericValueAssignment_5.eContents().get(0);
		private final RuleCall cCLOSING_SQUARE_BRACKETTerminalRuleCall_6 = (RuleCall)cGroup.eContents().get(6);
		
		//NumericalAssignmentGroup:
		//
		//	OPENING_SQUARE_BRACKET ingredientConcept=Concept EQUAL_SIGN substance=RValue COMMA numericValue=NumericalAssignment
		//
		//	CLOSING_SQUARE_BRACKET;
		public ParserRule getRule() { return rule; }

		//OPENING_SQUARE_BRACKET ingredientConcept=Concept EQUAL_SIGN substance=RValue COMMA numericValue=NumericalAssignment
		//
		//CLOSING_SQUARE_BRACKET
		public Group getGroup() { return cGroup; }

		//OPENING_SQUARE_BRACKET
		public RuleCall getOPENING_SQUARE_BRACKETTerminalRuleCall_0() { return cOPENING_SQUARE_BRACKETTerminalRuleCall_0; }

		//ingredientConcept=Concept
		public Assignment getIngredientConceptAssignment_1() { return cIngredientConceptAssignment_1; }

		//Concept
		public RuleCall getIngredientConceptConceptParserRuleCall_1_0() { return cIngredientConceptConceptParserRuleCall_1_0; }

		//EQUAL_SIGN
		public RuleCall getEQUAL_SIGNTerminalRuleCall_2() { return cEQUAL_SIGNTerminalRuleCall_2; }

		//substance=RValue
		public Assignment getSubstanceAssignment_3() { return cSubstanceAssignment_3; }

		//RValue
		public RuleCall getSubstanceRValueParserRuleCall_3_0() { return cSubstanceRValueParserRuleCall_3_0; }

		//COMMA
		public RuleCall getCOMMATerminalRuleCall_4() { return cCOMMATerminalRuleCall_4; }

		//numericValue=NumericalAssignment
		public Assignment getNumericValueAssignment_5() { return cNumericValueAssignment_5; }

		//NumericalAssignment
		public RuleCall getNumericValueNumericalAssignmentParserRuleCall_5_0() { return cNumericValueNumericalAssignmentParserRuleCall_5_0; }

		//CLOSING_SQUARE_BRACKET
		public RuleCall getCLOSING_SQUARE_BRACKETTerminalRuleCall_6() { return cCLOSING_SQUARE_BRACKETTerminalRuleCall_6; }
	}

	public class RValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "RValue");
		private final RuleCall cOrParserRuleCall = (RuleCall)rule.eContents().get(1);
		
		//RValue:
		//
		//	Or;
		public ParserRule getRule() { return rule; }

		//Or
		public RuleCall getOrParserRuleCall() { return cOrParserRuleCall; }
	}

	public class OrElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Or");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cAndParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cOrLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final RuleCall cOR_TOKENTerminalRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightAndParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//Or returns RValue:
		//
		//	And ({Or.left=current} OR_TOKEN right=And)*;
		public ParserRule getRule() { return rule; }

		//And ({Or.left=current} OR_TOKEN right=And)*
		public Group getGroup() { return cGroup; }

		//And
		public RuleCall getAndParserRuleCall_0() { return cAndParserRuleCall_0; }

		//({Or.left=current} OR_TOKEN right=And)*
		public Group getGroup_1() { return cGroup_1; }

		//{Or.left=current}
		public Action getOrLeftAction_1_0() { return cOrLeftAction_1_0; }

		//OR_TOKEN
		public RuleCall getOR_TOKENTerminalRuleCall_1_1() { return cOR_TOKENTerminalRuleCall_1_1; }

		//right=And
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }

		//And
		public RuleCall getRightAndParserRuleCall_1_2_0() { return cRightAndParserRuleCall_1_2_0; }
	}

	public class AndElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "And");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cTerminalRValueParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cAndLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final RuleCall cAND_TOKENTerminalRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightTerminalRValueParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//And returns RValue:
		//
		//	TerminalRValue ({And.left=current} AND_TOKEN right=TerminalRValue)*;
		public ParserRule getRule() { return rule; }

		//TerminalRValue ({And.left=current} AND_TOKEN right=TerminalRValue)*
		public Group getGroup() { return cGroup; }

		//TerminalRValue
		public RuleCall getTerminalRValueParserRuleCall_0() { return cTerminalRValueParserRuleCall_0; }

		//({And.left=current} AND_TOKEN right=TerminalRValue)*
		public Group getGroup_1() { return cGroup_1; }

		//{And.left=current}
		public Action getAndLeftAction_1_0() { return cAndLeftAction_1_0; }

		//AND_TOKEN
		public RuleCall getAND_TOKENTerminalRuleCall_1_1() { return cAND_TOKENTerminalRuleCall_1_1; }

		//right=TerminalRValue
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }

		//TerminalRValue
		public RuleCall getRightTerminalRValueParserRuleCall_1_2_0() { return cRightTerminalRValueParserRuleCall_1_2_0; }
	}

	public class NegatableSubExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "NegatableSubExpression");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cNegatedAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cNegatedNOT_TOKENTerminalRuleCall_0_0 = (RuleCall)cNegatedAssignment_0.eContents().get(0);
		private final RuleCall cOPENING_ROUND_BRACKETTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cExpressionAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cExpressionExpressionParserRuleCall_2_0 = (RuleCall)cExpressionAssignment_2.eContents().get(0);
		private final RuleCall cCLOSING_ROUND_BRACKETTerminalRuleCall_3 = (RuleCall)cGroup.eContents().get(3);
		
		//NegatableSubExpression:
		//
		//	negated?=NOT_TOKEN? OPENING_ROUND_BRACKET expression=Expression CLOSING_ROUND_BRACKET;
		public ParserRule getRule() { return rule; }

		//negated?=NOT_TOKEN? OPENING_ROUND_BRACKET expression=Expression CLOSING_ROUND_BRACKET
		public Group getGroup() { return cGroup; }

		//negated?=NOT_TOKEN?
		public Assignment getNegatedAssignment_0() { return cNegatedAssignment_0; }

		//NOT_TOKEN
		public RuleCall getNegatedNOT_TOKENTerminalRuleCall_0_0() { return cNegatedNOT_TOKENTerminalRuleCall_0_0; }

		//OPENING_ROUND_BRACKET
		public RuleCall getOPENING_ROUND_BRACKETTerminalRuleCall_1() { return cOPENING_ROUND_BRACKETTerminalRuleCall_1; }

		//expression=Expression
		public Assignment getExpressionAssignment_2() { return cExpressionAssignment_2; }

		//Expression
		public RuleCall getExpressionExpressionParserRuleCall_2_0() { return cExpressionExpressionParserRuleCall_2_0; }

		//CLOSING_ROUND_BRACKET
		public RuleCall getCLOSING_ROUND_BRACKETTerminalRuleCall_3() { return cCLOSING_ROUND_BRACKETTerminalRuleCall_3; }
	}

	public class TerminalRValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "TerminalRValue");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final Group cGroup_0 = (Group)cAlternatives.eContents().get(0);
		private final RuleCall cOPENING_ROUND_BRACKETTerminalRuleCall_0_0 = (RuleCall)cGroup_0.eContents().get(0);
		private final RuleCall cRValueParserRuleCall_0_1 = (RuleCall)cGroup_0.eContents().get(1);
		private final RuleCall cCLOSING_ROUND_BRACKETTerminalRuleCall_0_2 = (RuleCall)cGroup_0.eContents().get(2);
		private final RuleCall cNegatableSubExpressionParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cLValueParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		//TerminalRValue returns RValue:
		//
		//	OPENING_ROUND_BRACKET RValue CLOSING_ROUND_BRACKET | NegatableSubExpression | LValue;
		public ParserRule getRule() { return rule; }

		//OPENING_ROUND_BRACKET RValue CLOSING_ROUND_BRACKET | NegatableSubExpression | LValue
		public Alternatives getAlternatives() { return cAlternatives; }

		//OPENING_ROUND_BRACKET RValue CLOSING_ROUND_BRACKET
		public Group getGroup_0() { return cGroup_0; }

		//OPENING_ROUND_BRACKET
		public RuleCall getOPENING_ROUND_BRACKETTerminalRuleCall_0_0() { return cOPENING_ROUND_BRACKETTerminalRuleCall_0_0; }

		//RValue
		public RuleCall getRValueParserRuleCall_0_1() { return cRValueParserRuleCall_0_1; }

		//CLOSING_ROUND_BRACKET
		public RuleCall getCLOSING_ROUND_BRACKETTerminalRuleCall_0_2() { return cCLOSING_ROUND_BRACKETTerminalRuleCall_0_2; }

		//NegatableSubExpression
		public RuleCall getNegatableSubExpressionParserRuleCall_1() { return cNegatableSubExpressionParserRuleCall_1; }

		//LValue
		public RuleCall getLValueParserRuleCall_2() { return cLValueParserRuleCall_2; }
	}

	public class TermElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Term");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cTermCharacterParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cWSTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final RuleCall cTermCharacterParserRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		
		//Term hidden():
		//
		//	TermCharacter+ (WS+ TermCharacter+)*;
		public ParserRule getRule() { return rule; }

		//TermCharacter+ (WS+ TermCharacter+)*
		public Group getGroup() { return cGroup; }

		//TermCharacter+
		public RuleCall getTermCharacterParserRuleCall_0() { return cTermCharacterParserRuleCall_0; }

		//(WS+ TermCharacter+)*
		public Group getGroup_1() { return cGroup_1; }

		//WS+
		public RuleCall getWSTerminalRuleCall_1_0() { return cWSTerminalRuleCall_1_0; }

		//TermCharacter+
		public RuleCall getTermCharacterParserRuleCall_1_1() { return cTermCharacterParserRuleCall_1_1; }
	}

	public class ConceptIdElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "ConceptId");
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
		
		//ConceptId hidden():
		//
		//	DIGIT_NONZERO (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO)
		//
		//	(DIGIT_NONZERO | ZERO)+;
		public ParserRule getRule() { return rule; }

		//DIGIT_NONZERO (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO
		//
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

	public class TermCharacterElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "TermCharacter");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cLETTERTerminalRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cSUBTYPETerminalRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		private final RuleCall cINCLUSIVE_SUBTYPETerminalRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		private final RuleCall cCOMMATerminalRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		private final RuleCall cCARETTerminalRuleCall_6 = (RuleCall)cAlternatives.eContents().get(6);
		private final RuleCall cNOT_TOKENTerminalRuleCall_7 = (RuleCall)cAlternatives.eContents().get(7);
		private final RuleCall cOPTIONALTerminalRuleCall_8 = (RuleCall)cAlternatives.eContents().get(8);
		private final RuleCall cOPENING_CURLY_BRACKETTerminalRuleCall_9 = (RuleCall)cAlternatives.eContents().get(9);
		private final RuleCall cCLOSING_CURLY_BRACKETTerminalRuleCall_10 = (RuleCall)cAlternatives.eContents().get(10);
		private final RuleCall cEQUAL_SIGNTerminalRuleCall_11 = (RuleCall)cAlternatives.eContents().get(11);
		private final RuleCall cOPENING_ROUND_BRACKETTerminalRuleCall_12 = (RuleCall)cAlternatives.eContents().get(12);
		private final RuleCall cCLOSING_ROUND_BRACKETTerminalRuleCall_13 = (RuleCall)cAlternatives.eContents().get(13);
		private final RuleCall cPLUS_SIGNTerminalRuleCall_14 = (RuleCall)cAlternatives.eContents().get(14);
		private final RuleCall cCOLONTerminalRuleCall_15 = (RuleCall)cAlternatives.eContents().get(15);
		private final RuleCall cPERIODTerminalRuleCall_16 = (RuleCall)cAlternatives.eContents().get(16);
		private final RuleCall cUnitTypeParserRuleCall_17 = (RuleCall)cAlternatives.eContents().get(17);
		private final RuleCall cAND_TOKENTerminalRuleCall_18 = (RuleCall)cAlternatives.eContents().get(18);
		private final RuleCall cOR_TOKENTerminalRuleCall_19 = (RuleCall)cAlternatives.eContents().get(19);
		private final RuleCall cOPENING_SQUARE_BRACKETTerminalRuleCall_20 = (RuleCall)cAlternatives.eContents().get(20);
		private final RuleCall cCLOSING_SQUARE_BRACKETTerminalRuleCall_21 = (RuleCall)cAlternatives.eContents().get(21);
		private final RuleCall cOTHER_ALLOWED_TERM_CHARACTERTerminalRuleCall_22 = (RuleCall)cAlternatives.eContents().get(22);
		
		//TermCharacter hidden():
		//
		//	DIGIT_NONZERO | ZERO | LETTER | SUBTYPE | INCLUSIVE_SUBTYPE | COMMA | CARET | NOT_TOKEN | OPTIONAL |
		//
		//	OPENING_CURLY_BRACKET | CLOSING_CURLY_BRACKET | EQUAL_SIGN | OPENING_ROUND_BRACKET | CLOSING_ROUND_BRACKET |
		//
		//	PLUS_SIGN | COLON | PERIOD | UnitType | AND_TOKEN | OR_TOKEN | OPENING_SQUARE_BRACKET | CLOSING_SQUARE_BRACKET |
		//
		//	OTHER_ALLOWED_TERM_CHARACTER;
		public ParserRule getRule() { return rule; }

		//DIGIT_NONZERO | ZERO | LETTER | SUBTYPE | INCLUSIVE_SUBTYPE | COMMA | CARET | NOT_TOKEN | OPTIONAL |
		//
		//OPENING_CURLY_BRACKET | CLOSING_CURLY_BRACKET | EQUAL_SIGN | OPENING_ROUND_BRACKET | CLOSING_ROUND_BRACKET | PLUS_SIGN
		//
		//| COLON | PERIOD | UnitType | AND_TOKEN | OR_TOKEN | OPENING_SQUARE_BRACKET | CLOSING_SQUARE_BRACKET |
		//
		//OTHER_ALLOWED_TERM_CHARACTER
		public Alternatives getAlternatives() { return cAlternatives; }

		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_0() { return cDIGIT_NONZEROTerminalRuleCall_0; }

		//ZERO
		public RuleCall getZEROTerminalRuleCall_1() { return cZEROTerminalRuleCall_1; }

		//LETTER
		public RuleCall getLETTERTerminalRuleCall_2() { return cLETTERTerminalRuleCall_2; }

		//SUBTYPE
		public RuleCall getSUBTYPETerminalRuleCall_3() { return cSUBTYPETerminalRuleCall_3; }

		//INCLUSIVE_SUBTYPE
		public RuleCall getINCLUSIVE_SUBTYPETerminalRuleCall_4() { return cINCLUSIVE_SUBTYPETerminalRuleCall_4; }

		//COMMA
		public RuleCall getCOMMATerminalRuleCall_5() { return cCOMMATerminalRuleCall_5; }

		//CARET
		public RuleCall getCARETTerminalRuleCall_6() { return cCARETTerminalRuleCall_6; }

		//NOT_TOKEN
		public RuleCall getNOT_TOKENTerminalRuleCall_7() { return cNOT_TOKENTerminalRuleCall_7; }

		//OPTIONAL
		public RuleCall getOPTIONALTerminalRuleCall_8() { return cOPTIONALTerminalRuleCall_8; }

		//OPENING_CURLY_BRACKET
		public RuleCall getOPENING_CURLY_BRACKETTerminalRuleCall_9() { return cOPENING_CURLY_BRACKETTerminalRuleCall_9; }

		//CLOSING_CURLY_BRACKET
		public RuleCall getCLOSING_CURLY_BRACKETTerminalRuleCall_10() { return cCLOSING_CURLY_BRACKETTerminalRuleCall_10; }

		//EQUAL_SIGN
		public RuleCall getEQUAL_SIGNTerminalRuleCall_11() { return cEQUAL_SIGNTerminalRuleCall_11; }

		//OPENING_ROUND_BRACKET
		public RuleCall getOPENING_ROUND_BRACKETTerminalRuleCall_12() { return cOPENING_ROUND_BRACKETTerminalRuleCall_12; }

		//CLOSING_ROUND_BRACKET
		public RuleCall getCLOSING_ROUND_BRACKETTerminalRuleCall_13() { return cCLOSING_ROUND_BRACKETTerminalRuleCall_13; }

		//PLUS_SIGN
		public RuleCall getPLUS_SIGNTerminalRuleCall_14() { return cPLUS_SIGNTerminalRuleCall_14; }

		//COLON
		public RuleCall getCOLONTerminalRuleCall_15() { return cCOLONTerminalRuleCall_15; }

		//PERIOD
		public RuleCall getPERIODTerminalRuleCall_16() { return cPERIODTerminalRuleCall_16; }

		//UnitType
		public RuleCall getUnitTypeParserRuleCall_17() { return cUnitTypeParserRuleCall_17; }

		//AND_TOKEN
		public RuleCall getAND_TOKENTerminalRuleCall_18() { return cAND_TOKENTerminalRuleCall_18; }

		//OR_TOKEN
		public RuleCall getOR_TOKENTerminalRuleCall_19() { return cOR_TOKENTerminalRuleCall_19; }

		//OPENING_SQUARE_BRACKET
		public RuleCall getOPENING_SQUARE_BRACKETTerminalRuleCall_20() { return cOPENING_SQUARE_BRACKETTerminalRuleCall_20; }

		//CLOSING_SQUARE_BRACKET
		public RuleCall getCLOSING_SQUARE_BRACKETTerminalRuleCall_21() { return cCLOSING_SQUARE_BRACKETTerminalRuleCall_21; }

		//OTHER_ALLOWED_TERM_CHARACTER
		public RuleCall getOTHER_ALLOWED_TERM_CHARACTERTerminalRuleCall_22() { return cOTHER_ALLOWED_TERM_CHARACTERTerminalRuleCall_22; }
	}

	public class IntegerElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Integer");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cZEROTerminalRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Alternatives cAlternatives_1_1 = (Alternatives)cGroup_1.eContents().get(1);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_1_1_0 = (RuleCall)cAlternatives_1_1.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_1_1_1 = (RuleCall)cAlternatives_1_1.eContents().get(1);
		
		//Integer returns ecore::EInt hidden():
		//
		//	ZERO | DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*;
		public ParserRule getRule() { return rule; }

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

	public class DecimalNumberElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "DecimalNumber");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Alternatives cAlternatives_0 = (Alternatives)cGroup.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_0_0 = (RuleCall)cAlternatives_0.eContents().get(0);
		private final Group cGroup_0_1 = (Group)cAlternatives_0.eContents().get(1);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_0_1_0 = (RuleCall)cGroup_0_1.eContents().get(0);
		private final Alternatives cAlternatives_0_1_1 = (Alternatives)cGroup_0_1.eContents().get(1);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_0_1_1_0 = (RuleCall)cAlternatives_0_1_1.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_0_1_1_1 = (RuleCall)cAlternatives_0_1_1.eContents().get(1);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cPERIODTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Alternatives cAlternatives_1_1 = (Alternatives)cGroup_1.eContents().get(1);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_1_1_0 = (RuleCall)cAlternatives_1_1.eContents().get(0);
		private final RuleCall cZEROTerminalRuleCall_1_1_1 = (RuleCall)cAlternatives_1_1.eContents().get(1);
		
		//DecimalNumber returns ecore::EBigDecimal hidden():
		//
		//	(ZERO | DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*) (PERIOD (DIGIT_NONZERO | ZERO)+)?;
		public ParserRule getRule() { return rule; }

		//(ZERO | DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*) (PERIOD (DIGIT_NONZERO | ZERO)+)?
		public Group getGroup() { return cGroup; }

		//ZERO | DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*
		public Alternatives getAlternatives_0() { return cAlternatives_0; }

		//ZERO
		public RuleCall getZEROTerminalRuleCall_0_0() { return cZEROTerminalRuleCall_0_0; }

		//DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*
		public Group getGroup_0_1() { return cGroup_0_1; }

		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_0_1_0() { return cDIGIT_NONZEROTerminalRuleCall_0_1_0; }

		//(DIGIT_NONZERO | ZERO)*
		public Alternatives getAlternatives_0_1_1() { return cAlternatives_0_1_1; }

		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_0_1_1_0() { return cDIGIT_NONZEROTerminalRuleCall_0_1_1_0; }

		//ZERO
		public RuleCall getZEROTerminalRuleCall_0_1_1_1() { return cZEROTerminalRuleCall_0_1_1_1; }

		//(PERIOD (DIGIT_NONZERO | ZERO)+)?
		public Group getGroup_1() { return cGroup_1; }

		//PERIOD
		public RuleCall getPERIODTerminalRuleCall_1_0() { return cPERIODTerminalRuleCall_1_0; }

		//(DIGIT_NONZERO | ZERO)+
		public Alternatives getAlternatives_1_1() { return cAlternatives_1_1; }

		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_1_1_0() { return cDIGIT_NONZEROTerminalRuleCall_1_1_0; }

		//ZERO
		public RuleCall getZEROTerminalRuleCall_1_1_1() { return cZEROTerminalRuleCall_1_1_1; }
	}

	public class OperatorElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Operator");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cEQUALS_OPERATORTerminalRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cSUBTYPETerminalRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cGREATER_THAN_OPERATORTerminalRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cLESS_EQUALS_OPERATORTerminalRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		private final RuleCall cGREATER_EQUALS_OPERATORTerminalRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		private final RuleCall cNOT_EQUALS_OPERATORTerminalRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		
		//Operator hidden():
		//
		//	EQUALS_OPERATOR | SUBTYPE | GREATER_THAN_OPERATOR | LESS_EQUALS_OPERATOR | GREATER_EQUALS_OPERATOR |
		//
		//	NOT_EQUALS_OPERATOR;
		public ParserRule getRule() { return rule; }

		//EQUALS_OPERATOR | SUBTYPE | GREATER_THAN_OPERATOR | LESS_EQUALS_OPERATOR | GREATER_EQUALS_OPERATOR | NOT_EQUALS_OPERATOR
		public Alternatives getAlternatives() { return cAlternatives; }

		//EQUALS_OPERATOR
		public RuleCall getEQUALS_OPERATORTerminalRuleCall_0() { return cEQUALS_OPERATORTerminalRuleCall_0; }

		//SUBTYPE
		public RuleCall getSUBTYPETerminalRuleCall_1() { return cSUBTYPETerminalRuleCall_1; }

		//GREATER_THAN_OPERATOR
		public RuleCall getGREATER_THAN_OPERATORTerminalRuleCall_2() { return cGREATER_THAN_OPERATORTerminalRuleCall_2; }

		//LESS_EQUALS_OPERATOR
		public RuleCall getLESS_EQUALS_OPERATORTerminalRuleCall_3() { return cLESS_EQUALS_OPERATORTerminalRuleCall_3; }

		//GREATER_EQUALS_OPERATOR
		public RuleCall getGREATER_EQUALS_OPERATORTerminalRuleCall_4() { return cGREATER_EQUALS_OPERATORTerminalRuleCall_4; }

		//NOT_EQUALS_OPERATOR
		public RuleCall getNOT_EQUALS_OPERATORTerminalRuleCall_5() { return cNOT_EQUALS_OPERATORTerminalRuleCall_5; }
	}

	public class UnitTypeElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "UnitType");
		private final Keyword cMgKeyword = (Keyword)rule.eContents().get(1);
		
		//UnitType hidden():
		//
		//	"mg";
		public ParserRule getRule() { return rule; }

		//"mg"
		public Keyword getMgKeyword() { return cMgKeyword; }
	}
	
	
	private ExpressionElements pExpression;
	private SubExpressionElements pSubExpression;
	private LValueElements pLValue;
	private RefSetElements pRefSet;
	private ConceptGroupElements pConceptGroup;
	private ConceptElements pConcept;
	private RefinementsElements pRefinements;
	private AttributeGroupElements pAttributeGroup;
	private AttributeSetElements pAttributeSet;
	private AttributeElements pAttribute;
	private AttributeAssignmentElements pAttributeAssignment;
	private ConceptAssignmentElements pConceptAssignment;
	private NumericalAssignmentElements pNumericalAssignment;
	private NumericalAssignmentGroupElements pNumericalAssignmentGroup;
	private RValueElements pRValue;
	private OrElements pOr;
	private AndElements pAnd;
	private NegatableSubExpressionElements pNegatableSubExpression;
	private TerminalRValueElements pTerminalRValue;
	private TermElements pTerm;
	private ConceptIdElements pConceptId;
	private TermCharacterElements pTermCharacter;
	private IntegerElements pInteger;
	private DecimalNumberElements pDecimalNumber;
	private OperatorElements pOperator;
	private UnitTypeElements pUnitType;
	private TerminalRule tZERO;
	private TerminalRule tDIGIT_NONZERO;
	private TerminalRule tLETTER;
	private TerminalRule tSUBTYPE;
	private TerminalRule tINCLUSIVE_SUBTYPE;
	private TerminalRule tEQUALS_OPERATOR;
	private TerminalRule tGREATER_THAN_OPERATOR;
	private TerminalRule tLESS_EQUALS_OPERATOR;
	private TerminalRule tGREATER_EQUALS_OPERATOR;
	private TerminalRule tNOT_EQUALS_OPERATOR;
	private TerminalRule tWS;
	private TerminalRule tML_COMMENT;
	private TerminalRule tSL_COMMENT;
	private TerminalRule tPIPE;
	private TerminalRule tCOLON;
	private TerminalRule tOPENING_CURLY_BRACKET;
	private TerminalRule tCLOSING_CURLY_BRACKET;
	private TerminalRule tEQUAL_SIGN;
	private TerminalRule tCOMMA;
	private TerminalRule tOPENING_ROUND_BRACKET;
	private TerminalRule tCLOSING_ROUND_BRACKET;
	private TerminalRule tOPENING_SQUARE_BRACKET;
	private TerminalRule tCLOSING_SQUARE_BRACKET;
	private TerminalRule tPLUS_SIGN;
	private TerminalRule tCARET;
	private TerminalRule tNOT_TOKEN;
	private TerminalRule tOPTIONAL;
	private TerminalRule tPERIOD;
	private TerminalRule tOTHER_ALLOWED_TERM_CHARACTER;
	private TerminalRule tAND_TOKEN;
	private TerminalRule tOR_TOKEN;
	private TerminalRule tUNION_TOKEN;
	
	private final Grammar grammar;

	@Inject
	public ESCGGrammarAccess(GrammarProvider grammarProvider) {
		this.grammar = internalFindGrammar(grammarProvider);
	}
	
	protected Grammar internalFindGrammar(GrammarProvider grammarProvider) {
		Grammar grammar = grammarProvider.getGrammar(this);
		while (grammar != null) {
			if ("com.b2international.snowowl.dsl.ESCG".equals(grammar.getName())) {
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
	
	
	public Grammar getGrammar() {
		return grammar;
	}
	

	
	//// parser rules
	// Expression hidden(WS, SL_COMMENT, ML_COMMENT):
	//
	//	(subExpression+=SubExpression (UNION_TOKEN subExpression+=SubExpression)*)?;
	public ExpressionElements getExpressionAccess() {
		return (pExpression != null) ? pExpression : (pExpression = new ExpressionElements());
	}
	
	public ParserRule getExpressionRule() {
		return getExpressionAccess().getRule();
	}

	//SubExpression:
	//
	//	lValues+=LValue (PLUS_SIGN lValues+=LValue)* (COLON refinements=Refinements)?;
	public SubExpressionElements getSubExpressionAccess() {
		return (pSubExpression != null) ? pSubExpression : (pSubExpression = new SubExpressionElements());
	}
	
	public ParserRule getSubExpressionRule() {
		return getSubExpressionAccess().getRule();
	}

	//LValue:
	//
	//	ConceptGroup | RefSet;
	public LValueElements getLValueAccess() {
		return (pLValue != null) ? pLValue : (pLValue = new LValueElements());
	}
	
	public ParserRule getLValueRule() {
		return getLValueAccess().getRule();
	}

	//RefSet:
	//
	//	negated?=NOT_TOKEN? CARET id=ConceptId (PIPE term=Term PIPE)?;
	public RefSetElements getRefSetAccess() {
		return (pRefSet != null) ? pRefSet : (pRefSet = new RefSetElements());
	}
	
	public ParserRule getRefSetRule() {
		return getRefSetAccess().getRule();
	}

	////  ! << 1234567|Left hand|
	// ConceptGroup:
	//
	//	negated?=NOT_TOKEN? constraint=(SUBTYPE | INCLUSIVE_SUBTYPE)? concept=Concept;
	public ConceptGroupElements getConceptGroupAccess() {
		return (pConceptGroup != null) ? pConceptGroup : (pConceptGroup = new ConceptGroupElements());
	}
	
	public ParserRule getConceptGroupRule() {
		return getConceptGroupAccess().getRule();
	}

	////  1234567|Left hand|
	// Concept:
	//
	//	id=ConceptId (PIPE WS* term=Term WS* PIPE)?;
	public ConceptElements getConceptAccess() {
		return (pConcept != null) ? pConcept : (pConcept = new ConceptElements());
	}
	
	public ParserRule getConceptRule() {
		return getConceptAccess().getRule();
	}

	//Refinements:
	//
	//	attributeSet=AttributeSet attributeGroups+=AttributeGroup* | attributeGroups+=AttributeGroup+;
	public RefinementsElements getRefinementsAccess() {
		return (pRefinements != null) ? pRefinements : (pRefinements = new RefinementsElements());
	}
	
	public ParserRule getRefinementsRule() {
		return getRefinementsAccess().getRule();
	}

	//AttributeGroup:
	//
	//	OPENING_CURLY_BRACKET AttributeSet CLOSING_CURLY_BRACKET;
	public AttributeGroupElements getAttributeGroupAccess() {
		return (pAttributeGroup != null) ? pAttributeGroup : (pAttributeGroup = new AttributeGroupElements());
	}
	
	public ParserRule getAttributeGroupRule() {
		return getAttributeGroupAccess().getRule();
	}

	//AttributeSet:
	//
	//	attributes+=Attribute (COMMA attributes+=Attribute)*;
	public AttributeSetElements getAttributeSetAccess() {
		return (pAttributeSet != null) ? pAttributeSet : (pAttributeSet = new AttributeSetElements());
	}
	
	public ParserRule getAttributeSetRule() {
		return getAttributeSetAccess().getRule();
	}

	//Attribute:
	//
	//	optional?=OPTIONAL? assignment=AttributeAssignment;
	public AttributeElements getAttributeAccess() {
		return (pAttribute != null) ? pAttribute : (pAttribute = new AttributeElements());
	}
	
	public ParserRule getAttributeRule() {
		return getAttributeAccess().getRule();
	}

	//AttributeAssignment:
	//
	//	ConceptAssignment | NumericalAssignment | NumericalAssignmentGroup;
	public AttributeAssignmentElements getAttributeAssignmentAccess() {
		return (pAttributeAssignment != null) ? pAttributeAssignment : (pAttributeAssignment = new AttributeAssignmentElements());
	}
	
	public ParserRule getAttributeAssignmentRule() {
		return getAttributeAssignmentAccess().getRule();
	}

	//ConceptAssignment:
	//
	//	name=LValue EQUAL_SIGN value=RValue;
	public ConceptAssignmentElements getConceptAssignmentAccess() {
		return (pConceptAssignment != null) ? pConceptAssignment : (pConceptAssignment = new ConceptAssignmentElements());
	}
	
	public ParserRule getConceptAssignmentRule() {
		return getConceptAssignmentAccess().getRule();
	}

	//NumericalAssignment:
	//
	//	name=Concept operator=Operator value=DecimalNumber unit=UnitType;
	public NumericalAssignmentElements getNumericalAssignmentAccess() {
		return (pNumericalAssignment != null) ? pNumericalAssignment : (pNumericalAssignment = new NumericalAssignmentElements());
	}
	
	public ParserRule getNumericalAssignmentRule() {
		return getNumericalAssignmentAccess().getRule();
	}

	//NumericalAssignmentGroup:
	//
	//	OPENING_SQUARE_BRACKET ingredientConcept=Concept EQUAL_SIGN substance=RValue COMMA numericValue=NumericalAssignment
	//
	//	CLOSING_SQUARE_BRACKET;
	public NumericalAssignmentGroupElements getNumericalAssignmentGroupAccess() {
		return (pNumericalAssignmentGroup != null) ? pNumericalAssignmentGroup : (pNumericalAssignmentGroup = new NumericalAssignmentGroupElements());
	}
	
	public ParserRule getNumericalAssignmentGroupRule() {
		return getNumericalAssignmentGroupAccess().getRule();
	}

	//RValue:
	//
	//	Or;
	public RValueElements getRValueAccess() {
		return (pRValue != null) ? pRValue : (pRValue = new RValueElements());
	}
	
	public ParserRule getRValueRule() {
		return getRValueAccess().getRule();
	}

	//Or returns RValue:
	//
	//	And ({Or.left=current} OR_TOKEN right=And)*;
	public OrElements getOrAccess() {
		return (pOr != null) ? pOr : (pOr = new OrElements());
	}
	
	public ParserRule getOrRule() {
		return getOrAccess().getRule();
	}

	//And returns RValue:
	//
	//	TerminalRValue ({And.left=current} AND_TOKEN right=TerminalRValue)*;
	public AndElements getAndAccess() {
		return (pAnd != null) ? pAnd : (pAnd = new AndElements());
	}
	
	public ParserRule getAndRule() {
		return getAndAccess().getRule();
	}

	//NegatableSubExpression:
	//
	//	negated?=NOT_TOKEN? OPENING_ROUND_BRACKET expression=Expression CLOSING_ROUND_BRACKET;
	public NegatableSubExpressionElements getNegatableSubExpressionAccess() {
		return (pNegatableSubExpression != null) ? pNegatableSubExpression : (pNegatableSubExpression = new NegatableSubExpressionElements());
	}
	
	public ParserRule getNegatableSubExpressionRule() {
		return getNegatableSubExpressionAccess().getRule();
	}

	//TerminalRValue returns RValue:
	//
	//	OPENING_ROUND_BRACKET RValue CLOSING_ROUND_BRACKET | NegatableSubExpression | LValue;
	public TerminalRValueElements getTerminalRValueAccess() {
		return (pTerminalRValue != null) ? pTerminalRValue : (pTerminalRValue = new TerminalRValueElements());
	}
	
	public ParserRule getTerminalRValueRule() {
		return getTerminalRValueAccess().getRule();
	}

	//Term hidden():
	//
	//	TermCharacter+ (WS+ TermCharacter+)*;
	public TermElements getTermAccess() {
		return (pTerm != null) ? pTerm : (pTerm = new TermElements());
	}
	
	public ParserRule getTermRule() {
		return getTermAccess().getRule();
	}

	//ConceptId hidden():
	//
	//	DIGIT_NONZERO (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO)
	//
	//	(DIGIT_NONZERO | ZERO)+;
	public ConceptIdElements getConceptIdAccess() {
		return (pConceptId != null) ? pConceptId : (pConceptId = new ConceptIdElements());
	}
	
	public ParserRule getConceptIdRule() {
		return getConceptIdAccess().getRule();
	}

	//TermCharacter hidden():
	//
	//	DIGIT_NONZERO | ZERO | LETTER | SUBTYPE | INCLUSIVE_SUBTYPE | COMMA | CARET | NOT_TOKEN | OPTIONAL |
	//
	//	OPENING_CURLY_BRACKET | CLOSING_CURLY_BRACKET | EQUAL_SIGN | OPENING_ROUND_BRACKET | CLOSING_ROUND_BRACKET |
	//
	//	PLUS_SIGN | COLON | PERIOD | UnitType | AND_TOKEN | OR_TOKEN | OPENING_SQUARE_BRACKET | CLOSING_SQUARE_BRACKET |
	//
	//	OTHER_ALLOWED_TERM_CHARACTER;
	public TermCharacterElements getTermCharacterAccess() {
		return (pTermCharacter != null) ? pTermCharacter : (pTermCharacter = new TermCharacterElements());
	}
	
	public ParserRule getTermCharacterRule() {
		return getTermCharacterAccess().getRule();
	}

	//Integer returns ecore::EInt hidden():
	//
	//	ZERO | DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*;
	public IntegerElements getIntegerAccess() {
		return (pInteger != null) ? pInteger : (pInteger = new IntegerElements());
	}
	
	public ParserRule getIntegerRule() {
		return getIntegerAccess().getRule();
	}

	//DecimalNumber returns ecore::EBigDecimal hidden():
	//
	//	(ZERO | DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*) (PERIOD (DIGIT_NONZERO | ZERO)+)?;
	public DecimalNumberElements getDecimalNumberAccess() {
		return (pDecimalNumber != null) ? pDecimalNumber : (pDecimalNumber = new DecimalNumberElements());
	}
	
	public ParserRule getDecimalNumberRule() {
		return getDecimalNumberAccess().getRule();
	}

	//Operator hidden():
	//
	//	EQUALS_OPERATOR | SUBTYPE | GREATER_THAN_OPERATOR | LESS_EQUALS_OPERATOR | GREATER_EQUALS_OPERATOR |
	//
	//	NOT_EQUALS_OPERATOR;
	public OperatorElements getOperatorAccess() {
		return (pOperator != null) ? pOperator : (pOperator = new OperatorElements());
	}
	
	public ParserRule getOperatorRule() {
		return getOperatorAccess().getRule();
	}

	//UnitType hidden():
	//
	//	"mg";
	public UnitTypeElements getUnitTypeAccess() {
		return (pUnitType != null) ? pUnitType : (pUnitType = new UnitTypeElements());
	}
	
	public ParserRule getUnitTypeRule() {
		return getUnitTypeAccess().getRule();
	}

	//terminal ZERO:
	//
	//	"0";
	public TerminalRule getZERORule() {
		return (tZERO != null) ? tZERO : (tZERO = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "ZERO"));
	} 

	//terminal DIGIT_NONZERO:
	//
	//	"1".."9";
	public TerminalRule getDIGIT_NONZERORule() {
		return (tDIGIT_NONZERO != null) ? tDIGIT_NONZERO : (tDIGIT_NONZERO = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "DIGIT_NONZERO"));
	} 

	//terminal LETTER:
	//
	//	"a".."z" | "A".."Z";
	public TerminalRule getLETTERRule() {
		return (tLETTER != null) ? tLETTER : (tLETTER = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "LETTER"));
	} 

	//terminal SUBTYPE:
	//
	//	"<";
	public TerminalRule getSUBTYPERule() {
		return (tSUBTYPE != null) ? tSUBTYPE : (tSUBTYPE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "SUBTYPE"));
	} 

	//terminal INCLUSIVE_SUBTYPE:
	//
	//	"<<";
	public TerminalRule getINCLUSIVE_SUBTYPERule() {
		return (tINCLUSIVE_SUBTYPE != null) ? tINCLUSIVE_SUBTYPE : (tINCLUSIVE_SUBTYPE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "INCLUSIVE_SUBTYPE"));
	} 

	//terminal EQUALS_OPERATOR:
	//
	//	"==";
	public TerminalRule getEQUALS_OPERATORRule() {
		return (tEQUALS_OPERATOR != null) ? tEQUALS_OPERATOR : (tEQUALS_OPERATOR = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "EQUALS_OPERATOR"));
	} 

	//terminal GREATER_THAN_OPERATOR:
	//
	//	">";
	public TerminalRule getGREATER_THAN_OPERATORRule() {
		return (tGREATER_THAN_OPERATOR != null) ? tGREATER_THAN_OPERATOR : (tGREATER_THAN_OPERATOR = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "GREATER_THAN_OPERATOR"));
	} 

	//terminal LESS_EQUALS_OPERATOR:
	//
	//	"<=";
	public TerminalRule getLESS_EQUALS_OPERATORRule() {
		return (tLESS_EQUALS_OPERATOR != null) ? tLESS_EQUALS_OPERATOR : (tLESS_EQUALS_OPERATOR = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "LESS_EQUALS_OPERATOR"));
	} 

	//terminal GREATER_EQUALS_OPERATOR:
	//
	//	">=";
	public TerminalRule getGREATER_EQUALS_OPERATORRule() {
		return (tGREATER_EQUALS_OPERATOR != null) ? tGREATER_EQUALS_OPERATOR : (tGREATER_EQUALS_OPERATOR = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "GREATER_EQUALS_OPERATOR"));
	} 

	//terminal NOT_EQUALS_OPERATOR:
	//
	//	"!=";
	public TerminalRule getNOT_EQUALS_OPERATORRule() {
		return (tNOT_EQUALS_OPERATOR != null) ? tNOT_EQUALS_OPERATOR : (tNOT_EQUALS_OPERATOR = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "NOT_EQUALS_OPERATOR"));
	} 

	//terminal WS:
	//
	//	" " | "\t" | "\n" | "\r";
	public TerminalRule getWSRule() {
		return (tWS != null) ? tWS : (tWS = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "WS"));
	} 

	//terminal ML_COMMENT:
	//
	//	"/ *"->"* /";
	public TerminalRule getML_COMMENTRule() {
		return (tML_COMMENT != null) ? tML_COMMENT : (tML_COMMENT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "ML_COMMENT"));
	} 

	//terminal SL_COMMENT:
	//
	//	"//" !("\n" | "\r")* ("\r"? "\n")?;
	public TerminalRule getSL_COMMENTRule() {
		return (tSL_COMMENT != null) ? tSL_COMMENT : (tSL_COMMENT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "SL_COMMENT"));
	} 

	//terminal PIPE:
	//
	//	"|";
	public TerminalRule getPIPERule() {
		return (tPIPE != null) ? tPIPE : (tPIPE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "PIPE"));
	} 

	//terminal COLON:
	//
	//	":";
	public TerminalRule getCOLONRule() {
		return (tCOLON != null) ? tCOLON : (tCOLON = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "COLON"));
	} 

	//terminal OPENING_CURLY_BRACKET:
	//
	//	"{";
	public TerminalRule getOPENING_CURLY_BRACKETRule() {
		return (tOPENING_CURLY_BRACKET != null) ? tOPENING_CURLY_BRACKET : (tOPENING_CURLY_BRACKET = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "OPENING_CURLY_BRACKET"));
	} 

	//terminal CLOSING_CURLY_BRACKET:
	//
	//	"}";
	public TerminalRule getCLOSING_CURLY_BRACKETRule() {
		return (tCLOSING_CURLY_BRACKET != null) ? tCLOSING_CURLY_BRACKET : (tCLOSING_CURLY_BRACKET = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "CLOSING_CURLY_BRACKET"));
	} 

	//terminal EQUAL_SIGN:
	//
	//	"=";
	public TerminalRule getEQUAL_SIGNRule() {
		return (tEQUAL_SIGN != null) ? tEQUAL_SIGN : (tEQUAL_SIGN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "EQUAL_SIGN"));
	} 

	//terminal COMMA:
	//
	//	",";
	public TerminalRule getCOMMARule() {
		return (tCOMMA != null) ? tCOMMA : (tCOMMA = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "COMMA"));
	} 

	//terminal OPENING_ROUND_BRACKET:
	//
	//	"(";
	public TerminalRule getOPENING_ROUND_BRACKETRule() {
		return (tOPENING_ROUND_BRACKET != null) ? tOPENING_ROUND_BRACKET : (tOPENING_ROUND_BRACKET = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "OPENING_ROUND_BRACKET"));
	} 

	//terminal CLOSING_ROUND_BRACKET:
	//
	//	")";
	public TerminalRule getCLOSING_ROUND_BRACKETRule() {
		return (tCLOSING_ROUND_BRACKET != null) ? tCLOSING_ROUND_BRACKET : (tCLOSING_ROUND_BRACKET = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "CLOSING_ROUND_BRACKET"));
	} 

	//terminal OPENING_SQUARE_BRACKET:
	//
	//	"[";
	public TerminalRule getOPENING_SQUARE_BRACKETRule() {
		return (tOPENING_SQUARE_BRACKET != null) ? tOPENING_SQUARE_BRACKET : (tOPENING_SQUARE_BRACKET = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "OPENING_SQUARE_BRACKET"));
	} 

	//terminal CLOSING_SQUARE_BRACKET:
	//
	//	"]";
	public TerminalRule getCLOSING_SQUARE_BRACKETRule() {
		return (tCLOSING_SQUARE_BRACKET != null) ? tCLOSING_SQUARE_BRACKET : (tCLOSING_SQUARE_BRACKET = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "CLOSING_SQUARE_BRACKET"));
	} 

	//terminal PLUS_SIGN:
	//
	//	"+";
	public TerminalRule getPLUS_SIGNRule() {
		return (tPLUS_SIGN != null) ? tPLUS_SIGN : (tPLUS_SIGN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "PLUS_SIGN"));
	} 

	//terminal CARET:
	//
	//	"^";
	public TerminalRule getCARETRule() {
		return (tCARET != null) ? tCARET : (tCARET = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "CARET"));
	} 

	//terminal NOT_TOKEN:
	//
	//	"!";
	public TerminalRule getNOT_TOKENRule() {
		return (tNOT_TOKEN != null) ? tNOT_TOKEN : (tNOT_TOKEN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "NOT_TOKEN"));
	} 

	//terminal OPTIONAL:
	//
	//	"~";
	public TerminalRule getOPTIONALRule() {
		return (tOPTIONAL != null) ? tOPTIONAL : (tOPTIONAL = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "OPTIONAL"));
	} 

	//terminal PERIOD:
	//
	//	".";
	public TerminalRule getPERIODRule() {
		return (tPERIOD != null) ? tPERIOD : (tPERIOD = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "PERIOD"));
	} 

	//terminal OTHER_ALLOWED_TERM_CHARACTER:
	//
	//	.;
	public TerminalRule getOTHER_ALLOWED_TERM_CHARACTERRule() {
		return (tOTHER_ALLOWED_TERM_CHARACTER != null) ? tOTHER_ALLOWED_TERM_CHARACTER : (tOTHER_ALLOWED_TERM_CHARACTER = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "OTHER_ALLOWED_TERM_CHARACTER"));
	} 

	//terminal AND_TOKEN:
	//
	//	"AND";
	public TerminalRule getAND_TOKENRule() {
		return (tAND_TOKEN != null) ? tAND_TOKEN : (tAND_TOKEN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "AND_TOKEN"));
	} 

	//terminal OR_TOKEN:
	//
	//	"OR";
	public TerminalRule getOR_TOKENRule() {
		return (tOR_TOKEN != null) ? tOR_TOKEN : (tOR_TOKEN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "OR_TOKEN"));
	} 

	//terminal UNION_TOKEN:
	//
	//	"UNION";
	public TerminalRule getUNION_TOKENRule() {
		return (tUNION_TOKEN != null) ? tUNION_TOKEN : (tUNION_TOKEN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "UNION_TOKEN"));
	} 
}