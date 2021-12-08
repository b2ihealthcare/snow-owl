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
 * Restricts access in low-level resource query expressions to resources that
 * are authorized for viewing.
 * 
 * @since 8.0.1
 */
public interface ResourceAuthorization {

	/**
	 * Configures security filters to allow access to certain resources only.
	 * 
	 * @param context      - the context where user information will be extracted
	 * @param queryBuilder - the query builder to append the clauses to
	 */
	void addSecurityFilter(ServiceProvider context, ExpressionBuilder queryBuilder);
}
