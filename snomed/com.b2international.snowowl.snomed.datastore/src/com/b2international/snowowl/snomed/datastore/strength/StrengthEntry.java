/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.strength;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Lightweight representation of a strength. Possible strength types are:
 * 
 * <pre>
 * NOT_APPLICABLE:
 * 	representing a not expressible strength
 * 
 * SIMPLE:
 * 	Consists of a numerator value and a numerator unit. 
 * 	E.g.: 20 mg
 * 
 * SIMPLE_RANGE:
 * 	Consists of a numerator minimum value, numerator maximum value and a numerator unit.
 * 	The existence of both values are not mandatory, but at least one must be specified.
 * 	E.g.: 1 - 20 mL, minimum 1 mL, maximum 20 mL
 * 
 * RATIO:
 * 	Consists of a numerator value, a numerator unit, a delimiter, a denominator value and a denominator unit.
 * 	The existence of both units are not mandatory in case the delimiter is {@link StrengthEntryDelimiter.COLON}.
 * 	E.g.: 10 mL / 5 mg, 1 : 2000
 * 
 * RATIO_RANGE:
 * 	Consists of a numerator minimum value, a numerator maximum value, a numerator unit, a delimiter, a denominator value and a denominator unit.
 * 	The existence of both numerator values are not mandatory, but at least one must be specified.
 * 	E.g. 1 - 20 mL / 5 mg, minimum 20 mL / 5 mg, maximum 1 mL / 5 mg
 * </pre>
 * 
 */
public class StrengthEntry implements Serializable {

	private static final long serialVersionUID = -5215627761604159758L;

	private final String name;
	private final StrengthEntryType type;

	private final BigDecimal numeratorValue;
	private final BigDecimal numeratorMinValue;
	private final BigDecimal numeratorMaxValue;
	private final long numeratorUnit;

	private final StrengthEntryDelimiter delimiter;

	private final BigDecimal denominatorValue;
	private final long denominatorUnit;

	/**
	 * NOT_APPLICABLE
	 * 
	 * @param name
	 *            - the name of the strength in lower camel caseformat.
	 */
	public StrengthEntry(final String name) {

		this.type = StrengthEntryType.NOT_APPLICABLE;
		this.name = checkNotNull(name, "name");

		this.numeratorValue = null;
		this.numeratorMinValue = null;
		this.numeratorMaxValue = null;
		this.numeratorUnit = -1L;

		this.denominatorValue = null;
		this.denominatorUnit = -1L;
		this.delimiter = null;
	}

	/**
	 * SIMPLE
	 * 
	 * @param name
	 *            - the name of the strength in lower camel caseformat.
	 * @param numeratorValue
	 *            - the numerator value
	 * @param numeratorUnit
	 *            - concept ID representing the numerator unit.
	 */
	public StrengthEntry(final String name, final BigDecimal numeratorValue, final long numeratorUnit) {
		checkArgument(numeratorUnit > 0L, "Numerator unit must be set.");

		this.type = StrengthEntryType.SIMPLE;
		this.name = checkNotNull(name, "name");

		this.numeratorValue = checkNotNull(numeratorValue, "numeratorValue");
		this.numeratorMinValue = null;
		this.numeratorMaxValue = null;
		this.numeratorUnit = numeratorUnit;

		this.denominatorValue = null;
		this.denominatorUnit = -1L;
		this.delimiter = null;
	}

	/**
	 * SIMPLE_RANGE
	 * 
	 * @param name
	 *            - the name of the strength in lower camel caseformat.
	 * @param numeratorMinValue
	 *            - the numerator minimum value
	 * @param numeratorMaxValue
	 *            - the numerator maximum value
	 * @param numeratorUnit
	 *            - concept ID representing the numerator unit.
	 */
	public StrengthEntry(final String name, final BigDecimal numeratorMinValue, final BigDecimal numeratorMaxValue, final long numeratorUnit) {
		checkArgument(numeratorUnit > 0L, "Numerator unit must be set.");
		checkArgument((numeratorMinValue != null || numeratorMaxValue != null), "Either numerator minimum or maximum value must be set.");

		this.type = StrengthEntryType.SIMPLE_RANGE;
		this.name = checkNotNull(name, "name");

		this.numeratorValue = null;
		this.numeratorMinValue = numeratorMinValue;
		this.numeratorMaxValue = numeratorMaxValue;
		this.numeratorUnit = numeratorUnit;

		this.denominatorValue = null;
		this.denominatorUnit = -1L;
		this.delimiter = null;
	}

