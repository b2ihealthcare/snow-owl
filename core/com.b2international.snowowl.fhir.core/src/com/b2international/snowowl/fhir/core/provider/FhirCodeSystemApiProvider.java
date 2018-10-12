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
package com.b2international.snowowl.fhir.core.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.fhir.core.FhirCoreActivator;
import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.ResourceNarrative;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.FhirCodeSystem;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem.Builder;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.b2international.snowowl.fhir.core.model.subsumption.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.subsumption.SubsumptionResult;
import com.google.common.collect.Sets;

/**
 * Provider for the internal FHIR terminologies
 * 
 * @since 6.4
 */
public class FhirCodeSystemApiProvider extends CodeSystemApiProvider {

	/*
	 * No repository associated with the internal hard-coded FHIR terminologies
	 */
	public FhirCodeSystemApiProvider() {
		super(null);
	}
	
	@Override
	public final Collection<CodeSystem> getCodeSystems() {
		
		Collection<CodeSystem> codeSystems = Sets.newHashSet();
		
		for (Class<?> codeSystemClass : getCodeSystemClasses()) {
			
			Object enumObject = createCodeSystemEnum(codeSystemClass);
			FhirCodeSystem fhirCodeSystem = (FhirCodeSystem) enumObject;
			CodeSystem codeSystem = buildCodeSystem(fhirCodeSystem);
			codeSystems.add(codeSystem);
		}
		return codeSystems;
	}

	@Override
	public boolean isSupported(LogicalId logicalId) {
		
		Optional<String> supportedPath = getSupportedURIs().stream()
			.map(u -> u.substring(u.lastIndexOf("/") + 1))
			.filter(p -> p.equals(logicalId.getBranchPath()))
			.findFirst();
		return supportedPath.isPresent();
	}
	
	@Override
	public CodeSystem getCodeSystem(LogicalId logicalId) {
		
		return getCodeSystemClasses().stream().map(cl-> { 
			Object enumObject = createCodeSystemEnum(cl);
			return (FhirCodeSystem) enumObject;
		}).filter(fcs -> fcs.getCodeSystemUri().endsWith(logicalId.getBranchPath()))
		.map(this::buildCodeSystem)
		.findFirst()
		.get();
	}

	@Override
	public LookupResult lookup(LookupRequest lookupRequest) {
		
		String system = lookupRequest.getSystem();
		String code = lookupRequest.getCode();
		
		if (StringUtils.isEmpty(system) || StringUtils.isEmpty(code)) {
			throw new BadRequestException("System or code parameters must not be null.");
		}
		validateRequestedProperties(lookupRequest);
		
		Collection<Class<?>> codeSystemClasses = getCodeSystemClasses();
		
		//find the matching code system first
		Class<?> codeSytemClass = codeSystemClasses.stream()
			.filter(csc -> {
				Object codeSystemEnum = createCodeSystemEnum(csc);
				if (codeSystemEnum instanceof FhirCodeSystem) {
					FhirCodeSystem fhirCodeSystem = (FhirCodeSystem) codeSystemEnum;
					return system.equals(fhirCodeSystem.getCodeSystemUri());
				}
				return false;
			})
			.findFirst()
			.orElseThrow(() -> new BadRequestException("Could not find code system [%s].", system));
		
		//map the code system class to enum constants
		Set<FhirCodeSystem> codeSystemEnums = Sets.newHashSet(codeSytemClass.getDeclaredFields()).stream()
			.filter(Field::isEnumConstant)
			.map(f -> (FhirCodeSystem) createEnumInstance(f.getName(), codeSytemClass))
			.collect(Collectors.toSet());
			
		//find the matching enum code
		FhirCodeSystem fhirCodeSystem = codeSystemEnums.stream()
			.filter(cs -> code.equals(cs.getCodeValue()))
			.findAny()
			.orElseThrow(() -> new BadRequestException("Could not find code [%s] for the known code system [%s].", code, system));
		
		LookupResult.Builder resultBuilder = LookupResult.builder();
		resultBuilder.name(codeSytemClass.getSimpleName());
		resultBuilder.display(fhirCodeSystem.getDisplayName());
		return resultBuilder.build();
	}
	
