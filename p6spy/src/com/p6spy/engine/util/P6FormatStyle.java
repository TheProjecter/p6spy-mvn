package com.p6spy.engine.util;

/**
 * Represents the the understood types or styles of formatting.
 * 
 * @author: Alex
 * @since: 2011-12-30
 * @version: $Revision: 1.0
 */

public class P6FormatStyle {
	public static final P6FormatStyle BASIC = new P6FormatStyle( "basic", new P6FormatterImpl() );
	public static final P6FormatStyle DDL = new P6FormatStyle( "ddl", new P6DDLFormatterImpl() );
	public static final P6FormatStyle NONE = new P6FormatStyle( "none", new P6NoFormatImpl() );

	private final String name;
	private final P6Formatter formatter;

	private P6FormatStyle(String name, P6Formatter formatter) {
		this.name = name;
		this.formatter = formatter;
	}

	public String getName() {
		return name;
	}

	public P6Formatter getFormatter() {
		return formatter;
	}

	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		P6FormatStyle that = ( P6FormatStyle ) o;

		return name.equals( that.name );

	}

	public int hashCode() {
		return name.hashCode();
	}

	private static class P6NoFormatImpl implements P6Formatter {
		public String format(String source) {
			return source;
		}
	}
}