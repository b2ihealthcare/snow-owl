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

import static java.text.MessageFormat.format;

import java.io.Serializable;

import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.snowowl.core.ComponentTypeNameCache;
import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Customized conflict wrapper indicating that an application conflict is due to a deleted component
 * that has been released on a target branch.
 *
 */
public class AlreadyReleasedConflictWrapper extends ConflictWrapper implements Serializable, CustomConflictWrapper {

	private static final String MESSAGE_TEMPLATE = "{0} ''{1}'' cannot be deleted because it has been published on ''{2}''.";
	private static final long serialVersionUID = -786089008930873072L;

	private final String message;

	public AlreadyReleasedConflictWrapper(final CDOID cdoId, final short terminologyComponentId, final String label, final IBranchPath branchPath) {
		super(createChange(cdoId), createChange(cdoId));
		final String componentName = ComponentTypeNameCache.INSTANCE.getComponentName(terminologyComponentId);
		message = format(MESSAGE_TEMPLATE, componentName, label, branchPath.getPath());
	}

	@Override
	public String toString() {
		return message;
	}
	
}