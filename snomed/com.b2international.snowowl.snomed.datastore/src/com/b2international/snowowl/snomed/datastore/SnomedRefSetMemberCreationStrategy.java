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
package com.b2international.snowowl.snomed.datastore;

import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * Representation of a new reference set member creation strategy.
 *
 */
public interface SnomedRefSetMemberCreationStrategy {

	/**
	 * Creates an returns with a new transient reference set member instance.
	 * @return the new unpersisted reference set member. 
	 */
	SnomedRefSetMember getRefSetMember();
	
	/**
	 * Returns with the reference set that will be the container of the new reference set member.
	 * @return the reference set for the new member.
	 */
	SnomedRefSet getRefSet();
	
	/**
	 * Returns with the editing context for the new reference set member creation process.
	 * @return the editing context for the member creation process.
	 */
	SnomedEditingContext getEditingContext();

	/**
	 * Creates the new reference set member by attaching it to the underlying transaction.
	 */
	void doCreate();

	
}