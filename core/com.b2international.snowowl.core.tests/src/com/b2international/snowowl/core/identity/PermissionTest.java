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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PermissionTest {
	
	@Test
	public void missingResourceTest() {
		
		 Exception exception = assertThrows(IllegalArgumentException.class, () -> {
		       Permission.of(Permission.OPERATION_BROWSE, "");
		 });
		 
	    String expectedMessage = "Resource must be specified.";
	    assertEquals(exception.getMessage(), expectedMessage);
	}
	
	@Test
	public void operationWithoutResourceTest() {
		Permission permission = Permission.of(Permission.OPERATION_BROWSE, "*");
		assertNotNull(permission);
		
		assertEquals(Permission.OPERATION_BROWSE, permission.getOperation());
		assertEquals("*", permission.getResource());
		
		//No resource allows permissions with no resource
		assertTrue(permission.implies(permission));
		
		//No resource does not mean 'everything'
		Permission permissionWithResource = Permission.of(Permission.OPERATION_BROWSE, "icdStore");
		assertTrue(permission.implies(permissionWithResource));
		
		Permission differentPermission = Permission.of(Permission.OPERATION_EDIT, "*");
		assertFalse(permission.implies(differentPermission));
	}

}
