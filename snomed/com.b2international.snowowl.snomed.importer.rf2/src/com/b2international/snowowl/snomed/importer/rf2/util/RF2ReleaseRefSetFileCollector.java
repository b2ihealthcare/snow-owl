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
package com.b2international.snowowl.snomed.importer.rf2.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.b2international.commons.ZipURLHandler;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration.ImportSourceKind;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet;
import com.google.common.collect.Sets;

/**
 * Based on the import configuration this helper class collects all the available refset files which have to be imported.
 * 
 */
public class RF2ReleaseRefSetFileCollector {
	
	public static Set<URL> collectUrlFromRelease(final ImportConfiguration configuration) {

		final ReleaseFileSet releaseFileSet = configuration.getReleaseFileSet();
		
		if (releaseFileSet == null) {
			return Collections.<URL>emptySet();
		}
		
		final List<String> refSetsRelativePaths = releaseFileSet.getRefSetPaths();
		final String relativeRoot = releaseFileSet.getRelativeRoot();
		final Set<URL> collectedUrlSet = Sets.newHashSet();
		
		for(final String refSetPath : refSetsRelativePaths) {
			
			Set<URL> parsedRefSetUrl = Sets.newHashSet();
			
			if (configuration.getSourceKind().equals(ImportSourceKind.ARCHIVE)) {
				parsedRefSetUrl = parseZip(configuration.getArchiveFile(), relativeRoot, refSetPath);
			} else if (configuration.getSourceKind().equals(ImportSourceKind.ROOT_DIRECTORY)) {
				parsedRefSetUrl = parseDirectory(configuration.getRootFile(), relativeRoot, refSetPath);
			}
			
			collectedUrlSet.addAll(parsedRefSetUrl);
		}

		// Add reference set URLs from the first wizard page in case they're not present
		try {
			
			for (File langFiles : configuration.getLanguageRefSetFiles()) {
				collectedUrlSet.add(configuration.toURL(langFiles));
			}
			
			if (null != configuration.getDescriptionType()) {
				collectedUrlSet.add(configuration.toURL(configuration.getDescriptionType()));
			}
			
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return collectedUrlSet;
	}
	
	private static Set<URL> parseDirectory(final File rootDirectory, final String relativeRoot, final String relativePath) {
		Set<URL> filesToImport = new HashSet<URL>();
		
		final File refSetRootDirectory = new File(rootDirectory, relativeRoot);
		final File refSetDirectory = new File(refSetRootDirectory, relativePath);
		
		final File[] directoryContents = refSetDirectory.listFiles();
		
		if (directoryContents != null) {
			for (final File file : directoryContents) {
				
				try {
					filesToImport.add(file.toURI().toURL());
				} catch (final MalformedURLException e) {
					e.printStackTrace();
				}
			}
		} else {
			filesToImport = Collections.<URL>emptySet();
		}
		
		return filesToImport;
	}
	
	private static Set<URL> parseZip(final File archiveFile, final String relativeRoot, final String relativePath) {
		final Set<URL> filesToImport = new HashSet<URL>();
		
		try {
			
			final ZipFile zipFile = new ZipFile(archiveFile);
			final Enumeration<? extends ZipEntry> entries = zipFile.entries();
			
			while(entries.hasMoreElements()) {

				final ZipEntry nextElement = entries.nextElement();
				if (!nextElement.isDirectory()) {
				
					final String name = nextElement.getName();
					
					if (name.indexOf(relativeRoot + relativePath) > -1) {
						final URL url = ZipURLHandler.createURL(archiveFile, name);
						filesToImport.add(url);
					}
				}
			}
			
			zipFile.close();
			
		} catch (final IOException e) {
			e.printStackTrace();
		} 
		
		return filesToImport;
	}
}