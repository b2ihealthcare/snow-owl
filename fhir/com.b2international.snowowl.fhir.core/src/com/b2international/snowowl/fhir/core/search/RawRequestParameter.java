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
package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.google.common.collect.Lists;

/**
 * FHIR URI request URI parameter raw (unprocessed) representation<br> 
 *  
 *  Format:<br>
 *  <pre>
 *  parameterName[:modifier] = value1, value2, value3...
 *  </pre>
 *  
 *  @since 7.17.0
 */
public final class RawRequestParameter {
	
	private final String name;
	private final String modifier;
	private final Collection<String> values;

	public RawRequestParameter(String parameterKey, Collection<String> parameterValues) {
		
		if (StringUtils.isEmpty(parameterKey)) {
			throw new BadRequestException(String.format("Empty parameter key."));
		}
		
		String[] parameterKeyArray = parameterKey.split(":");
		if (parameterKeyArray.length == 1) {
			name = parameterKeyArray[0];
			modifier = null;
		} else if (parameterKeyArray.length == 2) {
			name = parameterKeyArray[0];
			modifier = parameterKeyArray[1];
		} else {
			throw new BadRequestException(String.format("Too many modifiers in parameter key '%s'", parameterKey));
		}
		
		values = splitParameterValues(parameterValues);
	}
	
	/*
	 * org.springframework.util.MultiValueMap does not split the comma separated values.
	 * We have to do it manually here.
	 */
	private List<String> splitParameterValues(Collection<String> unsplitValues) {
		
		List<String> splitParameters = Lists.newArrayList();
		for (String element : unsplitValues) {
			
			//Spring can return a collection with nulls as elements
			if (StringUtils.isEmpty(element)) continue;
			
			element = element.replaceAll(" ", "");
			if (element.contains(",")) {
				String requestedFields[] = element.split(",");
				splitParameters.addAll(Lists.newArrayList(requestedFields));
			} else {
				splitParameters.add(element);
			}
		}
		return splitParameters;
	}
	
	public String getName() {
		return name;
	}
	
	public String getModifier() {
		return modifier;
	}

	public boolean hasModifier() {
		return modifier !=null;
	}
	
	public Collection<String> getValues() {
		return values;
	}
}
