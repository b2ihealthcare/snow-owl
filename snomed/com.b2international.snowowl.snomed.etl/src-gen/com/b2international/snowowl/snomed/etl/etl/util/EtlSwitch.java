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
package com.b2international.snowowl.snomed.etl.etl.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

import com.b2international.snowowl.snomed.etl.etl.*;

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
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage
 * @generated
 */
public class EtlSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static EtlPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EtlSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = EtlPackage.eINSTANCE;
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
      case EtlPackage.EXPRESSION_TEMPLATE:
      {
        ExpressionTemplate expressionTemplate = (ExpressionTemplate)theEObject;
        T result = caseExpressionTemplate(expressionTemplate);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.SUB_EXPRESSION:
      {
        SubExpression subExpression = (SubExpression)theEObject;
        T result = caseSubExpression(subExpression);
        if (result == null) result = caseAttributeValue(subExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.FOCUS_CONCEPT:
      {
        FocusConcept focusConcept = (FocusConcept)theEObject;
        T result = caseFocusConcept(focusConcept);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.REFINEMENT:
      {
        Refinement refinement = (Refinement)theEObject;
        T result = caseRefinement(refinement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.ATTRIBUTE_GROUP:
      {
        AttributeGroup attributeGroup = (AttributeGroup)theEObject;
        T result = caseAttributeGroup(attributeGroup);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.ATTRIBUTE:
      {
        Attribute attribute = (Attribute)theEObject;
        T result = caseAttribute(attribute);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.ATTRIBUTE_VALUE:
      {
        AttributeValue attributeValue = (AttributeValue)theEObject;
        T result = caseAttributeValue(attributeValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.CONCEPT_ID_REPLACEMENT_SLOT:
      {
        ConceptIdReplacementSlot conceptIdReplacementSlot = (ConceptIdReplacementSlot)theEObject;
        T result = caseConceptIdReplacementSlot(conceptIdReplacementSlot);
        if (result == null) result = caseConceptReplacementSlot(conceptIdReplacementSlot);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.EXPRESSION_REPLACEMENT_SLOT:
      {
        ExpressionReplacementSlot expressionReplacementSlot = (ExpressionReplacementSlot)theEObject;
        T result = caseExpressionReplacementSlot(expressionReplacementSlot);
        if (result == null) result = caseConceptReplacementSlot(expressionReplacementSlot);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.TOKEN_REPLACEMENT_SLOT:
      {
        TokenReplacementSlot tokenReplacementSlot = (TokenReplacementSlot)theEObject;
        T result = caseTokenReplacementSlot(tokenReplacementSlot);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.TEMPLATE_INFORMATION_SLOT:
      {
        TemplateInformationSlot templateInformationSlot = (TemplateInformationSlot)theEObject;
        T result = caseTemplateInformationSlot(templateInformationSlot);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.CONCRETE_VALUE_REPLACEMENT_SLOT:
      {
        ConcreteValueReplacementSlot concreteValueReplacementSlot = (ConcreteValueReplacementSlot)theEObject;
        T result = caseConcreteValueReplacementSlot(concreteValueReplacementSlot);
        if (result == null) result = caseAttributeValue(concreteValueReplacementSlot);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.STRING_REPLACEMENT_SLOT:
      {
        StringReplacementSlot stringReplacementSlot = (StringReplacementSlot)theEObject;
        T result = caseStringReplacementSlot(stringReplacementSlot);
        if (result == null) result = caseConcreteValueReplacementSlot(stringReplacementSlot);
        if (result == null) result = caseAttributeValue(stringReplacementSlot);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.INTEGER_REPLACEMENT_SLOT:
      {
        IntegerReplacementSlot integerReplacementSlot = (IntegerReplacementSlot)theEObject;
        T result = caseIntegerReplacementSlot(integerReplacementSlot);
        if (result == null) result = caseConcreteValueReplacementSlot(integerReplacementSlot);
        if (result == null) result = caseAttributeValue(integerReplacementSlot);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.DECIMAL_REPLACEMENT_SLOT:
      {
        DecimalReplacementSlot decimalReplacementSlot = (DecimalReplacementSlot)theEObject;
        T result = caseDecimalReplacementSlot(decimalReplacementSlot);
        if (result == null) result = caseConcreteValueReplacementSlot(decimalReplacementSlot);
        if (result == null) result = caseAttributeValue(decimalReplacementSlot);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.ETL_CARDINALITY:
      {
        EtlCardinality etlCardinality = (EtlCardinality)theEObject;
        T result = caseEtlCardinality(etlCardinality);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.CONCEPT_REPLACEMENT_SLOT:
      {
        ConceptReplacementSlot conceptReplacementSlot = (ConceptReplacementSlot)theEObject;
        T result = caseConceptReplacementSlot(conceptReplacementSlot);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.CONCEPT_REFERENCE:
      {
        ConceptReference conceptReference = (ConceptReference)theEObject;
        T result = caseConceptReference(conceptReference);
        if (result == null) result = caseAttributeValue(conceptReference);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.STRING_VALUE:
      {
        StringValue stringValue = (StringValue)theEObject;
        T result = caseStringValue(stringValue);
        if (result == null) result = caseAttributeValue(stringValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.INTEGER_VALUE:
      {
        IntegerValue integerValue = (IntegerValue)theEObject;
        T result = caseIntegerValue(integerValue);
        if (result == null) result = caseAttributeValue(integerValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.DECIMAL_VALUE:
      {
        DecimalValue decimalValue = (DecimalValue)theEObject;
        T result = caseDecimalValue(decimalValue);
        if (result == null) result = caseAttributeValue(decimalValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.SLOT_INTEGER:
      {
        SlotInteger slotInteger = (SlotInteger)theEObject;
        T result = caseSlotInteger(slotInteger);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.SLOT_INTEGER_VALUE:
      {
        SlotIntegerValue slotIntegerValue = (SlotIntegerValue)theEObject;
        T result = caseSlotIntegerValue(slotIntegerValue);
        if (result == null) result = caseSlotInteger(slotIntegerValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.SLOT_INTEGER_RANGE:
      {
        SlotIntegerRange slotIntegerRange = (SlotIntegerRange)theEObject;
        T result = caseSlotIntegerRange(slotIntegerRange);
        if (result == null) result = caseSlotInteger(slotIntegerRange);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.SLOT_INTEGER_MINIMUM_VALUE:
      {
        SlotIntegerMinimumValue slotIntegerMinimumValue = (SlotIntegerMinimumValue)theEObject;
        T result = caseSlotIntegerMinimumValue(slotIntegerMinimumValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.SLOT_INTEGER_MAXIMUM_VALUE:
      {
        SlotIntegerMaximumValue slotIntegerMaximumValue = (SlotIntegerMaximumValue)theEObject;
        T result = caseSlotIntegerMaximumValue(slotIntegerMaximumValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.SLOT_DECIMAL:
      {
        SlotDecimal slotDecimal = (SlotDecimal)theEObject;
        T result = caseSlotDecimal(slotDecimal);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.SLOT_DECIMAL_VALUE:
      {
        SlotDecimalValue slotDecimalValue = (SlotDecimalValue)theEObject;
        T result = caseSlotDecimalValue(slotDecimalValue);
        if (result == null) result = caseSlotDecimal(slotDecimalValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.SLOT_DECIMAL_RANGE:
      {
        SlotDecimalRange slotDecimalRange = (SlotDecimalRange)theEObject;
        T result = caseSlotDecimalRange(slotDecimalRange);
        if (result == null) result = caseSlotDecimal(slotDecimalRange);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.SLOT_DECIMAL_MINIMUM_VALUE:
      {
        SlotDecimalMinimumValue slotDecimalMinimumValue = (SlotDecimalMinimumValue)theEObject;
        T result = caseSlotDecimalMinimumValue(slotDecimalMinimumValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case EtlPackage.SLOT_DECIMAL_MAXIMUM_VALUE:
      {
        SlotDecimalMaximumValue slotDecimalMaximumValue = (SlotDecimalMaximumValue)theEObject;
        T result = caseSlotDecimalMaximumValue(slotDecimalMaximumValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Expression Template</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Expression Template</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExpressionTemplate(ExpressionTemplate object)
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
   * Returns the result of interpreting the object as an instance of '<em>Focus Concept</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Focus Concept</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFocusConcept(FocusConcept object)
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
   * Returns the result of interpreting the object as an instance of '<em>Attribute Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttributeValue(AttributeValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Concept Id Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Concept Id Replacement Slot</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConceptIdReplacementSlot(ConceptIdReplacementSlot object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Expression Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Expression Replacement Slot</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExpressionReplacementSlot(ExpressionReplacementSlot object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Token Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Token Replacement Slot</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTokenReplacementSlot(TokenReplacementSlot object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Template Information Slot</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Template Information Slot</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTemplateInformationSlot(TemplateInformationSlot object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Concrete Value Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Concrete Value Replacement Slot</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConcreteValueReplacementSlot(ConcreteValueReplacementSlot object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>String Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>String Replacement Slot</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStringReplacementSlot(StringReplacementSlot object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Integer Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Integer Replacement Slot</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntegerReplacementSlot(IntegerReplacementSlot object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Decimal Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Decimal Replacement Slot</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDecimalReplacementSlot(DecimalReplacementSlot object)
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
  public T caseEtlCardinality(EtlCardinality object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Concept Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Concept Replacement Slot</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConceptReplacementSlot(ConceptReplacementSlot object)
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
   * Returns the result of interpreting the object as an instance of '<em>String Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>String Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStringValue(StringValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Integer Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Integer Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntegerValue(IntegerValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Decimal Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Decimal Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDecimalValue(DecimalValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Slot Integer</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Slot Integer</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSlotInteger(SlotInteger object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Slot Integer Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Slot Integer Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSlotIntegerValue(SlotIntegerValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Slot Integer Range</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Slot Integer Range</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSlotIntegerRange(SlotIntegerRange object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Slot Integer Minimum Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Slot Integer Minimum Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSlotIntegerMinimumValue(SlotIntegerMinimumValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Slot Integer Maximum Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Slot Integer Maximum Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSlotIntegerMaximumValue(SlotIntegerMaximumValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Slot Decimal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Slot Decimal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSlotDecimal(SlotDecimal object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Slot Decimal Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Slot Decimal Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSlotDecimalValue(SlotDecimalValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Slot Decimal Range</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Slot Decimal Range</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSlotDecimalRange(SlotDecimalRange object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Slot Decimal Minimum Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Slot Decimal Minimum Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSlotDecimalMinimumValue(SlotDecimalMinimumValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Slot Decimal Maximum Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Slot Decimal Maximum Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSlotDecimalMaximumValue(SlotDecimalMaximumValue object)
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

} //EtlSwitch
