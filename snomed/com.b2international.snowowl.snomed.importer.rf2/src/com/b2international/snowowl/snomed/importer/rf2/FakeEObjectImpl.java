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
package com.b2international.snowowl.snomed.importer.rf2;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;

/**
 * @since 4.3
 */
public class FakeEObjectImpl extends BasicEObjectImpl implements CDOObject {

	private long storageKey;
	
	public FakeEObjectImpl(long storageKey) {
		this.storageKey = storageKey;
	}

	@Override
	public CDOID cdoID() {
		return CDOIDUtil.createLong(storageKey);
	}

	@Override
	public CDOState cdoState() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean cdoConflict() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean cdoInvalid() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CDOView cdoView() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CDORevision cdoRevision() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CDOResource cdoResource() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CDOResource cdoDirectResource() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CDOLock cdoReadLock() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CDOLock cdoWriteLock() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CDOLock cdoWriteOption() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CDOLockState cdoLockState() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cdoPrefetch(int depth) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cdoReload() {
		throw new UnsupportedOperationException();
	}

}
