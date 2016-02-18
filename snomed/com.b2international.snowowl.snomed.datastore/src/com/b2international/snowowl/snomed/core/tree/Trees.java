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
package com.b2international.snowowl.snomed.core.tree;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.BaseSnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 4.6
 */
public class Trees {

	public static final String STATED_FORM = "stated";
	public static final String INFERRED_FORM = "inferred";

	private String form;
	private BaseSnomedClientTerminologyBrowser browser;
	private Collection<SnomedConceptIndexEntry> topLevelConcepts;

	public final Trees withInferredForm() {
		this.form = INFERRED_FORM;
		return this;
	}

	public final Trees withStatedForm() {
		this.form = STATED_FORM;
		return this;
	}

	public final Trees setBrowser(final BaseSnomedClientTerminologyBrowser browser) {
		this.browser = browser;
		return this;
	}

	public final Trees setTopLevelConcepts(final Collection<SnomedConceptIndexEntry> topLevelConcepts) {
		this.topLevelConcepts = topLevelConcepts;
		return this;
	}

	public final Trees withDefaultTopLevelConcepts(final String branch) {
		setTopLevelConcepts(getDefaultTopLevelConcepts(branch));
		return this;
	}

	private List<SnomedConceptIndexEntry> getDefaultTopLevelConcepts(final String branch) {
		final ISnomedConcept root = SnomedRequests
				.prepareGetConcept()
				.setComponentId(Concepts.ROOT_CONCEPT)
				.setExpand("pt(),descendants(form:\"inferred\",direct:true,expand(pt()))")
				.setLocales(getLocales())
				.build(branch)
				.executeSync(getBus());

		final Collection<ISnomedConcept> requiredTreeItemConcepts = newHashSet();
		requiredTreeItemConcepts.add(root);
		requiredTreeItemConcepts.addAll(root.getDescendants().getItems());

		return SnomedConceptIndexEntry.fromConcepts(requiredTreeItemConcepts);
	}

	private List<ExtendedLocale> getLocales() {
		return ApplicationContext.getInstance().getService(LanguageSetting.class).getLanguagePreference();
	}

	private IEventBus getBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}

	public final TreeBuilder createTreeBuilder() {
		final TreeBuilderImpl treeBuilder = new TreeBuilderImpl();
		treeBuilder.setForm(form);
		treeBuilder.setBrowser(browser);
		treeBuilder.setTopLevelConcepts(topLevelConcepts);

		return treeBuilder;
	}

}
