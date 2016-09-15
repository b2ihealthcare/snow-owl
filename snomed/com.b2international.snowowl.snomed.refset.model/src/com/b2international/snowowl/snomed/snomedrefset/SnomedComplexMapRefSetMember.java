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
package com.b2international.snowowl.snomed.snomedrefset;


/**
 * Represents a complex map type reference set member. Its purpose is to establish mapping between a given SNOMED&nbsp;CT concept
 * and one or more codes in a target scheme.
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapGroup <em>Map Group</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapPriority <em>Map Priority</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapRule <em>Map Rule</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapAdvice <em>Map Advice</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getCorrelationId <em>Correlation Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedComplexMapRefSetMember()
 * @model
 * @generated
 */
public interface SnomedComplexMapRefSetMember extends SnomedSimpleMapRefSetMember {
	/**
	 * &quotAn integer, grouping a set of complex map records from which one may be selected as a target code. 
	 * Where a SNOMED&nbsp;CT concept maps onto 'n' target codes, there will be 'n' groups, each containing 
	 * one or more complex map records.&quot<b>[1]</b><br>
	 * <b>[1]:</b>&nbsp;SNOMED&nbsp;CT Technical Implementation Guider January 2013. (5.5.2.7.2)
	 * @return the map group.
	 * @see #setMapGroup(byte)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedComplexMapRefSetMember_MapGroup()
	 * @model required="true"
	 * @generated
	 */
	int getMapGroup();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapGroup <em>Map Group</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Map Group</em>' attribute.
	 * @see #getMapGroup()
	 * @generated
	 */
	void setMapGroup(int value);

	/**
	 * &quotWithin a group, the mapPriority specifies the order in which complex map records should be checked. 
	 * Only the first map record meeting the run - time selection criteria will be taken as the target code 
	 * within the group of alternate codes.&quot<b>[1]</b><br>
	 * <b>[1]:</b>&nbsp;SNOMED&nbsp;CT Technical Implementation Guider January 2013. (5.5.2.7.2)
	 * @return the map priority.
	 * @see #setMapPriority(byte)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedComplexMapRefSetMember_MapPriority()
	 * @model required="true"
	 * @generated
	 */
	int getMapPriority();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapPriority <em>Map Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Map Priority</em>' attribute.
	 * @see #getMapPriority()
	 * @generated
	 */
	void setMapPriority(int value);

	/**
	 * &quotA machine-readable rule, (evaluating to either 'true' or 'false' at run-time) that indicates whether 
	 * this map record should be selected within its.&quot<b>[1]</b><br>
	 * <b>[1]:</b>&nbsp;SNOMED&nbsp;CT Technical Implementation Guider January 2013. (5.5.2.7.2)
	 * @return the map rule of the reference set member.
	 * @see #setMapRule(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedComplexMapRefSetMember_MapRule()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getMapRule();

	/**
	 * Counterpart of the {@link #getMapRule()}.
	 * @param value the new value of the map rule.
	 * @see #getMapRule()
	 * @generated
	 */
	void setMapRule(String value);

	/**
	 * &quotHuman-readable advice, that may be employed by the software vendor to give an end-user advice on selection 
	 * of the appropriate target code from the alternatives presented to him within the group.&quot<b>[1]</b><br>
	 * <b>[1]:</b>&nbsp;SNOMED&nbsp;CT Technical Implementation Guider January 2013. (5.5.2.7.2)
	 * @return the map advice.
	 * @see #setMapAdvice(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedComplexMapRefSetMember_MapAdvice()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getMapAdvice();

	/**
	 * Counterpart of {@link #getMapAdvice()}.
	 * @param value the new map advice value.
	 * @see #getMapAdvice()
	 * @generated
	 */
	void setMapAdvice(String value);

	/**
	 * &quotA child of |Map correlation value| in the metadata hierarchy, identifying the correlation
	 *  between the SNOMED CT concept and the target code.&quot<b>[1]</b><br>
	 * <b>[1]:</b>&nbsp;SNOMED&nbsp;CT Technical Implementation Guider January 2013. (5.5.2.7.2)
	 * @return the value of the '<em>Correlation Id</em>' attribute.
	 * @see #setCorrelationId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedComplexMapRefSetMember_CorrelationId()
	 * @model required="true"
	 * @generated
	 */
	String getCorrelationId();

	/**
	 * Counterpart of the {@link #getCorrelationId()}.
	 * @param value the new correlation concept identifier. 
	 * @see #getCorrelationId()
	 * @generated
	 */
	void setCorrelationId(String value);

	/**
	 * Returns with a SNOMED&nbsp;CT concept ID representing the map category for the current complex map type reference set.
	 * The followings can be interpreted as the map category:
	 * <ul>
	 * <li>Properly classified.</li>
	 * <li>Patient context employed in map rule property for the mapping.</li>
	 * <li>Cannot be classified with available data.</li>
	 * </ul>
	 * @return the concept ID of the map category for the complex map type reference set member.
	 * @see #setMapCategoryId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedComplexMapRefSetMember_MapCategoryId()
	 * @model required="true"
	 * @generated
	 */
	String getMapCategoryId();

	/**
	 * Counterpart of the {@link #getMapCategoryId()}.
	 * @param value the SNOMED&nbsp;CT concept ID of the map target category.
	 * @see #getMapCategoryId()
	 * @generated
	 */
	void setMapCategoryId(String value);

} // SnomedComplexMapRefSetMember