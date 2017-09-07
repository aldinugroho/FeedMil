package com.uniteksolusi.otomill.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.uniteksolusi.otomill.controller.CommandController;
import com.uniteksolusi.otomill.controller.MainController;

public class CLIHttpHandler implements HttpHandler {


	@Override
	public void handle(HttpExchange t) throws IOException {
		
		// parse request
        Map<String, Object> parameters = new HashMap<String, Object>();
        InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        HttpHelper.parseRequestParameters(query, parameters);
        
        Object commandString = parameters.get("cmd");
        String commandResponse = null;
        if(commandString == null) {
        	commandString = "";
        }
        commandResponse = CommandController.getInstance().processCommand(commandString.toString());
        
        
        String response = ""
        		+ "<html>"
        		+ "<head>"
        		//+ "<meta http-equiv=\"refresh\" content=\"1\">
        		+ "<script>\n"
        		+ "var reloading; \n"
        		+ "var refresh_time = 1000;\n"
    
        		+ "function checkReloading() { \n"
        		+ "   if (window.location.hash==\"#autoreload\") {  \n"
        		+ "   reloading=setTimeout(\"window.location.reload();\", refresh_time);  \n"
        		+ "   document.getElementById(\"reloadCB\").checked=true;  \n"
        		+ "   }  \n"
        		+ "}  \n"
    
        		+ "function toggleAutoRefresh(cb) {   \n"
        		+ "   if (cb.checked) {   \n"
        		+ "      window.location.replace(\"#autoreload\");   \n"
        		+ "      reloading=setTimeout(\"window.location.reload();\", refresh_time);  \n"
        		+ "   } else {  \n"
        		+ "      window.location.replace(\"#\");  \n"
        		+ "      clearTimeout(reloading);  \n"
        		+ "   }  \n"
        		+ "}  \n"
    
        		+ "window.onload=checkReloading;  \n"
        		+ "</script> \n"
        		
        		+ "</head>"
        		+ "<body>"


        		+ "<tt>"
        		+ "<input type=\"checkbox\" onclick=\"toggleAutoRefresh(this);\" id=\"reloadCB\"> Auto Refresh"
        		+ "<br><br>"

        		+ "<form action=\"/cli\" method=\"post\">"
				+ "  Command: <input type=\"text\" name=\"cmd\"> "
				+ "  <input type=\"submit\" value=\"Submit\">"
				+ "</form>"
				
				+ "  <textarea rows=\"10\" cols=\"100\">" 
				+ 			commandResponse 
				+ "  </textarea>"
				+ "<br>"

				+ "<br>"
        		+ "	<table border=5px>"
        		+ "		<tr>"
        		+ "			<td colspan=5>0x11<br>" + MainController.getInstance().getModel("0x11").printStateDetails().replaceAll("\n", "<br/>") + "</td>"
        		+ "     </tr>"
        		+ "		<tr>"
        		+ "			<td>0x21<br>" + MainController.getInstance().getModel("0x21").printStateDetails().replaceAll("\n", "<br/>") + "</td>"
        		+ "			<td>0x22<br>" + MainController.getInstance().getModel("0x22").printStateDetails().replaceAll("\n", "<br/>") + "</td>"
        		+ "			<td>0x23<br>" + MainController.getInstance().getModel("0x23").printStateDetails().replaceAll("\n", "<br/>") + "</td>"
        		+ "			<td>0x24<br>" + MainController.getInstance().getModel("0x24").printStateDetails().replaceAll("\n", "<br/>") + "</td>"
        		+ "			<td>0x25<br>" + MainController.getInstance().getModel("0x25").printStateDetails().replaceAll("\n", "<br/>") + "</td>"
        		+ "			<td>0x26<br>" + MainController.getInstance().getModel("0x26").printStateDetails().replaceAll("\n", "<br/>") + "</td>"
        		+ "		</tr>"	
        		+ "		<tr>"
        		+ "			<td colspan=6>0x31<br>" + MainController.getInstance().getModel("0x31").printStateDetails().replaceAll("\n", "<br/>") + "</td>"
        		+ "		</tr>"
        		+ "		<tr>"
        		+ "			<td colspan=6>0x41<br>" + MainController.getInstance().getModel("0x41").printStateDetails().replaceAll("\n", "<br/>") + "</td>"
        		+ "		</tr>"		
        		+ ""
        		+ "	</table>"
        		+ "</tt>"
        		+ "</body>"

        		+ "</html>";
        
                    
//        byte[] encoded = Files.readAllBytes(Paths.get("status.html"));
//        response =  new String(encoded, Charset.defaultCharset());
        
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
        
	}

}