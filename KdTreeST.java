import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class KdTreeST<Value> {
    // the first Node inserted within the kd tree
    private Node root;
    // stores the total number of nodes within the tree beginning from the root
    private int size;

    // private helper class for storing each node in the tree
    private class Node {
        // stores point associated with Node
        private Point2D point;
        // stores value mapped by symbol table with given key of point2d
        private Value val;
        // the axis-aligned rectangle corresponding to this node
        private RectHV rect;
        // the left/bottom subtree
        private Node leftBottom;
        // the right/top subtree
        private Node rightTop;
        // the orientation of the current node: true for comparing x values and
        // false for comparing y values
        private boolean isX;

        // node constructor - helpful for put private helper method
        public Node(Point2D point, Value val, boolean isX, RectHV box) {
            this.point = point;
            this.val = val;
            this.isX = isX;
            this.rect = box;
        }
    }

    // construct an empty symbol table of points
    public KdTreeST() {
    }

    // helper method that takes an object as its parameter and checks if it is
    // null or 'valid'
    // throws an Illegal Argument Exception if invalid
    private void checkInvalid(Object x) {
        if (x == null) {
            throw new IllegalArgumentException("Argument is null!");
        }
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        // call size() and manually check if it is equal to 0
        return size() == 0;
    }

    // number of points
    public int size() {
        // if root is null, then symbol table is empty with size of 0
        if (root == null) return 0;
        else return size;
    }

    // associate the value val with point p
    // takes parameters of point to be added and its corresponding value
    public void put(Point2D point, Value val) {
        checkInvalid(point);
        checkInvalid(val);

        // set up root node first if beginning from empty tree
        if (root == null) {
            // since root node is level 0 set isX = true
            // its bounding box is infinity in each direction
            root = new Node(point, val, true, new RectHV(Double.NEGATIVE_INFINITY,
                                                         Double.NEGATIVE_INFINITY,
                                                         Double.POSITIVE_INFINITY,
                                                         Double.POSITIVE_INFINITY));

            // bc special edge case manually update size
            size++;
        }
        // otherwise use put private helper method
        else {
            // begin traversal from root, set isX = true bc root orientation is always
            // vertical
            put(root, point, val, true);
            size++;
        }
    }

    // takes four parameters: current node (technically always the parent), point
    // of current node, value of current node and its orientation

    // recursive helper method that generates a new node if point to be added doesn't
    // exist yet
    private void put(Node node, Point2D point, Value val, boolean isX) {
        double cmp;

        // orientation of the node determines comparator
        if (isX) {
            cmp = point.x() - node.point.x();
        }
        else {
            cmp = point.y() - node.point.y();
        }

        // comparison result determines which branch to traverse
        // negative comparison result goes left
        if (cmp < 0) {
            // if the leftBottom child doesn't exist add a new node
            if (node.leftBottom == null) {
                // create a bounding box for the new node (child of current node)
                RectHV box;

                // orientation of the current node determines which edge of the
                // current node's box to update
                if (isX) {
                    box = new RectHV(node.rect.xmin(), node.rect.ymin(),
                                     node.point.x(), node.rect.ymax());
                }
                else {
                    box = new RectHV(node.rect.xmin(), node.rect.ymin(),
                                     node.rect.xmax(), node.point.y());
                }
                node.leftBottom = new Node(point, val, !node.isX, box);
            }
            // leftBottom child exists so continue traversing
            else {
                put(node.leftBottom, point, val, !node.isX);
            }
        }
        // positive or 0 comparison results go right
        else {
            // if the point is the same as query point update value
            if (node.point.equals(point)) {
                node.val = val;
                // decrement size because every call to put() increments size even
                // if no node is added
                size--;
            }
            // if the rightTop child doesn't exist add a new node
            else if (node.rightTop == null) {
                // create a bounding box for the new node (child of current node)
                RectHV box;
                // orientation of the current node determines which edge of the
                // current node's box to update
                if (isX) {
                    box = new RectHV(node.point.x(), node.rect.ymin(),
                                     node.rect.xmax(), node.rect.ymax());
                }
                else {
                    box = new RectHV(node.rect.xmin(), node.point.y(),
                                     node.rect.xmax(), node.rect.ymax());
                }
                node.rightTop = new Node(point, val, !node.isX, box);
            }
            // child exists so continue traversing
            else {
                put(node.rightTop, point, val, !node.isX);
            }
        }
    }

    // value associated with point p
    public Value get(Point2D p) {
        checkInvalid(p);
        return get(root, p);
    }

    // helper method that recursively searches the tree for the query point and
    // returns its value
    private Value get(Node node, Point2D point) {
        if (node == null) return null;
        double cmp;
        // orientation of the node determines comparator
        if (node.isX) {
            cmp = point.x() - node.point.x();
        }
        else {
            cmp = point.y() - node.point.y();
        }
        // comparison result determines which branch to search
        if (cmp < 0) return get(node.leftBottom, point);
        else if (cmp > 0) return get(node.rightTop, point);
        else {
            // returns the value of the current node if it equals the query point
            if (node.point.equals(point)) {
                return node.val;
            }
            // otherwise searches right branch because ties are put in right branch
            return get(node.rightTop, point);
        }
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D point) {
        checkInvalid(point);
        return get(point) != null;
    }

    // returns all points in the symbol table as a queue
    public Iterable<Point2D> points() {
        Queue<Point2D> keys = new Queue<Point2D>();
        Queue<Node> nodes = new Queue<Node>();
        if (isEmpty()) return keys;
        nodes.enqueue(root);
        while (!nodes.isEmpty()) {
            Node currentNode = nodes.dequeue();
            if (currentNode == null) continue;
            keys.enqueue(currentNode.point);
            nodes.enqueue(currentNode.leftBottom);
            nodes.enqueue(currentNode.rightTop);
        }
        return keys;
    }

    // returns all points that are inside the rectangle (or on the boundary) in a queue
    public Iterable<Point2D> range(RectHV rect) {
        checkInvalid(rect);
        Queue<Point2D> result = new Queue<Point2D>();
        range(root, rect, result);
        return result;
    }

    // helper method that recursively traverses the tree and adds points within rect
    // to the queue
    private void range(Node node, RectHV rect, Queue<Point2D> queue) {
        if (node == null || rect == null) {
            return;
        }
        // prunes branches that don't intersect with rect
        if (!node.rect.intersects(rect)) {
            return;
        }
        // enqueues point if it is within rect
        if (rect.contains(node.point)) {
            queue.enqueue(node.point);
        }
        // searches children of the node
        range(node.leftBottom, rect, queue);
        range(node.rightTop, rect, queue);
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D point) {
        checkInvalid(point);
        if (isEmpty()) return null;
        return nearest(root, point, point.distanceSquaredTo(root.point), root.point);
    }

    // helper method that recursively traverses the tree to find the nearest neighbor
    // of point
    private Point2D nearest(Node node, Point2D point, double minDistance,
                            Point2D champion) {
        // backtracks if the node is null
        if (node == null) {
            return champion;
        }
        // if points are equal, then it is the nearest
        if (node.point.equals(point)) {
            champion = node.point;
            return champion;
        }
        // updates the champion and minDistance if closer than current champion
        if (point.distanceSquaredTo(node.point) < minDistance) {
            champion = node.point;
            minDistance = point.distanceSquaredTo(node.point);
        }
        // goes toward query point first
        if ((node.leftBottom != null) && (node.leftBottom.rect.contains(point))) {
            champion = nearest(node.leftBottom, point, minDistance, champion);
            minDistance = point.distanceSquaredTo(champion);
            // checks other branch if it could contain any point closer than current
            // champion
            if ((node.rightTop != null) && (node.rightTop.rect.distanceSquaredTo(point)
                    < minDistance)) {
                champion = nearest(node.rightTop, point, minDistance, champion);
            }
        }
        // goes toward query point first
        else if ((node.rightTop != null) && (node.rightTop.rect.contains(point))) {
            champion = nearest(node.rightTop, point, minDistance, champion);
            minDistance = point.distanceSquaredTo(champion);
            // checks other branch if it could contain any point closer than current
            // champion
            if ((node.leftBottom != null) &&
                    (node.leftBottom.rect.distanceSquaredTo(point) < minDistance)) {
                champion = nearest(node.leftBottom, point, minDistance, champion);
            }
        }
        // neither branch contains query point
        else {
            // checks if both branches could contain any point closer than current
            // champion
            if ((node.leftBottom != null) &&
                    (node.leftBottom.rect.distanceSquaredTo(point) < minDistance) &&
                    (node.rightTop != null) &&
                    (node.rightTop.rect.distanceSquaredTo(point) < minDistance)) {
                // goes toward query point first
                if (node.rightTop.rect.distanceSquaredTo(point)
                        < node.leftBottom.rect.distanceSquaredTo(point)) {
                    champion = nearest(node.rightTop, point, minDistance, champion);
                    minDistance = point.distanceSquaredTo(champion);
                    // checks other branch if it could contain any point closer than
                    // current champion
                    if (node.leftBottom.rect.distanceSquaredTo(point) < minDistance) {
                        champion = nearest(node.leftBottom, point, minDistance,
                                           champion);
                    }
                }
                else {
                    champion = nearest(node.leftBottom, point, minDistance, champion);
                    minDistance = point.distanceSquaredTo(champion);
                    // checks other branch if it could contain any point closer than
                    // current champion
                    if (node.rightTop.rect.distanceSquaredTo(point) < minDistance) {
                        champion = nearest(node.rightTop, point, minDistance,
                                           champion);
                    }
                }
            }
            // only left branch could contain a point closer than current champion
            else if ((node.leftBottom != null) &&
                    (node.leftBottom.rect.distanceSquaredTo(point) < minDistance)) {
                champion = nearest(node.leftBottom, point, minDistance, champion);
            }
            // only right branch could contain a point closer than current champion
            else if ((node.rightTop != null) &&
                    (node.rightTop.rect.distanceSquaredTo(point) < minDistance)) {
                champion = nearest(node.rightTop, point, minDistance, champion);
            }
        }
        return champion;
    }

    // unit testing
    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        KdTreeST<Integer> kdtree = new KdTreeST<Integer>();
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.put(p, i);
        }
        Point2D pt;
        Stopwatch time = new Stopwatch();
        double t1 = time.elapsedTime();
        int m = 5000000;
        for (int i = 0; i < m; i++) {
            pt = new Point2D(StdRandom.uniformDouble(0.0, 1.0), StdRandom.uniformDouble(0.0, 1.0));
            kdtree.nearest(pt);
        }
        double t2 = time.elapsedTime();
        System.out.println(t2 - t1);
        System.out.println(m / (t2 - t1));
    }
}
