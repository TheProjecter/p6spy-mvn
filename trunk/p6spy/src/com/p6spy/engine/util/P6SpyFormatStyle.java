package com.p6spy.engine.util;

/**
 * Represents the the understood types or styles of formatting.
 * 
 * @author: Alex
 * @since: 2011-12-30
 * @version: $Revision: 1.0
 */

public class P6SpyFormatStyle {
	public static final P6SpyFormatStyle BASIC = new P6SpyFormatStyle( "basic", new P6SpyFormatterImpl() );
	public static final P6SpyFormatStyle DDL = new P6SpyFormatStyle( "ddl", new P6SpyDDLFormatterImpl() );
	public static final P6SpyFormatStyle NONE = new P6SpyFormatStyle( "none", new P6NoFormatImpl() );

	private final String name;
	private final P6SpyFormatter formatter;

	private P6SpyFormatStyle(String name, P6SpyFormatter formatter) {
		this.name = name;
		this.formatter = formatter;
	}

	public String getName() {
		return name;
	}

	public P6SpyFormatter getFormatter() {
		return formatter;
	}

	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		P6SpyFormatStyle that = ( P6SpyFormatStyle ) o;

		return name.equals( that.name );

	}

	public int hashCode() {
		return name.hashCode();
	}

	private static class P6NoFormatImpl implements P6SpyFormatter {
		public String format(String source) {
			return source;
		}
	}
}