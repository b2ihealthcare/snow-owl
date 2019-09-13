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
package com.b2international.snowowl.server.product;

import java.util.concurrent.CountDownLatch;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.SnowOwl;

/**
 * @since 7.2
 */
public class SnowOwlServerApplication implements IApplication {

	private final CountDownLatch latch = new CountDownLatch(1);

	private SnowOwl snowowl;

	@Override
	public Object start(IApplicationContext context) throws Exception {
		try {
			snowowl = SnowOwl.create().bootstrap().run();
			context.applicationRunning();
			latch.await();
		} catch (Throwable e) {
			LoggerFactory.getLogger("snowowl").error(e.getMessage(), e);
			return -1;
		}
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		if (snowowl != null) {
			snowowl.shutdown();
		}
		latch.countDown();
	}

}
