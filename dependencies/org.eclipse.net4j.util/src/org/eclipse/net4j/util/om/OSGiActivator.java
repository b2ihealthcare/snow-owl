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
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractBundle;
import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.internal.util.om.OSGiBundle;
import org.eclipse.net4j.util.io.IOUtil;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

/**
 * A default implementation of an OSGi {@link BundleActivator bundle activator} for OM {@link OMBundle bundles}.
 * 
 * @author Eike Stepper
 */
public abstract class OSGiActivator implements BundleActivator
{
  private OMBundle omBundle;

  /**
   * @since 2.0
   */
  protected BundleContext bundleContext;

  public OSGiActivator(OMBundle omBundle)
  {
    this.omBundle = omBundle;
  }

  public final OMBundle getOMBundle()
  {
    return omBundle;
  }

  public final void start(BundleContext context) throws Exception
  {
    bundleContext = context;
    OSGiActivator.traceStart(context);
    if (omBundle == null)
    {
      throw new IllegalStateException("bundle == null"); //$NON-NLS-1$
    }

    try
    {
      setBundleContext(context);
      ((AbstractBundle)omBundle).start();
      doStart();
    }
    catch (Error error)
    {
      omBundle.logger().error(error);
      throw error;
    }
    catch (Exception ex)
    {
      omBundle.logger().error(ex);
      throw ex;
    }
  }

  public final void stop(BundleContext context) throws Exception
  {
    OSGiActivator.traceStop(context);
    if (omBundle == null)
    {
      throw new IllegalStateException("bundle == null"); //$NON-NLS-1$
    }

    try
    {
      doStop();
      ((AbstractBundle)omBundle).stop();
      setBundleContext(null);
    }
    catch (Error error)
    {
      omBundle.logger().error(error);
      throw error;
    }
    catch (Exception ex)
    {
      omBundle.logger().error(ex);
      throw ex;
    }
  }

  @Override
  public final boolean equals(Object obj)
  {
    return super.equals(obj);
  }

  @Override
  public final int hashCode()
  {
    return super.hashCode();
  }

  @Override
  public final String toString()
  {
    return super.toString();
  }

  @Override
  protected final Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  @Override
  protected final void finalize() throws Throwable
  {
    super.finalize();
  }

  /**
   * @since 2.0
   */
  protected void doStart() throws Exception
  {
  }

  /**
   * @since 2.0
   */
  protected void doStop() throws Exception
  {
  }

  @SuppressWarnings("deprecation")
  private void setBundleContext(BundleContext context)
  {
    omBundle.setBundleContext(context);
  }

  /**
   * @since 2.0
   */
  public static void traceStart(BundleContext context)
  {
    try
    {
      if (OM.TRACER.isEnabled())
      {
        OM.TRACER.format("Starting bundle {0}", context.getBundle().getSymbolicName()); //$NON-NLS-1$
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  /**
   * @since 2.0
   */
  public static void traceStop(BundleContext context)
  {
    try
    {
      if (OM.TRACER.isEnabled())
      {
        OM.TRACER.format("Stopping bundle {0}", context.getBundle().getSymbolicName()); //$NON-NLS-1$
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  /**
   * Saves and loads {@link OMBundle bundle} state.
   * 
   * @author Eike Stepper
   * @since 3.1
   */
  public static abstract class StateHandler
  {
    private OSGiBundle bundle;

    public StateHandler(OMBundle bundle)
    {
      this.bundle = (OSGiBundle)bundle;
    }

    public final void start() throws Exception
    {
      Object state = null;
      File stateFile = getStateFile();
      if (stateFile.exists())
      {
        FileInputStream fis = null;

        try
        {
          fis = new FileInputStream(stateFile);
          ObjectInputStream ois = new ObjectInputStream(fis)
          {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
            {
              String className = desc.getName();
              return bundle.getAccessor().getClassLoader().loadClass(className);
            }
          };

          state = ois.readObject();
          IOUtil.close(ois);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
          IOUtil.close(fis);
          fis = null;
          stateFile.delete();
        }
        finally
        {
          IOUtil.close(fis);
        }
      }

      startWithState(state);
    }

    public final void stop() throws Exception
    {
      FileOutputStream fos = null;

      try
      {
        Object state = stopWithState();

        File stateFile = getStateFile();
        fos = new FileOutputStream(stateFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(state);
        IOUtil.close(oos);
      }
      finally
      {
        IOUtil.close(fos);
      }
    }

    private File getStateFile()
    {
      return new File(bundle.getStateLocation(), "state.bin");
    }

    protected abstract void startWithState(Object state) throws Exception;

    protected abstract Object stopWithState() throws Exception;
  }

  /**
   * An OSGi {@link OSGiActivator activator} that stores {@link StateHandler bundle state} between sessions.
   * 
   * @author Eike Stepper
   * @since 3.1
   * @apiviz.has {@link OSGiActivator.StateHandler} oneway - - stateHandler
   */
  public static abstract class WithState extends OSGiActivator
  {
    private StateHandler handler = new StateHandler(getOMBundle())
    {
      @Override
      protected void startWithState(Object state) throws Exception
      {
        doStartWithState(state);
      }

      @Override
      protected Object stopWithState() throws Exception
      {
        return doStopWithState();
      }
    };

    public WithState(OMBundle bundle)
    {
      super(bundle);
    }

    @Override
    protected final void doStart() throws Exception
    {
      handler.start();
    }

    @Override
    protected final void doStop() throws Exception
    {
      handler.stop();
    }

    protected abstract void doStartWithState(Object state) throws Exception;

    protected abstract Object doStopWithState() throws Exception;
  }
}
