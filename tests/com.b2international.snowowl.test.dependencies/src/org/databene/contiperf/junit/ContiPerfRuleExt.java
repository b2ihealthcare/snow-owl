/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package org.databene.contiperf.junit;

import java.lang.reflect.Field;

import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * That is just a workaround to support both JUnit 4.11 and 4.12 with the rule.
 * This performance rule extension makes sure that each statement is wrapped into
 * the JUnit 4.11 format.
 * 
 * <p>
 * From JUnit 4.12, the {@code fNext} field has been replaced with the
 * {@code next} field on both {@code RunAfters} and {@code RunBefores}
 * statements. This class is for handling both cases gracefully.
 * <p>
 * More details about the issue can be found 
 * <a href="https://github.com/lucaspouzac/contiperf/issues/9">here</a>.
 */
public class ContiPerfRuleExt extends ContiPerfRule {

	public static final String FIELD_NAME_JUNIT_411 = "fNext";
	public static final String FIELD_NAME_JUNIT_412 = "next";

	@Override
	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		return super.apply(wrappedStatement(base), method, target);
	}
	
	private static Statement wrappedStatement(Statement base) {
		if (base instanceof RunBefores) {
			if (requiresFieldMapping(base)) {
				return new RunBefores_411((RunBefores) base, getNextFieldValue(base));
			}
		} else if (base instanceof RunAfters) {
			if (requiresFieldMapping(base)) {
				return new RunAfters_411((RunAfters) base, getNextFieldValue(base));
			}
		}
		return base;
	}

	private static boolean requiresFieldMapping(Statement it) {
		return hasField(it, FIELD_NAME_JUNIT_412) && !hasField(it, FIELD_NAME_JUNIT_411);
	}

	private static boolean hasField(Statement it, String fieldName) {
		for (Field field : it.getClass().getDeclaredFields()) {
			if (field.getName().equals(fieldName)) {
				return true;
			}
		}
		return false;
	}

	private static Statement getNextFieldValue(Statement it) {
		try {
			final Field field = it.getClass().getDeclaredField(FIELD_NAME_JUNIT_412);
			field.setAccessible(true);
			return (Statement) field.get(it);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class RunBefores_411 extends RunBefores {

		private final Statement delegate;
		private final Statement fNext;

		private RunBefores_411(RunBefores delegate, Statement it) {
			// We delegate to the evaluate method anyway.
			super(null, null, null);
			this.delegate = delegate;
			this.fNext = wrappedStatement(it);
		}

		@Override
		public void evaluate() throws Throwable {
			delegate.evaluate();
		}

	}

	private static class RunAfters_411 extends RunAfters {

		private final Statement delegate;
		private final Statement fNext;

		private RunAfters_411(RunAfters delegate, Statement it) {
			// We delegate to the evaluate method anyway.
			super(null, null, null);
			this.delegate = delegate;
			this.fNext = wrappedStatement(it);
		}

		@Override
		public void evaluate() throws Throwable {
			delegate.evaluate();
		}

	}
	
}