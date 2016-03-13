/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.batch.http;

import coyote.batch.Service;
import coyote.dataframe.DataFrame;
import coyote.loader.cfg.ConfigurationException;


/**
 * This represents the interface all StaticManagers must implement so commands 
 * can be implemented in the HTTP server.
 */
public interface ManagerFactoryBinder {

  /**
   * Create a manager with the given configuration for the given Service job.
   * 
   * @param cfg Optional configuration for the returned HttpManager
   * @param svc The service the returned managager is to manage.
   * 
   * @return A configured HttpManager connected to the given Batch service
   * 
   * @throws ConfigurationException if the passed connection was invalid
   */
  public HttpManager createManager( DataFrame cfg, Service svc ) throws ConfigurationException;
}
