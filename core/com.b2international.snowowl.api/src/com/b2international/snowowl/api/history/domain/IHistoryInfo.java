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
package com.b2international.snowowl.api.history.domain;

import java.util.List;


/**
 * Represents a history information element. This element provides detailed information about some 
 * historical modifications made on an element in the past.
 * 
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link IHistoryInfo#getVersion() <em>Version</em>}</li>
 *   <li>{@link IHistoryInfo#getTimestamp() <em>Timestamp</em>}</li>
 *   <li>{@link IHistoryInfo#getAuthor() <em>Author</em>}</li>
 *   <li>{@link IHistoryInfo#getComments() <em>Comments</em>}</li>
 *   <li>{@link IHistoryInfo#getDetails() <em>History information details</em>}</li>
 * </ul>
 * </p>
 * @see IHistoryVersion
 * @see IHistoryInfoDetails 
 */
public interface IHistoryInfo {

	/**
	 * Returns with the version of the historical modification.
	 * @return the version.
	 * @see IHistoryInfo
 	 */
	IHistoryVersion getVersion();
	
	/**
	 * Returns with the timestamp of the historical modification, expressed 
	 * in milliseconds since January 1, 1970, 00:00:00 GMT.
	 * @return the timestamp.
	 * @see IHistoryInfo
	 */
	long getTimestamp();

	/**
	 * Returns with the name of the author.
	 * @return the name of the author.
	 * @see IHistoryInfo
	 */
	String getAuthor();

	/**
	 * Returns with the comment entered by the author, which may provide additional details about the modification.
	 * @return the author's comment associated with the historical modification.
	 * @see IHistoryInfo
	 */
	String getComments();

	/**
	 * Returns with a {@link List list} of {@link IHistoryInfoDetails detailed information} about the historical modification.
	 * @return a {@link List} of detailed history information.
	 * @see IHistoryInfo
	 */
	List<IHistoryInfoDetails> getDetails();
}