	@Override
	public SubsumptionResult subsumes(SubsumptionRequest subsumption) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected Set<String> fetchAncestors(String branchPath, String componentId) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Collection<String> getSupportedURIs() {

		Collection<String> codeSytemUris = Sets.newHashSet();

		Collection<Class<?>> codeSystemClasses = getCodeSystemClasses();
		
		for (Class<?> codeSystemPackageClass : codeSystemClasses) {
			Object enumObject = createCodeSystemEnum(codeSystemPackageClass);
			FhirCodeSystem fhirCodeSystem = (FhirCodeSystem) enumObject;
			codeSytemUris.add(fhirCodeSystem.getCodeSystemUri());
		}
		return codeSytemUris;
	}
	
	/* private methods */
	private CodeSystem buildCodeSystem(FhirCodeSystem fhirCodeSystem) {
		
		String supportedUri = fhirCodeSystem.getCodeSystemUri();

		String id = getIdFromSystem(supportedUri);
		
		Builder builder = CodeSystem.builder(id)
			.language("en")
			.name(fhirCodeSystem.getClass().getSimpleName())
			.publisher("www.hl7.org")
			.copyright("© 2011+ HL7")
			.version(fhirCodeSystem.getVersion())
			.caseSensitive(true)
			.status(PublicationStatus.ACTIVE)
			.url(new Uri(supportedUri))
			.content(CodeSystemContentMode.COMPLETE);
		
		//human-readable narrative
		ResourceNarrative resourceNarrative = fhirCodeSystem.getClass().getAnnotation(ResourceNarrative.class);
		if (resourceNarrative != null) {
			builder.text(Narrative.builder()
				.div("<div>" + resourceNarrative.value() + "</div>")
				.status(NarrativeStatus.GENERATED)
				.build());
		}
		
		int counter = 0;
		
		Field[] declaredFields = fhirCodeSystem.getClass().getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
			if (declaredFields[i].isEnumConstant()) {
				FhirCodeSystem codeSystemEnumConstant = (FhirCodeSystem) createEnumInstance(declaredFields[i].getName(), fhirCodeSystem.getClass());
				
				Concept concept = Concept.builder()
					.code(codeSystemEnumConstant.getCode().getCodeValue())
					.display(codeSystemEnumConstant.getDisplayName())
					.build();
				
				builder.addConcept(concept);
				counter++;
			}
		}
				
		builder.count(counter);
		return builder.build();
	}
	
/**
	 * @param supportedUri
	 * @return
	 */
	private String getIdFromSystem(String supportedUri) {
		return supportedUri.substring(supportedUri.lastIndexOf("/") + 1, supportedUri.length());
	}

	/**
	 * @param codeSystemPackageClass
	 * @return
	 */
	private Object createCodeSystemEnum(Class<?> codeSystemPackageClass) {
		
		Field[] declaredFields = codeSystemPackageClass.getDeclaredFields();
		
		//create the first enum constant if exists
		if (declaredFields.length > 0 && declaredFields[0].isEnumConstant()) {
			return createEnumInstance(declaredFields[0].getName(), codeSystemPackageClass);
		}
		throw new NullPointerException("Could not create an enum for the class: " + codeSystemPackageClass);
	}

	/**
	 * @return
	 */
	private Collection<Class<?>> getCodeSystemClasses() {
		
		Collection<Class<?>> codeSystemClasses = Sets.newHashSet();
		
		Bundle bundle = FhirCoreActivator.getDefault().getBundle();
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		Collection<String> listResources = bundleWiring.listResources("/com/b2international/snowowl/fhir/core/codesystems", "*", BundleWiring.FINDENTRIES_RECURSE);

		for (String codeSystemPackageClassName : listResources) {
			try {
				Class<?> codeSystemPackageClass = bundle.loadClass(codeSystemPackageClassName.replaceAll("/", ".").replaceAll(".class", ""));
				
				if (codeSystemPackageClass.isEnum()) {
					codeSystemClasses.add(codeSystemPackageClass);
				}
			} catch (Exception e) {
				//swallow the exception, only log
				e.printStackTrace();
			}
		}
		return codeSystemClasses;
				
	}

	@SuppressWarnings("unchecked")
	private <T extends Enum<T>> T createEnumInstance(String name, Type type) {
		return Enum.valueOf((Class<T>) type, name);
	}

	@Override
	protected Uri getFhirUri(CodeSystemEntry codeSystemEntry, CodeSystemVersionEntry codeSystemVersion) {
		//handled on the per Core terminology basis (like LCS) 
		return null;
	}

	@Override
	protected int getCount(CodeSystemVersionEntry codeSystemVersion) {
		//handled on the per Core terminology basis (like LCS) 
		return 0;
	}

	@Override
	protected String getCodeSystemShortName() {
		return null;
	}

	@Override
	protected String getRepositoryId() {
		return null;
	}

}
