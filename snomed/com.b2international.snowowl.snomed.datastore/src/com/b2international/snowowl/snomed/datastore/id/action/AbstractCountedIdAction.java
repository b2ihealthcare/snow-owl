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
package com.b2international.snowowl.snomed.datastore.id.action;

import java.util.Set;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.id.request.AbstractSnomedIdentifierCountedRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
abstract class AbstractCountedIdAction extends AbstractIdAction<Set<String>> {

	private final String namespace;
	private final ComponentCategory category;
	private final int quantity;

	public AbstractCountedIdAction(final Provider<IEventBus> bus, final String namespace, final ComponentCategory category, final int quantity) {
		super(bus);
		this.namespace = namespace;
		this.category = category;
		this.quantity = quantity;
	}
	
	@Override
	protected final Set<String> doExecute() {
		return createRequestBuilder()
				.setNamespace(namespace)
				.setCategory(category)
				.setQuantity(quantity)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(bus.get())
				.then(response -> ImmutableSet.copyOf(response.getItems()))
				.getSync();
	}
	
	protected abstract AbstractSnomedIdentifierCountedRequestBuilder<?> createRequestBuilder();

	@Override
	protected final void doRollback(Set<String> storedResults) {
		SnomedRequests.identifiers()
				.prepareRelease()
				.setComponentIds(storedResults)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(bus.get())
				.getSync();
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("namespace", namespace)
				.add("category", category)
				.add("quantity", quantity)
				.toString();
	}
}
