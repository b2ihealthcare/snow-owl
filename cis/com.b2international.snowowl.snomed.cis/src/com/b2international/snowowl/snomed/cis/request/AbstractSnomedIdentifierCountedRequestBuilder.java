/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.request;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.domain.SctIds;

/**
 * @since 5.5
 */
public abstract class AbstractSnomedIdentifierCountedRequestBuilder<B extends AbstractSnomedIdentifierCountedRequestBuilder<B>> 
		extends BaseRequestBuilder<B, ServiceProvider, SctIds> 
		implements SystemRequestBuilder<SctIds> {

	protected ComponentCategory category;
	protected String namespace;
	protected int quantity = 1;
	
	public B setCategory(ComponentCategory category) {
		this.category = category;
		return getSelf();
	}
	
	public B setNamespace(String namespace) {
		this.namespace = namespace;
		return getSelf();
	}
	
	public B setQuantity(int quantity) {
		this.quantity = quantity;
		return getSelf();
	}

}
