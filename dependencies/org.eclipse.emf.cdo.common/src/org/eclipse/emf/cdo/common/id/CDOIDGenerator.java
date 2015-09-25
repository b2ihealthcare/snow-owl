/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.id;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.net4j.util.UUIDGenerator;

/**
 * Generates {@link CDOID IDs}.
 * 
 * @author Eike Stepper
 * @since 4.1
 */
public interface CDOIDGenerator
{
  /**
   * Generates {@link CDOID#NULL NULL} values.
   */
  public static final CDOIDGenerator NULL = new CDOIDGenerator()
  {
    public CDOID generateCDOID(EObject object)
    {
      return CDOID.NULL;
    }

    public void reset()
    {
      // Do nothing
    }
  };

  /**
   * Generates {@link EcoreUtil#generateUUID(byte[]) UUID} values.
   */
  public static final CDOIDGenerator UUID = new CDOIDGenerator()
  {
    public CDOID generateCDOID(EObject object)
    {
      byte[] bytes = new byte[16];
      UUIDGenerator.DEFAULT.generate(bytes);
      return CDOIDUtil.createUUID(bytes);
    }

    public void reset()
    {
      // Do nothing
    }
  };

  /**
   * Generates a {@link CDOID}.
   * 
   * @param object
   *          the object to generate a new CDOID for if available, <code>null</code> otherwise.
   */
  public CDOID generateCDOID(EObject object);

  /**
   * Called at the end of a commit operation to give this ID generator a chance to reset its sequence of IDs.
   */
  public void reset();
}
