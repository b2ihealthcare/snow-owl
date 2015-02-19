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
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.view.CDOView;

/**
 * An unchecked exception being thrown if <i><a href="http://wiki.eclipse.org/CDO_Legacy_Mode">legacy objects</a></i>
 * are to be accessed and the associated {@link CDOView view} is not in {@link CDOView#isLegacyModeEnabled() legacy
 * mode}.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class LegacyModeNotEnabledException extends IllegalStateException
{
  private static final long serialVersionUID = 1L;

  public LegacyModeNotEnabledException()
  {
    this("Legacy mode is not enabled");
  }

  public LegacyModeNotEnabledException(String s)
  {
    super(s);
  }

  public LegacyModeNotEnabledException(Throwable cause)
  {
    super(cause);
  }

  public LegacyModeNotEnabledException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
