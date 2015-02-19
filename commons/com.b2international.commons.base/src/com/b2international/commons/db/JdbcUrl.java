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
package com.b2international.commons.db;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Represents a JDBC connection URL.
 * 
 */
public final class JdbcUrl {

	private final String scheme;
	private final String domain;
	private final String jdbcSettings;

	/**
	 * @param scheme
	 * @param domain
	 * @param jdbcSettings
	 *            - may be <code>null</code>
	 */
	public JdbcUrl(final String scheme, final String domain, final String jdbcSettings) {
		this.scheme = Preconditions.checkNotNull(scheme, "URL scheme argument cannot be null.");
		this.domain = Preconditions.checkNotNull(domain, "URL domain argument cannot be null.");
		this.jdbcSettings = Strings.nullToEmpty(jdbcSettings);
	}

	/** Builds and returns with the JDBC URL for the connection as a string. */
	public String build(final String repositoryName) {
		return new StringBuilder(scheme).append(domain)
				.append(Preconditions.checkNotNull(repositoryName, "Repository name argument cannot be null."))
				.append(jdbcSettings).toString();
	}
}