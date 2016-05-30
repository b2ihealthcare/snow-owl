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
package com.b2international.snowowl.datastore.index;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.component.IconIdProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 4.7
 */
public abstract class RevisionDocument extends Revision implements IComponent<String>, IconIdProvider<String> {

	/**
	 * @since 4.7
	 */
	public static class Fields {
		public static final String ID = "id";
		public static final String ICON_ID = "iconId";
	}
	
	/**
	 * @param <B> - the builder type
	 * @since 4.7
	 */
	public static abstract class RevisionDocumentBuilder<B extends RevisionDocumentBuilder<B>> {
		
		protected String id;
		protected String label;
		protected String iconId;
		
		public B id(final String id) {
			this.id = id;
			return getSelf();
		}
		
		/**
		 * @deprecated - UNSUPPORTED, will be removed in 4.7
		 */
		public B label(final String label) {
			this.label = label;
			return getSelf();
		}
		
		public B iconId(final String iconId) {
			this.iconId = iconId;
			return getSelf();
		}

		
		protected abstract B getSelf();
		
	}
	
	private final String id;
	private final String label;
	private final String iconId;
	
	protected RevisionDocument(final String id, final String label, String iconId) {
		this.id = checkNotNull(id, "id");
		this.label = checkNotNull(label, "label");
		this.iconId = iconId;
	}
	
	@JsonIgnore
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getIconId() {
		return iconId;
	}
	
	@Override
	public String toString() {
		return String.format("%02.2f - %s [%d]", id, label);
	}

	/**
	 * (non-API)
	 * 
	 * @return returns with the storage key of the current component as a CDO ID.
	 */
	public CDOID cdoID() {
		return CDOIDUtil.createLong(getStorageKey());
	}

}
