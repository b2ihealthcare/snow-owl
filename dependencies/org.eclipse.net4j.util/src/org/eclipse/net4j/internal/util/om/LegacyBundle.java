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
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Eike Stepper
 */
public class LegacyBundle extends AbstractBundle
{
  private URL baseURL;

  public LegacyBundle(AbstractPlatform platform, String bundleID, Class<?> accessor)
  {
    super(platform, bundleID, accessor);

    try
    {
      computeBaseURL(accessor);
      if (baseURL == null)
      {
        throw new IllegalStateException("No base URL");
      }

      loadOptions();
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }
  }

  public String getStateLocation()
  {
    return new File(getPlatform().getStateFolder(), ".plugins/" + getBundleID()).getAbsolutePath(); //$NON-NLS-1$
  }

  public URL getBaseURL()
  {
    return baseURL;
  }

  public Iterator<Class<?>> getClasses()
  {
    List<Class<?>> result = new ArrayList<Class<?>>();

    if (isArchiveProtocol(baseURL.getProtocol()))
    {
      JarFile jarFile = null;

      try
      {
        jarFile = new JarFile(baseURL.getFile());

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements())
        {
          JarEntry jarEntry = entries.nextElement();
          if (!jarEntry.isDirectory())
          {
            Class<?> c = getClassFromBundle(jarEntry.getName());
            if (c != null)
            {
              result.add(c);
            }
          }
        }
      }
      catch (IOException ex)
      {
        throw WrappedException.wrap(ex);
      }
      finally
      {
        if (jarFile != null)
        {
          try
          {
            jarFile.close();
          }
          catch (IOException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      }
    }
    else
    {
      try
      {
        URL url = getClassesURL(getAccessor());
        File folder = new File(url.getFile());
        collectFileClasses(folder, null, result);
      }
      catch (MalformedURLException ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    return result.iterator();
  }

  private void collectFileClasses(File folder, String path, List<Class<?>> result)
  {
    File file = folder;
    if (path == null)
    {
      path = "";
    }
    else
    {
      file = new File(folder, path);
    }

    if (file.isDirectory())
    {
      for (String child : file.list())
      {
        collectFileClasses(folder, path + "/" + child, result);
      }
    }
    else
    {
      Class<?> c = getClassFromBundle(path);
      if (c != null)
      {
        result.add(c);
      }
    }
  }

  private void loadOptions()
  {
    InputStream inputStream = null;
    Properties properties = new Properties();

    try
    {
      final String prefix = getBundleID() + "/"; //$NON-NLS-1$
      final int length = prefix.length();

      inputStream = getInputStream(".options"); //$NON-NLS-1$
      properties.load(inputStream);

      for (Entry<Object, Object> entry : properties.entrySet())
      {
        try
        {
          String key = (String)entry.getKey();
          if (key.startsWith(prefix))
          {
            String value = (String)entry.getValue();
            setDebugOption(key.substring(length), value);
          }
        }
        catch (RuntimeException ignore)
        {
        }
      }
    }
    catch (IOException ignore)
    {
    }
    finally
    {
      IOUtil.closeSilent(inputStream);
    }
  }

  private void computeBaseURL(Class<?> accessor) throws MalformedURLException
  {
    // Determine the URL for the class itself. The URL will be of one of the
    // following forms, so there are a few good places to consider looking for
    // the plugin.properties.
    //
    // For a plugin.xml with runtime="common.jar":
    // jar:file:/D:/sandbox/unpackage1-3.1M7/eclipse/plugins/org.eclipse.emf.common/common.jar!/org/eclipse/common/
    // CommonPlugin.class
    //
    // For a plugin.xml with runtime="runtime/common.jar":
    // jar:file:/D:/sandbox/unpackage1-3.1M7/eclipse/plugins/org.eclipse.emf.common/runtime/common.jar!/org/eclipse/common
    // /CommonPlugin.class
    //
    // For a plugin.xml with runtime="." where the plugin is jarred:
    // jar:file:/D:/sandbox/unpackage1-3.1M7/eclipse/plugins/org.eclipse.emf.common.jar!/org/eclipse/common/CommonPlugin.
    // class
    //
    // For a plugin.xml with runtime="." where the plugin is not jarred.
    // file:/D:/sandbox/unpackage1-3.1M7/eclipse/plugins/org.eclipse.emf.common/org/eclipse/emf/common/CommonPlugin.class
    //
    // Running in PDE with bin on classpath:
    // file:/D:/sandbox/unpackage1-3.1M7/eclipse/plugins/org.eclipse.emf.common/bin/org/eclipse/emf/common/CommonPlugin.
    // class

    URL url = getClassesURL(accessor);

    // For an archive URI, check for the plugin.properties in the archive.
    if (isArchiveProtocol(url.getProtocol()))
    {
      try
      {
        // If we can open an input stream, then the plugin.properties is there,
        // and we have a good base URL.
        URL u = new URL(url.toString() + "plugin.properties");
        InputStream inputStream = null;

        try
        {
          inputStream = u.openStream();
        }
        finally
        {
          IOUtil.close(inputStream);
        }

        baseURL = url;
      }
      catch (IOException exception)
      {
        // If the plugin.properties isn't within the root of the archive, create
        // a new URI for the folder location of the archive, so we can look in
        // the folder that contains it.
        url = trimSegments(new URL(url.getFile()), 1);
      }
    }

    // If we didn't find the plugin.properties in the usual place nor in the
    // archive...
    if (baseURL == null)
    {
      // Trim off the "bin" or "runtime" segment.
      String lastSegment = lastSegment(url);
      if ("bin".equals(lastSegment) || "runtime".equals(lastSegment)) //$NON-NLS-1$ //$NON-NLS-2$
      {
        url = trimSegments(url, 1);
      }

      try
      {
        // If we can open an input stream, then the plugin.properties is in the
        // folder, and we have a good base URL.
        InputStream inputStream = new URL(url.toString() + "plugin.properties").openStream(); //$NON-NLS-1$
        inputStream.close();
        baseURL = url;
      }
      catch (IOException exception)
      {
      }
    }

    // If we still don't have a good base URL, complain about it.
    if (baseURL == null)
    {
      throw new MissingResourceException("Missing properties: " + accessor.getName(), accessor.getName(), //$NON-NLS-1$
          "plugin.properties"); //$NON-NLS-1$
    }
  }

  private static URL getClassesURL(Class<?> accessor) throws MalformedURLException
  {
    String className = accessor.getName();
    URL url = accessor.getResource(ReflectUtil.getSimpleName(accessor) + ".class"); //$NON-NLS-1$

    int segmentsToTrim = 1 + StringUtil.occurrences(className, '.');
    return trimSegments(url, segmentsToTrim);
  }

  private static String lastSegment(URL url)
  {
    String path = url.getPath();
    if (path.endsWith("/")) //$NON-NLS-1$
    {
      path = path.substring(0, path.length() - 1);
    }

    int pos = path.lastIndexOf('/');
    return pos == -1 ? path : path.substring(pos + 1);
  }

  private static URL trimSegments(URL url, int count) throws MalformedURLException
  {
    String path = url.getPath();
    if (path.endsWith("/")) //$NON-NLS-1$
    {
      ++count;
    }

    for (int i = 0; i < count; i++)
    {
      int pos = path.lastIndexOf('/');
      if (pos == -1)
      {
        break;
      }

      // TODO Optimize
      path = path.substring(0, pos);
    }

    return new URL(url.getProtocol() + ":" + path + "/"); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * TODO Make configurable
   * 
   * @see org.eclipse.emf.common.util.URI#isArchiveScheme(String)
   */
  private static boolean isArchiveProtocol(String scheme)
  {
    return "jar".equalsIgnoreCase(scheme); //$NON-NLS-1$
  }
}
