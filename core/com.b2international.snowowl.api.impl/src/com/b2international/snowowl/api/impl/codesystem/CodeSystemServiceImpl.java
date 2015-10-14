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
package com.b2international.snowowl.api.impl.codesystem;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.api.codesystem.ICodeSystemService;
import com.b2international.snowowl.api.codesystem.domain.ICodeSystem;
import com.b2international.snowowl.api.impl.codesystem.domain.CodeSystem;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.UserBranchPathMap;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;

/**
 */
public class CodeSystemServiceImpl implements ICodeSystemService {

	private static final Function<? super com.b2international.snowowl.datastore.ICodeSystem, ICodeSystem> CODE_SYSTEM_CONVERTER = 
			new Function<com.b2international.snowowl.datastore.ICodeSystem, ICodeSystem>() {

		@Override
		public ICodeSystem apply(final com.b2international.snowowl.datastore.ICodeSystem input) {
			final CodeSystem result = new CodeSystem();
			result.setCitation(input.getCitation());
			result.setName(input.getName());
			result.setOid(input.getOid());
			result.setOrganizationLink(input.getOrgLink());
			result.setPrimaryLanguage(input.getLanguage());
			result.setShortName(input.getShortName());
			return result;
		}
	};

	private static final Ordering<ICodeSystem> SHORT_NAME_ORDERING = Ordering.natural().onResultOf(new Function<ICodeSystem, String>() {
		@Override
		public String apply(final ICodeSystem input) {
			return input.getShortName();
		}
	});

	protected static final UserBranchPathMap MAIN_BRANCH_PATH_MAP = new UserBranchPathMap();

	protected static TerminologyRegistryService getRegistryService() {
		return ApplicationContext.getServiceForClass(TerminologyRegistryService.class);
	}

	@Override
	public List<ICodeSystem> getCodeSystems() {
		return toSortedCodeSystemList(getRegistryService().getCodeSystems(MAIN_BRANCH_PATH_MAP));
	}

	@Override
	public ICodeSystem getCodeSystemByShortNameOrOid(String shortNameOrOid) {
		checkNotNull(shortNameOrOid, "Shortname Or OID parameter may not be null.");
		final TerminologyRegistryService service = getRegistryService();
		com.b2international.snowowl.datastore.ICodeSystem codeSystem = service.getCodeSystemByOid(MAIN_BRANCH_PATH_MAP, shortNameOrOid);
		if (codeSystem == null) {
			codeSystem = service.getCodeSystemByShortName(MAIN_BRANCH_PATH_MAP, shortNameOrOid);
			if (codeSystem == null) {
				throw new CodeSystemNotFoundException(shortNameOrOid);
			}
		}
		return toCodeSystem(codeSystem).get();
	}
	
	private Optional<ICodeSystem> toCodeSystem(final com.b2international.snowowl.datastore.ICodeSystem sourceCodeSystem) {
		return Optional.fromNullable(sourceCodeSystem).transform(CODE_SYSTEM_CONVERTER);
	}

	private List<ICodeSystem> toSortedCodeSystemList(final Collection<com.b2international.snowowl.datastore.ICodeSystem> sourceCodeSystems) {
		return SHORT_NAME_ORDERING.immutableSortedCopy(transform(sourceCodeSystems, CODE_SYSTEM_CONVERTER));
	}
}