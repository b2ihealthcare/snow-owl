/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.ComponentIconProvider;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.collect.Sets;

/**
 * <p>
 * An icon provider for snomed concepts that doesn't have any SWT dependency so it can be used on the server side and
 * with Swing.
 * </p>
 * 
 * 
 */
public class SnomedIconProvider extends ComponentIconProvider<String> {

	private static final String EXT_PNG = ".png";
	private static final String SNOMEDICONS_FOLDER = "snomedicons";
	private static SnomedIconProvider instance;
	private Pattern IMAGE_FILE_NAME_PATTERN;

	/**
	 * A list of "icon concepts" sorted by depth DESC, so deeper concepts will
	 * be found first before more top level ones while linearly searching this array.
	 */
	private Collection<String> imageConceptIds;
	
	public static SnomedIconProvider getInstance() {
		if (null == instance) {
			synchronized (SnomedIconProvider.class) {
				if (null == instance) {
					instance = new SnomedIconProvider();
				}
			}
		}
		return instance;
	}

	@Override
	public void refresh() {
	}
	
	/**
	 * Tries to find the icon id for the selected conceptId, based on information of the
	 * {@link BranchPathUtils#createActivePath() currently active path}. Returns the ROOT_Concept Icon Id if none found.
	 * Returns <code>null</code> if the terminology browser not available or the input is <code>null</code>.
	 * 
	 * @param conceptId
	 * @return
	 * @deprecated - UNSUPPORTED API
	 */
	@Override
	public String getIconComponentId(String componentId) {
		throw new UnsupportedOperationException("Getting icon ID on server side is not supported without specifying a branch");
	}
	
	/**
	 * Returns with a view of IDs that have matching image resource.
	 * @return a collection of SNOMED&nbsp;CT concept IDs that have associated image file resource. 
	 */
	public Collection<String> getAvailableIconIds() {
		return Collections.unmodifiableCollection(imageConceptIds);
	}
	
	private Collection<String> readAvailableImageNames() {
		if (imageConceptIds == null) {
			final File iconDirectory = getIconDirectory();
			if (iconDirectory.canRead()) {
				imageConceptIds = Sets.newHashSet();
				for (File file : iconDirectory.listFiles()) {
					if (null == IMAGE_FILE_NAME_PATTERN) {
						IMAGE_FILE_NAME_PATTERN = Pattern.compile("([0-9]+)\\.png");
					}
					Matcher matcher = IMAGE_FILE_NAME_PATTERN.matcher(file.getName());
					if (matcher.matches()) {
						imageConceptIds.add(matcher.group(1));
					}
				}
			} else {
				imageConceptIds = Collections.singleton(Concepts.ROOT_CONCEPT);
			}
		}
		return imageConceptIds;
	}

	/** @return the File pointing at the icon .png for the specified concept */
	@Override
	public File getIconFile(String componentId) {
		String iconConceptId = getIconComponentId(componentId);
		if (iconConceptId == null) {
			return null;
		}
		return getExactFile(iconConceptId);
	}

	/** @return the File for the specified concept, file will not exist if concept is not an icon concept */
	@Override
	public File getExactFile(String componentId) {
		return new File(getIconDirectory(), componentId + EXT_PNG);
	}

	@Override
	public File getExactFile(String componentId, String defaultComponentId) {
		File icon = getExactFile(componentId);
		if (icon == null || !icon.exists()) {
			icon = getExactFile(defaultComponentId);
		}
		return icon;
	}

	@Override
	public String getIconFileName(String componentId) {
		File iconFile = getIconFile(componentId);
		if (null == iconFile) {
			return null;
		}
		return iconFile.getName();
	}

	@Override
	protected String getFolderName() {
		return SNOMEDICONS_FOLDER;
	}

	private SnomedIconProvider() {
		readAvailableImageNames();
	}

}