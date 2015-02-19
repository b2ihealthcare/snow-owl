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
package com.b2international.snowowl.datastore.server.snomed.index.init;

import static com.b2international.snowowl.datastore.index.IndexUtils.TYPE_PRECISE_INT_STORED;
import static com.b2international.snowowl.datastore.index.IndexUtils.TYPE_PRECISE_LONG_NOT_STORED;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.StringUtil;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.CDOViewFunction;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.index.DocumentWithScore;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.index.FSIndexServerService;
import com.b2international.snowowl.datastore.server.index.IIndexPostProcessor;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Index service for improving performance for SNOMED&nbsp;CT import.
 */
public class ImportIndexServerService extends FSIndexServerService<IIndexEntry> {

	protected static final org.slf4j.Logger IMPORT_LOGGER = org.slf4j.LoggerFactory.getLogger(ImportIndexServerService.class);
	
	/**Enumeration supporting natural ordering for different term types.*/
	public static enum TermType {
		
		PT,
		FSN,
		SYNONYM_AND_DESCENDANTS,
		OTHER;
		
	}
	
	public static interface IDescriptionTypePredicate {
		boolean isFsn();
		boolean isSynonymOrDescendant();
	}

	public static class DescriptionTypePredicateAdapter implements IDescriptionTypePredicate {
		@Override public boolean isFsn() { return false; }
		@Override public boolean isSynonymOrDescendant() { return false; }
	}
	
	public static final class TermWithType {
		public String term;
		public int termTypeOrdinal;
		public TermWithType(final String term, final int termTypeOrdinal) {
			this.term = term;
			this.termTypeOrdinal = termTypeOrdinal;
		}
	}

	public static final String COMPONENT_ID = "componentId";
	public static final String REF_SET_ID = "refSetId";
	public static final String MEMBER_UUID = "memberUuid";
	public static final String CDO_ID = "cdoId";
	public static final String DESCRIPTION_ID = "descriptionId";
	public static final String TERM = "term";
	public static final String ACCEPTABILITY_ID = "acceptabilityId";
	public static final String CONTAINER_CONCEPT_ID = "containerConceptId";
	public static final String CONCEPT_ID = "conceptId";
	public static final String TERM_TYPE = "termType";
	public static final String ACTIVE = "active";

	private static final String DIRECTORY_PATH = "sct_import";
	private static final IBranchPath SUPPORTING_INDEX_BRANCH_PATH = BranchPathUtils.createMainPath();
	
	private final IBranchPath importTargetBranchPath;
	
	/**
	 * A set containing the storage keys of the Synonym description type concept and its all descendant.
	 * @param synonymAndDescendantCdoIds
	 * @param fsnCdoId unique storage key of the FSN description type concept
	 */
	public ImportIndexServerService(final IBranchPath importTargetBranchPath) {
		super(getDirectoryFolder());
		this.importTargetBranchPath = importTargetBranchPath;
		getManager(SUPPORTING_INDEX_BRANCH_PATH); //triggers directory creation
	}

	/*returns with the file pointing to the index directory.
	 *File#deleteOnExit is set on file.*/
	private static final File getDirectoryFolder() {
		
		final StringBuilder sb = new StringBuilder();
		sb.append(DIRECTORY_PATH);
		sb.append("_");
		sb.append(Dates.formatByHostTimeZone(new Date(), DateFormats.FULL));
		sb.append("_");
		sb.append(UUID.randomUUID().toString());
		final File file = new File(sb.toString());
		file.deleteOnExit();
		return file;
		
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.index.IndexServerService#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		getDirectoryManager().cleanUp(SUPPORTING_INDEX_BRANCH_PATH, true);
	}
	
	public void registerComponent(final String componentId, final CDOID cdoId) {
		registerItem(new StringField(COMPONENT_ID, componentId, Store.YES), cdoId);
	}
	
	public void registerRefSet(final String identifierConceptId, final CDOID cdoId) {
		registerItem(new StringField(REF_SET_ID, identifierConceptId, Store.YES), cdoId);
	}
	
