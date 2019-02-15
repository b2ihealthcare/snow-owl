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
package com.b2international.snowowl.snomed.ql.serializer;

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
import com.b2international.snowowl.snomed.ecl.ecl.Script;
import com.b2international.snowowl.snomed.ecl.ecl.StringValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.StringValueNotEquals;
import com.b2international.snowowl.snomed.ecl.serializer.EclSemanticSequencer;
import com.b2international.snowowl.snomed.ql.ql.ActiveFilter;
import com.b2international.snowowl.snomed.ql.ql.ActiveTerm;
import com.b2international.snowowl.snomed.ql.ql.Conjunction;
import com.b2international.snowowl.snomed.ql.ql.Description;
import com.b2international.snowowl.snomed.ql.ql.DescriptionFilter;
import com.b2international.snowowl.snomed.ql.ql.Descriptiontype;
import com.b2international.snowowl.snomed.ql.ql.Disjunction;
import com.b2international.snowowl.snomed.ql.ql.EclFilter;
import com.b2international.snowowl.snomed.ql.ql.Exclusion;
import com.b2international.snowowl.snomed.ql.ql.NestedFilter;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;
import com.b2international.snowowl.snomed.ql.ql.Query;
import com.b2international.snowowl.snomed.ql.ql.RegularExpression;
import com.b2international.snowowl.snomed.ql.ql.TermFilter;
import com.b2international.snowowl.snomed.ql.services.QLGrammarAccess;
import com.google.inject.Inject;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.Action;
import org.eclipse.xtext.Parameter;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.serializer.ISerializationContext;
import org.eclipse.xtext.serializer.acceptor.SequenceFeeder;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;

@SuppressWarnings("all")
public class QLSemanticSequencer extends EclSemanticSequencer {

