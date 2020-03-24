/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;

/**
 * @since 7.0
 */
public interface TerminologyRepositoryConfigurer {

	/**
	 * @return the repository ID that should be configured/customized by this configurer.
	 */
	String getRepositoryId();
	
	/**
	 * @return additional mappings that should be managed and configured for use within the configured repository.
	 */
	Collection<Class<?>> getAdditionalMappings();

	/**
	 * @return additional component deletion policies for subtypes of RevisionDocument
	 */
	CompositeComponentDeletionPolicy getComponentDeletionPolicy();
	
}
