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
package org.easyb.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

import java.util.ArrayList;
import java.util.List;

public class RunNotifierReplay extends RunNotifier {
	private class Event {
		String method;
		Description description;
		Failure failure;
		long time = System.currentTimeMillis();

		public Event(final String method, final Description description) {
			this.method = method;
			this.description = description;
		}

		public Event(final String method, final Failure failure) {
			this.method = method;
			this.failure = failure;
		}
	}

	private final List<Event> events = new ArrayList<Event>();

	public void fireTestStarted(final Description description) throws StoppedByUserException {
		events.add(new Event("start", description));
	}

	public void fireTestFinished(final Description description) {
		events.add(new Event("finish", description));
	}

	public void fireTestFailure(final Failure failure) {
		events.add(new Event("fail", failure));
	}

	public void replay(final RunNotifier notifier, final boolean trackTime) {
		long start = 0;
		for (final Event event : events) {
			if (event.method.equals("start")) {
				notifier.fireTestStarted(event.description);
				start = event.time;
			} else if (event.method.equals("finish")) {
				if (trackTime)
					sleep(event.time - start);
				notifier.fireTestFinished(event.description);
			} else {
				notifier.fireTestFailure(event.failure);
			}
		}
	}

	private void sleep(final long time) {
		try {
			Thread.sleep(time);
		} catch (final InterruptedException e) {
		}
	}
}