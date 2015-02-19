/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.index.diff.tests.mock;

import groovy.transform.TupleConstructor;

import com.b2international.snowowl.core.api.index.IIndexEntry

/**
 */
@TupleConstructor
public class MockIndexEntry implements IIndexEntry {
	String label
	String id
	float score
	long storageKey
}
