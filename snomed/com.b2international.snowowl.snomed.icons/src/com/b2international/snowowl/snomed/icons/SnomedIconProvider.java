/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.icons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.commons.platform.PlatformUtil;
import com.google.common.io.Files;

/**
 * <p>
 * An icon provider for SNOMED CT Concepts that doesn't have any dependency.
 * </p>
 */
public final class SnomedIconProvider {

	private static final String ICONS_PATH = "icons";
	private static SnomedIconProvider INSTANCE;

	public static SnomedIconProvider getInstance() {
		if (null == INSTANCE) {
			synchronized (SnomedIconProvider.class) {
				if (null == INSTANCE) {
					INSTANCE = new SnomedIconProvider();
				}
			}
		}
		return INSTANCE;
	}
	
	private final Map<String, URL> availableIcons;

	private SnomedIconProvider() {
		final String iconsPrefix = "/" + ICONS_PATH;
		URL iconsUrl = PlatformUtil.toFileURL(getClass(), iconsPrefix);
		if (iconsUrl.getProtocol().equals("file")) {
			try (
					final InputStream is = iconsUrl.openStream();
					final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
					final BufferedReader br = new BufferedReader(isr)) {
				availableIcons = br.lines()
						.map(file -> PlatformUtil.toFileURL(getClass(), iconsPrefix + "/" + file))
						.collect(Collectors.toMap(url -> Files.getNameWithoutExtension(url.getFile()), url -> url));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else if (iconsUrl.getProtocol().equals("jar")) {
			try {
				JarURLConnection connection = (JarURLConnection) iconsUrl.openConnection();
				availableIcons = ((JarURLConnection) connection).getJarFile().stream()
						.filter(file -> file.getName().startsWith(ICONS_PATH) && !file.isDirectory())
						.map(file -> PlatformUtil.toFileURL(getClass(), "/" + file.getName()))
						.collect(Collectors.toMap(url -> Files.getNameWithoutExtension(url.getFile()), url -> url));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			// error
			availableIcons = new HashMap<>();
		}
		// throw error if icons are empty
	}
	
	/**
	 * Returns with a view of IDs that have matching image resource.
	 * @return a collection of SNOMED&nbsp;CT concept IDs that have associated image file resource. 
	 */
	public Collection<String> getAvailableIconIds() {
		return Collections.unmodifiableSet(availableIcons.keySet());
	}
	
	/** 
	 * @return the File for the specified concept, file will not exist if concept is not an icon concept 
	 */
	public URL getExactFile(String componentId) {
		return availableIcons.get(componentId);
	}

	public URL getExactFile(String componentId, String defaultComponentId) {
		URL icon = getExactFile(componentId);
		if (icon == null) {
			icon = getExactFile(defaultComponentId);
		}
		return icon;
	}

}