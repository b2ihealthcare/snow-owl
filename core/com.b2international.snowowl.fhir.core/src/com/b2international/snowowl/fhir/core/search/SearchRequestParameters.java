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
package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SearchRequestParameterKey;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SearchRequestParameterType;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SummaryParameterValue;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Search request result parameter keys
 * <br>
 * parameter[:modifier]=[prefix]value
 * <br>
 * parameter[:modifier]=[prefix]system|code for tokens
 * 
 * <br>
 * where the modifiers are:<ul>
 * 		<li>missing
 * 		<li>exact
 * 		<li>contains
 * 		<li>text
 * 		<li>in
 * 		<li>below
 * 		<li>above
 * 		<li>not-in
 * 		<li>type
 * </ul>		
 * where the value types are:<ul>
 * 		<li>number (missing)
 * 		<li>date (missing)
 * 		<li>string (missing, exact, contains)
 * 		<li>token (missing, text, in, below, above, not-in)
 * 		<li>reference (missing, type)
 * 		<li>composite (missing)
 * 		<li>quantity (missing)
 * 		<li>uri (missing, below, above)
 * </ul>
 * prefixes are:<ul>
 * 		<li>eq - equal (default if no prefix is present)
 * 		<li>ne
 * 		<li>gt
 * 		<li>lt
 * 		<li>ge
 * 		<li>le
 * 		<li>sa
 * 		<li>eb
 * 		<li>ap
 * 
 * https://www.hl7.org/fhir/searchparameter-registry.html
 * @since 6.4
 */
public class SearchRequestParameters {
	
	private SearchRequestParameter id;
	
	private SearchRequestParameter summary;
	
	private SearchRequestParameter elements;
	
	private SearchRequestParameter lastUpdated;
	
	public SearchRequestParameters(Multimap<String, String> multiMap) {
		
		Set<String> parameterKeys = multiMap.keySet();
		
		//validate keys
		parameterKeys.stream()
			.map(k -> k.split(":")[0])
			.forEach(SearchRequestParameterKey::valueOf);
		
		parameterKeys.stream()
			.forEach(key -> {
				
				Collection<String> values = multiMap.get(key);
				
				//ID
				if (key.equals(SearchRequestParameterKey._id.name())) {
					validateSingleValue(values, SearchRequestParameterKey._id.name());
					id = SearchRequestParameter.idParameter(values.iterator().next());
				}
				
				if (key.equals(SearchRequestParameterKey._summary.name())) {
					validateSingleValue(values, SearchRequestParameterKey._summary.name());
					summary = SearchRequestParameter.builder()
						.name(SearchRequestParameterKey._summary)
						.type(SearchRequestParameterType.STRING)
						.value(values.stream().findFirst().get())
						.build();
				}
				
				if (key.equals(SearchRequestParameterKey._elements.name())) {
					
					List<String> parsedValues = getCommaSeparatedParameterValues(values);
					
					elements = SearchRequestParameter.builder()
						.name(SearchRequestParameterKey._elements)
						.type(SearchRequestParameterType.STRING)
						.values(parsedValues)
						.build();
				}
				
				if (key.startsWith(SearchRequestParameterKey._lastUpdated.name())) {
					validateSingleValue(values, SearchRequestParameterKey._lastUpdated.name());
					lastUpdated = SearchRequestParameter.lastUpdatedParameter(key, values.iterator().next());
				}
				
			});
		
		validateRequestParams();
	}
	
	private void validateSingleValue(Collection<String> values, String parameterName) {
		
		if (values.isEmpty()) {
			throw new BadRequestException("No %s parameter is submitted.", "SearchRequestParameter", parameterName);
		}
		
		if (values.size() != 1) {
			throw new BadRequestException("Too many %s parameter values are submitted.", "SearchRequestParameter", parameterName);
		}
	}

	public SearchRequestParameter getIdParameter() {
		return id;
	}
	
	public String getId() {
		if (id == null) {
			return null;
		} else {
			//already validated for empty collection
			return id.getValues().iterator().next();
					
		}
	}

	public SearchRequestParameter getSummaryParameter() {
		return summary;
	}
	
	public SummaryParameterValue getSummary() {
		if (summary == null) {
			return null;
		} else {
			//already validated for empty collection
			return SummaryParameterValue.fromRequestParameter(summary.getValues().iterator().next());
					
		}
	}

	public SearchRequestParameter getElementParameters() {
		return elements;
	}

	public Collection<String> getElements() {
		if (elements == null) {
			return null;
		} else {
			return elements.getValues();
		}
	}
	
	public SearchRequestParameter getLastUpdatedParameter() {
		return lastUpdated;
	}
	
	private List<String> getCommaSeparatedParameterValues(Collection<String> values) {
		
		List<String> requestedParameters = Lists.newArrayList();
		for (String element : values) {
			element = element.replaceAll(" ", "");
			if (element.contains(",")) {
				String requestedFields[] = element.split(",");
				requestedParameters.addAll(Lists.newArrayList(requestedFields));
			} else {
				if (!StringUtils.isEmpty(element)) {
					requestedParameters.add(element);
				} else {
					//thow an exception??
				}
			}
		}
		return requestedParameters;
	}
	
	/**
	 * Cross field request parameter key validation
	 * TODO: this is incorrect, see count paramater validation
	 */
	protected void validateRequestParams() {
		
		if (summary != null && elements !=null) {
			throw new IllegalArgumentException("Both '_summary' and '_elements' search parameters cannot be specified at the same time.");
		}
		
		if (summary != null) {
			try {
				if (getSummary() == SummaryParameterValue.COUNT) {
					throw new IllegalArgumentException("'Count' summary parameter is only allowed for search operations.");
				}
			} catch (RuntimeException re) {
				throw new IllegalArgumentException("Unknown '_summary' parameter value '" + summary + "'. Only 'true', 'false', 'text', 'data' are permitted.");
			}
		}
	}

}
