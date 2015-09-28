/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.index.diff.tests

import static com.b2international.commons.pcj.LongSets.*
import static com.b2international.snowowl.datastore.server.index.IndexBranchService.getIndexCommit

import com.b2international.snowowl.core.api.IBranchPath
import com.b2international.snowowl.index.diff.*
import com.b2international.snowowl.index.diff.tests.dsl.DslInjector
import com.b2international.snowowl.index.diff.tests.mock.IdLabelPair
import com.b2international.snowowl.index.diff.tests.mock.MockIndexServerService

def printerln = System.err.&println
new DslInjector().injectDslInto this

def assertPcjCollections = { leftLongSet, right ->
	def left = toSet(leftLongSet)
	0 == ((left - right) + (right - left)).size()
}

def calculateDiff = { service, sourceBranchPath, targetBranchPath -> 
	
	def sourceService = service.getBranchService sourceBranchPath
	def targetService = service.getBranchService targetBranchPath
	
	def sourceCommit = getIndexCommit(sourceService.directory, sourceBranchPath)
	def targetCommit = getIndexCommit(targetService.directory, targetBranchPath)
	
	IndexDifferFactory.INSTANCE.createDiffer().calculateDiff(sourceCommit, targetCommit)
	
}

def calculateThreeWayDiff = { service, sourceBranchPath, targetBranchPath ->
	
	def sourceService = service.getBranchService sourceBranchPath
	def targetService = service.getBranchService targetBranchPath
	
	def ancestorCommit = getIndexCommit(sourceService.directory, sourceBranchPath)
	def sourceCommit = sourceService.headIndexCommit
	def targetCommit = targetService.headIndexCommit
	
	IndexDifferFactory.INSTANCE.createDiffer().calculateDiff(ancestorCommit, sourceCommit, targetCommit)
	
}

def calculateDiffAgainstHead = { service, sourceBranchPath, targetBranchPath ->
	
	def sourceService = service.getBranchService sourceBranchPath
	def targetService = service.getBranchService targetBranchPath
	
	def sourceCommit = getIndexCommit(sourceService.directory, sourceBranchPath)
	def targetCommit = targetService.headIndexCommit
	
	IndexDifferFactory.INSTANCE.createDiffer().calculateDiff(sourceCommit, targetCommit)
	
}

scenario "Check Lucene API.", {
	
	given "An index service.", {
		service = new MockIndexServerService()
	}
	
	then "Force keep fully deleted segment property should be reachable and true on MAIN.", {
		service.getBranchService(service.branchPath).indexWriter.keepFullyDeletedSegments.shouldBe true
	} 
	
}


scenario "DSL API check.", {
	
	given "Initialize service.", {
		service = new MockIndexServerService()
	}
	
	when "Indexing single document as String.", {
		service.indexDocs '1'
	}
	
	when "Indexing documents as String variable arguments.", {
		service.indexDocs '2', '3', '4'
	}
	
	when "Indexing documents as String array.", {
		service.indexDocs (['5', '6', '7'] as String [])
	}
	
	when "Indexing documents as String list.", {
		service.indexDocs (['8', '9'])
	}
	
	when "Indexing documents as a single ID and label pair instance.", {
		service.indexDocs new IdLabelPair('10', 'j_label')
	}
	
	when "Indexing documents as a ID and label array.", {
		service.indexDocs ([new IdLabelPair('11', 'k_label'), new IdLabelPair('12', 'l_label')]) //as IdLabelPair [])
	}
	
	when "Indexing documents as a ID and label list.", {
		service.indexDocs ([new IdLabelPair('13', 'm_label'), new IdLabelPair('14', 'n_label')])
	}
	
	given "Expected IDs to be indexed.", {
		expectedResults = { '1'..'14' as List}
	}
	
	then "Each document exists.", {
		expectedResults().each {
			service.allDocsIds.shouldHave it
		}
	}
	
}

