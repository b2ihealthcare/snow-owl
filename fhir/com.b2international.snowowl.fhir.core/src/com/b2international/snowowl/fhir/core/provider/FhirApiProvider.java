/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.codesystem.CodeSystemVersions;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
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
	
	/**
	 * @param version - the version to target 
	 * @return an absolute branch path to use in terminology API requests
	 */
	protected final String getBranchPath(String version) {
		
		if (version != null) {
			return Branch.get(Branch.MAIN_PATH, version);
		} else {
			
			//get the last version for now
			Optional<CodeSystemVersionEntry> latestVersion = CodeSystemRequests.prepareSearchCodeSystemVersion()
				.one()
				.filterByCodeSystemShortName(getCodeSystemShortName())
				.sortBy(SearchResourceRequest.SortField.ascending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build(getRepositoryId())
				.execute(getBus())
				.getSync()
				.first();
			
			if (latestVersion.isPresent()) {
				return latestVersion.get().getPath();
			}
			
			//no version supplied, no version found in the repository, we should probably throw an exception, but for now returning MAIN
			return Branch.MAIN_PATH;
		}
	}
	
	/**
	 * Returns the code system version for the logical id
	 * @param logicalId
	 * @return
	 */
	protected CodeSystemVersionEntry findCodeSystemVersion(LogicalId logicalId) {
		
		Optional<CodeSystemVersionEntry> codeSystemOptional = CodeSystemRequests.prepareSearchCodeSystemVersion()
			.one()
			.filterByBranchPath(logicalId.getBranchPath())
			.build(getRepositoryId())
			.execute(getBus())
			.getSync()
			.first();
			
		return codeSystemOptional.orElseThrow(() -> 
			new BadRequestException(String.format("Could not find corresponding version [%s] for logical id [%s].", logicalId.getBranchPath(), logicalId), "ValueSet.id"));
	}
	
	/**
	 * Returns a code system version that matches the provided effective date
	 * @param versionEffectiveDate
	 * @return code system version with the effective date
	 */
	protected CodeSystemVersionEntry getCodeSystemVersion(String versionEffectiveDate) {
		
		if (versionEffectiveDate == null) {
			//get the last version
			return CodeSystemRequests.prepareSearchCodeSystemVersion()
				.one()
				.filterByCodeSystemShortName(getCodeSystemShortName())
				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build(getRepositoryId())
				.execute(getBus())
				.getSync()
				.first()
				.orElseThrow(() -> new BadRequestException(String.format("Could not find any versions for %s with effective date '%s'", getCodeSystemShortName(), versionEffectiveDate), "CodeSystem.system"));
		} else {
			return CodeSystemRequests.prepareSearchCodeSystemVersion()
				.one()
				.filterByEffectiveDate(EffectiveTimes.parse(versionEffectiveDate, DateFormats.SHORT))
				.filterByCodeSystemShortName(getCodeSystemShortName())
				.build(getRepositoryId())
				.execute(getBus())
				.getSync()
				.first()
				.orElseThrow(() -> new BadRequestException(String.format("Could not find code system for %s version '%s'", getCodeSystemShortName(), versionEffectiveDate), "CodeSystem.system"));
		}
	}
	
	protected List<CodeSystemVersionEntry> collectCodeSystemVersions(String repositoryId) {
		
		List<CodeSystemVersionEntry> codeSystemVersionList = Lists.newArrayList();
		
		CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
			.all()
			.build(repositoryId)
			.execute(getBus())
			.getSync();
		
		//fetch all the versions
		CodeSystemVersions codeSystemVersions = CodeSystemRequests.prepareSearchCodeSystemVersion()
			.all()
			.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
			.build(repositoryId)
			.execute(getBus())
			.getSync();
		
		codeSystems.forEach(cse -> { 
			
			List<CodeSystemVersionEntry> versions = codeSystemVersions.stream()
			.filter(csv -> csv.getCodeSystemShortName().equals(cse.getShortName()))
			.collect(Collectors.toList());
			codeSystemVersionList.addAll(versions);
		});
		
		return codeSystemVersionList;
	}
	
	/**
	 * Returns the code system short name for the provider
	 * @return
	 */
	protected abstract String getCodeSystemShortName();
	
	/**
	 * Returns the repository id for the provider
	 * @return
	 */
	protected abstract String getRepositoryId();
		
	
	/**
	 * Returns (attempts) the ISO 639 two letter code based on the language name.
	 * @return two letter language code
	 */
	protected static String getLanguageCode(String language) {
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
