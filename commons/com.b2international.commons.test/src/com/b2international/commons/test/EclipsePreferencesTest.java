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
package com.b2international.commons.test;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Test for checking the deadlock in EclipsePreferences.
 * See: https://github.com/b2ihealthcare/mohh/issues/797 .
 * 
 * Description is copied from: https://bugs.eclipse.org/bugs/show_bug.cgi?id=389630#c1
 *  The problem is fairly simple:
 * - Persistence of a hierarchy of preference nodes happens at some root node
 * - If you attempt to flush a child of this root, we walk *up* to the root node and then flush the root
 * - When  the root is flushing, it walks *down* the preference hierarchy to collect all the values to be persisted
 * - We lock each node as we are traversing up or down
 * - If one thread is walking up while the other is walking down, we can deadlock.
 * 
 *
 */
public class EclipsePreferencesTest {

	public static final String RUNTIME_TESTS = "com.b2international.commons.test.EclipsePreferencesTest.RUNTIME_TESTS";
	
	@Test(timeout=5000L)
	public void testFlushDeadlock() {

		final IEclipsePreferences parent = InstanceScope.INSTANCE.getNode(RUNTIME_TESTS);
		final Preferences child = parent.node("testFlushDeadlock");
		class FlushJob extends Job {
			private final Preferences node;

			FlushJob(final Preferences node) {
				super("testFlushDeadlock");
				this.node = node;
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try {
					node.flush();
				} catch (final BackingStoreException e) {
					return new Status(IStatus.ERROR, RUNTIME_TESTS, "unexpected flush failure", e);
				}
				return Status.OK_STATUS;
			}

		}
		//make sure node is dirty
		child.putBoolean("testFlushDeadlock", true);
		//flush the parent of the load level, and the child
		final Job flushParent = new FlushJob(parent);
		final Job flushChild = new FlushJob(child);
		flushParent.schedule();
		flushChild.schedule();

		try {
			flushParent.join();
			flushChild.join();
		} catch (final InterruptedException e) {
			throw new RuntimeException("Interrupted while updating preferences store.", e);
		}
	}


}