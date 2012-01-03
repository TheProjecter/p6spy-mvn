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

package com.p6spy.engine.logging.appender;

import java.util.Properties;

import com.p6spy.engine.common.P6SpyProperties;
import com.p6spy.engine.util.P6SpyFormatStyle;
import com.p6spy.engine.util.P6SpyFormatter;
import com.p6spy.engine.util.P6PropertiesHelper;
import com.p6spy.engine.util.P6SpyConst;

/**
 * Centralize logging handling for SQL statements.
 * 
 * @author: Alex
 * @since: 2011-12-30
 * @version: $Revision: 1.4.0
 */

public abstract class FormattedLogger {

	protected String lastEntry;
	private P6SpyFormatter formatter;

	/**
	 * handling for SQL statements output.
	 */
	public void logSQL(int connectionId, String now, long elapsed, String category, String prepared, String sql) {
		P6SpyProperties properties = new P6SpyProperties();

		String logEntry = "";
		String logFormat = "";
		boolean isShowSql = Boolean.FALSE;
		boolean isFormatSql = Boolean.FALSE;

		Properties props = properties.forceReadProperties();

		isShowSql = P6PropertiesHelper.getBoolean("show_sql", props);
		isFormatSql = P6PropertiesHelper.getBoolean("format_sql", props);
		logFormat = P6PropertiesHelper.getString("logformat", props, null);

		if (Boolean.TRUE.equals(isShowSql)) {
			String statement = sql.replaceAll("\\s+", " ");

			if (Boolean.TRUE.equals(isFormatSql)) {
				formatter = getFormatter(statement);
				statement = formatter.format(statement);
			}

			if ("1".equals(logFormat)) {
				logEntry = statement + ";\r\n";
				logEntry += "============================================================";
			} else if ("2".equals(logFormat)) {
				logEntry = category + " | " + statement + ";";
			} else if ("3".equals(logFormat)) {
				logEntry = statement + ";\r\n End Statment";
			} else if ("4".equals(logFormat)) {
				logEntry = category + ": \r\n" + statement + ";";
			} else {
				logEntry =
						"|" + elapsed + "|" + (connectionId == -1 ? "" : String.valueOf(connectionId)) + "|" + category + "|"
								+ prepared + "|" + statement + ";";
			}

			logText(logEntry);
		}

	}

	/**
	 * Get the formatter for SQL type.
	 */
	public P6SpyFormatter getFormatter(String sql) {
		String lowerSql = sql.toLowerCase();
		if (lowerSql.startsWith(P6SpyConst.DDL_CREATE_TABLE) || lowerSql.startsWith(P6SpyConst.DDL_DROP_TABLE)
				|| lowerSql.startsWith(P6SpyConst.DDL_ALTER_TABLE) || lowerSql.startsWith(P6SpyConst.DDL_COMMENT_ON)
				|| lowerSql.startsWith(P6SpyConst.DDL_TRUNC_TABLE)) {
			return P6SpyFormatStyle.DDL.getFormatter();
		} else if (lowerSql.startsWith(P6SpyConst.DML_INSERT_PREFIX) || lowerSql.startsWith(P6SpyConst.DML_UPDATE_PREFIX)
				|| lowerSql.startsWith(P6SpyConst.DML_DELETE_PREFIX) || lowerSql.startsWith(P6SpyConst.DML_SELECT_PREFIX)) {
			return P6SpyFormatStyle.BASIC.getFormatter();
		} else {
			return P6SpyFormatStyle.NONE.getFormatter();
		}
	}

	public abstract void logText(String text);

	// they also all need to have the last entry thing
	public void setLastEntry(String inVar) {
		lastEntry = inVar;
	}

	public String getLastEntry() {
		return lastEntry;
	}
}
