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

import com.b2international.snowowl.core.api.component.IdProvider;
import com.b2international.snowowl.core.api.component.LabelProvider;

/**
 * Represents a terminology independent component with a unique identifier and a human readable label.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link IComponent#getId() <em>Retrieve the identifier</em>}</li>
 *   <li>{@link IComponent#getLabel() <em>Retrieve the component label</em>}</li>
 * </ul>
 * </p>
 * @param <K> type of the identifier.
 */
public interface IComponent<K> extends LabelProvider, IdProvider<K>, Serializable {
	
}