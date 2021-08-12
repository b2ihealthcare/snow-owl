/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.rest;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.StringUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.fhir.core.model.FhirResource;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @since 6.4
 */
public abstract class AbstractFhirResourceController<R extends FhirResource> extends AbstractFhirController {

	//TODO: should this be grabbed from the server preferences or from the request?
	public static final String NHS_REALM_LANGUAGE_REFSET_ID = "999000671000001103";
	public static final ExtendedLocale NHS_REALM_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_LANGUAGE_REFSET_ID);

	public static final String NHS_REALM_CLINICAL_LANGUAGE_REFSET_ID = "999001261000000100";
	public static final ExtendedLocale NHS_REALM_CLINICAL_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_CLINICAL_LANGUAGE_REFSET_ID);
	
	public static final String NHS_REALM_PHARMACY_LANGUAGE_REFSET_ID = "999000691000001104";
	public static final ExtendedLocale NHS_REALM_PHARMACY_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_PHARMACY_LANGUAGE_REFSET_ID);
	public static final ExtendedLocale INT_LOCALE = ExtendedLocale.valueOf("en-us");
	
	protected List<ExtendedLocale> locales = ImmutableList.of(INT_LOCALE, NHS_REALM_LOCALE, NHS_REALM_CLINICAL_LOCALE, NHS_REALM_PHARMACY_LOCALE);
	
	protected abstract Class<R> getModelClass();
	
	protected List<String> getRequestedFields(Collection<String> elements) {
		
		List<String> requestedParameters = Lists.newArrayList();
		for (String element : elements) {
			element = element.replaceAll(" ", "");
			if (element.contains(",")) {
				String requestedFields[] = element.split(",");
				requestedParameters.addAll(Lists.newArrayList(requestedFields));
			} else {
				if (!StringUtils.isEmpty(element)) {
					requestedParameters.add(element);
				}
			}
		}
		return requestedParameters;
	}
	
}
