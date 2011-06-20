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

package edu.cornell.mannlib.vitro.webapp.utils;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oracle.spatial.rdf.client.jena.GraphOracleSem;
import oracle.spatial.rdf.client.jena.ModelOracleSem;
import oracle.spatial.rdf.client.jena.Oracle;
import oracle.spatial.rdf.client.jena.OracleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VivoOracleUtils
{
    private static final Log log = LogFactory.getLog(VivoOracleUtils.class.getName());
    
    private ArrayList<Model> internalModelList = new ArrayList<Model>();
    private ArrayList<OntModel> internalOntModelList = new ArrayList<OntModel>();        

    private Oracle oracle;

    public VivoOracleUtils()
    {
        oracle = JenaDataSourceSetupBase.getOracle();
    }

    private Oracle getOracle()
    {
        return oracle;
    }

    public List<String> listModels()
    {        

        List<String> modelList = null;
        
        try
        {
            modelList = OracleUtils.getAllReadableModels(oracle);
        }
        catch(SQLException e)
        {
            log.error(e);
        }

        return modelList;
    }

    public ModelOracleSem getModel(String modelName)
    {
        Oracle oracle = JenaDataSourceSetupBase.getOracle();
        ModelOracleSem m = null;
        
        try
        {
            GraphOracleSem g = new GraphOracleSem(getOracle(), modelName);
            m = new ModelOracleSem(g);
            //m = ModelFactory.createModelForGraph(g);
            internalModelList.add(m);
        }
        catch(SQLException e)
        {
            log.error(e);
        }
        
        return m;
    }

    public OntModel getOntModel(String modelName)
    {
        OntModel m = null;

        try
        {
            GraphOracleSem g = new GraphOracleSem(getOracle(), modelName);
            Model model = new ModelOracleSem(g);
            //Model model = ModelFactory.createModelForGraph(g);
            m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
            internalModelList.add(model);
        }
        catch(SQLException e)
        {
            log.error(e);
        }

        internalOntModelList.add(m);

        return m;
    }        

    public void close()
    {
        try
        {
            for(Model m : internalModelList)
            {
                m.close();
            }

            for(OntModel m : internalOntModelList)
            {
                m.close();
            }

            oracle.dispose();
        }
        catch(Exception e)
        {
            log.error(e);
        }
    }

    private int getLastMatch(String searchPattern,String textString)
    {
       int index = -1;
       Pattern pattern = Pattern.compile(searchPattern);
       Matcher matcher = pattern.matcher(textString);

       while(matcher.find()) {
               index = matcher.start();
       }
       return index;
   }

    public String validateModelName(String modelName)
    {
        String newModelName = modelName;

        //VIVO has url's for its initial setup models, we need to modify
        //these for Oracle
        if(modelName.startsWith("http://"))
        {
            int slashIndex = getLastMatch("/", modelName);
            newModelName = modelName.substring(slashIndex);

            //remove all non alphanumeric chars
            newModelName = newModelName.replaceAll("[^a-zA-Z0-9]", "");
        }

        if(newModelName.length() >= 22)
        {
            //truncate to avoid upsetting the Oracle API
            newModelName = newModelName.substring(0, 21);

        }
        return newModelName;
    }
}
