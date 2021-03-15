/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.b2international.index.revision.Commit;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;

/**
 * @since 7.16
 */
public class DelegatingTransactionContext extends DelegatingBranchContext implements TransactionContext {

	public DelegatingTransactionContext(TransactionContext context) {
		super(context);
	}

	@Override
	protected TransactionContext getDelegate() {
		return (TransactionContext) super.getDelegate();
	}
	
	@Override
	public void close() throws Exception {
		getDelegate().close();
	}

	@Override
	public String author() {
		return getDelegate().author();
	}

	@Override
	public String add(Object obj) {
		return getDelegate().add(obj);
	}

	@Override
	public void update(Revision oldVersion, Revision newVersion) {
		getDelegate().update(oldVersion, newVersion);
	}

	@Override
	public void delete(Object obj) {
		getDelegate().delete(obj);
	}

	@Override
	public void delete(Object obj, boolean force) {
		getDelegate().delete(obj, force);
	}

	@Override
	public Optional<Commit> commit() {
		return getDelegate().commit();
	}

	@Override
	public Optional<Commit> commit(String commitComment) {
		return getDelegate().commit(commitComment);
	}

	@Override
	public Optional<Commit> commit(String commitComment, String parentContextDescription) {
		return getDelegate().commit(commitComment, parentContextDescription);
	}

	@Override
	public Optional<Commit> commit(String userId, String commitComment, String parentContextDescription) {
		return getDelegate().commit(userId, commitComment, parentContextDescription);
	}

	@Override
	public boolean isNotificationEnabled() {
		return getDelegate().isNotificationEnabled();
	}

	@Override
	public void setNotificationEnabled(boolean notificationEnabled) {
		getDelegate().setNotificationEnabled(notificationEnabled);
	}

	@Override
	public <T> T lookup(String componentId, Class<T> type) throws ComponentNotFoundException {
		return getDelegate().lookup(componentId, type);
	}

	@Override
	public <T> T lookupIfExists(String componentId, Class<T> type) {
		return getDelegate().lookupIfExists(componentId, type);
	}

	@Override
	public <T> Map<String, T> lookup(Collection<String> componentIds, Class<T> type) {
		return getDelegate().lookup(componentIds, type);
	}

	@Override
	public void rollback() {
		getDelegate().rollback();
	}
	
	@Override
	public void clearContents() {
		getDelegate().clearContents();
	}

	@Override
	public boolean isDirty() {
		return getDelegate().isDirty();
	}

	@Override
	public String parentLock() {
		return getDelegate().parentLock();
	}

}
