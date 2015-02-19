/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fluegge - EMap support
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class DynamicCDOObjectImpl extends CDOObjectImpl
{
  private EClass eClass;

  public DynamicCDOObjectImpl(EClass eClass)
  {
    eSetClass(eClass);
  }

  @Override
  public EClass eClass()
  {
    return eClass;
  }

  @Override
  public void eSetClass(EClass eClass)
  {
    this.eClass = eClass;
  }

  @Override
  protected EClass eDynamicClass()
  {
    return eClass;
  }

  /**
   * @author Martin Fluegge
   * @since 3.0
   */
  public static final class BasicEMapEntry<K, V> extends DynamicCDOObjectImpl implements BasicEMap.Entry<K, V>
  {
    protected int hash = -1;

    protected EStructuralFeature keyFeature;

    protected EStructuralFeature valueFeature;

    /**
     * Creates a dynamic EObject.
     */
    public BasicEMapEntry(EClass eClass)
    {
      super(eClass);
    }

    @SuppressWarnings("unchecked")
    public K getKey()
    {
      return (K)eGet(keyFeature);
    }

    public void setKey(Object key)
    {
      eSet(keyFeature, key);
    }

    public int getHash()
    {
      if (hash == -1)
      {
        Object theKey = getKey();
        hash = theKey == null ? 0 : theKey.hashCode();
      }

      return hash;
    }

    public void setHash(int hash)
    {
      this.hash = hash;
    }

    @SuppressWarnings("unchecked")
    public V getValue()
    {
      return (V)eGet(valueFeature);
    }

    public V setValue(V value)
    {
      @SuppressWarnings("unchecked")
      V result = (V)eGet(valueFeature);
      eSet(valueFeature, value);
      return result;
    }

    @Override
    public void eSetClass(EClass eClass)
    {
      super.eSetClass(eClass);
      keyFeature = eClass.getEStructuralFeature("key");
      valueFeature = eClass.getEStructuralFeature("value");
    }
  }
}
