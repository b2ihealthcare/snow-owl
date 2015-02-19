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
package com.b2international.snowowl.snomed.mrcm.core.server;

import static com.google.common.base.Strings.nullToEmpty;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.SnowowlServiceException;

/**
 * MRCM file registry singleton.
 *
 */
public enum MrcmFileRegistryImpl implements MrcmFileRegistry {

	INSTANCE;

	private static final Logger LOGGER = LoggerFactory.getLogger(MrcmFileRegistryImpl.class);
	private static final String MRCM_CONTENT_FOLDER = "mrcm_content";
	private static final String MRCM_DEFAULTS_FILE = "mrcm_defaults.xmi";
	
	private URI mrcmFileUri;
	
	@Override
	public URI getMrcmFileUri() {
		if (null != mrcmFileUri) {
			return mrcmFileUri;
		} else {
			final Bundle bundle = MrcmCoreServerActivator.getContext().getBundle();
			final String symbolicName = bundle.getSymbolicName();
			try {
				return new URL("platform:/plugin/" + symbolicName + "/" + MRCM_CONTENT_FOLDER + "/" + MRCM_DEFAULTS_FILE).toURI(); 
			} catch (final Exception e) {
				LOGGER.error("Error while trying to load default MRCM file.\n" + nullToEmpty(e.getMessage()));
				return null;
			}
		}
	}

	@Override
	public void configureMrcmFile(final URI mrcmFileUri) {
		tryConfigureMrcmFile(mrcmFileUri);
	}

	private void tryConfigureMrcmFile(final URI mrcmFileUri) {
		try {
			this.mrcmFileUri = checkUri(mrcmFileUri);
			LOGGER.info("MRCM file URI successfully configured. MRCM URI: " + mrcmFileUri);
		} catch (final SnowowlServiceException e) {
			LOGGER.warn("Invalid MRCM file URI. " + nullToEmpty(e.getMessage()));
		}
	}

	private URI checkUri(final URI mrcmFileUri) throws SnowowlServiceException {
		if (null == mrcmFileUri) {
			throw new SnowowlServiceException("MRCM file URI should be specified.");
		}
		final File file = new File(mrcmFileUri);
		if (!file.exists()) {
			throw new SnowowlServiceException("File does not exist at: " + file.getAbsolutePath());
		}
		
		if (!file.isFile()) {
			throw new SnowowlServiceException("Given MRCM location is not a file. " + file.getAbsolutePath());
		}
		
		if (!file.canRead()) {
			throw new SnowowlServiceException("File cannot be read. File: " + file.getAbsolutePath());
		}
		
		return mrcmFileUri;
	}

}