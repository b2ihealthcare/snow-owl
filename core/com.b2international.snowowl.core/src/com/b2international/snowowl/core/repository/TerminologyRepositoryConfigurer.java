/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.setup.Environment;

/**
 * @since 7.0
 */
public interface TerminologyRepositoryConfigurer {

	/**
	 * @return the repository ID that should be configured/customized by this configurer.
	 */
	String getToolingId();
	
	/**
	 * @since 8.0
	 */
	default Map<Class<?>, Object> bindAdditionalServices(Environment env) {
		return Collections.emptyMap();
	}
	
	/**
	 * @return additional mappings that should be managed and configured for use within the configured repository.
	 */
	default Collection<Class<?>> getAdditionalMappings() {
		return List.of();
	}

	/**
	 * @return additional terminology components that should be handled by the configured repository.
	 */
	default Collection<Class<? extends IComponent>> getAdditionalTerminologyComponents() {
		return List.of();
	}

	/**
	 * @return additional component deletion policies for subtypes of RevisionDocument
	 */
	default CompositeComponentDeletionPolicy getComponentDeletionPolicy() {
		return null;
	}
}
