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
import org.easyb.listener.ExecutionListenerAdaptor;
import org.easyb.result.Result;
import org.easyb.util.BehaviorStepType;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.util.Arrays;
import java.util.List;

import static org.easyb.junit.RunProperties.isEclipse;
import static org.easyb.junit.RunProperties.isIDEA;
import static org.easyb.util.BehaviorStepType.*;
import static org.junit.runner.Description.createSuiteDescription;

public class JUnitExecutionListener extends ExecutionListenerAdaptor {
	private static final List<BehaviorStepType> typesToTrack = Arrays.asList(SCENARIO, GIVEN, WHEN, THEN, AND, IT, BEFORE, AFTER);
	private final Description behaviorDescription;
	private Description scenarioDescription;
	private final RunNotifier notifier;
	private BehaviorStep behaviorStep;
	private Description currentDescription;
	private boolean stepRunning;
	private static int counter;

	public JUnitExecutionListener(final Description behaviorDescription, final RunNotifier notifier) {
		this.behaviorDescription = behaviorDescription;
		this.notifier = notifier;
	}

	public void gotResult(final Result result) {
		testForFailure(result);
	}

	private void testForFailure(final Result result) {
		if (result.failed()) {
			notifier.fireTestFailure(new Failure(currentDescription, result.cause));
			final Throwable cause = result.cause;
			final String msg = cause == null ? "unknown cause" : cause.getMessage();
			System.out.print(" -> Failed: " + msg);
		}
	}

	public void startStep(final BehaviorStep behaviorStep) {
		if (shouldStart(behaviorStep)) {
			this.behaviorStep = behaviorStep;
			startBehaviorStep();
		}
	}

	private boolean shouldStart(final BehaviorStep behaviorStep) {
		return typesToTrack.contains(behaviorStep.getStepType());
	}

	@Override
	public void startBehavior(final Behavior behavior) {
		System.out.println(behavior.getPhrase());
	}

	private void startBehaviorStep() {
		stopStepIfRunning();
		if (behaviorStep.getStepType() == SCENARIO)
			System.out.println();
		if (shouldPrintDescription())
			System.out.print(getStepDescriptionText());
		createStepDescription();
		notifier.fireTestStarted(currentDescription);
		stepRunning = true;
	}

	private boolean shouldPrintDescription() {
		return behaviorStep.getStepType() != BEFORE && behaviorStep.getStepType() != AFTER;
	}

	public void stopStep() {
		stopStepIfRunning();
	}

	private void stopStepIfRunning() {
		if (stepRunning) {
			notifier.fireTestFinished(currentDescription);
			stepRunning = false;
			if (shouldPrintDescription())
				System.out.println();
		}
	}

	private void createStepDescription() {
		if (behaviorStep.getStepType() == SCENARIO && (isEclipse() || isIDEA())) {
			scenarioDescription = createSuiteDescription(getStepDescriptionText());
			behaviorDescription.addChild(scenarioDescription);
			currentDescription = scenarioDescription;
		} else {
			currentDescription = createSuiteDescription(getStepDescriptionText() + "(" + getBehaviorHiddenName() + ")");
			if (scenarioDescription == null) {
				behaviorDescription.addChild(currentDescription);
			} else {
				scenarioDescription.addChild(currentDescription);
			}
		}
	}

	/*
	 * This is a bit of a hack, but in order to make sure the description for a
	 * step is unique in Eclipse here we are just incrementing a number as the
	 * behavior hidden name. But we don't want to do this when jUnit is being
	 * run through Ant because ant can handle duplicate descriptions names just
	 * fine, and using the counter will mess up the Ant output.
	 */
	private String getBehaviorHiddenName() {
		return isEclipse() ? String.valueOf(counter++) : behaviorDescription.getDisplayName();
	}

	private String getStepDescriptionText() {
		return format(behaviorStep.getStepType()) + " " + behaviorStep.getName();
	}

	private String format(final BehaviorStepType type) {
		return typeShouldHaveSemiColon(type) ? type.type() + ":" : type.type();
	}

	private boolean typeShouldHaveSemiColon(final BehaviorStepType type) {
		return type == SCENARIO || type == BEFORE || type == AFTER;
	}

	public void stopBehavior(final BehaviorStep behaviorStep, final Behavior behavior) {
		stopStepIfRunning();
	}
}