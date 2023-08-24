/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.elasticsearch.core.List;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.bundle.BundleRequests;
import com.b2international.snowowl.core.collection.TerminologyResourceCollection;
import com.b2international.snowowl.core.context.ResourceRepositoryCommitRequestBuilder;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.request.resource.ResourceDeleteRequestBuilder;
import com.b2international.snowowl.core.request.resource.ResourceGetRequestBuilder;
import com.b2international.snowowl.core.request.version.VersionCreateRequestBuilder;
import com.b2international.snowowl.core.request.version.VersionGetRequestBuilder;
import com.b2international.snowowl.core.request.version.VersionSearchRequestBuilder;

/**
 * @since 8.0
 */
public final class ResourceRequests {

	public static final String VERSION_JOB_KEY_PREFIX = "version-";
	
	public static BundleRequests bundles() {
		return new BundleRequests();
	}
	
	public static ResourceUpdateRequestBuilder prepareUpdate(final String resourceId) {
		return new ResourceUpdateRequestBuilder(resourceId);
	}
	
	public static ResourceGetRequestBuilder prepareGet(String resourceId) {
		// XXX the resourceType part of the URI won't and should not be used in the request itself, it is okay to use any here
		return prepareGet(ResourceURI.of("any", resourceId));
	}
	
	public static ResourceGetRequestBuilder prepareGet(ResourceURI resourceUri) {
		return new ResourceGetRequestBuilder(resourceUri);
	}
	
	public static ResourceSearchRequestBuilder prepareSearch() {
		return new ResourceSearchRequestBuilder();
	}
	
	public static ResourceDeleteRequestBuilder prepareDelete(ResourceURI resourceUri) {
		return new ResourceDeleteRequestBuilder(resourceUri);
	}
	
	public static VersionSearchRequestBuilder prepareSearchVersion() {
		return new VersionSearchRequestBuilder();
	}
	
	public static VersionGetRequestBuilder prepareGetVersion(ResourceURI versionUri) {
		return prepareGetVersion(versionUri == null ? null : versionUri.withoutResourceType());
	}
	
	public static VersionGetRequestBuilder prepareGetVersion(String versionUri) {
		return new VersionGetRequestBuilder(versionUri);
	}
	
	public static VersionCreateRequestBuilder prepareNewVersion() {
		return new VersionCreateRequestBuilder();
	}
	
	public static ResourceRepositoryCommitRequestBuilder prepareCommit() {
		return new ResourceRepositoryCommitRequestBuilder();
	}
	
	public static String versionJobKey(ResourceURI codeSystemUri) {
		return VERSION_JOB_KEY_PREFIX.concat(codeSystemUri.toString());
	}
	
	public static boolean isVersionJob(RemoteJobEntry job) {
		return job != null && job.getKey().startsWith(VERSION_JOB_KEY_PREFIX);
	}

	public static ResourceSearchRequestBuilder prepareSearchCollections() {
		return prepareSearch()
					// TODO fix hardcoded info about known collection-like resource types 
					.filterByResourceTypes(List.of(Bundle.RESOURCE_TYPE, TerminologyResourceCollection.RESOURCE_TYPE));
	}
	
}
