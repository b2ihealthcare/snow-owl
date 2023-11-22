/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.converter;

import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;

/**
 * @param <T> the FHIR representation of a value set
 * @param <P> the FHIR representation of a parameter list
 * @since 9.0
 */
public interface ConceptMapConverter<T, P> {

	/**
	 * @param conceptMap
	 * @return
	 */
	T fromInternal(ConceptMap conceptMap);

	/**
	 * @param conceptMap
	 * @return
	 */
	ConceptMap toInternal(T conceptMap);
	
	// $translate operation
	
	/**
	 * @param validateCodeResult
	 * @return
	 */
	P fromTranslateResult(TranslateResult translateResult);
	
	/**
	 * @param parameters
	 * @return
	 */
	TranslateRequest toTranslateRequest(P parameters);
}
