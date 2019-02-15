/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ql.services;

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
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.UnorderedGroup;
import org.eclipse.xtext.service.AbstractElementFinder.AbstractGrammarElementFinder;
import org.eclipse.xtext.service.GrammarProvider;

@Singleton
public class QLGrammarAccess extends AbstractGrammarElementFinder {
	
	public class QueryElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.Query");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cQueryAction_0 = (Action)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConstraintConstraintParserRuleCall_1_0 = (RuleCall)cConstraintAssignment_1.eContents().get(0);
		
		//Query:
		//	{Query} constraint=Constraint?;
		@Override public ParserRule getRule() { return rule; }
		
		//{Query} constraint=Constraint?
		public Group getGroup() { return cGroup; }
		
		//{Query}
		public Action getQueryAction_0() { return cQueryAction_0; }
		
		//constraint=Constraint?
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//Constraint
		public RuleCall getConstraintConstraintParserRuleCall_1_0() { return cConstraintConstraintParserRuleCall_1_0; }
	}
	public class ConstraintElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.Constraint");
		private final RuleCall cDisjunctionParserRuleCall = (RuleCall)rule.eContents().get(1);
		
		//Constraint:
		//	Disjunction;
		@Override public ParserRule getRule() { return rule; }
		
		//Disjunction
		public RuleCall getDisjunctionParserRuleCall() { return cDisjunctionParserRuleCall; }
	}
	public class DisjunctionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.Disjunction");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cConjunctionParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cDisjunctionLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final Keyword cORKeyword_1_1 = (Keyword)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightConjunctionParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//Disjunction Constraint:
		//	Conjunction ({Disjunction.left=current} 'OR' right=Conjunction)*;
		@Override public ParserRule getRule() { return rule; }
		
		//Conjunction ({Disjunction.left=current} 'OR' right=Conjunction)*
		public Group getGroup() { return cGroup; }
		
		//Conjunction
		public RuleCall getConjunctionParserRuleCall_0() { return cConjunctionParserRuleCall_0; }
		
		//({Disjunction.left=current} 'OR' right=Conjunction)*
		public Group getGroup_1() { return cGroup_1; }
		
		//{Disjunction.left=current}
		public Action getDisjunctionLeftAction_1_0() { return cDisjunctionLeftAction_1_0; }
		
		//'OR'
		public Keyword getORKeyword_1_1() { return cORKeyword_1_1; }
		
		//right=Conjunction
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		//Conjunction
		public RuleCall getRightConjunctionParserRuleCall_1_2_0() { return cRightConjunctionParserRuleCall_1_2_0; }
	}
	public class ConjunctionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.Conjunction");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cExclusionParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cConjunctionLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final Keyword cANDKeyword_1_1 = (Keyword)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightExclusionParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//Conjunction Constraint:
		//	Exclusion ({Conjunction.left=current} 'AND' right=Exclusion)*;
		@Override public ParserRule getRule() { return rule; }
		
		//Exclusion ({Conjunction.left=current} 'AND' right=Exclusion)*
		public Group getGroup() { return cGroup; }
		
		//Exclusion
		public RuleCall getExclusionParserRuleCall_0() { return cExclusionParserRuleCall_0; }
		
		//({Conjunction.left=current} 'AND' right=Exclusion)*
		public Group getGroup_1() { return cGroup_1; }
		
		//{Conjunction.left=current}
		public Action getConjunctionLeftAction_1_0() { return cConjunctionLeftAction_1_0; }
		
		//'AND'
		public Keyword getANDKeyword_1_1() { return cANDKeyword_1_1; }
		
		//right=Exclusion
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		//Exclusion
		public RuleCall getRightExclusionParserRuleCall_1_2_0() { return cRightExclusionParserRuleCall_1_2_0; }
	}
	public class ExclusionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.Exclusion");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cFilterParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cExclusionLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final Keyword cMINUSKeyword_1_1 = (Keyword)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightFilterParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//Exclusion Constraint:
		//	Filter ({Exclusion.left=current} 'MINUS' right=Filter)?;
		@Override public ParserRule getRule() { return rule; }
		
		//Filter ({Exclusion.left=current} 'MINUS' right=Filter)?
		public Group getGroup() { return cGroup; }
		
		//Filter
		public RuleCall getFilterParserRuleCall_0() { return cFilterParserRuleCall_0; }
		
		//({Exclusion.left=current} 'MINUS' right=Filter)?
		public Group getGroup_1() { return cGroup_1; }
		
		//{Exclusion.left=current}
		public Action getExclusionLeftAction_1_0() { return cExclusionLeftAction_1_0; }
		
		//'MINUS'
		public Keyword getMINUSKeyword_1_1() { return cMINUSKeyword_1_1; }
		
		//right=Filter
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		//Filter
		public RuleCall getRightFilterParserRuleCall_1_2_0() { return cRightFilterParserRuleCall_1_2_0; }
	}
	public class NestedFilterElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.NestedFilter");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cROUND_OPENTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConstraintConstraintParserRuleCall_1_0 = (RuleCall)cConstraintAssignment_1.eContents().get(0);
		private final RuleCall cROUND_CLOSETerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		
		//NestedFilter:
		//	ROUND_OPEN constraint=Constraint ROUND_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//ROUND_OPEN constraint=Constraint ROUND_CLOSE
		public Group getGroup() { return cGroup; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_0() { return cROUND_OPENTerminalRuleCall_0; }
		
		//constraint=Constraint
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//Constraint
		public RuleCall getConstraintConstraintParserRuleCall_1_0() { return cConstraintConstraintParserRuleCall_1_0; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_2() { return cROUND_CLOSETerminalRuleCall_2; }
	}
	public class FilterElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.Filter");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cActiveFilterParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cEclFilterParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cDescriptionParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cNestedFilterParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		//Filter:
		//	ActiveFilter | EclFilter | Description | NestedFilter;
		@Override public ParserRule getRule() { return rule; }
		
		//ActiveFilter | EclFilter | Description | NestedFilter
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//ActiveFilter
		public RuleCall getActiveFilterParserRuleCall_0() { return cActiveFilterParserRuleCall_0; }
		
		//EclFilter
		public RuleCall getEclFilterParserRuleCall_1() { return cEclFilterParserRuleCall_1; }
		
		//Description
		public RuleCall getDescriptionParserRuleCall_2() { return cDescriptionParserRuleCall_2; }
		
		//NestedFilter
		public RuleCall getNestedFilterParserRuleCall_3() { return cNestedFilterParserRuleCall_3; }
	}
	public class EclFilterElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.EclFilter");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cECLTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cEclAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cEclScriptParserRuleCall_1_0 = (RuleCall)cEclAssignment_1.eContents().get(0);
		
		//EclFilter:
		//	ECL ecl=Script;
		@Override public ParserRule getRule() { return rule; }
		
		//ECL ecl=Script
		public Group getGroup() { return cGroup; }
		
		//ECL
		public RuleCall getECLTerminalRuleCall_0() { return cECLTerminalRuleCall_0; }
		
		//ecl=Script
		public Assignment getEclAssignment_1() { return cEclAssignment_1; }
		
		//Script
		public RuleCall getEclScriptParserRuleCall_1_0() { return cEclScriptParserRuleCall_1_0; }
	}
	public class ActiveFilterElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.ActiveFilter");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cACTIVETerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cActiveAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cActiveBooleanParserRuleCall_1_0 = (RuleCall)cActiveAssignment_1.eContents().get(0);
		
		//ActiveFilter:
		//	ACTIVE active=Boolean;
		@Override public ParserRule getRule() { return rule; }
		
		//ACTIVE active=Boolean
		public Group getGroup() { return cGroup; }
		
		//ACTIVE
		public RuleCall getACTIVETerminalRuleCall_0() { return cACTIVETerminalRuleCall_0; }
		
		//active=Boolean
		public Assignment getActiveAssignment_1() { return cActiveAssignment_1; }
		
		//Boolean
		public RuleCall getActiveBooleanParserRuleCall_1_0() { return cActiveBooleanParserRuleCall_1_0; }
	}
	public class DescriptionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.Description");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cDescriptionAction_0 = (Action)cGroup.eContents().get(0);
		private final RuleCall cOPEN_DOUBLE_BRACESTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cFilterAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cFilterDescriptionFilterParserRuleCall_2_0 = (RuleCall)cFilterAssignment_2.eContents().get(0);
		private final RuleCall cCLOSE_DOUBLE_BRACESTerminalRuleCall_3 = (RuleCall)cGroup.eContents().get(3);
		
		//Description:
		//	{Description} OPEN_DOUBLE_BRACES filter=DescriptionFilter CLOSE_DOUBLE_BRACES;
		@Override public ParserRule getRule() { return rule; }
		
		//{Description} OPEN_DOUBLE_BRACES filter=DescriptionFilter CLOSE_DOUBLE_BRACES
		public Group getGroup() { return cGroup; }
		
		//{Description}
		public Action getDescriptionAction_0() { return cDescriptionAction_0; }
		
		//OPEN_DOUBLE_BRACES
		public RuleCall getOPEN_DOUBLE_BRACESTerminalRuleCall_1() { return cOPEN_DOUBLE_BRACESTerminalRuleCall_1; }
		
		//filter=DescriptionFilter
		public Assignment getFilterAssignment_2() { return cFilterAssignment_2; }
		
		//DescriptionFilter
		public RuleCall getFilterDescriptionFilterParserRuleCall_2_0() { return cFilterDescriptionFilterParserRuleCall_2_0; }
		
		//CLOSE_DOUBLE_BRACES
		public RuleCall getCLOSE_DOUBLE_BRACESTerminalRuleCall_3() { return cCLOSE_DOUBLE_BRACESTerminalRuleCall_3; }
	}
	public class DescriptionFilterElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.DescriptionFilter");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cDescriptionFilterAction_0 = (Action)cGroup.eContents().get(0);
		private final UnorderedGroup cUnorderedGroup_1 = (UnorderedGroup)cGroup.eContents().get(1);
		private final Assignment cTermFilterAssignment_1_0 = (Assignment)cUnorderedGroup_1.eContents().get(0);
		private final RuleCall cTermFilterTermFilterParserRuleCall_1_0_0 = (RuleCall)cTermFilterAssignment_1_0.eContents().get(0);
		private final Assignment cActiveAssignment_1_1 = (Assignment)cUnorderedGroup_1.eContents().get(1);
		private final RuleCall cActiveActiveTermParserRuleCall_1_1_0 = (RuleCall)cActiveAssignment_1_1.eContents().get(0);
		private final Assignment cTypeAssignment_1_2 = (Assignment)cUnorderedGroup_1.eContents().get(2);
		private final RuleCall cTypeDescriptiontypeParserRuleCall_1_2_0 = (RuleCall)cTypeAssignment_1_2.eContents().get(0);
		private final Assignment cRegexAssignment_1_3 = (Assignment)cUnorderedGroup_1.eContents().get(3);
		private final RuleCall cRegexRegularExpressionParserRuleCall_1_3_0 = (RuleCall)cRegexAssignment_1_3.eContents().get(0);
		
		//DescriptionFilter:
		//	{DescriptionFilter} (termFilter=TermFilter?
		//	& active=ActiveTerm?
		//	& type=Descriptiontype?
		//	& regex=RegularExpression?);
		@Override public ParserRule getRule() { return rule; }
		
		//{DescriptionFilter} (termFilter=TermFilter? & active=ActiveTerm? & type=Descriptiontype? & regex=RegularExpression?)
		public Group getGroup() { return cGroup; }
		
		//{DescriptionFilter}
		public Action getDescriptionFilterAction_0() { return cDescriptionFilterAction_0; }
		
		//termFilter=TermFilter? & active=ActiveTerm? & type=Descriptiontype? & regex=RegularExpression?
		public UnorderedGroup getUnorderedGroup_1() { return cUnorderedGroup_1; }
		
		//termFilter=TermFilter?
		public Assignment getTermFilterAssignment_1_0() { return cTermFilterAssignment_1_0; }
		
		//TermFilter
		public RuleCall getTermFilterTermFilterParserRuleCall_1_0_0() { return cTermFilterTermFilterParserRuleCall_1_0_0; }
		
		//active=ActiveTerm?
		public Assignment getActiveAssignment_1_1() { return cActiveAssignment_1_1; }
		
		//ActiveTerm
		public RuleCall getActiveActiveTermParserRuleCall_1_1_0() { return cActiveActiveTermParserRuleCall_1_1_0; }
		
		//type=Descriptiontype?
		public Assignment getTypeAssignment_1_2() { return cTypeAssignment_1_2; }
		
		//Descriptiontype
		public RuleCall getTypeDescriptiontypeParserRuleCall_1_2_0() { return cTypeDescriptiontypeParserRuleCall_1_2_0; }
		
		//regex=RegularExpression?
		public Assignment getRegexAssignment_1_3() { return cRegexAssignment_1_3; }
		
		//RegularExpression
		public RuleCall getRegexRegularExpressionParserRuleCall_1_3_0() { return cRegexRegularExpressionParserRuleCall_1_3_0; }
	}
	public class TermFilterElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.TermFilter");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cTERMTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cEQUALTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cTermAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cTermSTRINGTerminalRuleCall_2_0 = (RuleCall)cTermAssignment_2.eContents().get(0);
		
		//TermFilter:
		//	TERM EQUAL term=STRING;
		@Override public ParserRule getRule() { return rule; }
		
		//TERM EQUAL term=STRING
		public Group getGroup() { return cGroup; }
		
		//TERM
		public RuleCall getTERMTerminalRuleCall_0() { return cTERMTerminalRuleCall_0; }
		
		//EQUAL
		public RuleCall getEQUALTerminalRuleCall_1() { return cEQUALTerminalRuleCall_1; }
		
		//term=STRING
		public Assignment getTermAssignment_2() { return cTermAssignment_2; }
		
		//STRING
		public RuleCall getTermSTRINGTerminalRuleCall_2_0() { return cTermSTRINGTerminalRuleCall_2_0; }
	}
	public class RegularExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.RegularExpression");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cREGEXTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cEQUALTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cRegexAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cRegexSTRINGTerminalRuleCall_2_0 = (RuleCall)cRegexAssignment_2.eContents().get(0);
		
		//RegularExpression:
		//	REGEX EQUAL regex=STRING;
		@Override public ParserRule getRule() { return rule; }
		
		//REGEX EQUAL regex=STRING
		public Group getGroup() { return cGroup; }
		
		//REGEX
		public RuleCall getREGEXTerminalRuleCall_0() { return cREGEXTerminalRuleCall_0; }
		
		//EQUAL
		public RuleCall getEQUALTerminalRuleCall_1() { return cEQUALTerminalRuleCall_1; }
		
		//regex=STRING
		public Assignment getRegexAssignment_2() { return cRegexAssignment_2; }
		
		//STRING
		public RuleCall getRegexSTRINGTerminalRuleCall_2_0() { return cRegexSTRINGTerminalRuleCall_2_0; }
	}
	public class DescriptiontypeElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.Descriptiontype");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cDESCRIPTION_TYPETerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cEQUALTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cEclAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cEclScriptParserRuleCall_2_0 = (RuleCall)cEclAssignment_2.eContents().get(0);
		
		//Descriptiontype:
		//	DESCRIPTION_TYPE EQUAL ecl=Script;
		@Override public ParserRule getRule() { return rule; }
		
		//DESCRIPTION_TYPE EQUAL ecl=Script
		public Group getGroup() { return cGroup; }
		
		//DESCRIPTION_TYPE
		public RuleCall getDESCRIPTION_TYPETerminalRuleCall_0() { return cDESCRIPTION_TYPETerminalRuleCall_0; }
		
		//EQUAL
		public RuleCall getEQUALTerminalRuleCall_1() { return cEQUALTerminalRuleCall_1; }
		
		//ecl=Script
		public Assignment getEclAssignment_2() { return cEclAssignment_2; }
		
		//Script
		public RuleCall getEclScriptParserRuleCall_2_0() { return cEclScriptParserRuleCall_2_0; }
	}
	public class ActiveTermElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.ActiveTerm");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cACTIVETerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cEQUALTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cActiveAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cActiveBooleanParserRuleCall_2_0 = (RuleCall)cActiveAssignment_2.eContents().get(0);
		
		//ActiveTerm:
		//	ACTIVE EQUAL active=Boolean;
		@Override public ParserRule getRule() { return rule; }
		
		//ACTIVE EQUAL active=Boolean
		public Group getGroup() { return cGroup; }
		
		//ACTIVE
		public RuleCall getACTIVETerminalRuleCall_0() { return cACTIVETerminalRuleCall_0; }
		
		//EQUAL
		public RuleCall getEQUALTerminalRuleCall_1() { return cEQUALTerminalRuleCall_1; }
		
		//active=Boolean
		public Assignment getActiveAssignment_2() { return cActiveAssignment_2; }
		
		//Boolean
		public RuleCall getActiveBooleanParserRuleCall_2_0() { return cActiveBooleanParserRuleCall_2_0; }
	}
	public class BooleanElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.Boolean");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cTRUETerminalRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cFALSETerminalRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//Boolean:
		//	TRUE | FALSE;
		@Override public ParserRule getRule() { return rule; }
		
		//TRUE | FALSE
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//TRUE
		public RuleCall getTRUETerminalRuleCall_0() { return cTRUETerminalRuleCall_0; }
		
		//FALSE
		public RuleCall getFALSETerminalRuleCall_1() { return cFALSETerminalRuleCall_1; }
	}
	
	
	private final QueryElements pQuery;
	private final ConstraintElements pConstraint;
	private final DisjunctionElements pDisjunction;
	private final ConjunctionElements pConjunction;
	private final ExclusionElements pExclusion;
	private final NestedFilterElements pNestedFilter;
	private final FilterElements pFilter;
	private final EclFilterElements pEclFilter;
	private final ActiveFilterElements pActiveFilter;
	private final DescriptionElements pDescription;
	private final DescriptionFilterElements pDescriptionFilter;
	private final TermFilterElements pTermFilter;
	private final RegularExpressionElements pRegularExpression;
	private final DescriptiontypeElements pDescriptiontype;
	private final ActiveTermElements pActiveTerm;
	private final BooleanElements pBoolean;
	private final TerminalRule tTERM;
	private final TerminalRule tECL;
	private final TerminalRule tACTIVE;
	private final TerminalRule tTRUE;
	private final TerminalRule tFALSE;
	private final TerminalRule tREGEX;
	private final TerminalRule tDESCRIPTION_TYPE;
	private final TerminalRule tOPEN_DOUBLE_BRACES;
	private final TerminalRule tCLOSE_DOUBLE_BRACES;
	
	private final Grammar grammar;
	
	private final EclGrammarAccess gaEcl;

	@Inject
	public QLGrammarAccess(GrammarProvider grammarProvider,
			EclGrammarAccess gaEcl) {
		this.grammar = internalFindGrammar(grammarProvider);
		this.gaEcl = gaEcl;
		this.pQuery = new QueryElements();
		this.pConstraint = new ConstraintElements();
		this.pDisjunction = new DisjunctionElements();
		this.pConjunction = new ConjunctionElements();
		this.pExclusion = new ExclusionElements();
		this.pNestedFilter = new NestedFilterElements();
		this.pFilter = new FilterElements();
		this.pEclFilter = new EclFilterElements();
		this.pActiveFilter = new ActiveFilterElements();
		this.pDescription = new DescriptionElements();
		this.pDescriptionFilter = new DescriptionFilterElements();
		this.pTermFilter = new TermFilterElements();
		this.pRegularExpression = new RegularExpressionElements();
		this.pDescriptiontype = new DescriptiontypeElements();
		this.pActiveTerm = new ActiveTermElements();
		this.pBoolean = new BooleanElements();
		this.tTERM = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.TERM");
		this.tECL = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.ECL");
		this.tACTIVE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.ACTIVE");
		this.tTRUE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.TRUE");
		this.tFALSE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.FALSE");
		this.tREGEX = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.REGEX");
		this.tDESCRIPTION_TYPE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.DESCRIPTION_TYPE");
		this.tOPEN_DOUBLE_BRACES = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.OPEN_DOUBLE_BRACES");
		this.tCLOSE_DOUBLE_BRACES = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ql.QL.CLOSE_DOUBLE_BRACES");
	}
	
	protected Grammar internalFindGrammar(GrammarProvider grammarProvider) {
		Grammar grammar = grammarProvider.getGrammar(this);
		while (grammar != null) {
			if ("com.b2international.snowowl.snomed.ql.QL".equals(grammar.getName())) {
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

	
	//Query:
	//	{Query} constraint=Constraint?;
	public QueryElements getQueryAccess() {
		return pQuery;
	}
	
	public ParserRule getQueryRule() {
		return getQueryAccess().getRule();
	}
	
	//Constraint:
	//	Disjunction;
	public ConstraintElements getConstraintAccess() {
		return pConstraint;
	}
	
	public ParserRule getConstraintRule() {
		return getConstraintAccess().getRule();
	}
	
	//Disjunction Constraint:
	//	Conjunction ({Disjunction.left=current} 'OR' right=Conjunction)*;
	public DisjunctionElements getDisjunctionAccess() {
		return pDisjunction;
	}
	
	public ParserRule getDisjunctionRule() {
		return getDisjunctionAccess().getRule();
	}
	
	//Conjunction Constraint:
	//	Exclusion ({Conjunction.left=current} 'AND' right=Exclusion)*;
	public ConjunctionElements getConjunctionAccess() {
		return pConjunction;
	}
	
	public ParserRule getConjunctionRule() {
		return getConjunctionAccess().getRule();
	}
	
	//Exclusion Constraint:
	//	Filter ({Exclusion.left=current} 'MINUS' right=Filter)?;
	public ExclusionElements getExclusionAccess() {
		return pExclusion;
	}
	
	public ParserRule getExclusionRule() {
		return getExclusionAccess().getRule();
	}
	
	//NestedFilter:
	//	ROUND_OPEN constraint=Constraint ROUND_CLOSE;
	public NestedFilterElements getNestedFilterAccess() {
		return pNestedFilter;
	}
	
	public ParserRule getNestedFilterRule() {
		return getNestedFilterAccess().getRule();
	}
	
	//Filter:
	//	ActiveFilter | EclFilter | Description | NestedFilter;
	public FilterElements getFilterAccess() {
		return pFilter;
	}
	
	public ParserRule getFilterRule() {
		return getFilterAccess().getRule();
	}
	
	//EclFilter:
	//	ECL ecl=Script;
	public EclFilterElements getEclFilterAccess() {
		return pEclFilter;
	}
	
	public ParserRule getEclFilterRule() {
		return getEclFilterAccess().getRule();
	}
	
	//ActiveFilter:
	//	ACTIVE active=Boolean;
	public ActiveFilterElements getActiveFilterAccess() {
		return pActiveFilter;
	}
	
	public ParserRule getActiveFilterRule() {
		return getActiveFilterAccess().getRule();
	}
	
	//Description:
	//	{Description} OPEN_DOUBLE_BRACES filter=DescriptionFilter CLOSE_DOUBLE_BRACES;
	public DescriptionElements getDescriptionAccess() {
		return pDescription;
	}
	
	public ParserRule getDescriptionRule() {
		return getDescriptionAccess().getRule();
	}
	
	//DescriptionFilter:
	//	{DescriptionFilter} (termFilter=TermFilter?
	//	& active=ActiveTerm?
	//	& type=Descriptiontype?
	//	& regex=RegularExpression?);
	public DescriptionFilterElements getDescriptionFilterAccess() {
		return pDescriptionFilter;
	}
	
	public ParserRule getDescriptionFilterRule() {
		return getDescriptionFilterAccess().getRule();
	}
	
	//TermFilter:
	//	TERM EQUAL term=STRING;
	public TermFilterElements getTermFilterAccess() {
		return pTermFilter;
	}
	
	public ParserRule getTermFilterRule() {
		return getTermFilterAccess().getRule();
	}
	
	//RegularExpression:
	//	REGEX EQUAL regex=STRING;
	public RegularExpressionElements getRegularExpressionAccess() {
		return pRegularExpression;
	}
	
	public ParserRule getRegularExpressionRule() {
		return getRegularExpressionAccess().getRule();
	}
	
	//Descriptiontype:
	//	DESCRIPTION_TYPE EQUAL ecl=Script;
	public DescriptiontypeElements getDescriptiontypeAccess() {
		return pDescriptiontype;
	}
	
	public ParserRule getDescriptiontypeRule() {
		return getDescriptiontypeAccess().getRule();
	}
	
	//ActiveTerm:
	//	ACTIVE EQUAL active=Boolean;
	public ActiveTermElements getActiveTermAccess() {
		return pActiveTerm;
	}
	
	public ParserRule getActiveTermRule() {
		return getActiveTermAccess().getRule();
	}
	
	//Boolean:
	//	TRUE | FALSE;
	public BooleanElements getBooleanAccess() {
		return pBoolean;
	}
	
	public ParserRule getBooleanRule() {
		return getBooleanAccess().getRule();
	}
	
	//terminal TERM:
	//	'term';
	public TerminalRule getTERMRule() {
		return tTERM;
	}
	
	//terminal ECL:
	//	'ecl';
	public TerminalRule getECLRule() {
		return tECL;
	}
	
	//terminal ACTIVE:
	//	'active';
	public TerminalRule getACTIVERule() {
		return tACTIVE;
	}
	
	//terminal TRUE:
	//	'true';
	public TerminalRule getTRUERule() {
		return tTRUE;
	}
	
	//terminal FALSE:
	//	'false';
	public TerminalRule getFALSERule() {
		return tFALSE;
	}
	
	//terminal REGEX:
	//	'regex';
	public TerminalRule getREGEXRule() {
		return tREGEX;
	}
	
	//terminal DESCRIPTION_TYPE:
	//	'type';
	public TerminalRule getDESCRIPTION_TYPERule() {
		return tDESCRIPTION_TYPE;
	}
	
	//terminal OPEN_DOUBLE_BRACES:
	//	'{{';
	public TerminalRule getOPEN_DOUBLE_BRACESRule() {
		return tOPEN_DOUBLE_BRACES;
	}
	
	//terminal CLOSE_DOUBLE_BRACES:
	//	'}}';
	public TerminalRule getCLOSE_DOUBLE_BRACESRule() {
		return tCLOSE_DOUBLE_BRACES;
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
	//	AndExpressionConstraint ({OrExpressionConstraint.left=current} 'OR' right=AndExpressionConstraint)*;
	public EclGrammarAccess.OrExpressionConstraintElements getOrExpressionConstraintAccess() {
		return gaEcl.getOrExpressionConstraintAccess();
	}
	
	public ParserRule getOrExpressionConstraintRule() {
		return getOrExpressionConstraintAccess().getRule();
	}
	
	//AndExpressionConstraint ExpressionConstraint:
	//	ExclusionExpressionConstraint ({AndExpressionConstraint.left=current} ('AND' | ',')
	//	right=ExclusionExpressionConstraint)*;
	public EclGrammarAccess.AndExpressionConstraintElements getAndExpressionConstraintAccess() {
		return gaEcl.getAndExpressionConstraintAccess();
	}
	
	public ParserRule getAndExpressionConstraintRule() {
		return getAndExpressionConstraintAccess().getRule();
	}
	
	//ExclusionExpressionConstraint ExpressionConstraint:
	//	RefinedExpressionConstraint ({ExclusionExpressionConstraint.left=current} 'MINUS' right=RefinedExpressionConstraint)?;
	public EclGrammarAccess.ExclusionExpressionConstraintElements getExclusionExpressionConstraintAccess() {
		return gaEcl.getExclusionExpressionConstraintAccess();
	}
	
	public ParserRule getExclusionExpressionConstraintRule() {
		return getExclusionExpressionConstraintAccess().getRule();
	}
	
	//RefinedExpressionConstraint ExpressionConstraint:
	//	DottedExpressionConstraint ({RefinedExpressionConstraint.constraint=current} COLON refinement=Refinement)?;
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
	//	ChildOf | DescendantOf | DescendantOrSelfOf | ParentOf | AncestorOf | AncestorOrSelfOf | FocusConcept;
	public EclGrammarAccess.SubExpressionConstraintElements getSubExpressionConstraintAccess() {
		return gaEcl.getSubExpressionConstraintAccess();
	}
	
	public ParserRule getSubExpressionConstraintRule() {
		return getSubExpressionConstraintAccess().getRule();
	}
	
	//FocusConcept ExpressionConstraint:
	//	MemberOf | ConceptReference | Any | NestedExpression;
	public EclGrammarAccess.FocusConceptElements getFocusConceptAccess() {
		return gaEcl.getFocusConceptAccess();
	}
	
	public ParserRule getFocusConceptRule() {
		return getFocusConceptAccess().getRule();
	}
	
	//ChildOf:
	//	LT_EM constraint=FocusConcept;
	public EclGrammarAccess.ChildOfElements getChildOfAccess() {
		return gaEcl.getChildOfAccess();
	}
	
	public ParserRule getChildOfRule() {
		return getChildOfAccess().getRule();
	}
	
	//DescendantOf:
	//	LT constraint=FocusConcept;
	public EclGrammarAccess.DescendantOfElements getDescendantOfAccess() {
		return gaEcl.getDescendantOfAccess();
	}
	
	public ParserRule getDescendantOfRule() {
		return getDescendantOfAccess().getRule();
	}
	
	//DescendantOrSelfOf:
	//	DBL_LT constraint=FocusConcept;
	public EclGrammarAccess.DescendantOrSelfOfElements getDescendantOrSelfOfAccess() {
		return gaEcl.getDescendantOrSelfOfAccess();
	}
	
	public ParserRule getDescendantOrSelfOfRule() {
		return getDescendantOrSelfOfAccess().getRule();
	}
	
	//ParentOf:
	//	GT_EM constraint=FocusConcept;
	public EclGrammarAccess.ParentOfElements getParentOfAccess() {
		return gaEcl.getParentOfAccess();
	}
	
	public ParserRule getParentOfRule() {
		return getParentOfAccess().getRule();
	}
	
	//AncestorOf:
	//	GT constraint=FocusConcept;
	public EclGrammarAccess.AncestorOfElements getAncestorOfAccess() {
		return gaEcl.getAncestorOfAccess();
	}
	
	public ParserRule getAncestorOfRule() {
		return getAncestorOfAccess().getRule();
	}
	
	//AncestorOrSelfOf:
	//	DBL_GT constraint=FocusConcept;
	public EclGrammarAccess.AncestorOrSelfOfElements getAncestorOrSelfOfAccess() {
		return gaEcl.getAncestorOrSelfOfAccess();
	}
	
	public ParserRule getAncestorOrSelfOfRule() {
		return getAncestorOrSelfOfAccess().getRule();
	}
	
	//MemberOf:
	//	CARET constraint=(ConceptReference | Any | NestedExpression);
	public EclGrammarAccess.MemberOfElements getMemberOfAccess() {
		return gaEcl.getMemberOfAccess();
	}
	
	public ParserRule getMemberOfRule() {
		return getMemberOfAccess().getRule();
	}
	
	//ConceptReference:
	//	id=SnomedIdentifier term=TERM_STRING?;
	public EclGrammarAccess.ConceptReferenceElements getConceptReferenceAccess() {
		return gaEcl.getConceptReferenceAccess();
	}
	
	public ParserRule getConceptReferenceRule() {
		return getConceptReferenceAccess().getRule();
	}
	
	//Any:
	//	WILDCARD {Any};
	public EclGrammarAccess.AnyElements getAnyAccess() {
		return gaEcl.getAnyAccess();
	}
	
	public ParserRule getAnyRule() {
		return getAnyAccess().getRule();
	}
	
	//Refinement:
	//	OrRefinement;
	public EclGrammarAccess.RefinementElements getRefinementAccess() {
		return gaEcl.getRefinementAccess();
	}
	
	public ParserRule getRefinementRule() {
		return getRefinementAccess().getRule();
	}
	
	//OrRefinement Refinement:
	//	AndRefinement -> ({OrRefinement.left=current} 'OR' right=AndRefinement)*;
	public EclGrammarAccess.OrRefinementElements getOrRefinementAccess() {
		return gaEcl.getOrRefinementAccess();
	}
	
	public ParserRule getOrRefinementRule() {
		return getOrRefinementAccess().getRule();
	}
	
	//AndRefinement Refinement:
	//	SubRefinement -> ({AndRefinement.left=current} ('AND' | ',') right=SubRefinement)*;
	public EclGrammarAccess.AndRefinementElements getAndRefinementAccess() {
		return gaEcl.getAndRefinementAccess();
	}
	
	public ParserRule getAndRefinementRule() {
		return getAndRefinementAccess().getRule();
	}
	
	//SubRefinement Refinement:
	//	AttributeConstraint | AttributeGroup | NestedRefinement;
	public EclGrammarAccess.SubRefinementElements getSubRefinementAccess() {
		return gaEcl.getSubRefinementAccess();
	}
	
	public ParserRule getSubRefinementRule() {
		return getSubRefinementAccess().getRule();
	}
	
	//NestedRefinement:
	//	ROUND_OPEN nested=Refinement ROUND_CLOSE;
	public EclGrammarAccess.NestedRefinementElements getNestedRefinementAccess() {
		return gaEcl.getNestedRefinementAccess();
	}
	
	public ParserRule getNestedRefinementRule() {
		return getNestedRefinementAccess().getRule();
	}
	
	//AttributeGroup:
	//	cardinality=Cardinality? CURLY_OPEN refinement=AttributeSet CURLY_CLOSE;
	public EclGrammarAccess.AttributeGroupElements getAttributeGroupAccess() {
		return gaEcl.getAttributeGroupAccess();
	}
	
	public ParserRule getAttributeGroupRule() {
		return getAttributeGroupAccess().getRule();
	}
	
	//AttributeSet Refinement:
	//	OrAttributeSet;
	public EclGrammarAccess.AttributeSetElements getAttributeSetAccess() {
		return gaEcl.getAttributeSetAccess();
	}
	
	public ParserRule getAttributeSetRule() {
		return getAttributeSetAccess().getRule();
	}
	
	//OrAttributeSet Refinement:
	//	AndAttributeSet ({OrRefinement.left=current} 'OR' right=AndAttributeSet)*;
	public EclGrammarAccess.OrAttributeSetElements getOrAttributeSetAccess() {
		return gaEcl.getOrAttributeSetAccess();
	}
	
	public ParserRule getOrAttributeSetRule() {
		return getOrAttributeSetAccess().getRule();
	}
	
	//AndAttributeSet Refinement:
	//	SubAttributeSet ({AndRefinement.left=current} ('AND' | ',') right=SubAttributeSet)*;
	public EclGrammarAccess.AndAttributeSetElements getAndAttributeSetAccess() {
		return gaEcl.getAndAttributeSetAccess();
	}
	
	public ParserRule getAndAttributeSetRule() {
		return getAndAttributeSetAccess().getRule();
	}
	
	//SubAttributeSet Refinement:
	//	AttributeConstraint | NestedAttributeSet;
	public EclGrammarAccess.SubAttributeSetElements getSubAttributeSetAccess() {
		return gaEcl.getSubAttributeSetAccess();
	}
	
	public ParserRule getSubAttributeSetRule() {
		return getSubAttributeSetAccess().getRule();
	}
	
	//NestedAttributeSet NestedRefinement:
	//	ROUND_OPEN nested=AttributeSet ROUND_CLOSE;
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
	//	StringValueEquals
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
	
	//terminal NOT:
	//	'!';
	public TerminalRule getNOTRule() {
		return gaEcl.getNOTRule();
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
