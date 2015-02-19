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

import org.easyb.BehaviorStep;
import org.easyb.domain.Behavior;
import org.easyb.listener.ExecutionListener;
import org.easyb.result.ReportingTag;
import org.easyb.result.Result;

import java.util.ArrayList;
import java.util.List;

public class JunitExecutionListenerRegistry implements ExecutionListener {

	private final List<ExecutionListener> listeners = new ArrayList<ExecutionListener>();

	public void startBehavior(final Behavior behavior) {
		for (final ExecutionListener listener : listeners) {
			listener.startBehavior(behavior);
		}
	}

	public void stopBehavior(final BehaviorStep step, final Behavior behavior) {
		for (final ExecutionListener listener : listeners) {
			listener.stopBehavior(step, behavior);
		}
	}

	public void tag(final ReportingTag tag) {
		for (final ExecutionListener listener : listeners) {
			listener.tag(tag);
		}
	}

	public void startStep(final BehaviorStep step) {
		for (final ExecutionListener listener : listeners) {
			listener.startStep(step);
		}
	}

	public void describeStep(final String description) {
		for (final ExecutionListener listener : listeners) {
			listener.describeStep(description);
		}
	}

	public void completeTesting() {
		for (final ExecutionListener listener : listeners) {
			listener.completeTesting();
		}
	}

	public void stopStep() {
		for (final ExecutionListener listener : listeners) {
			listener.stopStep();
		}
	}

	public void gotResult(final Result result) {
		for (final ExecutionListener listener : listeners) {
			listener.gotResult(result);
		}
	}

	public void registerListener(final ExecutionListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public void unregisterListener(final ExecutionListener listener) {
		listeners.remove(listener);
	}
}