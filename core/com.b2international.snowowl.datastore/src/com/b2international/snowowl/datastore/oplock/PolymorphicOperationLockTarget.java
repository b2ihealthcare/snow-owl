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

import java.io.IOException;
import java.io.ObjectInputStream;

import org.eclipse.xtext.util.PolymorphicDispatcher;

/**
 * Represents an implementation of {@link IOperationLockTarget} which may contain a series of multi-dispatched method calls named
 * {@value #DISPATCH_METHOD_NAME} to check for lock conflicts. All {@link PolymorphicOperationLockTarget}s conflict with
 * {@link #equals(Object) equal} instances.
 * 
 */
public class PolymorphicOperationLockTarget implements IOperationLockTarget {

	private static final long serialVersionUID = 1L;

	private static final String DISPATCH_METHOD_NAME = "_conflicts";

	private transient PolymorphicDispatcher<Boolean> dispatcher = initDispatcher();

	private PolymorphicDispatcher<Boolean> initDispatcher() {
		return PolymorphicDispatcher.createForSingleTarget(DISPATCH_METHOD_NAME, this);
	}

	@Override
	public final boolean conflicts(final IOperationLockTarget other) {
		return dispatcher.invoke(other);
	}

	/**
	 * The default implementation returns {@code true} if the given argument is equal to this target. Subclasses should
	 * override or introduce additional methods that check conflicts for more concrete subtypes of {@link IOperationLockTarget}.
	 * 
	 * @see #conflicts(IOperationLockTarget)
	 */
	protected boolean _conflicts(final IOperationLockTarget other) {
		return equals(other);
	}

	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		dispatcher = initDispatcher();
	}
}