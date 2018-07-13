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

import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.COMPLEX_MAP;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.EXTENDED_MAP;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.QUERY;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.SIMPLE_MAP;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.SnowOwl;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRF2Folder;
import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/** 
 * Utility class collecting commons operations related to SNOMED CT reference sets. 
 */
public abstract class SnomedRefSetUtil {

	/***
	 * Mapping between the association indication reference set IDs and the human readable names instead of
	 * their CAPITAL CASE preferred terms.
	 */
	public static final Map<String, String> ASSOCIATION_REFSETS = ImmutableMap.<String, String>builder()
			.put(Concepts.REFSET_ALTERNATIVE_ASSOCIATION, "Alternative concept")
			.put(Concepts.REFSET_MOVED_FROM_ASSOCIATION, "Moved from")
			.put(Concepts.REFSET_MOVED_TO_ASSOCIATION, "Moved to")
			.put(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION, "Possibly equivalent to")
			.put(Concepts.REFSET_REFERS_TO_ASSOCIATION, "Refers to")
			.put(Concepts.REFSET_REPLACED_BY_ASSOCIATION, "Replaced by")
			.put(Concepts.REFSET_SAME_AS_ASSOCIATION, "Same as")
			.put(Concepts.REFSET_SIMILAR_TO_ASSOCIATION, "Similar to")
			.put(Concepts.REFSET_WAS_A_ASSOCIATION, "Was a")
			.build();
	
	
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

	/**
	 * The set of all available datatypes.
	 */
	public static final Set<DataType> ALL_DATATYPES = ImmutableSet.copyOf(DataType.values());
	
	/**
	 * The set of datatypes for which no UI/constraint support exists at this time.
	 */
	public static final Set<DataType> UNSUPPORTED_DATATYPES = ImmutableSet.of(DataType.DATE);
	
	/**
	 * The set of supported datatypes: a difference of {@link #ALL_DATATYPES} and {@link #UNSUPPORTED_DATATYPES}.
	 */
	public static final Set<DataType> SUPPORTED_DATATYPES = ImmutableSet.copyOf(Sets.difference(ALL_DATATYPES, UNSUPPORTED_DATATYPES));
	
	private static SnomedCoreConfiguration getCoreConfiguration() {
		return ApplicationContext.getServiceForClass(SnowOwlConfiguration.class).getModuleConfig(SnomedCoreConfiguration.class);
	}
	
	public static EClass getRefSetMemberClass(final SnomedRefSetType type) {
		
		switch (type) {
			case SIMPLE:
				return SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER;
			case ATTRIBUTE_VALUE:
				return SnomedRefSetPackage.Literals.SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER;
			case LANGUAGE:
				return SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER;
			case COMPLEX_MAP: //$FALL-THROUGH$
			case EXTENDED_MAP:
				return SnomedRefSetPackage.Literals.SNOMED_COMPLEX_MAP_REF_SET_MEMBER;
			case SIMPLE_MAP: //$FALL-THROUGH$
			case SIMPLE_MAP_WITH_DESCRIPTION:
				return SnomedRefSetPackage.Literals.SNOMED_SIMPLE_MAP_REF_SET_MEMBER;
			case QUERY:
				return SnomedRefSetPackage.Literals.SNOMED_QUERY_REF_SET_MEMBER;
			case DESCRIPTION_TYPE:
				return SnomedRefSetPackage.Literals.SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER;
			case ASSOCIATION:
				return SnomedRefSetPackage.Literals.SNOMED_ASSOCIATION_REF_SET_MEMBER;
			case CONCRETE_DATA_TYPE:
				return SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER;
			case MODULE_DEPENDENCY:
				return SnomedRefSetPackage.Literals.SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER;
			case OWL_AXIOM: //$FALL-THROUGH$
			case OWL_ONTOLOGY:
				return SnomedRefSetPackage.Literals.SNOMED_OWL_EXPRESSION_REF_SET_MEMBER;
			case MRCM_DOMAIN:
				return SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER;
			case MRCM_ATTRIBUTE_DOMAIN:
				return SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER;
			case MRCM_ATTRIBUTE_RANGE:
				return SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER;
			case MRCM_MODULE_SCOPE:
				return SnomedRefSetPackage.Literals.SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER;
			default:
				throw new IllegalArgumentException("Unsupported reference set type: " + type);
		}
	}
	
