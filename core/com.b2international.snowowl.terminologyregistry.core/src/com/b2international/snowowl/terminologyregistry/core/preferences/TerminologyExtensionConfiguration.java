/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core;

import java.io.File;
import java.util.Collection;

import org.osgi.service.prefs.PreferencesService;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.preferences.ConfigNode;
import com.b2international.snowowl.core.api.preferences.PreferenceBase;
import com.b2international.snowowl.core.api.preferences.io.ConfigurationEntrySerializer;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.UserBranchPathMap;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimaps;

/**
 *	 
 * 	@author endre
 */
public class TerminologyExtensionConfiguration extends PreferenceBase {

	private static final String NODE_NAME = "Terminology extensions";

	private static final String CODE_SYSTEMS_KEY = "com.b2international.snowowl.terminologyregistry.core.codeSystems";
	
	public ConfigurationEntrySerializer<ConfigNode<String, ICodeSystem>> codeSystemSerializer;
	
	public TerminologyExtensionConfiguration(PreferencesService preferencesService, File defaultsPath) {
		super(preferencesService, NODE_NAME);
		init(defaultsPath);
	}

	private void init(File defaultsPath) {
		
		codeSystemSerializer = new ConfigurationEntrySerializer<ConfigNode<String, ICodeSystem>>(preferences, CODE_SYSTEMS_KEY, new File(defaultsPath, "extensions.xml")) {
			
			@Override
			protected ConfigNode<String, ICodeSystem> computeDefault() {
				
				ConfigNode<String, ICodeSystem> configNode = new ConfigNode<String, ICodeSystem>(CODE_SYSTEMS_KEY);

				
				Collection<ICodeSystem> codeSystems = ApplicationContext.getInstance().getService(TerminologyRegistryService.class).getCodeSystems(new UserBranchPathMap());
				ImmutableListMultimap<String, ICodeSystem> repositoryTocodeSystemMultiMap = Multimaps.index(codeSystems, new Function<ICodeSystem, String>() {
					@Override
					public String apply(ICodeSystem input) {
						return input.getRepositoryUuid();
					}
				});
				
				ImmutableMap<String, Collection<ICodeSystem>> asMap = repositoryTocodeSystemMultiMap.asMap();
				for (String key : asMap.keySet()) {
					Collection<ICodeSystem> collection = asMap.get(key);
					
					if (collection.isEmpty())
						continue;
					
					configNode.addChild(key, Iterables.getLast(collection));
				}

				return configNode;
			}
			
		};
	}

	public ConfigurationEntrySerializer<ConfigNode<String, ICodeSystem>> getCodeSystemSerializer() {
		return codeSystemSerializer;
	}
}
