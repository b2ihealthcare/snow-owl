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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;

import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.search.FhirParameter.PrefixedValue;
import com.google.common.collect.Sets;

/**
 * Superclass to represent the definition of a valid and supported FHIR URI request parameter.
 * 
 * @since 7.14
 */
public class FhirUriParameterDefinition {
	
	protected static final String SEARCH_REQUEST_PARAMETER_MARKER = "SearchRequestParameter"; //$NON-NLS-N$
	
	public enum SearchRequestParameterModifier {
		missing,
		exact,
		contains,
		text,
		in,
		below,
		above,
		not_in,
		type;
		
		public String getParameterString() {
			return name().replaceAll("_", "-");
		}
		
		public static SearchRequestParameterModifier fromRequestParameter(String parameterModifier) {
			return valueOf(parameterModifier.replaceAll("-", "_").toLowerCase());
		}
		
		public static boolean hasValue(String parameterModifier) {
			return Arrays.stream(values())
					.anyMatch(v -> parameterModifier.replaceAll("-", "_").toLowerCase().equals(v.name()));
		}
	}
	
	/**
	 * Request parameter types
	 * 	<li>number (missing)
	 * 	<li>date (missing)
	 * 	<li>string (missing, exact, contains)
	 * 	<li>token (missing, text, in, below, above, not-in)
	 * 	<li>reference (missing, type)
	 * 	<li>composite (missing)
	 * 	<li>quantity (missing)
	 * 	<li>uri (missing, below, above) 
	 */
	public enum FhirRequestParameterType {
		
		NUMBER(Sets.newHashSet(SearchRequestParameterModifier.missing)),
		DATE(Sets.newHashSet(SearchRequestParameterModifier.missing)),
		DATETIME(Sets.newHashSet(SearchRequestParameterModifier.missing)),
		STRING(Sets.newHashSet(SearchRequestParameterModifier.missing, 
				SearchRequestParameterModifier.exact, 
				SearchRequestParameterModifier.contains)),
		TOKEN(Sets.newHashSet(SearchRequestParameterModifier.missing,
				SearchRequestParameterModifier.text,
				SearchRequestParameterModifier.in,
				SearchRequestParameterModifier.below,
				SearchRequestParameterModifier.above,
				SearchRequestParameterModifier.not_in)),
		REFERENCE(Sets.newHashSet(SearchRequestParameterModifier.missing,
				SearchRequestParameterModifier.type)),
		COMPOSITE(Sets.newHashSet(SearchRequestParameterModifier.missing)),
		QUANTITY(Sets.newHashSet(SearchRequestParameterModifier.missing)),
		URI(Sets.newHashSet(SearchRequestParameterModifier.missing,
				SearchRequestParameterModifier.below,
				SearchRequestParameterModifier.above));
		
		private HashSet<SearchRequestParameterModifier> availableModifiers;

		FhirRequestParameterType(HashSet<SearchRequestParameterModifier> availableModifiers) {
			this.availableModifiers = availableModifiers;
		}
		
		public HashSet<SearchRequestParameterModifier> getAvailableModifiers() {
			return availableModifiers;
		}

		public static FhirRequestParameterType fromRequestParameter(String requestParam) {
			return valueOf(requestParam.toUpperCase());
		}
	}
	
	private static String[] DATETIME_PATTERNS = new String[] {
			"yyyy", 
			"yyyy-MM", 
			"yyyy-MM-dd",
			"yyyy-MM-dd'T'HH:mm:ss",
			"yyyy-MM-dd'T'HH:mm:ssZ",
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ",
			"yyyy-MM-dd'T'HH:mm:ssXXX", 
			"yyyy-MM-dd'T'HH:mm:ss.SSSSXXX",
			"yyyy-MM-dd'T'HH:mm:ss.SSSX"};
	
	protected String name;
	
	protected FhirRequestParameterType type;
	
	protected boolean isMultipleValuesSupported;
	
	protected Set<String> supportedValues = Collections.emptySet();
	
	public FhirUriParameterDefinition(final String name, final String type, final boolean isMultipleValuesSupported) {
		this.name = name;
		this.type = FhirRequestParameterType.fromRequestParameter(type);
		this.isMultipleValuesSupported = isMultipleValuesSupported;
	}

	public FhirUriParameterDefinition(final String name, final FhirRequestParameterType type, final boolean isMultipleValuesSupported, Set<String> supportedValues) {
		this.name = name;
		this.type = type;
		this.isMultipleValuesSupported = isMultipleValuesSupported;
		this.supportedValues = supportedValues;
	}
	
