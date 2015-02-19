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
package com.b2international.snowowl.core.api.browser;

import com.b2international.snowowl.core.api.IComponent;

/**
 * Service interface aggregating client side {@link IClientTerminologyBrowser} and 
 * {@link IClientRefSetBrowser} services.
 * @param <R> - type of the reference set.
 * @param <C> - type of the components.
 * @param <K> - type of the unique component and reference set identifiers.
 * @see IClientTerminologyBrowser
 * @see IClientRefSetBrowser
 */
public interface IClientTerminologyAndRefSetBrowser<R extends IComponent<K>, C extends IComponent<K>, K> extends IClientTerminologyBrowser<C, K>, IClientRefSetBrowser<R, C, K> {

}