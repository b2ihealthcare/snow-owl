/**
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
      case EclPackage.ATTRIBUTE_VALUE_EQUALS:
      {
        AttributeValueEquals attributeValueEquals = (AttributeValueEquals)theEObject;
        T result = caseAttributeValueEquals(attributeValueEquals);
        if (result == null) result = caseComparison(attributeValueEquals);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EclPackage.ATTRIBUTE_VALUE_NOT_EQUALS:
      {
        AttributeValueNotEquals attributeValueNotEquals = (AttributeValueNotEquals)theEObject;
        T result = caseAttributeValueNotEquals(attributeValueNotEquals);
        if (result == null) result = caseComparison(attributeValueNotEquals);
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
      default: return defaultCase(theEObject);
    }
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
