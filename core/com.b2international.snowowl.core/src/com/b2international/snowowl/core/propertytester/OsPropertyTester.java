/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.propertytester;

import org.eclipse.core.expressions.PropertyTester;

import com.b2international.commons.SystemUtils;

/**
 * Property tester for testing the OS.
 * 
 * @since 3.2
 */
public class OsPropertyTester extends PropertyTester {
	
	private static final String MAC = "mac";
	private static final String WINDOWS = "windows";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		
		if (MAC.equals(property)) {
			return SystemUtils.isMac();
		} else if (WINDOWS.equals(property)) {
			return SystemUtils.isWindows();
		}
		
		return false;
	}

}