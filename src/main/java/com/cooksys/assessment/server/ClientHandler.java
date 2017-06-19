package com.cooksys.assessment.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

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
			Logger log,
			Date time) {
		message.setTime(time);
		log.info("[{}] user <{}> connected", message.getTime(), usr);
		users.addUser(usr, mapper, reader, writer);
		users.tellAll(message);
	}
	
	public synchronized void disconnect(
			String usr, 
			Message message, 
			Logger log,
			Date time) {
		message.setTime(time);
		log.info("[{}] user <{}> disconnected", message.getTime(), usr);
		users.remUser(usr);
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		users.tellAll(message);
	}
	
	public synchronized void echo(
			String user, 
			Message message, 
			Logger log,
			Date time) {
		message.setTime(time);
		log.info("[{}] user <{}> echoed message <{}>",message.getTime(), user, message.getContents());
		users.tellUser(user, message);
	}

	public synchronized void broadcast(
			String user, 
			Message message, 
			Logger log,
			Date time) {
		message.setTime(time);
		log.info("[{}] user <{}> echoed message <{}>", message.getTime(), user, message.getContents());
		users.tellAll(message);
	}
	
	public synchronized void whisper(
			String user, 
			Message message, 
			Logger log,
			Date time) {
		message.setTime(time);
		String[] args = message.getContents().split(" ");
		String target = args[0];
		log.info("[{}] user <{}> whispered: ...",message.getTime(), user);
		users.tellUser(target, message);
	}
	
	public synchronized void getUsers(
			String user, 
			Message message, 
			Logger log,
			Date time) {
		message.setTime(time);
		log.info("[{}] user <{}> get users: ...", message.getTime(), user);
		String[] usernames = users.getUserNames();
		message.setContents(String.join(" ", usernames));
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
				Date time = new Date();
				
				switch (message.getCommand()) {
					case "connect":
						connect(usr, mapper, reader, writer, message, log, time);
						break;
					case "disconnect":
						disconnect(usr, message, log, time);
						break;
					case "echo":
						echo(usr, message, log, time);
						break;
					case "broadcast":
						broadcast(usr, message, log, time);
						break;
					case "whisper":
						whisper(usr, message, log, time);
						break;
					case "users":
						getUsers(usr, message, log, time);
						break;
				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

}