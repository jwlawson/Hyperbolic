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
package uk.co.jwlawson.hyperbolic.client.simplevoronoi;

import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.geometry.euclidean.EuclPoint;

/**
 * @author John
 * 
 */
public class GraphEdgeAdapter {

	private GraphEdge edge;

	public GraphEdgeAdapter(GraphEdge edge) {
		this.edge = edge;
		scaleToBoundary();
	}

	public Point getStart() {
		return new Point(edge.x1, edge.y1);
	}

	public Point getEnd() {
		return new Point(edge.x2, edge.y2);
	}

	/**
	 * Kinda hacky way to ensure that points lie inside the hyperbolic disk.
	 * Probably could be done in a much nicer way.
	 */
	private void scaleToBoundary() {

		EuclPoint p1 = new EuclPoint(edge.x1, edge.y1);
		EuclPoint p2 = new EuclPoint(edge.x2, edge.y2);

		if (p1.magnitude() >= 0.99999999 && p2.magnitude() >= 0.99999999) {
			// TODO Edge outside disk.
			edge.x1 = Double.NaN;
			edge.y1 = Double.NaN;
			edge.x2 = Double.NaN;
			edge.y2 = Double.NaN;
			return;
		}

		double m = (edge.y1 - edge.y2) / (edge.x2 - edge.x1);
		double c = edge.y1 - m * edge.x1;

		double pm = 0.99999999 + m * m - c * c;
		if (pm < 0) {
//			System.out.println("NaN! m = " + m + " c = " + c + " pm^2 = "
//					+ (0.99999999 + m * m - c * c));
		}
		pm = Math.sqrt(pm);

		double x1 = (-m * c + pm) / (1 + m * m);
		double y1 = m * x1 + c;
		if (m == Double.NEGATIVE_INFINITY || m == Double.POSITIVE_INFINITY) {
			// x vaules are the same
			x1 = edge.x1;
			y1 = Math.sqrt(0.99999999 - x1 * x1);
		}
		Point ideal1 = new Point(x1, y1);

		double x2 = (-m * c - pm) / (1 + m * m);
		double y2 = m * x2 + c;
		if (m == Double.NEGATIVE_INFINITY || m == Double.POSITIVE_INFINITY) {
			// x vaules are the same
			x2 = edge.x1;
			y2 = -Math.sqrt(0.99999999 - x1 * x1);
		}
		Point ideal2 = new Point(x2, y2);

		if (p1.magnitude() >= 1) {
			if (p1.distance(ideal1) > p1.distance(ideal2)) {
				edge.x1 = x2;
				edge.y1 = y2;
//				System.out.println("Replacing " + p1 + "with " + ideal2 + " not " + ideal1
//						+ " where p2 = " + p2);
			} else {
				edge.x1 = x1;
				edge.y1 = y1;
//				System.out.println("Replacing " + p1 + "with " + ideal1 + " not " + ideal2
//						+ " where p2 = " + p2);
			}
		}
		if (p2.magnitude() >= 1) {
			if (p2.distance(ideal1) < p2.distance(ideal2)) {
				edge.x2 = x1;
				edge.y2 = y1;
//				System.out.println("Replacing p2=" + p2 + "with " + ideal1 + " not " + ideal2
//						+ " where p1 = " + p1);
			} else {
				edge.x2 = x2;
				edge.y2 = y2;
//				System.out.println("Replacing p2=" + p2 + "with " + ideal2 + " not " + ideal1
//						+ " where p1 = " + p1);
			}
		}

	}
}
