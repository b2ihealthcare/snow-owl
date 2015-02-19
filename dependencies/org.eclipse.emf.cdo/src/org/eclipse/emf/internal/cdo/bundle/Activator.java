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
package org.eclipse.emf.internal.cdo.bundle;

import org.eclipse.emf.internal.cdo.view.CDOViewProviderRegistryImpl;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OSGiActivator;
import org.eclipse.net4j.util.om.log.OMLogger;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;

import org.osgi.framework.BundleContext;

/**
 * @author Eike Stepper
 */
public final class Activator extends EMFPlugin
{
  // @Singleton
  public static final Activator INSTANCE = new Activator();

  private static Implementation plugin;

  public Activator()
  {
    super(new ResourceLocator[] {});
  }

  @Override
  public ResourceLocator getPluginResourceLocator()
  {
    return plugin;
  }

  public static Implementation getPlugin()
  {
    return plugin;
  }

  /**
   * @author Eike Stepper
   */
  public static class Implementation extends EclipsePlugin
  {
    public Implementation()
    {
      plugin = this;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
      OSGiActivator.traceStart(context);
      if (OM.BUNDLE == null)
      {
        throw new IllegalStateException("bundle == null"); //$NON-NLS-1$
      }

      try
      {
        super.start(context);
        setBundleContext(context);
        doStart();
      }
      catch (Error error)
      {
        OM.BUNDLE.logger().error(error);
        throw error;
      }
      catch (Exception ex)
      {
        OM.BUNDLE.logger().error(ex);
        throw ex;
      }
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
      plugin = null;
      OSGiActivator.traceStop(context);
      if (OM.BUNDLE == null)
      {
        throw new IllegalStateException("bundle == null"); //$NON-NLS-1$
      }

      try
      {
        doStop();
        setBundleContext(null);
        super.stop(context);
      }
      catch (Error error)
      {
        OM.BUNDLE.logger().error(error);
        throw error;
      }
      catch (Exception ex)
      {
        OM.BUNDLE.logger().error(ex);
        throw ex;
      }
    }

    /**
     * @since 2.0
     */
    protected void doStart() throws Exception
    {
      CDOViewProviderRegistryImpl.INSTANCE.activate();
    }

    /**
     * @since 2.0
     */
    protected void doStop() throws Exception
    {
      LifecycleUtil.deactivate(CDOViewProviderRegistryImpl.INSTANCE, OMLogger.Level.WARN);
    }

    @SuppressWarnings("deprecation")
    private void setBundleContext(BundleContext context)
    {
      OM.BUNDLE.setBundleContext(context);
    }
  }
}
