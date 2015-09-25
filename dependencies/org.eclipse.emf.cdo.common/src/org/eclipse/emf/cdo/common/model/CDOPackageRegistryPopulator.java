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
package org.eclipse.emf.cdo.common.model;

import java.util.Map.Entry;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.util.concurrent.Worker;

/**
 * Populates a {@link #getTarget() target} package registry by asynchronously polling a {@link #getSource() source}
 * package registry for new {@link EPackage} registrations.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @apiviz.uses {@link CDOPackageRegistryPopulator.Descriptor} - - creates
 * @apiviz.uses {@link CDOPackageRegistry} - - populates
 * @apiviz.uses {@link org.eclipse.emf.ecore.EPackage.Registry} - - polls
 */
public class CDOPackageRegistryPopulator extends Worker
{
  public static final int DEFAULT_SOURCE_POLL_INTERVAL = 5000;

  private long sourcePollInterval = DEFAULT_SOURCE_POLL_INTERVAL;

  private EPackage.Registry source;

  private CDOPackageRegistry target;

  public CDOPackageRegistryPopulator(CDOPackageRegistry target)
  {
    this(EPackage.Registry.INSTANCE, target);
  }

  public CDOPackageRegistryPopulator(EPackage.Registry source, CDOPackageRegistry target)
  {
    this.source = source;
    this.target = target;
  }

  public EPackage.Registry getSource()
  {
    return source;
  }

  public CDOPackageRegistry getTarget()
  {
    return target;
  }

  public long getSourcePollInterval()
  {
    return sourcePollInterval;
  }

  public void setSourcePollInterval(long sourcePollInterval)
  {
    this.sourcePollInterval = sourcePollInterval;
  }

  @Override
  protected void work(WorkContext context) throws Exception
  {
    doWork();
    context.nextWork(getSourcePollInterval());
  }

  protected void doWork()
  {
    populate(getSource(), getTarget());
  }

  @Override
  protected void doActivate() throws Exception
  {
    doWork();
    super.doActivate();
  }

  public static boolean populate(CDOPackageRegistry target)
  {
    return populate(EPackage.Registry.INSTANCE, target);
  }

  public static boolean populate(EPackage.Registry source, CDOPackageRegistry target)
  {
    boolean populated = false;
    while (populateFirstMatch(source, target))
    {
      populated = true;
    }

    return populated;
  }

  private static boolean populateFirstMatch(EPackage.Registry source, CDOPackageRegistry target)
  {
    for (Entry<String, Object> entry : source.entrySet())
    {
      String nsURI = entry.getKey();
      if (!target.containsKey(nsURI))
      {
        target.put(nsURI, new Descriptor(source, nsURI));
        return true;
      }
    }

    return false;
  }

  /**
   * A package {@link org.eclipse.emf.ecore.EPackage.Descriptor descriptor} that resolves {@link EPackage packages} from
   * a {@link #getSource() source } package registry.
   * 
   * @author Eike Stepper
   */
  public static class Descriptor implements EPackage.Descriptor
  {
    private EPackage.Registry source;

    private String nsURI;

    public Descriptor(EPackage.Registry source, String nsURI)
    {
      this.source = source;
      this.nsURI = nsURI;
    }

    public EPackage.Registry getSource()
    {
      return source;
    }

    public String getNsURI()
    {
      return nsURI;
    }

    public EFactory getEFactory()
    {
      return source.getEFactory(nsURI);
    }

    public EPackage getEPackage()
    {
      return source.getEPackage(nsURI);
    }
  }
}
