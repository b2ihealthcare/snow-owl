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
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;

/**
 * Handles cases where the user has browse permissions for any resource (including
 * administrators, as they have permission to perform any action on any resource).
 * <p>
 * If the user is authorized to browse any resource, the next implementation in the
 * chain will not be called and no further filter clauses will be added to the 
 * query builder. 
 * 
 * @since 8.0.1
 */
public final class AnyResourceAuthorization extends BaseResourceAuthorization {

	public AnyResourceAuthorization() {
		super();
	}
	
	public AnyResourceAuthorization(final ResourceAuthorization next) {
		super(next);
	}

	@Override
	public void addSecurityFilter(final ServiceProvider context, final ExpressionBuilder queryBuilder) {
		final User user = context.service(User.class);

		if (!user.hasPermission(Permission.requireAll(Permission.OPERATION_BROWSE, Permission.ALL))) {
			super.addSecurityFilter(context, queryBuilder);
		}
	}
}
