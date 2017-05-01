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
package coyote.dx;

import java.io.File;

import coyote.commons.template.SymbolTable;


/**
 *  
 */
public interface TransformEngine extends RunnableComponent {

  /**
   * @param object
   */
  public void addListener( ContextListener object );




  @Override
  public TransformContext getContext();




  public FrameMapper getMapper();




  public FrameReader getReader();




  public void setContext( TransformContext context );




  public void setMapper( FrameMapper mapper );




  public void setReader( FrameReader reader );




  public void addWriter( FrameWriter writer );




  /**
   * @return the name of the transform engine
   */
  public String getName();




  /**
   * @param name the transform engine name to set
   */
  public void setName( String name );




  /**
   * @return the symbol table the engine uses for template lookups
   */
  public SymbolTable getSymbolTable();




  /**
   * Add the task to an ordered list of tasks to be run before the transform is 
   * run.
   * 
   * <p>Tasks will be executed in the order they were added to the engine.</p>
   * 
   * @param task the task to add
   * 
   * @return the sequence in which it will be executed
   */
  public int addPreProcessTask( TransformTask task );




  /**
   * Add the task to an ordered list of tasks to be run after the transform is 
   * run.
   * 
   * <p>Tasks will be executed in the order they were added to the engine.</p>
   * 
   * @param task the task to add
   * 
   * @return the sequence in which it will be executed
   */
  public int addPostProcessTask( TransformTask task );




  public void addDatabase( Database database );




  public void addValidator( FrameValidator validator );




  public void addTransformer( FrameTransform transformer );




  /**
   * Called when the JVM terminates.
   * 
   * <p>This is designed to be called by the Runtime.shutdownHook to terminate 
   * any long-running processes and after the engine terminates normally. This 
   * is not part of the transform engine run loop, but part of the Loader life 
   * cycle management loop.</p>
   * 
   * <p>This should always be called when the runtime terminates. The only 
   * time this might not be called is when the process terminates abnormally. 
   * In which case it is possible that everything in the runtime will be 
   * terminated abruptly as well.</p>
   */
  public void shutdown();




  /**
   * The Job Directory is a location on the local file system in which we can 
   * write and read files this this engine and its components can use.
   * 
   * <p>This is a place where data can be persisted between multiple runs of 
   * the same job. The output of a job can be read in later. For example, the 
   * job can store its last run time in this directory and when it runs again, 
   * it can query that time to determine how long it has been since it last 
   * ran.</p>
   * 
   * @return the directory in which we can read and write our own files.
   */
  public File getJobDirectory();




  /**
   * The Work Directory is a common location of many jobs in the system.
   * 
   * <p>This is a place where data can be persisted between jobs. For example,
   * a job may generate a list of users it has imported from another system as 
   * a file and multiple other jobs can use that file as input to their 
   * operations. These other jobs do not know how that file got generated, only 
   * that it has the data it needs. This provides a level of abstraction 
   * between jobs.</p>
   * 
   * <p>Also, this directory can be used to store the state of the jobs and 
   * other jobs can determine if they should run based on the presence of data 
   * in that directory.</p>
   *  
   * @return the directory in which we can read and write files to share with other engines and jobs.
   */
  public File getWorkDirectory();




  /**
   * This allows a component to be set which will initialize logging in the
   * engine in coordination with its life cycle and for loggers to use value in 
   * the engines transform context.
   * 
   * @param logmgr The logging manager to use for this engine. 
   */
  public void setLogManager( LogManager logmgr );




  /**
   * Adds a frame filter to the engine.
   * 
   * <p>Frame Filters (or just "filters") are designed to remove data from the 
   * stream which is not to be processed. For example, some readers have the 
   * ability to read many different frame formats and the engine is intended to 
   * handle only one.</p>
   * 
   * <p>This is not to be confused with Validators which generate error events 
   * when data does not match expectations. Filters prevent data from entering 
   * the processing stream and if a frame is rejected, it never is validate or
   * otherwise processed by the transform engine.</p>
   * 
   * <p>The engine will apply filters in the order they are added. This allows 
   * for creating a few {@code accept} filters and one catch-all {@code reject} 
   * filter to filter-out any unexpected data.</p>
   * 
   * @param filter the filter to add
   * 
   * @return the sequence in which it will be executed
   */
  public int addFilter( FrameFilter filter );

}