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

package edu.cornell.mannlib.vitro.webapp.servlet.setup;

import com.hp.hpl.jena.ontology.OntModelSpec;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import edu.cornell.mannlib.vitro.webapp.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OracleJenaModelMaker;
import edu.cornell.mannlib.vitro.webapp.utils.VivoOracleUtils;

/**
 * WARNING: This configuration may not reconnect to MySQL after connection timeouts.
 * @author bjl23
 *
 */
public class JenaPersistentOracleDataSourceSetup extends JenaDataSourceSetupBase implements ServletContextListener {
	
	private static final Log log = LogFactory.getLog(JenaPersistentOracleDataSourceSetup.class.getName());

    //ModelOracleSem dbModel, accountsDbModel, infDbModel, appDbModel;
    //Model dbModel, accountsDbModel, infDbModel, appDbModel;
	
	public void contextInitialized(ServletContextEvent sce)
    {
        log.info("JenaPersistentOracleDataSourceSetup intialisation");

        String jdbcUrl = ConfigurationProperties.getProperty("VitroConnection.DataSource.url");
        String username = ConfigurationProperties.getProperty("VitroConnection.DataSource.username");
        String password = ConfigurationProperties.getProperty("VitroConnection.DataSource.password");

        //Oracle oracle = new Oracle(jdbcUrl, username, password);

        OracleJenaModelMaker ojmm = new OracleJenaModelMaker(jdbcUrl, username, password);

        try
        {
            VivoOracleUtils vou = new VivoOracleUtils();
            Model memModel = ojmm.getModel(vou.validateModelName(JENA_DB_MODEL));
            vou.close();
            //dbModel = ModelOracleSem.createOracleSemModel(oracle, validateModelName(JENA_DB_MODEL));
        	//OracleGraphGenerator dbGen = new OracleGraphGenerator(jdbcUrl, username, password, JENA_DB_MODEL);
            //Graph g = dbGen.generateGraph();

            //memModel = OntologyUtils.createInMemOwlModel(oracle, "VITROKB2");

            if (memModel.isEmpty()) {
                long startTime = System.currentTimeMillis();
                System.out.println("Reading ontology files into database");
                ServletContext ctx = sce.getServletContext();
                readOntologyFilesInPathSet(USERPATH, ctx, memModel);
                readOntologyFilesInPathSet(SYSTEMPATH, ctx, memModel);
                //NEW
                //readOntologyFilesInPathSet(SUBMODELS, ctx, dbModel);
                System.out.println((System.currentTimeMillis()-startTime)/1000+" seconds to populate DB");
            }

            //OntModel memModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dbModel);

            if(memModel == null)
            {
                log.info("memModel is NULL");
            }

            //memModel.register(new MemToRDBModelSynchronizer(dbGen));
            //memModel.register(new OracleSynchronizer(dbModel));

            sce.getServletContext().setAttribute("jenaOntModel", ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, memModel));
            
        } catch (Throwable t) {
			System.out.println("**** ERROR *****");
            System.out.println("Vitro unable to open Jena database model.");
			System.out.println("Check that the configuration properties file has been created in WEB-INF/classes, ");
			System.out.println("and that the database connection parameters are accurate. ");
			System.out.println("****************");
        }
        
        // default inference graph
        try {
            VivoOracleUtils vou = new VivoOracleUtils();
            Model infModel = ojmm.getModel(vou.validateModelName(JENA_INF_MODEL));
            vou.close();
            //infDbModel = ModelOracleSem.createOracleSemModel(oracle, validateModelName(JENA_INF_MODEL));
            //OntModel infModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC, dbInfModel);

            //OracleGraphGenerator dbGen = new OracleGraphGenerator(jdbcUrl, username, password, JENA_INF_MODEL);
            //Graph g = dbGen.generateGraph();
            //Model infDbModel = ModelFactory.createModelForGraph(g);

           // OntModel infModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, infDbModel);
            //infModel.register(new OracleSynchronizer(infDbModel));
	    	//OntModel infModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC);
	    	//infModel.add(infDbModel);

            //infModel.register(new MemToRDBModelSynchronizer(dbGen));
            //infDbModel.close();

            sce.getServletContext().setAttribute("inferenceOntModel", ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, infModel));
        } catch (Throwable e) {
        	log.error(e, e);
        }           

        try {
            VivoOracleUtils vou = new VivoOracleUtils();
            Model userAccountsModel = ojmm.getModel(vou.validateModelName(JENA_USER_ACCOUNTS_MODEL));
            vou.close();
            //accountsDbModel = ModelOracleSem.createOracleSemModel(oracle, validateModelName(JENA_USER_ACCOUNTS_MODEL));

        	/*OracleGraphGenerator dbGen = new OracleGraphGenerator(jdbcUrl, username, password, JENA_USER_ACCOUNTS_MODEL);
            Graph g = dbGen.generateGraph();
            Model accountsDbModel = ModelFactory.createModelForGraph(g);*/

            if (userAccountsModel.isEmpty())
            {
                log.info("-- user accounts model is empty --");
				readOntologyFilesInPathSet(AUTHPATH, sce.getServletContext(),userAccountsModel);
                createInitialAdminUser(userAccountsModel);
            }

	    	//OntModel userAccountsModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC);
	    	//userAccountsModel.add(accountsDbModel);

           // OntModel userAccountsModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, accountsDbModel);
            //userAccountsModel.register(new OracleSynchronizer(accountsDbModel));

            //userAccountsModel.register(new MemToRDBModelSynchronizer(dbGen));
            //accountsDbModel.close();

            //OntModel userAccountsModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC, accountsDbModel);

            sce.getServletContext().setAttribute("userAccountsOntModel", ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, userAccountsModel));
        } catch (Throwable e) {
        	log.error(e, e);
        }

        try {
            VivoOracleUtils vou = new VivoOracleUtils();
        	Model appModel = ojmm.getModel(vou.validateModelName(JENA_DISPLAY_METADATA_MODEL));
            vou.close();

            /*OracleGraphGenerator dbGen = new OracleGraphGenerator(jdbcUrl, username, password, JENA_DISPLAY_METADATA_MODEL);
            Graph g = dbGen.generateGraph();
            Model appDbModel = ModelFactory.createModelForGraph(g);*/
            
            //appDbModel = ModelOracleSem.createOracleSemModel(oracle, validateModelName(JENA_DISPLAY_METADATA_MODEL));

            if (appModel.isEmpty())
            {
				readOntologyFilesInPathSet(APPPATH, sce.getServletContext(),appModel);
            }

	    	//OntModel appModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC);
	    	//appModel.add(appDbModel);

            //appModel.register(new MemToRDBModelSynchronizer(dbGen));
            //appDbModel.close();

            //OntModel appModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, appDbModel);
            //appModel.register(new OracleSynchronizer(appDbModel));

            //OntModel appModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC, appDbModel);

            sce.getServletContext().setAttribute("displayOntModel", ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, appModel));
        } catch (Throwable e) {
        	log.error(e, e);
        }

	}
	
	public void contextDestroyed(ServletContextEvent sce)
    {
        /*log.info("------------------ Closing model");
        this.dbModel.close();
        this.infDbModel.close();
        this.appDbModel.close();
        this.accountsDbModel.close();
        log.info("------------------ Model closed");*/
	}    
	
}
