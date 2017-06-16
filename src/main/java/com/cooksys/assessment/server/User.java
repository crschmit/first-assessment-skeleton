package com.cooksys.assessment.server;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class User {

	public String name;
	public BufferedReader reader;
	public ObjectMapper mapper;
	public PrintWriter writer;

	public User(String name, ObjectMapper mapper, BufferedReader reader, PrintWriter writer) {
		this.name = name;
		this.mapper = mapper;
		this.reader = reader;
		this.writer = writer;
	}

}
