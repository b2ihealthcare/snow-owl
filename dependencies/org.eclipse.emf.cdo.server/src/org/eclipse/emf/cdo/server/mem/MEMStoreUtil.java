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
package org.eclipse.emf.cdo.server.mem;

import org.eclipse.emf.cdo.internal.server.mem.MEMStore;

/**
 * Creates {@link IMEMStore} instances.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public final class MEMStoreUtil
{
  private MEMStoreUtil()
  {
  }

  /**
   * Creates a {@link IMEMStore} instance.
   * 
   * @since 4.0
   */
  public static IMEMStore createMEMStore()
  {
    return new MEMStore();
  }
}
