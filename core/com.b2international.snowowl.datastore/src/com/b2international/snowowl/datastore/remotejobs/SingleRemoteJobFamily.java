/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.remotejobs;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class SingleRemoteJobFamily implements Predicate<RemoteJob> {

	private final Collection<String> ids;

	public static Predicate<RemoteJob> create(String id) {
		return create(Collections.singleton(id));
	}
	
	public static Predicate<RemoteJob> create(Collection<String> ids) {
		return Predicates.and(RemoteJobFamily.INSTANCE, new SingleRemoteJobFamily(ids));
	}
	
	private SingleRemoteJobFamily(Collection<String> ids) {
		Preconditions.checkNotNull(ids, "Unique identifier set may not be null.");
		this.ids = ids;
	}
	
	@Override
	public boolean apply(RemoteJob input) {
		return ids.contains(input.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(ids);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SingleRemoteJobFamily)) {
			return false;
		}
		SingleRemoteJobFamily other = (SingleRemoteJobFamily) obj;
		return ids.equals(other.ids);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SingleRemoteJobFamily [ids=");
		builder.append(ids);
		builder.append("]");
		return builder.toString();
	}

}