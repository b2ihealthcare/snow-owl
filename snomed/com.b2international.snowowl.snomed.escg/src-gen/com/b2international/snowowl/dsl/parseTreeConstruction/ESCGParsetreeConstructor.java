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
package com.b2international.snowowl.dsl.parseTreeConstruction;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parsetree.reconstr.IEObjectConsumer;

import com.b2international.snowowl.dsl.services.ESCGGrammarAccess;

import com.google.inject.Inject;

@SuppressWarnings("all")
public class ESCGParsetreeConstructor extends org.eclipse.xtext.parsetree.reconstr.impl.AbstractParseTreeConstructor {
		
	@Inject
	private ESCGGrammarAccess grammarAccess;
	
	@Override
	protected AbstractToken getRootToken(IEObjectConsumer inst) {
		return new ThisRootNode(inst);	
	}
	
protected class ThisRootNode extends RootToken {
	public ThisRootNode(IEObjectConsumer inst) {
		super(inst);
	}
	
	@Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Expression_Group(this, this, 0, inst);
			case 1: return new SubExpression_Group(this, this, 1, inst);
			case 2: return new LValue_Alternatives(this, this, 2, inst);
			case 3: return new RefSet_Group(this, this, 3, inst);
			case 4: return new ConceptGroup_Group(this, this, 4, inst);
			case 5: return new Concept_Group(this, this, 5, inst);
			case 6: return new Refinements_Alternatives(this, this, 6, inst);
			case 7: return new AttributeGroup_Group(this, this, 7, inst);
			case 8: return new AttributeSet_Group(this, this, 8, inst);
			case 9: return new Attribute_Group(this, this, 9, inst);
			case 10: return new AttributeAssignment_Alternatives(this, this, 10, inst);
			case 11: return new ConceptAssignment_Group(this, this, 11, inst);
			case 12: return new NumericalAssignment_Group(this, this, 12, inst);
			case 13: return new NumericalAssignmentGroup_Group(this, this, 13, inst);
			case 14: return new RValue_OrParserRuleCall(this, this, 14, inst);
			case 15: return new Or_Group(this, this, 15, inst);
			case 16: return new And_Group(this, this, 16, inst);
			case 17: return new NegatableSubExpression_Group(this, this, 17, inst);
			case 18: return new TerminalRValue_Alternatives(this, this, 18, inst);
			default: return null;
		}	
	}	
}
	

/************ begin Rule Expression ****************
 *
 * // parser rules
 *  Expression hidden(WS, SL_COMMENT, ML_COMMENT):
 * 
 * 	(subExpression+=SubExpression (UNION_TOKEN subExpression+=SubExpression)*)?;
 *
 **/

// (subExpression+=SubExpression (UNION_TOKEN subExpression+=SubExpression)*)?
protected class Expression_Group extends GroupToken {
	
	public Expression_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getExpressionAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Expression_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Expression_SubExpressionAssignment_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getExpressionRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// subExpression+=SubExpression
protected class Expression_SubExpressionAssignment_0 extends AssignmentToken  {
	
	public Expression_SubExpressionAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getExpressionAccess().getSubExpressionAssignment_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new SubExpression_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("subExpression",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("subExpression");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getSubExpressionRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getExpressionAccess().getSubExpressionSubExpressionParserRuleCall_0_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, consumed);
		}	
	}	
}

// (UNION_TOKEN subExpression+=SubExpression)*
protected class Expression_Group_1 extends GroupToken {
	
	public Expression_Group_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getExpressionAccess().getGroup_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Expression_SubExpressionAssignment_1_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// UNION_TOKEN
protected class Expression_UNION_TOKENTerminalRuleCall_1_0 extends UnassignedTextToken {

	public Expression_UNION_TOKENTerminalRuleCall_1_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getExpressionAccess().getUNION_TOKENTerminalRuleCall_1_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Expression_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Expression_SubExpressionAssignment_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}

// subExpression+=SubExpression
protected class Expression_SubExpressionAssignment_1_1 extends AssignmentToken  {
	
	public Expression_SubExpressionAssignment_1_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getExpressionAccess().getSubExpressionAssignment_1_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new SubExpression_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("subExpression",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("subExpression");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getSubExpressionRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getExpressionAccess().getSubExpressionSubExpressionParserRuleCall_1_1_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new Expression_UNION_TOKENTerminalRuleCall_1_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}



/************ end Rule Expression ****************/


/************ begin Rule SubExpression ****************
 *
 * SubExpression:
 * 
 * 	lValues+=LValue (PLUS_SIGN lValues+=LValue)* (COLON refinements=Refinements)?;
 *
 **/

// lValues+=LValue (PLUS_SIGN lValues+=LValue)* (COLON refinements=Refinements)?
protected class SubExpression_Group extends GroupToken {
	
	public SubExpression_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getSubExpressionAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new SubExpression_Group_2(lastRuleCallOrigin, this, 0, inst);
			case 1: return new SubExpression_Group_1(lastRuleCallOrigin, this, 1, inst);
			case 2: return new SubExpression_LValuesAssignment_0(lastRuleCallOrigin, this, 2, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getSubExpressionRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// lValues+=LValue
protected class SubExpression_LValuesAssignment_0 extends AssignmentToken  {
	
	public SubExpression_LValuesAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getSubExpressionAccess().getLValuesAssignment_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new LValue_Alternatives(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("lValues",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("lValues");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getLValueRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getSubExpressionAccess().getLValuesLValueParserRuleCall_0_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, consumed);
		}	
	}	
}

// (PLUS_SIGN lValues+=LValue)*
protected class SubExpression_Group_1 extends GroupToken {
	
	public SubExpression_Group_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getSubExpressionAccess().getGroup_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new SubExpression_LValuesAssignment_1_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// PLUS_SIGN
protected class SubExpression_PLUS_SIGNTerminalRuleCall_1_0 extends UnassignedTextToken {