scenario "Index service smoke test.", {
	
	given "Given and empty index service.", {
		service = new MockIndexServerService() 
	}
	
	when "Index two document.", {
		service.indexDocs '1', '2'
	}
	
	then "Check existence of the two documents.", {
		service.allDocsCount.shouldBe 2
	}
	
	when "Document '1' is removed from the index.", {
		service.deleteDocs '1'
	}
	
	then "Index has only one document.", {
		service.allDocsCount.shouldBe 1
	}
	
	and "This only one document is the '2'.", {
		service.allDocs.size.shouldBe 1	
		service.allDocsIds.first().shouldBe '2'
	}
	
	when "Creating new document with '2' unique index should not modify index content.", {
		service.indexDocs new IdLabelPair('2', 'B_new_label')
	}
	
	then "Only '2' should be in the index and only once.", {
		service.allDocs.size.shouldBe 1
		service.allDocsIds.first().shouldBe '2'
	}
	
	when "Index new document '3'.", {
		service.indexDocs '3'
	}
	
	then "Index should contain two documents.", {
		service.allDocsCount.shouldBe 2
	}
	
	and "Both '2' and '3' should be in the index.", {
		service.allDocsIds.shouldHave '2'
		service.allDocsIds.shouldHave '3'
	}
	
	when "Clear all documents are cleared from index.", {
		service.deleteDocs service.allDocsIds
	}
	
	then "Index should be empty.", {
		service.allDocsCount.shouldBe 0
		service.allDocsIds.shouldNotHave '1'
		service.allDocsIds.shouldNotHave '2'
		service.allDocsIds.shouldNotHave '3'
	}
	
}

scenario "Index import test.", {
	
	given "Given an empty index service.", {
		service = new MockIndexServerService()
	}
	
	when "Checking document count.", {
		docCount = {
			service.allDocsCount
		}
	}
	
	then "Index contains zero documents.", {
		docCount().shouldBe 0
	}
	
	given "Create version branches; TAG_1, TAG_2 and TAG_3.", {
		tag1 = { service.getBranchPath("TAG_1") }
		tag2 = { service.getBranchPath("TAG_2") }
		tag3 = { service.getBranchPath("TAG_3") }
	}
	
	then "Check version branch instances." , {
		assert tag1() instanceof IBranchPath
		assert tag2() instanceof IBranchPath
		assert tag3() instanceof IBranchPath
	}
	
	given "Given an import data.", {
		importData = { list -> 
			list.inject([:]) {map, item -> map << [(item.toUpperCase()) : "${item}_label"]}
		}
	}

	then "Create version TAG_1 on empty index content.", {
		service.tag tag1()
	}
		
	when "After importing data into index.", {
		service.import importData('1'..'4' as List)
	}

	then "Create version TAG_2 onto initialized index content.", {
		service.tag tag2()
	}

	when "After importing new data into index.", {
		service.import importData('3'..'6' as List)
	}
	
	then "Create version TAG_3 onto initialized index content.", {
		service.tag tag3()
	}
		
	then "All document should be available in the index.", {
		service.allDocsCount.shouldBe importData('1'..'6' as List).size()
	}
	
	then "All unique ID should be indexed on MAIN as well.", {
		importData('1'..'6' as List).each { id, label ->
			service.allDocs.shouldHave id
		}
	}
	
	then "Index content must be empty on TAG_1 branch.", {
		importData('1'..'4' as List).each { id, label ->
			service.getAllDocs(tag1()).shouldNotHave id
		}
		service.getAllDocsCount(tag1()).shouldBe 0
	}
	
	then "Index content must have all documents on TAG_2 branch.", {
		importData('1'..'4' as List).each { id, label ->
			service.getAllDocs(tag2()).shouldHave id
		}
		service.getAllDocsCount(tag2()).shouldBe importData('1'..'4' as List).size() 
	}
	
	then "Index content must have all documents on TAG_3 branch.", {
		importData('1'..'6' as List).each { id, label ->
			service.getAllDocs(tag3()).shouldHave id
		}
		service.getAllDocsCount(tag3()).shouldBe importData('a'..'f' as List).size()
	}
	
}

