/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.provider;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;
import com.google.common.collect.Lists;

/**
 * 
 * @since 7.0
 */
public abstract class FhirApiProvider {
	
	private final List<ExtendedLocale> locales;
	private final IEventBus bus;
	
	public FhirApiProvider(IEventBus bus, List<ExtendedLocale> locales) {
		this.bus = bus;
		this.locales = locales;
	}
	
	protected final IEventBus getBus() {
		return bus;
	}
	
	protected final List<ExtendedLocale> getLocales() {
		return locales;
	}
	
//	/**
//	 * @param version - the version to target 
//	 * @return an absolute branch path to use in terminology API requests
//	 */
//	protected final String getBranchPath(String version) {
//		
//		if (version != null) {
//			return Branch.get(Branch.MAIN_PATH, version);
//		} else {
//			
//			//get the last version for now
//			Optional<Version> latestVersion = CodeSystemRequests.prepareSearchVersion()
//				.one()
//				.filterByCodeSystemShortName(getCodeSystemShortName())
//				.sortBy(SearchResourceRequest.SortField.descending(VersionDocument.Fields.EFFECTIVE_TIME))
//				.build(getRepositoryId())
//				.execute(getBus())
//				.getSync()
//				.first();
//			
//			if (latestVersion.isPresent()) {
//				return latestVersion.get().getPath();
//			}
//			
//			//no version supplied, no version found in the repository, we should probably throw an exception, but for now returning MAIN
//			return Branch.MAIN_PATH;
//		}
//	}
	
	/**
	 * Returns the code system version for the component URI
	 * @param componentURI
	 * @param location for logging the location in case of an exception
	 * @return
	 */
	protected Version findCodeSystemVersion(ComponentURI componentURI, String location) {
		return ResourceRequests.prepareSearchVersion()
			.one()
			.filterById(componentURI.resourceUri().withoutResourceType())
			.buildAsync()
			.execute(getBus())
			.getSync()
			.first()
			.orElseThrow(() -> new BadRequestException(String.format("Could not find corresponding version [%s] for the component id [%s].", componentURI.resourceUri(), componentURI.identifier()), location));
	}
	
//	/**
//	 * Returns a code system version that matches the provided effective date
//	 * @param versionEffectiveDate
//	 * @return code system version with the effective date
//	 */
//	protected Version getCodeSystemVersion(String versionEffectiveDate) {
//		
//		if (versionEffectiveDate == null) {
//			//get the last version
//			return CodeSystemRequests.prepareSearchVersion()
//				.one()
//				.filterByCodeSystemShortName(getCodeSystemShortName())
//				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
//				.build(getRepositoryId())
//				.execute(getBus())
//				.getSync()
//				.first()
//				.orElseThrow(() -> new BadRequestException(String.format("Could not find any versions for %s with effective date '%s'", getCodeSystemShortName(), versionEffectiveDate), "CodeSystem.system"));
//		} else {
//			return CodeSystemRequests.prepareSearchVersion()
//				.one()
//				.filterByEffectiveDate(EffectiveTimes.parse(versionEffectiveDate, DateFormats.SHORT))
//				.filterByCodeSystemShortName(getCodeSystemShortName())
//				.build(getRepositoryId())
//				.execute(getBus())
//				.getSync()
//				.first()
//				.orElseThrow(() -> new BadRequestException(String.format("Could not find code system for %s version '%s'", getCodeSystemShortName(), versionEffectiveDate), "CodeSystem.system"));
//		}
//	}
	
//	protected List<CodeSystemVersion> collectCodeSystemVersions(String repositoryId) {
//		
//		List<CodeSystemVersion> codeSystemVersionList = Lists.newArrayList();
//		
//		CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
//			.all()
//			.build(repositoryId)
//			.execute(getBus())
//			.getSync();
//		
//		//fetch all the versions
//		CodeSystemVersions codeSystemVersions = CodeSystemRequests.prepareSearchVersion()
//			.all()
//			.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
//			.build(repositoryId)
//			.execute(getBus())
//			.getSync();
//		
//		codeSystems.forEach(cse -> { 
//			
//			List<CodeSystemVersion> versions = codeSystemVersions.stream()
//			.filter(csv -> csv.getUri().getCodeSystem().equals(cse.getShortName()))
//			.collect(Collectors.toList());
//			codeSystemVersionList.addAll(versions);
//		});
//		
//		return codeSystemVersionList;
//	}
	
	protected Optional<FhirSearchParameter> getSearchParam(final Set<FhirSearchParameter> searchParameters, String parameterName) {
		return searchParameters.stream().filter(p -> parameterName.equals(p.getName())).findFirst();
	}
	
	/**
	 * Returns the collection of the Snow Owl identifier part (last segment) of 
	 * the passed in URIs.
	 * @param uris
	 * @return collection of identifiers
	 */
	protected Collection<String> collectIds(Collection<String> uris) {
		return uris.stream()
			.filter(u -> u.contains("/"))
			.map(u -> u.substring(u.lastIndexOf('/') + 1, u.length()))
			.collect(Collectors.toSet());
	}

}
