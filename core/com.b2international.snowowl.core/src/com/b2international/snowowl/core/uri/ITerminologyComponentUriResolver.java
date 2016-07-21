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
package com.b2international.snowowl.core.uri;

import com.b2international.snowowl.core.api.IComponent;

/**
 * Interface for terminology component URI resolvers.
 * 
 *
 * @param <C> the terminology component type
 * @deprecated - UNSUPPORTED API
 */
public interface ITerminologyComponentUriResolver<C extends IComponent<?>> {
	/**
	 * Returns the component referenced by the specified URI.
	 * 
	 * @param uri the URI
	 * @return the referenced component, or <code>null</code> if not found
	 */
	C getComponent(String uri);
	
	/**
	 * Returns the terminology component URI of the specified component.
	 * 
	 * @param component the component
	 * @return the terminology component URI
	 */
	String getUri(C component);
}