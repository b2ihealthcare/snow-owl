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
package com.b2international.snowowl.snomed.ecl.services;

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
public class EclGrammarAccess extends AbstractGrammarElementFinder {
	
	public class ExpressionConstraintElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.ExpressionConstraint");
		private final RuleCall cOrExpressionConstraintParserRuleCall = (RuleCall)rule.eContents().get(1);
		
		//ExpressionConstraint:
		//	OrExpressionConstraint;
		@Override public ParserRule getRule() { return rule; }
		
		//OrExpressionConstraint
		public RuleCall getOrExpressionConstraintParserRuleCall() { return cOrExpressionConstraintParserRuleCall; }
	}
	public class OrExpressionConstraintElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.OrExpressionConstraint");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cAndExpressionConstraintParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cOrExpressionConstraintLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final RuleCall cORTerminalRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightAndExpressionConstraintParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//OrExpressionConstraint ExpressionConstraint:
		//	AndExpressionConstraint ({OrExpressionConstraint.left=current} OR right=AndExpressionConstraint)*;
		@Override public ParserRule getRule() { return rule; }
		
		//AndExpressionConstraint ({OrExpressionConstraint.left=current} OR right=AndExpressionConstraint)*
		public Group getGroup() { return cGroup; }
		
		//AndExpressionConstraint
		public RuleCall getAndExpressionConstraintParserRuleCall_0() { return cAndExpressionConstraintParserRuleCall_0; }
		
		//({OrExpressionConstraint.left=current} OR right=AndExpressionConstraint)*
		public Group getGroup_1() { return cGroup_1; }
		
		//{OrExpressionConstraint.left=current}
		public Action getOrExpressionConstraintLeftAction_1_0() { return cOrExpressionConstraintLeftAction_1_0; }
		
		//OR
		public RuleCall getORTerminalRuleCall_1_1() { return cORTerminalRuleCall_1_1; }
		
		//right=AndExpressionConstraint
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		//AndExpressionConstraint
		public RuleCall getRightAndExpressionConstraintParserRuleCall_1_2_0() { return cRightAndExpressionConstraintParserRuleCall_1_2_0; }
	}
	public class AndExpressionConstraintElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AndExpressionConstraint");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cExclusionExpressionConstraintParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cAndExpressionConstraintLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final RuleCall cAndOperatorParserRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightExclusionExpressionConstraintParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//AndExpressionConstraint ExpressionConstraint:
		//	ExclusionExpressionConstraint ({AndExpressionConstraint.left=current} AndOperator
		//	right=ExclusionExpressionConstraint)*;
		@Override public ParserRule getRule() { return rule; }
		
		//ExclusionExpressionConstraint ({AndExpressionConstraint.left=current} AndOperator right=ExclusionExpressionConstraint)*
		public Group getGroup() { return cGroup; }
		
		//ExclusionExpressionConstraint
		public RuleCall getExclusionExpressionConstraintParserRuleCall_0() { return cExclusionExpressionConstraintParserRuleCall_0; }
		
		//({AndExpressionConstraint.left=current} AndOperator right=ExclusionExpressionConstraint)*
		public Group getGroup_1() { return cGroup_1; }
		
		//{AndExpressionConstraint.left=current}
		public Action getAndExpressionConstraintLeftAction_1_0() { return cAndExpressionConstraintLeftAction_1_0; }
		
		//AndOperator
		public RuleCall getAndOperatorParserRuleCall_1_1() { return cAndOperatorParserRuleCall_1_1; }
		
		//right=ExclusionExpressionConstraint
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		//ExclusionExpressionConstraint
		public RuleCall getRightExclusionExpressionConstraintParserRuleCall_1_2_0() { return cRightExclusionExpressionConstraintParserRuleCall_1_2_0; }
	}
	public class ExclusionExpressionConstraintElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.ExclusionExpressionConstraint");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cRefinedExpressionConstraintParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cExclusionExpressionConstraintLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final RuleCall cMINUSTerminalRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightRefinedExpressionConstraintParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//ExclusionExpressionConstraint ExpressionConstraint:
		//	RefinedExpressionConstraint ({ExclusionExpressionConstraint.left=current} MINUS right=RefinedExpressionConstraint)?;
		@Override public ParserRule getRule() { return rule; }
		
		//RefinedExpressionConstraint ({ExclusionExpressionConstraint.left=current} MINUS right=RefinedExpressionConstraint)?
		public Group getGroup() { return cGroup; }
		
		//RefinedExpressionConstraint
		public RuleCall getRefinedExpressionConstraintParserRuleCall_0() { return cRefinedExpressionConstraintParserRuleCall_0; }
		
		//({ExclusionExpressionConstraint.left=current} MINUS right=RefinedExpressionConstraint)?
		public Group getGroup_1() { return cGroup_1; }
		
		//{ExclusionExpressionConstraint.left=current}
		public Action getExclusionExpressionConstraintLeftAction_1_0() { return cExclusionExpressionConstraintLeftAction_1_0; }
		
		//MINUS
		public RuleCall getMINUSTerminalRuleCall_1_1() { return cMINUSTerminalRuleCall_1_1; }
		
		//right=RefinedExpressionConstraint
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		//RefinedExpressionConstraint
		public RuleCall getRightRefinedExpressionConstraintParserRuleCall_1_2_0() { return cRightRefinedExpressionConstraintParserRuleCall_1_2_0; }
	}
	public class RefinedExpressionConstraintElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.RefinedExpressionConstraint");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cDottedExpressionConstraintParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cRefinedExpressionConstraintConstraintAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final RuleCall cCOLONTerminalRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final Assignment cRefinementAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRefinementRefinementParserRuleCall_1_2_0 = (RuleCall)cRefinementAssignment_1_2.eContents().get(0);
		
		//RefinedExpressionConstraint ExpressionConstraint:
		//	DottedExpressionConstraint ({RefinedExpressionConstraint.constraint=current} COLON refinement=Refinement)?;
		@Override public ParserRule getRule() { return rule; }
		
		//DottedExpressionConstraint ({RefinedExpressionConstraint.constraint=current} COLON refinement=Refinement)?
		public Group getGroup() { return cGroup; }
		
		//DottedExpressionConstraint
		public RuleCall getDottedExpressionConstraintParserRuleCall_0() { return cDottedExpressionConstraintParserRuleCall_0; }
		
		//({RefinedExpressionConstraint.constraint=current} COLON refinement=Refinement)?
		public Group getGroup_1() { return cGroup_1; }
		
		//{RefinedExpressionConstraint.constraint=current}
		public Action getRefinedExpressionConstraintConstraintAction_1_0() { return cRefinedExpressionConstraintConstraintAction_1_0; }
		
		//COLON
		public RuleCall getCOLONTerminalRuleCall_1_1() { return cCOLONTerminalRuleCall_1_1; }
		
		//refinement=Refinement
		public Assignment getRefinementAssignment_1_2() { return cRefinementAssignment_1_2; }
		
		//Refinement
		public RuleCall getRefinementRefinementParserRuleCall_1_2_0() { return cRefinementRefinementParserRuleCall_1_2_0; }
	}
	public class DottedExpressionConstraintElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DottedExpressionConstraint");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cSimpleExpressionConstraintParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cDottedExpressionConstraintConstraintAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final RuleCall cDOTTerminalRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final Assignment cAttributeAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cAttributeAttributeParserRuleCall_1_2_0 = (RuleCall)cAttributeAssignment_1_2.eContents().get(0);
		
		//DottedExpressionConstraint ExpressionConstraint:
		//	SimpleExpressionConstraint ({DottedExpressionConstraint.constraint=current} DOT attribute=Attribute)*;
		@Override public ParserRule getRule() { return rule; }
		
		//SimpleExpressionConstraint ({DottedExpressionConstraint.constraint=current} DOT attribute=Attribute)*
		public Group getGroup() { return cGroup; }
		
		//SimpleExpressionConstraint
		public RuleCall getSimpleExpressionConstraintParserRuleCall_0() { return cSimpleExpressionConstraintParserRuleCall_0; }
		
		//({DottedExpressionConstraint.constraint=current} DOT attribute=Attribute)*
		public Group getGroup_1() { return cGroup_1; }
		
		//{DottedExpressionConstraint.constraint=current}
		public Action getDottedExpressionConstraintConstraintAction_1_0() { return cDottedExpressionConstraintConstraintAction_1_0; }
		
		//DOT
		public RuleCall getDOTTerminalRuleCall_1_1() { return cDOTTerminalRuleCall_1_1; }
		
		//attribute=Attribute
		public Assignment getAttributeAssignment_1_2() { return cAttributeAssignment_1_2; }
		
		//Attribute
		public RuleCall getAttributeAttributeParserRuleCall_1_2_0() { return cAttributeAttributeParserRuleCall_1_2_0; }
	}
	public class SimpleExpressionConstraintElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.SimpleExpressionConstraint");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cChildOfParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cDescendantOfParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cDescendantOrSelfOfParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cParentOfParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		private final RuleCall cAncestorOfParserRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		private final RuleCall cAncestorOrSelfOfParserRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		private final RuleCall cFocusConceptParserRuleCall_6 = (RuleCall)cAlternatives.eContents().get(6);
		
		//SimpleExpressionConstraint ExpressionConstraint:
		//	ChildOf | DescendantOf | DescendantOrSelfOf | ParentOf | AncestorOf | AncestorOrSelfOf | FocusConcept;
		@Override public ParserRule getRule() { return rule; }
		
		//ChildOf | DescendantOf | DescendantOrSelfOf | ParentOf | AncestorOf | AncestorOrSelfOf | FocusConcept
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//ChildOf
		public RuleCall getChildOfParserRuleCall_0() { return cChildOfParserRuleCall_0; }
		
		//DescendantOf
		public RuleCall getDescendantOfParserRuleCall_1() { return cDescendantOfParserRuleCall_1; }
		
		//DescendantOrSelfOf
		public RuleCall getDescendantOrSelfOfParserRuleCall_2() { return cDescendantOrSelfOfParserRuleCall_2; }
		
		//ParentOf
		public RuleCall getParentOfParserRuleCall_3() { return cParentOfParserRuleCall_3; }
		
		//AncestorOf
		public RuleCall getAncestorOfParserRuleCall_4() { return cAncestorOfParserRuleCall_4; }
		
		//AncestorOrSelfOf
		public RuleCall getAncestorOrSelfOfParserRuleCall_5() { return cAncestorOrSelfOfParserRuleCall_5; }
		
		//FocusConcept
		public RuleCall getFocusConceptParserRuleCall_6() { return cFocusConceptParserRuleCall_6; }
	}
	public class FocusConceptElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.FocusConcept");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cMemberOfParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cConceptReferenceParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cAnyParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cNestedExpressionParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		//FocusConcept ExpressionConstraint:
		//	MemberOf | ConceptReference | Any | NestedExpression;
		@Override public ParserRule getRule() { return rule; }
		
		//MemberOf | ConceptReference | Any | NestedExpression
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//MemberOf
		public RuleCall getMemberOfParserRuleCall_0() { return cMemberOfParserRuleCall_0; }
		
		//ConceptReference
		public RuleCall getConceptReferenceParserRuleCall_1() { return cConceptReferenceParserRuleCall_1; }
		
		//Any
		public RuleCall getAnyParserRuleCall_2() { return cAnyParserRuleCall_2; }
		
		//NestedExpression
		public RuleCall getNestedExpressionParserRuleCall_3() { return cNestedExpressionParserRuleCall_3; }
	}
	public class ChildOfElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.ChildOf");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cLT_EMTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConstraintFocusConceptParserRuleCall_1_0 = (RuleCall)cConstraintAssignment_1.eContents().get(0);
		
		//ChildOf:
		//	LT_EM constraint=FocusConcept;
		@Override public ParserRule getRule() { return rule; }
		
		//LT_EM constraint=FocusConcept
		public Group getGroup() { return cGroup; }
		
		//LT_EM
		public RuleCall getLT_EMTerminalRuleCall_0() { return cLT_EMTerminalRuleCall_0; }
		
		//constraint=FocusConcept
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//FocusConcept
		public RuleCall getConstraintFocusConceptParserRuleCall_1_0() { return cConstraintFocusConceptParserRuleCall_1_0; }
	}
	public class DescendantOfElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DescendantOf");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cLTTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConstraintFocusConceptParserRuleCall_1_0 = (RuleCall)cConstraintAssignment_1.eContents().get(0);
		
		//DescendantOf:
		//	LT constraint=FocusConcept;
		@Override public ParserRule getRule() { return rule; }
		
		//LT constraint=FocusConcept
		public Group getGroup() { return cGroup; }
		
		//LT
		public RuleCall getLTTerminalRuleCall_0() { return cLTTerminalRuleCall_0; }
		
		//constraint=FocusConcept
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//FocusConcept
		public RuleCall getConstraintFocusConceptParserRuleCall_1_0() { return cConstraintFocusConceptParserRuleCall_1_0; }
	}
	public class DescendantOrSelfOfElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DescendantOrSelfOf");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cDBL_LTTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConstraintFocusConceptParserRuleCall_1_0 = (RuleCall)cConstraintAssignment_1.eContents().get(0);
		
		//DescendantOrSelfOf:
		//	DBL_LT constraint=FocusConcept;
		@Override public ParserRule getRule() { return rule; }
		
		//DBL_LT constraint=FocusConcept
		public Group getGroup() { return cGroup; }
		
		//DBL_LT
		public RuleCall getDBL_LTTerminalRuleCall_0() { return cDBL_LTTerminalRuleCall_0; }
		
		//constraint=FocusConcept
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//FocusConcept
		public RuleCall getConstraintFocusConceptParserRuleCall_1_0() { return cConstraintFocusConceptParserRuleCall_1_0; }
	}
	public class ParentOfElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.ParentOf");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cGT_EMTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConstraintFocusConceptParserRuleCall_1_0 = (RuleCall)cConstraintAssignment_1.eContents().get(0);
		
		//ParentOf:
		//	GT_EM constraint=FocusConcept;
		@Override public ParserRule getRule() { return rule; }
		
		//GT_EM constraint=FocusConcept
		public Group getGroup() { return cGroup; }
		
		//GT_EM
		public RuleCall getGT_EMTerminalRuleCall_0() { return cGT_EMTerminalRuleCall_0; }
		
		//constraint=FocusConcept
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//FocusConcept
		public RuleCall getConstraintFocusConceptParserRuleCall_1_0() { return cConstraintFocusConceptParserRuleCall_1_0; }
	}
	public class AncestorOfElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AncestorOf");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cGTTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConstraintFocusConceptParserRuleCall_1_0 = (RuleCall)cConstraintAssignment_1.eContents().get(0);
		
		//AncestorOf:
		//	GT constraint=FocusConcept;
		@Override public ParserRule getRule() { return rule; }
		
		//GT constraint=FocusConcept
		public Group getGroup() { return cGroup; }
		
		//GT
		public RuleCall getGTTerminalRuleCall_0() { return cGTTerminalRuleCall_0; }
		
		//constraint=FocusConcept
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//FocusConcept
		public RuleCall getConstraintFocusConceptParserRuleCall_1_0() { return cConstraintFocusConceptParserRuleCall_1_0; }
	}
	public class AncestorOrSelfOfElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AncestorOrSelfOf");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cDBL_GTTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConstraintFocusConceptParserRuleCall_1_0 = (RuleCall)cConstraintAssignment_1.eContents().get(0);
		
		//AncestorOrSelfOf:
		//	DBL_GT constraint=FocusConcept;
		@Override public ParserRule getRule() { return rule; }
		
		//DBL_GT constraint=FocusConcept
		public Group getGroup() { return cGroup; }
		
		//DBL_GT
		public RuleCall getDBL_GTTerminalRuleCall_0() { return cDBL_GTTerminalRuleCall_0; }
		
		//constraint=FocusConcept
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//FocusConcept
		public RuleCall getConstraintFocusConceptParserRuleCall_1_0() { return cConstraintFocusConceptParserRuleCall_1_0; }
	}
	public class MemberOfElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.MemberOf");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cCARETTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final Alternatives cConstraintAlternatives_1_0 = (Alternatives)cConstraintAssignment_1.eContents().get(0);
		private final RuleCall cConstraintConceptReferenceParserRuleCall_1_0_0 = (RuleCall)cConstraintAlternatives_1_0.eContents().get(0);
		private final RuleCall cConstraintAnyParserRuleCall_1_0_1 = (RuleCall)cConstraintAlternatives_1_0.eContents().get(1);
		
		//MemberOf:
		//	CARET constraint=(ConceptReference | Any);
		@Override public ParserRule getRule() { return rule; }
		
		//CARET constraint=(ConceptReference | Any)
		public Group getGroup() { return cGroup; }
		
		//CARET
		public RuleCall getCARETTerminalRuleCall_0() { return cCARETTerminalRuleCall_0; }
		
		//constraint=(ConceptReference | Any)
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//(ConceptReference | Any)
		public Alternatives getConstraintAlternatives_1_0() { return cConstraintAlternatives_1_0; }
		
		//ConceptReference
		public RuleCall getConstraintConceptReferenceParserRuleCall_1_0_0() { return cConstraintConceptReferenceParserRuleCall_1_0_0; }
		
		//Any
		public RuleCall getConstraintAnyParserRuleCall_1_0_1() { return cConstraintAnyParserRuleCall_1_0_1; }
	}
	public class ConceptReferenceElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.ConceptReference");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cIdAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cIdSnomedIdentifierParserRuleCall_0_0 = (RuleCall)cIdAssignment_0.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cPIPETerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final Assignment cTermAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cTermTermParserRuleCall_1_1_0 = (RuleCall)cTermAssignment_1_1.eContents().get(0);
		private final RuleCall cPIPETerminalRuleCall_1_2 = (RuleCall)cGroup_1.eContents().get(2);
		
		//ConceptReference:
		//	id=SnomedIdentifier (PIPE term=Term PIPE)?;
		@Override public ParserRule getRule() { return rule; }
		
		//id=SnomedIdentifier (PIPE term=Term PIPE)?
		public Group getGroup() { return cGroup; }
		
		//id=SnomedIdentifier
		public Assignment getIdAssignment_0() { return cIdAssignment_0; }
		
		//SnomedIdentifier
		public RuleCall getIdSnomedIdentifierParserRuleCall_0_0() { return cIdSnomedIdentifierParserRuleCall_0_0; }
		
		//(PIPE term=Term PIPE)?
		public Group getGroup_1() { return cGroup_1; }
		
		//PIPE
		public RuleCall getPIPETerminalRuleCall_1_0() { return cPIPETerminalRuleCall_1_0; }
		
		//term=Term
		public Assignment getTermAssignment_1_1() { return cTermAssignment_1_1; }
		
		//Term
		public RuleCall getTermTermParserRuleCall_1_1_0() { return cTermTermParserRuleCall_1_1_0; }
		
		//PIPE
		public RuleCall getPIPETerminalRuleCall_1_2() { return cPIPETerminalRuleCall_1_2; }
	}
	public class AnyElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.Any");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cWILDCARDTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Action cAnyAction_1 = (Action)cGroup.eContents().get(1);
		
		//Any:
		//	WILDCARD {Any};
		@Override public ParserRule getRule() { return rule; }
		
		//WILDCARD {Any}
		public Group getGroup() { return cGroup; }
		
		//WILDCARD
		public RuleCall getWILDCARDTerminalRuleCall_0() { return cWILDCARDTerminalRuleCall_0; }
		
		//{Any}
		public Action getAnyAction_1() { return cAnyAction_1; }
	}
	public class RefinementElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.Refinement");
		private final RuleCall cOrRefinementParserRuleCall = (RuleCall)rule.eContents().get(1);
		
		//Refinement:
		//	OrRefinement;
		@Override public ParserRule getRule() { return rule; }
		
		//OrRefinement
		public RuleCall getOrRefinementParserRuleCall() { return cOrRefinementParserRuleCall; }
	}
	public class OrRefinementElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.OrRefinement");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cAndRefinementParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		private final Action cOrRefinementLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		private final RuleCall cORTerminalRuleCall_1_0_1 = (RuleCall)cGroup_1_0.eContents().get(1);
		private final Assignment cRightAssignment_1_0_2 = (Assignment)cGroup_1_0.eContents().get(2);
		private final RuleCall cRightAndRefinementParserRuleCall_1_0_2_0 = (RuleCall)cRightAssignment_1_0_2.eContents().get(0);
		
		//OrRefinement Refinement:
		//	AndRefinement -> ({OrRefinement.left=current} OR right=AndRefinement)*;
		@Override public ParserRule getRule() { return rule; }
		
		//AndRefinement -> ({OrRefinement.left=current} OR right=AndRefinement)*
		public Group getGroup() { return cGroup; }
		
		//AndRefinement
		public RuleCall getAndRefinementParserRuleCall_0() { return cAndRefinementParserRuleCall_0; }
		
		//-> ({OrRefinement.left=current} OR right=AndRefinement)*
		public Group getGroup_1() { return cGroup_1; }
		
		//{OrRefinement.left=current} OR right=AndRefinement
		public Group getGroup_1_0() { return cGroup_1_0; }
		
		//{OrRefinement.left=current}
		public Action getOrRefinementLeftAction_1_0_0() { return cOrRefinementLeftAction_1_0_0; }
		
		//OR
		public RuleCall getORTerminalRuleCall_1_0_1() { return cORTerminalRuleCall_1_0_1; }
		
		//right=AndRefinement
		public Assignment getRightAssignment_1_0_2() { return cRightAssignment_1_0_2; }
		
		//AndRefinement
		public RuleCall getRightAndRefinementParserRuleCall_1_0_2_0() { return cRightAndRefinementParserRuleCall_1_0_2_0; }
	}
	public class AndRefinementElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AndRefinement");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cSubRefinementParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		private final Action cAndRefinementLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		private final RuleCall cAndOperatorParserRuleCall_1_0_1 = (RuleCall)cGroup_1_0.eContents().get(1);
		private final Assignment cRightAssignment_1_0_2 = (Assignment)cGroup_1_0.eContents().get(2);
		private final RuleCall cRightSubRefinementParserRuleCall_1_0_2_0 = (RuleCall)cRightAssignment_1_0_2.eContents().get(0);
		
		//AndRefinement Refinement:
		//	SubRefinement -> ({AndRefinement.left=current} AndOperator right=SubRefinement)*;
		@Override public ParserRule getRule() { return rule; }
		
		//SubRefinement -> ({AndRefinement.left=current} AndOperator right=SubRefinement)*
		public Group getGroup() { return cGroup; }
		
		//SubRefinement
		public RuleCall getSubRefinementParserRuleCall_0() { return cSubRefinementParserRuleCall_0; }
		
		//-> ({AndRefinement.left=current} AndOperator right=SubRefinement)*
		public Group getGroup_1() { return cGroup_1; }
		
		//{AndRefinement.left=current} AndOperator right=SubRefinement
		public Group getGroup_1_0() { return cGroup_1_0; }
		
		//{AndRefinement.left=current}
		public Action getAndRefinementLeftAction_1_0_0() { return cAndRefinementLeftAction_1_0_0; }
		
		//AndOperator
		public RuleCall getAndOperatorParserRuleCall_1_0_1() { return cAndOperatorParserRuleCall_1_0_1; }
		
		//right=SubRefinement
		public Assignment getRightAssignment_1_0_2() { return cRightAssignment_1_0_2; }
		
		//SubRefinement
		public RuleCall getRightSubRefinementParserRuleCall_1_0_2_0() { return cRightSubRefinementParserRuleCall_1_0_2_0; }
	}
	public class SubRefinementElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.SubRefinement");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cAttributeConstraintParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cAttributeGroupParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cNestedRefinementParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		//SubRefinement Refinement:
		//	AttributeConstraint | AttributeGroup | NestedRefinement;
		@Override public ParserRule getRule() { return rule; }
		
		//AttributeConstraint | AttributeGroup | NestedRefinement
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//AttributeConstraint
		public RuleCall getAttributeConstraintParserRuleCall_0() { return cAttributeConstraintParserRuleCall_0; }
		
		//AttributeGroup
		public RuleCall getAttributeGroupParserRuleCall_1() { return cAttributeGroupParserRuleCall_1; }
		
		//NestedRefinement
		public RuleCall getNestedRefinementParserRuleCall_2() { return cNestedRefinementParserRuleCall_2; }
	}
	public class NestedRefinementElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.NestedRefinement");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cROUND_OPENTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cNestedAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNestedRefinementParserRuleCall_1_0 = (RuleCall)cNestedAssignment_1.eContents().get(0);
		private final RuleCall cROUND_CLOSETerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		
		//NestedRefinement:
		//	ROUND_OPEN nested=Refinement ROUND_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//ROUND_OPEN nested=Refinement ROUND_CLOSE
		public Group getGroup() { return cGroup; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_0() { return cROUND_OPENTerminalRuleCall_0; }
		
		//nested=Refinement
		public Assignment getNestedAssignment_1() { return cNestedAssignment_1; }
		
		//Refinement
		public RuleCall getNestedRefinementParserRuleCall_1_0() { return cNestedRefinementParserRuleCall_1_0; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_2() { return cROUND_CLOSETerminalRuleCall_2; }
	}
	public class AttributeGroupElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AttributeGroup");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cCardinalityAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cCardinalityCardinalityParserRuleCall_0_0 = (RuleCall)cCardinalityAssignment_0.eContents().get(0);
		private final RuleCall cCURLY_OPENTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cRefinementAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cRefinementAttributeSetParserRuleCall_2_0 = (RuleCall)cRefinementAssignment_2.eContents().get(0);
		private final RuleCall cCURLY_CLOSETerminalRuleCall_3 = (RuleCall)cGroup.eContents().get(3);
		
		//AttributeGroup:
		//	cardinality=Cardinality? CURLY_OPEN refinement=AttributeSet CURLY_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//cardinality=Cardinality? CURLY_OPEN refinement=AttributeSet CURLY_CLOSE
		public Group getGroup() { return cGroup; }
		
		//cardinality=Cardinality?
		public Assignment getCardinalityAssignment_0() { return cCardinalityAssignment_0; }
		
		//Cardinality
		public RuleCall getCardinalityCardinalityParserRuleCall_0_0() { return cCardinalityCardinalityParserRuleCall_0_0; }
		
		//CURLY_OPEN
		public RuleCall getCURLY_OPENTerminalRuleCall_1() { return cCURLY_OPENTerminalRuleCall_1; }
		
		//refinement=AttributeSet
		public Assignment getRefinementAssignment_2() { return cRefinementAssignment_2; }
		
		//AttributeSet
		public RuleCall getRefinementAttributeSetParserRuleCall_2_0() { return cRefinementAttributeSetParserRuleCall_2_0; }
		
		//CURLY_CLOSE
		public RuleCall getCURLY_CLOSETerminalRuleCall_3() { return cCURLY_CLOSETerminalRuleCall_3; }
	}
	public class AttributeSetElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AttributeSet");
		private final RuleCall cOrAttributeSetParserRuleCall = (RuleCall)rule.eContents().get(1);
		
		//AttributeSet Refinement:
		//	OrAttributeSet;
		@Override public ParserRule getRule() { return rule; }
		
		//OrAttributeSet
		public RuleCall getOrAttributeSetParserRuleCall() { return cOrAttributeSetParserRuleCall; }
	}
	public class OrAttributeSetElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.OrAttributeSet");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cAndAttributeSetParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cOrRefinementLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final RuleCall cORTerminalRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightAndAttributeSetParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//OrAttributeSet Refinement:
		//	AndAttributeSet ({OrRefinement.left=current} OR right=AndAttributeSet)*;
		@Override public ParserRule getRule() { return rule; }
		
		//AndAttributeSet ({OrRefinement.left=current} OR right=AndAttributeSet)*
		public Group getGroup() { return cGroup; }
		
		//AndAttributeSet
		public RuleCall getAndAttributeSetParserRuleCall_0() { return cAndAttributeSetParserRuleCall_0; }
		
		//({OrRefinement.left=current} OR right=AndAttributeSet)*
		public Group getGroup_1() { return cGroup_1; }
		
		//{OrRefinement.left=current}
		public Action getOrRefinementLeftAction_1_0() { return cOrRefinementLeftAction_1_0; }
		
		//OR
		public RuleCall getORTerminalRuleCall_1_1() { return cORTerminalRuleCall_1_1; }
		
		//right=AndAttributeSet
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		//AndAttributeSet
		public RuleCall getRightAndAttributeSetParserRuleCall_1_2_0() { return cRightAndAttributeSetParserRuleCall_1_2_0; }
	}
	public class AndAttributeSetElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AndAttributeSet");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cSubAttributeSetParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cAndRefinementLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final RuleCall cAndOperatorParserRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightSubAttributeSetParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//AndAttributeSet Refinement:
		//	SubAttributeSet ({AndRefinement.left=current} AndOperator right=SubAttributeSet)*;
		@Override public ParserRule getRule() { return rule; }
		
		//SubAttributeSet ({AndRefinement.left=current} AndOperator right=SubAttributeSet)*
		public Group getGroup() { return cGroup; }
		
		//SubAttributeSet
		public RuleCall getSubAttributeSetParserRuleCall_0() { return cSubAttributeSetParserRuleCall_0; }
		
		//({AndRefinement.left=current} AndOperator right=SubAttributeSet)*
		public Group getGroup_1() { return cGroup_1; }
		
		//{AndRefinement.left=current}
		public Action getAndRefinementLeftAction_1_0() { return cAndRefinementLeftAction_1_0; }
		
		//AndOperator
		public RuleCall getAndOperatorParserRuleCall_1_1() { return cAndOperatorParserRuleCall_1_1; }
		
		//right=SubAttributeSet
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		//SubAttributeSet
		public RuleCall getRightSubAttributeSetParserRuleCall_1_2_0() { return cRightSubAttributeSetParserRuleCall_1_2_0; }
	}
	public class SubAttributeSetElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.SubAttributeSet");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cAttributeConstraintParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cNestedAttributeSetParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//SubAttributeSet Refinement:
		//	AttributeConstraint | NestedAttributeSet;
		@Override public ParserRule getRule() { return rule; }
		
		//AttributeConstraint | NestedAttributeSet
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//AttributeConstraint
		public RuleCall getAttributeConstraintParserRuleCall_0() { return cAttributeConstraintParserRuleCall_0; }
		
		//NestedAttributeSet
		public RuleCall getNestedAttributeSetParserRuleCall_1() { return cNestedAttributeSetParserRuleCall_1; }
	}
	public class NestedAttributeSetElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.NestedAttributeSet");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cROUND_OPENTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cNestedAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNestedAttributeSetParserRuleCall_1_0 = (RuleCall)cNestedAssignment_1.eContents().get(0);
		private final RuleCall cROUND_CLOSETerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		
		//NestedAttributeSet NestedRefinement:
		//	ROUND_OPEN nested=AttributeSet ROUND_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//ROUND_OPEN nested=AttributeSet ROUND_CLOSE
		public Group getGroup() { return cGroup; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_0() { return cROUND_OPENTerminalRuleCall_0; }
		
		//nested=AttributeSet
		public Assignment getNestedAssignment_1() { return cNestedAssignment_1; }
		
		//AttributeSet
		public RuleCall getNestedAttributeSetParserRuleCall_1_0() { return cNestedAttributeSetParserRuleCall_1_0; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_2() { return cROUND_CLOSETerminalRuleCall_2; }
	}
	public class AttributeConstraintElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AttributeConstraint");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cCardinalityAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cCardinalityCardinalityParserRuleCall_0_0 = (RuleCall)cCardinalityAssignment_0.eContents().get(0);
		private final Assignment cReversedAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cReversedREVERSEDTerminalRuleCall_1_0 = (RuleCall)cReversedAssignment_1.eContents().get(0);
		private final Assignment cAttributeAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cAttributeAttributeParserRuleCall_2_0 = (RuleCall)cAttributeAssignment_2.eContents().get(0);
		private final Assignment cComparisonAssignment_3 = (Assignment)cGroup.eContents().get(3);
		private final RuleCall cComparisonComparisonParserRuleCall_3_0 = (RuleCall)cComparisonAssignment_3.eContents().get(0);
		
		//AttributeConstraint:
		//	cardinality=Cardinality? reversed?=REVERSED? attribute=Attribute comparison=Comparison;
		@Override public ParserRule getRule() { return rule; }
		
		//cardinality=Cardinality? reversed?=REVERSED? attribute=Attribute comparison=Comparison
		public Group getGroup() { return cGroup; }
		
		//cardinality=Cardinality?
		public Assignment getCardinalityAssignment_0() { return cCardinalityAssignment_0; }
		
		//Cardinality
		public RuleCall getCardinalityCardinalityParserRuleCall_0_0() { return cCardinalityCardinalityParserRuleCall_0_0; }
		
		//reversed?=REVERSED?
		public Assignment getReversedAssignment_1() { return cReversedAssignment_1; }
		
		//REVERSED
		public RuleCall getReversedREVERSEDTerminalRuleCall_1_0() { return cReversedREVERSEDTerminalRuleCall_1_0; }
		
		//attribute=Attribute
		public Assignment getAttributeAssignment_2() { return cAttributeAssignment_2; }
		
		//Attribute
		public RuleCall getAttributeAttributeParserRuleCall_2_0() { return cAttributeAttributeParserRuleCall_2_0; }
		
		//comparison=Comparison
		public Assignment getComparisonAssignment_3() { return cComparisonAssignment_3; }
		
		//Comparison
		public RuleCall getComparisonComparisonParserRuleCall_3_0() { return cComparisonComparisonParserRuleCall_3_0; }
	}
	public class AttributeElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.Attribute");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cAttributeDescendantOfParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cAttributeDescendantOrSelfOfParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cConceptReferenceParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cAnyParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		//Attribute ExpressionConstraint:
		//	AttributeDescendantOf | AttributeDescendantOrSelfOf | ConceptReference | Any;
		@Override public ParserRule getRule() { return rule; }
		
		//AttributeDescendantOf | AttributeDescendantOrSelfOf | ConceptReference | Any
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//AttributeDescendantOf
		public RuleCall getAttributeDescendantOfParserRuleCall_0() { return cAttributeDescendantOfParserRuleCall_0; }
		
		//AttributeDescendantOrSelfOf
		public RuleCall getAttributeDescendantOrSelfOfParserRuleCall_1() { return cAttributeDescendantOrSelfOfParserRuleCall_1; }
		
		//ConceptReference
		public RuleCall getConceptReferenceParserRuleCall_2() { return cConceptReferenceParserRuleCall_2; }
		
		//Any
		public RuleCall getAnyParserRuleCall_3() { return cAnyParserRuleCall_3; }
	}
	public class AttributeDescendantOfElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AttributeDescendantOf");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cLTTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final Alternatives cConstraintAlternatives_1_0 = (Alternatives)cConstraintAssignment_1.eContents().get(0);
		private final RuleCall cConstraintConceptReferenceParserRuleCall_1_0_0 = (RuleCall)cConstraintAlternatives_1_0.eContents().get(0);
		private final RuleCall cConstraintAnyParserRuleCall_1_0_1 = (RuleCall)cConstraintAlternatives_1_0.eContents().get(1);
		
		//AttributeDescendantOf DescendantOf:
		//	LT constraint=(ConceptReference | Any);
		@Override public ParserRule getRule() { return rule; }
		
		//LT constraint=(ConceptReference | Any)
		public Group getGroup() { return cGroup; }
		
		//LT
		public RuleCall getLTTerminalRuleCall_0() { return cLTTerminalRuleCall_0; }
		
		//constraint=(ConceptReference | Any)
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//(ConceptReference | Any)
		public Alternatives getConstraintAlternatives_1_0() { return cConstraintAlternatives_1_0; }
		
		//ConceptReference
		public RuleCall getConstraintConceptReferenceParserRuleCall_1_0_0() { return cConstraintConceptReferenceParserRuleCall_1_0_0; }
		
		//Any
		public RuleCall getConstraintAnyParserRuleCall_1_0_1() { return cConstraintAnyParserRuleCall_1_0_1; }
	}
	public class AttributeDescendantOrSelfOfElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AttributeDescendantOrSelfOf");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cDBL_LTTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final Alternatives cConstraintAlternatives_1_0 = (Alternatives)cConstraintAssignment_1.eContents().get(0);
		private final RuleCall cConstraintConceptReferenceParserRuleCall_1_0_0 = (RuleCall)cConstraintAlternatives_1_0.eContents().get(0);
		private final RuleCall cConstraintAnyParserRuleCall_1_0_1 = (RuleCall)cConstraintAlternatives_1_0.eContents().get(1);
		
		//AttributeDescendantOrSelfOf DescendantOrSelfOf:
		//	DBL_LT constraint=(ConceptReference | Any);
		@Override public ParserRule getRule() { return rule; }
		
		//DBL_LT constraint=(ConceptReference | Any)
		public Group getGroup() { return cGroup; }
		
		//DBL_LT
		public RuleCall getDBL_LTTerminalRuleCall_0() { return cDBL_LTTerminalRuleCall_0; }
		
		//constraint=(ConceptReference | Any)
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//(ConceptReference | Any)
		public Alternatives getConstraintAlternatives_1_0() { return cConstraintAlternatives_1_0; }
		
		//ConceptReference
		public RuleCall getConstraintConceptReferenceParserRuleCall_1_0_0() { return cConstraintConceptReferenceParserRuleCall_1_0_0; }
		
		//Any
		public RuleCall getConstraintAnyParserRuleCall_1_0_1() { return cConstraintAnyParserRuleCall_1_0_1; }
	}
	public class CardinalityElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.Cardinality");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cSQUARE_OPENTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cMinAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cMinNonNegativeIntegerParserRuleCall_1_0 = (RuleCall)cMinAssignment_1.eContents().get(0);
		private final RuleCall cTOTerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		private final Assignment cMaxAssignment_3 = (Assignment)cGroup.eContents().get(3);
		private final RuleCall cMaxMaxValueParserRuleCall_3_0 = (RuleCall)cMaxAssignment_3.eContents().get(0);
		private final RuleCall cSQUARE_CLOSETerminalRuleCall_4 = (RuleCall)cGroup.eContents().get(4);
		
		//Cardinality:
		//	SQUARE_OPEN min=NonNegativeInteger TO max=MaxValue SQUARE_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//SQUARE_OPEN min=NonNegativeInteger TO max=MaxValue SQUARE_CLOSE
		public Group getGroup() { return cGroup; }
		
		//SQUARE_OPEN
		public RuleCall getSQUARE_OPENTerminalRuleCall_0() { return cSQUARE_OPENTerminalRuleCall_0; }
		
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
		
		//SQUARE_CLOSE
		public RuleCall getSQUARE_CLOSETerminalRuleCall_4() { return cSQUARE_CLOSETerminalRuleCall_4; }
	}
	public class ComparisonElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.Comparison");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cAttributeComparisonParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cDataTypeComparisonParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//Comparison:
		//	AttributeComparison | DataTypeComparison;
		@Override public ParserRule getRule() { return rule; }
		
		//AttributeComparison | DataTypeComparison
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//AttributeComparison
		public RuleCall getAttributeComparisonParserRuleCall_0() { return cAttributeComparisonParserRuleCall_0; }
		
		//DataTypeComparison
		public RuleCall getDataTypeComparisonParserRuleCall_1() { return cDataTypeComparisonParserRuleCall_1; }
	}
	public class AttributeComparisonElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AttributeComparison");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cAttributeValueEqualsParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cAttributeValueNotEqualsParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//AttributeComparison:
		//	AttributeValueEquals | AttributeValueNotEquals;
		@Override public ParserRule getRule() { return rule; }
		
		//AttributeValueEquals | AttributeValueNotEquals
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//AttributeValueEquals
		public RuleCall getAttributeValueEqualsParserRuleCall_0() { return cAttributeValueEqualsParserRuleCall_0; }
		
		//AttributeValueNotEquals
		public RuleCall getAttributeValueNotEqualsParserRuleCall_1() { return cAttributeValueNotEqualsParserRuleCall_1; }
	}
	public class DataTypeComparisonElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DataTypeComparison");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cStringValueEqualsParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cStringValueNotEqualsParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cIntegerValueEqualsParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cIntegerValueNotEqualsParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		private final RuleCall cIntegerValueGreaterThanParserRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		private final RuleCall cIntegerValueGreaterThanEqualsParserRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		private final RuleCall cIntegerValueLessThanParserRuleCall_6 = (RuleCall)cAlternatives.eContents().get(6);
		private final RuleCall cIntegerValueLessThanEqualsParserRuleCall_7 = (RuleCall)cAlternatives.eContents().get(7);
		private final RuleCall cDecimalValueEqualsParserRuleCall_8 = (RuleCall)cAlternatives.eContents().get(8);
		private final RuleCall cDecimalValueNotEqualsParserRuleCall_9 = (RuleCall)cAlternatives.eContents().get(9);
		private final RuleCall cDecimalValueGreaterThanParserRuleCall_10 = (RuleCall)cAlternatives.eContents().get(10);
		private final RuleCall cDecimalValueGreaterThanEqualsParserRuleCall_11 = (RuleCall)cAlternatives.eContents().get(11);
		private final RuleCall cDecimalValueLessThanParserRuleCall_12 = (RuleCall)cAlternatives.eContents().get(12);
		private final RuleCall cDecimalValueLessThanEqualsParserRuleCall_13 = (RuleCall)cAlternatives.eContents().get(13);
		
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
		@Override public ParserRule getRule() { return rule; }
		
		//StringValueEquals | StringValueNotEquals | IntegerValueEquals | IntegerValueNotEquals | IntegerValueGreaterThan |
		//IntegerValueGreaterThanEquals | IntegerValueLessThan | IntegerValueLessThanEquals | DecimalValueEquals |
		//DecimalValueNotEquals | DecimalValueGreaterThan | DecimalValueGreaterThanEquals | DecimalValueLessThan |
		//DecimalValueLessThanEquals
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//StringValueEquals
		public RuleCall getStringValueEqualsParserRuleCall_0() { return cStringValueEqualsParserRuleCall_0; }
		
		//StringValueNotEquals
		public RuleCall getStringValueNotEqualsParserRuleCall_1() { return cStringValueNotEqualsParserRuleCall_1; }
		
		//IntegerValueEquals
		public RuleCall getIntegerValueEqualsParserRuleCall_2() { return cIntegerValueEqualsParserRuleCall_2; }
		
		//IntegerValueNotEquals
		public RuleCall getIntegerValueNotEqualsParserRuleCall_3() { return cIntegerValueNotEqualsParserRuleCall_3; }
		
		//IntegerValueGreaterThan
		public RuleCall getIntegerValueGreaterThanParserRuleCall_4() { return cIntegerValueGreaterThanParserRuleCall_4; }
		
		//IntegerValueGreaterThanEquals
		public RuleCall getIntegerValueGreaterThanEqualsParserRuleCall_5() { return cIntegerValueGreaterThanEqualsParserRuleCall_5; }
		
		//IntegerValueLessThan
		public RuleCall getIntegerValueLessThanParserRuleCall_6() { return cIntegerValueLessThanParserRuleCall_6; }
		
		//IntegerValueLessThanEquals
		public RuleCall getIntegerValueLessThanEqualsParserRuleCall_7() { return cIntegerValueLessThanEqualsParserRuleCall_7; }
		
		//DecimalValueEquals
		public RuleCall getDecimalValueEqualsParserRuleCall_8() { return cDecimalValueEqualsParserRuleCall_8; }
		
		//DecimalValueNotEquals
		public RuleCall getDecimalValueNotEqualsParserRuleCall_9() { return cDecimalValueNotEqualsParserRuleCall_9; }
		
		//DecimalValueGreaterThan
		public RuleCall getDecimalValueGreaterThanParserRuleCall_10() { return cDecimalValueGreaterThanParserRuleCall_10; }
		
		//DecimalValueGreaterThanEquals
		public RuleCall getDecimalValueGreaterThanEqualsParserRuleCall_11() { return cDecimalValueGreaterThanEqualsParserRuleCall_11; }
		
		//DecimalValueLessThan
		public RuleCall getDecimalValueLessThanParserRuleCall_12() { return cDecimalValueLessThanParserRuleCall_12; }
		
		//DecimalValueLessThanEquals
		public RuleCall getDecimalValueLessThanEqualsParserRuleCall_13() { return cDecimalValueLessThanEqualsParserRuleCall_13; }
	}
	public class AttributeValueEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AttributeValueEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cEQUALTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConstraintSimpleExpressionConstraintParserRuleCall_1_0 = (RuleCall)cConstraintAssignment_1.eContents().get(0);
		
		//AttributeValueEquals:
		//	EQUAL constraint=SimpleExpressionConstraint;
		@Override public ParserRule getRule() { return rule; }
		
		//EQUAL constraint=SimpleExpressionConstraint
		public Group getGroup() { return cGroup; }
		
		//EQUAL
		public RuleCall getEQUALTerminalRuleCall_0() { return cEQUALTerminalRuleCall_0; }
		
		//constraint=SimpleExpressionConstraint
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//SimpleExpressionConstraint
		public RuleCall getConstraintSimpleExpressionConstraintParserRuleCall_1_0() { return cConstraintSimpleExpressionConstraintParserRuleCall_1_0; }
	}
	public class AttributeValueNotEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AttributeValueNotEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cNOT_EQUALTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cConstraintAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cConstraintSimpleExpressionConstraintParserRuleCall_1_0 = (RuleCall)cConstraintAssignment_1.eContents().get(0);
		
		//AttributeValueNotEquals:
		//	NOT_EQUAL constraint=SimpleExpressionConstraint;
		@Override public ParserRule getRule() { return rule; }
		
		//NOT_EQUAL constraint=SimpleExpressionConstraint
		public Group getGroup() { return cGroup; }
		
		//NOT_EQUAL
		public RuleCall getNOT_EQUALTerminalRuleCall_0() { return cNOT_EQUALTerminalRuleCall_0; }
		
		//constraint=SimpleExpressionConstraint
		public Assignment getConstraintAssignment_1() { return cConstraintAssignment_1; }
		
		//SimpleExpressionConstraint
		public RuleCall getConstraintSimpleExpressionConstraintParserRuleCall_1_0() { return cConstraintSimpleExpressionConstraintParserRuleCall_1_0; }
	}
	public class StringValueEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.StringValueEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cEQUALTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cValueAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cValueSTRINGTerminalRuleCall_1_0 = (RuleCall)cValueAssignment_1.eContents().get(0);
		
		//StringValueEquals:
		//	EQUAL value=STRING;
		@Override public ParserRule getRule() { return rule; }
		
		//EQUAL value=STRING
		public Group getGroup() { return cGroup; }
		
		//EQUAL
		public RuleCall getEQUALTerminalRuleCall_0() { return cEQUALTerminalRuleCall_0; }
		
		//value=STRING
		public Assignment getValueAssignment_1() { return cValueAssignment_1; }
		
		//STRING
		public RuleCall getValueSTRINGTerminalRuleCall_1_0() { return cValueSTRINGTerminalRuleCall_1_0; }
	}
	public class StringValueNotEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.StringValueNotEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cNOT_EQUALTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cValueAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cValueSTRINGTerminalRuleCall_1_0 = (RuleCall)cValueAssignment_1.eContents().get(0);
		
		//StringValueNotEquals:
		//	NOT_EQUAL value=STRING;
		@Override public ParserRule getRule() { return rule; }
		
		//NOT_EQUAL value=STRING
		public Group getGroup() { return cGroup; }
		
		//NOT_EQUAL
		public RuleCall getNOT_EQUALTerminalRuleCall_0() { return cNOT_EQUALTerminalRuleCall_0; }
		
		//value=STRING
		public Assignment getValueAssignment_1() { return cValueAssignment_1; }
		
		//STRING
		public RuleCall getValueSTRINGTerminalRuleCall_1_0() { return cValueSTRINGTerminalRuleCall_1_0; }
	}
	public class IntegerValueEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.IntegerValueEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cEQUALTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueIntegerParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//IntegerValueEquals:
		//	EQUAL HASH value=Integer;
		@Override public ParserRule getRule() { return rule; }
		
		//EQUAL HASH value=Integer
		public Group getGroup() { return cGroup; }
		
		//EQUAL
		public RuleCall getEQUALTerminalRuleCall_0() { return cEQUALTerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Integer
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Integer
		public RuleCall getValueIntegerParserRuleCall_2_0() { return cValueIntegerParserRuleCall_2_0; }
	}
	public class IntegerValueNotEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.IntegerValueNotEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cNOT_EQUALTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueIntegerParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//IntegerValueNotEquals:
		//	NOT_EQUAL HASH value=Integer;
		@Override public ParserRule getRule() { return rule; }
		
		//NOT_EQUAL HASH value=Integer
		public Group getGroup() { return cGroup; }
		
		//NOT_EQUAL
		public RuleCall getNOT_EQUALTerminalRuleCall_0() { return cNOT_EQUALTerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Integer
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Integer
		public RuleCall getValueIntegerParserRuleCall_2_0() { return cValueIntegerParserRuleCall_2_0; }
	}
	public class IntegerValueGreaterThanElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.IntegerValueGreaterThan");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cGTTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueIntegerParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//IntegerValueGreaterThan:
		//	GT HASH value=Integer;
		@Override public ParserRule getRule() { return rule; }
		
		//GT HASH value=Integer
		public Group getGroup() { return cGroup; }
		
		//GT
		public RuleCall getGTTerminalRuleCall_0() { return cGTTerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Integer
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Integer
		public RuleCall getValueIntegerParserRuleCall_2_0() { return cValueIntegerParserRuleCall_2_0; }
	}
	public class IntegerValueLessThanElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.IntegerValueLessThan");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cLTTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueIntegerParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//IntegerValueLessThan:
		//	LT HASH value=Integer;
		@Override public ParserRule getRule() { return rule; }
		
		//LT HASH value=Integer
		public Group getGroup() { return cGroup; }
		
		//LT
		public RuleCall getLTTerminalRuleCall_0() { return cLTTerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Integer
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Integer
		public RuleCall getValueIntegerParserRuleCall_2_0() { return cValueIntegerParserRuleCall_2_0; }
	}
	public class IntegerValueGreaterThanEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.IntegerValueGreaterThanEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cGTETerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueIntegerParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//IntegerValueGreaterThanEquals:
		//	GTE HASH value=Integer;
		@Override public ParserRule getRule() { return rule; }
		
		//GTE HASH value=Integer
		public Group getGroup() { return cGroup; }
		
		//GTE
		public RuleCall getGTETerminalRuleCall_0() { return cGTETerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Integer
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Integer
		public RuleCall getValueIntegerParserRuleCall_2_0() { return cValueIntegerParserRuleCall_2_0; }
	}
	public class IntegerValueLessThanEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.IntegerValueLessThanEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cLTETerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueIntegerParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//IntegerValueLessThanEquals:
		//	LTE HASH value=Integer;
		@Override public ParserRule getRule() { return rule; }
		
		//LTE HASH value=Integer
		public Group getGroup() { return cGroup; }
		
		//LTE
		public RuleCall getLTETerminalRuleCall_0() { return cLTETerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Integer
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Integer
		public RuleCall getValueIntegerParserRuleCall_2_0() { return cValueIntegerParserRuleCall_2_0; }
	}
	public class DecimalValueEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DecimalValueEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cEQUALTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueDecimalParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//DecimalValueEquals:
		//	EQUAL HASH value=Decimal;
		@Override public ParserRule getRule() { return rule; }
		
		//EQUAL HASH value=Decimal
		public Group getGroup() { return cGroup; }
		
		//EQUAL
		public RuleCall getEQUALTerminalRuleCall_0() { return cEQUALTerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Decimal
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Decimal
		public RuleCall getValueDecimalParserRuleCall_2_0() { return cValueDecimalParserRuleCall_2_0; }
	}
	public class DecimalValueNotEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DecimalValueNotEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cNOT_EQUALTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueDecimalParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//DecimalValueNotEquals:
		//	NOT_EQUAL HASH value=Decimal;
		@Override public ParserRule getRule() { return rule; }
		
		//NOT_EQUAL HASH value=Decimal
		public Group getGroup() { return cGroup; }
		
		//NOT_EQUAL
		public RuleCall getNOT_EQUALTerminalRuleCall_0() { return cNOT_EQUALTerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Decimal
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Decimal
		public RuleCall getValueDecimalParserRuleCall_2_0() { return cValueDecimalParserRuleCall_2_0; }
	}
	public class DecimalValueGreaterThanElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DecimalValueGreaterThan");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cGTTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueDecimalParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//DecimalValueGreaterThan:
		//	GT HASH value=Decimal;
		@Override public ParserRule getRule() { return rule; }
		
		//GT HASH value=Decimal
		public Group getGroup() { return cGroup; }
		
		//GT
		public RuleCall getGTTerminalRuleCall_0() { return cGTTerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Decimal
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Decimal
		public RuleCall getValueDecimalParserRuleCall_2_0() { return cValueDecimalParserRuleCall_2_0; }
	}
	public class DecimalValueLessThanElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DecimalValueLessThan");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cLTTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueDecimalParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//DecimalValueLessThan:
		//	LT HASH value=Decimal;
		@Override public ParserRule getRule() { return rule; }
		
		//LT HASH value=Decimal
		public Group getGroup() { return cGroup; }
		
		//LT
		public RuleCall getLTTerminalRuleCall_0() { return cLTTerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Decimal
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Decimal
		public RuleCall getValueDecimalParserRuleCall_2_0() { return cValueDecimalParserRuleCall_2_0; }
	}
	public class DecimalValueGreaterThanEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DecimalValueGreaterThanEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cGTETerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueDecimalParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//DecimalValueGreaterThanEquals:
		//	GTE HASH value=Decimal;
		@Override public ParserRule getRule() { return rule; }
		
		//GTE HASH value=Decimal
		public Group getGroup() { return cGroup; }
		
		//GTE
		public RuleCall getGTETerminalRuleCall_0() { return cGTETerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Decimal
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Decimal
		public RuleCall getValueDecimalParserRuleCall_2_0() { return cValueDecimalParserRuleCall_2_0; }
	}
	public class DecimalValueLessThanEqualsElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DecimalValueLessThanEquals");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cLTETerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final RuleCall cHASHTerminalRuleCall_1 = (RuleCall)cGroup.eContents().get(1);
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cValueDecimalParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//DecimalValueLessThanEquals:
		//	LTE HASH value=Decimal;
		@Override public ParserRule getRule() { return rule; }
		
		//LTE HASH value=Decimal
		public Group getGroup() { return cGroup; }
		
		//LTE
		public RuleCall getLTETerminalRuleCall_0() { return cLTETerminalRuleCall_0; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_1() { return cHASHTerminalRuleCall_1; }
		
		//value=Decimal
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		//Decimal
		public RuleCall getValueDecimalParserRuleCall_2_0() { return cValueDecimalParserRuleCall_2_0; }
	}
	public class NestedExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.NestedExpression");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cROUND_OPENTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Assignment cNestedAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNestedExpressionConstraintParserRuleCall_1_0 = (RuleCall)cNestedAssignment_1.eContents().get(0);
		private final RuleCall cROUND_CLOSETerminalRuleCall_2 = (RuleCall)cGroup.eContents().get(2);
		
		//NestedExpression:
		//	ROUND_OPEN nested=ExpressionConstraint ROUND_CLOSE;
		@Override public ParserRule getRule() { return rule; }
		
		//ROUND_OPEN nested=ExpressionConstraint ROUND_CLOSE
		public Group getGroup() { return cGroup; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_0() { return cROUND_OPENTerminalRuleCall_0; }
		
		//nested=ExpressionConstraint
		public Assignment getNestedAssignment_1() { return cNestedAssignment_1; }
		
		//ExpressionConstraint
		public RuleCall getNestedExpressionConstraintParserRuleCall_1_0() { return cNestedExpressionConstraintParserRuleCall_1_0; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_2() { return cROUND_CLOSETerminalRuleCall_2; }
	}
	public class SnomedIdentifierElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.SnomedIdentifier");
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
		
		//// hidden grammar rules
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
	public class TermElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.Term");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cTermCharacterParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final RuleCall cWSTerminalRuleCall_1_0 = (RuleCall)cGroup_1.eContents().get(0);
		private final RuleCall cTermCharacterParserRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		
		//Term hidden():
		//	TermCharacter+ (WS+ TermCharacter+)*;
		@Override public ParserRule getRule() { return rule; }
		
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
	public class TermCharacterElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.TermCharacter");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cLTTerminalRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cGTTerminalRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cDBL_LTTerminalRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cDBL_GTTerminalRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		private final RuleCall cLT_EMTerminalRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		private final RuleCall cGT_EMTerminalRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		private final RuleCall cGTETerminalRuleCall_6 = (RuleCall)cAlternatives.eContents().get(6);
		private final RuleCall cLTETerminalRuleCall_7 = (RuleCall)cAlternatives.eContents().get(7);
		private final RuleCall cANDTerminalRuleCall_8 = (RuleCall)cAlternatives.eContents().get(8);
		private final RuleCall cORTerminalRuleCall_9 = (RuleCall)cAlternatives.eContents().get(9);
		private final RuleCall cNOTTerminalRuleCall_10 = (RuleCall)cAlternatives.eContents().get(10);
		private final RuleCall cMINUSTerminalRuleCall_11 = (RuleCall)cAlternatives.eContents().get(11);
		private final RuleCall cZEROTerminalRuleCall_12 = (RuleCall)cAlternatives.eContents().get(12);
		private final RuleCall cDIGIT_NONZEROTerminalRuleCall_13 = (RuleCall)cAlternatives.eContents().get(13);
		private final RuleCall cLETTERTerminalRuleCall_14 = (RuleCall)cAlternatives.eContents().get(14);
		private final RuleCall cCARETTerminalRuleCall_15 = (RuleCall)cAlternatives.eContents().get(15);
		private final RuleCall cEQUALTerminalRuleCall_16 = (RuleCall)cAlternatives.eContents().get(16);
		private final RuleCall cNOT_EQUALTerminalRuleCall_17 = (RuleCall)cAlternatives.eContents().get(17);
		private final RuleCall cPLUSTerminalRuleCall_18 = (RuleCall)cAlternatives.eContents().get(18);
		private final RuleCall cCURLY_OPENTerminalRuleCall_19 = (RuleCall)cAlternatives.eContents().get(19);
		private final RuleCall cCURLY_CLOSETerminalRuleCall_20 = (RuleCall)cAlternatives.eContents().get(20);
		private final RuleCall cROUND_OPENTerminalRuleCall_21 = (RuleCall)cAlternatives.eContents().get(21);
		private final RuleCall cROUND_CLOSETerminalRuleCall_22 = (RuleCall)cAlternatives.eContents().get(22);
		private final RuleCall cSQUARE_OPENTerminalRuleCall_23 = (RuleCall)cAlternatives.eContents().get(23);
		private final RuleCall cSQUARE_CLOSETerminalRuleCall_24 = (RuleCall)cAlternatives.eContents().get(24);
		private final RuleCall cDOTTerminalRuleCall_25 = (RuleCall)cAlternatives.eContents().get(25);
		private final RuleCall cCOLONTerminalRuleCall_26 = (RuleCall)cAlternatives.eContents().get(26);
		private final RuleCall cCOMMATerminalRuleCall_27 = (RuleCall)cAlternatives.eContents().get(27);
		private final RuleCall cREVERSEDTerminalRuleCall_28 = (RuleCall)cAlternatives.eContents().get(28);
		private final RuleCall cTOTerminalRuleCall_29 = (RuleCall)cAlternatives.eContents().get(29);
		private final RuleCall cWILDCARDTerminalRuleCall_30 = (RuleCall)cAlternatives.eContents().get(30);
		private final RuleCall cHASHTerminalRuleCall_31 = (RuleCall)cAlternatives.eContents().get(31);
		private final RuleCall cDASHTerminalRuleCall_32 = (RuleCall)cAlternatives.eContents().get(32);
		private final RuleCall cOTHER_CHARACTERTerminalRuleCall_33 = (RuleCall)cAlternatives.eContents().get(33);
		
		//TermCharacter hidden():
		//	LT | GT
		//	| DBL_LT | DBL_GT
		//	| LT_EM | GT_EM
		//	| GTE | LTE
		//	| AND | OR | NOT | MINUS
		//	| ZERO | DIGIT_NONZERO
		//	| LETTER | CARET
		//	| EQUAL | NOT_EQUAL | PLUS
		//	| CURLY_OPEN | CURLY_CLOSE
		//	| ROUND_OPEN | ROUND_CLOSE
		//	| SQUARE_OPEN | SQUARE_CLOSE
		//	| DOT | COLON | COMMA
		//	| REVERSED | TO
		//	| WILDCARD | HASH | DASH
		//	| OTHER_CHARACTER;
		@Override public ParserRule getRule() { return rule; }
		
		//LT | GT | DBL_LT | DBL_GT | LT_EM | GT_EM | GTE | LTE | AND | OR | NOT | MINUS | ZERO | DIGIT_NONZERO | LETTER | CARET |
		//EQUAL | NOT_EQUAL | PLUS | CURLY_OPEN | CURLY_CLOSE | ROUND_OPEN | ROUND_CLOSE | SQUARE_OPEN | SQUARE_CLOSE | DOT |
		//COLON | COMMA | REVERSED | TO | WILDCARD | HASH | DASH | OTHER_CHARACTER
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//LT
		public RuleCall getLTTerminalRuleCall_0() { return cLTTerminalRuleCall_0; }
		
		//GT
		public RuleCall getGTTerminalRuleCall_1() { return cGTTerminalRuleCall_1; }
		
		//DBL_LT
		public RuleCall getDBL_LTTerminalRuleCall_2() { return cDBL_LTTerminalRuleCall_2; }
		
		//DBL_GT
		public RuleCall getDBL_GTTerminalRuleCall_3() { return cDBL_GTTerminalRuleCall_3; }
		
		//LT_EM
		public RuleCall getLT_EMTerminalRuleCall_4() { return cLT_EMTerminalRuleCall_4; }
		
		//GT_EM
		public RuleCall getGT_EMTerminalRuleCall_5() { return cGT_EMTerminalRuleCall_5; }
		
		//GTE
		public RuleCall getGTETerminalRuleCall_6() { return cGTETerminalRuleCall_6; }
		
		//LTE
		public RuleCall getLTETerminalRuleCall_7() { return cLTETerminalRuleCall_7; }
		
		//AND
		public RuleCall getANDTerminalRuleCall_8() { return cANDTerminalRuleCall_8; }
		
		//OR
		public RuleCall getORTerminalRuleCall_9() { return cORTerminalRuleCall_9; }
		
		//NOT
		public RuleCall getNOTTerminalRuleCall_10() { return cNOTTerminalRuleCall_10; }
		
		//MINUS
		public RuleCall getMINUSTerminalRuleCall_11() { return cMINUSTerminalRuleCall_11; }
		
		//ZERO
		public RuleCall getZEROTerminalRuleCall_12() { return cZEROTerminalRuleCall_12; }
		
		//DIGIT_NONZERO
		public RuleCall getDIGIT_NONZEROTerminalRuleCall_13() { return cDIGIT_NONZEROTerminalRuleCall_13; }
		
		//LETTER
		public RuleCall getLETTERTerminalRuleCall_14() { return cLETTERTerminalRuleCall_14; }
		
		//CARET
		public RuleCall getCARETTerminalRuleCall_15() { return cCARETTerminalRuleCall_15; }
		
		//EQUAL
		public RuleCall getEQUALTerminalRuleCall_16() { return cEQUALTerminalRuleCall_16; }
		
		//NOT_EQUAL
		public RuleCall getNOT_EQUALTerminalRuleCall_17() { return cNOT_EQUALTerminalRuleCall_17; }
		
		//PLUS
		public RuleCall getPLUSTerminalRuleCall_18() { return cPLUSTerminalRuleCall_18; }
		
		//CURLY_OPEN
		public RuleCall getCURLY_OPENTerminalRuleCall_19() { return cCURLY_OPENTerminalRuleCall_19; }
		
		//CURLY_CLOSE
		public RuleCall getCURLY_CLOSETerminalRuleCall_20() { return cCURLY_CLOSETerminalRuleCall_20; }
		
		//ROUND_OPEN
		public RuleCall getROUND_OPENTerminalRuleCall_21() { return cROUND_OPENTerminalRuleCall_21; }
		
		//ROUND_CLOSE
		public RuleCall getROUND_CLOSETerminalRuleCall_22() { return cROUND_CLOSETerminalRuleCall_22; }
		
		//SQUARE_OPEN
		public RuleCall getSQUARE_OPENTerminalRuleCall_23() { return cSQUARE_OPENTerminalRuleCall_23; }
		
		//SQUARE_CLOSE
		public RuleCall getSQUARE_CLOSETerminalRuleCall_24() { return cSQUARE_CLOSETerminalRuleCall_24; }
		
		//DOT
		public RuleCall getDOTTerminalRuleCall_25() { return cDOTTerminalRuleCall_25; }
		
		//COLON
		public RuleCall getCOLONTerminalRuleCall_26() { return cCOLONTerminalRuleCall_26; }
		
		//COMMA
		public RuleCall getCOMMATerminalRuleCall_27() { return cCOMMATerminalRuleCall_27; }
		
		//REVERSED
		public RuleCall getREVERSEDTerminalRuleCall_28() { return cREVERSEDTerminalRuleCall_28; }
		
		//TO
		public RuleCall getTOTerminalRuleCall_29() { return cTOTerminalRuleCall_29; }
		
		//WILDCARD
		public RuleCall getWILDCARDTerminalRuleCall_30() { return cWILDCARDTerminalRuleCall_30; }
		
		//HASH
		public RuleCall getHASHTerminalRuleCall_31() { return cHASHTerminalRuleCall_31; }
		
		//DASH
		public RuleCall getDASHTerminalRuleCall_32() { return cDASHTerminalRuleCall_32; }
		
		//OTHER_CHARACTER
		public RuleCall getOTHER_CHARACTERTerminalRuleCall_33() { return cOTHER_CHARACTERTerminalRuleCall_33; }
	}
	public class NonNegativeIntegerElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.NonNegativeInteger");
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
	public class MaxValueElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.MaxValue");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cNonNegativeIntegerParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cWILDCARDTerminalRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//MaxValue ecore::EInt hidden():
		//	NonNegativeInteger | WILDCARD;
		@Override public ParserRule getRule() { return rule; }
		
		//NonNegativeInteger | WILDCARD
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//NonNegativeInteger
		public RuleCall getNonNegativeIntegerParserRuleCall_0() { return cNonNegativeIntegerParserRuleCall_0; }
		
		//WILDCARD
		public RuleCall getWILDCARDTerminalRuleCall_1() { return cWILDCARDTerminalRuleCall_1; }
	}
	public class AndOperatorElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AndOperator");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cANDTerminalRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cCOMMATerminalRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//AndOperator hidden():
		//	AND | COMMA;
		@Override public ParserRule getRule() { return rule; }
		
		//AND | COMMA
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//AND
		public RuleCall getANDTerminalRuleCall_0() { return cANDTerminalRuleCall_0; }
		
		//COMMA
		public RuleCall getCOMMATerminalRuleCall_1() { return cCOMMATerminalRuleCall_1; }
	}
	public class IntegerElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.Integer");
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
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.Decimal");
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
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.NonNegativeDecimal");
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
	
	
	private final ExpressionConstraintElements pExpressionConstraint;
	private final OrExpressionConstraintElements pOrExpressionConstraint;
	private final AndExpressionConstraintElements pAndExpressionConstraint;
	private final ExclusionExpressionConstraintElements pExclusionExpressionConstraint;
	private final RefinedExpressionConstraintElements pRefinedExpressionConstraint;
	private final DottedExpressionConstraintElements pDottedExpressionConstraint;
	private final SimpleExpressionConstraintElements pSimpleExpressionConstraint;
	private final FocusConceptElements pFocusConcept;
	private final ChildOfElements pChildOf;
	private final DescendantOfElements pDescendantOf;
	private final DescendantOrSelfOfElements pDescendantOrSelfOf;
	private final ParentOfElements pParentOf;
	private final AncestorOfElements pAncestorOf;
	private final AncestorOrSelfOfElements pAncestorOrSelfOf;
	private final MemberOfElements pMemberOf;
	private final ConceptReferenceElements pConceptReference;
	private final AnyElements pAny;
	private final RefinementElements pRefinement;
	private final OrRefinementElements pOrRefinement;
	private final AndRefinementElements pAndRefinement;
	private final SubRefinementElements pSubRefinement;
	private final NestedRefinementElements pNestedRefinement;
	private final AttributeGroupElements pAttributeGroup;
	private final AttributeSetElements pAttributeSet;
	private final OrAttributeSetElements pOrAttributeSet;
	private final AndAttributeSetElements pAndAttributeSet;
	private final SubAttributeSetElements pSubAttributeSet;
	private final NestedAttributeSetElements pNestedAttributeSet;
	private final AttributeConstraintElements pAttributeConstraint;
	private final AttributeElements pAttribute;
	private final AttributeDescendantOfElements pAttributeDescendantOf;
	private final AttributeDescendantOrSelfOfElements pAttributeDescendantOrSelfOf;
	private final CardinalityElements pCardinality;
	private final ComparisonElements pComparison;
	private final AttributeComparisonElements pAttributeComparison;
	private final DataTypeComparisonElements pDataTypeComparison;
	private final AttributeValueEqualsElements pAttributeValueEquals;
	private final AttributeValueNotEqualsElements pAttributeValueNotEquals;
	private final StringValueEqualsElements pStringValueEquals;
	private final StringValueNotEqualsElements pStringValueNotEquals;
	private final IntegerValueEqualsElements pIntegerValueEquals;
	private final IntegerValueNotEqualsElements pIntegerValueNotEquals;
	private final IntegerValueGreaterThanElements pIntegerValueGreaterThan;
	private final IntegerValueLessThanElements pIntegerValueLessThan;
	private final IntegerValueGreaterThanEqualsElements pIntegerValueGreaterThanEquals;
	private final IntegerValueLessThanEqualsElements pIntegerValueLessThanEquals;
	private final DecimalValueEqualsElements pDecimalValueEquals;
	private final DecimalValueNotEqualsElements pDecimalValueNotEquals;
	private final DecimalValueGreaterThanElements pDecimalValueGreaterThan;
	private final DecimalValueLessThanElements pDecimalValueLessThan;
	private final DecimalValueGreaterThanEqualsElements pDecimalValueGreaterThanEquals;
	private final DecimalValueLessThanEqualsElements pDecimalValueLessThanEquals;
	private final NestedExpressionElements pNestedExpression;
	private final SnomedIdentifierElements pSnomedIdentifier;
	private final TermElements pTerm;
	private final TermCharacterElements pTermCharacter;
	private final NonNegativeIntegerElements pNonNegativeInteger;
	private final MaxValueElements pMaxValue;
	private final AndOperatorElements pAndOperator;
	private final IntegerElements pInteger;
	private final DecimalElements pDecimal;
	private final NonNegativeDecimalElements pNonNegativeDecimal;
	private final TerminalRule tREVERSED;
	private final TerminalRule tTO;
	private final TerminalRule tAND;
	private final TerminalRule tOR;
	private final TerminalRule tMINUS;
	private final TerminalRule tZERO;
	private final TerminalRule tDIGIT_NONZERO;
	private final TerminalRule tLETTER;
	private final TerminalRule tPIPE;
	private final TerminalRule tCOLON;
	private final TerminalRule tCURLY_OPEN;
	private final TerminalRule tCURLY_CLOSE;
	private final TerminalRule tCOMMA;
	private final TerminalRule tROUND_OPEN;
	private final TerminalRule tROUND_CLOSE;
	private final TerminalRule tSQUARE_OPEN;
	private final TerminalRule tSQUARE_CLOSE;
	private final TerminalRule tPLUS;
	private final TerminalRule tDASH;
	private final TerminalRule tCARET;
	private final TerminalRule tNOT;
	private final TerminalRule tDOT;
	private final TerminalRule tWILDCARD;
	private final TerminalRule tEQUAL;
	private final TerminalRule tNOT_EQUAL;
	private final TerminalRule tLT;
	private final TerminalRule tGT;
	private final TerminalRule tDBL_LT;
	private final TerminalRule tDBL_GT;
	private final TerminalRule tLT_EM;
	private final TerminalRule tGT_EM;
	private final TerminalRule tGTE;
	private final TerminalRule tLTE;
	private final TerminalRule tHASH;
	private final TerminalRule tWS;
	private final TerminalRule tML_COMMENT;
	private final TerminalRule tSL_COMMENT;
	private final TerminalRule tOTHER_CHARACTER;
	private final TerminalRule tSTRING;
	
	private final Grammar grammar;

	@Inject
	public EclGrammarAccess(GrammarProvider grammarProvider) {
		this.grammar = internalFindGrammar(grammarProvider);
		this.pExpressionConstraint = new ExpressionConstraintElements();
		this.pOrExpressionConstraint = new OrExpressionConstraintElements();
		this.pAndExpressionConstraint = new AndExpressionConstraintElements();
		this.pExclusionExpressionConstraint = new ExclusionExpressionConstraintElements();
		this.pRefinedExpressionConstraint = new RefinedExpressionConstraintElements();
		this.pDottedExpressionConstraint = new DottedExpressionConstraintElements();
		this.pSimpleExpressionConstraint = new SimpleExpressionConstraintElements();
		this.pFocusConcept = new FocusConceptElements();
		this.pChildOf = new ChildOfElements();
		this.pDescendantOf = new DescendantOfElements();
		this.pDescendantOrSelfOf = new DescendantOrSelfOfElements();
		this.pParentOf = new ParentOfElements();
		this.pAncestorOf = new AncestorOfElements();
		this.pAncestorOrSelfOf = new AncestorOrSelfOfElements();
		this.pMemberOf = new MemberOfElements();
		this.pConceptReference = new ConceptReferenceElements();
		this.pAny = new AnyElements();
		this.pRefinement = new RefinementElements();
		this.pOrRefinement = new OrRefinementElements();
		this.pAndRefinement = new AndRefinementElements();
		this.pSubRefinement = new SubRefinementElements();
		this.pNestedRefinement = new NestedRefinementElements();
		this.pAttributeGroup = new AttributeGroupElements();
		this.pAttributeSet = new AttributeSetElements();
		this.pOrAttributeSet = new OrAttributeSetElements();
		this.pAndAttributeSet = new AndAttributeSetElements();
		this.pSubAttributeSet = new SubAttributeSetElements();
		this.pNestedAttributeSet = new NestedAttributeSetElements();
		this.pAttributeConstraint = new AttributeConstraintElements();
		this.pAttribute = new AttributeElements();
		this.pAttributeDescendantOf = new AttributeDescendantOfElements();
		this.pAttributeDescendantOrSelfOf = new AttributeDescendantOrSelfOfElements();
		this.pCardinality = new CardinalityElements();
		this.pComparison = new ComparisonElements();
		this.pAttributeComparison = new AttributeComparisonElements();
		this.pDataTypeComparison = new DataTypeComparisonElements();
		this.pAttributeValueEquals = new AttributeValueEqualsElements();
		this.pAttributeValueNotEquals = new AttributeValueNotEqualsElements();
		this.pStringValueEquals = new StringValueEqualsElements();
		this.pStringValueNotEquals = new StringValueNotEqualsElements();
		this.pIntegerValueEquals = new IntegerValueEqualsElements();
		this.pIntegerValueNotEquals = new IntegerValueNotEqualsElements();
		this.pIntegerValueGreaterThan = new IntegerValueGreaterThanElements();
		this.pIntegerValueLessThan = new IntegerValueLessThanElements();
		this.pIntegerValueGreaterThanEquals = new IntegerValueGreaterThanEqualsElements();
		this.pIntegerValueLessThanEquals = new IntegerValueLessThanEqualsElements();
		this.pDecimalValueEquals = new DecimalValueEqualsElements();
		this.pDecimalValueNotEquals = new DecimalValueNotEqualsElements();
		this.pDecimalValueGreaterThan = new DecimalValueGreaterThanElements();
		this.pDecimalValueLessThan = new DecimalValueLessThanElements();
		this.pDecimalValueGreaterThanEquals = new DecimalValueGreaterThanEqualsElements();
		this.pDecimalValueLessThanEquals = new DecimalValueLessThanEqualsElements();
		this.pNestedExpression = new NestedExpressionElements();
		this.pSnomedIdentifier = new SnomedIdentifierElements();
		this.pTerm = new TermElements();
		this.pTermCharacter = new TermCharacterElements();
		this.pNonNegativeInteger = new NonNegativeIntegerElements();
		this.pMaxValue = new MaxValueElements();
		this.pAndOperator = new AndOperatorElements();
		this.pInteger = new IntegerElements();
		this.pDecimal = new DecimalElements();
		this.pNonNegativeDecimal = new NonNegativeDecimalElements();
		this.tREVERSED = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.REVERSED");
		this.tTO = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.TO");
		this.tAND = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.AND");
		this.tOR = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.OR");
		this.tMINUS = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.MINUS");
		this.tZERO = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.ZERO");
		this.tDIGIT_NONZERO = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DIGIT_NONZERO");
		this.tLETTER = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.LETTER");
		this.tPIPE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.PIPE");
		this.tCOLON = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.COLON");
		this.tCURLY_OPEN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.CURLY_OPEN");
		this.tCURLY_CLOSE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.CURLY_CLOSE");
		this.tCOMMA = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.COMMA");
		this.tROUND_OPEN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.ROUND_OPEN");
		this.tROUND_CLOSE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.ROUND_CLOSE");
		this.tSQUARE_OPEN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.SQUARE_OPEN");
		this.tSQUARE_CLOSE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.SQUARE_CLOSE");
		this.tPLUS = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.PLUS");
		this.tDASH = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DASH");
		this.tCARET = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.CARET");
		this.tNOT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.NOT");
		this.tDOT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DOT");
		this.tWILDCARD = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.WILDCARD");
		this.tEQUAL = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.EQUAL");
		this.tNOT_EQUAL = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.NOT_EQUAL");
		this.tLT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.LT");
		this.tGT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.GT");
		this.tDBL_LT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DBL_LT");
		this.tDBL_GT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.DBL_GT");
		this.tLT_EM = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.LT_EM");
		this.tGT_EM = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.GT_EM");
		this.tGTE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.GTE");
		this.tLTE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.LTE");
		this.tHASH = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.HASH");
		this.tWS = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.WS");
		this.tML_COMMENT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.ML_COMMENT");
		this.tSL_COMMENT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.SL_COMMENT");
		this.tOTHER_CHARACTER = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.OTHER_CHARACTER");
		this.tSTRING = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "com.b2international.snowowl.snomed.ecl.Ecl.STRING");
	}
	
	protected Grammar internalFindGrammar(GrammarProvider grammarProvider) {
		Grammar grammar = grammarProvider.getGrammar(this);
		while (grammar != null) {
			if ("com.b2international.snowowl.snomed.ecl.Ecl".equals(grammar.getName())) {
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
	

	
	//ExpressionConstraint:
	//	OrExpressionConstraint;
	public ExpressionConstraintElements getExpressionConstraintAccess() {
		return pExpressionConstraint;
	}
	
	public ParserRule getExpressionConstraintRule() {
		return getExpressionConstraintAccess().getRule();
	}
	
	//OrExpressionConstraint ExpressionConstraint:
	//	AndExpressionConstraint ({OrExpressionConstraint.left=current} OR right=AndExpressionConstraint)*;
	public OrExpressionConstraintElements getOrExpressionConstraintAccess() {
		return pOrExpressionConstraint;
	}
	
	public ParserRule getOrExpressionConstraintRule() {
		return getOrExpressionConstraintAccess().getRule();
	}
	
	//AndExpressionConstraint ExpressionConstraint:
	//	ExclusionExpressionConstraint ({AndExpressionConstraint.left=current} AndOperator
	//	right=ExclusionExpressionConstraint)*;
	public AndExpressionConstraintElements getAndExpressionConstraintAccess() {
		return pAndExpressionConstraint;
	}
	
	public ParserRule getAndExpressionConstraintRule() {
		return getAndExpressionConstraintAccess().getRule();
	}
	
	//ExclusionExpressionConstraint ExpressionConstraint:
	//	RefinedExpressionConstraint ({ExclusionExpressionConstraint.left=current} MINUS right=RefinedExpressionConstraint)?;
	public ExclusionExpressionConstraintElements getExclusionExpressionConstraintAccess() {
		return pExclusionExpressionConstraint;
	}
	
	public ParserRule getExclusionExpressionConstraintRule() {
		return getExclusionExpressionConstraintAccess().getRule();
	}
	
	//RefinedExpressionConstraint ExpressionConstraint:
	//	DottedExpressionConstraint ({RefinedExpressionConstraint.constraint=current} COLON refinement=Refinement)?;
	public RefinedExpressionConstraintElements getRefinedExpressionConstraintAccess() {
		return pRefinedExpressionConstraint;
	}
	
	public ParserRule getRefinedExpressionConstraintRule() {
		return getRefinedExpressionConstraintAccess().getRule();
	}
	
	//DottedExpressionConstraint ExpressionConstraint:
	//	SimpleExpressionConstraint ({DottedExpressionConstraint.constraint=current} DOT attribute=Attribute)*;
	public DottedExpressionConstraintElements getDottedExpressionConstraintAccess() {
		return pDottedExpressionConstraint;
	}
	
	public ParserRule getDottedExpressionConstraintRule() {
		return getDottedExpressionConstraintAccess().getRule();
	}
	
	//SimpleExpressionConstraint ExpressionConstraint:
	//	ChildOf | DescendantOf | DescendantOrSelfOf | ParentOf | AncestorOf | AncestorOrSelfOf | FocusConcept;
	public SimpleExpressionConstraintElements getSimpleExpressionConstraintAccess() {
		return pSimpleExpressionConstraint;
	}
	
	public ParserRule getSimpleExpressionConstraintRule() {
		return getSimpleExpressionConstraintAccess().getRule();
	}
	
	//FocusConcept ExpressionConstraint:
	//	MemberOf | ConceptReference | Any | NestedExpression;
	public FocusConceptElements getFocusConceptAccess() {
		return pFocusConcept;
	}
	
	public ParserRule getFocusConceptRule() {
		return getFocusConceptAccess().getRule();
	}
	
	//ChildOf:
	//	LT_EM constraint=FocusConcept;
	public ChildOfElements getChildOfAccess() {
		return pChildOf;
	}
	
	public ParserRule getChildOfRule() {
		return getChildOfAccess().getRule();
	}
	
	//DescendantOf:
	//	LT constraint=FocusConcept;
	public DescendantOfElements getDescendantOfAccess() {
		return pDescendantOf;
	}
	
	public ParserRule getDescendantOfRule() {
		return getDescendantOfAccess().getRule();
	}
	
	//DescendantOrSelfOf:
	//	DBL_LT constraint=FocusConcept;
	public DescendantOrSelfOfElements getDescendantOrSelfOfAccess() {
		return pDescendantOrSelfOf;
	}
	
	public ParserRule getDescendantOrSelfOfRule() {
		return getDescendantOrSelfOfAccess().getRule();
	}
	
	//ParentOf:
	//	GT_EM constraint=FocusConcept;
	public ParentOfElements getParentOfAccess() {
		return pParentOf;
	}
	
	public ParserRule getParentOfRule() {
		return getParentOfAccess().getRule();
	}
	
	//AncestorOf:
	//	GT constraint=FocusConcept;
	public AncestorOfElements getAncestorOfAccess() {
		return pAncestorOf;
	}
	
	public ParserRule getAncestorOfRule() {
		return getAncestorOfAccess().getRule();
	}
	
	//AncestorOrSelfOf:
	//	DBL_GT constraint=FocusConcept;
	public AncestorOrSelfOfElements getAncestorOrSelfOfAccess() {
		return pAncestorOrSelfOf;
	}
	
	public ParserRule getAncestorOrSelfOfRule() {
		return getAncestorOrSelfOfAccess().getRule();
	}
	
	//MemberOf:
	//	CARET constraint=(ConceptReference | Any);
	public MemberOfElements getMemberOfAccess() {
		return pMemberOf;
	}
	
	public ParserRule getMemberOfRule() {
		return getMemberOfAccess().getRule();
	}
	
	//ConceptReference:
	//	id=SnomedIdentifier (PIPE term=Term PIPE)?;
	public ConceptReferenceElements getConceptReferenceAccess() {
		return pConceptReference;
	}
	
	public ParserRule getConceptReferenceRule() {
		return getConceptReferenceAccess().getRule();
	}
	
	//Any:
	//	WILDCARD {Any};
	public AnyElements getAnyAccess() {
		return pAny;
	}
	
	public ParserRule getAnyRule() {
		return getAnyAccess().getRule();
	}
	
	//Refinement:
	//	OrRefinement;
	public RefinementElements getRefinementAccess() {
		return pRefinement;
	}
	
	public ParserRule getRefinementRule() {
		return getRefinementAccess().getRule();
	}
	
	//OrRefinement Refinement:
	//	AndRefinement -> ({OrRefinement.left=current} OR right=AndRefinement)*;
	public OrRefinementElements getOrRefinementAccess() {
		return pOrRefinement;
	}
	
	public ParserRule getOrRefinementRule() {
		return getOrRefinementAccess().getRule();
	}
	
	//AndRefinement Refinement:
	//	SubRefinement -> ({AndRefinement.left=current} AndOperator right=SubRefinement)*;
	public AndRefinementElements getAndRefinementAccess() {
		return pAndRefinement;
	}
	
	public ParserRule getAndRefinementRule() {
		return getAndRefinementAccess().getRule();
	}
	
	//SubRefinement Refinement:
	//	AttributeConstraint | AttributeGroup | NestedRefinement;
	public SubRefinementElements getSubRefinementAccess() {
		return pSubRefinement;
	}
	
	public ParserRule getSubRefinementRule() {
		return getSubRefinementAccess().getRule();
	}
	
	//NestedRefinement:
	//	ROUND_OPEN nested=Refinement ROUND_CLOSE;
	public NestedRefinementElements getNestedRefinementAccess() {
		return pNestedRefinement;
	}
	
	public ParserRule getNestedRefinementRule() {
		return getNestedRefinementAccess().getRule();
	}
	
	//AttributeGroup:
	//	cardinality=Cardinality? CURLY_OPEN refinement=AttributeSet CURLY_CLOSE;
	public AttributeGroupElements getAttributeGroupAccess() {
		return pAttributeGroup;
	}
	
	public ParserRule getAttributeGroupRule() {
		return getAttributeGroupAccess().getRule();
	}
	
	//AttributeSet Refinement:
	//	OrAttributeSet;
	public AttributeSetElements getAttributeSetAccess() {
		return pAttributeSet;
	}
	
	public ParserRule getAttributeSetRule() {
		return getAttributeSetAccess().getRule();
	}
	
	//OrAttributeSet Refinement:
	//	AndAttributeSet ({OrRefinement.left=current} OR right=AndAttributeSet)*;
	public OrAttributeSetElements getOrAttributeSetAccess() {
		return pOrAttributeSet;
	}
	
	public ParserRule getOrAttributeSetRule() {
		return getOrAttributeSetAccess().getRule();
	}
	
	//AndAttributeSet Refinement:
	//	SubAttributeSet ({AndRefinement.left=current} AndOperator right=SubAttributeSet)*;
	public AndAttributeSetElements getAndAttributeSetAccess() {
		return pAndAttributeSet;
	}
	
	public ParserRule getAndAttributeSetRule() {
		return getAndAttributeSetAccess().getRule();
	}
	
	//SubAttributeSet Refinement:
	//	AttributeConstraint | NestedAttributeSet;
	public SubAttributeSetElements getSubAttributeSetAccess() {
		return pSubAttributeSet;
	}
	
	public ParserRule getSubAttributeSetRule() {
		return getSubAttributeSetAccess().getRule();
	}
	
	//NestedAttributeSet NestedRefinement:
	//	ROUND_OPEN nested=AttributeSet ROUND_CLOSE;
	public NestedAttributeSetElements getNestedAttributeSetAccess() {
		return pNestedAttributeSet;
	}
	
	public ParserRule getNestedAttributeSetRule() {
		return getNestedAttributeSetAccess().getRule();
	}
	
	//AttributeConstraint:
	//	cardinality=Cardinality? reversed?=REVERSED? attribute=Attribute comparison=Comparison;
	public AttributeConstraintElements getAttributeConstraintAccess() {
		return pAttributeConstraint;
	}
	
	public ParserRule getAttributeConstraintRule() {
		return getAttributeConstraintAccess().getRule();
	}
	
	//Attribute ExpressionConstraint:
	//	AttributeDescendantOf | AttributeDescendantOrSelfOf | ConceptReference | Any;
	public AttributeElements getAttributeAccess() {
		return pAttribute;
	}
	
	public ParserRule getAttributeRule() {
		return getAttributeAccess().getRule();
	}
	
	//AttributeDescendantOf DescendantOf:
	//	LT constraint=(ConceptReference | Any);
	public AttributeDescendantOfElements getAttributeDescendantOfAccess() {
		return pAttributeDescendantOf;
	}
	
	public ParserRule getAttributeDescendantOfRule() {
		return getAttributeDescendantOfAccess().getRule();
	}
	
	//AttributeDescendantOrSelfOf DescendantOrSelfOf:
	//	DBL_LT constraint=(ConceptReference | Any);
	public AttributeDescendantOrSelfOfElements getAttributeDescendantOrSelfOfAccess() {
		return pAttributeDescendantOrSelfOf;
	}
	
	public ParserRule getAttributeDescendantOrSelfOfRule() {
		return getAttributeDescendantOrSelfOfAccess().getRule();
	}
	
	//Cardinality:
	//	SQUARE_OPEN min=NonNegativeInteger TO max=MaxValue SQUARE_CLOSE;
	public CardinalityElements getCardinalityAccess() {
		return pCardinality;
	}
	
	public ParserRule getCardinalityRule() {
		return getCardinalityAccess().getRule();
	}
	
	//Comparison:
	//	AttributeComparison | DataTypeComparison;
	public ComparisonElements getComparisonAccess() {
		return pComparison;
	}
	
	public ParserRule getComparisonRule() {
		return getComparisonAccess().getRule();
	}
	
	//AttributeComparison:
	//	AttributeValueEquals | AttributeValueNotEquals;
	public AttributeComparisonElements getAttributeComparisonAccess() {
		return pAttributeComparison;
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
	public DataTypeComparisonElements getDataTypeComparisonAccess() {
		return pDataTypeComparison;
	}
	
	public ParserRule getDataTypeComparisonRule() {
		return getDataTypeComparisonAccess().getRule();
	}
	
	//AttributeValueEquals:
	//	EQUAL constraint=SimpleExpressionConstraint;
	public AttributeValueEqualsElements getAttributeValueEqualsAccess() {
		return pAttributeValueEquals;
	}
	
	public ParserRule getAttributeValueEqualsRule() {
		return getAttributeValueEqualsAccess().getRule();
	}
	
	//AttributeValueNotEquals:
	//	NOT_EQUAL constraint=SimpleExpressionConstraint;
	public AttributeValueNotEqualsElements getAttributeValueNotEqualsAccess() {
		return pAttributeValueNotEquals;
	}
	
	public ParserRule getAttributeValueNotEqualsRule() {
		return getAttributeValueNotEqualsAccess().getRule();
	}
	
	//StringValueEquals:
	//	EQUAL value=STRING;
	public StringValueEqualsElements getStringValueEqualsAccess() {
		return pStringValueEquals;
	}
	
	public ParserRule getStringValueEqualsRule() {
		return getStringValueEqualsAccess().getRule();
	}
	
	//StringValueNotEquals:
	//	NOT_EQUAL value=STRING;
	public StringValueNotEqualsElements getStringValueNotEqualsAccess() {
		return pStringValueNotEquals;
	}
	
	public ParserRule getStringValueNotEqualsRule() {
		return getStringValueNotEqualsAccess().getRule();
	}
	
	//IntegerValueEquals:
	//	EQUAL HASH value=Integer;
	public IntegerValueEqualsElements getIntegerValueEqualsAccess() {
		return pIntegerValueEquals;
	}
	
	public ParserRule getIntegerValueEqualsRule() {
		return getIntegerValueEqualsAccess().getRule();
	}
	
	//IntegerValueNotEquals:
	//	NOT_EQUAL HASH value=Integer;
	public IntegerValueNotEqualsElements getIntegerValueNotEqualsAccess() {
		return pIntegerValueNotEquals;
	}
	
	public ParserRule getIntegerValueNotEqualsRule() {
		return getIntegerValueNotEqualsAccess().getRule();
	}
	
	//IntegerValueGreaterThan:
	//	GT HASH value=Integer;
	public IntegerValueGreaterThanElements getIntegerValueGreaterThanAccess() {
		return pIntegerValueGreaterThan;
	}
	
	public ParserRule getIntegerValueGreaterThanRule() {
		return getIntegerValueGreaterThanAccess().getRule();
	}
	
	//IntegerValueLessThan:
	//	LT HASH value=Integer;
	public IntegerValueLessThanElements getIntegerValueLessThanAccess() {
		return pIntegerValueLessThan;
	}
	
	public ParserRule getIntegerValueLessThanRule() {
		return getIntegerValueLessThanAccess().getRule();
	}
	
	//IntegerValueGreaterThanEquals:
	//	GTE HASH value=Integer;
	public IntegerValueGreaterThanEqualsElements getIntegerValueGreaterThanEqualsAccess() {
		return pIntegerValueGreaterThanEquals;
	}
	
	public ParserRule getIntegerValueGreaterThanEqualsRule() {
		return getIntegerValueGreaterThanEqualsAccess().getRule();
	}
	
	//IntegerValueLessThanEquals:
	//	LTE HASH value=Integer;
	public IntegerValueLessThanEqualsElements getIntegerValueLessThanEqualsAccess() {
		return pIntegerValueLessThanEquals;
	}
	
	public ParserRule getIntegerValueLessThanEqualsRule() {
		return getIntegerValueLessThanEqualsAccess().getRule();
	}
	
	//DecimalValueEquals:
	//	EQUAL HASH value=Decimal;
	public DecimalValueEqualsElements getDecimalValueEqualsAccess() {
		return pDecimalValueEquals;
	}
	
	public ParserRule getDecimalValueEqualsRule() {
		return getDecimalValueEqualsAccess().getRule();
	}
	
	//DecimalValueNotEquals:
	//	NOT_EQUAL HASH value=Decimal;
	public DecimalValueNotEqualsElements getDecimalValueNotEqualsAccess() {
		return pDecimalValueNotEquals;
	}
	
	public ParserRule getDecimalValueNotEqualsRule() {
		return getDecimalValueNotEqualsAccess().getRule();
	}
	
	//DecimalValueGreaterThan:
	//	GT HASH value=Decimal;
	public DecimalValueGreaterThanElements getDecimalValueGreaterThanAccess() {
		return pDecimalValueGreaterThan;
	}
	
	public ParserRule getDecimalValueGreaterThanRule() {
		return getDecimalValueGreaterThanAccess().getRule();
	}
	
	//DecimalValueLessThan:
	//	LT HASH value=Decimal;
	public DecimalValueLessThanElements getDecimalValueLessThanAccess() {
		return pDecimalValueLessThan;
	}
	
	public ParserRule getDecimalValueLessThanRule() {
		return getDecimalValueLessThanAccess().getRule();
	}
	
	//DecimalValueGreaterThanEquals:
	//	GTE HASH value=Decimal;
	public DecimalValueGreaterThanEqualsElements getDecimalValueGreaterThanEqualsAccess() {
		return pDecimalValueGreaterThanEquals;
	}
	
	public ParserRule getDecimalValueGreaterThanEqualsRule() {
		return getDecimalValueGreaterThanEqualsAccess().getRule();
	}
	
	//DecimalValueLessThanEquals:
	//	LTE HASH value=Decimal;
	public DecimalValueLessThanEqualsElements getDecimalValueLessThanEqualsAccess() {
		return pDecimalValueLessThanEquals;
	}
	
	public ParserRule getDecimalValueLessThanEqualsRule() {
		return getDecimalValueLessThanEqualsAccess().getRule();
	}
	
	//NestedExpression:
	//	ROUND_OPEN nested=ExpressionConstraint ROUND_CLOSE;
	public NestedExpressionElements getNestedExpressionAccess() {
		return pNestedExpression;
	}
	
	public ParserRule getNestedExpressionRule() {
		return getNestedExpressionAccess().getRule();
	}
	
	//// hidden grammar rules
	//SnomedIdentifier hidden():
	//	DIGIT_NONZERO (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO) (DIGIT_NONZERO | ZERO)
	//	(DIGIT_NONZERO | ZERO)+;
	public SnomedIdentifierElements getSnomedIdentifierAccess() {
		return pSnomedIdentifier;
	}
	
	public ParserRule getSnomedIdentifierRule() {
		return getSnomedIdentifierAccess().getRule();
	}
	
	//Term hidden():
	//	TermCharacter+ (WS+ TermCharacter+)*;
	public TermElements getTermAccess() {
		return pTerm;
	}
	
	public ParserRule getTermRule() {
		return getTermAccess().getRule();
	}
	
	//TermCharacter hidden():
	//	LT | GT
	//	| DBL_LT | DBL_GT
	//	| LT_EM | GT_EM
	//	| GTE | LTE
	//	| AND | OR | NOT | MINUS
	//	| ZERO | DIGIT_NONZERO
	//	| LETTER | CARET
	//	| EQUAL | NOT_EQUAL | PLUS
	//	| CURLY_OPEN | CURLY_CLOSE
	//	| ROUND_OPEN | ROUND_CLOSE
	//	| SQUARE_OPEN | SQUARE_CLOSE
	//	| DOT | COLON | COMMA
	//	| REVERSED | TO
	//	| WILDCARD | HASH | DASH
	//	| OTHER_CHARACTER;
	public TermCharacterElements getTermCharacterAccess() {
		return pTermCharacter;
	}
	
	public ParserRule getTermCharacterRule() {
		return getTermCharacterAccess().getRule();
	}
	
	//NonNegativeInteger ecore::EInt hidden():
	//	ZERO | DIGIT_NONZERO (DIGIT_NONZERO | ZERO)*;
	public NonNegativeIntegerElements getNonNegativeIntegerAccess() {
		return pNonNegativeInteger;
	}
	
	public ParserRule getNonNegativeIntegerRule() {
		return getNonNegativeIntegerAccess().getRule();
	}
	
	//MaxValue ecore::EInt hidden():
	//	NonNegativeInteger | WILDCARD;
	public MaxValueElements getMaxValueAccess() {
		return pMaxValue;
	}
	
	public ParserRule getMaxValueRule() {
		return getMaxValueAccess().getRule();
	}
	
	//AndOperator hidden():
	//	AND | COMMA;
	public AndOperatorElements getAndOperatorAccess() {
		return pAndOperator;
	}
	
	public ParserRule getAndOperatorRule() {
		return getAndOperatorAccess().getRule();
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
	
	//terminal REVERSED:
	//	'R';
	public TerminalRule getREVERSEDRule() {
		return tREVERSED;
	}
	
	//terminal TO:
	//	'..';
	public TerminalRule getTORule() {
		return tTO;
	}
	
	//terminal AND:
	//	'AND';
	public TerminalRule getANDRule() {
		return tAND;
	}
	
	//terminal OR:
	//	'OR';
	public TerminalRule getORRule() {
		return tOR;
	}
	
	//terminal MINUS:
	//	'MINUS';
	public TerminalRule getMINUSRule() {
		return tMINUS;
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
	
	//terminal LETTER:
	//	'a'..'z' | 'A'..'Z';
	public TerminalRule getLETTERRule() {
		return tLETTER;
	}
	
	//terminal PIPE:
	//	'|';
	public TerminalRule getPIPERule() {
		return tPIPE;
	}
	
	//terminal COLON:
	//	':';
	public TerminalRule getCOLONRule() {
		return tCOLON;
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
	
	//terminal ROUND_OPEN:
	//	'(';
	public TerminalRule getROUND_OPENRule() {
		return tROUND_OPEN;
	}
	
	//terminal ROUND_CLOSE:
	//	')';
	public TerminalRule getROUND_CLOSERule() {
		return tROUND_CLOSE;
	}
	
	//terminal SQUARE_OPEN:
	//	'[';
	public TerminalRule getSQUARE_OPENRule() {
		return tSQUARE_OPEN;
	}
	
	//terminal SQUARE_CLOSE:
	//	']';
	public TerminalRule getSQUARE_CLOSERule() {
		return tSQUARE_CLOSE;
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
	
	//terminal CARET:
	//	'^';
	public TerminalRule getCARETRule() {
		return tCARET;
	}
	
	//terminal NOT:
	//	'!';
	public TerminalRule getNOTRule() {
		return tNOT;
	}
	
	//terminal DOT:
	//	'.';
	public TerminalRule getDOTRule() {
		return tDOT;
	}
	
	//terminal WILDCARD:
	//	'*';
	public TerminalRule getWILDCARDRule() {
		return tWILDCARD;
	}
	
	//terminal EQUAL:
	//	'=';
	public TerminalRule getEQUALRule() {
		return tEQUAL;
	}
	
	//terminal NOT_EQUAL:
	//	'!=';
	public TerminalRule getNOT_EQUALRule() {
		return tNOT_EQUAL;
	}
	
	//terminal LT:
	//	'<';
	public TerminalRule getLTRule() {
		return tLT;
	}
	
	//terminal GT:
	//	'>';
	public TerminalRule getGTRule() {
		return tGT;
	}
	
	//terminal DBL_LT:
	//	'<<';
	public TerminalRule getDBL_LTRule() {
		return tDBL_LT;
	}
	
	//terminal DBL_GT:
	//	'>>';
	public TerminalRule getDBL_GTRule() {
		return tDBL_GT;
	}
	
	//terminal LT_EM:
	//	'<!';
	public TerminalRule getLT_EMRule() {
		return tLT_EM;
	}
	
	//terminal GT_EM:
	//	'>!';
	public TerminalRule getGT_EMRule() {
		return tGT_EM;
	}
	
	//terminal GTE:
	//	'>=';
	public TerminalRule getGTERule() {
		return tGTE;
	}
	
	//terminal LTE:
	//	'<=';
	public TerminalRule getLTERule() {
		return tLTE;
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
	
	//terminal OTHER_CHARACTER:
	//	!'|';
	public TerminalRule getOTHER_CHARACTERRule() {
		return tOTHER_CHARACTER;
	}
	
	//terminal STRING:
	//	'"' ('\\' . | !('\\' | '"'))* '"' |
	//	"'" ('\\' . | !('\\' | "'"))* "'";
	public TerminalRule getSTRINGRule() {
		return tSTRING;
	}
}
