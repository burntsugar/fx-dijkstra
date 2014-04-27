package application;



public class MyNode {

	private MyNode parentNode;
	private double x;
	private double y;
	private double width;
	private double height;
	private int fCost;
	private int gCost;

	public MyNode(MyNode parentNode, double x, double y, double width, double height,int g, double distance) {
		this.parentNode = parentNode;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		gCost = g;
		
		if (parentNode != null) {
			gCost = g + parentNode.getgCost();
		}

		fCost = (int) ((int) g + (distance * 10));
	}

	public MyNode(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public MyNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(MyNode parentNode) {
		this.parentNode = parentNode;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public int getfCost() {
		return fCost;
	}

	public void setfCost(int fCost) {
		this.fCost = fCost;
	}
	
	

	public int getgCost() {
		return gCost;
	}

	public void setgCost(int gCost) {
		this.gCost = gCost;
	}

	@Override
	public String toString() {
		return "MyNode [x=" + x + ", y=" + y
				+ ", width=" + width + ", height=" + height + ", fCost="
				+ fCost + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fCost;
		long temp;
		temp = Double.doubleToLongBits(height);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((parentNode == null) ? 0 : parentNode.hashCode());
		temp = Double.doubleToLongBits(width);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyNode other = (MyNode) obj;
		if (fCost != other.fCost)
			return false;
		if (Double.doubleToLongBits(height) != Double
				.doubleToLongBits(other.height))
			return false;
		if (parentNode == null) {
			if (other.parentNode != null)
				return false;
		} else if (!parentNode.equals(other.parentNode))
			return false;
		if (Double.doubleToLongBits(width) != Double
				.doubleToLongBits(other.width))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	
	
}