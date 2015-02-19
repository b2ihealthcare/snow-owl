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

/**
 * Represents a SNOMED&nbsp;CT module dependency reference set member.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember#getSourceEffectiveTime <em>Source Effective Time</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember#getTargetEffectiveTime <em>Target Effective Time</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedModuleDependencyRefSetMember()
 * @model
 * @generated
 */
public interface SnomedModuleDependencyRefSetMember extends SnomedRefSetMember {
	/**
	 * Returns with the effective time of the source module. Could be {@code null} if unset.
	 * @return the effective time of the dependent module.
	 * @see #setSourceEffectiveTime(Date)
	 * @model required="true"
	 * @generated
	 */
	Date getSourceEffectiveTime();

	/**
	 * Counterpart of {@link #getSourceEffectiveTime()}. Sets the effective to the desired value.
	 * @param value the desired effective time for the source/dependent effective time.
	 * @see #getSourceEffectiveTime()
	 * @generated
	 */
	void setSourceEffectiveTime(Date value);

	/**
	 * Returns with the effective time of the target module. Could be {@code null} if unset.
	 * @return the effective time of the depending module.
	 * @see #setTargetEffectiveTime(Date)
	 * @model required="true"
	 * @generated
	 */
	Date getTargetEffectiveTime();

	/**
	 * Counterpart of the {@link #getTargetEffectiveTime()}. Sets the effective time on the target module.
	 * @param value the new effective time for the target module.
	 * @see #getTargetEffectiveTime()
	 * @generated
	 */
	void setTargetEffectiveTime(Date value);

} // SnomedModuleDependencyRefSetMember