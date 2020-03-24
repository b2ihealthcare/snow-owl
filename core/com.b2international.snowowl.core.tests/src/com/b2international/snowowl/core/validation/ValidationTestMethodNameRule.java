/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * A JUnit rule that can be used to extract the name of the method that is currently being executed.
 * @since 6.6
 */
public final class ValidationTestMethodNameRule extends TestWatcher {

	private String methodName;

	@Override
	protected void starting(Description description) {
		System.out.println("===== Start of " + description + " =====");
		this.methodName = description.getMethodName();
	}
	
	@Override
	protected void finished(Description description) {
		System.out.println("===== End of " + description + " =====");
	}
	
	public String get() {
		return methodName;
	}
	
}
