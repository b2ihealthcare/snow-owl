/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitIntegrityException;

import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Caspar De Groot
 * @since 4.0
 */
public class CommitIntegrityCheck
{
  private InternalCDOTransaction transaction;

  private Style style;

  private Set<CDOID> newIDs, dirtyIDs, detachedIDs;

  private Set<CDOObject> missingObjects = new HashSet<CDOObject>();

  private StringBuilder exceptionMessage = new StringBuilder();

  public CommitIntegrityCheck(InternalCDOCommitContext commitContext)
  {
    this(commitContext, Style.EXCEPTION_FAST);
  }

  public CommitIntegrityCheck(InternalCDOCommitContext commitContext, Style style)
  {
    transaction = commitContext.getTransaction();

    CheckUtil.checkArg(style, "style");
    this.style = style;

    newIDs = commitContext.getNewObjects().keySet();
    dirtyIDs = commitContext.getDirtyObjects().keySet();
    detachedIDs = commitContext.getDetachedObjects().keySet();
  }

  public void check() throws CommitIntegrityException
  {
    // For new objects: ensure that their container is included,
    // as well as the targets of the new object's bidi references
    for (CDOID newID : newIDs)
    {
      CDOObject newObject = transaction.getObject(newID);
      checkContainerIncluded(newObject, "new");
      checkCurrentRefTargetsIncluded(newObject, "new");
    }

    // For detached objects: ensure that their former container is included,
    // as well as the targets of the detached object's bidi references
    for (CDOID detachedID : detachedIDs)
    {
      CDOObject detachedObject = transaction.getObject(detachedID);
      checkFormerContainerIncluded(detachedObject);
      checkFormerBidiRefTargetsIncluded(detachedObject, "detached");
    }

    // For dirty objects: if any of the deltas for the object, affect containment (i.e. object was moved)
    // or a bi-di reference, ensure that for containment, both the old and new containers are included,
    // (or that the child is included if we are considering the dirty parent),
    // and that for a bi-di reference, the object holding the other end of the bi-di is included,
    // as well as possibly the *former* object holding the other end.
    for (CDOID dirtyID : dirtyIDs)
    {
      CDOObject dirtyObject = transaction.getObject(dirtyID);
      analyzeRevisionDelta((InternalCDOObject)dirtyObject);
    }

    if (!missingObjects.isEmpty() && style == Style.EXCEPTION)
    {
      throw createException();
    }
  }

  public Set<? extends EObject> getMissingObjects()
  {
    return missingObjects;
  }

  private CDOID getContainerOrResourceID(InternalCDORevision revision)
  {
    CDOID containerOrResourceID = null;
    Object idOrObject = revision.getContainerID();
    if (idOrObject != null)
    {
      containerOrResourceID = (CDOID)transaction.convertObjectToID(idOrObject);
    }

    if (CDOIDUtil.isNull(containerOrResourceID))
    {
      idOrObject = revision.getResourceID();
      if (idOrObject != null)
      {
        containerOrResourceID = (CDOID)transaction.convertObjectToID(idOrObject);
      }
    }

    return containerOrResourceID;
  }

