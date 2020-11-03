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

import com.b2international.snowowl.core.CoreActivator;
import com.b2international.snowowl.core.util.PlatformUtil;

/**
 * Property tester for development version test. Returns <code>true</code> if the running SnowOwl is a dev version,
 * return <code>false</code> in production environments or in server environments.
 * 
 * @since 3.0
 */
public class DevelopmentVersionPropertyTester extends PropertyTester {

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		String bundle = args.length > 0 ? (String) args[0] : CoreActivator.PLUGIN_ID;
		return PlatformUtil.isDevVersion(bundle);
	}

}