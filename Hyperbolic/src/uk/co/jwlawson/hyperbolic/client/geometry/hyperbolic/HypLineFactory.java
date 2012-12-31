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
package uk.co.jwlawson.hyperbolic.client.geometry.hyperbolic;

import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.LineFactory;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.geometry.hyperbolic.HypLine.Builder;

import java.util.logging.Logger;

/**
 * @author John
 * 
 */
public class HypLineFactory implements LineFactory {

	private static final Logger log = Logger.getLogger("HypLineFactory");
	private HypLine.Builder builder;

	public HypLineFactory(double scale) {
		builder = new Builder();
		builder.setScale(scale);
	}

	@Override
	public Line getPerpendicularBisector(Point p1, Point p2) {

		if (p1.equals(p2)) {
			throw new IllegalArgumentException("Points cannot be equal: " + p1 + " , " + p2);
		}

		Point centre = findPerpBisectorCentre(p1, p2);

		builder.setCentre(centre);
		builder.calcRadius();
		builder.calcAngles();

		return builder.build();
	}

	private Point findPerpBisectorCentre(Point p1, Point p2) {
		return findPerpBisectorCentre(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	private Point findPerpBisectorCentre(double x1, double y1, double x2, double y2) {
		double a = -x1 + (x1 * x2 * x2) + (x1 * y2 * y2) + x2 - (x1 * x1 * x2) - (x2 * y1 * y1);
		double b = -y1 + (x2 * x2 * y1) + (y1 * y2 * y2) + y2 - (x1 * x1 * y2) - (y1 * y1 * y2);
		double div = (x1 * x1) + (y1 * y1) - (x2 * x2) - (y2 * y2);

		a = a / div;
		b = b / div;

		return new Point(a, b);
	}

	@Override
	public Line getGeodesicThrough(Point p1, Point p2) {
		Point centre = findCentre(p1, p2);

		builder.setCentre(centre);
		builder.calcRadius();
		builder.calcAngles();

		return builder.build();
	}

	private boolean isDiameter(Point p1, Point p2) {
		return p1.getX() * p2.getY() == p1.getY() * p2.getX();
	}

	private Point findCentre(Point p1, Point p2) {
		return findCentre(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	private Point findCentre(double x1, double y1, double x2, double y2) {
		double y = findCentreY(x1, y1, x2, y2);
		double x = findCentreX(x1, y1, y);

		return new Point(x, y);
	}

	private double findCentreY(double x1, double y1, double x2, double y2) {
		double b = x2 * (x1 * x1 + y1 * y1 + 1) - x1 * (x2 * x2 + y2 * y2 + 1);
		b = b / (2 * (x2 * y1 - x1 * y2));

		return b;
	}

	private double findCentreX(double x1, double y1, double centreY) {
		double a = x1 * x1 + y1 * y1 + 1 - 2 * y1 * centreY;
		a = a / (2 * x1);
		return a;
	}

	@Override
	public Line getSegmentJoining(Point p1, Point p2) {
		Point centre = findCentre(p1, p2);
		if (isDiameter(p1, p2)) {
			System.out.println("Centre diam: " + centre);
		}

		builder.setCentre(centre);
		builder.calcRadius();

		double a1 = findAngleBetweenPoints(p1, centre);
		double a2 = findAngleBetweenPoints(p2, centre);
		builder.setAngles(a1, a2);

		return builder.build();
	}

	private double findAngleBetweenPoints(Point p1, Point p2) {
		double dx = p1.getX() - p2.getX();
		double dy = p1.getY() - p2.getY();

		double theta = Math.atan2(dy, dx);
		if (theta < 0) {
			theta += Math.PI * 2;
		}
		return theta;
	}
}
