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
package com.b2international.snowowl.datastore.index;

import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;

/**
 * Adapter class for the {@link CDOFeatureDeltaVisitor} interface.
 */
public abstract class CDOFeatureDeltaVisitorAdapter implements CDOFeatureDeltaVisitor {

	@Override
	public void visit(CDOMoveFeatureDelta delta) {
		visitDelta(delta);
	}

	@Override
	public void visit(CDOAddFeatureDelta delta) {
		visitDelta(delta);
	}

	@Override
	public void visit(CDORemoveFeatureDelta delta) {
		visitDelta(delta);
	}

	@Override
	public void visit(CDOSetFeatureDelta delta) {
		visitDelta(delta);
	}

	@Override
	public void visit(CDOUnsetFeatureDelta delta) {
		visitDelta(delta);
	}

	@Override
	public void visit(CDOListFeatureDelta delta) {
		visitDelta(delta);
	}

	@Override
	public void visit(CDOClearFeatureDelta delta) {
		visitDelta(delta);
	}

	@Override
	public void visit(CDOContainerFeatureDelta delta) {
		visitDelta(delta);
	}
	
	protected abstract void visitDelta(CDOFeatureDelta delta);

}
