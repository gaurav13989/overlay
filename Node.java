import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Node implements NodeInterface {
	InetAddress address; //
	String hostAddress; //
	int ID; //
	int groupID; //
	List<Node> neighbors;
	Set<Node> allNodes;
	int expectedNeighbors;
	int actualNeighbors;
	HashMap<Node, Integer> vacancies;
	int pollingPort = 43124;
	int listeningPort = 43125;
	Socket clientSocket;
	int status; // 1 - starting, 2 - started listening, 3 - started polling and
				// ready
	int latency; // milliseconds

	/**
	 * Constructor
	 * 
	 * @param IP
	 * @param type
	 * @throws UnknownHostException
	 */
	public Node(String IP) throws UnknownHostException {
		if (IP == null) {
			address = InetAddress.getLocalHost();
			hostAddress = address.getHostAddress();
		} else {
			hostAddress = IP;
			address = InetAddress.getByName(IP);
		}
		ID = getID(hostAddress);
		groupID = getGroupID(hostAddress, System.currentTimeMillis());
		if (groupID < 0) {
			groupID *= -1;
		}
		neighbors = new ArrayList<Node>();
		allNodes = new HashSet<Node>();
		expectedNeighbors = 0;
		actualNeighbors = 0;
		vacancies = new HashMap<Node, Integer>();
		latency = -1;
		status = 1; // starting
	}

	/**
	 * -Start node -Assign ID -Assign group ID
	 * 
	 * @throws UnknownHostException
	 * @throws SocketException
	 */
	@Override
	public void start() throws UnknownHostException, SocketException {
		try {
			// initialize variables
			System.out.println("Node(ID: " + ID + ", groupID: " + groupID
					+ ") starting... IP: " + address);
			listenAndPoll(this);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Status: " + status);
		}
	}

	public void listenAndPoll(Node n) throws IOException {
		// start listening
		Thread listening = new Thread(new Listen(n));
		listening.start();
		status = 2;

		// start polling
		Thread polling = new Thread(new Polling(n));
		polling.start();
		status = 3;
	}

	/**
	 * -Start node -Send connect request to node n -After connection call
	 * furtherProcessing()
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	@Override
	public void join(String IP) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		Message m = new Message(this, 2);
		// send join message to IP
		clientSocket = new Socket(InetAddress.getByName(IP), listeningPort);
		ObjectOutputStream outToServer = new ObjectOutputStream(
				clientSocket.getOutputStream());
		outToServer.writeObject(m);
		outToServer.flush();
		outToServer.close();
	}

	/**
	 * -Check if connection can be accepted -if yes --send necessary info
	 * --processNewJoineeInfo() -if no --send necessary info
	 */
	@Override
	public void processJoin() {
		// TODO Auto-generated method stub

	}

	/**
	 * -Send new joinee info to new joinee
	 */
	public NodeInfo sendNewJoineeInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * -Process new joinee info from newly joined node
	 */
	public void processNewJoineeInfo() {
		// TODO Auto-generated method stub

	}

	/**
	 * -parameters and if any below threshold -inform other node of possible
	 * link drop -find new neighbors -drop old link if contacted by new neighbor
	 */
	@Override
	public NodeInfo converge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void poll(List<Node> neighbors) {
		// TODO Auto-generated method stub

	}

	/**
	 * -Add node to neighbors -Share new network with neighbors -Share
	 * availability with neighbors
	 * 
	 * @param n
	 */
	public void furtherProcessing(Node n) {
		// TODO Auto-generated method stub
		System.out.println("Updating node with info from neighbor");
	}

	/**
	 * -Send force remove request to neighbors
	 */
	@Override
	public void remove(Node n, int reuqestId) {
		// TODO Auto-generated method stub

	}

	/**
	 * Class that polls each neighbor periodically. Every t seconds.
	 * 
	 * @author Gaurav
	 *
	 */
	class Polling implements Runnable {
		Socket clientSocket;
		Node n;
		int t;

		public Polling(Node n) {
			clientSocket = new Socket();
			this.n = n;
			t = 10000; // milliseconds
		}

		@Override
		public void run() {
			ObjectOutputStream outToServer = null;
			ObjectInputStream inFromServer = null;
			while (true) {
				// for each neighbor
				try {
					Message m = new Message(n, 1); // poll message
					Thread.sleep(t); // sleep for 10 seconds
					for (Node neighbor : neighbors) { // polling each neighbor
						long pollSend = System.currentTimeMillis();

						if (neighbor.clientSocket == null) {
							neighbor.clientSocket = new Socket(address,
									listeningPort);
						}
						clientSocket = neighbor.clientSocket;

						outToServer = new ObjectOutputStream(
								clientSocket.getOutputStream());

						outToServer.writeObject(m);
						outToServer.flush();
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Handles received requests by checking what type message was received.
	 * 
	 * @author Gaurav
	 *
	 */
	class Process implements Runnable {
		ObjectOutputStream outToServer;
		ObjectInputStream inFromServer;
		Node n;

		public Process(Socket clientSocket, Node n) throws IOException {
			this.n = n;
			outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			inFromServer = new ObjectInputStream(clientSocket.getInputStream());
		}

		// process received request
		@Override
		public void run() {
			while (true) {
				try {
					Message m = (Message) inFromServer.readObject();
					if (m.type == 0) { // receive poll
						System.out.println("Received poll from "
								+ m.n.hostAddress);
						outToServer.writeObject(new Message(n, 1)); // send
																	// response
					} else if (m.type == 1) { // receive response
						System.out.println("Received poll response from "
								+ m.n.hostAddress);
						furtherProcessing(m.n);
					} else if (m.type == 2) {
						// new join request received
						System.out.println("Join request received from "
								+ m.n.hostAddress);
						outToServer.writeObject(new Message(n, 3)); // response
																	// to
																	// join
					} else if (m.type == 3) {
						System.out.println("Join rsponse received from "
								+ m.n.hostAddress);
					}
					outToServer.flush();
					outToServer.close();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Class that handles listening to requests from neighbors and new nodes.
	 * Delegates the requests to the Process class for further handling.
	 * 
	 * @author Gaurav
	 *
	 */
	class Listen implements Runnable {
		ServerSocket listen;
		Node n;

		public Listen(Node n) throws IOException {
			listen = new ServerSocket(listeningPort);
			this.n = n;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					Socket clientRequest = listen.accept();
					new Thread(new Process(clientRequest, n)).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Messages exchanged between nodes
	 * 
	 * @author Gaurav
	 *
	 */
	class Message {
		int type; // poll
		Node n;

		public Message(Node n, int type) {
			this.type = type; // 0-poll, 1-poll response, 2-join ,3-remove,
								// 4-updates
			this.n = n;
		}
	}

	/**
	 * Returns a unique ID for the IP address
	 * 
	 * @param IP
	 * @return
	 */
	private int getID(String IP) {
		int ID = 0;
		int prime1 = 3407;
		int prime2 = 982451653;
		for (int i = 0; i < IP.length(); i++) {
			if (IP.charAt(i) != '.')
				ID += ((IP.charAt(i) - 48) * prime1) % prime2;
		}
		return ID;
	}

	/**
	 * Returns a unique group ID for the IP address for the time when it was
	 * created
	 * 
	 * @param IP
	 * @param currentTime
	 * @return
	 */
	private int getGroupID(String IP, long currentTime) {
		int groupID = 0;
		int prime1 = 3571;
		int prime2 = 961748941;
		for (int i = 0; i < IP.length(); i++) {
			if (IP.charAt(i) != '.')
				groupID += ((IP.charAt(i) - 48) * prime1) % prime2;
		}
		groupID += currentTime;
		return groupID;
	}

	/**
	 * The application
	 * 
	 * @param args
	 * @throws UnknownHostException
	 * @throws SocketException
	 */
	public static void main(String[] args) throws UnknownHostException,
			SocketException, IOException {
		Node n = new Node(InetAddress.getLocalHost().getHostAddress());
		// main thread to accept user commands
		Scanner s = new Scanner(System.in);
		int i = -1;
		while (true) {
			System.out.println("0 - start node");
			System.out.println("1 - join node");
			System.out.println("99 - exit");
			i = Integer.parseInt(s.nextLine());
			if (i == 0) {
				n.start();
				break;
			}
			if (i == 1) {
				System.out.println("Enter IP of new node to join: ");
				String newIP = s.nextLine();
				n.join(newIP);
				break;
			}
			if (i == 99) {
				System.out.println("Exiting.");
				break;
			}
			System.out.println("Try again...");
		}
		if (i != 99) {
			while (true) {
				System.out.println("0 - Print neighbors");
				System.out.println("1 - Print vacancies");
				System.out.println("2 - Print set of all nodes");
				System.out.println("3 - Print meta-data");
				i = s.nextInt();
				switch (i) {
				case 0:
					System.out.println(n.neighbors);
					break;
				case 1:
					System.out.println(n.vacancies);
					break;
				case 2:
					System.out.println(n.allNodes);
					break;
				case 3:
					System.out.println("ID: " + n.ID + " :: Group ID: "
							+ n.groupID + " :: Address: " + n.hostAddress);
					break;
				default:
					System.out.println("Try again...");
					break;
				}
			}
		}
	}
}