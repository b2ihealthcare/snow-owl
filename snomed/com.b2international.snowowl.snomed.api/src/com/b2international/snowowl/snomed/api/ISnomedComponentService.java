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
package com.b2international.snowowl.snomed.api;

import com.b2international.snowowl.api.IComponentService;
import com.b2international.snowowl.snomed.api.domain.ISnomedComponent;
import com.b2international.snowowl.snomed.api.domain.ISnomedComponentInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedComponentUpdate;

/**
 * SNOMED CT component service implementations provide methods for <b>c</b>reating, <b>r</b>eading, <b>u</b>pdating and
 * <b>d</b>eleting a single item of a particular component type.
 * 
 * @param <C> the input model type (used when creating a new component; must implement {@link ISnomedComponentInput})
 * @param <R> the read model type (used when retrieving component details; must implement {@link ISnomedComponent})
 * @param <U> the update model type (used when updating an existing component; must implement {@link ISnomedComponentUpdate})
 */
public interface ISnomedComponentService<C extends ISnomedComponentInput, R extends ISnomedComponent, U extends ISnomedComponentUpdate> 
	extends IComponentService<C, R, U> {
	// Empty interface body
}
