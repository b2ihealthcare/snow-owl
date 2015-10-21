/*******************************************************************************
 * Copyright (c) 2015 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.datastore.index;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.datastore.index.AbstractIndexQueryAdapter;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * Query adapter for getting active concept descriptions with a given type.
 * 
 * @author gnagy
 * @since SO 4.4
 */
public class SnomedDescriptionByTypeIndexQueryAdapter extends SnomedDescriptionIndexQueryAdapter {

	private static final long serialVersionUID = 8854964978374142899L;
	
	private final String conceptId;
	private final String typeId;

	protected SnomedDescriptionByTypeIndexQueryAdapter(final String conceptId, final String typeId) {
		super(null, AbstractIndexQueryAdapter.SEARCH_DEFAULT, null);
		this.conceptId = checkNotNull(conceptId, "conceptId");
		this.typeId = checkNotNull(typeId, "typeId");
	}

	@Override
	protected IndexQueryBuilder createIndexQueryBuilder() {
		return super.createIndexQueryBuilder()
				.require(SnomedMappings.newQuery().active().descriptionConcept(conceptId).descriptionType(typeId).matchAll());
	}
	
}
