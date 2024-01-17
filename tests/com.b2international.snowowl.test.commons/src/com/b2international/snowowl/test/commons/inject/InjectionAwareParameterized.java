/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.test.commons.inject;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 */
public class InjectionAwareParameterized extends Parameterized {

	private static Map<Class<?>, IInjectorProvider> injectorProviderClassCache = newHashMap();
	private List<Runner> runners = newArrayList();

	public InjectionAwareParameterized(Class<?> klass) throws Throwable {
		super(klass);
		List<Object[]> parametersList = getParametersList(getTestClass());
		for (int i = 0; i < parametersList.size(); i++) {
			runners.add(new InjectionAwareTestClassRunner(getTestClass().getJavaClass(), parametersList, i));
		}
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getParametersList(TestClass klass) throws Throwable {
		return (List<Object[]>) getParametersMethod(klass).invokeExplosively(null);
	}

	private FrameworkMethod getParametersMethod(TestClass testClass) throws Exception {
		List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Parameters.class);
		for (FrameworkMethod each : methods) {
			int modifiers = each.getMethod().getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}

		throw new Exception("No public static parameters method on class " + testClass.getName());
	}

	protected IInjectorProvider getInjectorProvider() {
		return injectorProviderClassCache.get(getTestClass().getJavaClass());
	}

	protected IInjectorProvider getOrCreateInjectorProvider() {
		IInjectorProvider injectorProvider = getInjectorProvider();
		if (injectorProvider == null) {
			injectorProvider = createInjectorProvider();
			injectorProviderClassCache.put(getTestClass().getJavaClass(), injectorProvider);
		}
		return injectorProvider;
	}

	protected IInjectorProvider createInjectorProvider() {
		IInjectorProvider injectorProvider = null;
		InjectWith injectWith = getTestClass().getJavaClass().getAnnotation(InjectWith.class);
		if (injectWith != null) {
			try {
				injectorProvider = injectWith.value().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				// throwUncheckedException(e);
			}
		}
		return injectorProvider;
	}

	private class InjectionAwareTestClassRunner extends BlockJUnit4ClassRunner {

		private final int fParameterSetNumber;

		private final List<Object[]> fParameterList;

		InjectionAwareTestClassRunner(Class<?> klass, List<Object[]> parameterList, int parameterSetNumber) throws InitializationError {
			super(klass);
			fParameterList = parameterList;
			fParameterSetNumber = parameterSetNumber;
		}

		@Override
		public Object createTest() throws Exception {
			Object object = getTestClass().getOnlyConstructor().newInstance(computeParams());
			getOrCreateInjectorProvider().getInjector().injectMembers(object);
			return object;
		}

		private Object[] computeParams() throws Exception {
			try {
				return fParameterList.get(fParameterSetNumber);
			} catch (ClassCastException e) {
				throw new Exception(String.format("%s.%s() must return a Collection of arrays.", getTestClass().getName(), getParametersMethod(getTestClass())
						.getName()));
			}
		}

		@Override
		protected String getName() {
			return String.format("[%s]", fParameterSetNumber);
		}

		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s[%s]", method.getName(), fParameterSetNumber);
		}

		@Override
		protected void validateConstructor(List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}

		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return childrenInvoker(notifier);
		}

	}

}
