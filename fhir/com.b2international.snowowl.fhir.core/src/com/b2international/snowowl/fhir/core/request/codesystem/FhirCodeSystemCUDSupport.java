/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.request.codesystem;

import java.util.Optional;

import org.elasticsearch.common.Strings;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.request.CommitResult;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.ContactPoint;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.usagecontext.CodeableConceptUsageContext;

/**
 * @since 8.2.0
 */
public interface FhirCodeSystemCUDSupport {

	FhirCodeSystemCUDSupport DEFAULT = new FhirCodeSystemCUDSupport() {
	};

	/**
	 * Default method to create or update a FHIR resource
	 *  
	 * @param context - the context to use when looking for this code system
	 * @param codeSystem 
	 */
	default void updateOrCreateCodeSystem(ServiceProvider context, CodeSystem codeSystem) {
		int total = CodeSystemRequests.prepareSearchCodeSystem()
				.setLimit(0)
				.filterById(id(codeSystem))
				.buildAsync()
				.execute(context)
				.getTotal();
		
		if (total == 0) {
			create(context, codeSystem);
		} else {
			update(context, codeSystem);
		}
	}
	
	/**
	 * Implementers need to create the specified codesystem in their tooling
	 * @param context - the context to use when creating this code system
	 * @param codeSystem
	 */
	default String create(ServiceProvider context, CodeSystem codesystem) {
		final String id = id(codesystem);
		CommitResult result = CodeSystemRequests.prepareNewCodeSystem()
			.setContact(getContact(codesystem))
			.setCopyright(codesystem.getCopyright())
			.setDescription(codesystem.getDescription())
			.setId(id)
			.setLanguage(language(codesystem))
			.setOwner(codesystem.getPublisher())
			.setOid(getOid(codesystem))
			.setPurpose(codesystem.getPurpose())
			.setStatus(codesystem.getStatus().getCodeValue())
			.setTitle(title(codesystem))
			.setToolingId(codesystem.getToolingId())
			.setUrl(codesystem.getUrl().getUriValue())
			.setUsage(getUsage(codesystem))
			.build(user(context), String.format("Creating new codesystem %s", codesystem.getName()))
			.execute(context);
		return id;
	}
	
	/**
	 * Implementers need to update the specified codesystem in their tooling
	 * @param context - the context to use when creating this code system
	 * @param codeSystem
	 * 
	 */
	default String update(ServiceProvider context, CodeSystem codesystem) {
		final String id = id(codesystem);
		CodeSystemRequests.prepareUpdateCodeSystem(id)
			.setCopyright(codesystem.getCopyright())
			.setDescription(codesystem.getDescription())
			.setLanguage(language(codesystem))
			.setOid(getOid(codesystem))
			.setPurpose(codesystem.getPurpose())
			.setStatus(codesystem.getStatus().getCodeValue())
			.setTitle(title(codesystem))
			.setUrl(codesystem.getUrl().getUriValue())
			.setUsage(getUsage(codesystem))
			.setContact(getContact(codesystem))
			.build(context.service(User.class).getUsername(), String.format("Updating codesystem %s", codesystem.getName()))
			.execute(context);
		return id;
	}
	
	/**
	 * Implementers need to update the specified codesystem in their tooling
	 * @param context - the context to delete this code system from
	 * @param id
	 */
	default void delete(ServiceProvider context, String id) {
	}
	
	default String id(CodeSystem codesystem) {
		return codesystem.getName();
	}
	
	default String user(ServiceProvider context) {
		return context.service(User.class).getUsername();
	}
	
	default String language(CodeSystem codesystem) {
		return codesystem.getLanguage() != null ? codesystem.getLanguage().getCodeValue() : ""; 
	}
	
	private String getOid(CodeSystem codesystem) {
		Optional<Identifier> oidIdentifier = codesystem.getIdentifiers().stream().filter(i -> i.getSystem().isOid()).findFirst();
		return oidIdentifier.isPresent() ? oidIdentifier.get().getSystem().getOid() : "";
	}
	
	private String title(CodeSystem codesystem) {
		return Strings.isNullOrEmpty(codesystem.getTitle()) ? codesystem.getName() : codesystem.getTitle();
	}
	
	private String getUsage(CodeSystem codesystem) {
		String usage = "";
		
		if (codesystem.getUsageContexts() != null) {
			 Optional<CodeableConceptUsageContext> usageContext = codesystem.getUsageContexts()
				.stream()
				.filter(uc -> uc instanceof CodeableConceptUsageContext)
				.map(CodeableConceptUsageContext.class::cast)
				.findFirst();
			 
			 if (usageContext.isPresent()) {
				 usage = usageContext.get().getValue().getText();
			 }
		}
		return usage;
	}
	
	private String getContact(CodeSystem codesystem) {
		String contact = "";
		
		if (codesystem.getContacts() != null) {
			contact = codesystem.getContacts()
				.stream()
				.filter(detail -> detail.getTelecoms() != null)
				.flatMap(detail -> detail.getTelecoms().stream())
				.map(ContactPoint::getValue)
				.findFirst()
				.orElse("");
		}
		return contact;
	}
	
}
