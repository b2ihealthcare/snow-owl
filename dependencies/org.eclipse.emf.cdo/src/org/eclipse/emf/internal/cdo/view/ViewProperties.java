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
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class ViewProperties extends Properties<CDOView>
{
  public static final IProperties<CDOView> INSTANCE = new ViewProperties();

  private static final String CATEGORY_VIEW = "View"; //$NON-NLS-1$

  private static final String CATEGORY_SESSION = "Session"; //$NON-NLS-1$

  private ViewProperties()
  {
    super(CDOView.class);

    add(new Property<CDOView>("open", //$NON-NLS-1$
        "Open", "Whether this view is open or not.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return !view.isClosed();
      }
    });

    add(new Property<CDOView>("viewID", //$NON-NLS-1$
        "ID", "The ID of this view.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getViewID();
      }
    });

    add(new Property<CDOView>("branchName") //$NON-NLS-1$
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getBranch().getName();
      }
    });

    add(new Property<CDOView>("branch", //$NON-NLS-1$
        "Branch", "The branch of this view.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getBranch().getPathName();
      }
    });

    add(new Property<CDOView>("timeStamp", //$NON-NLS-1$
        "Time Stamp", "The time stamp of this view.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return CDOCommonUtil.formatTimeStamp(view.getTimeStamp());
      }
    });

    add(new Property<CDOView>("lastUpdateTime", //$NON-NLS-1$
        "Last Update", "The time stamp of the last passive update.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return CDOCommonUtil.formatTimeStamp(view.getLastUpdateTime());
      }
    });

    add(new Property<CDOView>("readOnly", //$NON-NLS-1$
        "Read-Only", "Whether this view is read-only or not.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.isReadOnly();
      }
    });

    add(new Property<CDOView>("dirty", //$NON-NLS-1$
        "Dirty", "Whether this view is dirty or not.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.isDirty();
      }
    });

    add(new Property<CDOView>("durable", //$NON-NLS-1$
        "Durable", "Whether this view is durable or not.", CATEGORY_VIEW)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getDurableLockingID() != null;
      }
    });

    add(new Property<CDOView>("sessionID", //$NON-NLS-1$
        "Session ID", "The ID of the session of this view.", CATEGORY_SESSION)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getSession().getSessionID();
      }
    });

    add(new Property<CDOView>("userID", //$NON-NLS-1$
        "User ID", "The user ID of the session of this view.", CATEGORY_SESSION)
    {
      @Override
      protected Object eval(CDOView view)
      {
        return view.getSession().getUserID();
      }
    });
  }

  /**
   *
   */
  public static void main(String[] args)
  {
    new Tester().dumpContributionMarkup();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Tester extends DefaultPropertyTester<CDOView>
  {
    public static final String NAMESPACE = "org.eclipse.emf.cdo.view";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
