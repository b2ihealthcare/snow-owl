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
package com.b2international.snowowl.core.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Represents history information.
 * 
 */
public interface IHistoryInfo extends Serializable {
	
	/**
	 * Returns the version.
	 * 
	 * @return the version
	 */
	IVersion<?> getVersion();
	
	/**
	 * Returns the timestamp.
	 * 
	 * @return the timestamp
	 */
	long getTimeStamp();
	
	/**
	 * Returns the author.
	 * 
	 * @return the author
	 */
	String getAuthor();
	
	/**
	 * Returns the comments.
	 * 
	 * @return the comments
	 */
	String getComments();
	
	/**
	 * Returns detailed information about the changes.
	 * 
	 * @return the change details
	 */
	List<IHistoryInfoDetails> getDetails();
	
	/**
	 * Returns true when the history information was not filled out completely, false otherwise.
	 * 
	 * @return true when the history information was not filled out completely, false otherwise
	 */
	boolean isIncomplete();
	
	/**
	 * Represents a version in the history.
	 * 
	 *
	 * @param <T>
	 */
	public static interface IVersion<T> extends Comparable<IVersion<?>>, Serializable {
		/**
		 * Empty IVersion implementation.
		 * 
		 */
		static final IVersion<?> EMPTY = new IVersion<Object>() {

			private static final long serialVersionUID = 4167392543094653893L;

			@Override
			public int getMajorVersion() {
				return 0;
			}

			@Override
			public int getMinorVersion() {
				return 0;
			}

			@Override
			public Map<Object, Long> getAffectedObjectIds() {
				return Collections.emptyMap();
			}

			@Override
			public int compareTo(IVersion<?> o) {
				return 0;
			}
			
		};
		
		/**
		 * Returns the major version number.
		 * 
		 * @return the major version number
		 */
		int getMajorVersion();
		
		/**
		 * Returns the minor version number.
		 * 
		 * @return the minor version number
		 */
		int getMinorVersion();
		
		/**
		 * Returns the identifiers of the affected objects.
		 * 
		 * @return the identifiers of the affected objects
		 */
		Map<T, Long> getAffectedObjectIds();
	}
}