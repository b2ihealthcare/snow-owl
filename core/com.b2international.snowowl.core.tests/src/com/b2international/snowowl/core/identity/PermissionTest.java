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
package com.b2international.snowowl.core.identity;

import static com.b2international.snowowl.core.identity.Permission.OPERATION_BROWSE;
import static com.b2international.snowowl.core.identity.Permission.OPERATION_IMPORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * @since 7.7.0
 *
 */
public class PermissionTest {
	
	@Test
	public void missingResourceTest() {
		
		 Exception exception = assertThrows(IllegalArgumentException.class, () -> {
		       Permission.of(OPERATION_BROWSE, "");
		 });
		 
	    String expectedMessage = "Resource must be specified.";
	    assertEquals(exception.getMessage(), expectedMessage);
	}
	
	@Test
	public void pathUriResourceTest() {
		
		String extensionUri = "SNOMEDCT-EXT/2020-01-31";
		Permission resourceToAuthorize = Permission.of(OPERATION_BROWSE, extensionUri);
		
		Permission userPermission = Permission.of(OPERATION_BROWSE, "SNOMEDCT-EXT/2020-01-31");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, "SNOMEDCT-EXT/2020-01-3?");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, "SNOMEDCT-EXT/*");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, "*SNOMEDCT-EXT/*");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, "*/SNOMEDCT-EXT/*");
		assertFalse(userPermission.implies(resourceToAuthorize));

		userPermission = Permission.of(OPERATION_BROWSE, "SNOMEDCT-EXT2/2020-01-31");
		assertFalse(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, "SNOMEDCT-EXT/2020-01-*");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, "SNOMEDCT-???/2020-01-31");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, "*/2020-01-31/*");
		assertFalse(userPermission.implies(resourceToAuthorize));
	}
	
	@Test
	public void noPathUriResourceTest() {
		
		String extensionUri = "SNOMEDCT-EXT";
		Permission resourceToAuthorize = Permission.of(OPERATION_BROWSE, extensionUri);
		
		Permission userPermission = Permission.of(OPERATION_IMPORT, extensionUri);
		assertFalse(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, extensionUri);
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, "*");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, "*EXT");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, "*/*");
		assertFalse(userPermission.implies(resourceToAuthorize));
		
		userPermission = Permission.of(OPERATION_BROWSE, "?NOMEDCT-EX?");
		assertTrue(userPermission.implies(resourceToAuthorize));
		
	}
	
}
