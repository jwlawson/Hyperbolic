/**
 * Copyright 2012 John Lawson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.jwlawson.hyperbolic.client.group;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.hyperbolic.HypPoint;

/**
 * @author John
 * 
 */
public class HypOrbitPoints implements Iterator<Point> {

	private static final Logger log = Logger.getLogger("HypOrbitPoints");

	private Point a = new Point(0.5, 0);
	private Point am = new Point(-a.getX(), -a.getY());
	private Point b = new Point(0, 0.5);
	private Point bm = new Point(-b.getX(), -b.getY());

	private Point current, next;
	private HypPoint hyp;
	private Iterator<Point> currentDepthIter;
	private List<Point> currentDepthList;

	private int count = 0;

	public HypOrbitPoints() {
		next = new HypPoint(0, 0);
		currentDepthList = new LinkedList<Point>();
		currentDepthList.add(next);

		currentDepthIter = currentDepthList.iterator();
		currentDepthIter.next();
		hyp = new HypPoint(next);
	}

	@Override
	public boolean hasNext() {
		// log.info("points next: " + next + ", hyp: " + hyp);
		hyp.clone(next);
		if (count > 200 && hyp.euclMag() > 0.99) {
			return currentDepthIter.hasNext();
		} else {
			return true;
		}
	}

	@Override
	public Point next() {

		current = new Point(next);
		if (currentDepthIter.hasNext()) {
			next = currentDepthIter.next();
		} else {
			currentDepthList = getListAtNextDepth();
			currentDepthIter = currentDepthList.iterator();
			next = currentDepthIter.next();
		}
		count++;
		return current;
	}

	/** Not used */
	@Override
	public void remove() {
	}

	private List<Point> getListAtNextDepth() {
		List<Point> list = new LinkedList<Point>();

		for (Point p : currentDepthList) {
			if (p.getX() >= 0) {
				// log.info("Adding point " + p + ", " + a + " = " + mobiusMap(p, a));
				list.add(mobiusMap(p, a));
			}
			if (p.getX() <= 0) {
				// log.info("Adding point " + p + ", " + am + " = " + mobiusMap(p, am));
				list.add(mobiusMap(p, am));
			}
			if (p.getY() >= 0) {
				// log.info("Adding point " + p + ", " + b + " = " + mobiusMap(p, b));
				list.add(mobiusMap(p, b));
			}
			if (p.getY() <= 0) {
				// log.info("Adding point " + p + ", " + bm + " = " + mobiusMap(p, bm));
				list.add(mobiusMap(p, bm));
			}
		}

		return list;
	}

	/**
	 * Simplified mobius map
	 * 
	 * <pre>
	 * T(z) =  z + a
	 *        z a' + 1
	 * </pre>
	 * 
	 * @return the point T(z)
	 */
	private Point mobiusMap(Point z, Point a) {
		double numX = z.getX() + a.getX();
		double numY = z.getY() + a.getY();
		double denX = 1 + z.getX() * a.getX() + z.getY() * a.getY();
		double denY = z.getY() * a.getX() - z.getX() * a.getY();

		return complexDivide(numX, numY, denX, denY);
	}

	private Point complexDivide(double x1, double y1, double x2, double y2) {
		double x = x1 * x2 + y1 * y2;
		double y = y1 * x2 - x1 * y2;
		double div = x2 * x2 + y2 * y2;
		x = x / div;
		y = y / div;

		return new Point(x, y);
	}
}
