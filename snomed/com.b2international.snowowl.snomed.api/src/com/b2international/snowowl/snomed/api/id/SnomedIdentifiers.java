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
package com.b2international.snowowl.snomed.api.id;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.datastore.ComponentNature;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;

/**
 * Shortcut methods to create SNOMED CT Identifiers.
 * 
 * @since 1.0
 */
public class SnomedIdentifiers {

	private SnomedIdentifiers() {}
	
	public static String generateConceptId() {
		return generateConceptId(null);
	}
	
	public static String generateConceptId(String namespace) {
		return generateComponentId(ComponentNature.CONCEPT, namespace);
	}
	
	public static String generateRelationshipId() {
		return generateRelationshipId(null);
	}
	
	public static String generateRelationshipId(String namespace) {
		return generateComponentId(ComponentNature.RELATIONSHIP, namespace);
	}
	
	public static String generateDescriptionId() {
		return generateRelationshipId(null);
	}
	
	public static String generateDescriptionId(String namespace) {
		return generateComponentId(ComponentNature.DESCRIPTION, namespace);
	}

	private static String generateComponentId(ComponentNature component, String namespace) {
		return getSnomedIdentifierService().generateId(component, namespace);
	}

	private static ISnomedIdentifierService getSnomedIdentifierService() {
		return getApplicationContext().getServiceChecked(ISnomedIdentifierService.class);
	}
	
	private static ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
	
}
