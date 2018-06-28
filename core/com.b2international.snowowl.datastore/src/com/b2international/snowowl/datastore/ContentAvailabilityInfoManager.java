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
package com.b2international.snowowl.datastore;

import static com.b2international.commons.extension.Extensions.getExtensions;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Manager for available and registered {@link ContentAvailabilityInfoProvider} instances.
 *
 */
public enum ContentAvailabilityInfoManager {

	INSTANCE;
	
	private static final String EXTENSION_POINT_ID = "com.b2international.snowowl.datastore.contentAvailabilityInfoProvider";
	
	/**
	 * Returns with {@code true} if the underlying content is available for the
	 * given tooling feature. 
	 * Otherwise returns with {@code false}.
	 * @param repositoryUuid the unique repository UUID to check the content availability. 
	 * @return {@code true} if the content is available.
	 */
	public boolean isAvailable(final String repositoryUuid) {
		return Iterables.find(getExtensions(EXTENSION_POINT_ID, ContentAvailabilityInfoProvider.class), new Predicate<ContentAvailabilityInfoProvider>() {
			public boolean apply(final ContentAvailabilityInfoProvider provider) {
				return checkNotNull(repositoryUuid, "repositoryUuid").equals(provider.getRepositoryUuid());
			}
		}, ContentAvailabilityInfoProvider.NULL_IMPL).isAvailable();
	}
	
}