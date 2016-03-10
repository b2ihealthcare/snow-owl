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
package com.b2international.snowowl.snomed.datastore.request;

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.core.domain.ConstantIdStrategy;
import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.RegisteringIdStrategy;
import com.b2international.snowowl.snomed.core.domain.ReservingIdStrategy;

/**
 * @since 4.0
 */
public abstract class BaseSnomedComponentCreateRequest extends BaseRequest<TransactionContext, String> implements SnomedComponentCreateRequest {

	/** 
	 * The maximum number of identifier service reservation calls (after which a namespace is known to be completely full). 
	 */
	private static final int ID_GENERATION_ATTEMPTS = 9999_9999;
	
	@NotNull
	private IdGenerationStrategy idGenerationStrategy;
	
	private String moduleId;

	@Override
	public IdGenerationStrategy getIdGenerationStrategy() {
		return idGenerationStrategy;
	}

	@Override
	public String getModuleId() {
		return moduleId;
	}

	void setIdGenerationStrategy(final IdGenerationStrategy idGenerationStrategy) {
		this.idGenerationStrategy = idGenerationStrategy;
	}

	void setModuleId(final String moduleId) {
		this.moduleId = moduleId;
	}
	
	@Override
	protected final Class<String> getReturnType() {
		return String.class;
	}

	protected final void ensureUniqueId(String type, TransactionContext context) {
		
		if (getIdGenerationStrategy() instanceof RegisteringIdStrategy) {
			final String componentId = getIdGenerationStrategy().generate(context);
			
			try {
				checkComponentExists(context, componentId);
				throw new AlreadyExistsException(type, componentId);
			} catch (ComponentNotFoundException e) {
				setIdGenerationStrategy(new ConstantIdStrategy(componentId));
				return;
			}
		}
		
		if (getIdGenerationStrategy() instanceof ReservingIdStrategy) {
			String componentId = null;
			
			for (int i = 0; i < ID_GENERATION_ATTEMPTS; i++) {
				componentId = getIdGenerationStrategy().generate(context);
				
				try {
					checkComponentExists(context, componentId);
				} catch (ComponentNotFoundException e) {
					setIdGenerationStrategy(new RegisteringIdStrategy(componentId));
					return;
				}
			}
			
			throw new BadRequestException("Couldn't generate unique identifier for %s after %d attempts.", type, ID_GENERATION_ATTEMPTS); 
		}
	}

	protected abstract void checkComponentExists(TransactionContext context, final String componentId) throws ComponentNotFoundException;
}
