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
package com.b2international.snowowl.datastore.internal.file;

import java.io.File;
import java.util.UUID;

import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.file.FileRegistry;

/**
 * @since 5.7
 */
public interface InternalFileRegistry extends FileRegistry {

	/**
	 * Returns the file associated with the given identifier.
	 * @param id - the unique identifier of the file
	 * @return the file
	 * @throws NotFoundException
	 *             - if the file does not exist with the given identifier
	 */
	File getFile(UUID id);
	
}
