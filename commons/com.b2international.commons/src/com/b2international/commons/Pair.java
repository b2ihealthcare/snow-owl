/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.commons;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;

/**
 * Represents a pair of values of the specified type.
 * <p>
 * Note that <code>Pair</code> is not {@link Serializable} and it doesn't override the default {@link #equals(Object)} 
 * and {@link #hashCode()} default implementations. If you need pairwise equality semantics, use the factory method 
 * {@link #identicalPairOf(Object, Object)}; for serialization purposes {@link #serializablePairOf(Serializable, Serializable)} 
 * should be used.
 */
public class Pair<A, B> implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private static final Joiner ARROW_JOINER = Joiner.on("->");
	
	private final A a;
	private final B b;

	public Pair(final A a, final B b) {
		this.a = a;
		this.b = b;
	}

	public final A getA() {
		return a;
	}

	public final B getB() {
		return b;
	}
	
	@Override
	public final int hashCode() {
		return Objects.hash(a, b);
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		return Objects.equals(getA(), other.getA()) && Objects.equals(getB(), other.getB());
	}

	/**
	 * Returns as a singleton immutable map.
	 * <br>This method eagerly creates a new map instance each time this method is accessed.
	 * @return a map of the pair.
	 */
	public final Map<A, B> asMap() {
		return Collections.singletonMap(a, b);
	}
	
	@Override
	public final String toString() {
		return ARROW_JOINER.join(a, b);
	}
	
	/**
	 * Creates a {@link Pair} instance from the specified A and B arguments.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static final <A, B> Pair<A, B> of(final A a, final B b) {
		return new Pair<A, B>(a, b);
	}

}