/*
  			Consider 6 individual index commits per versions and a HEAD modification: v1, v2, v3, v4, v5, v6 and MAIN 
			Compare v1 with v4
			v1 initially contains 10, 1, 2
			v2 add 3, 4, 6, 7 and modify 1, 2. index 10 as an irrelevant change
			v3 delete 6, 7 and modify 4 as an irrelevant change in respect of compare
			v4 add 5, delete 2 and modify 1
			v5 modified 3 and change 1 as an irrelevant one.
			v6 modified 3 and changed 10 as an irrelevant change
			MAIN add 8, delete 1, 5 and modify 10
			
			index difference should be the followings:
			 - v1 and v2: new: 3, 4, 6 and 7. modified 1 and 2.
			 - v1 and v3: new 3 and 4. modified 1 and 2.
			 - v1 and v4: new 3, 4 and 5. modified 1. detached 2.
			 - v1 and v5: new 3, 4 and 5. modified 1. detached 2.
			 - v1 and v6: new 3, 4 and 5. modified 1. detached 2.
			 - v1 and HEAD: new 3, 4, 8. modified 10 and detached 1, 2

			 - v2 and v3: detached 6 and 7.
			 - v2 and v4: new 5. modified 1. detached 2, 6 and 7.
			 - v2 and v5: new 5. modified 1, 3. detached 2, 6 and 7.
			 - v2 and v6: new 5. modified 1, 3. detached 2, 6 and 7.
			 - v2 and HEAD: new 8. modified 3, 10 and detached 1, 2, 6, 7

			 - v3 and v4: new 5, modified 1 and detached 2.
			 - v3 and v5: new 5, modified 1, 3. detached 2.
			 - v3 and v6: new 5, modified 1, 3. detached 2.
			 - v3 and HEAD: new 8, modified 3 and 10. detached 1 and 2.

			 - v4 and v5: modified 3.
			 - v4 and v6: modified 3.
			 - v4 and HEAD: new 8. modified 3 and 10. detached 1 and 5.

			 - v5 and v6: modified 3.
			 - v5 and HEAD: new 8. modified 10, 3. detached 1 and 5.

			 - v6 and HEAD: new 8, modified 10. detached 1 and 5. 
*/
scenario "Index import test with compare test cases.", {
	
	given "Index import test with compare test case.", {
		service = new MockIndexServerService()
	}
	
	given "Import data for all four phases.", {
		phase1ImportData = {
			[
				'10' : '_',
				'1' : 'a',
				'2' : 'b',
				]
		}
		
		phase2ImportData = {
			[
				'3' : 'c',
				'4' : 'd',
				'6' : 'x',
				'7' : 'y',
				'1' : 'f',
				'2' : 'h'
				]
		}
		
		phase3DeletionData = {
			[
				'6',
				'7'
				]
		}

		phase4DeletionData = {
			[
				'2'
				]
		}
		
		phase4ImportData = {
			[
				'5' : 'e',
				'1' : 'g'
				]
		}
		
		phase5ImportData = {
			[
				'3' : 'j'
				]
		}
		
		phase6ImportData = {
			[
				'3' : 'k'
				]
		}
		
		phaseHeadImportData = {
			[
				'8' : 'L',
				'10' : 'z'
				]
		}
		
		phaseHeadDeletionData = {
			[
				'1',
				'5'
				]
		} 
		
		phase_1 = { service.getBranchPath("v1") }
		phase_2 = { service.getBranchPath("v2") }
		phase_3 = { service.getBranchPath("v3") }
		phase_4 = { service.getBranchPath("v4") }
		phase_5 = { service.getBranchPath("v5") }
		phase_6 = { service.getBranchPath("v6") }
		
	}
	
	when "Processing four phases and creating version tags after each commits.", {
		service.import phase1ImportData()
		service.tag phase_1()
		
		service.import phase2ImportData()
		service.indexDocsAsIrrelevant '10'
		service.tag phase_2()
		
		service.deleteDocs phase3DeletionData()
		service.indexDocsAsIrrelevant '4'
		service.tag phase_3()
		
		service.deleteDocs phase4DeletionData()
		service.import phase4ImportData()
		service.tag phase_4()
				
		service.import phase5ImportData()
		service.indexDocsAsIrrelevant '1'
		service.indexDocsAsIrrelevant '5'
		service.tag phase_5()
		
		service.import phase6ImportData()
		service.indexDocsAsIrrelevant '10'
		service.tag phase_6()
		
		service.import phaseHeadImportData()
		service.deleteDocs phaseHeadDeletionData()
		
	}
	
	then "Print all documents. Index has to have 4 documents. [10, 3, 4, 8].", {
		service.allDocsCount.shouldBe 4
		[10, 3, 4, 8].toListString().each { id ->
			service.allDocs.shouldHave id
		}
		
	}
	
	then "Calculate index difference: v1 and v2: new: 3, 4, 6 and 7. modified 1 and 2.", {
		def diff = calculateDiff(service, phase_1(), phase_2())
		assert diff.getNewIds().size() == 4
		assert diff.getChangedIds().size() == 2
		assert diff.getDetachedIds().size() == 0
		assert assertPcjCollections(diff.getNewIds(), [3, 4, 6, 7])
		assert assertPcjCollections(diff.getChangedIds(), [1, 2])
		assert assertPcjCollections(diff.getDetachedIds(), [])
	}
	
	then "Calculate index difference: v1 and v3: new 3 and 4. modified 1 and 2.", {
		def diff = calculateDiff(service, phase_1(), phase_3())
		assert diff.getNewIds().size() == 2
		assert diff.getChangedIds().size() == 2
		assert diff.getDetachedIds().size() == 0
		assert assertPcjCollections(diff.getNewIds(), [3, 4])
		assert assertPcjCollections(diff.getChangedIds(), [1, 2])
		assert assertPcjCollections(diff.getDetachedIds(), [])
	}
	
	then "Calculate index difference: v1 and v4: new 3, 4 and 5. modified 1. detached 2.", {
		def diff = calculateDiff(service, phase_1(), phase_4())
		assert diff.getNewIds().size() == 3
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 1
		assert assertPcjCollections(diff.getNewIds(), [3, 4, 5])
		assert assertPcjCollections(diff.getChangedIds(), [1])
		assert assertPcjCollections(diff.getDetachedIds(), [2])
	}
	
	then "Calculate index difference: v1 and v5: new 3, 4 and 5. modified 1. detached 2.", {
		def diff = calculateDiff(service, phase_1(), phase_5())
		assert diff.getNewIds().size() == 3
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 1
		assert assertPcjCollections(diff.getNewIds(), [3, 4, 5])
		assert assertPcjCollections(diff.getChangedIds(), [1])
		assert assertPcjCollections(diff.getDetachedIds(), [2])
	}
	
	then "Calculate index difference: v1 and v6: new 3, 4 and 5. modified 1. detached 2.", {
		def diff = calculateDiff(service, phase_1(), phase_6())
		assert diff.getNewIds().size() == 3
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 1
		assert assertPcjCollections(diff.getNewIds(), [3, 4, 5])
		assert assertPcjCollections(diff.getChangedIds(), [1])
		assert assertPcjCollections(diff.getDetachedIds(), [2])
	}
	
	then "Calculate index difference: v1 and HEAD: new 3, 4, 8. modified 10 and detached 1, 2", {
		def diff = calculateDiffAgainstHead(service, phase_1(), service.branchPath)
		assert diff.getNewIds().size() == 3
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 2
		assert assertPcjCollections(diff.getNewIds(), [3, 4, 8])
		assert assertPcjCollections(diff.getChangedIds(), [10])
		assert assertPcjCollections(diff.getDetachedIds(), [1, 2])
	}
	
	then "Calculate index difference: v2 and v3: detached 6 and 7.", {
		def diff = calculateDiff(service, phase_2(), phase_3())
		assert diff.getNewIds().size() == 0
		assert diff.getChangedIds().size() == 0
		assert diff.getDetachedIds().size() == 2
		assert assertPcjCollections(diff.getNewIds(), [])
		assert assertPcjCollections(diff.getChangedIds(), [])
		assert assertPcjCollections(diff.getDetachedIds(), [6, 7])
	}
	
	then "Calculate index difference: v2 and v4: new 5. modified 1. detached 2, 6 and 7.", {
		def diff = calculateDiff(service, phase_2(), phase_4())
		assert diff.getNewIds().size() == 1
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 3
		assert assertPcjCollections(diff.getNewIds(), [5])
		assert assertPcjCollections(diff.getChangedIds(), [1])
		assert assertPcjCollections(diff.getDetachedIds(), [2, 6, 7])
	}
	
	then "Calculate index difference: v2 and v5: new 5. modified 1, 3. detached 2, 6 and 7.", {
		def diff = calculateDiff(service, phase_2(), phase_5())
		assert diff.getNewIds().size() == 1
		assert diff.getChangedIds().size() == 2
		assert diff.getDetachedIds().size() == 3
		assert assertPcjCollections(diff.getNewIds(), [5])
		assert assertPcjCollections(diff.getChangedIds(), [1, 3])
		assert assertPcjCollections(diff.getDetachedIds(), [2, 6, 7])
	}
	
	then "Calculate index difference: v2 and v6: new 5. modified 1, 3. detached 2, 6 and 7.", {
		def diff = calculateDiff(service, phase_2(), phase_6())
		assert diff.getNewIds().size() == 1
		assert diff.getChangedIds().size() == 2
		assert diff.getDetachedIds().size() == 3
		assert assertPcjCollections(diff.getNewIds(), [5])
		assert assertPcjCollections(diff.getChangedIds(), [1, 3])
		assert assertPcjCollections(diff.getDetachedIds(), [2, 6, 7])
	}
	
	then "Calculate index difference: v2 and HEAD: new 8. modified 3, 10 and detached 1, 2, 6, 7", {
		def diff = calculateDiffAgainstHead(service, phase_2(), service.branchPath)
		assert diff.getNewIds().size() == 1
		assert diff.getChangedIds().size() == 2
		assert diff.getDetachedIds().size() == 4
		assert assertPcjCollections(diff.getNewIds(), [8])
		assert assertPcjCollections(diff.getChangedIds(), [3, 10])
		assert assertPcjCollections(diff.getDetachedIds(), [1, 2, 6, 7])
	}
	
	then "Calculate index difference: v3 and v4: new 5, modified 1 and detached 2.", {
		def diff = calculateDiff(service, phase_3(), phase_4())
		assert diff.getNewIds().size() == 1
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 1
		assert assertPcjCollections(diff.getNewIds(), [5])
		assert assertPcjCollections(diff.getChangedIds(), [1])
		assert assertPcjCollections(diff.getDetachedIds(), [2])
	}
	
	then "Calculate index difference: v3 and v5: new 5, modified 1, 3. detached 2.", {
		def diff = calculateDiff(service, phase_3(), phase_5())
		assert diff.getNewIds().size() == 1
		assert diff.getChangedIds().size() == 2
		assert diff.getDetachedIds().size() == 1
		assert assertPcjCollections(diff.getNewIds(), [5])
		assert assertPcjCollections(diff.getChangedIds(), [1, 3])
		assert assertPcjCollections(diff.getDetachedIds(), [2])
	}
	
	then "Calculate index difference: v3 and v6: new 5, modified 1, 3. detached 2.", {
		def diff = calculateDiff(service, phase_3(), phase_6())
		assert diff.getNewIds().size() == 1
		assert diff.getChangedIds().size() == 2
		assert diff.getDetachedIds().size() == 1
		assert assertPcjCollections(diff.getNewIds(), [5])
		assert assertPcjCollections(diff.getChangedIds(), [1, 3])
		assert assertPcjCollections(diff.getDetachedIds(), [2])
	}

		then "Calculate index difference: v3 and HEAD: new 8, modified 3 and 10. detached 1 and 2.", {
		def diff = calculateDiffAgainstHead(service, phase_3(), service.branchPath)
		assert diff.getNewIds().size() == 1
		assert diff.getChangedIds().size() == 2
		assert diff.getDetachedIds().size() == 2
		assert assertPcjCollections(diff.getNewIds(), [8])
		assert assertPcjCollections(diff.getChangedIds(), [3, 10])
		assert assertPcjCollections(diff.getDetachedIds(), [1, 2])
	}
	
	then "Calculate index difference: v4 and v5: modified 3.", {
		def diff = calculateDiff(service, phase_4(), phase_5())
		assert diff.getNewIds().size() == 0
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 0
		assert assertPcjCollections(diff.getNewIds(), [])
		assert assertPcjCollections(diff.getChangedIds(), [3])
		assert assertPcjCollections(diff.getDetachedIds(), [])
	}
	
	then "Calculate index difference: v4 and v6: modified 3.", {
		def diff = calculateDiff(service, phase_4(), phase_6())
		assert diff.getNewIds().size() == 0
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 0
		assert assertPcjCollections(diff.getNewIds(), [])
		assert assertPcjCollections(diff.getChangedIds(), [3])
		assert assertPcjCollections(diff.getDetachedIds(), [])
	}
	
	then "Calculate index difference: v4 and HEAD: new 8. modified 3 and 10. detached 1 and 5.", {
		def diff = calculateDiffAgainstHead(service, phase_4(), service.branchPath)
		assert diff.getNewIds().size() == 1
		assert diff.getChangedIds().size() == 2
		assert diff.getDetachedIds().size() == 2
		assert assertPcjCollections(diff.getNewIds(), [8])
		assert assertPcjCollections(diff.getChangedIds(), [3, 10])
		assert assertPcjCollections(diff.getDetachedIds(), [1, 5])
	}
	
	then "Calculate index difference: v5 and v6: modified 3.", {
		def diff = calculateDiff(service, phase_5(), phase_6())
		assert diff.getNewIds().size() == 0
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 0
		assert assertPcjCollections(diff.getNewIds(), [])
		assert assertPcjCollections(diff.getChangedIds(), [3])
		assert assertPcjCollections(diff.getDetachedIds(), [])
	}
	
	then "Calculate index difference: v5 and HEAD: new 8. modified 10, 3. detached 1 and 5", {
		def diff = calculateDiffAgainstHead(service, phase_5(), service.branchPath)
		assert diff.getNewIds().size() == 1
		assert diff.getChangedIds().size() == 2
		assert diff.getDetachedIds().size() == 2
		assert assertPcjCollections(diff.getNewIds(), [8])
		assert assertPcjCollections(diff.getChangedIds(), [3, 10])
		assert assertPcjCollections(diff.getDetachedIds(), [1, 5])
	}
	
	then "Calculate index difference: v6 and HEAD: new 8. modified 10. detached 1 and 5", {
		def diff = calculateDiffAgainstHead(service, phase_6(), service.branchPath)
		assert diff.getNewIds().size() == 1
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 2
		assert assertPcjCollections(diff.getNewIds(), [8])
		assert assertPcjCollections(diff.getChangedIds(), [10])
		assert assertPcjCollections(diff.getDetachedIds(), [1, 5])
	}
	

}

