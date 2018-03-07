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
package com.b2international.snowowl.snomed.importer.release;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.release.SimpleReleaseFile.SimpleReleaseComponentType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Configures the two release file set selector instances from an externally supplied property file.
 * 
 */
public final class ReleaseFileSetSelectors {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseFileSetSelectors.class);
	
	public static final ReleaseFileSetSelector SELECTORS;
	
	private static Properties getProperties() {
		final Properties properties = new Properties();
		final InputStream propertiesInputStream = ReleaseFileSetSelectors.class.getResourceAsStream("releases.properties");
		
		try {
			properties.load(propertiesInputStream);
		} catch (final IOException e) {
			LOGGER.error("Couldn't load release file configuration.", e);
		}
		
		return properties;
	}

	private static ReleaseFileSetSelector parseProperties(final Properties properties, final String prefix) {
		
		int idx = 0;
		final ImmutableList.Builder<ReleaseFileSet> releaseFileSetBuilder = ImmutableList.builder();
		
		while (properties.containsKey(prefix + "." + idx + ".terminologyRoot")) {

			releaseFileSetBuilder.add(
					createSnapshotReleaseFileSet(
							getProperty(properties, prefix, idx, "terminologyRoot"), 
							getProperty(properties, prefix, idx, "refSetRoot"), 
							getProperty(properties, prefix, idx, "languageRefSetRoot"),
							Boolean.valueOf(getProperty(properties, prefix, idx, "includesStatedRelationships")),
							Boolean.valueOf(getProperty(properties, prefix, idx, "testRelease")), 
							getListProperty(properties, prefix, idx, "refSetPaths")));
			
			idx++;
		}
		
		return new ReleaseFileSetSelector(releaseFileSetBuilder.build());
	}
	
	private static List<String> getListProperty(final Properties properties, final String prefix, final int idx, final String key) {

		int subIdx = 0;
		final ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
		
		String item;
		
		while ((item = getListItemProperty(properties, prefix, idx, key, subIdx)) != null) {
			listBuilder.add(item);
			subIdx++;
		}
		
		return listBuilder.build();
	}

	private static String getListItemProperty(final Properties properties, final String prefix, final int idx, final String key, final int subIdx) {
		 return properties.getProperty(prefix + "." + idx + "." + key + "." + subIdx);
	}

	private static String getProperty(final Properties properties, final String prefix, final int idx, final String key) {
		return properties.getProperty(prefix + "." + idx + "." + key);
	}

	private static ReleaseFileSet createSnapshotReleaseFileSet(final String terminologyRoot, 
			final String refSetRoot,
			final String languageRefSetRoot, 
			final boolean includesStatedRelationships, 
			final boolean testRelease, 
			final List<String> refSetPaths) {
		
		final ImmutableMap.Builder<ReleaseComponentType, ReleaseFile> componentMapBuilder = ImmutableMap.builder();
		
		componentMapBuilder.put(ReleaseComponentType.CONCEPT, 
				new SimpleReleaseFile(terminologyRoot, SimpleReleaseComponentType.CONCEPT));
		componentMapBuilder.put(ReleaseComponentType.RELATIONSHIP, 
				new SimpleReleaseFile(terminologyRoot, SimpleReleaseComponentType.RELATIONSHIP));
		componentMapBuilder.put(ReleaseComponentType.DESCRIPTION, 
				new DescriptionReleaseFile(terminologyRoot));
		componentMapBuilder.put(ReleaseComponentType.LANGUAGE_REFERENCE_SET, 
				new LanguageRefsetReleaseFile(languageRefSetRoot));
		componentMapBuilder.put(ReleaseComponentType.TEXT_DEFINITION,
				new TextDefinitionReleaseFile(terminologyRoot));
		
		if (includesStatedRelationships) {
			componentMapBuilder.put(ReleaseComponentType.STATED_RELATIONSHIP, 
					new SimpleReleaseFile(terminologyRoot, SimpleReleaseComponentType.STATEDRELATIONSHIP));
		}
		
		return new ReleaseFileSet(testRelease, componentMapBuilder.build(), refSetPaths);
	}
	
	static {
		final Properties properties = getProperties();
		SELECTORS = parseProperties(properties, "selector");
	}
	
	private ReleaseFileSetSelectors() {
		// Prevent instantiation
	}
}