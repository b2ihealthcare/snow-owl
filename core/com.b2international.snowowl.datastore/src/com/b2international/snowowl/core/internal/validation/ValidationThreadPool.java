/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal.validation;

import org.eclipse.core.internal.jobs.JobManager;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.validation.rule.ValidationRule.CheckType;

/**
 * @since 6.0
 */
public final class ValidationThreadPool {

	public ValidationThreadPool(int nThreads) {
	}
	
	public Promise<Boolean> submit(CheckType checkType, Runnable runnable) {
		final ValidationJob job = new ValidationJob(checkType.getName(), runnable);
		final ValidationRuleSchedulingRule schedulingRule = new ValidationRuleSchedulingRule(checkType);
		job.setSystem(true);
		job.setRule(schedulingRule);
		job.schedule();
		final JobManager manager = (JobManager) Job.getJobManager();
		Job[] find = manager.find(checkType.getName());
		return Promise.immediate(Boolean.TRUE);
	}
	
}
