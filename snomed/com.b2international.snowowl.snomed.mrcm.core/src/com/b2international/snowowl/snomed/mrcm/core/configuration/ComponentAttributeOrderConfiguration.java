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
package com.b2international.snowowl.snomed.mrcm.core.configuration;


import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.PreferencesService;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.preferences.PreferenceBase;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

/**
 * @deprecated - UNSUPPORTED, see {@link SnomedSimpleTypeRefSetAttributeConfiguration}
 */
public class ComponentAttributeOrderConfiguration extends PreferenceBase {

	/*unique ID of this configuration node*/
	private static final String NODE_NAME = 
			"com.b2international.snowowl.snomed.refset.core.preferences.ComponentAttributeOrderConfiguration";
	
	private final Map<String, SnomedSimpleTypeRefSetAttributeConfiguration> refSetIdConfigurationMap = Maps.newHashMap();
	private final XStream xstream;
	
	public ComponentAttributeOrderConfiguration(final PreferencesService preferencesService, final File defaultsPath) {
		super(preferencesService, NODE_NAME);
		xstream = new XStream(new Xpp3Driver());
		xstream.setClassLoader(this.getClass().getClassLoader());
		init(defaultsPath);
	}
	
	private void init(File defaultsPath) {
		
		try {
			
			String[] storedKeys = preferences.keys();
			
			if (0 == storedKeys.length) {
				
				try {
					ImmutableMap<String, SnomedSimpleTypeRefSetAttributeConfiguration> defaultRefSetIdConfigurationMap = (ImmutableMap<String, SnomedSimpleTypeRefSetAttributeConfiguration>) 
							xstream.fromXML(new File(defaultsPath, "attributeOrder.xml"));
					
					refSetIdConfigurationMap.putAll(defaultRefSetIdConfigurationMap);
				} catch (StreamException ignored) {
					// Assume defaults file could not be read
				}
				
			} else {
				
				for (String key : storedKeys) {
					
					String xml = preferences.get(key, null);
					
					if (!StringUtils.isEmpty(xml)) {
						refSetIdConfigurationMap.put(key, (SnomedSimpleTypeRefSetAttributeConfiguration) xstream.fromXML(xml));
					}
				}
			}
			
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public SnomedSimpleTypeRefSetAttributeConfiguration getConfiguration(final String refSetId) {
		return refSetIdConfigurationMap.get(refSetId);
	}
	
	public void savePreferences() {
		
		try {
			preferences.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		
		for (Entry<String, SnomedSimpleTypeRefSetAttributeConfiguration> configEntry : refSetIdConfigurationMap.entrySet()) {
			preferences.put(configEntry.getKey(), xstream.toXML(configEntry.getValue()));
		}
	}
	
	public void add(final String refSetId, final SnomedSimpleTypeRefSetAttributeConfiguration configuration) {
		refSetIdConfigurationMap.put(refSetId, configuration);
	}
}