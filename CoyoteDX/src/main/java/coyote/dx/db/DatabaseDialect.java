/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.dx.db;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import coyote.commons.StringUtil;
import coyote.commons.template.SymbolTable;
import coyote.commons.template.Template;
import coyote.dx.CDX;
import coyote.loader.log.Log;
import coyote.loader.log.LogMsg;


/**
 * This class holds the mapping of different database types to DataFrame types 
 * so tables can be created in different databases for DX data.
 */
public class DatabaseDialect {

  // The symbols we expect to find in the symbol table
  public static final String TABLE_NAME_SYM = "tableName";
  public static final String DB_SCHEMA_SYM = "schemaName";
  public static final String DATABASE_SYM = "database";
  public static final String DATABASE_VERSION_SYM = "databaseVersion";
  public static final String DATABASE_MAJOR_SYM = "dbMajorVersion";
  public static final String DATABASE_MINOR_SYM = "dbMinorVersion";

  public static final String CREATE = "create";
  public static final String GRANT = "grant";
  public static final String INSERT = "insert";
  public static final String UPDATE = "update";
  public static final String DELETE = "delete";
  public static final String TRUNCATE = "truncate";
  public static final String TIMESTAMP = "timestamp";
  public static final String ALTER_COLUMN = "column_change";

  // Database Technologies supported
  public static final String ORACLE = "Oracle";
  public static final String MYSQL = "MySQL";
  public static final String H2 = "H2";

  private static final String DEFAULT = "default";

  private static final Map<String, Map<String, String>> TYPES = new HashMap<String, Map<String, String>>();
  private static final Map<String, Map<String, String>> SYNTAX = new HashMap<String, Map<String, String>>();

