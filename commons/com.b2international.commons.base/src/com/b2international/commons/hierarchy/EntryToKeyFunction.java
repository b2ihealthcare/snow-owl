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
package com.b2international.commons.hierarchy;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;

/**
 * @param <T> type for entry key and value
 *
 */
public class EntryToKeyFunction<T> implements Function<Map.Entry<T, T>, T> {

	@Override
	public T apply(Entry<T, T> input) {
		return input.getKey();
	}

}