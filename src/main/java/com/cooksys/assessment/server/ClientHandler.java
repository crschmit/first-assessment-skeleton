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

import java.util.Arrays;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	private Socket socket;
	private SerialExecutor exec;
	private ObjectMapper mapper;
	private BufferedReader clientReader;
	private PrintWriter clientWriter;
	
	public ClientHandler(Socket socket, SerialExecutor executor) {
		super();
		this.socket = socket;
		this.exec = executor;
		this.mapper = null;
		this.clientReader = null;
		this.clientWriter = null;
	}

	public ObjectMapper getMapper() {
		if (this.mapper == null) this.mapper = new ObjectMapper();
		return this.mapper;
	}
	
	public BufferedReader getClientReader() throws IOException {
		if (this.clientReader == null) {
			this.clientReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		}
		return this.clientReader;
	}
	
	public PrintWriter getClientWriter() throws IOException {
		if (this.clientWriter == null) {
			this.clientWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		return this.clientWriter;
	}
	
	public String readLine() throws IOException {
		return this.getClientReader().readLine();
	}
	
	public Message readMessage() throws IOException {
		return this.getMapper().readValue(this.readLine(), Message.class);
	}
	
	public void run() {
		try {

			ObjectMapper mapper = getMapper();
			BufferedReader reader = getClientReader();
			PrintWriter writer = getClientWriter();

			while (!socket.isClosed()) {
				
				Message message = this.readMessage();

				switch (message.getCommand()) {
					case "connect":
						log.info("user <{}> connected", message.getUsername());
						break;
					case "disconnect":
						log.info("user <{}> disconnected", message.getUsername());
						this.socket.close();
						break;
					case "echo":
						log.info("user <{}> echoed message <{}>", message.getUsername(), message.getContents());
						String response = mapper.writeValueAsString(message);
						writer.write(response);
						writer.flush();
						break;
					case "broadcast":
						log.info("user <{}> broadcast message <{}>", message.getUsername(), message.getContents());
						String r = mapper.writeValueAsString(message);
						writer.write(r);
						writer.flush();
						break;
					case "whisper":
						String[] contents = message.getContents().split(" ");
						log.info("user <{}> whishpered message <{}> to user <{}>",
								 message.getUsername(),
								 String.join(" ", Arrays.copyOfRange(contents, 1, contents.length)),
								 contents[0]);
						String s = mapper.writeValueAsString(message);
						writer.write(s);
						writer.flush();
						break;
					case "users":
						log.info("user <{}> users: ...", message.getUsername());
						String t = mapper.writeValueAsString(message);
						writer.write(t);
						writer.flush();
						break;
				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

}
