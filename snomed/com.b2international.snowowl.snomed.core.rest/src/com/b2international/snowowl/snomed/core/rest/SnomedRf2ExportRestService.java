/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.attachments.InternalAttachmentRegistry;
import com.b2international.snowowl.core.domain.ExportResult;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRf2ExportConfiguration;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 7.5
 */
@Api(value = "Export", description="Export", tags = "export")
@Controller
@RequestMapping(value="/{path:**}/export")
public class SnomedRf2ExportRestService extends AbstractSnomedRestService {

	@Autowired
	private AttachmentRegistry attachments;
	
	@ApiOperation(
		value="Export SNOMED CT content to RF2", 
		notes="Exports SNOMED CT content from the given branch to RF2."
	)
	@ApiResponses({
		@ApiResponse(code=200, message="OK")
	})
	@GetMapping
	public @ResponseBody ResponseEntity<?> export(
			@ApiParam(value = "The branch path", required = true)
			@PathVariable(value="path")
			final String branch,

			final SnomedRf2ExportConfiguration params,
			
			@ApiParam(value = "Accepted language tags, in order of preference")
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		final Rf2RefSetExportLayout globalExportLayout = ApplicationContext.getServiceForClass(SnomedCoreConfiguration.class).getExport().getRefSetExportLayout();
		
		final ExportResult exportedFile = SnomedRequests.rf2().prepareExport()
			.setReleaseType(params.getType() == null ? null : Rf2ReleaseType.getByNameIgnoreCase(params.getType()))
			.setExtensionOnly(params.isExtensionOnly())
			.setLocales(acceptLanguage)
			.setIncludePreReleaseContent(params.isIncludeUnpublished())
			.setModules(params.getModuleIds())
			.setRefSets(params.getRefSetIds())
			.setCountryNamespaceElement(params.getNamespaceId())
			// .setNamespaceFilter(namespaceFilter) is not supported on REST, yet
			.setTransientEffectiveTime(params.getTransientEffectiveTime())
			.setStartEffectiveTime(params.getStartEffectiveTime())
			.setEndEffectiveTime(params.getEndEffectiveTime())
			.setRefSetExportLayout(params.getRefSetLayout() == null ? globalExportLayout : Rf2RefSetExportLayout.getByNameIgnoreCase(params.getRefSetLayout()))
			.build(this.repositoryId, branch)
			.execute(getBus())
			.getSync();
		
		final File file = ((InternalAttachmentRegistry) attachments).getAttachment(exportedFile.getRegistryId());
		final Resource exportZipResource = new FileSystemResource(file);
		
		final HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.setContentDispositionFormData("attachment", exportedFile.getName());

		// TODO figure out a smart way to cache export results, probably it could be tied to commitTimestamps/versions/etc. 
		file.deleteOnExit();
		return new ResponseEntity<>(exportZipResource, httpHeaders, HttpStatus.OK);
	}
	
}
