/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.index;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.b2international.index.migrate.SchemaRevision;

/**
 * @since 4.7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Doc {

	String type() default "";

	boolean nested() default true;

	/**
	 * Whether to index or not the type annotated with this annotation. Root objects always indexed and have index fields, nested objects how ever can
	 * set this value to either <code>true</code> or <code>false</code> depending on whether they would like to support search or not.
	 * 
	 * @return
	 */
	boolean index() default true;

	/**
	 * @return an array of field names that should be used for computing the revision hash, if empty no hash will be computed and the component will
	 *         not have property level change history
	 */
	String[] revisionHash() default {};
	
	/**
	 * Schema revisions associated with this document type. The current schema revision version (default is 1) is stored in the current mapping
	 * `_meta.version` value. Every change to the schema must be recorded as a {@link SchemaRevision} annotation in the corresponding type's
	 * {@link Doc} annotation, otherwise the system will report an error and won't be able to start even if the change is compatible with the current
	 * schema.
	 * 
	 * @return an array of schema revisions assigned to this document type or an empty array if no revisions have been registered yet.
	 */
	SchemaRevision[] revisions() default {};
	
}
