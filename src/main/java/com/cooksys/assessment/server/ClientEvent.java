package com.cooksys.assessment.server;

import com.cooksys.assessment.model.Message;

public interface ClientEvent extends Runnable {
	
	public Message getMessage();
	public String stringifyMessage();
	
	public void run();
	
}
