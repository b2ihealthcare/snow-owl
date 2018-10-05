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
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.snomed.api.rest.io.SnomedImportApiTest;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 7.0
 */
public class SnomedAttachmentRestRequestTest extends AbstractSnomedApiTest {
	
	private final String fileName = "SnomedCT_Release_INT_20150131_index_init_bug.zip";
	
	@Test
	public void testFileUploadWithValidId() {
		final String attachmentId = UUID.randomUUID().toString();
		uploadAttachment(attachmentId, fileName).statusCode(204);
	}
	
	@Test
	public void testFileUploadWithInvalidId() throws IllegalArgumentException {
		final String attachmentId = "20";
		try {
			uploadAttachment(attachmentId, fileName).statusCode(404);
		} catch (IllegalArgumentException e) {
			
		}
		
		fail();
	}
	
	@Test
	public void testRetrievalOfUploadedAttachment() {
		final String attachmentId = UUID.randomUUID().toString();
		uploadAttachment(attachmentId, fileName).statusCode(204);
		getAttachment(attachmentId).statusCode(200);
	}
	
	@Test
	public void testRetrievalOfUploadedAttachmentWithErroneousId() throws IllegalArgumentException {
		final String attachmentId = "30";
		try {
			getAttachment(attachmentId).statusCode(404);
		} catch (IllegalArgumentException e) {
			
		}
		// exception should be thrown
		fail();
	}
	
	@Test
	public void testRetrievalOfUploadedAttachmentWithNonExistantId() {
		final String attachmentId = UUID.randomUUID().toString();
		uploadAttachment(attachmentId, fileName).statusCode(204);
		
		try {
			final String nonExistantAttachmentId = UUID.randomUUID().toString();
			
			getAttachment(nonExistantAttachmentId).statusCode(404);
			
		} catch (NotFoundException e) {
			
		}
		
		fail();
	}
	
	@Test
	public void testDeletionOfAttachment() {
		final String attachmentId = UUID.randomUUID().toString();
		uploadAttachment(attachmentId, fileName).statusCode(204);
		
		getAttachment(attachmentId).statusCode(200);
		
		deleteAttachment(attachmentId).statusCode(204);
	}
	
	@Test
	public void testDeletionOfNonExistantAttachment() {
		final String attachmentId = UUID.randomUUID().toString();
		uploadAttachment(attachmentId, fileName).statusCode(204);
		
		getAttachment(attachmentId).statusCode(200);
			final String nonExistantAttachmentId = UUID.randomUUID().toString();
			deleteAttachment(nonExistantAttachmentId).statusCode(404);
		
		fail();
	}
	
	private ValidatableResponse uploadAttachment(String attachmentId, String fileName) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.multiPart(new File(PlatformUtil.toAbsolutePath(SnomedImportApiTest.class, fileName)))
				.post("/attachments/{attachmentId}", attachmentId)
				.then();
	}
	
	private ValidatableResponse getAttachment(String attachmentId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.get("/attachments/{attachmentId}", attachmentId)
				.then();
	}
	
	private ValidatableResponse deleteAttachment(String attachmentId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.delete("/attachments/{attachmentId}", attachmentId)
				.then();
	}
	
}
