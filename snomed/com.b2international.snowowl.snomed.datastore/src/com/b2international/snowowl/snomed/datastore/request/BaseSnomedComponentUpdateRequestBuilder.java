/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.snomed.core.domain.InactivationProperties;

/**
 * @since 4.5
 */
public abstract class BaseSnomedComponentUpdateRequestBuilder<B extends BaseSnomedComponentUpdateRequestBuilder<B, R>, R extends SnomedComponentUpdateRequest> 
		extends SnomedComponentUpdateRequestBuilderBase<B, R> {

	private InactivationProperties inactivationProperties; 
	private String moduleId;
	private Boolean active;
	private String effectiveTime;

	protected BaseSnomedComponentUpdateRequestBuilder(String componentId) {
		super(componentId);
	}
	
	public final B setActive(Boolean active) {
		this.active = active;
		return getSelf();
	}
	
	public final B setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return getSelf();
	}
	
	public final B setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
		return getSelf();
	}
	
	public final B setInactivationProperties(InactivationProperties inactivationProperties) {
		this.inactivationProperties = inactivationProperties;
		return getSelf();
	}
	
	@OverridingMethodsMustInvokeSuper
	@Override
	protected void init(R req) {
		super.init(req);
		req.setActive(active);
		req.setModuleId(moduleId);
		req.setInactivationProperties(inactivationProperties);
		req.setEffectiveTime(effectiveTime);
	}

	
}
