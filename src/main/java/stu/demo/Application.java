package stu.demo;

import stu.demo.server.ServerManager;


public class Application {

	public static void main(String[] args) {
		// Runtime.getRuntime().addShutdownHook(null);
		ServerManager.startup();
	}
}
