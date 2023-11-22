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

import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.codesystem.*;

/**
 * @param <T> the FHIR representation of a code system
 * @param <P> the FHIR representation of a parameter list
 * @since 9.0
 */
public interface CodeSystemConverter<T, P> {

	// Resource conversion
	
	/**
	 * @param codeSystem
	 * @return
	 */
	T fromInternal(CodeSystem codeSystem);

	/**
	 * @param codeSystem
	 * @return
	 */
	CodeSystem toInternal(T codeSystem);

	// $lookup operation

	/**
	 * @param lookupResult
	 * @return
	 */
	P fromLookupResult(LookupResult lookupResult);
	
	/**
	 * @param parameters
	 * @return
	 */
	LookupRequest toLookupRequest(P parameters);
	
	// $subsumes operation
	
	/**
	 * @param subsumptionResult
	 * @return
	 */
	P fromSubsumptionResult(SubsumptionResult subsumptionResult);
	
	/**
	 * @param parameters
	 * @return
	 */
	SubsumptionRequest toSubsumptionRequest(P parameters);
	
	// $validate-code operation
	
	/**
	 * @param validateCodeResult
	 * @return
	 */
	P fromValidateCodeResult(ValidateCodeResult validateCodeResult);
	
	/**
	 * @param parameters
	 * @return
	 */
	ValidateCodeRequest toValidateCodeRequest(P parameters);
}
