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
package com.b2international.snowowl.fhir.core.search;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.search.FhirUriFilterParameterDefinition.FhirFilterParameterKey;
import com.b2international.snowowl.fhir.core.search.FhirUriSearchParameterDefinition.FhirCommonSearchKey;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * FHIR URI request parameter manager.
 * @since 7.14
 */
public class FhirUriParameterManager {
	
	private static final String SEARCH_REQUEST_PARAMETER_MARKER = "SearchRequestParameter"; //$NON-NLS-N$

	private Map<String, FhirUriFilterParameterDefinition> supportedFilterParameters = Maps.newHashMap();
	
	private Map<String, FhirUriSearchParameterDefinition> supportedSearchParameters = Maps.newHashMap();

	/**
	 * Creates a new manager with the supported definitions loaded from the model class passed in.
	 * @param model
	 * @return
	 */
	public static FhirUriParameterManager createFor(final Class<?> model) {
		
		FhirUriParameterManager definitions = new FhirUriParameterManager();
		
		List<Filterable> filterableAnnotations = collectFilterParameters(model);
		
		for (Filterable filterableAnnotation : filterableAnnotations) {
			FhirUriFilterParameterDefinition filterResultUriParameter = new FhirUriFilterParameterDefinition(filterableAnnotation.filter(), filterableAnnotation.supportsMultipleValues(), Sets.newHashSet(filterableAnnotation.values()));
			definitions.supportedFilterParameters.put(filterableAnnotation.filter(), filterResultUriParameter);
		}
		
		List<Field> searchableFields = collectSearchableFields(model);
		definitions.supportedSearchParameters.putAll(createSearchableParameters(searchableFields));
		return definitions;
	}
	
	public Pair<Set<FhirFilterParameter>, Set<FhirSearchParameter>> processParameters(Multimap<String, String> multiMap) {
	
		 Set<FhirParameter> fhirParameters = multiMap.keySet().stream()
				.map(k -> new RawRequestParameter(k, multiMap.get(k)))
				.map(p -> processParameter(p))
				.collect(Collectors.toSet());
		
		Set<FhirFilterParameter> filterParameters = fhirParameters.stream()
				.filter(FhirFilterParameter.class::isInstance)
				.map(FhirFilterParameter.class::cast)
				.collect(Collectors.toSet());
		
		//cross-parameter validation
		validateFilterParameters(filterParameters);
		
		Set<FhirSearchParameter> searchParameters = fhirParameters.stream()
				.filter(FhirSearchParameter.class::isInstance)
				.map(FhirSearchParameter.class::cast)
				.collect(Collectors.toSet());
		
		//cross-parameter validation
		validateSearchParameters(searchParameters);
				
		return Pair.of(filterParameters, searchParameters);
	}
	
	private FhirParameter processParameter(RawRequestParameter fhirParameter) {
		
		String parameterName = fhirParameter.getName();
		if (supportedSearchParameters.containsKey(parameterName)) {
			FhirUriSearchParameterDefinition supportedSearchParameter = supportedSearchParameters.get(parameterName);
			
			return FhirSearchParameter.builder()
					.parameterDefinition(supportedSearchParameter)
					.modifier(fhirParameter.getModifier())
					.values(fhirParameter.getValues())
					.build();
			
		} else if (supportedFilterParameters.containsKey(parameterName)) {
			FhirUriFilterParameterDefinition supportedFilterParameter = supportedFilterParameters.get(parameterName);
			
			return FhirFilterParameter.builder()
					.parameterDefinition(supportedFilterParameter)
					.values(fhirParameter.getValues())
					.build();
			
		} else if (FhirCommonSearchKey.hasParameter(parameterName)) {
			throw FhirException.createFhirError(String.format("Search parameter %s is not supported. Supported search parameters are %s.", parameterName, Arrays.toString(supportedSearchParameters.keySet().toArray())), OperationOutcomeCode.MSG_PARAM_UNKNOWN, SEARCH_REQUEST_PARAMETER_MARKER);
		
		} else if (FhirFilterParameterKey.hasParameter(parameterName)) {
			throw FhirException.createFhirError(String.format("Filter parameter %s is not supported. Supported filter parameters are %s.", parameterName, Arrays.toString(supportedFilterParameters.keySet().toArray())), OperationOutcomeCode.MSG_PARAM_UNKNOWN, SEARCH_REQUEST_PARAMETER_MARKER);
		
		} else {
			SetView<String> union = Sets.union(supportedSearchParameters.keySet(), supportedFilterParameters.keySet());
			throw FhirException.createFhirError(String.format("URI parameter %s is unknown. Supported parameters are %s.", parameterName, Arrays.toString(union.toArray())), OperationOutcomeCode.MSG_PARAM_UNKNOWN, SEARCH_REQUEST_PARAMETER_MARKER);
		
		}
	}
	
	public Map<String, FhirUriFilterParameterDefinition> getSupportedFilterParameters() {
		return supportedFilterParameters;
	}
	
	public Map<String, FhirUriSearchParameterDefinition> getSupportedSearchParameters() {
		return supportedSearchParameters;
	}
	
