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
package com.b2international.snowowl.datastore.version;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.datastore.ICodeSystem;

/**
 * Exception indicating an error for the version operation.
 */
public class VersioningException extends Exception {

	private static final long serialVersionUID = 7803362752957857254L;

	private final ICodeSystem codeSystem;

	public VersioningException(ICodeSystem codeSystem) {
		this.codeSystem = checkNotNull(codeSystem, "codeSystem");
	}

	/**
	 * Returns with the UUID of the repository where the current exception was
	 * thrown.
	 */
	public String getRepositoryUuid() {
		return codeSystem.getRepositoryUuid();
	}

	public ICodeSystem getCodeSystem() {
		return codeSystem;
	}
}