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
package com.b2international.snowowl.fhir.core;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemHierarchyMeaning;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;

/**
 * FHIR provider base class.
 * 
 * @since 6.3
 */
public abstract class FhirProvider implements IFhirProvider {
	
	protected IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
	
	@Override
	public CodeSystem getCodeSystem(Path codeSystemPath) {
		String repositoryId = codeSystemPath.getParent().toString();
		String shortName = codeSystemPath.getFileName().toString();
		CodeSystemEntry codeSystemEntry = CodeSystemRequests.prepareGetCodeSystem(shortName).build(repositoryId).execute(eventBus).getSync();
		return createCodeSystemBuilder(codeSystemEntry).build();
	}
	
	/**
	 * Returns the FHIR code systems available for the given repository
	 * @param repositoryId
	 * @return collection of {@link CodeSystem}
	 */
	protected Collection<CodeSystem> getCodeSystems(String repositoryId) {
		
		Promise<CodeSystems> codeSystemsPromise = CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.build(repositoryId)
			.execute(eventBus);
			
			return codeSystemsPromise.then(cs -> {
				List<CodeSystem> codeSystems = cs.stream()
					.map(c -> createCodeSystemBuilder(c).build())
					.collect(Collectors.toList());
				return codeSystems;
			}).getSync();
	}
	
	/**
	 * Returns the designated FHIR Uri for the given code system
	 * @return
	 */
	protected abstract Uri getFhirUri();
	
	/**
	 * Creates a FHIR {@link CodeSystem} from a {@link CodeSystemEntry}
	 * @param codeSystemEntry
	 * @return FHIR Code system
	 */
	protected Builder createCodeSystemBuilder(final CodeSystemEntry codeSystemEntry) {
		
		Identifier identifier = new Identifier(IdentifierUse.OFFICIAL.getCode(), null, new Uri("www.hl7.org"), codeSystemEntry.getOid());
		
		String id = codeSystemEntry.getRepositoryUuid() + "/" + codeSystemEntry.getShortName();
		
		Builder builder = CodeSystem.builder(id)
			.identifier(identifier)
			.language(FhirUtils.getLanguageCode(codeSystemEntry.getLanguage()))
			.name(codeSystemEntry.getShortName())
			.narrative(NarrativeStatus.ADDITIONAL, codeSystemEntry.getCitation())
			.publisher(codeSystemEntry.getOrgLink())
			.status(PublicationStatus.ACTIVE)
			.hierarchyMeaning(CodeSystemHierarchyMeaning.IS_A)
			.title(codeSystemEntry.getName())
			.description(codeSystemEntry.getCitation())
			.url(getFhirUri());
		
		return addSpecificProperties(builder);
	}
	
	/**
	 * Build code system specific properties. Subclasses to override.
	 * @param builder
	 * @return builder
	 */
	protected Builder addSpecificProperties(final Builder builder) {
		return builder;
	}
	
	/**
	 * @param version
	 * @return
	 */
	protected IBranchPath getBranchPath(String version) {
		if (version == null) {
			return BranchPathUtils.createMainPath();
		} else {
			return BranchPathUtils.createPath(BranchPathUtils.createMainPath(), version);
		}
	}

	/**
	 * Returns the properties supported by this provider.
	 * Subclasses to override
	 * @return
	 */
	protected Collection<CommonConceptProperties> getSupportedConceptProperties() {
		return Collections.emptySet();
	}
	
	/**
	 * @param properties
	 */
	protected void validateRequestedProperties(Collection<Code> properties) {
		
		Set<Code> supportedCodes = getSupportedConceptProperties().stream().map(p -> p.getCode()).collect(Collectors.toSet());
		if (!supportedCodes.containsAll(properties)) {
			throw new BadRequestException("Unrecognized properties '%s.'", Arrays.toString(properties.toArray()));
		}
	}

}
