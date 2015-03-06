import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Node implements NodeInterface {
	InetAddress address;
	int ID;
	int groupID;
	List<Node> neighbors;
	Set<Node> allNodes;
	int expectedNeighbors;
	int actualNeighbors;
	HashMap<Node, Integer> vacancies;
	int status;

	/**
	 * -Start node -Assign ID -Assign group ID
	 */
	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	/**
	 * -Start node -Send connect request to node n -After connection call
	 * furtherProcessing()
	 */
	@Override
	public NodeInfo join(Node n) {
		// TODO Auto-generated method stub
		return null;
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
	 */
	public void furtherProcessing() {
		// TODO Auto-generated method stub

	}

	/**
	 * -Send force remove request to neighbors
	 */
	@Override
	public void remove(Node n, int reuqestId) {
		// TODO Auto-generated method stub

	}
}