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
package com.b2international.snowowl.snomed.datastore.factory;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.commons.StringUtils;
import com.b2international.commons.TypeSafeAdapterFactory;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedClientRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedIconProvider;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;

public class SnomedRefSetAdapterFactory extends TypeSafeAdapterFactory {

	@Override
	public <T> T getAdapterSafe(Object adaptableObject, Class<T> adapterType) {

		if (IComponent.class != adapterType) {
			return null;
		}
		
		if (adaptableObject instanceof SnomedRefSetIndexEntry || adaptableObject instanceof SnomedRefSetIndexEntry) {
			return adapterType.cast(adaptableObject);
		} 
		
		if (adaptableObject instanceof SnomedRefSet) {
			
			final SnomedRefSet refSet = (SnomedRefSet) adaptableObject;
			final SnomedClientRefSetBrowser refSetBrowser = ApplicationContext.getInstance().getService(SnomedClientRefSetBrowser.class);
			final SnomedRefSetIndexEntry refSetMini = refSetBrowser.getRefSet(refSet.getIdentifierId());
			
			if (null != refSetMini) {
				return adapterType.cast(refSetMini);
			}
			
			return adapterType.cast(createRefSetMini(refSet));
		}
		
		return null;
	}

	@Override
	public Class<?>[] getAdapterListSafe() {
		return new Class<?>[] { IComponent.class };
	}
	
	private SnomedRefSetIndexEntry createRefSetMini(final SnomedRefSet refSet) {
		return new SnomedRefSetIndexEntry(
				refSet.getIdentifierId(), 
				getIdentifierLabel(refSet), 
				SnomedIconProvider.getInstance().getIconComponentId(refSet.getIdentifierId()),
				new SnomedConceptLookupService().getComponent(refSet.getIdentifierId(), refSet.cdoView()).getModule().getId(),
				0.0F, 
				CDOIDUtils.asLongSafe(refSet.cdoID()),
				false, 
				true,
				refSet.getType(), 
				refSet.getReferencedComponentType(), refSet instanceof SnomedStructuralRefSet);
	}

	private String getIdentifierLabel(final SnomedRefSet refSet) {
		
		final String id = refSet.getIdentifierId();
		IComponent<String> component = getConcept(id);
		String label = null;
		if (null != component)
			label = component.getLabel();
		if (null == label) {
			//TODO refactor this. we can fix read only exception, because no PT is created for the reference set identifier concept. 
			final Object cdoObject = CoreTerminologyBroker.getInstance().getLookupService(SnomedTerminologyComponentConstants.CONCEPT).getComponent(refSet.getIdentifierId(), refSet.cdoView());
			label = SnomedConceptNameProvider.INSTANCE.getText(cdoObject);
			if (StringUtils.isEmpty(label)) {
				if (refSet.cdoView() instanceof CDOTransaction) {
					label = SnomedConceptNameProvider.INSTANCE.getText(refSet.getIdentifierId(), (CDOTransaction) refSet.cdoView());
					if (null == label)
						label = id;
				}
			}
			return label;
		}
		return label;
	}

	private IComponent<String> getConcept(final String id) {
		return getConceptLookupService().getComponent(BranchPathUtils.createActivePath(SnomedRefSetPackage.eINSTANCE), id);
	}

	private ILookupService<String, Object, Object> getConceptLookupService() {
		return CoreTerminologyBroker.getInstance().getLookupService(SnomedTerminologyComponentConstants.CONCEPT);
	}
}