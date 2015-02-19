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

import java.util.Date;

import org.eclipse.emf.cdo.CDOObject;

/**
 * Base representation of a SNOMED&nbsp;CT reference set member.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getReferencedComponentType <em>Referenced Component Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getEffectiveTime <em>Effective Time</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#isActive <em>Active</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getRefSet <em>Ref Set</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#isReleased <em>Released</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getReferencedComponentId <em>Referenced Component Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getModuleId <em>Module Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getRefSetIdentifierId <em>Ref Set Identifier Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSetMember()
 * @model
 * @generated
 */
public interface SnomedRefSetMember extends CDOObject {
	/**
	 * Returns with the application specific type of the component referenced by the current reference set member.
	 * @return the application specific terminology component type of the referenced component.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSetMember_ReferencedComponentType()
	 * @model required="true" transient="true" changeable="false" derived="true"
	 * @generated
	 */
	short getReferencedComponentType();

	/**
	 * Returns with the nominal date of release of the current reference set member. Could be {@code null}. If {@code null}
	 * the component has not been published yet, or may have unpublished changes.
	 * @return the value of the publication date. Could be {@code null}.
	 * @see #isSetEffectiveTime()
	 * @see #unsetEffectiveTime()
	 * @see #setEffectiveTime(Date)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSetMember_EffectiveTime()
	 * @model unsettable="true" required="true"
	 * @generated
	 */
	Date getEffectiveTime();

	/**
	 * Counterpart of the {@link #getEffectiveTime()}.
	 * @param value the new date for altering the nominal date of the release for the component. Could be {@code null}.
	 * @see #isSetEffectiveTime()
	 * @see #unsetEffectiveTime()
	 * @see #getEffectiveTime()
	 * @generated
	 */
	void setEffectiveTime(Date value);

	/**
	 * Unsets the effective time of the current component. This is identical as {@link #setEffectiveTime(Date)} with {@code null} argument.
	 * @see #isSetEffectiveTime()
	 * @see #getEffectiveTime()
	 * @see #setEffectiveTime(Date)
	 * @generated
	 */
	void unsetEffectiveTime();

	/**
	 * Returns {@code true} if the component has been published. In other words the reference set has a *NON* {@code null}
	 * effective time.<br>More formally:
	 * <pre>
	 * null != {@link #getEffectiveTime()}
	 * <pre>
	 * @return whether the value of the '<em>Effective Time</em>' attribute is set.
	 * @see #unsetEffectiveTime()
	 * @see #getEffectiveTime()
	 * @see #setEffectiveTime(Date)
	 * @generated
	 */
	boolean isSetEffectiveTime();

	/**
	 * Returns wit the status of the SNOMED&nbsp;CT reference set member.
	 * <br>If the reference set member is active this will return with {@code true}, if the reference set member is retired 
	 * this method will return with {@code false}.
	 * @return {@code true} if the reference set member is active, otherwise it returns with {@code false}.
	 * @see #setActive(boolean)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSetMember_Active()
	 * @model required="true"
	 * @generated
	 */
	boolean isActive();

	/**
	 * Counterpart of {@link #isActive()}.
	 * @param value the new value of the status.
	 * @see #isActive()
	 * @generated
	 */
	void setActive(boolean value);

	/**
	 * Returns with the container reference set of the current SNOMED&nbsp;CT reference set member.
	 * @return the container reference set.
	 * @see #setRefSet(SnomedRefSet)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSetMember_RefSet()
	 * @model required="true"
	 * @generated
	 */
	SnomedRefSet getRefSet();

	/**
	 * Counterpart of the {@link #getRefSet()}.
	 * @param value the new container reference set.
	 * @see #getRefSet()
	 * @generated
	 */
	void setRefSet(SnomedRefSet value);

	/**
	 * Returns with a flag indicating whether the SNOMED&nbsp;CT reference set member has been published or not.
	 * @return with {@code true} if the reference set member is published, otherwise returns with {@code false}.
	 * @see #setReleased(boolean)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSetMember_Released()
	 * @model required="true"
	 * @generated
	 */
	boolean isReleased();

	/**
	 * Counterpart of {@link #isReleased()}.
	 * @param value the new released value. {@code true} if released, otherwise {@code false}. 
	 * @see #isReleased()
	 * @generated
	 */
	void setReleased(boolean value);

	/**
	 * Returns with the terminology specific unique identifier of the referenced component.
	 * @return the referenced component's unique identifier.
	 * @see #setReferencedComponentId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSetMember_ReferencedComponentId()
	 * @model required="true"
	 * @generated
	 */
	String getReferencedComponentId();

	/**
	 * Counterpart of the {@link #getReferencedComponentId()}.
	 * @param value the terminology specific component identifier uniquely determining the component referenced by the current member. 
	 * @see #getReferencedComponentId()
	 * @generated
	 */
	void setReferencedComponentId(String value);

	/**
	 * Returns with the SNOMED&nbsp;CT module concept ID of the reference set member.
	 * @return the SNOMED&nbsp;CT module concept ID of the current member.
	 * @see #setModuleId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSetMember_ModuleId()
	 * @model required="true"
	 * @generated
	 */
	String getModuleId();

	/**
	 * Counterpart of {@link #getModuleId()}.
	 * @param value the module concept of the current reference set member.
	 * @see #getModuleId()
	 * @generated
	 */
	void setModuleId(String value);

	/**
	 * Returns with the identifier concept ID of the container reference set.
	 * @return the identifier concept ID of the container reference set.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSetMember_RefSetIdentifierId()
	 * @model default="" required="true" transient="true" changeable="false" derived="true"
	 * @generated
	 */
	String getRefSetIdentifierId();

	/**
	 * Returns the value of the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Uuid</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Uuid</em>' attribute.
	 * @see #setUuid(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSetMember_Uuid()
	 * @model required="true"
	 * @generated
	 */
	String getUuid();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getUuid <em>Uuid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uuid</em>' attribute.
	 * @see #getUuid()
	 * @generated
	 */
	void setUuid(String value);

} // SnomedRefSetMember