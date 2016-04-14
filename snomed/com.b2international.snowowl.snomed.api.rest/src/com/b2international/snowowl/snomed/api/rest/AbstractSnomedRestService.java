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
package com.b2international.snowowl.snomed.api.rest;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.b2international.commons.http.AcceptHeader;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.server.domain.ComponentRef;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * Abstract SNOMED CT REST service base class.
 * 
 * @since 1.0
 */
public abstract class AbstractSnomedRestService extends AbstractRestService {

	@Autowired
	@Value("${codeSystemShortName}")
	protected String codeSystemShortName;
	
	@Autowired
	protected IEventBus bus;

	protected IComponentRef createComponentRef(final String branchPath, final String componentId) {
		final ComponentRef conceptRef = new ComponentRef(codeSystemShortName, branchPath, componentId);
		conceptRef.checkStorageExists();
		return conceptRef;
	}

	protected List<ExtendedLocale> getExtendedLocales(final String acceptLanguage) {
		try {
			return AcceptHeader.parseExtendedLocales(new StringReader(acceptLanguage));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
	}

}
