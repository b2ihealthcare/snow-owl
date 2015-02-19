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
package com.b2international.snowowl.snomed.reasoner.server.classification;

import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;

/**
 * <em>Note:</em> setter methods must be synchronized externally, as atomic behavior must be kept for multiple consecutive calls.
 * 
 */
public class ReasonerStateMachine implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private volatile ReasonerState state;
	
	public ReasonerStateMachine(final ReasonerState initialState) {
		this.state = initialState;
	}
	
	public ReasonerState getState() {
		return state;
	}

	public void beginClassification() {
		checkState(notClassifying());
		this.state = ReasonerState.CLASSIFYING;
	}
	
	public void setStale() {
		checkState(notClassifying());
		if (ReasonerState.SYNCHRONIZED.equals(state)) {
			this.state = ReasonerState.IDLE;
		}
	}

	private boolean notClassifying() {
		return !ReasonerState.CLASSIFYING.equals(state);
	}
	
	public void endClassification() {
		checkState(state.oneOf(ReasonerState.CLASSIFYING));
		this.state = ReasonerState.SYNCHRONIZED;
	}

	public void fail() {
		this.state = ReasonerState.FAILED;
	}
	
	public void unload() {
		this.state = ReasonerState.UNLOADED;
	}
}