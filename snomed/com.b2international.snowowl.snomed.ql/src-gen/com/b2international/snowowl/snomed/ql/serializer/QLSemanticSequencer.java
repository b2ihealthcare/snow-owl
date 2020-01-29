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
package com.b2international.snowowl.snomed.ql.serializer;

import com.b2international.snowowl.snomed.ecl.ecl.AncestorOf;
import com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AndRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.Any;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.BooleanValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.BooleanValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.Cardinality;
import com.b2international.snowowl.snomed.ecl.ecl.ChildOf;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThan;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThanEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThan;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThanEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOf;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.EclAttributeGroup;
import com.b2international.snowowl.snomed.ecl.ecl.EclConceptReference;
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
import com.b2international.snowowl.snomed.ql.ql.CaseSignificanceFilter;
import com.b2international.snowowl.snomed.ql.ql.ConjunctionFilter;
import com.b2international.snowowl.snomed.ql.ql.DisjunctionFilter;
import com.b2international.snowowl.snomed.ql.ql.DomainQuery;
import com.b2international.snowowl.snomed.ql.ql.ExclusionFilter;
import com.b2international.snowowl.snomed.ql.ql.LanguageCodeFilter;
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
		else if (epackage == QlPackage.eINSTANCE)
			switch (semanticObject.eClass().getClassifierID()) {
			case QlPackage.ACCEPTABLE_IN_FILTER:
				sequence_AcceptableInFilter(context, (AcceptableInFilter) semanticObject); 
				return; 
			case QlPackage.ACTIVE_FILTER:
				sequence_ActiveFilter(context, (ActiveFilter) semanticObject); 
				return; 
			case QlPackage.CASE_SIGNIFICANCE_FILTER:
				sequence_CaseSignificanceFilter(context, (CaseSignificanceFilter) semanticObject); 
				return; 
			case QlPackage.CONJUNCTION_FILTER:
				sequence_ConjunctionFilter(context, (ConjunctionFilter) semanticObject); 
				return; 
			case QlPackage.DISJUNCTION_FILTER:
				sequence_DisjunctionFilter(context, (DisjunctionFilter) semanticObject); 
				return; 
			case QlPackage.DOMAIN_QUERY:
				sequence_DomainQuery(context, (DomainQuery) semanticObject); 
				return; 
			case QlPackage.EXCLUSION_FILTER:
				sequence_ExclusionFilter(context, (ExclusionFilter) semanticObject); 
				return; 
			case QlPackage.LANGUAGE_CODE_FILTER:
				sequence_LanguageCodeFilter(context, (LanguageCodeFilter) semanticObject); 
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
	 *     DisjunctionFilter returns AcceptableInFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns AcceptableInFilter
	 *     ConjunctionFilter returns AcceptableInFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns AcceptableInFilter
	 *     ExclusionFilter returns AcceptableInFilter
	 *     ExclusionFilter.ExclusionFilter_1_0 returns AcceptableInFilter
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
	 *     DisjunctionFilter returns ActiveFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns ActiveFilter
	 *     ConjunctionFilter returns ActiveFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns ActiveFilter
	 *     ExclusionFilter returns ActiveFilter
	 *     ExclusionFilter.ExclusionFilter_1_0 returns ActiveFilter
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
	 *     Filter returns CaseSignificanceFilter
	 *     DisjunctionFilter returns CaseSignificanceFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns CaseSignificanceFilter
	 *     ConjunctionFilter returns CaseSignificanceFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns CaseSignificanceFilter
	 *     ExclusionFilter returns CaseSignificanceFilter
	 *     ExclusionFilter.ExclusionFilter_1_0 returns CaseSignificanceFilter
	 *     PropertyFilter returns CaseSignificanceFilter
	 *     CaseSignificanceFilter returns CaseSignificanceFilter
	 *
	 * Constraint:
	 *     caseSignificanceId=ExpressionConstraint
	 */
	protected void sequence_CaseSignificanceFilter(ISerializationContext context, CaseSignificanceFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.CASE_SIGNIFICANCE_FILTER__CASE_SIGNIFICANCE_ID) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.CASE_SIGNIFICANCE_FILTER__CASE_SIGNIFICANCE_ID));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getCaseSignificanceFilterAccess().getCaseSignificanceIdExpressionConstraintParserRuleCall_2_0(), semanticObject.getCaseSignificanceId());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns ConjunctionFilter
	 *     DisjunctionFilter returns ConjunctionFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns ConjunctionFilter
	 *     ConjunctionFilter returns ConjunctionFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns ConjunctionFilter
	 *
	 * Constraint:
	 *     (left=ConjunctionFilter_ConjunctionFilter_1_0 right=ExclusionFilter)
	 */
	protected void sequence_ConjunctionFilter(ISerializationContext context, ConjunctionFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.CONJUNCTION_FILTER__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.CONJUNCTION_FILTER__LEFT));
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.CONJUNCTION_FILTER__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.CONJUNCTION_FILTER__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getConjunctionFilterAccess().getConjunctionFilterLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getConjunctionFilterAccess().getRightExclusionFilterParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns DisjunctionFilter
	 *     DisjunctionFilter returns DisjunctionFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns DisjunctionFilter
	 *
	 * Constraint:
	 *     (left=DisjunctionFilter_DisjunctionFilter_1_0 right=ConjunctionFilter)
	 */
	protected void sequence_DisjunctionFilter(ISerializationContext context, DisjunctionFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.DISJUNCTION_FILTER__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.DISJUNCTION_FILTER__LEFT));
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.DISJUNCTION_FILTER__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.DISJUNCTION_FILTER__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getDisjunctionFilterAccess().getDisjunctionFilterLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getDisjunctionFilterAccess().getRightConjunctionFilterParserRuleCall_1_2_0(), semanticObject.getRight());
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
	 *     Filter returns ExclusionFilter
	 *     DisjunctionFilter returns ExclusionFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns ExclusionFilter
	 *     ConjunctionFilter returns ExclusionFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns ExclusionFilter
	 *     ExclusionFilter returns ExclusionFilter
	 *
	 * Constraint:
	 *     (left=ExclusionFilter_ExclusionFilter_1_0 right=PropertyFilter)
	 */
	protected void sequence_ExclusionFilter(ISerializationContext context, ExclusionFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.EXCLUSION_FILTER__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.EXCLUSION_FILTER__LEFT));
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.EXCLUSION_FILTER__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.EXCLUSION_FILTER__RIGHT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getExclusionFilterAccess().getExclusionFilterLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getExclusionFilterAccess().getRightPropertyFilterParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns LanguageCodeFilter
	 *     DisjunctionFilter returns LanguageCodeFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns LanguageCodeFilter
	 *     ConjunctionFilter returns LanguageCodeFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns LanguageCodeFilter
	 *     ExclusionFilter returns LanguageCodeFilter
	 *     ExclusionFilter.ExclusionFilter_1_0 returns LanguageCodeFilter
	 *     PropertyFilter returns LanguageCodeFilter
	 *     LanguageCodeFilter returns LanguageCodeFilter
	 *
	 * Constraint:
	 *     languageCode=STRING
	 */
	protected void sequence_LanguageCodeFilter(ISerializationContext context, LanguageCodeFilter semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, QlPackage.Literals.LANGUAGE_CODE_FILTER__LANGUAGE_CODE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, QlPackage.Literals.LANGUAGE_CODE_FILTER__LANGUAGE_CODE));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getLanguageCodeFilterAccess().getLanguageCodeSTRINGTerminalRuleCall_2_0(), semanticObject.getLanguageCode());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Filter returns LanguageRefSetFilter
	 *     DisjunctionFilter returns LanguageRefSetFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns LanguageRefSetFilter
	 *     ConjunctionFilter returns LanguageRefSetFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns LanguageRefSetFilter
	 *     ExclusionFilter returns LanguageRefSetFilter
	 *     ExclusionFilter.ExclusionFilter_1_0 returns LanguageRefSetFilter
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
	 *     DisjunctionFilter returns ModuleFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns ModuleFilter
	 *     ConjunctionFilter returns ModuleFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns ModuleFilter
	 *     ExclusionFilter returns ModuleFilter
	 *     ExclusionFilter.ExclusionFilter_1_0 returns ModuleFilter
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
	 *     DisjunctionFilter returns NestedFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns NestedFilter
	 *     ConjunctionFilter returns NestedFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns NestedFilter
	 *     ExclusionFilter returns NestedFilter
	 *     ExclusionFilter.ExclusionFilter_1_0 returns NestedFilter
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
	 *     DisjunctionFilter returns PreferredInFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns PreferredInFilter
	 *     ConjunctionFilter returns PreferredInFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns PreferredInFilter
	 *     ExclusionFilter returns PreferredInFilter
	 *     ExclusionFilter.ExclusionFilter_1_0 returns PreferredInFilter
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
	 *     DisjunctionFilter returns TermFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns TermFilter
	 *     ConjunctionFilter returns TermFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns TermFilter
	 *     ExclusionFilter returns TermFilter
	 *     ExclusionFilter.ExclusionFilter_1_0 returns TermFilter
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
	 *     DisjunctionFilter returns TypeFilter
	 *     DisjunctionFilter.DisjunctionFilter_1_0 returns TypeFilter
	 *     ConjunctionFilter returns TypeFilter
	 *     ConjunctionFilter.ConjunctionFilter_1_0 returns TypeFilter
	 *     ExclusionFilter returns TypeFilter
	 *     ExclusionFilter.ExclusionFilter_1_0 returns TypeFilter
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
