package com.cooksys.assessment.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Users {

	private Map<String, User> usrs = null;
	
	public Users() {
		this.usrs = Collections.synchronizedMap(new HashMap<String, User>());
	}
	
	public boolean hasUser(String name) {
		return usrs.containsKey(name);
	}
	
	public User getUser(String name) {
		User u = null;
		if (hasUser(name)) u = usrs.get(name);
		return u;
	}
	
	public String[] getUserNames() {
		return this.usrs.keySet().toArray(new String[0]);
	}
	public void addUser(String name, ObjectMapper mapper, BufferedReader reader, PrintWriter writer) {
		usrs.put(name, new User(name, mapper, reader, writer));
	}
	
	public void remUser(String name) {
		usrs.remove(name);
	}

	public Message pollUser(String name) {
		Message message = null;
		if (hasUser(name)) {
			User u = getUser(name);
			String raw;
			try {
				raw = u.reader.readLine();
				message = u.mapper.readValue(raw, Message.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return message;
	}
	
	public void tellUser(String name, Message message) {
		if (hasUser(name)) {
			User u = getUser(name);
		
			String response = "";
			try {
				response = u.mapper.writeValueAsString(message);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			u.writer.write(response);
			u.writer.flush();
		}
	}
	
	public void tellAll(Message message) {
		//String[] names = this.usrs.keySet().toArray(new String[0]);
		String[] names = getUserNames();
		for (String n : names) {
			tellUser(n, message);
		}
	}
}
