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
package com.b2international.snowowl.snomed.datastore.index;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.RecursiveTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Value set hierarchy browser service.
 * This service implementation provides the hierarchy for the simple type SNOMED&nbsp;CT reference set members.
 *
 */
public class ValueSetBrowser implements IClientTerminologyBrowser<SnomedConceptIndexEntry, String> {

	public static final ValueSetBrowser create(final SnomedClientRefSetBrowser refSetBrowser) {
		return new ValueSetBrowser(Preconditions.checkNotNull(refSetBrowser, "SNOMED CT reference set browser argument cannot be null."));
	}
	
	private final SnomedClientRefSetBrowser delegate;

	private ValueSetBrowser(final SnomedClientRefSetBrowser delegate) {
		this.delegate = Preconditions.checkNotNull(delegate, "SNOMED CT reference set browser argument cannot be null.");
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getRootConcepts() {
		return Collections.singletonList(getConcept(Concepts.REFSET_SIMPLE_TYPE));
	}

	@Override
	public SnomedConceptIndexEntry getConcept(final String key) {
		return delegate.getConcept(key);
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypes(final SnomedConceptIndexEntry concept) {
		return delegate.getSuperTypes(concept);
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSubTypes(final SnomedConceptIndexEntry concept) {
		if (Concepts.REFSET_SIMPLE_TYPE.equals(concept.getId())) {
			final List<SnomedConceptIndexEntry> refSets = Lists.newArrayList(delegate.getSubTypes(concept));
			final SnomedConceptIndexEntry b2iExample = delegate.getConcept(Concepts.REFSET_B2I_EXAMPLE);
			if (null != b2iExample) {
				refSets.add(b2iExample);
				return ComponentUtils.sortByLabel(refSets);
			}
		} else if (Concepts.REFSET_B2I_EXAMPLE.equals(concept.getId())) {
			final List<SnomedConceptIndexEntry> refSets = Lists.newArrayList(delegate.getSubTypes(concept));
			final SnomedConceptIndexEntry cmtConcept = delegate.getConcept(Concepts.REFSET_KP_CONVERGENT_MEDICAL_TERMINOLOGY);
			if (null != cmtConcept) {
				refSets.add(cmtConcept);
				return ComponentUtils.sortByLabel(refSets);
			}
		}
		return delegate.getSubTypes(concept);
	}
	
	/**
	 * Returns with the member count. Root 'Simple type reference set', 'B2i examples' and 'KP Convergent Medical Terminology'
	 * does not count.
	 * @return the member count.
	 */
	public int getMemberCount() {
		final RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String> recursiveBrowser = new RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String>(this);
		final Collection<SnomedConceptIndexEntry> subTypes = recursiveBrowser.getSubTypes(getConcept(Concepts.REFSET_SIMPLE_TYPE));
		int size = subTypes.size();
		if (subTypes.contains(getConcept(Concepts.REFSET_B2I_EXAMPLE))) {
			size--;
		}
		if (subTypes.contains(getConcept(Concepts.REFSET_KP_CONVERGENT_MEDICAL_TERMINOLOGY))) {
			size--;
		}
		return size;
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypesById(String id) {
		return getSuperTypes(getConcept(id));
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSubTypesById(String id) {
		return getSubTypes(getConcept(id));
	}

	@Override
	public List<SnomedConceptIndexEntry> getSubTypesAsList(SnomedConceptIndexEntry concept) {
		return Lists.newArrayList(getSubTypes(concept));
	}
	
	@Override
	public Collection<String> getSuperTypeIds(String conceptId) {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	public Collection<SnomedConceptIndexEntry> getAllSubTypes(SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public Collection<SnomedConceptIndexEntry> getAllSuperTypes(SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public int getAllSubTypeCount(SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public int getSubTypeCount(SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public int getAllSuperTypeCount(SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public int getSuperTypeCount(SnomedConceptIndexEntry concept) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSuperTypesById(String id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubTypesById(String id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSubTypeCountById(String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSubTypeCountById(String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSuperTypeCountById(String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSuperTypeCountById(String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public SnomedConceptIndexEntry getTopLevelConcept(SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String componentId) {
		return null != getConcept(componentId);
	}

	@Override
	public boolean isTerminologyAvailable() {
		return true;
	}

	@Override
	public boolean isSuperTypeOf(SnomedConceptIndexEntry superType, SnomedConceptIndexEntry subType) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean isSuperTypeOfById(String superTypeId, String subTypeId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#filterTerminologyBrowser(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFilterClientTerminologyBrowser<SnomedConceptIndexEntry, String> filterTerminologyBrowser(String expression, IProgressMonitor monitor) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<IComponentWithChildFlag<String>> getSubTypesWithChildFlag(SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}
}
