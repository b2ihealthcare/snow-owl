/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.resource;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.request.resource.BaseTerminologyResourceSearchRequest.OptionKey;

/**
 * @since 8.12
 * @param <RB>
 * @param <R>
 */
public abstract class BaseTerminologyResourceSearchRequestBuilder<RB extends BaseTerminologyResourceSearchRequestBuilder<RB, R>, R extends PageableCollectionResource<?>> 
	extends BaseResourceSearchRequestBuilder<RB, R> {

	public final RB filterByDependency(String dependency) {
		return addOption(OptionKey.DEPENDENCY, dependency);
	}
	
}
