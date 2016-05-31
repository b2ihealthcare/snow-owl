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
package com.b2international.snowowl.datastore.version;

import java.util.Collection;

import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * Service for collection existing {@link ICodeSystemVersion versions}.
 * 
 * @deprecated - use {@link Request} API instead.
 */
public interface IVersionCollector {

	/**Function for extracting the version name from a {@link VersionNameWithCreationDate} instance.*/
	Function<ICodeSystemVersion, String> GET_VERSION_NAME_FUNC = new Function<ICodeSystemVersion, String>() {
		@Override public String apply(final ICodeSystemVersion version) {
			return Preconditions.checkNotNull(version).getVersionId();
		}
	};
	
	/**Returns with a collection of existing {@link ICodeSystemVersion versions}.*/
	Collection<ICodeSystemVersion> getVersions();
	
}