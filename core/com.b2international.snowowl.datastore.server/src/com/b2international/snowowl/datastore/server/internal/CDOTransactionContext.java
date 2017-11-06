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
package com.b2international.snowowl.datastore.server.internal;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.exceptions.Exceptions;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingBranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.exceptions.CycleDetectedException;
import com.b2international.snowowl.core.exceptions.LockedException;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;

/**
 * @since 4.5
 */
public final class CDOTransactionContext extends DelegatingBranchContext implements TransactionContext {

	private CDOEditingContext editingContext;

	CDOTransactionContext(BranchContext context, CDOEditingContext editingContext) {
		super(context);
		this.editingContext = editingContext;
	}
	
	@Override
	public <T> T service(Class<T> type) {
		if (CDOEditingContext.class.isAssignableFrom(type)) {
			return type.cast(editingContext);
		}
		return super.service(type);
	}
	
	@Override
	public <T extends EObject> T lookup(String componentId, Class<T> type) {
		return editingContext.lookup(componentId, type);
	}
	
	@Override
	public <T extends EObject> T lookupIfExists(String componentId, Class<T> type) {
		return editingContext.lookupIfExists(componentId, type);
	}
	
	@Override
	public <T extends EObject> Map<String, T> lookup(Collection<String> componentIds, Class<T> type) {
		return editingContext.lookup(componentIds, type);
	}
	
	@Override
	public void add(EObject o) {
		editingContext.add(o);
	}
	
	@Override
	public void delete(EObject o) {
		editingContext.delete(o);
	}
	
	@Override
	public void delete(EObject o, boolean force) {
		editingContext.delete(o, force);
	}
	
	@Override
	public void close() throws Exception {
		editingContext.close();
	}

	@Override
	public void preCommit() {
		editingContext.preCommit();
	}
	
	@Override
	public void rollback() {
		editingContext.rollback();
	}

	@Override
	public long commit(String userId, String commitComment, String parentContextDescription) {
		try {
			final CDOCommitInfo info = new CDOServerCommitBuilder(userId, commitComment, editingContext.getTransaction())
					.parentContextDescription(parentContextDescription)
					.commitOne();
			return info.getTimeStamp();
		} catch (final CommitException e) {
			final RepositoryLockException cause = Exceptions.extractCause(e, getClass().getClassLoader(), RepositoryLockException.class);
			if (cause != null) {
				throw new LockedException(cause.getMessage());
			}
			
			final ConcurrentModificationException cause2 = Exceptions.extractCause(e, getClass().getClassLoader(), ConcurrentModificationException.class);
			if (cause2 != null) {
				throw new ConflictException("Concurrent modifications prevented the commit from being processed. Please try again.");
			}
			
			final CycleDetectedException cause3 = Exceptions.extractCause(e.getCause(), getClass().getClassLoader(), CycleDetectedException.class);
			if (cause3 != null) {
				throw cause3;
			}
			throw new SnowowlRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void clearContents() {
		editingContext.clearContents();
	}

}
