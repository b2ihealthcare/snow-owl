/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedOWLExpressionRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 6.14
 */
public abstract class MemberEffectiveTimeRestorer implements IEffectiveTimeRestorer<SnomedRefSetMember> {

	@Override
	public void tryRestoreEffectiveTime(String branchPath, SnomedRefSetMember memberToRestore) {
		final List<String> branchesForPreviousVersion = getAvailableVersionPaths(branchPath);
		SnomedReferenceSetMember previousVersion = null;

		for (String branch : branchesForPreviousVersion) {

			try {

				previousVersion = SnomedRequests.prepareGetMember(memberToRestore.getUuid())
									.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
									.get();

				if (previousVersion != null) {
					break;
				}

			} catch (NotFoundException e) {
				// check next available branch if possible
			}

		}
		
		if (previousVersion == null) {
			throw new IllegalStateException("Previous version of released component could not be found. ID: " + memberToRestore.getUuid() + ", branch: " + branchPath);
		} else {
			boolean canRestore = false;
			canRestore |= !memberToRestore.isActive() ^ previousVersion.isActive();
			canRestore |= memberToRestore.getModuleId().equals(previousVersion.getModuleId());

			if (canRestore && canRestoreMemberEffectiveTime(memberToRestore, previousVersion)) {
				memberToRestore.setEffectiveTime(previousVersion.getEffectiveTime());
			}

		}
	}
	
	/**
	 * Method to compare specific properties on member types eg.: ({@link SnomedAssociationRefSetMember}, {@link SnomedOWLExpressionRefSetMember}, {@link SnomedComplexMapRefSetMember}, etc...).
	 * 
	 * @param memberToRestore
	 * @param previousMember the latest released version of the member above.
	 * @return
	 */
	protected abstract boolean canRestoreMemberEffectiveTime(SnomedRefSetMember memberToRestore, SnomedReferenceSetMember previousMember);
	
}
