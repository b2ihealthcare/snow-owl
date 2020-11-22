/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.jobs;

import java.util.Arrays;

import com.b2international.commons.exceptions.BadRequestException;
import com.google.common.collect.Lists;

/**
 * @since 6.0
 */
public enum RemoteJobState {
	
	SCHEDULED, RUNNING, CANCEL_REQUESTED, FINISHED, FAILED, CANCELED;

	public boolean oneOf(RemoteJobState firstState, RemoteJobState... restStates) {
		return Lists.asList(firstState, restStates).contains(this);
	}

	/**
	 * @param stateValue - the raw state name value
	 * @return the first matching {@link RemoteJobState} value ignoring case, or <code>null</code> if the given stateValue is <code>null</code>
	 * @throws BadRequestException - if the input is not <code>null</code> and there is no matching {@link RemoteJobState} enum literal can be found. 
	 */
	public static RemoteJobState valueOfIgnoreCase(String stateValue) {
		if (stateValue == null) {
			return null;
		}
		for (RemoteJobState state : values()) {
			if (state.name().equalsIgnoreCase(stateValue)) {
				return state;
			}
		}
		throw new BadRequestException("Unrecognized job state value '%s'.", stateValue).withDeveloperMessage("Accepted values are: " + Arrays.toString(values()).toLowerCase());
	}
}