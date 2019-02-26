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
import com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter;
import com.b2international.snowowl.snomed.ql.ql.ActiveFilter;
import com.b2international.snowowl.snomed.ql.ql.Conjunction;
import com.b2international.snowowl.snomed.ql.ql.Disjunction;
import com.b2international.snowowl.snomed.ql.ql.DomainQuery;
import com.b2international.snowowl.snomed.ql.ql.Exclusion;
import com.b2international.snowowl.snomed.ql.ql.LanguageRefSetFilter;
import com.b2international.snowowl.snomed.ql.ql.ModuleFilter;
import com.b2international.snowowl.snomed.ql.ql.NestedFilter;
import com.b2international.snowowl.snomed.ql.ql.NestedQuery;
import com.b2international.snowowl.snomed.ql.ql.PreferredInFilter;
import com.b2international.snowowl.snomed.ql.ql.QlPackage;
import com.b2international.snowowl.snomed.ql.ql.Query;
import com.b2international.snowowl.snomed.ql.ql.QueryConjunction;
import com.b2international.snowowl.snomed.ql.ql.QueryDisjunction;
import com.b2international.snowowl.snomed.ql.ql.QueryExclusion;
import com.b2international.snowowl.snomed.ql.ql.TermFilter;
import com.b2international.snowowl.snomed.ql.ql.TypeFilter;
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
			case QlPackage.ACCEPTABLE_IN_FILTER:
				sequence_AcceptableInFilter(context, (AcceptableInFilter) semanticObject); 
				return; 
			case QlPackage.ACTIVE_FILTER:
				sequence_ActiveFilter(context, (ActiveFilter) semanticObject); 
				return; 
			case QlPackage.CONJUNCTION:
				sequence_Conjunction(context, (Conjunction) semanticObject); 
				return; 
			case QlPackage.DISJUNCTION:
				sequence_Disjunction(context, (Disjunction) semanticObject); 
				return; 
			case QlPackage.DOMAIN_QUERY:
				sequence_DomainQuery(context, (DomainQuery) semanticObject); 
				return; 
			case QlPackage.EXCLUSION:
				sequence_Exclusion(context, (Exclusion) semanticObject); 
				return; 
			case QlPackage.LANGUAGE_REF_SET_FILTER:
				sequence_LanguageRefSetFilter(context, (LanguageRefSetFilter) semanticObject); 
				return; 
			case QlPackage.MODULE_FILTER:
				sequence_ModuleFilter(context, (ModuleFilter) semanticObject); 
				return; 
			case QlPackage.NESTED_FILTER:
				sequence_NestedFilter(context, (NestedFilter) semanticObject); 
				return; 
			case QlPackage.NESTED_QUERY:
				sequence_NestedQuery(context, (NestedQuery) semanticObject); 
				return; 
			case QlPackage.PREFERRED_IN_FILTER:
				sequence_PreferredInFilter(context, (PreferredInFilter) semanticObject); 
				return; 
			case QlPackage.QUERY:
				sequence_Query(context, (Query) semanticObject); 
				return; 
			case QlPackage.QUERY_CONJUNCTION:
				sequence_QueryConjunction(context, (QueryConjunction) semanticObject); 
				return; 
			case QlPackage.QUERY_DISJUNCTION:
				sequence_QueryDisjunction(context, (QueryDisjunction) semanticObject); 
				return; 
			case QlPackage.QUERY_EXCLUSION:
				sequence_QueryExclusion(context, (QueryExclusion) semanticObject); 
				return; 
			case QlPackage.TERM_FILTER:
				sequence_TermFilter(context, (TermFilter) semanticObject); 
				return; 
			case QlPackage.TYPE_FILTER:
				sequence_TypeFilter(context, (TypeFilter) semanticObject); 
				return; 
			}
		if (errorAcceptor != null)
			errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Contexts:
	 *     Filter returns AcceptableInFilter
	 *     Disjunction returns AcceptableInFilter
	 *     Disjunction.Disjunction_1_0 returns AcceptableInFilter
	 *     Conjunction returns AcceptableInFilter
	 *     Conjunction.Conjunction_1_0 returns AcceptableInFilter
	 *     Exclusion returns AcceptableInFilter
	 *     Exclusion.Exclusion_1_0 returns AcceptableInFilter
	 *     PropertyFilter returns AcceptableInFilter
	 *     AcceptableInFilter returns AcceptableInFilter
	 *
	 * Constraint:
	 *     languageRefSetId=ExpressionConstraint
	 */
	protected void sequence_AcceptableInFilter(ISerializationContext context, AcceptableInFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.ACCEPTABLE_IN_FILTER__LANGUAGE_REF_SET_ID) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.ACCEPTABLE_IN_FILTER__LANGUAGE_REF_SET_ID));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getAcceptableInFilterAccess().getLanguageRefSetIdExpressionConstraintParserRuleCall_2_0(), semanticObject.getLanguageRefSetId());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns ActiveFilter
	 *     Disjunction returns ActiveFilter
	 *     Disjunction.Disjunction_1_0 returns ActiveFilter
	 *     Conjunction returns ActiveFilter
	 *     Conjunction.Conjunction_1_0 returns ActiveFilter
	 *     Exclusion returns ActiveFilter
	 *     Exclusion.Exclusion_1_0 returns ActiveFilter
	 *     PropertyFilter returns ActiveFilter
	 *     ActiveFilter returns ActiveFilter
	 *
	 * Constraint:
	 *     (domain=Domain? active=Boolean)
	 */
	protected void sequence_ActiveFilter(ISerializationContext context, ActiveFilter semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns Conjunction
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
	 *     Filter returns Disjunction
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
	 *     QueryConstraint returns DomainQuery
	 *     QueryDisjunction returns DomainQuery
	 *     QueryDisjunction.QueryDisjunction_1_0 returns DomainQuery
	 *     QueryConjunction returns DomainQuery
	 *     QueryConjunction.QueryConjunction_1_0 returns DomainQuery
	 *     QueryExclusion returns DomainQuery
	 *     QueryExclusion.QueryExclusion_1_0 returns DomainQuery
	 *     SubQuery returns DomainQuery
	 *     DomainQuery returns DomainQuery
	 *
	 * Constraint:
	 *     (ecl=ExpressionConstraint? filter=Filter?)
	 */
	protected void sequence_DomainQuery(ISerializationContext context, DomainQuery semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns Exclusion
	 *     Disjunction returns Exclusion
	 *     Disjunction.Disjunction_1_0 returns Exclusion
	 *     Conjunction returns Exclusion
	 *     Conjunction.Conjunction_1_0 returns Exclusion
	 *     Exclusion returns Exclusion
	 *
	 * Constraint:
	 *     (left=Exclusion_Exclusion_1_0 right=PropertyFilter)
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
		feeder.accept(grammarAccess.getExclusionAccess().getRightPropertyFilterParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns LanguageRefSetFilter
	 *     Disjunction returns LanguageRefSetFilter
	 *     Disjunction.Disjunction_1_0 returns LanguageRefSetFilter
	 *     Conjunction returns LanguageRefSetFilter
	 *     Conjunction.Conjunction_1_0 returns LanguageRefSetFilter
	 *     Exclusion returns LanguageRefSetFilter
	 *     Exclusion.Exclusion_1_0 returns LanguageRefSetFilter
	 *     PropertyFilter returns LanguageRefSetFilter
	 *     LanguageRefSetFilter returns LanguageRefSetFilter
	 *
	 * Constraint:
	 *     languageRefSetId=ExpressionConstraint
	 */
	protected void sequence_LanguageRefSetFilter(ISerializationContext context, LanguageRefSetFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.LANGUAGE_REF_SET_FILTER__LANGUAGE_REF_SET_ID) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.LANGUAGE_REF_SET_FILTER__LANGUAGE_REF_SET_ID));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getLanguageRefSetFilterAccess().getLanguageRefSetIdExpressionConstraintParserRuleCall_2_0(), semanticObject.getLanguageRefSetId());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns ModuleFilter
	 *     Disjunction returns ModuleFilter
	 *     Disjunction.Disjunction_1_0 returns ModuleFilter
	 *     Conjunction returns ModuleFilter
	 *     Conjunction.Conjunction_1_0 returns ModuleFilter
	 *     Exclusion returns ModuleFilter
	 *     Exclusion.Exclusion_1_0 returns ModuleFilter
	 *     PropertyFilter returns ModuleFilter
	 *     ModuleFilter returns ModuleFilter
	 *
	 * Constraint:
	 *     (domain=Domain? moduleId=ExpressionConstraint)
	 */
	protected void sequence_ModuleFilter(ISerializationContext context, ModuleFilter semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns NestedFilter
	 *     Disjunction returns NestedFilter
	 *     Disjunction.Disjunction_1_0 returns NestedFilter
	 *     Conjunction returns NestedFilter
	 *     Conjunction.Conjunction_1_0 returns NestedFilter
	 *     Exclusion returns NestedFilter
	 *     Exclusion.Exclusion_1_0 returns NestedFilter
	 *     NestedFilter returns NestedFilter
	 *     PropertyFilter returns NestedFilter
	 *
	 * Constraint:
	 *     nested=Filter
	 */
	protected void sequence_NestedFilter(ISerializationContext context, NestedFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.NESTED_FILTER__NESTED) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.NESTED_FILTER__NESTED));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getNestedFilterAccess().getNestedFilterParserRuleCall_1_0(), semanticObject.getNested());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     QueryConstraint returns NestedQuery
	 *     QueryDisjunction returns NestedQuery
	 *     QueryDisjunction.QueryDisjunction_1_0 returns NestedQuery
	 *     QueryConjunction returns NestedQuery
	 *     QueryConjunction.QueryConjunction_1_0 returns NestedQuery
	 *     QueryExclusion returns NestedQuery
	 *     QueryExclusion.QueryExclusion_1_0 returns NestedQuery
	 *     SubQuery returns NestedQuery
	 *     NestedQuery returns NestedQuery
	 *
	 * Constraint:
	 *     nested=QueryConstraint
	 */
	protected void sequence_NestedQuery(ISerializationContext context, NestedQuery semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.NESTED_QUERY__NESTED) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.NESTED_QUERY__NESTED));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getNestedQueryAccess().getNestedQueryConstraintParserRuleCall_1_0(), semanticObject.getNested());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns PreferredInFilter
	 *     Disjunction returns PreferredInFilter
	 *     Disjunction.Disjunction_1_0 returns PreferredInFilter
	 *     Conjunction returns PreferredInFilter
	 *     Conjunction.Conjunction_1_0 returns PreferredInFilter
	 *     Exclusion returns PreferredInFilter
	 *     Exclusion.Exclusion_1_0 returns PreferredInFilter
	 *     PropertyFilter returns PreferredInFilter
	 *     PreferredInFilter returns PreferredInFilter
	 *
	 * Constraint:
	 *     languageRefSetId=ExpressionConstraint
	 */
	protected void sequence_PreferredInFilter(ISerializationContext context, PreferredInFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getPreferredInFilterAccess().getLanguageRefSetIdExpressionConstraintParserRuleCall_2_0(), semanticObject.getLanguageRefSetId());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     QueryConstraint returns QueryConjunction
	 *     QueryDisjunction returns QueryConjunction
	 *     QueryDisjunction.QueryDisjunction_1_0 returns QueryConjunction
	 *     QueryConjunction returns QueryConjunction
	 *     QueryConjunction.QueryConjunction_1_0 returns QueryConjunction
	 *
	 * Constraint:
	 *     (left=QueryConjunction_QueryConjunction_1_0 right=QueryExclusion)
	 */
	protected void sequence_QueryConjunction(ISerializationContext context, QueryConjunction semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.QUERY_CONJUNCTION__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.QUERY_CONJUNCTION__LEFT));
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.QUERY_CONJUNCTION__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.QUERY_CONJUNCTION__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getQueryConjunctionAccess().getQueryConjunctionLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getQueryConjunctionAccess().getRightQueryExclusionParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     QueryConstraint returns QueryDisjunction
	 *     QueryDisjunction returns QueryDisjunction
	 *     QueryDisjunction.QueryDisjunction_1_0 returns QueryDisjunction
	 *
	 * Constraint:
	 *     (left=QueryDisjunction_QueryDisjunction_1_0 right=QueryConjunction)
	 */
	protected void sequence_QueryDisjunction(ISerializationContext context, QueryDisjunction semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.QUERY_DISJUNCTION__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.QUERY_DISJUNCTION__LEFT));
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.QUERY_DISJUNCTION__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.QUERY_DISJUNCTION__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getQueryDisjunctionAccess().getQueryDisjunctionLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getQueryDisjunctionAccess().getRightQueryConjunctionParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     QueryConstraint returns QueryExclusion
	 *     QueryDisjunction returns QueryExclusion
	 *     QueryDisjunction.QueryDisjunction_1_0 returns QueryExclusion
	 *     QueryConjunction returns QueryExclusion
	 *     QueryConjunction.QueryConjunction_1_0 returns QueryExclusion
	 *     QueryExclusion returns QueryExclusion
	 *
	 * Constraint:
	 *     (left=QueryExclusion_QueryExclusion_1_0 right=SubQuery)
	 */
	protected void sequence_QueryExclusion(ISerializationContext context, QueryExclusion semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.QUERY_EXCLUSION__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.QUERY_EXCLUSION__LEFT));
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.QUERY_EXCLUSION__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.QUERY_EXCLUSION__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getQueryExclusionAccess().getQueryExclusionLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getQueryExclusionAccess().getRightSubQueryParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Query returns Query
	 *
	 * Constraint:
	 *     query=QueryConstraint?
	 */
	protected void sequence_Query(ISerializationContext context, Query semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns TermFilter
	 *     Disjunction returns TermFilter
	 *     Disjunction.Disjunction_1_0 returns TermFilter
	 *     Conjunction returns TermFilter
	 *     Conjunction.Conjunction_1_0 returns TermFilter
	 *     Exclusion returns TermFilter
	 *     Exclusion.Exclusion_1_0 returns TermFilter
	 *     PropertyFilter returns TermFilter
	 *     TermFilter returns TermFilter
	 *
	 * Constraint:
	 *     (lexicalSearchType=LexicalSearchType? term=STRING)
	 */
	protected void sequence_TermFilter(ISerializationContext context, TermFilter semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns TypeFilter
	 *     Disjunction returns TypeFilter
	 *     Disjunction.Disjunction_1_0 returns TypeFilter
	 *     Conjunction returns TypeFilter
	 *     Conjunction.Conjunction_1_0 returns TypeFilter
	 *     Exclusion returns TypeFilter
	 *     Exclusion.Exclusion_1_0 returns TypeFilter
	 *     PropertyFilter returns TypeFilter
	 *     TypeFilter returns TypeFilter
	 *
	 * Constraint:
	 *     type=ExpressionConstraint
	 */
	protected void sequence_TypeFilter(ISerializationContext context, TypeFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.TYPE_FILTER__TYPE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.TYPE_FILTER__TYPE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getTypeFilterAccess().getTypeExpressionConstraintParserRuleCall_2_0(), semanticObject.getType());
		feeder.finish();
	}
	
	
}
