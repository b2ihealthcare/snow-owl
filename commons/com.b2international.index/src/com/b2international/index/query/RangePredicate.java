/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.query;

import java.util.Objects;

/**
 * @since 5.4
 */
public abstract class RangePredicate<T> extends Predicate {

	private static final String LT = "<";
	private static final String LTE = "<=";
	private static final String GT = ">";
	private static final String GTE = ">=";
	
	private final T lower;
	private final T upper;
	private final boolean includeLower;
	private final boolean includeUpper;

	protected RangePredicate(String field, T lower, T upper, boolean includeLower, boolean includeUpper) {
		super(field);
		this.lower = lower;
		this.upper = upper;
		this.includeLower = includeLower;
		this.includeUpper = includeUpper;
	}
	
	public final T lower() {
		return lower;
	}
	
	public final T upper() {
		return upper;
	}
	
	public final boolean isIncludeLower() {
		return includeLower;
	}
	
	public final boolean isIncludeUpper() {
		return includeUpper;
	}
	
	@Override
	public final int hashCode() {
		return Objects.hash(getField(), lower(), upper(), isIncludeLower(), isIncludeUpper());
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (super.equals(obj)) {
			final RangePredicate<?> other = (RangePredicate<?>) obj;
			return Objects.equals(lower(), other.lower()) 
					&& Objects.equals(upper(), other.upper())
					&& isIncludeLower() == other.isIncludeLower()
					&& isIncludeUpper() == other.isIncludeUpper();
		} else {
			return false;
		}
	}
	
	@Override
	public final String toString() {
		return String.format("%s %s %s and %s %s %s", getField(), isIncludeLower() ? GTE : GT, lower, getField(), isIncludeUpper() ? LTE : LT, upper);
	}

}
