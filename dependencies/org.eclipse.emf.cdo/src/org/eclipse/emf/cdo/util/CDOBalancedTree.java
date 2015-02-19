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

import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A utility class that attaches {@link EObject objects} to a {@link CDOResourceFolder folder} rooted, balanced tree such that configurable
 * capacities for resources per folder and objects per resource are never exceeded. This class is useful if a large number of objects
 * does not form a tree naturally but long lists would hinder fast lazy loading.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public class CDOBalancedTree
{
  public static final int DEFAULT_CAPACITY = 20;

  public static final int DEFAULT_LOCK_TIMEOUT = 1000;

  private static final boolean TRACE = false;

  private final CDOResourceFolder root;

  private final int folderCapacity;

  private final int resourceCapacity;

  private int lockAttempts;

  private long lockTimeout = DEFAULT_LOCK_TIMEOUT;

  public CDOBalancedTree(CDOResourceFolder root, int folderCapacity, int resourceCapacity)
  {
    this.root = root;
    this.folderCapacity = folderCapacity;
    this.resourceCapacity = resourceCapacity;
  }

  public CDOBalancedTree(CDOResourceFolder root, int nodeCapacity)
  {
    this(root, nodeCapacity, nodeCapacity);
  }

  public CDOBalancedTree(CDOResourceFolder root)
  {
    this(root, DEFAULT_CAPACITY);
  }

  public final CDOResourceFolder getRoot()
  {
    return root;
  }

  public final int getFolderCapacity()
  {
    return folderCapacity;
  }

  public final int getResourceCapacity()
  {
    return resourceCapacity;
  }

  public final int getLockAttempts()
  {
    return lockAttempts;
  }

  public final void setLockAttempts(int lockAttempts)
  {
    this.lockAttempts = lockAttempts;
  }

  public final long getLockTimeout()
  {
    return lockTimeout;
  }

  public final void setLockTimeout(long lockTimeout)
  {
    this.lockTimeout = lockTimeout;
  }

  public void addObject(EObject object)
  {
    if (lockAttempts == 0)
    {
      addObjectToRoot(object);
      return;
    }

    int attempts = lockAttempts;
    while (attempts-- != 0)
    {
      try
      {
        root.cdoWriteLock().lock(lockTimeout);
        addObjectToRoot(object);
        return;
      }
      catch (Exception ex)
      {
        // Try again, if not all attempts have been made
      }
    }

    throw new CDOException("Unable to aquire write lock on balanced tree " + root.getPath());
  }

  private void addObjectToRoot(EObject object)
  {
    CDOResource firstResource = null;

    Queue<CDOResourceFolder> folders = new LinkedList<CDOResourceFolder>();
    CDOResourceFolder folder = root;
    while (folder != null)
    {
      EList<CDOResourceNode> nodes = folder.getNodes();
      for (CDOResourceNode node : nodes)
      {
        if (node instanceof CDOResourceFolder)
        {
          folders.offer((CDOResourceFolder)node);
        }
        else if (node instanceof CDOResource)
        {
          if (firstResource == null)
          {
            firstResource = (CDOResource)node;
          }

          if (addObjectToResource(object, (CDOResource)node))
          {
            return;
          }
        }
      }

      int size = nodes.size();
      if (size < folderCapacity)
      {
        String name = getResourceName(size + 1);
        CDOResource resource = folder.addResource(name);

        if (TRACE)
        {
          IOUtil.OUT().println("Added resource " + resource.getPath());
        }

        addObjectToResource(object, resource);
        return;
      }

      folder = folders.poll();
    }

    CDOResource resource = addObjectWithSplit(firstResource);
    addObjectToResource(object, resource);
  }

  private boolean addObjectToResource(EObject object, CDOResource resource)
  {
    EList<EObject> contents = resource.getContents();
    if (contents.size() < resourceCapacity)
    {
      contents.add(object);
      if (TRACE)
      {
        IOUtil.OUT().println("Added object to resource " + resource.getPath());
      }

      return true;
    }

    return false;
  }

  private CDOResource addObjectWithSplit(CDOResource resource)
  {
    String path = resource.getPath();
    String name = resource.getName();
    resource.setName("_" + name);

    CDOResourceFolder splitFolder = resource.getFolder().addResourceFolder(name);
    splitFolder.getNodes().add(resource);
    resource.setName(getResourceName(1));
    if (TRACE)
    {
      IOUtil.OUT().println("Moved resource " + path + " to " + resource.getPath());
    }

    return splitFolder.addResource(getResourceName(2));
  }

  private String getResourceName(int n)
  {
    return Integer.toString(n);
  }
}
