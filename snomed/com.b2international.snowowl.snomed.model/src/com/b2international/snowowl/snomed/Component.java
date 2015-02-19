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
package com.b2international.snowowl.snomed;

import java.util.Date;

import org.eclipse.emf.cdo.CDOObject;


/**
 * Abstract representation of all SNOMED&nbsp;CT core components.
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.Component#getId <em>Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Component#isActive <em>Active</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Component#getEffectiveTime <em>Effective Time</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Component#getModule <em>Module</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.Component#isReleased <em>Released</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.SnomedPackage#getComponent()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface Component extends CDOObject {
	/**
	 * Returns with the unique SNOMED&nbsp;CT identifier of the core SNOMED&nbsp;CT component.
	 * @return the unique SNOMED&nbsp;CT identifier of the component.
	 * @see #setId(String)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getComponent_Id()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnLength='19'"
	 * @generated
	 */
	String getId();

	/**
	 * Counterpart of the {@link #getId()}.
	 * @param value the new value of the SNOMED&nbsp;CT identifier.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns wit the status of the SNOMED&nbsp;CT component.
	 * <br>If the component is active this will return with {@code true}, if the component is retired 
	 * this method will return with {@code false}.
	 * @return {@code true} if the component is active, otherwise it returns with {@code false}.
	 * @see #setActive(boolean)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getComponent_Active()
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
	 * Effective time is the point in time when the current SNOMED&nbsp;CT component has been modified.
	 * <br>Can be {@code null}. If {@code null}, it means that the component has been modified but not published yet.
	 * <br>For more details, please see {@link #isReleased() released}.
	 * @return the value of the '<em>Effective Time</em>' attribute.
	 * @see #isSetEffectiveTime()
	 * @see #unsetEffectiveTime()
	 * @see #setEffectiveTime(Date)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getComponent_EffectiveTime()
	 * @model unsettable="true"
	 * @generated
	 */
	Date getEffectiveTime();

	/**
	 * Counterpart of {@link #getEffectiveTime()}.
	 * <br>Invoking this method with {@code null} value is identical with calling {@link #unsetEffectiveTime()}.
	 * @param value the new effective time for the current component. Can be {@code null}.
	 * @see #isSetEffectiveTime()
	 * @see #unsetEffectiveTime()
	 * @see #getEffectiveTime()
	 * @generated
	 */
	void setEffectiveTime(Date value);

	/**
	 * Unsets the effective time of the current component.
	 * <br>Identical with {@code #setEffectiveTime(null)}.
	 * @see #isSetEffectiveTime()
	 * @see #getEffectiveTime()
	 * @see #setEffectiveTime(Date)
	 * @generated
	 */
	void unsetEffectiveTime();

	/**
	 * Returns {@code true} if the effective time is specified on the component. Otherwise returns with {@code false}.
	 * @return {@code true} if the effective time is set on the component, more formally if {@code null != getEffectiveTime()}. Otherwise {@code false}.
	 * @see #unsetEffectiveTime()
	 * @see #getEffectiveTime()
	 * @see #setEffectiveTime(Date)
	 * @generated
	 */
	boolean isSetEffectiveTime();

	/**
	 * Returns with the SNOMED&nbsp;CT module concept of the component.
	 * @return the SNOMED&nbsp;CT module concept of the current component.
	 * @see #setModule(Concept)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getComponent_Module()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnName='module0'"
	 * @generated
	 */
	Concept getModule();

	/**
	 * Counterpart of {@link #getModule()}.
	 * @param value the module concept of the current component.
	 * @see #getModule()
	 * @generated
	 */
	void setModule(Concept value);

	/**
	 * Returns with a flag indicating whether the SNOMED&nbsp;CT component has been published or not.
	 * @return with {@code true} if the component is published, otherwise returns with {@code false}.
	 * @see #setReleased(boolean)
	 * @see com.b2international.snowowl.snomed.SnomedPackage#getComponent_Released()
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

} // Component