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

import javax.annotation.Nullable;

/**
 * Implementation of a {@link FeatureChange feature change}.
 *
 */
public class FeatureChangeImpl implements FeatureChange {

	private static final long serialVersionUID = -8324782501933260485L;
	private final String featureName;
	private final String fromValue;
	private final String toValue;

	public static FeatureChange createFeatureChange(@Nullable final String featureName, @Nullable final String fromValue, @Nullable final String toValue) {
		return new FeatureChangeImpl(featureName, fromValue, toValue);
	}
	
	public static FeatureChange createFromFeatureChange(@Nullable final String featureName, final String fromValue) {
		return createFeatureChange(featureName, fromValue, null);
	}
	
	public static FeatureChange createToFeatureChange(@Nullable final String featureName, final String toValue) {
		return createFeatureChange(featureName, null, toValue);
	}

	private FeatureChangeImpl(@Nullable final String featureName, @Nullable final String fromValue, @Nullable final String toValue) {
		this.featureName = featureName;
		this.fromValue = fromValue;
		this.toValue = toValue;
	}

	
	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public String getFromValue() {
		return fromValue;
	}

	@Override
	public String getToValue() {
		return toValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((featureName == null) ? 0 : featureName.hashCode());
		result = prime * result + ((fromValue == null) ? 0 : fromValue.hashCode());
		result = prime * result + ((toValue == null) ? 0 : toValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FeatureChangeImpl))
			return false;
		final FeatureChangeImpl other = (FeatureChangeImpl) obj;
		if (featureName == null) {
			if (other.featureName != null)
				return false;
		} else if (!featureName.equals(other.featureName))
			return false;
		if (fromValue == null) {
			if (other.fromValue != null)
				return false;
		} else if (!fromValue.equals(other.fromValue))
			return false;
		if (toValue == null) {
			if (other.toValue != null)
				return false;
		} else if (!toValue.equals(other.toValue))
			return false;
		return true;
	}

}