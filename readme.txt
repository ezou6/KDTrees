Programming Assignment 4: K-d Trees

/* *****************************************************************************
 *  Describe the Node data type you used to implement the
 *  2d-tree data structure.
 **************************************************************************** */

Our node data type has six instance variables, which are the 2d point (our key), the
value mapped to the key by the symbol table, its bounding box (where all its possible children can lie)
stored as a RectHV object, its leftBottom subtree, its rightTop subtree, and its orientation
isX. However, the actual constructor only allows the user to set four of these instance variables
which are the point, value, orientation and box. This is because the leftbottom and righttop children
nodes are specified upon recursively constructing a new node where it serves as the current node. In addition,
it would be a waste of memory allocation to purposely set the leftBottom and rightBottom
children each time a new node is created as we have to check if those children are not null.
Storing the orientation in the node is helpful in the put and get method for traversing down one
path in the tree. Storing the box in the node is helpful for our nearest and range methods when we have to check which nodes'
bounding boxes contain the query point or intersect with the given rect.

/* *****************************************************************************
 *  Describe your method for range search in a k-d tree.
 **************************************************************************** */

Our range search method uses a private helper class for recursively traversing down the
tree and checking to see if the children nodes have bounding boxes intersecting with the given rect/box.
In our public method, we only set up an Iterable<> data type to be returned, which was a queue containing
2d points. Then we called our private helper class, which has a void return type. We manualy return the
created queue.

Our private range method accepts three parameters which are the current node it's checking (technically the parent
since we pass in the root in our public class to begin), the rectHV object that we want to search in, and the newly created queue
containing all points that are included in the passed in rectHv object. First we check if the current node is null. If it's the first
pass with the root, then we have an empty symbol table and we return out of the private range method, returning an empty queue of 2d points.
Otherwise, if the currentNode reaches the end of a path, then we similarly return out of the private range method. Similarly,
we return out of the private range method if we find that our current node's bounding box doesn't intersect with the passed in RectHV object,
which means it's meaningless to further check if that node's children are contained within the rect. If the node's bounding box
does intersect, we'll check if the given rectHV actually contains the 2d-point of the current node. If so, enqueue it into our queue.
Then recursively call the private range method for the left bottom and right bottom children nodes as we will have to check both
sides by default. The if statements above the recursive calls all intend to prune and optimize the tree to avoid taking
unnecessary traversals down.


/* *****************************************************************************
 *  Describe your method for nearest neighbor search in a k-d tree.
 **************************************************************************** */
To search for the nearest neighbor, we first call the helper method on the root
node. The helper method loops recursively, and at the beginning it always checks
if the point is equal to the query point (set new champion) or if it is null (return since
no need to traverse down a non-existent path) before updating the champion. If the current node
isn't null, but has a point that isn't identical to the query point, we will check if
its distance to the query point is closer than the current champion. if so, update the champion
and minDistance. Next, we will recursively traverse down the tree and always go toward the query point
(if possible, subtree with bounding box that contains the query point); this way, we may not be required
to traverse down the other subtree due to our pruning rule. Once we go toward the subtree containing the query point,
we'll check if it is closer to the query than our current champion and update champion
and minDistance. Then, we'll check if the distance from our query to the BOUNDING BOX of the other subtree
is smaller than our query to our current champion and update champion and minDistance; this is because, even
if that other child's point may not necessarily be closer to the query than our current champion, its children
could be. Finally, we have the case where neither subtree contains the query point, but it is still worth traversing
down the tree. We have three cases. First, if the distances from the query point to both BOUNDING BOXES of both subtrees
is smaller than the current minDistance, we'll check which subtree to go toward first and update
champion and minDistance. Then, we'll check if it's still worth going toward the other subtree (might be pruend), and update
champion. The second case is if only the distance from either the query to the rectangle of rightTop subtree or
the query to the rectangle of the leftBottom subtree is smaller than the minDistance and we'll update champion.
The third case, which isn't explictly programmed, is if both distances from the subtrees' rectangles to the query is not smaller
than the current minDistance. In that instance, we would just return champion, which happens anyway at the end of the method.



/* *****************************************************************************
 *  How many nearest-neighbor calculations can your PointST implementation
 *  perform per second for input1M.txt (1 million points), where the query
 *  points are random points in the unit square?
 *
 *  Fill in the table below, rounding each value to use one digit after
 *  the decimal point. Use at least 1 second of CPU time. Do not use -Xint.
 *  (Do not count the time to read the points or to build the 2d-tree.)
 *  (See the checklist for information on how to do this)
 *
 *  Repeat the same question but with your KdTreeST implementation.
 *
 **************************************************************************** */


                 # calls to         /   CPU time     =   # calls to nearest()
                 client nearest()       (seconds)        per second
                ------------------------------------------------------
PointST:         100                    2.3              42.8

KdTreeST:        5000000                6.2              801410.5

Note: more calls per second indicates better performance.

/* *****************************************************************************
 *  Suppose you wanted to add a method numberInRange(RectHV rect) to your
 *  KdTreeST, which should return the number of points that are inside rect
 *  (or on the boundary), i.e. the number of points in the iterable returned by
 *  calling range(rect).
 *
 *  Describe a pruning rule that would make this more efficient than the
 *  range() method. Also, briefly describe how you would implement it.
 *
 *  Hint: consider a range search. What can you do when the query rectangle
 *  completely contains the rectangle corresponding to a node?
 **************************************************************************** */

We would first modify the private node constructor so that it would store the size of
the subtree at the node (the current node and its children). Then, we would follow this pruning rule:
if we find a node with a rectangle that is completely contained by the query rectangle, then we can
infer that all of that node's children are also contaiend in the rectangle. Since we have access to
the size of the node (including its subtrees/children), we can just return the size with no need to traverse
down other paths.

/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on  how helpful the class meeting was and on how much you learned
 * from doing the assignment, and whether you enjoyed doing it.
 **************************************************************************** */

Although the project was difficult to debug and we had trouble in finding all the edge
cases to the nearest() method, we had fun working out the logic and found the project
mentally stimulating. It was interesting to refine our recursive programming skills.
