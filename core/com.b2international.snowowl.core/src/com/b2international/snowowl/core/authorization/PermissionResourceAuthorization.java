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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.ResourceDocument;

/**
 * Constructs filter clauses based on the permissions extracted from the
 * {@link User} instance of the context.
 * 
 * @since 8.0.1
 */
public final class PermissionResourceAuthorization extends BaseResourceAuthorization {

	public PermissionResourceAuthorization() {
		super();
	}
	
	public PermissionResourceAuthorization(final ResourceAuthorization next) {
		super(next);
	}

	@Override
	public void addSecurityFilter(final ServiceProvider context, final ExpressionBuilder queryBuilder) {
		final User user = context.service(User.class);

		// Collect resource IDs and prefixes from "browse" and "*" operation-permitting elements
		final List<Permission> readPermissions = user.getPermissions().stream()
			.filter(p -> Permission.ALL.equals(p.getOperation()) || Permission.OPERATION_BROWSE.equals(p.getOperation()))
			.collect(Collectors.toList());

		// Split resource IDs into two sets, depending on whether they include a wildcard at the end or not
		final Map<Boolean, Set<String>> resourceIdPartitions = readPermissions.stream()
			.flatMap(p -> p.getResources().stream())
			.collect(Collectors.partitioningBy(r -> r.endsWith("*"), Collectors.toSet()));

		final Set<String> resourceIdPrefixes = resourceIdPartitions.get(true);
		final Set<String> resourceIds = resourceIdPartitions.get(false);

		if (!resourceIdPrefixes.isEmpty() || !resourceIds.isEmpty()) {
			context.log().info("Restricting user '{}' to resources exact: '{}', prefix: '{}'.", user.getUsername(), resourceIds, resourceIdPrefixes);

			final ExpressionBuilder securityFilter = Expressions.builder();

			// Look for permitted values in resources, bundles and bundle ancestors, as read access is inherited from bundles
			if (!resourceIds.isEmpty()) {
				securityFilter.should(ResourceDocument.Expressions.ids(resourceIds));
				securityFilter.should(ResourceDocument.Expressions.bundleIds(resourceIds));
				securityFilter.should(ResourceDocument.Expressions.bundleAncestorIds(resourceIds));
			}

			if (!resourceIdPrefixes.isEmpty()) {
				securityFilter.should(ResourceDocument.Expressions.idPrefixes(resourceIdPrefixes));
				securityFilter.should(ResourceDocument.Expressions.bundleIdPrefixes(resourceIdPrefixes));
				securityFilter.should(ResourceDocument.Expressions.bundleAncestorIdPrefixes(resourceIdPrefixes));
			}

			queryBuilder.filter(securityFilter.build());
		}

		// Always call the next authorization strategy in the chain in case it wants to add further clauses to the expression
		super.addSecurityFilter(context, queryBuilder);
	}
}
