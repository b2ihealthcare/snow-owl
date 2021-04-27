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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SearchRequestParameterKey;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SearchRequestParameterType;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameter.SummaryParameterValue;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameters.SearchablePropertyDefinition;
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
	
	private SearchRequestParameter name;
	
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
				
				//Summary
				if (key.equals(SearchRequestParameterKey._summary.name())) {
					validateSingleValue(values, SearchRequestParameterKey._summary.name());
					summary = SearchRequestParameter.builder()
						.name(SearchRequestParameterKey._summary)
						.type(SearchRequestParameterType.STRING)
						.value(values.stream().findFirst().get())
						.build();
				}
				
				//Elements
				if (key.equals(SearchRequestParameterKey._elements.name())) {
					
					List<String> parsedValues = getCommaSeparatedParameterValues(values);
					
					elements = SearchRequestParameter.builder()
						.name(SearchRequestParameterKey._elements)
						.type(SearchRequestParameterType.STRING)
						.values(parsedValues)
						.build();
				}
				
				//LastUpdated
				if (key.startsWith(SearchRequestParameterKey._lastUpdated.name())) {
					validateSingleValue(values, SearchRequestParameterKey._lastUpdated.name());
					lastUpdated = SearchRequestParameter.lastUpdatedParameter(key, values.iterator().next());
				}
				
				//Name
				//LastUpdated
				if (key.startsWith(SearchRequestParameterKey._name.name())) {
					validateSingleValue(values, SearchRequestParameterKey._name.name());
					name = SearchRequestParameter.name(key, values.iterator().next());
				}
				
				
			});
		
		validateRequestParams();
	}
	
	public class SearchablePropertyDefinition {

		private String name;
		private String type;

		public SearchablePropertyDefinition(String name, String type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}
		
		public String getType() {
			return type;
		}
	}
	
	public SearchRequestParameters(final Class<?> model, Multimap<String, String> multiMap) {
		
		List<Field> allSearchableFields = getAllSearchFields(model);
		
		Map<String, SearchablePropertyDefinition> searchablePropertyDefinitions = allSearchableFields.stream().map(f -> {
			SearchParameter[] declaredAnnotationsByType = f.getDeclaredAnnotationsByType(SearchParameter.class);
			if (declaredAnnotationsByType.length > 1) {
				throw new IllegalArgumentException(String.format("%s is present multiple times on %s.%s", SearchParameter.class.getSimpleName(), model.getSimpleName(), f.getName()));
			}
			
			
			SearchParameter simpleParameterAnnotation = declaredAnnotationsByType[0];
			
			if (!StringUtils.isEmpty(simpleParameterAnnotation.name())) {
				return new SearchablePropertyDefinition(simpleParameterAnnotation.name(), simpleParameterAnnotation.type());
			} else {
				return new SearchablePropertyDefinition(f.getName(), simpleParameterAnnotation.type());
			}
		}).collect(Collectors.toMap(k -> "_" + k.getName(), Function.identity()));
		
		//validate keys
		Set<String> parameterKeys = multiMap.keySet();
		
		
		for (String parameterKey : parameterKeys) {
			if (!searchablePropertyDefinitions.keySet().contains(parameterKey)) {
				throw new BadRequestException(String.format("Parameter %s is not supported. Supported parameters are %s.", parameterKey, Arrays.toString(searchablePropertyDefinitions.keySet().toArray())), "SearchRequestParameter");
			}
			Collection<String> values = multiMap.get(parameterKey);
			//TODO: multi/single value annotation
			validateSingleValue(values, parameterKey);
			
		}
		
		//Create the parameter
		for (String key : parameterKeys) {
			Collection<String> values = multiMap.get(key);
			
			SearchablePropertyDefinition searchablePropertyDefinition = searchablePropertyDefinitions.get(key);
			
			SearchRequestParameter searchRequestParameter = SearchRequestParameter.builder()
				.name(key)
				.type(searchablePropertyDefinition.getType())
				.values(values)
				.build();
			
			System.out.println(searchRequestParameter);
		}
			
			
//		allSearchableFieldsld field : allFields) {
//				
//				
//				System.out.println("field: " + field.getName());
//				
//				SearchParameter[] declaredAnnotationsByType = field.getDeclaredAnnotationsByType(SearchParameter.class);
//				
//				for (SearchParameter param : declaredAnnotationsByType) {
//					System.out.println("Annotation: " + param.name() + " : " + param.type());
//				}
//			}
//			
//		}
		
		parameterKeys.stream()
			.map(k -> k.split(":")[0])
			.forEach(System.out::println);
		
		
		
		for (Field field : allSearchableFields) {
			System.out.println("field: " + field.getName());
			
			SearchParameter[] declaredAnnotationsByType = field.getDeclaredAnnotationsByType(SearchParameter.class);
			
			for (SearchParameter param : declaredAnnotationsByType) {
				System.out.println("Annotation: " + param.name() + " : " + param.type());
			}
		}
		
		
//		Field[] declaredFields = model.getDeclaredFields();
//		for (Field field : declaredFields) {
//			
//			System.out.println(field.getName());
//			if (!field.isAnnotationPresent(SearchParameter.class)) continue;
//			
//			SearchParameter[] declaredAnnotationsByType = field.getDeclaredAnnotationsByType(SearchParameter.class);
//			
//			for (SearchParameter param : declaredAnnotationsByType) {
//				System.out.println(param.toString());
//			}
//			
//		}
		
	}
	
	private List<Field> getAllSearchFields(Class<?> clazz) {
		
		if (clazz == null) {
	        return Collections.emptyList();
	    }
		
	    List<Field> result = Lists.newArrayList(getAllSearchFields(clazz.getSuperclass()));
	    List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields())
	      .filter(f -> f.isAnnotationPresent(SearchParameter.class))
	      .collect(Collectors.toList());
	    result.addAll(filteredFields);
	    return result;
	}

	private void validateSingleValue(Collection<String> values, String parameterName) {
		
		if (values.isEmpty()) {
			throw new BadRequestException(String.format("No %s parameter is submitted.", parameterName), "SearchRequestParameter");
		}
		
		if (values.size() != 1) {
			throw new BadRequestException(String.format("Too many %s parameter values are submitted.", parameterName), "SearchRequestParameter");
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
	
	public SearchRequestParameter getNameParameter() {
		return name;
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
	 * TODO: this is incorrect, see count paramater validation (only allowed for search operations)
	 */
	protected void validateRequestParams() {
		
		if (summary != null && elements !=null) {
			throw new BadRequestException("Both '_summary' and '_elements' search parameters cannot be specified at the same time.", OperationOutcomeCode.MSG_PARAM_INVALID, "Request.count & Request.summary");
		}
		
		if (summary != null) {
			if (getSummary() == SummaryParameterValue.COUNT) {
				throw new BadRequestException("'Count' summary parameter is only allowed for search operations.", OperationOutcomeCode.MSG_PARAM_INVALID, "Request.count");
			}
		}
	}

}
