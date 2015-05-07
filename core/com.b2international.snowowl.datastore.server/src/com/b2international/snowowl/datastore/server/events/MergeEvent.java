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
package com.b2international.snowowl.datastore.server.events;

import com.google.common.base.Strings;

/**
 * @since 4.1
 */
public class MergeEvent extends BaseBranchEvent {

	private final String source;
	private final String target;
	private final String commitMessage;

	public MergeEvent(final String repositoryId, final String source, final String target, final String commitMessage) {
		super(repositoryId);
		this.source = source;
		this.target = target;
		this.commitMessage = Strings.isNullOrEmpty(commitMessage) ? defaultMessage() : commitMessage;
	}

	private String defaultMessage() {
		return String.format("Merge branch '%s' into '%s'", getSource(), getTarget());
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}
}
