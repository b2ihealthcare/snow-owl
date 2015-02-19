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
package com.b2international.snowowl.datastore.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import javax.annotation.Nonnull;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.ComponentUtils;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Utility class for terminology independent components represented as {@link CDOObject CDO objects}. 
 * @see ComponentUtils
 */
public abstract class ComponentUtils2 {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComponentUtils2.class);
	
	/**
	 * Comparator for comparing EClass class names. If the specified classes based on the arguments are equals it falls back to the instances' hash code.
	 */
	public static final Comparator<CDOObject> CDO_OBJECT_COMPARATOR = new Comparator<CDOObject>() {
		@Override public int compare(final CDOObject o1, final CDOObject o2) {
			if (null == o1) {
				LOGGER.warn("Object was null.");
				return -1;
			}
			if (null == o2) {
				LOGGER.warn("Object was null.");
				return 1;
			}
			final int result = o1.eClass().getName().compareTo(o2.eClass().getName());
			return result == 0 ? o1.hashCode() - o2.hashCode(): result;
		}
	};
	
	/**Function for converting CDO objects to CDO revisions.*/
	public static final Function<CDOObject, InternalCDORevision> OBJECT_TO_REVISION_FUNCTION = new Function<CDOObject, InternalCDORevision>() {
		@Override public InternalCDORevision apply(final CDOObject object) {
			return (InternalCDORevision) object.cdoRevision();
		}
	};
	
	/**Function for converting CDO revisions to CDO IDs.*/
	public static final Function<InternalCDORevision, CDOID> REVISION_TO_ID_FUNCTION = new Function<InternalCDORevision, CDOID>() {
		@Override public CDOID apply(final InternalCDORevision revision) {
			return revision.getID();
		}
	};
	
	/**
	 * Returns all {@link CDOState#NEW not persisted} objects managed by the CDO view that matches with the specified class.  
	 * @param view the CDO view where the search has to be performed. Cannot be {@code null}.
	 * @param clazz the filter class. All other objects will be excluded from the results. Cannot be {@code null}.
	 * @return an iterable of the results.
	 */
	public static <T extends CDOObject> Iterable<T> getNewObjects(@Nonnull final CDOView view, @Nonnull final Class<T> clazz) {
		return getObjects(view, clazz, CDOState.NEW);
	}
	
	/**
	 * Returns all {@link CDOState#DIRTY changed} objects managed by the CDO view that matches with the specified class.  
	 * @param view the CDO view where the search has to be performed. Cannot be {@code null}.
	 * @param clazz the filter class. All other objects will be excluded from the results. Cannot be {@code null}.
	 * @return an iterable of the results.
	 */
	public static <T extends CDOObject> Iterable<T> getDirtyObjects(@Nonnull final CDOView view, @Nonnull final Class<T> clazz) {
		return getObjects(view, clazz, CDOState.DIRTY);
	}
	
	/**
	 * Returns all {@link CDOState#TRANSIENT detached} objects managed by the CDO view that matches with the specified class.  
	 * @param view the CDO view where the search has to be performed. Cannot be {@code null}.
	 * @param clazz the filter class. All other objects will be excluded from the results. Cannot be {@code null}.
	 * @return an iterable of the results.
	 */
	public static <T extends CDOObject> Iterable<T> getDetachedObjects(@Nonnull final CDOView view, @Nonnull final Class<T> clazz) {
		return getObjects(view, clazz, CDOState.TRANSIENT);
	}
	
	/*returns with all objects retrieved from the underlying CDO view specified by the CDO state arguemnt*/
 	private static <T extends CDOObject> Iterable<T> getObjects(@Nonnull final CDOView view, @Nonnull final Class<T> clazz, @Nonnull final CDOState state) {
		checkView(view);
		checkNotNull(clazz, "Class argument cannot be null.");
		checkNotNull(state, "CDO state argument cannot be null");
		if (view instanceof CDOTransaction) {
			return Iterables.filter(getObjectsFromTransaction((CDOTransaction) view, state), clazz);
		}
		
		final Iterable<T> objects = internalGetObjects(view, clazz, state);
		
		//this is required because of the change processors
		//since we are creating CDO views after a CDO invalidation event all new objects get persisted and their state is clean
		//so we concatenate the new objects with the clean ones 
		if (CDOState.NEW.equals(state)) {
			return Iterables.concat(objects, internalGetObjects(view, clazz, CDOState.CLEAN));
		}
		return objects;
	}
	
 	/*returns with all CDO objects from the underlying view that fits with the specified CDO state*/
	private static <T extends CDOObject> Iterable<T> internalGetObjects(final CDOView view, final Class<T> clazz, final CDOState state) {
		final Set<CDOObject> objects = Sets.newHashSet();
		if (view instanceof InternalCDOView) {
			final InternalCDOView internalView = (InternalCDOView) view;
			for (final CDOObject object : internalView.getObjects().values()) {
				if (state.equals(object.cdoState())) {
					objects.add(object);
				}
			}
		}
		return Iterables.filter(objects, clazz);
	}
	
	/*returns with all objects from the transaction that fits to the CDO state argument*/
	private static Collection<CDOObject> getObjectsFromTransaction(final CDOTransaction transaction, final CDOState state) {
		switch (state) {
			case NEW: return  transaction.getNewObjects().values();
			case TRANSIENT: return transaction.getDetachedObjects().values();
			case DIRTY: return transaction.getDirtyObjects().values();
			default: throw new UnsupportedOperationException("Unsupported CDO state: " + state);
		}
	}

	/*checks the CDO view*/
	private static void checkView(final CDOView view) {
		Preconditions.checkNotNull(view, "CDO view argument cannot be null.");
		Preconditions.checkState(!view.isClosed(), "CDO view is not active.");
	}

	/**
	 * Private constructor.
	 */
	private ComponentUtils2() { /*suppress instantiation*/ }
	
}