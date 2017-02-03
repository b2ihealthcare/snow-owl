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
package com.b2international.snowowl.snomed.mrcm.core.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.prefs.PreferencesService;

import com.b2international.commons.ExplicitFirstOrdering;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.NullComponent;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.DataTypeUtils;
import com.b2international.snowowl.snomed.mrcm.core.configuration.AttributeOrderConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.configuration.SnomedSimpleTypeRefSetAttributeConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DataTypeWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DescriptionWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.LeafWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipWidgetBean;
import com.b2international.snowowl.snomed.mrcm.mini.SectionType;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

/**
 * Utility class to sort concept attributes in detailed concept editor based on the associated
 * {@link AttributeOrderConfiguration configurations} retrieved from {@link PreferencesService preference service}.
 * 
 */
public abstract class LeafWidgetBeanSorter {
	
	private static final String SUFFIX_UNITS = "units";
	
	private static final String SUFFIX_VALUE = "value";

	private static final boolean USE_CONFIGURATION = false;
	
	/**
	 * Merges and sorts the passed in {@link LeafWidgetBean} instances.
	 * 
	 * @param type type that determines whether the attribute should be shown in the <b>Descriptions</b> or
	 * <b>Properties</b> section in the detailed concept editor.
	 * @param configuration the configuration associated with a SNOMED&nbsp;CT simple type reference set.
	 * @param lists the list of {@link LeafWidgetBean} instances to merge and sort.
	 * @return the merged and sorted list of {@link LeafWidgetBean}s.
	 */
	public static List<LeafWidgetBean> mergeAndSortElements(final SectionType type, final SnomedSimpleTypeRefSetAttributeConfiguration configuration, final boolean doSort, final List<LeafWidgetBean>... lists) {
		
		final List<LeafWidgetBean> merged = Lists.newArrayList(Iterables.concat(lists));

		if (USE_CONFIGURATION && null != configuration) {
			filterAndSortByConfiguration(type, configuration, merged, doSort);
		} else {
			filterAndSortByDefault(type, merged, doSort);
		}
		
		return merged;
	}

	private static void filterAndSortByConfiguration(final SectionType type, final SnomedSimpleTypeRefSetAttributeConfiguration configuration, final List<LeafWidgetBean> merged, final boolean doSort) {
		
		for (final Iterator<LeafWidgetBean> itr = merged.iterator(); itr.hasNext(); /* nothing */) {
			
			final LeafWidgetBean leafWidgetBean = itr.next();
			final AttributeOrderConfiguration config = getConfiguration(leafWidgetBean, configuration.getEntries());
			
			if (null != config) {
				if (!type.equals(config.getSectionType())) {
					itr.remove();
				}
			} else {
				filterLeaf(type, itr, leafWidgetBean);
			}
		}
		
		if (doSort) {
			Collections.sort(merged, new LeafWidgetBeanComparator(configuration, type));
		}
	}

	private static void filterAndSortByDefault(final SectionType type, final List<LeafWidgetBean> merged, final boolean doSort) {
		
		for (final Iterator<LeafWidgetBean> itr = merged.iterator(); itr.hasNext(); /* nothing */) {
			final LeafWidgetBean leafWidgetBean = itr.next();
			filterLeaf(type, itr, leafWidgetBean);
		}
		
		if (doSort) {
			Collections.sort(merged, getDefaultComparator(type));
		}
	}

	private static void filterLeaf(final SectionType type, final Iterator<LeafWidgetBean> itr, final LeafWidgetBean leafWidgetBean) {
		
		if (leafWidgetBean instanceof DataTypeWidgetBean) {
			if (SectionType.DESCRIPTION_SECTION.equals(type)) {
				itr.remove();
			}
			
		} else if (leafWidgetBean instanceof DescriptionWidgetBean) {
			
			// Descriptions by default don't belong to the properties section
			if (SectionType.PROPERTY_SECTION.equals(type)) {
				itr.remove();
			}
			
		} else if (leafWidgetBean instanceof RelationshipWidgetBean) {
			
			// Relationships by default don't belong to the descriptions section
			if (SectionType.DESCRIPTION_SECTION.equals(type)) {
				itr.remove();
			}
		}
	}

