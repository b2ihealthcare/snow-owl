/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.commit;

import java.util.List;

import com.b2international.snowowl.core.rest.domain.ObjectRestSearch;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 8.0
 */
public class CommitInfoRestSearch extends ObjectRestSearch {

	@Parameter(description = "The author of the commit to match")
	private String author;
	
	@Parameter(description = "Affected component identifier to match")
	private String affectedComponentId;
	
	@Parameter(description = "Commit comment term to match")
	private String comment;
	
	@Parameter(description = "One or more branch paths to match")
	private List<String> branch;
	
	@Parameter(description = "Commit timestamp to match")
	private Long timestamp;
	
	@Parameter(description = "Minimum commit timestamp to search matches from")
	private Long timestampFrom;
	
	@Parameter(description = "Maximum commit timestamp to search matches to")
	private Long timestampTo;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAffectedComponentId() {
		return affectedComponentId;
	}

	public void setAffectedComponentId(String affectedComponentId) {
		this.affectedComponentId = affectedComponentId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<String> getBranch() {
		return branch;
	}

	public void setBranch(List<String> branch) {
		this.branch = branch;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Long getTimestampFrom() {
		return timestampFrom;
	}

	public void setTimestampFrom(Long timestampFrom) {
		this.timestampFrom = timestampFrom;
	}

	public Long getTimestampTo() {
		return timestampTo;
	}

	public void setTimestampTo(Long timestampTo) {
		this.timestampTo = timestampTo;
	}
	
	
	
}
