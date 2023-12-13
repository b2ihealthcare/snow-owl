/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.snowowl.core.ServiceProvider;

/**
 * Interface to allow nested requests to configure the read timestamp of the current revision read operation. Usually the information to determine the
 * actual read timestamp is carried by the nested request, this interface allows the nested request to configure the index read operation with that
 * timestamp.
 * 
 * @since 8.7
 */
public interface RevisionIndexReadRequestTimestampProvider {

	/**
	 * Provides the actual read timestamp based on the current request configuration and the given context.
	 * 
	 * @param context - the context opened to read from the latest revision index snapshot
	 * @return
	 */
	Long getReadTimestamp(ServiceProvider context);

}
