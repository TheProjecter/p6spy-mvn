/*
 *
 * ====================================================================
 *
 * The P6Spy Software License, Version 1.1
 *
 * This license is derived and fully compatible with the Apache Software
 * license, see http://www.apache.org/LICENSE.txt
 *
 * Copyright (c) 2001-2002 Andy Martin, Ph.D. and Jeff Goke
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 * any, must include the following acknowlegement:
 * "The original concept and code base for P6Spy was conceived
 * and developed by Andy Martin, Ph.D. who generously contribued
 * the first complete release to the public under this license.
 * This product was due to the pioneering work of Andy
 * that began in December of 1995 developing applications that could
 * seamlessly be deployed with minimal effort but with dramatic results.
 * This code is maintained and extended by Jeff Goke and with the ideas
 * and contributions of other P6Spy contributors.
 * (http://www.p6spy.com)"
 * Alternately, this acknowlegement may appear in the software itself,
 * if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "P6Spy", "Jeff Goke", and "Andy Martin" must not be used
 * to endorse or promote products derived from this software without
 * prior written permission. For written permission, please contact
 * license@p6spy.com.
 *
 * 5. Products derived from this software may not be called "P6Spy"
 * nor may "P6Spy" appear in their names without prior written
 * permission of Jeff Goke and Andy Martin.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

// used by the P6Options class to bind together a class name and
// an option
package com.p6spy.engine.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class OptionReloader implements Runnable {

	/* this is our list of option classes we need to call,
	 * we use a set because we only want to call each class
	 * once */
	protected static Set<P6Options> options = new HashSet<P6Options>();

	protected long sleepTime = 0;
	protected boolean running = false;

	public OptionReloader(long sleep) {
		setSleep(sleep);
		setRunning(true);
	}

	public void setSleep(long sleep) {
		sleepTime = sleep;
	}

	public void setRunning(boolean run) {
		running = run;
	}

	public boolean getRunning() {
		return running;
	}

	@SuppressWarnings("static-access")
	public void run() {
		while (running) {
			// this will always run its own thread,
			// so it should be all right to call sleep
			// on the current thread
			try {
				Thread.currentThread().sleep(sleepTime);
			} catch (InterruptedException e) {
				// nothing.
			}
			reload();
		}
	}

	public static void add(P6Options p6options, P6SpyProperties properties) {
		options.add(p6options);
		// when added make sure to deal with this
		if (properties.isNewProperties() == false) {
			properties.forceReadProperties();
		}
		p6options.reload(properties);
	}

	public static void reload() {
		P6SpyProperties properties = new P6SpyProperties();
		// if nothing to reload, don't call the reload function
		if (properties.isNewProperties() == false) {
			return;
		}
		Iterator<P6Options> i = options.iterator();
		while (i.hasNext()) {
			P6Options options = (P6Options) i.next();
			options.reload(properties);
		}
	}

	public static Iterator<P6Options> iterator() {
		return options.iterator();
	}

}
