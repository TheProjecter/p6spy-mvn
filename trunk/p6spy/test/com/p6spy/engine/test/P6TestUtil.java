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

/**
 * Description: Test class for statements
 *
 * $Author: Alex $
 * $Revision: 1.4 $
 * $Date: 2011/12/31 10:20:26 $
 * support JDK 1.5
 * 
 * $Author: cheechq $
 * $Revision: 1.10 $
 * $Date: 2003/06/03 19:20:26 $
 *
 * $Id: P6TestUtil.java,v 1.10 2003/06/03 19:20:26 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestUtil.java,v $
 * $Log: P6TestUtil.java,v $
 * Revision 1.10  2003/06/03 19:20:26  cheechq
 * removed unused imports
 *
 * Revision 1.9  2003/03/07 22:06:46  aarvesen
 * made some things public
 * added in deregisterdrivers = true
 *
 * Revision 1.8  2003/02/14 22:22:16  aarvesen
 * use a define for the property file
 *
 * Revision 1.7  2003/01/28 19:32:32  jeffgoke
 * fixed bug exposed by test framework where option reloading was having problems if options were manipulated before the driver was created.
 *
 * Revision 1.6  2003/01/28 17:59:14  jeffgoke
 * fixed test cases to use new options
 *
 * Revision 1.5  2003/01/23 00:43:37  aarvesen
 * Changed the module to be dot rather than underscore
 *
 * Revision 1.4  2003/01/03 21:19:24  aarvesen
 * use the new P6Util.forName
 *
 * Revision 1.3  2002/12/19 23:45:18  aarvesen
 * change to be factory rather than driver
 *
 * Revision 1.2  2002/12/12 01:39:01  jeffgoke
 * no message
 *
 * Revision 1.1  2002/10/06 18:24:04  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:30:46  jeffgoke
 * version 1 rewrite
 *
 *
 *
 */

package com.p6spy.engine.test;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import junit.framework.Assert;

import com.p6spy.engine.common.OptionReloader;
import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.P6SpyProperties;
import com.p6spy.engine.common.P6Util;

public class P6TestUtil {

	protected P6TestUtil() {
	}

	/**
	 * Support to read the properties file from class path.
	 * @param filename
	 * @return
	 */
	public static Properties loadProperties(String filename) {
		if (filename == null) {
			System.err.println("No " + filename + " properties file specified.");
			System.exit(1);
		}

		Properties props = new Properties();
		try {
			String path = classPathFile(filename);
			if (path == null) {
				System.err.println("Unable to find properties file: " + filename);
				System.exit(1);
			} else {
				FileInputStream in = new FileInputStream(path);
				props.load(in);
				in.close();
			}
		} catch (FileNotFoundException e1) {
			System.err.println("Unable to find properties file: " + filename);
			System.exit(1);
		} catch (IOException e2) {
			System.err.println("Unable to read properties from properties file: " + filename + ".  Exception: "
					+ e2.toString());
			System.exit(1);
		}

		return props;
	}

	/**
	 * Here we attempt to find the file in the current dir and the classpath
	 * If we can't find it then we return null
	 */
	public static String classPathFile(String file) {
		File fp = null;
		String path = null;
		String separator = System.getProperty("path.separator");
		String slash = System.getProperty("file.separator");
		String classpath = "." + separator + System.getProperty("java.class.path");
		String local = System.getProperty("p6.home");

		// first try to load options via the classloader
		try {
			// If p6.home is specified, just look there
			if (local != null) {
				fp = new File(local, file);
			} else {
				// try to get the classloader via the current thread
				fp = classLoadPropertyFile(Thread.currentThread().getContextClassLoader().getResource(file));

				if (fp == null) {
					// next try the current class
					fp = classLoadPropertyFile(P6Util.class.getClassLoader().getResource(file));
				}

				if (fp == null) {
					// now the ClassLoader system resource
					classLoadPropertyFile(ClassLoader.getSystemResource(file));
				}
			}

			if (fp.exists()) {
				return fp.getCanonicalPath();
			}
		} catch (Exception exc) {}

		// if that failed, see what we can do on our own
		StringTokenizer tok = new StringTokenizer(classpath, separator);

		do {
			String dir = tok.nextToken();
			path = dir.equals(".") ? file : dir + slash + file;
			fp = new File(path);
		} while (!fp.exists() && tok.hasMoreTokens());

		return fp.exists() ? path : null;
	}

	public static File classLoadPropertyFile(java.net.URL purl) {
		try {
			if (purl != null) {
				// modified by jayakumar for JDK 1.2 support
				//return new File(purl.getPath());
				return new File(getPath(purl));
				// end of modification
			}
		} catch (Exception e) {
			// we ignore this, since JDK 1.2 does not suppport this method
		}
		return null;
	}

	// method add by jayakumar for JDK1.2 support for URL.getPath()
	public static String getPath(URL theURL) {
		String file = theURL.getFile();
		String path = null;
		if (file != null) {
			int q = file.lastIndexOf('?');
			if (q != -1) {
				path = file.substring(0, q);
			} else {
				path = file;
			}
		}
		return path;
	}

