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
import com.b2international.snowowl.snomed.datastore.id.IdAction.IdActionType;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * @since 4.5
 */
public class IdManagerImpl implements IdManager {

	private enum Type {
		ROLLBACK, COMMIT
	}

	private class PostJob {
		private Type type;
		private IdAction action;

		public PostJob(final Type type, final IdAction action) {
			this.type = type;
			this.action = action;
		}

		public Type getType() {
			return type;
		}

		public IdAction getAction() {
			return action;
		}
	}

	private final Collection<PostJob> postJobs = Lists.newArrayList();

	private final ISnomedIdentifierService identifierService;

	public IdManagerImpl(final ISnomedIdentifierService identifierService) {
		this.identifierService = identifierService;
	}

	@Override
	public void rollback() {
		executeActions(Type.ROLLBACK);
	}

	@Override
	public void commit() {
		executeActions(Type.COMMIT);
	}

	@Override
	public SnomedIdentifier generate(final String namespace, final ComponentCategory category) {
		final SnomedIdentifier identifier = identifierService.generate(namespace, category);
		addAction(Type.ROLLBACK, identifier, IdActionType.RELEASE);

		return identifier;
	}

	@Override
	public void register(final SnomedIdentifier identifier) {
		identifierService.register(identifier);
		// TODO check: rollback should not be executed when exception occurred
		// because of already registered id
		addAction(Type.ROLLBACK, identifier, IdActionType.RELEASE);
	}

	@Override
	public SnomedIdentifier reserve(final String namespace, final ComponentCategory category) {
		final SnomedIdentifier identifier = identifierService.reserve(namespace, category);
		addAction(Type.ROLLBACK, identifier, IdActionType.RELEASE);
		addAction(Type.COMMIT, identifier, IdActionType.REGISTER);

		return identifier;
	}

	@Override
	public void deprecate(final SnomedIdentifier identifier) {
		identifierService.deprecate(identifier);
	}

	@Override
	public void release(final SnomedIdentifier identifier) {
		identifierService.release(identifier);
		addAction(Type.ROLLBACK, identifier, IdActionType.REGISTER);
	}

	@Override
	public void publish(final SnomedIdentifier identifier) {
		identifierService.publish(identifier);
	}

	private void addAction(final Type type, final SnomedIdentifier identifier, final IdActionType actionType) {
		final IdAction idAction = new IdAction(identifier, actionType);
		postJobs.add(new PostJob(type, idAction));
	}

	private void executeActions(final Type type) {
		final Collection<PostJob> filteredJobs = Collections2.filter(postJobs, new Predicate<PostJob>() {
			@Override
			public boolean apply(PostJob input) {
				return type == input.getType();
			}
		});

		for (final PostJob job : filteredJobs) {
			executeAction(job.getAction());
		}
	}

	private void executeAction(final IdAction action) {
		final SnomedIdentifier identifier = action.getIdentifier();

		switch (action.getType()) {
		case GENERATE:
			identifierService.generate(identifier.getNamespace(), identifier.getComponentCategory());
			break;
		case REGISTER:
			identifierService.register(identifier);
			break;
		case RESERVE:
			identifierService.reserve(identifier.getNamespace(), identifier.getComponentCategory());
			break;
		case DEPRECATE:
			identifierService.deprecate(identifier);
			break;
		case RELEASE:
			identifierService.release(identifier);
			break;
		case PUBLISH:
			identifierService.publish(identifier);
			break;
		}
	}

}
