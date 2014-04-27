package application;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;


public class Main extends Application {

	FlowPane root;
	Scene scene;
	double preprocessedHeight, preprocessedWidth;
	double windowWidth, windowHeight;

	BufferedImage preprocessedImage;
	Graphics2D preprocessedImageGraphics;

	List<MyNode> obstacles;

	double startPointX, startPointY;
	double endPointX, endPointY;
	double nodeWidth;
	double nodeHeight;

	double endPointBuffer;

	LinkedList<MyNode> openList = new LinkedList<MyNode>();
	LinkedList<MyNode> closedList = new LinkedList<MyNode>();

	MyNode rootNode = null;
	MyNode goalNode = null;
	MyNode pathNode = null;

	int gDiagonal = 14;
	int gOrthogonal = 10;

	ImageView iv;

	@Override
	public void start(Stage primaryStage) {
		try {
			initStuff();
			root = new FlowPane();
			scene = new Scene(root,windowWidth,windowHeight);
			root.getChildren().add(iv);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			drawStuff();
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void initStuff() {

		iv = new ImageView();

		windowWidth = 500;
		windowHeight = 500;

		preprocessedHeight = 100;
		preprocessedWidth = 100;

		nodeWidth = 1;
		nodeHeight = 1;

		preprocessedImage = new BufferedImage((int)preprocessedWidth, (int)preprocessedHeight,BufferedImage.TYPE_INT_ARGB );
		preprocessedImageGraphics = preprocessedImage.createGraphics();

		endPointBuffer = preprocessedWidth / 20;
		startPointX = endPointBuffer;
		startPointY = endPointBuffer;
		endPointX = preprocessedWidth - endPointBuffer;
		endPointY = preprocessedHeight - endPointBuffer;

		makeObstacles();
	}

	public boolean isEndPoint(MyNode obs) {
		if (startPointX >= obs.getX() && (startPointX + nodeWidth)  <= obs.getX() + obs.getWidth()
				&&
				startPointY >= obs.getY() && (startPointY + nodeHeight) <= obs.getY() + obs.getHeight()) {
			return true;
		}
		return false;
	}

	public void makeObstacles() {
		obstacles = new ArrayList<MyNode>();

		int numOfObs = 50;
		int countObs = 1;
		while (countObs < numOfObs) {
			MyNode o = makeRandomObstacle();
			if (!isEndPoint(o)) {
				obstacles.add(o);
				countObs ++;
			}

		}
	}

	public MyNode makeRandomObstacle() {
		int maxObsHeight = (int) (preprocessedHeight / 5);
		int minObsHeight = (int) (preprocessedHeight / 10);
		int maxObsWidth = (int) (preprocessedWidth / 5);
		int minObsWidth = (int) (preprocessedHeight / 10);

		int minObsX = 0;
		int minObsY = 0;
		int maxObsX = (int) preprocessedWidth;
		int maxObsY = (int) preprocessedHeight;

		Random r = new Random();

		double ox = r.nextInt(maxObsX - minObsX + 1);
		double oy = r.nextInt(maxObsY - minObsY + 1);

		double ow = r.nextInt(maxObsWidth - minObsWidth + 1);
		double oh = r.nextInt(maxObsHeight - minObsHeight + 1);

		MyNode o = new MyNode(ox,oy,ow,oh);

		return o;
	}

	public void drawStuff() {
		drawBackground();
		drawObstacle();
		drawEndpoints();
		scan();
		drawPath();
		animateClosedList();
		//doDraw();
	}

	public void drawBackground() {
		preprocessedImageGraphics.setColor(java.awt.Color.BLACK);
		preprocessedImageGraphics.fillRect(0, 0, (int)preprocessedWidth, (int)preprocessedHeight);
	}

	public void drawObstacle() {
		preprocessedImageGraphics.setColor(java.awt.Color.WHITE);
		for (MyNode o: obstacles) {
			preprocessedImageGraphics.fillRect((int)o.getX(), (int)o.getY(), (int)o.getWidth(), (int)o.getHeight());
		}
	}

	public void drawEndpoints() {
		preprocessedImageGraphics.setColor(java.awt.Color.GREEN);
		preprocessedImageGraphics.fillRect((int)startPointX, (int)startPointY, (int)nodeWidth * 2, (int)nodeHeight * 2);
		preprocessedImageGraphics.setColor(java.awt.Color.RED);
		preprocessedImageGraphics.fillRect((int)endPointX, (int)endPointY, (int)nodeWidth * 2, (int)nodeHeight * 2);
	}

	private void drawPath() {

		System.out.println("OpenList size: " + openList.size());
		System.out.println("ClosedList size: " + closedList.size());

		MyNode currentNode = pathNode;
		drawObstacle();
		drawEndpoints();

		if (pathNode == null) {
			System.out.println("NO PATH AVAILABLE");
			return;
		}

		preprocessedImageGraphics.setColor(java.awt.Color.YELLOW);
		while (currentNode.getParentNode() != null) {
			preprocessedImageGraphics.fillRect((int)currentNode.getX(), (int)currentNode.getY(), (int)currentNode.getWidth(), (int)currentNode.getHeight());
			currentNode = currentNode.getParentNode();
		}

	}

	public void animateClosedList() {

		AnimationTimer timer = new AnimationTimer() {

			ArrayList<MyNode> dn = new ArrayList<MyNode>();
			int count = 0;

			public void handle(long now) {

				for (MyNode nn: dn) {
					preprocessedImageGraphics.setColor(java.awt.Color.ORANGE);
					preprocessedImageGraphics.fillRect((int)nn.getX(), (int)nn.getY(), (int)nn.getWidth(), (int)nn.getHeight());
				}

				MyNode n = closedList.get(count);
				preprocessedImageGraphics.setColor(java.awt.Color.RED);
				preprocessedImageGraphics.fillRect((int)n.getX(), (int)n.getY(), (int)n.getWidth(), (int)n.getHeight());
				dn.add(n);
				count ++;
				if (count == closedList.size() -1) {
					count = 0;
					dn.clear();
				}
				doDraw();
			}
		};
		timer.start();
	}

	public BufferedImage scale(BufferedImage image, int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, image.getType());
		Graphics g = bi.createGraphics();
		if (g instanceof Graphics2D) {
			Graphics2D g2 = ((Graphics2D) g);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		g.drawImage(image, 0, 0, width, height, null);
		return bi;
	}

	private void doDraw() {
		BufferedImage bi = scale(preprocessedImage,(int)windowWidth, (int)windowHeight);
		Image input = SwingFXUtils.toFXImage(bi, null);
		iv.setImage(input);
	}

	public void scan() {
		// root node - starting point.
		// g from root to first current is always gOrthogonal (checks north first).
		rootNode = makeNode(null,startPointX,startPointY,nodeWidth,nodeHeight,0);
		goalNode = makeNode(null,endPointX,endPointY,nodeWidth,nodeHeight,0);

		// add to open
		openList.add(rootNode);

		MyNode currentNode = null;

		while (openList.size() > 0) {
			System.out.println("scanning...");

			// Find the node on the openList with
			// the lowest fCost.
			currentNode = openList.get(0);
			for (int x = 0; x < openList.size(); x ++) {
				if (openList.get(x).getfCost() <= currentNode.getfCost()) {
					currentNode = openList.get(x);
				}
			}

			// Remove current from the openList.
			openList.remove(currentNode);

			// Check if the goal node has been found.
			if (currentNode.getX() == goalNode.getX() && currentNode.getY() == goalNode.getY()) {
				pathNode = currentNode;
				return;
			}

			// Get each node surrounding currentNode (8 max).
			List<MyNode> successorNodes = getSuccessors(currentNode);

			Iterator<MyNode> i = successorNodes.iterator();
			while (i.hasNext()) {
				MyNode n = i.next();

				// If already on the openList and has a greater fCost
				// than the currentNode, remove and exit this iteration.
				if (isOnOpenList(n)) {
					if (n.getfCost() > currentNode.getfCost()) {
						openList.remove(n);
						closedList.add(n);
						continue;
					}
				} 

				if (isOnClosedList(n)) {
					continue;
				}

				openList.add(n);
			}

			// add the currentNode to the closedList.
			closedList.add(currentNode);

			System.out.println("openList.size " + openList.size());
			System.out.println("closedList.size " + closedList.size());
		}

	}

	private boolean isOnOpenList(MyNode node) {
		for (MyNode n: openList) {
			if (n.getX() == node.getX() && n.getY() == node.getY()) {
				return true;
			}
		}
		return false;
	}

	private boolean isOnClosedList(MyNode node) {
		for (MyNode n: closedList) {
			if (n.getX() == node.getX() && n.getY() == node.getY()) {
				return true;
			}
		}
		return false;
	}

	private boolean isObstacle(MyNode n) {

		for (MyNode o: obstacles) {
			if (n.getX() >= o.getX() && n.getX() <= o.getX() + o.getWidth()
					&&
					n.getY() >= o.getY() && n.getY() <= o.getY() + o.getHeight()) {
				System.out.println("OBSTACLE");
				return true;
			}
		}
		return false;
	}

	private MyNode makeNode(MyNode parent, double x, double y, double width, double height, int g) {
		// G = movement cost from parent node.
		// H = movement cost from current node to target node G * distance
		// F = G + H.
		double xDistance = Math.abs(endPointX - x);
		double yDistance = Math.abs(endPointY - y);
		double distance = xDistance + yDistance;
		MyNode n = new MyNode(parent,x,y,width,height,g,distance);
		return n;
	}

	private boolean checkForEdge(double coord, char direction) { /////
		switch (direction) {
		case 'n': 
			if (coord < 0) {
				//System.out.println("North Edge found");
				return true;
			}
		case 'e': 
			if (coord > preprocessedWidth) {
				//System.out.println("East Edge found");
				return true;
			}
		case 's': 
			if (coord > preprocessedHeight) {
				//System.out.println("South Edge found");
				return true;
			}
		case 'w': 
			if (coord < 0) {
				//System.out.println("West Edge found");
				return true;
			}
		}
		//System.out.println("No Edge found");
		return false;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public List<MyNode> getSuccessors(MyNode currentNode) {

		List<MyNode> nodes = new ArrayList<MyNode>();

		double currentY = currentNode.getY();
		double currentX = currentNode.getX();

		// north
		if (!checkForEdge(currentY - 1,'n')) {
			MyNode north = makeNode(currentNode,currentX, currentY - 1,nodeWidth,nodeHeight,gOrthogonal);
			if (!isObstacle(north)) {
				nodes.add(north);
			}


			if (!checkForEdge(currentX + 1, 'e')) {
				MyNode northEast = makeNode(currentNode, currentX + 1, currentY - 1,nodeWidth,nodeHeight,gDiagonal);
				if (!isObstacle(northEast)) {
					nodes.add(northEast);
				}
			}
		}
		// east
		if (!checkForEdge(currentX + 1,'e')) {
			MyNode east = makeNode(currentNode,currentX + 1, currentY,nodeWidth,nodeHeight,gOrthogonal);
			if (!isObstacle(east)) {
				nodes.add(east);
			}
			if (!checkForEdge(currentY + 1,'s')) {
				MyNode southEast = makeNode(currentNode,currentX + 1, currentY + 1,nodeWidth,nodeHeight,gDiagonal);
				if (!isObstacle(southEast)) {
					nodes.add(southEast);
				}
			}
		}
		// south
		if (!checkForEdge(currentY + 1,'s')) {
			MyNode south = makeNode(currentNode,currentX, currentY + 1,nodeWidth,nodeHeight,gOrthogonal);
			if (!isObstacle(south)) {
				nodes.add(south);
			}

			if (!checkForEdge(currentX - 1,'w')) {
				MyNode southWest = makeNode(currentNode,currentX - 1, currentY + 1,nodeWidth,nodeHeight,gDiagonal);
				if (!isObstacle(southWest)) {
					nodes.add(southWest);
				}
			}
		}
		// west
		if (!checkForEdge(currentX - 1,'w')) {
			MyNode west = makeNode(currentNode,currentX - 1, currentY,nodeWidth,nodeHeight,gOrthogonal);
			if (!isObstacle(west)) {
				nodes.add(west);
			}

			if (!checkForEdge(currentY - 1,'n')) {
				MyNode northWest = makeNode(currentNode,currentX - 1, currentY - 1,nodeWidth,nodeHeight,gDiagonal);
				if (!isObstacle(northWest)) {
					nodes.add(northWest);
				}
			}
		}	

		return nodes;

	}

}