  private void analyzeRevisionDelta(InternalCDOObject dirtyObject) throws CommitIntegrityException
  {
    // Getting the deltas from the TX is not a good idea...
    // We better recompute a fresh delta:
    InternalCDORevision cleanRev = transaction.getCleanRevisions().get(dirtyObject);
    CheckUtil.checkNull(cleanRev, "Could not obtain clean revision for dirty object " + dirtyObject);

    InternalCDORevision dirtyRev = dirtyObject.cdoRevision();
    CDORevisionDelta rDelta = dirtyRev.compare(cleanRev);

    for (CDOFeatureDelta featureDelta : rDelta.getFeatureDeltas())
    {
      EStructuralFeature feat = featureDelta.getFeature();
      if (feat == CDOContainerFeatureDelta.CONTAINER_FEATURE)
      {
        // Three possibilities here:
        // 1. Object's container has changed
        // 2. Object's containment feature has changed
        // 3. Object's resource has changed
        // (or several of the above)

        // @1
        CDOID currentContainerID = (CDOID)transaction.convertObjectToID(dirtyRev.getContainerID());
        CDOID cleanContainerID = (CDOID)transaction.convertObjectToID(cleanRev.getContainerID());
        if (!CDOIDUtil.equals(currentContainerID, cleanContainerID))
        {
          if (currentContainerID != CDOID.NULL)
          {
            checkIncluded(currentContainerID, "container of moved", dirtyObject);
          }

          if (cleanContainerID != CDOID.NULL)
          {
            checkIncluded(cleanContainerID, "former container of moved", dirtyObject);
          }
        }

        // @2
        // Nothing to be done. (I think...)

        // @3
        CDOID currentResourceID = (CDOID)transaction.convertObjectToID(dirtyRev.getResourceID());
        CDOID cleanResourceID = (CDOID)transaction.convertObjectToID(cleanRev.getResourceID());
        if (!CDOIDUtil.equals(currentResourceID, cleanResourceID))
        {
          if (currentResourceID != CDOID.NULL)
          {
            checkIncluded(currentResourceID, "resource of moved", dirtyObject);
          }

          if (cleanResourceID != CDOID.NULL)
          {
            checkIncluded(cleanResourceID, "former resource of moved", dirtyObject);
          }
        }
      }
      else if (feat instanceof EReference)
      {
        if (featureDelta instanceof CDOListFeatureDelta)
        {
          for (CDOFeatureDelta innerFeatDelta : ((CDOListFeatureDelta)featureDelta).getListChanges())
          {
            checkFeatureDelta(innerFeatDelta, dirtyObject);
          }
        }
        else
        {
          checkFeatureDelta(featureDelta, dirtyObject);
        }
      }
    }
  }

  private void checkIncluded(Object idOrObject, String msg, CDOObject o) throws CommitIntegrityException
  {
    idOrObject = transaction.convertObjectToID(idOrObject);
    if (idOrObject instanceof CDOID)
    {
      CDOID id = (CDOID)idOrObject;
      if (!id.isNull())
      {
        checkIncluded(id, msg, o);
      }
    }

    // else: Transient object -- ignore
  }

  private void checkFeatureDelta(CDOFeatureDelta featureDelta, CDOObject dirtyObject) throws CommitIntegrityException
  {
    EReference ref = (EReference)featureDelta.getFeature();
    boolean containmentOrWithOpposite = ref.isContainment() || hasPersistentOpposite(ref);

    if (featureDelta instanceof CDOAddFeatureDelta)
    {
      Object idOrObject = ((CDOAddFeatureDelta)featureDelta).getValue();
      if (containmentOrWithOpposite || isNew(idOrObject))
      {
        checkIncluded(idOrObject, "added child / refTarget of", dirtyObject);
      }
    }
    else if (featureDelta instanceof CDOSetFeatureDelta)
    {
      Object oldIDOrObject = ((CDOSetFeatureDelta)featureDelta).getOldValue();
      CDOID oldID = (CDOID)transaction.convertObjectToID(oldIDOrObject);
      if (!CDOIDUtil.isNull(oldID))
      {
        // Old child must be included if it's the container or has an eOpposite
        if (containmentOrWithOpposite)
        {
          checkIncluded(oldID, "removed / former child / refTarget of", dirtyObject);
        }
      }

      Object newIDOrObject = ((CDOSetFeatureDelta)featureDelta).getValue();
      if (newIDOrObject != null)
      {
        // New child must be included
        newIDOrObject = transaction.convertObjectToID(newIDOrObject);
        if (containmentOrWithOpposite || isNew(newIDOrObject))
        {
          checkIncluded(newIDOrObject, "new child / refTarget of", dirtyObject);
        }
      }
    }
    else if (containmentOrWithOpposite)
    {
      if (featureDelta instanceof CDORemoveFeatureDelta)
      {
        Object idOrObject = ((CDORemoveFeatureDelta)featureDelta).getValue();
        CDOID id = (CDOID)transaction.convertObjectToID(idOrObject);
        checkIncluded(id, "removed child / refTarget of", dirtyObject);
      }
      else if (featureDelta instanceof CDOClearFeatureDelta)
      {
        EStructuralFeature feat = ((CDOClearFeatureDelta)featureDelta).getFeature();
        InternalCDORevision cleanRev = transaction.getCleanRevisions().get(dirtyObject);
        int n = cleanRev.size(feat);
        for (int i = 0; i < n; i++)
        {
          Object idOrObject = cleanRev.get(feat, i);
          CDOID id = (CDOID)transaction.convertObjectToID(idOrObject);
          checkIncluded(id, "removed child / refTarget of", dirtyObject);
        }
      }
      else if (featureDelta instanceof CDOUnsetFeatureDelta)
      {
        EStructuralFeature feat = ((CDOUnsetFeatureDelta)featureDelta).getFeature();
        InternalCDORevision cleanRev = transaction.getCleanRevisions().get(dirtyObject);
        Object idOrObject = cleanRev.getValue(feat);
        CDOID id = (CDOID)transaction.convertObjectToID(idOrObject);
        checkIncluded(id, "removed child / refTarget of", dirtyObject);
      }
      else if (featureDelta instanceof CDOMoveFeatureDelta)
      {
        // Nothing to do: a move doesn't affect the child being moved
        // so that child does not need to be included
      }
      else
      {
        throw new IllegalArgumentException("Unexpected delta type: " + featureDelta.getClass().getSimpleName());
      }
    }
  }

