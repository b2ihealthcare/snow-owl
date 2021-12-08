/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.authorization;

import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;

/**
 * Base class for chaining resource authorization implementations. Subclasses
 * should make changes on their own, then (optionally) call the method in the
 * parent class to invoke the next implementation in the chain.
 * 
 * @since 8.0.1
 */
public abstract class BaseResourceAuthorization implements ResourceAuthorization {

	private final ResourceAuthorization next;

	protected BaseResourceAuthorization() {
		this(null);
	}
	
	protected BaseResourceAuthorization(final ResourceAuthorization next) {
		this.next = next;
	}
	
	@Override
	public void addSecurityFilter(final ServiceProvider context, final ExpressionBuilder queryBuilder) {
		if (next != null) {
			next.addSecurityFilter(context, queryBuilder);
		}
	}
}
