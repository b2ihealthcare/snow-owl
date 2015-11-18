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
package com.b2international.snowowl.snomed.datastore.snor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.ConceptParentAdapter;
import com.b2international.snowowl.snomed.datastore.SnomedClientRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 *
 */
public class SnomedTerminologyBrowserProvider extends SnomedClientTerminologyBrowser {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedTerminologyBrowserProvider.class);

	public static SnomedClientTerminologyBrowser getTerminologyBrowser(final Concept concept) {
		Preconditions.checkNotNull(concept, "SNOMED CT concept argument cannot be null.");
		switch (concept.cdoState()) {
			case CLEAN:
			case DIRTY:
			case PROXY:
			case CONFLICT:
				if (null != getTerminologyBrowser().getConcept(concept.getId())) {
					return getTerminologyBrowser();
				}
			case NEW:
				final ConceptParentAdapter parentAdapter = getParentAdapter(concept);
				final Set<String> parentIds = getParentConceptIds(parentAdapter);
				if (parentIds == null) {
					LOGGER.warn("SNOMED CT concept does not have parent adapter. Falling back to default SNOMED CT terminology delegate. ID: " + concept.getId());
					return getTerminologyBrowser();
				} else {
					return new SnomedTerminologyBrowserProvider(concept, parentIds);
				}
			case TRANSIENT:
				throw new IllegalStateException("SNOMED CT concept has been detached. ID: " + CDOUtils.getAttribute(concept, SnomedPackage.eINSTANCE.getComponent_Id(), String.class));
			default:
				throw new IllegalArgumentException("Illegal CDO state for SNOMED CT concept. ID: " + concept.getId() + " State: " + concept.cdoState());
		}
	}

	/*returns with the SNOMED CT concept hierarchy delegate service.*/
	private static SnomedClientTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
	}

	/*returns with the parent concept ID as a string extracted from the specified parent adapter.*/
	@Nullable
	private static Set<String> getParentConceptIds(@Nullable final ConceptParentAdapter parentAdapter) {
		if (null == parentAdapter) {
			return null;
		} else {
			return ImmutableSet.copyOf(parentAdapter.getParentConceptIds());
		}
	}

	/*returns with the parent adapter attached to the concept. may return with null.*/
	@Nullable
	private static ConceptParentAdapter getParentAdapter(final Concept concept) {
		return Iterables.getFirst(Iterables.filter(concept.eAdapters(), ConceptParentAdapter.class), null);
	}

	private final SnomedClientTerminologyBrowser delegate;
	private final Concept concept;
	private final Set<String> parentIds;
	private final String conceptId;


	@Override
	public Collection<SnomedConceptIndexEntry> getRootConcepts() {
		return delegate.getRootConcepts();
	}

	@Override
	public SnomedConceptIndexEntry getConcept(final String key) {
		if (conceptId.equals(key)) {
			return getConcept();
		}
		return delegate.getConcept(key);
	}


	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypes(final SnomedConceptIndexEntry concept) {
		if (conceptId.equals(concept.getId())) {
			return Lists.newArrayList(Iterables.transform(parentIds, new ConceptIdToSnomedConceptIndexEntryFromDelegateFunction()));
		}
		return delegate.getSuperTypes(concept);
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSubTypes(final SnomedConceptIndexEntry concept) {
		if (conceptId.equals(concept.getId())) {
			return Collections.emptyList();
		} else if (parentIds.contains(concept.getId())) {
			return Lists.newArrayList(Iterables.concat(delegate.getSubTypes(concept), getConceptAsIterable()));
		} else {
			return delegate.getSubTypes(concept);
		}
	}

	@Override
	public int getSuperTypeCount(final SnomedConceptIndexEntry concept) {
		if (conceptId.equals(concept.getId())) {
			return 1;
		}
		return delegate.getSuperTypeCount(concept);
	}

	@Override
	public int getAllSuperTypeCount(final SnomedConceptIndexEntry concept) {
		if (conceptId.equals(concept.getId())) {
			int delegateAllSuperTypeCount = 0;
			for (final String parentId : parentIds) {
				delegateAllSuperTypeCount += delegate.getAllSuperTypeCount(delegate.getConcept(parentId));
			}
			return 1 + delegateAllSuperTypeCount;
		}
		return delegate.getAllSuperTypeCount(concept);
	}

	@Override
	public int getSubTypeCount(final SnomedConceptIndexEntry concept) {
		if (conceptId.equals(concept.getId())) {
			return 0;
		} else if (parentIds.contains(concept.getId())) {
			return 1 + delegate.getSubTypeCount(concept);
		} else {
			return delegate.getSubTypeCount(concept);
		}
	}

	@Override
	public int getAllSubTypeCount(final SnomedConceptIndexEntry concept) {
		if (conceptId.equals(concept.getId())) {
			return 0;
		} else if (parentIds.contains(concept.getId())) {
			return 1 + delegate.getAllSubTypeCount(concept);
		} else {
			return delegate.getAllSubTypeCount(concept);
		}
	}


	@Override
	public Collection<SnomedConceptIndexEntry> getAllSuperTypes(final SnomedConceptIndexEntry concept) {
		if (conceptId.equals(concept.getId())) {
			final Collection<SnomedConceptIndexEntry> superTypes = Sets.newHashSet();
			for (final String parentId : parentIds) {
				superTypes.addAll(delegate.getAllSuperTypes(delegate.getConcept(parentId)));
			}
			final Set<SnomedConceptIndexEntry> concepts = new HashSet<SnomedConceptIndexEntry>(superTypes.size() + 1);
			concepts.addAll(Collections2.transform(parentIds, new ConceptIdToSnomedConceptIndexEntryFromDelegateFunction()));
			for (final SnomedConceptIndexEntry conceptMini : superTypes) {
				concepts.add(conceptMini);
			}
			return concepts; 
		}
		return delegate.getAllSuperTypes(concept);
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubTypes(final SnomedConceptIndexEntry concept) {
		if (conceptId.equals(concept.getId())) {
			return Collections.emptySet();
		} else if (parentIds.contains(concept.getId())) {
			final Collection<SnomedConceptIndexEntry> allSubTypes = delegate.getAllSubTypes(concept);
			final Set<SnomedConceptIndexEntry> concepts = new HashSet<SnomedConceptIndexEntry>(allSubTypes.size() + 1);
			concepts.add(getConcept());
			for (final SnomedConceptIndexEntry conceptMini : allSubTypes) {
				concepts.add(conceptMini);
			}
			return concepts;
		} else {
			final Collection<SnomedConceptIndexEntry> allSubTypes = delegate.getAllSubTypes(concept);
			final Set<SnomedConceptIndexEntry> concepts = new HashSet<SnomedConceptIndexEntry>(allSubTypes.size() + 1);
			for (final String parentId : parentIds) {
				final SnomedConceptIndexEntry parent = getConcept(parentId);
				if (null == parent) { //parent has been deleted meanwhile
					return allSubTypes;
				}
				if (allSubTypes.contains(parent)) {
					concepts.add(getConcept());
					for (final SnomedConceptIndexEntry conceptMini : allSubTypes) {
						concepts.add(conceptMini);
					}
				}
			}
			return concepts;
		}
	}

	/*returns with the concept as a singleton collection instance*/
	private Iterable<SnomedConceptIndexEntry> getConceptAsIterable() {
		return Collections.singleton(getConcept());
	}

	/*adapts the underlying CDO concept to a lighter representation and returns with it*/
	private SnomedConceptIndexEntry getConcept() {
		return (SnomedConceptIndexEntry) CoreTerminologyBroker.getInstance().adapt(concept);
	}

	private SnomedTerminologyBrowserProvider(final Concept concept, final Set<String> parentIds) {
		super(ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class));
		this.concept = Preconditions.checkNotNull(concept, "SNOMED CT concept argument cannot be null.");
		this.parentIds = Preconditions.checkNotNull(parentIds, "Parent concept ID cannot be null.");
		conceptId = Preconditions.checkNotNull(concept.getId(), "SNOMED CT concept ID cannot be null.");
		delegate = getTerminologyBrowser();
	}

	@Override
	public boolean isSuperTypeOf(final SnomedConceptIndexEntry superType, final SnomedConceptIndexEntry subType) {
		return isSuperTypeOfById(superType.getId(), subType.getId());
	}

	@Override
	public boolean isSuperTypeOfById(final String superTypeId, final String subTypeId) {
		if (subTypeId.equals(concept.getId())) {
			boolean isSuperTypeOf = false;
			for (final String parentId : parentIds) {
				isSuperTypeOf |= delegate.isSuperTypeOfById(superTypeId, parentId);
			}
			return isSuperTypeOf;
		}
		return delegate.isSuperTypeOfById(superTypeId, superTypeId);
	}
	
	@Override
	public List<SnomedConceptIndexEntry> getSubTypesAsList(final SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypesById(final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getSubTypesById(final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSuperTypesById(final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubTypesById(final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSubTypeCountById(final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSubTypeCountById(final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSuperTypeCountById(final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSuperTypeCountById(final String id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public SnomedConceptIndexEntry getTopLevelConcept(final SnomedConceptIndexEntry concept) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean isTerminologyAvailable() {
		return true;
	}

	/**
	 *
	 */
	private final class ConceptIdToSnomedConceptIndexEntryFromDelegateFunction implements Function<String, SnomedConceptIndexEntry> {
		@Override
		public SnomedConceptIndexEntry apply(final String input) {
			return delegate.getConcept(input);
		}
	}

	/**
	 * SNOMED&nbsp;CT reference set hierarchy browser handling reference set membership for unpersisted SNOMED&nbsp;CT concepts.
	 * @see SnomedRefSetBrowser
	 */
	public final static class SnomedRefSetBrowserProvider extends SnomedClientRefSetBrowser {

		public static SnomedClientRefSetBrowser getRefSetBrowser(final Concept concept) {
			CDOUtils.check(concept);
			if (concept.cdoView().isDirty()) {
				return new SnomedRefSetBrowserProvider(concept);
			} else {
				return ApplicationContext.getServiceForClass(SnomedClientRefSetBrowser.class);
			}
		}
		
		private String conceptId;
		private final Set<String> refSetIds;
		private SnomedClientRefSetBrowser delegate;

		/**
		 * @param wrapperService
		 */
		private SnomedRefSetBrowserProvider(final Concept concept) {
			super(ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class));
			this.refSetIds = Sets.newHashSet();
			Preconditions.checkNotNull(concept, "SNOMED CT concept argument cannot be null.");
			this.conceptId = Preconditions.checkNotNull(concept.getId(), "SNOMED CT concept ID cannot be null.");
			
			// Save a snapshot from the new members from the transaction
			final Iterable<SnomedRefSetMember> newRefSetMembers = ImmutableList.copyOf(ComponentUtils2.getNewObjects(concept.cdoView(), SnomedRefSetMember.class));
			for (final SnomedRefSetMember newRefSetMember : newRefSetMembers) {
				if (CDOUtils.checkObject(newRefSetMember)
						&& newRefSetMember.isActive()
						&& SnomedTerminologyComponentConstants.CONCEPT_NUMBER == newRefSetMember.getReferencedComponentType()
						&& conceptId.equals(newRefSetMember.getReferencedComponentId())) {
					
					this.refSetIds.add(newRefSetMember.getRefSetIdentifierId());
				}
			}
			
			this.delegate = getRefSetBrowser();
		}

		private static SnomedClientRefSetBrowser getRefSetBrowser() {
			return ApplicationContext.getInstance().getService(SnomedClientRefSetBrowser.class);
		}

		/* (non-Javadoc)
		 * @see com.b2international.snowowl.snomed.datastore.AbstractClientRefSetBrowser#isReferenced(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean isReferenced(final String refSetId, final String componentId) {
			if (conceptId.equals(componentId)) {
				return refSetIds.contains(refSetId) || delegate.isReferenced(refSetId, componentId);
			} else {
				return delegate.isReferenced(refSetId, componentId);
			}
		}
	}
}