	/**
	 * RATIO
	 *
	 * If the delimiter is {@link StrengthEntryDelimiter.COLON} both numerator and denominator units are not mandatory.
	 * 
	 * @param name
	 *            - the name of the strength in lower camel caseformat.
	 * @param numeratorValue
	 *            - the numerator value
	 * @param numeratorUnit
	 *            - concept ID representing the numerator unit.
	 * @param delimiter
	 *            - the delimiter
	 * @param denominatorValue
	 *            - the denominator value
	 * @param denominatorUnit
	 *            - concept ID representing the denominator unit.
	 */
	public StrengthEntry(final String name, final BigDecimal numeratorValue, final long numeratorUnit, final StrengthEntryDelimiter delimiter,
			final BigDecimal denominatorValue, final long denominatorUnit) {

		this.type = StrengthEntryType.RATIO;
		this.name = checkNotNull(name, "name");

		this.delimiter = checkNotNull(delimiter, "delimiter");

		if (delimiter == StrengthEntryDelimiter.COLON) {
			this.numeratorValue = checkNotNull(numeratorValue, "numeratorValue");
			this.numeratorMinValue = null;
			this.numeratorMaxValue = null;
			this.numeratorUnit = numeratorUnit;

			this.denominatorValue = checkNotNull(denominatorValue, "denominatorValue");
			this.denominatorUnit = denominatorUnit;
		} else {
			checkArgument(numeratorUnit > 0L, "Numerator unit must be set.");
			checkArgument(denominatorUnit > 0L, "Denominator unit must be set.");

			this.numeratorValue = checkNotNull(numeratorValue, "numeratorValue");
			this.numeratorMinValue = null;
			this.numeratorMaxValue = null;
			this.numeratorUnit = numeratorUnit;

			this.denominatorValue = checkNotNull(denominatorValue, "denominatorValue");
			this.denominatorUnit = denominatorUnit;
		}
	}

	/**
	 * RATIO_RANGE
	 * 
	 * @param name
	 *            - the name of the strength in lower camel caseformat.
	 * @param numeratorMinValue
	 *            - the numerator minimum value
	 * @param numeratorMaxValue
	 *            - the numerator maximum value
	 * @param numeratorUnit
	 *            - concept ID representing the numerator unit.
	 * @param delimiter
	 *            - the delimiter
	 * @param denominatorValue
	 *            - the denominator value
	 * @param denominatorUnit
	 *            - concept ID representing the denominator unit.
	 */
	public StrengthEntry(final String name, final BigDecimal numeratorMinValue, final BigDecimal numeratorMaxValue, final long numeratorUnit,
			final StrengthEntryDelimiter delimiter, final BigDecimal denominatorValue, final long denominatorUnit) {
		checkArgument(numeratorUnit > 0L, "Numerator unit must be set.");
		checkArgument(denominatorUnit > 0L, "Denominator unit must be set.");
		checkArgument((numeratorMinValue != null || numeratorMaxValue != null), "Either numerator minimum or maximum value must be set.");

		this.type = StrengthEntryType.RATIO_RANGE;
		this.name = checkNotNull(name, "name");
		this.delimiter = checkNotNull(delimiter, "delimiter");

		this.numeratorValue = null;
		this.numeratorMinValue = numeratorMinValue;
		this.numeratorMaxValue = numeratorMaxValue;
		this.numeratorUnit = numeratorUnit;

		this.denominatorValue = checkNotNull(denominatorValue, "denominatorValue");
		this.denominatorUnit = denominatorUnit;
	}

	/**
	 * Returns the name in lower camel case representation.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of the strength as a {@link StrengthEntryType}.
	 * 
	 * @return
	 */
	public StrengthEntryType getType() {
		return type;
	}

