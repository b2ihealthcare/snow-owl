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

import java.math.BigDecimal;

/**
 * @since 5.4
 */
public final class DecimalRangePredicate extends Predicate {

	private final BigDecimal from;
	private final BigDecimal to;
	private boolean includeFrom;
	private boolean includeTo;

	DecimalRangePredicate(String field, BigDecimal from, BigDecimal to) {
		this(field, from, to, true, true);
	}
	
	DecimalRangePredicate(String field, BigDecimal from, BigDecimal to, boolean includeFrom, boolean includeTo) {
		super(field);
		this.from = from;
		this.to = to;
		this.includeFrom = includeFrom;
		this.includeTo = includeTo;
	}
	
	public boolean isIncludeFrom() {
		return includeFrom;
	}
	
	public boolean isIncludeTo() {
		return includeTo;
	}
	
	public BigDecimal from() {
		return from;
	}
	
	public BigDecimal to() {
		return to;
	}
	
	@Override
	public String toString() {
		return String.format("%s is gte(%s) and lte(%s)", getField(), from, to);
	}
	
}
