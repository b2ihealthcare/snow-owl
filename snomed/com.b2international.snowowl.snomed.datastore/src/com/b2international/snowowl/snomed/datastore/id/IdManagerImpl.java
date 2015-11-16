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
package com.b2international.snowowl.snomed.datastore.id;

import java.util.Collection;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.action.BulkDeprecateAction;
import com.b2international.snowowl.snomed.datastore.id.action.BulkGenerateAction;
import com.b2international.snowowl.snomed.datastore.id.action.BulkPublishAction;
import com.b2international.snowowl.snomed.datastore.id.action.BulkRegisterAction;
import com.b2international.snowowl.snomed.datastore.id.action.BulkReleaseAction;
import com.b2international.snowowl.snomed.datastore.id.action.BulkReserveAction;
import com.b2international.snowowl.snomed.datastore.id.action.DeprecateAction;
import com.b2international.snowowl.snomed.datastore.id.action.GenerateAction;
import com.b2international.snowowl.snomed.datastore.id.action.IIdAction;
import com.b2international.snowowl.snomed.datastore.id.action.PublishAction;
import com.b2international.snowowl.snomed.datastore.id.action.RegisterAction;
import com.b2international.snowowl.snomed.datastore.id.action.ReleaseAction;
import com.b2international.snowowl.snomed.datastore.id.action.ReserveAction;
import com.google.common.collect.Lists;

/**
 * @since 4.5
 */
public class IdManagerImpl implements IdManager {

	private final Collection<IIdAction<?>> actions = Lists.newArrayList();

	private final ISnomedIdentifierService identifierService;

	public IdManagerImpl(final ISnomedIdentifierService identifierService) {
		this.identifierService = identifierService;
	}

	@Override
	public void rollback() {
		for (final IIdAction<?> action : actions) {
			if (!action.isFailed())
				action.rollback();
		}
	}

	@Override
	public void commit() {
		for (final IIdAction<?> action : actions) {
			if (!action.isFailed())
				action.commit();
		}
	}

	@Override
	public String generate(final String namespace, final ComponentCategory category) {
		final GenerateAction action = new GenerateAction(namespace, category, identifierService);
		executeAction(action);

		return action.get();
	}

	@Override
	public void register(final String identifier) {
		final RegisterAction action = new RegisterAction(identifier, identifierService);
		executeAction(action);
	}

	@Override
	public String reserve(final String namespace, final ComponentCategory category) {
		final ReserveAction action = new ReserveAction(namespace, category, identifierService);
		executeAction(action);

		return action.get();
	}

	@Override
	public void deprecate(final String identifier) {
		final DeprecateAction action = new DeprecateAction(identifier, identifierService);
		executeAction(action);
	}

	@Override
	public void release(final String identifier) {
		final ReleaseAction action = new ReleaseAction(identifier, identifierService);
		executeAction(action);
	}

	@Override
	public void publish(final String identifier) {
		final PublishAction action = new PublishAction(identifier, identifierService);
		executeAction(action);
	}

	@Override
	public Collection<String> bulkGenerate(final String namespace, final ComponentCategory category, final int quantity) {
		final BulkGenerateAction action = new BulkGenerateAction(namespace, category, quantity, identifierService);
		executeAction(action);

		return action.get();
	}

	@Override
	public void bulkRegister(final Collection<String> componentIds) {
		final BulkRegisterAction action = new BulkRegisterAction(componentIds, identifierService);
		executeAction(action);
	}

	@Override
	public Collection<String> bulkReserve(final String namespace, final ComponentCategory category, final int quantity) {
		final BulkReserveAction action = new BulkReserveAction(namespace, category, quantity, identifierService);
		executeAction(action);

		return action.get();
	}

	@Override
	public void bulkDeprecate(final Collection<String> componentIds) {
		final BulkDeprecateAction action = new BulkDeprecateAction(componentIds, identifierService);
		executeAction(action);
	}

	@Override
	public void bulkRelease(final Collection<String> componentIds) {
		final BulkReleaseAction action = new BulkReleaseAction(componentIds, identifierService);
		executeAction(action);
	}

	@Override
	public void bulkPublish(final Collection<String> componentIds) {
		final BulkPublishAction action = new BulkPublishAction(componentIds, identifierService);
		executeAction(action);
	}

	private void executeAction(final IIdAction<?> action) {
		try {
			actions.add(action);
			action.execute();
		} catch (Exception e) {
			action.setFailed(true);
			throw e;
		}
	}

}
