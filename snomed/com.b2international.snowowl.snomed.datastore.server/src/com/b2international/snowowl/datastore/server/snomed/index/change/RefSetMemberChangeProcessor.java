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
package com.b2international.snowowl.datastore.server.snomed.index.change;

import java.util.Collection;

import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberImmutablePropertyUpdater;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberMutablePropertyUpdater;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

/**
 * @since 4.3
 */
public class RefSetMemberChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> {

	public RefSetMemberChangeProcessor() {
		super("reference set member changes");
	}

	@Override
	protected void indexDocuments(ICDOCommitChangeSet commitChangeSet) {
		final Iterable<SnomedRefSetMember> newRefSetMembers = getNewComponents(commitChangeSet, SnomedRefSetMember.class);
		for (SnomedRefSetMember newMember : newRefSetMembers) {
			registerImmutablePropertyUpdates(newMember);
			registerMutablePropertyUpdates(newMember);
		}
	}
	
	@Override
	protected void updateDocuments(ICDOCommitChangeSet commitChangeSet) {
		final Iterable<SnomedRefSetMember> dirtyRefSetMembers = getDirtyComponents(commitChangeSet, SnomedRefSetMember.class);
		final Collection<CDOID> deletedRefSetMembers = getDeletedMembers(commitChangeSet); 
		for (SnomedRefSetMember dirtyMember : dirtyRefSetMembers) {
			// skip any deleted member from dirty ones, fixes problem with revert
			if (!deletedRefSetMembers.contains(dirtyMember.cdoID())) {
				registerMutablePropertyUpdates(dirtyMember);
			}
		}
	}
	
	@Override
	protected void deleteDocuments(ICDOCommitChangeSet commitChangeSet) {
		registerDeletions(getDeletedMembers(commitChangeSet));
	}

	private Collection<CDOID> getDeletedMembers(ICDOCommitChangeSet commitChangeSet) {
		return getDetachedComponents(commitChangeSet, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER);
	}

	private void registerMutablePropertyUpdates(SnomedRefSetMember member) {
		registerUpdate(member.getUuid(), new RefSetMemberMutablePropertyUpdater(member));
	}
	
	private void registerImmutablePropertyUpdates(SnomedRefSetMember member) {
		registerUpdate(member.getUuid(), new RefSetMemberImmutablePropertyUpdater(member));
	}
	
}
