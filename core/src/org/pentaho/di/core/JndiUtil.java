/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2013 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.core;

import java.io.File;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.pentaho.di.core.exception.KettleException;

public class JndiUtil {
  private static final AtomicBoolean USE_SIMPLE_JNDI = new AtomicBoolean( false );
  private static String path = null;

  public static void initJNDI() throws KettleException {
    USE_SIMPLE_JNDI.set( true );
    path = Const.JNDI_DIRECTORY;

    if ( path == null || path.equals( "" ) ) {
      try {
        File file = new File( "simple-jndi" );
        path = file.getCanonicalPath();
      } catch ( Exception e ) {
        throw new KettleException( "Error initializing JNDI", e );
      }
      Const.JNDI_DIRECTORY = path;
    }
  }

  public static Hashtable<String, String> getJndiEnv() {
    Hashtable<String, String> env = new Hashtable<String, String>();
    if ( USE_SIMPLE_JNDI.get() ) {
      env.put( "java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory" );
      env.put( "org.osjava.sj.root", path );
      env.put( "org.osjava.sj.delimiter", "/" );
    }
    return env;
  }
}
