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
package com.b2international.snowowl.datastore.server.editor.operation.executor;

import java.util.Set;

import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.editor.bean.IdentifiedBean;
import com.b2international.snowowl.datastore.editor.operation.AbstractOperation;
import com.b2international.snowowl.datastore.server.editor.session.EditorSession;

/**
 * 
 * @since 2.9
 */
public interface OperationExecutorFactory {

	/**
	 * Creates and returns a session independent operation executor for the
	 * given operation, that will be executed with the specified user. 
	 * <br>
	 * The created executor's responsibility to create or obtain the editing context
	 * that it can be operating on.
	 * 
	 * @param operation
	 * @param userId
	 * @return
	 */
	public AbstractOperationExecutor<AbstractOperation> createGlobalOperationExecutor(AbstractOperation operation, String userId);
	
	/**
	 * Creates and returns an operation executor for the given operation that
	 * will be operating on the beans of the specified session.
	 * 
	 * @param operation
	 * @param session
	 * @return
	 */
	public AbstractOperationExecutor<AbstractOperation> createSessionOperationExecutor(AbstractOperation operation, EditorSession<?> session);
	
	/**
	 * Creates and returns an operation executor for the given operation that
	 * will be operating on the specified editing context.
	 * 
	 * @param operation
	 * @param editingContext
	 * @param editedComponentBean The session's edited component, represented as an identified bean.
	 * @param deletedObjectStorageKeys The set of deleted object storage keys.
	 * @return
	 */
	public AbstractOperationExecutor<AbstractOperation> createCommitOperationExecutor(AbstractOperation operation, CDOEditingContext editingContext, IdentifiedBean editedComponentBean, Set<Long> deletedObjectStorageKeys);
	
}