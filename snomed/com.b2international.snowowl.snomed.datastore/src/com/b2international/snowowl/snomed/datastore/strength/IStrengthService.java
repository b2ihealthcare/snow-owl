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
package com.b2international.snowowl.snomed.datastore.strength;

import java.util.Collection;

import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * Service for extracting strength information from {@link SnomedRefSetMemberIndexEntry}s.
 * 
 */
public interface IStrengthService {

	/**
	 * Returns a collection of {@link StrengthEntry} for the specified collection of {@link SnomedRefSetMemberIndexEntry}s
	 * 
	 * @param entries
	 * @return
	 */
	Collection<StrengthEntry> getStrengths(Collection<SnomedReferenceSetMember> entries);
}