	/*returns with the associated attribute order configuration instance. or null if no configuration exists.*/
	private static AttributeOrderConfiguration getConfiguration(final LeafWidgetBean leafWidgetBean, final Map<String, AttributeOrderConfiguration> orderConfigurations) {
		
		if (leafWidgetBean instanceof RelationshipWidgetBean) {
			
			for (final Entry<String, AttributeOrderConfiguration> entry : orderConfigurations.entrySet()) {
				final IComponent<String> selectedType = ((RelationshipWidgetBean) leafWidgetBean).getSelectedType();
				if (!NullComponent.isNullComponent(selectedType) && getConfigurationId(entry.getKey()).equals(selectedType.getId())) {
					return entry.getValue();
				}
			}
			
		} else if (leafWidgetBean instanceof DescriptionWidgetBean) {
		
			final DescriptionWidgetBean descriptionWidgetBean = (DescriptionWidgetBean) leafWidgetBean;
			
			// XXX: fake the preferred term ID for the configuration
			final String id = getDescriptionTypeId(descriptionWidgetBean);
			
			for (final Entry<String, AttributeOrderConfiguration> entry : orderConfigurations.entrySet()) {
				
				if (getConfigurationId(entry.getKey()).equals(id)) {
					return entry.getValue();
				}
			}
			
		} else if (leafWidgetBean instanceof DataTypeWidgetBean) {
			
			for (final Entry<String, AttributeOrderConfiguration> entry : orderConfigurations.entrySet()) {
				if (getConfigurationId(entry.getKey()).equals(((DataTypeWidgetBean) leafWidgetBean).getSelectedLabel())) {
					return entry.getValue();
				}
			}
		}
		
		return null;
	}

	/**
	 * @param descriptionWidgetBean the widget bean to extract the type ID from
	 * @return the beans selected description type ID or {@link DescriptionWidgetBean#PREFERRED_TERM_PLACEHOLDER the
	 * preferred term placeholder ID}, used for backwards compatibility. May return with {@code null} if the description type is unset.
	 */
	private static String getDescriptionTypeId(final DescriptionWidgetBean descriptionWidgetBean) {
		
		if (NullComponent.isNullComponent(descriptionWidgetBean.getSelectedType())) {
			return null;
		}
		
		if (descriptionWidgetBean.isPreferred() && !Concepts.FULL_NAME.equals(descriptionWidgetBean.getSelectedType().getId())) {
			return DescriptionWidgetBean.PREFERRED_TERM_PLACEHOLDER.getId(); 
		} else {
			return descriptionWidgetBean.getSelectedType().getId();
		}
	}
	
	/*returns with the entry key.*/
	//TODO ugly but around 1.8 we will store the priority in the MRCM rule itself. will be eliminated once and for all.
	private static String getConfigurationId(final String entryKey) {
		return entryKey.split("_")[1].trim();
	}
	
	private LeafWidgetBeanSorter() {
		/*intentionally ignored*/
	}

	/////////////////////////////////////////////////
	// Descriptions section
	/////////////////////////////////////////////////

	private static final Function<LeafWidgetBean, String> GET_CLASS_SIMPLE_NAME = new Function<LeafWidgetBean, String>() { @Override public String apply(final LeafWidgetBean input) {
		return input.getClass().getSimpleName();
	}};
	
	private static final Function<LeafWidgetBean, String> GET_DESCRIPTION_TYPE_ID = new Function<LeafWidgetBean, String>() { @Override public String apply(final LeafWidgetBean input) {
		
		if (input instanceof DescriptionWidgetBean) {
			final String typeId = getDescriptionTypeId((DescriptionWidgetBean) input);
			return StringUtils.isEmpty(typeId) ? null : typeId;
		} else {
			return null;
		}
		
	}};
	
	private static final Function<LeafWidgetBean, String> GET_DESCRIPTION_TYPE_LABEL = new Function<LeafWidgetBean, String>() { @Override public String apply(final LeafWidgetBean input) {
		return (input instanceof DescriptionWidgetBean) ? ((DescriptionWidgetBean) input).getSelectedType().getLabel() : null;
	}};

	private static final List<String> DESCRIPTION_TYPE_RANK = ImmutableList.of(
			Concepts.FULL_NAME,
			Concepts.FULLY_SPECIFIED_NAME,
			DescriptionWidgetBean.PREFERRED_TERM_PLACEHOLDER.getId(),
			Concepts.SYNONYM);

	// Types mentioned in DESCRIPTION_TYPE_RANK goes in the order of appearance, leaves which don't have a description type or weren't mentioned go to the end.
	private static final Ordering<LeafWidgetBean> DESCRIPTION_TYPE_ORDERING = ExplicitFirstOrdering.create(DESCRIPTION_TYPE_RANK)
			.onResultOf(GET_DESCRIPTION_TYPE_ID)
			.nullsLast();
	
