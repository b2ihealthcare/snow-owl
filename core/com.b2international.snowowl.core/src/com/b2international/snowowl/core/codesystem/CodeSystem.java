/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.TerminologyResource;

/**
 * Captures metadata about a code system, which holds a set of concepts of medical significance (optionally with other, supporting components that
 * together make up the definition of concepts) and their corresponding unique code.
 * 
 * @since 1.0
 */
public class CodeSystem extends TerminologyResource {
	
	private static final long serialVersionUID = 761L;
	
	/**
	 * @since 8.0
	 */
	public static final class CommonSettings {
		public static final String LOCALES = "locales";
	}

	@Override
	public String getResourceType() {
		return "codesystem";
	}
	
	/**
	 * @return the list of {@link ExtendedLocale} instances representing the language content this code system carries (can be {@code null})
	 */
	public List<ExtendedLocale> getLocales() {
		return (List<ExtendedLocale>) getSettings().get(CommonSettings.LOCALES);
	}
	
//	/**
//	 * Returns all code system short name dependencies and itself.
//	 */
//	@JsonIgnore
//	public SortedSet<String> getDependenciesAndSelf() {
//		ImmutableSortedSet.Builder<String> affectedCodeSystems = ImmutableSortedSet.naturalOrder();
//		affectedCodeSystems.addAll(getDependencies());
//		affectedCodeSystems.add(shortName);
//		return affectedCodeSystems.build();
//	}
	
//	/**
//	 * Returns the short names of all affected code systems
//	 */
//	@JsonIgnore
//	public SortedSet<String> getDependencies() {
//		return TerminologyRegistry.INSTANCE.getTerminology(terminologyId).getDependencies();
//	}
	
}
