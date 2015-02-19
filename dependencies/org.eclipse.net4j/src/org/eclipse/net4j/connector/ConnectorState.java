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
package org.eclipse.net4j.connector;

import org.eclipse.net4j.util.security.INegotiator;

/**
 * Enumerates the lifecycle states of an {@link IConnector}.
 * <p>
 * <dt><b>State Machine Diagram:</b></dt>
 * <dd><img src="doc-files/ConnectorState-1.gif" title="Diagram Connector States" border="0"
 * usemap="#ConnectorState-1.gif"/></dd>
 * <p>
 * <MAP NAME="ConnectorState-1.gif"> <AREA SHAPE="RECT" COORDS="26,135,143,159" HREF="ConnectorState.html#DISCONNECTED">
 * <AREA SHAPE="RECT" COORDS="449,50,547,73" HREF="ConnectorState.html#CONNECTING"> <AREA SHAPE="RECT"
 * COORDS="449,133,549,159" HREF="ConnectorState.html#NEGOTIATING"> <AREA SHAPE="RECT" COORDS="451,216,545,240"
 * HREF="ConnectorState.html#CONNECTED"> <AREA SHAPE="POLYGON" COORDS="10,89,11,183,164,183,163,109,77,109,77,89,11,88"
 * HREF="../util/lifecycle/ILifecycle.html#isActive()"> <AREA SHAPE="POLYGON"
 * COORDS="429,10,428,262,597,263,597,30,483,29,483,10,428,11" HREF="../util/lifecycle/ILifecycle.html#isActive()">
 * </MAP>
 * 
 * @see IConnector#getState()
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 */
public enum ConnectorState
{
  /**
   * Indicates that the {@link IConnector} has not been connected yet or has been disconnected after being connected
   * previously.
   * <p>
   * A connector is <code>DISCONNECTED</code> if and only if it is not
   * {@link org.eclipse.net4j.util.lifecycle.LifecycleUtil#isActive(Object) active}. A transition to {@link #CONNECTING}
   * can be triggered by calling {@link IConnector#connect(long)} or {@link IConnector#connectAsync()}.
   * 
   * @see IConnector#getState()
   * @see org.eclipse.net4j.util.lifecycle.ILifecycle#isActive()
   */
  DISCONNECTED,

  /**
   * Indicates that the {@link IConnector} is currently trying to establish an underlying physical connection like a TCP
   * socket connection.
   * <p>
   * A connector can only be <code>CONNECTING</code> if it is
   * {@link org.eclipse.net4j.util.lifecycle.LifecycleUtil#isActive(Object) active}. As soon as the underlying physical
   * connection is successfully established the state of the connector automatically transitions to {@link #NEGOTIATING}.
   * 
   * @see IConnector#getState()
   * @see org.eclipse.net4j.util.lifecycle.ILifecycle#isActive()
   */
  CONNECTING,

  /**
   * Indicates that the {@link IConnector} has successfully managed to establish the underlying physical connection and
   * has currently delegated control over this connection to an {@link INegotiator}.
   * <p>
   * A connector can only be <code>NEGOTIATING</code> if it is
   * {@link org.eclipse.net4j.util.lifecycle.LifecycleUtil#isActive(Object) active} and a negotiator has been supplied.
   * As soon as the negotiator has successfully negotiated both peers (or a negotiator has not been supplied) the state
   * of the connector automatically transitions to {@link #CONNECTED}.
   * <p>
   * Negotiators can implement arbitrary handshake protocols, challenge-response sequences or other authentication
   * procedures. They can also be used to initially setup connection encryption if the connector implementation is not
   * able to do so.
   * 
   * @see IConnector#getState()
   * @see org.eclipse.net4j.util.lifecycle.ILifecycle#isActive()
   */
  NEGOTIATING,

  /**
   * Indicates that the {@link IConnector} has successfully managed to establish and negotiate the underlying physical
   * connection and is ready now to perform actual communications.
   * <p>
   * A connector can only be <code>CONNECTED</code> if it is
   * {@link org.eclipse.net4j.util.lifecycle.LifecycleUtil#isActive(Object) active}. A transition to
   * {@link #DISCONNECTED} can be triggered by calling {@link IConnector#close()}.
   * 
   * @see IConnector#getState()
   * @see org.eclipse.net4j.util.lifecycle.ILifecycle#isActive()
   */
  CONNECTED
}
