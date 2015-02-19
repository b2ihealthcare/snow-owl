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
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class XOROutputStream extends DelegatingOutputStream
{
  private int[] key;

  private int index;

  public XOROutputStream(OutputStream out, int... key)
  {
    super(out);
    this.key = key;
  }

  public int[] getKey()
  {
    return key;
  }

  @Override
  public void write(int b) throws IOException
  {
    if (key != null && key.length != 0)
    {
      if (index == key.length)
      {
        index = 0;
      }

      b = b & 0xFF ^ key[index++] & 0xFF;
    }

    super.write(b);
  }
}
