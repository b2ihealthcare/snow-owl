/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.migrate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a schema revision, a mapping change compared to the previous document mapping.  
 * 
 * @since 9.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SchemaRevision {

	/**
	 * The model version number associated with this schema revision. When upgrading a mapping from an older version then the associated {@link #strategy()} will be applied to existing indices.
	 * Usually this number starts from 2, since the first revision of the schema is the version that is applied when the mapping is first created.
	 * 
	 * @return 
	 */
	long version();
	
	/**
	 * @return a human-readable description describing the model changes compared to the previous mapping and this schema revision
	 */
	String description() default "";
	
	/**
	 * @return the {@link DocumentMappingMigrationStrategy} selected for this schema revision
	 */
	DocumentMappingMigrationStrategy strategy();
	
	/**
	 * @return a transformation script that will be executed when migrating from an older version to this schema revision.
	 */
	Class<? extends DocumentMappingMigrator> migrator() default DocumentMappingMigrator.ReindexAsIs.class;
	
}
