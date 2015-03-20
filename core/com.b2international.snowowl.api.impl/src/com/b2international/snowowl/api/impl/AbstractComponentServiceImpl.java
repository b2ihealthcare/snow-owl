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
package com.b2international.snowowl.api.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ConcurrentModificationException;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.util.CommitException;

import com.b2international.commons.exceptions.Exceptions;
import com.b2international.snowowl.api.IComponentService;
import com.b2international.snowowl.api.domain.IComponent;
import com.b2international.snowowl.api.domain.IComponentInput;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.exception.AlreadyExistsException;
import com.b2international.snowowl.api.exception.ComponentNotFoundException;
import com.b2international.snowowl.api.exception.ConflictException;
import com.b2international.snowowl.api.exception.LockedException;
import com.b2international.snowowl.api.impl.domain.ComponentRef;
import com.b2international.snowowl.api.task.exception.TaskAlreadyPromotedException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.tasks.ITaskStateManager;

/**
 * TODO: validate misguided references (eg. when the incoming code system short name is ATC in SnomedConceptServiceImpl)?
 * 
 */
public abstract class AbstractComponentServiceImpl<C extends IComponentInput, R extends IComponent, U, E extends CDOEditingContext, M extends CDOObject> 
	implements IComponentService<C, R, U> {

	private static ITaskStateManager getTaskStateManager() {
		return ApplicationContext.getServiceForClass(ITaskStateManager.class);
	}

	protected final String handledRepositoryUuid;
	protected final ComponentCategory handledCategory;

	/**
	 * @param handledRepositoryUuid
	 * @param handledCategory
	 */
	protected AbstractComponentServiceImpl(final String handledRepositoryUuid, final ComponentCategory handledCategory) {
		this.handledRepositoryUuid = handledRepositoryUuid;
		this.handledCategory = handledCategory;
	}

	@Override
	public R create(final C input, final String userId, final String commitComment) {
		checkNotNull(input, "Component input may not be null.");
		checkNotNull(userId, "User identifier may not be null.");
		checkNotNull(commitComment, "Commit comment may not be null.");
		checkClosedTask(input.getTaskId());

		if (componentExists(input)) {
			throw createDuplicateComponentException(input);
		}
	
		try (E editingContext = createEditingContext(input)) {

			final M component = convertAndRegister(input, editingContext);
			editingContext.preCommit();

			/*
			 * FIXME: at this point, the component identifier might have changed even though the input 
			 * required an exact ID to be assigned. What to do?
			 */
			final String componentId = getComponentId(component);
			doCommit(userId, commitComment, editingContext);
			return read(createComponentRef(input, componentId));
		
		}
	}

	@Override
	public R read(final IComponentRef ref) {
		checkNotNull(ref, "Component reference may not be null.");
		checkComponentExists(ref);

		final R result = doRead(ref);
		return result;
	}

	@Override
	public R update(final IComponentRef ref, final U update, final String userId, final String commitComment) {
		checkComponentExists(ref);
		checkClosedTask(ref.getTaskId());

		try (E editingContext = createEditingContext(ref)) {
			doUpdate(ref, update, editingContext);
			doCommit(userId, commitComment, editingContext);
			return read(ref);
		}
	}

	@Override
	public void delete(final IComponentRef ref, final String userId, final String commitComment) {
		checkComponentExists(ref);
		checkClosedTask(ref.getTaskId());

		try (E editingContext = createEditingContext(ref)) {
			doDelete(ref, editingContext);
			doCommit(userId, commitComment, editingContext);
			return;
		}
	}

	protected void checkComponentExists(final IComponentRef ref) {
		if (!componentExists(ref)) {
			throw new ComponentNotFoundException(handledCategory, ref.getComponentId());
		}
	}

	protected IComponentRef createComponentRef(final C input, final String componentId) {
		final ComponentRef result = new ComponentRef();
		result.setShortName(input.getCodeSystemShortName());
		result.setVersion(input.getCodeSystemVersionId());
		result.setTaskId(input.getTaskId());
		result.setComponentId(componentId);
		result.checkStorageExists();
		return result;
	}

	private E createEditingContext(final C input) {
		// XXX: Since we don't know what the componentId will become at this point, we set it to null
		return createEditingContext(createComponentRef(input, null));
	}

	private void doCommit(final String userId, final String commitComment, final E editingContext) {
		try {
			CDOServerUtils.commit(editingContext.getTransaction(), userId, commitComment, null);
		} catch (final CommitException e) {
			final RepositoryLockException cause = Exceptions.extractCause(e, getClass().getClassLoader(), RepositoryLockException.class);
			if (cause != null) {
				throw new LockedException(cause.getMessage());
			}
			
			final ConcurrentModificationException cause2 = Exceptions.extractCause(e, getClass().getClassLoader(), ConcurrentModificationException.class);
			if (cause2 != null) {
				throw new ConflictException("Concurrent modifications prevented the concept from being persisted. Please try again.");
			}
			
			throw new SnowowlRuntimeException(e.getMessage(), e);
		}
	}

	protected void checkClosedTask(final String taskId) {
		if (null != taskId && getTaskStateManager().isClosed(taskId)) {
			throw new TaskAlreadyPromotedException("Task " + taskId + " is already promoted.");
		}
	}

	protected abstract boolean componentExists(C input);

	protected abstract boolean componentExists(IComponentRef ref);

	protected abstract AlreadyExistsException createDuplicateComponentException(C input);

	protected abstract E createEditingContext(IComponentRef ref);

	protected abstract M convertAndRegister(C input, E editingContext);

	protected abstract String getComponentId(M component);

	protected abstract R doRead(IComponentRef ref);

	protected abstract void doUpdate(IComponentRef ref, U update, E editingContext);

	protected abstract void doDelete(IComponentRef ref, E editingContext);
}