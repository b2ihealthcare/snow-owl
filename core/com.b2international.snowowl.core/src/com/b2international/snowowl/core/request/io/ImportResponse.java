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
package com.b2international.snowowl.core.request.io;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * @since 7.12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ImportResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String error;
	private final Set<ComponentURI> visitedComponents;
	private final List<ImportDefect> defects;
	
	@JsonCreator
	public ImportResponse(
			@JsonProperty("error") final String error, 
			@JsonProperty("visitedComponents") final Set<ComponentURI> visitedComponents, 
			@JsonProperty("defects") final List<ImportDefect> defects) {
		this.error = error;
		this.visitedComponents = visitedComponents;
		this.defects = defects;
	}
	
	public boolean isSuccess() {
		return Strings.isNullOrEmpty(error);
	}

	/**
	 * @return all defects registered in this response
	 */
	public List<ImportDefect> getDefects() {
		return defects;
	}
	
	@JsonIgnore
	public List<ImportDefect> getErrors() {
		return Collections3.toImmutableList(getDefects()).stream().filter(ImportDefect::isError).collect(Collectors.toList());
	}
	
	@JsonIgnore
	public List<ImportDefect> getWarnings() {
		return Collections3.toImmutableList(getDefects()).stream().filter(ImportDefect::isWarning).collect(Collectors.toList());
	}
	
	@JsonIgnore
	public List<ImportDefect> getInfos() {
		return Collections3.toImmutableList(getDefects()).stream().filter(ImportDefect::isInfo).collect(Collectors.toList());
	}
	
	public String getError() {
		return error;
	}
	
	public Set<ComponentURI> getVisitedComponents() {
		return visitedComponents;
	}

	public static ImportResponse error(String error) {
		return new ImportResponse(error, Set.of(), List.of());
	}

	public static ImportResponse success(Set<ComponentURI> visitedComponents) {
		return new ImportResponse(null, visitedComponents, List.of());
	}
	
	public static ImportResponse defects(List<ImportDefect> defects) {
		return new ImportResponse(String.format("There are '%s' issues with the import file.", defects.size()), Set.of(), defects);
	}

}
