/*
 * Copyright 2024 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.b2international.commons.exceptions.BadRequestException;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @since 9.4
 */
public class PreferHandlingInterceptor implements HandlerInterceptor {

	public static final String PREFER_HEADER = "Prefer";
	public static final String PREFER_HANDLING_STRICT = "handling=strict";
	public static final String PREFER_HANDLING_LENIENT = "handling=lenient";

	@Override
	public boolean preHandle(
		final HttpServletRequest request, 
		final HttpServletResponse response, 
		final Object handler
	) throws Exception {
		
		if (!(handler instanceof HandlerMethod handlerMethod)) {
			// We can only check requests where a controller method was bound
			return true;
		}
		
		final Set<String> preferValues = Sets.newHashSet(request.getHeaders(PREFER_HEADER).asIterator());
		final boolean strict = preferValues.contains(PREFER_HANDLING_STRICT) && !preferValues.contains(PREFER_HANDLING_LENIENT);
		if (!strict) {
			// Skip if the client did not ask for strict handling or it specifies both handling options
			return true;
		}
		
		// XXX: Query parameters and x-www-form-urlencoded fields are both considered parameters
		final SortedSet<String> actualNames = Sets.newTreeSet();
		Iterators.addAll(actualNames, request.getParameterNames().asIterator());
		if (actualNames.isEmpty()) {
			// No request parameters were supplied
			return true;
		}
		
		final SortedSet<String> allowedNames = Sets.newTreeSet();
		
		for (final MethodParameter param : handlerMethod.getMethodParameters()) {
			if (param.hasParameterAnnotation(RequestHeader.class)
				|| param.hasParameterAnnotation(PathVariable.class)
				|| param.hasParameterAnnotation(RequestBody.class)
			) {
				// Ignore parameters with an annotation that indicates that it is not a query parameter
				continue;
			}
			
			final RequestParam annotation = param.getParameterAnnotation(RequestParam.class);
			if (annotation != null) {
				
				if (Strings.isNullOrEmpty(annotation.name()) 
					&& Strings.isNullOrEmpty(annotation.value())
					&& !annotation.required()
					&& Map.class.isAssignableFrom(param.getParameterType())
				) {
					// This method has a catch-all Map argument that does not specify any name inside the annotation
					return true;
				}
				
				// Extract the query parameter name from the annotation, falling back to the parameter's name (if available)
				Stream.of(annotation.name(), annotation.value(), param.getParameterName())
					.filter(s -> !Strings.isNullOrEmpty(s))
					.findFirst()
					.ifPresent(allowedNames::add);

				continue;
			}
			
			/*
			 * Parameters without an annotation should be parameter objects; get its
			 * property descriptors and include these in the allowed parameter name set.
			 */
			final PropertyDescriptor[] properties = BeanUtils.getPropertyDescriptors(param.getParameterType());
			for (final PropertyDescriptor property : properties) {
				if (property.getWriteMethod() != null) {
					// Only consider writable properties
					final Class<?> propertyType = property.getPropertyType();
					if (Collection.class.isAssignableFrom(propertyType) 
						|| Map.class.isAssignableFrom(propertyType)
						|| BeanUtils.isSimpleProperty(propertyType)) {
						// Only consider "sufficiently simple" values
						allowedNames.add(property.getName());
					}
				}
			}
		}
		
		actualNames.removeAll(allowedNames);
		if (!actualNames.isEmpty()) {
			throw new BadRequestException("Unknown or unsupported parameters found in the request: %s. Accepted parameters are: %s.", actualNames, allowedNames);
		}
		
		return true;
	}
}
