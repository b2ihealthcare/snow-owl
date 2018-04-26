/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons;


/**
 * Abstract class containing paths to various terminology content resource.
 * 
 * @since 3.6
 */
public class Resources {

	private static final String SEPARATOR = "/";
	public static final String RESOURCES = "resources";
	
	private Resources() {
	}
	
	/**
	 * Resource class containing paths to SNOMED CT RF2 import content.
	 * 
	 * @since 3.6
	 */
	public static class Snomed {

		public static final String SNOMED_RESOURCES = createResourcePath(RESOURCES, "snomed");
		public static final String MINI_RF2_INT = createResourcePath(SNOMED_RESOURCES, "SnomedCT_InternationalRF2_PRODUCTION_20170731T150000Z-minified.zip");
		public static final String MINI_RF2_EXT = createResourcePath(SNOMED_RESOURCES, "SnomedCT_RF2Release_INT_20160501_B2i_cd_refsets.zip");
		
		private Snomed() {
		}
		
	}
	
	private static String createResourcePath(String parent, String child) {
		return String.format("%s%s%s", parent, SEPARATOR, child);
	}

}
