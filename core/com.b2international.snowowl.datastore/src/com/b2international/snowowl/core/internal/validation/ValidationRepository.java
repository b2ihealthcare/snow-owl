/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal.validation;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.index.Index;
import com.b2international.index.IndexRead;
import com.b2international.index.IndexWrite;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.snowowl.core.IDisposableService;

/**
 * @since 6.0
 */
public final class ValidationRepository implements Index, IDisposableService {

	private final Index index;
	private final AtomicBoolean disposed = new AtomicBoolean(false);

	public ValidationRepository(Index index) {
		this.index = index;
	}
	
	@Override
	public boolean isDisposed() {
		return disposed.get();
	}

	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			admin().close();
		}
	}
	
	@Override
	public IndexAdmin admin() {
		return index.admin();
	}

	@Override
	public String name() {
		return index.name();
	}

	@Override
	public <T> T read(IndexRead<T> read) {
		return index.read(read);
	}

	@Override
	public <T> T write(IndexWrite<T> write) {
		return index.write(write);
	}
	
}
