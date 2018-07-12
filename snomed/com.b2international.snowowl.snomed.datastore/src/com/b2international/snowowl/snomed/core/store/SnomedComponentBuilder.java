/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.store;

import java.util.Date;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;

/**
 * @since 4.5
 *
 * @param <T> the type of the Component to build.
 */
public abstract class SnomedComponentBuilder<B extends SnomedComponentBuilder<B, CB, T>, CB extends SnomedDocument.Builder<CB, T>, T extends SnomedDocument> extends SnomedBaseComponentBuilder<B, CB, T> {

	protected SnomedComponentBuilder() {
	}

	private String id;
	private Date effectiveTime;
	private String moduleId;
	private boolean active = true;

	/**
	 * Specifies the SNOMED CT Identifier.
	 * 
	 * @param id the ID to use for the newly created component.
	 * @return
	 */
	public final B withId(String id) {
		this.id = id;
		return getSelf();
	}

	/**
	 * Specifies the activity flag to use for the new component.
	 * 
	 * @param active
	 * @return
	 */
	public final B withActive(boolean active) {
		this.active = active;
		return getSelf();
	}

	/**
	 * Specifies the module of the newly created component.
	 * 
	 * @param moduleId
	 * @return
	 */
	public final B withModule(String moduleId) {
		this.moduleId = moduleId;
		return getSelf();
	}

	/**
	 * Specifies the effective time of the newly created component.
	 * 
	 * @param effectiveTime
	 * @return
	 */
	public final B withEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
		return getSelf();
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	public void init(CB component, TransactionContext context) {
		component
			.id(id)
			.active(active)
			.moduleId(moduleId);
		
		// check that the module does exist in the system
		if (!id.equals(moduleId)) {
			context.lookup(moduleId, SnomedConceptDocument.class);
		}
		
		if (effectiveTime == null) {
			component.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
			component.released(false);
		} else {
			component.effectiveTime(effectiveTime.getTime());
			component.released(true);
		}
	}

}
