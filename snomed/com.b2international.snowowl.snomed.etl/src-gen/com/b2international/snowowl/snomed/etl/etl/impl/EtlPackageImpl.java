/**
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.etl.etl.impl;

import com.b2international.snomed.ecl.ecl.EclPackage;

import com.b2international.snowowl.snomed.etl.etl.Attribute;
import com.b2international.snowowl.snomed.etl.etl.AttributeGroup;
import com.b2international.snowowl.snomed.etl.etl.AttributeValue;
import com.b2international.snowowl.snomed.etl.etl.ConceptIdReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.ConceptReference;
import com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.ConcreteValueReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.DecimalReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.DecimalValue;
import com.b2international.snowowl.snomed.etl.etl.EtlCardinality;
import com.b2international.snowowl.snomed.etl.etl.EtlFactory;
import com.b2international.snowowl.snomed.etl.etl.EtlPackage;
import com.b2international.snowowl.snomed.etl.etl.ExpressionReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate;
import com.b2international.snowowl.snomed.etl.etl.FocusConcept;
import com.b2international.snowowl.snomed.etl.etl.IntegerReplacementSlot;
import com.b2international.snowowl.snomed.etl.etl.IntegerValue;
import com.b2international.snowowl.snomed.etl.etl.Refinement;
import com.b2international.snowowl.snomed.etl.etl.SlotDecimal;
import com.b2international.snowowl.snomed.etl.etl.SlotDecimalMaximumValue;
import com.b2international.snowowl.snomed.etl.etl.SlotDecimalMinimumValue;
import com.b2international.snowowl.snomed.etl.etl.SlotDecimalRange;
import com.b2international.snowowl.snomed.etl.etl.SlotDecimalValue;
import com.b2international.snowowl.snomed.etl.etl.SlotInteger;
import com.b2international.snowowl.snomed.etl.etl.SlotIntegerMaximumValue;
import com.b2international.snowowl.snomed.etl.etl.SlotIntegerMinimumValue;
import com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange;
import com.b2international.snowowl.snomed.etl.etl.SlotIntegerValue;
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
  private EClass conceptIdReplacementSlotEClass = null;

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
  private EClass etlCardinalityEClass = null;

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
  private EClass conceptReferenceEClass = null;

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
  private EClass integerValueEClass = null;

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
  private EClass slotIntegerEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass slotIntegerValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass slotIntegerRangeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass slotIntegerMinimumValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass slotIntegerMaximumValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass slotDecimalEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass slotDecimalValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass slotDecimalRangeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass slotDecimalMinimumValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass slotDecimalMaximumValueEClass = null;

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
  public EClass getConceptIdReplacementSlot()
  {
    return conceptIdReplacementSlotEClass;
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
  public EAttribute getStringReplacementSlot_Values()
  {
    return (EAttribute)stringReplacementSlotEClass.getEStructuralFeatures().get(0);
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
  public EClass getEtlCardinality()
  {
    return etlCardinalityEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getEtlCardinality_Min()
  {
    return (EAttribute)etlCardinalityEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getEtlCardinality_Max()
  {
    return (EAttribute)etlCardinalityEClass.getEStructuralFeatures().get(1);
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
  public EReference getConceptReplacementSlot_Constraint()
  {
    return (EReference)conceptReplacementSlotEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getConceptReplacementSlot_Name()
  {
    return (EAttribute)conceptReplacementSlotEClass.getEStructuralFeatures().get(1);
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
  public EClass getSlotInteger()
  {
    return slotIntegerEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSlotIntegerValue()
  {
    return slotIntegerValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSlotIntegerValue_Value()
  {
    return (EAttribute)slotIntegerValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSlotIntegerRange()
  {
    return slotIntegerRangeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSlotIntegerRange_Minimum()
  {
    return (EReference)slotIntegerRangeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSlotIntegerRange_Maximum()
  {
    return (EReference)slotIntegerRangeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSlotIntegerMinimumValue()
  {
    return slotIntegerMinimumValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSlotIntegerMinimumValue_Exclusive()
  {
    return (EAttribute)slotIntegerMinimumValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSlotIntegerMinimumValue_Value()
  {
    return (EAttribute)slotIntegerMinimumValueEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSlotIntegerMaximumValue()
  {
    return slotIntegerMaximumValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSlotIntegerMaximumValue_Exclusive()
  {
    return (EAttribute)slotIntegerMaximumValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSlotIntegerMaximumValue_Value()
  {
    return (EAttribute)slotIntegerMaximumValueEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSlotDecimal()
  {
    return slotDecimalEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSlotDecimalValue()
  {
    return slotDecimalValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSlotDecimalValue_Value()
  {
    return (EAttribute)slotDecimalValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSlotDecimalRange()
  {
    return slotDecimalRangeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSlotDecimalRange_Minimum()
  {
    return (EReference)slotDecimalRangeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSlotDecimalRange_Maximum()
  {
    return (EReference)slotDecimalRangeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSlotDecimalMinimumValue()
  {
    return slotDecimalMinimumValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSlotDecimalMinimumValue_Exclusive()
  {
    return (EAttribute)slotDecimalMinimumValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSlotDecimalMinimumValue_Value()
  {
    return (EAttribute)slotDecimalMinimumValueEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSlotDecimalMaximumValue()
  {
    return slotDecimalMaximumValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSlotDecimalMaximumValue_Exclusive()
  {
    return (EAttribute)slotDecimalMaximumValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSlotDecimalMaximumValue_Value()
  {
    return (EAttribute)slotDecimalMaximumValueEClass.getEStructuralFeatures().get(1);
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

    conceptIdReplacementSlotEClass = createEClass(CONCEPT_ID_REPLACEMENT_SLOT);

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
    createEAttribute(stringReplacementSlotEClass, STRING_REPLACEMENT_SLOT__VALUES);

    integerReplacementSlotEClass = createEClass(INTEGER_REPLACEMENT_SLOT);
    createEReference(integerReplacementSlotEClass, INTEGER_REPLACEMENT_SLOT__VALUES);

    decimalReplacementSlotEClass = createEClass(DECIMAL_REPLACEMENT_SLOT);
    createEReference(decimalReplacementSlotEClass, DECIMAL_REPLACEMENT_SLOT__VALUES);

    etlCardinalityEClass = createEClass(ETL_CARDINALITY);
    createEAttribute(etlCardinalityEClass, ETL_CARDINALITY__MIN);
    createEAttribute(etlCardinalityEClass, ETL_CARDINALITY__MAX);

    conceptReplacementSlotEClass = createEClass(CONCEPT_REPLACEMENT_SLOT);
    createEReference(conceptReplacementSlotEClass, CONCEPT_REPLACEMENT_SLOT__CONSTRAINT);
    createEAttribute(conceptReplacementSlotEClass, CONCEPT_REPLACEMENT_SLOT__NAME);

    conceptReferenceEClass = createEClass(CONCEPT_REFERENCE);
    createEReference(conceptReferenceEClass, CONCEPT_REFERENCE__SLOT);
    createEAttribute(conceptReferenceEClass, CONCEPT_REFERENCE__ID);
    createEAttribute(conceptReferenceEClass, CONCEPT_REFERENCE__TERM);

    stringValueEClass = createEClass(STRING_VALUE);
    createEAttribute(stringValueEClass, STRING_VALUE__VALUE);

    integerValueEClass = createEClass(INTEGER_VALUE);
    createEAttribute(integerValueEClass, INTEGER_VALUE__VALUE);

    decimalValueEClass = createEClass(DECIMAL_VALUE);
    createEAttribute(decimalValueEClass, DECIMAL_VALUE__VALUE);

    slotIntegerEClass = createEClass(SLOT_INTEGER);

    slotIntegerValueEClass = createEClass(SLOT_INTEGER_VALUE);
    createEAttribute(slotIntegerValueEClass, SLOT_INTEGER_VALUE__VALUE);

    slotIntegerRangeEClass = createEClass(SLOT_INTEGER_RANGE);
    createEReference(slotIntegerRangeEClass, SLOT_INTEGER_RANGE__MINIMUM);
    createEReference(slotIntegerRangeEClass, SLOT_INTEGER_RANGE__MAXIMUM);

    slotIntegerMinimumValueEClass = createEClass(SLOT_INTEGER_MINIMUM_VALUE);
    createEAttribute(slotIntegerMinimumValueEClass, SLOT_INTEGER_MINIMUM_VALUE__EXCLUSIVE);
    createEAttribute(slotIntegerMinimumValueEClass, SLOT_INTEGER_MINIMUM_VALUE__VALUE);

    slotIntegerMaximumValueEClass = createEClass(SLOT_INTEGER_MAXIMUM_VALUE);
    createEAttribute(slotIntegerMaximumValueEClass, SLOT_INTEGER_MAXIMUM_VALUE__EXCLUSIVE);
    createEAttribute(slotIntegerMaximumValueEClass, SLOT_INTEGER_MAXIMUM_VALUE__VALUE);

    slotDecimalEClass = createEClass(SLOT_DECIMAL);

    slotDecimalValueEClass = createEClass(SLOT_DECIMAL_VALUE);
    createEAttribute(slotDecimalValueEClass, SLOT_DECIMAL_VALUE__VALUE);

    slotDecimalRangeEClass = createEClass(SLOT_DECIMAL_RANGE);
    createEReference(slotDecimalRangeEClass, SLOT_DECIMAL_RANGE__MINIMUM);
    createEReference(slotDecimalRangeEClass, SLOT_DECIMAL_RANGE__MAXIMUM);

    slotDecimalMinimumValueEClass = createEClass(SLOT_DECIMAL_MINIMUM_VALUE);
    createEAttribute(slotDecimalMinimumValueEClass, SLOT_DECIMAL_MINIMUM_VALUE__EXCLUSIVE);
    createEAttribute(slotDecimalMinimumValueEClass, SLOT_DECIMAL_MINIMUM_VALUE__VALUE);

    slotDecimalMaximumValueEClass = createEClass(SLOT_DECIMAL_MAXIMUM_VALUE);
    createEAttribute(slotDecimalMaximumValueEClass, SLOT_DECIMAL_MAXIMUM_VALUE__EXCLUSIVE);
    createEAttribute(slotDecimalMaximumValueEClass, SLOT_DECIMAL_MAXIMUM_VALUE__VALUE);
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
    conceptIdReplacementSlotEClass.getESuperTypes().add(this.getConceptReplacementSlot());
    expressionReplacementSlotEClass.getESuperTypes().add(this.getConceptReplacementSlot());
    concreteValueReplacementSlotEClass.getESuperTypes().add(this.getAttributeValue());
    stringReplacementSlotEClass.getESuperTypes().add(this.getConcreteValueReplacementSlot());
    integerReplacementSlotEClass.getESuperTypes().add(this.getConcreteValueReplacementSlot());
    decimalReplacementSlotEClass.getESuperTypes().add(this.getConcreteValueReplacementSlot());
    conceptReferenceEClass.getESuperTypes().add(this.getAttributeValue());
    stringValueEClass.getESuperTypes().add(this.getAttributeValue());
    integerValueEClass.getESuperTypes().add(this.getAttributeValue());
    decimalValueEClass.getESuperTypes().add(this.getAttributeValue());
    slotIntegerValueEClass.getESuperTypes().add(this.getSlotInteger());
    slotIntegerRangeEClass.getESuperTypes().add(this.getSlotInteger());
    slotDecimalValueEClass.getESuperTypes().add(this.getSlotDecimal());
    slotDecimalRangeEClass.getESuperTypes().add(this.getSlotDecimal());

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

    initEClass(conceptIdReplacementSlotEClass, ConceptIdReplacementSlot.class, "ConceptIdReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(expressionReplacementSlotEClass, ExpressionReplacementSlot.class, "ExpressionReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(tokenReplacementSlotEClass, TokenReplacementSlot.class, "TokenReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTokenReplacementSlot_Tokens(), ecorePackage.getEString(), "tokens", null, 0, -1, TokenReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTokenReplacementSlot_Name(), ecorePackage.getEString(), "name", null, 0, 1, TokenReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(templateInformationSlotEClass, TemplateInformationSlot.class, "TemplateInformationSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTemplateInformationSlot_Cardinality(), this.getEtlCardinality(), null, "cardinality", null, 0, 1, TemplateInformationSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTemplateInformationSlot_Name(), ecorePackage.getEString(), "name", null, 0, 1, TemplateInformationSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(concreteValueReplacementSlotEClass, ConcreteValueReplacementSlot.class, "ConcreteValueReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getConcreteValueReplacementSlot_Name(), ecorePackage.getEString(), "name", null, 0, 1, ConcreteValueReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringReplacementSlotEClass, StringReplacementSlot.class, "StringReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringReplacementSlot_Values(), ecorePackage.getEString(), "values", null, 0, -1, StringReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerReplacementSlotEClass, IntegerReplacementSlot.class, "IntegerReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getIntegerReplacementSlot_Values(), this.getSlotInteger(), null, "values", null, 0, -1, IntegerReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalReplacementSlotEClass, DecimalReplacementSlot.class, "DecimalReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDecimalReplacementSlot_Values(), this.getSlotDecimal(), null, "values", null, 0, -1, DecimalReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(etlCardinalityEClass, EtlCardinality.class, "EtlCardinality", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEtlCardinality_Min(), ecorePackage.getEInt(), "min", null, 0, 1, EtlCardinality.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEtlCardinality_Max(), ecorePackage.getEInt(), "max", null, 0, 1, EtlCardinality.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conceptReplacementSlotEClass, ConceptReplacementSlot.class, "ConceptReplacementSlot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConceptReplacementSlot_Constraint(), theEclPackage.getExpressionConstraint(), null, "constraint", null, 0, 1, ConceptReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getConceptReplacementSlot_Name(), ecorePackage.getEString(), "name", null, 0, 1, ConceptReplacementSlot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conceptReferenceEClass, ConceptReference.class, "ConceptReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConceptReference_Slot(), this.getConceptReplacementSlot(), null, "slot", null, 0, 1, ConceptReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getConceptReference_Id(), ecorePackage.getEString(), "id", null, 0, 1, ConceptReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getConceptReference_Term(), ecorePackage.getEString(), "term", null, 0, 1, ConceptReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringValueEClass, StringValue.class, "StringValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringValue_Value(), ecorePackage.getEString(), "value", null, 0, 1, StringValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerValueEClass, IntegerValue.class, "IntegerValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntegerValue_Value(), ecorePackage.getEInt(), "value", null, 0, 1, IntegerValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(decimalValueEClass, DecimalValue.class, "DecimalValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDecimalValue_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, DecimalValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(slotIntegerEClass, SlotInteger.class, "SlotInteger", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(slotIntegerValueEClass, SlotIntegerValue.class, "SlotIntegerValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSlotIntegerValue_Value(), ecorePackage.getEInt(), "value", null, 0, 1, SlotIntegerValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(slotIntegerRangeEClass, SlotIntegerRange.class, "SlotIntegerRange", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSlotIntegerRange_Minimum(), this.getSlotIntegerMinimumValue(), null, "minimum", null, 0, 1, SlotIntegerRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSlotIntegerRange_Maximum(), this.getSlotIntegerMaximumValue(), null, "maximum", null, 0, 1, SlotIntegerRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(slotIntegerMinimumValueEClass, SlotIntegerMinimumValue.class, "SlotIntegerMinimumValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSlotIntegerMinimumValue_Exclusive(), ecorePackage.getEBoolean(), "exclusive", null, 0, 1, SlotIntegerMinimumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSlotIntegerMinimumValue_Value(), ecorePackage.getEInt(), "value", null, 0, 1, SlotIntegerMinimumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(slotIntegerMaximumValueEClass, SlotIntegerMaximumValue.class, "SlotIntegerMaximumValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSlotIntegerMaximumValue_Exclusive(), ecorePackage.getEBoolean(), "exclusive", null, 0, 1, SlotIntegerMaximumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSlotIntegerMaximumValue_Value(), ecorePackage.getEInt(), "value", null, 0, 1, SlotIntegerMaximumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(slotDecimalEClass, SlotDecimal.class, "SlotDecimal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(slotDecimalValueEClass, SlotDecimalValue.class, "SlotDecimalValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSlotDecimalValue_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, SlotDecimalValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(slotDecimalRangeEClass, SlotDecimalRange.class, "SlotDecimalRange", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSlotDecimalRange_Minimum(), this.getSlotDecimalMinimumValue(), null, "minimum", null, 0, 1, SlotDecimalRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSlotDecimalRange_Maximum(), this.getSlotDecimalMaximumValue(), null, "maximum", null, 0, 1, SlotDecimalRange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(slotDecimalMinimumValueEClass, SlotDecimalMinimumValue.class, "SlotDecimalMinimumValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSlotDecimalMinimumValue_Exclusive(), ecorePackage.getEBoolean(), "exclusive", null, 0, 1, SlotDecimalMinimumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSlotDecimalMinimumValue_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, SlotDecimalMinimumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(slotDecimalMaximumValueEClass, SlotDecimalMaximumValue.class, "SlotDecimalMaximumValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSlotDecimalMaximumValue_Exclusive(), ecorePackage.getEBoolean(), "exclusive", null, 0, 1, SlotDecimalMaximumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSlotDecimalMaximumValue_Value(), ecorePackage.getEBigDecimal(), "value", null, 0, 1, SlotDecimalMaximumValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} //EtlPackageImpl
