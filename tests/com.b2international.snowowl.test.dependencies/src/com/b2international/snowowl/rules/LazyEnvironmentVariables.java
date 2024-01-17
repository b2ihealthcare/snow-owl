/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @since 8.4.0
 */
public class LazyEnvironmentVariables extends EnvironmentVariables {
	
	private final Map<String, Supplier<String>> lazyEnvironmentVariables = new HashMap<>();
	
	public EnvironmentVariables set(String name, Supplier<String> value) {
		lazyEnvironmentVariables.put(name, value);
		return this;
	}
	
	@Override
	public Statement apply(Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				// before apply, resolve all lazy values
				lazyEnvironmentVariables.forEach((key, value) -> {
					set(key, value.get());
				});				
				LazyEnvironmentVariables.super.apply(base, description).evaluate();
			}
		}; 
	}
	
}