  private boolean isNew(Object idOrObject)
  {
    CDOObject object = null;
    if (idOrObject instanceof CDOObject)
    {
      object = (CDOObject)idOrObject;
    }
    else if (idOrObject instanceof EObject)
    {
      object = CDOUtil.getCDOObject((EObject)idOrObject);
    }
    else if (idOrObject instanceof CDOID)
    {
      object = transaction.getObject((CDOID)idOrObject);
    }

    if (object != null)
    {
      return object.cdoState() == CDOState.NEW;
    }

    return false;
  }

  private void checkIncluded(CDOID id, String msg, CDOObject o) throws CommitIntegrityException
  {
    if (id.isNull())
    {
      throw new IllegalArgumentException("CDOID must not be NULL");
    }

    if (!dirtyIDs.contains(id) && !detachedIDs.contains(id) && !newIDs.contains(id))
    {
      CDOObject missingObject = transaction.getObject(id);
      if (missingObject == null)
      {
        throw new IllegalStateException("Could not find object for CDOID " + id);
      }

      missingObjects.add(missingObject);

      if (exceptionMessage.length() > 0)
      {
        exceptionMessage.append('\n');
      }

      String m = String.format("The %s object %s needs to be included in the commit but isn't", msg, o);
      exceptionMessage.append(m);

      if (style == Style.EXCEPTION_FAST)
      {
        throw createException();
      }
    }
  }

  private CommitIntegrityException createException()
  {
    return new CommitIntegrityException(exceptionMessage.toString(), missingObjects);
  }

  /**
   * Checks whether the container of a given object is included in the commit
   */
  private void checkContainerIncluded(CDOObject object, String msgFrag) throws CommitIntegrityException
  {
    EObject eContainer = object.eContainer();
    if (eContainer == null)
    {
      // It's a top-level object
      CDOResource resource = object.cdoDirectResource();
      checkIncluded(resource.cdoID(), "resource of " + msgFrag, object);
    }
    else
    {
      CDOObject container = CDOUtil.getCDOObject(eContainer);
      checkIncluded(container.cdoID(), "container of " + msgFrag, object);
    }
  }

  private void checkCurrentRefTargetsIncluded(CDOObject referencer, String msgFrag) throws CommitIntegrityException
  {
    for (EReference eRef : referencer.eClass().getEAllReferences())
    {
      if (EMFUtil.isPersistent(eRef))
      {
        if (eRef.isMany())
        {
          EList<?> list = (EList<?>)referencer.eGet(eRef);
          for (Object refTarget : list)
          {
            checkBidiRefTargetOrNewNonBidiTargetIncluded(referencer, eRef, refTarget, msgFrag);
          }
        }
        else
        {
          Object refTarget = referencer.eGet(eRef);
          if (refTarget != null)
          {
            checkBidiRefTargetOrNewNonBidiTargetIncluded(referencer, eRef, refTarget, msgFrag);
          }
        }
      }
    }
  }

