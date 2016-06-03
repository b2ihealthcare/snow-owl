/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import com.b2international.commons.platform.Extensions;

/**
 * @since 4.7
 */
public enum CodeSystemEntryBuilderBroker {

	INSTANCE;

	public CodeSystemEntryBuilder getCodeSystemEntryBuilder(final String repositoryUuid) {
		checkNotNull(repositoryUuid, "Repository identifier may not be null.");

		final Collection<CodeSystemEntryBuilder> builders = Extensions.getExtensions(CodeSystemEntryBuilder.EXTENSION_ID,
				CodeSystemEntryBuilder.class);
		for (final CodeSystemEntryBuilder builder : builders) {
			if (repositoryUuid.equals(builder.getRepositoryUuid())) {
				return builder;
			}
		}

		// TODO return base code system builder as default
		throw new IllegalStateException(String.format("Couldn't find code system entry builder for repository %s.", repositoryUuid));
	}

}
