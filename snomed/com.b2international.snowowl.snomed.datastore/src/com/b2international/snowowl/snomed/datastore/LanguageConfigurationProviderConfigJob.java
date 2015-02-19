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

import static com.google.common.base.Preconditions.checkArgument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob;
import com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.google.common.base.Strings;

/**
 * Registers a {@link ILanguageConfigurationProvider language configuration provider}.
 * 
 */
public class LanguageConfigurationProviderConfigJob extends ServiceConfigJob{

	private static final Logger LOGGER = LoggerFactory.getLogger(LanguageConfigurationProviderConfigJob.class);
	private static final String LANGUAGE_PREFIX = "en-"; //$NON-NLS-1$
	
	/**
	 * {@value}
	 */
	private static final String DEFAULT_LANGUAGE = "en-gb"; //$NON-NLS-1$
	
	public LanguageConfigurationProviderConfigJob() {
		this("Language configuration provider configuration job...", SnomedDatastoreActivator.PLUGIN_ID); //$NON-NLS-1$
	}
	
	protected LanguageConfigurationProviderConfigJob(final String name, final Object family) {
		super(name, family);
	}

	@Override
	protected boolean initService() throws SnowowlServiceException {
		final SnomedCoreConfiguration snomedConfiguration = getSnowOwlConfiguration().getModuleConfig(SnomedCoreConfiguration.class);
		final String language = snomedConfiguration.getLanguage();
		configureLanguage(getLanguageReferenceSetId(language));
		return true;
	}

	/**
	 * Configures SNOMED CT to use the given language reference set ID.
	 * 
	 * @param languageReferenceSetId
	 */
	private void configureLanguage(String languageReferenceSetId) {
		LOGGER.info("Registering '{}' language configuration.", LanguageCodeReferenceSetIdentifierMapping.getLanguageCode(languageReferenceSetId));
		ApplicationContext.getInstance().registerService(ILanguageConfigurationProvider.class, new ConstantLanguageConfigurationProvider(languageReferenceSetId));		
	}
	
	/**
	 * Returns and finds the proper language reference set ID to use for the
	 * given language code. If the desired reference set is not available then
	 * this method falls back to {@link #DEFAULT_LANGUAGE_REFSET}.
	 * 
	 * @param configuration
	 *            - language code, or SNOMED CT Language Reference Set Identifier Concept ID is allowed.
	 * @return
	 */
	private String getLanguageReferenceSetId(String configuration) {
		checkArgument(!Strings.isNullOrEmpty(configuration), "Language code|Concept ID should not be null or empty.");
		final String languageCode = LanguageCodeReferenceSetIdentifierMapping.getLanguageCode(configuration);
		// as we accept concept ID as well, we validate it. if we can map ID to a valid language configuration, we're done
		if (Strings.isNullOrEmpty(languageCode)) { 
			// try mapping from en-language code to reference set id, ignore case
			final String referenceSetId = getLanguageReferenceSetIdByLanguageCode(configuration);
			if (!Strings.isNullOrEmpty(referenceSetId)) {
				return referenceSetId;
			}
		} else { 
			// property is a valid language reference set ID
			return configuration;
		}
		LOGGER.warn("SNOMED CT language is not properly specified in configuration.\n"
				+ "It was: '{}'. Possible allowed values are a language code (e.g.: 'en-gb', 'en-us', 'us' or 'gb') or\n"
				+ "a SNOMED CT language type reference set identifier concept ID.\n"
				+ "Falling back to default language: {}", configuration, DEFAULT_LANGUAGE);
		return getLanguageReferenceSetIdByLanguageCode(DEFAULT_LANGUAGE);
	}

	/**
	 * Returns a SNOMED CT language reference set ID if the given language code.
	 * The method ignore casing in the given language code. The language code
	 * can be a portion of a valid language code (like <code>'us'</code>), in
	 * this case the variable will be prefixed with {@value #LANGUAGE_PREFIX}.
	 * 
	 * @param languageCode
	 * @return
	 */
	private String getLanguageReferenceSetIdByLanguageCode(String languageCode) {
		final String langCodeLowerCase = languageCode.toLowerCase();
		String referenceSetId = LanguageCodeReferenceSetIdentifierMapping.getReferenceSetIdentifier(langCodeLowerCase);
		if (Strings.isNullOrEmpty(referenceSetId)) {
			referenceSetId = LanguageCodeReferenceSetIdentifierMapping.getReferenceSetIdentifier(LANGUAGE_PREFIX + langCodeLowerCase);
		}
		return referenceSetId;
	}
	
}