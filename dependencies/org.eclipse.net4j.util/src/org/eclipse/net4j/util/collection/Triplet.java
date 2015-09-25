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
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Caspar De Groot
 * @since 3.0
 */
public class Triplet<T1, T2, T3> extends Pair<T1, T2>
{
  private T3 element3;

  public Triplet()
  {
  }

  public Triplet(T1 element1, T2 element2, T3 element3)
  {
    super(element1, element2);
    this.element3 = element3;
  }

  public Triplet(Triplet<T1, T2, T3> source)
  {
    super(source.getElement1(), source.getElement2());
    element3 = source.element3;
  }

  public final T3 getElement3()
  {
    return element3;
  }

  public void setElement3(T3 element3)
  {
    this.element3 = element3;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj instanceof Triplet<?, ?, ?>)
    {
      Triplet<?, ?, ?> that = (Triplet<?, ?, ?>)obj;
      return ObjectUtil.equals(getElement1(), that.getElement1()) //
          && ObjectUtil.equals(getElement2(), that.getElement2()) //
          && ObjectUtil.equals(element3, that.element3);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(getElement1()) ^ ObjectUtil.hashCode(getElement2()) ^ ObjectUtil.hashCode(element3);
  }

  @Override
  public String toString()
  {
    return "Triplet[" + getElement1() + ", " + getElement2() + ", " + element3 + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }
}
