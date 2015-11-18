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
package com.b2international.snowowl.snomed.mrcm.core.configuration;

import static com.b2international.snowowl.snomed.mrcm.core.configuration.AttributeType.CONCRETE_DATA_TYPE;
import static com.b2international.snowowl.snomed.mrcm.core.configuration.AttributeType.DESCRIPTION;
import static com.b2international.snowowl.snomed.mrcm.core.configuration.AttributeType.RELATIONSHIP;
import static com.b2international.snowowl.snomed.mrcm.mini.SectionType.DESCRIPTION_SECTION;
import static com.b2international.snowowl.snomed.mrcm.mini.SectionType.PROPERTY_SECTION;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.browser.IClientRefSetBrowser;
import com.b2international.snowowl.core.api.preferences.AbstractSerializableConfiguration;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedClientRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.mrcm.mini.SectionType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("snomedSimpleTypeRefSetAttributeConfiguration")
public final class SnomedSimpleTypeRefSetAttributeConfiguration extends AbstractSerializableConfiguration<AttributeOrderConfiguration>{
	
	private static final Set<SnomedRefSetType> SIMPLE_TYPE_SET = Collections.singleton(SnomedRefSetType.SIMPLE);

	public static SnomedSimpleTypeRefSetAttributeConfiguration getConfiguration(final Concept concept) {
		if (concept.cdoView().isClosed()) {
			return null;
		}
		
		final ComponentAttributeOrderConfiguration service = ApplicationContext.getInstance().getService(ComponentAttributeOrderConfiguration.class);
		if (null == service)
			return null;
		
		final SnomedRefSetMembershipLookupService lookupService = new SnomedRefSetMembershipLookupService();
		final Collection<SnomedRefSetMemberIndexEntry> members = lookupService.getMembersForType(
				SnomedTerminologyComponentConstants.CONCEPT, 
				SIMPLE_TYPE_SET, 
				concept.getId(),
				//XXX akitta
				//the number of the simple type concept referenced component type reference sets are stored in snor.
				//simple type reference sets cannot contain duplicate reference set members.
				//we can limit the search to gain ~50 times speed up. (~100 - 150 ms)
				Sets.newHashSet(getRefSetBrowserService().getRefsSets()).size());
		
		for (final SnomedRefSetMemberIndexEntry refSetMembers : members) {
			final String refSetIdentifierId = refSetMembers.getRefSetIdentifierId();
			final SnomedSimpleTypeRefSetAttributeConfiguration configuration = service.getConfiguration(refSetIdentifierId);
			if (null != configuration)
				return configuration;
		}
		for (final SnomedRefSetMember member : ComponentUtils2.getNewObjects(concept.cdoView(), SnomedRefSetMember.class)) {
			if (concept.getId().equals(member.getReferencedComponentId())) {
				final SnomedSimpleTypeRefSetAttributeConfiguration configuration = service.getConfiguration(member.getRefSetIdentifierId());
				if (null != configuration)
					return configuration;
			}
		}
		return null;
	}
	
	public static SnomedSimpleTypeRefSetAttributeConfiguration getConfiguration(final String conceptId) {
		final ComponentAttributeOrderConfiguration service = ApplicationContext.getInstance().getService(ComponentAttributeOrderConfiguration.class);
		if (null == service)
			return null;
		
		final SnomedRefSetMembershipLookupService lookupService = new SnomedRefSetMembershipLookupService();
		final Collection<SnomedRefSetMemberIndexEntry> members = lookupService.getMembersForType(
				SnomedTerminologyComponentConstants.CONCEPT, 
				SIMPLE_TYPE_SET, 
				conceptId,
				//XXX akitta
				//the number of the simple type concept referenced component type reference sets are stored in snor.
				//simple type reference sets cannot contain duplicate reference set members.
				//we can limit the search to gain ~50 times speed up. (~100 - 150 ms)
				Sets.newHashSet(getRefSetBrowserService().getRefsSets()).size());
		
		for (final SnomedRefSetMemberIndexEntry refSetMembers : members) {
			final String refSetIdentifierId = refSetMembers.getRefSetIdentifierId();
			final SnomedSimpleTypeRefSetAttributeConfiguration configuration = service.getConfiguration(refSetIdentifierId);
			if (null != configuration)
				return configuration;
		}
		return null;
	}

	private static IClientRefSetBrowser<SnomedRefSetIndexEntry, SnomedConceptIndexEntry, String> getRefSetBrowserService() {
		return ApplicationContext.getInstance().getService(SnomedClientRefSetBrowser.class);
	}

	/**
	 * Default constructor for serialization.
	 */
	protected SnomedSimpleTypeRefSetAttributeConfiguration() {
	}
	
	public SnomedSimpleTypeRefSetAttributeConfiguration (final String refSetId) {
		super(refSetId);
	}
	
	public void addChild(final String id) {
		switch (SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(id)) {
			case 101: addDescriptionTypeChild(id); break;
			case 102: addRelationshipTypeChild(id); break;
			case -1: addConcreteDataTypeTypeChild(id); break;
			default : throw new IllegalArgumentException("Unkown component type for ID: " + id);
		}
	}
	
	public void removeChild(final String id) {
		remove(getEntry(getKey() + "_" + id));
	}

	public void remove(final AttributeOrderConfiguration configuration) {
		if (null == configuration)
			return;
		if (null == entires || CompareUtils.isEmpty(entryList))
			return;
		entires.remove(configuration.getEntryKey());
		entryList.remove(configuration);
		for (final AttributeOrderConfiguration config : entryList) {
			if (configuration.getSectionType().equals(config.getSectionType()) && config.getPriority() > configuration.getPriority()) {
				config.setPriority(config.getPriority() - 1);
			}
		}
	}

	private void addRelationshipTypeChild(final String id) {
		if (null == getEntry(getKey() + "_" + id))
			add(AttributeOrderConfiguration.create(getKey(), calculatePriority(PROPERTY_SECTION), id, RELATIONSHIP, PROPERTY_SECTION));
	}

	private void addDescriptionTypeChild(final String id) {
		if (null == getEntry(getKey() + "_" + id))
			add(AttributeOrderConfiguration.create(getKey(), calculatePriority(DESCRIPTION_SECTION), id, DESCRIPTION, DESCRIPTION_SECTION));
	}
	
	private void addConcreteDataTypeTypeChild(final String label) {
		if (null == getEntry(getKey() + "_" + label))
			add(AttributeOrderConfiguration.create(getKey(), calculatePriority(PROPERTY_SECTION), label, CONCRETE_DATA_TYPE, PROPERTY_SECTION));
	}
	
	private int calculatePriority(final SectionType type) {	
		final Iterable<AttributeOrderConfiguration> matchingTypes = Iterables.filter(getEntries().values(), new Predicate<AttributeOrderConfiguration>() {
			@Override public boolean apply(final AttributeOrderConfiguration config) {
				return type.equals(config.getSectionType());
			}
		});
		
		int max = Ints.max(Ints.toArray(Lists.newArrayList(Iterables.transform(matchingTypes, new Function<AttributeOrderConfiguration, Integer>() {
			@Override public Integer apply(final AttributeOrderConfiguration config) {
				return config.getPriority();
			}
		}))));
		return max++;
	}
}