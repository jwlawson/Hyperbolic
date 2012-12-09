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
	private int nunberOfEdges;
	private int numberOfSites;
	private Site[] sites;
	private Site bottomsite;
	private int sqrt_nsites;
	private double minDistanceBetweenSites;
	private int PQcount;
	private int PQmin;
	private int PQhashsize;
	private HalfEdge PQhash[];

	private final static int LE = 0;
	private final static int RE = 1;

	private int ELhashsize;
	private HalfEdge ELhash[];
	private HalfEdge ELleftend, ELrightend;
	private List<GraphEdge> allEdges;

	/*********************************************************
	 * Public methods
	 ********************************************************/

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
		sort(xValuesIn, yValuesIn, xValuesIn.length);

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

		siteIndex = 0;
		voronoi_bd();

		return allEdges;
	}

	/*********************************************************
	 * Private methods - implementation details
	 ********************************************************/

	private void sort(double[] xValuesIn, double[] yValuesIn, int count) {
		sites = null;
		allEdges = new LinkedList<GraphEdge>();

		numberOfSites = count;
		nvertices = 0;
		nunberOfEdges = 0;

		double sn = (double) numberOfSites + 4;
		sqrt_nsites = (int) Math.sqrt(sn);

		// Copy the inputs so we don't modify the originals
		double[] xValues = new double[count];
		double[] yValues = new double[count];
		for (int i = 0; i < count; i++) {
			xValues[i] = xValuesIn[i];
			yValues[i] = yValuesIn[i];
		}
		sortNode(xValues, yValues, count);
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

	private void sortNode(double xValues[], double yValues[], int numPoints) {
		int i;
		numberOfSites = numPoints;
		sites = new Site[numberOfSites];
		xmin = xValues[0];
		ymin = yValues[0];
		xmax = xValues[0];
		ymax = yValues[0];
		for (i = 0; i < numberOfSites; i++) {
			sites[i] = new Site();
			sites[i].coord.setPoint(xValues[i], yValues[i]);
			sites[i].sitenbr = i;

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

	/* return a single in-storage site */
	private Site getNextSiteOrNull() {
		Site s;
		if (siteIndex < numberOfSites) {
			s = sites[siteIndex];
			siteIndex += 1;
			return (s);
		} else {
			return (null);
		}
	}

	private Edge findBisectorOfSites(Site s1, Site s2) {
		double dx, dy, adx, ady;

		Edge newedge = new Edge();

		// store the sites that this edge is bisecting
		newedge.reg[0] = s1;
		newedge.reg[1] = s2;
		// to begin with, there are no endpoints on the bisector - it goes to
		// infinity
		newedge.ep[0] = null;
		newedge.ep[1] = null;

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

		newedge.edgenbr = nunberOfEdges;

		nunberOfEdges += 1;
		return (newedge);
	}

	private void makevertex(Site v) {
		v.sitenbr = nvertices;
		nvertices += 1;
	}

	private boolean PQinitialize() {
		PQcount = 0;
		PQmin = 0;
		PQhashsize = 4 * sqrt_nsites;
		PQhash = new HalfEdge[PQhashsize];

		for (int i = 0; i < PQhashsize; i += 1) {
			PQhash[i] = new HalfEdge();
		}
		return true;
	}

	private int getPQbucket(HalfEdge he) {
		int bucket;

		bucket = (int) ((he.ystar - ymin) / deltay * PQhashsize);
		if (bucket < 0) {
			bucket = 0;
		}
		if (bucket >= PQhashsize) {
			bucket = PQhashsize - 1;
		}
		if (bucket < PQmin) {
			PQmin = bucket;
		}
		return (bucket);
	}

	// push the HalfEdge into the ordered linked list of vertices
	private void PQinsert(HalfEdge he, Site v, double offset) {
		HalfEdge last, next;

		he.vertex = v;
		he.ystar = v.coord.y + offset;
		last = PQhash[getPQbucket(he)];
		while ((next = last.PQnext) != null
				&& (he.ystar > next.ystar || (he.ystar == next.ystar && v.coord.x > next.vertex.coord.x))) {
			last = next;
		}
		he.PQnext = last.PQnext;
		last.PQnext = he;
		PQcount += 1;
	}

	// remove the HalfEdge from the list of vertices
	private void PQdelete(HalfEdge he) {

		if (he.vertex != null) {
			HalfEdge last = PQhash[getPQbucket(he)];
			while (last.PQnext != he) {
				last = last.PQnext;
			}

			last.PQnext = he.PQnext;
			PQcount -= 1;
			he.vertex = null;
		}
	}

	private boolean isPQempty() {
		return (PQcount == 0);
	}

	private Point PQ_min() {
		Point answer = new Point();

		while (PQhash[PQmin].PQnext == null) {
			PQmin += 1;
		}
		answer.x = PQhash[PQmin].PQnext.vertex.coord.x;
		answer.y = PQhash[PQmin].PQnext.ystar;
		return (answer);
	}

	private HalfEdge PQextractmin() {

		HalfEdge curr = PQhash[PQmin].PQnext;
		PQhash[PQmin].PQnext = curr.PQnext;
		PQcount -= 1;
		return (curr);
	}

	private HalfEdge createHalfEdge(Edge e, int pm) {
		HalfEdge answer;
		answer = new HalfEdge();
		answer.ELedge = e;
		answer.ELpm = pm;
		answer.PQnext = null;
		answer.vertex = null;
		return (answer);
	}

	private boolean ELinitialize() {
		ELhashsize = 2 * sqrt_nsites;
		ELhash = new HalfEdge[ELhashsize];

		for (int i = 0; i < ELhashsize; i += 1) {
			ELhash[i] = null;
		}
		ELleftend = createHalfEdge(null, 0);
		ELrightend = createHalfEdge(null, 0);
		ELleftend.halfEdgeToLeft = null;
		ELleftend.halfEdgeToRight = ELrightend;
		ELrightend.halfEdgeToLeft = ELleftend;
		ELrightend.halfEdgeToRight = null;
		ELhash[0] = ELleftend;
		ELhash[ELhashsize - 1] = ELrightend;

		return true;
	}

	private HalfEdge getHalfEdgeToRightOf(HalfEdge he) {
		return (he.halfEdgeToRight);
	}

	private HalfEdge getHalfEdgeToLeftOf(HalfEdge he) {
		return (he.halfEdgeToLeft);
	}

	private Site leftreg(HalfEdge he) {
		if (he.ELedge == null) {
			return (bottomsite);
		}
		return (he.ELpm == LE ? he.ELedge.reg[LE] : he.ELedge.reg[RE]);
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

		if (b < 0 || b >= ELhashsize) {
			return (null);
		}
		he = ELhash[b];
		if (he == null || !he.deleted) {
			return (he);
		}

		/* Hash table points to deleted half edge. Patch as necessary. */
		ELhash[b] = null;
		return (null);
	}

	private HalfEdge getLeftBoundHalfEdge(Point p) {

		/* Use hash table to get close to desired halfedge */
		// use the hash function to find the place in the hash map that this
		// HalfEdge should be
		int bucket = (int) ((p.x - xmin) / deltax * ELhashsize);

		// make sure that the bucket position in within the range of the hash
		// array
		if (bucket < 0) {
			bucket = 0;
		}
		if (bucket >= ELhashsize) {
			bucket = ELhashsize - 1;
		}

		HalfEdge he = getHalfEdgeFromHash(bucket);
		if (he == null)
		// if the HE isn't found, search backwards and forwards in the hash map
		// for the first non-null entry
		{
			for (int i = 1; i < ELhashsize; i += 1) {
				if ((he = getHalfEdgeFromHash(bucket - i)) != null) {
					break;
				}
				if ((he = getHalfEdgeFromHash(bucket + i)) != null) {
					break;
				}
			}
		}
		/* Now search linear list of halfedges for the correct one */
		if (he == ELleftend || (he != ELrightend && isPointRightOfEdge(he, p))) {
			// keep going right on the list until either the end is reached, or
			// you find the 1st edge which the point isn't to the right of
			do {
				he = he.halfEdgeToRight;
			} while (he != ELrightend && isPointRightOfEdge(he, p));
			he = he.halfEdgeToLeft;
		} else
		// if the point is to the left of the HalfEdge, then search left for
		// the HE just to the left of the point
		{
			do {
				he = he.halfEdgeToLeft;
			} while (he != ELleftend && !isPointRightOfEdge(he, p));
		}

		/* Update hash table and reference counts */
		if (bucket > 0 && bucket < ELhashsize - 1) {
			ELhash[bucket] = he;
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

		newEdge.site1 = leftSite.sitenbr;
		newEdge.site2 = rightSite.sitenbr;
	}

	private void clip_line(Edge e) {
		double pxmin, pxmax, pymin, pymax;
		Site s1, s2;
		double x1 = 0, x2 = 0, y1 = 0, y2 = 0;

		x1 = e.reg[0].coord.x;
		x2 = e.reg[1].coord.x;
		y1 = e.reg[0].coord.y;
		y2 = e.reg[1].coord.y;

		// if the distance between the two points this line was created from is
		// less than the square root of 2, then ignore it
		if (dist(new Site(x1, y1), new Site(x2, y2)) < minDistanceBetweenSites) {
			return;
		}
		pxmin = borderMinX;
		pxmax = borderMaxX;
		pymin = borderMinY;
		pymax = borderMaxY;

		if (e.a == 1.0 && e.b >= 0.0) {
			s1 = e.ep[1];
			s2 = e.ep[0];
		} else {
			s1 = e.ep[0];
			s2 = e.ep[1];
		}

		if (e.a == 1.0) {
			y1 = pymin;
			if (s1 != null && s1.coord.y > pymin) {
				y1 = s1.coord.y;
			}
			if (y1 > pymax) {
				y1 = pymax;
			}
			x1 = e.c - e.b * y1;
			y2 = pymax;
			if (s2 != null && s2.coord.y < pymax) {
				y2 = s2.coord.y;
			}

			if (y2 < pymin) {
				y2 = pymin;
			}
			x2 = (e.c) - (e.b) * y2;
			if (((x1 > pxmax) & (x2 > pxmax)) | ((x1 < pxmin) & (x2 < pxmin))) {
				return;
			}
			if (x1 > pxmax) {
				x1 = pxmax;
				y1 = (e.c - x1) / e.b;
			}
			if (x1 < pxmin) {
				x1 = pxmin;
				y1 = (e.c - x1) / e.b;
			}
			if (x2 > pxmax) {
				x2 = pxmax;
				y2 = (e.c - x2) / e.b;
			}
			if (x2 < pxmin) {
				x2 = pxmin;
				y2 = (e.c - x2) / e.b;
			}
		} else {
			x1 = pxmin;
			if (s1 != null && s1.coord.x > pxmin) {
				x1 = s1.coord.x;
			}
			if (x1 > pxmax) {
				x1 = pxmax;
			}
			y1 = e.c - e.a * x1;
			x2 = pxmax;
			if (s2 != null && s2.coord.x < pxmax) {
				x2 = s2.coord.x;
			}
			if (x2 < pxmin) {
				x2 = pxmin;
			}
			y2 = e.c - e.a * x2;
			if (((y1 > pymax) & (y2 > pymax)) | ((y1 < pymin) & (y2 < pymin))) {
				return;
			}
			if (y1 > pymax) {
				y1 = pymax;
				x1 = (e.c - y1) / e.a;
			}
			if (y1 < pymin) {
				y1 = pymin;
				x1 = (e.c - y1) / e.a;
			}
			if (y2 > pymax) {
				y2 = pymax;
				x2 = (e.c - y2) / e.a;
			}
			if (y2 < pymin) {
				y2 = pymin;
				x2 = (e.c - y2) / e.a;
			}
		}

		pushGraphEdge(e.reg[0], e.reg[1], x1, y1, x2, y2);
	}

	private void endpoint(Edge e, int lr, Site s) {
		e.ep[lr] = s;
		if (e.ep[RE - lr] == null) {
			return;
		}
		clip_line(e);
	}

	/** returns true if p is to right of halfedge e */
	private boolean isPointRightOfEdge(HalfEdge el, Point p) {
		Edge e;
		Site topsite;
		boolean right_of_site;
		boolean above, fast;
		double dxp, dyp, dxs, t1, t2, t3, yl;

		e = el.ELedge;
		topsite = e.reg[1];
		if (p.x > topsite.coord.x) {
			right_of_site = true;
		} else {
			right_of_site = false;
		}
		if (right_of_site && el.ELpm == LE) {
			return (true);
		}
		if (!right_of_site && el.ELpm == RE) {
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
				dxs = topsite.coord.x - (e.reg[0]).coord.x;
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
		return (el.ELpm == LE ? above : !above);
	}

	private Site rightreg(HalfEdge he) {
		if (he.ELedge == null)
		// if this halfedge has no edge, return the bottom site (whatever
		// that is)
		{
			return (bottomsite);
		}

		// if the ELpm field is zero, return the site 0 that this edge bisects,
		// otherwise return site number 1
		return (he.ELpm == LE ? he.ELedge.reg[RE] : he.ELedge.reg[LE]);
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
		Edge edge1, edge2, edge;
		HalfEdge halfEdgeL;
		double d, xint, yint;
		boolean rightOfSite;

		edge1 = el1.ELedge;
		edge2 = el2.ELedge;
		if (edge1 == null || edge2 == null) {
			return null;
		}

		// if the two edges bisect the same parent, return null
		if (edge1.reg[1] == edge2.reg[1]) {
			return null;
		}

		d = edge1.a * edge2.b - edge1.b * edge2.a;
		if (-1.0e-10 < d && d < 1.0e-10) {
			return null;
		}

		xint = (edge1.c * edge2.b - edge2.c * edge1.b) / d;
		yint = (edge2.c * edge1.a - edge1.c * edge2.a) / d;

		if ((edge1.reg[1].coord.y < edge2.reg[1].coord.y)
				|| (edge1.reg[1].coord.y == edge2.reg[1].coord.y && edge1.reg[1].coord.x < edge2.reg[1].coord.x)) {
			halfEdgeL = el1;
			edge = edge1;
		} else {
			halfEdgeL = el2;
			edge = edge2;
		}

		rightOfSite = xint >= edge.reg[1].coord.x;
		if ((rightOfSite && halfEdgeL.ELpm == LE) || (!rightOfSite && halfEdgeL.ELpm == RE)) {
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
		Point newintstar = null;
		int pm;
		HalfEdge lbnd, rbnd, llbnd, rrbnd, bisector;
		Edge edge;

		PQinitialize();
		ELinitialize();

		bottomsite = getNextSiteOrNull();
		newsite = getNextSiteOrNull();
		while (true) {
			if (!isPQempty()) {
				newintstar = PQ_min();
			}
			// if the lowest site has a smaller y value than the lowest vector
			// intersection,
			// process the site otherwise process the vector intersection

			if (newsite != null
					&& (isPQempty() || newsite.coord.y < newintstar.y || (newsite.coord.y == newintstar.y && newsite.coord.x < newintstar.x))) {
				/* new site is smallest -this is a site event */
				// get the first HalfEdge to the LEFT of the new site
				lbnd = getLeftBoundHalfEdge((newsite.coord));
				// get the first HalfEdge to the RIGHT of the new site
				rbnd = getHalfEdgeToRightOf(lbnd);
				// if this halfedge has no edge,bot =bottom site (whatever that
				// is)
				bottomSite = rightreg(lbnd);
				// create a new edge that bisects
				edge = findBisectorOfSites(bottomSite, newsite);

				// create a new HalfEdge, setting its ELpm field to 0
				bisector = createHalfEdge(edge, LE);
				// insert this new bisector edge between the left and right
				// vectors in a linked list
				insertHalfEdgeToLeftOf(lbnd, bisector);

				// if the new bisector intersects with the left edge,
				// remove the left edge's vertex, and put in the new one
				if ((p = findWhereEdgesIntersect(lbnd, bisector)) != null) {
					PQdelete(lbnd);
					PQinsert(lbnd, p, dist(p, newsite));
				}
				lbnd = bisector;
				// create a new HalfEdge, setting its ELpm field to 1
				bisector = createHalfEdge(edge, RE);
				// insert the new HE to the right of the original bisector
				// earlier in the IF stmt
				insertHalfEdgeToLeftOf(lbnd, bisector);

				// if this new bisector intersects with the new HalfEdge
				if ((p = findWhereEdgesIntersect(bisector, rbnd)) != null) {
					// push the HE into the ordered linked list of vertices
					PQinsert(bisector, p, dist(p, newsite));
				}
				newsite = getNextSiteOrNull();
			} else if (!isPQempty())
			/* intersection is smallest - this is a vector event */
			{
				// pop the HalfEdge with the lowest vector off the ordered list
				// of vectors
				lbnd = PQextractmin();
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
				topSite = rightreg(rbnd);

				v = lbnd.vertex; // get the vertex that caused this event
				makevertex(v); // set the vertex number - couldn't do this
				// earlier since we didn't know when it would be processed
				endpoint(lbnd.ELedge, lbnd.ELpm, v);
				// set the endpoint of
				// the left HalfEdge to be this vector
				endpoint(rbnd.ELedge, rbnd.ELpm, v);
				// set the endpoint of the right HalfEdge to
				// be this vector
				deleteHalfEdge(lbnd); // mark the lowest HE for
				// deletion - can't delete yet because there might be pointers
				// to it in Hash Map
				PQdelete(rbnd);
				// remove all vertex events to do with the right HE
				deleteHalfEdge(rbnd); // mark the right HE for
				// deletion - can't delete yet because there might be pointers
				// to it in Hash Map
				pm = LE; // set the pm variable to zero

				if (bottomSite.coord.y > topSite.coord.y)
				// if the site to the left of the event is higher than the
				// Site
				{ // to the right of it, then swap them and set the 'pm'
					// variable to 1
					temp = bottomSite;
					bottomSite = topSite;
					topSite = temp;
					pm = RE;
				}
				edge = findBisectorOfSites(bottomSite, topSite); // create an
																	// Edge (or
																	// line)
				// that is between the two Sites. This creates the formula of
				// the line, and assigns a line number to it
				bisector = createHalfEdge(edge, pm); // create a HE from the
														// Edge
														// 'e',
				// and make it point to that edge
				// with its ELedge field
				insertHalfEdgeToLeftOf(llbnd, bisector); // insert the new
															// bisector to the
				// right of the left HE
				endpoint(edge, RE - pm, v); // set one endpoint to the new edge
				// to be the vector point 'v'.
				// If the site to the left of this bisector is higher than the
				// right Site, then this endpoint
				// is put in position 0; otherwise in pos 1

				// if left HE and the new bisector intersect, then delete
				// the left HE, and reinsert it
				if ((p = findWhereEdgesIntersect(llbnd, bisector)) != null) {
					PQdelete(llbnd);
					PQinsert(llbnd, p, dist(p, bottomSite));
				}

				// if right HE and the new bisector intersect, then
				// reinsert it
				if ((p = findWhereEdgesIntersect(bisector, rrbnd)) != null) {
					PQinsert(bisector, p, dist(p, bottomSite));
				}
			} else {
				break;
			}
		}

		for (lbnd = getHalfEdgeToRightOf(ELleftend); lbnd != ELrightend; lbnd = getHalfEdgeToRightOf(lbnd)) {
			edge = lbnd.ELedge;
			clip_line(edge);
		}

		return true;
	}

}
