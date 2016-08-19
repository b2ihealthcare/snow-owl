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
package com.b2international.snowowl.api.impl.history.domain;

import java.util.List;

import com.b2international.snowowl.core.history.domain.IHistoryInfo;
import com.b2international.snowowl.core.history.domain.IHistoryInfoDetails;
import com.b2international.snowowl.core.history.domain.IHistoryVersion;

/**
 *
 */
public class HistoryInfo implements IHistoryInfo {

	private final IHistoryVersion version;
	private final long timestamp;
	private final String author;
	private final String comments;
	private final List<IHistoryInfoDetails> details;

	/**
	 * @param version
	 * @param timestamp
	 * @param author
	 * @param comments
	 * @param details
	 */
	public HistoryInfo(final IHistoryVersion version, final long timestamp, final String author, final String comments, final List<IHistoryInfoDetails> details) {
		this.version = version;
		this.timestamp = timestamp;
		this.author = author;
		this.comments = comments;
		this.details = details;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.rest.api.domain.IHistoryInfo#getVersion()
	 */
	@Override
	public IHistoryVersion getVersion() {
		return version;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.rest.api.domain.IHistoryInfo#getTimestamp()
	 */
	@Override
	public long getTimestamp() {
		return timestamp;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.rest.api.domain.IHistoryInfo#getAuthor()
	 */
	@Override
	public String getAuthor() {
		return author;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.rest.api.domain.IHistoryInfo#getComments()
	 */
	@Override
	public String getComments() {
		return comments;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.rest.api.domain.IHistoryInfo#getDetails()
	 */
	@Override
	public List<IHistoryInfoDetails> getDetails() {
		return details;
	}
}