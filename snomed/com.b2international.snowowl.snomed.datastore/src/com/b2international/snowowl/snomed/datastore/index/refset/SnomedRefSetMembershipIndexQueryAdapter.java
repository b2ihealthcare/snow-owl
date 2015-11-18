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
package com.b2international.snowowl.snomed.datastore.index.refset;

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.mrcm.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.Lists;

/**
 * Lucene specific query adapter for retrieving lightweight representations of SNOMED CT reference set members.
 */
public class SnomedRefSetMembershipIndexQueryAdapter extends SnomedRefSetMemberIndexQueryAdapter implements Serializable {

	private static final long serialVersionUID = 1947806511934554585L;

	public static SnomedRefSetMembershipIndexQueryAdapter createFindByStorageKeyQuery(final long storageKey) {
		checkArgument(storageKey > 0L, "storageKey may not be 0 or negative.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -4427757769646167620L;
			@Override public Query createQuery() {
				return SnomedMappings.newQuery().storageKey(storageKey).matchAll();
			}
		};
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindByUuidQuery(final String uuid) {
		checkNotNull(uuid, "SNOMED CT reference set member UUID argument cannot be null.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -4427757769646167620L;
			@Override public Query createQuery() {
				return SnomedMappings.newQuery().field(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID, uuid).matchAll();
			}
		};
	}
	
	public static SnomedRefSetMemberIndexQueryAdapter createFindReferencingMembers(final String componentId) {
		checkNotNull(componentId, "Component identifier argument cannot be null.");
		checkArgument(!StringUtils.isEmpty(componentId), "Component identifier argument cannot be empty.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				return SnomedMappings.newQuery().memberReferencedComponentId(componentId).matchAll();
			}
		};
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindByRefSetTypeQuery(final Iterable<SnomedRefSetType> types) {
		
		checkArgument(!CompareUtils.isEmpty(types), "SNOMED CT reference set type argument cannot be empty.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				return createRefSetTypeQuery(types);
			}
		};
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindByRefSetTypeQuery(
			final String terminologyComponentId, final Iterable<SnomedRefSetType> types, final Iterable<String> referencedComponentIds) {
		
		checkArgument(!CompareUtils.isEmpty(types), "SNOMED CT reference set type argument cannot be empty.");
		checkArgument(!CompareUtils.isEmpty(referencedComponentIds), "Referenced component identifiers argument cannot be empty.");
		checkNotNull(terminologyComponentId, "Terminology component ID argument cannot be null.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				final Query query = SnomedMappings.newQuery()
						.and(createReferencedComponentTypeQuery(terminologyComponentId))
						.and(createRefSetTypeQuery(types))
						.matchAll();
				return new FilteredQuery(query, createMemberReferencedComponentIdFilter(referencedComponentIds));
			}
		};
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindPreferredTermMembersQuery(final Iterable<String> descriptionIds) {
		return createFindPreferredTermMembersQuery(descriptionIds, getConfiguredLanguageRefSetId());
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindPreferredTermMembersQuery(final Iterable<String> descriptionIds, final String languageRefSetId) {
		checkArgument(!CompareUtils.isEmpty(descriptionIds), "SNOMED CT description IDs argument cannot be empty.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				return new FilteredQuery(SnomedMappings.newQuery()
						.active()
						.memberRefSetId(languageRefSetId)
						.field(REFERENCE_SET_MEMBER_ACCEPTABILITY_ID, Long.valueOf(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED))
						.matchAll(),
						createMemberReferencedComponentIdFilter(descriptionIds));
			}
		};
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindAllLanguageMembersQuery(final Iterable<String> descriptionIds) {
		return createFindAllLanguageMembersQuery(descriptionIds, getConfiguredLanguageRefSetId());
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindAllLanguageMembersQuery(final Iterable<String> descriptionIds, final String languageRefSetId) {
		checkArgument(!CompareUtils.isEmpty(descriptionIds), "SNOMED CT description IDs argument cannot be empty.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				return new FilteredQuery(SnomedMappings.newQuery()
						.active()
						.memberRefSetId(languageRefSetId)
						.matchAll(), 
						createMemberReferencedComponentIdFilter(descriptionIds));
			}
		};
	}

