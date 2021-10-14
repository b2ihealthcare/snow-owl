/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.identity;

import static com.b2international.snowowl.core.identity.Permission.OPERATION_BROWSE;
import static com.b2international.snowowl.core.identity.Permission.OPERATION_IMPORT;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @since 7.7.0
 *
 */
public class PermissionTest {
	
	@Test
	public void requireAllMissingResource() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			Permission.requireAll(OPERATION_BROWSE, "");
		});
		 
	    String expectedMessage = "Resource descriptor cannot be null or empty.";
	    assertEquals(exception.getMessage(), expectedMessage);
	}
	
	@Test
	public void requireAnyMissingResource() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			Permission.requireAny(OPERATION_BROWSE, "");
		});
		 
	    String expectedMessage = "Resource descriptor cannot be null or empty.";
	    assertEquals(exception.getMessage(), expectedMessage);
	}
	
	@Test
	public void pathUriResourceTest() {
		
		Permission resourceToAuthorize = Permission.requireAll(OPERATION_BROWSE, "SNOMEDCT-EXT/2020-01-31");
		
		Permission userPermission = Permission.requireAll(OPERATION_BROWSE, "SNOMEDCT-EXT/2020-01-31");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, "SNOMEDCT-EXT/2020-01-3?");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, "SNOMEDCT-EXT/*");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, "*SNOMEDCT-EXT/*");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, "*/SNOMEDCT-EXT/*");
		assertFalse(userPermission.implies(resourceToAuthorize));

		userPermission = Permission.requireAll(OPERATION_BROWSE, "SNOMEDCT-EXT2/2020-01-31");
		assertFalse(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, "SNOMEDCT-EXT/2020-01-*");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, "SNOMEDCT-???/2020-01-31");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, "*/2020-01-31/*");
		assertFalse(userPermission.implies(resourceToAuthorize));
		
	}
	
	@Test
	public void authorizeAnyPermission() throws Exception {
		Permission userPermission = Permission.requireAll(OPERATION_BROWSE, "SNOMEDCT-UK-CL*");
		assertTrue(userPermission.implies(Permission.requireAny(OPERATION_BROWSE, "codeSystems/SNOMEDCT-UK-CL", "SNOMEDCT-UK-CL")));
		assertTrue(userPermission.implies(Permission.requireAny(OPERATION_BROWSE, "codeSystems/SNOMEDCT-UK-CL/2021-03-17", "SNOMEDCT-UK-CL/2021-03-17")));
	}
	
	@Test
	public void noPathUriResourceTest() {
		
		String extensionUri = "SNOMEDCT-EXT";
		Permission resourceToAuthorize = Permission.requireAll(OPERATION_BROWSE, extensionUri);
		
		Permission userPermission = Permission.requireAll(OPERATION_IMPORT, extensionUri);
		assertFalse(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, extensionUri);
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, "*");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, "*EXT");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, "*/*");
		assertFalse(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.requireAll(OPERATION_BROWSE, "?NOMEDCT-EX?");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
	}
	
}
