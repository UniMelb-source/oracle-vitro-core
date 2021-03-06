<%--
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
--%>

<%@ page language="java" %>
<%@ page errorPage="error.jsp"%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Portal" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vedit.beans.LoginStatusBean" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%><%/* this odd thing points to something in web.xml */ %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.PortalWebUtil" %>

<%
// application variables not stored in application bean
    final int CALS_IMPACT = 6;
    final int CALS_SEARCHBOX_SIZE = 25;
    final int VIVO_SEARCHBOX_SIZE = 20;

    LoginStatusBean loginBean = LoginStatusBean.getBean(request);
    boolean isEditor = loginBean.isLoggedInAtLeast(LoginStatusBean.EDITOR);
    String loginName = loginBean.getUsername();

    VitroRequest vreq = new VitroRequest(request);
    ApplicationBean appBean = vreq.getAppBean();
    Portal portal = vreq.getPortal();
    PortalWebUtil.populateSearchOptions(portal, appBean, vreq.getWebappDaoFactory().getPortalDao());
    PortalWebUtil.populateNavigationChoices(portal, request, appBean, vreq.getWebappDaoFactory().getPortalDao());

    String fixedTabStr = (fixedTabStr = request.getParameter("fixed")) == null ? null : fixedTabStr.equals("") ? null : fixedTabStr;
    final String DEFAULT_SEARCH_METHOD = "fulltext";

%>
<c:set var="portal" value="${requestScope.portalBean}"/>
<c:set var="appBean" value="${requestScope.appBean}"/>

<div id="header">

    <c:set var='themeDir' >
        <c:out value='${portal.themeDir}' />
    </c:set>

    <table id="head"><tr>
    <td id="LogotypeArea">
    	<table><tr>
    	<td>

		<%
		   String homeURL = (portal.getRootBreadCrumbURL()!=null && portal.getRootBreadCrumbURL().length()>0) ?
				   portal.getRootBreadCrumbURL() : request.getContextPath()+"/";
		%>

        <a href="<%=homeURL%>">
            
           <img class="closecrop" src="${themeDir}site_icons/<%=appBean.getRootLogotypeImage()%>"
                                width="<%=appBean.getRootLogotypeWidth()%>"
                     height="<%=appBean.getRootLogotypeHeight()%>"
                     alt="<%=appBean.getRootLogotypeTitle()%>"/></a>
           
        
        </td><td>
        <a href="<%=homeURL%>">
            <img class="closecrop" src="${themeDir}site_icons/<%=portal.getLogotypeImage()%>"
                     width="<%=portal.getLogotypeWidth()%>" height="<%=portal.getLogotypeHeight()%>"
                     alt="<%=portal.getAppName()%>"/></a>
        </td>
        </tr></table>
    </td>

        <td id="SearchArea" <%if ((portal.getBannerImage() == null || portal.getBannerImage().equals(""))){%>align="right"<% } %>>
<%          if (fixedTabStr != null && fixedTabStr.equalsIgnoreCase("Search")) { %>
<%          } else { %>
	    	<table align="center"><tr><td>
        		<div class="searchForm">
                <c:url var="searchURL" value="/search"/>
        		<form action="${searchURL}" >                	
                	<table><tr>
                	<td>
                        <label for="search">Search </label>
	                </td>
	                <td>
<%              	if (isEditor && appBean.isFlag1Active()) { %>
                    	<select id="select" name="flag1" class="form-item" >
                    	<option value="nofiltering" selected="selected">entire database (<%=loginName%>)</option>
                    	<option value="<%=portal.getPortalId()%>"><%=portal.getShortHand()%></option>
                    	</select>
<%              	} else {%>
                    	<input type="hidden" name="flag1" value="<%=portal.getPortalId()%>" />
<%              	} %>
                	<input type="text" name="querytext" id="search" class="search-form-item" value="<c:out value="${requestScope.querytext}"/>" 
                	   	size="<%=VIVO_SEARCHBOX_SIZE%>" />
                	</td>
                	<td>
	                	<input class="search-form-button" name="submit" type="submit"  value="Go" />
	                </td>
	                </tr></table>
        		</form>
				</div>
        		</td></tr></table>
<%          } // not a fixed tab %>
        </td>
<% if (!(portal.getBannerImage() == null || portal.getBannerImage().equals("")))
{
%>
        <td id="BannerArea" align="right">
	        <img src="${portal.themeDir}site_icons/<%=portal.getBannerImage()%>" alt="<%=portal.getShortHand()%>"/>
        </td>
<% } %>

    </tr></table>

</div><!--header-->

