/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.version;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.datastore.version.INoopPublishManager;

/**
 * NOOP publish manager implementation. Does not modify any components but creates corresponding 
 * code system version entries and creates tag branches.  
 *
 */
public abstract class NoopPublishManager extends PublishManager implements INoopPublishManager {

	@Override
	protected LongSet getUnversionedComponentStorageKeys(String branchPath) {
		return LongCollections.emptySet();
	}
	
	@Override
	protected EStructuralFeature getEffectiveTimeFeature(final EClass eClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected EStructuralFeature getReleasedFeature(EClass eClass) {
		throw new UnsupportedOperationException();
	}
	
}