/**
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.ecl.ecl.util;

import com.b2international.snowowl.snomed.ecl.ecl.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage
 * @generated
 */
public class EclSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static EclPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EclSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = EclPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
      case EclPackage.SCRIPT:
      {
        Script script = (Script)theEObject;
        T result = caseScript(script);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.EXPRESSION_CONSTRAINT:
      {
        ExpressionConstraint expressionConstraint = (ExpressionConstraint)theEObject;
        T result = caseExpressionConstraint(expressionConstraint);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.CHILD_OF:
      {
        ChildOf childOf = (ChildOf)theEObject;
        T result = caseChildOf(childOf);
        if (result == null) result = caseExpressionConstraint(childOf);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.DESCENDANT_OF:
      {
        DescendantOf descendantOf = (DescendantOf)theEObject;
        T result = caseDescendantOf(descendantOf);
        if (result == null) result = caseExpressionConstraint(descendantOf);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.DESCENDANT_OR_SELF_OF:
      {
        DescendantOrSelfOf descendantOrSelfOf = (DescendantOrSelfOf)theEObject;
        T result = caseDescendantOrSelfOf(descendantOrSelfOf);
        if (result == null) result = caseExpressionConstraint(descendantOrSelfOf);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.PARENT_OF:
      {
        ParentOf parentOf = (ParentOf)theEObject;
        T result = caseParentOf(parentOf);
        if (result == null) result = caseExpressionConstraint(parentOf);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.ANCESTOR_OF:
      {
        AncestorOf ancestorOf = (AncestorOf)theEObject;
        T result = caseAncestorOf(ancestorOf);
        if (result == null) result = caseExpressionConstraint(ancestorOf);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.ANCESTOR_OR_SELF_OF:
      {
        AncestorOrSelfOf ancestorOrSelfOf = (AncestorOrSelfOf)theEObject;
        T result = caseAncestorOrSelfOf(ancestorOrSelfOf);
        if (result == null) result = caseExpressionConstraint(ancestorOrSelfOf);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.MEMBER_OF:
      {
        MemberOf memberOf = (MemberOf)theEObject;
        T result = caseMemberOf(memberOf);
        if (result == null) result = caseExpressionConstraint(memberOf);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.CONCEPT_REFERENCE:
      {
        ConceptReference conceptReference = (ConceptReference)theEObject;
        T result = caseConceptReference(conceptReference);
        if (result == null) result = caseExpressionConstraint(conceptReference);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.ANY:
      {
        Any any = (Any)theEObject;
        T result = caseAny(any);
        if (result == null) result = caseExpressionConstraint(any);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.REFINEMENT:
      {
        Refinement refinement = (Refinement)theEObject;
        T result = caseRefinement(refinement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.NESTED_REFINEMENT:
      {
        NestedRefinement nestedRefinement = (NestedRefinement)theEObject;
        T result = caseNestedRefinement(nestedRefinement);
        if (result == null) result = caseRefinement(nestedRefinement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.ATTRIBUTE_GROUP:
      {
        AttributeGroup attributeGroup = (AttributeGroup)theEObject;
        T result = caseAttributeGroup(attributeGroup);
        if (result == null) result = caseRefinement(attributeGroup);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.ATTRIBUTE_CONSTRAINT:
      {
        AttributeConstraint attributeConstraint = (AttributeConstraint)theEObject;
        T result = caseAttributeConstraint(attributeConstraint);
        if (result == null) result = caseRefinement(attributeConstraint);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.CARDINALITY:
      {
        Cardinality cardinality = (Cardinality)theEObject;
        T result = caseCardinality(cardinality);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.COMPARISON:
      {
        Comparison comparison = (Comparison)theEObject;
        T result = caseComparison(comparison);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.ATTRIBUTE_COMPARISON:
      {
        AttributeComparison attributeComparison = (AttributeComparison)theEObject;
        T result = caseAttributeComparison(attributeComparison);
        if (result == null) result = caseComparison(attributeComparison);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.DATA_TYPE_COMPARISON:
      {
        DataTypeComparison dataTypeComparison = (DataTypeComparison)theEObject;
        T result = caseDataTypeComparison(dataTypeComparison);
        if (result == null) result = caseComparison(dataTypeComparison);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.ATTRIBUTE_VALUE_EQUALS:
      {
        AttributeValueEquals attributeValueEquals = (AttributeValueEquals)theEObject;
        T result = caseAttributeValueEquals(attributeValueEquals);
        if (result == null) result = caseAttributeComparison(attributeValueEquals);
        if (result == null) result = caseComparison(attributeValueEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.ATTRIBUTE_VALUE_NOT_EQUALS:
      {
        AttributeValueNotEquals attributeValueNotEquals = (AttributeValueNotEquals)theEObject;
        T result = caseAttributeValueNotEquals(attributeValueNotEquals);
        if (result == null) result = caseAttributeComparison(attributeValueNotEquals);
        if (result == null) result = caseComparison(attributeValueNotEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.STRING_VALUE_EQUALS:
      {
        StringValueEquals stringValueEquals = (StringValueEquals)theEObject;
        T result = caseStringValueEquals(stringValueEquals);
        if (result == null) result = caseDataTypeComparison(stringValueEquals);
        if (result == null) result = caseComparison(stringValueEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.STRING_VALUE_NOT_EQUALS:
      {
        StringValueNotEquals stringValueNotEquals = (StringValueNotEquals)theEObject;
        T result = caseStringValueNotEquals(stringValueNotEquals);
        if (result == null) result = caseDataTypeComparison(stringValueNotEquals);
        if (result == null) result = caseComparison(stringValueNotEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.INTEGER_VALUE_EQUALS:
      {
        IntegerValueEquals integerValueEquals = (IntegerValueEquals)theEObject;
        T result = caseIntegerValueEquals(integerValueEquals);
        if (result == null) result = caseDataTypeComparison(integerValueEquals);
        if (result == null) result = caseComparison(integerValueEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.INTEGER_VALUE_NOT_EQUALS:
      {
        IntegerValueNotEquals integerValueNotEquals = (IntegerValueNotEquals)theEObject;
        T result = caseIntegerValueNotEquals(integerValueNotEquals);
        if (result == null) result = caseDataTypeComparison(integerValueNotEquals);
        if (result == null) result = caseComparison(integerValueNotEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.INTEGER_VALUE_GREATER_THAN:
      {
        IntegerValueGreaterThan integerValueGreaterThan = (IntegerValueGreaterThan)theEObject;
        T result = caseIntegerValueGreaterThan(integerValueGreaterThan);
        if (result == null) result = caseDataTypeComparison(integerValueGreaterThan);
        if (result == null) result = caseComparison(integerValueGreaterThan);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.INTEGER_VALUE_LESS_THAN:
      {
        IntegerValueLessThan integerValueLessThan = (IntegerValueLessThan)theEObject;
        T result = caseIntegerValueLessThan(integerValueLessThan);
        if (result == null) result = caseDataTypeComparison(integerValueLessThan);
        if (result == null) result = caseComparison(integerValueLessThan);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.INTEGER_VALUE_GREATER_THAN_EQUALS:
      {
        IntegerValueGreaterThanEquals integerValueGreaterThanEquals = (IntegerValueGreaterThanEquals)theEObject;
        T result = caseIntegerValueGreaterThanEquals(integerValueGreaterThanEquals);
        if (result == null) result = caseDataTypeComparison(integerValueGreaterThanEquals);
        if (result == null) result = caseComparison(integerValueGreaterThanEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.INTEGER_VALUE_LESS_THAN_EQUALS:
      {
        IntegerValueLessThanEquals integerValueLessThanEquals = (IntegerValueLessThanEquals)theEObject;
        T result = caseIntegerValueLessThanEquals(integerValueLessThanEquals);
        if (result == null) result = caseDataTypeComparison(integerValueLessThanEquals);
        if (result == null) result = caseComparison(integerValueLessThanEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.DECIMAL_VALUE_EQUALS:
      {
        DecimalValueEquals decimalValueEquals = (DecimalValueEquals)theEObject;
        T result = caseDecimalValueEquals(decimalValueEquals);
        if (result == null) result = caseDataTypeComparison(decimalValueEquals);
        if (result == null) result = caseComparison(decimalValueEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.DECIMAL_VALUE_NOT_EQUALS:
      {
        DecimalValueNotEquals decimalValueNotEquals = (DecimalValueNotEquals)theEObject;
        T result = caseDecimalValueNotEquals(decimalValueNotEquals);
        if (result == null) result = caseDataTypeComparison(decimalValueNotEquals);
        if (result == null) result = caseComparison(decimalValueNotEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.DECIMAL_VALUE_GREATER_THAN:
      {
        DecimalValueGreaterThan decimalValueGreaterThan = (DecimalValueGreaterThan)theEObject;
        T result = caseDecimalValueGreaterThan(decimalValueGreaterThan);
        if (result == null) result = caseDataTypeComparison(decimalValueGreaterThan);
        if (result == null) result = caseComparison(decimalValueGreaterThan);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.DECIMAL_VALUE_LESS_THAN:
      {
        DecimalValueLessThan decimalValueLessThan = (DecimalValueLessThan)theEObject;
        T result = caseDecimalValueLessThan(decimalValueLessThan);
        if (result == null) result = caseDataTypeComparison(decimalValueLessThan);
        if (result == null) result = caseComparison(decimalValueLessThan);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.DECIMAL_VALUE_GREATER_THAN_EQUALS:
      {
        DecimalValueGreaterThanEquals decimalValueGreaterThanEquals = (DecimalValueGreaterThanEquals)theEObject;
        T result = caseDecimalValueGreaterThanEquals(decimalValueGreaterThanEquals);
        if (result == null) result = caseDataTypeComparison(decimalValueGreaterThanEquals);
        if (result == null) result = caseComparison(decimalValueGreaterThanEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.DECIMAL_VALUE_LESS_THAN_EQUALS:
      {
        DecimalValueLessThanEquals decimalValueLessThanEquals = (DecimalValueLessThanEquals)theEObject;
        T result = caseDecimalValueLessThanEquals(decimalValueLessThanEquals);
        if (result == null) result = caseDataTypeComparison(decimalValueLessThanEquals);
        if (result == null) result = caseComparison(decimalValueLessThanEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.NESTED_EXPRESSION:
      {
        NestedExpression nestedExpression = (NestedExpression)theEObject;
        T result = caseNestedExpression(nestedExpression);
        if (result == null) result = caseExpressionConstraint(nestedExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.OR_EXPRESSION_CONSTRAINT:
      {
        OrExpressionConstraint orExpressionConstraint = (OrExpressionConstraint)theEObject;
        T result = caseOrExpressionConstraint(orExpressionConstraint);
        if (result == null) result = caseExpressionConstraint(orExpressionConstraint);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.AND_EXPRESSION_CONSTRAINT:
      {
        AndExpressionConstraint andExpressionConstraint = (AndExpressionConstraint)theEObject;
        T result = caseAndExpressionConstraint(andExpressionConstraint);
        if (result == null) result = caseExpressionConstraint(andExpressionConstraint);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.EXCLUSION_EXPRESSION_CONSTRAINT:
      {
        ExclusionExpressionConstraint exclusionExpressionConstraint = (ExclusionExpressionConstraint)theEObject;
        T result = caseExclusionExpressionConstraint(exclusionExpressionConstraint);
        if (result == null) result = caseExpressionConstraint(exclusionExpressionConstraint);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.REFINED_EXPRESSION_CONSTRAINT:
      {
        RefinedExpressionConstraint refinedExpressionConstraint = (RefinedExpressionConstraint)theEObject;
        T result = caseRefinedExpressionConstraint(refinedExpressionConstraint);
        if (result == null) result = caseExpressionConstraint(refinedExpressionConstraint);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.DOTTED_EXPRESSION_CONSTRAINT:
      {
        DottedExpressionConstraint dottedExpressionConstraint = (DottedExpressionConstraint)theEObject;
        T result = caseDottedExpressionConstraint(dottedExpressionConstraint);
        if (result == null) result = caseExpressionConstraint(dottedExpressionConstraint);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.OR_REFINEMENT:
      {
        OrRefinement orRefinement = (OrRefinement)theEObject;
        T result = caseOrRefinement(orRefinement);
        if (result == null) result = caseRefinement(orRefinement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.AND_REFINEMENT:
      {
        AndRefinement andRefinement = (AndRefinement)theEObject;
        T result = caseAndRefinement(andRefinement);
        if (result == null) result = caseRefinement(andRefinement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Script</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Script</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseScript(Script object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Expression Constraint</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExpressionConstraint(ExpressionConstraint object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Child Of</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Child Of</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseChildOf(ChildOf object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Descendant Of</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Descendant Of</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDescendantOf(DescendantOf object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Descendant Or Self Of</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Descendant Or Self Of</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDescendantOrSelfOf(DescendantOrSelfOf object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Parent Of</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Parent Of</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseParentOf(ParentOf object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ancestor Of</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ancestor Of</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAncestorOf(AncestorOf object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ancestor Or Self Of</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ancestor Or Self Of</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAncestorOrSelfOf(AncestorOrSelfOf object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Member Of</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Member Of</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMemberOf(MemberOf object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Concept Reference</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Concept Reference</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConceptReference(ConceptReference object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Any</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Any</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAny(Any object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Refinement</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Refinement</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefinement(Refinement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Nested Refinement</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Nested Refinement</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNestedRefinement(NestedRefinement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute Group</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute Group</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttributeGroup(AttributeGroup object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute Constraint</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute Constraint</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttributeConstraint(AttributeConstraint object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Cardinality</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Cardinality</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCardinality(Cardinality object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Comparison</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Comparison</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseComparison(Comparison object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute Comparison</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute Comparison</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttributeComparison(AttributeComparison object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Data Type Comparison</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Data Type Comparison</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDataTypeComparison(DataTypeComparison object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute Value Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute Value Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttributeValueEquals(AttributeValueEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute Value Not Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute Value Not Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttributeValueNotEquals(AttributeValueNotEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>String Value Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>String Value Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStringValueEquals(StringValueEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>String Value Not Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>String Value Not Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStringValueNotEquals(StringValueNotEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Integer Value Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Integer Value Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntegerValueEquals(IntegerValueEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Integer Value Not Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Integer Value Not Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntegerValueNotEquals(IntegerValueNotEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Integer Value Greater Than</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Integer Value Greater Than</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntegerValueGreaterThan(IntegerValueGreaterThan object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Integer Value Less Than</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Integer Value Less Than</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntegerValueLessThan(IntegerValueLessThan object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Integer Value Greater Than Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Integer Value Greater Than Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntegerValueGreaterThanEquals(IntegerValueGreaterThanEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Integer Value Less Than Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Integer Value Less Than Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntegerValueLessThanEquals(IntegerValueLessThanEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Decimal Value Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Decimal Value Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDecimalValueEquals(DecimalValueEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Decimal Value Not Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Decimal Value Not Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDecimalValueNotEquals(DecimalValueNotEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Decimal Value Greater Than</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Decimal Value Greater Than</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDecimalValueGreaterThan(DecimalValueGreaterThan object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Decimal Value Less Than</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Decimal Value Less Than</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDecimalValueLessThan(DecimalValueLessThan object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Decimal Value Greater Than Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Decimal Value Greater Than Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDecimalValueGreaterThanEquals(DecimalValueGreaterThanEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Decimal Value Less Than Equals</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Decimal Value Less Than Equals</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDecimalValueLessThanEquals(DecimalValueLessThanEquals object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Nested Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Nested Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNestedExpression(NestedExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Or Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Or Expression Constraint</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOrExpressionConstraint(OrExpressionConstraint object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>And Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>And Expression Constraint</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAndExpressionConstraint(AndExpressionConstraint object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Exclusion Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Exclusion Expression Constraint</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExclusionExpressionConstraint(ExclusionExpressionConstraint object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Refined Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Refined Expression Constraint</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefinedExpressionConstraint(RefinedExpressionConstraint object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Dotted Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Dotted Expression Constraint</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDottedExpressionConstraint(DottedExpressionConstraint object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Or Refinement</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Or Refinement</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOrRefinement(OrRefinement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>And Refinement</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>And Refinement</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAndRefinement(AndRefinement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} //EclSwitch
