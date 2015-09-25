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
package org.eclipse.net4j.acceptor;

import org.eclipse.net4j.ILocationAware.Location;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.spi.net4j.Acceptor;

/**
 * Accepts incoming connection requests from {@link Location#CLIENT client} {@link IConnector connectors} and creates
 * the appropriate {@link Location#SERVER server} connectors.
 * <p>
 * Since the process of accepting connection requests is heavily dependent on the implementation of the respective
 * connectors the only public API is introspection and notification.
 * <p>
 * This interface is <b>not</b> intended to be implemented by clients. Service providers <b>must</b> extend the abstract
 * {@link Acceptor} class.
 * <p>
 * <dt><b>Class Diagram:</b></dt>
 * <dd><img src="doc-files/IAcceptor-1.gif" title="Diagram Acceptors" border="0" usemap="#IAcceptor-1.gif"/></dd>
 * <p>
 * <MAP NAME="IAcceptor-1.gif"> <AREA SHAPE="RECT" COORDS="10,8,99,58" HREF="IAcceptor.html"> <AREA SHAPE="RECT"
 * COORDS="289,8,378,58" HREF="../connector/IConnector.html"> </MAP>
 * <p>
 * <dt><b>Sequence Diagram:</b></dt>
 * <dd><img src="doc-files/IAcceptor-2.gif" title="Connection Process" border="0" usemap="#IAcceptor-2.gif"/></dd>
 * <p>
 * <MAP NAME="IAcceptor-2.gif"> <AREA SHAPE="RECT" COORDS="146,136,265,165" HREF="IConnector.html"> <AREA SHAPE="RECT"
 * COORDS="485,75,564,105" HREF="IAcceptor.html"> <AREA SHAPE="RECT" COORDS="296,325,414,355" HREF="IConnector.html">
 * <AREA SHAPE="RECT" COORDS="64,426,444,506" HREF="ConnectorState.html#CONNECTING"> <AREA SHAPE="RECT"
 * COORDS="64,516,444,596" HREF="ConnectorState.html#NEGOTIATING"> </MAP>
 * 
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IAcceptor extends IContainer<IConnector>, Closeable
{
  /**
   * Returns an array of the connectors that have been accepted by this acceptor and not been closed since.
   */
  public IConnector[] getAcceptedConnectors();
}
