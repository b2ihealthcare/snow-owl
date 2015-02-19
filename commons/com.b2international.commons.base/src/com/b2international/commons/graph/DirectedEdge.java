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
package com.b2international.commons.graph;

/**
 * Represents a directed graph edge.
 * 
 */
public class DirectedEdge<N> {
	private final N from;
	private final N to;
	
	public DirectedEdge(N from, N to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Returns the node, which the edge starts from.
	 * 
	 * @return the node the edge starts from
	 */
	public N getSource() {
		return from;
	}
	
	/**
	 * Returns the node, which the edge points to.
	 * 
	 * @return the node the edge points to
	 */
	public N getDestination() {
		return to;
	}
	
	/**
	 * Returns the reverse of this directed edge.
	 * 
	 * @return the reverse of this directed edge
	 */
	public DirectedEdge<N> reverse() {
		return new DirectedEdge<N>(to, from);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirectedEdge other = (DirectedEdge) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return from + " => " + to;
	}
}