  private void checkBidiRefTargetOrNewNonBidiTargetIncluded(CDOObject referencer, EReference eRef, Object refTarget,
      String msgFrag) throws CommitIntegrityException
  {
    if (hasPersistentOpposite(eRef))
    {
      // It's a bi-di ref; the target must definitely be included
      checkBidiRefTargetIncluded(refTarget, referencer, eRef.getName(), msgFrag);
    }
    else if (isNew(refTarget))
    {
      // It's a non-bidi ref; the target doesn't have to be included unless it's NEW
      checkIncluded(refTarget, "target of reference '" + eRef.getName() + "' of " + msgFrag, referencer);
    }
  }

  private void checkFormerBidiRefTargetsIncluded(CDOObject referencer, String msgFrag) throws CommitIntegrityException
  {
    // The referencer argument should really be a detached object, and so we know
    // that we can find the pre-detach revision in tx.getFormerRevisions(). However,
    // the object may have already been dirty prior to detachment, so we check the
    // clean revisions first.
    InternalCDORevision cleanRev = transaction.getCleanRevisions().get(referencer);
    CheckUtil.checkState(cleanRev, "cleanRev");

    for (EReference eRef : referencer.eClass().getEAllReferences())
    {
      if (EMFUtil.isPersistent(eRef) && hasPersistentOpposite(eRef))
      {
        Object value = cleanRev.get(eRef, EStore.NO_INDEX);
        if (value != null)
        {
          if (eRef.isMany())
          {
            EList<?> list = (EList<?>)value;
            for (Object element : list)
            {
              checkBidiRefTargetIncluded(element, referencer, eRef.getName(), msgFrag);
            }
          }
          else
          {
            checkBidiRefTargetIncluded(value, referencer, eRef.getName(), msgFrag);
          }
        }
      }
    }
  }

  private void checkBidiRefTargetIncluded(Object refTarget, CDOObject referencer, String refName, String msgFrag)
      throws CommitIntegrityException
  {
    CheckUtil.checkArg(refTarget, "refTarget");
    CDOID refTargetID = null;
    if (refTarget instanceof EObject)
    {
      refTargetID = CDOUtil.getCDOObject((EObject)refTarget).cdoID();
      if (refTargetID == null)
      {
        // No ID, means object is TRANSIENT; ignore.
        return;
      }
    }
    else if (refTarget instanceof CDOID)
    {
      refTargetID = (CDOID)refTarget;
    }

    checkIncluded(refTargetID, "target of reference '" + refName + "' of " + msgFrag, referencer);
  }

  private void checkFormerContainerIncluded(CDOObject detachedObject) throws CommitIntegrityException
  {
    InternalCDORevision rev = transaction.getCleanRevisions().get(detachedObject);
    CheckUtil.checkNull(rev, "Could not obtain clean revision for detached object " + detachedObject);

    CDOID id = getContainerOrResourceID(rev);
    checkIncluded(id, "former container (or resource) of detached", detachedObject);
  }

  private static boolean hasPersistentOpposite(EReference ref)
  {
    EReference eOpposite = ref.getEOpposite();
    return eOpposite != null && EMFUtil.isPersistent(eOpposite);
  }

  /**
   * Designates an exception style for a {@link CommitIntegrityCheck}
   * 
   * @author Caspar De Groot
   */
  public static enum Style
  {
    /**
     * Throw an exception as soon as this {@link CommitIntegrityCheck} encounters the first problem
     */
    EXCEPTION_FAST,

    /**
     * Throw an exception when this {@link CommitIntegrityCheck} finishes performing all possible checks, in case any
     * problems were found
     */
    EXCEPTION,

    /**
     * Do not throw an exception. Caller must invoke {@link CommitIntegrityCheck#getMissingObjects()} to find out if the
     * check discovered any problems.
     */
    NO_EXCEPTION
  }
}
