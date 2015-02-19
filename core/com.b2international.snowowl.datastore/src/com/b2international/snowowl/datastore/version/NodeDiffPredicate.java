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
package com.b2international.snowowl.datastore.version;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.commons.Change;
import com.b2international.snowowl.datastore.index.diff.NodeDiff;
import com.google.common.base.Predicate;

/**
 * Predicate working on {@link NodeDiff} instances.
 *
 */
public interface NodeDiffPredicate extends Predicate<NodeDiff> {
	
	Predicate<NodeDiff> HAS_CHANGED_PREDICATE = new NodeDiffPredicate() {
		@Override public boolean apply(final NodeDiff nodeDiff) {
			return Change.HAS_CHANGED_PREDICATE.apply(checkNotNull(nodeDiff, "nodeDiff"));
		}
	};
	
	Predicate<NodeDiff> HAS_PARENT_PREDICATE = new NodeDiffPredicate() {
		@Override public boolean apply(final NodeDiff nodeDiff) {
			return null != checkNotNull(nodeDiff, "nodeDiff").getParent();
		}
	};
	
}