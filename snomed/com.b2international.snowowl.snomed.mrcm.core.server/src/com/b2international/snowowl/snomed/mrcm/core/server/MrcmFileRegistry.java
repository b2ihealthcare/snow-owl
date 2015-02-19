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

import java.net.URI;

import javax.annotation.Nullable;

/**
 * Registry for getting and setting the currently configured one 
 * and only MRCM file location on the server-side.
 * <p>Clients should note that this configuration is not persistent.
 *
 */
public interface MrcmFileRegistry {

	/**
	 * Returns with the currently configured MRCM file location on the server.
	 * Could be {@code null} if not configured yet.
	 * @return the MRCM file.
	 */
	@Nullable URI getMrcmFileUri();
	
	/**
	 * Sets the MRCM file location on the server. The MRCM file URI argument will 
	 * be set as the currently configured one.
	 * All previously configured file URIs will be overridden.
	 * @param mrcmFileUri the MRCM file URI to set as the configured one.
	 */
	void configureMrcmFile(final URI mrcmFileUri);
	
}