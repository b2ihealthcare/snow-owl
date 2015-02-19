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
package com.b2international.snowowl.datastore.server.snomed.index.interest;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.util.Map;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.datastore.index.interest.ISearchProfileManager;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfile;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfilePreferences;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfileRule;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfiles;
import com.google.common.collect.ImmutableSet;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

/**
 */
public class SearchProfileServerManager implements ISearchProfileManager {

	public static final String SEARCH_PROFILE_NODE = "searchProfile";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchProfileServerManager.class);

	private File defaultsDirectory;
	
	public SearchProfileServerManager(File defaultsDirectory) {
		this.defaultsDirectory = checkNotNull(defaultsDirectory, "defaultsDirectory");
	}
	
	@Override
	public synchronized SearchProfilePreferences checkOut(final String userId) {
		
		final Preferences userPreferences = getUserPreferences(userId);

		final Map<String, SearchProfile> profiles = newHashMap();
		SearchProfile activeProfile = null;
		
		for (final SearchProfile immutableProfile : getBuiltInProfiles(defaultsDirectory)) {
			profiles.put(immutableProfile.getName(), immutableProfile);
		}
		
		try {
			
			if (userPreferences.nodeExists(SEARCH_PROFILE_NODE)) {
				
				final Preferences profileParentNode = userPreferences.node(SEARCH_PROFILE_NODE);
				
				for (final String childName : profileParentNode.childrenNames()) {
					final SearchProfile profile = new SearchProfile(profileParentNode.node(childName));
					profiles.put(profile.getName(), profile);
				}
				
				activeProfile = profiles.get(profileParentNode.get(SearchProfilePreferences.PROP_ACTIVE_PROFILE, null));
			}
			
		} catch (final BackingStoreException ignored) {
			// Fall through
		}
		
		if (null == activeProfile) {
			activeProfile = SearchProfiles.DEFAULT;
		}
		
		return new SearchProfilePreferences(profiles.values(), activeProfile);
	}
	
	private Preferences getUserPreferences(final String userId) {
		final ApplicationContext ctx = ApplicationContext.getInstance();
		final PreferencesService preferencesService = ctx.getService(PreferencesService.class);
		final Preferences userPreferences = preferencesService.getUserPreferences(userId);
		return userPreferences;
	}
	
	private ImmutableSet<SearchProfile> getBuiltInProfiles(final File defaultsPath) {
		final XStream xstream = new XStream(new Xpp3Driver());
		xstream.setClassLoader(SearchProfilePreferences.class.getClassLoader());
		xstream.alias("SearchProfilePreferences", SearchProfilePreferences.class);
		xstream.alias("SearchProfile", SearchProfile.class);
		xstream.alias("SearchProfileRule", SearchProfileRule.class);
		
		ImmutableSet<SearchProfile> builtInProfiles = null;
		
		try {
			builtInProfiles = ImmutableSet.<SearchProfile>copyOf(((SearchProfilePreferences) xstream.fromXML(new File(defaultsPath, "searchProfileDefaults.xml"))).getProfiles());
		} catch (final StreamException ignored) {
			// Assume file could not be read 
		}
		
		if (null == builtInProfiles) {
			return getFallbackBuiltInProfiles(); 
		}
		
		for (final SearchProfile searchProfile : builtInProfiles) {
			searchProfile.setMutable(false);
		}
		
		return builtInProfiles;
	}

	private ImmutableSet<SearchProfile> getFallbackBuiltInProfiles() {
		return SearchProfiles.BUILT_IN;
	}

	@Override
	public synchronized void checkIn(final String userId, final SearchProfilePreferences profilePreferences) {
		
		final Preferences userPreferences = getUserPreferences(userId);
		
		try {
			
			final Preferences profileParentNode = userPreferences.node(SEARCH_PROFILE_NODE);
			profileParentNode.removeNode();
			userPreferences.flush();
			
		} catch (final BackingStoreException e) {
			LOGGER.error("Caught exception when removing search profile preference node.", e);
			return;
		}
		
		final Preferences profileParentNode = userPreferences.node(SEARCH_PROFILE_NODE);
		profileParentNode.put(SearchProfilePreferences.PROP_ACTIVE_PROFILE, profilePreferences.getActiveProfile().getName());
			
		for (final SearchProfile profile : profilePreferences.getProfiles()) {
			try {
				profile.save(profileParentNode);
			} catch (final BackingStoreException e) {
				LOGGER.error("Caught exception when saving search profile.", e);
				continue;
			}
		}
		
		try {
			userPreferences.flush();
		} catch (final BackingStoreException e) {
			LOGGER.error("Caught exception when persisting search profile changes.", e);
			return;
		}
	}

	@Override
	public synchronized SearchProfile getActiveProfile(final String userId) {
		return checkOut(userId).getActiveProfile();
	}
}