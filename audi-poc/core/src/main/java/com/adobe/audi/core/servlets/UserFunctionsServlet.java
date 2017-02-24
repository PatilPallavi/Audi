package com.adobe.audi.core.servlets;

import java.io.IOException;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(paths = "/bin/audi/userFunctions",metatype=true)
public class UserFunctionsServlet extends SlingAllMethodsServlet {
	
	private static final long serialVersionUID = 1L;
	private final Logger Log = LoggerFactory.getLogger(UserFunctionsServlet.class);
	
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
	@SuppressWarnings({ "deprecation", "rawtypes" })
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		String userId = request.getParameter("userId");
		String userFunctions = request.getParameter("userFunctions");
		try {
			String resourcePath = "/content/usergenerated/Audi";
			Resource res = resourceResolverFactory.getAdministrativeResourceResolver(null).getResource(resourcePath);
			Node node = res.adaptTo(Node.class);
			NodeIterator childrenNodes = (NodeIterator) node.getNodes();
			
			while (((Iterator) childrenNodes).hasNext()) {
				Node next = childrenNodes.nextNode();

				if (next.getProperty("./email").getString().equals(userId)) {
					next.setProperty("userFunctions", userFunctions);
					next.getSession().save();
					break;
				}
			}
		} catch (Exception e) {
			Log.error("Error while sending request"+e.getMessage());
		}
	}
}
