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
package com.b2international.snowowl.dsl.escg.util;

import com.b2international.snowowl.dsl.escg.*;

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
 * @see com.b2international.snowowl.dsl.escg.EscgPackage
 * @generated
 */
public class EscgSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static EscgPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EscgSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = EscgPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @parameter ePackage the package in question.
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
      case EscgPackage.EXPRESSION:
      {
        Expression expression = (Expression)theEObject;
        T result = caseExpression(expression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.SUB_EXPRESSION:
      {
        SubExpression subExpression = (SubExpression)theEObject;
        T result = caseSubExpression(subExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.LVALUE:
      {
        LValue lValue = (LValue)theEObject;
        T result = caseLValue(lValue);
        if (result == null) result = caseRValue(lValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.REF_SET:
      {
        RefSet refSet = (RefSet)theEObject;
        T result = caseRefSet(refSet);
        if (result == null) result = caseLValue(refSet);
        if (result == null) result = caseRValue(refSet);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.CONCEPT_GROUP:
      {
        ConceptGroup conceptGroup = (ConceptGroup)theEObject;
        T result = caseConceptGroup(conceptGroup);
        if (result == null) result = caseLValue(conceptGroup);
        if (result == null) result = caseRValue(conceptGroup);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.CONCEPT:
      {
        Concept concept = (Concept)theEObject;
        T result = caseConcept(concept);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.REFINEMENTS:
      {
        Refinements refinements = (Refinements)theEObject;
        T result = caseRefinements(refinements);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.ATTRIBUTE_GROUP:
      {
        AttributeGroup attributeGroup = (AttributeGroup)theEObject;
        T result = caseAttributeGroup(attributeGroup);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.ATTRIBUTE_SET:
      {
        AttributeSet attributeSet = (AttributeSet)theEObject;
        T result = caseAttributeSet(attributeSet);
        if (result == null) result = caseAttributeGroup(attributeSet);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.ATTRIBUTE:
      {
        Attribute attribute = (Attribute)theEObject;
        T result = caseAttribute(attribute);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.ATTRIBUTE_ASSIGNMENT:
      {
        AttributeAssignment attributeAssignment = (AttributeAssignment)theEObject;
        T result = caseAttributeAssignment(attributeAssignment);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.CONCEPT_ASSIGNMENT:
      {
        ConceptAssignment conceptAssignment = (ConceptAssignment)theEObject;
        T result = caseConceptAssignment(conceptAssignment);
        if (result == null) result = caseAttributeAssignment(conceptAssignment);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.NUMERICAL_ASSIGNMENT:
      {
        NumericalAssignment numericalAssignment = (NumericalAssignment)theEObject;
        T result = caseNumericalAssignment(numericalAssignment);
        if (result == null) result = caseAttributeAssignment(numericalAssignment);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.NUMERICAL_ASSIGNMENT_GROUP:
      {
        NumericalAssignmentGroup numericalAssignmentGroup = (NumericalAssignmentGroup)theEObject;
        T result = caseNumericalAssignmentGroup(numericalAssignmentGroup);
        if (result == null) result = caseAttributeAssignment(numericalAssignmentGroup);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.RVALUE:
      {
        RValue rValue = (RValue)theEObject;
        T result = caseRValue(rValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.NEGATABLE_SUB_EXPRESSION:
      {
        NegatableSubExpression negatableSubExpression = (NegatableSubExpression)theEObject;
        T result = caseNegatableSubExpression(negatableSubExpression);
        if (result == null) result = caseRValue(negatableSubExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.OR:
      {
        Or or = (Or)theEObject;
        T result = caseOr(or);
        if (result == null) result = caseRValue(or);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EscgPackage.AND:
      {
        And and = (And)theEObject;
        T result = caseAnd(and);
        if (result == null) result = caseRValue(and);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExpression(Expression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Sub Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Sub Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSubExpression(SubExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>LValue</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>LValue</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLValue(LValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ref Set</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ref Set</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefSet(RefSet object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Concept Group</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Concept Group</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConceptGroup(ConceptGroup object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Concept</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Concept</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConcept(Concept object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Refinements</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Refinements</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRefinements(Refinements object)
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
   * Returns the result of interpreting the object as an instance of '<em>Attribute Set</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute Set</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttributeSet(AttributeSet object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttribute(Attribute object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute Assignment</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute Assignment</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttributeAssignment(AttributeAssignment object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Concept Assignment</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Concept Assignment</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConceptAssignment(ConceptAssignment object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Numerical Assignment</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Numerical Assignment</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNumericalAssignment(NumericalAssignment object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Numerical Assignment Group</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Numerical Assignment Group</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNumericalAssignmentGroup(NumericalAssignmentGroup object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>RValue</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>RValue</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRValue(RValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Negatable Sub Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Negatable Sub Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNegatableSubExpression(NegatableSubExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Or</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Or</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOr(Or object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>And</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>And</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAnd(And object)
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

} //EscgSwitch