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
package com.b2international.snowowl.core.propertyhelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class SnowOwlPropertyHelper {
	
	private static final String PROPERTY_FILE_DEFAULT = "snowowl.properties.default";
	private static final String PROPERTY_FILE = "snowowl.properties";
	
	public static Properties getProperties() throws IOException {
		Properties properties = new Properties();
		
		InputStream propertiesInputStream = SnowOwlPropertyHelper.class.getResourceAsStream(PROPERTY_FILE);
		
		if(propertiesInputStream == null){
			propertiesInputStream = SnowOwlPropertyHelper.class.getResourceAsStream(PROPERTY_FILE_DEFAULT);
		}
		
		properties.load(propertiesInputStream);
		
		return properties;
	}
	
	private SnowOwlPropertyHelper() { }
}