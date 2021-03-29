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
package com.b2international.snowowl.snomed.etl.serializer;

import com.b2international.snowowl.snomed.etl.services.EtlGrammarAccess;
import com.google.inject.Inject;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.AbstractElementAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.AlternativeAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.TokenAlias;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynNavigable;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynTransition;
import org.eclipse.xtext.serializer.sequencer.AbstractSyntacticSequencer;

@SuppressWarnings("all")
public class EtlSyntacticSequencer extends AbstractSyntacticSequencer {

	protected EtlGrammarAccess grammarAccess;
	protected AbstractElementAlias match_AndAttributeSet_COMMATerminalRuleCall_1_1_1_or_CONJUNCTIONTerminalRuleCall_1_1_0;
	protected AbstractElementAlias match_AndExpressionConstraint_COMMATerminalRuleCall_1_1_1_or_CONJUNCTIONTerminalRuleCall_1_1_0;
	protected AbstractElementAlias match_AndRefinement_COMMATerminalRuleCall_1_0_1_1_or_CONJUNCTIONTerminalRuleCall_1_0_1_0;
	protected AbstractElementAlias match_EtlCardinality_TILDETerminalRuleCall_0_q;
	protected AbstractElementAlias match_ExpressionReplacementSlot_SCGTerminalRuleCall_3_q;
	protected AbstractElementAlias match_ExpressionTemplate_EQUIVALENT_TOTerminalRuleCall_1_0_0_1_q;
	protected AbstractElementAlias match_Refinement_COMMATerminalRuleCall_1_0_q;
	
