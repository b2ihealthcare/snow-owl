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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.b2international.commons.pcj.LongSets.in;
import static com.b2international.commons.pcj.LongSets.LongPredicate.ALL_PREDICATE;
import static com.b2international.snowowl.core.api.index.CommonIndexConstants.COMPONENT_LABEL_SINGLE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.ceil;
import static org.apache.lucene.search.FieldCache.DEFAULT;
import static org.apache.lucene.util.packed.PackedInts.FAST;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.LongCollection;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;

import com.b2international.commons.pcj.LongSets.LongPredicate;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;

/**
 * Collector for gathering SNOMED&nbsp;CT component IDs and a human readable label for the component.
 * Could be concept preferred term or description term. This collector can be used
 * if the component label is stored as the {@link CommonIndexConstants#COMPONENT_LABEL_SINGLE} field
 * and the component ID is stored as a numeric doc value field called {@link CommonIndexConstants#COMPONENT_ID}.
 */
public class SnomedComponentLabelCollector extends AbstractDocsOutOfOrderCollector {

	private final LongPredicate componentIdsPredicate;
	private final LongKeyMap idLabelMapping;

	private BinaryDocValues terms;
	private NumericDocValues idDocValues;
	
	public SnomedComponentLabelCollector() {
		this(ALL_PREDICATE, new LongKeyOpenHashMap());
	}
	
	public SnomedComponentLabelCollector(final LongCollection componentIds) {
		this(
			in(componentIds), 
			new LongKeyOpenHashMap((int) ceil(componentIds.size() / 0.75))
			);
	}

	private SnomedComponentLabelCollector(final LongPredicate componentIdsPredicate, final LongKeyMap idLabelMapping) {
		this.componentIdsPredicate = componentIdsPredicate;
		this.idLabelMapping = idLabelMapping;
	}

	@Override
	public void collect(final int doc) throws IOException {
		if (null != idDocValues) {
			final long componentId = idDocValues.get(doc);
			if (componentIdsPredicate.apply(componentId)) {
				idLabelMapping.put(componentId, terms.get(doc).utf8ToString());
			}
		}
	}

	@Override
	public void setNextReader(final AtomicReaderContext context) throws IOException {
		checkNotNull(context, "Atomic reader context argument cannot be null.");
		terms = DEFAULT.getTerms(
				checkNotNull(context.reader(), "Index reader cannot be null for " + context), 
				COMPONENT_LABEL_SINGLE,
				false,
				FAST);
		
		idDocValues = context.reader().getNumericDocValues(COMPONENT_ID);
		
	}

	/**
	 * Returns with a map of component IDs and the component labels such
	 * as concept preferred terms and description terms.
	 * @return the map between component IDs and their human readable labels. 
	 */
	public LongKeyMap getIdLabelMapping() {
		return idLabelMapping;
	}
	
}