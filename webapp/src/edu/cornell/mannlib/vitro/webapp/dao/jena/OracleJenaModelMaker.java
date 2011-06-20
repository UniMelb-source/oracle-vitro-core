/*
Copyright (c) 2010, Cornell University
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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphMaker;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;
import edu.cornell.mannlib.vitro.webapp.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;

import oracle.spatial.rdf.client.jena.ModelOracleSem;
import oracle.spatial.rdf.client.jena.Oracle;
import oracle.spatial.rdf.client.jena.OracleUtils;

/**
 * This is a bit of a nutty idea but we'll see if it works.  This can wrap an RDBModelMaker and return a memory model 
 * synced with the underlying RDB model.  Note, however, that a Jena RDBModelMaker won't auto-reconnect.  Maybe I can 
 * revisit the reconnecting IDBConnection issue or make a special RDBModelMaker that uses the reconnection system.
 *  
 * @author bjl23
 *
 */
public class OracleJenaModelMaker implements ModelMaker
{

    private static final Log log = LogFactory.getLog(VitroJenaModelMaker.class);
    private static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";
    
    private HashMap<String, Model> modelCache;
    private HttpServletRequest request = null;

    private String jdbcUrl, username, password;

    public OracleJenaModelMaker(String jdbcUrl, String username, String password)
    {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;                

        String driverName = ConfigurationProperties.getProperty("VitroConnection.DataSource.driver");
        // This property is no longer used?
        // We'll change it all around in 1.2 anyway.
        if (driverName == null)
        {
            driverName = DEFAULT_DRIVER;
        }
        
        modelCache = new HashMap<String, Model>();
    }

    protected HashMap<String, Model> getCache()
    {
        return this.modelCache;
    }

    public void close()
    {
        // TODO Auto-generated method stub
        // So, in theory, this should close database connections and drop references
        // to in-memory models and all that kind of stuff.
    }

    public Model createModel(String arg0)
    {
        Model specialModel = null;
        if ((specialModel = getSpecialModel(arg0)) != null)
        {
            return specialModel;
        }
        Model cachedModel = modelCache.get(arg0);
        if (cachedModel != null)
        {
            log.debug("Returning " + arg0 + " (" + cachedModel.hashCode() + ") from cache");
            return cachedModel;
        } else
        {
            Model newModel = makeDBModel(arg0);
            modelCache.put(arg0, newModel);
            log.debug("Returning " + arg0 + " (" + newModel.hashCode() + ") from cache");
            return newModel;
        }
    }

    public Model createModel(String arg0, boolean arg1)
    {
        Model specialModel = null;
        if ((specialModel = getSpecialModel(arg0)) != null)
        {
            return specialModel;
        }
        Model cachedModel = modelCache.get(arg0);
        if (cachedModel != null)
        {
            return cachedModel;
        } else
        {
            Model newModel = makeDBModel(arg0);
            modelCache.put(arg0, newModel);
            return newModel;
        }
    }

    public GraphMaker getGraphMaker()
    {
        throw new UnsupportedOperationException(this.getClass().getName()
                + " does not support getGraphMaker()");
    }

    public boolean hasModel(String arg0)
    {
        Oracle oracle = JenaDataSourceSetupBase.getOracle();

        List<String> modelList = null;

        try
        {
            modelList = OracleUtils.getAllReadableModels(oracle);
            oracle.dispose();
        } catch (Exception e)
        {
            System.out.println(e);
        }

        if (modelList.contains(arg0))
        {            
            return true;
        }
        else
        {
            return false;
        }
    }

