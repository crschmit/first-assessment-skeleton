package com.cooksys.assessment.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	private Socket socket;
	private Users users;

	public ClientHandler(Socket socket, Users users) {
		super();
		this.socket = socket;
		this.users = users;
	}
	
	public synchronized void connect(
			String usr, 
			ObjectMapper mapper, 
			BufferedReader reader,
			PrintWriter writer,
			Message message, 
			Logger log) {
		log.info("user <{}> connected", usr);
		users.addUser(usr, mapper, reader, writer);
		//users.tellUser(usr, message);
		users.tellAll(message);
	}
	
	public synchronized void disconnect(String usr, Message message, Logger log) {
		log.info("user <{}> disconnected", usr);
		users.tellAll(message);
		users.remUser(usr);
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void echo(String user, Message message, Logger log) {
		log.info("user <{}> echoed message <{}>", user, message.getContents());
		//User u = users.getUser(user);
		users.tellUser(user, message);
	}

	public synchronized void broadcast(String user, Message message, Logger log) {
		log.info("user <{}> echoed message <{}>", user, message.getContents());
		users.tellAll(message);
	}
	
	public synchronized void whisper(String user, Message message, Logger log) {
		String[] args = message.getContents().split(" ");
		String target = args[0];
		log.info("user <{}> whispered: ...", user);
		users.tellUser(target, message);
	}
	
	public synchronized void getUsers(String user, Message message, Logger log) {
		log.info("user <{}> get users: ...", user);
		String[] usernames = users.getUserNames();
		message.setContents(String.join("\n", usernames));
		users.tellUser(user, message);
	}
	
	public void run() {
		try {
			
			
			ObjectMapper mapper = new ObjectMapper();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			while (!socket.isClosed()) {
				String raw = reader.readLine();
				Message message = mapper.readValue(raw, Message.class);
				String usr = message.getUsername();
				String cmd = message.getCommand();
				String cts = message.getContents();
				
				switch (message.getCommand()) {
					case "connect":
						//log.info("user <{}> connected", usr);
						//users.add(usr, mapper, reader, writer);
						////users.tellUser(usr, message);
						//users.tellAll(message);
						connect(usr, mapper, reader, writer, message, log);
						break;
					case "disconnect":
//						log.info("user <{}> disconnected", usr);
//						this.socket.close();
						disconnect(usr, message, log);
						break;
					case "echo":
//						log.info("user <{}> echoed message <{}>", usr, cts);
//						String response = mapper.writeValueAsString(message);
//						writer.write(response);
//						writer.flush();
						echo(usr, message, log);
						break;
					case "broadcast":
//						log.info("user <{}> broadcast: ...", usr);
//						String s = mapper.writeValueAsString(message);
//						writer.write(s);
//						writer.flush();
						broadcast(usr, message, log);
						break;
					case "whisper":
//						log.info("user <{}> whispered: ...", message.getUsername());
//						String t = mapper.writeValueAsString(message);
//						writer.write(t);
//						writer.flush();
						whisper(usr, message, log);
						break;
					case "users":
//						log.info("user <{}> get users: ...", message.getUsername());
//						String v = mapper.writeValueAsString(message);
//						writer.write(v);
//						writer.flush();
						getUsers(usr, message, log);
						break;
				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

}