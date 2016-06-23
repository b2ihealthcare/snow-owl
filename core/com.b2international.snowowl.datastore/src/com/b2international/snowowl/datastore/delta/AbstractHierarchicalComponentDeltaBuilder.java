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
package com.b2international.snowowl.datastore.delta;

import java.util.Collection;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.ChangeKind;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.core.api.component.IconIdProvider;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.datastore.AbstractLookupService;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;

/**
 * Extended {@link AbstractComponentDeltaBuilder component delta builder} responsible for building and providing taxonomy information about 
 * the {@link HierarchicalComponentDelta component delta}s.
 * 
 * @see AbstractComponentDeltaBuilder
 * @see HierarchicalComponentDelta
 */
public abstract class AbstractHierarchicalComponentDeltaBuilder<C extends HierarchicalComponentDelta> extends AbstractComponentDeltaBuilder<C> {

	/**
	 * Supplier for the root component IDs in the terminology.
	 */
	private final Supplier<Collection<String>> rootConceptIdSupplier = Suppliers.memoize(new Supplier<Collection<String>>() {
		@Override 
		public Collection<String> get() {
			return getTerminologyBrowser().getRootConceptIds(getBranchPath());
		}
	});

	private boolean terminologyAvailable; 

	@Override
	protected void preProcess() {
		super.preProcess();
		terminologyAvailable = getTerminologyBrowser().isTerminologyAvailable(getBranchPath());
	}

	/**
	 * Builds the taxonomy among the component deltas.
	 */
	@Override
	protected void postProcess() {
		for (final C componentDelta : getDeltas()) {
			C currentDelta = componentDelta;

			while (true) {

				if (hasParent(currentDelta) || isTerminologyBrowserRoot(currentDelta)) {
					break;
				}

				final String parentId = getParentId(currentDelta);
				if (null == parentId) {
					break;
				}

				// Get first available item from parents, create a blank one if it didn't exist 
				final Collection<C> parentDeltas = get(parentId);
				C parentDelta = Iterables.getFirst(parentDeltas, null);

				if (parentDelta == null) {
					parentDelta = buildUnchangedDelta(getIndexEntry(parentId));
					put(parentDelta);
				}

				// Register parentage
				parentDelta.getChildren().add(currentDelta);
				currentDelta.setParent(parentDelta);

				// Continue the process with the parent delta until the top is reached
				currentDelta = parentDelta;
			}
		}

		super.postProcess();
	}

	private boolean hasParent(final C delta) {
		return delta.getParent() != null;
	}

	/**
	 * Returns {@code true} if the component is a root element in the terminology.
	 * 
	 * @param component the terminology independent component
	 * @return {@code true} if the component is a root element, {@code false} otherwise
	 */
	private boolean isTerminologyBrowserRoot(final C component) {
		return rootConceptIdSupplier.get().contains(component.getId());
	}

	private String getParentId(final C delta) {
		if (!terminologyAvailable) {
			return null;
		}

		// Try the terminology browser-based approach first, look in CDO directly if the approach fails 
		final String parentId = getParentIdFromTerminologyBrowser(delta.getId());
		if (null != parentId) {
			return parentId;
		} else {
			return getParentIdFromCdoObject(delta);
		}
	}

	protected String getParentIdFromTerminologyBrowser(final String id) {
		return Iterables.getFirst(getTerminologyBrowser().getSuperTypeIds(getBranchPath(), id), null);
	}

	private String getParentIdFromCdoObject(final C delta) {
		final CDOObject cdoObject;

		if (delta.isDeleted()) {
			cdoObject = CDOUtils.getObjectIfExists(getBaseView(), CDOIDUtil.createLong(delta.getCdoId()));
		} else {
			cdoObject = CDOUtils.getObjectIfExists(getCurrentView(), CDOIDUtil.createLong(delta.getCdoId()));
		}			

		return getParentIdFromCdoObject(cdoObject);
	}

	protected abstract String getParentIdFromCdoObject(final CDOObject cdoObject);

	private IIndexEntry getIndexEntry(final String id) {
		if (!terminologyAvailable) {
			return null;
		}

		// Try the terminology browser-based approach first, look in CDO directly if the approach fails
		final IIndexEntry indexEntry = getIndexEntryFromTerminologyBrowser(id);
		if (null != indexEntry) {
			return indexEntry;
		} else {
			return getIndexEntryFromCdoObject(id);
		}
	}

	protected IIndexEntry getIndexEntryFromTerminologyBrowser(final String id) {
		return getTerminologyBrowser().getConcept(getBranchPath(), id);
	}

	protected abstract IIndexEntry getIndexEntryFromCdoObject(final String ancestorId);

	protected abstract AbstractLookupService<String, ? extends CDOObject, CDOView> createLookupService();

	protected abstract ITerminologyBrowser<? extends IIndexEntry, String> getTerminologyBrowser();

	protected abstract short getTerminologyComponentId();

	/**
	 * Builds the delta based on the specified component.
	 */
	@SuppressWarnings("unchecked")
	protected <CC extends IIndexEntry> C buildUnchangedDelta(final CC component) {
		final short terminologyComponentId = getTerminologyComponentId();
		final String codeSystemOid = getCodeSystemOID(terminologyComponentId);

		return (C) new HierarchicalComponentDelta(
				component.getId(), 
				component.getStorageKey(),
				getBranchPath(),
				component.getLabel(),
				component instanceof IconIdProvider<?> ? ((IconIdProvider<String>)component).getIconId() : null,
				terminologyComponentId,
				codeSystemOid,
				ChangeKind.UNCHANGED);
	}
}
