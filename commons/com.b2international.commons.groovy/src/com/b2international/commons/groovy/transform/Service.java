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
package com.b2international.commons.groovy.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

/**
 * Variable annotation used for injecting a service instance into the current script with field visibility.
 * <p>
 * The type of the variable annotated with {@Service} must NOT extend {@link groovy.lang.Script}.
 *
 */
@java.lang.annotation.Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.LOCAL_VARIABLE })
@GroovyASTTransformationClass("com.b2international.commons.groovy.transform.ServiceASTTransformation")
public @interface Service {
}