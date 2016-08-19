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

import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.datastore.index.ComponentBaseUpdater;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.update.ComponentModuleUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.DescriptionImmutablePropertyUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.DescriptionMutablePropertyUpdater;

/**
 * @since 4.3
 */
public class DescriptionChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> {

	public DescriptionChangeProcessor() {
		super("description changes");
	}

	@Override
	protected void indexDocuments(ICDOCommitChangeSet commitChangeSet) {
		final Iterable<Description> newDescriptions = getNewComponents(commitChangeSet, Description.class);
		for (final Description description : newDescriptions) {
			registerImmutablePropertyUpdates(description);
			registerMutablePropertyUpdates(description);
		}
	}
	
	@Override
	protected void updateDocuments(ICDOCommitChangeSet commitChangeSet) {
		final Iterable<Description> dirtyDescriptions = getDirtyComponents(commitChangeSet, Description.class);
		for (final Description description : dirtyDescriptions) {
			registerMutablePropertyUpdates(description);
		}
	}

	@Override
	protected void deleteDocuments(ICDOCommitChangeSet commitChangeSet) {
		registerDeletions(getDetachedComponents(commitChangeSet, SnomedPackage.Literals.DESCRIPTION));
	}

	private void registerImmutablePropertyUpdates(final Description description) {
		final String id = description.getId();
		registerUpdate(id, new ComponentBaseUpdater<SnomedDocumentBuilder>(id, SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, description.cdoID()));
		registerUpdate(id, new DescriptionImmutablePropertyUpdater(description));
	}

	private void registerMutablePropertyUpdates(Description description) {
		final String id = description.getId();
		registerUpdate(id, new DescriptionMutablePropertyUpdater(description));
		registerUpdate(id, new ComponentModuleUpdater(description));
	}
}