	@Inject
	private QLGrammarAccess grammarAccess;
	
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
				sequence_DescendantOf(context, (DescendantOf) semanticObject); 
				return; 
			case EclPackage.DESCENDANT_OR_SELF_OF:
				sequence_DescendantOrSelfOf(context, (DescendantOrSelfOf) semanticObject); 
				return; 
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
		else if (epackage == QlPackage.eINSTANCE)
			switch (semanticObject.eClass().getClassifierID()) {
			case QlPackage.ACTIVE_FILTER:
				sequence_ActiveFilter(context, (ActiveFilter) semanticObject); 
				return; 
			case QlPackage.ACTIVE_TERM:
				sequence_ActiveTerm(context, (ActiveTerm) semanticObject); 
				return; 
			case QlPackage.CONJUNCTION:
				sequence_Conjunction(context, (Conjunction) semanticObject); 
				return; 
			case QlPackage.DESCRIPTION:
				sequence_Description(context, (Description) semanticObject); 
				return; 
			case QlPackage.DESCRIPTION_FILTER:
				sequence_DescriptionFilter(context, (DescriptionFilter) semanticObject); 
				return; 
			case QlPackage.DESCRIPTIONTYPE:
				sequence_Descriptiontype(context, (Descriptiontype) semanticObject); 
				return; 
			case QlPackage.DISJUNCTION:
				sequence_Disjunction(context, (Disjunction) semanticObject); 
				return; 
			case QlPackage.ECL_FILTER:
				sequence_EclFilter(context, (EclFilter) semanticObject); 
				return; 
			case QlPackage.EXCLUSION:
				sequence_Exclusion(context, (Exclusion) semanticObject); 
				return; 
			case QlPackage.NESTED_FILTER:
				sequence_NestedFilter(context, (NestedFilter) semanticObject); 
				return; 
			case QlPackage.QUERY:
				sequence_Query(context, (Query) semanticObject); 
				return; 
			case QlPackage.REGULAR_EXPRESSION:
				sequence_RegularExpression(context, (RegularExpression) semanticObject); 
				return; 
			case QlPackage.TERM_FILTER:
				sequence_TermFilter(context, (TermFilter) semanticObject); 
				return; 
			}
		if (errorAcceptor != null)
			errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Contexts:
	 *     Constraint returns ActiveFilter
	 *     Disjunction returns ActiveFilter
	 *     Disjunction.Disjunction_1_0 returns ActiveFilter
	 *     Conjunction returns ActiveFilter
	 *     Conjunction.Conjunction_1_0 returns ActiveFilter
	 *     Exclusion returns ActiveFilter
	 *     Exclusion.Exclusion_1_0 returns ActiveFilter
	 *     Filter returns ActiveFilter
	 *     ActiveFilter returns ActiveFilter
	 *
	 * Constraint:
	 *     active=Boolean
	 */
	protected void sequence_ActiveFilter(ISerializationContext context, ActiveFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.ACTIVE_FILTER__ACTIVE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.ACTIVE_FILTER__ACTIVE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getActiveFilterAccess().getActiveBooleanParserRuleCall_1_0(), semanticObject.getActive());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     ActiveTerm returns ActiveTerm
	 *
	 * Constraint:
	 *     active=Boolean
	 */
	protected void sequence_ActiveTerm(ISerializationContext context, ActiveTerm semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.ACTIVE_TERM__ACTIVE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.ACTIVE_TERM__ACTIVE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getActiveTermAccess().getActiveBooleanParserRuleCall_2_0(), semanticObject.getActive());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Constraint returns Conjunction
	 *     Disjunction returns Conjunction
	 *     Disjunction.Disjunction_1_0 returns Conjunction
	 *     Conjunction returns Conjunction
	 *     Conjunction.Conjunction_1_0 returns Conjunction
	 *
	 * Constraint:
	 *     (left=Conjunction_Conjunction_1_0 right=Exclusion)
	 */
	protected void sequence_Conjunction(ISerializationContext context, Conjunction semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.CONJUNCTION__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.CONJUNCTION__LEFT));
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.CONJUNCTION__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.CONJUNCTION__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getConjunctionAccess().getConjunctionLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getConjunctionAccess().getRightExclusionParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     DescriptionFilter returns DescriptionFilter
	 *
	 * Constraint:
	 *     (termFilter=TermFilter | active=ActiveTerm | type=Descriptiontype | regex=RegularExpression)*
	 */
	protected void sequence_DescriptionFilter(ISerializationContext context, DescriptionFilter semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Constraint returns Description
	 *     Disjunction returns Description
	 *     Disjunction.Disjunction_1_0 returns Description
	 *     Conjunction returns Description
	 *     Conjunction.Conjunction_1_0 returns Description
	 *     Exclusion returns Description
	 *     Exclusion.Exclusion_1_0 returns Description
	 *     Filter returns Description
	 *     Description returns Description
	 *
	 * Constraint:
	 *     filter=DescriptionFilter
	 */
	protected void sequence_Description(ISerializationContext context, Description semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.DESCRIPTION__FILTER) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.DESCRIPTION__FILTER));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDescriptionAccess().getFilterDescriptionFilterParserRuleCall_2_0(), semanticObject.getFilter());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Descriptiontype returns Descriptiontype
	 *
	 * Constraint:
	 *     ecl=Script
	 */
	protected void sequence_Descriptiontype(ISerializationContext context, Descriptiontype semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.DESCRIPTIONTYPE__ECL) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.DESCRIPTIONTYPE__ECL));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDescriptiontypeAccess().getEclScriptParserRuleCall_2_0(), semanticObject.getEcl());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Constraint returns Disjunction
	 *     Disjunction returns Disjunction
	 *     Disjunction.Disjunction_1_0 returns Disjunction
	 *
	 * Constraint:
	 *     (left=Disjunction_Disjunction_1_0 right=Conjunction)
	 */
	protected void sequence_Disjunction(ISerializationContext context, Disjunction semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.DISJUNCTION__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.DISJUNCTION__LEFT));
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.DISJUNCTION__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.DISJUNCTION__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDisjunctionAccess().getDisjunctionLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getDisjunctionAccess().getRightConjunctionParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Constraint returns EclFilter
	 *     Disjunction returns EclFilter
	 *     Disjunction.Disjunction_1_0 returns EclFilter
	 *     Conjunction returns EclFilter
	 *     Conjunction.Conjunction_1_0 returns EclFilter
	 *     Exclusion returns EclFilter
	 *     Exclusion.Exclusion_1_0 returns EclFilter
	 *     Filter returns EclFilter
	 *     EclFilter returns EclFilter
	 *
	 * Constraint:
	 *     ecl=Script
	 */
	protected void sequence_EclFilter(ISerializationContext context, EclFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.ECL_FILTER__ECL) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.ECL_FILTER__ECL));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getEclFilterAccess().getEclScriptParserRuleCall_1_0(), semanticObject.getEcl());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Constraint returns Exclusion
	 *     Disjunction returns Exclusion
	 *     Disjunction.Disjunction_1_0 returns Exclusion
	 *     Conjunction returns Exclusion
	 *     Conjunction.Conjunction_1_0 returns Exclusion
	 *     Exclusion returns Exclusion
	 *
	 * Constraint:
	 *     (left=Exclusion_Exclusion_1_0 right=Filter)
	 */
	protected void sequence_Exclusion(ISerializationContext context, Exclusion semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.EXCLUSION__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.EXCLUSION__LEFT));
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.EXCLUSION__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.EXCLUSION__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getExclusionAccess().getExclusionLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getExclusionAccess().getRightFilterParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Constraint returns NestedFilter
	 *     Disjunction returns NestedFilter
	 *     Disjunction.Disjunction_1_0 returns NestedFilter
	 *     Conjunction returns NestedFilter
	 *     Conjunction.Conjunction_1_0 returns NestedFilter
	 *     Exclusion returns NestedFilter
	 *     Exclusion.Exclusion_1_0 returns NestedFilter
	 *     NestedFilter returns NestedFilter
	 *     Filter returns NestedFilter
	 *
	 * Constraint:
	 *     constraint=Constraint
	 */
	protected void sequence_NestedFilter(ISerializationContext context, NestedFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.NESTED_FILTER__CONSTRAINT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.NESTED_FILTER__CONSTRAINT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getNestedFilterAccess().getConstraintConstraintParserRuleCall_1_0(), semanticObject.getConstraint());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Query returns Query
	 *
	 * Constraint:
	 *     constraint=Constraint?
	 */
	protected void sequence_Query(ISerializationContext context, Query semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     RegularExpression returns RegularExpression
	 *
	 * Constraint:
	 *     regex=STRING
	 */
	protected void sequence_RegularExpression(ISerializationContext context, RegularExpression semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.REGULAR_EXPRESSION__REGEX) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.REGULAR_EXPRESSION__REGEX));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getRegularExpressionAccess().getRegexSTRINGTerminalRuleCall_2_0(), semanticObject.getRegex());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     TermFilter returns TermFilter
	 *
	 * Constraint:
	 *     term=STRING
	 */
	protected void sequence_TermFilter(ISerializationContext context, TermFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.TERM_FILTER__TERM) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.TERM_FILTER__TERM));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getTermFilterAccess().getTermSTRINGTerminalRuleCall_2_0(), semanticObject.getTerm());
		feeder.finish();
	}
	
	
}
