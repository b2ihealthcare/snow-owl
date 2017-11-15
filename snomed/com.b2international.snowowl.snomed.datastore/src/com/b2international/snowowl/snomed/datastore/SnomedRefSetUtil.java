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
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.ATTRIBUTE_VALUE;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.COMPLEX_MAP;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.DESCRIPTION_TYPE;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.EXTENDED_MAP;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.QUERY;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.SIMPLE;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.SIMPLE_MAP;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

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
	
	// NEHTA AMT SNOMED CT extension
	// TODO refactor extension data type handling
	@Deprecated
	public static final Map<String, DataType> AMT_REFSET_TO_DATATYPE_MAP = ImmutableMap.<String, DataType>builder()
			.put(Concepts.REFSET_STRENGTH, DataType.DECIMAL)
			.put(Concepts.REFSET_UNIT_OF_USE_QUANTITY, DataType.DECIMAL)
			.put(Concepts.REFSET_UNIT_OF_USE_SIZE, DataType.DECIMAL)
			.put(Concepts.REFSET_SUBPACK_QUANTITY, DataType.INTEGER)
			.build();

	public static final ImmutableSet<DataType> UNSUPPORTED_DATATYPES = ImmutableSet.<DataType> of(DataType.DATE);
	
	private static SnomedCoreConfiguration getCoreConfiguration() {
		return SnowOwlApplication.INSTANCE.getConfiguration().getModuleConfig(SnomedCoreConfiguration.class);
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
			case OWL_AXIOM:
				return SnomedRefSetPackage.Literals.SNOMED_ANNOTATION_REF_SET_MEMBER;
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
		if (getConcreteDomainRefSetMap().inverse().containsKey(refsetId)) {
			return getConcreteDomainRefSetMap().inverse().get(refsetId);
		} else {
			return AMT_REFSET_TO_DATATYPE_MAP.get(refsetId);
		}
	}
	
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
//	@Nullable public static String getSpecialComponentIdIndexField(final SnomedRefSetType type) {
//		Preconditions.checkNotNull(type, "SNOMED CT reference set type argument cannot be null.");
//		switch (type) {
//			case ASSOCIATION:
//				return SnomedMappings.memberTargetComponentId().fieldName();
//			case ATTRIBUTE_VALUE:
//				return SnomedMappings.memberValueId().fieldName();
//			case COMPLEX_MAP: //$FALL-THROUGH$
//			case EXTENDED_MAP:
//				return SnomedMappings.memberMapTargetComponentId().fieldName();
//			case CONCRETE_DATA_TYPE:
//				// FIXME: The previous value was memberDataTypeOrdinal, but this seems to fit better
//				return SnomedMappings.memberSerializedValue().fieldName(); 
//			case DESCRIPTION_TYPE:
//				return SnomedMappings.memberDescriptionFormatId().fieldName();
//			case LANGUAGE:
//				return SnomedMappings.memberAcceptabilityId().fieldName();
//			case QUERY:
//				return SnomedMappings.memberQuery().fieldName();
//			case SIMPLE:
//				return null; //intentionally null.
//			case SIMPLE_MAP:
//				return SnomedMappings.memberMapTargetComponentId().fieldName();
//			case MODULE_DEPENDENCY:
//				return null;
//			default: throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + type);
//		}
//	}
	
	// concrete domain reference set members should not be shown in the UI.
	public static SnomedRefSetType getByConceptId(String conceptId) {
		switch (conceptId) {
		case Concepts.REFSET_ATTRIBUTE_VALUE_TYPE: return SnomedRefSetType.ATTRIBUTE_VALUE;
		case Concepts.REFSET_LANGUAGE_TYPE: return SnomedRefSetType.LANGUAGE;
		case Concepts.REFSET_QUERY_SPECIFICATION_TYPE: return SnomedRefSetType.QUERY;
		case Concepts.SDD_DRUG_REFERENCE_SET:
		case Concepts.REFSET_SIMPLE_MAP_TYPE: return SnomedRefSetType.SIMPLE_MAP;
		case Concepts.SDD_SIMPLE_TYPE_REFERENCE_SET:
		case Concepts.REFSET_SIMPLE_TYPE: return SnomedRefSetType.SIMPLE;
		case Concepts.REFSET_COMPLEX_MAP_TYPE: return SnomedRefSetType.COMPLEX_MAP;
		case Concepts.EXTENDED_MAP_TYPE: return SnomedRefSetType.EXTENDED_MAP;
		case Concepts.REFSET_DESCRIPTION_TYPE: return SnomedRefSetType.DESCRIPTION_TYPE;
		case Concepts.REFSET_ASSOCIATION_TYPE: return SnomedRefSetType.ASSOCIATION;
		case Concepts.REFSET_MODULE_DEPENDENCY_TYPE: return SnomedRefSetType.MODULE_DEPENDENCY;
		
		default: return null;
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
	 * Get the type dependent label
	 * 
	 * @param type SnomedRefSetType
	 * @return
	 */
	public static String getTypeLabel(SnomedRefSetType type) {
		
		switch (type) {
			case ATTRIBUTE_VALUE: return "Attribute value type reference set";
			case LANGUAGE: return "Language type reference set";
			case QUERY: return "Query type reference set";
			case SIMPLE_MAP: return "Simple map type reference set";
			case SIMPLE: return "Simple type reference set";
			case COMPLEX_MAP: return "Complex map type reference set";
			case EXTENDED_MAP: return "Extended map type reference set";
			case DESCRIPTION_TYPE: return "Description type reference set";
			case ASSOCIATION: return "Association type reference set";
			case CONCRETE_DATA_TYPE: return "Concrete domain type reference set";
			case MODULE_DEPENDENCY: return "Module dependency type reference set";
			default:
				throw new IllegalArgumentException("Unexpected reference set type: " + type);
		}
	}

	//concrete domain reference set members should not be shown in the UI.
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
				return getCoreConfiguration().getConcreteDomainTypeRefsetIdentifier();
			case ASSOCIATION:
				return Concepts.REFSET_ASSOCIATION_TYPE;
			case MODULE_DEPENDENCY:
				return Concepts.REFSET_MODULE_DEPENDENCY_TYPE;
			case EXTENDED_MAP:
				return Concepts.EXTENDED_MAP_TYPE;
			case OWL_AXIOM:
				return Concepts.REFSET_OWL_AXIOM;
			default:
				throw new IllegalArgumentException("Unexpected reference set type: " + type);
		}
	}
	
	/**
	 * Returns concept IDs of the currently known and handled reference set types.
	 * @return
	 */
	public static Collection<String> getUIRefSetTypeConceptIds() {
		final Builder<String> ids = ImmutableSet.builder();
		for (SnomedRefSetType type : getTypesForUI()) {
			ids.add(getConceptId(type));
		}
		return ids.build();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T deserializeValue(final DataType dataType, final String serializedValue) {
		checkArgument(null != dataType, "Datatype argument cannot be null.");
		checkArgument(null != serializedValue, "Serialized value argument cannot be null.");
		
		switch (dataType) {
			case BOOLEAN: 
				Boolean booleanValue = BooleanUtils.valueOf(serializedValue);
				checkArgument(booleanValue != null, String.format("Illegal serialized form of a boolean value. Expected either '0' or '1'. Was: '%s'", serializedValue));
				return (T) booleanValue;
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

	/**
	 * Computes whether a reference set is structural or not.
	 * @param refSetId
	 * @param type
	 * @return
	 */
	public static boolean isStructural(final String refSetId, final SnomedRefSetType type) {
		switch (type) {
			case LANGUAGE: //$FALL-THROUGH$
			case CONCRETE_DATA_TYPE: //$FALL-THROUGH$
			case ASSOCIATION: //$FALL-THROUGH$
			case MODULE_DEPENDENCY: //$FALL-THROUGH$
				return true;
			case ATTRIBUTE_VALUE:
				return 
						Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(refSetId) 
						|| Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR.equals(refSetId) 
						|| Concepts.REFSET_RELATIONSHIP_REFINABILITY.equals(refSetId);
			default: return false;
		}
	}
}