	public void registerMember(final String memberUuid, final CDOID cdoId) {
		registerItem(new StringField(MEMBER_UUID, memberUuid, Store.YES), cdoId);
	}
	
	private void registerItem(final Field businessIdField, final CDOID cdoId) {
		
		final Document doc = new Document();
		doc.add(businessIdField);
		final long _cdoId = CDOIDUtils.asLong(cdoId);
		doc.add(new LongField(CDO_ID, _cdoId, Store.YES));
		
		index(SUPPORTING_INDEX_BRANCH_PATH, doc, new Term(CDO_ID, IndexUtils.longToPrefixCoded(_cdoId)));
	}

	public long getMemberCdoId(final String uuid) {
		final long memberStorageKey = ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class).getMemberStorageKey(importTargetBranchPath, uuid);
		return CDOUtils.NO_STORAGE_KEY == memberStorageKey ? getItemCdoId(new TermQuery(new Term(MEMBER_UUID, uuid))) : memberStorageKey;
	}
	
	public long getRefSetCdoId(final String identifierConceptId) {
		final long storageKey = ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class).getStorageKey(importTargetBranchPath, identifierConceptId);
		return CDOUtils.NO_STORAGE_KEY == storageKey ? getItemCdoId(new TermQuery(new Term(REF_SET_ID, identifierConceptId))) : storageKey; 
	}
	
	public long getComponentCdoId(final String componentId) {
		final long storageKey = getComponentCdoIdIfExists(componentId);
		
		if (CDOUtils.NO_STORAGE_KEY == storageKey) {
			throw new IllegalStateException("No storage key found for component " + componentId + " neither in supporting index nor in the target branch index.");
		}
		
		return storageKey;
	}
	
	public boolean componentExists(final String componentId) {
		return CDOUtils.NO_STORAGE_KEY != getComponentCdoIdIfExists(componentId);
	}

	private long getComponentCdoIdIfExists(final String componentId) {
		long storageKey = CDOUtils.NO_STORAGE_KEY;
		
		final short terminologyComponentIdValue = SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(componentId);
		switch (terminologyComponentIdValue) {
			
			case SnomedTerminologyComponentConstants.CONCEPT_NUMBER:
				storageKey = ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class).getStorageKey(importTargetBranchPath, componentId);
				break;
				
			case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
				storageKey = ApplicationContext.getInstance().getService(ISnomedComponentService.class).getDescriptionStorageKey(importTargetBranchPath, componentId);
				break;
				
			case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
				storageKey = ApplicationContext.getInstance().getService(SnomedStatementBrowser.class).getStorageKey(importTargetBranchPath, componentId);
				break;
			
			default:
				
				throw new IllegalArgumentException("Unknown SNOMED CT component type: " + terminologyComponentIdValue);
		}

		if (CDOUtils.NO_STORAGE_KEY == storageKey) {
			storageKey = getItemCdoId(new TermQuery(new Term(COMPONENT_ID, componentId)));
		}
		
		return storageKey;
	}

	private long getItemCdoId(final Query idQuery) {
		final TopDocs docs = search(SUPPORTING_INDEX_BRANCH_PATH, idQuery, 1);
		if (null == docs || CompareUtils.isEmpty(docs.scoreDocs)) {
			return CDOUtils.NO_STORAGE_KEY;
		}
		return IndexUtils.getLongValue(document(SUPPORTING_INDEX_BRANCH_PATH, docs.scoreDocs[0].doc, CDO_ID_FIELD_TO_LOAD).getField(CDO_ID));
	}
	
	public void registerDescription(final String descriptionId, final String conceptId, final String term, final IDescriptionTypePredicate predicate, final boolean active) {
		
		final Document doc = new Document();
		
		doc.add(new LongField(DESCRIPTION_ID, Long.parseLong(descriptionId), TYPE_PRECISE_LONG_NOT_STORED)); //description ID
		doc.add(new StringField(CONTAINER_CONCEPT_ID, conceptId, Store.YES)); //concept ID
		doc.add(new StringField(TERM, term, Store.YES)); //term
		doc.add(new StringField(ACTIVE, Boolean.toString(active), Store.YES)); //status
		
		if (predicate.isFsn()) {
		
			doc.add(new IntField(TERM_TYPE, TermType.FSN.ordinal(), TYPE_PRECISE_INT_STORED)); //FSN term type
			
		} else {
			
			if (predicate.isSynonymOrDescendant()) {

				doc.add(new IntField(TERM_TYPE, TermType.SYNONYM_AND_DESCENDANTS.ordinal(), TYPE_PRECISE_INT_STORED)); //synonym and descendant
				
			} else {
				
				doc.add(new IntField(TERM_TYPE, TermType.OTHER.ordinal(), TYPE_PRECISE_INT_STORED)); //other description types
				
			}
			
		}
		
		index(SUPPORTING_INDEX_BRANCH_PATH, doc, new Term(DESCRIPTION_ID, IndexUtils.longToPrefixCoded(descriptionId)));
		
	}
	
	private static final Sort TERM_TYPE_SORT = new Sort(new SortField(TERM_TYPE, SortField.Type.INT));
	
	public String getConceptLabel(final String conceptId) {
		return getConceptLabel(Long.parseLong(conceptId));
	}
	
	public String getRelationshipLabel(final String relationshipId) {
		
		return CDOUtils.apply(new CDOViewFunction<String, CDOView>(getConnection(), importTargetBranchPath) {
			@Override protected String apply(final CDOView view) {
				final Relationship relationship = new SnomedRelationshipLookupService().getComponent(relationshipId, view);
				final String sourceId = relationship.getSource().getId();
				final String typeId = relationship.getType().getId();
				final String destinationId = relationship.getDestination().getId();
				return new StringBuilder(sourceId).append(" - ").append(typeId).append(" - ").append(destinationId).toString();
			}
		});
		
	}
	
	public String getConceptLabel(final long conceptId) {
		
		final Long _conceptId = conceptId;
		final Query idQuery = NumericRangeQuery.newLongRange(CONCEPT_ID, Integer.MAX_VALUE, _conceptId, _conceptId, true, true);
		
		final List<DocumentWithScore> $ = search(SUPPORTING_INDEX_BRANCH_PATH, idQuery, null, TERM_TYPE_SORT, 1);
		
		
		if (CompareUtils.isEmpty($)) {
			
			final String fsn = CDOUtils.apply(new CDOViewFunction<String, CDOView>(getConnection(), importTargetBranchPath) {
				@Override protected String apply(final CDOView view) {
					
					final List<DocumentWithScore> descriptionDocs = search(SUPPORTING_INDEX_BRANCH_PATH, new TermQuery(new Term(CONTAINER_CONCEPT_ID, String.valueOf(conceptId))), null, TERM_TYPE_SORT, 1);
					if (!CompareUtils.isEmpty(descriptionDocs)) {
						return Iterables.get(descriptionDocs, 0).getDocument().get(TERM);
					}
					
					final Concept component = new SnomedConceptLookupService().getComponent(Long.toString(conceptId), view);
					return null == component ? String.valueOf(conceptId) : component.getFullySpecifiedName();
				}
			});
			
			return StringUtil.isEmpty(fsn) ? Long.toString(conceptId) : fsn;
			
		}
		
		return Iterables.getFirst($, null).getDocument().get(TERM);
		
	}
	
	public List<TermWithType> getConceptDescriptions(final long conceptId) {
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			manager = getManager(SUPPORTING_INDEX_BRANCH_PATH);
			searcher = manager.acquire();
			
			final Long _conceptId = conceptId;
			final Query idQuery = NumericRangeQuery.newLongRange(CONCEPT_ID, Integer.MAX_VALUE, _conceptId, _conceptId, true, true);
			
			final TotalHitCountCollector hitCountCollector = new TotalHitCountCollector();
			searcher.search(idQuery, hitCountCollector);
			
			final TopFieldDocs topDocs = searcher.search(idQuery, hitCountCollector.getTotalHits() > 0 ? hitCountCollector.getTotalHits() : 1, TERM_TYPE_SORT);
			
			if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
				//if concept does not have any preferred language member, fall back to FSN
				return Collections.singletonList(new TermWithType(getConceptLabel(conceptId), TermType.OTHER.ordinal()));
				
			}
			
			final TermWithType [] $ = new TermWithType[topDocs.scoreDocs.length];
			
			int i = 0;
			for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
				
				final Document doc = searcher.doc(scoreDoc.doc, TERM_AND_TYPE_TO_LOAD);
				
				$[i++] = new TermWithType(doc.get(TERM), IndexUtils.getIntValue(doc.getField(TERM_TYPE)));
				
			}
			
			return Arrays.asList($);
			
		} catch (final IOException e) {
			
			LOGGER.error("Error while searching for concept descriptions.");
			throw new SnowowlRuntimeException(e);
			
		} finally {
			
			if (null != manager && null != searcher) {
				
				try {
					
					manager.release(searcher);
					
				} catch (final IOException e) {
					
					LOGGER.error("Error while releasing index searcher.");
					throw new SnowowlRuntimeException(e);
					
				}
				
			}
			
		}
		
	}
	
	public String getDescriptionLabel(final String componentId) {
		
		final Long _componentId = Long.parseLong(componentId);
		final Query idQuery = NumericRangeQuery.newLongRange(DESCRIPTION_ID, Integer.MAX_VALUE, _componentId, _componentId, true, true);
		
		final TopDocs topDocs = search(SUPPORTING_INDEX_BRANCH_PATH, idQuery, 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			
			final String term = CDOUtils.apply(new CDOViewFunction<String, CDOView>(getConnection(), importTargetBranchPath) {
				@Override protected String apply(final CDOView view) {
					final Description component = new SnomedDescriptionLookupService().getComponent(componentId, view);
					return null == component ? componentId : component.getTerm();
				}
			});
			
			return StringUtil.isEmpty(term) ? componentId : term;
			
		}
		
		return document(SUPPORTING_INDEX_BRANCH_PATH, topDocs.scoreDocs[0].doc, TERM_TO_LOAD).get(TERM);
		
	}
	
	private static final Set<String> TERM_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(TERM));
	private static final Set<String> CDO_ID_FIELD_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(CDO_ID));
	private static final Set<String> TERM_AND_TYPE_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(TERM, TERM_TYPE));
	private static final Set<String> DESCRIPTION_FRAGMENT_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(CONTAINER_CONCEPT_ID, TERM, TERM_TYPE, ACTIVE));
	
	public void registerConcept(final String descriptionId, final String acceptabilityId, final boolean active) {
		
		final Long _descriptionId = Long.parseLong(descriptionId);
		final Query descriptionIdQuery = NumericRangeQuery.newLongRange(DESCRIPTION_ID, Integer.MAX_VALUE, _descriptionId, _descriptionId, true, true);

		final TopDocs descriptionTopDocs = search(SUPPORTING_INDEX_BRANCH_PATH, descriptionIdQuery, 1);
		
		if (null == descriptionTopDocs || CompareUtils.isEmpty(descriptionTopDocs.scoreDocs)) {
			
			final String message = "Container concept does not exist for description. Skipping concept label registration. Description ID: '" + descriptionId + "'.";
			LOGGER.warn(message);
			return;
			
		}
		
		final int descriptionDocId = descriptionTopDocs.scoreDocs[0].doc;
		final Document descriptionDoc = document(SUPPORTING_INDEX_BRANCH_PATH, descriptionDocId, DESCRIPTION_FRAGMENT_TO_LOAD);
		final long conceptId = Long.parseLong(descriptionDoc.get(CONTAINER_CONCEPT_ID));
		final boolean descriptionActive = Boolean.parseBoolean(descriptionDoc.get(ACTIVE));
		
		if (!descriptionActive) {
			
			final BooleanQuery conceptByDescriptionIdQuery = new BooleanQuery(true);
			conceptByDescriptionIdQuery.add(NumericRangeQuery.newLongRange(CONCEPT_ID, Integer.MAX_VALUE, conceptId, conceptId, true, true), Occur.MUST);
			conceptByDescriptionIdQuery.add(new TermQuery(new Term(DESCRIPTION_ID, descriptionId)), Occur.MUST);
			try {
				getBranchService(SUPPORTING_INDEX_BRANCH_PATH).deleteDocuments(conceptByDescriptionIdQuery);
			} catch (final IOException e) {
				throw new SnowowlRuntimeException(e);
			}
			return;
			
		}
		
		final String term = descriptionDoc.get(TERM);
		final int termTypeOrdinal = IndexUtils.getIntValue(descriptionDoc.getField(TERM_TYPE));
		
		final Document conceptDoc = new Document();
		conceptDoc.add(new LongField(CONCEPT_ID, conceptId, TYPE_PRECISE_LONG_NOT_STORED)); //concept ID
		conceptDoc.add(new StringField(TERM, term, Store.YES)); //PT (or FSN or fall back description)
		conceptDoc.add(new StringField(ACCEPTABILITY_ID, acceptabilityId, Store.NO));
		conceptDoc.add(new StringField(DESCRIPTION_ID, descriptionId, Store.NO));
		
		
		//synonym and descendants with preferred acceptability should end up as a PT 
		if (TermType.SYNONYM_AND_DESCENDANTS.ordinal() == termTypeOrdinal && Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(acceptabilityId)) {
		
			//check whether already has a PT for the concept
			final BooleanQuery memberQuery = new BooleanQuery(true);
			memberQuery.add(NumericRangeQuery.newLongRange(CONCEPT_ID, Integer.MAX_VALUE, conceptId, conceptId, true, true), Occur.MUST);
			memberQuery.add(new TermQuery(new Term(TERM_TYPE, IndexUtils.intToPrefixCoded(TermType.PT.ordinal()))), Occur.MUST);
			memberQuery.add(new TermQuery(new Term(ACCEPTABILITY_ID, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)), Occur.MUST);

			try {
				getBranchService(SUPPORTING_INDEX_BRANCH_PATH).deleteDocuments(memberQuery);
			} catch (final IOException e) {
				throw new SnowowlRuntimeException(e);
			}
			
			conceptDoc.add(new IntField(TERM_TYPE, TermType.PT.ordinal(), TYPE_PRECISE_INT_STORED)); //PT term type ordinal
			addDocument(SUPPORTING_INDEX_BRANCH_PATH, conceptDoc);
			
		} else {
			
			conceptDoc.add(new IntField(TERM_TYPE, termTypeOrdinal, TYPE_PRECISE_INT_STORED)); //term type ordinal
			addDocument(SUPPORTING_INDEX_BRANCH_PATH, conceptDoc);
			
		}
		
	}

	@Override
	public final void commit(IBranchPath branchPath) {
		LOGGER.warn("Don't use #commit(IBranchPath) in Import time indexing, use the #commit() method instead.");
		commit();
	}
	
	@Override
	public final void rollback(IBranchPath branchPath) {
		LOGGER.warn("Don't use #rollback(IBranchPath) in Import time indexing, use the #rollback() method instead.");
		rollback();
	}
	
	public final void commit() {
		super.commit(SUPPORTING_INDEX_BRANCH_PATH);
	}
	
	public final void rollback() {
		super.rollback(SUPPORTING_INDEX_BRANCH_PATH);
	}
	
	/*returns with the CDO connection*/
	private ICDOConnection getConnection() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexUpdater#getRepositoryUuid()
	 */
	@Override
	public String getRepositoryUuid() {
		return ImportIndexServerService.class.getName(); //intentionally a fake one
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.index.IndexServerService#getIndexPostProcessor()
	 */
	@Override
	protected IIndexPostProcessor getIndexPostProcessor() {
		return IIndexPostProcessor.NOOP;
	}
}
