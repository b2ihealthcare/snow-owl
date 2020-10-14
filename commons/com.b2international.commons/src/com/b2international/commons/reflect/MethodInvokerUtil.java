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
package com.b2international.commons.reflect;

import static com.google.common.collect.Lists.newArrayList;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;

/**
 * Utility methods when working with method invocations (through reflections).
 * 
 * @since 2.9
 */
public class MethodInvokerUtil {

	/**
	 * Invokes the methods within the given it {@link Object} where the method
	 * has the specified {@link Annotation}.
	 * 
	 * @param it
	 * @param annotation
	 * @param args
	 * @throws InvocationTargetException
	 */
	public static void invokeAnnotatedMethods(Object it, Class<? extends Annotation> annotation, Object... args)
			throws InvocationTargetException {
		invokeMethods(it, new AnnotationPredicate(annotation), args);
	}

	/**
	 * Invokes the methods declared in the given it {@link Object}, where the
	 * method is meets the requirements of the given {@link Predicate}.
	 * 
	 * @param it
	 * @param predicate
	 * @throws InvocationTargetException
	 */
	public static void invokeMethods(Object it, Predicate<Method> predicate, Object... args)
			throws InvocationTargetException {
		for (Method method : it.getClass().getMethods()) {
			if (predicate.apply(method)) {
				invoke(method, it, args);
			}
		}
	}

	/**
	 * Invokes the specified method on the given it Object with the given
	 * arguments. Throws an {@link Error} if the method invocation results in a
	 * checked exception.
	 * 
	 * @param method
	 * @param it
	 * @param args
	 * @throws InvocationTargetException
	 * @throws Error
	 * @return
	 * @see Method#invoke(Object, Object...)
	 */
	public static Object invoke(Method method, Object it, Object... args) throws InvocationTargetException {
		try {
			method.setAccessible(true);
			return method.invoke(it, args);
		} catch (IllegalArgumentException e) {
			throw new Error(String.format("Method '%s' rejected arguments(%s)", method.getName(), argumentList(args)),
					e);
		} catch (IllegalAccessException e) {
			throw new Error("Method became inaccessible.", e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof Error) {
				throw (Error) e.getCause();
			}
			throw e;
		}
	}

	/**
	 * Creates a comma separated list from the given arguments.
	 * 
	 * @param args
	 * @return
	 */
	public static String argumentList(Object[] args) {
		if (args == null) {
			return "null";
		}
		return Joiner.on(", ").useForNull("null").join(newArrayList(args));
	}

	/**
	 * Returns <code>true</code> if the actually tested method has the specified
	 * {@link Annotation}.
	 * 
	 * 
	 */
	public static class AnnotationPredicate implements Predicate<Method> {

		private final Class<? extends Annotation> annotation;

		public AnnotationPredicate(Class<? extends Annotation> annotation) {
			this.annotation = annotation;
		}

		@Override
		public boolean apply(Method input) {
			return input.isAnnotationPresent(annotation);
		}

	}

}