package com.cooksys.assessment.model;

import java.util.Date;

public class Message {

	private String username;
	private String command;
	private String contents;
	private String time;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getTime() {
		return time;
	}
	
	public void setTime(Date time) {
		this.time = time.toString();
	}

}