	@Inject
	protected void init(IGrammarAccess access) {
		grammarAccess = (EtlGrammarAccess) access;
		match_AndAttributeSet_COMMATerminalRuleCall_1_1_1_or_CONJUNCTIONTerminalRuleCall_1_1_0 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getAndAttributeSetAccess().getCOMMATerminalRuleCall_1_1_1()), new TokenAlias(false, false, grammarAccess.getAndAttributeSetAccess().getCONJUNCTIONTerminalRuleCall_1_1_0()));
		match_AndExpressionConstraint_COMMATerminalRuleCall_1_1_1_or_CONJUNCTIONTerminalRuleCall_1_1_0 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getAndExpressionConstraintAccess().getCOMMATerminalRuleCall_1_1_1()), new TokenAlias(false, false, grammarAccess.getAndExpressionConstraintAccess().getCONJUNCTIONTerminalRuleCall_1_1_0()));
		match_AndRefinement_COMMATerminalRuleCall_1_0_1_1_or_CONJUNCTIONTerminalRuleCall_1_0_1_0 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getAndRefinementAccess().getCOMMATerminalRuleCall_1_0_1_1()), new TokenAlias(false, false, grammarAccess.getAndRefinementAccess().getCONJUNCTIONTerminalRuleCall_1_0_1_0()));
		match_EtlCardinality_TILDETerminalRuleCall_0_q = new TokenAlias(false, true, grammarAccess.getEtlCardinalityAccess().getTILDETerminalRuleCall_0());
		match_ExpressionReplacementSlot_SCGTerminalRuleCall_3_q = new TokenAlias(false, true, grammarAccess.getExpressionReplacementSlotAccess().getSCGTerminalRuleCall_3());
		match_ExpressionTemplate_EQUIVALENT_TOTerminalRuleCall_1_0_0_1_q = new TokenAlias(false, true, grammarAccess.getExpressionTemplateAccess().getEQUIVALENT_TOTerminalRuleCall_1_0_0_1());
		match_Refinement_COMMATerminalRuleCall_1_0_q = new TokenAlias(false, true, grammarAccess.getRefinementAccess().getCOMMATerminalRuleCall_1_0());
	}
	
	@Override
	protected String getUnassignedRuleCallToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (ruleCall.getRule() == grammarAccess.getCARETRule())
			return getCARETToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getCOLONRule())
			return getCOLONToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getCOMMARule())
			return getCOMMAToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getCONJUNCTIONRule())
			return getCONJUNCTIONToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getCURLY_CLOSERule())
			return getCURLY_CLOSEToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getCURLY_OPENRule())
			return getCURLY_OPENToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getDBL_GTRule())
			return getDBL_GTToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getDBL_GT_EMRule())
			return getDBL_GT_EMToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getDBL_LTRule())
			return getDBL_LTToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getDBL_LT_EMRule())
			return getDBL_LT_EMToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getDECRule())
			return getDECToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getDISJUNCTIONRule())
			return getDISJUNCTIONToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getDOTRule())
			return getDOTToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getDOUBLE_SQUARE_CLOSERule())
			return getDOUBLE_SQUARE_CLOSEToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getDOUBLE_SQUARE_OPENRule())
			return getDOUBLE_SQUARE_OPENToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getEQUALRule())
			return getEQUALToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getEQUIVALENT_TORule())
			return getEQUIVALENT_TOToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getEXCLUSIONRule())
			return getEXCLUSIONToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getGTRule())
			return getGTToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getGTERule())
			return getGTEToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getGT_EMRule())
			return getGT_EMToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getHASHRule())
			return getHASHToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getIDRule())
			return getIDToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getINTRule())
			return getINTToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getLTRule())
			return getLTToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getLTERule())
			return getLTEToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getLT_EMRule())
			return getLT_EMToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getNOT_EQUALRule())
			return getNOT_EQUALToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getPLUSRule())
			return getPLUSToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getREVERSEDRule())
			return getREVERSEDToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getROUND_CLOSERule())
			return getROUND_CLOSEToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getROUND_OPENRule())
			return getROUND_OPENToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getSCGRule())
			return getSCGToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getSQUARE_CLOSERule())
			return getSQUARE_CLOSEToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getSQUARE_OPENRule())
			return getSQUARE_OPENToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getSTRRule())
			return getSTRToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getSUBTYPE_OFRule())
			return getSUBTYPE_OFToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getTILDERule())
			return getTILDEToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getTORule())
			return getTOToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getTOKRule())
			return getTOKToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getWILDCARDRule())
			return getWILDCARDToken(semanticObject, ruleCall, node);
		return "";
	}
	
	/**
	 * terminal CARET:
	 * 	'^';
	 */
	protected String getCARETToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "^";
	}
	
	/**
	 * terminal COLON:
	 * 	':';
	 */
	protected String getCOLONToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return ":";
	}
	
	/**
	 * terminal COMMA:
	 * 	',';
	 */
	protected String getCOMMAToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return ",";
	}
	
	/**
	 * terminal CONJUNCTION:
	 * 	('a' | 'A') ('n' | 'N') ('d' | 'D');
	 */
	protected String getCONJUNCTIONToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "and";
	}
	
	/**
	 * terminal CURLY_CLOSE:
	 * 	'}';
	 */
	protected String getCURLY_CLOSEToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "}";
	}
	
	/**
	 * terminal CURLY_OPEN:
	 * 	'{';
	 */
	protected String getCURLY_OPENToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "{";
	}
	
	/**
	 * terminal DBL_GT:
	 * 	'>>';
	 */
	protected String getDBL_GTToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return ">>";
	}
	
	/**
	 * terminal DBL_GT_EM:
	 * 	'>>!';
	 */
	protected String getDBL_GT_EMToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return ">>!";
	}
	
	/**
	 * terminal DBL_LT:
	 * 	'<<';
	 */
	protected String getDBL_LTToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "<<";
	}
	
	/**
	 * terminal DBL_LT_EM:
	 * 	'<<!';
	 */
	protected String getDBL_LT_EMToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "<<!";
	}
	
	/**
	 * terminal DEC:
	 * 	'dec';
	 */
	protected String getDECToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "dec";
	}
	
	/**
	 * terminal DISJUNCTION:
	 * 	('o' | 'O') ('r' | 'R');
	 */
	protected String getDISJUNCTIONToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "or";
	}
	
	/**
	 * terminal DOT:
	 * 	'.';
	 */
	protected String getDOTToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return ".";
	}
	
	/**
	 * terminal DOUBLE_SQUARE_CLOSE:
	 * 	']]';
	 */
	protected String getDOUBLE_SQUARE_CLOSEToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "]]";
	}
	
	/**
	 * terminal DOUBLE_SQUARE_OPEN:
	 * 	'[[';
	 */
	protected String getDOUBLE_SQUARE_OPENToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "[[";
	}
	
	/**
	 * terminal EQUAL:
	 * 	'=';
	 */
	protected String getEQUALToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "=";
	}
	
	/**
	 * terminal EQUIVALENT_TO:
	 * 	'===';
	 */
	protected String getEQUIVALENT_TOToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "===";
	}
	
	/**
	 * terminal EXCLUSION:
	 * 	('m' | 'M') ('i' | 'I') ('n' | 'N') ('u' | 'U') ('s' | 'S');
	 */
	protected String getEXCLUSIONToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "minus";
	}
	
	/**
	 * terminal GT:
	 * 	'>';
	 */
	protected String getGTToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return ">";
	}
	
	/**
	 * terminal GTE:
	 * 	'>=';
	 */
	protected String getGTEToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return ">=";
	}
	
	/**
	 * terminal GT_EM:
	 * 	'>!';
	 */
	protected String getGT_EMToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return ">!";
	}
	
	/**
	 * terminal HASH:
	 * 	'#';
	 */
	protected String getHASHToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "#";
	}
	
	/**
	 * terminal ID:
	 * 	'id';
	 */
	protected String getIDToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "id";
	}
	
	/**
	 * terminal INT:
	 * 	'int';
	 */
	protected String getINTToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "int";
	}
	
	/**
	 * terminal LT:
	 * 	'<';
	 */
	protected String getLTToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "<";
	}
	
	/**
	 * terminal LTE:
	 * 	'<=';
	 */
	protected String getLTEToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "<=";
	}
	
	/**
	 * terminal LT_EM:
	 * 	'<!';
	 */
	protected String getLT_EMToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "<!";
	}
	
	/**
	 * terminal NOT_EQUAL:
	 * 	'!=';
	 */
	protected String getNOT_EQUALToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "!=";
	}
	
	/**
	 * terminal PLUS:
	 * 	'+';
	 */
	protected String getPLUSToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "+";
	}
	
	/**
	 * terminal REVERSED:
	 * 	'R';
	 */
	protected String getREVERSEDToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "R";
	}
	
	/**
	 * terminal ROUND_CLOSE:
	 * 	')';
	 */
	protected String getROUND_CLOSEToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return ")";
	}
	
	/**
	 * terminal ROUND_OPEN:
	 * 	'(';
	 */
	protected String getROUND_OPENToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "(";
	}
	
	/**
	 * terminal SCG:
	 * 	'scg';
	 */
	protected String getSCGToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "scg";
	}
	
	/**
	 * terminal SQUARE_CLOSE:
	 * 	']';
	 */
	protected String getSQUARE_CLOSEToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "]";
	}
	
	/**
	 * terminal SQUARE_OPEN:
	 * 	'[';
	 */
	protected String getSQUARE_OPENToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "[";
	}
	
	/**
	 * terminal STR:
	 * 	'str';
	 */
	protected String getSTRToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "str";
	}
	
	/**
	 * terminal SUBTYPE_OF:
	 * 	'<<<';
	 */
	protected String getSUBTYPE_OFToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "<<<";
	}
	
	/**
	 * terminal TILDE:
	 * 	'~';
	 */
	protected String getTILDEToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "~";
	}
	
	/**
	 * terminal TO:
	 * 	'..';
	 */
	protected String getTOToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "..";
	}
	
	/**
	 * terminal TOK:
	 * 	'tok';
	 */
	protected String getTOKToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "tok";
	}
	
	/**
	 * terminal WILDCARD:
	 * 	'*';
	 */
	protected String getWILDCARDToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "*";
	}
	
	@Override
	protected void emitUnassignedTokens(EObject semanticObject, ISynTransition transition, INode fromNode, INode toNode) {
		if (transition.getAmbiguousSyntaxes().isEmpty()) return;
		List<INode> transitionNodes = collectNodes(fromNode, toNode);
		for (AbstractElementAlias syntax : transition.getAmbiguousSyntaxes()) {
			List<INode> syntaxNodes = getNodesFor(transitionNodes, syntax);
			if (match_AndAttributeSet_COMMATerminalRuleCall_1_1_1_or_CONJUNCTIONTerminalRuleCall_1_1_0.equals(syntax))
				emit_AndAttributeSet_COMMATerminalRuleCall_1_1_1_or_CONJUNCTIONTerminalRuleCall_1_1_0(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_AndExpressionConstraint_COMMATerminalRuleCall_1_1_1_or_CONJUNCTIONTerminalRuleCall_1_1_0.equals(syntax))
				emit_AndExpressionConstraint_COMMATerminalRuleCall_1_1_1_or_CONJUNCTIONTerminalRuleCall_1_1_0(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_AndRefinement_COMMATerminalRuleCall_1_0_1_1_or_CONJUNCTIONTerminalRuleCall_1_0_1_0.equals(syntax))
				emit_AndRefinement_COMMATerminalRuleCall_1_0_1_1_or_CONJUNCTIONTerminalRuleCall_1_0_1_0(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_EtlCardinality_TILDETerminalRuleCall_0_q.equals(syntax))
				emit_EtlCardinality_TILDETerminalRuleCall_0_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_ExpressionReplacementSlot_SCGTerminalRuleCall_3_q.equals(syntax))
				emit_ExpressionReplacementSlot_SCGTerminalRuleCall_3_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_ExpressionTemplate_EQUIVALENT_TOTerminalRuleCall_1_0_0_1_q.equals(syntax))
				emit_ExpressionTemplate_EQUIVALENT_TOTerminalRuleCall_1_0_0_1_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_Refinement_COMMATerminalRuleCall_1_0_q.equals(syntax))
				emit_Refinement_COMMATerminalRuleCall_1_0_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else acceptNodes(getLastNavigableState(), syntaxNodes);
		}
	}

	/**
	 * Ambiguous syntax:
	 *     CONJUNCTION | COMMA
	 *
	 * This ambiguous syntax occurs at:
	 *     {AndRefinement.left=} (ambiguity) right=SubAttributeSet
	 */
	protected void emit_AndAttributeSet_COMMATerminalRuleCall_1_1_1_or_CONJUNCTIONTerminalRuleCall_1_1_0(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     CONJUNCTION | COMMA
	 *
	 * This ambiguous syntax occurs at:
	 *     {AndExpressionConstraint.left=} (ambiguity) right=ExclusionExpressionConstraint
	 */
	protected void emit_AndExpressionConstraint_COMMATerminalRuleCall_1_1_1_or_CONJUNCTIONTerminalRuleCall_1_1_0(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     CONJUNCTION | COMMA
	 *
	 * This ambiguous syntax occurs at:
	 *     {AndRefinement.left=} (ambiguity) right=SubRefinement
	 */
	protected void emit_AndRefinement_COMMATerminalRuleCall_1_0_1_1_or_CONJUNCTIONTerminalRuleCall_1_0_1_0(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     TILDE?
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) (ambiguity) min=NonNegativeInteger
	 */
	protected void emit_EtlCardinality_TILDETerminalRuleCall_0_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     SCG?
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) DOUBLE_SQUARE_OPEN PLUS (ambiguity) DOUBLE_SQUARE_CLOSE (rule start)
	 *     (rule start) DOUBLE_SQUARE_OPEN PLUS (ambiguity) ROUND_OPEN constraint=ExpressionConstraint
	 *     (rule start) DOUBLE_SQUARE_OPEN PLUS (ambiguity) name=SLOTNAME_STRING
	 */
	protected void emit_ExpressionReplacementSlot_SCGTerminalRuleCall_3_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     EQUIVALENT_TO?
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) (ambiguity) expression=SubExpression
	 */
	protected void emit_ExpressionTemplate_EQUIVALENT_TOTerminalRuleCall_1_0_0_1_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     COMMA?
	 *
	 * This ambiguous syntax occurs at:
	 *     attributes+=Attribute (ambiguity) groups+=AttributeGroup
	 *     groups+=AttributeGroup (ambiguity) groups+=AttributeGroup
	 */
	protected void emit_Refinement_COMMATerminalRuleCall_1_0_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
}
