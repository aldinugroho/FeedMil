package com.uniteksolusi.feedmill.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineInterface implements Runnable {
	
	BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

	public void processCommand(String inputString) {
	
		inputString = inputString.trim();
		
		if(inputString.equals("?")) {
			//show help
			System.out.println("?                 show this help");
			System.out.println("sa                show all arduinos status");
			System.out.println("s0x01             show status of arduino of specific IDs");
			System.out.println("c0x01/command     send command to arduino of specific IDs.\n");
			
		} else if(inputString.equals("sa")) {
			
			//show all status
			System.out.println(MainLoop.jagungSbmBuffer);
			System.out.println(MainLoop.mbmKatulGritBuffer);
			
			System.out.println(MainLoop.jagungLoadCell);
			System.out.println(MainLoop.sbmLoadCell);
			System.out.println(MainLoop.mbmLoadCell);
			System.out.println(MainLoop.gritLoadCell);
			System.out.println(MainLoop.katulLoadCell);
			
			System.out.println(MainLoop.mixer);
			
		} else if(inputString.startsWith("s0x")) {
			
			//show specific arduino
			
		} else if(inputString.startsWith("c0x") && inputString.contains("/")) {
			
			//send command to arduino
			
		} else {
			System.out.println("Invalid command : " + inputString);
			System.out.println("Available commands : ");
			processCommand("?");
		}
		
		
	}
	
	
	
	@Override
	public void run() {

		while(true) {
			
			System.out.print("feedmill-shell$ > ");
			
			try {
				
				String inputString = inputReader.readLine();

				processCommand(inputString);
				
				
				System.out.println(); //just a new line
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	public static void main(String args[]) {
		
		new Thread(new CommandLineInterface()).run();
		
	}

}
