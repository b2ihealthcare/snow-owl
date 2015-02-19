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
package com.b2international.commons;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility class for {@link AutoCloseable} instances.
 *
 */
public class AutoCloseables {

	/**
	 * Tries to close the argument. Has no effect if the argument is *NOT*
	 * an {@link AutoCloseable} instance.
	 * @param object the object to try to close
	 * @throws Exception
	 */
	public static void tryClose(final Object object) throws Exception {
		if (object instanceof AutoCloseable) {
			((AutoCloseable) object).close();
		}
	}
	
	/**
	 * Tries to close each element of the iterable of {@link AutoCloseable} instances.
	 * @param closeables an iterable of {@link AutoCloseable} instances to close.
	 * @throws Exception
	 */
	public static <E extends AutoCloseable> void close(Iterable<? extends E> closeables) throws Exception {
		Exception exc = null;
		for (final E element : checkNotNull(closeables, "closeables")) {
			final Exception e = tryClose(element);
			if (null == exc) {
				exc = e;
			} else {
				exc.addSuppressed(e);
			}
		}
		if (null != exc) {
			throw exc;
		}
	}

	private static <E extends AutoCloseable> Exception tryClose(final E element) {
		try {
			checkNotNull(element, "element").close();
			return null;
		} catch (final Exception e) {
			if (null != element) {
				try {
					element.close();
				} catch (final Exception e1) {
					e.addSuppressed(e1);
				}
			}
			return e;
		}
	}
	
}