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
package com.b2international.snowowl.snomed.datastore.uri;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.uri.ITerminologyComponentUriResolver;
import com.b2international.snowowl.core.uri.UriUtils;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;

public class SnomedComponentUriResolver implements ITerminologyComponentUriResolver<IComponent<String>> {

	private static final CoreTerminologyBroker CORE_TERMINOLOGY_BROKER = CoreTerminologyBroker.getInstance();

	@Override
	public IComponent<String> getComponent(String uri) {
		checkNotNull(uri, "URI must not be null.");
		checkArgument(UriUtils.isTerminologyUri(uri), "Unexpected URI: " + uri);
		List<String> uriSegments = UriUtils.getUriSegments(uri);
		String command = uriSegments.get(UriUtils.COMPONENT_TYPE_INDEX);
		if ("code".equals(command)) {
			// concept
			ILookupService<String, Object, Object> lookupService = CORE_TERMINOLOGY_BROKER.getLookupService(SnomedTerminologyComponentConstants.CONCEPT);
			return lookupService.getComponent(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), uriSegments.get(UriUtils.COMPONENT_ID_INDEX));
		} else if ("refset".equals(command)) {
			// refset
			ILookupService<String, Object, Object> lookupService = CORE_TERMINOLOGY_BROKER.getLookupService(SnomedTerminologyComponentConstants.REFSET);
			return lookupService.getComponent(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), uriSegments.get(UriUtils.COMPONENT_ID_INDEX));
		}
		throw new IllegalArgumentException("Can't handle URI: " + uri);
	}

	@Override
	public String getUri(IComponent<String> component) {
		checkNotNull(component, "Terminology component must not be null.");
		String oid = CORE_TERMINOLOGY_BROKER.getTerminologyOid(component);
		String terminologyPrefix = UriUtils.TERMINOLOGY_PROTOCOL + ":" + oid + "?";
		if (component instanceof SnomedConceptIndexEntry
				|| component instanceof SnomedConceptIndexEntry) {
			return terminologyPrefix + "code=" + component.getId();
		} else if (component instanceof SnomedRefSetIndexEntry) {
			return terminologyPrefix + "refset=" + component.getId();
		}
		throw new IllegalArgumentException("Unexpected component: " + component);
	}

}