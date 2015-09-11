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
package com.b2international.snowowl.snomed.datastore;

import java.io.Serializable;
import java.util.Date;

/**
 * Bare minimum representation of a SNOMED CT module dependency reference set member.
 */
public class SnomedModuleDependencyRefSetMemberFragment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long storageKey;
	private String moduleId;
	private String referencedComponentId;
	private Date sourceEffectiveTime;
	private Date targetEffectiveTime;
	
	public long getStorageKey() {
		return storageKey;
	}
	
	public void setStorageKey(long storageKey) {
		this.storageKey = storageKey;
	}
	
	public String getModuleId() {
		return moduleId;
	}
	
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	public String getReferencedComponentId() {
		return referencedComponentId;
	}
	
	public void setReferencedComponentId(String referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
	}
	
	public Date getSourceEffectiveTime() {
		return sourceEffectiveTime;
	}
	
	public void setSourceEffectiveTime(Date sourceEffectiveTime) {
		this.sourceEffectiveTime = sourceEffectiveTime;
	}
	
	public Date getTargetEffectiveTime() {
		return targetEffectiveTime;
	}
	
	public void setTargetEffectiveTime(Date targetEffectiveTime) {
		this.targetEffectiveTime = targetEffectiveTime;
	}

	@Override
	public int hashCode() {
		return 31 + (int) (storageKey ^ (storageKey >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		
		SnomedModuleDependencyRefSetMemberFragment other = (SnomedModuleDependencyRefSetMemberFragment) obj;
		return (storageKey == other.storageKey);
	}

	@Override
	public String toString() {
		return "SnomedModuleDependencyRefSetMemberFragment"
				+ " [storageKey=" + storageKey 
				+ ", moduleId=" + moduleId
				+ ", referencedComponentId=" + referencedComponentId 
				+ ", sourceEffectiveTime=" + sourceEffectiveTime
				+ ", targetEffectiveTime=" + targetEffectiveTime 
				+ "]";
	}
}
