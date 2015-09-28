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
package com.b2international.snowowl.snomed.api;

import java.util.List;
import java.util.Locale;

import com.b2international.snowowl.api.codesystem.exception.CodeSystemNotFoundException;
import com.b2international.snowowl.api.codesystem.exception.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.api.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.api.domain.ISnomedDescriptionInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedDescriptionUpdate;
import com.b2international.snowowl.snomed.api.exception.FullySpecifiedNameNotFoundException;
import com.b2international.snowowl.snomed.api.exception.PreferredTermNotFoundException;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

/**
 * SNOMED CT relationship service implementations provide methods for <b>c</b>reating, <b>r</b>eading, <b>u</b>pdating
 * and <b>d</b>eleting individual descriptions, as well as collecting descriptions of a particular concept.
 */
public interface ISnomedDescriptionService extends ISnomedComponentService<ISnomedDescriptionInput, ISnomedDescription, ISnomedDescriptionUpdate> {

	/**
	 * Returns all descriptions of the concept identified by the given {@link IComponentRef component reference}, if it exists.
	 * <p>
	 * The returned descriptions are sorted by description ID.
	 * 
	 * @param conceptRef the reference to the concept for which descriptions should be returned (may not be {@code null})
	 * 
	 * @return a list of descriptions associated with the specified concept
	 */
	List<ISnomedDescription> readConceptDescriptions(IComponentRef conceptRef);

	/**
	 * Retrieves the preferred term for the concept identified by the given {@link IComponentRef component reference}, if it exists. 
	 * <p>
	 * The first active description with "synonym" or descendant as the type will be returned, where all of the following conditions apply:
	 * <ul>
	 * <li>the description's language code matches the supplied {@code Locale}'s language (eg. {@code "en"} on description, {@code "en-US"} on {@code Locale});
	 * <li>a matching well-known language reference set exists for the given {@code Locale} (eg. {@code "en-GB"}), or
	 * the {@code Locale} itself includes a language reference set ID as the extension, which exists (eg. {@code "en-GB-x-900000000000508004"});
	 * <li>the description has a language reference set member in the reference set identified above with preferred acceptability.
	 * </ul>
	 * <p>
	 * If no such description can be found, the process is repeated with the next {@code Locale} in the list.
	 * 
	 * @param conceptRef the reference to the concept for which the preferred term should be returned (may not be {@code null})
	 * @param locales    a list of {@link Locale}s to use, in order of preference
	 * 
	 * @return the preferred term for the concept
	 *
	 * @throws CodeSystemNotFoundException        if SNOMED CT as a code system is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for SNOMED CT with the given identifier
	 *                                            is not registered
	 * @throws PreferredTermNotFoundException     if no preferred term could be collected as a result of the process above
	 */
	ISnomedDescription getPreferredTerm(IComponentRef conceptRef, List<Locale> locales);

	ISnomedDescription getPreferredTerm(List<ISnomedDescription> descriptions, IComponentRef conceptRef, List<Locale> locales);

	/**
	 * Retrieves the fully specified name for the concept identified by the given {@link IComponentRef component reference}, if it exists. 
	 * <p>
	 * The first active description with "fully specified name" as the type will be returned, where all of the following conditions apply:
	 * <ul>
	 * <li>the description's language code matches the supplied {@code Locale}'s language (eg. {@code "en"} on description, {@code "en-US"} on {@code Locale});
	 * <li>a matching well-known language reference set exists for the given {@code Locale} (eg. {@code "en-GB"}), or
	 * the {@code Locale} itself includes a language reference set ID as the extension, which exists (eg. {@code "en-GB-x-900000000000508004"});
	 * <li>the description has a language reference set member in the reference set identified above with preferred acceptability.
	 * </ul>
	 * <p>
	 * If no such description can be found, the search is repeated with the following conditions:
	 * <ul>
	 * <li>the description's language code matches the supplied {@code Locale}'s language (eg. {@code "en"} on description, {@code "en-US"} on {@code Locale});
	 * </ul>
	 * <p>
	 * Failing that, the whole check starts from the beginning with the next {@link Locale} in the list.
	 * The method falls back to the first active fully specified name if the language code does not match any of the specified {@code Locale}s.
	 * 
	 * @param conceptRef the reference to the concept for which the preferred term should be returned (may not be {@code null})
	 * @param locales    a list of {@link Locale}s to use, in order of preference
	 * 
	 * @return the preferred term for the concept
	 *
	 * @throws FullySpecifiedNameNotFoundException if no fully specified name could be collected as a result of the process above
	 */
	ISnomedDescription getFullySpecifiedName(IComponentRef conceptRef, List<Locale> locales);

	ISnomedDescription getFullySpecifiedName(List<ISnomedDescription> descriptions, IComponentRef conceptRef, List<Locale> locales);

	/**
	 * Converts a list of user-specified {@link Locale}s to language reference set identifiers.
	 * 
	 * @param locales    a list of {@link Locale}s to use, in order of preference
	 * @param branchPath the branch path to look up language reference sets on
	 * @return a {@link BiMap bidirectional map} indexing reference set identifiers by {@link Locale}
	 */
	ImmutableBiMap<Locale, String> getLanguageIdMap(List<Locale> locales, IBranchPath branchPath);
}
