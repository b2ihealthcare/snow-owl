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


/**
 * Represents a version with a major and a minor version number.
 * <p>
 * Successive edits of a component either increment the minor version by one, or reset it to zero and increment the
 * major version number instead, depending on extent the of the change.
 */
public interface IHistoryVersion extends Comparable<IHistoryVersion> {

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
}
