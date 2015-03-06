import java.util.List;

public interface NodeInterface {
	// Start new network
	void start();

	// Join existing network
	NodeInfo join(Node n);

	// Process information
	void processJoin();

	// find new neighbors for weak link
	NodeInfo converge();

	// poll neighbors to update metadata
	void poll(List<Node> neighbors);

	// remove node
	void remove(Node n, int reuqestId);
}
