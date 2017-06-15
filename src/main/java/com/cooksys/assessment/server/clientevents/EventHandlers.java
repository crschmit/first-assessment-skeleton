package com.cooksys.assessment.server.clientevents;

import java.io.PrintWriter;
import java.util.Arrays;

import org.slf4j.Logger;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class EventHandlers {
	
	private synchronized static String stringifyMessage(ObjectMapper mpr, Message msg) {
		try {
			return mpr.writeValueAsString(msg);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized static Runnable handleConnect(Message msg, Logger log, ObjectMapper mpr) {
		return () -> log.info("user <{}> connected", msg.getUsername());
	}
	
	public synchronized static Runnable handleDisconnect(Message msg, Logger log, ObjectMapper mpr) {
		return () -> log.info("user <{}> disconnected", msg.getUsername());
	}
	
	public synchronized static Runnable handleEcho(Message msg, Logger log, ObjectMapper mpr, PrintWriter client) {
		return () -> {
			log.info("user <{}> echoed message <{}>", msg.getUsername(), msg.getContents());
			String response = null;
			try {
				response = mpr.writeValueAsString(msg);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.write(response);
			client.flush();
		};
	}
	
	public synchronized static Runnable handleBroadcast(Message msg, Logger log, ObjectMapper mpr, PrintWriter client) {
		return () -> {
			log.info("user <{}> broadcast message <{}>", msg.getUsername(), msg.getContents());
			String r = null;
			try {
				r = mpr.writeValueAsString(msg);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.write(r);
			client.flush();
		};
	}
	
	public synchronized static Runnable handleWhisper(Message msg, Logger log, ObjectMapper mpr, PrintWriter client) {
		return () -> {
			String[] contents = msg.getContents().split(" ");
			log.info("user <{}> whishpered message <{}> to user <{}>",
					 msg.getUsername(),
					 String.join(" ", Arrays.copyOfRange(contents, 1, contents.length)),
					 contents[0]);
			String s = null;
			try {
				s = mpr.writeValueAsString(msg);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.write(s);
			client.flush();

		};
	}

	public synchronized static Runnable handleUsers(Message msg, Logger log, ObjectMapper mpr, PrintWriter client) {
		return () -> {
			log.info("user <{}> users: ...", msg.getUsername());
			String t = null;
			try {
				t = mpr.writeValueAsString(msg);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.write(t);
			client.flush();
		};
	}
	
}
