package com.adobe.audi.core.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@SlingServlet(paths = "/bin/audi/twitterFeed", metatype = true)
@Properties({ @Property(name = "Access Token", value = "823520623116845057-lF46MnCrHRv2QcH8n3x1RHPVOW1rcDS"),
		@Property(name = "Access Token Secret", value = "v8AGMbvXU4ohzHqt5It1SfI941AIuREkaCflKURkgSuOM"),
		@Property(name = "Consumer Key", value = "voGxhi6xvRkrDuUBKsLIht6ZV"),
		@Property(name = "Consumer Secret (API Secret)", value = "OUaa07jx7Ywx4XDK7x2lUnnFtVMRi1jVX8QxWBWJrrf0AJwKL2")
})

public class TwitterFeedServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private final Logger Log = LoggerFactory.getLogger(TwitterFeedServlet.class);
	
	@Reference
	private ConfigurationAdmin configAdmin;

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		Log.error("START: TwitterFeedServlet.doGet method");
		//Comment next 2 lines where proxy is not required
		System.setProperty("http.proxyHost", "10.155.103.176");
		System.setProperty("http.proxyPort", "6050");

		Configuration config = configAdmin.getConfiguration("com.adobe.audi.core.servlets.TwitterFeedServlet");

		Log.debug("Tweeter Config = " + config);
		Dictionary<String, Object> properties = config.getProperties();

		String accessToken = PropertiesUtil.toString(properties.get("Access Token"),
				"823520623116845057-lF46MnCrHRv2QcH8n3x1RHPVOW1rcDS");
		String accessTokenSecret = PropertiesUtil.toString(properties.get("Access Token Secret"),
				"v8AGMbvXU4ohzHqt5It1SfI941AIuREkaCflKURkgSuOM");
		String consumerKey = PropertiesUtil.toString(properties.get("Consumer Key"), "voGxhi6xvRkrDuUBKsLIht6ZV");
		String consumerSecret = PropertiesUtil.toString(properties.get("Consumer Secret (API Secret)"),
				"OUaa07jx7Ywx4XDK7x2lUnnFtVMRi1jVX8QxWBWJrrf0AJwKL2");

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);

		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		String s = request.getParameter("model");
		String[] splited = s.split("\\s+");
		String model = "Audi_Online #" + splited[0];
		Query query = new Query(model);
		Log.error("Twitter Query = " + query);

		QueryResult result = null;
		String tweet = null;
		String tweetDate = null;
		JSONObject obj = null;

		try {
			result = twitter.search(query);
			if(result != null) {
				List<Status> statusList = result.getTweets();
				for (int i = 0; i < statusList.size(); i++) {
					if(i == 0) {
						Status status = statusList.get(i);
						tweet = status.getText();
						Log.error("Tweet = " + tweet);
						Date tempDate = status.getCreatedAt();
						tweetDate = new SimpleDateFormat("dd-MM-yyyy").format(tempDate);
						break;
					}
				}
			} else {
				Log.error("TwitterFeedServlet.doGet: Twitter search query result is null");
			}
			response.setContentType("application/json");
			obj = new JSONObject();
			obj.put("date", tweetDate);
			obj.put("tweet", tweet);
			response.getWriter().write(obj.toString());
			response.setStatus(SlingHttpServletResponse.SC_OK);
		} catch (TwitterException e) {
			Log.error("TwitterException in TwitterFeedServlet.doGet: " + e);
		} catch (JSONException e) {
			Log.error("JSONException in TwitterFeedServlet.doGet: " + e);
		} catch (Exception e) {
			Log.error("Error in TwitterFeedServlet.doGet: " + e);
		}
		Log.error("JSON object = " + obj);
		Log.error("END: TwitterFeedServlet.doGet method");
	}
}
