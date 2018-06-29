/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.terminology;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.b2international.snowowl.core.domain.IComponent;

/**
 * @since 7.0
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Terminology {

	/**
	 * @return the unique application specific identifier of the terminology.
	 */
	String id();
	
	/**
	 * @return the associated unique repository ID to use for the database.
	 */
	String repositoryId();
	
	/**
	 * @return the human-readable name of the terminology.
	 */
	String name();
	
	/**
	 * @return the icon associated with the terminology.
	 */
	String icon();
	
	/**
	 * @return the application specific component ID of the primary component for the terminology.
	 */
	String primaryComponentId();
	
	/**
	 * @return whether the 'effective time' property is supported hence it can be interpreted for the terminology components or not. By default it is set to <code>false</code>.
	 */
	boolean supportsEffectiveTime() default false;
	
	/**
	 * @return the terminology component classes for this terminology
	 */
	Class<? extends IComponent>[] terminologyComponents();
	
}
