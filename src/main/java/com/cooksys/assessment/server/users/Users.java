package com.cooksys.assessment.server.users;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.cooksys.assessment.model.Message;

public class Users {
	private Map<String, User> usrs;
	
	public Users() {
		this.usrs = Collections.synchronizedMap(new HashMap<String, User>());
	}
	
	public boolean hasUser(String name) {
		return usrs.containsKey(name);
	}
	
	public User getUser(String name) {
		return hasUser(name) ? usrs.get(name) : null;
	}
	
	public void addUser(String name) {
		if (!hasUser(name)) usrs.put(name, new User(name));
	}
	
	public void remUser(String name) {
		usrs.remove(name);
	}
	
	public void tellUser(String name, Message mssg) {
		if (hasUser(name)) getUser(name).send(mssg);
//			String s = mapper.writeValueAsString(message);
//			writer.write(s);
//			writer.flush();
	}
	
	public Message pollUser(String name) {
		if (hasUser(name)) {
			return getUser(name).recv();
		}
		return null;
	}
}