  static {
    // MySQL
    Map<String, String> map = new HashMap<String, String>();
    TYPES.put( MYSQL, map );
    map.put( "STR", "VARCHAR(#)" );
    map.put( "BOL", "TINYINT" );
    map.put( "S8", "TINYINT" );
    map.put( "U8", "TINYINT" );
    map.put( "S16", "INTEGER" );
    map.put( "U16", "INTEGER" );
    map.put( "S32", "INTEGER" );
    map.put( "U32", "INTEGER" );
    map.put( "S64", "INTEGER" );
    map.put( "U64", "INTEGER" );
    map.put( "DBL", "DOUBLE" );
    map.put( "FLT", "DOUBLE" );
    map.put( DEFAULT, "VARCHAR(#)" );
    map = new HashMap<String, String>();
    SYNTAX.put( MYSQL, map );
    map.put( CREATE, "CREATE TABLE [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#] ( [#$fielddefinitions#] )" );
    map.put( GRANT, "" );
    map.put( INSERT, "INSERT INTO [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#] ( [#$fieldnames#] ) VALUES ([#$fieldvalues#])" );
    map.put( UPDATE, "UPDATE [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#] SET [#$fieldmap#] WHERE \"sys_id\" = [#$keyvalue#]" );
    map.put( DELETE, "DELETE FROM [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#] WHERE \"sys_id\" = [#$keyvalue#]" );
    map.put( TRUNCATE, "TRUNCATE TABLE [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#]" );
    map.put( ALTER_COLUMN, "ALTER TABLE [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#] MODIFY [#$columnName#] [#$columnType#]" );

    // Oracle dialect
    map = new HashMap<String, String>();
    TYPES.put( ORACLE, map );
    map.put( "STR", "VARCHAR2(#)" );
    map.put( "BOL", "NUMBER(1)" );
    map.put( "S8", "NUMBER(8)" );
    map.put( "U8", "NUMBER(8)" );
    map.put( "S16", "NUMBER(10)" );
    map.put( "U16", "NUMBER(10)" );
    map.put( "S32", "NUMBER" );
    map.put( "U32", "NUMBER" );
    map.put( "S64", "NUMBER" );
    map.put( "U64", "NUMBER" );
    map.put( "DBL", "NUMBER" );
    map.put( "DAT", "TIMESTAMP" );
    map.put( "FLT", "NUMBER" );
    map.put( DEFAULT, "VARCHAR2(#)" );
    map = new HashMap<String, String>();
    SYNTAX.put( ORACLE, map );
    map.put( CREATE, "CREATE TABLE [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#] ( [#$fielddefinitions#] )" );
    map.put( GRANT, "GRANT SELECT,REFERENCES ON [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#] TO PUBLIC" );
    map.put( INSERT, "INSERT INTO [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#] ( [#$fieldnames#] VALUES [#$fieldvalues#])" );
    map.put( UPDATE, "UPDATE [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#] SET [#$fieldmap#] WHERE SYS_ID=[#$keyvalue#]" );
    map.put( DELETE, "DELETE FROM [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#] WHERE SYS_ID=[#$keyvalue#]" );
    map.put( TRUNCATE, "TRUNCATE TABLE [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#]" );
    map.put( ALTER_COLUMN, "ALTER TABLE [#$" + DB_SCHEMA_SYM + "#].[#$" + TABLE_NAME_SYM + "#] MODIFY [#$columnName#] [#$columnType#]" );

    // H2 Dialect
    map = new HashMap<String, String>();
    TYPES.put( H2, map );
    map.put( "STR", "VARCHAR(#)" );
    map.put( "BOL", "BOOLEAN" );
    map.put( "S8", "TINYINT" );
    map.put( "U8", "TINYINT" );
    map.put( "S16", "SMALLINT" );
    map.put( "U16", "SMALLINT" );
    map.put( "S32", "INTEGER" );
    map.put( "U32", "INTEGER" );
    map.put( "S64", "BIGINT" );
    map.put( "U64", "BIGINT" );
    map.put( "DBL", "DOUBLE" );
    map.put( "DAT", "TIMESTAMP" );
    map.put( "FLT", "REAL" );
    map.put( DEFAULT, "VARCHAR(#)" );
    map = new HashMap<String, String>();
    SYNTAX.put( H2, map );
    map.put( CREATE, "CREATE TABLE [#$" + TABLE_NAME_SYM + "#]( [#$fielddefinitions#] )" );
    map.put( GRANT, "" );
    map.put( INSERT, "INSERT INTO [#$" + TABLE_NAME_SYM + "#] ( [#$fieldnames#] VALUES ([#$fieldvalues#])" );
    map.put( UPDATE, "UPDATE [#$" + TABLE_NAME_SYM + "#] SET [#$fieldmap#] WHERE SYS_ID=[#$keyvalue#]" );
    map.put( DELETE, "DELETE FROM [#$" + TABLE_NAME_SYM + "#] WHERE SYS_ID=[#$keyvalue#]" );
    map.put( TRUNCATE, "TRUNCATE TABLE [#$" + TABLE_NAME_SYM + "#]" );
    map.put( ALTER_COLUMN, "ALTER TABLE [#$" + TABLE_NAME_SYM + "#] ALTER COLUMN [#$columnName#] [#$columnType#]" );
  }




  /**
   * Retrieve the syntax for a command for a particular database product.
   * 
   * <p>The result will be a template of the command and the caller will need 
   * to substitute specific tokens for intended values.</p>
   * 
   * <p>Hint: Use the static constants of this class to ensure conformity.</p>
   * 
   * @param database The database product being used
   * @param command the command to retrieve
   * @param symbols symbols to be used in resolving template variables
   * 
   * @return the command template or null if the database is not supported or the command is not recognized.
   */
  public static String getSQL( String database, String command, SymbolTable symbols ) {
    // find the map of commands for this database
    Map<String, String> map = SYNTAX.get( database );

    // If this database is supported
    if ( map != null ) {
      // lookup the requested command in this databases map of commands
      String cmdstr = map.get( command );

      if ( StringUtil.isNotBlank( cmdstr ) ) {

        // If we have a symbol table,
        if ( symbols != null ) {

          // resolve the variables in the command string
          Template tmplt = new Template( cmdstr, symbols );
          return tmplt.toString();

        } else {

          //...otherwise just return the raw template
          return cmdstr;
        }
      }
    }
    return null;
  }