	// end of support method

	protected static void writeProperty(String filename, Map<String, String> props) throws IOException {
		File reload = new File(filename);
		reload.delete();

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(reload)));

		Iterator<String> i = props.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			String value = (String) props.get(key);
			out.println(key + "=" + value);
		}

		out.close();
	}

	protected static void writeFile(String filename, List<String> entries) throws IOException {
		File file = new File(filename);
		file.delete();

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		for (int i = 0; i < entries.size(); i++) {
			String entry = (String) entries.get(i);
			out.println(entry);
		}

		out.close();
	}

	protected static Map<String, String> getDefaultPropertyFile() {

		Properties props = loadProperties("P6Test.properties");
		String realdrivername = props.getProperty("p6realdriver");

		Properties props2 = loadProperties("P6Test.properties");
		String realdrivername2 = props2.getProperty("p6realdriver2");

		Map<String, String> tp = new HashMap<String, String>();
		//tp.put("module.outage", "com.p6spy.engine.outage.P6OutageFactory");
		tp.put("module.log", "com.p6spy.engine.logging.P6LogFactory");
		tp.put("realdriver", realdrivername);
		tp.put("realdriver2", realdrivername2);
		tp.put("filter", "false");
		tp.put("include", "");
		tp.put("exclude", "");
		tp.put("trace", "true");
		tp.put("autoflush", "true");
		tp.put("logfile", "spy.log");
		tp.put("append", "true");
		tp.put("dateformat", "");
		tp.put("includecategories", "");
		tp.put("excludecategories", "debug,result,batch");
		tp.put("stringmatcher", "");
		tp.put("stacktrace", "false");
		tp.put("show_sql", "true");
		tp.put("format_sql", "true");
		tp.put("logformat", "4");
		tp.put("stacktraceclass", "");
		tp.put("reloadproperties", "false");
		tp.put("reloadpropertiesinterval", "1");
		tp.put("useprefix", "false");
		tp.put("outagedetection", "false");
		tp.put("outagedetectioninterval", "");
		tp.put("entries", "");
		tp.put("forms", "");
		tp.put("formsfile", "testspy.forms");
		tp.put("formslog", "testforms.log");
		tp.put("formstrace", "true");
		tp.put("deregisterdrivers", "true");
		return tp;
	}

	protected static void reloadProperty(Map<String, String> props) throws IOException {
		writeProperty(P6TestFramework.PROPERTY_FILE, props);

		P6SpyProperties.setSpyProperties(P6TestFramework.PROPERTY_FILE);
		OptionReloader.reload();
	}

	protected static void assertIsLastQuery(String query) {
		boolean isTrue = P6LogQuery.getLastEntry().indexOf(query) != -1;
		if (!isTrue) {
			System.err.println(query + " was not the last query, this was: " + P6LogQuery.getLastEntry());
		}
		Assert.assertTrue(isTrue);
	}

	protected static void assertIsNotLastQuery(String query) {
		boolean isFalse = P6LogQuery.getLastEntry().indexOf(query) == -1;
		if (!isFalse) {
			System.err.println(query + " was the last query and should not have been");
		}
		Assert.assertTrue(isFalse);
	}

	protected static String getStackTrace(Exception e) {
		CharArrayWriter c = new CharArrayWriter();
		e.printStackTrace(new PrintWriter(c));
		return c.toString();
	}

	protected static void printAllDrivers() {
		for (Enumeration<?> e = DriverManager.getDrivers(); e.hasMoreElements();) {
			System.err.println("2 DRIVER FOUND == " + e.nextElement());
		}
	}

	protected static void unloadDrivers() throws SQLException {
		Properties props = loadProperties("P6Test.properties");
		String drivername = props.getProperty("p6driver");
		String user = props.getProperty("user");
		String password = props.getProperty("password");
		String url = props.getProperty("url");

		System.out.println("driverName: " + drivername);
		System.out.println("username: " + user);
		System.out.println("password: " + password);
		System.out.println("url:" + url);

		try {
			Driver driver;
			while ((driver = DriverManager.getDriver(url)) != null) {
				System.err.println("Deregistering driver: " + driver.getClass().getName());
				DriverManager.deregisterDriver(driver);
			}
		} catch (Exception e) {}
	}

	public static Connection loadDrivers(String drivernameProperty) throws SQLException, IOException, ClassNotFoundException {
		Properties props = loadProperties("P6Test.properties");
		String drivername = props.getProperty(drivernameProperty);
		String user = props.getProperty("user");
		String password = props.getProperty("password");
		String url = props.getProperty("url");

		System.err.println("UTIL REGISTERING DRIVER PROPERTY == " + drivernameProperty + " TO REGISTER DRIVER == "
				+ drivername);
		P6Util.forName(drivername);
		Driver driver = DriverManager.getDriver(url);
		System.err.println("UTIL USING DRIVER == " + driver.getClass().getName() + " FOR URL " + url);
		Connection connection = DriverManager.getConnection(url, user, password);
		printAllDrivers();
		return connection;
	}
}
