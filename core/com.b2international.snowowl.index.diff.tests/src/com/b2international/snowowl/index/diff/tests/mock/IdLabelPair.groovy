/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.index.diff.tests.mock

import groovy.transform.Immutable;

/**
 * POJO wrapping and ID and a label
 *
 */
@Immutable
class IdLabelPair {
	String id
	String label
	@Override String toString() {
		"ID: $id | Label: $label"
	}
	
}
