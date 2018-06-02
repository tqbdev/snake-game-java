package com.tqbdev.server_core;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		Server server = new Server();
		try {
			server.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
