import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public interface NodeInterface {
	// Start new network
	void start() throws UnknownHostException, SocketException;

	// Join existing network
	void join(String IP) throws UnknownHostException, IOException;

	// Process information
	void processJoin();

	// find new neighbors for weak link
	NodeInfo converge();

	// poll neighbors to update metadata
	void poll(List<Node> neighbors);

	// remove node
	void remove(Node n, int reuqestId);
}
