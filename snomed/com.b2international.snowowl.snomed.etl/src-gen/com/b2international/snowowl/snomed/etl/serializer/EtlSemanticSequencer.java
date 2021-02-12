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
package com.b2international.snowowl.snomed.etl.serializer;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.Action;
import org.eclipse.xtext.Parameter;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.serializer.ISerializationContext;
import org.eclipse.xtext.serializer.acceptor.SequenceFeeder;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;

import com.b2international.snomed.ecl.ecl.*;
import com.b2international.snomed.ecl.serializer.EclSemanticSequencer;
import com.b2international.snowowl.snomed.etl.etl.*;
import com.b2international.snowowl.snomed.etl.services.EtlGrammarAccess;
import com.google.inject.Inject;

@SuppressWarnings("all")
public class EtlSemanticSequencer extends EclSemanticSequencer {

	@Inject
	private EtlGrammarAccess grammarAccess;
	
	@Override
	public void sequence(ISerializationContext context, EObject semanticObject) {
		EPackage epackage = semanticObject.eClass().getEPackage();
		ParserRule rule = context.getParserRule();
		Action action = context.getAssignedAction();
		Set<Parameter> parameters = context.getEnabledBooleanParameters();
		if (epackage == EclPackage.eINSTANCE)
			switch (semanticObject.eClass().getClassifierID()) {
			case EclPackage.ANCESTOR_OF:
				sequence_AncestorOf(context, (AncestorOf) semanticObject); 
				return; 
			case EclPackage.ANCESTOR_OR_SELF_OF:
				sequence_AncestorOrSelfOf(context, (AncestorOrSelfOf) semanticObject); 
				return; 
			case EclPackage.AND_EXPRESSION_CONSTRAINT:
				sequence_AndExpressionConstraint(context, (AndExpressionConstraint) semanticObject); 
				return; 
			case EclPackage.AND_REFINEMENT:
				if (rule == grammarAccess.getEclAttributeSetRule()
						|| rule == grammarAccess.getOrAttributeSetRule()
						|| action == grammarAccess.getOrAttributeSetAccess().getOrRefinementLeftAction_1_0()
						|| rule == grammarAccess.getAndAttributeSetRule()
						|| action == grammarAccess.getAndAttributeSetAccess().getAndRefinementLeftAction_1_0()) {
					sequence_AndAttributeSet(context, (AndRefinement) semanticObject); 
					return; 
				}
				else if (rule == grammarAccess.getEclRefinementRule()
						|| rule == grammarAccess.getOrRefinementRule()
						|| action == grammarAccess.getOrRefinementAccess().getOrRefinementLeftAction_1_0_0()
						|| rule == grammarAccess.getAndRefinementRule()
						|| action == grammarAccess.getAndRefinementAccess().getAndRefinementLeftAction_1_0_0()) {
					sequence_AndRefinement(context, (AndRefinement) semanticObject); 
					return; 
				}
				else break;
			case EclPackage.ANY:
				sequence_Any(context, (Any) semanticObject); 
				return; 
			case EclPackage.ATTRIBUTE_CONSTRAINT:
				sequence_AttributeConstraint(context, (AttributeConstraint) semanticObject); 
				return; 
			case EclPackage.ATTRIBUTE_VALUE_EQUALS:
				sequence_AttributeValueEquals(context, (AttributeValueEquals) semanticObject); 
				return; 
			case EclPackage.ATTRIBUTE_VALUE_NOT_EQUALS:
				sequence_AttributeValueNotEquals(context, (AttributeValueNotEquals) semanticObject); 
				return; 
			case EclPackage.BOOLEAN_VALUE_EQUALS:
				sequence_BooleanValueEquals(context, (BooleanValueEquals) semanticObject); 
				return; 
			case EclPackage.BOOLEAN_VALUE_NOT_EQUALS:
				sequence_BooleanValueNotEquals(context, (BooleanValueNotEquals) semanticObject); 
				return; 
			case EclPackage.CARDINALITY:
				sequence_Cardinality(context, (Cardinality) semanticObject); 
				return; 
			case EclPackage.CHILD_OF:
				sequence_ChildOf(context, (ChildOf) semanticObject); 
				return; 
			case EclPackage.DECIMAL_VALUE_EQUALS:
				sequence_DecimalValueEquals(context, (DecimalValueEquals) semanticObject); 
				return; 
			case EclPackage.DECIMAL_VALUE_GREATER_THAN:
				sequence_DecimalValueGreaterThan(context, (DecimalValueGreaterThan) semanticObject); 
				return; 
			case EclPackage.DECIMAL_VALUE_GREATER_THAN_EQUALS:
				sequence_DecimalValueGreaterThanEquals(context, (DecimalValueGreaterThanEquals) semanticObject); 
				return; 
			case EclPackage.DECIMAL_VALUE_LESS_THAN:
				sequence_DecimalValueLessThan(context, (DecimalValueLessThan) semanticObject); 
				return; 
			case EclPackage.DECIMAL_VALUE_LESS_THAN_EQUALS:
				sequence_DecimalValueLessThanEquals(context, (DecimalValueLessThanEquals) semanticObject); 
				return; 
			case EclPackage.DECIMAL_VALUE_NOT_EQUALS:
				sequence_DecimalValueNotEquals(context, (DecimalValueNotEquals) semanticObject); 
				return; 
			case EclPackage.DESCENDANT_OF:
				sequence_DescendantOf(context, (DescendantOf) semanticObject); 
				return; 
			case EclPackage.DESCENDANT_OR_SELF_OF:
				sequence_DescendantOrSelfOf(context, (DescendantOrSelfOf) semanticObject); 
				return; 
			case EclPackage.DOTTED_EXPRESSION_CONSTRAINT:
				sequence_DottedExpressionConstraint(context, (DottedExpressionConstraint) semanticObject); 
				return; 
			case EclPackage.ECL_ATTRIBUTE_GROUP:
				sequence_EclAttributeGroup(context, (EclAttributeGroup) semanticObject); 
				return; 
			case EclPackage.ECL_CONCEPT_REFERENCE:
				sequence_EclConceptReference(context, (EclConceptReference) semanticObject); 
				return; 
			case EclPackage.EXCLUSION_EXPRESSION_CONSTRAINT:
				sequence_ExclusionExpressionConstraint(context, (ExclusionExpressionConstraint) semanticObject); 
				return; 
			case EclPackage.INTEGER_VALUE_EQUALS:
				sequence_IntegerValueEquals(context, (IntegerValueEquals) semanticObject); 
				return; 
			case EclPackage.INTEGER_VALUE_GREATER_THAN:
				sequence_IntegerValueGreaterThan(context, (IntegerValueGreaterThan) semanticObject); 
				return; 
			case EclPackage.INTEGER_VALUE_GREATER_THAN_EQUALS:
				sequence_IntegerValueGreaterThanEquals(context, (IntegerValueGreaterThanEquals) semanticObject); 
				return; 
			case EclPackage.INTEGER_VALUE_LESS_THAN:
				sequence_IntegerValueLessThan(context, (IntegerValueLessThan) semanticObject); 
				return; 
			case EclPackage.INTEGER_VALUE_LESS_THAN_EQUALS:
				sequence_IntegerValueLessThanEquals(context, (IntegerValueLessThanEquals) semanticObject); 
				return; 
			case EclPackage.INTEGER_VALUE_NOT_EQUALS:
				sequence_IntegerValueNotEquals(context, (IntegerValueNotEquals) semanticObject); 
				return; 
			case EclPackage.MEMBER_OF:
				sequence_MemberOf(context, (MemberOf) semanticObject); 
				return; 
			case EclPackage.NESTED_EXPRESSION:
				sequence_NestedExpression(context, (NestedExpression) semanticObject); 
				return; 
			case EclPackage.NESTED_REFINEMENT:
				if (rule == grammarAccess.getEclAttributeSetRule()
						|| rule == grammarAccess.getOrAttributeSetRule()
						|| action == grammarAccess.getOrAttributeSetAccess().getOrRefinementLeftAction_1_0()
						|| rule == grammarAccess.getAndAttributeSetRule()
						|| action == grammarAccess.getAndAttributeSetAccess().getAndRefinementLeftAction_1_0()
						|| rule == grammarAccess.getSubAttributeSetRule()
						|| rule == grammarAccess.getNestedAttributeSetRule()) {
					sequence_NestedAttributeSet(context, (NestedRefinement) semanticObject); 
					return; 
				}
				else if (rule == grammarAccess.getEclRefinementRule()
						|| rule == grammarAccess.getOrRefinementRule()
						|| action == grammarAccess.getOrRefinementAccess().getOrRefinementLeftAction_1_0_0()
						|| rule == grammarAccess.getAndRefinementRule()
						|| action == grammarAccess.getAndRefinementAccess().getAndRefinementLeftAction_1_0_0()
						|| rule == grammarAccess.getSubRefinementRule()
						|| rule == grammarAccess.getNestedRefinementRule()) {
					sequence_NestedRefinement(context, (NestedRefinement) semanticObject); 
					return; 
				}
				else break;
			case EclPackage.OR_EXPRESSION_CONSTRAINT:
				sequence_OrExpressionConstraint(context, (OrExpressionConstraint) semanticObject); 
				return; 
			case EclPackage.OR_REFINEMENT:
				if (rule == grammarAccess.getEclAttributeSetRule()
						|| rule == grammarAccess.getOrAttributeSetRule()
						|| action == grammarAccess.getOrAttributeSetAccess().getOrRefinementLeftAction_1_0()) {
					sequence_OrAttributeSet(context, (OrRefinement) semanticObject); 
					return; 
				}
				else if (rule == grammarAccess.getEclRefinementRule()
						|| rule == grammarAccess.getOrRefinementRule()
						|| action == grammarAccess.getOrRefinementAccess().getOrRefinementLeftAction_1_0_0()) {
					sequence_OrRefinement(context, (OrRefinement) semanticObject); 
					return; 
				}
				else break;
			case EclPackage.PARENT_OF:
				sequence_ParentOf(context, (ParentOf) semanticObject); 
				return; 
			case EclPackage.REFINED_EXPRESSION_CONSTRAINT:
				sequence_RefinedExpressionConstraint(context, (RefinedExpressionConstraint) semanticObject); 
				return; 
			case EclPackage.SCRIPT:
				sequence_Script(context, (Script) semanticObject); 
				return; 
			case EclPackage.STRING_VALUE_EQUALS:
				sequence_StringValueEquals(context, (StringValueEquals) semanticObject); 
				return; 
			case EclPackage.STRING_VALUE_NOT_EQUALS:
				sequence_StringValueNotEquals(context, (StringValueNotEquals) semanticObject); 
				return; 
			}
		else if (epackage == EtlPackage.eINSTANCE)
			switch (semanticObject.eClass().getClassifierID()) {
			case EtlPackage.ATTRIBUTE:
				sequence_Attribute(context, (Attribute) semanticObject); 
				return; 
			case EtlPackage.ATTRIBUTE_GROUP:
				sequence_AttributeGroup(context, (AttributeGroup) semanticObject); 
				return; 
			case EtlPackage.CONCEPT_ID_REPLACEMENT_SLOT:
				sequence_ConceptIdReplacementSlot(context, (ConceptIdReplacementSlot) semanticObject); 
				return; 
			case EtlPackage.CONCEPT_REFERENCE:
				sequence_ConceptReference(context, (ConceptReference) semanticObject); 
				return; 
			case EtlPackage.DECIMAL_REPLACEMENT_SLOT:
				sequence_DecimalReplacementSlot(context, (DecimalReplacementSlot) semanticObject); 
				return; 
			case EtlPackage.DECIMAL_VALUE:
				sequence_DecimalValue(context, (DecimalValue) semanticObject); 
				return; 
			case EtlPackage.ETL_CARDINALITY:
				sequence_EtlCardinality(context, (EtlCardinality) semanticObject); 
				return; 
			case EtlPackage.EXPRESSION_REPLACEMENT_SLOT:
				sequence_ExpressionReplacementSlot(context, (ExpressionReplacementSlot) semanticObject); 
				return; 
			case EtlPackage.EXPRESSION_TEMPLATE:
				sequence_ExpressionTemplate(context, (ExpressionTemplate) semanticObject); 
				return; 
			case EtlPackage.FOCUS_CONCEPT:
				sequence_FocusConcept(context, (FocusConcept) semanticObject); 
				return; 
			case EtlPackage.INTEGER_REPLACEMENT_SLOT:
				sequence_IntegerReplacementSlot(context, (IntegerReplacementSlot) semanticObject); 
				return; 
			case EtlPackage.INTEGER_VALUE:
				sequence_IntegerValue(context, (IntegerValue) semanticObject); 
				return; 
			case EtlPackage.REFINEMENT:
				sequence_Refinement(context, (Refinement) semanticObject); 
				return; 
			case EtlPackage.SLOT_DECIMAL_MAXIMUM_VALUE:
				sequence_SlotDecimalMaximumValue(context, (SlotDecimalMaximumValue) semanticObject); 
				return; 
			case EtlPackage.SLOT_DECIMAL_MINIMUM_VALUE:
				sequence_SlotDecimalMinimumValue(context, (SlotDecimalMinimumValue) semanticObject); 
				return; 
			case EtlPackage.SLOT_DECIMAL_RANGE:
				sequence_SlotDecimalRange(context, (SlotDecimalRange) semanticObject); 
				return; 
			case EtlPackage.SLOT_DECIMAL_VALUE:
				sequence_SlotDecimalValue(context, (SlotDecimalValue) semanticObject); 
				return; 
			case EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE:
				sequence_SlotIntegerMaximumValue(context, (SlotIntegerMaximumValue) semanticObject); 
				return; 
			case EtlPackage.SLOT_INTEGER_MINIMUM_VALUE:
				sequence_SlotIntegerMinimumValue(context, (SlotIntegerMinimumValue) semanticObject); 
				return; 
			case EtlPackage.SLOT_INTEGER_RANGE:
				sequence_SlotIntegerRange(context, (SlotIntegerRange) semanticObject); 
				return; 
			case EtlPackage.SLOT_INTEGER_VALUE:
				sequence_SlotIntegerValue(context, (SlotIntegerValue) semanticObject); 
				return; 
			case EtlPackage.STRING_REPLACEMENT_SLOT:
				sequence_StringReplacementSlot(context, (StringReplacementSlot) semanticObject); 
				return; 
			case EtlPackage.STRING_VALUE:
				sequence_StringValue(context, (StringValue) semanticObject); 
				return; 
			case EtlPackage.SUB_EXPRESSION:
				sequence_SubExpression(context, (SubExpression) semanticObject); 
				return; 
			case EtlPackage.TEMPLATE_INFORMATION_SLOT:
				sequence_TemplateInformationSlot(context, (TemplateInformationSlot) semanticObject); 
				return; 
			case EtlPackage.TOKEN_REPLACEMENT_SLOT:
				sequence_TokenReplacementSlot(context, (TokenReplacementSlot) semanticObject); 
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
	 *     (slot=TemplateInformationSlot? attributes+=Attribute attributes+=Attribute*)
	 */
	protected void sequence_AttributeGroup(ISerializationContext context, AttributeGroup semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Attribute returns Attribute
	 *
	 * Constraint:
	 *     (slot=TemplateInformationSlot? name=ConceptReference value=AttributeValue)
	 */
	protected void sequence_Attribute(ISerializationContext context, Attribute semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     ConceptIdReplacementSlot returns ConceptIdReplacementSlot
	 *     ConceptReplacementSlot returns ConceptIdReplacementSlot
	 *
	 * Constraint:
	 *     (constraint=ExpressionConstraint? name=SLOTNAME_STRING?)
	 */
	protected void sequence_ConceptIdReplacementSlot(ISerializationContext context, ConceptIdReplacementSlot semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     AttributeValue returns ConceptReference
	 *     ConceptReference returns ConceptReference
	 *
	 * Constraint:
	 *     (slot=ConceptReplacementSlot | (id=SnomedIdentifier term=TERM_STRING?))
	 */
	protected void sequence_ConceptReference(ISerializationContext context, ConceptReference semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     AttributeValue returns DecimalReplacementSlot
	 *     ConcreteValueReplacementSlot returns DecimalReplacementSlot
	 *     DecimalReplacementSlot returns DecimalReplacementSlot
	 *
	 * Constraint:
	 *     ((values+=SlotDecimal values+=SlotDecimal*)? name=SLOTNAME_STRING?)
	 */
	protected void sequence_DecimalReplacementSlot(ISerializationContext context, DecimalReplacementSlot semanticObject) {
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
			if (transientValues.isValueTransient(semanticObject, EtlPackage.Literals.DECIMAL_VALUE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EtlPackage.Literals.DECIMAL_VALUE__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDecimalValueAccess().getValueDecimalParserRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     EtlCardinality returns EtlCardinality
	 *
	 * Constraint:
	 *     (min=NonNegativeInteger max=MaxValue)
	 */
	protected void sequence_EtlCardinality(ISerializationContext context, EtlCardinality semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EtlPackage.Literals.ETL_CARDINALITY__MIN) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EtlPackage.Literals.ETL_CARDINALITY__MIN));
			if (transientValues.isValueTransient(semanticObject, EtlPackage.Literals.ETL_CARDINALITY__MAX) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EtlPackage.Literals.ETL_CARDINALITY__MAX));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getEtlCardinalityAccess().getMinNonNegativeIntegerParserRuleCall_1_0(), semanticObject.getMin());
		feeder.accept(grammarAccess.getEtlCardinalityAccess().getMaxMaxValueParserRuleCall_3_0(), semanticObject.getMax());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionReplacementSlot returns ExpressionReplacementSlot
	 *     ConceptReplacementSlot returns ExpressionReplacementSlot
	 *
	 * Constraint:
	 *     (constraint=ExpressionConstraint? name=SLOTNAME_STRING?)
	 */
	protected void sequence_ExpressionReplacementSlot(ISerializationContext context, ExpressionReplacementSlot semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionTemplate returns ExpressionTemplate
	 *
	 * Constraint:
	 *     ((primitive?=SUBTYPE_OF | slot=TokenReplacementSlot)? expression=SubExpression)?
	 */
	protected void sequence_ExpressionTemplate(ISerializationContext context, ExpressionTemplate semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     FocusConcept returns FocusConcept
	 *
	 * Constraint:
	 *     (slot=TemplateInformationSlot? concept=ConceptReference)
	 */
	protected void sequence_FocusConcept(ISerializationContext context, FocusConcept semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     AttributeValue returns IntegerReplacementSlot
	 *     ConcreteValueReplacementSlot returns IntegerReplacementSlot
	 *     IntegerReplacementSlot returns IntegerReplacementSlot
	 *
	 * Constraint:
	 *     ((values+=SlotInteger values+=SlotInteger*)? name=SLOTNAME_STRING?)
	 */
	protected void sequence_IntegerReplacementSlot(ISerializationContext context, IntegerReplacementSlot semanticObject) {
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
			if (transientValues.isValueTransient(semanticObject, EtlPackage.Literals.INTEGER_VALUE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EtlPackage.Literals.INTEGER_VALUE__VALUE));
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
	 *     SlotDecimalMaximumValue returns SlotDecimalMaximumValue
	 *
	 * Constraint:
	 *     (exclusive?=LT? value=NonNegativeDecimal)
	 */
	protected void sequence_SlotDecimalMaximumValue(ISerializationContext context, SlotDecimalMaximumValue semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     SlotDecimalMinimumValue returns SlotDecimalMinimumValue
	 *
	 * Constraint:
	 *     (exclusive?=GT? value=NonNegativeDecimal)
	 */
	protected void sequence_SlotDecimalMinimumValue(ISerializationContext context, SlotDecimalMinimumValue semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     SlotDecimal returns SlotDecimalRange
	 *     SlotDecimalRange returns SlotDecimalRange
	 *
	 * Constraint:
	 *     ((minimum=SlotDecimalMinimumValue maximum=SlotDecimalMaximumValue?) | maximum=SlotDecimalMaximumValue)
	 */
	protected void sequence_SlotDecimalRange(ISerializationContext context, SlotDecimalRange semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     SlotDecimal returns SlotDecimalValue
	 *     SlotDecimalValue returns SlotDecimalValue
	 *
	 * Constraint:
	 *     value=NonNegativeDecimal
	 */
	protected void sequence_SlotDecimalValue(ISerializationContext context, SlotDecimalValue semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EtlPackage.Literals.SLOT_DECIMAL_VALUE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EtlPackage.Literals.SLOT_DECIMAL_VALUE__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getSlotDecimalValueAccess().getValueNonNegativeDecimalParserRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     SlotIntegerMaximumValue returns SlotIntegerMaximumValue
	 *
	 * Constraint:
	 *     (exclusive?=LT? value=NonNegativeInteger)
	 */
	protected void sequence_SlotIntegerMaximumValue(ISerializationContext context, SlotIntegerMaximumValue semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     SlotIntegerMinimumValue returns SlotIntegerMinimumValue
	 *
	 * Constraint:
	 *     (exclusive?=GT? value=NonNegativeInteger)
	 */
	protected void sequence_SlotIntegerMinimumValue(ISerializationContext context, SlotIntegerMinimumValue semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     SlotInteger returns SlotIntegerRange
	 *     SlotIntegerRange returns SlotIntegerRange
	 *
	 * Constraint:
	 *     ((minimum=SlotIntegerMinimumValue maximum=SlotIntegerMaximumValue?) | maximum=SlotIntegerMaximumValue)
	 */
	protected void sequence_SlotIntegerRange(ISerializationContext context, SlotIntegerRange semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     SlotInteger returns SlotIntegerValue
	 *     SlotIntegerValue returns SlotIntegerValue
	 *
	 * Constraint:
	 *     value=NonNegativeInteger
	 */
	protected void sequence_SlotIntegerValue(ISerializationContext context, SlotIntegerValue semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EtlPackage.Literals.SLOT_INTEGER_VALUE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EtlPackage.Literals.SLOT_INTEGER_VALUE__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getSlotIntegerValueAccess().getValueNonNegativeIntegerParserRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     AttributeValue returns StringReplacementSlot
	 *     ConcreteValueReplacementSlot returns StringReplacementSlot
	 *     StringReplacementSlot returns StringReplacementSlot
	 *
	 * Constraint:
	 *     ((values+=STRING values+=STRING*)? name=SLOTNAME_STRING?)
	 */
	protected void sequence_StringReplacementSlot(ISerializationContext context, StringReplacementSlot semanticObject) {
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
			if (transientValues.isValueTransient(semanticObject, EtlPackage.Literals.STRING_VALUE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EtlPackage.Literals.STRING_VALUE__VALUE));
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
	 *     (focusConcepts+=FocusConcept focusConcepts+=FocusConcept* refinement=Refinement?)
	 */
	protected void sequence_SubExpression(ISerializationContext context, SubExpression semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     TemplateInformationSlot returns TemplateInformationSlot
	 *
	 * Constraint:
	 *     (cardinality=EtlCardinality? name=SLOTNAME_STRING?)
	 */
	protected void sequence_TemplateInformationSlot(ISerializationContext context, TemplateInformationSlot semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     TokenReplacementSlot returns TokenReplacementSlot
	 *
	 * Constraint:
	 *     ((tokens+=SlotToken tokens+=SlotToken*)? name=SLOTNAME_STRING?)
	 */
	protected void sequence_TokenReplacementSlot(ISerializationContext context, TokenReplacementSlot semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
}
