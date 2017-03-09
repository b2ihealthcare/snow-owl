/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons;

import java.util.List;

import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * A modification of {@link Suite} that substitutes {@link LogRunner} in place of the default {@link BlockJUnit4ClassRunner}.
 * 
 * @since 4.6
 */
public class LogSuite extends Suite {

	private List<Runner> transformedChildren;

	public LogSuite(final Class<?> klass, final RunnerBuilder builder) throws InitializationError {
		super(klass, builder);
	}

	public LogSuite(final RunnerBuilder builder, final Class<?>[] classes) throws InitializationError {
		super(builder, classes);
	}

	@Override
	protected List<Runner> getChildren() {
		if (transformedChildren == null) {
			transformedChildren = FluentIterable.from(super.getChildren())
					.transform(new Function<Runner, Runner>() {
						@Override
						public Runner apply(final Runner input) {

							if (input instanceof BlockJUnit4ClassRunner) { 

								try {
									return new LogRunner(getJavaClass(input));
								} catch (final InitializationError unexpected) {
									// XXX: This should not happen, as a JUnit runner could be successfully created using the test class...
									return input;
								}

							} else {
								return input;
							}
						}

						private Class<?> getJavaClass(final Runner input) {
							return ((ParentRunner<?>) input).getTestClass().getJavaClass();
						}
					})
					.toList();
		}

		return transformedChildren;
	}

}
