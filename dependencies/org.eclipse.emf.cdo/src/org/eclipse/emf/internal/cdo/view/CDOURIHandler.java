/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOURIHandler implements URIHandler
{
  private static final String CDO_URI_SCHEME = "cdo"; //$NON-NLS-1$

  private InternalCDOView view;

  public CDOURIHandler(InternalCDOView view)
  {
    this.view = view;
  }

  public InternalCDOView getView()
  {
    return view;
  }

  public boolean canHandle(URI uri)
  {
    if (CDO_URI_SCHEME.equals(uri.scheme()))
    {
      String uuid = view.getSession().getRepositoryInfo().getUUID();
      return uuid.equals(CDOURIUtil.extractRepositoryUUID(uri));
    }

    CDOViewProvider[] viewProviders = CDOViewProviderRegistry.INSTANCE.getViewProviders(uri);
    return viewProviders.length != 0;
  }

  public boolean exists(URI uri, Map<?, ?> options)
  {
    return view.hasResource(CDOURIUtil.extractResourcePath(uri));
  }

  public void delete(URI uri, Map<?, ?> options) throws IOException
  {
    String path = CDOURIUtil.extractResourcePath(uri);
    CDOTransaction transaction = null;

    try
    {
      transaction = view.getSession().openTransaction();
      CDOResourceNode node = transaction.getResourceNode(path);
      node.delete(options);
      transaction.commit();
    }
    catch (Exception ex)
    {
      IOException ioException = new IOException(ex.getMessage());
      ioException.initCause(ex);
      throw ioException;
    }
    finally
    {
      IOUtil.closeSilent(transaction);
    }
  }

  public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException
  {
    throw new IOException("CDOURIHandler.createInputStream() not implemented"); //$NON-NLS-1$
  }

  public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException
  {
    throw new IOException("CDOURIHandler.createOutputStream() not implemented"); //$NON-NLS-1$
  }

  public Map<String, ?> contentDescription(URI uri, Map<?, ?> options) throws IOException
  {
    // ViK: I hardly find this useful for CDO. ContentHandler defines, for instance, VALIDITY_PROPERTY
    // It might make sense in CDO... We could also introduce some CDO-Specific information here...
    return ContentHandler.INVALID_CONTENT_DESCRIPTION;
  }

  @SuppressWarnings("unchecked")
  public Map<String, ?> getAttributes(URI uri, Map<?, ?> options)
  {
    Map<String, Object> result = new HashMap<String, Object>();
    String path = CDOURIUtil.extractResourcePath(uri);
    CDOResourceNode node = view.getResourceNode(path);
    if (node != null)
    {
      Set<String> requestedAttributes = (Set<String>)options.get(URIConverter.OPTION_REQUESTED_ATTRIBUTES);
      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_TIME_STAMP))
      {
        long stamp = node instanceof CDOResource ? ((CDOResource)node).getTimeStamp() : URIConverter.NULL_TIME_STAMP;
        result.put(URIConverter.ATTRIBUTE_TIME_STAMP, stamp);
      }

      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_DIRECTORY))
      {
        result.put(URIConverter.ATTRIBUTE_DIRECTORY, node instanceof CDOResourceFolder);
      }

      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_READ_ONLY))
      {
        result.put(URIConverter.ATTRIBUTE_READ_ONLY, view.isReadOnly());
      }
    }

    return result;
  }

  public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException
  {
    // ViK: We can't change any of the proposed attributes. Only TIME_STAMP, and I believe we are not
    // storing that attribute in the server. Due to CDOResouce distributed nature, changing it wouldn't make much sense.
  }
}
