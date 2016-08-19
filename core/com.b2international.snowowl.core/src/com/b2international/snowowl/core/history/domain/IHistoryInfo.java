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
package com.b2international.snowowl.core.history.domain;

import java.util.List;

/**
 * Contains the change version number, the commit time, author and comments along with detailed changes for a single
 * commit.
 */
public interface IHistoryInfo {

	/**
	 * Returns the change version of this commit.
	 * 
	 * @return the version
	 */
	IHistoryVersion getVersion();

	/**
	 * Returns the timestamp of this commit.
	 * 
	 * @return the commit timestamp
	 */
	long getTimestamp();

	/**
	 * Returns the name of the author of this commit.
	 * 
	 * @return the author name
	 */
	String getAuthor();

	/**
	 * Returns the comment entered by the author for this commit, which may provide additional details about the modification.
	 * 
	 * @return the author's commit comment
	 */
	String getComments();

	/**
	 * Returns the list of {@link IHistoryInfoDetails detailed changes} contained within this commit.
	 * 
	 * @return the list of changes
	 */
	List<IHistoryInfoDetails> getDetails();
}