	// Description labels are sorted in natural order; leaves which don't have a description label go to the end.
	private static final Ordering<LeafWidgetBean> DESCRIPTION_LABEL_ORDERING = Ordering.natural()
			.onResultOf(GET_DESCRIPTION_TYPE_LABEL)
			.nullsLast();
	
	// Descriptions go first in a description section; in case of a tie, the two orderings above should decide.
	private static final Ordering<LeafWidgetBean> DESCRIPTION_SECTION_ORDERING = ExplicitFirstOrdering.create(DescriptionWidgetBean.class.getSimpleName())
			.onResultOf(GET_CLASS_SIMPLE_NAME)
			.compound(DESCRIPTION_TYPE_ORDERING)
			.compound(DESCRIPTION_LABEL_ORDERING)
			.nullsLast();

	/////////////////////////////////////////////////
	// Properties section
	/////////////////////////////////////////////////
	
	private static final Function<LeafWidgetBean, String> GET_RELATIONSHIP_TYPE_ID = new Function<LeafWidgetBean, String>() { @Override public String apply(final LeafWidgetBean input) {
		
		if (input instanceof RelationshipWidgetBean) {
			final RelationshipWidgetBean rwb = (RelationshipWidgetBean) input;
			return NullComponent.isNullComponent(rwb.getSelectedType()) ? null : rwb.getSelectedType().getId();
		} else {
			return null;
		}
		
	}};
	
	private static final Function<LeafWidgetBean, String> GET_RELATIONSHIP_CHARACTERISTIC_TYPE_ID = new Function<LeafWidgetBean, String>() { @Override public String apply(final LeafWidgetBean input) {
		if (input instanceof RelationshipWidgetBean) {
			final RelationshipWidgetBean rwb = (RelationshipWidgetBean) input;
			return NullComponent.isNullComponent(rwb.getSelectedCharacteristicType()) ? null : rwb.getSelectedCharacteristicType().getId();
		} else {
			return null;
		}
	}};
	
	private static final Function<LeafWidgetBean, String> GET_LEAF_TYPE_LABEL = new Function<LeafWidgetBean, String>() { @Override public String apply(final LeafWidgetBean input) {

		String label = null;
		
		if (input instanceof DescriptionWidgetBean) {
			final IComponent<String> selectedType = ((DescriptionWidgetBean) input).getSelectedType();
			if (!NullComponent.isNullComponent(selectedType)) {
				label =  selectedType.getLabel();
			}
		} else if (input instanceof RelationshipWidgetBean) {
			final IComponent<String> selectedType = ((RelationshipWidgetBean) input).getSelectedType();
			if (!NullComponent.isNullComponent(selectedType)) {
				label =  selectedType.getLabel();
			}
		} else if (input instanceof DataTypeWidgetBean) {
			label =  DataTypeUtils.getDefaultDataTypeLabel(((DataTypeWidgetBean) input).getSelectedLabel());
		}
		
		if (null == label) {
			return null;
		}
		
		// Replace "value" and "units" suffixes with "1" and "2", respectively, to make values show up before units
		if (label.endsWith(SUFFIX_VALUE)) {
			return label.substring(0, label.length() - SUFFIX_VALUE.length()) + "1";
		}
		
		if (label.endsWith(SUFFIX_UNITS)) {
			return label.substring(0, label.length() - SUFFIX_UNITS.length()) + "2";
		}
		
		return label;
	}};
	
	private static final Function<LeafWidgetBean, String> GET_LEAF_VALUE_LABEL = new Function<LeafWidgetBean, String>() { @Override public String apply(final LeafWidgetBean input) {
		
		if (input instanceof DescriptionWidgetBean) {
			return ((DescriptionWidgetBean) input).getTerm();
		}
		
		if (input instanceof RelationshipWidgetBean) {
			final IComponent<String> selectedValue = ((RelationshipWidgetBean) input).getSelectedValue();
			if (!NullComponent.isNullComponent(selectedValue)) {
				return selectedValue.getLabel();
			}
		}
		
		if (input instanceof DataTypeWidgetBean) {
			return String.valueOf(((DataTypeWidgetBean) input).getSelectedValue());
		}
		
		return null;
		
	}};
	
	private static final List<String> RELATIONSHIP_TYPE_RANK = ImmutableList.of(
			Concepts.IS_A);
	