scenario "Index import test with compare test cases. Test non-optimized tagging.", {
	
	given "Index import test with compare test case.", {
		service = new MockIndexServerService()
	}
	
	given "Import data for all four phases.", {
		phase1ImportData = {
			[
				'1' : 'a',
				'2' : 'b',
				]
		}
		
		phase2ImportData = {
			[
				'2' : 'c',
				]
		}
		
		
		phase_1 = { service.getBranchPath("v1") }
		phase_2 = { service.getBranchPath("v2") }
		
	}
	
	when "Processing two phases and creating version tags after each commits.", {
		service.import phase1ImportData()
		service.tag phase_1()

		service.import phase2ImportData()
		service.indexDocsAsIrrelevant '1'
		service.indexDocsAsIrrelevant '2'
		service.tag phase_2()
		
	}
	
	then "Print all documents. Index has to have 2 documents. [1, 2].", {
		service.allDocsCount.shouldBe 2
		[1, 2].toListString().each { id ->
			service.allDocs.shouldHave id
		}
		
	}
	
	then "Calculate index difference: v1 and v2: modified 2.", {
		def diff = calculateDiff(service, phase_1(), phase_2())
		assert diff.getNewIds().size() == 0
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 0
		assert assertPcjCollections(diff.getNewIds(), [])
		assert assertPcjCollections(diff.getChangedIds(), [2])
		assert assertPcjCollections(diff.getDetachedIds(), [])
	}

}

