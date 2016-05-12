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
package com.b2international.snowowl.datastore.index;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;


/**
 * @since 4.3
 */
public abstract class DocumentUpdaterBase<D extends DocumentBuilderBase<D>> implements DocumentUpdater<D> {

	private static final Logger LOG = LoggerFactory.getLogger("repository");
	
	private String componentId;

	public DocumentUpdaterBase(String componentId) {
		this.componentId = checkNotNull(componentId, "componentId");
	}
	
	protected String getComponentId() {
		return componentId;
	}
	
	@Override
	public final void update(D doc) {
		try {
			LOG.trace("Executing updater {} on {} doc:[{}]", getClass().getSimpleName(), getComponentId(), doc.build());
			doUpdate(doc);
		} finally {
			LOG.trace("Executed updater {} on {} doc:[{}]", getClass().getSimpleName(), getComponentId(), doc.build());
		}
	}
	
	protected abstract void doUpdate(D doc);
	
	@Override
	public int hashCode() {
		return Objects.hash(componentId, getClass());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DocumentUpdaterBase<?> other = (DocumentUpdaterBase<?>) obj;
		return Objects.equals(componentId, other.componentId);
	}
	
}
