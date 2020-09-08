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
package com.b2international.snowowl.core.request;

import java.io.Serializable;

import com.b2international.commons.ClassUtils;

/**
 * @since 4.5
 */
public final class CommitResult implements Serializable {

	private static final long serialVersionUID = -7479022976959306232L;
	
	private final Long commitTimestamp;
	private final Object result;

	CommitResult(Long commitTimestamp, Object result) {
		this.commitTimestamp = commitTimestamp;
		this.result = result;
	}
	
	public Long getCommitTimestamp() {
		return commitTimestamp;
	}
	
	public Object getResult() {
		return result;
	}

	public <T> T getResultAs(Class<T> type) {
		return ClassUtils.checkAndCast(result, type);
	}
	
}
