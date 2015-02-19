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
package com.b2international.snowowl.datastore.cdo;

import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.resolveElementProxies;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl.BasicEStoreEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Customized merge {@link Copier copier} which looks up any arbitrary CDO object in the 
 * destination {@link CDOView view} instead of copying it.
 */
public class CDOMergeCopier extends Copier {

	private static final long serialVersionUID = 6409924066711485378L;
	
	private final Predicate<EStructuralFeature> stopPredicate;
	private final CDOView destinationView;
	
	public CDOMergeCopier(final CDOView destinationView) {
		this(Collections.<EStructuralFeature>emptySet(), check(destinationView));
	}
	
	public CDOMergeCopier(final Collection<? extends EStructuralFeature> stopFeatures, final CDOView destinationView) {
		this(Predicates.in(Preconditions.checkNotNull(ImmutableSet.<EStructuralFeature>copyOf(stopFeatures))), check(destinationView));
	}
	
	public CDOMergeCopier(final Predicate<EStructuralFeature> stopPredicate, final CDOView destinationView) {
		super(); //default linked hash map initialization
		this.stopPredicate = Preconditions.checkNotNull(stopPredicate, "Stop predicate argument cannot be null.");
		this.destinationView = destinationView;
	}

	/**
	 * Same behavior as {@link Copier#copy(EObject)} but this method returns with the type safe copied instance.
	 */
	@SuppressWarnings("unchecked")
	public <E extends EObject> E copy2(final E eObject) {
		return (E) super.copy(eObject);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.util.EcoreUtil.Copier#copyAll(java.util.Collection)
	 */
	@Override
	public <T> Collection<T> copyAll(final Collection<? extends T> eObjects) {
		
		final Collection<T> $ = Lists.newArrayListWithExpectedSize(eObjects.size());
		
		@SuppressWarnings("unchecked") final T[] copy = (T[]) new Object[eObjects.size()];
		eObjects.toArray(copy);
		
		for (final T t : copy) {
			@SuppressWarnings("unchecked") final T copyT = (T) copy((EObject) t);
			$.add(copyT);
		}
		
		return $;
	}
	
	/* (non-Javadoc)
	 * @see java.util.LinkedHashMap#get(java.lang.Object)
	 */
	@Override
	public EObject get(final Object key) {
		
		final EObject value = super.get(key);
		
		if (null != value) {
			
			return value;
			
		}
		
		if (key instanceof CDOObject) {
		
			final CDOObject object = CDOUtils.getObjectIfExists(destinationView, ((CDOObject) key).cdoID());
			if (null != object) {
				return object;
			}
			
		} else if (key instanceof CDOID) {
			
			final CDOObject object = CDOUtils.getObjectIfExists(destinationView, (CDOID) key);
			if (null != object) {
				return object;
			}
			
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.util.EcoreUtil.Copier#copyContainment(org.eclipse.emf.ecore.EReference, org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected void copyContainment(final EReference eReference, final EObject eObject, final EObject copyEObject) {
		if (!stopPredicate.apply(eReference)) {

			if (isCdo(eObject) && isCdo(copyEObject)) {

				final CDOObject cdoObject = toCdo(eObject);
				final CDOObject copyCdoObject = toCdo(copyEObject);

				if (cdoObject.eIsSet(eReference)) {

					if (eReference.isMany()) {

						resolveElementProxies(cdoObject);
						resolveElementProxies(copyCdoObject);

						@SuppressWarnings("unchecked") final List<CDOObject> source = (List<CDOObject>) cdoObject.eGet(eReference);
						@SuppressWarnings("unchecked") final List<CDOObject> target = (List<CDOObject>) copyCdoObject.eGet(getTarget(eReference));

						
						if (source.isEmpty()) {
							target.clear();
						} else {
							final Collection<CDOObject> copyAll = copyAll(source);
							target.addAll(copyAll);
							
							// Remove all other items not in copyAll
							for (Iterator<CDOObject> itr = target.iterator(); itr.hasNext(); /* empty */) {
								if (!copyAll.contains(itr.next())) {
									itr.remove();
								}
							}
						}

					} else {
						final CDOObject childCdoObject = (CDOObject) eObject.eGet(eReference);
						copyEObject.eSet(getTarget(eReference), childCdoObject == null ? null : copy(childCdoObject));
					}
				}

			} else {
				super.copyContainment(eReference, eObject, copyEObject);
			}

		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.util.EcoreUtil.Copier#createCopy(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected EObject createCopy(final EObject eObject) {
		
		if (isCdo(eObject)) {
			
			final CDOObject cdoObject = toCdo(eObject);
			final CDOObject copyCdoObject = CDOUtils.getObjectIfExists(destinationView, cdoObject.cdoID());
			if (null != copyCdoObject) {
				
				final CDORevision cdoRevision = cdoObject.cdoRevision();
				final CDOBranch branch = cdoRevision.getBranch();
				final int version = cdoRevision.getVersion();
				
				final CDORevision copyCdoRevision = copyCdoObject.cdoRevision();
				final CDOBranch copyBranch = null == copyCdoRevision ? destinationView.getBranch() : copyCdoRevision.getBranch();
				final int copyVersion = null == copyCdoRevision ? CDORevision.FIRST_VERSION : copyCdoRevision.getVersion();
				
				if (branch.equals(copyBranch) && version == copyVersion) {
					
					return copyCdoObject;
					
				} else {
					
					//never copy item without container
					if (null == copyCdoObject.eContainer()) {
						return copyCdoObject;
					}
					
					EcoreUtil.delete(copyCdoObject);
					return super.createCopy(cdoObject);
					
				}
				
			} else {
				return super.createCopy(eObject);
			}
			
		}
		
		return super.createCopy(eObject);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.util.EcoreUtil.Copier#copyReference(org.eclipse.emf.ecore.EReference, org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected void copyReference(final EReference eReference, final EObject eObject, final EObject copyEObject) {
		if (!stopPredicate.apply(eReference)) {
			
			if (isCdo(eObject) && isCdo(copyEObject)) {
				
				final CDOObject cdoObject = toCdo(eObject);
				final CDOObject copyCdoObject = toCdo(copyEObject);
				
				if (cdoObject.eIsSet(eReference)) {
					
					if (eReference.isMany()) {
						
						resolveElementProxies(cdoObject);
						resolveElementProxies(copyCdoObject);
						
						@SuppressWarnings("unchecked")
						final BasicEStoreEList<CDOObject> source = (BasicEStoreEList<CDOObject>) cdoObject.eGet(eReference);
						@SuppressWarnings("unchecked")
						final BasicEStoreEList<CDOObject> target = (BasicEStoreEList<CDOObject>) copyCdoObject.eGet(getTarget(eReference));
						
						if (source.isEmpty()) {
							
							target.clear();
							
						} else {
							
							final boolean isBidirectional = eReference.getEOpposite() != null;
							int index = 0;
							for (final Iterator<CDOObject> itr = resolveProxies ? source.iterator() : source.basicIterator(); itr.hasNext(); /* nothing */) {
								
								final CDOObject referencedCDOObject = itr.next();
								final CDOObject copyReferencedCDOObject = (CDOObject) get(referencedCDOObject);
								if (copyReferencedCDOObject == null) {
									
									if (useOriginalReferences && !isBidirectional) {
										target.addUnique(index, referencedCDOObject);
										++index;
									}
									
								} else {
									
									if (isBidirectional) {
										final int position = target.indexOf(copyReferencedCDOObject);
										if (position == -1) {
											target.addUnique(index, copyReferencedCDOObject);
										} else if (index != position) {
											target.move(index, copyReferencedCDOObject);
										}
									} else {
										target.addUnique(index, copyReferencedCDOObject);
									}
									++index;
									
								}
								
							}
						}
						
					} else {
						
						final Object referencedEObject = eObject.eGet(eReference, resolveProxies);
						
						if (null == referencedEObject) {
							
							copyEObject.eSet(getTarget(eReference), null);
							
						} else {
							
							final Object copyReferencedEObject = get(referencedEObject);
							
							if (null == copyReferencedEObject) {
								if (useOriginalReferences && null == eReference.getEOpposite()) {
									copyEObject.eSet(getTarget(eReference), referencedEObject);
								}
							} else {
								copyEObject.eSet(getTarget(eReference), copyReferencedEObject);
							}
							
						}
						
					}
					
				}
				
			} else {
				
				super.copyReference(eReference, eObject, copyEObject);
				
			}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.util.EcoreUtil.Copier#copyAttribute(org.eclipse.emf.ecore.EAttribute, org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected void copyAttribute(final EAttribute eAttribute, final EObject eObject, final EObject copyEObject) {
		if (!stopPredicate.apply(eAttribute)) {
			
			if (eAttribute.isUnsettable() && !eObject.eIsSet(eAttribute)) {
				return;
			}
				
			if (eAttribute.isMany()) {
				List<?> source = (List<?>)eObject.eGet(eAttribute);
				@SuppressWarnings("unchecked") List<Object> target = (List<Object>)copyEObject.eGet(getTarget(eAttribute));
				
				// Replace existing items in target
				target.clear();
				target.addAll(source);
			} else {
				copyEObject.eSet(getTarget(eAttribute), eObject.eGet(eAttribute));
			}
				
			
		}
	}
	
	/**Returns {@code true} if the {@link Object} argument is a {@link CDOObject}. Otherwise {@code false}.*/
	protected boolean isCdo(final Object eObject) {
		return eObject instanceof CDOObject;
	}
	
	/**Performs a static cast on the {@link EObject} argument and returns with a {@link CDOObject}.*/
	protected CDOObject toCdo(final Object eObject) {
		Preconditions.checkState(isCdo(eObject));
		return (CDOObject) eObject;
	}
	
}