/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
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
		public static final String MINI_RF2_INT = createResourcePath(SNOMED_RESOURCES, "MiniCT_INT_GB_Full_20140131.zip");
		public static final String MINI_RF2__INT_20160131 = createResourcePath(SNOMED_RESOURCES, "SnomedCT_RF2Release_INT_20160131_minified.zip");
		public static final String MINI_RF2_EXT = createResourcePath(SNOMED_RESOURCES, "SnomedCT_RF2Release_INT_20160501_B2i_cd_refsets.zip");
		public static final String MINI_RF2_SG = createResourcePath(SNOMED_RESOURCES, "MiniCT_INT_SG_Copied_GB_language_Delta_20140123.zip");
		
		private Snomed() {
		}
		
	}
	
	private static String createResourcePath(String parent, String child) {
		return String.format("%s%s%s", parent, SEPARATOR, child);
	}

}
