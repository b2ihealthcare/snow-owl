/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.common.Strings;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.branch.Branch;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @since 7.5
 */
public final class CodeSystemURI implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private static final Pattern URI_PATTERN = Pattern.compile("^([^\\/]+)(([\\/]{1}[^\\/\\s]+)*)$");
	
	/**
	 * Represents the latest released version of a code system.
	 */
	public static final String LATEST = "LATEST";
	
	/**
	 * Represents the latest development version of a code system.
	 */
	public static final String HEAD = "HEAD";
	
	private final String uri;
	private final String codeSystem;
	private final String path;
	
	@JsonCreator
	public CodeSystemURI(String uri) throws BadRequestException {
		if (Strings.isNullOrEmpty(uri)) {
			throw new BadRequestException("Malformed CodeSystem URI value: '%s' is empty.", uri);
		}

		if (uri.startsWith(Branch.MAIN_PATH)) {
			throw new BadRequestException("Malformed CodeSystem URI value: '%s' cannot start with MAIN.", uri);
		}
		
		final Matcher matcher = URI_PATTERN.matcher(uri);
		if (!matcher.matches()) {
			throw new BadRequestException("Malformed CodeSystem URI value: '%s' must be in format '<shortName>/<path>'.", uri);
		}
		this.uri = uri;
		this.codeSystem = matcher.group(1);
		this.path = CompareUtils.isEmpty(matcher.group(2)) ? LATEST : matcher.group(2).substring(1); // removes the leading slash character
	}
	
	public String getUri() {
		return uri;
	}
	
	public String getCodeSystem() {
		return codeSystem;
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean isLatest() {
		return LATEST.equals(getPath());
	}
	
	public boolean isHead() {
		return HEAD.equals(getPath());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(uri);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CodeSystemURI other = (CodeSystemURI) obj;
		return Objects.equals(uri, other.uri);
	}

	@JsonValue
	@Override
	public String toString() {
		return getUri();
	}

	public static CodeSystemURI head(String codeSystem) {
		return new CodeSystemURI(String.format("%s/%s", codeSystem, HEAD));
	}

	public static CodeSystemURI branch(String codeSystem, String path) {
		StringBuilder uri = new StringBuilder(codeSystem);
		if (!Strings.isNullOrEmpty(path)) {
			uri.append(Branch.SEPARATOR);
			uri.append(path);
		}
		return new CodeSystemURI(uri.toString());
	}

	public static CodeSystemURI branch(String codeSystem, String path) {
		StringBuilder uri = new StringBuilder(codeSystem);
		if (!Strings.isNullOrEmpty(path)) {
			uri.append(Branch.SEPARATOR);
			uri.append(path);
		}
		return new CodeSystemURI(uri.toString());
	}
	
}