	public static final Collection<EClass> REFSET_CLASSES = unmodifiableSet(newHashSet(
			SnomedRefSetPackage.Literals.SNOMED_MAPPING_REF_SET, 
			SnomedRefSetPackage.Literals.SNOMED_STRUCTURAL_REF_SET, 
			SnomedRefSetPackage.Literals.SNOMED_REGULAR_REF_SET
	));
	
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
	 * Attempts to retrieve the reference set type associated with the specified
	 * ancestor concept in the reference set hierarchy.
	 * <p>
	 * <b>Note</b>: Certain reference set types do not have a corresponding (fixed) common
	 * ancestor, and so will not be returned by this method, eg.: "concrete domain",
	 * "simple map with map target description"
	 * 
	 * @param conceptId the concept ID to find a corresponding reference set type for
	 * @return the detected reference set type, or <code>null</code>
	 */
	public static SnomedRefSetType getByConceptId(String conceptId) {
		switch (conceptId) {
			case Concepts.REFSET_ATTRIBUTE_VALUE_TYPE: 
				return SnomedRefSetType.ATTRIBUTE_VALUE;
			case Concepts.REFSET_LANGUAGE_TYPE: 
				return SnomedRefSetType.LANGUAGE;
			case Concepts.REFSET_QUERY_SPECIFICATION_TYPE: 
				return SnomedRefSetType.QUERY;
			case Concepts.SDD_DRUG_REFERENCE_SET: //$FALL-THROUGH$
			case Concepts.REFSET_SIMPLE_MAP_TYPE: 
				return SnomedRefSetType.SIMPLE_MAP;
			case Concepts.SDD_SIMPLE_TYPE_REFERENCE_SET: //$FALL-THROUGH$
			case Concepts.REFSET_SIMPLE_TYPE: 
				return SnomedRefSetType.SIMPLE;
			case Concepts.REFSET_COMPLEX_MAP_TYPE: 
				return SnomedRefSetType.COMPLEX_MAP;
			case Concepts.EXTENDED_MAP_TYPE: 
				return SnomedRefSetType.EXTENDED_MAP;
			case Concepts.REFSET_DESCRIPTION_TYPE: 
				return SnomedRefSetType.DESCRIPTION_TYPE;
			case Concepts.REFSET_ASSOCIATION_TYPE: 
				return SnomedRefSetType.ASSOCIATION;
			case Concepts.REFSET_MODULE_DEPENDENCY_TYPE: 
				return SnomedRefSetType.MODULE_DEPENDENCY;
			case Concepts.REFSET_OWL_AXIOM:
				return SnomedRefSetType.OWL_AXIOM;
			case Concepts.REFSET_OWL_ONTOLOGY:
				return SnomedRefSetType.OWL_ONTOLOGY;
			case Concepts.REFSET_MRCM_DOMAIN_INTERNATIONAL:
				return SnomedRefSetType.MRCM_DOMAIN;
			case Concepts.REFSET_MRCM_ATTRIBUTE_DOMAIN_INTERNATIONAL:
				return SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN;
			case Concepts.REFSET_MRCM_ATTRIBUTE_RANGE_INTERNATIONAL:
				return SnomedRefSetType.MRCM_ATTRIBUTE_RANGE;
			case Concepts.REFSET_MRCM_MODULE_SCOPE:
				return SnomedRefSetType.MRCM_MODULE_SCOPE;
			default: 
				return null;
		}
	}

	/**
	 * Returns {@code true} if the specified reference set type is either complex map or extended map type.
	 * @param type the type to check.
	 * @return {@code true} if the reference set type is complex or extended map type, otherwise returns with {@code false}.
	 */
	public static boolean isComplexMapping(final SnomedRefSetType type) {
		return COMPLEX_MAP.equals(type) || EXTENDED_MAP.equals(type);
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
				|| EXTENDED_MAP.equals(type);
	}
	
	/**Returns with {@code true} if the reference set argument is a query type reference set. Otherwise {@code false}.*/
	public static boolean isQueryType(final SnomedRefSet refSet) {
		return QUERY.equals(check(refSet).getType());
	}
	
	/**
	 * Returns a human-readable label for the specified reference set type.
	 * 
	 * @param type
	 * @return
	 */
	public static String getTypeLabel(SnomedRefSetType type) {
		
		switch (type) {
			case ATTRIBUTE_VALUE: return "Attribute value type reference set";
			case LANGUAGE: return "Language type reference set";
			case QUERY: return "Query type reference set";
			case SIMPLE_MAP: return "Simple map type reference set";
			case SIMPLE_MAP_WITH_DESCRIPTION: return "Simple map type with map target description";
			case SIMPLE: return "Simple type reference set";
			case COMPLEX_MAP: return "Complex map type reference set";
			case EXTENDED_MAP: return "Extended map type reference set";
			case DESCRIPTION_TYPE: return "Description type reference set";
			case ASSOCIATION: return "Association type reference set";
			case CONCRETE_DATA_TYPE: return "Concrete domain type reference set";
			case MODULE_DEPENDENCY: return "Module dependency type reference set";
			case OWL_AXIOM: return "OWL axiom type reference set";
			case OWL_ONTOLOGY: return "OWL ontology type reference set";
			case MRCM_DOMAIN: return "MRCM domain type reference set";
			case MRCM_ATTRIBUTE_DOMAIN: return "MRCM attribute domain type reference set";
			case MRCM_ATTRIBUTE_RANGE: return "MRCM attribute range type reference set";
			case MRCM_MODULE_SCOPE: return "MRCM module scope type reference set";
			default:
				throw new IllegalArgumentException("Unexpected reference set type: " + type);
		}
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
	
	@SuppressWarnings("unchecked")
	public static <T> T defaultValueForDataType(final DataType dataType) {
		checkArgument(null != dataType, "Datatype argument cannot be null.");
		
		switch (dataType) {
			case BOOLEAN: return (T) Boolean.FALSE;
			case DECIMAL: return (T) BigDecimal.ZERO;
			case INTEGER: return (T) Integer.valueOf(0);
			case DATE: return (T) Dates.todayGmt();
			case STRING: return (T) "";
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
	
	public static String getLabel(final DataType dataType) {
		switch (dataType) {
			case BOOLEAN: return "Boolean";
			case DATE: return "Date";
			case DECIMAL: return "Decimal";
			case INTEGER: return "Integer";
			case STRING: return "String";
			default: throw new IllegalArgumentException("Unknown datatype: " + dataType);
		}
	}
	
	public static <T> DataType getByClass(Class<T> clazz) {
		if (clazz.equals(String.class)) {
			return DataType.STRING;
		} else if (clazz.equals(Integer.class)) {
			return DataType.INTEGER;
		} else if (clazz.equals(BigDecimal.class)) {
			return DataType.DECIMAL;
		} else if (clazz.equals(Boolean.class)) {
			return DataType.BOOLEAN;
		} else if (clazz.equals(Date.class)) {
			return DataType.DATE;
		} else {
			throw new IllegalArgumentException("Unsupported datatype class: " + clazz.getName());
		}
	}

	private SnomedRefSetUtil() {
		// Suppress instantiation
	}

}
