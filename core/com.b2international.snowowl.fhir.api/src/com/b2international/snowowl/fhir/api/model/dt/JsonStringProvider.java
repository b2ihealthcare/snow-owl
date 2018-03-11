/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.model.dt;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Classes implementing this interface will be serialized based on
 * the returns string of their {@link #toJsonString()} method.
 * 
 * @since 6.3
 */
@JsonSerialize(using = ToJsonStringSerializer.class)
public interface JsonStringProvider {

	/**
	 * Returns the string to be serialized which will represent this object.
	 * Most of the time the simple string value of the data type.
	 * @return
	 */
	String toJsonString();

}
