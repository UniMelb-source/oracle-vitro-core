/*
Copyright (c) 2011, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.auth.policy.setup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.SelfEditing2RoleIdentifierFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.ServletIdentifierBundleFactory;

/**
 * Add the SelfEditing2RoleIdentifier factory to the IdentifierFactory list
 * in the servlet context.
 * 
 * This should be added to the IdentifierFactory list after the
 * SelfEditingIdentiferFactory.
 * 
 * This only sets up a IdentifierFactoy that maps SelfEditing identifiers to
 * roles associated with the Individual that represents the self editor.  This
 * does not set up a policy or the SelfEditingIdentifierFactory.
 * 
 * @author bdc34
 *
 */
public class SelfEditing2RoleIdentifierSetup implements ServletContextListener{

    private static final Log log = LogFactory.getLog(SelfEditing2RoleIdentifierSetup.class.getName());
    
    public void contextDestroyed(ServletContextEvent sce) {
        //do nothing            
    }

    public void contextInitialized(ServletContextEvent sce) {
        try{
            log.debug("Setting up SelfEditing2RoleIdentifier");                                

            SelfEditing2RoleIdentifierFactory niif =new SelfEditing2RoleIdentifierFactory();
            ServletIdentifierBundleFactory.addIdentifierBundleFactory(sce.getServletContext(), niif);

            log.debug( "SelfEditing2RoleIdentifier has been setup. " );            
        }catch(Exception e){
            log.error("could not run SelfEditing2RoleIdentifier: " + e);
            e.printStackTrace();
        }
    }

}