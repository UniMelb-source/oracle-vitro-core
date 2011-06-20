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

package edu.cornell.mannlib.vitro.webapp.filters;

import com.hp.hpl.jena.query.DataSource;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;


import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryOracle;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;
import edu.cornell.mannlib.vitro.webapp.utils.VivoOracleUtils;

public class WebappDaoFactoryOraclePrep implements Filter {
	
	private final static Log log = LogFactory.getLog(WebappDaoFactoryOraclePrep.class);
	
	ServletContext _ctx;

    /**
     * The filter will be applied to all incoming urls,
     this is a list of URI patterns to skip.  These are
     matched against the requestURI sans query parameters,
     * e.g.
     * "/vitro/index.jsp"
     * "/vitro/themes/enhanced/css/edit.css"
     *
     * These patterns are from VitroRequestPrep.java
    */
    Pattern[] skipPatterns = {
            Pattern.compile(".*\\.(gif|GIF|jpg|jpeg)$"),
            Pattern.compile(".*\\.css$"),
            Pattern.compile(".*\\.js$"),
            Pattern.compile("/.*/themes/.*/site_icons/.*"),
            Pattern.compile("/.*/images/.*")
    };
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		
		if ((request.getAttribute("WebappDaoFactoryOraclePrep.setup") != null) ) {
			// don't run multiple times
		    filterChain.doFilter(request, response);
			return;
		}
		
		for( Pattern skipPattern : skipPatterns){
            Matcher match =skipPattern.matcher( ((HttpServletRequest)request).getRequestURI() );
            if( match.matches()  ){
                log.debug("request matched a skipPattern, skipping VitroRequestPrep"); 
                filterChain.doFilter(request, response);
                return;
            }
        }
		
        OntModelSelector oms = (OntModelSelector) _ctx.getAttribute("unionOntModelSelector");
        String defaultNamespace = (String) _ctx.getAttribute("defaultNamespace");

		//Dataset dataset = null;
		WebappDaoFactory wadf = null;
		
		try {		
		    if (oms == null) {
		        throw new RuntimeException("oms not properly set up");
		    }            

            VivoOracleUtils vou = new VivoOracleUtils();
            DataSource dataset = DatasetFactory.create();
            Model m = oms.getFullModel();
            dataset.addNamedModel(vou.validateModelName(JenaDataSourceSetupBase.JENA_DB_MODEL), m);
            dataset.setDefaultModel(m);
            vou.close();

            VitroRequest vreq = new VitroRequest((HttpServletRequest) request);
            wadf = new WebappDaoFactoryOracle(oms, defaultNamespace, null, null);
            vreq.setWebappDaoFactory(wadf);
            vreq.setFullWebappDaoFactory(wadf);
            vreq.setDataset(dataset);            

		} catch (Throwable t) {
			log.error("Unable to filter request to set up Oracle connection", t);
		}
		
		request.setAttribute("WebappDaoFactoryOraclePrep.setup", 1);        
		
		try {
			filterChain.doFilter(request, response);
			return;
		} finally {
			/*if (conn != null)
            {
				try
                {
                    conn.close();
                }
                catch(SQLException e)
                {
                    log.error(e);
                }
			}*/
			/*if (dataset != null) {
			    dataset.close();
			}
			if (wadf != null) {
			    wadf.close();
			}*/
		}
		
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			_ctx = filterConfig.getServletContext();
		} catch (Throwable t) {
			log.error("Unable to initialize WebappDaoFactoryOraclePrep", t);
		}		
	}
	
	public void destroy() {
		// no destroy actions
	}   

}
