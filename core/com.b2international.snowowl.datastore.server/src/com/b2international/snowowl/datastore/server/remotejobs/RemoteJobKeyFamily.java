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
package com.b2international.snowowl.datastore.server.remotejobs;

import org.eclipse.core.runtime.jobs.Job;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 */
public class RemoteJobKeyFamily implements Predicate<Job> {

	private final RemoteJobKey key;

	public static Predicate<Job> create(final RemoteJobKey key) {
		return Predicates.and(RemoteJobFamily.INSTANCE, new RemoteJobKeyFamily(key));
	}
	
	private RemoteJobKeyFamily(final RemoteJobKey key) {
		Preconditions.checkNotNull(key, "Remote job key may not be null.");
		this.key = key;
	}
	
	public RemoteJobKey getKey() {
		return key;
	}

	@Override
	public boolean apply(final Job input) {
		return key.equals(input.getRule());
	}

	@Override
	public int hashCode() {
		return 31 + key.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RemoteJobKeyFamily)) {
			return false;
		}
		final RemoteJobKeyFamily other = (RemoteJobKeyFamily) obj;
		return key.equals(other.key);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RemoteJobKeyFamily [key=");
		builder.append(key);
		builder.append("]");
		return builder.toString();
	}
}