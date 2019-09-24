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
package com.b2international.snowowl.fhir.core.codesystems;

import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.ResourceNarrative;
import com.google.common.collect.Lists;

/**
 * FHIR Code system content mode
 * 
 * @since 6.4
 */
@ResourceNarrative("How much of the content of the code system - the concepts and codes it defines - are represented in a code system resource.")
public enum CodeSystemContentMode implements FhirCodeSystem {
	
	//None of the concepts defined by the code system are included in the code system resource.
	NOT_PRESENT,
	
	//A few representative concepts are included in the code system resource
	EXAMPLE,
	
	//A subset of the code system concepts are included in the code system resource
	FRAGMENT,
	
	//All the concepts defined by the code system are included in the code system resource
	COMPLETE;
	
	public static final String CODE_SYSTEM_URI = "http://hl7.org/fhir/codesystem-content-mode";

	@Override
	public String getCodeValue() {
		return name().toLowerCase().replaceAll("_", "-");
	}
	
	@Override
	public String getDisplayName() {
		
		String name = name().toLowerCase();
		if (StringUtils.isSingleWord(name)) {
			return StringUtils.capitalizeFirstLetter(name);
		} else {
			Iterable<String> words = StringUtils.getWords(name());
			List<String> wordList = Lists.newArrayList(words);
			return wordList.stream().map(StringUtils::capitalizeFirstLetter).collect(Collectors.joining());
		}
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}

}
