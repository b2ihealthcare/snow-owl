/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * @since 9.2.0
 */
public final class R5ObjectFields {

	private R5ObjectFields() {}

	public static abstract class Resource {
	
		public static final String ID = "id";
		public static final String META = "meta";
		public static final String LANGUAGE = "language";
		
		// TODO do we need implicitRules???
		
		public static final Set<String> MANDATORY = Set.of(ID, META);
		public static final Set<String> SUMMARY = ImmutableSet.<String>builder()
				.addAll(MANDATORY)
				.build();
		
	}
	
	public static abstract class DomainResource extends Resource {
		
		public static final String TEXT = "text";
		
	}
	
	public static abstract class MetadataResource extends DomainResource {
		
		public static final String URL = "url";
		public static final String VERSION = "version";
		public static final String NAME = "name";
		public static final String TITLE = "title";
		public static final String STATUS = "status";
		public static final String DATE = "date";
		public static final String PUBLISHER = "publisher";
		public static final String CONTACT = "contact";
		public static final String DESCRIPTION = "description";
		// XXX do we need usageContexts???
		// XXX do we need jurisdictions???
		public static final String PURPOSE = "purpose";
		
		public static final Set<String> MANDATORY = ImmutableSet.<String>builder()
				.addAll(Resource.MANDATORY)
				.add(STATUS)
				.build();
		
		public static final Set<String> SUMMARY = ImmutableSet.<String>builder()
				.addAll(Resource.SUMMARY)
				.add(URL, VERSION, NAME, TITLE, DATE, PUBLISHER)
				.build();
		
	}
	
	public static final class CodeSystem extends MetadataResource {
		
		// XXX do we need caseSensitive???
		public static final String CONTENT = "content";
		// TODO valueSet
		public static final String COUNT = "count";
		// XXX do we need hierarchyMeaning???
		// XXX do we need compositional???
		// XXX do we need versionNeeded???
		// XXX do we need supplements???

		public static final String COPYRIGHT = "copyright";
		
		// complex properties
		public static final String IDENTIFIER = "identifier";
		public static final String FILTER = "filter";
		public static final String PROPERTY = "property";
		public static final String CONCEPT = "concept";
		
		public static final Set<String> MANDATORY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.MANDATORY)
				.add(CONTENT)
				.build();
		
		public static final Set<String> SUMMARY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.SUMMARY)
				.add(COUNT, FILTER, PROPERTY, IDENTIFIER)
				.build();
		
		public static final Set<String> SUMMARY_TEXT = ImmutableSet.<String>builder()
				.addAll(MetadataResource.MANDATORY)
				.add(TEXT)
				.build();
		
		public static final Set<String> SUMMARY_DATA = MANDATORY;
		
		public static final Set<String> ALL = ImmutableSet.<String>builder()
				.addAll(MANDATORY)
				.addAll(SUMMARY)
				.add(TEXT)
				.add(CONCEPT)
				.build();
		
	}
	
	public static final class ConceptMap extends MetadataResource {
		
		public static final String GROUP = "group";
		
		public static final Set<String> MANDATORY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.MANDATORY)
				.build();
		
		public static final Set<String> SUMMARY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.SUMMARY)
				.build();
		
		public static final Set<String> SUMMARY_TEXT = ImmutableSet.<String>builder()
				.addAll(MetadataResource.MANDATORY)
				.build();
		
		public static final Set<String> SUMMARY_DATA = MANDATORY;
		
		public static final Set<String> ALL = ImmutableSet.<String>builder()
				.addAll(MANDATORY)
				.addAll(SUMMARY)
				.add(GROUP)
				.build();

		
	}
	
	public static final class Fields extends MetadataResource {
		
		public static final String IMMUTABLE = "immutable";
		public static final String COMPOSE = "compose";
		public static final String EXPANSION = "expansion";
		
		public static final Set<String> MANDATORY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.MANDATORY)
				.build();
		
		public static final Set<String> SUMMARY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.SUMMARY)
				.build();
		
		public static final Set<String> SUMMARY_TEXT = ImmutableSet.<String>builder()
				.addAll(MetadataResource.MANDATORY)
				.build();
		
		public static final Set<String> SUMMARY_DATA = MANDATORY;
		
		public static final Set<String> ALL = ImmutableSet.<String>builder()
				.addAll(MANDATORY)
				.addAll(SUMMARY)
				.add(IMMUTABLE)
				.add(COMPOSE)
				.add(EXPANSION)
				.build();
			
	}
	
}
