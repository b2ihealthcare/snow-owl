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
package com.b2international.snowowl.snomed.datastore;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;

/**
 * Class for ESCG expression constants.
 *
 */
public abstract class EscgExpressionConstants {

	public static final String UNRESTRICTED_EXPRESSION = "<<" + Concepts.ROOT_CONCEPT;
	public static final String REJECT_ALL_EXPRESSION = "com.b2international.snowowl.snomed.datastore.EscgExpressionConstants.REJECT_ALL_EXPRESSION";
	
	private EscgExpressionConstants() {
	}
	
	
}