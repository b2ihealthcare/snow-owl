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
public class SCGGrammarAccess extends AbstractGrammarElementFinder {
	
	
	public class ExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Expression");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cConceptsAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cConceptsConceptParserRuleCall_0_0 = (RuleCall)cConceptsAssignment_0.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cPLUS_SIGNTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Assignment cConceptsAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cConceptsConceptParserRuleCall_1_1_0 = (RuleCall)cConceptsAssignment_1_1.eContents().get(0);
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		private final RuleCall cCOLONTerminalRuleCall_2_0 = (RuleCall)cGroup_2.eContents().get(0);
		private final Group cGroup_2_1 = (Group)cGroup_2.eContents().get(1);
		private final Assignment cAttributesAssignment_2_1_0 = (Assignment)cGroup_2_1.eContents().get(0);
		private final RuleCall cAttributesAttributeParserRuleCall_2_1_0_0 = (RuleCall)cAttributesAssignment_2_1_0.eContents().get(0);
		private final Group cGroup_2_1_1 = (Group)cGroup_2_1.eContents().get(1);
		private final RuleCall cCOMMATerminalRuleCall_2_1_1_0 = (RuleCall)cGroup_2_1_1.eContents().get(0);
		private final Assignment cAttributesAssignment_2_1_1_1 = (Assignment)cGroup_2_1_1.eContents().get(1);
		private final RuleCall cAttributesAttributeParserRuleCall_2_1_1_1_0 = (RuleCall)cAttributesAssignment_2_1_1_1.eContents().get(0);
		private final Assignment cGroupsAssignment_2_2 = (Assignment)cGroup_2.eContents().get(2);
		private final RuleCall cGroupsGroupParserRuleCall_2_2_0 = (RuleCall)cGroupsAssignment_2_2.eContents().get(0);
		
		//Expression hidden(WS, SL_COMMENT, ML_COMMENT):
		//
		//	concepts+=Concept (PLUS_SIGN concepts+=Concept)* (COLON (attributes+=Attribute (COMMA attributes+=Attribute)*)?
		//
		//	groups+=Group*)?;
		public ParserRule getRule() { return rule; }

		//concepts+=Concept (PLUS_SIGN concepts+=Concept)* (COLON (attributes+=Attribute (COMMA attributes+=Attribute)*)?
		//
		//groups+=Group*)?
		public Group getGroup() { return cGroup; }

		//concepts+=Concept
		public Assignment getConceptsAssignment_0() { return cConceptsAssignment_0; }

		//Concept
		public RuleCall getConceptsConceptParserRuleCall_0_0() { return cConceptsConceptParserRuleCall_0_0; }

		//(PLUS_SIGN concepts+=Concept)*
		public Group getGroup_1() { return cGroup_1; }

		//PLUS_SIGN
		public RuleCall getPLUS_SIGNTerminalRuleCall_1_0() { return cPLUS_SIGNTerminalRuleCall_1_0; }

		//concepts+=Concept
		public Assignment getConceptsAssignment_1_1() { return cConceptsAssignment_1_1; }

		//Concept
		public RuleCall getConceptsConceptParserRuleCall_1_1_0() { return cConceptsConceptParserRuleCall_1_1_0; }

		//(COLON (attributes+=Attribute (COMMA attributes+=Attribute)*)? groups+=Group*)?
		public Group getGroup_2() { return cGroup_2; }

		//COLON
		public RuleCall getCOLONTerminalRuleCall_2_0() { return cCOLONTerminalRuleCall_2_0; }

		//(attributes+=Attribute (COMMA attributes+=Attribute)*)?
		public Group getGroup_2_1() { return cGroup_2_1; }

		//attributes+=Attribute
		public Assignment getAttributesAssignment_2_1_0() { return cAttributesAssignment_2_1_0; }

		//Attribute
		public RuleCall getAttributesAttributeParserRuleCall_2_1_0_0() { return cAttributesAttributeParserRuleCall_2_1_0_0; }

		//(COMMA attributes+=Attribute)*
		public Group getGroup_2_1_1() { return cGroup_2_1_1; }

		//COMMA
		public RuleCall getCOMMATerminalRuleCall_2_1_1_0() { return cCOMMATerminalRuleCall_2_1_1_0; }

		//attributes+=Attribute
		public Assignment getAttributesAssignment_2_1_1_1() { return cAttributesAssignment_2_1_1_1; }

		//Attribute
		public RuleCall getAttributesAttributeParserRuleCall_2_1_1_1_0() { return cAttributesAttributeParserRuleCall_2_1_1_1_0; }

		//groups+=Group*
		public Assignment getGroupsAssignment_2_2() { return cGroupsAssignment_2_2; }

		//Group
		public RuleCall getGroupsGroupParserRuleCall_2_2_0() { return cGroupsGroupParserRuleCall_2_2_0; }
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
		
