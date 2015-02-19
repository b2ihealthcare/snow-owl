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
package com.b2international.snowowl.datastore.utils;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Iterators;

/**
 * A special set that appears empty, but contains all {@link String}s when asked.
 * 
 */
public class UnrestrictedStringSet extends AbstractSet<String> implements Serializable {
	
	private static final long serialVersionUID = -9154673999804672532L;
	
	public static final Set<String> INSTANCE = new UnrestrictedStringSet();

	@Override
	public boolean contains(Object o) {
		return (o instanceof String);
	}
	
	@Override
	public Iterator<String> iterator() {
		return Iterators.emptyIterator();
	}

	@Override
	public int size() {
		return 0;
	}
}