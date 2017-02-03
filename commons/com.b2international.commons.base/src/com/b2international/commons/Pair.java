/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Map.Entry;

import com.google.common.base.Objects;

/**
 * Represents a pair of values of the specified type.
 * <p>
 * Note that <code>Pair</code> is not {@link Serializable} and it doesn't override the default {@link #equals(Object)} 
 * and {@link #hashCode()} default implementations. If you need pairwise equality semantics, use the factory method 
 * {@link #identicalPairOf(Object, Object)}; for serialization purposes {@link #serializablePairOf(Serializable, Serializable)} 
 * should be used.
 */
public class Pair<A, B> {

	private A a;
	private B b;

	public Pair() {
	}

	public Pair(final A a, final B b) {
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return a;
	}

	public void setA(final A a) {
		this.a = a;
	}

	public B getB() {
		return b;
	}

	public void setB(final B b) {
		this.b = b;
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
	
	/**Creates a new pair from the given entry.*/
	public static final <A, B> Pair<A, B> of (final Entry<A, B> entry) {
		return of(entry.getKey(), entry.getValue());
	}
	
	/**Creates a new identical pair instance with the given values.*/
	public static <A, B> IdenticalPair<A, B> identicalPairOf(final A a, final B b) {
		return new IdenticalPair<A, B>(a, b);
	}

	/**Creates a new serializable pair instance for the given values.*/
	public static <A extends Serializable, B extends Serializable> SerializablePair<A, B> serializablePairOf(final A a, final B b) {
		return new SerializablePair<A, B>(a, b);
	}

	/**
	 * Returns as a singleton immutable map.
	 * <br>This method eagerly creates a new map instance each time this method is accessed.
	 * @return a map of the pair.
	 */
	public Map<A, B> asMap() {
		return Collections.singletonMap(a, b);
	}
	
	@Override
	public String toString() {
		return a + "->" + b;
	}

	/**
	 * A serializable pair of values.
	 * @see Pair
	 * @see Serializable
	 */
	public static class SerializablePair<A extends Serializable, B extends Serializable> extends Pair<A, B> implements Serializable {
		private static final long serialVersionUID = 6569609379343461859L;
		private SerializablePair(final A a, final B b) { super(a, b); }
	}
	
	/**
	 * A pair of values. Each instance of the class are equal if {@link #getA() A} and {@link #getB() B} values are equal.
	 */
	public static class IdenticalPair<A, B> extends Pair<A, B> {
		
		private IdenticalPair(final A a, final B b) { super(a, b); }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((getA() == null) ? 0 : getA().hashCode());
			result = prime * result + ((getB() == null) ? 0 : getB().hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final IdenticalPair<?, ?> other = (IdenticalPair<?, ?>) obj;
			return Objects.equal(getA(), other.getA()) && Objects.equal(getB(), other.getB());
		}
	}
}
