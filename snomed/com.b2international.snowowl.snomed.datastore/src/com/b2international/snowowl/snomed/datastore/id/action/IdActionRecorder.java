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
package com.b2international.snowowl.snomed.datastore.id.action;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Set;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.inject.Provider;

/**
 * @since 5.5
 */
public class IdActionRecorder {

	private final Provider<IEventBus> bus;
	private final List<IdAction<?>> actions = newArrayList();

	public IdActionRecorder(final Provider<IEventBus> bus) {
		this.bus = bus;
	}

	public void rollback() {
		for (final IdAction<?> action : actions) {
			action.rollback();
		}
	}

	public void commit() {
		for (final IdAction<?> action : actions) {
			action.commit();
		}
	}

	public Set<String> generate(final String namespace, final ComponentCategory category, final int quantity) {
		final GenerateAction action = new GenerateAction(bus, namespace, category, quantity);
		return executeAction(action);
	}

	public void register(final Set<String> componentIds) {
		final RegisterAction action = new RegisterAction(bus, componentIds);
		executeAction(action);
	}

	public Set<String> reserve(final String namespace, final ComponentCategory category, final int quantity) {
		final ReserveAction action = new ReserveAction(bus, namespace, category, quantity);
		return executeAction(action);
	}

	public void deprecate(final Set<String> componentIds) {
		final DeprecateAction action = new DeprecateAction(bus, componentIds);
		executeAction(action);
	}

	public void publish(final Set<String> componentIds) {
		final PublishAction action = new PublishAction(bus, componentIds);
		executeAction(action);
	}

	private <T> T executeAction(final IdAction<T> action) {
		final T result = action.execute();
		actions.add(action);
		return result;
	}

}
