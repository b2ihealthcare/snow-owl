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
import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
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
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.map.LongKeyOpenHashMap;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.BranchPathUtils;
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
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Index service for improving performance for SNOMED CT import.
 */
public class ImportIndexServerService extends FSIndexServerService<IIndexEntry> {

    protected static final Logger IMPORT_LOGGER = LoggerFactory.getLogger(ImportIndexServerService.class);

    /** Enumerates available term types for descriptions. */
    public enum TermType {
        SYNONYM_AND_DESCENDANTS,
        FSN,
        OTHER;
    }

    /** Pairs description terms with high-level term types. */
    public static final class TermWithType {
        public String term;
        public TermType type;

        public TermWithType(final String term, final TermType type) {
            this.term = term;
            this.type = type;
        }
    }

    // Component storage key registration
    private static final String COMPONENT_ID = "componentId";
    private static final String REF_SET_ID = "refSetId";
    private static final String MEMBER_UUID = "memberUuid";
    private static final String CDO_ID = "cdoId";

    private static final Set<String> CDO_ID_ONLY = ImmutableSet.of(CDO_ID);

    // Description documents
    private static final String DESCRIPTION_ID = "descriptionId";
    private static final String CONCEPT_ID = "conceptId";
    private static final String TERM = "term";
    private static final String ACTIVE = "active";
    private static final String TERM_TYPE = "termType";
    private static final String PREFERRED_MEMBER_ID = "preferredMemberId";
    private static final String ACCEPTABLE_MEMBER_ID = "acceptableMemberId";

    private static final Set<String> DESCRIPTION_FIELDS = ImmutableSet.of(DESCRIPTION_ID, CONCEPT_ID, TERM, ACTIVE, TERM_TYPE, PREFERRED_MEMBER_ID, ACCEPTABLE_MEMBER_ID);
    private static final Set<String> TERM_ONLY = ImmutableSet.of(TERM);
    private static final Set<String> TERM_TYPE_AND_ACCEPTABILITY_ONLY = ImmutableSet.of(TERM, TERM_TYPE, PREFERRED_MEMBER_ID, ACCEPTABLE_MEMBER_ID);

    private static final Sort TERM_TYPE_SORT = new Sort(new SortField(TERM_TYPE, SortField.Type.INT));

    private static final String DIRECTORY_PATH = "sct_import";
    private static final IBranchPath SUPPORTING_INDEX_BRANCH_PATH = BranchPathUtils.createMainPath();

    private final IBranchPath importTargetBranchPath;
    private final LongKeyMap pendingDescriptionDocuments = new LongKeyOpenHashMap();

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
        final long storageKey = CDOIDUtil.getLong(cdoId);

        final Document doc = new Document();
        doc.add(businessIdField);
        doc.add(new LongField(CDO_ID, storageKey, IndexUtils.TYPE_PRECISE_LONG_STORED));

        index(SUPPORTING_INDEX_BRANCH_PATH, doc, new Term(CDO_ID, IndexUtils.longToPrefixCoded(storageKey)));
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

        ReferenceManager<IndexSearcher> manager = null;
        IndexSearcher searcher = null;