    public ExtendedIterator listModels()
    {
        Oracle oracle = JenaDataSourceSetupBase.getOracle();
        List<String> modelList = null;

        try
        {
            modelList = OracleUtils.getAllReadableModels(oracle);
            oracle.dispose();
            //return WrappedIterator.create(modelList.iterator());
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        return WrappedIterator.create(modelList.iterator());
    }

    public Model openModel(String arg0, boolean arg1)
    {
        Model specialModel = null;
        if ((specialModel = getSpecialModel(arg0)) != null)
        {
            return specialModel;
        }
        Model cachedModel = modelCache.get(arg0);
        if (cachedModel != null)
        {
            return cachedModel;
        } else
        {
            Model newModel = makeDBModel(arg0);
            modelCache.put(arg0, newModel);
            return newModel;
        }
    }

    public void removeModel(String arg0)
    {
        Oracle oracle = JenaDataSourceSetupBase.getOracle();
        Model m = modelCache.get(arg0);
        if (m != null)
        {
            m.close();
            modelCache.remove(arg0);
        }

        //Oracle way

        try
        {
            OracleUtils.dropSemanticModel(oracle, arg0);
            oracle.dispose();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public Model addDescription(Model arg0, Resource arg1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Model createModelOver(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Model getDescription()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Model getDescription(Resource arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Model createDefaultModel()
    {
        throw new UnsupportedOperationException(this.getClass().getName()
                + " does not support createDefaultModel()");
    }

    public Model createFreshModel()
    {
        throw new UnsupportedOperationException(this.getClass().getName()
                + " does not support createFreshModel()");
    }

    @Deprecated
    public Model createModel()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public Model getModel()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Model openModel(String arg0)
    {
        Model specialModel = null;
        if ((specialModel = getSpecialModel(arg0)) != null)
        {
            return specialModel;
        }
        Model cachedModel = modelCache.get(arg0);
        if (cachedModel != null)
        {
            return cachedModel;
        } else
        {
            Model newModel = makeDBModel(arg0);
            modelCache.put(arg0, newModel);
            return newModel;
        }
    }

    public Model openModelIfPresent(String arg0)
    {
        Model specialModel = null;
        if ((specialModel = getSpecialModel(arg0)) != null)
        {
            return specialModel;
        }
        Model cachedModel = modelCache.get(arg0);
        if (cachedModel != null)
        {
            return cachedModel;
        } else
        {
            Model newModel = makeDBModel(arg0);
            modelCache.put(arg0, newModel);
            return newModel;
        }
    }

    public Model getModel(String arg0)
    {
        Oracle oracle = JenaDataSourceSetupBase.getOracle();

        Model specialModel = null;
        if ((specialModel = getSpecialModel(arg0)) != null)
        {
            return specialModel;
        }
        Model cachedModel = modelCache.get(arg0);
        if (cachedModel != null)
        {
            return cachedModel;
        }
        else if(ifModelIsInDB(arg0))
        {
            Model newModel = null;
            
            try
            {
                newModel = ModelOracleSem.createOracleSemModel(oracle, arg0);
            }
            catch(SQLException e)
            {
                System.out.println("Error getting Oracle DB model: " + e);
            }

            modelCache.put(arg0, newModel);

            return newModel;
        }
        else
        {
            Model newModel = makeDBModel(arg0);
            modelCache.put(arg0, newModel);
            return newModel;
        }
    }

    public Model getModel(String arg0, ModelReader arg1)
    {
        Oracle oracle = JenaDataSourceSetupBase.getOracle();
        
        Model specialModel = null;
        if ((specialModel = getSpecialModel(arg0)) != null)
        {
            return specialModel;
        }
        Model cachedModel = modelCache.get(arg0);
        if (cachedModel != null)
        {
            return cachedModel;
        }
        else if(ifModelIsInDB(arg0))
        {
            Model newModel = null;

            try
            {
                newModel = ModelOracleSem.createOracleSemModel(oracle, arg0);
            }
            catch(SQLException e)
            {
                System.out.println("Error getting Oracle DB model: " + e);
            }

            modelCache.put(arg0, newModel);
            return newModel;
        }
        else
        {
            Model newModel = makeDBModel(arg0);
            modelCache.put(arg0, newModel);
            return newModel;
        }
    }

    private boolean ifModelIsInDB(String modelName)
    {
        Oracle oracle = JenaDataSourceSetupBase.getOracle();
        
        List<String> modelList = null;

        try
        {
            modelList = OracleUtils.getAllReadableModels(oracle);
            oracle.dispose();
            //return WrappedIterator.create(modelList.iterator());
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        if (modelList.contains(modelName.toUpperCase()))
        {
            return true;
        }

        return false;
    }

    /**
     * This will trap for strings like "vitro:jenaOntModel" and return the
     * appropriate in-memory model used by the current webapp context.
     * To use this functionality, the VitroJenaModelMaker must be constructed
     * with a VitroRequest parameter
     */
    private Model getSpecialModel(String modelName)
    {
        if (request != null)
        {
            if ("vitro:jenaOntModel".equals(modelName))
            {
                Object sessionOntModel = request.getSession().getAttribute("jenaOntModel");
                if (sessionOntModel != null && sessionOntModel instanceof OntModel)
                {
                    log.debug("Returning jenaOntModel from session");
                    return (OntModel) sessionOntModel;
                } else
                {
                    log.debug("Returning jenaOntModel from context");
                    return (OntModel) request.getSession().getServletContext().getAttribute("jenaOntModel");
                }
            } else if ("vitro:baseOntModel".equals(modelName))
            {
                Object sessionOntModel = request.getSession().getAttribute("baseOntModel");
                if (sessionOntModel != null && sessionOntModel instanceof OntModel)
                {
                    return (OntModel) sessionOntModel;
                } else
                {
                    return (OntModel) request.getSession().getServletContext().getAttribute("baseOntModel");
                }
            } else if ("vitro:inferenceOntModel".equals(modelName))
            {
                Object sessionOntModel = request.getSession().getAttribute("inferenceOntModel");
                if (sessionOntModel != null && sessionOntModel instanceof OntModel)
                {
                    return (OntModel) sessionOntModel;
                } else
                {
                    return (OntModel) request.getSession().getServletContext().getAttribute("inferenceOntModel");
                }
            } else
            {
                return null;
            }
        }
        return null;
    }

    private OntModel makeDBModel(String jenaDbModelName)
    {
        OntModel memCache = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        
        OracleGraphGenerator gen = new OracleGraphGenerator(jenaDbModelName);

        Graph g = gen.generateGraph();
        Model m = ModelFactory.createModelForGraph(g);
        memCache.add(m);
        memCache.register(new MemToRDBModelSynchronizer(gen));
        m.close();                
        
        // This next piece is so that we return a fresh model object each time so we don't get cross-contamination of extra listeners, etc.
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, ModelFactory.createUnion(memCache, ModelFactory.createDefaultModel()));
    }
}
