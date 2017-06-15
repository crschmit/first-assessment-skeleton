package com.cooksys.assessment.server.clientevents;

import java.io.PrintWriter;

import org.slf4j.Logger;

import com.cooksys.assessment.model.Message;
import com.cooksys.assessment.server.ClientEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Echoed implements ClientEvent {

	private Message srcMssg;
	private Logger log;
	private PrintWriter client;
	private ObjectMapper mapper;
	
	public Echoed(Message echo, Logger log, ObjectMapper m, PrintWriter client) {
		this.srcMssg = echo;
		this.log = log;
		this.client = client;
		this.mapper = m;
	}	

	public ObjectMapper getMapper() {
		return this.mapper;
	}
	
	@Override
	public Message getMessage() {
		return this.srcMssg;
	}

	@Override
	public String stringifyMessage() {
		try {
			return this.getMapper().writeValueAsString(this.getMessage());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public PrintWriter getClient() {
		return this.client;
	}
	
	@Override
	public void run() {
		log.info("user <{}> echoed message <{}>", this.getMessage().getUsername(), this.getMessage().getContents());
		this.getClient().write(this.stringifyMessage());
		this.getClient().flush();
	}

}
