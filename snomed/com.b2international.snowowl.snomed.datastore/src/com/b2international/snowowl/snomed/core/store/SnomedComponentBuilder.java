/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.eclipse.emf.cdo.CDOObject;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 4.5
 *
 * @param <T> the type of the Component to build.
 */
public abstract class SnomedComponentBuilder<B extends SnomedComponentBuilder<B, T>, T extends CDOObject> extends SnomedBaseComponentBuilder<B, T> {

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

	/**
	 * Builds and returns a component based on the configured properties.
	 * 
	 * @param context the context to use when building the component
	 * @return
	 */
	@OverridingMethodsMustInvokeSuper
	public void init(T t, TransactionContext context) {
		if (t instanceof Component) {
			final Component component = (Component) t;
			component.setId(id);
			component.setActive(active);
			component.setModule(context.lookup(moduleId, Concept.class));

			if (effectiveTime == null) {
				component.unsetEffectiveTime();
				component.setReleased(false);
			} else {
				component.setEffectiveTime(effectiveTime);
				component.setReleased(true);
			}
			
		} else if (t instanceof SnomedRefSetMember) {
			final SnomedRefSetMember member = (SnomedRefSetMember) t;
			member.setUuid(id);
			member.setActive(active);
			member.setModuleId(moduleId);
			
			if (effectiveTime == null) {
				member.unsetEffectiveTime();
				member.setReleased(false);
			} else {
				member.setEffectiveTime(effectiveTime);
				member.setReleased(true);
			}
		}
	}

}
