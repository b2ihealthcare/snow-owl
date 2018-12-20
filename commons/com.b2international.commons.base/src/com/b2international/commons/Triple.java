/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * An immutable triple of values.
 * <p>Overridden {@link Triple#equals(Object) equals} and {@link Triple#hashCode() hashCode}.</p>
 * @param <A> - type of the A.
 * @param <B> - type of the B.
 * @param <C> - type of the C.
 */
public class Triple<A, B, C> implements Serializable {
	
	private static final long serialVersionUID = 5130357061588438430L;

	private final A a;
	private final B b;
	private final C c;

	/**
	 * Creates a {@link Triple triple} of the specified items.
	 * @param a Cannot be {@code null}.
	 * @param b Cannot be {@code null}.
	 * @param c Cannot be {@code null}.
	 * @return a triple of something.
	 */
	public static <A, B, C> Triple<A, B, C> of(final A a, final B b, final C c) {
		return new Triple<A, B, C>(a, b, c);
	}

	/**
	 * Creates a {@link Triple triple} of something.
	 * @param a Cannot be {@code null}.
	 * @param b Cannot be {@code null}.
	 * @param c Cannot be {@code null}.
	 */
	public Triple(final A a, final B b, final C c) {
		this.a = Preconditions.checkNotNull(a, "Argument 'A' cannot be null.");
		this.b = Preconditions.checkNotNull(b, "Argument 'B' cannot be null.");
		this.c = Preconditions.checkNotNull(c, "Argument 'C' cannot be null.");
	}
	
	/**
	 * Returns with the 'A'. Never {@code null}.
	 * @return the A.
	 */
	public A getA() {
		return a;
	}
	
	/**
	 * Returns with the 'B'. Cannot be {@code null}.
	 * @return the B.
	 */
	public B getB() {
		return b;
	}
	
	/**
	 * Returns with the 'C'. Never {@code null}.
	 * @return the C.
	 */
	public C getC() {
		return c;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("A", a).add("B", b).add("C", c).toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Triple)) {
			return false;
		}
		final Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
		if (a == null) {
			if (other.a != null) {
				return false;
			}
		} else if (!a.equals(other.a)) {
			return false;
		}
		if (b == null) {
			if (other.b != null) {
				return false;
			}
		} else if (!b.equals(other.b)) {
			return false;
		}
		if (c == null) {
			if (other.c != null) {
				return false;
			}
		} else if (!c.equals(other.c)) {
			return false;
		}
		return true;
	}
	
	
	
	
}