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

import static com.b2international.commons.ChangeKind.UNCHANGED;
import static com.b2international.commons.StringUtils.EMPTY_STRING;
import static com.b2international.snowowl.core.CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;

import java.io.Serializable;

import com.b2international.commons.Change;
import com.b2international.commons.ChangeKind;
import com.b2international.snowowl.core.api.component.LabelProvider;
import com.b2international.snowowl.core.api.component.TerminologyComponentIdProvider;

/**
 * Represents a node delta for the version compare.
 *
 */
public interface NodeDelta extends TerminologyComponentIdProvider, Change, Serializable, FeatureChange, LabelProvider {
	
	/**Null node delta implementation.*/
	public static final NodeDelta NULL_IMPL = new NodeDelta() {

		private static final long serialVersionUID = 7469910156220270227L;

		@Override
		public boolean isNew() {
			return getChange().isNew();
		}
		
		@Override
		public boolean isDirty() {
			return getChange().isDirty();
		}
		
		@Override
		public boolean isDeleted() {
			return getChange().isDeleted();
		}
		
		@Override
		public boolean hasChanged() {
			return getChange().hasChanged();
		}
		
		@Override
		public ChangeKind getChange() {
			return UNCHANGED;
		}
		
		@Override
		public short getTerminologyComponentId() {
			return UNSPECIFIED_NUMBER_SHORT;
		}
		
		@Override
		public String getFeatureName() {
			return EMPTY_STRING;
		}
		
		@Override
		public String getFromValue() {
			return EMPTY_STRING;
		}
		
		@Override
		public String getToValue() {
			return EMPTY_STRING;
		}
		
		@Override
		public String getLabel() {
			return EMPTY_STRING;
		};
		
		@Override
		public int hashCode() {
			return 0;
		};
		
		@Override
		public boolean equals(final Object obj) {
			return NULL_IMPL == obj;
		};
		
	};
	
}