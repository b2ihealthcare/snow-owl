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
package com.b2international.snowowl.snomed.refset.core.services;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;

import org.eclipse.emf.cdo.view.CDOView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.ComponentIdAndLabel;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Preconditions;

/**
 * Component name provider implementation for SNOMED CT reference set members.
 */
public enum SnomedRefSetMemberNameProvider implements IComponentNameProvider {

	INSTANCE;

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedRefSetMemberNameProvider.class);
	
	/**
	 * Accepts the followings as argument:
	 * <p>
	 * <ul>
	 * <li>{@link SnomedRefSetMember SNOMED CT reference set member}</li>
	 * <li>{@link SnomedRefSetMemberIndexEntry SNOMED CT reference set member Lucene index entry}</li>
	 * <li>{@link String SNOMED CT reference set member UUID as string}</li>
	 * </ul>
	 * </p>
	 */
	public String getText(final Object object) {
		return getText(object, null);
	}

	/**
	 * Returns with the label for the SNOMED&nbsp;CT reference set member.
	 * @param object the object where the label has to be specified.
	 * @param cdoView the CDO view instance. Can be {@code null}. If {@code null} a new editing context will be instantiated and disposed. 
	 * If the specified CDO view is not {@code null} callers should ensure to close the CDO view in the right time since this method will not close it. 
	 * @return the label for the SNOMED&nbsp;CT reference set. This method never returns with {@code null}.
	 */
	public String getText(final Object object, final CDOView cdoView) {
		if (object instanceof String) {
			final ILookupService<String, Object, Object> lookupService = CoreTerminologyBroker.getInstance().getLookupService(SnomedTerminologyComponentConstants.REFSET_MEMBER);
			if (null == lookupService) {
				LOGGER.warn("SNOMED CT reference set member lookup service was null for " + object);
				return String.valueOf(object);
			}
			IComponent<?> component = lookupService.getComponent(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), (String) object);
			if (null == component) {
				SnomedRefSetEditingContext context = null;
				try {
					if (null == cdoView)
						context = SnomedRefSetEditingContext.createInstance();
					final Object adapt = lookupService.getComponent((String) object, null == cdoView ? context.getTransaction() : cdoView);
					if (null != adapt)
						component = CoreTerminologyBroker.getInstance().adapt(adapt);
				} finally {
					if (null != context) {
						context.close();
					}
				}
			}
			return (null == component) ? "" : component.getLabel();
		}
		final IComponent<?> component = CoreTerminologyBroker.getInstance().adapt(object);
		return null == component ? null == object ? "" : String.valueOf(object) : component.getLabel();
	}
	
	@Override
	public String getComponentLabel(final IBranchPath branchPath, final String componentId) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(componentId, "Component ID argument cannot be null.");
		
		final Pair<String, String> labelPair = getServiceForClass(ISnomedComponentService.class).getMemberLabel(branchPath, componentId);
		final StringBuffer sb = new StringBuffer();
		sb.append(labelPair.getA());
		if (!isEmpty(labelPair.getB())) {
			sb.append(" - ");
			sb.append(labelPair.getB());
		}
		return sb.toString();
	}

	// TODO
	@Override
	public ComponentIdAndLabel getComponentIdAndLabel(IBranchPath branchPath, long storageKey) {
		return null;
	}
}
