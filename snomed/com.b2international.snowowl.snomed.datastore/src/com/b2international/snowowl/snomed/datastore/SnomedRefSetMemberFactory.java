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
 * Representation of a reference set member factory for SNOMED&nbsp;CT.
 *
 */
public interface SnomedRefSetMemberFactory {

	/**
	 * Creates a SNOMED&nbsp;CT reference set member for the given reference set with the editing context argument.
	 * @param context the editing context for the member creation.
	 * @param refSet the reference set for the new member.
	 * @return the new member.
	 */
	SnomedRefSetMember createMember(SnomedEditingContext context, SnomedRefSet refSet);

	/**
	 * Creates a SNOMED&nbsp;CT reference set member for the given reference set with the editing context argument.
	 * @param context the editing context for the member creation.
	 * @param refSet the reference set for the new member.
	 * @return the new member.
	 */
	SnomedRefSetMember createMember(SnomedRefSetEditingContext context, SnomedRefSet refSet);

}