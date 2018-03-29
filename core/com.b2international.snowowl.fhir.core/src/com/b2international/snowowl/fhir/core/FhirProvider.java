/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.CodeSystemEntry;
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
 * @since 6.4
 */
public abstract class FhirProvider implements IFhirProvider {
	
	private final String repositoryId;

	public FhirProvider(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	@Override
	public final boolean isSupported(String uri) {
		return getSupportedURIs().stream()
			.filter(uri::equalsIgnoreCase)
			.findAny()
			.isPresent();
	}
	
	@Override
	public final CodeSystem getCodeSystem(Path codeSystemPath) {
		String repositoryId = codeSystemPath.getParent().toString();
		String shortName = codeSystemPath.getFileName().toString();
		CodeSystemEntry codeSystemEntry = CodeSystemRequests.prepareGetCodeSystem(shortName).build(repositoryId).execute(getBus()).getSync();
		return createCodeSystemBuilder(codeSystemEntry).build();
	}
	
	@Override
	public final CodeSystem getCodeSystem(String codeSystemUri) {
		if (!isSupported(codeSystemUri)) {
			throw new BadRequestException("Code system with URI %s is not supported by this provider %s.", codeSystemUri, this.getClass().getSimpleName());
		}
		return getCodeSystems()
				.stream()
				.findFirst()
				.orElseThrow(() -> new NotFoundException("Could not find any code systems for %s.", codeSystemUri));
	}
	
	@Override
	public final Collection<CodeSystem> getCodeSystems() {
		return CodeSystemRequests.prepareSearchCodeSystem()
				.all()
				.build(repositoryId)
				.execute(getBus())
				.then(codeSystems -> {
					return codeSystems.stream()
						.map(this::createCodeSystemBuilder)
						.map(Builder::build)
						.collect(Collectors.toList());
				})
				.getSync();
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
	protected final Builder createCodeSystemBuilder(final CodeSystemEntry codeSystemEntry) {
		
		Identifier identifier = new Identifier(IdentifierUse.OFFICIAL, null, new Uri("www.hl7.org"), codeSystemEntry.getOid());
		
		String id = codeSystemEntry.getRepositoryUuid() + "/" + codeSystemEntry.getShortName();
		
		Builder builder = CodeSystem.builder(id)
			.identifier(identifier)
			.language(getLanguageCode(codeSystemEntry.getLanguage()))
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
	 * Subclasses may override this method to provide additional properties supported by this provider.
	 * @return the supported properties
	 */
	protected Collection<CommonConceptProperties> getSupportedConceptProperties() {
		return Collections.emptySet();
	}
	
	/**
	 * @param properties
	 */
	protected void validateRequestedProperties(Collection<Code> properties) {
		Set<Code> supportedCodes = getSupportedConceptProperties().stream().map(CommonConceptProperties::getCode).collect(Collectors.toSet());
		if (!supportedCodes.containsAll(properties)) {
			throw new BadRequestException("Unrecognized properties '%s.'", Arrays.toString(properties.toArray()));
		}
	}
	
	/**
	 * @param version - the version to target 
	 * @return an absolute branch path to use in terminology API requests
	 */
	protected final String getBranchPath(String version) {
		return CompareUtils.isEmpty(version) ? Branch.MAIN_PATH : Branch.get(Branch.MAIN_PATH, version); 
	}
	
	/**
	 * @return the {@link IEventBus} service to access terminology resources.
	 */
	protected final IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
	
	/**
	 * Returns (attempts) the ISO 639 two letter code based on the language name.
	 * @return two letter language code
	 */
	private static String getLanguageCode(String language) {
		if (language == null) return null;
		
	    Locale loc = new Locale("en");
	    String[] languages = Locale.getISOLanguages(); // list of language codes

	    return Arrays.stream(languages)
	    		.filter(l -> {
	    			Locale locale = new Locale(l,"US");
	    			return locale.getDisplayLanguage(loc).equalsIgnoreCase(language) 
	    					|| locale.getISO3Language().equalsIgnoreCase(language);
	    		})
	    		.findFirst()
	    		.orElse(null);
	}

}