	public SubExpression_PLUS_SIGNTerminalRuleCall_1_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getSubExpressionAccess().getPLUS_SIGNTerminalRuleCall_1_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new SubExpression_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new SubExpression_LValuesAssignment_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}

// lValues+=LValue
protected class SubExpression_LValuesAssignment_1_1 extends AssignmentToken  {
	
	public SubExpression_LValuesAssignment_1_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getSubExpressionAccess().getLValuesAssignment_1_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new LValue_Alternatives(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("lValues",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("lValues");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getLValueRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getSubExpressionAccess().getLValuesLValueParserRuleCall_1_1_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new SubExpression_PLUS_SIGNTerminalRuleCall_1_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}


// (COLON refinements=Refinements)?
protected class SubExpression_Group_2 extends GroupToken {
	
	public SubExpression_Group_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getSubExpressionAccess().getGroup_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new SubExpression_RefinementsAssignment_2_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// COLON
protected class SubExpression_COLONTerminalRuleCall_2_0 extends UnassignedTextToken {

	public SubExpression_COLONTerminalRuleCall_2_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getSubExpressionAccess().getCOLONTerminalRuleCall_2_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new SubExpression_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new SubExpression_LValuesAssignment_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}

// refinements=Refinements
protected class SubExpression_RefinementsAssignment_2_1 extends AssignmentToken  {
	
	public SubExpression_RefinementsAssignment_2_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getSubExpressionAccess().getRefinementsAssignment_2_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Refinements_Alternatives(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("refinements",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("refinements");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getRefinementsRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getSubExpressionAccess().getRefinementsRefinementsParserRuleCall_2_1_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new SubExpression_COLONTerminalRuleCall_2_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}



/************ end Rule SubExpression ****************/


/************ begin Rule LValue ****************
 *
 * LValue:
 * 
 * 	ConceptGroup | RefSet;
 *
 **/

// ConceptGroup | RefSet
protected class LValue_Alternatives extends AlternativesToken {

	public LValue_Alternatives(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Alternatives getGrammarElement() {
		return grammarAccess.getLValueAccess().getAlternatives();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new LValue_ConceptGroupParserRuleCall_0(lastRuleCallOrigin, this, 0, inst);
			case 1: return new LValue_RefSetParserRuleCall_1(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getConceptGroupRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getRefSetRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// ConceptGroup
protected class LValue_ConceptGroupParserRuleCall_0 extends RuleCallToken {
	
	public LValue_ConceptGroupParserRuleCall_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getLValueAccess().getConceptGroupParserRuleCall_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new ConceptGroup_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getConceptGroupRule().getType().getClassifier())
			return null;
		if(checkForRecursion(ConceptGroup_Group.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}

// RefSet
protected class LValue_RefSetParserRuleCall_1 extends RuleCallToken {
	
	public LValue_RefSetParserRuleCall_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getLValueAccess().getRefSetParserRuleCall_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new RefSet_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getRefSetRule().getType().getClassifier())
			return null;
		if(checkForRecursion(RefSet_Group.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}


/************ end Rule LValue ****************/


/************ begin Rule RefSet ****************
 *
 * RefSet:
 * 
 * 	negated?=NOT_TOKEN? CARET id=ConceptId (PIPE term=Term PIPE)?;
 *
 **/

// negated?=NOT_TOKEN? CARET id=ConceptId (PIPE term=Term PIPE)?
protected class RefSet_Group extends GroupToken {
	
	public RefSet_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getRefSetAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new RefSet_Group_3(lastRuleCallOrigin, this, 0, inst);
			case 1: return new RefSet_IdAssignment_2(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getRefSetRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// negated?=NOT_TOKEN?
protected class RefSet_NegatedAssignment_0 extends AssignmentToken  {
	
	public RefSet_NegatedAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getRefSetAccess().getNegatedAssignment_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("negated",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("negated");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getRefSetAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0(), value, null)) {
			type = AssignmentType.TERMINAL_RULE_CALL;
			element = grammarAccess.getRefSetAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0();
			return obj;
		}
		return null;
	}

}

// CARET
protected class RefSet_CARETTerminalRuleCall_1 extends UnassignedTextToken {

	public RefSet_CARETTerminalRuleCall_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getRefSetAccess().getCARETTerminalRuleCall_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new RefSet_NegatedAssignment_0(lastRuleCallOrigin, this, 0, inst);
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index - 1, inst);
		}	
	}

}

// id=ConceptId
protected class RefSet_IdAssignment_2 extends AssignmentToken  {
	
	public RefSet_IdAssignment_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getRefSetAccess().getIdAssignment_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new RefSet_CARETTerminalRuleCall_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("id",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("id");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getRefSetAccess().getIdConceptIdParserRuleCall_2_0(), value, null)) {
			type = AssignmentType.DATATYPE_RULE_CALL;
			element = grammarAccess.getRefSetAccess().getIdConceptIdParserRuleCall_2_0();
			return obj;
		}
		return null;
	}

}

// (PIPE term=Term PIPE)?
protected class RefSet_Group_3 extends GroupToken {
	
	public RefSet_Group_3(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getRefSetAccess().getGroup_3();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new RefSet_PIPETerminalRuleCall_3_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// PIPE
protected class RefSet_PIPETerminalRuleCall_3_0 extends UnassignedTextToken {

	public RefSet_PIPETerminalRuleCall_3_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getRefSetAccess().getPIPETerminalRuleCall_3_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new RefSet_IdAssignment_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// term=Term
protected class RefSet_TermAssignment_3_1 extends AssignmentToken  {
	
	public RefSet_TermAssignment_3_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getRefSetAccess().getTermAssignment_3_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new RefSet_PIPETerminalRuleCall_3_0(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("term",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("term");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getRefSetAccess().getTermTermParserRuleCall_3_1_0(), value, null)) {
			type = AssignmentType.DATATYPE_RULE_CALL;
			element = grammarAccess.getRefSetAccess().getTermTermParserRuleCall_3_1_0();
			return obj;
		}
		return null;
	}

}

// PIPE
protected class RefSet_PIPETerminalRuleCall_3_2 extends UnassignedTextToken {

	public RefSet_PIPETerminalRuleCall_3_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getRefSetAccess().getPIPETerminalRuleCall_3_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new RefSet_TermAssignment_3_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}



/************ end Rule RefSet ****************/


/************ begin Rule ConceptGroup ****************
 *
 * //  ! << 1234567|Left hand|
 *  ConceptGroup:
 * 
 * 	negated?=NOT_TOKEN? constraint=(SUBTYPE | INCLUSIVE_SUBTYPE)? concept=Concept;
 *
 **/

// negated?=NOT_TOKEN? constraint=(SUBTYPE | INCLUSIVE_SUBTYPE)? concept=Concept
protected class ConceptGroup_Group extends GroupToken {
	
	public ConceptGroup_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getConceptGroupAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new ConceptGroup_ConceptAssignment_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getConceptGroupRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// negated?=NOT_TOKEN?
protected class ConceptGroup_NegatedAssignment_0 extends AssignmentToken  {
	
	public ConceptGroup_NegatedAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getConceptGroupAccess().getNegatedAssignment_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("negated",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("negated");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getConceptGroupAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0(), value, null)) {
			type = AssignmentType.TERMINAL_RULE_CALL;
			element = grammarAccess.getConceptGroupAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0();
			return obj;
		}
		return null;
	}

}

// constraint=(SUBTYPE | INCLUSIVE_SUBTYPE)?
protected class ConceptGroup_ConstraintAssignment_1 extends AssignmentToken  {
	
	public ConceptGroup_ConstraintAssignment_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getConceptGroupAccess().getConstraintAssignment_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new ConceptGroup_NegatedAssignment_0(lastRuleCallOrigin, this, 0, inst);
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index - 1, inst);
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("constraint",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("constraint");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getConceptGroupAccess().getConstraintSUBTYPETerminalRuleCall_1_0_0(), value, null)) {
			type = AssignmentType.TERMINAL_RULE_CALL;
			element = grammarAccess.getConceptGroupAccess().getConstraintSUBTYPETerminalRuleCall_1_0_0();
			return obj;
		}
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getConceptGroupAccess().getConstraintINCLUSIVE_SUBTYPETerminalRuleCall_1_0_1(), value, null)) {
			type = AssignmentType.TERMINAL_RULE_CALL;
			element = grammarAccess.getConceptGroupAccess().getConstraintINCLUSIVE_SUBTYPETerminalRuleCall_1_0_1();
			return obj;
		}
		return null;
	}

}

// concept=Concept
protected class ConceptGroup_ConceptAssignment_2 extends AssignmentToken  {
	
	public ConceptGroup_ConceptAssignment_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getConceptGroupAccess().getConceptAssignment_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Concept_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("concept",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("concept");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getConceptRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getConceptGroupAccess().getConceptConceptParserRuleCall_2_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new ConceptGroup_ConstraintAssignment_1(lastRuleCallOrigin, next, actIndex, consumed);
			case 1: return new ConceptGroup_NegatedAssignment_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index - 2, consumed);
		}	
	}	
}


/************ end Rule ConceptGroup ****************/


/************ begin Rule Concept ****************
 *
 * //  1234567|Left hand|
 *  Concept:
 * 
 * 	id=ConceptId (PIPE WS* term=Term WS* PIPE)?;
 *
 **/

// id=ConceptId (PIPE WS* term=Term WS* PIPE)?
protected class Concept_Group extends GroupToken {
	
