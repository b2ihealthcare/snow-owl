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

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.LongCollection;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;

import com.b2international.commons.pcj.LongSets;
import com.b2international.commons.pcj.LongSets.LongPredicate;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * Collector for gathering SNOMED CT component IDs and a human readable label for the component.
 * <p>
 * The readable label can be a concept's stored preferred term or a description's own term.
 * <p>
 * This collector can be used if the component label is stored as the
 * {@link CommonIndexConstants#COMPONENT_LABEL_SINGLE} field and the component ID is stored as a numeric doc value field
 * called {@link CommonIndexConstants#COMPONENT_ID}.
 */
public class SnomedComponentLabelCollector extends AbstractDocsOutOfOrderCollector {

	private final LongPredicate componentIdsPredicate;
	private final LongKeyMap idLabelMapping;

	private BinaryDocValues labelDocValues;
	private NumericDocValues idDocValues;

	private static int getExpectedSize(final LongCollection componentIds) {
		return (int) Math.ceil(componentIds.size() / 0.75);
	}

	/**
	 * Creates a new instance which accepts all component identifiers, and uses the default hash map for collecting
	 * results.
	 */
	public SnomedComponentLabelCollector() {
		this(LongPredicate.ALL_PREDICATE, new LongKeyOpenHashMap());
	}

	/**
	 * Creates a new instance which accepts only the specified component identifiers, and sizes the resulting hash map
	 * accordingly.
	 * 
	 * @param componentIds the component identifiers to accept
	 */
	public SnomedComponentLabelCollector(final LongCollection componentIds) {
		this(LongSets.in(componentIds), new LongKeyOpenHashMap(getExpectedSize(componentIds)));
	}

	private SnomedComponentLabelCollector(final LongPredicate componentIdsPredicate, final LongKeyMap idLabelMapping) {
		this.componentIdsPredicate = componentIdsPredicate;
		this.idLabelMapping = idLabelMapping;
	}

	@Override
	public void collect(final int docId) throws IOException {
		final long componentId = idDocValues.get(docId);

		if (componentIdsPredicate.apply(componentId)) {
			final String label = labelDocValues.get(docId).utf8ToString();
			idLabelMapping.put(componentId, label);
		}
	}

	@Override
	protected void initDocValues(final AtomicReader reader) throws IOException {
		idDocValues = SnomedMappings.id().getDocValues(reader);
		labelDocValues = Mappings.label().getDocValues(reader);
	}

	@Override
	protected boolean isLeafCollectible() {
		return idDocValues != null && labelDocValues != null;
	}

	/**
	 * Returns with a map of component IDs and the component labels such as concept preferred terms and description
	 * terms.
	 * 
	 * @return the map between component IDs and their human readable labels
	 */
	public LongKeyMap getIdLabelMapping() {
		return idLabelMapping;
	}
}
