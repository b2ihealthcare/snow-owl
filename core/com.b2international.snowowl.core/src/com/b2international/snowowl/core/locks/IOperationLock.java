/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.locks;

import java.util.Collection;
import java.util.Date;

import com.b2international.snowowl.core.internal.locks.DatastoreLockContext;

/**
 * Represents a lock for an owning context of arbitrary type and a lock target. 
 */
public interface IOperationLock {

	int getId();
	
	int getLevel();
	
	Date getCreationDate();
	
	DatastoreLockContext getContext();

	Collection<DatastoreLockContext> getAllContexts();
	
	Lockable getTarget();
	
	boolean targetConflicts(Lockable otherTarget);
	
	boolean targetEquals(Lockable otherTarget);
	
	void acquire(DatastoreLockContext context);
	
	void release(DatastoreLockContext context);
	
	boolean isLocked();
}