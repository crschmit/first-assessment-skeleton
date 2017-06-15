package com.cooksys.assessment.server.clientevents;

import org.slf4j.Logger;

import com.cooksys.assessment.model.Message;
import com.cooksys.assessment.server.ClientEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Disconnected implements ClientEvent {

	private Message srcMssg;
	private Logger log;
	private ObjectMapper mapper;
	
	public Disconnected(Message disconnect, Logger log, ObjectMapper mapper) {
		this.srcMssg = disconnect;
		this.log = log;
		this.mapper = mapper;
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

	@Override
	public void run() {
		log.info("user <{}> disconnected", this.getMessage().getUsername());
	}

}
