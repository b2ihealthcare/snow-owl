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
package com.b2international.snowowl.datastore.server.internal.lucene.store;

import java.io.IOException;
import java.util.Date;

import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;

/**
 * A {@link LockFactory} implementation which delegates actual work to another lock.
 * 
 */
public class LoggingLockFactory extends DelegatingLockFactory {

	public static void log(final String template, final Object... args) {
		System.err.println(String.format("[%1$tF %1$tT] %2$s", new Date(), String.format(template, args)));
	}

	public LoggingLockFactory(final LockFactory delegate) {
		super(delegate);
	}
	
	@Override
	public Lock makeLock(final String lockName) {
		final LoggingLock lock = new LoggingLock(super.makeLock(lockName));
		log("Made lock with name: %s", lockName);
		return lock;
	}
	
	@Override
	public void clearLock(String lockName) throws IOException {
		super.clearLock(lockName);
		log("Cleared lock with name: %s", lockName);
	}
	
	@Override
	public void setLockPrefix(String lockPrefix) {
		super.setLockPrefix(lockPrefix);
		log("Set lock prefix to: %s", lockPrefix);
	}
}