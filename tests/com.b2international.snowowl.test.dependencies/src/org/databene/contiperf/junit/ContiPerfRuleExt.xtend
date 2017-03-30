/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package org.databene.contiperf.junit

import org.junit.internal.runners.statements.RunAfters
import org.junit.internal.runners.statements.RunBefores
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement

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
 * 
 */
class ContiPerfRuleExt extends ContiPerfRule {

	static val FIELD_NAME_JUNIT_411 = 'fNext';
	static val FIELD_NAME_JUNIT_412 = 'next';

	@Override
	override apply(Statement it, FrameworkMethod method, Object target) {
		super.apply(wrappedStatement, method, target);
	}

	private static dispatch def wrappedStatement(Statement it) {
		return it;
	}

	private static dispatch def wrappedStatement(RunBefores it) {
		if (requiresFieldMapping) {
			return new RunBefores_411(it, nextFieldValue);
		}
		return it;
	}

	private static dispatch def wrappedStatement(RunAfters it) {
		if (requiresFieldMapping) {
			return new RunAfters_411(it, nextFieldValue);
		}
		return it;
	}

	private static def requiresFieldMapping(Statement it) {
		return hasField(FIELD_NAME_JUNIT_412) && !hasField(FIELD_NAME_JUNIT_411);
	}

	private static def hasField(Statement it, String fieldName) {
		return class.declaredFields.exists[fieldName == name];
	}

	private static def getNextFieldValue(Statement it) {
		val field = class.getDeclaredField(FIELD_NAME_JUNIT_412);
		field.accessible = true;
		return field.get(it) as Statement;
	}

	private static class RunBefores_411 extends RunBefores {

		val Statement delegate;
		val Statement fNext;

		private new(RunBefores delegate, Statement it) {
			// We delegate to the evaluate method anyway.
			super(null, null, null);
			this.delegate = delegate;
			this.fNext = wrappedStatement;
		}

		@Override
		override evaluate() throws Throwable {
			delegate.evaluate();
		}

	}

	private static class RunAfters_411 extends RunAfters {

		val Statement delegate;
		val Statement fNext;

		private new(RunAfters delegate, Statement it) {
			// We delegate to the evaluate method anyway.
			super(null, null, null);
			this.delegate = delegate;
			this.fNext = wrappedStatement;
		}

		@Override
		override evaluate() throws Throwable {
			delegate.evaluate();
		}

	}

}
