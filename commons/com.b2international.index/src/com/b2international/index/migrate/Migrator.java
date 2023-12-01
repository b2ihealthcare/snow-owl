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

import java.lang.annotation.*;

/**
 * @since 9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Migrator {

	/**
	 * The model version number associated with this migrator. When upgrading a mapping from an older version than this the associated {@link #script()} will be executed.
	 * 
	 * @return 
	 */
	long version();
	
	/**
	 * @return a human-readable description describing the model changes compared to the previous mapping and this version
	 */
	String description() default "";
	
	/**
	 * @return the {@link DocumentMappingMigrationStrategy} selected for this schema version
	 */
	DocumentMappingMigrationStrategy strategy();
	
	/**
	 * 
	 * @return
	 */
	String scriptPainless();
	
	/**
	 * @return a transformation script that will be executed when migrating from an older version to the version described in {@link #version()}.
	 */
	Class<? extends DocumentMappingMigrator<?>> scriptJava() default DocumentMappingMigrator.ReindexAsIs.class;

}
