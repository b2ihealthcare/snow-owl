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
package com.b2international.snowowl.snomed.core.domain;

import com.b2international.snowowl.core.exceptions.IllegalQueryParameterException;

/**
 * @since 4.5
 */
public class UnsupportedEscgQueryParameterException extends IllegalQueryParameterException {

	private static final long serialVersionUID = -3009248965656139170L;
	
	public UnsupportedEscgQueryParameterException(String escg) {
		super("Unsupported ESCG query %s", escg);
	}
	
	@Override
	protected String getDeveloperMessage() {
		return "Only <<, <, ^, +, UNION operators are allowed. Check input values.";
	}

}
