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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexQueries;
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
				final BooleanQuery query = new BooleanQuery();
				query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_STORAGE_KEY, IndexUtils.longToPrefixCoded(storageKey))), Occur.MUST);
				return query;
			}
		};
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindByUuidQuery(final String uuid) {
		checkNotNull(uuid, "SNOMED CT reference set member UUID argument cannot be null.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -4427757769646167620L;
			@Override public Query createQuery() {
				final BooleanQuery query = new BooleanQuery();
				final BooleanQuery fieldQuery = new BooleanQuery();
				query.add(internalAddParsedClause(fieldQuery, SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID, uuid), Occur.MUST);
				return query;
			}
		};
	}
	
	public static SnomedRefSetMemberIndexQueryAdapter createFindReferencingMembers(final String componentId) {
		checkNotNull(componentId, "Component identifier argument cannot be null.");
		checkArgument(!StringUtils.isEmpty(componentId), "Component identifier argument cannot be empty.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				final BooleanQuery query = new BooleanQuery();
				query.add(createComponentIdQuery(Collections.singleton(componentId)), Occur.MUST);
				return query;
			}
		};
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindByRefSetTypeQuery(final Iterable<SnomedRefSetType> types) {
		
		checkArgument(!CompareUtils.isEmpty(types), "SNOMED CT reference set type argument cannot be empty.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				final BooleanQuery query = new BooleanQuery();
				query.add(createRefSetTypeQuery(types), Occur.MUST);
				return query;
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
				final BooleanQuery query = new BooleanQuery();
				query.add(createReferencedComponentTypeQuery(terminologyComponentId), Occur.MUST);
				query.add(createRefSetTypeQuery(types), Occur.MUST);
				query.add(createComponentIdQuery(referencedComponentIds), Occur.MUST);
				return query;
			}
		};
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindByRefSetTypeQuery(
			final String terminologyComponentId, final Iterable<SnomedRefSetType> types) {
		
		checkArgument(!CompareUtils.isEmpty(types), "SNOMED CT reference set type argument cannot be empty.");
		checkNotNull(terminologyComponentId, "Terminology component ID argument cannot be null.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				final BooleanQuery query = new BooleanQuery();
				query.add(createReferencedComponentTypeQuery(terminologyComponentId), Occur.MUST);
				query.add(createRefSetTypeQuery(types), Occur.MUST);
				return query;
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
				final BooleanQuery query = new BooleanQuery();
				query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(languageRefSetId))), Occur.MUST);
				query.add(createComponentIdQuery(descriptionIds), Occur.MUST);
				query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_ACCEPTABILITY_ID, IndexUtils.longToPrefixCoded(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED))), Occur.MUST);
				query.add(new TermQuery(new Term(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))), Occur.MUST);
				return query;
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
				final BooleanQuery query = new BooleanQuery();
				query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(languageRefSetId))), Occur.MUST);
				query.add(createComponentIdQuery(descriptionIds), Occur.MUST);
				query.add(new TermQuery(new Term(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))), Occur.MUST);
				return query;
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
				final BooleanQuery query = new BooleanQuery();
				query.add(createReferencedComponentTypeQuery(componentType), Occur.MUST);
				query.add(createRefSetIdQuery(refSetIds), Occur.MUST);
				query.add(createComponentIdQuery(referencedComponentIds), Occur.MUST);
				return query;
			}
		};
	} 
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindUnsetEffectiveTimeMembersQuery(final Iterable<String> refSetIds) {
		checkArgument(!CompareUtils.isEmpty(refSetIds), "Reference set identifiers argument cannot be empty.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				final BooleanQuery query = new BooleanQuery();
				query.add(createRefSetIdQuery(refSetIds), Occur.MUST);
				query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_EFFECTIVE_TIME, EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL)), Occur.MUST);
				return query;
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
				final BooleanQuery query = new BooleanQuery();
				query.add(createRefSetTypeQuery(Lists.newArrayList(SnomedRefSetType.COMPLEX_MAP, SnomedRefSetType.SIMPLE_MAP, SnomedRefSetType.EXTENDED_MAP)), Occur.MUST);
				
				final BooleanQuery componentIdQuery = new BooleanQuery();
				
				final BooleanQuery referencedComponentQuery = new BooleanQuery();
				referencedComponentQuery.add(createReferencedComponentTypeQuery(componentType), Occur.MUST);
				referencedComponentQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, componentId)), Occur.MUST);

				final BooleanQuery specialFieldQuery = new BooleanQuery();
				specialFieldQuery.add(createSpecialFieldTypeQuery(componentType, REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID), Occur.MUST);
				specialFieldQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, componentId)), Occur.MUST);
				
				componentIdQuery.add(referencedComponentQuery, Occur.SHOULD);
				componentIdQuery.add(specialFieldQuery, Occur.SHOULD);
				query.add(componentIdQuery, Occur.MUST);
				return query;
			}
		};
	} 
	
	private static Query createComponentIdQuery(final Iterable<String> ids) {
		final BooleanQuery idQuery = new BooleanQuery();
		for (final String id : ids) {
			idQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, id)), Occur.SHOULD);
		}
		return idQuery;
	}
	
	private static Query createRefSetIdQuery(final Iterable<String> ids) {
		final BooleanQuery idQuery = new BooleanQuery();
		for (final String id : ids) {
			idQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(id))), Occur.SHOULD);
		}
		return idQuery;
	}
	
	private static Query createRefSetTypeQuery(final Iterable<SnomedRefSetType> types) {
		final BooleanQuery typeQuery = new BooleanQuery();
		for (final SnomedRefSetType type : types) {
			typeQuery.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE, IndexUtils.intToPrefixCoded(type.getValue()))), Occur.SHOULD);
		}
		return typeQuery;
	}
	
	private static Query createReferencedComponentTypeQuery(final String terminologyComponentId) {
		final int componentTypeValue = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsInt(terminologyComponentId);
		return new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_TYPE, IndexUtils.intToPrefixCoded(componentTypeValue)));
	}
	
	private static Query createSpecialFieldTypeQuery(final String terminologyComponentId, final String fieldName) {
		final int componentTypeValue = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsInt(terminologyComponentId);
		return new TermQuery(new Term(fieldName, IndexUtils.intToPrefixCoded(componentTypeValue)));
	}
	
	
	private static Query internalAddParsedClause(final BooleanQuery query, final String fieldName, final String searchString) {
		final List<String> tokens = internalTokenize(searchString);
		for (final String token : tokens) {
			query.add(new BooleanClause(new TermQuery(new Term(fieldName, token)), Occur.MUST));
		}
		return query;
	}

	private static List<String> internalTokenize(final String searchString) {
		final List<String> queryTokens = new ArrayList<String>();
		final StringTokenizer tokenizer = new StringTokenizer(searchString);
		while (tokenizer.hasMoreTokens()) {
			queryTokens.add(tokenizer.nextToken());
		}
		return queryTokens;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter#buildSearchResult(org.apache.lucene.document.Document, float)
	 */
	@Override
	public SnomedRefSetMemberIndexEntry buildSearchResult(final Document doc, final IBranchPath branchPath, final float score) {
		return new SnomedRefSetMemberIndexEntry(super.buildSearchResult(doc, branchPath, score));
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
					final BooleanQuery query = new BooleanQuery();
					query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_STORAGE_KEY, IndexUtils.longToPrefixCoded(storageKey))), Occur.MUST);
					return query;
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
					final BooleanQuery query = new BooleanQuery();
					final int componentTypeValue = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsInt(componentType);
					query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE, IndexUtils.intToPrefixCoded(SnomedRefSetType.CONCRETE_DATA_TYPE_VALUE))), Occur.MUST);
					query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_TYPE, IndexUtils.intToPrefixCoded(componentTypeValue))), Occur.MUST);
					query.add(SnomedIndexQueries.ACTIVE_COMPONENT_QUERY, Occur.MUST);
					final List<String> ids = Lists.newArrayList(referencedComponentIds);
					if (ids.size() > 1) {
						query.add(createComponentIdQuery(referencedComponentIds), Occur.MUST);
					} else {
						query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID, ids.get(0))), Occur.MUST);
					}
					return query;
				}
			};
		}
		

		@SuppressWarnings("unchecked")
		public static <T extends SnomedRefSetMemberIndexEntry> IIndexQueryAdapter<T> createFindByRefSetTypeQuery() {

			return (IIndexQueryAdapter<T>) new SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter() {
				private static final long serialVersionUID = -785494956030520757L;
				@Override public Query createQuery() {
					final BooleanQuery query = new BooleanQuery();
					query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE, IndexUtils.intToPrefixCoded(SnomedRefSetType.CONCRETE_DATA_TYPE_VALUE))), Occur.MUST);
					return query;
				}
			};
		}
		
		@SuppressWarnings("unchecked")
		public static <T extends SnomedRefSetMemberIndexEntry> IIndexQueryAdapter<T> createFindByRefSetTypeQuery(final String componentType) {
			checkNotNull(componentType, "Referenced component type argument cannot be null.");
			
			return (IIndexQueryAdapter<T>) new SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter() {
				private static final long serialVersionUID = -8318912010028083774L;
				@Override public Query createQuery() {
					final BooleanQuery query = new BooleanQuery();
					final int componentTypeValue = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsInt(componentType);
					query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE, IndexUtils.intToPrefixCoded(SnomedRefSetType.CONCRETE_DATA_TYPE_VALUE))), Occur.MUST);
					query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_TYPE, IndexUtils.intToPrefixCoded(componentTypeValue))), Occur.MUST);
					return query;
				}
			};
		}
		
		@SuppressWarnings("unchecked")
		public static <T extends SnomedRefSetMemberIndexEntry> IIndexQueryAdapter<T> createFindByDataType(final DataType type) {
			checkNotNull(type, "Data type argument cannot be null.");

			return (IIndexQueryAdapter<T>) new SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter() {
				private static final long serialVersionUID = -4361993210015507504L;
				@Override public Query createQuery() {
					final BooleanQuery query = new BooleanQuery();
					query.add(new TermQuery(new Term(REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(SnomedRefSetUtil.getRefSetId(type)))), Occur.MUST);
					query.add(new TermQuery(new Term(COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))), Occur.MUST);
					return query;
				}
			};
		}
		

		/*
		 * (non-Javadoc)
		 * @see com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter#buildSearchResult(org.apache.lucene.document.Document, float)
		 */
		@Override
		public SnomedConcreteDataTypeRefSetMemberIndexEntry buildSearchResult(final Document doc, final IBranchPath branchPath, final float score) {
			return com.b2international.snowowl.snomed.datastore.index.refset.SnomedConcreteDataTypeRefSetMemberIndexQueryAdapter.
					buildSearchResult(new SnomedConcreteDataTypeRefSetMemberIndexEntry(super.buildSearchResult(doc, branchPath, score)), doc);
		}
		
	}
	
}