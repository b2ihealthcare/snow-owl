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
import java.math.BigDecimal;
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

	private final RelationshipValueType type;
	
	// Only one of the fields below should be non-null
	private final BigDecimal numericValue;
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
			// Remove prefix, parse number (treat it as a decimal value even if it has no fractional part)
			final String numericLiteral = literal.substring(1);
			return new RelationshipValue(new BigDecimal(numericLiteral));
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
	 * @param numericValue
	 * @param stringValue
	 * @return
	 */
	public static RelationshipValue fromTypeAndObjects(
		final RelationshipValueType valueType, 
		final BigDecimal numericValue, 
		final String stringValue) {
	
		if (valueType == null) {
			return null;
		}
	
		switch (valueType) {
			case INTEGER: //$FALL-THROUGH$
			case DECIMAL:
				return new RelationshipValue(valueType, numericValue, null);
			case STRING: 
				return new RelationshipValue(stringValue);
			default: 
				throw new IllegalArgumentException("Unexpected relationship value type: " + valueType);
		}
	}

	/**
	 * @param integerValue
	 */
	public RelationshipValue(final Integer integerValue) {
		this(RelationshipValueType.INTEGER, new BigDecimal(checkNotNull(integerValue, "Value may not be null.")), null);
	}

	/**
	 * @param decimalValue
	 */
	public RelationshipValue(final BigDecimal decimalValue) {
		this(RelationshipValueType.DECIMAL, checkNotNull(decimalValue, "Value may not be null."), null);
	}

	/**
	 * @param stringValue
	 */
	public RelationshipValue(final String stringValue) {
		this(RelationshipValueType.STRING, null, checkNotNull(stringValue, "Value may not be null."));
	}

	private RelationshipValue(final RelationshipValueType type, final BigDecimal numericValue, final String stringValue) {
		this.type = type;
		this.numericValue = numericValue;
		this.stringValue = stringValue;
	}

	/**
	 * @return
	 */
	public RelationshipValueType type() {
		return type;
	}

	/**
	 * @return
	 */
	public String toLiteral() {
		return map(
			i -> "#" + i,                                // add "#" prefix
			d -> "#" + d.toPlainString(),                // add "#" prefix, use format without an exponent field
			s -> "\"" + s.replace("\"", "\\\"") + "\""); // add leading and trailing quote, escape inner quotes
	}

	public Object toObject() {
		return map(i -> i, d -> d, s -> s);
	}

	public String toRawValue() {
		return map(
			i -> i.toString(),
			d -> d.toPlainString(),
			s -> s);
	}

	/**
	 * @param integerConsumer
	 * @return this instance, for method chaining
	 */
	public RelationshipValue ifInteger(final Consumer<Integer> integerConsumer) {
		if (RelationshipValueType.INTEGER.equals(type)) { integerConsumer.accept(numericIntValue()); }
		return this;
	}

	/**
	 * @param decimalConsumer
	 * @return this instance, for method chaining
	 */
	public RelationshipValue ifDecimal(final Consumer<BigDecimal> decimalConsumer) {
		if (RelationshipValueType.DECIMAL.equals(type)) { decimalConsumer.accept(numericValue); }
		return this;
	}

	/**
	 * @param stringConsumer
	 * @return this instance, for method chaining
	 */
	public RelationshipValue ifString(final Consumer<String> stringConsumer) {
		if (RelationshipValueType.STRING.equals(type)) { stringConsumer.accept(stringValue); }
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
		final Function<BigDecimal, T> decimalFn, 
		final Function<String, T> stringFn) {

		switch (type) {
			case INTEGER: return integerFn.apply(numericIntValue());
			case DECIMAL: return decimalFn.apply(numericValue);
			case STRING: return stringFn.apply(stringValue);
			default: throw new IllegalArgumentException("Unexpected relationship value type: " + type + ", can not map to value");
		}
	}

	private Integer numericIntValue() {
		return numericValue.intValueExact();
	}
	
	public boolean matches(final Operator operator, final RelationshipValue other) {
		checkNotNull(operator, "Comparison operator may not be null");
		if (!type().equals(other.type())) {
			// Automatic type conversion is not supported; #5 is not equal to #5.0
			return false;
		}
		
		final int comparison = map(
			i -> i.compareTo(other.numericIntValue()), 
			d -> d.compareTo(other.numericValue), 
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
		if (!type().equals(other.type())) {
			// Automatic type conversion is not supported; #5 is not equal to #5.0
			return false;
		}
		
		return map(
			i -> i.equals(other.numericIntValue()),
			d -> d.equals(other.numericValue),
			s -> s.equals(other.stringValue));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, numericValue, stringValue);
	}
	
	@Override
	public String toString() {
		return toLiteral();
	}
}
