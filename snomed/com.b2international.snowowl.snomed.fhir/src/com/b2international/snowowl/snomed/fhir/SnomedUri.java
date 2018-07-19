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
package com.b2international.snowowl.snomed.fhir;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;

/**
 * SnomedUri
 * 
 * @see <a href="https://confluence.ihtsdotools.org/display/DOCURI">SNOMED CT URI specification</a>
 * @since 6.7
 */
public class SnomedUri {
	
	public static final String VERSION_PATH_SEGMENT = "version"; //$NON-NLS-N$
	public static final String SNOMED_BASE_URI = "http://snomed.info/sct"; //$NON-NLS-N$

	private String uriString;
	private String extensionModuleId;
	private String versionTag;
	
	public SnomedUri(String uriString) {
		this.uriString = uriString;
		parseUri(uriString);
	}
	
	private void parseUri(String uriString) {
		
		Path uriPath = Paths.get(uriString);
		
		if (!uriPath.startsWith(SNOMED_BASE_URI)) {
			throw new IllegalArgumentException(String.format("URI '%s' is not a valid SNOMED CT URI. It should start as '%s'.", uriString, SNOMED_BASE_URI));
		}
		
		Path relativeUri = Paths.get(SNOMED_BASE_URI).relativize(uriPath);
		
		Iterator<Path> pathIterator = relativeUri.iterator();
	
		//this should not happen
		if (!pathIterator.hasNext()) return;
		
		//extension
		String pathSegment = pathIterator.next().toString();
			
		//No extension or version definition provided - SNOMED CT INT Edition
		if (StringUtils.isEmpty(pathSegment)) return;
		
		if (!SnomedIdentifiers.isValid(pathSegment.toString())) {
			throw new IllegalArgumentException(String.format("Invalid extension module ID [%s] defined.", pathSegment));
		} else {
			extensionModuleId = pathSegment;
		}
		
		//version parameter
		if (!pathIterator.hasNext()) return;
		String versionParameterKeySegment = pathIterator.next().toString();
		if (!VERSION_PATH_SEGMENT.equals(versionParameterKeySegment)) {
			throw new IllegalArgumentException(String.format("Invalid path segment [%s], 'version' expected.", versionParameterKeySegment));
		}
		
		//Version tag
		if (!pathIterator.hasNext()) {
			throw new IllegalArgumentException(String.format("No version tag is specified after the 'version' parameter."));
		}
		versionTag = pathIterator.next().toString();
		//to validate
		try {
			EffectiveTimes.parse(versionTag, DateFormats.SHORT);
		} catch(RuntimeException re) {
			throw new IllegalArgumentException(String.format("Could not parse version date [%s].", versionTag), re);
		}
	}
	
	public String getExtensionModuleId() {
		return extensionModuleId;
	}
	public String getVersionTag() {
		return versionTag;
	}
	
	/**
	 * Returns the standard URI string for this SNOMED CT URI
	 * @return
	 */
	public String toUri() {
		return uriString;
	}
	
}