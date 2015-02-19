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

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface StringIO
{
  public static final StringIO DIRECT = new StringIO()
  {
    public void write(ExtendedDataOutput out, String string) throws IOException
    {
      out.writeString(string);
    }

    public String read(ExtendedDataInput in) throws IOException
    {
      return in.readString();
    }

    @Override
    public String toString()
    {
      return "DIRECT"; //$NON-NLS-1$
    }
  };

  public void write(ExtendedDataOutput out, String string) throws IOException;

  public String read(ExtendedDataInput in) throws IOException;
}
