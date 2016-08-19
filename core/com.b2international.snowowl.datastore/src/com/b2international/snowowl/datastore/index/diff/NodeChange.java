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
package com.b2international.snowowl.datastore.index.diff;

import static com.b2international.commons.StringUtils.EMPTY_STRING;
import static java.util.Collections.emptySet;

import java.io.Serializable;
import java.util.Collection;

import com.b2international.snowowl.core.api.component.LabelProvider;

/**
 * Represents a node change.
 *
 */
public interface NodeChange extends Serializable, LabelProvider {

	/**
	 * Returns with a collection of {@link NodeDelta deltas} for the current {@link NodeChange change}.
	 * @return a collection of deltas.
	 */
	Collection<NodeDelta> getDeltas();
	
	/**
	 * Returns with the underlying node's ID, or code.
	 * @return the node ID. (Or code.)
	 */
	String getNodeId();
	
	/**Null implementation. Always supplies an empty collection of {@link NodeDelta deltas}.*/
	NodeChange NULL_IMPL = new NodeChange() {
		private static final long serialVersionUID = -8974045285635376236L;
		public Collection<NodeDelta> getDeltas() {
			return emptySet();
		}
		public String getLabel() {
			return EMPTY_STRING;
		}
		public String getNodeId() {
			return EMPTY_STRING;
		}
	};
	
}