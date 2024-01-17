/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.uri;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver.PathWithVersion;

/**
 * Special URI resolver interface that can resolve single path segment special URI values to actual branch paths.
 * 
 * @since 9.0.0
 */
public interface TerminologyResourceURIPathResolver {

	/**
	 * @param uri
	 * @return <code>true</code> if the given {@link ResourceURI} can be resolved to an actual branch path by this implementation.
	 */
	boolean canResolve(ResourceURI uri);
	
	/**
	 * Resolves a {@link ResourceURI} to an absolute branch path.
	 * 
	 * @param context - the context to use for the resolution
	 * @param uriToResolve - the full URI to resolve
	 * @param resource - the resource identified from the {@link ResourceURI}
	 * @return the resolved {@link PathWithVersion} entry for the URI
	 */
	PathWithVersion resolve(ServiceProvider context, ResourceURI uriToResolve, TerminologyResource resource);
	
}
