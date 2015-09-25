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
package org.eclipse.net4j.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public interface IStreamWrapper
{
  public InputStream wrapInputStream(InputStream in) throws IOException;

  public OutputStream wrapOutputStream(OutputStream out) throws IOException;

  public void finishInputStream(InputStream in) throws IOException;

  public void finishOutputStream(OutputStream out) throws IOException;
}