  /**
   * Generate the proper SQL command to create a table which will hold data 
   * described by the given metric schema.
   * 
   * <p>A {@code Template} is used to handle variable substitution. If the 
   * {@code SymbolTable} is null, then the template will not be evaluated and 
   * returned to the caller as a raw template which the caller must process 
   * before using as a valid SQL statement. This can be helpful during the 
   * development process to see what symbols should be provided in the 
   * table.</p>
   * 
   * @param database the name of the database dialect to use (e.g. Oracle, MySQL, etc.)
   * @param schema the schema of the records to be stored
   * @param symbols the table of symbol values to use in variable substitution
   * 
   * @return a template string which can be used to generate the
   * 
   * @see coyote.commons.template.Template
   */
  @SuppressWarnings("unchecked")
  public static String getCreate( String database, MetricSchema schema, SymbolTable symbols ) {

    Map<String, String> typeMap = TYPES.get( database );
    if ( typeMap != null ) {

      StringBuffer b = new StringBuffer();

      // if ( Log.isLogging( Log.DEBUG_EVENTS ) ) {
      //   for ( Entry<String, String> entry : typeMap.entrySet() ) {
      //     Log.debug( String.format( "DB: \"%s\",\"%s\"", entry.getKey(), entry.getValue() ) );
      //   }
      // }

      for ( FieldMetrics metrics : schema.getMetrics() ) {

        final String fieldname = metrics.getName();
        final String fieldtype = metrics.getType();
        final long fieldlen = metrics.getMaximumStringLength();

        // Log.debug( String.format( "SN: \"%s\",\"%s\",%d", fieldname, fieldtype, fieldlen ) );

        b.append( fieldname );
        b.append( " " );

        // lookup the database type to use for this field
        String type = null;

        if ( StringUtil.isNotBlank( fieldtype ) )
          type = typeMap.get( fieldtype.toUpperCase() );

        if ( StringUtil.isBlank( type ) ) {
          Log.debug( LogMsg.createMsg( CDX.MSG, "Database.could_not_find_type", DatabaseDialect.class.getSimpleName(), fieldtype, type, typeMap.get( DEFAULT ), fieldname ) );
          type = typeMap.get( DEFAULT );
        }

        //Replace any required length tokens
        if ( type.indexOf( '#' ) > -1 ) {
          type = type.replace( "#", Long.toString( fieldlen ) );
        }

        // place the field and type in the buffer 
        b.append( type );
        b.append( ", " );
      }

      // trim off the last delimiter
      b.delete( b.length() - 2, b.length() );

      // if there is a symbol table, place the field definitions in it
      if ( symbols != null ) {
        symbols.put( "fielddefinitions", b.toString() );
      }

      // now have return the create command using the field definitions in the 
      // symbol table to fill out the command template
      return getSQL( database, CREATE, symbols );

    } else {
      Log.error( LogMsg.createMsg( CDX.MSG, "Could not find type definition for '{}' database", database ) );
    }

    return null;

  }

  public static String getCreate( String database, TableDefinition tdef ) {
    Map<String, String> typeMap = TYPES.get( database );
    if ( typeMap != null ) {
      StringBuffer b = new StringBuffer();

      if ( Log.isLogging( Log.DEBUG_EVENTS ) ) {
        for ( Entry<String, String> entry : typeMap.entrySet() ) {
           Log.debug( String.format( "DB: \"%s\",\"%s\"", entry.getKey(), entry.getValue() ) );
         }
       }

      for ( ColumnDefinition column : tdef.getColumns() ) {

        final String fieldname = column.getName();
        final String fieldtype = column.getType().getName();
        final long fieldlen = column.getLength();

        Log.debug( String.format( "SN: \"%s\",\"%s\",%d", fieldname, fieldtype, fieldlen ) );

        b.append( fieldname );
        b.append( " " );

        // lookup the database type to use for this field
        String type = null;

        if ( StringUtil.isNotBlank( fieldtype ) )
          type = typeMap.get( fieldtype.toUpperCase() );

        if ( StringUtil.isBlank( type ) ) {
          Log.debug( LogMsg.createMsg( CDX.MSG, "Database.could_not_find_type", DatabaseDialect.class.getSimpleName(), fieldtype, type, typeMap.get( DEFAULT ), fieldname ) );
          type = typeMap.get( DEFAULT );
        }

        //Replace any required length tokens
        if ( type.indexOf( '#' ) > -1 ) {
          type = type.replace( "#", Long.toString( fieldlen ) );
        }

        // place the field and type in the buffer 
        b.append( type );
        b.append( ", " );
      }

      // trim off the last delimiter
      b.delete( b.length() - 2, b.length() );

      SymbolTable symbols= new SymbolTable();
      symbols.put( "fielddefinitions", b.toString() );
      Log.debug( b.toString() );
      return getSQL( database, CREATE, symbols );

    } else {
      Log.error( LogMsg.createMsg( CDX.MSG, "Could not find type definition for '{}' database", database ) );
    }

    return null;    
  }


