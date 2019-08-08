/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Date;

import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockTarget;

/**
 * Contains all information about an operation lock which should be presented on an administrative interface. Sorts by date.
 *
 */
public class OperationLockInfo implements Comparable<OperationLockInfo> {

	private final int id;

	private final int level;

	private final Date creationDate;
	
	private final DatastoreLockTarget target;
	
	private final DatastoreLockContext context;

	public OperationLockInfo(final int id, final int lockLevel, final Date creationDate, final DatastoreLockTarget target, final DatastoreLockContext context) {
		this.id = id;
		this.level = lockLevel;
		this.creationDate = creationDate;
		this.target = target;
		this.context = context;
	}
	
	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public DatastoreLockTarget getTarget() {
		return target;
	}
	
	public DatastoreLockContext getContext() {
		return context;
	}

	@Override
	public int compareTo(final OperationLockInfo otherInfo) {
		return creationDate.compareTo(otherInfo.creationDate);
	}
}