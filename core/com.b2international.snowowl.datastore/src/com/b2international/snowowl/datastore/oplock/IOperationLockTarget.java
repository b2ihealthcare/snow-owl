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
package com.b2international.snowowl.datastore.oplock;

import java.io.Serializable;

/**
 * Represents an arbitrary object that can be locked to prevent concurrent access to it. Lock targets allow determining
 * eligibility of lock attempts by implementing the method of this interface.
 * 
 */
public interface IOperationLockTarget extends Serializable {

	/**
	 * Checks whether a lock attempt for the specified lock target can potentially be conflicting if a lock for this
	 * target already exists. The method should return {@code false} in case conflict can not be determined.
	 * 
	 * @param other the target to check for conflicts
	 * @return {@code true} if {@code other} conflicts with this lock target, {@code false} otherwise.
	 */
	boolean conflicts(IOperationLockTarget other);
}