		//Concept:
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

	public class GroupElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Group");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cOPENING_CURLY_BRACKETTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cAttributesAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cAttributesAttributeParserRuleCall_1_0 = (RuleCall)cAttributesAssignment_1.eContents().get(0);
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		private final RuleCall cCOMMATerminalRuleCall_2_0 = (RuleCall)cGroup_2.eContents().get(0);
		private final Assignment cAttributesAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		private final RuleCall cAttributesAttributeParserRuleCall_2_1_0 = (RuleCall)cAttributesAssignment_2_1.eContents().get(0);
		private final RuleCall cCLOSING_CURLY_BRACKETTerminalRuleCall_3 = (RuleCall)cGroup.eContents().get(3);
		
		//Group:
		//
		//	OPENING_CURLY_BRACKET attributes+=Attribute (COMMA attributes+=Attribute)* CLOSING_CURLY_BRACKET;
		public ParserRule getRule() { return rule; }

		//OPENING_CURLY_BRACKET attributes+=Attribute (COMMA attributes+=Attribute)* CLOSING_CURLY_BRACKET
		public Group getGroup() { return cGroup; }

		//OPENING_CURLY_BRACKET
		public RuleCall getOPENING_CURLY_BRACKETTerminalRuleCall_0() { return cOPENING_CURLY_BRACKETTerminalRuleCall_0; }

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