	private static final List<String> RELATIONSHIP_CHARACTERISTIC_TYPE_RANK = ImmutableList.of(
			Concepts.STATED_RELATIONSHIP,
			Concepts.INFERRED_RELATIONSHIP,
			Concepts.DEFINING_RELATIONSHIP,
			Concepts.QUALIFYING_RELATIONSHIP,
			Concepts.ADDITIONAL_RELATIONSHIP);
	
	private static final List<String> PROPERTIES_SECTION_CLASS_RANK = ImmutableList.of(
			RelationshipWidgetBean.class.getSimpleName(),
			DataTypeWidgetBean.class.getSimpleName());

	// Relationships with an IS A type identifier go first
	private static final Ordering<LeafWidgetBean> RELATIONSHIP_TYPE_ORDERING = ExplicitFirstOrdering.create(RELATIONSHIP_TYPE_RANK)
			.nullsLast()
			.onResultOf(GET_RELATIONSHIP_TYPE_ID);
	
	private static final Ordering<Comparable> NULL_SAFE_NATURAL_ORDERING = Ordering.natural().nullsLast();
	
	// ...but then all kinds of leaves are sorted based on their type label (allowing unit relationships and value concrete domains to match up)
	private static final Ordering<LeafWidgetBean> LEAF_TYPE_LABEL_ORDERING = NULL_SAFE_NATURAL_ORDERING
			.onResultOf(GET_LEAF_TYPE_LABEL);
	
	// ...leaves with equal labels can be sorted by their class rank...
	private static final Ordering<LeafWidgetBean> LEAF_CLASS_ORDERING = ExplicitFirstOrdering.create(PROPERTIES_SECTION_CLASS_RANK)
			.onResultOf(GET_CLASS_SIMPLE_NAME);
	
	// ...and following that, relationships should be sorted by characteristic type. 
	private static final Ordering<LeafWidgetBean> RELATIONSHIP_CHARACTERISTIC_TYPE_ORDERING = ExplicitFirstOrdering.create(RELATIONSHIP_CHARACTERISTIC_TYPE_RANK)
			.nullsLast()
			.onResultOf(GET_RELATIONSHIP_CHARACTERISTIC_TYPE_ID);
	
	private static final Ordering<LeafWidgetBean> LEAF_VALUE_ORDERING = NULL_SAFE_NATURAL_ORDERING
			.onResultOf(GET_LEAF_VALUE_LABEL);
	
	// The final ordering is a compound of the orderings configured above.
	private static final Ordering<LeafWidgetBean> PROPERTIES_SECTION_ORDERING = RELATIONSHIP_TYPE_ORDERING
			.compound(LEAF_TYPE_LABEL_ORDERING)
			.compound(LEAF_CLASS_ORDERING)
			.compound(RELATIONSHIP_CHARACTERISTIC_TYPE_ORDERING)
			.compound(LEAF_VALUE_ORDERING)
			.nullsLast();
	
	private static Ordering<LeafWidgetBean> getDefaultComparator(final SectionType type) {
		
		switch (type) {
			case DESCRIPTION_SECTION:
				return DESCRIPTION_SECTION_ORDERING;
			case PROPERTY_SECTION:
				return PROPERTIES_SECTION_ORDERING;
			default:
				throw new IllegalStateException("Unhandled section type: " + type);
		}
	}
	
	private static final class LeafWidgetBeanComparator implements Comparator<LeafWidgetBean> {

		private final Map<String, AttributeOrderConfiguration> orderConfigurations;
		private final SectionType type;

		private LeafWidgetBeanComparator(final SnomedSimpleTypeRefSetAttributeConfiguration configuration, final SectionType type) {
			this.orderConfigurations = Maps.newHashMap(configuration.getEntries());
			this.type = type;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final LeafWidgetBean o1, final LeafWidgetBean o2) {
			
			final AttributeOrderConfiguration configuration = getConfiguration(o1, orderConfigurations);
			final AttributeOrderConfiguration configuration2 = getConfiguration(o2, orderConfigurations);
			if (null == configuration && null == configuration2) {
				return getDefaultComparator(type).compare(o1, o2);
			} else if (null == configuration) {
				return 1;
			} else if (null == configuration2) {
				return -1;
			} else {
				final int configurationCompare = configuration.compareTo(configuration2);
				return (0 != configurationCompare) ? configurationCompare : getDefaultComparator(type).compare(o1, o2);
			}
		}
	}
}