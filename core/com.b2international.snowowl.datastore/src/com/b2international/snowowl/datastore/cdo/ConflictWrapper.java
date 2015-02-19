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

import static com.b2international.commons.ChangeKind.UPDATED;

import java.io.Serializable;

import org.eclipse.emf.cdo.common.id.CDOID;

/**
 * Class to wrap a conflict that may occur when comparing change sets. Wraps the conflicting changes that happened on the main branch, and the task branch.
 *
 */
public class ConflictWrapper implements Serializable {
	
	private static final long serialVersionUID = -758590804279579157L;
	
	private final ConflictingChange changeOnTarget;
	private final ConflictingChange changeOnSource;
	
	protected static ConflictingChange createChange(final CDOID cdoId) {
		return new ConflictingChange(UPDATED, cdoId);
	}

	public ConflictWrapper(final ConflictingChange changeOnTarget, final ConflictingChange changeOnSource) {
		this.changeOnTarget = changeOnTarget;
		this.changeOnSource = changeOnSource;
	}
	
	@Override
	public String toString() {
		return changeOnTarget + " -> " + changeOnSource;
	}

	public ConflictingChange getChangeOnTarget() {
		return changeOnTarget;
	}
	
	public ConflictingChange getChangeOnSource() {
		return changeOnSource;
	}
}