	private static String getConfiguredLanguageRefSetId() {
		return ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId();
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindByRefSetIdQuery(
			final String componentType, final Iterable<String> refSetIds, final Iterable<String> referencedComponentIds) {
		
		checkArgument(!CompareUtils.isEmpty(referencedComponentIds), "SNOMED CT reference set type argument cannot be empty.");
		checkArgument(!CompareUtils.isEmpty(refSetIds), "Reference set identifiers argument cannot be empty.");
		checkNotNull(componentType, "Referenced component type argument cannot be null.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				final Query query = SnomedMappings.newQuery()
							.and(createReferencedComponentTypeQuery(componentType))
							.and(createRefSetIdQuery(refSetIds))
							.matchAll();
				return new FilteredQuery(query, createMemberReferencedComponentIdFilter(referencedComponentIds));
			}
		};
	} 
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindUnsetEffectiveTimeMembersQuery(final Iterable<String> refSetIds) {
		checkArgument(!CompareUtils.isEmpty(refSetIds), "Reference set identifiers argument cannot be empty.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				return SnomedMappings.newQuery()
						.and(createRefSetIdQuery(refSetIds))
						.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
						.matchAll();
			}
		};
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createMappingMembershipQuery(
			final String componentType, final String componentId) {
		
		checkNotNull(componentType, "Terminology component type argument cannot be null.");
		checkNotNull(componentId, "Component identifier argument cannot be null.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = 615721958388015634L;
			@Override public Query createQuery() {
				
				final SnomedQueryBuilder queryBuilder = SnomedMappings.newQuery();
				
				try { // workaround to skip component IDs with alphabetic characters
					Long.parseLong(componentId);
					final Query referencedComponentQuery = SnomedMappings.newQuery()
							.memberReferencedComponentId(componentId)
							.and(createReferencedComponentTypeQuery(componentType))
							.matchAll();
					queryBuilder.and(referencedComponentQuery);
					
				} catch (final NumberFormatException e) { /* ignore */ }
				
				final Query specialFieldQuery = SnomedMappings.newQuery()
						.and(createSpecialFieldTypeQuery(componentType, REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID))
						.field(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, componentId)
						.matchAll();

				queryBuilder.and(specialFieldQuery);
				
				return SnomedMappings.newQuery()
						.and(createRefSetTypeQuery(Lists.newArrayList(SnomedRefSetType.COMPLEX_MAP, SnomedRefSetType.SIMPLE_MAP, SnomedRefSetType.EXTENDED_MAP)))
						.and(queryBuilder.matchAny())
						.matchAll();
			}
		};
	} 
	
	private static Filter createMemberReferencedComponentIdFilter(final Iterable<String> ids) {
		final BooleanFilter idFilter = new BooleanFilter();
		for (final String id : ids) {
			final Query query = SnomedMappings.newQuery().memberReferencedComponentId(id).matchAll();
			idFilter.add(new QueryWrapperFilter(query), Occur.SHOULD);
		}
		return idFilter;
	}
	
	private static Query createRefSetIdQuery(final Iterable<String> ids) {
		final SnomedQueryBuilder qb = SnomedMappings.newQuery();
		for (final String id : ids) {
			qb.memberRefSetId(id);
		}
		return qb.matchAny();
	}
	
	private static Query createRefSetTypeQuery(final Iterable<SnomedRefSetType> types) {
		final SnomedQueryBuilder qb = SnomedMappings.newQuery();
		for (final SnomedRefSetType type : types) {
			qb.memberRefSetType(type);
		}
		return qb.matchAny();
	}
	
	private static Query createReferencedComponentTypeQuery(final String terminologyComponentId) {
		final int componentTypeValue = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsInt(terminologyComponentId);
		return SnomedMappings.newQuery().memberReferencedComponentType(componentTypeValue).matchAll();
	}
	
	private static Query createSpecialFieldTypeQuery(final String terminologyComponentId, final String fieldName) {
		final int componentTypeValue = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsInt(terminologyComponentId);
		return SnomedMappings.newQuery().field(fieldName, componentTypeValue).matchAll();
	}
	
