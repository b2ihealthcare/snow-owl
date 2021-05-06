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

import java.util.function.Consumer;
import java.util.function.Function;

import com.b2international.commons.exceptions.BadRequestException;

/**
 * Represents a "union class" holder for all supported relationship value types.
 * 
 * @since 7.17
 */
public final class RelationshipValue {

	// Only one of these fields should be set to non-null
	private final Integer integerValue;
	private final Double decimalValue;
	private final String stringValue;
	private final Boolean booleanValue;

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

		if (literal.equals("true")) {
			return new RelationshipValue(Boolean.TRUE);
		}

		if (literal.equals("false")) {
			return new RelationshipValue(Boolean.FALSE);
		}

		throw new BadRequestException("Couldn't convert literal <" + literal + "> to a concrete value.");
	}

	/**
	 * @param integerValue
	 */
	public RelationshipValue(final Integer integerValue) {
		this(checkNotNull(integerValue, "Value may not be null."), null, null, null);
	}

	/**
	 * @param doubleValue
	 */
	public RelationshipValue(final Double doubleValue) {
		this(null, checkNotNull(doubleValue, "Value may not be null."), null, null);
	}

	/**
	 * @param stringValue
	 */
	public RelationshipValue(final String stringValue) {
		this(null, null, checkNotNull(stringValue, "Value may not be null."), null);
	}

	/**
	 * @param booleanValue
	 */
	public RelationshipValue(final Boolean booleanValue) {
		this(null, null, null, checkNotNull(booleanValue, "Value may not be null."));
	}

	private RelationshipValue(final Integer integerValue, final Double decimalValue, final String stringValue, final Boolean booleanValue) {
		this.integerValue = integerValue;
		this.decimalValue = decimalValue;
		this.stringValue = stringValue;
		this.booleanValue = booleanValue;
	}

	/**
	 * @return
	 */
	public RelationshipValueType type() {
		return map(
			i -> RelationshipValueType.INTEGER,
			d -> RelationshipValueType.DECIMAL,
			s -> RelationshipValueType.STRING,
			b -> RelationshipValueType.BOOLEAN);
	}

	/**
	 * @return
	 */
	public String toLiteral() {
		return map(
			i -> "#" + i,                               // add "#" prefix
			d -> "#" + d,                               // add "#" prefix
			s -> "\"" + s.replace("\"", "\\\"") + "\"", // add leading and trailing quote, escape inner quotes
			b -> b ? "true" : "false");                 // use lowercase literals
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
	 * @param booleanConsumer
	 * @return this instance, for method chaining
	 */
	public RelationshipValue ifBoolean(final Consumer<Boolean> booleanConsumer) {
		if (booleanValue != null) { booleanConsumer.accept(booleanValue); }
		return this;
	}

	/**
	 * @param <T>
	 * @param integerFn
	 * @param decimalFn
	 * @param stringFn
	 * @param booleanFn
	 * @return
	 */
	public <T> T map(
		final Function<Integer, T> integerFn, 
		final Function<Double, T> decimalFn, 
		final Function<String, T> stringFn, 
		final Function<Boolean, T> booleanFn) {

		if (integerValue != null) { return integerFn.apply(integerValue); }
		if (decimalValue != null) { return decimalFn.apply(decimalValue); }
		if (stringValue != null) { return stringFn.apply(stringValue); }
		if (booleanValue != null) { return booleanFn.apply(booleanValue); }

		throw new IllegalStateException("All stored values were null, can not map to value");
	}
}
