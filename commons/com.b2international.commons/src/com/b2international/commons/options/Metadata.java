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
package com.b2international.commons.options;

import java.util.Map;

/**
 * @since 4.1
 */
public interface Metadata extends Map<String, Object> {

	<T> T get(String key, Class<T> type);
	
	String getString(String key);

	Integer getInt(String key);

	Boolean getBoolean(String key);

	Float getFloat(String key);

	Double getDouble(String key);

	Short getShort(String key);

	Character getChar(String key);

	Byte getByte(String key);

}