	public String getName() {
		return name;
	}

	public FhirRequestParameterType getType() {
		return type;
	}
	
	public boolean isMultipleValuesSupported() {
		return isMultipleValuesSupported;
	}
	
	/**
	 * Returns the supported values for this parameter
	 * @return
	 */
	public Set<String> getSupportedValues() {
		return supportedValues;
	}
	
	public void validateModifier(SearchRequestParameterModifier modifier) {
		if (modifier == null) return;
		
		if (!Sets.newHashSet(SearchRequestParameterModifier.values()).contains(modifier)) {
			throw FhirException.createFhirError(String.format("Uknown modifier '%s' for parameter '%s'.", modifier, name), OperationOutcomeCode.MSG_PARAM_INVALID);
		}
		
		if (!type.availableModifiers.contains(modifier)) {
			throw FhirException.createFhirError(String.format("Invalid modifier '%s' for parameter [%s:%s].", modifier, name, type.name()), OperationOutcomeCode.MSG_PARAM_INVALID);
		}
	}
	
	public void validateValues(final Collection<PrefixedValue> prefixedValues) {
		
		if (!isMultipleValuesSupported() && prefixedValues.size() > 1) {
			String values = Arrays.toString(prefixedValues.stream().map(PrefixedValue::getValue).collect(Collectors.toSet()).toArray());
			throw FhirException.createFhirError(String.format("Too many filter parameter values %s are submitted for parameter '%s'.", values, getName()), OperationOutcomeCode.MSG_PARAM_INVALID, SEARCH_REQUEST_PARAMETER_MARKER);
		}
		
		Set<String> supportedValues = getSupportedValues();
		Set<String> values = prefixedValues.stream().map(PrefixedValue::getValue).collect(Collectors.toSet());
		
		Set<String> uppercaseValues = values.stream()
				.map(String::toUpperCase)
				.collect(Collectors.toSet());
		
		if (!supportedValues.isEmpty() && !supportedValues.containsAll(uppercaseValues)) {
			throw FhirException.createFhirError(String.format("Filter parameter value %s is not supported. Supported parameter values are %s.", Arrays.toString(values.toArray()), Arrays.toString(supportedValues.toArray())), 
				OperationOutcomeCode.MSG_PARAM_UNKNOWN, "SEARCH_REQUEST_PARAMETER_MARKER");
		}
		
		for (String value : values) {
			switch (type) {
			case NUMBER:
				try {
					Double.parseDouble(value);
				} catch (NullPointerException | NumberFormatException e) {
					throw FhirException.createFhirError(String.format("Invalid %s type parameter value '%s' are submitted for parameter '%s'.", type, value, name), OperationOutcomeCode.MSG_PARAM_INVALID, SEARCH_REQUEST_PARAMETER_MARKER);
				}
				break;
			case DATE:
				try {
					LocalDate.parse(value);
				} catch (NullPointerException | DateTimeParseException e) {
					throw FhirException.createFhirError(String.format("Invalid %s type parameter value '%s' are submitted for parameter '%s'.", type, value, name), OperationOutcomeCode.MSG_PARAM_INVALID, SEARCH_REQUEST_PARAMETER_MARKER);
				}
				break;
			case DATETIME:
				try {
					DateUtils.parseDate(value, DATETIME_PATTERNS);
				} catch (NullPointerException | ParseException e) {
					throw FhirException.createFhirError(String.format("Invalid %s type parameter value '%s' are submitted for parameter '%s'.", type, value, name), OperationOutcomeCode.MSG_PARAM_INVALID, SEARCH_REQUEST_PARAMETER_MARKER);
				}
				break;
			case URI:
				try {
					new URI(value);
				} catch (URISyntaxException e) {
					throw FhirException.createFhirError(String.format("Invalid %s type parameter value '%s' are submitted for parameter '%s'.", type, value, name), OperationOutcomeCode.MSG_PARAM_INVALID, SEARCH_REQUEST_PARAMETER_MARKER);
				}
			default:
				break;
			}
		}
		
	}

	@Override
	public String toString() {
		String cardinalityString = "";
		if (isMultipleValuesSupported) {
			cardinalityString = "*";
		}
		return String.format("%s : %s%s %s = %s", name, type.name(), cardinalityString, Arrays.toString(type.getAvailableModifiers().toArray()),
				Arrays.toString(supportedValues.toArray()));
	}


}
