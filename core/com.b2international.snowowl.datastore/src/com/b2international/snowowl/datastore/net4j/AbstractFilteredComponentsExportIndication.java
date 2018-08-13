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
package com.b2international.snowowl.datastore.net4j;

import java.util.Collection;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import com.google.common.collect.Sets;

/**
 * Export indication for exports where a set of components are exported.
 * 
 * @since 3.3
 */
public abstract class AbstractFilteredComponentsExportIndication extends AbstractExportIndication {
	
	private final Collection<String> componentIds = Sets.newHashSet();

	public AbstractFilteredComponentsExportIndication(final SignalProtocol<?> protocol, final short exportSignal) {
		super(protocol, exportSignal);
	}
	
	@Override
	protected void postIndicating(ExtendedDataInputStream in) throws Exception {
		
		final int size = in.readInt();
		for (int i = 0; i < size; i++) {
			componentIds.add(in.readUTF());
		}
		
		
	}

	public Collection<String> getComponentIds() {
		return componentIds;
	}


}