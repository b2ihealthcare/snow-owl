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
package com.b2international.snowowl.server.product;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.b2international.snowowl.core.ApplicationContext;

/**
 * Server-side Snow Owl OSGi application.
 *
 */
public class ServerApplication implements IApplication {

	private final Object appLock = new Object();
	private boolean done = false;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext appContext) throws Exception {
		
		waitForDone();

		return IApplication.EXIT_OK;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		
		//dispose the application context
		ApplicationContext appContext = ApplicationContext.getInstance();
		appContext.dispose();

		synchronized (appLock) {
			done = true;
			appLock.notifyAll();
		}
	}

	private void waitForDone() {
		// then just wait here
		synchronized (appLock) {
			while (!done) {
				try {
					appLock.wait();
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}
	}

}