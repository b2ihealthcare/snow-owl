/**
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ecl.ecl.impl;

import com.b2international.snowowl.snomed.ecl.ecl.AncestorOf;
import com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AndRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.Any;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeComparison;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.Cardinality;
import com.b2international.snowowl.snomed.ecl.ecl.ChildOf;
import com.b2international.snowowl.snomed.ecl.ecl.Comparison;
import com.b2international.snowowl.snomed.ecl.ecl.ConceptReference;
import com.b2international.snowowl.snomed.ecl.ecl.DataTypeComparison;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThan;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThanEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThan;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThanEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DecimalValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOf;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.EclFactory;
import com.b2international.snowowl.snomed.ecl.ecl.EclPackage;
import com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
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
import com.b2international.snowowl.snomed.ecl.ecl.Refinement;
import com.b2international.snowowl.snomed.ecl.ecl.Script;
import com.b2international.snowowl.snomed.ecl.ecl.StringValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.StringValueNotEquals;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EclPackageImpl extends EPackageImpl implements EclPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass scriptEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass expressionConstraintEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass childOfEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass descendantOfEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass descendantOrSelfOfEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass parentOfEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass ancestorOfEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass ancestorOrSelfOfEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass memberOfEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conceptReferenceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass anyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass refinementEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass nestedRefinementEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeGroupEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeConstraintEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass cardinalityEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass comparisonEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeComparisonEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dataTypeComparisonEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeValueEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeValueNotEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass stringValueEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass stringValueNotEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerValueEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerValueNotEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerValueGreaterThanEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerValueLessThanEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerValueGreaterThanEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerValueLessThanEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalValueEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalValueNotEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalValueGreaterThanEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalValueLessThanEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalValueGreaterThanEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalValueLessThanEqualsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass nestedExpressionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass orExpressionConstraintEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass andExpressionConstraintEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass exclusionExpressionConstraintEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass refinedExpressionConstraintEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dottedExpressionConstraintEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass orRefinementEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass andRefinementEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private EclPackageImpl()
  {
    super(eNS_URI, EclFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link EclPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static EclPackage init()
  {
    if (isInited) return (EclPackage)EPackage.Registry.INSTANCE.getEPackage(EclPackage.eNS_URI);

    // Obtain or create and register package
    Object registeredEclPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    EclPackageImpl theEclPackage = registeredEclPackage instanceof EclPackageImpl ? (EclPackageImpl)registeredEclPackage : new EclPackageImpl();

    isInited = true;

    // Create package meta-data objects
    theEclPackage.createPackageContents();

    // Initialize created meta-data
    theEclPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theEclPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(EclPackage.eNS_URI, theEclPackage);
    return theEclPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getScript()
  {
    return scriptEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getScript_Constraint()
  {
    return (EReference)scriptEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExpressionConstraint()
  {
    return expressionConstraintEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getChildOf()
  {
    return childOfEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getChildOf_Constraint()
  {
    return (EReference)childOfEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDescendantOf()
  {
    return descendantOfEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDescendantOf_Constraint()
  {
    return (EReference)descendantOfEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDescendantOrSelfOf()
  {
    return descendantOrSelfOfEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDescendantOrSelfOf_Constraint()
  {
    return (EReference)descendantOrSelfOfEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getParentOf()
  {
    return parentOfEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getParentOf_Constraint()
  {
    return (EReference)parentOfEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAncestorOf()
  {
    return ancestorOfEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAncestorOf_Constraint()
  {
    return (EReference)ancestorOfEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAncestorOrSelfOf()
  {
    return ancestorOrSelfOfEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAncestorOrSelfOf_Constraint()
  {
    return (EReference)ancestorOrSelfOfEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMemberOf()
  {
    return memberOfEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMemberOf_Constraint()
  {
    return (EReference)memberOfEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConceptReference()
  {
    return conceptReferenceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getConceptReference_Id()
  {
    return (EAttribute)conceptReferenceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getConceptReference_Term()
  {
    return (EAttribute)conceptReferenceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAny()
  {
    return anyEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRefinement()
  {
    return refinementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNestedRefinement()
  {
    return nestedRefinementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getNestedRefinement_Nested()
  {
    return (EReference)nestedRefinementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeGroup()
  {
    return attributeGroupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttributeGroup_Cardinality()
  {
    return (EReference)attributeGroupEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttributeGroup_Refinement()
  {
    return (EReference)attributeGroupEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeConstraint()
  {
    return attributeConstraintEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttributeConstraint_Cardinality()
  {
    return (EReference)attributeConstraintEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAttributeConstraint_Reversed()
  {
    return (EAttribute)attributeConstraintEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttributeConstraint_Attribute()
  {
    return (EReference)attributeConstraintEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttributeConstraint_Comparison()
  {
    return (EReference)attributeConstraintEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCardinality()
  {
    return cardinalityEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCardinality_Min()
  {
    return (EAttribute)cardinalityEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCardinality_Max()
  {
    return (EAttribute)cardinalityEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getComparison()
  {
    return comparisonEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeComparison()
  {
    return attributeComparisonEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAttributeComparison_Constraint()
  {
    return (EReference)attributeComparisonEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDataTypeComparison()
  {
    return dataTypeComparisonEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeValueEquals()
  {
    return attributeValueEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAttributeValueNotEquals()
  {
    return attributeValueNotEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getStringValueEquals()
  {
    return stringValueEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getStringValueEquals_Value()
  {
    return (EAttribute)stringValueEqualsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getStringValueNotEquals()
  {
    return stringValueNotEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getStringValueNotEquals_Value()
  {
    return (EAttribute)stringValueNotEqualsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIntegerValueEquals()
  {
    return integerValueEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getIntegerValueEquals_Value()
  {
    return (EAttribute)integerValueEqualsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIntegerValueNotEquals()
  {
    return integerValueNotEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getIntegerValueNotEquals_Value()
  {
    return (EAttribute)integerValueNotEqualsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIntegerValueGreaterThan()
  {
    return integerValueGreaterThanEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getIntegerValueGreaterThan_Value()
  {
    return (EAttribute)integerValueGreaterThanEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIntegerValueLessThan()
  {
    return integerValueLessThanEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getIntegerValueLessThan_Value()
  {
    return (EAttribute)integerValueLessThanEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIntegerValueGreaterThanEquals()
  {
    return integerValueGreaterThanEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getIntegerValueGreaterThanEquals_Value()
  {
    return (EAttribute)integerValueGreaterThanEqualsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIntegerValueLessThanEquals()
  {
    return integerValueLessThanEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getIntegerValueLessThanEquals_Value()
  {
    return (EAttribute)integerValueLessThanEqualsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDecimalValueEquals()
  {
    return decimalValueEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecimalValueEquals_Value()
  {
    return (EAttribute)decimalValueEqualsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDecimalValueNotEquals()
  {
    return decimalValueNotEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecimalValueNotEquals_Value()
  {
    return (EAttribute)decimalValueNotEqualsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDecimalValueGreaterThan()
  {
    return decimalValueGreaterThanEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecimalValueGreaterThan_Value()
  {
    return (EAttribute)decimalValueGreaterThanEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDecimalValueLessThan()
  {
    return decimalValueLessThanEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecimalValueLessThan_Value()
  {
    return (EAttribute)decimalValueLessThanEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDecimalValueGreaterThanEquals()
  {
    return decimalValueGreaterThanEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecimalValueGreaterThanEquals_Value()
  {
    return (EAttribute)decimalValueGreaterThanEqualsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDecimalValueLessThanEquals()
  {
    return decimalValueLessThanEqualsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDecimalValueLessThanEquals_Value()
  {
    return (EAttribute)decimalValueLessThanEqualsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNestedExpression()
  {
    return nestedExpressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getNestedExpression_Nested()
  {
    return (EReference)nestedExpressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOrExpressionConstraint()
  {
    return orExpressionConstraintEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOrExpressionConstraint_Left()
  {
    return (EReference)orExpressionConstraintEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOrExpressionConstraint_Right()
  {
    return (EReference)orExpressionConstraintEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAndExpressionConstraint()
  {
    return andExpressionConstraintEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAndExpressionConstraint_Left()
  {
    return (EReference)andExpressionConstraintEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAndExpressionConstraint_Right()
  {
    return (EReference)andExpressionConstraintEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExclusionExpressionConstraint()
  {
    return exclusionExpressionConstraintEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExclusionExpressionConstraint_Left()
  {
    return (EReference)exclusionExpressionConstraintEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExclusionExpressionConstraint_Right()
  {
    return (EReference)exclusionExpressionConstraintEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRefinedExpressionConstraint()
  {
    return refinedExpressionConstraintEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRefinedExpressionConstraint_Constraint()
  {
    return (EReference)refinedExpressionConstraintEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRefinedExpressionConstraint_Refinement()
  {
    return (EReference)refinedExpressionConstraintEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDottedExpressionConstraint()
  {
    return dottedExpressionConstraintEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDottedExpressionConstraint_Constraint()
  {
    return (EReference)dottedExpressionConstraintEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDottedExpressionConstraint_Attribute()
  {
    return (EReference)dottedExpressionConstraintEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOrRefinement()
  {
    return orRefinementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOrRefinement_Left()
  {
    return (EReference)orRefinementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getOrRefinement_Right()
  {
    return (EReference)orRefinementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAndRefinement()
  {
    return andRefinementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAndRefinement_Left()
  {
    return (EReference)andRefinementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAndRefinement_Right()
  {
    return (EReference)andRefinementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EclFactory getEclFactory()
  {
    return (EclFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    scriptEClass = createEClass(SCRIPT);
    createEReference(scriptEClass, SCRIPT__CONSTRAINT);

    expressionConstraintEClass = createEClass(EXPRESSION_CONSTRAINT);

    childOfEClass = createEClass(CHILD_OF);
    createEReference(childOfEClass, CHILD_OF__CONSTRAINT);

    descendantOfEClass = createEClass(DESCENDANT_OF);
    createEReference(descendantOfEClass, DESCENDANT_OF__CONSTRAINT);

    descendantOrSelfOfEClass = createEClass(DESCENDANT_OR_SELF_OF);
    createEReference(descendantOrSelfOfEClass, DESCENDANT_OR_SELF_OF__CONSTRAINT);

    parentOfEClass = createEClass(PARENT_OF);
    createEReference(parentOfEClass, PARENT_OF__CONSTRAINT);

    ancestorOfEClass = createEClass(ANCESTOR_OF);
    createEReference(ancestorOfEClass, ANCESTOR_OF__CONSTRAINT);

    ancestorOrSelfOfEClass = createEClass(ANCESTOR_OR_SELF_OF);
    createEReference(ancestorOrSelfOfEClass, ANCESTOR_OR_SELF_OF__CONSTRAINT);

    memberOfEClass = createEClass(MEMBER_OF);
    createEReference(memberOfEClass, MEMBER_OF__CONSTRAINT);

    conceptReferenceEClass = createEClass(CONCEPT_REFERENCE);
    createEAttribute(conceptReferenceEClass, CONCEPT_REFERENCE__ID);
    createEAttribute(conceptReferenceEClass, CONCEPT_REFERENCE__TERM);

    anyEClass = createEClass(ANY);

    refinementEClass = createEClass(REFINEMENT);

    nestedRefinementEClass = createEClass(NESTED_REFINEMENT);
    createEReference(nestedRefinementEClass, NESTED_REFINEMENT__NESTED);

    attributeGroupEClass = createEClass(ATTRIBUTE_GROUP);
    createEReference(attributeGroupEClass, ATTRIBUTE_GROUP__CARDINALITY);
    createEReference(attributeGroupEClass, ATTRIBUTE_GROUP__REFINEMENT);

    attributeConstraintEClass = createEClass(ATTRIBUTE_CONSTRAINT);
    createEReference(attributeConstraintEClass, ATTRIBUTE_CONSTRAINT__CARDINALITY);
    createEAttribute(attributeConstraintEClass, ATTRIBUTE_CONSTRAINT__REVERSED);
    createEReference(attributeConstraintEClass, ATTRIBUTE_CONSTRAINT__ATTRIBUTE);
    createEReference(attributeConstraintEClass, ATTRIBUTE_CONSTRAINT__COMPARISON);

    cardinalityEClass = createEClass(CARDINALITY);
    createEAttribute(cardinalityEClass, CARDINALITY__MIN);
    createEAttribute(cardinalityEClass, CARDINALITY__MAX);

    comparisonEClass = createEClass(COMPARISON);

    attributeComparisonEClass = createEClass(ATTRIBUTE_COMPARISON);
    createEReference(attributeComparisonEClass, ATTRIBUTE_COMPARISON__CONSTRAINT);

    dataTypeComparisonEClass = createEClass(DATA_TYPE_COMPARISON);

    attributeValueEqualsEClass = createEClass(ATTRIBUTE_VALUE_EQUALS);

    attributeValueNotEqualsEClass = createEClass(ATTRIBUTE_VALUE_NOT_EQUALS);

    stringValueEqualsEClass = createEClass(STRING_VALUE_EQUALS);
    createEAttribute(stringValueEqualsEClass, STRING_VALUE_EQUALS__VALUE);

    stringValueNotEqualsEClass = createEClass(STRING_VALUE_NOT_EQUALS);
    createEAttribute(stringValueNotEqualsEClass, STRING_VALUE_NOT_EQUALS__VALUE);

    integerValueEqualsEClass = createEClass(INTEGER_VALUE_EQUALS);
    createEAttribute(integerValueEqualsEClass, INTEGER_VALUE_EQUALS__VALUE);

    integerValueNotEqualsEClass = createEClass(INTEGER_VALUE_NOT_EQUALS);
    createEAttribute(integerValueNotEqualsEClass, INTEGER_VALUE_NOT_EQUALS__VALUE);

    integerValueGreaterThanEClass = createEClass(INTEGER_VALUE_GREATER_THAN);
    createEAttribute(integerValueGreaterThanEClass, INTEGER_VALUE_GREATER_THAN__VALUE);

    integerValueLessThanEClass = createEClass(INTEGER_VALUE_LESS_THAN);
    createEAttribute(integerValueLessThanEClass, INTEGER_VALUE_LESS_THAN__VALUE);

    integerValueGreaterThanEqualsEClass = createEClass(INTEGER_VALUE_GREATER_THAN_EQUALS);
    createEAttribute(integerValueGreaterThanEqualsEClass, INTEGER_VALUE_GREATER_THAN_EQUALS__VALUE);

    integerValueLessThanEqualsEClass = createEClass(INTEGER_VALUE_LESS_THAN_EQUALS);
    createEAttribute(integerValueLessThanEqualsEClass, INTEGER_VALUE_LESS_THAN_EQUALS__VALUE);

    decimalValueEqualsEClass = createEClass(DECIMAL_VALUE_EQUALS);
    createEAttribute(decimalValueEqualsEClass, DECIMAL_VALUE_EQUALS__VALUE);

    decimalValueNotEqualsEClass = createEClass(DECIMAL_VALUE_NOT_EQUALS);
    createEAttribute(decimalValueNotEqualsEClass, DECIMAL_VALUE_NOT_EQUALS__VALUE);

    decimalValueGreaterThanEClass = createEClass(DECIMAL_VALUE_GREATER_THAN);
    createEAttribute(decimalValueGreaterThanEClass, DECIMAL_VALUE_GREATER_THAN__VALUE);

    decimalValueLessThanEClass = createEClass(DECIMAL_VALUE_LESS_THAN);
    createEAttribute(decimalValueLessThanEClass, DECIMAL_VALUE_LESS_THAN__VALUE);

    decimalValueGreaterThanEqualsEClass = createEClass(DECIMAL_VALUE_GREATER_THAN_EQUALS);
    createEAttribute(decimalValueGreaterThanEqualsEClass, DECIMAL_VALUE_GREATER_THAN_EQUALS__VALUE);

    decimalValueLessThanEqualsEClass = createEClass(DECIMAL_VALUE_LESS_THAN_EQUALS);
    createEAttribute(decimalValueLessThanEqualsEClass, DECIMAL_VALUE_LESS_THAN_EQUALS__VALUE);

    nestedExpressionEClass = createEClass(NESTED_EXPRESSION);
    createEReference(nestedExpressionEClass, NESTED_EXPRESSION__NESTED);

    orExpressionConstraintEClass = createEClass(OR_EXPRESSION_CONSTRAINT);
    createEReference(orExpressionConstraintEClass, OR_EXPRESSION_CONSTRAINT__LEFT);
    createEReference(orExpressionConstraintEClass, OR_EXPRESSION_CONSTRAINT__RIGHT);

    andExpressionConstraintEClass = createEClass(AND_EXPRESSION_CONSTRAINT);
    createEReference(andExpressionConstraintEClass, AND_EXPRESSION_CONSTRAINT__LEFT);
    createEReference(andExpressionConstraintEClass, AND_EXPRESSION_CONSTRAINT__RIGHT);

    exclusionExpressionConstraintEClass = createEClass(EXCLUSION_EXPRESSION_CONSTRAINT);
    createEReference(exclusionExpressionConstraintEClass, EXCLUSION_EXPRESSION_CONSTRAINT__LEFT);
    createEReference(exclusionExpressionConstraintEClass, EXCLUSION_EXPRESSION_CONSTRAINT__RIGHT);

    refinedExpressionConstraintEClass = createEClass(REFINED_EXPRESSION_CONSTRAINT);
    createEReference(refinedExpressionConstraintEClass, REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT);
    createEReference(refinedExpressionConstraintEClass, REFINED_EXPRESSION_CONSTRAINT__REFINEMENT);

    dottedExpressionConstraintEClass = createEClass(DOTTED_EXPRESSION_CONSTRAINT);
    createEReference(dottedExpressionConstraintEClass, DOTTED_EXPRESSION_CONSTRAINT__CONSTRAINT);
    createEReference(dottedExpressionConstraintEClass, DOTTED_EXPRESSION_CONSTRAINT__ATTRIBUTE);

    orRefinementEClass = createEClass(OR_REFINEMENT);
    createEReference(orRefinementEClass, OR_REFINEMENT__LEFT);
    createEReference(orRefinementEClass, OR_REFINEMENT__RIGHT);

    andRefinementEClass = createEClass(AND_REFINEMENT);
    createEReference(andRefinementEClass, AND_REFINEMENT__LEFT);
    createEReference(andRefinementEClass, AND_REFINEMENT__RIGHT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    childOfEClass.getESuperTypes().add(this.getExpressionConstraint());
    descendantOfEClass.getESuperTypes().add(this.getExpressionConstraint());
    descendantOrSelfOfEClass.getESuperTypes().add(this.getExpressionConstraint());
    parentOfEClass.getESuperTypes().add(this.getExpressionConstraint());
    ancestorOfEClass.getESuperTypes().add(this.getExpressionConstraint());
    ancestorOrSelfOfEClass.getESuperTypes().add(this.getExpressionConstraint());
    memberOfEClass.getESuperTypes().add(this.getExpressionConstraint());
    conceptReferenceEClass.getESuperTypes().add(this.getExpressionConstraint());
    anyEClass.getESuperTypes().add(this.getExpressionConstraint());
    nestedRefinementEClass.getESuperTypes().add(this.getRefinement());
    attributeGroupEClass.getESuperTypes().add(this.getRefinement());
    attributeConstraintEClass.getESuperTypes().add(this.getRefinement());
    attributeComparisonEClass.getESuperTypes().add(this.getComparison());
    dataTypeComparisonEClass.getESuperTypes().add(this.getComparison());
    attributeValueEqualsEClass.getESuperTypes().add(this.getAttributeComparison());
    attributeValueNotEqualsEClass.getESuperTypes().add(this.getAttributeComparison());
    stringValueEqualsEClass.getESuperTypes().add(this.getDataTypeComparison());
    stringValueNotEqualsEClass.getESuperTypes().add(this.getDataTypeComparison());
    integerValueEqualsEClass.getESuperTypes().add(this.getDataTypeComparison());
    integerValueNotEqualsEClass.getESuperTypes().add(this.getDataTypeComparison());
    integerValueGreaterThanEClass.getESuperTypes().add(this.getDataTypeComparison());
    integerValueLessThanEClass.getESuperTypes().add(this.getDataTypeComparison());
    integerValueGreaterThanEqualsEClass.getESuperTypes().add(this.getDataTypeComparison());
    integerValueLessThanEqualsEClass.getESuperTypes().add(this.getDataTypeComparison());
    decimalValueEqualsEClass.getESuperTypes().add(this.getDataTypeComparison());
    decimalValueNotEqualsEClass.getESuperTypes().add(this.getDataTypeComparison());
    decimalValueGreaterThanEClass.getESuperTypes().add(this.getDataTypeComparison());
    decimalValueLessThanEClass.getESuperTypes().add(this.getDataTypeComparison());
    decimalValueGreaterThanEqualsEClass.getESuperTypes().add(this.getDataTypeComparison());
    decimalValueLessThanEqualsEClass.getESuperTypes().add(this.getDataTypeComparison());
    nestedExpressionEClass.getESuperTypes().add(this.getExpressionConstraint());
    orExpressionConstraintEClass.getESuperTypes().add(this.getExpressionConstraint());
    andExpressionConstraintEClass.getESuperTypes().add(this.getExpressionConstraint());
    exclusionExpressionConstraintEClass.getESuperTypes().add(this.getExpressionConstraint());
    refinedExpressionConstraintEClass.getESuperTypes().add(this.getExpressionConstraint());
    dottedExpressionConstraintEClass.getESuperTypes().add(this.getExpressionConstraint());
    orRefinementEClass.getESuperTypes().add(this.getRefinement());
    andRefinementEClass.getESuperTypes().add(this.getRefinement());

    // Initialize classes and features; add operations and parameters
    initEClass(scriptEClass, Script.class, "Script", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getScript_Constraint(), this.getExpressionConstraint(), null, "constraint", null, 0, 1, Script.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(expressionConstraintEClass, ExpressionConstraint.class, "ExpressionConstraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(childOfEClass, ChildOf.class, "ChildOf", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getChildOf_Constraint(), this.getExpressionConstraint(), null, "constraint", null, 0, 1, ChildOf.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(descendantOfEClass, DescendantOf.class, "DescendantOf", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDescendantOf_Constraint(), this.getExpressionConstraint(), null, "constraint", null, 0, 1, DescendantOf.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(descendantOrSelfOfEClass, DescendantOrSelfOf.class, "DescendantOrSelfOf", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDescendantOrSelfOf_Constraint(), this.getExpressionConstraint(), null, "constraint", null, 0, 1, DescendantOrSelfOf.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(parentOfEClass, ParentOf.class, "ParentOf", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getParentOf_Constraint(), this.getExpressionConstraint(), null, "constraint", null, 0, 1, ParentOf.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(ancestorOfEClass, AncestorOf.class, "AncestorOf", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAncestorOf_Constraint(), this.getExpressionConstraint(), null, "constraint", null, 0, 1, AncestorOf.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(ancestorOrSelfOfEClass, AncestorOrSelfOf.class, "AncestorOrSelfOf", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAncestorOrSelfOf_Constraint(), this.getExpressionConstraint(), null, "constraint", null, 0, 1, AncestorOrSelfOf.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(memberOfEClass, MemberOf.class, "MemberOf", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMemberOf_Constraint(), this.getExpressionConstraint(), null, "constraint", null, 0, 1, MemberOf.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conceptReferenceEClass, ConceptReference.class, "ConceptReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getConceptReference_Id(), ecorePackage.getEString(), "id", null, 0, 1, ConceptReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getConceptReference_Term(), ecorePackage.getEString(), "term", null, 0, 1, ConceptReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(anyEClass, Any.class, "Any", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(refinementEClass, Refinement.class, "Refinement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(nestedRefinementEClass, NestedRefinement.class, "NestedRefinement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getNestedRefinement_Nested(), this.getRefinement(), null, "nested", null, 0, 1, NestedRefinement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeGroupEClass, AttributeGroup.class, "AttributeGroup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAttributeGroup_Cardinality(), this.getCardinality(), null, "cardinality", null, 0, 1, AttributeGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttributeGroup_Refinement(), this.getRefinement(), null, "refinement", null, 0, 1, AttributeGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeConstraintEClass, AttributeConstraint.class, "AttributeConstraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAttributeConstraint_Cardinality(), this.getCardinality(), null, "cardinality", null, 0, 1, AttributeConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAttributeConstraint_Reversed(), ecorePackage.getEBoolean(), "reversed", null, 0, 1, AttributeConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttributeConstraint_Attribute(), this.getExpressionConstraint(), null, "attribute", null, 0, 1, AttributeConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttributeConstraint_Comparison(), this.getComparison(), null, "comparison", null, 0, 1, AttributeConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(cardinalityEClass, Cardinality.class, "Cardinality", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCardinality_Min(), ecorePackage.getEInt(), "min", null, 0, 1, Cardinality.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getCardinality_Max(), ecorePackage.getEInt(), "max", null, 0, 1, Cardinality.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(comparisonEClass, Comparison.class, "Comparison", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(attributeComparisonEClass, AttributeComparison.class, "AttributeComparison", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAttributeComparison_Constraint(), this.getExpressionConstraint(), null, "constraint", null, 0, 1, AttributeComparison.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dataTypeComparisonEClass, DataTypeComparison.class, "DataTypeComparison", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(attributeValueEqualsEClass, AttributeValueEquals.class, "AttributeValueEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(attributeValueNotEqualsEClass, AttributeValueNotEquals.class, "AttributeValueNotEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(stringValueEqualsEClass, StringValueEquals.class, "StringValueEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringValueEquals_Value(), ecorePackage.getEString(), "value", null, 0, 1, StringValueEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringValueNotEqualsEClass, StringValueNotEquals.class, "StringValueNotEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringValueNotEquals_Value(), ecorePackage.getEString(), "value", null, 0, 1, StringValueNotEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerValueEqualsEClass, IntegerValueEquals.class, "IntegerValueEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntegerValueEquals_Value(), ecorePackage.getEInt(), "value", null, 0, 1, IntegerValueEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerValueNotEqualsEClass, IntegerValueNotEquals.class, "IntegerValueNotEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntegerValueNotEquals_Value(), ecorePackage.getEInt(), "value", null, 0, 1, IntegerValueNotEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerValueGreaterThanEClass, IntegerValueGreaterThan.class, "IntegerValueGreaterThan", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntegerValueGreaterThan_Value(), ecorePackage.getEInt(), "value", null, 0, 1, IntegerValueGreaterThan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerValueLessThanEClass, IntegerValueLessThan.class, "IntegerValueLessThan", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntegerValueLessThan_Value(), ecorePackage.getEInt(), "value", null, 0, 1, IntegerValueLessThan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerValueGreaterThanEqualsEClass, IntegerValueGreaterThanEquals.class, "IntegerValueGreaterThanEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntegerValueGreaterThanEquals_Value(), ecorePackage.getEInt(), "value", null, 0, 1, IntegerValueGreaterThanEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerValueLessThanEqualsEClass, IntegerValueLessThanEquals.class, "IntegerValueLessThanEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntegerValueLessThanEquals_Value(), ecorePackage.getEInt(), "value", null, 0, 1, IntegerValueLessThanEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalValueEqualsEClass, DecimalValueEquals.class, "DecimalValueEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecimalValueEquals_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, DecimalValueEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalValueNotEqualsEClass, DecimalValueNotEquals.class, "DecimalValueNotEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecimalValueNotEquals_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, DecimalValueNotEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalValueGreaterThanEClass, DecimalValueGreaterThan.class, "DecimalValueGreaterThan", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecimalValueGreaterThan_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, DecimalValueGreaterThan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalValueLessThanEClass, DecimalValueLessThan.class, "DecimalValueLessThan", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecimalValueLessThan_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, DecimalValueLessThan.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalValueGreaterThanEqualsEClass, DecimalValueGreaterThanEquals.class, "DecimalValueGreaterThanEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecimalValueGreaterThanEquals_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, DecimalValueGreaterThanEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalValueLessThanEqualsEClass, DecimalValueLessThanEquals.class, "DecimalValueLessThanEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecimalValueLessThanEquals_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, DecimalValueLessThanEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(nestedExpressionEClass, NestedExpression.class, "NestedExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getNestedExpression_Nested(), this.getExpressionConstraint(), null, "nested", null, 0, 1, NestedExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(orExpressionConstraintEClass, OrExpressionConstraint.class, "OrExpressionConstraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOrExpressionConstraint_Left(), this.getExpressionConstraint(), null, "left", null, 0, 1, OrExpressionConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOrExpressionConstraint_Right(), this.getExpressionConstraint(), null, "right", null, 0, 1, OrExpressionConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(andExpressionConstraintEClass, AndExpressionConstraint.class, "AndExpressionConstraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAndExpressionConstraint_Left(), this.getExpressionConstraint(), null, "left", null, 0, 1, AndExpressionConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAndExpressionConstraint_Right(), this.getExpressionConstraint(), null, "right", null, 0, 1, AndExpressionConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(exclusionExpressionConstraintEClass, ExclusionExpressionConstraint.class, "ExclusionExpressionConstraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getExclusionExpressionConstraint_Left(), this.getExpressionConstraint(), null, "left", null, 0, 1, ExclusionExpressionConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExclusionExpressionConstraint_Right(), this.getExpressionConstraint(), null, "right", null, 0, 1, ExclusionExpressionConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(refinedExpressionConstraintEClass, RefinedExpressionConstraint.class, "RefinedExpressionConstraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefinedExpressionConstraint_Constraint(), this.getExpressionConstraint(), null, "constraint", null, 0, 1, RefinedExpressionConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRefinedExpressionConstraint_Refinement(), this.getRefinement(), null, "refinement", null, 0, 1, RefinedExpressionConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dottedExpressionConstraintEClass, DottedExpressionConstraint.class, "DottedExpressionConstraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDottedExpressionConstraint_Constraint(), this.getExpressionConstraint(), null, "constraint", null, 0, 1, DottedExpressionConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDottedExpressionConstraint_Attribute(), this.getExpressionConstraint(), null, "attribute", null, 0, 1, DottedExpressionConstraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(orRefinementEClass, OrRefinement.class, "OrRefinement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getOrRefinement_Left(), this.getRefinement(), null, "left", null, 0, 1, OrRefinement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getOrRefinement_Right(), this.getRefinement(), null, "right", null, 0, 1, OrRefinement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(andRefinementEClass, AndRefinement.class, "AndRefinement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAndRefinement_Left(), this.getRefinement(), null, "left", null, 0, 1, AndRefinement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAndRefinement_Right(), this.getRefinement(), null, "right", null, 0, 1, AndRefinement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} //EclPackageImpl
