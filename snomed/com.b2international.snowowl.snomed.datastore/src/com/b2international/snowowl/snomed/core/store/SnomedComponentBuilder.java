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
package com.b2international.snowowl.snomed.core.store;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Date;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.emf.cdo.CDOObject;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.ReservingIdStrategy;
import com.b2international.snowowl.snomed.core.domain.UUIDIdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.RegisteringIdStrategy;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Strings;

/**
 * @since 4.5
 *
 * @param <T>
 *            - the type of the Component to build.
 */
public abstract class SnomedComponentBuilder<B extends SnomedComponentBuilder<B, T>, T extends CDOObject> extends SnomedBaseComponentBuilder<B, T> {

	private Date effectiveTime;
	private String moduleId;
	private boolean active = true;
	private IdGenerationStrategy identifierGenerationStrategy;
	private ComponentCategory category;

	protected SnomedComponentBuilder(ComponentCategory category) {
		this.category = category;
	}

	/**
	 * Specifies the SNOMED CT Identifier.
	 * 
	 * @param identifier
	 *            - the ID to use for the newly created Concept.
	 * @return
	 */
	public final B withId(String identifier) {
		return withId(new RegisteringIdStrategy(identifier));
	}

	/**
	 * Specifies the SNOMED CT Identifier generation strategy to use when generating the ID for the new Concept.
	 * 
	 * @param strategy
	 *            - the identifier generation strategy to use
	 * @return
	 */
	public final B withId(IdGenerationStrategy strategy) {
		this.identifierGenerationStrategy = strategy;
		return getSelf();
	}

	/**
	 * Specifies the namespace to use when generating a completely new ID for the SNOMED CT Component.
	 * 
	 * @param namespace
	 * @return
	 */
	public final B withIdFromNamespace(String namespace) {
		return withId(new ReservingIdStrategy(category, namespace));
	}

	/**
	 * Specifies the activity flag to use for the new concept.
	 * 
	 * @param active
	 * @return
	 */
	public final B withActive(boolean active) {
		this.active = active;
		return getSelf();
	}

	/**
	 * Specifies the module of the newly created concept.
	 * 
	 * @param moduleId
	 * @return
	 */
	public final B withModule(String moduleId) {
		this.moduleId = moduleId;
		return getSelf();
	}

	/**
	 * Specifies the effective time of the newly created concept.
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
	 * @param context
	 *            - the context to use when building the component.
	 * @return
	 */
	@OverridingMethodsMustInvokeSuper
	protected void init(T t, TransactionContext context) {
		final String module = Strings.isNullOrEmpty(moduleId) ? context.config().getModuleConfig(SnomedCoreConfiguration.class).getDefaultModule() : moduleId; 
		if (t instanceof Component) {
			final Component component = (Component) t;
			final String identifier = identifierGenerationStrategy.generate(context);
			component.setId(identifier);
			component.setActive(active);
			component.setEffectiveTime(effectiveTime);
			component.setReleased(effectiveTime != null);
			component.setModule(context.lookup(module, Concept.class));
			component.unsetEffectiveTime();
		} else if (t instanceof SnomedRefSetMember) {
			final SnomedRefSetMember member = (SnomedRefSetMember) t;
			checkArgument(identifierGenerationStrategy instanceof UUIDIdGenerationStrategy, "Only UUIDs can be used for reference set member IDs");
			member.setUuid(identifierGenerationStrategy.generate(context));
			member.setActive(active);
			member.setEffectiveTime(effectiveTime);
			member.setReleased(effectiveTime != null);
			member.setModuleId(module);
			member.unsetEffectiveTime();
		}
	}

}
