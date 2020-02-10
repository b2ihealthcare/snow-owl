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
package com.b2international.snowowl.core.rest.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * @since 1.0
 */
public class ResourceRequest<T> {

	private T change;
	private String commitComment;

	@JsonUnwrapped
	public T getChange() {
		return change;
	}

	public String getCommitComment() {
		return commitComment;
	}

	public void setChange(final T change) {
		this.change = change;
	}

	public void setCommitComment(final String commitComment) {
		this.commitComment = commitComment;
	}

	@Override
	public final String toString() {
		ToStringHelper toStringHelper = MoreObjects.toStringHelper(getClass()).add("change", change).add("commitComment", commitComment);
		doToString(toStringHelper);
		return toStringHelper.toString();
	}

	protected void doToString(ToStringHelper toStringHelper) {
	}
	
}