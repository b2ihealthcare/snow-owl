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

import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.ATTRIBUTE_VALUE;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.COMPLEX_MAP;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.DESCRIPTION_TYPE;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.EXTENDED_MAP;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.LANGUAGE;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.QUERY;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.SIMPLE;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.SIMPLE_MAP;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.get;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Shorts;

/** 
 * Utility class collecting commons operations related to SNOMED CT reference sets. 
 *
 */
public abstract class SnomedRefSetUtil {

	private static final SnomedRefSetType[] REF_SET_VIEW_ROOTS = new SnomedRefSetType[] {
		ATTRIBUTE_VALUE, 
		COMPLEX_MAP, 
		DESCRIPTION_TYPE, 
		QUERY, 
		SIMPLE_MAP, 
		SIMPLE,
		EXTENDED_MAP
	};

	/***
	 * Mapping between the association indication reference set IDs and the human readable names instead of
	 * their CAPITAL CASE preferred terms.
	 */
	public static final Map<String, String> ASSOCIATION_REFSETS = ImmutableMap.<String, String>builder()
			.put(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR, "Inactivity status")
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
		
	
	/**
	 * Map for looking up the concrete data type reference set identifier
	 * concept IDs based on the associated concrete domain data types.
	 */
	private static final BiMap<com.b2international.snowowl.snomed.mrcm.DataType, String> TYPE_TO_REFSET_MAP = ImmutableBiMap.<com.b2international.snowowl.snomed.mrcm.DataType, String>builder()
			.put(com.b2international.snowowl.snomed.mrcm.DataType.BOOLEAN, Concepts.REFSET_BOOLEAN_TYPE)
			.put(com.b2international.snowowl.snomed.mrcm.DataType.DATE, Concepts.REFSET_DATETIME_TYPE)
			.put(com.b2international.snowowl.snomed.mrcm.DataType.FLOAT, Concepts.REFSET_FLOAT_TYPE)
			.put(com.b2international.snowowl.snomed.mrcm.DataType.INTEGER, Concepts.REFSET_INTEGER_TYPE)
			.put(com.b2international.snowowl.snomed.mrcm.DataType.STRING, Concepts.REFSET_STRING_TYPE)
			.build();
	
	/**
	 * Map for looking up MRCM datatype enum values for AU concrete domain reference set identifiers.
	 */
	private static final Map<String, com.b2international.snowowl.snomed.mrcm.DataType> AU_REFSET_TO_TYPE_MAP = ImmutableMap.<String, com.b2international.snowowl.snomed.mrcm.DataType>builder()
			.put(Concepts.REFSET_STRENGTH, com.b2international.snowowl.snomed.mrcm.DataType.FLOAT)
			.put(Concepts.REFSET_UNIT_OF_USE_QUANTITY, com.b2international.snowowl.snomed.mrcm.DataType.FLOAT)
			.put(Concepts.REFSET_UNIT_OF_USE_SIZE, com.b2international.snowowl.snomed.mrcm.DataType.FLOAT)
			.put(Concepts.REFSET_SUBPACK_QUANTITY, com.b2international.snowowl.snomed.mrcm.DataType.INTEGER)
			.build();
	
