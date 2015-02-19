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
package org.eclipse.net4j.internal.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractBundle;
import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.AbstractIterator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Eike Stepper
 */
public class OSGiBundle extends AbstractBundle
{
  public OSGiBundle(AbstractPlatform platform, String bundleID, Class<?> accessor)
  {
    super(platform, bundleID, accessor);
  }

  @Override
  public BundleContext getBundleContext()
  {
    return (BundleContext)super.getBundleContext();
  }

  public URL getBaseURL()
  {
    try
    {
      URL entry = getBundleContext().getBundle().getEntry("/"); //$NON-NLS-1$
      URL baseURL = FileLocator.resolve(entry);
      String str = baseURL.toExternalForm();
      if (str.endsWith("/./")) //$NON-NLS-1$
      {
        baseURL = new URL(str.substring(0, str.length() - 2));
      }

      return baseURL;
    }
    catch (IOException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public Iterator<Class<?>> getClasses()
  {
    final Queue<String> folders = new LinkedList<String>();
    folders.offer("/");

    return new AbstractIterator<Class<?>>()
    {
      private Enumeration<String> entryPaths;

      @Override
      protected Object computeNextElement()
      {
        for (;;)
        {
          while (entryPaths != null && entryPaths.hasMoreElements())
          {
            String entryPath = entryPaths.nextElement();
            if (entryPath.endsWith("/"))
            {
              folders.offer(entryPath);
            }
            else
            {
              Class<?> c = getClassFromBundle(entryPath);
              if (c != null)
              {
                return c;
              }
            }
          }

          String folder = folders.poll();
          if (folder == null)
          {
            return END_OF_DATA;
          }

          Bundle bundle = getBundleContext().getBundle();
          entryPaths = bundle.getEntryPaths(folder);
        }
      }
    };
  }

  public String getStateLocation()
  {
    Bundle bundle = getBundleContext().getBundle();
    return Platform.getStateLocation(bundle).toString();
  }
}
