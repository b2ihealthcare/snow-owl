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
package com.b2international.commons.hierarchy;

import static com.b2international.commons.CompareUtils.isEmpty;
import static com.b2international.commons.collections.Collections3.forEach;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.asList;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.reverse;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

import com.b2international.commons.collections.Procedure;

/**
 * Iterator for traversing a derivation on {@link Ancestor} instances in pre-order fashion.
 *
 */
public class PreorderIterator<E extends Ancestor<E>> implements Iterator<E> {

	private Stack<E> stack;

	public PreorderIterator(final E node, @SuppressWarnings("unchecked") final E... others) {
		this(asList(checkNotNull(node, "node"), checkNotNull(others, "others")));
	}
	
	public PreorderIterator(final Iterable<E> nodes) {
		stack = new Stack<E>();
		push(checkNotNull(nodes, "nodes"));
	}
	
	@Override
	public boolean hasNext() {
		return !isEmpty(stack);
	}

	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		final E node = stack.pop();
		if (!isEmpty(node.getChildren())) {
			push(node.getChildren());
		}
		return node;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private Iterable<E> push(final Iterable<E> nodes) {
		final List<E> reverseNodes = newArrayList(nodes);
		reverse(reverseNodes);
		forEach(reverseNodes, new Procedure<E>() {
			protected void doApply(final E node) {
				stack.push(checkNotNull(node, "node"));
			}
		});
		return reverseNodes;
	}
}