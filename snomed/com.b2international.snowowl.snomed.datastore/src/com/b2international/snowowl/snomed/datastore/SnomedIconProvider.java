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
package com.b2international.snowowl.snomed.datastore;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.ComponentIconProvider;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
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
	private static final SnomedConceptIconIdProvider CONCEPT_ICON_ID_PROVIDER = new SnomedConceptIconIdProvider();
	private Pattern IMAGE_FILE_NAME_PATTERN;
	private static final Supplier<ICDOConnection> CONNECTION_SUPPLIER = Suppliers.memoize(new Supplier<ICDOConnection>() {
		@Override public ICDOConnection get() {
			return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE);
		}
	});

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
	
	public String getIconId(Object source, final IBranchPath branchPath) {
		if (source instanceof Long) {
			return getIconComponentId(String.valueOf(source), branchPath);
		} else if (source instanceof String) {
			return getIconComponentId((String) source, branchPath);
		} else if (source instanceof RevisionDocument) {
			// SNOMED Description entry sometimes contains false iconId (probably a bug in the indexing), so using the type
			if (source instanceof SnomedDescriptionIndexEntry) {
				return getIconComponentId(((SnomedDescriptionIndexEntry) source).getTypeId(), branchPath);
			}
			return ((RevisionDocument) source).getIconId();
		} else if (source instanceof IComponent<?>) {
			return ((IComponent<?>) source).getId() == null ? null : getIconId(((IComponent<?>) source).getId(), branchPath);
		} else if (source instanceof Concept) {
			// for Concepts using the Concept ID itself to search for the icon
			return getIconComponentId(((Concept) source).getId(), branchPath);
		} else if (source instanceof Description) {
			// for Description sources using the Type Concept ID
			return getIconComponentId(((Description) source).getType().getId(), branchPath);
		} else if (source instanceof Relationship) {
			// for relationships using the characteristic type ID
			return getIconComponentId(((Relationship) source).getCharacteristicType().getId(), branchPath);
		} else if (source instanceof IStatus) {
			return null;
		}
		throw new IllegalArgumentException("Unhandled type: " + source);
	}

	/**
	 * Returns the Icon from the specified source of information.
	 * 
	 * @param source
	 * @return
	 */
	public File getIcon(final String branch, Object source) {
		// return fast if the given source contains enough information to provide the image file (String or Long)
		final File resolved = resolveFast(source);
		if (resolved != null && resolved.exists()) {
			return resolved;
		}
		final String iconId = getIconId(source, BranchPathUtils.createPath(branch));
		return getExactFile(iconId, Concepts.ROOT_CONCEPT);
	}

	private File resolveFast(Object source) {
		File resolved = null;
		if (source instanceof String) {
			resolved = getExactFile((String)source);
		} else if (source instanceof Long) {
			resolved = getExactFile(String.valueOf(source));
		} else if (source instanceof RevisionDocument) {
			resolved = getExactFile(((RevisionDocument) source).getIconId());
		}
		return resolved;
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
	
	public String getIconComponentId(String componentId, final IBranchPath branchPath) {
		if (componentId == null) {
			return null;
		}
		
		String iconId;
		
		// Option 1: Quick index-based lookup, combined with slow CDO path
		iconId = CONCEPT_ICON_ID_PROVIDER.getIconId(BranchPointUtils.create(CONNECTION_SUPPLIER.get(), branchPath), componentId);
		if (!StringUtils.isEmpty(iconId) && !Concepts.ROOT_CONCEPT.equals(iconId)) {
			return iconId;
		}
		
		return Concepts.ROOT_CONCEPT; 
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