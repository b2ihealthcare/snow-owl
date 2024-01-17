/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;

/**
 */
public abstract class AbstractDisposableService implements IDisposableService {

	private final AtomicBoolean disposed = new AtomicBoolean();
	
	@Override
	public final boolean isDisposed() {
		return disposed.get();
	}

	@Override
	public final void dispose() {
		if (disposed.compareAndSet(false, true)) {
			onDispose();
		}
	}

	/**
	 * Template method which runs on the first call to {@link #dispose()} (subsequent calls will not do anything, and
	 * the service remains disposed). Subclasses should override to do any one-time disposal of resources.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void onDispose() { }

	/**
	 * Convenience method that checks if the service is capable of servicing an incoming request.
	 * @throws SnowowlRuntimeException if the service is already disposed
	 */
	protected final void throwIfDisposed() throws SnowowlRuntimeException {
		if (isDisposed()) {
			throw new SnowowlRuntimeException("This service is disposed.");
		}
	}
}