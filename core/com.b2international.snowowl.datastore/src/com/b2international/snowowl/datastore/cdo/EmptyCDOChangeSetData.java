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
package com.b2international.snowowl.datastore.cdo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;

/**
 * Empty {@link CDOChangeSetData change set data} representation.
 */
public class EmptyCDOChangeSetData implements CDOChangeSetData {

	public static CDOChangeSetData INSTANCE = new EmptyCDOChangeSetData();
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeKindProvider#getChangeKind(org.eclipse.emf.cdo.common.id.CDOID)
	 */
	@Override
	public CDOChangeKind getChangeKind(final CDOID id) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#copy()
	 */
	@Override
	public CDOChangeSetData copy() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#merge(org.eclipse.emf.cdo.common.commit.CDOChangeSetData)
	 */
	@Override
	public void merge(final CDOChangeSetData changeSetData) {

	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#getNewObjects()
	 */
	@Override
	public List<CDOIDAndVersion> getNewObjects() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#getChangedObjects()
	 */
	@Override
	public List<CDORevisionKey> getChangedObjects() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#getDetachedObjects()
	 */
	@Override
	public List<CDOIDAndVersion> getDetachedObjects() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.common.commit.CDOChangeSetData#getChangeKinds()
	 */
	@Override
	public Map<CDOID, CDOChangeKind> getChangeKinds() {
		return Collections.emptyMap();
	}

	private EmptyCDOChangeSetData() { /*suppress instantiation*/ }

}