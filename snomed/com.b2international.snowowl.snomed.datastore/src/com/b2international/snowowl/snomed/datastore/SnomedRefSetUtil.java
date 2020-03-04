/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.COMPLEX_BLOCK_MAP;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.COMPLEX_MAP;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.EXTENDED_MAP;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.SIMPLE_MAP;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRF2Folder;
import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

/** 
 * Utility class collecting commons operations related to SNOMED CT reference sets. 
 */
public abstract class SnomedRefSetUtil {

	private static BiMap<DataType, String> DATATYPE_TO_REFSET_MAP;
	
	public static final BiMap<DataType, String> getConcreteDomainRefSetMap() {
		if (DATATYPE_TO_REFSET_MAP == null) {
			DATATYPE_TO_REFSET_MAP = ImmutableBiMap.<DataType, String>builder()
				.put(DataType.BOOLEAN, getCoreConfiguration().getBooleanDatatypeRefsetIdentifier())
				.put(DataType.DATE, getCoreConfiguration().getDatetimeDatatypeRefsetIdentifier())
				.put(DataType.DECIMAL, getCoreConfiguration().getFloatDatatypeRefsetIdentifier())
				.put(DataType.INTEGER, getCoreConfiguration().getIntegerDatatypeRefsetIdentifier())
				.put(DataType.STRING, getCoreConfiguration().getStringDatatypeRefsetIdentifier())
				.build();
		}
		return DATATYPE_TO_REFSET_MAP;
	}
	
	public static final Multimap<SnomedRF2Folder, SnomedRefSetType> FOLDER_TO_REFSET_TYPE_MAP = ImmutableListMultimap.<SnomedRF2Folder, SnomedRefSetType>builder()
			.putAll(SnomedRF2Folder.TERMINOLOGY, 
						SnomedRefSetType.OWL_AXIOM,
						SnomedRefSetType.OWL_ONTOLOGY)
			.putAll(SnomedRF2Folder.CONTENT, 
						SnomedRefSetType.SIMPLE,
						SnomedRefSetType.ASSOCIATION,
						SnomedRefSetType.ATTRIBUTE_VALUE,
						SnomedRefSetType.QUERY,
						SnomedRefSetType.CONCRETE_DATA_TYPE)
			.putAll(SnomedRF2Folder.LANGUAGE, 
						SnomedRefSetType.LANGUAGE)
			.putAll(SnomedRF2Folder.MAP, 
						SnomedRefSetType.SIMPLE_MAP,
						SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION,
						SnomedRefSetType.COMPLEX_MAP,
						SnomedRefSetType.COMPLEX_BLOCK_MAP,
						SnomedRefSetType.EXTENDED_MAP)
			.putAll(SnomedRF2Folder.METADATA, 
						SnomedRefSetType.MRCM_DOMAIN,
						SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN,
						SnomedRefSetType.MRCM_ATTRIBUTE_RANGE,
						SnomedRefSetType.MRCM_MODULE_SCOPE,
						SnomedRefSetType.MODULE_DEPENDENCY,
						SnomedRefSetType.DESCRIPTION_TYPE)
			.build();
	
	public static final Map<SnomedRefSetType, SnomedRF2Folder> REFSET_TYPE_TO_FOLDER_MAP = FOLDER_TO_REFSET_TYPE_MAP.entries().stream()
			.collect(collectingAndThen(toMap(Entry::getValue, Entry::getKey), ImmutableMap::copyOf));

	private static SnomedCoreConfiguration getCoreConfiguration() {
		return ApplicationContext.getServiceForClass(SnowOwlConfiguration.class).getModuleConfig(SnomedCoreConfiguration.class);
	}
	
	/**
	 * Returns with the identifier concept ID of the concrete domain reference set specified by the data type enumeration.
	 * <br>May return with {@code null}.
	 * @param dataType the data type.
	 * @return the identifier concept ID of the SNOMED&nbsp;CT concrete domain reference set.
	 */
	public static String getRefSetId(final DataType dataType) {
		return getConcreteDomainRefSetMap().get(dataType);
	}
	
	/**
	 * Returns the proper {@link DataType} for the specified reference set id. The mapping is based on the IDs provided by the configuration.
	 * @param refsetId the id of the concrete domain reference set
	 * @return the proper datatype for the specified reference set id
	 */
	public static DataType getDataType(String refsetId) {
		return getConcreteDomainRefSetMap().inverse().get(refsetId);
	}
	
	/**
	 * Checks whether the supplied reference set identifier corresponds to a valid concrete domain type reference set.
	 * @param refSetId the reference set ID to check
	 * @return {@code true} if the ID matches a concrete domain type reference set, {@code false} otherwise  
	 */
	public static boolean isConcreteDomain(final String refSetId) {
		return getConcreteDomainRefSetMap().containsValue(refSetId);
	}
	