scenario "Patch version compare.", {

	given "Index import test with compare test case.", {
		service = new MockIndexServerService()
	}
	
	given "Import data for creating initial content and patching both version.", {
		
		initialImportData = {
			[
				'1' : 'a',
				'2' : 'b',
				'3' : 'c',
				'6' : 'f'
				]
		}
		
		phaseV1PatchImportData = {
			[
				'1' : 'A',
				'3' : 'C',
				'4' : 'D'
				]
		}
		
		phaseV1PatchDeletionData = {
			[
				'2',
				'6'
				]
		}
		
		
		phaseV2PatchImportData = {
			[
				'1' : 'AA',
				'2' : 'B',
				'5' : 'E'
				]
		}
		
		phaseV2PatchDeletionData = {
			[
				'3',
				'6'
				]
		}
		
		
		
		phase_1 = { service.getBranchPath("v1") }
		phase_2 = { service.getBranchPath("v2") }
		
	}
	
	when "Processing import initial content and create two individual versions.", {
		service.import initialImportData()
		service.tag phase_1()
		service.tag phase_2()
		
		service.import(phase_1(), phaseV1PatchImportData())
		service.deleteDocs(phase_1(), phaseV1PatchDeletionData())
		
		service.import(phase_2(), phaseV2PatchImportData())
		service.deleteDocs(phase_2(), phaseV2PatchDeletionData())
		
	}
	
	then "Print all documents on the HEAD of MAIN. Index has to have 4 documents. [1, 2, 3, 6].", {
		service.allDocsCount.shouldBe 4
		[1, 2, 3, 6].toListString().each { id ->
			service.allDocs.shouldHave id
		}
		
	}
	
	then "Print all documents on the HEAD of V1. Index has to have 3 documents. [1, 3, 4].", {
		service.getAllDocsCount(phase_1()).shouldBe 3
		[1, 3, 4].toListString().each { id ->
			service.getAllDocs(phase_1()).shouldHave id
		}
		
	}
	
	then "Print all documents on the HEAD of V2. Index has to have 3 documents. [1, 2, 5].", {
		service.getAllDocsCount(phase_2()).shouldBe 3
		[1, 2, 5].toListString().each { id ->
			service.getAllDocs(phase_2()).shouldHave id
		}
		
	}
	
	then "Calculate index difference: v1 patch and v2 patch: new 2, 5. changed: 1 and detached 3, 4", {
		def diff = calculateThreeWayDiff(service, phase_1(), phase_2())
		assert diff.getNewIds().size() == 2
		assert diff.getChangedIds().size() == 1
		assert diff.getDetachedIds().size() == 2
		assert assertPcjCollections(diff.getNewIds(), [2, 5])
		assert assertPcjCollections(diff.getChangedIds(), [1])
		assert assertPcjCollections(diff.getDetachedIds(), [3, 4])
	}

}

