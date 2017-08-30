package com.uniteksolusi.otomill.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.uniteksolusi.otomill.controller.MainController;
import com.uniteksolusi.otomill.model.ArduinoModelIfc;

public class WebServiceHandler implements HttpHandler {

	private static Gson gson = new Gson();

	@Override
	public void handle(HttpExchange t) throws IOException {

		// parse request
		Map<String, Object> parameters = new HashMap<String, Object>();
		HttpHelper.parseRequestParameters(t.getRequestURI().getQuery(), parameters);

		HttpHelper.log(Level.FINE, t, "Processing Commands: " + parameters.get("id") + ", " + parameters.get("cmd"));
		
		String cmdString = (String) parameters.getOrDefault("cmd", "status");
		String idString = (String) parameters.getOrDefault("id", "");

		if("status".equals(cmdString)) {

			handleCommandStatus(t, idString);

		} else {
			
			handleCommandModel(t, idString, cmdString);
			
		}



	}
	
	private void handleCommandModel(HttpExchange t, String idString, String cmdString) throws IOException {

		ArduinoModelIfc ard = MainController.getInstance().getModel(idString);
		String cmdResponse = ard.processCommand(cmdString);
		
		if(cmdResponse.startsWith("OK")) {
			t.sendResponseHeaders(200, cmdResponse.length());
		} else {
			t.sendResponseHeaders(500, cmdResponse.length());
		}
		
		OutputStream os = t.getResponseBody();
		os.write(cmdResponse.getBytes());
		os.close();
		
	}



	private void handleCommandStatus(HttpExchange t, String idString) throws IOException {

		//      String response = ""
		//      		+ "<html>"
		//      		+ "<body>"
		//
		//      		+ "	<table border=5px>"
		//      		+ "		<tr>"
		//      		+ "			<td>ID="+idString+"<br>" + MainController.getInstance().getModel(idString).toString().replaceAll("\n", "<br/>") + "</td>"
		//      		+ "		</tr>"
		//      		+ "	</table>"
		//      		+ "</body>"
		//
		//      		+ "</html>";

		String response = gson.toJson(MainController.getInstance().getModel(idString));
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();

	}

}