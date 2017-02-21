package com.adobe.audi.core.servlets;

import java.io.FileReader;
import java.io.IOException;
import java.util.Dictionary;
import java.util.List;

import javax.jcr.Node;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

/**
 * @author 579458
 *
 */
@SlingServlet(paths = "/bin/audi/machinelearningoutput", metatype = true)
@Properties({ @Property(name = "Path of output file", value = "C:/ML-R/output.csv")})
public class MachineLearningOutputServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(Authentication.class);
	
	@Reference
	private ConfigurationAdmin configAdmin;
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		log.error("START: MachineLearningOutputServlet.doGet");
		
		Configuration config = configAdmin.getConfiguration("com.adobe.audi.core.servlets.MachineLearningOutputServlet");
		Dictionary<String, Object> properties = config.getProperties();
		String outputFilePath = PropertiesUtil.toString(properties.get("Path of output file"), "C:/ML-R/output.csv");
		log.error("outputFilePath = " + outputFilePath);
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(outputFilePath));
			List<String[]> lineEntries = reader.readAll();
			
			String[] line;
			ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            String resourcePath = null;
            Resource resource = null;
            Node userNode = null;
            for (int i = 0; i < lineEntries.size(); i++) {
            	if(i != 0) {
            		line = lineEntries.get(i);
                	resourcePath = "/content/usergenerated/Audi/" + line[0] + "/machine_learning_rec";
                	resource = resourceResolver.getResource(resourcePath);
                	if (null != resource) {
                		userNode = resource.adaptTo(Node.class);
                		log.error("PredictiveAction before = " + userNode.getProperty("predictiveAction").toString());
                		userNode.setProperty("predictiveAction", line[1]);
                		log.error("PredictiveAction after = " + userNode.getProperty("predictiveAction").toString());
                	}
            	}
    		}
            resourceResolver.commit();
		} catch (Exception e) {
			log.error("Error in MachineLearningOutputServlet.doGet: " + e.getMessage());
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
		log.error("END: MachineLearningOutputServlet.doGet");
	}

}
