/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.batch.validate;

import java.io.IOException;

import coyote.batch.AbstractConfigurableComponent;
import coyote.batch.Batch;
import coyote.batch.ConfigTag;
import coyote.batch.ConfigurableComponent;
import coyote.batch.ConfigurationException;
import coyote.batch.FrameValidator;
import coyote.batch.TransactionContext;
import coyote.batch.TransformContext;
import coyote.commons.StringUtil;
import coyote.dataframe.DataFrame;
import coyote.dataframe.DataFrameException;
import coyote.loader.log.Log;
import coyote.loader.log.LogMsg;


/**
 * Validators work on the working frame and check to see if the working data 
 * meets the requirements of the batch job.
 * 
 * <p>Configuration example:
 * <pre>"Validate" : {
 *   "Distinct" : { "field" : "asset_tag",  "desc" : "Asset tag must be unique"  }
 * },</pre></p>
 */
public abstract class AbstractValidator extends AbstractConfigurableComponent implements FrameValidator, ConfigurableComponent {

  protected boolean halt = false;

  protected String fieldName = null;
  protected String description = null;




  @Override
  public void open( TransformContext context ) {

  }




  protected boolean haltOnFail() {
    return halt;
  }




  @Override
  public void close() throws IOException {

  }




  /**
   * @see coyote.batch.FrameValidator#process(coyote.batch.TransactionContext)
   */
  @Override
  public boolean process( TransactionContext context ) throws ValidationException {
    return false;
  }




  /**
   * 
   * @param context
   * @param field
   * @param message
   */
  protected void fail( TransactionContext context, String field, String message ) {
    context.fireValidationFailed( this, message );
    if ( haltOnFail() ) {
      context.setError( message );
    }
  }




  /**
   * Fires the validation failed event in the listeners with the description of this validation rule and optionally
   * @param context
   * @param field
   */
  protected void fail( TransactionContext context, String field ) {
    if ( StringUtil.isNotBlank( description ) ) {
      fail( context, field, description );
    } else {
      fail( context, field, getClass() + " validation of " + field + " failed" );
    }
  }




  /**
   * @see coyote.batch.AbstractConfigurableComponent#setConfiguration(coyote.dataframe.DataFrame)
   */
  @Override
  public void setConfiguration( DataFrame frame ) throws ConfigurationException {
    configuration = frame;

    // All validators need to know which fields to validate
    if ( frame.contains( ConfigTag.FIELD ) ) {
      fieldName = frame.getAsString( ConfigTag.FIELD );
    } else {
      throw new ConfigurationException( "Missing required '" + ConfigTag.FIELD + "' attribute" );
    }

    if ( frame.contains( ConfigTag.DESCRIPTION ) ) {
      description = frame.getAsString( ConfigTag.DESCRIPTION );
    }

    // Check if we are to thrown a context error if validation fails
    if ( frame.contains( ConfigTag.HALT_ON_FAIL ) ) {
      try {
        halt = frame.getAsBoolean( ConfigTag.HALT_ON_FAIL );
      } catch ( DataFrameException e ) {
        Log.info( LogMsg.createMsg( Batch.MSG, "Task.Header flag not valid " + e.getMessage() ) );
        halt = false;
      }
    } else {
      Log.debug( LogMsg.createMsg( Batch.MSG, "Task.No halt config" ) );
    }
  }




  /**
   * @return the name of the field to which this validator is targeted
   */
  public String getFieldName() {
    return fieldName;
  }




  /**
   * @return the description of this validator (Also used in error messages)
   */
  public String getDescription() {
    return description;
  }

}
