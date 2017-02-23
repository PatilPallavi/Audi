package com.adobe.audi.core.servlets;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Dictionary;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

@SlingServlet(paths = "/bin/audi/machinelearninginput", metatype = true)
@Properties({ @Property(name = "Enter Path to save file", value = "C:/ML-R/input.csv")})

public class MachineLearningInputServlet extends SlingAllMethodsServlet {
	
	private static final long serialVersionUID = 1L;
	
	@Reference
	private ConfigurationAdmin configAdmin;
	
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
	private final Logger Log = LoggerFactory.getLogger(MachineLearningInputServlet.class);

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		Configuration config = configAdmin.getConfiguration("com.adobe.audi.core.servlets.MachineLearningInputServlet");
		Log.error("AUDI=======Config" + config);

		Dictionary<String, Object> properties = config.getProperties();

		String csvPath = PropertiesUtil.toString(properties.get("Enter Path to save file"), "C:/ML-R/input.csv");
		Log.error("AUDI=======Config" + config);

		String UserID = request.getParameter("UserID");

		if (UserID.isEmpty()) {
			Log.error("USerID is Empty: " + UserID);
		} else {
			Log.error("AUDI=======UserID: " + UserID);

			String nodePath = "/content/usergenerated/Audi/" + UserID + "/machine_learning_input";
			Log.error("AUDI=======nodePath: " + nodePath);

			Resource res;
			try {
				res = resourceResolverFactory.getAdministrativeResourceResolver(null).getResource(nodePath);
				Node node = res.adaptTo(Node.class);
				try {
					CSVWriter writer = new CSVWriter(new FileWriter(csvPath), ',', CSVWriter.NO_QUOTE_CHARACTER);
					String[] heading = new String[] { "User", "Browse", "Inform", "Configure", "Dealer", "Newsletter", "Buy" };
					writer.writeNext(heading);
					String[] csvValues = new String[] { UserID, node.getProperty("browse").getString(),
							node.getProperty("inform").getString(), node.getProperty("configure").getString(),
							node.getProperty("dealer").getString(), node.getProperty("newsletter").getString(),
							node.getProperty("buy").getString() };
					writer.writeNext(csvValues);
					writer.close();
				} catch (ValueFormatException e) {
					Log.error("Error in MachineLearningInputServlet.doGet: " + e.getMessage());
				} catch (PathNotFoundException e) {
					Log.error("Error in MachineLearningInputServlet.doGet: " + e.getMessage());
				} catch (RepositoryException e) {
					Log.error("Error in MachineLearningInputServlet.doGet: " + e.getMessage());
				}
			} catch (LoginException e) {
				Log.error("Error in MachineLearningInputServlet.doGet: " + e.getMessage());
			}
		}
	}
}