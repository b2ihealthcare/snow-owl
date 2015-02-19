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

import javax.annotation.Nullable;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.CoreTerminologyBroker.ICoreTerminologyInformation;
import com.google.common.base.Preconditions;

/**
 * Abstract representation of a {@link CDOManagedItem managed item}.
 */
public abstract class CDOManagedItem<T extends ICDOManagedItem<T>> extends Lifecycle implements ICDOManagedItem<T> {

	private final byte namespaceId;
	private final String repositoryUuid;
	private final String repositoryName;
	private final ICoreTerminologyInformation terminologyInformation;
	private final String dependsOnRepositoryUuid;
	private final boolean meta;

	private ICDOContainer<T> container;
	
	/**Protected constructor.
	 * @param toolingId 
	 * @param meta 
	 * @param dependsOnRepositoryUuid */
	protected CDOManagedItem(final String repositoryUuid, @Nullable final String repositoryName, final byte namespaceId, 
			@Nullable final String toolingId, @Nullable final String dependsOnRepositoryUuid, final boolean meta) {
		
		this.dependsOnRepositoryUuid = dependsOnRepositoryUuid;
		this.meta = meta;
		this.repositoryUuid = Preconditions.checkNotNull(repositoryUuid);
		this.namespaceId = namespaceId;
		this.repositoryName = StringUtils.isEmpty(repositoryName) ? repositoryUuid : repositoryName;
		terminologyInformation = CoreTerminologyBroker.getInstance().getTerminologyInformation(toolingId);
	}
	
	@Override
	public byte getNamespaceId() {
		return namespaceId;
	}
	
	@Override
	public String getRepositoryName() {
		return repositoryName;
	}
	
	@Override
	public void setContainer(final ICDOContainer<T> container) {
		checkInactive();
		Preconditions.checkNotNull(container, "CDO container argument cannot be null.");
		this.container = container;
	}
	
	@Override
	public String getUuid() {
		return repositoryUuid;
	}
	
	@Override
	public String getSnowOwlTerminologyComponentId() {
		return terminologyInformation.getId();
	}
	
	@Override
	public String getSnowOwlTerminologyComponentName() {
		return terminologyInformation.getName();
	}

	@Override
	@Nullable public String getMasterUuid() {
		return dependsOnRepositoryUuid;
	}
	
	@Override
	public boolean isMeta() {
		return meta;
	}
	
	/**
	 * Returns with the {@link ICDOContainer container} of the current managed instance.
	 */
	@Nullable protected ICDOContainer<T> getContainer() {
		return container;
	}
	
}