	private void validateFilterParameters(Set<FhirFilterParameter> filterParameters) {
		
		filterParameters.forEach(p -> {
			if (!p.getParameterDefinition().isMultipleValuesSupported() && p.getValues().size() > 1) {
				throw FhirException.createFhirError(String.format("Too many filter parameter values %s are submitted for parameter %s.", Arrays.toString(p.getValues().toArray()), p.getName()), OperationOutcomeCode.MSG_PARAM_INVALID, SEARCH_REQUEST_PARAMETER_MARKER);
			}
		});
		
		
		if (filterParameters.stream().anyMatch(p -> p.getName().equals(FhirFilterParameterKey._summary.name())) && 
			filterParameters.stream().anyMatch(p -> p.getName().equals(FhirFilterParameterKey._elements.name()))) {
			
			throw FhirException.createFhirError(String.format("Both '_summary' and '_elements' search parameters cannot be specified at the same time."), OperationOutcomeCode.MSG_PARAM_INVALID, SEARCH_REQUEST_PARAMETER_MARKER);
		}
	}
	
	private void validateSearchParameters(Set<FhirSearchParameter> searchParameters) {
		
		searchParameters.forEach(p -> {
			if (!p.getParameterDefinition().isMultipleValuesSupported() && p.getValues().size() > 1) {
				throw FhirException.createFhirError(String.format("Too many search parameter values %s are submitted for parameter %s.", Arrays.toString(p.getValues().toArray()), p.getName()), OperationOutcomeCode.MSG_PARAM_INVALID, SEARCH_REQUEST_PARAMETER_MARKER);
			}
		});
	}
	
	private static List<Filterable> collectFilterParameters(Class<?> model) {
		
		if (model == null) {
	        return Collections.emptyList();
	    }
		
		List<Filterable> result = Lists.newArrayList(collectFilterParameters(model.getSuperclass()));
		
		if (model.isAnnotationPresent(Filterables.class)) {
			Filterable[] filterableAnnotations = model.getAnnotationsByType(Filterable.class);
			for (Filterable filterable : filterableAnnotations) {
				result.add(filterable);
			}
		}
		return result;
	}
	
	/*
	 * Recursively collect the annotated fields from the class hierarchy.
	 * @see @SearchParameter
	 */
	private static List<Field> collectSearchableFields(Class<?> clazz) {
		
		if (clazz == null) {
	        return Collections.emptyList();
	    }

		//search in the hieararchy
	    List<Field> results = Lists.newArrayList(collectSearchableFields(clazz.getSuperclass()));
	    
	    Field[] declaredFields = clazz.getDeclaredFields();
	    List<Field> filteredFields = Arrays.stream(declaredFields)
	    		.filter(f -> f.isAnnotationPresent(Searchable.class))
	    		.collect(Collectors.toList());
	    results.addAll(filteredFields);
	   
	    //search the graph - only direct references
	    List<Class<?>> referencedClasses = Arrays.stream(declaredFields)
	    		.filter(f -> clazz.getPackageName().startsWith(f.getType().getPackageName()))
	    		.map(f -> f.getType())
	    		.collect(Collectors.toList());
	    
	    for (Class<?> referencedClass : referencedClasses) {
	    	List<Field> referencedClassFields = Lists.newArrayList(collectSearchableFields(referencedClass));
	    	results.addAll(referencedClassFields);
		}
	    
	    return results;
	}
	
	private static Map<String, FhirUriSearchParameterDefinition> createSearchableParameters(final List<Field> allSearchableFields) {
		return allSearchableFields.stream().map(f -> {
			
			Searchable[] declaredAnnotationsByType = f.getDeclaredAnnotationsByType(Searchable.class);
			
			if (declaredAnnotationsByType.length > 1) {
				throw new IllegalArgumentException(String.format("%s is present multiple times on %s.%s", Searchable.class.getSimpleName(), f.getDeclaringClass().getSimpleName(), f.getName()));
			}
			
			Searchable simpleParameterAnnotation = declaredAnnotationsByType[0];
			Set<String> supportedModifiers = Sets.newHashSet(simpleParameterAnnotation.modifiers());
			
			if (!StringUtils.isEmpty(simpleParameterAnnotation.name())) {
				return new FhirUriSearchParameterDefinition(simpleParameterAnnotation.name(), simpleParameterAnnotation.type(), supportedModifiers, simpleParameterAnnotation.supportsMultipleValues());
			} else {
				return new FhirUriSearchParameterDefinition("_" + f.getName(), simpleParameterAnnotation.type(), supportedModifiers, simpleParameterAnnotation.supportsMultipleValues()); 
			}
		}).collect(Collectors.toMap(k -> k.getName(), Function.identity()));
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder("Supported FRIR URI parameters:\n");
		sb.append("  Filter parameters:\n");
		
		Set<String> filterKeys = supportedFilterParameters.keySet();
		
		for (String filterKey : filterKeys) {
			FhirUriFilterParameterDefinition supportedFilterParameter = supportedFilterParameters.get(filterKey);
			sb.append("\t\t");
			sb.append(supportedFilterParameter);
			sb.append("\n");
		}
		
		sb.append("  Search parameters:\n");
		
		Set<String> searchKeys = supportedSearchParameters.keySet();
		
		for (String searchKey : searchKeys) {
			FhirUriSearchParameterDefinition searchParameter = supportedSearchParameters.get(searchKey);
			sb.append("\t\t");
			sb.append(searchParameter);
			sb.append("\n");
		}
		
		return sb.toString();
	}

}
