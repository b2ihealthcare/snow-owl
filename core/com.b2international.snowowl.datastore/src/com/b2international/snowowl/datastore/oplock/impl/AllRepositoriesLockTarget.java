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
package com.b2international.snowowl.datastore.oplock.impl;

import java.io.ObjectStreamException;

/**
 * A lock target which represents the a single terminology store. Conflicts with any other
 * {@link AbstractDatastoreLockTarget}.
 * 
 */
public class AllRepositoriesLockTarget extends AbstractDatastoreLockTarget {

	private static final long serialVersionUID = 1L;
	
	public static final AllRepositoriesLockTarget INSTANCE = new AllRepositoriesLockTarget();

	private AllRepositoriesLockTarget() {
		super();
	}

	protected boolean _conflicts(final AbstractDatastoreLockTarget other) {
		return true;
	}

	@Override
	public String toString() {
		return "all repositories";
	}
	
	private Object readResolve() throws ObjectStreamException {
		return INSTANCE;
	}
}