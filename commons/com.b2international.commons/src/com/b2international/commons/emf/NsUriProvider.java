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
package com.b2international.commons.emf;

import static java.util.Collections.emptySet;

import java.util.Set;

/**
 * Representation of a namespace URI provider.
 *
 */
public interface NsUriProvider {

	/**
	 * This will be used by EMF Compare in order to retrieve the namespace URIs detected in the scope.
	 * @return The namespace URIs.
	 */
	Set<String> getNsURIs();

	/**
	 * This will be used by EMF Compare in order to retrieve the resource URIs detected in the scope.
	 * @return The resource URIs.
	 */
	Set<String> getResourceURIs();
	
	/**Shared null namespace URI provider implementation.*/
	NsUriProvider NULL_IMPL = new NsUriProvider() {
		
		@Override
		public Set<String> getResourceURIs() {
			return emptySet();
		}
		
		@Override
		public Set<String> getNsURIs() {
			return emptySet();
		}
	};
	
}