/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.util.List;

/**
 * A {@link CDOTransactionHandler1 transaction handler} that automatically attaches cross-referenced objects to the
 * {@link CDOResource resource} that contains the referencing {@link CDOObject object}.
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOAutoAttacher extends CDODefaultTransactionHandler1
{
  private CDOTransaction transaction;

  public CDOAutoAttacher(CDOTransaction transaction)
  {
    this.transaction = transaction;
    transaction.addTransactionHandler(this);
  }

  public CDOTransaction getTransaction()
  {
    return transaction;
  }

  @Override
  public void attachingObject(CDOTransaction transaction, CDOObject object)
  {
    if (object instanceof CDOResource)
    {
      return;
    }

    // Persist the graph as well.
    EObject obj = CDOUtil.getEObject(object);
    handle(obj, obj);
  }

  @Override
  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
  {
    if (object instanceof CDOResource)
    {
      return;
    }

    if (featureChange != null)
    {
      CDOFeatureDeltaVisitorAutoAttach featureChangeVisitor = new CDOFeatureDeltaVisitorAutoAttach(object);
      featureChange.accept(featureChangeVisitor);
    }
  }

  protected void persist(EObject res, Object object)
  {
    if (!(object instanceof CDOResource) && object instanceof InternalCDOObject)
    {
      InternalCDOObject cdoObject = (InternalCDOObject)object;
      if (FSMUtil.isTransient(cdoObject))
      {
        res.eResource().getContents().add(cdoObject);
      }
    }
  }

  private void check(EObject referrer, EReference reference, EObject element)
  {
    if (element != null && element.eResource() == null)
    {
      if (reference != null && reference.isContainment())
      {
        handle(referrer, element);
      }
      else
      {
        persist(referrer, CDOUtil.getCDOObject(element));
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void handle(EObject referrer, EObject eObject)
  {
    for (EReference reference : eObject.eClass().getEAllReferences())
    {
      if (reference.isMany())
      {
        List<EObject> list = (List<EObject>)eObject.eGet(reference);
        for (EObject element : list)
        {
          check(referrer, reference, element);
        }
      }
      else
      {
        check(referrer, reference, (EObject)eObject.eGet(reference));
      }
    }
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  private class CDOFeatureDeltaVisitorAutoAttach implements CDOFeatureDeltaVisitor
  {
    private EObject referrer;

    public CDOFeatureDeltaVisitorAutoAttach(EObject referrer)
    {
      this.referrer = referrer;
    }

    public void visit(CDOAddFeatureDelta featureChange)
    {
      persist(referrer, featureChange.getValue());
    }

    public void visit(CDOClearFeatureDelta featureChange)
    {
    }

    public void visit(CDOListFeatureDelta featureChange)
    {
    }

    public void visit(CDOMoveFeatureDelta featureChange)
    {
    }

    public void visit(CDORemoveFeatureDelta featureChange)
    {
    }

    public void visit(CDOSetFeatureDelta featureChange)
    {
      persist(referrer, featureChange.getValue());
    }

    public void visit(CDOUnsetFeatureDelta featureChange)
    {
    }

    public void visit(CDOContainerFeatureDelta featureChange)
    {
    }
  }
}
