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
package com.b2international.snowowl.snomed.ecl.serializer;

import com.b2international.snowowl.snomed.ecl.ecl.AncestorOf;
import com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AndRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.Any;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.Cardinality;
import com.b2international.snowowl.snomed.ecl.ecl.ChildOf;
import com.b2international.snowowl.snomed.ecl.ecl.ConceptReference;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThan;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThanEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThan;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThanEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOf;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.EclPackage;
import com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.IntegerValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThan;
import com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThanEquals;
import com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThan;
import com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThanEquals;
import com.b2international.snowowl.snomed.ecl.ecl.IntegerValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.MemberOf;
import com.b2international.snowowl.snomed.ecl.ecl.NestedExpression;
import com.b2international.snowowl.snomed.ecl.ecl.NestedRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.OrRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.ParentOf;
import com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.StringValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.StringValueNotEquals;
import com.b2international.snowowl.snomed.ecl.services.EclGrammarAccess;
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
public class EclSemanticSequencer extends AbstractDelegatingSemanticSequencer {

	@Inject
	private EclGrammarAccess grammarAccess;
	
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
				if (rule == grammarAccess.getAttributeSetRule()
						|| rule == grammarAccess.getOrAttributeSetRule()
						|| action == grammarAccess.getOrAttributeSetAccess().getOrRefinementLeftAction_1_0()
						|| rule == grammarAccess.getAndAttributeSetRule()
						|| action == grammarAccess.getAndAttributeSetAccess().getAndRefinementLeftAction_1_0()) {
					sequence_AndAttributeSet(context, (AndRefinement) semanticObject); 
					return; 
				}
				else if (rule == grammarAccess.getRefinementRule()
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
			case EclPackage.ATTRIBUTE_GROUP:
				sequence_AttributeGroup(context, (AttributeGroup) semanticObject); 
				return; 
			case EclPackage.ATTRIBUTE_VALUE_EQUALS:
				sequence_AttributeValueEquals(context, (AttributeValueEquals) semanticObject); 
				return; 
			case EclPackage.ATTRIBUTE_VALUE_NOT_EQUALS:
				sequence_AttributeValueNotEquals(context, (AttributeValueNotEquals) semanticObject); 
				return; 
			case EclPackage.CARDINALITY:
				sequence_Cardinality(context, (Cardinality) semanticObject); 
				return; 
			case EclPackage.CHILD_OF:
				sequence_ChildOf(context, (ChildOf) semanticObject); 
				return; 
			case EclPackage.CONCEPT_REFERENCE:
				sequence_ConceptReference(context, (ConceptReference) semanticObject); 
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
				if (rule == grammarAccess.getAttributeRule()
						|| rule == grammarAccess.getAttributeDescendantOfRule()) {
					sequence_AttributeDescendantOf(context, (DescendantOf) semanticObject); 
					return; 
				}
				else if (rule == grammarAccess.getExpressionConstraintRule()
						|| rule == grammarAccess.getOrExpressionConstraintRule()
						|| action == grammarAccess.getOrExpressionConstraintAccess().getOrExpressionConstraintLeftAction_1_0()
						|| rule == grammarAccess.getAndExpressionConstraintRule()
						|| action == grammarAccess.getAndExpressionConstraintAccess().getAndExpressionConstraintLeftAction_1_0()
						|| rule == grammarAccess.getExclusionExpressionConstraintRule()
						|| action == grammarAccess.getExclusionExpressionConstraintAccess().getExclusionExpressionConstraintLeftAction_1_0()
						|| rule == grammarAccess.getRefinedExpressionConstraintRule()
						|| action == grammarAccess.getRefinedExpressionConstraintAccess().getRefinedExpressionConstraintConstraintAction_1_0()
						|| rule == grammarAccess.getDottedExpressionConstraintRule()
						|| action == grammarAccess.getDottedExpressionConstraintAccess().getDottedExpressionConstraintConstraintAction_1_0()
						|| rule == grammarAccess.getSimpleExpressionConstraintRule()
						|| rule == grammarAccess.getDescendantOfRule()) {
					sequence_DescendantOf(context, (DescendantOf) semanticObject); 
					return; 
				}
				else break;
			case EclPackage.DESCENDANT_OR_SELF_OF:
				if (rule == grammarAccess.getAttributeRule()
						|| rule == grammarAccess.getAttributeDescendantOrSelfOfRule()) {
					sequence_AttributeDescendantOrSelfOf(context, (DescendantOrSelfOf) semanticObject); 
					return; 
				}
				else if (rule == grammarAccess.getExpressionConstraintRule()
						|| rule == grammarAccess.getOrExpressionConstraintRule()
						|| action == grammarAccess.getOrExpressionConstraintAccess().getOrExpressionConstraintLeftAction_1_0()
						|| rule == grammarAccess.getAndExpressionConstraintRule()
						|| action == grammarAccess.getAndExpressionConstraintAccess().getAndExpressionConstraintLeftAction_1_0()
						|| rule == grammarAccess.getExclusionExpressionConstraintRule()
						|| action == grammarAccess.getExclusionExpressionConstraintAccess().getExclusionExpressionConstraintLeftAction_1_0()
						|| rule == grammarAccess.getRefinedExpressionConstraintRule()
						|| action == grammarAccess.getRefinedExpressionConstraintAccess().getRefinedExpressionConstraintConstraintAction_1_0()
						|| rule == grammarAccess.getDottedExpressionConstraintRule()
						|| action == grammarAccess.getDottedExpressionConstraintAccess().getDottedExpressionConstraintConstraintAction_1_0()
						|| rule == grammarAccess.getSimpleExpressionConstraintRule()
						|| rule == grammarAccess.getDescendantOrSelfOfRule()) {
					sequence_DescendantOrSelfOf(context, (DescendantOrSelfOf) semanticObject); 
					return; 
				}
				else break;
			case EclPackage.DOTTED_EXPRESSION_CONSTRAINT:
				sequence_DottedExpressionConstraint(context, (DottedExpressionConstraint) semanticObject); 
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
				if (rule == grammarAccess.getAttributeSetRule()
						|| rule == grammarAccess.getOrAttributeSetRule()
						|| action == grammarAccess.getOrAttributeSetAccess().getOrRefinementLeftAction_1_0()
						|| rule == grammarAccess.getAndAttributeSetRule()
						|| action == grammarAccess.getAndAttributeSetAccess().getAndRefinementLeftAction_1_0()
						|| rule == grammarAccess.getSubAttributeSetRule()
						|| rule == grammarAccess.getNestedAttributeSetRule()) {
					sequence_NestedAttributeSet(context, (NestedRefinement) semanticObject); 
					return; 
				}
				else if (rule == grammarAccess.getRefinementRule()
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
				if (rule == grammarAccess.getAttributeSetRule()
						|| rule == grammarAccess.getOrAttributeSetRule()
						|| action == grammarAccess.getOrAttributeSetAccess().getOrRefinementLeftAction_1_0()) {
					sequence_OrAttributeSet(context, (OrRefinement) semanticObject); 
					return; 
				}
				else if (rule == grammarAccess.getRefinementRule()
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
			case EclPackage.STRING_VALUE_EQUALS:
				sequence_StringValueEquals(context, (StringValueEquals) semanticObject); 
				return; 
			case EclPackage.STRING_VALUE_NOT_EQUALS:
				sequence_StringValueNotEquals(context, (StringValueNotEquals) semanticObject); 
				return; 
			}
		if (errorAcceptor != null)
			errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns AncestorOf
	 *     OrExpressionConstraint returns AncestorOf
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns AncestorOf
	 *     AndExpressionConstraint returns AncestorOf
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns AncestorOf
	 *     ExclusionExpressionConstraint returns AncestorOf
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns AncestorOf
	 *     RefinedExpressionConstraint returns AncestorOf
	 *     RefinedExpressionConstraint.RefinedExpressionConstraint_1_0 returns AncestorOf
	 *     DottedExpressionConstraint returns AncestorOf
	 *     DottedExpressionConstraint.DottedExpressionConstraint_1_0 returns AncestorOf
	 *     SimpleExpressionConstraint returns AncestorOf
	 *     AncestorOf returns AncestorOf
	 *
	 * Constraint:
	 *     constraint=FocusConcept
	 */
	protected void sequence_AncestorOf(ISerializationContext context, AncestorOf semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.ANCESTOR_OF__CONSTRAINT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.ANCESTOR_OF__CONSTRAINT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getAncestorOfAccess().getConstraintFocusConceptParserRuleCall_1_0(), semanticObject.getConstraint());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns AncestorOrSelfOf
	 *     OrExpressionConstraint returns AncestorOrSelfOf
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns AncestorOrSelfOf
	 *     AndExpressionConstraint returns AncestorOrSelfOf
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns AncestorOrSelfOf
	 *     ExclusionExpressionConstraint returns AncestorOrSelfOf
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns AncestorOrSelfOf
	 *     RefinedExpressionConstraint returns AncestorOrSelfOf
	 *     RefinedExpressionConstraint.RefinedExpressionConstraint_1_0 returns AncestorOrSelfOf
	 *     DottedExpressionConstraint returns AncestorOrSelfOf
	 *     DottedExpressionConstraint.DottedExpressionConstraint_1_0 returns AncestorOrSelfOf
	 *     SimpleExpressionConstraint returns AncestorOrSelfOf
	 *     AncestorOrSelfOf returns AncestorOrSelfOf
	 *
	 * Constraint:
	 *     constraint=FocusConcept
	 */
	protected void sequence_AncestorOrSelfOf(ISerializationContext context, AncestorOrSelfOf semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.ANCESTOR_OR_SELF_OF__CONSTRAINT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.ANCESTOR_OR_SELF_OF__CONSTRAINT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getAncestorOrSelfOfAccess().getConstraintFocusConceptParserRuleCall_1_0(), semanticObject.getConstraint());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     AttributeSet returns AndRefinement
	 *     OrAttributeSet returns AndRefinement
	 *     OrAttributeSet.OrRefinement_1_0 returns AndRefinement
	 *     AndAttributeSet returns AndRefinement
	 *     AndAttributeSet.AndRefinement_1_0 returns AndRefinement
	 *
	 * Constraint:
	 *     (left=AndAttributeSet_AndRefinement_1_0 right=SubAttributeSet)
	 */
	protected void sequence_AndAttributeSet(ISerializationContext context, AndRefinement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.AND_REFINEMENT__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.AND_REFINEMENT__LEFT));
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.AND_REFINEMENT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.AND_REFINEMENT__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getAndAttributeSetAccess().getAndRefinementLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getAndAttributeSetAccess().getRightSubAttributeSetParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns AndExpressionConstraint
	 *     OrExpressionConstraint returns AndExpressionConstraint
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns AndExpressionConstraint
	 *     AndExpressionConstraint returns AndExpressionConstraint
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns AndExpressionConstraint
	 *
	 * Constraint:
	 *     (left=AndExpressionConstraint_AndExpressionConstraint_1_0 right=ExclusionExpressionConstraint)
	 */
	protected void sequence_AndExpressionConstraint(ISerializationContext context, AndExpressionConstraint semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.AND_EXPRESSION_CONSTRAINT__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.AND_EXPRESSION_CONSTRAINT__LEFT));
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.AND_EXPRESSION_CONSTRAINT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.AND_EXPRESSION_CONSTRAINT__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getAndExpressionConstraintAccess().getAndExpressionConstraintLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getAndExpressionConstraintAccess().getRightExclusionExpressionConstraintParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Refinement returns AndRefinement
	 *     OrRefinement returns AndRefinement
	 *     OrRefinement.OrRefinement_1_0_0 returns AndRefinement
	 *     AndRefinement returns AndRefinement
	 *     AndRefinement.AndRefinement_1_0_0 returns AndRefinement
	 *
	 * Constraint:
	 *     (left=AndRefinement_AndRefinement_1_0_0 right=SubRefinement)
	 */
	protected void sequence_AndRefinement(ISerializationContext context, AndRefinement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.AND_REFINEMENT__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.AND_REFINEMENT__LEFT));
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.AND_REFINEMENT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.AND_REFINEMENT__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getAndRefinementAccess().getAndRefinementLeftAction_1_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getAndRefinementAccess().getRightSubRefinementParserRuleCall_1_0_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns Any
	 *     OrExpressionConstraint returns Any
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns Any
	 *     AndExpressionConstraint returns Any
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns Any
	 *     ExclusionExpressionConstraint returns Any
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns Any
	 *     RefinedExpressionConstraint returns Any
	 *     RefinedExpressionConstraint.RefinedExpressionConstraint_1_0 returns Any
	 *     DottedExpressionConstraint returns Any
	 *     DottedExpressionConstraint.DottedExpressionConstraint_1_0 returns Any
	 *     SimpleExpressionConstraint returns Any
	 *     FocusConcept returns Any
	 *     Any returns Any
	 *     Attribute returns Any
	 *
	 * Constraint:
	 *     {Any}
	 */
	protected void sequence_Any(ISerializationContext context, Any semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Refinement returns AttributeConstraint
	 *     OrRefinement returns AttributeConstraint
	 *     OrRefinement.OrRefinement_1_0_0 returns AttributeConstraint
	 *     AndRefinement returns AttributeConstraint
	 *     AndRefinement.AndRefinement_1_0_0 returns AttributeConstraint
	 *     SubRefinement returns AttributeConstraint
	 *     AttributeSet returns AttributeConstraint
	 *     OrAttributeSet returns AttributeConstraint
	 *     OrAttributeSet.OrRefinement_1_0 returns AttributeConstraint
	 *     AndAttributeSet returns AttributeConstraint
	 *     AndAttributeSet.AndRefinement_1_0 returns AttributeConstraint
	 *     SubAttributeSet returns AttributeConstraint
	 *     AttributeConstraint returns AttributeConstraint
	 *
	 * Constraint:
	 *     (cardinality=Cardinality? reversed?=REVERSED? attribute=Attribute comparison=Comparison)
	 */
	protected void sequence_AttributeConstraint(ISerializationContext context, AttributeConstraint semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Attribute returns DescendantOf
	 *     AttributeDescendantOf returns DescendantOf
	 *
	 * Constraint:
	 *     (constraint=ConceptReference | constraint=Any)
	 */
	protected void sequence_AttributeDescendantOf(ISerializationContext context, DescendantOf semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Attribute returns DescendantOrSelfOf
	 *     AttributeDescendantOrSelfOf returns DescendantOrSelfOf
	 *
	 * Constraint:
	 *     (constraint=ConceptReference | constraint=Any)
	 */
	protected void sequence_AttributeDescendantOrSelfOf(ISerializationContext context, DescendantOrSelfOf semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Refinement returns AttributeGroup
	 *     OrRefinement returns AttributeGroup
	 *     OrRefinement.OrRefinement_1_0_0 returns AttributeGroup
	 *     AndRefinement returns AttributeGroup
	 *     AndRefinement.AndRefinement_1_0_0 returns AttributeGroup
	 *     SubRefinement returns AttributeGroup
	 *     AttributeGroup returns AttributeGroup
	 *
	 * Constraint:
	 *     (cardinality=Cardinality? refinement=AttributeSet)
	 */
	protected void sequence_AttributeGroup(ISerializationContext context, AttributeGroup semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns AttributeValueEquals
	 *     AttributeComparison returns AttributeValueEquals
	 *     AttributeValueEquals returns AttributeValueEquals
	 *
	 * Constraint:
	 *     constraint=SimpleExpressionConstraint
	 */
	protected void sequence_AttributeValueEquals(ISerializationContext context, AttributeValueEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.ATTRIBUTE_COMPARISON__CONSTRAINT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.ATTRIBUTE_COMPARISON__CONSTRAINT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getAttributeValueEqualsAccess().getConstraintSimpleExpressionConstraintParserRuleCall_1_0(), semanticObject.getConstraint());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns AttributeValueNotEquals
	 *     AttributeComparison returns AttributeValueNotEquals
	 *     AttributeValueNotEquals returns AttributeValueNotEquals
	 *
	 * Constraint:
	 *     constraint=SimpleExpressionConstraint
	 */
	protected void sequence_AttributeValueNotEquals(ISerializationContext context, AttributeValueNotEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.ATTRIBUTE_COMPARISON__CONSTRAINT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.ATTRIBUTE_COMPARISON__CONSTRAINT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getAttributeValueNotEqualsAccess().getConstraintSimpleExpressionConstraintParserRuleCall_1_0(), semanticObject.getConstraint());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Cardinality returns Cardinality
	 *
	 * Constraint:
	 *     (min=NonNegativeInteger max=MaxValue)
	 */
	protected void sequence_Cardinality(ISerializationContext context, Cardinality semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.CARDINALITY__MIN) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.CARDINALITY__MIN));
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.CARDINALITY__MAX) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.CARDINALITY__MAX));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getCardinalityAccess().getMinNonNegativeIntegerParserRuleCall_1_0(), semanticObject.getMin());
		feeder.accept(grammarAccess.getCardinalityAccess().getMaxMaxValueParserRuleCall_3_0(), semanticObject.getMax());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns ChildOf
	 *     OrExpressionConstraint returns ChildOf
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns ChildOf
	 *     AndExpressionConstraint returns ChildOf
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns ChildOf
	 *     ExclusionExpressionConstraint returns ChildOf
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns ChildOf
	 *     RefinedExpressionConstraint returns ChildOf
	 *     RefinedExpressionConstraint.RefinedExpressionConstraint_1_0 returns ChildOf
	 *     DottedExpressionConstraint returns ChildOf
	 *     DottedExpressionConstraint.DottedExpressionConstraint_1_0 returns ChildOf
	 *     SimpleExpressionConstraint returns ChildOf
	 *     ChildOf returns ChildOf
	 *
	 * Constraint:
	 *     constraint=FocusConcept
	 */
	protected void sequence_ChildOf(ISerializationContext context, ChildOf semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.CHILD_OF__CONSTRAINT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.CHILD_OF__CONSTRAINT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getChildOfAccess().getConstraintFocusConceptParserRuleCall_1_0(), semanticObject.getConstraint());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns ConceptReference
	 *     OrExpressionConstraint returns ConceptReference
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns ConceptReference
	 *     AndExpressionConstraint returns ConceptReference
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns ConceptReference
	 *     ExclusionExpressionConstraint returns ConceptReference
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns ConceptReference
	 *     RefinedExpressionConstraint returns ConceptReference
	 *     RefinedExpressionConstraint.RefinedExpressionConstraint_1_0 returns ConceptReference
	 *     DottedExpressionConstraint returns ConceptReference
	 *     DottedExpressionConstraint.DottedExpressionConstraint_1_0 returns ConceptReference
	 *     SimpleExpressionConstraint returns ConceptReference
	 *     FocusConcept returns ConceptReference
	 *     ConceptReference returns ConceptReference
	 *     Attribute returns ConceptReference
	 *
	 * Constraint:
	 *     (id=SnomedIdentifier term=Term?)
	 */
	protected void sequence_ConceptReference(ISerializationContext context, ConceptReference semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns DecimalValueEquals
	 *     DataTypeComparison returns DecimalValueEquals
	 *     DecimalValueEquals returns DecimalValueEquals
	 *
	 * Constraint:
	 *     value=Decimal
	 */
	protected void sequence_DecimalValueEquals(ISerializationContext context, DecimalValueEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.DECIMAL_VALUE_EQUALS__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.DECIMAL_VALUE_EQUALS__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDecimalValueEqualsAccess().getValueDecimalParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns DecimalValueGreaterThanEquals
	 *     DataTypeComparison returns DecimalValueGreaterThanEquals
	 *     DecimalValueGreaterThanEquals returns DecimalValueGreaterThanEquals
	 *
	 * Constraint:
	 *     value=Decimal
	 */
	protected void sequence_DecimalValueGreaterThanEquals(ISerializationContext context, DecimalValueGreaterThanEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.DECIMAL_VALUE_GREATER_THAN_EQUALS__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.DECIMAL_VALUE_GREATER_THAN_EQUALS__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDecimalValueGreaterThanEqualsAccess().getValueDecimalParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns DecimalValueGreaterThan
	 *     DataTypeComparison returns DecimalValueGreaterThan
	 *     DecimalValueGreaterThan returns DecimalValueGreaterThan
	 *
	 * Constraint:
	 *     value=Decimal
	 */
	protected void sequence_DecimalValueGreaterThan(ISerializationContext context, DecimalValueGreaterThan semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.DECIMAL_VALUE_GREATER_THAN__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.DECIMAL_VALUE_GREATER_THAN__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDecimalValueGreaterThanAccess().getValueDecimalParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns DecimalValueLessThanEquals
	 *     DataTypeComparison returns DecimalValueLessThanEquals
	 *     DecimalValueLessThanEquals returns DecimalValueLessThanEquals
	 *
	 * Constraint:
	 *     value=Decimal
	 */
	protected void sequence_DecimalValueLessThanEquals(ISerializationContext context, DecimalValueLessThanEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.DECIMAL_VALUE_LESS_THAN_EQUALS__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.DECIMAL_VALUE_LESS_THAN_EQUALS__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDecimalValueLessThanEqualsAccess().getValueDecimalParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns DecimalValueLessThan
	 *     DataTypeComparison returns DecimalValueLessThan
	 *     DecimalValueLessThan returns DecimalValueLessThan
	 *
	 * Constraint:
	 *     value=Decimal
	 */
	protected void sequence_DecimalValueLessThan(ISerializationContext context, DecimalValueLessThan semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.DECIMAL_VALUE_LESS_THAN__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.DECIMAL_VALUE_LESS_THAN__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDecimalValueLessThanAccess().getValueDecimalParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns DecimalValueNotEquals
	 *     DataTypeComparison returns DecimalValueNotEquals
	 *     DecimalValueNotEquals returns DecimalValueNotEquals
	 *
	 * Constraint:
	 *     value=Decimal
	 */
	protected void sequence_DecimalValueNotEquals(ISerializationContext context, DecimalValueNotEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.DECIMAL_VALUE_NOT_EQUALS__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.DECIMAL_VALUE_NOT_EQUALS__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDecimalValueNotEqualsAccess().getValueDecimalParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns DescendantOf
	 *     OrExpressionConstraint returns DescendantOf
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns DescendantOf
	 *     AndExpressionConstraint returns DescendantOf
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns DescendantOf
	 *     ExclusionExpressionConstraint returns DescendantOf
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns DescendantOf
	 *     RefinedExpressionConstraint returns DescendantOf
	 *     RefinedExpressionConstraint.RefinedExpressionConstraint_1_0 returns DescendantOf
	 *     DottedExpressionConstraint returns DescendantOf
	 *     DottedExpressionConstraint.DottedExpressionConstraint_1_0 returns DescendantOf
	 *     SimpleExpressionConstraint returns DescendantOf
	 *     DescendantOf returns DescendantOf
	 *
	 * Constraint:
	 *     constraint=FocusConcept
	 */
	protected void sequence_DescendantOf(ISerializationContext context, DescendantOf semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.DESCENDANT_OF__CONSTRAINT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.DESCENDANT_OF__CONSTRAINT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDescendantOfAccess().getConstraintFocusConceptParserRuleCall_1_0(), semanticObject.getConstraint());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns DescendantOrSelfOf
	 *     OrExpressionConstraint returns DescendantOrSelfOf
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns DescendantOrSelfOf
	 *     AndExpressionConstraint returns DescendantOrSelfOf
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns DescendantOrSelfOf
	 *     ExclusionExpressionConstraint returns DescendantOrSelfOf
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns DescendantOrSelfOf
	 *     RefinedExpressionConstraint returns DescendantOrSelfOf
	 *     RefinedExpressionConstraint.RefinedExpressionConstraint_1_0 returns DescendantOrSelfOf
	 *     DottedExpressionConstraint returns DescendantOrSelfOf
	 *     DottedExpressionConstraint.DottedExpressionConstraint_1_0 returns DescendantOrSelfOf
	 *     SimpleExpressionConstraint returns DescendantOrSelfOf
	 *     DescendantOrSelfOf returns DescendantOrSelfOf
	 *
	 * Constraint:
	 *     constraint=FocusConcept
	 */
	protected void sequence_DescendantOrSelfOf(ISerializationContext context, DescendantOrSelfOf semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.DESCENDANT_OR_SELF_OF__CONSTRAINT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.DESCENDANT_OR_SELF_OF__CONSTRAINT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDescendantOrSelfOfAccess().getConstraintFocusConceptParserRuleCall_1_0(), semanticObject.getConstraint());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns DottedExpressionConstraint
	 *     OrExpressionConstraint returns DottedExpressionConstraint
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns DottedExpressionConstraint
	 *     AndExpressionConstraint returns DottedExpressionConstraint
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns DottedExpressionConstraint
	 *     ExclusionExpressionConstraint returns DottedExpressionConstraint
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns DottedExpressionConstraint
	 *     RefinedExpressionConstraint returns DottedExpressionConstraint
	 *     RefinedExpressionConstraint.RefinedExpressionConstraint_1_0 returns DottedExpressionConstraint
	 *     DottedExpressionConstraint returns DottedExpressionConstraint
	 *     DottedExpressionConstraint.DottedExpressionConstraint_1_0 returns DottedExpressionConstraint
	 *
	 * Constraint:
	 *     (constraint=DottedExpressionConstraint_DottedExpressionConstraint_1_0 attribute=Attribute)
	 */
	protected void sequence_DottedExpressionConstraint(ISerializationContext context, DottedExpressionConstraint semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.DOTTED_EXPRESSION_CONSTRAINT__CONSTRAINT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.DOTTED_EXPRESSION_CONSTRAINT__CONSTRAINT));
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.DOTTED_EXPRESSION_CONSTRAINT__ATTRIBUTE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.DOTTED_EXPRESSION_CONSTRAINT__ATTRIBUTE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDottedExpressionConstraintAccess().getDottedExpressionConstraintConstraintAction_1_0(), semanticObject.getConstraint());
		feeder.accept(grammarAccess.getDottedExpressionConstraintAccess().getAttributeAttributeParserRuleCall_1_2_0(), semanticObject.getAttribute());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns ExclusionExpressionConstraint
	 *     OrExpressionConstraint returns ExclusionExpressionConstraint
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns ExclusionExpressionConstraint
	 *     AndExpressionConstraint returns ExclusionExpressionConstraint
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns ExclusionExpressionConstraint
	 *     ExclusionExpressionConstraint returns ExclusionExpressionConstraint
	 *
	 * Constraint:
	 *     (left=ExclusionExpressionConstraint_ExclusionExpressionConstraint_1_0 right=RefinedExpressionConstraint)
	 */
	protected void sequence_ExclusionExpressionConstraint(ISerializationContext context, ExclusionExpressionConstraint semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.EXCLUSION_EXPRESSION_CONSTRAINT__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.EXCLUSION_EXPRESSION_CONSTRAINT__LEFT));
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.EXCLUSION_EXPRESSION_CONSTRAINT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.EXCLUSION_EXPRESSION_CONSTRAINT__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getExclusionExpressionConstraintAccess().getExclusionExpressionConstraintLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getExclusionExpressionConstraintAccess().getRightRefinedExpressionConstraintParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns IntegerValueEquals
	 *     DataTypeComparison returns IntegerValueEquals
	 *     IntegerValueEquals returns IntegerValueEquals
	 *
	 * Constraint:
	 *     value=Integer
	 */
	protected void sequence_IntegerValueEquals(ISerializationContext context, IntegerValueEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.INTEGER_VALUE_EQUALS__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.INTEGER_VALUE_EQUALS__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getIntegerValueEqualsAccess().getValueIntegerParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns IntegerValueGreaterThanEquals
	 *     DataTypeComparison returns IntegerValueGreaterThanEquals
	 *     IntegerValueGreaterThanEquals returns IntegerValueGreaterThanEquals
	 *
	 * Constraint:
	 *     value=Integer
	 */
	protected void sequence_IntegerValueGreaterThanEquals(ISerializationContext context, IntegerValueGreaterThanEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.INTEGER_VALUE_GREATER_THAN_EQUALS__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.INTEGER_VALUE_GREATER_THAN_EQUALS__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getIntegerValueGreaterThanEqualsAccess().getValueIntegerParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns IntegerValueGreaterThan
	 *     DataTypeComparison returns IntegerValueGreaterThan
	 *     IntegerValueGreaterThan returns IntegerValueGreaterThan
	 *
	 * Constraint:
	 *     value=Integer
	 */
	protected void sequence_IntegerValueGreaterThan(ISerializationContext context, IntegerValueGreaterThan semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.INTEGER_VALUE_GREATER_THAN__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.INTEGER_VALUE_GREATER_THAN__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getIntegerValueGreaterThanAccess().getValueIntegerParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns IntegerValueLessThanEquals
	 *     DataTypeComparison returns IntegerValueLessThanEquals
	 *     IntegerValueLessThanEquals returns IntegerValueLessThanEquals
	 *
	 * Constraint:
	 *     value=Integer
	 */
	protected void sequence_IntegerValueLessThanEquals(ISerializationContext context, IntegerValueLessThanEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.INTEGER_VALUE_LESS_THAN_EQUALS__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.INTEGER_VALUE_LESS_THAN_EQUALS__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getIntegerValueLessThanEqualsAccess().getValueIntegerParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns IntegerValueLessThan
	 *     DataTypeComparison returns IntegerValueLessThan
	 *     IntegerValueLessThan returns IntegerValueLessThan
	 *
	 * Constraint:
	 *     value=Integer
	 */
	protected void sequence_IntegerValueLessThan(ISerializationContext context, IntegerValueLessThan semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.INTEGER_VALUE_LESS_THAN__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.INTEGER_VALUE_LESS_THAN__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getIntegerValueLessThanAccess().getValueIntegerParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns IntegerValueNotEquals
	 *     DataTypeComparison returns IntegerValueNotEquals
	 *     IntegerValueNotEquals returns IntegerValueNotEquals
	 *
	 * Constraint:
	 *     value=Integer
	 */
	protected void sequence_IntegerValueNotEquals(ISerializationContext context, IntegerValueNotEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.INTEGER_VALUE_NOT_EQUALS__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.INTEGER_VALUE_NOT_EQUALS__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getIntegerValueNotEqualsAccess().getValueIntegerParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns MemberOf
	 *     OrExpressionConstraint returns MemberOf
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns MemberOf
	 *     AndExpressionConstraint returns MemberOf
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns MemberOf
	 *     ExclusionExpressionConstraint returns MemberOf
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns MemberOf
	 *     RefinedExpressionConstraint returns MemberOf
	 *     RefinedExpressionConstraint.RefinedExpressionConstraint_1_0 returns MemberOf
	 *     DottedExpressionConstraint returns MemberOf
	 *     DottedExpressionConstraint.DottedExpressionConstraint_1_0 returns MemberOf
	 *     SimpleExpressionConstraint returns MemberOf
	 *     FocusConcept returns MemberOf
	 *     MemberOf returns MemberOf
	 *
	 * Constraint:
	 *     (constraint=ConceptReference | constraint=Any)
	 */
	protected void sequence_MemberOf(ISerializationContext context, MemberOf semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     AttributeSet returns NestedRefinement
	 *     OrAttributeSet returns NestedRefinement
	 *     OrAttributeSet.OrRefinement_1_0 returns NestedRefinement
	 *     AndAttributeSet returns NestedRefinement
	 *     AndAttributeSet.AndRefinement_1_0 returns NestedRefinement
	 *     SubAttributeSet returns NestedRefinement
	 *     NestedAttributeSet returns NestedRefinement
	 *
	 * Constraint:
	 *     nested=AttributeSet
	 */
	protected void sequence_NestedAttributeSet(ISerializationContext context, NestedRefinement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.NESTED_REFINEMENT__NESTED) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.NESTED_REFINEMENT__NESTED));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getNestedAttributeSetAccess().getNestedAttributeSetParserRuleCall_1_0(), semanticObject.getNested());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns NestedExpression
	 *     OrExpressionConstraint returns NestedExpression
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns NestedExpression
	 *     AndExpressionConstraint returns NestedExpression
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns NestedExpression
	 *     ExclusionExpressionConstraint returns NestedExpression
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns NestedExpression
	 *     RefinedExpressionConstraint returns NestedExpression
	 *     RefinedExpressionConstraint.RefinedExpressionConstraint_1_0 returns NestedExpression
	 *     DottedExpressionConstraint returns NestedExpression
	 *     DottedExpressionConstraint.DottedExpressionConstraint_1_0 returns NestedExpression
	 *     SimpleExpressionConstraint returns NestedExpression
	 *     FocusConcept returns NestedExpression
	 *     NestedExpression returns NestedExpression
	 *
	 * Constraint:
	 *     nested=ExpressionConstraint
	 */
	protected void sequence_NestedExpression(ISerializationContext context, NestedExpression semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.NESTED_EXPRESSION__NESTED) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.NESTED_EXPRESSION__NESTED));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getNestedExpressionAccess().getNestedExpressionConstraintParserRuleCall_1_0(), semanticObject.getNested());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Refinement returns NestedRefinement
	 *     OrRefinement returns NestedRefinement
	 *     OrRefinement.OrRefinement_1_0_0 returns NestedRefinement
	 *     AndRefinement returns NestedRefinement
	 *     AndRefinement.AndRefinement_1_0_0 returns NestedRefinement
	 *     SubRefinement returns NestedRefinement
	 *     NestedRefinement returns NestedRefinement
	 *
	 * Constraint:
	 *     nested=Refinement
	 */
	protected void sequence_NestedRefinement(ISerializationContext context, NestedRefinement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.NESTED_REFINEMENT__NESTED) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.NESTED_REFINEMENT__NESTED));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getNestedRefinementAccess().getNestedRefinementParserRuleCall_1_0(), semanticObject.getNested());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     AttributeSet returns OrRefinement
	 *     OrAttributeSet returns OrRefinement
	 *     OrAttributeSet.OrRefinement_1_0 returns OrRefinement
	 *
	 * Constraint:
	 *     (left=OrAttributeSet_OrRefinement_1_0 right=AndAttributeSet)
	 */
	protected void sequence_OrAttributeSet(ISerializationContext context, OrRefinement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.OR_REFINEMENT__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.OR_REFINEMENT__LEFT));
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.OR_REFINEMENT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.OR_REFINEMENT__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getOrAttributeSetAccess().getOrRefinementLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getOrAttributeSetAccess().getRightAndAttributeSetParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns OrExpressionConstraint
	 *     OrExpressionConstraint returns OrExpressionConstraint
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns OrExpressionConstraint
	 *
	 * Constraint:
	 *     (left=OrExpressionConstraint_OrExpressionConstraint_1_0 right=AndExpressionConstraint)
	 */
	protected void sequence_OrExpressionConstraint(ISerializationContext context, OrExpressionConstraint semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.OR_EXPRESSION_CONSTRAINT__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.OR_EXPRESSION_CONSTRAINT__LEFT));
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.OR_EXPRESSION_CONSTRAINT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.OR_EXPRESSION_CONSTRAINT__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getOrExpressionConstraintAccess().getOrExpressionConstraintLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getOrExpressionConstraintAccess().getRightAndExpressionConstraintParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Refinement returns OrRefinement
	 *     OrRefinement returns OrRefinement
	 *     OrRefinement.OrRefinement_1_0_0 returns OrRefinement
	 *
	 * Constraint:
	 *     (left=OrRefinement_OrRefinement_1_0_0 right=AndRefinement)
	 */
	protected void sequence_OrRefinement(ISerializationContext context, OrRefinement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.OR_REFINEMENT__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.OR_REFINEMENT__LEFT));
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.OR_REFINEMENT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.OR_REFINEMENT__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getOrRefinementAccess().getOrRefinementLeftAction_1_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getOrRefinementAccess().getRightAndRefinementParserRuleCall_1_0_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns ParentOf
	 *     OrExpressionConstraint returns ParentOf
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns ParentOf
	 *     AndExpressionConstraint returns ParentOf
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns ParentOf
	 *     ExclusionExpressionConstraint returns ParentOf
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns ParentOf
	 *     RefinedExpressionConstraint returns ParentOf
	 *     RefinedExpressionConstraint.RefinedExpressionConstraint_1_0 returns ParentOf
	 *     DottedExpressionConstraint returns ParentOf
	 *     DottedExpressionConstraint.DottedExpressionConstraint_1_0 returns ParentOf
	 *     SimpleExpressionConstraint returns ParentOf
	 *     ParentOf returns ParentOf
	 *
	 * Constraint:
	 *     constraint=FocusConcept
	 */
	protected void sequence_ParentOf(ISerializationContext context, ParentOf semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.PARENT_OF__CONSTRAINT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.PARENT_OF__CONSTRAINT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getParentOfAccess().getConstraintFocusConceptParserRuleCall_1_0(), semanticObject.getConstraint());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ExpressionConstraint returns RefinedExpressionConstraint
	 *     OrExpressionConstraint returns RefinedExpressionConstraint
	 *     OrExpressionConstraint.OrExpressionConstraint_1_0 returns RefinedExpressionConstraint
	 *     AndExpressionConstraint returns RefinedExpressionConstraint
	 *     AndExpressionConstraint.AndExpressionConstraint_1_0 returns RefinedExpressionConstraint
	 *     ExclusionExpressionConstraint returns RefinedExpressionConstraint
	 *     ExclusionExpressionConstraint.ExclusionExpressionConstraint_1_0 returns RefinedExpressionConstraint
	 *     RefinedExpressionConstraint returns RefinedExpressionConstraint
	 *
	 * Constraint:
	 *     (constraint=RefinedExpressionConstraint_RefinedExpressionConstraint_1_0 refinement=Refinement)
	 */
	protected void sequence_RefinedExpressionConstraint(ISerializationContext context, RefinedExpressionConstraint semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT));
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.REFINED_EXPRESSION_CONSTRAINT__REFINEMENT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.REFINED_EXPRESSION_CONSTRAINT__REFINEMENT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getRefinedExpressionConstraintAccess().getRefinedExpressionConstraintConstraintAction_1_0(), semanticObject.getConstraint());
		feeder.accept(grammarAccess.getRefinedExpressionConstraintAccess().getRefinementRefinementParserRuleCall_1_2_0(), semanticObject.getRefinement());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns StringValueEquals
	 *     DataTypeComparison returns StringValueEquals
	 *     StringValueEquals returns StringValueEquals
	 *
	 * Constraint:
	 *     value=STRING
	 */
	protected void sequence_StringValueEquals(ISerializationContext context, StringValueEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.STRING_VALUE_EQUALS__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.STRING_VALUE_EQUALS__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getStringValueEqualsAccess().getValueSTRINGTerminalRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Comparison returns StringValueNotEquals
	 *     DataTypeComparison returns StringValueNotEquals
	 *     StringValueNotEquals returns StringValueNotEquals
	 *
	 * Constraint:
	 *     value=STRING
	 */
	protected void sequence_StringValueNotEquals(ISerializationContext context, StringValueNotEquals semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, EclPackage.Literals.STRING_VALUE_NOT_EQUALS__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, EclPackage.Literals.STRING_VALUE_NOT_EQUALS__VALUE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getStringValueNotEqualsAccess().getValueSTRINGTerminalRuleCall_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
}
