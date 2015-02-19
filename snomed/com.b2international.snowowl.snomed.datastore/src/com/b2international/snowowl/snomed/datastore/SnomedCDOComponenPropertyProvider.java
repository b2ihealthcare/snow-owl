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
package com.b2international.snowowl.snomed.datastore;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentPropertyProvider;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMemberNameProvider;
import com.b2international.snowowl.snomed.datastore.services.SnomedRelationshipNameProvider;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * CDOish component property provider implementation for SNOMED&nbsp;CT.
 * <p>Clients may spare the GC by saving a reference from an instance of this class
 * an modify the underlying object via {@link #setObject(Object)}. 
 */
public class SnomedCDOComponenPropertyProvider implements IComponentPropertyProvider {

	private static final String REFERENCE_SET = "Reference set";

	private Object object;
	
	/**Sole constructor.*/
	public SnomedCDOComponenPropertyProvider() {
		this(null);
	}
	
	/**Creates a new property provider with the given object argument.*/
	public SnomedCDOComponenPropertyProvider(@Nullable final Object object) {
		this.object = object;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentPropertyProvider#getId()
	 */
	@Override
	public String getId() {
		if (object instanceof Concept) {
			return ((Concept) object).getId();
		} else if (object instanceof Relationship) {
			return ((Relationship) object).getId();
		} else if (object instanceof Description) {
			return ((Description) object).getId();
		} else if (object instanceof SnomedRefSet) {
			return ((SnomedRefSet) object).getIdentifierId();
		} else if (object instanceof SnomedRefSetMember) {
			return ((SnomedRefSetMember) object).getUuid();
		}
		return UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentPropertyProvider#getLabel()
	 */
	@Override
	public String getLabel() {
		if (object instanceof Concept) {
			return SnomedConceptNameProvider.INSTANCE.getText(object);
		} else if (object instanceof Relationship) {
			return SnomedRelationshipNameProvider.INSTANCE.getText(((Relationship) object).getId());
		} else if (object instanceof Description) {
			return ((Description) object).getTerm();
		} else if (object instanceof SnomedRefSet) {
			return getLabel((SnomedRefSet) object);
		} else if (object instanceof SnomedRefSetMember) {
			return SnomedRefSetMemberNameProvider.INSTANCE.getText(object, ((CDOObject) object).cdoView());
		}
		return EMPTY;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentPropertyProvider#getArtefactType()
	 */
	@Override
	public String getArtefactType() {
		if (object instanceof SnomedRefSet) {
			return REFERENCE_SET;
		} else if (object instanceof SnomedRefSetMember) {
			return getLabel((SnomedRefSetMember) object);
		}
		return object instanceof EObject ? ((EObject) object).eClass().getName() : null == object ? UNKNOWN : object.getClass().getSimpleName();
	}
	
	/**
	 * Sets the underlying object reference based on the argument.
	 */
	public void setObject(@Nullable final Object object) {
		this.object = object;
	}
	
	private String getLabel(final SnomedRefSet refSet) {
		return getConceptLabelForId(refSet.getIdentifierId(), BranchPathUtils.createPath(refSet));
	}

	private String getLabel(final SnomedRefSetMember member) {
		return getConceptLabelForId(member.getRefSetIdentifierId(), BranchPathUtils.createPath(member));
	}

	private String getConceptLabelForId(final String id, final IBranchPath branchPath) {
		return getTerminologyBrowser().getConcept(branchPath, id).getLabel();
	}

	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}

}