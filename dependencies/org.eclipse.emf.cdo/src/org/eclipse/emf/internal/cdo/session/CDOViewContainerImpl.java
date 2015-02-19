/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewContainer;

import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class CDOViewContainerImpl extends Container<CDOView> implements CDOViewContainer
{
  private Set<InternalCDOView> views = new HashSet<InternalCDOView>();

  @ExcludeFromDump
  private int lastViewID;

  public CDOViewContainerImpl()
  {
  }

  public CDOView getView(int viewID)
  {
    checkActive();
    for (InternalCDOView view : getViews())
    {
      if (view.getViewID() == viewID)
      {
        return view;
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public InternalCDOView[] getViews()
  {
    checkActive();
    synchronized (views)
    {
      return views.toArray(new InternalCDOView[views.size()]);
    }
  }

  public CDOView[] getElements()
  {
    return getViews();
  }

  @Override
  public boolean isEmpty()
  {
    checkActive();
    return views.isEmpty();
  }

  public CDOView openView(CDOBranchPoint target, ResourceSet resourceSet)
  {
    return openView(target.getBranch(), target.getTimeStamp(), resourceSet);
  }

  public CDOView openView(CDOBranchPoint target)
  {
    return openView(target, createResourceSet());
  }

  public InternalCDOView openView(CDOBranch branch, long timeStamp, ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOView view = createView(branch, timeStamp);
    initView(view, resourceSet);
    return view;
  }

  public InternalCDOView openView(CDOBranch branch, long timeStamp)
  {
    return openView(branch, timeStamp, createResourceSet());
  }

  public InternalCDOView openView(CDOBranch branch)
  {
    return openView(branch, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public InternalCDOView openView(long timeStamp)
  {
    return openView(getMainBranch(), timeStamp);
  }

  public InternalCDOView openView(ResourceSet resourceSet)
  {
    return openView(getMainBranch(), CDOBranchPoint.UNSPECIFIED_DATE, resourceSet);
  }

  /**
   * @since 2.0
   */
  public InternalCDOView openView()
  {
    return openView(CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public CDOView openView(String durableLockingID)
  {
    return openView(durableLockingID, createResourceSet());
  }

  public CDOView openView(String durableLockingID, ResourceSet resourceSet)
  {
    checkActive();
    InternalCDOView view = createView(durableLockingID);
    initView(view, resourceSet);
    return view;
  }

  /**
   * @since 2.0
   */
  public void viewDetached(InternalCDOView view)
  {
    // Detach viewset from the view
    view.getViewSet().remove(view);
    synchronized (views)
    {
      if (!views.remove(view))
      {
        return;
      }
    }

    if (isActive())
    {
      try
      {
        LifecycleUtil.deactivate(view);
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    fireElementRemovedEvent(view);
  }

  /**
   * @since 2.0
   */
  protected InternalCDOView createView(CDOBranch branch, long timeStamp)
  {
    return new CDOViewImpl(branch, timeStamp);
  }

  /**
   * @since 4.0
   */
  protected InternalCDOView createView(String durableLockingID)
  {
    return new CDOViewImpl(durableLockingID);
  }

  protected ResourceSet createResourceSet()
  {
    return new ResourceSetImpl();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (InternalCDOView view : views.toArray(new InternalCDOView[views.size()]))
    {
      try
      {
        view.close();
      }
      catch (RuntimeException ignore)
      {
      }
    }

    views.clear();
    super.doDeactivate();
  }

  /**
   * @since 2.0
   */
  protected void initView(InternalCDOView view, ResourceSet resourceSet)
  {
    InternalCDOViewSet viewSet = SessionUtil.prepareResourceSet(resourceSet);
    synchronized (views)
    {
      view.setViewID(++lastViewID);
      initViewSynced(view);
      views.add(view);
    }

    // Link ViewSet with View
    view.setViewSet(viewSet);
    viewSet.add(view);

    try
    {
      view.activate();
      fireElementAddedEvent(view);
    }
    catch (RuntimeException ex)
    {
      synchronized (views)
      {
        views.remove(view);
      }

      viewSet.remove(view);
      throw ex;
    }
  }

  protected void initViewSynced(InternalCDOView view)
  {
  }

  protected abstract CDOBranch getMainBranch();
}
