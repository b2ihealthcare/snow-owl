/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.remotejobs;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.snowowl.datastore.remotejobs.RemoteJobFamily;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 */
public class RemoteJobRuleFamily implements Predicate<Job> {

	private final ISchedulingRule rule;

	public static Predicate<Job> create(final ISchedulingRule rule) {
		return Predicates.and(RemoteJobFamily.INSTANCE, new RemoteJobRuleFamily(rule));
	}

	private RemoteJobRuleFamily(final ISchedulingRule rule) {
		Preconditions.checkNotNull(rule, "Scheduling rule may not be null.");
		this.rule = rule;
	}

	public ISchedulingRule getRule() {
		return rule;
	}

	@Override
	public boolean apply(final Job input) {
		return rule.equals(input.getRule());
	}

	@Override
	public int hashCode() {
		return 31 + rule.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof RemoteJobRuleFamily)) { return false; }
		final RemoteJobRuleFamily other = (RemoteJobRuleFamily) obj;
		return rule.equals(other.rule);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("rule", rule).toString();
	}
}
