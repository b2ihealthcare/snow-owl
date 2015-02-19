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
package com.b2international.snowowl.snomed.datastore;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.CDOViewFunction;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Represents the language configuration for the SNOMED&nbsp;CT terminology.
 * @see LanguageCodeReferenceSetIdentifierMapping
 */
@Immutable
@ThreadSafe
public class LanguageConfiguration {

	private static final String[] DEFAULT_EN_LANGUAGE_IDS = { Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US };
	
	/**The default language code abbreviation. Value: {@value}*/
	private static final String EN_LANGUAGE_CODE = "en";
	private final List<String> languageRefSetIds;

	/**
	 * Creates a new instance of this class based on the specified SNOMED&nbsp;CT language type reference set identifier concept ID.
	 * @param languageRefSetId the ID of the language type reference set concept.
	 */
	public LanguageConfiguration(final String languageRefSetId, final String... fallBackIds) {
		checkState(!StringUtils.isEmpty(languageRefSetId), "The ID argument cannot be null or empty.");
		this.languageRefSetIds = Lists.asList(languageRefSetId, CompareUtils.isEmpty(fallBackIds) ? DEFAULT_EN_LANGUAGE_IDS : fallBackIds);
		Preconditions.checkState(!CompareUtils.isEmpty(this.languageRefSetIds), "At least one language reference set identifier concept ID has to be specified.");
	}

	/**
	 * Returns with the SNOMED&nbsp;CT language type reference set identifier concept ID. 
	 * @return the ID of the language type reference set concept.
	 */
	public String getLanguageRefSetId() {
		return Preconditions.checkNotNull(Iterables.getFirst(languageRefSetIds, null), "Language reference set identifier concept ID is not configured.");
	}

	/**
	 * Returns with the configured language reference set ID on the given branch after checking its existence.
	 * This method falls back to existing language reference set if the configured primary one does not exist.
	 * This method never returns with {@code null} in case of empty database content, it returns with the configured primary language ID.  
	 */
	public String getLanguageRefSetId(final IBranchPath branchPath) {
		Preconditions.checkNotNull(branchPath, "branchPath");
		for (final String refSetId : languageRefSetIds) {
			if (existsInIndex(branchPath, refSetId)) {
				return refSetId;
			}
		}
		
		for (final String refSetId : languageRefSetIds) {
			if (existsInCdo(branchPath, refSetId)) {
				return refSetId;
			}
		}
		
		return getLanguageRefSetId();
	}

	private boolean existsInCdo(final IBranchPath branchPath, final String refSetId) {
		return CDOUtils.apply(new CDOViewFunction<Boolean, CDOView>(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath) {
			@Override protected Boolean apply(final CDOView view) {
				return null != new SnomedRefSetLookupService().getComponent(refSetId, view);
			}
		});
	}
	
	@Nullable private boolean existsInIndex(final IBranchPath branchPath, final String refSetId) {
		return new SnomedRefSetLookupService().exists(branchPath, refSetId);
	} 
	
	/**
	 * Returns with the abbreviation of the language code associated with the SNOMED&nbsp;CT language type reference set.
	 * <p>E.g.:
	 * <ul>
	 * <li>en</li>
	 * <li>en-us</li>
	 * <li>en-gb</li>
	 * <li>en-sg</li>
	 * <li>en-au</li>
	 * </ul>
	 * </p>
	 * @return the abbreviation of the language code.
	 */
	public String getLanguageCode() {
		final String languageCode = LanguageCodeReferenceSetIdentifierMapping.getLanguageCode(getLanguageRefSetId());
		return null == languageCode ? EN_LANGUAGE_CODE : languageCode;
	}
}