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

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import com.hp.hpl.jena.query.DataSource;
import java.util.HashSet;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;
import edu.cornell.mannlib.vitro.webapp.utils.VivoOracleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class WebappDaoFactoryOracle extends WebappDaoFactoryJena {
	
    private SDBDatasetMode datasetMode = SDBDatasetMode.ASSERTIONS_AND_INFERENCES;
    private final static Log log = LogFactory.getLog(WebappDaoFactoryOracle.class);
    
	/**
	 * For use when any database connection associated with the Dataset
	 * is managed externally
	 */
	public WebappDaoFactoryOracle(OntModelSelector ontModelSelector, Dataset dataset) {
		super(ontModelSelector);
		this.dwf = new StaticDatasetFactory(dataset);
	}
	
    /**
     * For use when any database connection associated with the Dataset
     * is managed externally
     */
	public WebappDaoFactoryOracle(OntModelSelector ontModelSelector,
	                            Dataset dataset, 
	                            String defaultNamespace, 
	                            HashSet<String> nonuserNamespaces, 
	                            String[] preferredLanguages) {
		super(ontModelSelector, defaultNamespace, nonuserNamespaces, preferredLanguages);
        this.dwf = new StaticDatasetFactory(dataset);
	}
	
    /**
     * For use when any Dataset access should get a temporary DB connection
     * from a pool
     */
    public WebappDaoFactoryOracle(OntModelSelector ontModelSelector,                                
                                String defaultNamespace, 
                                HashSet<String> nonuserNamespaces, 
                                String[] preferredLanguages) {
        super(ontModelSelector, defaultNamespace, nonuserNamespaces, preferredLanguages);

        VivoOracleUtils vou = new VivoOracleUtils();
        DataSource dataset = DatasetFactory.create();
        Model m = ontModelSelector.getFullModel();
        dataset.addNamedModel(vou.validateModelName(JenaDataSourceSetupBase.JENA_DB_MODEL), m);
        dataset.setDefaultModel(m);
        vou.close();

        this.dwf = new StaticDatasetFactory(dataset);
        //this.dwf = new ReconnectingDatasetFactory(ontModelSelector);
    }
    
    /**
     * For use when any Dataset access should get a temporary DB connection
     * from a pool, and access to the inference graph needs to be specified.
     */
    public WebappDaoFactoryOracle(OntModelSelector ontModelSelector,
                                String defaultNamespace, 
                                HashSet<String> nonuserNamespaces, 
                                String[] preferredLanguages,
                                SDBDatasetMode datasetMode) {
        super(ontModelSelector, defaultNamespace, nonuserNamespaces, preferredLanguages);
        //this.dwf = new ReconnectingDatasetFactory(ontModelSelector);

        VivoOracleUtils vou = new VivoOracleUtils();
        DataSource dataset = DatasetFactory.create();
        Model m = ontModelSelector.getFullModel();
        dataset.addNamedModel(vou.validateModelName(JenaDataSourceSetupBase.JENA_DB_MODEL), m);
        dataset.setDefaultModel(m);
        vou.close();

        this.dwf = new StaticDatasetFactory(dataset);

        this.datasetMode = datasetMode;
    }
   
    
    public WebappDaoFactoryOracle(WebappDaoFactoryOracle base, String userURI) {
        super(base.ontModelSelector);
        this.ontModelSelector = base.ontModelSelector;
        this.defaultNamespace = base.defaultNamespace;
        this.nonuserNamespaces = base.nonuserNamespaces;
        this.preferredLanguages = base.preferredLanguages;
        this.userURI = userURI;
        this.flag2ValueMap = base.flag2ValueMap;
        this.flag2ClassLabelMap = base.flag2ClassLabelMap;
        this.dwf = base.dwf;
    }
	
	@Override
    public IndividualDao getIndividualDao() {
        if (entityWebappDao != null)
            return entityWebappDao;
        else
            return entityWebappDao = new IndividualDaoOracle(
                    dwf, datasetMode, this);
    }
	
	@Override
	public DataPropertyStatementDao getDataPropertyStatementDao() {
		if (dataPropertyStatementDao != null) 
			return dataPropertyStatementDao;
		else
			return dataPropertyStatementDao = new DataPropertyStatementDaoOracle(
			        dwf, datasetMode, this);
	}
	
	@Override
	public ObjectPropertyStatementDao getObjectPropertyStatementDao() {
		if (objectPropertyStatementDao != null) 
			return objectPropertyStatementDao;
		else
			return objectPropertyStatementDao = 
			    new ObjectPropertyStatementDaoOracle(dwf, datasetMode, this);
	}
	
	@Override
	public VClassDao getVClassDao() {
		if (vClassDao != null) 
			return vClassDao;
		else
			return vClassDao = new VClassDaoOracle(dwf, datasetMode, this);
	}
	
	public WebappDaoFactory getUserAwareDaoFactory(String userURI) {
        // TODO: put the user-aware factories in a hashmap so we don't keep re-creating them
        return new WebappDaoFactoryOracle(this, userURI);
    }
	
	public enum SDBDatasetMode {
	    ASSERTIONS_ONLY, INFERENCES_ONLY, ASSERTIONS_AND_INFERENCES
	}
	
	public static String getFilterBlock(String[] graphVars, 
	                                    SDBDatasetMode datasetMode) {
        VivoOracleUtils vou = new VivoOracleUtils();
	    StringBuffer filterBlock = new StringBuffer();
	    for (int i = 0; i < graphVars.length; i++) {
	        switch (datasetMode) {
	            case ASSERTIONS_ONLY :  
	                    filterBlock.append("FILTER (")
	                        .append("(!bound(").append(graphVars[i])
	                        .append(")) || (")
	                        .append(graphVars[i])
	                        .append(" != <")
	                        .append(vou.validateModelName(JenaDataSourceSetupBase.JENA_INF_MODEL))
	                        .append("> && ").append(graphVars[i])
	                        .append(" != <")
	                        .append(vou.validateModelName(JenaDataSourceSetupBase.JENA_TBOX_INF_MODEL))
	                        .append(">) ) \n");
	                    break;
	            case INFERENCES_ONLY :  
                    filterBlock.append("FILTER (")
                        .append("(!bound(").append(graphVars[i])
                        .append(")) || (")
                        .append(graphVars[i])
                        .append(" = <")
                        .append(vou.validateModelName(JenaDataSourceSetupBase.JENA_INF_MODEL))
                        .append("> || ").append(graphVars[i])
                        .append(" = <")
                        .append(vou.validateModelName(JenaDataSourceSetupBase.JENA_TBOX_INF_MODEL))
                        .append(">) )\n");
                    break;
	            default:
	                break;
	        }
	    }
        vou.close();
	    return filterBlock.toString();
	}
	
	private class ReconnectingDatasetFactory implements DatasetWrapperFactory
    {
        @Override
	    public DatasetWrapper getDatasetWrapper()
        {
            throw new UnsupportedOperationException("Oracle reconnecting dataset not implemented");
	        /*try
            {                
                
            } catch (Exception sqe) {
                throw new RuntimeException("Unable to connect to database", sqe);
            }*/
	    }        
	    
	}    
	
}
