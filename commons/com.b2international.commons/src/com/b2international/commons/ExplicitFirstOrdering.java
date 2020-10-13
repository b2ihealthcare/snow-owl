/*
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.commons;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * An ordering that compares objects according to a given order. If a compared
 * object is not among the given elements, it will be placed last in the order.
 * 
 */
public class ExplicitFirstOrdering<T> extends Ordering<T> implements Serializable {

	private static final long serialVersionUID = 0;
	
	public static <T> ExplicitFirstOrdering<T> create(T firstValue, T... nextValues) {
		return new ExplicitFirstOrdering<T>(Lists.asList(firstValue, nextValues));
	}

	public static <T> ExplicitFirstOrdering<T> create(List<T> valuesInOrder) {
		return new ExplicitFirstOrdering<T>(valuesInOrder);
	}
	
	private final ImmutableMap<T, Integer> rankMap;
	private final int maxRank;
	
	private ExplicitFirstOrdering(List<T> valuesInOrder) {
		
		ImmutableMap.Builder<T, Integer> builder = ImmutableMap.builder();
		int rank = 0;
		
		for (T value : valuesInOrder) {
			builder.put(value, rank++);
		}
		
		this.rankMap = builder.build();
		this.maxRank = rank;
	}

	public int compare(T left, T right) {
		return rank(left) - rank(right); // safe because both are nonnegative
	}

	private int rank(T value) {
		Integer rank = rankMap.get(value);
		return (rank == null) ? maxRank : rank;
	}

	@Override
	public boolean equals(Object object) {
		
		if (object instanceof ExplicitFirstOrdering<?>) {
			ExplicitFirstOrdering<?> that = (ExplicitFirstOrdering<?>) object;
			return this.rankMap.equals(that.rankMap);
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return rankMap.hashCode();
	}

	@Override
	public String toString() {
		return "Ordering.explicitFirst(" + rankMap.keySet() + ")";
	}
}
