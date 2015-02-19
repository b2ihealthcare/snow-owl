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

import java.io.Serializable;

import com.b2international.snowowl.api.IComponentNodeService;
import com.b2international.snowowl.snomed.api.domain.ISnomedConcept;

/**
 * TODO review javadoc
 * 
 * SNOMED CT specific interface of the RESTful Terminology Browser Service.
 * 
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link ISnomedClientTerminologyBrowserService#getRootConcepts() <em>Retrieve root concepts</em>}</li>
 *   <li>{@link ISnomedClientTerminologyBrowserService#getSuperTypes(Serializable, boolean) <em>Retrieve super concepts</em>}</li>
 *   <li>{@link ISnomedClientTerminologyBrowserService#getSubTypes(Serializable, boolean) <em>Retrieve sub concepts</em>}</li>
 * </ul>
 * </p>
 * @param <T> type of the SNOMED CT concept. 
 * The &lt;<b>T</b>&gt; type should implement the {@link IContainerElement} interface.
 * @param <K> type of the SNOMED CT concept's unique identifier.
 * The &lt;<b>K</b>&gt; type should implement the {@link Serializable} interface.  
 * 
 */
public interface ISnomedTerminologyBrowserService extends IComponentNodeService<ISnomedConcept> {
	// Empty interface body
}