package uk.co.jwlawson.hyperbolic.client.simplevoronoi;

/*
 * The author of this software is Steven Fortune.  Copyright (c) 1994 by AT&T
 * Bell Laboratories.
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

/* 
 * This code was originally written by Stephan Fortune in C code.  I, Shane O'Sullivan,
 * have since modified it, encapsulating it in a C++ class and, fixing memory leaks and
 * adding accessors to the Voronoi Edges.
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

/* 
 * Java Version by Zhenyu Pan
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Voronoi {
	// ************* Private members ******************
	private double borderMinX, borderMaxX, borderMinY, borderMaxY;
	private int siteIndex;
	private double xmin, xmax, ymin, ymax, deltax, deltay;
	private int nvertices;
	private int numberOfEdges;
	private int numberOfSites;
	private Site[] sites;
	private Site bottomsite;
	private int sqrt_nsites;
	private double minDistanceBetweenSites;
	private int intersectionEventscount;
	private int intersectionEventsmin;
	private int intersectionEventshashsize;
	private HalfEdge intersectionEventshash[];

	private final static int LEFT_EDGE = 0;
	private final static int RIGHT_EDGE = 1;

	private int edgeListHashsize;
	private HalfEdge edgeListHash[];
	private HalfEdge edgeListLeftEnd, edgeListRightEnd;
	private List<GraphEdge> allEdges;

	public Voronoi(double minDistanceBetweenSites) {
		siteIndex = 0;
		sites = null;

		allEdges = null;
		this.minDistanceBetweenSites = minDistanceBetweenSites;
	}

	/**
	 * 
	 * @param xValuesIn
	 *            Array of X values for each site.
	 * @param yValuesIn
	 *            Array of Y values for each site. Must be identical length to
	 *            yValuesIn
	 * @param minX
	 *            The minimum X of the bounding box around the voronoi
	 * @param maxX
	 *            The maximum X of the bounding box around the voronoi
	 * @param minY
	 *            The minimum Y of the bounding box around the voronoi
	 * @param maxY
	 *            The maximum Y of the bounding box around the voronoi
	 * @return
	 */
	public List<GraphEdge> generateVoronoi(double[] xValuesIn, double[] yValuesIn, double minX,
			double maxX, double minY, double maxY) {
		init(xValuesIn.length);
		sort(xValuesIn, yValuesIn, xValuesIn.length);

		setBoundingBox(minX, maxX, minY, maxY);

		siteIndex = 0;
		voronoi_bd();

		return allEdges;
	}

	private void setBoundingBox(double minX, double maxX, double minY, double maxY) {
		// Check bounding box inputs - if mins are bigger than maxes, swap them
		double temp = 0;
		if (minX > maxX) {
			temp = minX;
			minX = maxX;
			maxX = temp;
		}
		if (minY > maxY) {
			temp = minY;
			minY = maxY;
			maxY = temp;
		}
		borderMinX = minX;
		borderMinY = minY;
		borderMaxX = maxX;
		borderMaxY = maxY;
	}

	private void init(int count) {
		sites = null;
		allEdges = new LinkedList<GraphEdge>();
	
		numberOfSites = count;
		nvertices = 0;
		numberOfEdges = 0;
	
		double sn = (double) numberOfSites + 4;
		sqrt_nsites = (int) Math.sqrt(sn);
	}

	private void sort(double[] xValuesIn, double[] yValuesIn, int count) {
		// Copy the inputs so we don't modify the originals
		double[] xValues = new double[count];
		double[] yValues = new double[count];
		for (int i = 0; i < count; i++) {
			xValues[i] = xValuesIn[i];
			yValues[i] = yValuesIn[i];
		}
		sortNode(xValues, yValues, count);
	}

	private void sortNode(double xValues[], double yValues[], int numPoints) {
		numberOfSites = numPoints;
		sites = new Site[numberOfSites];
		xmin = xValues[0];
		ymin = yValues[0];
		xmax = xValues[0];
		ymax = yValues[0];
		for (int i = 0; i < numberOfSites; i++) {
			sites[i] = new Site();
			sites[i].coord.setPoint(xValues[i], yValues[i]);
			sites[i].siteNumber = i;

			if (xValues[i] < xmin) {
				xmin = xValues[i];
			} else if (xValues[i] > xmax) {
				xmax = xValues[i];
			}

			if (yValues[i] < ymin) {
				ymin = yValues[i];
			} else if (yValues[i] > ymax) {
				ymax = yValues[i];
			}
		}
		qsort(sites);
		deltay = ymax - ymin;
		deltax = xmax - xmin;
	}

	private void qsort(Site[] sites) {
		List<Site> listSites = new ArrayList<Site>(sites.length);
		for (Site s : sites) {
			listSites.add(s);
		}

		Collections.sort(listSites, new Comparator<Site>() {
			@Override
			public final int compare(Site p1, Site p2) {
				Point s1 = p1.coord, s2 = p2.coord;
				if (s1.y < s2.y) {
					return (-1);
				}
				if (s1.y > s2.y) {
					return (1);
				}
				if (s1.x < s2.x) {
					return (-1);
				}
				if (s1.x > s2.x) {
					return (1);
				}
				return (0);
			}
		});

		// Copy back into the array
		for (int i = 0; i < sites.length; i++) {
			sites[i] = listSites.get(i);
		}
	}

	/* return a single in-storage site */
	private Site getNextSiteOrNull() {
		if (siteIndex < numberOfSites) {
			Site s = sites[siteIndex];
			siteIndex += 1;
			return s;
		} else {
			return null;
		}
	}

	private Edge findBisectorOfSites(Site s1, Site s2) {
		double dx, dy, adx, ady;

		Edge newedge = new Edge();

		// store the sites that this edge is bisecting
		newedge.sitesGeneratingThis[0] = s1;
		newedge.sitesGeneratingThis[1] = s2;
		// to begin with, there are no endpoints on the bisector - it goes to
		// infinity
		newedge.endPoints[0] = null;
		newedge.endPoints[1] = null;

		// get the difference in x dist between the sites
		dx = s2.coord.x - s1.coord.x;
		dy = s2.coord.y - s1.coord.y;
		// make sure that the difference in positive
		adx = dx > 0 ? dx : -dx;
		ady = dy > 0 ? dy : -dy;
		// get the slope of the line
		newedge.c = s1.coord.x * dx + s1.coord.y * dy + (dx * dx + dy * dy) * 0.5;
		if (adx > ady) {
			newedge.a = 1.0f;
			newedge.b = dy / dx;
			newedge.c /= dx;// set formula of line, with x fixed to 1
		} else {
			newedge.b = 1.0f;
			newedge.a = dx / dy;
			newedge.c /= dy;// set formula of line, with y fixed to 1
		}
		/*
		 * Lines are stored in the form ax + by = c
		 * I think.
		 */

		newedge.edgeNumber = numberOfEdges;

		numberOfEdges += 1;
		return newedge;
	}

	private void makevertex(Site v) {
		v.siteNumber = nvertices;
		nvertices += 1;
	}

	private boolean intersectionEventsinitialize() {
		intersectionEventscount = 0;
		intersectionEventsmin = 0;
		intersectionEventshashsize = 4 * sqrt_nsites;
		intersectionEventshash = new HalfEdge[intersectionEventshashsize];

		for (int i = 0; i < intersectionEventshashsize; i += 1) {
			intersectionEventshash[i] = new HalfEdge();
		}
		return true;
	}

	private int getintersectionEventsbucket(HalfEdge he) {
		int bucket;

		bucket = (int) ((he.ystar - ymin) / deltay * intersectionEventshashsize);
		if (bucket < 0) {
			bucket = 0;
		}
		if (bucket >= intersectionEventshashsize) {
			bucket = intersectionEventshashsize - 1;
		}
		if (bucket < intersectionEventsmin) {
			intersectionEventsmin = bucket;
		}
		return (bucket);
	}

	// push the HalfEdge into the ordered linked list of vertices
	private void intersectionEventsinsert(HalfEdge he, Site v, double offset) {
		HalfEdge last, next;

		he.vertex = v;
		he.ystar = v.coord.y + offset;
		last = intersectionEventshash[getintersectionEventsbucket(he)];
		while ((next = last.intersectionEventsNext) != null
				&& (he.ystar > next.ystar || (he.ystar == next.ystar && v.coord.x > next.vertex.coord.x))) {
			last = next;
		}
		he.intersectionEventsNext = last.intersectionEventsNext;
		last.intersectionEventsNext = he;
		intersectionEventscount += 1;
	}

	// remove the HalfEdge from the list of vertices
	private void intersectionEventsdelete(HalfEdge he) {

		if (he.vertex != null) {
			HalfEdge last = intersectionEventshash[getintersectionEventsbucket(he)];
			while (last.intersectionEventsNext != he) {
				last = last.intersectionEventsNext;
			}

			last.intersectionEventsNext = he.intersectionEventsNext;
			intersectionEventscount -= 1;
			he.vertex = null;
		}
	}

	private boolean isintersectionEventsEmpty() {
		return (intersectionEventscount == 0);
	}

	private Point intersectionEvents_min() {
		Point answer = new Point();

		while (intersectionEventshash[intersectionEventsmin].intersectionEventsNext == null) {
			intersectionEventsmin += 1;
		}
		answer.x = intersectionEventshash[intersectionEventsmin].intersectionEventsNext.vertex.coord.x;
		answer.y = intersectionEventshash[intersectionEventsmin].intersectionEventsNext.ystar;
		return answer;
	}

	private HalfEdge intersectionEventsextractmin() {

		HalfEdge curr = intersectionEventshash[intersectionEventsmin].intersectionEventsNext;
		intersectionEventshash[intersectionEventsmin].intersectionEventsNext = curr.intersectionEventsNext;
		intersectionEventscount -= 1;
		return (curr);
	}

	private HalfEdge createHalfEdge(Edge e, int pm) {
		HalfEdge answer;
		answer = new HalfEdge();
		answer.edgeListEdge = e;
		answer.ELpm = pm;
		answer.intersectionEventsNext = null;
		answer.vertex = null;
		return (answer);
	}

	private void edgeListInitialize() {
		edgeListHashsize = 2 * sqrt_nsites;
		edgeListHash = new HalfEdge[edgeListHashsize];

		for (int i = 0; i < edgeListHashsize; i += 1) {
			edgeListHash[i] = null;
		}
		edgeListLeftEnd = createHalfEdge(null, 0);
		edgeListRightEnd = createHalfEdge(null, 0);
		edgeListLeftEnd.halfEdgeToLeft = null;
		edgeListLeftEnd.halfEdgeToRight = edgeListRightEnd;
		edgeListRightEnd.halfEdgeToLeft = edgeListLeftEnd;
		edgeListRightEnd.halfEdgeToRight = null;
		edgeListHash[0] = edgeListLeftEnd;
		edgeListHash[edgeListHashsize - 1] = edgeListRightEnd;

	}

	private HalfEdge getHalfEdgeToRightOf(HalfEdge he) {
		return (he.halfEdgeToRight);
	}

	private HalfEdge getHalfEdgeToLeftOf(HalfEdge he) {
		return (he.halfEdgeToLeft);
	}

	private Site leftreg(HalfEdge he) {
		if (he.edgeListEdge == null) {
			return (bottomsite);
		}
		return (he.ELpm == LEFT_EDGE ? he.edgeListEdge.sitesGeneratingThis[LEFT_EDGE]
				: he.edgeListEdge.sitesGeneratingThis[RIGHT_EDGE]);
	}

	private void insertHalfEdgeToLeftOf(HalfEdge lb, HalfEdge newHe) {
		newHe.halfEdgeToLeft = lb;
		newHe.halfEdgeToRight = lb.halfEdgeToRight;
		(lb.halfEdgeToRight).halfEdgeToLeft = newHe;
		lb.halfEdgeToRight = newHe;
	}

	/*
	 * This delete routine can't reclaim node, since pointers from hash table
	 * may be present.
	 */
	private void deleteHalfEdge(HalfEdge he) {
		(he.halfEdgeToLeft).halfEdgeToRight = he.halfEdgeToRight;
		(he.halfEdgeToRight).halfEdgeToLeft = he.halfEdgeToLeft;
		he.deleted = true;
	}

	/* Get entry from hash table, pruning any deleted nodes */
	private HalfEdge getHalfEdgeFromHash(int b) {
		HalfEdge he;

		if (b < 0 || b >= edgeListHashsize) {
			return (null);
		}
		he = edgeListHash[b];
		if (he == null || !he.deleted) {
			return (he);
		}

		/* Hash table points to deleted half edge. Patch as necessary. */
		edgeListHash[b] = null;
		return (null);
	}

	private HalfEdge getLeftBoundHalfEdge(Point p) {

		/* Use hash table to get close to desired halfedge */
		// use the hash function to find the place in the hash map that this
		// HalfEdge should be
		int bucket = (int) ((p.x - xmin) / deltax * edgeListHashsize);

		// make sure that the bucket position in within the range of the hash
		// array
		if (bucket < 0) {
			bucket = 0;
		}
		if (bucket >= edgeListHashsize) {
			bucket = edgeListHashsize - 1;
		}

		HalfEdge he = getHalfEdgeFromHash(bucket);
		if (he == null)
		// if the HE isn't found, search backwards and forwards in the hash map
		// for the first non-null entry
		{
			for (int i = 1; i < edgeListHashsize; i += 1) {
				if ((he = getHalfEdgeFromHash(bucket - i)) != null) {
					break;
				}
				if ((he = getHalfEdgeFromHash(bucket + i)) != null) {
					break;
				}
			}
		}
		/* Now search linear list of halfedges for the correct one */
		if (he == edgeListLeftEnd || (he != edgeListRightEnd && isPointRightOfEdge(he, p))) {
			// keep going right on the list until either the end is reached, or
			// you find the 1st edge which the point isn't to the right of
			do {
				he = he.halfEdgeToRight;
			} while (he != edgeListRightEnd && isPointRightOfEdge(he, p));
			he = he.halfEdgeToLeft;
		} else
		// if the point is to the left of the HalfEdge, then search left for
		// the HE just to the left of the point
		{
			do {
				he = he.halfEdgeToLeft;
			} while (he != edgeListLeftEnd && !isPointRightOfEdge(he, p));
		}

		/* Update hash table and reference counts */
		if (bucket > 0 && bucket < edgeListHashsize - 1) {
			edgeListHash[bucket] = he;
		}
		return (he);
	}

	private void pushGraphEdge(Site leftSite, Site rightSite, double x1, double y1, double x2,
			double y2) {
		GraphEdge newEdge = new GraphEdge();
		allEdges.add(newEdge);
		newEdge.x1 = x1;
		newEdge.y1 = y1;
		newEdge.x2 = x2;
		newEdge.y2 = y2;

		newEdge.site1 = leftSite.siteNumber;
		newEdge.site2 = rightSite.siteNumber;
	}

	private void findEndpointsAndAddToList(Edge edge) {
		Site s1, s2;
		double x1 = 0, x2 = 0, y1 = 0, y2 = 0;

		x1 = edge.sitesGeneratingThis[0].coord.x;
		x2 = edge.sitesGeneratingThis[1].coord.x;
		y1 = edge.sitesGeneratingThis[0].coord.y;
		y2 = edge.sitesGeneratingThis[1].coord.y;

		// if the distance between the two points this line was created from is
		// less than the square root of 2, then ignore it
		if (dist(new Site(x1, y1), new Site(x2, y2)) < minDistanceBetweenSites) {
			return;
		}

		if (edge.a == 1.0 && edge.b >= 0.0) {
			s1 = edge.endPoints[1];
			s2 = edge.endPoints[0];
		} else {
			s1 = edge.endPoints[0];
			s2 = edge.endPoints[1];
		}

		if (edge.a == 1.0) {
			y1 = borderMinY;
			if (s1 != null && s1.coord.y > borderMinY) {
				y1 = s1.coord.y;
			}
			if (y1 > borderMaxY) {
				y1 = borderMaxY;
			}
			x1 = edge.c - edge.b * y1;
			y2 = borderMaxY;
			if (s2 != null && s2.coord.y < borderMaxY) {
				y2 = s2.coord.y;
			}

			if (y2 < borderMinY) {
				y2 = borderMinY;
			}
			x2 = (edge.c) - (edge.b) * y2;
			if (((x1 > borderMaxX) & (x2 > borderMaxX)) | ((x1 < borderMinX) & (x2 < borderMinX))) {
				return;
			}
			if (x1 > borderMaxX) {
				x1 = borderMaxX;
				y1 = (edge.c - x1) / edge.b;
			}
			if (x1 < borderMinX) {
				x1 = borderMinX;
				y1 = (edge.c - x1) / edge.b;
			}
			if (x2 > borderMaxX) {
				x2 = borderMaxX;
				y2 = (edge.c - x2) / edge.b;
			}
			if (x2 < borderMinX) {
				x2 = borderMinX;
				y2 = (edge.c - x2) / edge.b;
			}
		} else {
			x1 = borderMinX;
			if (s1 != null && s1.coord.x > borderMinX) {
				x1 = s1.coord.x;
			}
			if (x1 > borderMaxX) {
				x1 = borderMaxX;
			}
			y1 = edge.c - edge.a * x1;
			x2 = borderMaxX;
			if (s2 != null && s2.coord.x < borderMaxX) {
				x2 = s2.coord.x;
			}
			if (x2 < borderMinX) {
				x2 = borderMinX;
			}
			y2 = edge.c - edge.a * x2;
			if (((y1 > borderMaxY) & (y2 > borderMaxY)) | ((y1 < borderMinY) & (y2 < borderMinY))) {
				return;
			}
			if (y1 > borderMaxY) {
				y1 = borderMaxY;
				x1 = (edge.c - y1) / edge.a;
			}
			if (y1 < borderMinY) {
				y1 = borderMinY;
				x1 = (edge.c - y1) / edge.a;
			}
			if (y2 > borderMaxY) {
				y2 = borderMaxY;
				x2 = (edge.c - y2) / edge.a;
			}
			if (y2 < borderMinY) {
				y2 = borderMinY;
				x2 = (edge.c - y2) / edge.a;
			}
		}

		pushGraphEdge(edge.sitesGeneratingThis[0], edge.sitesGeneratingThis[1], x1, y1, x2, y2);
	}

	private void setEndpoint(Edge e, int leftOrRight, Site endPoint) {
		e.endPoints[leftOrRight] = endPoint;
		if (e.endPoints[RIGHT_EDGE - leftOrRight] == null) {
			return;
		}
		findEndpointsAndAddToList(e);
	}

	/** returns true if p is to right of halfedge e */
	private boolean isPointRightOfEdge(HalfEdge el, Point p) {
		Edge e;
		Site topsite;
		boolean right_of_site;
		boolean above, fast;
		double dxp, dyp, dxs, t1, t2, t3, yl;

		e = el.edgeListEdge;
		topsite = e.sitesGeneratingThis[1];
		if (p.x > topsite.coord.x) {
			right_of_site = true;
		} else {
			right_of_site = false;
		}
		if (right_of_site && el.ELpm == LEFT_EDGE) {
			return (true);
		}
		if (!right_of_site && el.ELpm == RIGHT_EDGE) {
			return (false);
		}

		if (e.a == 1.0) {
			dyp = p.y - topsite.coord.y;
			dxp = p.x - topsite.coord.x;
			fast = false;
			if ((!right_of_site & (e.b < 0.0)) | (right_of_site & (e.b >= 0.0))) {
				above = dyp >= e.b * dxp;
				fast = above;
			} else {
				above = p.x + p.y * e.b > e.c;
				if (e.b < 0.0) {
					above = !above;
				}
				if (!above) {
					fast = true;
				}
			}
			if (!fast) {
				dxs = topsite.coord.x - (e.sitesGeneratingThis[0]).coord.x;
				above = e.b * (dxp * dxp - dyp * dyp) < dxs * dyp
						* (1.0 + 2.0 * dxp / dxs + e.b * e.b);
				if (e.b < 0.0) {
					above = !above;
				}
			}
		} else /* e.b==1.0 */

		{
			yl = e.c - e.a * p.x;
			t1 = p.y - yl;
			t2 = p.x - topsite.coord.x;
			t3 = yl - topsite.coord.y;
			above = t1 * t1 > t2 * t2 + t3 * t3;
		}
		return (el.ELpm == LEFT_EDGE ? above : !above);
	}

	private Site getSiteGeneratingEdgeOnRight(HalfEdge he) {
		if (he.edgeListEdge == null)
		// if this halfedge has no edge, return the bottom site (whatever
		// that is)
		{
			return (bottomsite);
		}

		// if the ELpm field is zero, return the site 0 that this edge bisects,
		// otherwise return site number 1
		return (he.ELpm == LEFT_EDGE ? he.edgeListEdge.sitesGeneratingThis[RIGHT_EDGE]
				: he.edgeListEdge.sitesGeneratingThis[LEFT_EDGE]);
	}

	protected double dist(Site s, Site t) {
		double dx, dy;
		dx = s.coord.x - t.coord.x;
		dy = s.coord.y - t.coord.y;
		return (Math.sqrt(dx * dx + dy * dy));
	}

	// create a new site where the HalfEdges el1 and el2 intersect - note that
	// the Point in the argument list is not used, don't know why it's there
	private Site findWhereEdgesIntersect(HalfEdge el1, HalfEdge el2) {
		Edge el1Edge, el2Edge, edge;
		HalfEdge halfEdgeL;
		double d, xint, yint;
		boolean rightOfSite;

		el1Edge = el1.edgeListEdge;
		el2Edge = el2.edgeListEdge;
		if (el1Edge == null || el2Edge == null) {
			return null;
		}

		// if the two edges bisect the same parent, return null
		if (el1Edge.sitesGeneratingThis[1] == el2Edge.sitesGeneratingThis[1]) {
			return null;
		}

		d = el1Edge.a * el2Edge.b - el1Edge.b * el2Edge.a;
		if (-1.0e-10 < d && d < 1.0e-10) {
			return null;
		}

		xint = (el1Edge.c * el2Edge.b - el2Edge.c * el1Edge.b) / d;
		yint = (el2Edge.c * el1Edge.a - el1Edge.c * el2Edge.a) / d;

		if ((el1Edge.sitesGeneratingThis[1].coord.y < el2Edge.sitesGeneratingThis[1].coord.y)
				|| (el1Edge.sitesGeneratingThis[1].coord.y == el2Edge.sitesGeneratingThis[1].coord.y && el1Edge.sitesGeneratingThis[1].coord.x < el2Edge.sitesGeneratingThis[1].coord.x)) {
			halfEdgeL = el1;
			edge = el1Edge;
		} else {
			halfEdgeL = el2;
			edge = el2Edge;
		}

		rightOfSite = (xint >= edge.sitesGeneratingThis[1].coord.x);
		if ((rightOfSite && halfEdgeL.ELpm == LEFT_EDGE)
				|| (!rightOfSite && halfEdgeL.ELpm == RIGHT_EDGE)) {
			return null;
		}

		// create a new site at the point of intersection - this is a new vector
		// event waiting to happen
		Site site = new Site();
		site.coord.x = xint;
		site.coord.y = yint;
		return (site);
	}

	/*
	 * implicit parameters: numberOfSites, sqrt_nsites, xmin, xmax, ymin, ymax,
	 * deltax,
	 * deltay (can all be estimates). Performance suffers if they are wrong;
	 * better to make numberOfSites, deltax, and deltay too big than too small.
	 * (?)
	 */
	private boolean voronoi_bd() {
		Site newsite, bottomSite, topSite, temp, p;
		Site v;
		Point minUnhandledIntersectionEvent = null;
		int pm;
		HalfEdge lbnd, rbnd, llbnd, rrbnd, bisector;
		Edge edge;

		intersectionEventsinitialize();
		edgeListInitialize();

		bottomsite = getNextSiteOrNull();
		newsite = getNextSiteOrNull();
		while (true) {
			if (!isintersectionEventsEmpty()) {
				minUnhandledIntersectionEvent = intersectionEvents_min();
			}

			if (isSiteEvent(newsite, minUnhandledIntersectionEvent)) {
				/* new site is smallest -this is a site event */
				// get the first HalfEdge to the LEFT of the new site
				lbnd = getLeftBoundHalfEdge((newsite.coord));
				// get the first HalfEdge to the RIGHT of the new site
				rbnd = getHalfEdgeToRightOf(lbnd);
				// if this halfedge has no edge,bot =bottom site (whatever that
				// is)
				bottomSite = getSiteGeneratingEdgeOnRight(lbnd);
				// create a new edge that bisects
				edge = findBisectorOfSites(bottomSite, newsite);

				// create a new HalfEdge, setting its ELpm field to 0
				bisector = createHalfEdge(edge, LEFT_EDGE);
				// insert this new bisector edge between the left and right
				// vectors in a linked list
				insertHalfEdgeToLeftOf(lbnd, bisector);

				// if the new bisector intersects with the left edge,
				// remove the left edge's vertex, and put in the new one
				if ((p = findWhereEdgesIntersect(lbnd, bisector)) != null) {
					intersectionEventsdelete(lbnd);
					intersectionEventsinsert(lbnd, p, dist(p, newsite));
				}
				lbnd = bisector;
				// create a new HalfEdge, setting its ELpm field to 1
				bisector = createHalfEdge(edge, RIGHT_EDGE);
				// insert the new HE to the right of the original bisector
				// earlier in the IF stmt
				insertHalfEdgeToLeftOf(lbnd, bisector);

				// if this new bisector intersects with the new HalfEdge
				if ((p = findWhereEdgesIntersect(bisector, rbnd)) != null) {
					// push the HE into the ordered linked list of vertices
					intersectionEventsinsert(bisector, p, dist(p, newsite));
				}
				newsite = getNextSiteOrNull();
			} else if (!isintersectionEventsEmpty())
			/* intersection is smallest - this is a vector event */
			{
				// pop the HalfEdge with the lowest vector off the ordered list
				// of vectors
				lbnd = intersectionEventsextractmin();
				// get the HalfEdge to the left of the above HE
				llbnd = getHalfEdgeToLeftOf(lbnd);
				// get the HalfEdge to the right of the above HE
				rbnd = getHalfEdgeToRightOf(lbnd);
				// get the HalfEdge to the right of the HE to the right of the
				// lowest HE
				rrbnd = getHalfEdgeToRightOf(rbnd);
				// get the Site to the left of the left HE which it bisects
				bottomSite = leftreg(lbnd);
				// get the Site to the right of the right HE which it bisects
				topSite = getSiteGeneratingEdgeOnRight(rbnd);

				v = lbnd.vertex; // get the vertex that caused this event
				makevertex(v); // set the vertex number - couldn't do this
				// earlier since we didn't know when it would be processed
				setEndpoint(lbnd.edgeListEdge, lbnd.ELpm, v);
				// set the endpoint of
				// the left HalfEdge to be this vector
				setEndpoint(rbnd.edgeListEdge, rbnd.ELpm, v);
				// set the endpoint of the right HalfEdge to
				// be this vector
				deleteHalfEdge(lbnd); // mark the lowest HE for
				// deletion - can't delete yet because there might be pointers
				// to it in Hash Map
				intersectionEventsdelete(rbnd);
				// remove all vertex events to do with the right HE
				deleteHalfEdge(rbnd); // mark the right HE for
				// deletion - can't delete yet because there might be pointers
				// to it in Hash Map
				pm = LEFT_EDGE; // set the pm variable to zero

				if (bottomSite.coord.y > topSite.coord.y)
				// if the site to the left of the event is higher than the
				// Site
				{ // to the right of it, then swap them and set the 'pm'
					// variable to 1
					temp = bottomSite;
					bottomSite = topSite;
					topSite = temp;
					pm = RIGHT_EDGE;
				}

				edge = findBisectorOfSites(bottomSite, topSite);
				// create an Edge (or line)
				// that is between the two Sites. This creates the formula of
				// the line, and assigns a line number to it

				bisector = createHalfEdge(edge, pm);
				// create a HE from the Edge 'e',
				// and make it point to that edge
				// with its edgeListEdge field
				insertHalfEdgeToLeftOf(llbnd, bisector);
				// insert the new bisector to the
				// right of the left HE
				setEndpoint(edge, RIGHT_EDGE - pm, v);
				// set one endpoint to the new edge
				// to be the vector point 'v'.
				// If the site to the left of this bisector is higher than the
				// right Site, then this endpoint
				// is put in position 0; otherwise in pos 1

				// if left HE and the new bisector intersect, then delete
				// the left HE, and reinsert it
				if ((p = findWhereEdgesIntersect(llbnd, bisector)) != null) {
					intersectionEventsdelete(llbnd);
					intersectionEventsinsert(llbnd, p, dist(p, bottomSite));
				}

				// if right HE and the new bisector intersect, then
				// reinsert it
				if ((p = findWhereEdgesIntersect(bisector, rrbnd)) != null) {
					intersectionEventsinsert(bisector, p, dist(p, bottomSite));
				}
			} else {
				break;
			}
		}

		for (lbnd = getHalfEdgeToRightOf(edgeListLeftEnd); lbnd != edgeListRightEnd; lbnd = getHalfEdgeToRightOf(lbnd)) {
			edge = lbnd.edgeListEdge;
			findEndpointsAndAddToList(edge);
		}

		return true;
	}

	private boolean isSiteEvent(Site newsite, Point newintstar) {
		// if the lowest site has a smaller y value than the lowest vector
		// intersection,
		// process the site otherwise process the vector intersection
		boolean result = (newsite != null);
		result = result
				&& (isintersectionEventsEmpty() || newsite.coord.y < newintstar.y || (newsite.coord.y == newintstar.y && newsite.coord.x < newintstar.x));
		return result;
	}

}
