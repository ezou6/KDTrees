import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class PointST<Value> {

    // create symbol table tree
    private RedBlackBST<Point2D, Value> tree;

    // construct an empty symbol table of points
    public PointST() {
        tree = new RedBlackBST<Point2D, Value>();
    }

    // checks if parameter object is null or "valid"
    private void checkInvalid(Object x) {
        if (x == null) {
            throw new IllegalArgumentException("Argument is null!");
        }
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return tree.isEmpty();
    }


    // number of points
    public int size() {
        return tree.size();
    }

    // associate the value val with point p
    // takes parameter of 2d point to be inserted and its value val that it's mapped to
    public void put(Point2D point, Value val) {
        checkInvalid(point);
        checkInvalid(val);
        tree.put(point, val);
    }

    // value associated with point p
    // takes parameter of 2d point to be searched for in tree and returns the value
    // it's mapped to
    public Value get(Point2D point) {
        checkInvalid(point);
        return tree.get(point);
    }

    // does the symbol table contain point p?
    // takes parameter of 2d point to be searched for and returns
    // whether it's contained in the tree as a boolean
    public boolean contains(Point2D point) {
        checkInvalid(point);
        return tree.contains(point);
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {
        return tree.keys();
    }

    // takes query rectangle as parameter and returns Iterable<> data type
    // containing all points within the range
    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        checkInvalid(rect);
        // add new Iterable<> data type containing 2d points
        Queue<Point2D> result = new Queue<Point2D>();
        // run through every point and see if the query rectangle contains it
        for (Point2D p : points()) {
            if (rect.contains(p)) {
                result.enqueue(p);
            }
        }
        return result;
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    // takes query point Point2D as parameter and passes nearest neigboring point in tree
    public Point2D nearest(Point2D point) {
        checkInvalid(point);
        if (isEmpty()) {
            return null;
        }
        // set initial champion to the node with the minimum point value (arbritrary)
        Point2D champion = tree.ceiling(tree.min());
        double minDist = point.distanceSquaredTo(champion);


        // find champion and new mindistance by comparing distances between a given point and
        // the current minDistance
        for (Point2D m : points()) {
            if (m.distanceSquaredTo(point) < minDist) {
                champion = m;
                minDist = point.distanceSquaredTo(champion);
            }
        }
        return champion;
    }

    // unit testing
    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        PointST<Integer> brute = new PointST<Integer>();
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.put(p, i);
        }
        Point2D pt;
        Stopwatch time = new Stopwatch();
        double t1 = time.elapsedTime();
        for (int m = 0; m < 100; m++) {
            pt = new Point2D(StdRandom.uniformDouble(0.0, 1.0), StdRandom.uniformDouble(0.0, 1.0));
            brute.nearest(pt);
        }
        double t2 = time.elapsedTime();
        System.out.println(t2 - t1);
        System.out.println(100 / (t2 - t1));
    }
}
