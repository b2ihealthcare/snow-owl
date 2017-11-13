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

import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.CDORevisionMerger;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Extended {@link CDORevisionMerger CDO revision merger} to handle list
 * addition into a greater index than the actual feature list size.
 */
class CDORevisionMerger2 extends CDORevisionMerger {

	private InternalCDORevision localRevision;

	public synchronized void merge(InternalCDORevision revision, CDORevisionDelta delta) {
		this.localRevision = revision;
		super.merge(revision, delta);
		delta.accept(this);
		revision = null;
	}

	@Override
	public void visit(final CDOAddFeatureDelta delta) {
		final EStructuralFeature feature = delta.getFeature();
		final int deltaIndex = delta.getIndex();
		final int currentFeatureListSize = localRevision.getList(feature).size();
		localRevision.add(feature, Math.min(deltaIndex, currentFeatureListSize), delta.getValue());
	}

}
