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

import com.b2international.snowowl.dsl.services.SCGGrammarAccess;

import com.google.inject.Inject;

@SuppressWarnings("all")
public class SCGParsetreeConstructor extends org.eclipse.xtext.parsetree.reconstr.impl.AbstractParseTreeConstructor {
		
	@Inject
	private SCGGrammarAccess grammarAccess;
	
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
			case 1: return new Concept_Group(this, this, 1, inst);
			case 2: return new Group_Group(this, this, 2, inst);
			case 3: return new Attribute_Group(this, this, 3, inst);
			case 4: return new AttributeValue_Alternatives(this, this, 4, inst);
			default: return null;
		}	
	}	
}
	

/************ begin Rule Expression ****************
 *
 * Expression hidden(WS, SL_COMMENT, ML_COMMENT):
 * 
 * 	concepts+=Concept (PLUS_SIGN concepts+=Concept)* (COLON (attributes+=Attribute (COMMA attributes+=Attribute)*)?
 * 
 * 	groups+=Group*)?;
 *
 **/

// concepts+=Concept (PLUS_SIGN concepts+=Concept)* (COLON (attributes+=Attribute (COMMA attributes+=Attribute)*)?
// 
// groups+=Group*)?
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
			case 0: return new Expression_Group_2(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Expression_Group_1(lastRuleCallOrigin, this, 1, inst);
			case 2: return new Expression_ConceptsAssignment_0(lastRuleCallOrigin, this, 2, inst);
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

// concepts+=Concept
protected class Expression_ConceptsAssignment_0 extends AssignmentToken  {
	
	public Expression_ConceptsAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getExpressionAccess().getConceptsAssignment_0();
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
		if((value = eObjectConsumer.getConsumable("concepts",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("concepts");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getConceptRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getExpressionAccess().getConceptsConceptParserRuleCall_0_0(); 
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

// (PLUS_SIGN concepts+=Concept)*
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
			case 0: return new Expression_ConceptsAssignment_1_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// PLUS_SIGN
protected class Expression_PLUS_SIGNTerminalRuleCall_1_0 extends UnassignedTextToken {

	public Expression_PLUS_SIGNTerminalRuleCall_1_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getExpressionAccess().getPLUS_SIGNTerminalRuleCall_1_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Expression_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Expression_ConceptsAssignment_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}

// concepts+=Concept
protected class Expression_ConceptsAssignment_1_1 extends AssignmentToken  {
	
	public Expression_ConceptsAssignment_1_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getExpressionAccess().getConceptsAssignment_1_1();
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
		if((value = eObjectConsumer.getConsumable("concepts",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("concepts");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getConceptRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getExpressionAccess().getConceptsConceptParserRuleCall_1_1_0(); 
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
			case 0: return new Expression_PLUS_SIGNTerminalRuleCall_1_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}


// (COLON (attributes+=Attribute (COMMA attributes+=Attribute)*)? groups+=Group*)?
protected class Expression_Group_2 extends GroupToken {
	
	public Expression_Group_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getExpressionAccess().getGroup_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Expression_GroupsAssignment_2_2(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Expression_Group_2_1(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}

// COLON
protected class Expression_COLONTerminalRuleCall_2_0 extends UnassignedTextToken {

	public Expression_COLONTerminalRuleCall_2_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getExpressionAccess().getCOLONTerminalRuleCall_2_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Expression_Group_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Expression_ConceptsAssignment_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}

// (attributes+=Attribute (COMMA attributes+=Attribute)*)?
protected class Expression_Group_2_1 extends GroupToken {
	
	public Expression_Group_2_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getExpressionAccess().getGroup_2_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Expression_Group_2_1_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Expression_AttributesAssignment_2_1_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}

// attributes+=Attribute
protected class Expression_AttributesAssignment_2_1_0 extends AssignmentToken  {
	
	public Expression_AttributesAssignment_2_1_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getExpressionAccess().getAttributesAssignment_2_1_0();
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
				element = grammarAccess.getExpressionAccess().getAttributesAttributeParserRuleCall_2_1_0_0(); 
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
			case 0: return new Expression_COLONTerminalRuleCall_2_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}

// (COMMA attributes+=Attribute)*
protected class Expression_Group_2_1_1 extends GroupToken {
	
	public Expression_Group_2_1_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getExpressionAccess().getGroup_2_1_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Expression_AttributesAssignment_2_1_1_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// COMMA
protected class Expression_COMMATerminalRuleCall_2_1_1_0 extends UnassignedTextToken {

	public Expression_COMMATerminalRuleCall_2_1_1_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getExpressionAccess().getCOMMATerminalRuleCall_2_1_1_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Expression_Group_2_1_1(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Expression_AttributesAssignment_2_1_0(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}

// attributes+=Attribute
protected class Expression_AttributesAssignment_2_1_1_1 extends AssignmentToken  {
	
	public Expression_AttributesAssignment_2_1_1_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getExpressionAccess().getAttributesAssignment_2_1_1_1();
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
				element = grammarAccess.getExpressionAccess().getAttributesAttributeParserRuleCall_2_1_1_1_0(); 
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
			case 0: return new Expression_COMMATerminalRuleCall_2_1_1_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}



// groups+=Group*
protected class Expression_GroupsAssignment_2_2 extends AssignmentToken  {
	
	public Expression_GroupsAssignment_2_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getExpressionAccess().getGroupsAssignment_2_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Group_Group(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("groups",false)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("groups");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getGroupRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getExpressionAccess().getGroupsGroupParserRuleCall_2_2_0(); 
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
			case 0: return new Expression_GroupsAssignment_2_2(lastRuleCallOrigin, next, actIndex, consumed);
			case 1: return new Expression_Group_2_1(lastRuleCallOrigin, next, actIndex, consumed);
			case 2: return new Expression_COLONTerminalRuleCall_2_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}



/************ end Rule Expression ****************/


/************ begin Rule Concept ****************
 *
 * Concept:
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


/************ begin Rule Group ****************
 *
 * Group:
 * 
 * 	OPENING_CURLY_BRACKET attributes+=Attribute (COMMA attributes+=Attribute)* CLOSING_CURLY_BRACKET;
 *
 **/

// OPENING_CURLY_BRACKET attributes+=Attribute (COMMA attributes+=Attribute)* CLOSING_CURLY_BRACKET
protected class Group_Group extends GroupToken {
	
	public Group_Group(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getGroupAccess().getGroup();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Group_CLOSING_CURLY_BRACKETTerminalRuleCall_3(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getGroupRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// OPENING_CURLY_BRACKET
protected class Group_OPENING_CURLY_BRACKETTerminalRuleCall_0 extends UnassignedTextToken {

	public Group_OPENING_CURLY_BRACKETTerminalRuleCall_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getGroupAccess().getOPENING_CURLY_BRACKETTerminalRuleCall_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

}

// attributes+=Attribute
protected class Group_AttributesAssignment_1 extends AssignmentToken  {
	
	public Group_AttributesAssignment_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getGroupAccess().getAttributesAssignment_1();
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
				element = grammarAccess.getGroupAccess().getAttributesAttributeParserRuleCall_1_0(); 
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
			case 0: return new Group_OPENING_CURLY_BRACKETTerminalRuleCall_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}

// (COMMA attributes+=Attribute)*
protected class Group_Group_2 extends GroupToken {
	
	public Group_Group_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getGroupAccess().getGroup_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Group_AttributesAssignment_2_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// COMMA
protected class Group_COMMATerminalRuleCall_2_0 extends UnassignedTextToken {

	public Group_COMMATerminalRuleCall_2_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getGroupAccess().getCOMMATerminalRuleCall_2_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Group_Group_2(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Group_AttributesAssignment_1(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}

// attributes+=Attribute
protected class Group_AttributesAssignment_2_1 extends AssignmentToken  {
	
	public Group_AttributesAssignment_2_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getGroupAccess().getAttributesAssignment_2_1();
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
				element = grammarAccess.getGroupAccess().getAttributesAttributeParserRuleCall_2_1_0(); 
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
			case 0: return new Group_COMMATerminalRuleCall_2_0(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}


// CLOSING_CURLY_BRACKET
protected class Group_CLOSING_CURLY_BRACKETTerminalRuleCall_3 extends UnassignedTextToken {

	public Group_CLOSING_CURLY_BRACKETTerminalRuleCall_3(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getGroupAccess().getCLOSING_CURLY_BRACKETTerminalRuleCall_3();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Group_Group_2(lastRuleCallOrigin, this, 0, inst);
			case 1: return new Group_AttributesAssignment_1(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

}


/************ end Rule Group ****************/


/************ begin Rule Attribute ****************
 *
 * Attribute:
 * 
 * 	name=Concept EQUAL_SIGN value=AttributeValue;
 *
 **/

// name=Concept EQUAL_SIGN value=AttributeValue
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
			case 0: return new Attribute_ValueAssignment_2(lastRuleCallOrigin, this, 0, inst);
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

// name=Concept
protected class Attribute_NameAssignment_0 extends AssignmentToken  {
	
	public Attribute_NameAssignment_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getAttributeAccess().getNameAssignment_0();
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
				element = grammarAccess.getAttributeAccess().getNameConceptParserRuleCall_0_0(); 
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
protected class Attribute_EQUAL_SIGNTerminalRuleCall_1 extends UnassignedTextToken {

	public Attribute_EQUAL_SIGNTerminalRuleCall_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeAccess().getEQUAL_SIGNTerminalRuleCall_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new Attribute_NameAssignment_0(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}

// value=AttributeValue
protected class Attribute_ValueAssignment_2 extends AssignmentToken  {
	
	public Attribute_ValueAssignment_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Assignment getGrammarElement() {
		return grammarAccess.getAttributeAccess().getValueAssignment_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeValue_Alternatives(this, this, 0, inst);
			default: return null;
		}	
	}

    @Override	
	public IEObjectConsumer tryConsume() {
		if((value = eObjectConsumer.getConsumable("value",true)) == null) return null;
		IEObjectConsumer obj = eObjectConsumer.cloneAndConsume("value");
		if(value instanceof EObject) { // org::eclipse::xtext::impl::RuleCallImpl
			IEObjectConsumer param = createEObjectConsumer((EObject)value);
			if(param.isInstanceOf(grammarAccess.getAttributeValueRule().getType().getClassifier())) {
				type = AssignmentType.PARSER_RULE_CALL;
				element = grammarAccess.getAttributeAccess().getValueAttributeValueParserRuleCall_2_0(); 
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
			case 0: return new Attribute_EQUAL_SIGNTerminalRuleCall_1(lastRuleCallOrigin, next, actIndex, consumed);
			default: return null;
		}	
	}	
}


/************ end Rule Attribute ****************/


/************ begin Rule AttributeValue ****************
 *
 * AttributeValue:
 * 
 * 	Concept | OPENING_ROUND_BRACKET Expression CLOSING_ROUND_BRACKET;
 *
 **/

// Concept | OPENING_ROUND_BRACKET Expression CLOSING_ROUND_BRACKET
protected class AttributeValue_Alternatives extends AlternativesToken {

	public AttributeValue_Alternatives(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Alternatives getGrammarElement() {
		return grammarAccess.getAttributeValueAccess().getAlternatives();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeValue_ConceptParserRuleCall_0(lastRuleCallOrigin, this, 0, inst);
			case 1: return new AttributeValue_Group_1(lastRuleCallOrigin, this, 1, inst);
			default: return null;
		}	
	}

    @Override
	public IEObjectConsumer tryConsume() {
		if(getEObject().eClass() != grammarAccess.getConceptRule().getType().getClassifier() && 
		   getEObject().eClass() != grammarAccess.getExpressionRule().getType().getClassifier())
			return null;
		return eObjectConsumer;
	}

}

// Concept
protected class AttributeValue_ConceptParserRuleCall_0 extends RuleCallToken {
	
	public AttributeValue_ConceptParserRuleCall_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeValueAccess().getConceptParserRuleCall_0();
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
		if(getEObject().eClass() != grammarAccess.getConceptRule().getType().getClassifier())
			return null;
		if(checkForRecursion(Concept_Group.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(next, actIndex , index, inst);
		}	
	}	
}

// OPENING_ROUND_BRACKET Expression CLOSING_ROUND_BRACKET
protected class AttributeValue_Group_1 extends GroupToken {
	
	public AttributeValue_Group_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public Group getGrammarElement() {
		return grammarAccess.getAttributeValueAccess().getGroup_1();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeValue_CLOSING_ROUND_BRACKETTerminalRuleCall_1_2(lastRuleCallOrigin, this, 0, inst);
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

// OPENING_ROUND_BRACKET
protected class AttributeValue_OPENING_ROUND_BRACKETTerminalRuleCall_1_0 extends UnassignedTextToken {

	public AttributeValue_OPENING_ROUND_BRACKETTerminalRuleCall_1_0(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeValueAccess().getOPENING_ROUND_BRACKETTerminalRuleCall_1_0();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			default: return lastRuleCallOrigin.createFollowerAfterReturn(this, index, index, inst);
		}	
	}

}

// Expression
protected class AttributeValue_ExpressionParserRuleCall_1_1 extends RuleCallToken {
	
	public AttributeValue_ExpressionParserRuleCall_1_1(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeValueAccess().getExpressionParserRuleCall_1_1();
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
		if(checkForRecursion(Expression_Group.class, eObjectConsumer)) return null;
		return eObjectConsumer;
	}
	
    @Override
	public AbstractToken createFollowerAfterReturn(AbstractToken next,	int actIndex, int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeValue_OPENING_ROUND_BRACKETTerminalRuleCall_1_0(lastRuleCallOrigin, next, actIndex, inst);
			default: return null;
		}	
	}	
}

// CLOSING_ROUND_BRACKET
protected class AttributeValue_CLOSING_ROUND_BRACKETTerminalRuleCall_1_2 extends UnassignedTextToken {

	public AttributeValue_CLOSING_ROUND_BRACKETTerminalRuleCall_1_2(AbstractToken lastRuleCallOrigin, AbstractToken next, int transitionIndex, IEObjectConsumer eObjectConsumer) {
		super(lastRuleCallOrigin, next, transitionIndex, eObjectConsumer);
	}
	
	@Override
	public RuleCall getGrammarElement() {
		return grammarAccess.getAttributeValueAccess().getCLOSING_ROUND_BRACKETTerminalRuleCall_1_2();
	}

    @Override
	public AbstractToken createFollower(int index, IEObjectConsumer inst) {
		switch(index) {
			case 0: return new AttributeValue_ExpressionParserRuleCall_1_1(lastRuleCallOrigin, this, 0, inst);
			default: return null;
		}	
	}

}



/************ end Rule AttributeValue ****************/




}