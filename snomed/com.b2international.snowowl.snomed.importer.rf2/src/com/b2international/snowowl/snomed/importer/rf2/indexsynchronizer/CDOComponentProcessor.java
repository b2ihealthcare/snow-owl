/*******************************************************************************
 * Copyright (c) 2016 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.importer.rf2.indexsynchronizer;

import java.util.List;

import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

/**
 *
 */
public interface CDOComponentProcessor {

	/**
	 * @param cdoRevision
	 * @return
	 */
	SnomedConceptDocument createDocument(InternalCDORevision cdoRevision);

	/**
	 * @param conceptRevisions 
	 * 
	 */
	void createConcepts(List<InternalCDORevision> conceptRevisions);

}
