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

import java.util.Date;

import org.eclipse.emf.cdo.CDOObject;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.NamespaceIdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.UserIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.google.common.base.Strings;

/**
 * @since 4.5
 *
 * @param <T>
 *            - the type of the Component to build.
 */
public abstract class SnomedComponentBuilder<B extends SnomedComponentBuilder<B, T>, T extends CDOObject> {

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
		return withId(new UserIdGenerationStrategy(identifier));
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
		return withId(new NamespaceIdGenerationStrategy(category, namespace));
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
	public final T build(TransactionContext context) {
		final T t = create();
		final String identifier = identifierGenerationStrategy.getId();
		final String module = Strings.isNullOrEmpty(moduleId) ? context.config().getModuleConfig(SnomedCoreConfiguration.class).getDefaultModule() : moduleId; 
		if (t instanceof Component) {
			final Component component = (Component) t;
			component.setId(identifier);
			component.setActive(active);
			component.setEffectiveTime(effectiveTime);
			component.setReleased(effectiveTime != null);
			component.setModule(context.lookup(module, Concept.class));
			component.unsetEffectiveTime();
		}
		init(t, context);
		return t;
	}

	/**
	 * Initialize any additional properties on the given component.
	 * 
	 * @param component
	 *            - the component to initialize with additional props
	 * @param context
	 *            - the context to use to get configuration options and other components
	 */
	protected abstract void init(T component, TransactionContext context);

	/**
	 * Creates an instance of the component.
	 * 
	 * @return
	 */
	protected abstract T create();

	@SuppressWarnings("unchecked")
	protected final B getSelf() {
		return (B) this;
	}

}
