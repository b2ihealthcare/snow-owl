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
package com.b2international.snowowl.core.request;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.request.resource.ResourceDeleteRequestBuilder;
import com.b2international.snowowl.core.request.version.VersionCreateRequestBuilder;
import com.b2international.snowowl.core.request.version.VersionGetRequestBuilder;
import com.b2international.snowowl.core.request.version.VersionSearchRequestBuilder;

/**
 * @since 8.0
 */
public final class ResourceRequests {

	public static final String VERSION_JOB_KEY_PREFIX = "version-";
	
	public static ResourceGetRequestBuilder prepareGet(ResourceURI resourceUri) {
		return prepareGet(resourceUri.getResourceId());
	}
	
	public static ResourceUpdateRequestBuilder prepareUpdate(final String resourceId) {
		return new ResourceUpdateRequestBuilder(resourceId);
	}
	
	public static ResourceGetRequestBuilder prepareGet(String resourceId) {
		return new ResourceGetRequestBuilder(resourceId);
	}
	
	public static ResourceSearchRequestBuilder prepareSearch() {
		return new ResourceSearchRequestBuilder();
	}
	
	public static ResourceDeleteRequestBuilder prepareDelete(ResourceURI resourceUri) {
		return prepareDelete(resourceUri.getResourceId());
	}
	
	public static ResourceDeleteRequestBuilder prepareDelete(String resourceId) {
		return new ResourceDeleteRequestBuilder(resourceId, ResourceDocument.class);
	}
	
	public static VersionSearchRequestBuilder prepareSearchVersion() {
		return new VersionSearchRequestBuilder();
	}
	
	public static VersionGetRequestBuilder prepareGetVersion(ResourceURI versionUri) {
		return new VersionGetRequestBuilder(versionUri);
	}
	
	public static VersionCreateRequestBuilder prepareNewVersion() {
		return new VersionCreateRequestBuilder();
	}
	
	public static String versionJobKey(ResourceURI codeSystemUri) {
		return VERSION_JOB_KEY_PREFIX.concat(codeSystemUri.toString());
	}
	
	public static boolean isVersionJob(RemoteJobEntry job) {
		return job != null && job.getKey().startsWith(VERSION_JOB_KEY_PREFIX);
	}
	
}