	/**
	 * Returns {@code true} if the specified reference set type is either complex map or extended map type.
	 * @param type the type to check.
	 * @return {@code true} if the reference set type is complex or extended map type, otherwise returns with {@code false}.
	 */
	public static boolean isComplexMapping(final SnomedRefSetType type) {
		return COMPLEX_MAP.equals(type) 
				|| COMPLEX_BLOCK_MAP.equals(type)
				|| EXTENDED_MAP.equals(type);
	}
	
	/**
	 * Returns {@code true} if the specified reference set type is simple map, complex map or extended map type.
	 * @param type the type to check.
	 * @return {@code true} if the reference set type is simple, complex or extended map type, otherwise returns with {@code false}.
	 */
	public static boolean isMapping(final SnomedRefSetType type) {
		return SIMPLE_MAP.equals(type) 
				|| SIMPLE_MAP_WITH_DESCRIPTION.equals(type)
				|| COMPLEX_MAP.equals(type)
				|| COMPLEX_BLOCK_MAP.equals(type)
				|| EXTENDED_MAP.equals(type);
	}
	
	/**
	 * Returns the identifier concept ancestor for the specified reference set type.
	 * 
	 * @param type
	 * @return
	 */
	public static String getParentConceptId(SnomedRefSetType type) {
		switch (type) {
			case ATTRIBUTE_VALUE:
				return Concepts.REFSET_ATTRIBUTE_VALUE_TYPE;
			case ANNOTATION:
				return Concepts.REFSET_ANNOTATION_TYPE;
			case LANGUAGE:
				return Concepts.REFSET_LANGUAGE_TYPE;
			case QUERY:
				return Concepts.REFSET_QUERY_SPECIFICATION_TYPE;
			case SIMPLE_MAP: //$FALL-THROUGH$
			case SIMPLE_MAP_WITH_DESCRIPTION:
				return Concepts.REFSET_SIMPLE_MAP_TYPE;
			case SIMPLE:
				return Concepts.REFSET_SIMPLE_TYPE;
			case COMPLEX_MAP:
				return Concepts.REFSET_COMPLEX_MAP_TYPE;
			case COMPLEX_BLOCK_MAP:
				return Concepts.REFSET_COMPLEX_BLOCK_MAP_TYPE;
			case DESCRIPTION_TYPE:
				return Concepts.REFSET_DESCRIPTION_TYPE;
			case CONCRETE_DATA_TYPE:
				return getCoreConfiguration().getConcreteDomainTypeRefsetIdentifier();
			case ASSOCIATION:
				return Concepts.REFSET_ASSOCIATION_TYPE;
			case MODULE_DEPENDENCY:
				return Concepts.REFSET_MODULE_DEPENDENCY_TYPE;
			case EXTENDED_MAP:
				return Concepts.EXTENDED_MAP_TYPE;
			case OWL_AXIOM: //$FALL-THROUGH$
			case OWL_ONTOLOGY:
				return Concepts.REFSET_OWL_EXPRESSION_TYPE;
			case MRCM_DOMAIN:
				return Concepts.REFSET_MRCM_DOMAIN_ROOT;
			case MRCM_ATTRIBUTE_DOMAIN:
				return Concepts.REFSET_MRCM_ATTRIBUTE_DOMAIN_ROOT;
			case MRCM_ATTRIBUTE_RANGE:
				return Concepts.REFSET_MRCM_ATTRIBUTE_RANGE_ROOT;
			case MRCM_MODULE_SCOPE:
				return Concepts.REFSET_MRCM_ROOT;
			default:
				throw new IllegalArgumentException("Unexpected reference set type: " + type);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T deserializeValue(final DataType dataType, final String serializedValue) {
		checkArgument(null != dataType, "Datatype argument cannot be null.");
		checkArgument(null != serializedValue, "Serialized value argument cannot be null.");
		
		switch (dataType) {
			case BOOLEAN: return (T) checkNotNull(BooleanUtils.valueOf(serializedValue));
			case DECIMAL: return (T) new BigDecimal(serializedValue);
			case INTEGER: return (T) Integer.valueOf(serializedValue);
			case DATE: return (T) new Date(Long.valueOf(serializedValue));
			case STRING: return (T) serializedValue;
			default: throw new IllegalArgumentException("Unknown datatype: " + dataType);
		}
	}
	
	public static <T> String serializeValue(final DataType dataType, final T value) {
		checkArgument(null != dataType, "Datatype argument cannot be null.");
		checkArgument(null != value, "Value argument cannot be null.");
		
		switch (dataType) {
			case BOOLEAN: return BooleanUtils.toString((Boolean) value);
			case DECIMAL: return ((BigDecimal) value).toPlainString();
			case INTEGER: return Integer.toString(((Integer) value).intValue());
			case DATE: return Long.toString(((Date) value).getTime());
			case STRING: return String.valueOf(value);
			default: throw new IllegalArgumentException("Unknown datatype: " + dataType);
		}
	}
	
	private SnomedRefSetUtil() {
		// Suppress instantiation
	}

}
