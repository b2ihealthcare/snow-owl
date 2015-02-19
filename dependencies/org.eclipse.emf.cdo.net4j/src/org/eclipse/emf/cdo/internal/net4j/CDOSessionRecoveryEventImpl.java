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
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.net4j.CDOSessionRecoveryEvent;
import org.eclipse.emf.cdo.session.CDOSession;

/**
 * @author Caspar De Groot
 */
public class CDOSessionRecoveryEventImpl implements CDOSessionRecoveryEvent
{
  private CDOSession source;

  private Type type;

  public CDOSessionRecoveryEventImpl(CDOSession source, Type type)
  {
    this.type = type;
    this.source = source;
  }

  public CDOSession getSource()
  {
    return source;
  }

  public Type getType()
  {
    return type;
  }
}
