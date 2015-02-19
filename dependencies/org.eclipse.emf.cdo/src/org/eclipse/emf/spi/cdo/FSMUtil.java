/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 246705
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.util.LegacyModeNotEnabledException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDOLegacyAdapter;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public final class FSMUtil
{
  private FSMUtil()
  {
  }

  public static boolean isTransient(CDOObject object)
  {
    CDOState state = object.cdoState();
    return state == CDOState.TRANSIENT || state == CDOState.PREPARED;
  }

  public static boolean isInvalid(CDOObject object)
  {
    CDOState state = object.cdoState();
    return state == CDOState.INVALID || state == CDOState.INVALID_CONFLICT;
  }

  public static boolean isConflict(CDOObject object)
  {
    CDOState state = object.cdoState();
    return state == CDOState.CONFLICT || state == CDOState.INVALID_CONFLICT;
  }

  public static boolean isNew(CDOObject object)
  {
    CDOState state = object.cdoState();
    return state == CDOState.NEW;
  }

  /**
   * @since 4.1
   */
  public static boolean isClean(CDOObject object)
  {
    CDOState state = object.cdoState();
    return state == CDOState.CLEAN;
  }

  public static boolean isNative(EObject eObject)
  {
    return eObject instanceof CDOObjectImpl;
  }

  public static InternalCDOObject adapt(Object object, CDOView view)
  {
    if (view.isClosed())
    {
      throw new IllegalStateException(Messages.getString("FSMUtil.0")); //$NON-NLS-1$
    }

    if (object instanceof InternalCDOObject)
    {
      return (InternalCDOObject)object;
    }

    if (object == null)
    {
      throw new IllegalArgumentException(Messages.getString("FSMUtil.1")); //$NON-NLS-1$
    }

    if (object instanceof InternalEObject)
    {
      if (!view.isLegacyModeEnabled())
      {
        throw new LegacyModeNotEnabledException();
      }

      return adaptLegacy((InternalEObject)object);
    }

    return null;
  }

  /*
   * IMPORTANT: Compile errors in this method might indicate an old version of EMF. Legacy support is only enabled for
   * EMF with fixed bug #247130. These compile errors do not affect native models!
   */
  public static InternalCDOObject adaptLegacy(InternalEObject object)
  {
    EList<Adapter> adapters = object.eAdapters();
    CDOLegacyAdapter adapter = getLegacyAdapter(adapters);
    if (adapter == null)
    {
      adapter = new CDOLegacyAdapter(object);
    }

    return adapter;

    // EList<InternalEObject.EReadListener> readListeners = object.eReadListeners();
    // CDOLegacyWrapper wrapper = getLegacyWrapper(readListeners);
    // if (wrapper == null)
    // {
    // wrapper = new CDOLegacyWrapper(object);
    // // TODO Only Load/Attach transitions should actually *add* the wrappers!
    // readListeners.add(0, wrapper);
    // object.eWriteListeners().add(0, wrapper);
    // }
    //
    // return wrapper;
  }

  private static CDOLegacyAdapter getLegacyAdapter(EList<Adapter> adapters)
  {
    return (CDOLegacyAdapter)EcoreUtil.getAdapter(adapters, CDOLegacyAdapter.class);
  }

  public static Iterator<InternalCDOObject> iterator(final Iterator<?> delegate, final InternalCDOView view)
  {
    return new Iterator<InternalCDOObject>()
    {
      private Object next;

      public boolean hasNext()
      {
        if (delegate.hasNext())
        {
          next = adapt(delegate.next(), view);
          return true;
        }

        return false;
      }

      public InternalCDOObject next()
      {
        return (InternalCDOObject)next;
      }

      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }

  public static Iterator<InternalCDOObject> iterator(Collection<?> instances, final InternalCDOView view)
  {
    return iterator(instances.iterator(), view);
  }
}