	/**
	 * Lucene specific query adapter for retrieving information about SNOMED&nbsp;CT concrete data type reference set members and memberships.
	 * @see SnomedRefSetMembershipLookupService
	 * @see SnomedRefSetMembershipIndexQueryAdapter
	 */
	public static class SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter extends SnomedRefSetMemberIndexQueryAdapter implements Serializable {

		private static final long serialVersionUID = -8340776001873027403L;

		@SuppressWarnings("unchecked")
		public static <T extends SnomedRefSetMemberIndexEntry> IIndexQueryAdapter<T> createFindByStorageKeyQuery(final long storageKey) {
			checkArgument(storageKey > 0L, "storageKey may not be 0 or negative.");
			
			return (IIndexQueryAdapter<T>) new SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter() {
				private static final long serialVersionUID = -4427757769646167621L;
				@Override public Query createQuery() {
					return SnomedMappings.newQuery().storageKey(storageKey).matchAll();
				}
			};
		}
		
		@SuppressWarnings("unchecked")
		public static <T extends SnomedRefSetMemberIndexEntry> IIndexQueryAdapter<T> createFindByReferencedComponentIdsQuery(
				final String componentType, final Iterable<String> referencedComponentIds) { // FIXME: variable naming!

			checkArgument(!CompareUtils.isEmpty(referencedComponentIds), "Referenced component identifiers argument cannot be empty.");
			checkNotNull(componentType, "Referenced component type argument cannot be null.");
			
			return (IIndexQueryAdapter<T>) new SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter() {
				private static final long serialVersionUID = 8756363743189925011L;
				@Override public Query createQuery() {
					final int componentTypeValue = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsInt(componentType);
					final SnomedQueryBuilder query = SnomedMappings.newQuery()
							.active()
							.memberRefSetType(SnomedRefSetType.CONCRETE_DATA_TYPE)
							.memberReferencedComponentType(componentTypeValue);
					
					final List<String> ids = Lists.newArrayList(referencedComponentIds);
					if (ids.size() > 1) {
						return new FilteredQuery(query.matchAll(), createMemberReferencedComponentIdFilter(referencedComponentIds));
					} else {
						return query.memberReferencedComponentId(ids.get(0)).matchAll();
					}
				}
			};
		}
		

		@SuppressWarnings("unchecked")
		public static <T extends SnomedRefSetMemberIndexEntry> IIndexQueryAdapter<T> createFindByRefSetTypeQuery() {

			return (IIndexQueryAdapter<T>) new SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter() {
				private static final long serialVersionUID = -785494956030520757L;
				@Override public Query createQuery() {
					return SnomedMappings.newQuery().memberRefSetType(SnomedRefSetType.CONCRETE_DATA_TYPE).matchAll();
				}
			};
		}
		
		@SuppressWarnings("unchecked")
		public static <T extends SnomedRefSetMemberIndexEntry> IIndexQueryAdapter<T> createFindByRefSetTypeQuery(final String componentType) {
			checkNotNull(componentType, "Referenced component type argument cannot be null.");
			
			return (IIndexQueryAdapter<T>) new SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter() {
				private static final long serialVersionUID = -8318912010028083774L;
				@Override public Query createQuery() {
					final int componentTypeValue = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsInt(componentType);
					return SnomedMappings.newQuery().memberRefSetType(SnomedRefSetType.CONCRETE_DATA_TYPE).memberReferencedComponentType(componentTypeValue).matchAll();
				}
			};
		}
		
		@SuppressWarnings("unchecked")
		public static <T extends SnomedRefSetMemberIndexEntry> IIndexQueryAdapter<T> createFindByDataType(final DataType type) {
			checkNotNull(type, "Data type argument cannot be null.");

			return (IIndexQueryAdapter<T>) new SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter() {
				private static final long serialVersionUID = -4361993210015507504L;
				@Override public Query createQuery() {
					return SnomedMappings.newQuery().active().memberRefSetId(SnomedRefSetUtil.getRefSetId(type)).matchAll();
				}
			};
		}
	}
}
