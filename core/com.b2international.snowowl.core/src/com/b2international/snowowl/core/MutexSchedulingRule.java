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
package com.b2international.snowowl.core;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Scheduling rule to make sure that jobs scheduled with
 * this rule cannot be executed parallel.
 *
 */
public class MutexSchedulingRule implements ISchedulingRule {

	private final Object object;

	public MutexSchedulingRule(Object object) {
		this.object = object;
	}
	
	public Object getObject() {
		return object;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	public boolean isConflicting(ISchedulingRule rule) {
		return rule instanceof MutexSchedulingRule  && ((MutexSchedulingRule) rule).object == object;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	public boolean contains(ISchedulingRule rule) {
		return isConflicting(rule);
	}
}