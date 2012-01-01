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
 * Description: Test class for prepared statements
 *
 * $Author: cheechq $
 * $Revision: 1.5 $
 * $Date: 2003/06/03 19:20:26 $
 *
 * $Id: P6TestPerform.java,v 1.5 2003/06/03 19:20:26 cheechq Exp $
 * $Source: /cvsroot/p6spy/p6spy/com/p6spy/engine/test/P6TestPerform.java,v $
 * $Log: P6TestPerform.java,v $
 * Revision 1.5  2003/06/03 19:20:26  cheechq
 * removed unused imports
 *
 * Revision 1.4  2003/01/23 00:43:37  aarvesen
 * Changed the module to be dot rather than underscore
 *
 * Revision 1.3  2002/12/19 23:45:48  aarvesen
 * use factory rather than driver
 *
 * Revision 1.2  2002/10/06 18:24:04  jeffgoke
 * no message
 *
 * Revision 1.1  2002/05/24 07:30:46  jeffgoke
 * version 1 rewrite
 *
 * Revision 1.1  2002/04/21 06:16:20  jeffgoke
 * added test cases, fixed batch bugs
 *
 *
 *
 */

package com.p6spy.engine.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

public class P6TestPerform extends P6TestFramework {

	public static int rowsToCreate = 100000;

	public P6TestPerform(java.lang.String testName) {
		super(testName);
	}

	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(P6TestPerform.class);
		return suite;
	}

	@Override
	protected void setUp() {
		super.setUp();
	}

	public void testBeginSlowMonitor() {
		try {
			Map<String, String> tp = P6TestUtil.getDefaultPropertyFile();
			reloadProperty(tp);

			// we are going to fill up a large table for the following tests
			Statement statement = connection.createStatement();
			drop(statement);
			statement.execute("create table big_table_test (col1 number(10), col2 varchar2(255))");
			statement.execute("create table little_table_test (col1 number(10), col2 varchar2(255))");
			statement.close();

			String sql = "insert into big_table_test (col1, col2) values (?, ?)";
			PreparedStatement ps = connection.prepareStatement(sql);

			String trunk = createTrunk();

			for (int i = 0; i < rowsToCreate; i++) {
				ps.setInt(1, i);
				ps.setString(2, trunk + "_" + i);
				ps.addBatch();

				if (i % 1000 == 0) {
					ps.executeBatch();
				}
			}
			ps.executeBatch();

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testSlowMonitor() {
		try {
			Statement statement = connection.createStatement();

			Map<String, String> tp = P6TestUtil.getDefaultPropertyFile();
			tp.put("trace", "false");
			reloadProperty(tp);

			String trunk = createTrunk();

			String query;
			ResultSet rs;

			// now the monitor should be active but this should be fast enough to be okay
			query = "select 'zzee' from little_table_test";
			rs = statement.executeQuery(query);
			assertIsNotLastQuery("OUTAGE");
			//assertIsNotLastQuery(query);

			// this should not - it should log an outage
			query = "select col1 from big_table_test where col2 like '%" + trunk + "_" + (rowsToCreate + 1) + "%'";
			rs = statement.executeQuery(query);
			assertIsLastQuery("OUTAGE");
			assertIsLastQuery(query);
			assertNull(rs);
		} catch (Exception e) {
			fail(e.getMessage() + getStackTrace(e));
		}
	}

	public void testCleanSlowMonitor() {
		try {
			Statement statement = connection.createStatement();
			drop(statement);
			statement.close();
		} catch (Exception e) {}
	}

	@Override
	protected void tearDown() {
		try {
			super.tearDown();
		} catch (Exception e) {}
	}

	protected String createTrunk() {
		StringBuffer trunc = new StringBuffer(150);
		for (int i = 0; i < 150; i++) {
			trunc.append("P");
		}
		String trunk = trunc.toString();
		return trunk;
	}

	protected void drop(Statement statement) {
		dropStatement("drop table big_table_test", statement);
		dropStatement("drop table little_table_test", statement);
	}

	protected void dropStatement(String sql, Statement statement) {
		try {
			statement.execute(sql);
		} catch (Exception e) {
			// we don't really care about cleanup failing
		}
	}
}
