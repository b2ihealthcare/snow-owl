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

import com.b2international.snowowl.snomed.etl.etl.*;

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
public class EtlFactoryImpl extends EFactoryImpl implements EtlFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static EtlFactory init()
  {
    try
    {
      EtlFactory theEtlFactory = (EtlFactory)EPackage.Registry.INSTANCE.getEFactory(EtlPackage.eNS_URI);
      if (theEtlFactory != null)
      {
        return theEtlFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new EtlFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EtlFactoryImpl()
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
      case EtlPackage.EXPRESSION_TEMPLATE: return createExpressionTemplate();
      case EtlPackage.SUB_EXPRESSION: return createSubExpression();
      case EtlPackage.FOCUS_CONCEPT: return createFocusConcept();
      case EtlPackage.REFINEMENT: return createRefinement();
      case EtlPackage.ATTRIBUTE_GROUP: return createAttributeGroup();
      case EtlPackage.ATTRIBUTE: return createAttribute();
      case EtlPackage.ATTRIBUTE_VALUE: return createAttributeValue();
      case EtlPackage.CONCEPT_ID_REPLACEMENT_SLOT: return createConceptIdReplacementSlot();
      case EtlPackage.EXPRESSION_REPLACEMENT_SLOT: return createExpressionReplacementSlot();
      case EtlPackage.TOKEN_REPLACEMENT_SLOT: return createTokenReplacementSlot();
      case EtlPackage.TEMPLATE_INFORMATION_SLOT: return createTemplateInformationSlot();
      case EtlPackage.CONCRETE_VALUE_REPLACEMENT_SLOT: return createConcreteValueReplacementSlot();
      case EtlPackage.STRING_REPLACEMENT_SLOT: return createStringReplacementSlot();
      case EtlPackage.INTEGER_REPLACEMENT_SLOT: return createIntegerReplacementSlot();
      case EtlPackage.DECIMAL_REPLACEMENT_SLOT: return createDecimalReplacementSlot();
      case EtlPackage.ETL_CARDINALITY: return createEtlCardinality();
      case EtlPackage.CONCEPT_REPLACEMENT_SLOT: return createConceptReplacementSlot();
      case EtlPackage.CONCEPT_REFERENCE: return createConceptReference();
      case EtlPackage.STRING_VALUE: return createStringValue();
      case EtlPackage.INTEGER_VALUE: return createIntegerValue();
      case EtlPackage.DECIMAL_VALUE: return createDecimalValue();
      case EtlPackage.SLOT_INTEGER: return createSlotInteger();
      case EtlPackage.SLOT_INTEGER_VALUE: return createSlotIntegerValue();
      case EtlPackage.SLOT_INTEGER_RANGE: return createSlotIntegerRange();
      case EtlPackage.SLOT_INTEGER_MINIMUM_VALUE: return createSlotIntegerMinimumValue();
      case EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE: return createSlotIntegerMaximumValue();
      case EtlPackage.SLOT_DECIMAL: return createSlotDecimal();
      case EtlPackage.SLOT_DECIMAL_VALUE: return createSlotDecimalValue();
      case EtlPackage.SLOT_DECIMAL_RANGE: return createSlotDecimalRange();
      case EtlPackage.SLOT_DECIMAL_MINIMUM_VALUE: return createSlotDecimalMinimumValue();
      case EtlPackage.SLOT_DECIMAL_MAXIMUM_VALUE: return createSlotDecimalMaximumValue();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ExpressionTemplate createExpressionTemplate()
  {
    ExpressionTemplateImpl expressionTemplate = new ExpressionTemplateImpl();
    return expressionTemplate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SubExpression createSubExpression()
  {
    SubExpressionImpl subExpression = new SubExpressionImpl();
    return subExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public FocusConcept createFocusConcept()
  {
    FocusConceptImpl focusConcept = new FocusConceptImpl();
    return focusConcept;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Refinement createRefinement()
  {
    RefinementImpl refinement = new RefinementImpl();
    return refinement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public AttributeGroup createAttributeGroup()
  {
    AttributeGroupImpl attributeGroup = new AttributeGroupImpl();
    return attributeGroup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Attribute createAttribute()
  {
    AttributeImpl attribute = new AttributeImpl();
    return attribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public AttributeValue createAttributeValue()
  {
    AttributeValueImpl attributeValue = new AttributeValueImpl();
    return attributeValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ConceptIdReplacementSlot createConceptIdReplacementSlot()
  {
    ConceptIdReplacementSlotImpl conceptIdReplacementSlot = new ConceptIdReplacementSlotImpl();
    return conceptIdReplacementSlot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ExpressionReplacementSlot createExpressionReplacementSlot()
  {
    ExpressionReplacementSlotImpl expressionReplacementSlot = new ExpressionReplacementSlotImpl();
    return expressionReplacementSlot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TokenReplacementSlot createTokenReplacementSlot()
  {
    TokenReplacementSlotImpl tokenReplacementSlot = new TokenReplacementSlotImpl();
    return tokenReplacementSlot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TemplateInformationSlot createTemplateInformationSlot()
  {
    TemplateInformationSlotImpl templateInformationSlot = new TemplateInformationSlotImpl();
    return templateInformationSlot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ConcreteValueReplacementSlot createConcreteValueReplacementSlot()
  {
    ConcreteValueReplacementSlotImpl concreteValueReplacementSlot = new ConcreteValueReplacementSlotImpl();
    return concreteValueReplacementSlot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public StringReplacementSlot createStringReplacementSlot()
  {
    StringReplacementSlotImpl stringReplacementSlot = new StringReplacementSlotImpl();
    return stringReplacementSlot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public IntegerReplacementSlot createIntegerReplacementSlot()
  {
    IntegerReplacementSlotImpl integerReplacementSlot = new IntegerReplacementSlotImpl();
    return integerReplacementSlot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DecimalReplacementSlot createDecimalReplacementSlot()
  {
    DecimalReplacementSlotImpl decimalReplacementSlot = new DecimalReplacementSlotImpl();
    return decimalReplacementSlot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EtlCardinality createEtlCardinality()
  {
    EtlCardinalityImpl etlCardinality = new EtlCardinalityImpl();
    return etlCardinality;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ConceptReplacementSlot createConceptReplacementSlot()
  {
    ConceptReplacementSlotImpl conceptReplacementSlot = new ConceptReplacementSlotImpl();
    return conceptReplacementSlot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
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
  @Override
  public StringValue createStringValue()
  {
    StringValueImpl stringValue = new StringValueImpl();
    return stringValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public IntegerValue createIntegerValue()
  {
    IntegerValueImpl integerValue = new IntegerValueImpl();
    return integerValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DecimalValue createDecimalValue()
  {
    DecimalValueImpl decimalValue = new DecimalValueImpl();
    return decimalValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SlotInteger createSlotInteger()
  {
    SlotIntegerImpl slotInteger = new SlotIntegerImpl();
    return slotInteger;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SlotIntegerValue createSlotIntegerValue()
  {
    SlotIntegerValueImpl slotIntegerValue = new SlotIntegerValueImpl();
    return slotIntegerValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SlotIntegerRange createSlotIntegerRange()
  {
    SlotIntegerRangeImpl slotIntegerRange = new SlotIntegerRangeImpl();
    return slotIntegerRange;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SlotIntegerMinimumValue createSlotIntegerMinimumValue()
  {
    SlotIntegerMinimumValueImpl slotIntegerMinimumValue = new SlotIntegerMinimumValueImpl();
    return slotIntegerMinimumValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SlotIntegerMaximumValue createSlotIntegerMaximumValue()
  {
    SlotIntegerMaximumValueImpl slotIntegerMaximumValue = new SlotIntegerMaximumValueImpl();
    return slotIntegerMaximumValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SlotDecimal createSlotDecimal()
  {
    SlotDecimalImpl slotDecimal = new SlotDecimalImpl();
    return slotDecimal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SlotDecimalValue createSlotDecimalValue()
  {
    SlotDecimalValueImpl slotDecimalValue = new SlotDecimalValueImpl();
    return slotDecimalValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SlotDecimalRange createSlotDecimalRange()
  {
    SlotDecimalRangeImpl slotDecimalRange = new SlotDecimalRangeImpl();
    return slotDecimalRange;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SlotDecimalMinimumValue createSlotDecimalMinimumValue()
  {
    SlotDecimalMinimumValueImpl slotDecimalMinimumValue = new SlotDecimalMinimumValueImpl();
    return slotDecimalMinimumValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SlotDecimalMaximumValue createSlotDecimalMaximumValue()
  {
    SlotDecimalMaximumValueImpl slotDecimalMaximumValue = new SlotDecimalMaximumValueImpl();
    return slotDecimalMaximumValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EtlPackage getEtlPackage()
  {
    return (EtlPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static EtlPackage getPackage()
  {
    return EtlPackage.eINSTANCE;
  }

} //EtlFactoryImpl
