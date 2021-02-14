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
package com.b2international.snowowl.snomed.etl.etl.util;

import com.b2international.snowowl.snomed.etl.etl.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage
 * @generated
 */
public class EtlAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static EtlPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EtlAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = EtlPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EtlSwitch<Adapter> modelSwitch =
    new EtlSwitch<Adapter>()
    {
      @Override
      public Adapter caseExpressionTemplate(ExpressionTemplate object)
      {
        return createExpressionTemplateAdapter();
      }
      @Override
      public Adapter caseSubExpression(SubExpression object)
      {
        return createSubExpressionAdapter();
      }
      @Override
      public Adapter caseFocusConcept(FocusConcept object)
      {
        return createFocusConceptAdapter();
      }
      @Override
      public Adapter caseRefinement(Refinement object)
      {
        return createRefinementAdapter();
      }
      @Override
      public Adapter caseAttributeGroup(AttributeGroup object)
      {
        return createAttributeGroupAdapter();
      }
      @Override
      public Adapter caseAttribute(Attribute object)
      {
        return createAttributeAdapter();
      }
      @Override
      public Adapter caseAttributeValue(AttributeValue object)
      {
        return createAttributeValueAdapter();
      }
      @Override
      public Adapter caseConceptIdReplacementSlot(ConceptIdReplacementSlot object)
      {
        return createConceptIdReplacementSlotAdapter();
      }
      @Override
      public Adapter caseExpressionReplacementSlot(ExpressionReplacementSlot object)
      {
        return createExpressionReplacementSlotAdapter();
      }
      @Override
      public Adapter caseTokenReplacementSlot(TokenReplacementSlot object)
      {
        return createTokenReplacementSlotAdapter();
      }
      @Override
      public Adapter caseTemplateInformationSlot(TemplateInformationSlot object)
      {
        return createTemplateInformationSlotAdapter();
      }
      @Override
      public Adapter caseConcreteValueReplacementSlot(ConcreteValueReplacementSlot object)
      {
        return createConcreteValueReplacementSlotAdapter();
      }
      @Override
      public Adapter caseStringReplacementSlot(StringReplacementSlot object)
      {
        return createStringReplacementSlotAdapter();
      }
      @Override
      public Adapter caseIntegerReplacementSlot(IntegerReplacementSlot object)
      {
        return createIntegerReplacementSlotAdapter();
      }
      @Override
      public Adapter caseDecimalReplacementSlot(DecimalReplacementSlot object)
      {
        return createDecimalReplacementSlotAdapter();
      }
      @Override
      public Adapter caseEtlCardinality(EtlCardinality object)
      {
        return createEtlCardinalityAdapter();
      }
      @Override
      public Adapter caseConceptReplacementSlot(ConceptReplacementSlot object)
      {
        return createConceptReplacementSlotAdapter();
      }
      @Override
      public Adapter caseConceptReference(ConceptReference object)
      {
        return createConceptReferenceAdapter();
      }
      @Override
      public Adapter caseStringValue(StringValue object)
      {
        return createStringValueAdapter();
      }
      @Override
      public Adapter caseIntegerValue(IntegerValue object)
      {
        return createIntegerValueAdapter();
      }
      @Override
      public Adapter caseDecimalValue(DecimalValue object)
      {
        return createDecimalValueAdapter();
      }
      @Override
      public Adapter caseSlotInteger(SlotInteger object)
      {
        return createSlotIntegerAdapter();
      }
      @Override
      public Adapter caseSlotIntegerValue(SlotIntegerValue object)
      {
        return createSlotIntegerValueAdapter();
      }
      @Override
      public Adapter caseSlotIntegerRange(SlotIntegerRange object)
      {
        return createSlotIntegerRangeAdapter();
      }
      @Override
      public Adapter caseSlotIntegerMinimumValue(SlotIntegerMinimumValue object)
      {
        return createSlotIntegerMinimumValueAdapter();
      }
      @Override
      public Adapter caseSlotIntegerMaximumValue(SlotIntegerMaximumValue object)
      {
        return createSlotIntegerMaximumValueAdapter();
      }
      @Override
      public Adapter caseSlotDecimal(SlotDecimal object)
      {
        return createSlotDecimalAdapter();
      }
      @Override
      public Adapter caseSlotDecimalValue(SlotDecimalValue object)
      {
        return createSlotDecimalValueAdapter();
      }
      @Override
      public Adapter caseSlotDecimalRange(SlotDecimalRange object)
      {
        return createSlotDecimalRangeAdapter();
      }
      @Override
      public Adapter caseSlotDecimalMinimumValue(SlotDecimalMinimumValue object)
      {
        return createSlotDecimalMinimumValueAdapter();
      }
      @Override
      public Adapter caseSlotDecimalMaximumValue(SlotDecimalMaximumValue object)
      {
        return createSlotDecimalMaximumValueAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate <em>Expression Template</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate
   * @generated
   */
  public Adapter createExpressionTemplateAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.SubExpression <em>Sub Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.SubExpression
   * @generated
   */
  public Adapter createSubExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.FocusConcept <em>Focus Concept</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.FocusConcept
   * @generated
   */
  public Adapter createFocusConceptAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.Refinement <em>Refinement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.Refinement
   * @generated
   */
  public Adapter createRefinementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.AttributeGroup <em>Attribute Group</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.AttributeGroup
   * @generated
   */
  public Adapter createAttributeGroupAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.Attribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.Attribute
   * @generated
   */
  public Adapter createAttributeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.AttributeValue <em>Attribute Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.AttributeValue
   * @generated
   */
  public Adapter createAttributeValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.ConceptIdReplacementSlot <em>Concept Id Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.ConceptIdReplacementSlot
   * @generated
   */
  public Adapter createConceptIdReplacementSlotAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.ExpressionReplacementSlot <em>Expression Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.ExpressionReplacementSlot
   * @generated
   */
  public Adapter createExpressionReplacementSlotAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot <em>Token Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot
   * @generated
   */
  public Adapter createTokenReplacementSlotAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.TemplateInformationSlot <em>Template Information Slot</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.TemplateInformationSlot
   * @generated
   */
  public Adapter createTemplateInformationSlotAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.ConcreteValueReplacementSlot <em>Concrete Value Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.ConcreteValueReplacementSlot
   * @generated
   */
  public Adapter createConcreteValueReplacementSlotAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.StringReplacementSlot <em>String Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.StringReplacementSlot
   * @generated
   */
  public Adapter createStringReplacementSlotAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.IntegerReplacementSlot <em>Integer Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.IntegerReplacementSlot
   * @generated
   */
  public Adapter createIntegerReplacementSlotAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.DecimalReplacementSlot <em>Decimal Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.DecimalReplacementSlot
   * @generated
   */
  public Adapter createDecimalReplacementSlotAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.EtlCardinality <em>Cardinality</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.EtlCardinality
   * @generated
   */
  public Adapter createEtlCardinalityAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot <em>Concept Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot
   * @generated
   */
  public Adapter createConceptReplacementSlotAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReference <em>Concept Reference</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.ConceptReference
   * @generated
   */
  public Adapter createConceptReferenceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.StringValue <em>String Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.StringValue
   * @generated
   */
  public Adapter createStringValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.IntegerValue <em>Integer Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.IntegerValue
   * @generated
   */
  public Adapter createIntegerValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.DecimalValue <em>Decimal Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.DecimalValue
   * @generated
   */
  public Adapter createDecimalValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.SlotInteger <em>Slot Integer</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotInteger
   * @generated
   */
  public Adapter createSlotIntegerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerValue <em>Slot Integer Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerValue
   * @generated
   */
  public Adapter createSlotIntegerValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange <em>Slot Integer Range</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange
   * @generated
   */
  public Adapter createSlotIntegerRangeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerMinimumValue <em>Slot Integer Minimum Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerMinimumValue
   * @generated
   */
  public Adapter createSlotIntegerMinimumValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerMaximumValue <em>Slot Integer Maximum Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerMaximumValue
   * @generated
   */
  public Adapter createSlotIntegerMaximumValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimal <em>Slot Decimal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimal
   * @generated
   */
  public Adapter createSlotDecimalAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalValue <em>Slot Decimal Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalValue
   * @generated
   */
  public Adapter createSlotDecimalValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalRange <em>Slot Decimal Range</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalRange
   * @generated
   */
  public Adapter createSlotDecimalRangeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalMinimumValue <em>Slot Decimal Minimum Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalMinimumValue
   * @generated
   */
  public Adapter createSlotDecimalMinimumValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalMaximumValue <em>Slot Decimal Maximum Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalMaximumValue
   * @generated
   */
  public Adapter createSlotDecimalMaximumValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //EtlAdapterFactory
