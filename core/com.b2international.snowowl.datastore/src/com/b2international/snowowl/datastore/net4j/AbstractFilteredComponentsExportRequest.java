/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.net4j;

import java.util.Collection;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Export request for exports where a set of components are exported.
 * 
 * @since 3.3
 */
public abstract class AbstractFilteredComponentsExportRequest extends AbstractExportRequest {

	private final Collection<String> componentIds;

	public AbstractFilteredComponentsExportRequest(final SignalProtocol<?> protocol, final short importSignal, final IBranchPath branchPath, final String exportPath, final String userId, final Collection<String> componentIds) {
		super(protocol, importSignal, branchPath, exportPath, userId);
		this.componentIds = componentIds;
	}
	
	@Override
	protected void postRequesting(final ExtendedDataOutputStream out) throws Exception {
		
		out.writeInt(componentIds.size());
		
		for (final String componentId : componentIds) {
			out.writeUTF(componentId);
		}
		
	}

	public Collection<String> getComponentIds() {
		return componentIds;
	}

}