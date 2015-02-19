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
package com.b2international.snowowl.datastore.server.cdo;

import java.util.Map;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.datastore.cdo.ConflictWrapper;

/**
 * Null conflict processor. Does nothing.
 */
public enum NullCDOConflictProcessor implements ICDOConflictProcessor {

	/**The singleton instance without implementation.*/
	INSTANCE;

	@Override 
	public void detachConflictingObject(final CDOObject objectToRemove) { 
		return;
	}

	@Override 
	public ConflictWrapper checkConflictForNewObjects(final CDOChangeSetData targetChangeSet, final CDOIDAndVersion newInSource, final CDOView sourceView) { 
		return null; 
	}
	
	@Override
	public ConflictWrapper checkConflictForDetachedObjects(final Map<CDOID, CDORevisionKey> changedComponentsMapping, 
			final CDOIDAndVersion detachedOnSource, final CDOView sourceView, final CDOView targetView) {
		
		return null;
	}
	
	@Override
	public String getRepositoryUuid() {
		throw new UnsupportedOperationException("Implementation error.");
	}
}