	/**
	 * Returns the numerator value. If numerator value exists, then numerator minimum and numerator maximum value should NOT.
	 * 
	 * @return
	 */
	public BigDecimal getNumeratorValue() {
		return numeratorValue;
	}

	/**
	 * Returns the numerator minimum value. If numerator minimum value exists then numerator value should NOT. The existence of numerator maximum
	 * value is optional.
	 * 
	 * @return
	 */
	public BigDecimal getNumeratorMinValue() {
		return numeratorMinValue;
	}

	/**
	 * Returns the numerator maximum value. If numerator maximum value exists then numerator value should NOT. The existence of numerator minimum
	 * value is optional.
	 * 
	 * @return
	 */
	public BigDecimal getNumeratorMaxValue() {
		return numeratorMaxValue;
	}

	/**
	 * Returns the numerator unit concept ID.
	 * 
	 * @return
	 */
	public long getNumeratorUnit() {
		return numeratorUnit;
	}

	/**
	 * Returns the delimiter as a {@link StrengthEntryDelimiter}
	 * 
	 * @return
	 */
	public StrengthEntryDelimiter getDelimiter() {
		return delimiter;
	}

	/**
	 * Returns the denominator value. The existence of this value restricts the type to be either {@link StrengthEntryType.RATIO} or
	 * {@link StrengthEntryType.RATIO_RANGE}.
	 * 
	 * @return
	 */
	public BigDecimal getDenominatorValue() {
		return denominatorValue;
	}

	/**
	 * Returns the denominator unit concept ID.
	 * 
	 * @return
	 */
	public long getDenominatorUnit() {
		return denominatorUnit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((delimiter == null) ? 0 : delimiter.hashCode());
		result = prime * result + (int) (denominatorUnit ^ (denominatorUnit >>> 32));
		result = prime * result + ((denominatorValue == null) ? 0 : denominatorValue.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((numeratorMaxValue == null) ? 0 : numeratorMaxValue.hashCode());
		result = prime * result + ((numeratorMinValue == null) ? 0 : numeratorMinValue.hashCode());
		result = prime * result + (int) (numeratorUnit ^ (numeratorUnit >>> 32));
		result = prime * result + ((numeratorValue == null) ? 0 : numeratorValue.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final StrengthEntry other = (StrengthEntry) obj;
		if (delimiter != other.delimiter)
			return false;
		if (denominatorUnit != other.denominatorUnit)
			return false;
		if (denominatorValue == null) {
			if (other.denominatorValue != null)
				return false;
		} else if (denominatorValue.compareTo(other.denominatorValue) != 0)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (numeratorMaxValue == null) {
			if (other.numeratorMaxValue != null)
				return false;
		} else if (numeratorMaxValue.compareTo(other.numeratorMaxValue) != 0)
			return false;
		if (numeratorMinValue == null) {
			if (other.numeratorMinValue != null)
				return false;
		} else if (numeratorMinValue.compareTo(other.numeratorMinValue) != 0)
			return false;
		if (numeratorUnit != other.numeratorUnit)
			return false;
		if (numeratorValue == null) {
			if (other.numeratorValue != null)
				return false;
		} else if (numeratorValue.compareTo(other.numeratorValue) != 0)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("StrengthEntry [");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (type != null)
			builder.append("type=").append(type).append(", ");
		if (numeratorValue != null)
			builder.append("numeratorValue=").append(numeratorValue).append(", ");
		if (numeratorMinValue != null)
			builder.append("numeratorMinValue=").append(numeratorMinValue).append(", ");
		if (numeratorMaxValue != null)
			builder.append("numeratorMaxValue=").append(numeratorMaxValue).append(", ");
		builder.append("numeratorUnit=").append(numeratorUnit).append(", ");
		if (delimiter != null)
			builder.append("delimiter=").append(delimiter).append(", ");
		if (denominatorValue != null)
			builder.append("denominatorValue=").append(denominatorValue).append(", ");
		builder.append("denominatorUnit=").append(denominatorUnit).append("]");
		return builder.toString();
	}

}