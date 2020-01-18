/**
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.etl.etl.impl;

import com.b2international.snowowl.snomed.ecl.ecl.EclPackage;

import com.b2international.snowowl.snomed.etl.etl.Attribute;
import com.b2international.snowowl.snomed.etl.etl.AttributeGroup;
import com.b2international.snowowl.snomed.etl.etl.AttributeValue;
import com.b2international.snowowl.snomed.etl.etl.Cardinality;
import com.b2international.snowowl.snomed.etl.etl.ConceptReference;
import com.b2international.snowowl.snomed.etl.etl.ConceptReferenceSlot;
import com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.ConcreteValueReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.DecimalMaximumValue;
import com.b2international.snowowl.snomed.etl.etl.DecimalMinimumValue;
import com.b2international.snowowl.snomed.etl.etl.DecimalRange;
import com.b2international.snowowl.snomed.etl.etl.DecimalReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.DecimalValue;
import com.b2international.snowowl.snomed.etl.etl.DecimalValues;
import com.b2international.snowowl.snomed.etl.etl.EtlFactory;
import com.b2international.snowowl.snomed.etl.etl.EtlPackage;
import com.b2international.snowowl.snomed.etl.etl.ExpressionReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate;
import com.b2international.snowowl.snomed.etl.etl.FocusConcept;
import com.b2international.snowowl.snomed.etl.etl.IntegerMaximumValue;
import com.b2international.snowowl.snomed.etl.etl.IntegerMinimumValue;
import com.b2international.snowowl.snomed.etl.etl.IntegerRange;
import com.b2international.snowowl.snomed.etl.etl.IntegerReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.IntegerValue;
import com.b2international.snowowl.snomed.etl.etl.IntegerValues;
import com.b2international.snowowl.snomed.etl.etl.Refinement;
import com.b2international.snowowl.snomed.etl.etl.StringReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.StringValue;
import com.b2international.snowowl.snomed.etl.etl.SubExpression;
import com.b2international.snowowl.snomed.etl.etl.TemplateInformationSlot;
import com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot;

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
public class EtlPackageImpl extends EPackageImpl implements EtlPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass expressionTemplateEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass subExpressionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass focusConceptEClass = null;

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
  private EClass attributeGroupEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass attributeValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conceptReplacementSlotEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass expressionReplacementSlotEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass tokenReplacementSlotEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass templateInformationSlotEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass concreteValueReplacementSlotEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass stringReplacementSlotEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerReplacementSlotEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalReplacementSlotEClass = null;

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
  private EClass stringValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerValuesEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerRangeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerMinimumValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass integerMaximumValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalValuesEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalRangeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalMinimumValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decimalMaximumValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conceptReferenceSlotEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conceptReferenceEClass = null;

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
   * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private EtlPackageImpl()
  {
    super(eNS_URI, EtlFactory.eINSTANCE);
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
   * <p>This method is used to initialize {@link EtlPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static EtlPackage init()
  {
    if (isInited) return (EtlPackage)EPackage.Registry.INSTANCE.getEPackage(EtlPackage.eNS_URI);

    // Obtain or create and register package
    Object registeredEtlPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    EtlPackageImpl theEtlPackage = registeredEtlPackage instanceof EtlPackageImpl ? (EtlPackageImpl)registeredEtlPackage : new EtlPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    EclPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theEtlPackage.createPackageContents();

    // Initialize created meta-data
    theEtlPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theEtlPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(EtlPackage.eNS_URI, theEtlPackage);
    return theEtlPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getExpressionTemplate()
  {
    return expressionTemplateEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getExpressionTemplate_Primitive()
  {
    return (EAttribute)expressionTemplateEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getExpressionTemplate_Slot()
  {
    return (EReference)expressionTemplateEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getExpressionTemplate_Expression()
  {
    return (EReference)expressionTemplateEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSubExpression()
  {
    return subExpressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSubExpression_FocusConcepts()
  {
    return (EReference)subExpressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSubExpression_Refinement()
  {
    return (EReference)subExpressionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getFocusConcept()
  {
    return focusConceptEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getFocusConcept_Slot()
  {
    return (EReference)focusConceptEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getFocusConcept_Concept()
  {
    return (EReference)focusConceptEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRefinement()
  {
    return refinementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRefinement_Attributes()
  {
    return (EReference)refinementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRefinement_Groups()
  {
    return (EReference)refinementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getAttributeGroup()
  {
    return attributeGroupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getAttributeGroup_Slot()
  {
    return (EReference)attributeGroupEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getAttributeGroup_Attributes()
  {
    return (EReference)attributeGroupEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getAttribute()
  {
    return attributeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getAttribute_Slot()
  {
    return (EReference)attributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getAttribute_Name()
  {
    return (EReference)attributeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getAttribute_Value()
  {
    return (EReference)attributeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getAttributeValue()
  {
    return attributeValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getConceptReplacementSlot()
  {
    return conceptReplacementSlotEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getExpressionReplacementSlot()
  {
    return expressionReplacementSlotEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getTokenReplacementSlot()
  {
    return tokenReplacementSlotEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTokenReplacementSlot_Tokens()
  {
    return (EAttribute)tokenReplacementSlotEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTokenReplacementSlot_Name()
  {
    return (EAttribute)tokenReplacementSlotEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getTemplateInformationSlot()
  {
    return templateInformationSlotEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTemplateInformationSlot_Cardinality()
  {
    return (EReference)templateInformationSlotEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTemplateInformationSlot_Name()
  {
    return (EAttribute)templateInformationSlotEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getConcreteValueReplacementSlot()
  {
    return concreteValueReplacementSlotEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getConcreteValueReplacementSlot_Name()
  {
    return (EAttribute)concreteValueReplacementSlotEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getStringReplacementSlot()
  {
    return stringReplacementSlotEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getStringReplacementSlot_Values()
  {
    return (EReference)stringReplacementSlotEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIntegerReplacementSlot()
  {
    return integerReplacementSlotEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getIntegerReplacementSlot_Values()
  {
    return (EReference)integerReplacementSlotEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDecimalReplacementSlot()
  {
    return decimalReplacementSlotEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDecimalReplacementSlot_Values()
  {
    return (EReference)decimalReplacementSlotEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getCardinality()
  {
    return cardinalityEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getStringValue()
  {
    return stringValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStringValue_Value()
  {
    return (EAttribute)stringValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIntegerValues()
  {
    return integerValuesEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIntegerValue()
  {
    return integerValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getIntegerValue_Value()
  {
    return (EAttribute)integerValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIntegerRange()
  {
    return integerRangeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getIntegerRange_Minimum()
  {
    return (EReference)integerRangeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getIntegerRange_Maximum()
  {
    return (EReference)integerRangeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIntegerMinimumValue()
  {
    return integerMinimumValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getIntegerMinimumValue_Exclusive()
  {
    return (EAttribute)integerMinimumValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getIntegerMinimumValue_Value()
  {
    return (EAttribute)integerMinimumValueEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIntegerMaximumValue()
  {
    return integerMaximumValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getIntegerMaximumValue_Exclusive()
  {
    return (EAttribute)integerMaximumValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getIntegerMaximumValue_Value()
  {
    return (EAttribute)integerMaximumValueEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDecimalValues()
  {
    return decimalValuesEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDecimalValue()
  {
    return decimalValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDecimalValue_Value()
  {
    return (EAttribute)decimalValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDecimalRange()
  {
    return decimalRangeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDecimalRange_Minimum()
  {
    return (EReference)decimalRangeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getDecimalRange_Maximum()
  {
    return (EReference)decimalRangeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDecimalMinimumValue()
  {
    return decimalMinimumValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDecimalMinimumValue_Exclusive()
  {
    return (EAttribute)decimalMinimumValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDecimalMinimumValue_Value()
  {
    return (EAttribute)decimalMinimumValueEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getDecimalMaximumValue()
  {
    return decimalMaximumValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDecimalMaximumValue_Exclusive()
  {
    return (EAttribute)decimalMaximumValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getDecimalMaximumValue_Value()
  {
    return (EAttribute)decimalMaximumValueEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getConceptReferenceSlot()
  {
    return conceptReferenceSlotEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getConceptReferenceSlot_Constraint()
  {
    return (EReference)conceptReferenceSlotEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getConceptReferenceSlot_Name()
  {
    return (EAttribute)conceptReferenceSlotEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getConceptReference()
  {
    return conceptReferenceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getConceptReference_Slot()
  {
    return (EReference)conceptReferenceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getConceptReference_Id()
  {
    return (EAttribute)conceptReferenceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getConceptReference_Term()
  {
    return (EAttribute)conceptReferenceEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EtlFactory getEtlFactory()
  {
    return (EtlFactory)getEFactoryInstance();
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
    expressionTemplateEClass = createEClass(EXPRESSION_TEMPLATE);
    createEAttribute(expressionTemplateEClass, EXPRESSION_TEMPLATE__PRIMITIVE);
    createEReference(expressionTemplateEClass, EXPRESSION_TEMPLATE__SLOT);
    createEReference(expressionTemplateEClass, EXPRESSION_TEMPLATE__EXPRESSION);

    subExpressionEClass = createEClass(SUB_EXPRESSION);
    createEReference(subExpressionEClass, SUB_EXPRESSION__FOCUS_CONCEPTS);
    createEReference(subExpressionEClass, SUB_EXPRESSION__REFINEMENT);

    focusConceptEClass = createEClass(FOCUS_CONCEPT);
    createEReference(focusConceptEClass, FOCUS_CONCEPT__SLOT);
    createEReference(focusConceptEClass, FOCUS_CONCEPT__CONCEPT);

    refinementEClass = createEClass(REFINEMENT);
    createEReference(refinementEClass, REFINEMENT__ATTRIBUTES);
    createEReference(refinementEClass, REFINEMENT__GROUPS);

    attributeGroupEClass = createEClass(ATTRIBUTE_GROUP);
    createEReference(attributeGroupEClass, ATTRIBUTE_GROUP__SLOT);
    createEReference(attributeGroupEClass, ATTRIBUTE_GROUP__ATTRIBUTES);

    attributeEClass = createEClass(ATTRIBUTE);
    createEReference(attributeEClass, ATTRIBUTE__SLOT);
    createEReference(attributeEClass, ATTRIBUTE__NAME);
    createEReference(attributeEClass, ATTRIBUTE__VALUE);

    attributeValueEClass = createEClass(ATTRIBUTE_VALUE);

    conceptReplacementSlotEClass = createEClass(CONCEPT_REPLACEMENT_SLOT);

    expressionReplacementSlotEClass = createEClass(EXPRESSION_REPLACEMENT_SLOT);

    tokenReplacementSlotEClass = createEClass(TOKEN_REPLACEMENT_SLOT);
    createEAttribute(tokenReplacementSlotEClass, TOKEN_REPLACEMENT_SLOT__TOKENS);
    createEAttribute(tokenReplacementSlotEClass, TOKEN_REPLACEMENT_SLOT__NAME);

    templateInformationSlotEClass = createEClass(TEMPLATE_INFORMATION_SLOT);
    createEReference(templateInformationSlotEClass, TEMPLATE_INFORMATION_SLOT__CARDINALITY);
    createEAttribute(templateInformationSlotEClass, TEMPLATE_INFORMATION_SLOT__NAME);

    concreteValueReplacementSlotEClass = createEClass(CONCRETE_VALUE_REPLACEMENT_SLOT);
    createEAttribute(concreteValueReplacementSlotEClass, CONCRETE_VALUE_REPLACEMENT_SLOT__NAME);

    stringReplacementSlotEClass = createEClass(STRING_REPLACEMENT_SLOT);
    createEReference(stringReplacementSlotEClass, STRING_REPLACEMENT_SLOT__VALUES);

    integerReplacementSlotEClass = createEClass(INTEGER_REPLACEMENT_SLOT);
    createEReference(integerReplacementSlotEClass, INTEGER_REPLACEMENT_SLOT__VALUES);

    decimalReplacementSlotEClass = createEClass(DECIMAL_REPLACEMENT_SLOT);
    createEReference(decimalReplacementSlotEClass, DECIMAL_REPLACEMENT_SLOT__VALUES);

    cardinalityEClass = createEClass(CARDINALITY);

    stringValueEClass = createEClass(STRING_VALUE);
    createEAttribute(stringValueEClass, STRING_VALUE__VALUE);

    integerValuesEClass = createEClass(INTEGER_VALUES);

    integerValueEClass = createEClass(INTEGER_VALUE);
    createEAttribute(integerValueEClass, INTEGER_VALUE__VALUE);

    integerRangeEClass = createEClass(INTEGER_RANGE);
    createEReference(integerRangeEClass, INTEGER_RANGE__MINIMUM);
    createEReference(integerRangeEClass, INTEGER_RANGE__MAXIMUM);

    integerMinimumValueEClass = createEClass(INTEGER_MINIMUM_VALUE);
    createEAttribute(integerMinimumValueEClass, INTEGER_MINIMUM_VALUE__EXCLUSIVE);
    createEAttribute(integerMinimumValueEClass, INTEGER_MINIMUM_VALUE__VALUE);

    integerMaximumValueEClass = createEClass(INTEGER_MAXIMUM_VALUE);
    createEAttribute(integerMaximumValueEClass, INTEGER_MAXIMUM_VALUE__EXCLUSIVE);
    createEAttribute(integerMaximumValueEClass, INTEGER_MAXIMUM_VALUE__VALUE);

    decimalValuesEClass = createEClass(DECIMAL_VALUES);

    decimalValueEClass = createEClass(DECIMAL_VALUE);
    createEAttribute(decimalValueEClass, DECIMAL_VALUE__VALUE);

    decimalRangeEClass = createEClass(DECIMAL_RANGE);
    createEReference(decimalRangeEClass, DECIMAL_RANGE__MINIMUM);
    createEReference(decimalRangeEClass, DECIMAL_RANGE__MAXIMUM);

    decimalMinimumValueEClass = createEClass(DECIMAL_MINIMUM_VALUE);
    createEAttribute(decimalMinimumValueEClass, DECIMAL_MINIMUM_VALUE__EXCLUSIVE);
    createEAttribute(decimalMinimumValueEClass, DECIMAL_MINIMUM_VALUE__VALUE);

    decimalMaximumValueEClass = createEClass(DECIMAL_MAXIMUM_VALUE);
    createEAttribute(decimalMaximumValueEClass, DECIMAL_MAXIMUM_VALUE__EXCLUSIVE);
    createEAttribute(decimalMaximumValueEClass, DECIMAL_MAXIMUM_VALUE__VALUE);

    conceptReferenceSlotEClass = createEClass(CONCEPT_REFERENCE_SLOT);
    createEReference(conceptReferenceSlotEClass, CONCEPT_REFERENCE_SLOT__CONSTRAINT);
    createEAttribute(conceptReferenceSlotEClass, CONCEPT_REFERENCE_SLOT__NAME);

    conceptReferenceEClass = createEClass(CONCEPT_REFERENCE);
    createEReference(conceptReferenceEClass, CONCEPT_REFERENCE__SLOT);
    createEAttribute(conceptReferenceEClass, CONCEPT_REFERENCE__ID);
    createEAttribute(conceptReferenceEClass, CONCEPT_REFERENCE__TERM);
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

    // Obtain other dependent packages
    EclPackage theEclPackage = (EclPackage)EPackage.Registry.INSTANCE.getEPackage(EclPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    subExpressionEClass.getESuperTypes().add(this.getAttributeValue());
    conceptReplacementSlotEClass.getESuperTypes().add(this.getConceptReferenceSlot());
    expressionReplacementSlotEClass.getESuperTypes().add(this.getConceptReferenceSlot());
    concreteValueReplacementSlotEClass.getESuperTypes().add(this.getAttributeValue());
    stringReplacementSlotEClass.getESuperTypes().add(this.getConcreteValueReplacementSlot());
    integerReplacementSlotEClass.getESuperTypes().add(this.getConcreteValueReplacementSlot());
    decimalReplacementSlotEClass.getESuperTypes().add(this.getConcreteValueReplacementSlot());
    cardinalityEClass.getESuperTypes().add(theEclPackage.getCardinality());
    stringValueEClass.getESuperTypes().add(this.getAttributeValue());
    integerValueEClass.getESuperTypes().add(this.getAttributeValue());
    integerValueEClass.getESuperTypes().add(this.getIntegerValues());
    integerRangeEClass.getESuperTypes().add(this.getIntegerValues());
    decimalValueEClass.getESuperTypes().add(this.getAttributeValue());
    decimalValueEClass.getESuperTypes().add(this.getDecimalValues());
    decimalRangeEClass.getESuperTypes().add(this.getDecimalValues());
    conceptReferenceEClass.getESuperTypes().add(this.getAttributeValue());

    // Initialize classes and features; add operations and parameters
    initEClass(expressionTemplateEClass, ExpressionTemplate.class, "ExpressionTemplate", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getExpressionTemplate_Primitive(), ecorePackage.getEBoolean(), "primitive", null, 0, 1, ExpressionTemplate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExpressionTemplate_Slot(), this.getTokenReplacementSlot(), null, "slot", null, 0, 1, ExpressionTemplate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExpressionTemplate_Expression(), this.getSubExpression(), null, "expression", null, 0, 1, ExpressionTemplate.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(subExpressionEClass, SubExpression.class, "SubExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSubExpression_FocusConcepts(), this.getFocusConcept(), null, "focusConcepts", null, 0, -1, SubExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSubExpression_Refinement(), this.getRefinement(), null, "refinement", null, 0, 1, SubExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(focusConceptEClass, FocusConcept.class, "FocusConcept", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getFocusConcept_Slot(), this.getTemplateInformationSlot(), null, "slot", null, 0, 1, FocusConcept.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getFocusConcept_Concept(), this.getConceptReference(), null, "concept", null, 0, 1, FocusConcept.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(refinementEClass, Refinement.class, "Refinement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRefinement_Attributes(), this.getAttribute(), null, "attributes", null, 0, -1, Refinement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRefinement_Groups(), this.getAttributeGroup(), null, "groups", null, 0, -1, Refinement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeGroupEClass, AttributeGroup.class, "AttributeGroup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAttributeGroup_Slot(), this.getTemplateInformationSlot(), null, "slot", null, 0, 1, AttributeGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttributeGroup_Attributes(), this.getAttribute(), null, "attributes", null, 0, -1, AttributeGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeEClass, Attribute.class, "Attribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAttribute_Slot(), this.getTemplateInformationSlot(), null, "slot", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttribute_Name(), this.getConceptReference(), null, "name", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAttribute_Value(), this.getAttributeValue(), null, "value", null, 0, 1, Attribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(attributeValueEClass, AttributeValue.class, "AttributeValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(conceptReplacementSlotEClass, ConceptReplacementSlot.class, "ConceptReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(expressionReplacementSlotEClass, ExpressionReplacementSlot.class, "ExpressionReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(tokenReplacementSlotEClass, TokenReplacementSlot.class, "TokenReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTokenReplacementSlot_Tokens(), ecorePackage.getEString(), "tokens", null, 0, -1, TokenReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTokenReplacementSlot_Name(), ecorePackage.getEString(), "name", null, 0, 1, TokenReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(templateInformationSlotEClass, TemplateInformationSlot.class, "TemplateInformationSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTemplateInformationSlot_Cardinality(), this.getCardinality(), null, "cardinality", null, 0, 1, TemplateInformationSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTemplateInformationSlot_Name(), ecorePackage.getEString(), "name", null, 0, 1, TemplateInformationSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(concreteValueReplacementSlotEClass, ConcreteValueReplacementSlot.class, "ConcreteValueReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getConcreteValueReplacementSlot_Name(), ecorePackage.getEString(), "name", null, 0, 1, ConcreteValueReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringReplacementSlotEClass, StringReplacementSlot.class, "StringReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getStringReplacementSlot_Values(), this.getStringValue(), null, "values", null, 0, -1, StringReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerReplacementSlotEClass, IntegerReplacementSlot.class, "IntegerReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getIntegerReplacementSlot_Values(), this.getIntegerValues(), null, "values", null, 0, -1, IntegerReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalReplacementSlotEClass, DecimalReplacementSlot.class, "DecimalReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDecimalReplacementSlot_Values(), this.getDecimalValues(), null, "values", null, 0, -1, DecimalReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(cardinalityEClass, Cardinality.class, "Cardinality", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(stringValueEClass, StringValue.class, "StringValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringValue_Value(), ecorePackage.getEString(), "value", null, 0, 1, StringValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerValuesEClass, IntegerValues.class, "IntegerValues", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(integerValueEClass, IntegerValue.class, "IntegerValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntegerValue_Value(), ecorePackage.getEInt(), "value", null, 0, 1, IntegerValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerRangeEClass, IntegerRange.class, "IntegerRange", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getIntegerRange_Minimum(), this.getIntegerMinimumValue(), null, "minimum", null, 0, 1, IntegerRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getIntegerRange_Maximum(), this.getIntegerMaximumValue(), null, "maximum", null, 0, 1, IntegerRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerMinimumValueEClass, IntegerMinimumValue.class, "IntegerMinimumValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntegerMinimumValue_Exclusive(), ecorePackage.getEBoolean(), "exclusive", null, 0, 1, IntegerMinimumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getIntegerMinimumValue_Value(), ecorePackage.getEInt(), "value", null, 0, 1, IntegerMinimumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerMaximumValueEClass, IntegerMaximumValue.class, "IntegerMaximumValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntegerMaximumValue_Exclusive(), ecorePackage.getEBoolean(), "exclusive", null, 0, 1, IntegerMaximumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getIntegerMaximumValue_Value(), ecorePackage.getEInt(), "value", null, 0, 1, IntegerMaximumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalValuesEClass, DecimalValues.class, "DecimalValues", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(decimalValueEClass, DecimalValue.class, "DecimalValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecimalValue_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, DecimalValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalRangeEClass, DecimalRange.class, "DecimalRange", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDecimalRange_Minimum(), this.getDecimalMinimumValue(), null, "minimum", null, 0, 1, DecimalRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDecimalRange_Maximum(), this.getDecimalMaximumValue(), null, "maximum", null, 0, 1, DecimalRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalMinimumValueEClass, DecimalMinimumValue.class, "DecimalMinimumValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecimalMinimumValue_Exclusive(), ecorePackage.getEBoolean(), "exclusive", null, 0, 1, DecimalMinimumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDecimalMinimumValue_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, DecimalMinimumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalMaximumValueEClass, DecimalMaximumValue.class, "DecimalMaximumValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecimalMaximumValue_Exclusive(), ecorePackage.getEBoolean(), "exclusive", null, 0, 1, DecimalMaximumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDecimalMaximumValue_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, DecimalMaximumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conceptReferenceSlotEClass, ConceptReferenceSlot.class, "ConceptReferenceSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConceptReferenceSlot_Constraint(), theEclPackage.getExpressionConstraint(), null, "constraint", null, 0, 1, ConceptReferenceSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getConceptReferenceSlot_Name(), ecorePackage.getEString(), "name", null, 0, 1, ConceptReferenceSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conceptReferenceEClass, ConceptReference.class, "ConceptReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConceptReference_Slot(), this.getConceptReferenceSlot(), null, "slot", null, 0, 1, ConceptReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getConceptReference_Id(), ecorePackage.getEString(), "id", null, 0, 1, ConceptReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getConceptReference_Term(), ecorePackage.getEString(), "term", null, 0, 1, ConceptReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} //EtlPackageImpl
