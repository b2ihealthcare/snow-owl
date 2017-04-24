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
package com.b2international.snowowl.scripting.core;

import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

/**
 * Singleton for collecting all example scripts registered via <i>scripts</i> extension-point.
 */
public enum ExampleScriptCollector {

	INSTANCE;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExampleScriptCollector.class);
	private static final String EXAMPLE_SCRIPT_EXTENSION_POINT_ID = "com.b2international.snowowl.scripting.core.scripts";
	private static final String SCRIPTS_ATTRIBUTE_ID = "script";
	private static final String SCRIPT_SHOULD_OPEN_ATTRIBUTE_ID = "open";
	
	/**Eagerly collects and returns with all contributed example scripts.*/
	public Map<File, Boolean> collectExampleScripts() {
		
		Map<File, Boolean> scripts = newHashMap();
		
		for (final IConfigurationElement scriptsElement : getScriptingConfigurationElements()) {
			try {
				final Pair<URL, String> fileURLWithName = getFileUrl(scriptsElement);
				final File script = tryLoadScript(fileURLWithName);
			    if (isValidScript(script)) {
			    	scripts.put(script, shouldOpenUponProjectCreation(scriptsElement));
			    }
			} catch (final SnowowlServiceException e) {
				LOGGER.error("Failed to load script from.", e);
			}
		}
		return scripts;
	}

	private boolean shouldOpenUponProjectCreation(IConfigurationElement scriptsElement) {
		String value = scriptsElement.getAttribute(SCRIPT_SHOULD_OPEN_ATTRIBUTE_ID);
		return null == value ? false : Boolean.valueOf(value).booleanValue();
	}

	private Pair<URL, String> getFileUrl(final IConfigurationElement scriptsElement) throws SnowowlServiceException {
		final String symbolicName = scriptsElement.getContributor().getName();
		try {
			final String fileNameWithExtension = scriptsElement.getAttribute(SCRIPTS_ATTRIBUTE_ID);
			return Pair.of(new URL("platform:/plugin/" + symbolicName + "/" +scriptsElement.getAttribute(SCRIPTS_ATTRIBUTE_ID)), fileNameWithExtension.split("/")[1]);
		} catch (final MalformedURLException e) {
			throw new SnowowlServiceException(e);
		} catch (final InvalidRegistryObjectException e) {
			throw new SnowowlServiceException(e);
		}
	}

	private IConfigurationElement[] getScriptingConfigurationElements() {
		return Platform.getExtensionRegistry().getConfigurationElementsFor(EXAMPLE_SCRIPT_EXTENSION_POINT_ID);
	}

	private File tryLoadScript(final Pair<URL, String> fileUrlWithName) throws SnowowlServiceException {
		
		final AtomicReference<InputStream> is = new AtomicReference<>();
		final URL fileUrl = fileUrlWithName.getA();
		final String[] fileNameWithExtension = fileUrlWithName.getB().split(".groovy");
		final String fileName = fileNameWithExtension[0];
		final String extension = "groovy";
		
		try {
			
			is.set(fileUrl.openStream());

			final File tmpDirectory = com.google.common.io.Files.createTempDir();
			final File tmpScriptFile = new File(tmpDirectory, fileName + "." + extension);
			tmpScriptFile.deleteOnExit();
			
			new ByteSource() {
				@Override
				public InputStream openStream() throws IOException {
					return is.get();
				}
			}.copyTo(Files.asByteSink(tmpScriptFile));
			return tmpScriptFile;
		} catch (final IOException e) {
			throw new SnowowlServiceException(e);
		} finally {
			
			if (null != is.get()) {
				try {
					is.get().close();
				} catch (final IOException e) {
					
					try {
						is.get().close();
					} catch (final IOException e1) {
						//ignored
					}
					
					throw new SnowowlServiceException(e);
				}
			}
			
		}
	}

	private boolean isValidScript(final File script) {
		return script.isFile() && script.canRead();
	}
	
}