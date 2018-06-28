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

import java.util.UUID;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.validation.rule.ValidationRule.CheckType;

/**
 * @since 6.0
 */
public final class ValidationThreadPool {
	
	private final int maxValidationThreadCount;

	public ValidationThreadPool(final int maxValidationThreadCount) {
		this.maxValidationThreadCount = maxValidationThreadCount;
	}
	
	public Promise<Object> submit(CheckType checkType, Runnable runnable) {
		final Job job = new ValidationJob(checkType.getName(), runnable);
		String uniqueRuleId = UUID.randomUUID().toString();
		final ISchedulingRule schedulingRule = new ValidationRuleSchedulingRule(checkType, maxValidationThreadCount, uniqueRuleId);
		final Promise<Object> isRuleDonePromise = new Promise<>();
		
		job.setSystem(true);
		job.setRule(schedulingRule);
		job.addJobChangeListener(new JobChangeAdapter() {
			
			public void done(IJobChangeEvent event) {
					isRuleDonePromise.resolve(event.getResult().isOK());
					
			};
			
		});
		job.schedule();
		return isRuleDonePromise;
	}
	
}
