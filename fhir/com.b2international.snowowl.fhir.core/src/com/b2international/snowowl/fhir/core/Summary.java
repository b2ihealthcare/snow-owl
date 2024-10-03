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
 * @since 9.3
 */
public interface Summary {

	String TRUE = "true";
	String FALSE = "false";
	String TEXT = "text";
	String DATA = "data";
	String COUNT = "count";
	
	Set<String> VALUES = ImmutableSet.of(TRUE, FALSE, TEXT, DATA, COUNT);
	
}