	public Concept_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getConceptAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Concept_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Concept_IdAssignment_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getConceptRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// id=ConceptId
protected class Concept_IdAssignment_0 extends AssignmentToken  {
	
	public Concept_IdAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getConceptAccess().getIdAssignment_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("id",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("id");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getConceptAccess().getIdConceptIdParserRuleCall_0_0(), value, null)) {
			type = AssignmentType.DATATYPE_RULE_CALL;
			element = grammarAccess.getConceptAccess().getIdConceptIdParserRuleCall_0_0();
			return obj;
		}
		return null;
	}

}

// (PIPE WS* term=Term WS* PIPE)?
protected class Concept_Group_1 extends GroupToken {
	
	public Concept_Group_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getConceptAccess().getGroup_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Concept_PIPETerminalRuleCall_1_4(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// PIPE
protected class Concept_PIPETerminalRuleCall_1_0 extends UnassignedTextToken {

	public Concept_PIPETerminalRuleCall_1_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getConceptAccess().getPIPETerminalRuleCall_1_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Concept_IdAssignment_0(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// term=Term
protected class Concept_TermAssignment_1_2 extends AssignmentToken  {
	
	public Concept_TermAssignment_1_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getConceptAccess().getTermAssignment_1_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Concept_PIPETerminalRuleCall_1_0(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("term",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("term");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getConceptAccess().getTermTermParserRuleCall_1_2_0(), value, null)) {
			type = AssignmentType.DATATYPE_RULE_CALL;
			element = grammarAccess.getConceptAccess().getTermTermParserRuleCall_1_2_0();
			return obj;
		}
		return null;
	}

}

// PIPE
protected class Concept_PIPETerminalRuleCall_1_4 extends UnassignedTextToken {

	public Concept_PIPETerminalRuleCall_1_4(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getConceptAccess().getPIPETerminalRuleCall_1_4();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Concept_TermAssignment_1_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}



/************ end Rule Concept ****************/


/************ begin Rule Refinements ****************
 *
 * Refinements:
 * 
 * 	attributeSet=AttributeSet attributeGroups+=AttributeGroup* | attributeGroups+=AttributeGroup+;
 *
 **/

// attributeSet=AttributeSet attributeGroups+=AttributeGroup* | attributeGroups+=AttributeGroup+
protected class Refinements_Alternatives extends AlternativesToken {

	public Refinements_Alternatives(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Alternatives getGrammarElement() {
		return grammarAccess.getRefinementsAccess().getAlternatives();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Refinements_Group_0(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Refinements_AttributeGroupsAssignment_1(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getRefinementsRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// attributeSet=AttributeSet attributeGroups+=AttributeGroup*
protected class Refinements_Group_0 extends GroupToken {
	
	public Refinements_Group_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getRefinementsAccess().getGroup_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Refinements_AttributeGroupsAssignment_0_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Refinements_AttributeSetAssignment_0_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}

// attributeSet=AttributeSet
protected class Refinements_AttributeSetAssignment_0_0 extends AssignmentToken  {
	
	public Refinements_AttributeSetAssignment_0_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getRefinementsAccess().getAttributeSetAssignment_0_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeSet_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("attributeSet",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("attributeSet");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getAttributeSetRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getRefinementsAccess().getAttributeSetAttributeSetParserRuleCall_0_0_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, consumed);
		}	
	}	
}

// attributeGroups+=AttributeGroup*
protected class Refinements_AttributeGroupsAssignment_0_1 extends AssignmentToken  {
	
	public Refinements_AttributeGroupsAssignment_0_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getRefinementsAccess().getAttributeGroupsAssignment_0_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeGroup_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("attributeGroups",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("attributeGroups");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getAttributeGroupRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getRefinementsAccess().getAttributeGroupsAttributeGroupParserRuleCall_0_1_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new Refinements_AttributeGroupsAssignment_0_1(lastRuleCallOrigin, next, actIndex, consumed);
			case 1: return new Refinements_AttributeSetAssignment_0_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}


// attributeGroups+=AttributeGroup+
protected class Refinements_AttributeGroupsAssignment_1 extends AssignmentToken  {
	
	public Refinements_AttributeGroupsAssignment_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getRefinementsAccess().getAttributeGroupsAssignment_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeGroup_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("attributeGroups",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("attributeGroups");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getAttributeGroupRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getRefinementsAccess().getAttributeGroupsAttributeGroupParserRuleCall_1_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new Refinements_AttributeGroupsAssignment_1(lastRuleCallOrigin, next, actIndex, consumed);
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index - 1, consumed);
		}	
	}	
}


/************ end Rule Refinements ****************/


/************ begin Rule AttributeGroup ****************
 *
 * AttributeGroup:
 * 
 * 	OPENING_CURLY_BRACKET AttributeSet CLOSING_CURLY_BRACKET;
 *
 **/

// OPENING_CURLY_BRACKET AttributeSet CLOSING_CURLY_BRACKET
protected class AttributeGroup_Group extends GroupToken {
	
	public AttributeGroup_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getAttributeGroupAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeGroup_CLOSING_CURLY_BRACKETTerminalRuleCall_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getAttributeSetRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// OPENING_CURLY_BRACKET
protected class AttributeGroup_OPENING_CURLY_BRACKETTerminalRuleCall_0 extends UnassignedTextToken {

	public AttributeGroup_OPENING_CURLY_BRACKETTerminalRuleCall_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeGroupAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

}

// AttributeSet
protected class AttributeGroup_AttributeSetParserRuleCall_1 extends RuleCallToken {
	
	public AttributeGroup_AttributeSetParserRuleCall_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeGroupAccess().getAttributeSetParserRuleCall_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeSet_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(checkForRecursion(AttributeSet_Group.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeGroup_OPENING_CURLY_BRACKETTerminalRuleCall_0(lastRuleCallOrigin, next, actIndex, inst);
			default: return null;
		}	
	}	
}

// CLOSING_CURLY_BRACKET
protected class AttributeGroup_CLOSING_CURLY_BRACKETTerminalRuleCall_2 extends UnassignedTextToken {

	public AttributeGroup_CLOSING_CURLY_BRACKETTerminalRuleCall_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeGroupAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeGroup_AttributeSetParserRuleCall_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}


/************ end Rule AttributeGroup ****************/


/************ begin Rule AttributeSet ****************
 *
 * AttributeSet:
 * 
 * 	attributes+=Attribute (COMMA attributes+=Attribute)*;
 *
 **/

// attributes+=Attribute (COMMA attributes+=Attribute)*
protected class AttributeSet_Group extends GroupToken {
	
	public AttributeSet_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getAttributeSetAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeSet_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new AttributeSet_AttributesAssignment_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getAttributeSetRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// attributes+=Attribute
protected class AttributeSet_AttributesAssignment_0 extends AssignmentToken  {
	
	public AttributeSet_AttributesAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getAttributeSetAccess().getAttributesAssignment_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Attribute_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("attributes",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("attributes");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getAttributeRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getAttributeSetAccess().getAttributesAttributeParserRuleCall_0_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, consumed);
		}	
	}	
}

// (COMMA attributes+=Attribute)*
protected class AttributeSet_Group_1 extends GroupToken {
	
	public AttributeSet_Group_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getAttributeSetAccess().getGroup_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeSet_AttributesAssignment_1_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// COMMA
protected class AttributeSet_COMMATerminalRuleCall_1_0 extends UnassignedTextToken {

	public AttributeSet_COMMATerminalRuleCall_1_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeSetAccess().getCOMMATerminalRuleCall_1_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeSet_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new AttributeSet_AttributesAssignment_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}

// attributes+=Attribute
protected class AttributeSet_AttributesAssignment_1_1 extends AssignmentToken  {
	
	public AttributeSet_AttributesAssignment_1_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getAttributeSetAccess().getAttributesAssignment_1_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Attribute_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("attributes",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("attributes");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getAttributeRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getAttributeSetAccess().getAttributesAttributeParserRuleCall_1_1_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new AttributeSet_COMMATerminalRuleCall_1_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}



/************ end Rule AttributeSet ****************/


/************ begin Rule Attribute ****************
 *
 * Attribute:
 * 
 * 	optional?=OPTIONAL? assignment=AttributeAssignment;
 *
 **/

// optional?=OPTIONAL? assignment=AttributeAssignment
protected class Attribute_Group extends GroupToken {
	
	public Attribute_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getAttributeAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Attribute_AssignmentAssignment_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getAttributeRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// optional?=OPTIONAL?
protected class Attribute_OptionalAssignment_0 extends AssignmentToken  {
	
	public Attribute_OptionalAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getAttributeAccess().getOptionalAssignment_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("optional",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("optional");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getAttributeAccess().getOptionalOPTIONALTerminalRuleCall_0_0(), value, null)) {
			type = AssignmentType.TERMINAL_RULE_CALL;
			element = grammarAccess.getAttributeAccess().getOptionalOPTIONALTerminalRuleCall_0_0();
			return obj;
		}
		return null;
	}

}

// assignment=AttributeAssignment
protected class Attribute_AssignmentAssignment_1 extends AssignmentToken  {
	
	public Attribute_AssignmentAssignment_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getAttributeAccess().getAssignmentAssignment_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeAssignment_Alternatives(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("assignment",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("assignment");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getAttributeAssignmentRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getAttributeAccess().getAssignmentAttributeAssignmentParserRuleCall_1_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new Attribute_OptionalAssignment_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index - 1, consumed);
		}	
	}	
}


/************ end Rule Attribute ****************/


/************ begin Rule AttributeAssignment ****************
 *
 * AttributeAssignment:
 * 
 * 	ConceptAssignment | NumericalAssignment | NumericalAssignmentGroup;
 *
 **/

// ConceptAssignment | NumericalAssignment | NumericalAssignmentGroup
protected class AttributeAssignment_Alternatives extends AlternativesToken {

	public AttributeAssignment_Alternatives(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Alternatives getGrammarElement() {
		return grammarAccess.getAttributeAssignmentAccess().getAlternatives();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeAssignment_ConceptAssignmentParserRuleCall_0(lastRuleCallOrigin, this, 0, inst);
			case 1: return new AttributeAssignment_NumericalAssignmentParserRuleCall_1(lastRuleCallOrigin, this, 1, inst);
			case 2: return new AttributeAssignment_NumericalAssignmentGroupParserRuleCall_2(lastRuleCallOrigin, this, 2, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getConceptAssignmentRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getNumericalAssignmentRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getNumericalAssignmentGroupRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// ConceptAssignment
protected class AttributeAssignment_ConceptAssignmentParserRuleCall_0 extends RuleCallToken {
	
	public AttributeAssignment_ConceptAssignmentParserRuleCall_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeAssignmentAccess().getConceptAssignmentParserRuleCall_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new ConceptAssignment_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getConceptAssignmentRule().getType().getClassifier())
			return null;
		if(checkForRecursion(ConceptAssignment_Group.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}

// NumericalAssignment
protected class AttributeAssignment_NumericalAssignmentParserRuleCall_1 extends RuleCallToken {
	
	public AttributeAssignment_NumericalAssignmentParserRuleCall_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeAssignmentAccess().getNumericalAssignmentParserRuleCall_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NumericalAssignment_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getNumericalAssignmentRule().getType().getClassifier())
			return null;
		if(checkForRecursion(NumericalAssignment_Group.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}

// NumericalAssignmentGroup
protected class AttributeAssignment_NumericalAssignmentGroupParserRuleCall_2 extends RuleCallToken {
	
	public AttributeAssignment_NumericalAssignmentGroupParserRuleCall_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeAssignmentAccess().getNumericalAssignmentGroupParserRuleCall_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NumericalAssignmentGroup_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getNumericalAssignmentGroupRule().getType().getClassifier())
			return null;
		if(checkForRecursion(NumericalAssignmentGroup_Group.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}


/************ end Rule AttributeAssignment ****************/


/************ begin Rule ConceptAssignment ****************
 *
 * ConceptAssignment:
 * 
 * 	name=LValue EQUAL_SIGN value=RValue;
 *
 **/

// name=LValue EQUAL_SIGN value=RValue
protected class ConceptAssignment_Group extends GroupToken {
	
	public ConceptAssignment_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getConceptAssignmentAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new ConceptAssignment_ValueAssignment_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getConceptAssignmentRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// name=LValue
protected class ConceptAssignment_NameAssignment_0 extends AssignmentToken  {
	
	public ConceptAssignment_NameAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getConceptAssignmentAccess().getNameAssignment_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new LValue_Alternatives(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("name",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("name");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getLValueRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getConceptAssignmentAccess().getNameLValueParserRuleCall_0_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, consumed);
		}	
	}	
}

// EQUAL_SIGN
protected class ConceptAssignment_EQUAL_SIGNTerminalRuleCall_1 extends UnassignedTextToken {

	public ConceptAssignment_EQUAL_SIGNTerminalRuleCall_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getConceptAssignmentAccess().getEQUAL_SIGNTerminalRuleCall_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new ConceptAssignment_NameAssignment_0(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// value=RValue
protected class ConceptAssignment_ValueAssignment_2 extends AssignmentToken  {
	
	public ConceptAssignment_ValueAssignment_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getConceptAssignmentAccess().getValueAssignment_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new RValue_OrParserRuleCall(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("value",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("value");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getRValueRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getConceptAssignmentAccess().getValueRValueParserRuleCall_2_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new ConceptAssignment_EQUAL_SIGNTerminalRuleCall_1(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}


/************ end Rule ConceptAssignment ****************/


/************ begin Rule NumericalAssignment ****************
 *
 * NumericalAssignment:
 * 
 * 	name=Concept operator=Operator value=DecimalNumber unit=UnitType;
 *
 **/

// name=Concept operator=Operator value=DecimalNumber unit=UnitType
protected class NumericalAssignment_Group extends GroupToken {
	
	public NumericalAssignment_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getNumericalAssignmentAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NumericalAssignment_UnitAssignment_3(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getNumericalAssignmentRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// name=Concept
protected class NumericalAssignment_NameAssignment_0 extends AssignmentToken  {
	
	public NumericalAssignment_NameAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getNumericalAssignmentAccess().getNameAssignment_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Concept_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("name",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("name");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getConceptRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getNumericalAssignmentAccess().getNameConceptParserRuleCall_0_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, consumed);
		}	
	}	
}

// operator=Operator
protected class NumericalAssignment_OperatorAssignment_1 extends AssignmentToken  {
	
	public NumericalAssignment_OperatorAssignment_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getNumericalAssignmentAccess().getOperatorAssignment_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NumericalAssignment_NameAssignment_0(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("operator",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("operator");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getNumericalAssignmentAccess().getOperatorOperatorParserRuleCall_1_0(), value, null)) {
			type = AssignmentType.DATATYPE_RULE_CALL;
			element = grammarAccess.getNumericalAssignmentAccess().getOperatorOperatorParserRuleCall_1_0();
			return obj;
		}
		return null;
	}

}

// value=DecimalNumber
protected class NumericalAssignment_ValueAssignment_2 extends AssignmentToken  {
	
	public NumericalAssignment_ValueAssignment_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getNumericalAssignmentAccess().getValueAssignment_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NumericalAssignment_OperatorAssignment_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("value",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("value");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getNumericalAssignmentAccess().getValueDecimalNumberParserRuleCall_2_0(), value, null)) {
			type = AssignmentType.DATATYPE_RULE_CALL;
			element = grammarAccess.getNumericalAssignmentAccess().getValueDecimalNumberParserRuleCall_2_0();
			return obj;
		}
		return null;
	}

}

// unit=UnitType
protected class NumericalAssignment_UnitAssignment_3 extends AssignmentToken  {
	
	public NumericalAssignment_UnitAssignment_3(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getNumericalAssignmentAccess().getUnitAssignment_3();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NumericalAssignment_ValueAssignment_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("unit",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("unit");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getNumericalAssignmentAccess().getUnitUnitTypeParserRuleCall_3_0(), value, null)) {
			type = AssignmentType.DATATYPE_RULE_CALL;
			element = grammarAccess.getNumericalAssignmentAccess().getUnitUnitTypeParserRuleCall_3_0();
			return obj;
		}
		return null;
	}

}


/************ end Rule NumericalAssignment ****************/


/************ begin Rule NumericalAssignmentGroup ****************
 *
 * NumericalAssignmentGroup:
 * 
 * 	OPENING_SQUARE_BRACKET ingredientConcept=Concept EQUAL_SIGN substance=RValue COMMA numericValue=NumericalAssignment
 * 
 * 	CLOSING_SQUARE_BRACKET;
 *
 **/

// OPENING_SQUARE_BRACKET ingredientConcept=Concept EQUAL_SIGN substance=RValue COMMA numericValue=NumericalAssignment
// 
// CLOSING_SQUARE_BRACKET
protected class NumericalAssignmentGroup_Group extends GroupToken {
	
	public NumericalAssignmentGroup_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getNumericalAssignmentGroupAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NumericalAssignmentGroup_CLOSING_SQUARE_BRACKETTerminalRuleCall_6(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getNumericalAssignmentGroupRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// OPENING_SQUARE_BRACKET
protected class NumericalAssignmentGroup_OPENING_SQUARE_BRACKETTerminalRuleCall_0 extends UnassignedTextToken {

	public NumericalAssignmentGroup_OPENING_SQUARE_BRACKETTerminalRuleCall_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getNumericalAssignmentGroupAccess().getOPENING_SQUARE_BRACKETTerminalRuleCall_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

}

// ingredientConcept=Concept
protected class NumericalAssignmentGroup_IngredientConceptAssignment_1 extends AssignmentToken  {
	
	public NumericalAssignmentGroup_IngredientConceptAssignment_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getNumericalAssignmentGroupAccess().getIngredientConceptAssignment_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Concept_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("ingredientConcept",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("ingredientConcept");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getConceptRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getNumericalAssignmentGroupAccess().getIngredientConceptConceptParserRuleCall_1_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new NumericalAssignmentGroup_OPENING_SQUARE_BRACKETTerminalRuleCall_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}

// EQUAL_SIGN
protected class NumericalAssignmentGroup_EQUAL_SIGNTerminalRuleCall_2 extends UnassignedTextToken {

	public NumericalAssignmentGroup_EQUAL_SIGNTerminalRuleCall_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getNumericalAssignmentGroupAccess().getEQUAL_SIGNTerminalRuleCall_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NumericalAssignmentGroup_IngredientConceptAssignment_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// substance=RValue
protected class NumericalAssignmentGroup_SubstanceAssignment_3 extends AssignmentToken  {
	
	public NumericalAssignmentGroup_SubstanceAssignment_3(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getNumericalAssignmentGroupAccess().getSubstanceAssignment_3();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new RValue_OrParserRuleCall(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("substance",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("substance");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getRValueRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getNumericalAssignmentGroupAccess().getSubstanceRValueParserRuleCall_3_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new NumericalAssignmentGroup_EQUAL_SIGNTerminalRuleCall_2(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}

// COMMA
protected class NumericalAssignmentGroup_COMMATerminalRuleCall_4 extends UnassignedTextToken {

	public NumericalAssignmentGroup_COMMATerminalRuleCall_4(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getNumericalAssignmentGroupAccess().getCOMMATerminalRuleCall_4();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NumericalAssignmentGroup_SubstanceAssignment_3(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// numericValue=NumericalAssignment
protected class NumericalAssignmentGroup_NumericValueAssignment_5 extends AssignmentToken  {
	
	public NumericalAssignmentGroup_NumericValueAssignment_5(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getNumericalAssignmentGroupAccess().getNumericValueAssignment_5();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NumericalAssignment_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("numericValue",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("numericValue");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getNumericalAssignmentRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getNumericalAssignmentGroupAccess().getNumericValueNumericalAssignmentParserRuleCall_5_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new NumericalAssignmentGroup_COMMATerminalRuleCall_4(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}

// CLOSING_SQUARE_BRACKET
protected class NumericalAssignmentGroup_CLOSING_SQUARE_BRACKETTerminalRuleCall_6 extends UnassignedTextToken {

	public NumericalAssignmentGroup_CLOSING_SQUARE_BRACKETTerminalRuleCall_6(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getNumericalAssignmentGroupAccess().getCLOSING_SQUARE_BRACKETTerminalRuleCall_6();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NumericalAssignmentGroup_NumericValueAssignment_5(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}


/************ end Rule NumericalAssignmentGroup ****************/


/************ begin Rule RValue ****************
 *
 * RValue:
 * 
 * 	Or;
 *
 **/

// Or
protected class RValue_OrParserRuleCall extends RuleCallToken {
	
	public RValue_OrParserRuleCall(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getRValueAccess().getOrParserRuleCall();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Or_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getAndAccess().getAndLeftAction_1_0().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getConceptGroupRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getNegatableSubExpressionRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getOrAccess().getOrLeftAction_1_0().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getRefSetRule().getType().getClassifier())
			return null;
		if(checkForRecursion(Or_Group.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}

/************ end Rule RValue ****************/


/************ begin Rule Or ****************
 *
 * Or returns RValue:
 * 
 * 	And ({Or.left=current} OR_TOKEN right=And)*;
 *
 **/

// And ({Or.left=current} OR_TOKEN right=And)*
protected class Or_Group extends GroupToken {
	
	public Or_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getOrAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Or_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Or_AndParserRuleCall_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getAndAccess().getAndLeftAction_1_0().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getConceptGroupRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getNegatableSubExpressionRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getOrAccess().getOrLeftAction_1_0().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getRefSetRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// And
protected class Or_AndParserRuleCall_0 extends RuleCallToken {
	
	public Or_AndParserRuleCall_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getOrAccess().getAndParserRuleCall_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new And_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(checkForRecursion(And_Group.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}

// ({Or.left=current} OR_TOKEN right=And)*
protected class Or_Group_1 extends GroupToken {
	
	public Or_Group_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getOrAccess().getGroup_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Or_RightAssignment_1_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getOrAccess().getOrLeftAction_1_0().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// {Or.left=current}
protected class Or_OrLeftAction_1_0 extends ActionToken  {

	public Or_OrLeftAction_1_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Action getGrammarElement() {
		return grammarAccess.getOrAccess().getOrLeftAction_1_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Or_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Or_AndParserRuleCall_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		Object val = eObjectConsumer.getConsumable("left", false);
		if(val == null) return null;
		if(!eObjectConsumer.isConsumedWithLastConsumtion("left")) return null;
		return createEObjectConsumer((EObject) val);
	}
}

// OR_TOKEN
protected class Or_OR_TOKENTerminalRuleCall_1_1 extends UnassignedTextToken {

	public Or_OR_TOKENTerminalRuleCall_1_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getOrAccess().getOR_TOKENTerminalRuleCall_1_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Or_OrLeftAction_1_0(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// right=And
protected class Or_RightAssignment_1_2 extends AssignmentToken  {
	
	public Or_RightAssignment_1_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getOrAccess().getRightAssignment_1_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new And_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("right",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("right");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getAndRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getOrAccess().getRightAndParserRuleCall_1_2_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new Or_OR_TOKENTerminalRuleCall_1_1(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}



/************ end Rule Or ****************/


/************ begin Rule And ****************
 *
 * And returns RValue:
 * 
 * 	TerminalRValue ({And.left=current} AND_TOKEN right=TerminalRValue)*;
 *
 **/

// TerminalRValue ({And.left=current} AND_TOKEN right=TerminalRValue)*
protected class And_Group extends GroupToken {
	
	public And_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getAndAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new And_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new And_TerminalRValueParserRuleCall_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getAndAccess().getAndLeftAction_1_0().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getConceptGroupRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getNegatableSubExpressionRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getOrAccess().getOrLeftAction_1_0().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getRefSetRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// TerminalRValue
protected class And_TerminalRValueParserRuleCall_0 extends RuleCallToken {
	
	public And_TerminalRValueParserRuleCall_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAndAccess().getTerminalRValueParserRuleCall_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new TerminalRValue_Alternatives(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(checkForRecursion(TerminalRValue_Alternatives.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}

// ({And.left=current} AND_TOKEN right=TerminalRValue)*
protected class And_Group_1 extends GroupToken {
	
	public And_Group_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getAndAccess().getGroup_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new And_RightAssignment_1_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getAndAccess().getAndLeftAction_1_0().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// {And.left=current}
protected class And_AndLeftAction_1_0 extends ActionToken  {

	public And_AndLeftAction_1_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Action getGrammarElement() {
		return grammarAccess.getAndAccess().getAndLeftAction_1_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new And_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new And_TerminalRValueParserRuleCall_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		Object val = eObjectConsumer.getConsumable("left", false);
		if(val == null) return null;
		if(!eObjectConsumer.isConsumedWithLastConsumtion("left")) return null;
		return createEObjectConsumer((EObject) val);
	}
}

// AND_TOKEN
protected class And_AND_TOKENTerminalRuleCall_1_1 extends UnassignedTextToken {

	public And_AND_TOKENTerminalRuleCall_1_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAndAccess().getAND_TOKENTerminalRuleCall_1_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new And_AndLeftAction_1_0(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// right=TerminalRValue
protected class And_RightAssignment_1_2 extends AssignmentToken  {
	
	public And_RightAssignment_1_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getAndAccess().getRightAssignment_1_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new TerminalRValue_Alternatives(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("right",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("right");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getTerminalRValueRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getAndAccess().getRightTerminalRValueParserRuleCall_1_2_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new And_AND_TOKENTerminalRuleCall_1_1(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}



/************ end Rule And ****************/


/************ begin Rule NegatableSubExpression ****************
 *
 * NegatableSubExpression:
 * 
 * 	negated?=NOT_TOKEN? OPENING_ROUND_BRACKET expression=Expression CLOSING_ROUND_BRACKET;
 *
 **/

// negated?=NOT_TOKEN? OPENING_ROUND_BRACKET expression=Expression CLOSING_ROUND_BRACKET
protected class NegatableSubExpression_Group extends GroupToken {
	
	public NegatableSubExpression_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getNegatableSubExpressionAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NegatableSubExpression_CLOSING_ROUND_BRACKETTerminalRuleCall_3(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getNegatableSubExpressionRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// negated?=NOT_TOKEN?
protected class NegatableSubExpression_NegatedAssignment_0 extends AssignmentToken  {
	
	public NegatableSubExpression_NegatedAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getNegatableSubExpressionAccess().getNegatedAssignment_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("negated",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("negated");
		if(valueSerializer.isValid(obj.getEObject(), grammarAccess.getNegatableSubExpressionAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0(), value, null)) {
			type = AssignmentType.TERMINAL_RULE_CALL;
			element = grammarAccess.getNegatableSubExpressionAccess().getNegatedNOT_TOKENTerminalRuleCall_0_0();
			return obj;
		}
		return null;
	}

}

// OPENING_ROUND_BRACKET
protected class NegatableSubExpression_OPENING_ROUND_BRACKETTerminalRuleCall_1 extends UnassignedTextToken {

	public NegatableSubExpression_OPENING_ROUND_BRACKETTerminalRuleCall_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getNegatableSubExpressionAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NegatableSubExpression_NegatedAssignment_0(lastRuleCallOrigin, this, 0, inst);
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index - 1, inst);
		}	
	}

}

// expression=Expression
protected class NegatableSubExpression_ExpressionAssignment_2 extends AssignmentToken  {
	
	public NegatableSubExpression_ExpressionAssignment_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getNegatableSubExpressionAccess().getExpressionAssignment_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Expression_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("expression",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("expression");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getExpressionRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getNegatableSubExpressionAccess().getExpressionExpressionParserRuleCall_2_0(); 
				consumed = obj;
				return param;
			}
		}
		return null;
	}

    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		if(value == inst.getEObject() && !inst.isConsumed()) return null;
		switch(index) {
			case 0: return new NegatableSubExpression_OPENING_ROUND_BRACKETTerminalRuleCall_1(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}

// CLOSING_ROUND_BRACKET
protected class NegatableSubExpression_CLOSING_ROUND_BRACKETTerminalRuleCall_3 extends UnassignedTextToken {

	public NegatableSubExpression_CLOSING_ROUND_BRACKETTerminalRuleCall_3(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getNegatableSubExpressionAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_3();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NegatableSubExpression_ExpressionAssignment_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}


/************ end Rule NegatableSubExpression ****************/


/************ begin Rule TerminalRValue ****************
 *
 * TerminalRValue returns RValue:
 * 
 * 	OPENING_ROUND_BRACKET RValue CLOSING_ROUND_BRACKET | NegatableSubExpression | LValue;
 *
 **/

// OPENING_ROUND_BRACKET RValue CLOSING_ROUND_BRACKET | NegatableSubExpression | LValue
protected class TerminalRValue_Alternatives extends AlternativesToken {

	public TerminalRValue_Alternatives(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Alternatives getGrammarElement() {
		return grammarAccess.getTerminalRValueAccess().getAlternatives();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new TerminalRValue_Group_0(lastRuleCallOrigin, this, 0, inst);
			case 1: return new TerminalRValue_NegatableSubExpressionParserRuleCall_1(lastRuleCallOrigin, this, 1, inst);
			case 2: return new TerminalRValue_LValueParserRuleCall_2(lastRuleCallOrigin, this, 2, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getAndAccess().getAndLeftAction_1_0().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getConceptGroupRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getNegatableSubExpressionRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getOrAccess().getOrLeftAction_1_0().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getRefSetRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// OPENING_ROUND_BRACKET RValue CLOSING_ROUND_BRACKET
protected class TerminalRValue_Group_0 extends GroupToken {
	
	public TerminalRValue_Group_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getTerminalRValueAccess().getGroup_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new TerminalRValue_CLOSING_ROUND_BRACKETTerminalRuleCall_0_2(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// OPENING_ROUND_BRACKET
protected class TerminalRValue_OPENING_ROUND_BRACKETTerminalRuleCall_0_0 extends UnassignedTextToken {

	public TerminalRValue_OPENING_ROUND_BRACKETTerminalRuleCall_0_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getTerminalRValueAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_0_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

}

// RValue
protected class TerminalRValue_RValueParserRuleCall_0_1 extends RuleCallToken {
	
	public TerminalRValue_RValueParserRuleCall_0_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getTerminalRValueAccess().getRValueParserRuleCall_0_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new RValue_OrParserRuleCall(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(checkForRecursion(RValue_OrParserRuleCall.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new TerminalRValue_OPENING_ROUND_BRACKETTerminalRuleCall_0_0(lastRuleCallOrigin, next, actIndex, inst);
			default: return null;
		}	
	}	
}

// CLOSING_ROUND_BRACKET
protected class TerminalRValue_CLOSING_ROUND_BRACKETTerminalRuleCall_0_2 extends UnassignedTextToken {

	public TerminalRValue_CLOSING_ROUND_BRACKETTerminalRuleCall_0_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getTerminalRValueAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_0_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new TerminalRValue_RValueParserRuleCall_0_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}


// NegatableSubExpression
protected class TerminalRValue_NegatableSubExpressionParserRuleCall_1 extends RuleCallToken {
	
	public TerminalRValue_NegatableSubExpressionParserRuleCall_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getTerminalRValueAccess().getNegatableSubExpressionParserRuleCall_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new NegatableSubExpression_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getNegatableSubExpressionRule().getType().getClassifier())
			return null;
		if(checkForRecursion(NegatableSubExpression_Group.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}

// LValue
protected class TerminalRValue_LValueParserRuleCall_2 extends RuleCallToken {
	
	public TerminalRValue_LValueParserRuleCall_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getTerminalRValueAccess().getLValueParserRuleCall_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new LValue_Alternatives(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getConceptGroupRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getRefSetRule().getType().getClassifier())
			return null;
		if(checkForRecursion(LValue_Alternatives.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}


/************ end Rule TerminalRValue ****************/








}