	/**
	 * Map for navigating between the {@link com.b2international.snowowl.snomed.mrcm.DataType} and the
	 * {@link DataType} enumerations.
	 */
	public static final BiMap<com.b2international.snowowl.snomed.mrcm.DataType, DataType> DATA_TYPE_BIMAP = ImmutableBiMap
			.<com.b2international.snowowl.snomed.mrcm.DataType, DataType> builder().put(com.b2international.snowowl.snomed.mrcm.DataType.BOOLEAN, DataType.BOOLEAN)
			.put(com.b2international.snowowl.snomed.mrcm.DataType.DATE, DataType.DATE).put(com.b2international.snowowl.snomed.mrcm.DataType.FLOAT, DataType.DECIMAL)
			.put(com.b2international.snowowl.snomed.mrcm.DataType.INTEGER, DataType.INTEGER).put(com.b2international.snowowl.snomed.mrcm.DataType.STRING, DataType.STRING).build();
	
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
			case SIMPLE_MAP:
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
			default:
				throw new IllegalArgumentException("Unsupported reference set type: " + type);
		}
	}
	
	public static final Collection<EClass> REFSET_CLASSES = unmodifiableSet(newHashSet(
			SnomedRefSetPackage.Literals.SNOMED_MAPPING_REF_SET, 
			SnomedRefSetPackage.Literals.SNOMED_STRUCTURAL_REF_SET, 
			SnomedRefSetPackage.Literals.SNOMED_REGULAR_REF_SET
	));
	
	public static String getTableName(final SnomedRefSetType type) {
		
		switch (type) {
			case SIMPLE:
				return "SNOMEDREFSET_SNOMEDREFSETMEMBER";
			case ATTRIBUTE_VALUE:
				return "SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER";
			case LANGUAGE:
				return "SNOMEDREFSET_SNOMEDLANGUAGEREFSETMEMBER";
			case SIMPLE_MAP:
				return "SNOMEDREFSET_SNOMEDSIMPLEMAPREFSETMEMBER";
			case QUERY:
				return "SNOMEDREFSET_SNOMEDQUERYREFSETMEMBER";
			case COMPLEX_MAP: //$FALL-THROUGH$
			case EXTENDED_MAP:
				return "SNOMEDREFSET_SNOMEDCOMPLEXMAPREFSETMEMBER";
			case DESCRIPTION_TYPE:
				return "SNOMEDREFSET_SNOMEDDESCRIPTIONTYPEREFSETMEMBER";
			case CONCRETE_DATA_TYPE:
				return "SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER";
			default:
				throw new IllegalArgumentException("Unsupported reference set type: " + type);
		}
	}

	public static String getSpecificCondition(final SnomedRefSetType type) {
		
		switch (type) {
			case COMPLEX_MAP: //$FALL-THROUGH$
			case SIMPLE_MAP: //$FALL-THROUGH$
			case EXTENDED_MAP:
				return "MAPTARGETID";
			case ATTRIBUTE_VALUE:
				return "VALUEID";
			case QUERY:
				return "QUERY";
			case SIMPLE: //$FALL-THROUGH$
			case LANGUAGE:
				return "ACCEPTABILITYID";
			case DESCRIPTION_TYPE:
				return "DESCRIPTIONFORMAT";
			default:
				throw new IllegalArgumentException("Unsupported reference set type: " + type);
		}
	}
	
	/**
	 * Returns with the identifier concept ID of the concrete data type reference set specified by the data type enumeration.
	 * <br>May return with {@code null}.
	 * @param dataType the data type.
	 * @return the identifier concept ID of the SNOMED&nbsp;CT concrete data type reference set.
	 */
	public static String getRefSetId(final com.b2international.snowowl.snomed.mrcm.DataType dataType) {
		return TYPE_TO_REFSET_MAP.get(dataType);
	}
	
	/**
	 * Returns with the identifier concept ID of the concrete data type reference set specified by the data type enumeration.
	 * <br>May return with {@code null}.
	 * @param dataType the data type.
	 * @return the identifier concept ID of the SNOMED&nbsp;CT concrete data type reference set.
	 */
	public static String getRefSetId(final DataType dataType) {
		return getRefSetId(DATA_TYPE_BIMAP.inverse().get(dataType));
	}
	
	/**
	 * Returns with the data type enumeration associated with the SNOMED&nbsp;CT concrete data type reference set.
	 * @param identifierConceptId the identifier concept ID of the concrete data type reference set.
	 * @return the data type enumeration for the reference set.
	 */
	public static com.b2international.snowowl.snomed.mrcm.DataType getDataType(final String identifierConceptId) {
		com.b2international.snowowl.snomed.mrcm.DataType dataType = TYPE_TO_REFSET_MAP.inverse().get(identifierConceptId);
		
		if (null == dataType) {
			dataType = AU_REFSET_TO_TYPE_MAP.get(identifierConceptId);
		}
		
		return dataType;
	}
	
	/**
	 * Returns with the unique terminology component ID as a short for the <i>special field</i> of a 
	 * SNOMED&nbsp;CT reference set member given as a {@link Document}.
	 * @param document the document representing a SNOMED&nbsp;CT reference set member.
	 * @return the terminology component ID.
	 */
	public static short getSpecialFieldComponentTypeId(final Document document) {
		SnomedRefSetType type = get(SnomedMappings.memberRefSetType().getValue(document));
		IndexableField typeFiled = null;
		switch (Preconditions.checkNotNull(type, "SNOMED CT reference set type was null for document: " + document)) {
			
			case LANGUAGE://$FALL-THROUGH$
			case ASSOCIATION://$FALL-THROUGH$
			case DESCRIPTION_TYPE://$FALL-THROUGH$
			case ATTRIBUTE_VALUE:
				return SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
			case QUERY://$FALL-THROUGH$
			case SIMPLE://$FALL-THROUGH$
			case CONCRETE_DATA_TYPE:
				return CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
			case SIMPLE_MAP://$FALL-THROUGH$
			case COMPLEX_MAP://$FALL-THROUGH$ 
			case EXTENDED_MAP:
				typeFiled = document.getField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID);
				return Shorts.checkedCast(IndexUtils.getIntValue(Preconditions.checkNotNull(typeFiled, "Referenced component type ID field does not exists: " + document)));
			default:
				throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + type);
		}
		
	}
	
	/**
	 * Returns with the unique terminology component ID as a short for the <i>special field</i> of a 
	 * SNOMED&nbsp;CT reference set member given as a {@link Document}.
	 * <p>
	 * <b>NOTE:&nbsp;</b>Does not support neither simple map nor complex map reference set types.</p>  
	 * @param document the document representing a SNOMED&nbsp;CT reference set member.
	 * @return the terminology component ID.
	 */
	public static short getSpecialFieldComponentTypeId(final SnomedRefSetType type) {
		
		switch (Preconditions.checkNotNull(type, "SNOMED CT reference set type argument cannot be null")) {
			
			case LANGUAGE://$FALL-THROUGH$
			case ASSOCIATION://$FALL-THROUGH$
			case DESCRIPTION_TYPE://$FALL-THROUGH$
			case ATTRIBUTE_VALUE:
				return SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
			case QUERY://$FALL-THROUGH$
			case SIMPLE://$FALL-THROUGH$
			case MODULE_DEPENDENCY://$FALL-THROUGH$
			case CONCRETE_DATA_TYPE:
				return CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
			case SIMPLE_MAP://$FALL-THROUGH$
			case COMPLEX_MAP://$FALL-THROUGH$
			case EXTENDED_MAP:
				throw new UnsupportedOperationException("Cannot specify special field concept type ID for mapping type reference sets.");
			default:
				throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + type);
		}
		
	}
	
	/**
	 * Returns with the unique index field name of  the <i>special field</i> ID.
	 * <br>Returns with {@code null} in case of {@link SnomedRefSetType#SIMPLE}.
	 * @param type the reference set type.
	 * @return the unique index field name for storing the <i>special field</>i ID.
	 */
	@Nullable public static String getSpecialComponentIdIndexField(final SnomedRefSetType type) {
		Preconditions.checkNotNull(type, "SNOMED CT reference set type argument cannot be null.");
		switch (type) {
			case ASSOCIATION:
				return SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID;
			case ATTRIBUTE_VALUE:
				return SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_VALUE_ID;
			case COMPLEX_MAP: //$FALL-THROUGH$
			case EXTENDED_MAP:
				return SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
			case CONCRETE_DATA_TYPE:
				return SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DATA_TYPE_VALUE;
			case DESCRIPTION_TYPE:
				return SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID;
			case LANGUAGE:
				return SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID;
			case QUERY:
				return SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_QUERY;
			case SIMPLE:
				return null; //intentionally null.
			case SIMPLE_MAP:
				return SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
			case MODULE_DEPENDENCY:
				return null;
			default: throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + type);
		}
	}
	
	// concrete data type reference set members should not be shown in the UI.
	public static SnomedRefSetType getByConceptId(String conceptId) {
		
		if (conceptId == null) {
			return null;
		}
		
		if (Concepts.REFSET_ATTRIBUTE_VALUE_TYPE.equals(conceptId)) {
			return ATTRIBUTE_VALUE;
		} else if (Concepts.REFSET_LANGUAGE_TYPE.equals(conceptId)) {
			return LANGUAGE;
		} else if (Concepts.REFSET_QUERY_SPECIFICATION_TYPE.equals(conceptId)) {
			return QUERY;
		} else if (Concepts.REFSET_SIMPLE_MAP_TYPE.equals(conceptId)) {
			return SIMPLE_MAP;
		} else if (Concepts.REFSET_SIMPLE_TYPE.equals(conceptId)) {
			return SIMPLE;
		} else if (Concepts.REFSET_COMPLEX_MAP_TYPE.equals(conceptId)) {
			return COMPLEX_MAP;
		} else if (Concepts.EXTENDED_MAP_TYPE.equals(conceptId)) {
			return EXTENDED_MAP;
		} else if (Concepts.REFSET_DESCRIPTION_TYPE.equals(conceptId)) {
			return DESCRIPTION_TYPE;
		} else if (Concepts.SDD_DRUG_REFERENCE_SET.equals(conceptId)) {
			return SIMPLE_MAP; 
		} else if (Concepts.SDD_SIMPLE_TYPE_REFERENCE_SET.equals(conceptId)) {
			return SIMPLE; 
		} else {
			return null;
		}
	}

	/**
	 * Returns {@code true} if the specified reference set type is either complex map or extended map type.
	 * @param type the type to check.
	 * @return {@code true} if the reference set type is complex or extended map type, otherwise returns with {@code false}.
	 */
	public static boolean isComplexMapping(final SnomedRefSetType type) {
		return COMPLEX_MAP.equals(type)
				|| EXTENDED_MAP.equals(type);
	}
	
	/**
	 * Returns {@code true} if the specified reference set type is simple map, complex map or extended map type.
	 * @param type the type to check.
	 * @return {@code true} if the reference set type is simple, complex or extended map type, otherwise returns with {@code false}.
	 */
	public static boolean isMapping(final SnomedRefSetType type) {
		return SIMPLE_MAP.equals(type) 
				|| COMPLEX_MAP.equals(type)
				|| EXTENDED_MAP.equals(type);
	}
	
	/**Returns with {@code true} if the reference set argument is a query type reference set. Otherwise {@code false}.*/
	public static boolean isQueryType(final SnomedRefSet refSet) {
		return QUERY.equals(check(refSet).getType());
	}
	
	public static Iterable<SnomedRefSetType> getTypesForUI() {
		return new LinkedHashSet<SnomedRefSetType>(Arrays.asList(REF_SET_VIEW_ROOTS));
	}

	/**
	 * Get the type dependent preference page tab item text
	 * 
	 * @param type SnomedRefSetType
	 * @return String tab item text
	 */
	//concrete data type reference set members should not be shown in the UI.
	public static String getPreferencePageTabItemText(SnomedRefSetType type) {
		
		switch (type) {
			case ATTRIBUTE_VALUE:
				return "Attribute value type reference set";
			case LANGUAGE:
				return "Language type reference set"; // not used at preferences
			case QUERY:
				return "Query type reference set";
			case SIMPLE_MAP:
				return "Simple map type reference set";
			case SIMPLE:
				return "Simple type reference set";
			case COMPLEX_MAP:
				return "Complex map type reference set";
			case EXTENDED_MAP:
				return "Extended map type reference set";
			case DESCRIPTION_TYPE:
				return "Description type reference set";
			default:
				throw new IllegalArgumentException("Unexpected reference set type: " + type);
		}
	}

	//concrete data type reference set members should not be shown in the UI.
	public static String getConceptId(SnomedRefSetType type) {
		
		switch (type) {
			case ATTRIBUTE_VALUE:
				return Concepts.REFSET_ATTRIBUTE_VALUE_TYPE;
			case LANGUAGE:
				return Concepts.REFSET_LANGUAGE_TYPE;
			case QUERY:
				return Concepts.REFSET_QUERY_SPECIFICATION_TYPE;
			case SIMPLE_MAP:
				return Concepts.REFSET_SIMPLE_MAP_TYPE;
			case SIMPLE:
				return Concepts.REFSET_SIMPLE_TYPE;
			case COMPLEX_MAP:
				return Concepts.REFSET_COMPLEX_MAP_TYPE;
			case DESCRIPTION_TYPE:
				return Concepts.REFSET_DESCRIPTION_TYPE;
			case CONCRETE_DATA_TYPE:
				return Concepts.REFSET_CONCRETE_DOMAIN_TYPE_SG;
			case ASSOCIATION:
				return Concepts.REFSET_ASSOCIATION_TYPE;
			case MODULE_DEPENDENCY:
				return Concepts.REFSET_MODULE_DEPENDENCY_TYPE;
			case EXTENDED_MAP:
				return Concepts.EXTENDED_MAP_TYPE;
			default:
				throw new IllegalArgumentException("Unexpected reference set type: " + type);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T deserializeValue(final DataType dataType, final String serializedValue) {
		if (null == dataType)
			throw new IllegalArgumentException("Datatype argument cannot be null.");
		if (null == serializedValue)
			throw new IllegalArgumentException("Serialized value argument cannot be null.");
		switch (dataType) {
			case BOOLEAN: 
				if ("0".equals(serializedValue)) {
					return (T) Boolean.FALSE;
				} else if ("1".equals(serializedValue)) {
					return (T) Boolean.TRUE;
				} else {
					throw new IllegalArgumentException("Illegal serialized form of a boolean value. Expected either '0' or '1'. Was: '" + serializedValue + "'.");
				}
			case DECIMAL: return (T) new BigDecimal(serializedValue);
			case INTEGER: return (T) Integer.valueOf(serializedValue);
			case DATE: return (T) new Date(Long.valueOf(serializedValue));
			case STRING: return (T) serializedValue;
			default: throw new IllegalArgumentException("Unknown datatype: " + dataType);
		}
	}
	
	public static <T> String serializeValue(final DataType dataType, final T value) {
		if (null == dataType)
			throw new IllegalArgumentException("Datatype argument cannot be null.");
		if (null == value)
			throw new IllegalArgumentException("Value argument cannot be null.");
		switch (dataType) {
			case BOOLEAN: return ((Boolean) value).booleanValue() ? "1" : "0";
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