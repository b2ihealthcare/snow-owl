/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.index.diff.tests.dsl

import org.apache.lucene.document.Document
import org.apache.lucene.document.NumericDocValuesField
import org.apache.lucene.document.StringField
import org.apache.lucene.index.Term
import org.apache.lucene.search.PrefixQuery

import com.b2international.snowowl.core.api.IBranchPath
import com.b2international.snowowl.datastore.BranchPathUtils
import com.b2international.snowowl.index.diff.tests.mock.IdLabelPair;
import com.b2international.snowowl.index.diff.tests.mock.MockIndexServerService;

/**
 * DSL for mock index server service.
 *
 */
class MockIndexServiceDsl {

	static init() {}
	
	static {
		
		MockIndexServerService.metaClass.getBranchPath << { ->
			BranchPathUtils.createMainPath()
		} << { String tagName ->
			BranchPathUtils.createPath(delegate.branchPath, tagName)
		}
		
		MockIndexServerService.metaClass.getAllDocsQuery {
			new PrefixQuery(new Term(delegate.idField))
		}
		
		MockIndexServerService.metaClass.getAllDocsCount << { IBranchPath branchPath ->
			delegate.getHitCount(branchPath, delegate.allDocsQuery, null)
		} << { ->
			delegate.getAllDocsCount delegate.branchPath
		}
		
		MockIndexServerService.metaClass.getAllDocs << { branchPath ->
			def hitCount = delegate.allDocsCount
			hitCount > 0 ? delegate.search(branchPath, delegate.allDocsQuery, hitCount).scoreDocs.collect {
				document(branchPath, it.doc, null)
			} : []
		} << { ->
			delegate.getAllDocs delegate.branchPath
		}
		
		MockIndexServerService.metaClass.indexDocsAsIrrelevant << { String[] ids ->
			delegate.indexDocsAsIrrelevant(delegate.branchPath, ids.collect {
				new IdLabelPair(it, "${it}_default_label")
			})
		} << { Collection<String> ids ->
			delegate.indexDocsAsIrrelevant(delegate.branchPath, ids.collect {
				new IdLabelPair(it, "${it}_default_label")
			})
		} << { String id ->
			delegate.indexDocsAsIrrelevant(delegate.branchPath, [id] as String[])
		} << { IdLabelPair idLabelPair ->
			delegate.indexDocsAsIrrelevant(delegate.branchPath, idLabelPair as IdLabelPair[])
		} << { IdLabelPair[] idLabelPairs ->
			delegate.indexDocsAsIrrelevant(delegate.branchPath, idLabelPairs as List)
		} << { Collection<IdLabelPair> idLabelPairs ->
			idLabelPairs.each {
				delegate.indexDocsAsIrrelevant(delegate.branchPath, it)
			}
		} << { IBranchPath branchPath, String[] ids ->
			delegate.indexDocsAsIrrelevant(branchPath, ids.collect {
				new IdLabelPair(it, "${it}_default_label")
			})
		} << { branchPath, Collection<String> ids ->
			delegate.indexDocsAsIrrelevant(branchPath, ids.collect {
				new IdLabelPair(it, "${it}_default_label")
			})
		} << { IBranchPath branchPath, String id ->
			delegate.indexDocsAsIrrelevant(branchPath, id as String[])
		} << { IBranchPath branchPath, IdLabelPair idLabelPair ->
			delegate.indexDocsAsIrrelevant(branchPath, idLabelPair as List)
		} << { IBranchPath branchPath, IdLabelPair[] idLabelPairs ->
			delegate.indexDocsAsIrrelevant(branchPath, idLabelPairs as List)
		} << { IBranchPath branchPath, Collection<IdLabelPair> idLabelPairs ->
			idLabelPairs.each {
				delegate.index(branchPath, delegate.createDoc(false, it), delegate.createUniqueTerm(it.id))
			}
			delegate.commit(branchPath)
		}
		
		MockIndexServerService.metaClass.indexDocs << { String[] ids ->
			delegate.indexDocs(delegate.branchPath, ids.collect {
				new IdLabelPair(it, "${it}_default_label")
			})
		} << { Collection<String> ids ->
			delegate.indexDocs(delegate.branchPath, ids.collect {
				new IdLabelPair(it, "${it}_default_label")
			})
		} << { String id ->
			delegate.indexDocs(delegate.branchPath, id as String[])
		} << { IdLabelPair idLabelPair ->
			delegate.indexDocs(delegate.branchPath, idLabelPair as IdLabelPair[])
		} << { IdLabelPair[] idLabelPairs ->
			delegate.indexDocs(delegate.branchPath, idLabelPairs as List)
		} << { Collection<IdLabelPair> idLabelPairs ->
			idLabelPairs.each {
				delegate.indexDocs(delegate.branchPath, it)
			}
		} << { IBranchPath branchPath, String[] ids ->
			delegate.indexDocs(branchPath, ids.collect {
				new IdLabelPair(it, "${it}_default_label")
			})
		} << { branchPath, Collection<String> ids ->
			delegate.indexDocs(branchPath, ids.collect {
				new IdLabelPair(it, "${it}_default_label")
			})
		} << { IBranchPath branchPath, String id ->
			delegate.indexDocs(branchPath, id as String[])
		} << { IBranchPath branchPath, IdLabelPair idLabelPair ->
			delegate.indexDocs(branchPath, idLabelPair as List)
		} << { IBranchPath branchPath, IdLabelPair[] idLabelPairs ->
			delegate.indexDocs(branchPath, idLabelPairs as List)
		} << { IBranchPath branchPath, Collection<IdLabelPair> idLabelPairs ->
			idLabelPairs.each {
				delegate.index(branchPath, delegate.createDoc(it), delegate.createUniqueTerm(it.id))
			}
			delegate.commit(branchPath)
		}
		
		MockIndexServerService.metaClass.deleteDocs << { branchPath, String[] ids ->
			delegate.deleteDocs(branchPath, ids as List)
		} << { branchPath, ids ->
			ids.each {
				delegate.delete(branchPath, delegate.createUniqueTerm(it))
			}
			delegate.commit(branchPath)
		} << { branchPath, String id ->
			delegate.deleteDocs(branchPath, id as List)
		} << { String[] ids ->
			delegate.deleteDocs(delegate.branchPath, ids as List)
		} << { ids ->
			delegate.deleteDocs(delegate.branchPath, ids)
		} << { String id ->
			delegate.deleteDocs(delegate.branchPath, id as List)
		}
		
		MockIndexServerService.metaClass.createDoc << { boolean relevantForCompare, IdLabelPair idLabelPair ->
			def doc = new Document()
			doc.add(delegate.createIdField(idLabelPair.id))
			doc.add(delegate.createLabelField(idLabelPair.label))
			doc.add(new NumericDocValuesField(delegate.compareField, relevantForCompare ? (idLabelPair.id).toLong() : -1L))
			if (!relevantForCompare) {
				doc.add(new NumericDocValuesField(delegate.ignoreCompareField, (idLabelPair.id).toLong()))
			}
			return doc
		} << { IdLabelPair idLabelPair ->
			delegate.createDoc(true, idLabelPair)
		}
		
		MockIndexServerService.metaClass.tag << { String tagName ->
			def tagBranch = delegate.getBranchPath tagName
			delegate.snapshotFor(tagBranch, true, false)
			delegate.allDocsCount(tagName)
		} << { IBranchPath branchPath ->
			delegate.snapshotFor(branchPath, true, false)
			delegate.getAllDocsCount(branchPath)
		}
		
		MockIndexServerService.metaClass.import << { Map dataMap ->
			delegate.import(delegate.branchPath, dataMap)
		} << { IBranchPath branchPath, Map dataMap ->
			dataMap.each { id, label ->
				delegate.indexDocs(branchPath, new IdLabelPair(id, label))
			}
		}
		
		MockIndexServerService.metaClass.getIdField {
			"id"
		}
		
		MockIndexServerService.metaClass.getLabelField {
			"label"
		}
		
		MockIndexServerService.metaClass.getCompareField {
			"component_compare_unique_key"
		}
		
		MockIndexServerService.metaClass.getIgnoreCompareField {
			"component_ignore_compare_unique_key"
		}
		
		MockIndexServerService.metaClass.getAllDocsString {
			delegate.allDocs.collect {
				new IdLabelPair(it.get(delegate.idField), it.get(delegate.labelField))
			}.groupBy( { idAndLabel -> idAndLabel.id } )
		}
		
		MockIndexServerService.metaClass.getAllDocsIds {
			delegate.allDocs*.get(delegate.idField)
		}
		
		MockIndexServerService.metaClass.createIdField = { value ->
			delegate.createField(delegate.idField, value)
		}
		
		MockIndexServerService.metaClass.createLabelField = { value ->
			delegate.createField(delegate.labelField, value)
		}
		
		MockIndexServerService.metaClass.createField = { name, value ->
			new StringField(name, value, org.apache.lucene.document.Field.Store.YES)
		}
		
		MockIndexServerService.metaClass.createUniqueTerm = { value ->
			new Term(delegate.idField, value)
		}
		
	}
		
	
}
