/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.codesystem.CodeSystem;

/**
 * @since 7.5
 */
@FunctionalInterface
public interface RepositoryCodeSystemProvider {

	/**
	 * Returns the closest relative CodeSystem for the given reference branch.
	 * 
	 * @param referenceBranch - the reference branch
	 * @return the closest relative {@link CodeSystem}
	 * @throws BadRequestException - if there is no relative CodeSystem can be found for the given reference branch
	 */
	CodeSystem get(String referenceBranch);
	
}
