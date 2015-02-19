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
package com.b2international.snowowl.snomed.reasoner.net4j;

/**
 * Holds constant values related to the SNOMED CT ontology export protocol.
 * 
 */
public abstract class SnomedOntologyProtocol {

	/**
	 * The Net4j protocol name.
	 */
	public static final String PROTOCOL_NAME = SnomedOntologyProtocol.class.getSimpleName();
	
	/**
	 * The signal identifier for the export request/indication. Value: {@value}
	 */
	public static final short EXPORT_SIGNAL_ID = 1;
	
	private SnomedOntologyProtocol() {
		// Prevent instantiation
	}
}