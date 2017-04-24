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
package com.b2international.snowowl.snomed.datastore.index.interest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.b2international.commons.beans.BeanPropertyChangeSupporter;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

/**
 * Collects all known search profiles for a user and records the currently applicable profile.
 *
 */
@XStreamAlias("SearchProfilePreferences")
public class SearchProfilePreferences extends BeanPropertyChangeSupporter implements Serializable {

	private static final long serialVersionUID = -1306119461047966461L;
	
	public static final String PROP_ACTIVE_PROFILE = "activeProfile";
	public static final String PROP_PROFILES = "profiles";
	
	private final LinkedHashSet<SearchProfile> profiles = Sets.newLinkedHashSet();
	
	private SearchProfile activeProfile;
	
	private SearchProfile savedActiveProfile;

	public SearchProfilePreferences(final Collection<SearchProfile> profiles, final SearchProfile activeProfile) {
		this.profiles.addAll(profiles);
		this.activeProfile = activeProfile;
		this.savedActiveProfile = activeProfile;
	}

	public void addProfile(@Nonnull final SearchProfile profile) {
		checkNotNull(profile, "profile");
		
		if (profiles.add(profile)) {
			firePropertyChange(PROP_PROFILES, null, null);
		}
	}
	
	public void removeProfile(@Nonnull final SearchProfile profile) {
		checkNotNull(profile, "profile");
		
		if (profiles.remove(profile)) {
			firePropertyChange(PROP_PROFILES, null, null);
		}
	}
	
	public @Nonnull Set<SearchProfile> getProfiles() {
		return profiles;
	}
	
	public @Nonnull SearchProfile getActiveProfile() {
		return activeProfile;
	}
	
	public void setActiveProfile(@Nonnull final SearchProfile activeProfile) {
		checkNotNull(activeProfile, "activeProfile");
		checkArgument(profiles.contains(activeProfile), "Couldn't find profile with name '%s'.", activeProfile.getName());
		
		final SearchProfile oldActiveProfile = this.activeProfile;
		this.activeProfile = activeProfile;
		firePropertyChange(PROP_ACTIVE_PROFILE, oldActiveProfile, activeProfile);
	}
	
	public boolean isActiveProfileChanged() {
		return savedActiveProfile != activeProfile;
	}
	
	public void exportToXml(@Nonnull final String xmlPath) throws IOException {

		FileOutputStream outputStream = null;

		try {
			outputStream = new FileOutputStream(xmlPath);
			final XStream xStream = new XStream(new Xpp3Driver());
			xStream.processAnnotations(SearchProfilePreferences.class);
			xStream.toXML(this, outputStream);
		} finally {
			Closeables.close(outputStream, true);
		}
	}
	
	public void importFromXml(@Nonnull final String xmlPath) throws IOException {

		FileInputStream inputStream = null;

		try {
			
			inputStream = new FileInputStream(xmlPath);
			final XStream xStream = new XStream(new Xpp3Driver());
			xStream.processAnnotations(SearchProfilePreferences.class);
			xStream.setClassLoader(getClass().getClassLoader());
			final SearchProfilePreferences readManager = (SearchProfilePreferences) xStream.fromXML(inputStream);
			
			if (null != readManager && profiles.addAll(readManager.profiles)) {
				firePropertyChange(PROP_PROFILES, null, null);
			}
			
		} finally {
			Closeables.closeQuietly(inputStream);
		}
	}

	@Override
	protected Object readResolve() throws ObjectStreamException {
		super.readResolve();
		return this;
	}
}