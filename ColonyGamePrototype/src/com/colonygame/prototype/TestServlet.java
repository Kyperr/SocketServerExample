package com.colonygame.prototype;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// This is the IP to my google cloud machine. You should change this.
	private static final String IP = "130.211.169.190";

	// Instance socket variable.
	private Socket clientSocket;

	// List of names to display.
	private List<String> names = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public TestServlet() {
		try {
			// Instantiate a socket.
			clientSocket = new Socket(IP, 45655);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.getWriter().println("Greetings, World.");

		/*
		 * This sets the attribute 'names' equal to the list called names in order to be
		 * used later in the web page.
		 */
		request.setAttribute("names", names);

		request.getRequestDispatcher("/WEB-INF/Main.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("first_name");
		System.out.println("Name: " + name);

		// Create input and output streams.
		ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
		ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

		System.out.println("Waiting for connection to confirm.");

		/*
		 * This next part is sloppy, but shows the basic functionality. This is where we
		 * would have our own wrapper and protocol. Also, this would obviously need some
		 * form of error catching built into the wrapper, but this is an example.
		 */
		try {
			// Print server message to console. (debug purposes)
			System.out.println(ois.readObject());
			System.out.println(ois.readObject());// "Send Name."

			// Send my name.
			oos.writeObject(name);

			// If the server responds with the 'Connected as' message, connection was
			// successful
			if (ois.readObject().equals("Connected as: " + name + ".")) {
				System.out.println("Requesting other member's names.");
				//Send the Members request.
				oos.writeObject("Members.");
				try {
					//Store the names of the other users.
					this.names = (List<String>) ois.readObject();
					System.out.println("Number of other members: " + names.size());
					for (String s : names) {
						System.out.println(s);
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				// invalid name
			}
			doGet(request, response);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // Should print off "Send Name."
	}

}
