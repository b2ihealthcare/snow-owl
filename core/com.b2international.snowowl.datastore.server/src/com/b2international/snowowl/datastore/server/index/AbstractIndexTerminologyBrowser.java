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
package com.b2international.snowowl.datastore.server.index;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.field.ComponentIdField;
import com.b2international.snowowl.datastore.index.field.ComponentIdStringField;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Abstract superclass for index based terminology browsers.
 * 
 */
abstract public class AbstractIndexTerminologyBrowser<E extends IIndexEntry> extends AbstractIndexBrowser<E> implements ITerminologyBrowser<E, String> {

	protected static final Set<String> COMPONENT_STORAGE_KEY_FIELD_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(CommonIndexConstants.COMPONENT_STORAGE_KEY));
	protected static final Set<String> COMPONENT_PARENT_FIELDS_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(CommonIndexConstants.COMPONENT_PARENT));
	private static final Set<String> COMPONENT_ID_FIELD_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(ComponentIdField.COMPONENT_ID));
	private static final Set<String> LABEL_FIELD_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(CommonIndexConstants.COMPONENT_LABEL));
	
	public AbstractIndexTerminologyBrowser(final IIndexService<?> service) {
		super(service);
	}

	@Override
	public long getStorageKey(final IBranchPath branchPath, final String conceptId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Concept ID argument cannot be null.");
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(getTerminologyComponentTypeQuery(), Occur.MUST);
		query.add(getComponentIdQuery(conceptId), Occur.MUST);

		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return -1L;
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, COMPONENT_STORAGE_KEY_FIELD_TO_LOAD);
		final IndexableField field = doc.getField(CommonIndexConstants.COMPONENT_STORAGE_KEY);
		
		if (null == field) {
			return -1L;
		}
		
		return IndexUtils.getLongValue(field);
	}
	
	@Override
	public Collection<E> getRootConcepts(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path must not be null.");
		
		final List<E> rootConcepts = Lists.newArrayList();
		// TODO: maybe this could become a cached filter, since the search criteria don't change
		final Query query = getRootConceptsQueryBuilder().toQuery();
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		DocIdsIterator iterator;
		try {
			iterator = collector.getDocIDs().iterator();
			while (iterator.next()) {
				final Document doc = service.document(branchPath, iterator.getDocID(), getFieldNamesToLoad());
				final E component = createResultObject(branchPath, doc);
				rootConcepts.add(component);
			}
		} catch (final IOException e) {
			throw new IndexException("Error when querying root concepts.", e);
		}
		return rootConcepts;
	}

	@Override
	public Collection<String> getRootConceptIds(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path must not be null.");
		
		final List<String> rootConceptIds = Lists.newArrayList();
		// TODO: maybe this could become a cached filter, since the search criteria don't change
		final Query query = getRootConceptsQueryBuilder().toQuery();
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);
		DocIdsIterator iterator;
		try {
			iterator = collector.getDocIDs().iterator();
			while (iterator.next()) {
				final Document doc = service.document(branchPath, iterator.getDocID(), COMPONENT_ID_FIELD_TO_LOAD);
				rootConceptIds.add(ComponentIdField.getString(doc));
			}
		} catch (final IOException e) {
			throw new IndexException("Error when querying root concepts.", e);
		}
		return rootConceptIds;
	}
	
	protected IndexQueryBuilder getRootConceptsQueryBuilder() {
		return new IndexQueryBuilder()
			.requireExactTerm(CommonIndexConstants.COMPONENT_PARENT, CommonIndexConstants.ROOT_ID)
			.require(getRootTerminologyComponentTypeQuery());
	}

	@Override
	public Collection<String> getSuperTypeIds(final IBranchPath branchPath, final String componentId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(componentId, "Component ID argument cannot be null.");
		
		final Query query = getConceptByIdQueryBuilder(componentId).toQuery();
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptyList();
		}
		
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, COMPONENT_PARENT_FIELDS_TO_LOAD);
		final IndexableField[] parentFields = document.getFields(CommonIndexConstants.COMPONENT_PARENT);
		final String[] parentIds = new String[parentFields.length];
		int i = 0;
		for (final IndexableField parentField : parentFields) {
			if (!CommonIndexConstants.ROOT_ID.equals(parentField.stringValue())) {
				parentIds[i++] = parentField.stringValue();
			}
		}
		return Arrays.asList(Arrays.copyOf(parentIds, i));
		
	}
	
	/**
	 * Returns with the human readable label of a terminology independent component identified by its unique ID
	 * from the given branch. This method may return with {@code null} if the component cannot be found on the 
	 * specified branch with the given component ID.
	 * @param branchPath the branch path uniquely identifying the branch where the lookup has to be performed.
	 * @param componentId the terminology specific unique ID of the component.
	 * @return the name/label of the component. Or {@code null} if the component cannot be found.
	 */
	@Override
	@Nullable 
	public String getComponentLabel(final IBranchPath branchPath, final String componentId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(componentId, "Component ID argument cannot be null.");
		
		final IndexQueryBuilder queryBuilder = getConceptByIdQueryBuilder(componentId);
		final Query query = queryBuilder.toQuery();
		
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		//cannot found matching label for component
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			
			return null;
			
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, LABEL_FIELD_TO_LOAD);
		
		final IndexableField field = doc.getField(CommonIndexConstants.COMPONENT_LABEL);
		
		if (null == field) {
			
			return null;
			
		}
		
		return field.stringValue();
		
	}
	
	@Override
	public E getConcept(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(conceptId, "conceptId");
		checkState(!conceptId.isEmpty(), "conceptId is empty.");
		
		final Query query = getConceptByIdQueryBuilder(conceptId).toQuery();
		return createSingleResultObject(branchPath, service.search(branchPath, query, 1));
	}

	protected IndexQueryBuilder getConceptByIdQueryBuilder(final String conceptId) {
		return new IndexQueryBuilder().require(new ComponentIdStringField(conceptId).toQuery());
	}

	@Override
	public Collection<E> getSuperTypesById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		
		final Query query = getConceptByIdQueryBuilder(id).toQuery();
		final TopDocs topDocs = service.search(branchPath, query, 1);
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptyList();
		}
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, COMPONENT_PARENT_FIELDS_TO_LOAD);
		final IndexableField[] parentFields = document.getFields(CommonIndexConstants.COMPONENT_PARENT);
		final Builder<E> builder = ImmutableList.builder();
		for (final IndexableField parentField : parentFields) {
			if (!CommonIndexConstants.ROOT_ID.equals(parentField.stringValue())) {
				builder.add(getConcept(branchPath, parentField.stringValue()));
			}
		}
		return builder.build();
	}

	@Override
	public Collection<E> getSubTypesById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		
		final Query query = getSubTypesQueryBuilder(id).toQuery();
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, query, collector);
			final DocIdsIterator docIdsIterator = collector.getDocIDs().iterator();
			return createResultObjects(branchPath, docIdsIterator);
		} catch (final IOException e) {
			throw new RuntimeException("Error when retrieving sub types of " + id + ".", e);
		}
	}

	protected Collection<String> getSubTypeIds(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		
		final Query query = getSubTypesQueryBuilder(id).toQuery();
		final Set<String> subTypeIds = Sets.newHashSet();
		
		try {
			
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, query, collector);
			final DocIdsIterator iterator = collector.getDocIDs().iterator();
			
			while (iterator.next()) {
				final Document doc = service.document(branchPath, iterator.getDocID(), COMPONENT_ID_FIELD_TO_LOAD);
				subTypeIds.add(ComponentIdField.getString(doc));
			}
			
			return subTypeIds;
			
		} catch (final IOException e) {
			throw new RuntimeException("Error when retrieving sub types of " + id + ".", e);
		}
	}
	
	protected IndexQueryBuilder getSubTypesQueryBuilder(final String id) {
		return new IndexQueryBuilder()
			.require(getTerminologyComponentTypeQuery())
			.requireExactTerm(CommonIndexConstants.COMPONENT_PARENT, id);
	}

	
	abstract protected short getConceptTerminologyComponentId();

	/**
	 * Returns with the query that should be performed when building the filtered terminology browser.
	 * @param expression the search expression.
	 * @return the query.
	 */
	protected abstract Query createFilterTerminologyBrowserQuery(final String expression);

	@Override
	public int getSubTypeCountById(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path must not be null.");
		checkNotNull(id, "ID must not be null.");
		
		final Query query = getSubTypesQueryBuilder(id).toQuery();
		try {
			return getQueryResultCount(branchPath, query);
		} catch (final IOException e) {
			throw new RuntimeException("Error when retrieving the number of sub types of " + id + ".", e);
		}
	}
	
	@Override
	public IFilterClientTerminologyBrowser<E, String> filterTerminologyBrowser(final IBranchPath branchPath, @Nullable final String expression, @Nullable final IProgressMonitor monitor) {
		final TerminologyBrowserFilter<E> terminologyBrowserFilter = new TerminologyBrowserFilter<E>(this, service);
		return terminologyBrowserFilter.filterTerminologyBrowser(branchPath, expression, monitor);
	}

	@Override
	public Collection<IComponentWithChildFlag<String>> getSubTypesWithChildFlag(final IBranchPath branchPath, final E concept) {
		// get direct subtypes
		final Collection<E> subTypes = getSubTypesById(branchPath, concept.getId());
		final List<IComponentWithChildFlag<String>> results = Lists.newArrayList();
		for (final E subTypeIndexEntry : subTypes) {
			results.add(createComponentWithChildFlag(subTypeIndexEntry, getSubTypeCountById(branchPath, subTypeIndexEntry.getId()) > 0));
		}
		return results;		
	}

	@Override
	public boolean exists(final IBranchPath branchPath, final String componentId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(componentId, "Component ID argument cannot be null.");
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(getComponentIdQuery(componentId), Occur.MUST);
		query.add(getTerminologyComponentTypeQuery(), Occur.MUST);

		return service.getHitCount(branchPath, query, null) > 0;
	}

	// Even when we accept multiple component types, we only want to display root concepts from a particular type -- see Icd10AmServerTerminologyBrowser
	protected Query getRootTerminologyComponentTypeQuery() {
		return getDefaultTerminologyComponentTypeQuery();
	}
	
	/**Returns with the query that will be run against the application specific terminology component ID of the component.*/
	protected Query getTerminologyComponentTypeQuery() {
		return getDefaultTerminologyComponentTypeQuery();
	}

	// The default implementation restricts the query to a single component type only
	private Query getDefaultTerminologyComponentTypeQuery() {
		return new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(getConceptTerminologyComponentId())));
	}
	
	/**
	 * Template method for creating an {@link IComponentWithChildFlag}.
	 * 
	 * @param entry the original index entry
	 * @param hasChildren the child flag
	 */
	abstract protected IComponentWithChildFlag<String> createComponentWithChildFlag(E entry, boolean hasChildren);
}