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

import java.util.Map;

import com.b2international.snowowl.api.domain.IComponentList;
import com.b2international.snowowl.snomed.api.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.api.domain.ISnomedConceptInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedConceptUpdate;
import com.b2international.snowowl.snomed.api.domain.SearchKind;

/**
 * TODO document
 */
public interface ISnomedConceptService extends ISnomedComponentService<ISnomedConceptInput, ISnomedConcept, ISnomedConceptUpdate> {

	IComponentList<ISnomedConcept> getAllConcepts(String version, String taskId, int offset, int limit);

	IComponentList<ISnomedConcept> search(String version, String taskId, Map<SearchKind, String> queryParams, int offset, int limit);
}