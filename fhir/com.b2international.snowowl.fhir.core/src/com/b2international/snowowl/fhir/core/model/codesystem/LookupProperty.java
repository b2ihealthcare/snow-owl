/*
 * Copyright 2018-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.codesystem;

import java.util.List;

import org.hl7.fhir.r5.model.*;

import com.b2international.commons.collections.Collections3;
import com.google.common.collect.ImmutableList;

import ca.uhn.fhir.model.primitive.*;

/**
 * Represents a FHIR Property in a lookup result object.
 * 
 * @since 6.4
 */
public final class LookupProperty {

	// Identifies the property returned (1..1)
	private CodeType code;

	// The value of the property returned (0..1)
	private Coding valueCoding;
	private BooleanType valueBoolean;
	private CodeType valueCode;
	private DateTimeType valueDateTime;
	private DecimalType valueDecimal;
	private IntegerType valueInteger;
	private String valueString;

	// Human-readable representation of the property value (e.g. display for a code) 0..1
	private String description;

	// If information from a supplement is included as a property (e.g. any additional property 
	// or property value), then this parameter must assert the URL of the supplement. (0..1)
	private UriDt source;

	// Nested properties (mainly used for SNOMED CT decomposition, for relationship Groups) (0..*)
	private List<LookupProperty> subProperties = ImmutableList.of();

	public LookupProperty(String code) {
		this(code, null);
	}

	public LookupProperty(String code, String description) {
		this(new CodeType(code), description);
	}
	
	public LookupProperty(CodeType code, String description) {
		this.code = code;
		this.description = description;
	}

	public CodeType getCode() {
		return code;
	}

	public void setCode(final CodeType code) {
		this.code = code;
	}

	public Coding getValueCoding() {
		return valueCoding;
	}

	public LookupProperty setValueCoding(final Coding valueCoding) {
		this.valueCoding = valueCoding;
		return this;
	}

	public BooleanType getValueBoolean() {
		return valueBoolean;
	}

	public LookupProperty setValueBoolean(final BooleanType valueBoolean) {
		this.valueBoolean = valueBoolean;
		return this;
	}

	public CodeType getValueCode() {
		return valueCode;
	}

	public LookupProperty setValueCode(final CodeType valueCode) {
		this.valueCode = valueCode;
		return this;
	}

	public DateTimeType getValueDateTime() {
		return valueDateTime;
	}

	public LookupProperty setValueDateTime(final DateTimeType valueDateTime) {
		this.valueDateTime = valueDateTime;
		return this;
	}

	public DecimalType getValueDecimal() {
		return valueDecimal;
	}

	public LookupProperty setValueDecimal(final DecimalType valueDecimal) {
		this.valueDecimal = valueDecimal;
		return this;
	}

	public IntegerType getValueInteger() {
		return valueInteger;
	}

	public LookupProperty setValueInteger(final IntegerType valueInteger) {
		this.valueInteger = valueInteger;
		return this;
	}

	public String getValueString() {
		return valueString;
	}

	public LookupProperty setValueString(final String valueString) {
		this.valueString = valueString;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public UriDt getSource() {
		return source;
	}

	public void setSource(final UriDt source) {
		this.source = source;
	}

	public List<LookupProperty> getSubProperties() {
		return subProperties;
	}

	public void setSubProperties(final Iterable<LookupProperty> subProperties) {
		this.subProperties = Collections3.toImmutableList(subProperties);
	}
}
