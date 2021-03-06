/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.net.socket;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.naming.CommunicationException;
import org.teiid.client.util.ResultsFuture;
import org.teiid.client.util.ResultsReceiver;
import org.teiid.core.crypto.Cryptor;
import org.teiid.net.HostInfo;


public interface SocketServerInstance {

	<T> T getService(Class<T> iface);

	void shutdown();
	
	HostInfo getHostInfo();
	
	boolean isOpen();
	
	Cryptor getCryptor();

	long getSynchTimeout();

	void send(Message message, ResultsReceiver<Object> receiver, Serializable key) throws CommunicationException, InterruptedException, org.teiid.net.CommunicationException;

	void read(long timeout, TimeUnit unit, ResultsFuture<?> resultsFuture) throws TimeoutException, InterruptedException;

	String getServerVersion();

	InetAddress getLocalAddress();

}