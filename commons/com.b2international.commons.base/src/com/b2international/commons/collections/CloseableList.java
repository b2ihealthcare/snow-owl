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
package com.b2international.commons.collections;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.b2international.commons.AutoCloseables;

/**
 * A closeable list for storing closeable elements.
 *
 */
public class CloseableList<E extends AutoCloseable> extends ArrayList<E> implements AutoCloseable {

	private static final long serialVersionUID = -2550312342958930662L;

	/**
	 * Creates a closeable list with the given elements.
	 * @param elements the elements.
	 * @return a mutable closeable list instance.
	 */
	public static <E extends AutoCloseable> CloseableList<E> newCloseableList(@SuppressWarnings("unchecked") final E... elements) {
		checkNotNull(elements);
		final CloseableList<E> list = new CloseableList<>();
		Collections.addAll(list, elements);
		return list;
	}
	
	/**
	 * Creates a closeable list with the given elements.
	 * @param elements the elements.
	 * @return a mutable closeable list instance.
	 */
	public static <E extends AutoCloseable> CloseableList<E> newCloseableList(final Collection<E> elements) {
		checkNotNull(elements);
		final CloseableList<E> list = new CloseableList<>();
		list.addAll(elements);
		return list;
	}
	
	@Override
	public void close() throws Exception {
		AutoCloseables.close(this);
	}

}