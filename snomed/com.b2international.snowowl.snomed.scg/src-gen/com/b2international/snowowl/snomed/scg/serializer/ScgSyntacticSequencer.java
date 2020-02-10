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
package com.b2international.snowowl.snomed.scg.serializer;

import com.b2international.snowowl.snomed.scg.services.ScgGrammarAccess;
import com.google.inject.Inject;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.AbstractElementAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.TokenAlias;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynNavigable;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynTransition;
import org.eclipse.xtext.serializer.sequencer.AbstractSyntacticSequencer;

@SuppressWarnings("all")
public class ScgSyntacticSequencer extends AbstractSyntacticSequencer {

	protected ScgGrammarAccess grammarAccess;
	protected AbstractElementAlias match_Expression_EQUIVALENT_TOTerminalRuleCall_1_0_1_q;
	protected AbstractElementAlias match_Refinement_COMMATerminalRuleCall_1_0_q;
	
	@Inject
	protected void init(IGrammarAccess access) {
		grammarAccess = (ScgGrammarAccess) access;
		match_Expression_EQUIVALENT_TOTerminalRuleCall_1_0_1_q = new TokenAlias(false, true, grammarAccess.getExpressionAccess().getEQUIVALENT_TOTerminalRuleCall_1_0_1());
		match_Refinement_COMMATerminalRuleCall_1_0_q = new TokenAlias(false, true, grammarAccess.getRefinementAccess().getCOMMATerminalRuleCall_1_0());
	}
	
	@Override
	protected String getUnassignedRuleCallToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (ruleCall.getRule() == grammarAccess.getCOLONRule())
			return getCOLONToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getCOMMARule())
			return getCOMMAToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getCURLY_CLOSERule())
			return getCURLY_CLOSEToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getCURLY_OPENRule())
			return getCURLY_OPENToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getEQUALRule())
			return getEQUALToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getEQUIVALENT_TORule())
			return getEQUIVALENT_TOToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getHASHRule())
			return getHASHToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getPLUSRule())
			return getPLUSToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getROUND_CLOSERule())
			return getROUND_CLOSEToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getROUND_OPENRule())
			return getROUND_OPENToken(semanticObject, ruleCall, node);
		else if (ruleCall.getRule() == grammarAccess.getSUBTYPE_OFRule())
			return getSUBTYPE_OFToken(semanticObject, ruleCall, node);
		return "";
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
	 * terminal HASH:
	 * 	'#';
	 */
	protected String getHASHToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "#";
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
	 * terminal SUBTYPE_OF:
	 * 	'<<<';
	 */
	protected String getSUBTYPE_OFToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "<<<";
	}
	
	@Override
	protected void emitUnassignedTokens(EObject semanticObject, ISynTransition transition, INode fromNode, INode toNode) {
		if (transition.getAmbiguousSyntaxes().isEmpty()) return;
		List<INode> transitionNodes = collectNodes(fromNode, toNode);
		for (AbstractElementAlias syntax : transition.getAmbiguousSyntaxes()) {
			List<INode> syntaxNodes = getNodesFor(transitionNodes, syntax);
			if (match_Expression_EQUIVALENT_TOTerminalRuleCall_1_0_1_q.equals(syntax))
				emit_Expression_EQUIVALENT_TOTerminalRuleCall_1_0_1_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_Refinement_COMMATerminalRuleCall_1_0_q.equals(syntax))
				emit_Refinement_COMMATerminalRuleCall_1_0_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else acceptNodes(getLastNavigableState(), syntaxNodes);
		}
	}

	/**
	 * Ambiguous syntax:
	 *     EQUIVALENT_TO?
	 *
	 * This ambiguous syntax occurs at:
	 *     (rule start) (ambiguity) expression=SubExpression
	 */
	protected void emit_Expression_EQUIVALENT_TOTerminalRuleCall_1_0_1_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
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
