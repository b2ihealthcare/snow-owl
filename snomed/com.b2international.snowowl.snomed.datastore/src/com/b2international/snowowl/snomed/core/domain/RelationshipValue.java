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
package com.b2international.snowowl.snomed.core.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.request.SearchResourceRequest.Operator;

/**
 * Represents a "union class" holder for all supported relationship value types.
 * 
 * @since 7.17
 */
public final class RelationshipValue implements Serializable {

	// Only one of these fields should be set to non-null
	private final Integer integerValue;
	private final Double decimalValue;
	private final String stringValue;

	/**
	 * @param literal
	 * @return
	 */
	public static final RelationshipValue fromLiteral(final String literal) {
		if (literal == null) { 
			return null; 
		}

		if (literal.startsWith("#")) {
			// Remove prefix, parse number
			final String numericLiteral = literal.substring(1);
			if (numericLiteral.contains(".")) {
				return new RelationshipValue(Double.valueOf(numericLiteral));
			} else {
				return new RelationshipValue(Integer.valueOf(numericLiteral));
			}
		}

		if (literal.startsWith("\"") && literal.endsWith("\"")) {
			// Remove prefix and suffix, unescape quotes
			final String quotedLiteral = literal.substring(1, literal.length() - 1);
			final String unescapedLiteral = quotedLiteral.replace("\\\"", "\"");
			return new RelationshipValue(unescapedLiteral);
		}

		throw new BadRequestException("Couldn't convert literal <" + literal + "> to a concrete value.");
	}

	/**
	 * @param valueType
	 * @param integerValue
	 * @param decimalValue
	 * @param stringValue
	 * @return
	 */
	public static RelationshipValue fromTypeAndObjects(
		final RelationshipValueType valueType, 
		final Integer integerValue, 
		final Double decimalValue, 
		final String stringValue) {
	
		if (valueType == null) {
			if (integerValue != null || decimalValue != null || stringValue != null) {
				throw new IllegalArgumentException("Value field has been loaded without a valueType field, unable to determine actual relationship value.");
			}
			
			return null;
		}
	
		switch (valueType) {
			case INTEGER: return new RelationshipValue(integerValue);
			case DECIMAL: return new RelationshipValue(decimalValue);
			case STRING: return new RelationshipValue(stringValue);
			default: throw new IllegalArgumentException("Unexpected relationship value type: " + valueType);
		}
	}

	/**
	 * @param integerValue
	 */
	public RelationshipValue(final Integer integerValue) {
		this(checkNotNull(integerValue, "Value may not be null."), null, null);
	}

	/**
	 * @param doubleValue
	 */
	public RelationshipValue(final Double doubleValue) {
		this(null, checkNotNull(doubleValue, "Value may not be null."), null);
	}

	/**
	 * @param stringValue
	 */
	public RelationshipValue(final String stringValue) {
		this(null, null, checkNotNull(stringValue, "Value may not be null."));
	}

	private RelationshipValue(final Integer integerValue, final Double decimalValue, final String stringValue) {
		this.integerValue = integerValue;
		this.decimalValue = decimalValue;
		this.stringValue = stringValue;
	}

	/**
	 * @return
	 */
	public RelationshipValueType type() {
		return map(
			i -> RelationshipValueType.INTEGER,
			d -> RelationshipValueType.DECIMAL,
			s -> RelationshipValueType.STRING);
	}

	/**
	 * @return
	 */
	public String toLiteral() {
		return map(
			i -> "#" + i,                                // add "#" prefix
			d -> "#" + d,                                // add "#" prefix
			s -> "\"" + s.replace("\"", "\\\"") + "\""); // add leading and trailing quote, escape inner quotes
	}

	public Object toObject() {
		return map(i -> i, d -> d, s -> s);
	}

	/**
	 * @param integerConsumer
	 * @return this instance, for method chaining
	 */
	public RelationshipValue ifInteger(final Consumer<Integer> integerConsumer) {
		if (integerValue != null) { integerConsumer.accept(integerValue); }
		return this;
	}

	/**
	 * @param decimalConsumer
	 * @return this instance, for method chaining
	 */
	public RelationshipValue ifDecimal(final Consumer<Double> decimalConsumer) {
		if (decimalValue != null) { decimalConsumer.accept(decimalValue); }
		return this;
	}

	/**
	 * @param stringConsumer
	 * @return this instance, for method chaining
	 */
	public RelationshipValue ifString(final Consumer<String> stringConsumer) {
		if (stringValue != null) { stringConsumer.accept(stringValue); }
		return this;
	}

	/**
	 * @param <T>
	 * @param integerFn
	 * @param decimalFn
	 * @param stringFn
	 * @return
	 */
	public <T> T map(
		final Function<Integer, T> integerFn, 
		final Function<Double, T> decimalFn, 
		final Function<String, T> stringFn) {

		if (integerValue != null) { return integerFn.apply(integerValue); }
		if (decimalValue != null) { return decimalFn.apply(decimalValue); }
		if (stringValue != null) { return stringFn.apply(stringValue); }

		throw new IllegalStateException("All stored values were null, can not map to value");
	}
	
	public boolean matches(final Operator operator, final RelationshipValue other) {
		checkNotNull(operator, "Comparison operator may not be null");
		if (!type().equals(other.type())) {
			// Automatic type conversion is not supported; #5 is not equal to #5.0
			return false;
		}
		
		final int comparison = map(
			i -> i.compareTo(other.integerValue), 
			d -> d.compareTo(other.decimalValue), 
			s -> s.compareTo(other.stringValue));
		
		switch (operator) {
			case EQUALS: return (comparison == 0); 
			case GREATER_THAN: return (comparison > 0);
			case GREATER_THAN_EQUALS: return (comparison >= 0);
			case LESS_THAN: return (comparison < 0);
			case LESS_THAN_EQUALS: return (comparison <= 0);
			case NOT_EQUALS: return (comparison != 0);
			default: throw new IllegalStateException("Unexpected operator '" + operator +  "'.");
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof RelationshipValue)) { return false; }
		
		final RelationshipValue other = (RelationshipValue) obj;
		return map(
			i -> i.equals(other.integerValue),
			d -> d.equals(other.decimalValue),
			s -> s.equals(other.stringValue));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(integerValue, decimalValue, stringValue);
	}
	
	@Override
	public String toString() {
		return toLiteral();
	}
}
