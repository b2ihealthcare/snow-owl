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

import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link TestRule} that logs the name of each test method as they start and finish (successfully or otherwise).
 * 
 * @since 4.6
 */
public class LogWatcher extends TestWatcher {

	private static final Logger LOG = LoggerFactory.getLogger("junit");

	@Override
	protected void starting(final Description description) {
		LOG.info("Test {} starting.", description);
	}

	@Override
	protected void succeeded(final Description description) {
		LOG.info("Test {} succeeded.", description);
	}

	@Override
	protected void failed(final Throwable e, final Description description) {
		LOG.error("Test {} failed with exception.", description, e);
	}
}
