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
package com.b2international.snowowl.core.quicksearch;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.core.CoreTerminologyBroker;

/**
 * This interface must be implemented by all quick search providers to be able to display elements in the quick search
 * pop-up dialog.
 * 
 */
public interface IQuickSearchProvider {

	/**
	 * Configuration key to specify the user's ID for whom the search is performed.
	 */
	static final String CONFIGURATION_USER_ID = "USER_ID";

	/**
	 * Configuration key to specify a {@link Set} of component identifier {@link String Strings}.
	 */
	static final String CONFIGURATION_VALUE_ID_SET = "VALUE_ID_SET";

	/**
	 * Configuration key to specify a serialized form of a {@link IQueryExpressionWrapper} representing a bunch of allowed components.
	 */
	static final String CONFIGURATION_VALUE_ID_EXPRESSION = "VALUE_ID_EXPRESSION";

	/**
	 * Configuration key to propagate any branch path map related restrictions to the server side when collecting results via the content providers. 
	 */
	static final String RESTRICTED_BRANCH_PATH_MAP = "RESTRICTED_BRANCH_PATH_MAP";

	/**
	 * Configuration key to specify a terminology component ID if the provider supports filtering by this property.
	 */
	static final String TERMINOLOGY_COMPONENT_ID = "TERMINOLOGY_COMPONENT_ID";

	/**
	 * Configuration key to specify that the quick search should show the entered expression as missing component in the results.
	 */
	static final String CONFIGURATION_SHOW_MISSING_COMPONENT = "SHOW_MISSING_COMPONENT_ID";

	/**
	 * Returns the quick search provider's unique identifier, preferably the fully qualified name of the provider's
	 * class.
	 * 
	 * @return the provider identifier
	 */
	String getId();

	/**
	 * Returns the quick access provider's name which will be shown on the UI.
	 * 
	 * @return the provider name
	 */
	String getName();

	/**
	 * Returns the terminology component ID for the wrapped components this provider returns.
	 * 
	 * @return the terminology component identifier, or {@link CoreTerminologyBroker#UNSPECIFIED} if a single
	 * terminology component identifier is not applicable for this provider
	 */
	String getTerminologyComponentId();

	/**
	 * Returns the priority of this provider, which determines the sort order of providers.
	 * 
	 * @return the provider's priority
	 */
	int getPriority();

	/**
	 * Used when computing the most popular possible continuation of the last filter expression; suffixes extracted from
	 * this provider will be multiplied by this value.
	 * 
	 * @return the suffix multiplier value
	 */
	int getSuffixMultiplier();

	/**
	 * Sets a the new state of the current quick search result provider instance. The state is a pair of number (total
	 * hit count) and the actual terminology components elements wrapped by the current quick search provider.
	 * 
	 * @param state the new state of the quick search result provider.
	 */
	void setState(final QuickSearchContentResult state);

	/**
	 * Returns quick search elements that match the last filter expression, or, if no filtering took place previously, a
	 * sample selection of them.
	 * 
	 * @return elements supplied by this provider
	 */
	List<QuickSearchElement> getElements();

	/**
	 * Returns the total number of matches for the last filter expression, or, if no filtering took place previously, a
	 * grand total of searched components.
	 * 
	 * @return the number of matches (may be different from the number of elements if creating all elements would be
	 * prohibitively expensive)
	 */
	int getTotalHitCount();

	/**
	 * Performs an operation as a response to the user selecting the specified quick search element. This usually
	 * involves calling a(n optionally element-specific) {@link IQuickSearchCallback} instance, which can be set using
	 * {@link #setCallback(IQuickSearchCallback)}.
	 * 
	 * @param quickSearchElementBase the selected element
	 */
	void handleSelection(final QuickSearchElement quickSearchElementBase);

	/**
	 * Sets a new callback on this provider. Selection of any element associated with this provider will be handled
	 * using the specified callback.
	 * 
	 * @param callback the callback to set
	 */
	void setCallback(final IQuickSearchCallback callback);

	/**
	 * Sets the provider specific configuration map. The interpretation of the key-value pairs is
	 * implementation-dependent.
	 * 
	 * @param configuration the configuration map
	 */
	void setConfiguration(final Map<String, Object> configuration);

	/**
	 * @return the currently set configuration
	 */
	Map<String, Object> getConfiguration();
	
	/**
	 * Returns {@code true} if the current quick search provider is globally available hence it can
	 * be accessed via the global quick search widget. Otherwise returns with {@code false}.
 	 * @return {@code true} if globally available.
	 */
	boolean isGloballyAvailable();

}