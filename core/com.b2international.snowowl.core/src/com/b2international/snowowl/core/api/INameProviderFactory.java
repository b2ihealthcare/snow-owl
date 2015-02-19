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
package com.b2international.snowowl.core.api;


/**
 * Factory class for creating or retrieving {@link IComponentNameProvider name provider} instances for
 * different terminology components.  
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link INameProviderFactory#getNameProvider() Get name provider}</li>
 * </ul>
 * </p>
 */
public interface INameProviderFactory {

	/**
	 * Returns with a terminology independent {@link IComponentNameProvider name provider} instance.
	 * @return the name provider for terminology independent components.
	 * @see INameProviderFactory
	 */
	IComponentNameProvider getNameProvider();
	
}