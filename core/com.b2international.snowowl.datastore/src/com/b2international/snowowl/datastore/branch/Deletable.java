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
package com.b2international.snowowl.datastore.branch;

/**
 * @since 4.1
 */
public interface Deletable {

	/**
	 * @return whether this object is deleted or not
	 */
	boolean isDeleted();

	/**
	 * Deletes this object. Calling {@link #isDeleted()} on the returned object should return <code>true</code> after a call to this method.
	 * @return 
	 */
	Deletable delete();
}
