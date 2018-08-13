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
package com.b2international.snowowl.datastore.oplock;

import java.util.Collection;
import java.util.Date;

/**
 * Represents a lock for an owning context of arbitrary type and a lock target. 
 *
 * @param C the lock context type
 */
public interface IOperationLock<C> {

	int getId();
	
	int getLevel();
	
	Date getCreationDate();
	
	C getContext();

	Collection<C> getAllContexts();
	
	IOperationLockTarget getTarget();
	
	boolean targetConflicts(IOperationLockTarget otherTarget);
	
	boolean targetEquals(IOperationLockTarget otherTarget);
	
	void acquire(C context) throws OperationLockException;
	
	void release(C context) throws OperationLockException;
	
	boolean isLocked();
}