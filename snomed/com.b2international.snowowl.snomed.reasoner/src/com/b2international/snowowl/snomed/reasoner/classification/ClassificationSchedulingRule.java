/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.classification;

import java.util.Objects;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.b2international.snowowl.datastore.remotejobs.SerializableSchedulingRule;

/**
 * @since 7.0
 */
public final class ClassificationSchedulingRule implements SerializableSchedulingRule {

	private static int nextId = 0;

	private final int id;
	private final String repositoryId;
	private final String branch;

	public static ClassificationSchedulingRule create(final int concurrentClassifications, final String repositoryId, final String branch) {
		return new ClassificationSchedulingRule((++nextId % concurrentClassifications), repositoryId, branch);
	}

	private ClassificationSchedulingRule(final int id, final String repositoryId, final String branch) {
		this.id = id;
		this.repositoryId = repositoryId;
		this.branch = branch;
	}

	@Override
	public boolean contains(final ISchedulingRule rule) {
		return equals(rule);
	}

	@Override
	public boolean isConflicting(final ISchedulingRule rule) {
		if (getClass() != rule.getClass()) { return false; }
		final ClassificationSchedulingRule other = (ClassificationSchedulingRule) rule;

		// If the IDs are the same, the number of concurrent classifications limit has been reached
		if (id == other.id) { return true; }

		// Otherwise, both branch and repositoryId must match for a conflict
		if (!Objects.equals(branch, other.branch)) { return false; }
		if (!Objects.equals(repositoryId, other.repositoryId)) { return false; }

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, branch, repositoryId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final ClassificationSchedulingRule other = (ClassificationSchedulingRule) obj;
		if (id != other.id) { return false; }
		if (!Objects.equals(branch, other.branch)) { return false; }
		if (!Objects.equals(repositoryId, other.repositoryId)) { return false; }

		return true;
	}
}
