/**
 */
package com.b2international.snowowl.snomed.ecl.ecl.impl;

import com.b2international.snowowl.snomed.ecl.ecl.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EclFactoryImpl extends EFactoryImpl implements EclFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static EclFactory init()
  {
    try
    {
      EclFactory theEclFactory = (EclFactory)EPackage.Registry.INSTANCE.getEFactory(EclPackage.eNS_URI);
      if (theEclFactory != null)
      {
        return theEclFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new EclFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EclFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case EclPackage.EXPRESSION_CONSTRAINT: return createExpressionConstraint();
      case EclPackage.DESCENDANT_OF: return createDescendantOf();
      case EclPackage.DESCENDANT_OR_SELF_OF: return createDescendantOrSelfOf();
      case EclPackage.MEMBER_OF: return createMemberOf();
      case EclPackage.CONCEPT_REFERENCE: return createConceptReference();
      case EclPackage.ANY: return createAny();
      case EclPackage.NESTABLE_EXPRESSION: return createNestableExpression();
      case EclPackage.OR_EXPRESSION_CONSTRAINT: return createOrExpressionConstraint();
      case EclPackage.AND_EXPRESSION_CONSTRAINT: return createAndExpressionConstraint();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionConstraint createExpressionConstraint()
  {
    ExpressionConstraintImpl expressionConstraint = new ExpressionConstraintImpl();
    return expressionConstraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DescendantOf createDescendantOf()
  {
    DescendantOfImpl descendantOf = new DescendantOfImpl();
    return descendantOf;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DescendantOrSelfOf createDescendantOrSelfOf()
  {
    DescendantOrSelfOfImpl descendantOrSelfOf = new DescendantOrSelfOfImpl();
    return descendantOrSelfOf;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MemberOf createMemberOf()
  {
    MemberOfImpl memberOf = new MemberOfImpl();
    return memberOf;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConceptReference createConceptReference()
  {
    ConceptReferenceImpl conceptReference = new ConceptReferenceImpl();
    return conceptReference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Any createAny()
  {
    AnyImpl any = new AnyImpl();
    return any;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NestableExpression createNestableExpression()
  {
    NestableExpressionImpl nestableExpression = new NestableExpressionImpl();
    return nestableExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrExpressionConstraint createOrExpressionConstraint()
  {
    OrExpressionConstraintImpl orExpressionConstraint = new OrExpressionConstraintImpl();
    return orExpressionConstraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AndExpressionConstraint createAndExpressionConstraint()
  {
    AndExpressionConstraintImpl andExpressionConstraint = new AndExpressionConstraintImpl();
    return andExpressionConstraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EclPackage getEclPackage()
  {
    return (EclPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static EclPackage getPackage()
  {
    return EclPackage.eINSTANCE;
  }

} //EclFactoryImpl
