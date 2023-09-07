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
package com.b2international.snowowl.fhir.core.request.codesystem;

import java.util.List;

import org.hl7.fhir.r5.model.CodeSystem.CodeSystemFilterComponent;
import org.hl7.fhir.r5.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.r5.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r5.model.CodeSystem.PropertyType;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;

/**
 * @since 8.0
 */
public interface FhirCodeSystemResourceConverter {

	// The concept identified in this property is a parent of the concept on which it is a property. 
	// The property type will be 'code'. The meaning of 'parent' is defined by the hierarchyMeaning attribute
	PropertyComponent PROPERTY_PARENT = new PropertyComponent("parent", PropertyType.CODE);
	
	// The concept identified in this property is a child of the concept on which it is a property. 
	// The property type will be 'code'. The meaning of 'child' is defined by the hierarchyMeaning attribute
	PropertyComponent PROPERTY_CHILD = new PropertyComponent("child", PropertyType.CODE);
	
	FhirCodeSystemResourceConverter DEFAULT = new FhirCodeSystemResourceConverter() { };

	/**
	 * Implementers may count the number of concepts in the given resource. This method by default uses the generic concept search API to provide the count value.
	 *  
	 * @param resourceUri
	 * @return
	 */
	default int count(ServiceProvider context, ResourceURI resourceUri) {
		return CodeSystemRequests.prepareSearchConcepts()
			.setLimit(0)
			.filterByCodeSystemUri(resourceUri)
			.buildAsync()
			.execute(context)
			.getTotal();
	}

	/**
	 * Implementers may expand a FHIR CodeSystem with its concepts if they wish to include them. This method by default does nothing.
	 * 
	 * @param context - the context to use when expanding additional information
	 * @param resourceURI 
	 * @param locales 
	 */
	default List<ConceptDefinitionComponent> expandConcepts(ServiceProvider context, ResourceURI resourceURI, List<ExtendedLocale> locales) {
		return List.of();
	}

	/**
	 * Implementers may expand a FHIR CodeSystem with its available filters if they wish to include them. This method by default does nothing.
	 * 
	 * @param context - the context to use when expanding additional information
	 * @param resourceURI 
	 * @param locales 
	 */
	default List<CodeSystemFilterComponent> expandFilters(ServiceProvider context, ResourceURI resourceURI, List<ExtendedLocale> locales) {
		return List.of();
	}

	/**
	 * Implementers may expand the FHIR CodeSystem with its available concept properties if they wish to include them. This method returns the common
	 * code system request properties by default. Overriding methods must ensure they return these as well when customizing the return value.
	 * 
	 * @param context - the context to use when expanding additional information
	 * @param resourceURI
	 * @param locales
	 */
	default List<PropertyComponent> expandProperties(ServiceProvider context, ResourceURI resourceURI, List<ExtendedLocale> locales) {
		return List.of();
	}
}