        try {

            manager = getManager(SUPPORTING_INDEX_BRANCH_PATH);
            searcher = manager.acquire();

            final TopDocs docs = searcher.search(idQuery, 1);

            if (null == docs || CompareUtils.isEmpty(docs.scoreDocs)) {
                return CDOUtils.NO_STORAGE_KEY;
            }

            final Document cdoIdDocument = searcher.doc(docs.scoreDocs[0].doc, CDO_ID_ONLY);
            final long storageKey = IndexUtils.getLongValue(cdoIdDocument.getField(CDO_ID));
            return storageKey;

        } catch (final IOException e) {

            LOGGER.error("Error while retrieving CDOID.");
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

    public void registerDescription(final String descriptionId, final String conceptId, final String term, final TermType type, final boolean active) {
        final Document doc = new Document();

        doc.add(new StringField(DESCRIPTION_ID, descriptionId, Store.YES));
        doc.add(new StringField(CONCEPT_ID, conceptId, Store.YES));
        doc.add(new StringField(TERM, term, Store.YES));
        doc.add(new StringField(ACTIVE, Boolean.toString(active), Store.YES));
        addTermType(doc, type);
        // XXX: no acceptability field is added at this time

        index(SUPPORTING_INDEX_BRANCH_PATH, doc, new Term(DESCRIPTION_ID, IndexUtils.longToPrefixCoded(descriptionId)));
    }

    public void registerAcceptability(final String descriptionId, final String memberId, final boolean preferred, final boolean active) {

        ReferenceManager<IndexSearcher> manager = null;
        IndexSearcher searcher = null;

        try {

            manager = getManager(SUPPORTING_INDEX_BRANCH_PATH);
            searcher = manager.acquire();

            final long longDescriptionId = Long.parseLong(descriptionId);
            Document pendingDescriptionDoc = (Document) pendingDescriptionDocuments.get(longDescriptionId);

            if (pendingDescriptionDoc == null) {
                final Query descriptionIdQuery = createDescriptionQuery(descriptionId);
                final TopDocs descriptionTopDocs = searcher.search(descriptionIdQuery, 1);

                if (null == descriptionTopDocs || CompareUtils.isEmpty(descriptionTopDocs.scoreDocs)) {
                    LOGGER.warn("Document for description '{}' does not exist. Skipping acceptability registration.", descriptionId);
                    return;
                }

                final int descriptionDocId = descriptionTopDocs.scoreDocs[0].doc;
                final Document descriptionDoc = searcher.doc(descriptionDocId, DESCRIPTION_FIELDS);

                pendingDescriptionDoc = new Document();
                pendingDescriptionDoc.add(new StringField(DESCRIPTION_ID, descriptionDoc.get(DESCRIPTION_ID), Store.YES));
                pendingDescriptionDoc.add(new StringField(CONCEPT_ID, descriptionDoc.get(CONCEPT_ID), Store.YES));
                pendingDescriptionDoc.add(new StringField(TERM, descriptionDoc.get(TERM), Store.YES));
                pendingDescriptionDoc.add(new StringField(ACTIVE, descriptionDoc.get(ACTIVE), Store.YES));
                addTermType(pendingDescriptionDoc, getTermType(descriptionDoc));

                for (final String preferredId : descriptionDoc.getValues(PREFERRED_MEMBER_ID)) {
                    pendingDescriptionDoc.add(new StringField(PREFERRED_MEMBER_ID, preferredId, Store.YES));
                }

                for (final String acceptableId : descriptionDoc.getValues(ACCEPTABLE_MEMBER_ID)) {
                    pendingDescriptionDoc.add(new StringField(ACCEPTABLE_MEMBER_ID, acceptableId, Store.YES));
                }

                pendingDescriptionDocuments.put(longDescriptionId, pendingDescriptionDoc);
            }

            final String fieldToAdd = preferred ? PREFERRED_MEMBER_ID : ACCEPTABLE_MEMBER_ID;
            boolean found = false;

            for (final Iterator<IndexableField> itr = pendingDescriptionDoc.getFields().iterator(); itr.hasNext(); /* emtpy */) {

                final IndexableField field = itr.next();
                final String fieldName = field.name();
                final String fieldValue = field.stringValue();
                
                // Skip fields not related to acceptability
				if (!fieldName.equals(PREFERRED_MEMBER_ID) && !fieldName.equals(ACCEPTABLE_MEMBER_ID)) {
					continue;
				}
				
				// Skip fields not containing information about the specified member
				if (!fieldValue.equals(memberId)) {
					continue;
				}

				// If the member is inactive, previously registered information should be removed
				if (!active) {
					itr.remove();
					continue;
				}
                
				/* 
				 * If the member is active, and it is recorded with the correct acceptability, set the flag, otherwise
				 * remove it from the opposite acceptability
				 */
				if (fieldName.equals(fieldToAdd)) {
                    found = true;
                } else {
                	itr.remove();
                }
            }

            // If the member was not found, but should be added, add it
            if (!found && active) {
                pendingDescriptionDoc.add(new StringField(fieldToAdd, memberId, Store.YES));
            }

        } catch (final IOException e) {

            LOGGER.error("Error while registering acceptability for description '{}'.", descriptionId);
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

    public String getConceptLabel(final String conceptId) {

        final BooleanQuery conceptLabelQuery = new BooleanQuery(true);
        conceptLabelQuery.add(createContainerConceptQuery(conceptId), Occur.MUST);
        conceptLabelQuery.add(createPreferredQuery(), Occur.MUST);

        // Try to find a preferred description first (PT or FSN, whichever comes first in the sorted set of documents)
        final List<DocumentWithScore> preferredDescriptionDocuments = search(SUPPORTING_INDEX_BRANCH_PATH, conceptLabelQuery, null, TERM_TYPE_SORT, 1);

        if (!CompareUtils.isEmpty(preferredDescriptionDocuments)) {
            final DocumentWithScore preferredDocument = Iterables.getFirst(preferredDescriptionDocuments, null);
            final Document unwrappedDocument = preferredDocument.getDocument();
            return unwrappedDocument.get(TERM);
        }

        // Get the FSN from CDO directly
        final String fsn = CDOUtils.apply(new CDOViewFunction<String, CDOView>(getConnection(), importTargetBranchPath) {
            @Override protected String apply(final CDOView view) {
                final Concept component = new SnomedConceptLookupService().getComponent(conceptId, view);
                return null == component ? String.valueOf(conceptId) : component.getFullySpecifiedName();
            }
        });

        if (!StringUtil.isEmpty(fsn)) {
            return fsn;
        }

        // Use the concept identifier as the label
        return conceptId;
    }

    public List<TermWithType> getConceptDescriptions(final String conceptId) {

        ReferenceManager<IndexSearcher> manager = null;
        IndexSearcher searcher = null;

        try {

            manager = getManager(SUPPORTING_INDEX_BRANCH_PATH);
            searcher = manager.acquire();

            final Query containerConceptQuery = createContainerConceptQuery(conceptId);
            final TotalHitCountCollector hitCountCollector = new TotalHitCountCollector();
            searcher.search(containerConceptQuery, hitCountCollector);

            // If the concept does not have any indexed descriptions, use the FSN
            final int totalHits = hitCountCollector.getTotalHits();
            
			if (totalHits < 1) {
                return Collections.singletonList(new TermWithType(getConceptLabel(conceptId), TermType.FSN));
            }

            final TopFieldDocs topDocs = searcher.search(containerConceptQuery, totalHits, TERM_TYPE_SORT);
            final List<TermWithType> conceptDescriptions = newArrayListWithCapacity(topDocs.scoreDocs.length);
            boolean labelPlacedFirst = false;
            
            for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
                final Document doc = searcher.doc(scoreDoc.doc, TERM_TYPE_AND_ACCEPTABILITY_ONLY);

                final String term = doc.get(TERM);
                final TermType termType = getTermType(doc);
                final boolean preferred = doc.getFields(PREFERRED_MEMBER_ID).length > 0;
                final TermWithType termWithType = new TermWithType(term, termType);
                
                // The first preferred SYN description we find should be the first in the returned list
                if (!labelPlacedFirst && preferred && TermType.SYNONYM_AND_DESCENDANTS.equals(termType)) {
                	conceptDescriptions.add(0, termWithType);
                	labelPlacedFirst = true;
                } else {
                	conceptDescriptions.add(termWithType);
                }
            }
            
            // Put an FSN to the front if no PT could be found
            if (!labelPlacedFirst) {
            	final int size = conceptDescriptions.size();
            	for (int i = 0; i < size; i++) {
					if (TermType.FSN.equals(conceptDescriptions.get(i).type)) {
						final TermWithType fsnTermWithType = conceptDescriptions.remove(i);
						conceptDescriptions.add(0, fsnTermWithType);
						labelPlacedFirst = true;
						break;
					}
				}
            }
            
            if (!labelPlacedFirst) {
            	LOGGER.warn("Neither PT nor FSN could be set as display label for concept '{}'.", conceptId);
            }

            return conceptDescriptions;

        } catch (final IOException e) {

            LOGGER.error("Error while searching for descriptions of concept '{}'.", conceptId);
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

    private TermQuery createContainerConceptQuery(final String conceptId) {
        return new TermQuery(new Term(CONCEPT_ID, conceptId));
    }

    private TermQuery createDescriptionQuery(final String descriptionId) {
        return new TermQuery(new Term(DESCRIPTION_ID, descriptionId));
    }

    private PrefixQuery createPreferredQuery() {
        return new PrefixQuery(new Term(PREFERRED_MEMBER_ID));
    }

    public String getDescriptionLabel(final String descriptionId) {

        ReferenceManager<IndexSearcher> manager = null;
        IndexSearcher searcher = null;

        try {

            manager = getManager(SUPPORTING_INDEX_BRANCH_PATH);
            searcher = manager.acquire();

            final Query descriptionQuery = createDescriptionQuery(descriptionId);
            final TopDocs topDocs = searcher.search(descriptionQuery, 1);

            if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {

                final String term = CDOUtils.apply(new CDOViewFunction<String, CDOView>(getConnection(), importTargetBranchPath) {
                    @Override protected String apply(final CDOView view) {
                        final Description component = new SnomedDescriptionLookupService().getComponent(descriptionId, view);
                        return null == component ? descriptionId : component.getTerm();
                    }
                });

                return StringUtil.isEmpty(term) ? descriptionId : term;
            }

            final Document descriptionDocument = searcher.doc(topDocs.scoreDocs[0].doc, TERM_ONLY);
            final String term = descriptionDocument.get(TERM);
            return term;

        } catch (final IOException e) {

            LOGGER.error("Error while searching for description '{}'.", descriptionId);
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

    private void addTermType(final Document doc, final TermType type) {
        final int typeOrdinal = type.ordinal();
        doc.add(new IntField(TERM_TYPE, typeOrdinal, TYPE_PRECISE_INT_STORED));
        doc.add(new NumericDocValuesField(TERM_TYPE, typeOrdinal));
    }

    private TermType getTermType(final Document doc) {
        final int typeOrdinal = IndexUtils.getIntValue(doc.getField(TERM_TYPE));
        return TermType.values()[typeOrdinal];
    }

    @Override
    public final void commit(final IBranchPath branchPath) {
        LOGGER.warn("Don't use #commit(IBranchPath) in Import time indexing, use the #commit() method instead.");
        commit();
    }

    @Override
    public final void rollback(final IBranchPath branchPath) {
        LOGGER.warn("Don't use #rollback(IBranchPath) in Import time indexing, use the #rollback() method instead.");
        rollback();
    }

    public final void commit() {
    	for (final LongKeyMapIterator itr = pendingDescriptionDocuments.entries(); itr.hasNext(); /* empty */) {
    		itr.next();
    		
    		final long descriptionId = itr.getKey();
    		final Document descriptionDoc = (Document) itr.getValue();
    		index(SUPPORTING_INDEX_BRANCH_PATH, descriptionDoc, new Term(DESCRIPTION_ID, String.valueOf(descriptionId)));
    	}
    	
    	pendingDescriptionDocuments.clear();
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
