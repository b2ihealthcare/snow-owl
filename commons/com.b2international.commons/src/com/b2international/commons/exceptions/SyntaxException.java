/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.exceptions;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Map.Entry;

import com.b2international.commons.Pair;

/**
 * Generic syntax exception
 * 
 * @since 5.4
 */
public final class SyntaxException extends BadRequestException {

	private static final long serialVersionUID = -6130929059690887226L;
	private final Map<String, Object> additionalInfo;
	
	public SyntaxException(String language, Map<Pair<Integer, Integer>, String> errors) {
		super("One or more %s syntax errors", language);
		additionalInfo = newHashMap();
		for (Entry<Pair<Integer, Integer>, String> entry : errors.entrySet()) {
			additionalInfo.put(String.format("[%s,%s]", entry.getKey().getA(), entry.getKey().getB()), entry.getValue());
		}
	}
	
	@Override
	protected Map<String, Object> getAdditionalInfo() {
		return additionalInfo;
	}
	
}
