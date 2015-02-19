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
package org.eclipse.net4j;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiator;
import org.eclipse.net4j.util.security.NegotiatorFactory;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.spi.net4j.Acceptor;
import org.eclipse.spi.net4j.AcceptorFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads an XML config file and creates, wires and starts the configured {@link IAcceptor acceptors}.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public class TransportConfigurator
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TransportConfigurator.class);

  private IManagedContainer container;

  public TransportConfigurator(IManagedContainer container)
  {
    this.container = container;
  }

  public IManagedContainer getContainer()
  {
    return container;
  }

  public IAcceptor[] configure(File configFile) throws ParserConfigurationException, SAXException, IOException,
      CoreException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Configuring Net4j server from " + configFile.getAbsolutePath()); //$NON-NLS-1$
    }

    List<IAcceptor> acceptors = new ArrayList<IAcceptor>();
    Document document = getDocument(configFile);
    NodeList acceptorConfigs = document.getElementsByTagName("acceptor"); //$NON-NLS-1$
    for (int i = 0; i < acceptorConfigs.getLength(); i++)
    {
      Element acceptorConfig = (Element)acceptorConfigs.item(i);
      IAcceptor acceptor = configureAcceptor(acceptorConfig);
      acceptors.add(acceptor);
    }

    return acceptors.toArray(new IAcceptor[acceptors.size()]);
  }

  protected IAcceptor configureAcceptor(Element acceptorConfig)
  {
    String type = acceptorConfig.getAttribute("type"); //$NON-NLS-1$
    // TODO Make the following dependent on the "type" attribute value
    String listenAddr = acceptorConfig.getAttribute("listenAddr"); //$NON-NLS-1$
    String port = acceptorConfig.getAttribute("port"); //$NON-NLS-1$
    String description = (listenAddr == null ? "" : listenAddr) + (port == null ? "" : ":" + port); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    Acceptor acceptor = (Acceptor)container.getElement(AcceptorFactory.PRODUCT_GROUP, type, description, false);

    NodeList negotiatorConfigs = acceptorConfig.getElementsByTagName("negotiator"); //$NON-NLS-1$
    if (negotiatorConfigs.getLength() > 1)
    {
      throw new IllegalStateException("A maximum of one negotiator can be configured for acceptor " + acceptor); //$NON-NLS-1$
    }

    if (negotiatorConfigs.getLength() == 1)
    {
      Element negotiatorConfig = (Element)negotiatorConfigs.item(0);
      INegotiator negotiator = configureNegotiator(negotiatorConfig);
      acceptor.getConfig().setNegotiator(negotiator);
    }

    acceptor.activate();
    return acceptor;
  }

  protected INegotiator configureNegotiator(Element negotiatorConfig)
  {
    String type = negotiatorConfig.getAttribute("type"); //$NON-NLS-1$
    String description = negotiatorConfig.getAttribute("description"); //$NON-NLS-1$
    return (INegotiator)container.getElement(NegotiatorFactory.PRODUCT_GROUP, type, description);
  }

  protected Document getDocument(File configFile) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(configFile);
  }

  protected Element getStoreConfig(Element repositoryConfig)
  {
    NodeList storeConfigs = repositoryConfig.getElementsByTagName("store"); //$NON-NLS-1$
    if (storeConfigs.getLength() != 1)
    {
      String repositoryName = repositoryConfig.getAttribute("name"); //$NON-NLS-1$
      throw new IllegalStateException("Exactly one store must be configured for repository " + repositoryName); //$NON-NLS-1$
    }

    return (Element)storeConfigs.item(0);
  }

  public static Map<String, String> getProperties(Element element, int levels)
  {
    Map<String, String> properties = new HashMap<String, String>();
    collectProperties(element, "", properties, levels); //$NON-NLS-1$
    return properties;
  }

  private static void collectProperties(Element element, String prefix, Map<String, String> properties, int levels)
  {
    if ("property".equals(element.getNodeName())) //$NON-NLS-1$
    {
      String name = element.getAttribute("name"); //$NON-NLS-1$
      String value = element.getAttribute("value"); //$NON-NLS-1$
      properties.put(prefix + name, value);
      prefix += name + "."; //$NON-NLS-1$
    }

    if (levels > 0)
    {
      NodeList childNodes = element.getChildNodes();
      for (int i = 0; i < childNodes.getLength(); i++)
      {
        Node childNode = childNodes.item(i);
        if (childNode instanceof Element)
        {
          collectProperties((Element)childNode, prefix, properties, levels - 1);
        }
      }
    }
  }
}
