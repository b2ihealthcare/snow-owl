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

import com.b2international.snowowl.snomed.scg.scg.Attribute;
import com.b2international.snowowl.snomed.scg.scg.AttributeGroup;
import com.b2international.snowowl.snomed.scg.scg.ConceptReference;
import com.b2international.snowowl.snomed.scg.scg.DecimalValue;
import com.b2international.snowowl.snomed.scg.scg.Expression;
import com.b2international.snowowl.snomed.scg.scg.IntegerValue;
import com.b2international.snowowl.snomed.scg.scg.Refinement;
import com.b2international.snowowl.snomed.scg.scg.ScgPackage;
import com.b2international.snowowl.snomed.scg.scg.StringValue;
import com.b2international.snowowl.snomed.scg.scg.SubExpression;
import com.b2international.snowowl.snomed.scg.services.ScgGrammarAccess;
import com.google.inject.Inject;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.Action;
import org.eclipse.xtext.Parameter;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.serializer.ISerializationContext;
import org.eclipse.xtext.serializer.acceptor.SequenceFeeder;
import org.eclipse.xtext.serializer.sequencer.AbstractDelegatingSemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;

@SuppressWarnings("all")
public class ScgSemanticSequencer extends AbstractDelegatingSemanticSequencer {

	@Inject
	private ScgGrammarAccess grammarAccess;
	
	@Override
	public void sequence(ISerializationContext context, EObject semanticObject) {
		EPackage epackage = semanticObject.eClass().getEPackage();
		ParserRule rule = context.getParserRule();
		Action action = context.getAssignedAction();
		Set<Parameter> parameters = context.getEnabledBooleanParameters();
		if (epackage == ScgPackage.eINSTANCE)
			switch (semanticObject.eClass().getClassifierID()) {
			case ScgPackage.ATTRIBUTE:
				sequence_Attribute(context, (Attribute) semanticObject); 
				return; 
			case ScgPackage.ATTRIBUTE_GROUP:
				sequence_AttributeGroup(context, (AttributeGroup) semanticObject); 
				return; 
			case ScgPackage.CONCEPT_REFERENCE:
				sequence_ConceptReference(context, (ConceptReference) semanticObject); 
				return; 
			case ScgPackage.DECIMAL_VALUE:
				sequence_DecimalValue(context, (DecimalValue) semanticObject); 
				return; 
			case ScgPackage.EXPRESSION:
				sequence_Expression(context, (Expression) semanticObject); 
				return; 
			case ScgPackage.INTEGER_VALUE:
				sequence_IntegerValue(context, (IntegerValue) semanticObject); 
				return; 
			case ScgPackage.REFINEMENT:
				sequence_Refinement(context, (Refinement) semanticObject); 
				return; 
			case ScgPackage.STRING_VALUE:
				sequence_StringValue(context, (StringValue) semanticObject); 
				return; 
			case ScgPackage.SUB_EXPRESSION:
				sequence_SubExpression(context, (SubExpression) semanticObject); 
				return; 
			}
		if (errorAcceptor != null)
			errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Contexts:
	 *     AttributeGroup returns AttributeGroup
	 *
	 * Constraint:
	 *     (attributes+=Attribute attributes+=Attribute*)
	 */
	protected void sequence_AttributeGroup(ISerializationContext context, AttributeGroup semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Attribute returns Attribute
	 *
	 * Constraint:
	 *     (name=ConceptReference value=AttributeValue)
	 */
	protected void sequence_Attribute(ISerializationContext context, Attribute semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, ScgPackage.Literals.ATTRIBUTE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, ScgPackage.Literals.ATTRIBUTE__NAME));
			if (transientValues.isValueTransient(semanticObject, ScgPackage.Literals.ATTRIBUTE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, ScgPackage.Literals.ATTRIBUTE__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getAttributeAccess().getNameConceptReferenceParserRuleCall_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getAttributeAccess().getValueAttributeValueParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     AttributeValue returns ConceptReference
	 *     ConceptReference returns ConceptReference
	 *
	 * Constraint:
	 *     (id=SnomedIdentifier term=TERM_STRING?)
	 */
	protected void sequence_ConceptReference(ISerializationContext context, ConceptReference semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     AttributeValue returns DecimalValue
	 *     DecimalValue returns DecimalValue
	 *
	 * Constraint:
	 *     value=Decimal
	 */
	protected void sequence_DecimalValue(ISerializationContext context, DecimalValue semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, ScgPackage.Literals.DECIMAL_VALUE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, ScgPackage.Literals.DECIMAL_VALUE__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDecimalValueAccess().getValueDecimalParserRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Expression returns Expression
	 *
	 * Constraint:
	 *     (primitive?=SUBTYPE_OF? expression=SubExpression)?
	 */
	protected void sequence_Expression(ISerializationContext context, Expression semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     AttributeValue returns IntegerValue
	 *     IntegerValue returns IntegerValue
	 *
	 * Constraint:
	 *     value=Integer
	 */
	protected void sequence_IntegerValue(ISerializationContext context, IntegerValue semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, ScgPackage.Literals.INTEGER_VALUE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, ScgPackage.Literals.INTEGER_VALUE__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getIntegerValueAccess().getValueIntegerParserRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Refinement returns Refinement
	 *
	 * Constraint:
	 *     (((attributes+=Attribute attributes+=Attribute*) | groups+=AttributeGroup) groups+=AttributeGroup*)
	 */
	protected void sequence_Refinement(ISerializationContext context, Refinement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     AttributeValue returns StringValue
	 *     StringValue returns StringValue
	 *
	 * Constraint:
	 *     value=STRING
	 */
	protected void sequence_StringValue(ISerializationContext context, StringValue semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, ScgPackage.Literals.STRING_VALUE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, ScgPackage.Literals.STRING_VALUE__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getStringValueAccess().getValueSTRINGTerminalRuleCall_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     SubExpression returns SubExpression
	 *     AttributeValue returns SubExpression
	 *
	 * Constraint:
	 *     (focusConcepts+=ConceptReference focusConcepts+=ConceptReference* refinement=Refinement?)
	 */
	protected void sequence_SubExpression(ISerializationContext context, SubExpression semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
}
