/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Arrays;
import java.util.Objects;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.snowowl.core.validation.rule.ValidationRule.CheckType;

/**
 * @since 6.6
 */
public class ValidationRuleSchedulingRule implements ISchedulingRule {
	
	public static final int MAXIMUM_AMOUNT_OF_CONCURRENT_NORMAL_JOBS = 3;
	
	private final CheckType checkType;
	private final int maxConcurrentJobs;
	private final String uniqueRuleId;
	
	public ValidationRuleSchedulingRule(final CheckType checkType, final int maxConcurrentJobs, final String uniqueRuleId) {
		this.checkType = checkType;
		this.maxConcurrentJobs = maxConcurrentJobs;
		this.uniqueRuleId = uniqueRuleId;
	}

	@Override
	public boolean contains(ISchedulingRule rule) {
		return equals(rule);
	}
	
	@Override
	public boolean isConflicting(ISchedulingRule schedulingRule) {
		if (schedulingRule instanceof ValidationRuleSchedulingRule) {
			final ValidationRuleSchedulingRule other = (ValidationRuleSchedulingRule) schedulingRule;
			final IJobManager manager = Job.getJobManager();
			
			long runningExpensiveJobs = Arrays.asList(manager.find(CheckType.EXPENSIVE.getName())).stream().filter(job -> job.getState() == Job.RUNNING).count();
			long runningNormalJobs = Arrays.asList(manager.find(CheckType.NORMAL.getName())).stream().filter(job -> job.getState() == Job.RUNNING).count();
			long runningFastJobs = Arrays.asList(manager.find(CheckType.FAST.getName())).stream().filter(job -> job.getState() == Job.RUNNING).count();
			
			long allRunningValidationJobs = runningExpensiveJobs + runningFastJobs + runningNormalJobs;
			
			if (allRunningValidationJobs >= maxConcurrentJobs) {
				return true;
			}
			
			if (CheckType.EXPENSIVE == checkType || CheckType.EXPENSIVE == other.checkType  || runningExpensiveJobs != 0) {
				return true;
			}
			
			if ((CheckType.NORMAL == checkType || CheckType.NORMAL == other.checkType) && runningNormalJobs >= MAXIMUM_AMOUNT_OF_CONCURRENT_NORMAL_JOBS) {
				return true;
			}
			
			
		}
		
		if (equals(schedulingRule)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + Objects.hash(checkType);
		hash = hash * 31 + maxConcurrentJobs;
		hash = hash * 31 + Objects.hash(uniqueRuleId); 
		
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ValidationRuleSchedulingRule other = (ValidationRuleSchedulingRule) obj;
		return Objects.equals(this.checkType, other.checkType) && Objects.equals(uniqueRuleId, other.uniqueRuleId);
	}
	
	@Override
	public String toString() {
		return uniqueRuleId;
	}

}
