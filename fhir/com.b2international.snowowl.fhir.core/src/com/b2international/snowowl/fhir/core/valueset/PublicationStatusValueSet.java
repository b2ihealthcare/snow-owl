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
package com.b2international.snowowl.fhir.core.valueset;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.google.common.collect.Sets;

/**
 * All codes from {@link PublicationStatus} code system.
 * I am not sure this makes sense.
 * @since 6.3
 */
public class PublicationStatusValueSet {
	
	private static Collection<Code> codes = Sets.newHashSet();
	
	static {
		codes = Arrays.stream(PublicationStatus.values()).map(s -> s.getCode()).collect(Collectors.toSet());
	}
	
	public static Code getCode(String codeValue) {
		codes.stream().filter(c -> c.getCodeValue().equals(codeValue));
		return null;
	}

}
