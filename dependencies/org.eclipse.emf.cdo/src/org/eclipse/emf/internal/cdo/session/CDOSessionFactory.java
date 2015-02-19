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
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.Factory;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class CDOSessionFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.sessions"; //$NON-NLS-1$

  private static final String TRUE = Boolean.TRUE.toString();

  public CDOSessionFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  public CDOSession create(String description)
  {
    try
    {
      URI uri = new URI(description);
      String query = uri.getQuery();
      if (StringUtil.isEmpty(query))
      {
        throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOSessionFactory.1"), description)); //$NON-NLS-1$
      }

      Map<String, String> result = CDOURIUtil.getParameters(query);
      String repositoryName = result.get("repositoryName"); //$NON-NLS-1$
      if (repositoryName == null)
      {
        throw new IllegalArgumentException("Repository name is missing: " + description);
      }

      boolean automaticPackageRegistry = TRUE.equals(result.get("automaticPackageRegistry")); //$NON-NLS-1$
      return createSession(repositoryName, automaticPackageRegistry);
    }
    catch (URISyntaxException ex)
    {
      throw new IllegalArgumentException(ex);
    }
  }

  /**
   * @since 2.0
   */
  protected abstract InternalCDOSession createSession(String repositoryName, boolean automaticPackageRegistry);
}
