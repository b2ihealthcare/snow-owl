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
package com.b2international.snowowl.dsl.scg.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.dsl.scg.ScgPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Expression</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.dsl.scg.impl.ExpressionImpl#getConcepts <em>Concepts</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.scg.impl.ExpressionImpl#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link com.b2international.snowowl.dsl.scg.impl.ExpressionImpl#getGroups <em>Groups</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExpressionImpl extends AttributeValueImpl implements Expression
{
  /**
   * The cached value of the '{@link #getConcepts() <em>Concepts</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConcepts()
   * @generated
   * @ordered
   */
  protected EList<Concept> concepts;

  /**
   * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributes()
   * @generated
   * @ordered
   */
  protected EList<Attribute> attributes;

  /**
   * The cached value of the '{@link #getGroups() <em>Groups</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGroups()
   * @generated
   * @ordered
   */
  protected EList<Group> groups;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ExpressionImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ScgPackage.Literals.EXPRESSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Concept> getConcepts()
  {
    if (concepts == null)
    {
      concepts = new EObjectContainmentEList<Concept>(Concept.class, this, ScgPackage.EXPRESSION__CONCEPTS);
    }
    return concepts;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Attribute> getAttributes()
  {
    if (attributes == null)
    {
      attributes = new EObjectContainmentEList<Attribute>(Attribute.class, this, ScgPackage.EXPRESSION__ATTRIBUTES);
    }
    return attributes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Group> getGroups()
  {
    if (groups == null)
    {
      groups = new EObjectContainmentEList<Group>(Group.class, this, ScgPackage.EXPRESSION__GROUPS);
    }
    return groups;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case ScgPackage.EXPRESSION__CONCEPTS:
        return ((InternalEList<?>)getConcepts()).basicRemove(otherEnd, msgs);
      case ScgPackage.EXPRESSION__ATTRIBUTES:
        return ((InternalEList<?>)getAttributes()).basicRemove(otherEnd, msgs);
      case ScgPackage.EXPRESSION__GROUPS:
        return ((InternalEList<?>)getGroups()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case ScgPackage.EXPRESSION__CONCEPTS:
        return getConcepts();
      case ScgPackage.EXPRESSION__ATTRIBUTES:
        return getAttributes();
      case ScgPackage.EXPRESSION__GROUPS:
        return getGroups();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case ScgPackage.EXPRESSION__CONCEPTS:
        getConcepts().clear();
        getConcepts().addAll((Collection<? extends Concept>)newValue);
        return;
      case ScgPackage.EXPRESSION__ATTRIBUTES:
        getAttributes().clear();
        getAttributes().addAll((Collection<? extends Attribute>)newValue);
        return;
      case ScgPackage.EXPRESSION__GROUPS:
        getGroups().clear();
        getGroups().addAll((Collection<? extends Group>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case ScgPackage.EXPRESSION__CONCEPTS:
        getConcepts().clear();
        return;
      case ScgPackage.EXPRESSION__ATTRIBUTES:
        getAttributes().clear();
        return;
      case ScgPackage.EXPRESSION__GROUPS:
        getGroups().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case ScgPackage.EXPRESSION__CONCEPTS:
        return concepts != null && !concepts.isEmpty();
      case ScgPackage.EXPRESSION__ATTRIBUTES:
        return attributes != null && !attributes.isEmpty();
      case ScgPackage.EXPRESSION__GROUPS:
        return groups != null && !groups.isEmpty();
    }
    return super.eIsSet(featureID);
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer();
    
    // get all LValue labels
    List<String> lValueLabels = new ArrayList<String>();
    for (Concept lValue : getConcepts()) {
		lValueLabels.add(lValue.toString());
	}
    // sort LValue labels
    Collections.sort(lValueLabels);
    
	for (Iterator<String> iterator = lValueLabels.iterator(); iterator.hasNext();) {
		result.append(iterator.next());
		if (iterator.hasNext())
			result.append('+');
	}
	// return if no refinements
    if (getAttributes().isEmpty() && getGroups().isEmpty()) {
    	return result.toString();
    }
    result.append(':');
    result.append(refinementsToString());
    return result.toString();
  }
  
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  private String refinementsToString()
  {
	StringBuffer result = new StringBuffer();
	// ungrouped attributes
	// get all attribute labels
	List<String> ungroupedAttributeLabels = new ArrayList<String>();
	for (Attribute ungroupedAttribute : getAttributes()) {
		ungroupedAttributeLabels.add(ungroupedAttribute.toString());
	}
	
	// sort attribute labels alphabetically
	Collections.sort(ungroupedAttributeLabels);
	
	for (Iterator<String> iterator = ungroupedAttributeLabels.iterator(); iterator.hasNext();) {
		String attributeLabel = (String) iterator.next();
		result.append(attributeLabel);
		if (iterator.hasNext())
			result.append(',');
	}
	// attribute groups
	List<String> attributeGroupLabels = new ArrayList<String>();
	// get all attribute groups labels
	for (Group attributeGroup : getGroups()) {
		attributeGroupLabels.add(attributeGroup.toString());
	}
	
	// sort attribute group labels
	Collections.sort(attributeGroupLabels);
	
	for (Iterator<String> iterator = attributeGroupLabels.iterator(); iterator.hasNext();) {
		result.append(iterator.next());
	}
	
	return result.toString();
  }

} //ExpressionImpl