		//CLOSING_CURLY_BRACKET
		public RuleCall getCLOSING_CURLY_BRACKETTerminalRuleCall_3() { return cCLOSING_CURLY_BRACKETTerminalRuleCall_3; }
	}

	public class AttributeElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Attribute");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cNameAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cNameConceptParserRuleCall_0_0 = (RuleCall)cNameAssignment_0.eContents().get(0);
		private final RuleCall cEQUAL_SIGNTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueAttributeValueParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//Attribute:
		//
		//	name=Concept EQUAL_SIGN value=AttributeValue;
		public ParserRule getRule() { return rule; }

		//name=Concept EQUAL_SIGN value=AttributeValue
		public Group getGroup() { return cGroup; }

		//name=Concept
		public Assignment getNameAssignment_0() { return cNameAssignment_0; }

		//Concept
		public RuleCall getNameConceptParserRuleCall_0_0() { return cNameConceptParserRuleCall_0_0; }

		//EQUAL_SIGN
		public RuleCall getEQUAL_SIGNTerminalRuleCall_1() { return cEQUAL_SIGNTerminalRuleCall_1; }

		//value=AttributeValue
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }

		//AttributeValue
		public RuleCall getValueAttributeValueParserRuleCall_2_0() { return cValueAttributeValueParserRuleCall_2_0; }
	}

	public class AttributeValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "AttributeValue");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cConceptParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final RuleCall cOPENING_ROUND_BRACKETTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final RuleCall cExpressionParserRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final RuleCall cCLOSING_ROUND_BRACKETTerminalRuleCall_1_2 = (RuleCall)cGroup_1.eContents().get(2);
		
		//AttributeValue:
		//
		//	Concept | OPENING_ROUND_BRACKET Expression CLOSING_ROUND_BRACKET;
		public ParserRule getRule() { return rule; }

		//Concept | OPENING_ROUND_BRACKET Expression CLOSING_ROUND_BRACKET
		public Alternatives getAlternatives() { return cAlternatives; }

		//Concept
		public RuleCall getConceptParserRuleCall_0() { return cConceptParserRuleCall_0; }

		//OPENING_ROUND_BRACKET Expression CLOSING_ROUND_BRACKET
		public Group getGroup_1() { return cGroup_1; }

		//OPENING_ROUND_BRACKET
		public RuleCall getOPENING_ROUND_BRACKETTerminalRuleCall_1_0() { return cOPENING_ROUND_BRACKETTerminalRuleCall_1_0; }

		//Expression
		public RuleCall getExpressionParserRuleCall_1_1() { return cExpressionParserRuleCall_1_1; }

		//CLOSING_ROUND_BRACKET
		public RuleCall getCLOSING_ROUND_BRACKETTerminalRuleCall_1_2() { return cCLOSING_ROUND_BRACKETTerminalRuleCall_1_2; }
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
		private final RuleCall cCOMMATerminalRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		private final RuleCall cOPENING_CURLY_BRACKETTerminalRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		private final RuleCall cCLOSING_CURLY_BRACKETTerminalRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		private final RuleCall cEQUAL_SIGNTerminalRuleCall_6 = (RuleCall)cAlternatives.eContents().get(6);
		private final RuleCall cOPENING_ROUND_BRACKETTerminalRuleCall_7 = (RuleCall)cAlternatives.eContents().get(7);
		private final RuleCall cCLOSING_ROUND_BRACKETTerminalRuleCall_8 = (RuleCall)cAlternatives.eContents().get(8);
		private final RuleCall cPLUS_SIGNTerminalRuleCall_9 = (RuleCall)cAlternatives.eContents().get(9);
		private final RuleCall cCOLONTerminalRuleCall_10 = (RuleCall)cAlternatives.eContents().get(10);
		private final RuleCall cOTHER_ALLOWED_TERM_CHARACTERTerminalRuleCall_11 = (RuleCall)cAlternatives.eContents().get(11);
		
		//TermCharacter hidden():
		//
		//	DIGIT_NONZERO | ZERO | LETTER | COMMA | OPENING_CURLY_BRACKET | CLOSING_CURLY_BRACKET | EQUAL_SIGN |
		//
		//	OPENING_ROUND_BRACKET | CLOSING_ROUND_BRACKET | PLUS_SIGN | COLON | OTHER_ALLOWED_TERM_CHARACTER;
		public ParserRule getRule() { return rule; }

		//DIGIT_NONZERO | ZERO | LETTER | COMMA | OPENING_CURLY_BRACKET | CLOSING_CURLY_BRACKET | EQUAL_SIGN |
		//
		//OPENING_ROUND_BRACKET | CLOSING_ROUND_BRACKET | PLUS_SIGN | COLON | OTHER_ALLOWED_TERM_CHARACTER
		public Alternatives getAlternatives() { return cAlternatives; }

		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_0() { return cDIGIT_NONZEROTerminalRuleCall_0; }

		//ZERO
		public RuleCall getZEROTerminalRuleCall_1() { return cZEROTerminalRuleCall_1; }

		//LETTER
		public RuleCall getLETTERTerminalRuleCall_2() { return cLETTERTerminalRuleCall_2; }

		//COMMA
		public RuleCall getCOMMATerminalRuleCall_3() { return cCOMMATerminalRuleCall_3; }

		//OPENING_CURLY_BRACKET
		public RuleCall getOPENING_CURLY_BRACKETTerminalRuleCall_4() { return cOPENING_CURLY_BRACKETTerminalRuleCall_4; }

		//CLOSING_CURLY_BRACKET
		public RuleCall getCLOSING_CURLY_BRACKETTerminalRuleCall_5() { return cCLOSING_CURLY_BRACKETTerminalRuleCall_5; }

		//EQUAL_SIGN
		public RuleCall getEQUAL_SIGNTerminalRuleCall_6() { return cEQUAL_SIGNTerminalRuleCall_6; }

		//OPENING_ROUND_BRACKET
		public RuleCall getOPENING_ROUND_BRACKETTerminalRuleCall_7() { return cOPENING_ROUND_BRACKETTerminalRuleCall_7; }

		//CLOSING_ROUND_BRACKET
		public RuleCall getCLOSING_ROUND_BRACKETTerminalRuleCall_8() { return cCLOSING_ROUND_BRACKETTerminalRuleCall_8; }

		//PLUS_SIGN
		public RuleCall getPLUS_SIGNTerminalRuleCall_9() { return cPLUS_SIGNTerminalRuleCall_9; }

		//COLON
		public RuleCall getCOLONTerminalRuleCall_10() { return cCOLONTerminalRuleCall_10; }

		//OTHER_ALLOWED_TERM_CHARACTER
		public RuleCall getOTHER_ALLOWED_TERM_CHARACTERTerminalRuleCall_11() { return cOTHER_ALLOWED_TERM_CHARACTERTerminalRuleCall_11; }
	}
	
	
	private ExpressionElements pExpression;
	private ConceptElements pConcept;
	private GroupElements pGroup;
	private AttributeElements pAttribute;
	private AttributeValueElements pAttributeValue;
	private TermElements pTerm;
	private ConceptIdElements pConceptId;
	private TermCharacterElements pTermCharacter;
	private TerminalRule tZERO;
	private TerminalRule tDIGIT_NONZERO;
	private TerminalRule tLETTER;
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
	private TerminalRule tPLUS_SIGN;
	private TerminalRule tOTHER_ALLOWED_TERM_CHARACTER;
	
	private final Grammar grammar;

	@Inject
	public SCGGrammarAccess(GrammarProvider grammarProvider) {
		this.grammar = internalFindGrammar(grammarProvider);
	}
	
	protected Grammar internalFindGrammar(GrammarProvider grammarProvider) {
		Grammar grammar = grammarProvider.getGrammar(this);
		while (grammar != null) {
			if ("com.b2international.snowowl.dsl.SCG".equals(grammar.getName())) {
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
	

	
	//Expression hidden(WS, SL_COMMENT, ML_COMMENT):
	//
	//	concepts+=Concept (PLUS_SIGN concepts+=Concept)* (COLON (attributes+=Attribute (COMMA attributes+=Attribute)*)?
	//
	//	groups+=Group*)?;
	public ExpressionElements getExpressionAccess() {
		return (pExpression != null) ? pExpression : (pExpression = new ExpressionElements());
	}
	
	public ParserRule getExpressionRule() {
		return getExpressionAccess().getRule();
	}

	//Concept:
	//
	//	id=ConceptId (PIPE WS* term=Term WS* PIPE)?;
	public ConceptElements getConceptAccess() {
		return (pConcept != null) ? pConcept : (pConcept = new ConceptElements());
	}
	
	public ParserRule getConceptRule() {
		return getConceptAccess().getRule();
	}

	//Group:
	//
	//	OPENING_CURLY_BRACKET attributes+=Attribute (COMMA attributes+=Attribute)* CLOSING_CURLY_BRACKET;
	public GroupElements getGroupAccess() {
		return (pGroup != null) ? pGroup : (pGroup = new GroupElements());
	}
	
	public ParserRule getGroupRule() {
		return getGroupAccess().getRule();
	}

	//Attribute:
	//
	//	name=Concept EQUAL_SIGN value=AttributeValue;
	public AttributeElements getAttributeAccess() {
		return (pAttribute != null) ? pAttribute : (pAttribute = new AttributeElements());
	}
	
	public ParserRule getAttributeRule() {
		return getAttributeAccess().getRule();
	}

	//AttributeValue:
	//
	//	Concept | OPENING_ROUND_BRACKET Expression CLOSING_ROUND_BRACKET;
	public AttributeValueElements getAttributeValueAccess() {
		return (pAttributeValue != null) ? pAttributeValue : (pAttributeValue = new AttributeValueElements());
	}
	
	public ParserRule getAttributeValueRule() {
		return getAttributeValueAccess().getRule();
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
	//	DIGIT_NONZERO | ZERO | LETTER | COMMA | OPENING_CURLY_BRACKET | CLOSING_CURLY_BRACKET | EQUAL_SIGN |
	//
	//	OPENING_ROUND_BRACKET | CLOSING_ROUND_BRACKET | PLUS_SIGN | COLON | OTHER_ALLOWED_TERM_CHARACTER;
	public TermCharacterElements getTermCharacterAccess() {
		return (pTermCharacter != null) ? pTermCharacter : (pTermCharacter = new TermCharacterElements());
	}
	
	public ParserRule getTermCharacterRule() {
		return getTermCharacterAccess().getRule();
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

	//terminal PLUS_SIGN:
	//
	//	"+";
	public TerminalRule getPLUS_SIGNRule() {
		return (tPLUS_SIGN != null) ? tPLUS_SIGN : (tPLUS_SIGN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "PLUS_SIGN"));
	} 

	////terminal OTHER_ALLOWED_TERM_CHARACTER : '-'|'/'|'\\'|';'|'.'|'?'|'_'|'<'|'>'|'['|']';
	//
	//
	//// ------ Original Snomed Composition Grammar (2008-12-23) ------
	//
	//
	////expression = concept *("+" concept) [":" ws refinements ] 
	// //concept = ws conceptId ws ["|" ws term ws "|" ws] 
	//
	//
	////conceptId = sctId 
	// //term = 1*nonwsnonpipe *( 1*SP 1*nonwsnonpipe )
	//
	//
	////refinements = ( attributeSet *attributeGroup ) / 1*attributeGroup 
	// //attributeGroup = "{" attributeSet "}" ws
	//
	//
	////attributeSet = attribute *("," attribute) 
	// //attribute = attributeName "=" attributeValue 
	//
	//
	////attributeName = ws attributeNameId ws ["|" ws term ws "|" ws] 
	//
	//
	////attributeValue = concept / (ws "(" expression ")" ws) 
	// //attributeNameId = sctId 
	//
	//
	////sctId = digitNonZero 5*17( digit ) 
	// //ws =*(SP/HTAB/CR/LF) ;whitespace
	// //SP = %x20 
	// //HTAB = %x09 
	// //CR = %x0D 
	//
	//
	////LF = %x0A 
	// //digit = %x30-39 
	// //digitNonZero = %x31-39 ; digits 1 through 9, but excluding 0 
	//
	//
	////nonwsnonpipe = %x21-7B / %x7D-7E / UTF8-2 / UTF8-3 / UTF8-4
	// terminal OTHER_ALLOWED_TERM_CHARACTER:
	//
	//	.;
	public TerminalRule getOTHER_ALLOWED_TERM_CHARACTERRule() {
		return (tOTHER_ALLOWED_TERM_CHARACTER != null) ? tOTHER_ALLOWED_TERM_CHARACTER : (tOTHER_ALLOWED_TERM_CHARACTER = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "OTHER_ALLOWED_TERM_CHARACTER"));
	} 
}