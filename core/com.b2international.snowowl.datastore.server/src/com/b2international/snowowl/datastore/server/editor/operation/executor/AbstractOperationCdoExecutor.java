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

/**
 * @param <T> editing context type
 * @param <O> operation type
 * 
 * @since 2.9
 */
abstract public class AbstractOperationCdoExecutor<T extends CDOEditingContext, O extends AbstractOperation> extends AbstractOperationExecutor<O> {

	protected final T editingContext;
	protected final IdentifiedBean editedComponentBean;
	
	public AbstractOperationCdoExecutor(Class<O> operationType, T editingContext, IdentifiedBean editedComponentBean, Set<Long> deletedObjectStorageKeys) {
		super(operationType, deletedObjectStorageKeys);
		this.editingContext = editingContext;
		this.editedComponentBean = editedComponentBean;
	}

}