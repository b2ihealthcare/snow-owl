/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.action;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Set;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.terminology.ComponentCategory;

/**
 * @since 5.5
 */
public class IdActionRecorder {

	private final RepositoryContext context;
	private final List<IdAction<?>> actions = newArrayList();

	public IdActionRecorder(final RepositoryContext context) {
		this.context = context;
	}

	public void rollback() {
		for (final IdAction<?> action : actions) {
			action.rollback(context);
		}
	}

	public void commit() {
		for (final IdAction<?> action : actions) {
			action.commit(context);
		}
	}

	public Set<String> generate(final String namespace, final ComponentCategory category, final int quantity) {
		final GenerateAction action = new GenerateAction(namespace, category, quantity);
		return executeAction(action);
	}

	public void register(final Set<String> componentIds) {
		final RegisterAction action = new RegisterAction(componentIds);
		executeAction(action);
	}

	public Set<String> reserve(final String namespace, final ComponentCategory category, final int quantity) {
		final ReserveAction action = new ReserveAction(namespace, category, quantity);
		return executeAction(action);
	}

	public void deprecate(final Set<String> componentIds) {
		final DeprecateAction action = new DeprecateAction(componentIds);
		executeAction(action);
	}

	public void publish(final Set<String> componentIds) {
		final PublishAction action = new PublishAction(componentIds);
		executeAction(action);
	}

	private <T> T executeAction(final IdAction<T> action) {
		final T result = action.execute(context);
		actions.add(action);
		return result;
	}

}
