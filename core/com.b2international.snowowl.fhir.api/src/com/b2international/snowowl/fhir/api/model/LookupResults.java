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
package com.b2international.snowowl.fhir.api.model;

import java.util.Collection;

/**
 * 
 * @author bbanfai
 *
 */
public class LookupResults {
	
	//A display name for the code system (1..1)
	private String name;
	
	//The version that these details are based on (0..1)
	private String version;
	
	//The preferred display for this concept (1..1)
	private String display;
	
	//Additional representations for this concept (0..*)
	private Collection<Designation> designations;   
	
	/*
	 * One or more properties that contain additional information about the code, 
	 * including status. For complex terminologies (e.g. SNOMED CT, LOINC, medications), these properties serve to decompose the code
	 * 0..*
	 */
	private Collection<Property> properties;
	
}
