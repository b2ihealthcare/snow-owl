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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_CHARACTERISTIC_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.LongCollection;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;

/**
 * Class for collecting SNOMED&nbsp;CT relationship properties.
 * This class supplies a mapping for relationships, where keys are relationship storage keys,
 * values are a primitive long array with the followings:
 * <ul>
 * <li>Relationship characteristic type concept ID</li>
 * <li>Relationship module concept ID</li>
 * <li>Relationship attribute/type concept ID</li>
 * <li>Relationship source concept ID</li>
 * <li>Relationship destination concept ID</li>
 * <li>Relationship modified concept ID</li>
 * </ul>
 *
 */
public class RelationshipPropertyCollector extends ComponentPropertyCollector {

	private static final long UNIVERSAL = Long.parseLong(Concepts.UNIVERSAL_RESTRICTION_MODIFIER);
	private static final long EXISTENTIAL = Long.parseLong(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);

	private NumericDocValues characteristicTypeIds;
	private NumericDocValues typdIds;
	private NumericDocValues sourceIds;
	private NumericDocValues destinationIds;
	private NumericDocValues modifiers;
	private NumericDocValues moduleIds;

	public RelationshipPropertyCollector(final LongCollection acceptedIds) {
		super(checkNotNull(acceptedIds, "acceptedIds"));
	}

	@Override
	protected void setNextReader(final AtomicReader reader) throws IOException {
		super.setNextReader(reader);
		characteristicTypeIds = reader.getNumericDocValues(RELATIONSHIP_CHARACTERISTIC_TYPE_ID);
		moduleIds = reader.getNumericDocValues(RELATIONSHIP_MODULE_ID);
		typdIds = reader.getNumericDocValues(RELATIONSHIP_ATTRIBUTE_ID);
		sourceIds = reader.getNumericDocValues(RELATIONSHIP_OBJECT_ID);
		destinationIds = reader.getNumericDocValues(RELATIONSHIP_VALUE_ID);
		modifiers = reader.getNumericDocValues(RELATIONSHIP_UNIVERSAL);
	}

	@Override
	protected boolean check() {
		return null != characteristicTypeIds 
			&& null != typdIds 
			&& null != sourceIds 
			&& null != destinationIds 
			&& null != modifiers 
			&& null != storageKeys
			&& null != moduleIds;
	}

	@Override
	protected long[] initProperties(final int doc) {
		return new long[] { 
			characteristicTypeIds.get(doc),
			moduleIds.get(doc),
			typdIds.get(doc), 
			sourceIds.get(doc), 
			destinationIds.get(doc), 
			getModifierId(doc) 
		};
	}

	private long getModifierId(final int doc) {
		return 1 == modifiers.get(doc) ? UNIVERSAL : EXISTENTIAL;
	}

}