  /**
   * Resolve the given object to a valid type supported by the DataFrame using
   * the given SQL type as the type indicator.
   * 
   * <p>The following table is used to translate JDBC types to Field Types:
   * <table border="1"><caption>JDBC Type and Name to DataFrame type</caption>
   * <tr><th>Value</th><th>SQL</th><th>Field</th></tr>
   * <tr><td>-7</td><td>BIT</td><td>BOL</td></tr>
   * <tr><td>-6</td><td>TINYINT</td><td>S8</td></tr>
   * <tr><td>-5</td><td>BIGINT</td><td>LONG</td></tr>
   * <tr><td>-4</td><td>LONGVARBINARY</td><td>STR</td></tr>
   * <tr><td>-3</td><td>VARBINARY</td><td>STR</td></tr>
   * <tr><td>-2</td><td>BINARY</td><td>STR</td></tr>
   * <tr><td>-1</td><td>LONGVARCHAR</td><td>STR</td></tr>
   * <tr><td>0</td><td>NULL</td><td>NUL</td></tr>
   * <tr><td>1</td><td>CHAR</td><td>STR</td></tr>
   * <tr><td>2</td><td>NUMERIC</td><td>DBL</td></tr>
   * <tr><td>3</td><td>DECIMAL</td><td>DBL</td></tr>
   * <tr><td>4</td><td>INTEGER</td><td>S32</td></tr>
   * <tr><td>5</td><td>SMALLINT</td><td>S16</td></tr>
   * <tr><td>6</td><td>FLOAT</td><td>DBL</td></tr>
   * <tr><td>7</td><td>REAL</td><td>FLT</td></tr>
   * <tr><td>8</td><td>DOUBLE</td><td>DBL</td></tr>
   * <tr><td>12</td><td>VARCHAR</td><td>STR</td></tr>
   * <tr><td>91</td><td>DATE</td><td>DAT</td></tr>
   * <tr><td>92</td><td>TIME</td><td>DAT</td></tr>
   * <tr><td>93</td><td>TIMESTAMP</td><td>DAT</td></tr>
   * <tr><td>1111&nbsp;</td><td>OTHER</td><td>STR</td></tr></table>
   * 
   * @param value the value to convert
   * @param type the SQL type of the value passed
   * 
   * @return an object which can be safely placed in a DataFrame field.
   */
  public static Object resolveValue( Object value, int type ) {
    Object retval = null;

    if ( value != null ) {
      switch ( type ) {
        case 2:
        case 3:
          retval = ( (java.math.BigDecimal)value ).doubleValue();
          break;
        case 4:
          retval = ( (Integer)value ).intValue();
          break;
        case 5:
          retval = ( (Integer)value ).shortValue();
          break;
        case 6:
          retval = ( (Double)value ).doubleValue();
          break;
        case 7:
          retval = ( (Float)value ).floatValue();
          break;
        case 8:
          retval = ( (Double)value ).doubleValue();
          break;
        case 91:
          retval = new Date( ( (java.sql.Date)value ).getTime() );
          break;
        case 92:
          retval = new Date( ( (java.sql.Time)value ).getTime() );
          break;
        case 93:
          retval = new Date( ( (java.sql.Timestamp)value ).getTime() );
          break;
        default:
          retval = value.toString();
      }

    }

    return retval;
  }




  
}
