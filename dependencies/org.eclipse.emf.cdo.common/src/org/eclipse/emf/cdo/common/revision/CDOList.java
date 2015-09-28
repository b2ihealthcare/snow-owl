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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.common.util.EList;
import org.eclipse.net4j.util.collection.MoveableList;

/**
 * A {@link MoveableList moveable} {@link EList}.
 * 
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.uses {@link CDOElementProxy} - - contains
 */
public interface CDOList extends MoveableList<Object>, EList<Object>
{
  /**
   * Returns the element at position index of this list and optionally resolves proxies (see CDOElementProxy).
   * <p>
   * 
   * @param index
   *          The position of the element to return from this list.
   * @param resolve
   *          A value of <code>false</code> indicates that {@link CDORevisionUtil#UNINITIALIZED} may be returned for
   *          unresolved elements. A value of <code>true</code> indicates that it should behave identical to
   *          {@link CDOList#get(int)}.
   */
  public Object get(int index, boolean resolve);
}
