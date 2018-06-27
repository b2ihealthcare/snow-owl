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

import java.util.Objects;

import org.eclipse.core.internal.jobs.JobManager;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.snowowl.core.validation.rule.ValidationRule.CheckType;

/**
 * @since 6.6
 */
@SuppressWarnings("restriction")
public class ValidationRuleSchedulingRule implements ISchedulingRule {
	
	private final CheckType checkType;
	
	public ValidationRuleSchedulingRule(CheckType checkType) {
		this.checkType = checkType;
	}

	@Override
	public boolean contains(ISchedulingRule rule) {
		return equals(rule);
	}
	
	@Override
	public boolean isConflicting(ISchedulingRule schedulingRule) {
		if (this.equals(schedulingRule)) {
			return true;
		}
		
		if (schedulingRule instanceof ValidationRuleSchedulingRule) {
			final ValidationRuleSchedulingRule rule = (ValidationRuleSchedulingRule) schedulingRule;
			final JobManager manager = (JobManager) Job.getJobManager();
			if (CheckType.NORMAL == checkType) {
				return manager.find(checkType.getName()).length >= 4;
			}
			
			if (CheckType.EXPENSIVE == checkType || CheckType.EXPENSIVE == rule.checkType) {
				return true;
			}
			
			if (CheckType.FAST == checkType || CheckType.FAST == rule.checkType) {
				return false;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(checkType);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ValidationRuleSchedulingRule other = (ValidationRuleSchedulingRule) obj;
		return Objects.equals(this.checkType, other.checkType);
	}

}
