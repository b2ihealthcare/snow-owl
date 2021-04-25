/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.mapping;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.b2international.index.ID;

/**
 * @since 8.0
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Field {

	/**
	 * @return whether this field is marked as the default sort field when nothing else is specified for the type. If none is specified default sort
	 *         field falls back to the {@link ID} annotated field.
	 */
	boolean defaultSortBy() default false;
	
	/**
	 * Define the aliases here via {@link FieldAlias} annotations. By default there is not alias defined on any field.
	 * @return
	 */
	FieldAlias[] aliases() default {};
	
	/**
	 * @return whether to make the field available in search or just store only, defaults to make any field available for search always.
	 */
	boolean index() default true;

}
