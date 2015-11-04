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

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.commons.Change;
import com.b2international.commons.ChangeKind;
import com.b2international.snowowl.core.api.component.LabelProvider;
import com.b2international.snowowl.core.api.component.TerminologyComponentIdProvider;

/**
 * {@link NodeDelta Node delta} implementation.
 *
 */
public class NodeDeltaImpl implements NodeDelta {

	private static final long serialVersionUID = -6372068627434074245L;

	private final String label;
	private final FeatureChange featureChange;
	private final short terminologyComponentId;
	private final ChangeKind change;


	public NodeDeltaImpl(final LabelProvider labelProvider, final FeatureChange featureChange, final TerminologyComponentIdProvider idProvider, final Change change) {
		this(
			checkNotNull(labelProvider, "labelProvider").getLabel(),
			checkNotNull(featureChange, "featureChange"),
			checkNotNull(idProvider, "idProvider").getTerminologyComponentId(),
			checkNotNull(change, "change").getChange());
	}
	
	public NodeDeltaImpl(final String label, final FeatureChange featureChange, final short terminologyComponentId, final Change change) {
		this.label = checkNotNull(label, "label");
		this.featureChange = checkNotNull(featureChange, "featureChange");
		this.terminologyComponentId = terminologyComponentId;
		this.change = checkNotNull(change, "change").getChange();
	}

	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public short getTerminologyComponentId() {
		return terminologyComponentId;
	}

	@Override
	public boolean isDirty() {
		return change.isDirty();
	}

	@Override
	public boolean isNew() {
		return change.isNew();
	}

	@Override
	public boolean isDeleted() {
		return change.isDeleted();
	}

	@Override
	public boolean hasChanged() {
		return change.hasChanged();
	}

	@Override
	public ChangeKind getChange() {
		return change;
	}

	@Override
	public String getFeatureName() {
		return featureChange.getFeatureName();
	}

	@Override
	public String getFromValue() {
		return featureChange.getFromValue();
	}

	@Override
	public String getToValue() {
		return featureChange.getToValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((change == null) ? 0 : change.hashCode());
		result = prime * result + ((featureChange == null) ? 0 : featureChange.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + terminologyComponentId;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NodeDeltaImpl))
			return false;
		final NodeDeltaImpl other = (NodeDeltaImpl) obj;
		if (change != other.change)
			return false;
		if (featureChange == null) {
			if (other.featureChange != null)
				return false;
		} else if (!featureChange.equals(other.featureChange))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (terminologyComponentId != other.terminologyComponentId)
			return false;
		return true;
	}

}