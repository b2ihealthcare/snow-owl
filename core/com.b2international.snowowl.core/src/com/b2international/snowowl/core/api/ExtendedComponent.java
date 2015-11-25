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

import java.io.Serializable;

import com.b2international.snowowl.core.api.component.IconIdProvider;
import com.b2international.snowowl.core.api.component.LabelProvider;
import com.b2international.snowowl.core.api.component.TerminologyComponentIdProvider;

/**
 * Serializable representation of an extended {@link IComponent component}. Beside all {@link IComponent component} 
 * properties the additional properties are also available:
 * <p>
 * <ul>
 *   <li>{@link IconIdProvider#getIconId() <em>Returns with the icon ID of the component.</em>}</li>
 *   <li>{@link TerminologyComponentIdProvider#getTerminologyComponentId() <em>Returns with the terminology component ID.</em>}</li>
 * </ul>
 * <p>Implementations are restricted to use {@code String} as the unique component ID. 
 * 
 * @see IComponent
 * @see LabelProvider
 * @see TerminologyComponentIdProvider
 * @see IconIdProvider
 */
public interface ExtendedComponent extends IComponent<String>, IconIdProvider<String>, TerminologyComponentIdProvider, Serializable {
}