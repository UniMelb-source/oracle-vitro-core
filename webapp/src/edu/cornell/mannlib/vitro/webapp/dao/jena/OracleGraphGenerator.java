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
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Graph;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;

import oracle.spatial.rdf.client.jena.Oracle;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import oracle.spatial.rdf.client.jena.GraphOracleSem;

public class OracleGraphGenerator implements GraphGenerator
{

    private static final Log log = LogFactory.getLog(OracleGraphGenerator.class.getName());    
    private Connection connection;
    private Oracle oracle;
    private String graphID = null;    

    public OracleGraphGenerator(String graphID)
    {
        this.oracle = JenaDataSourceSetupBase.getOracle();
        this.graphID = validateModelName(graphID);
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

    private String validateModelName(String modelName)
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

    public Graph generateGraph()
    {        
        GraphOracleSem oracleGraph = null;
        
        try
        {
            if ((this.connection == null) || (this.connection.isClosed()))
            {
                this.connection = oracle.getConnection();
            }

            oracleGraph = new GraphOracleSem(oracle, graphID);

            if(oracleGraph == null)
            {
                log.info("\nGraph Is Empty\n");
            }
        }
        catch(SQLException e)
        {
            System.out.println("Error regenerating Oracle graph: " + e);
        }
        
        return oracleGraph;
    }

    public Connection getConnection()
    {
        return connection;
    }
}
