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
package uk.co.jwlawson.hyperbolic.client.hyperbolic;

import uk.co.jwlawson.hyperbolic.client.euclidean.EuclPoint;
import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.LineFactory;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.hyperbolic.HypLine.Builder;

import java.util.logging.Logger;

/**
 * @author John
 * 
 */
public class HypLineFactory implements LineFactory {

	private static final Logger log = Logger.getLogger("HypLineFactory");
	private static final double E_HALF = Math.sqrt(Math.E);
	private HypLine.Builder builder;

	public HypLineFactory(double scale) {
		builder = new Builder();
		builder.setScale(scale);
	}

	@Override
	public Line getPerpendicularBisector(Point p1, Point p2) {

		Point mapped = findMappedPoint(p1, p2);

		Point centreMapped = findCentreForPerpBisectorWithOrigin(mapped);

		Point centre = inverseCentre(p1, centreMapped);
		log.info("Building line with centre " + centre);

		builder.setCentre(centre);
		builder.calcRadius();
		builder.calcAngles();

		return builder.build();
	}

	/** Mobius map that takes p1 to zero. Returns the image of p2 */
	private Point findMappedPoint(Point p1, Point p2) {
		double numX = p2.getX() - p1.getX();
		double numY = p2.getY() - p1.getY();
		double denX = 1 - p1.getX() * p2.getX() - p1.getY() * p2.getY();
		double denY = p2.getX() * p1.getY() - p1.getX() * p2.getY();
		Point mapped = complexDivide(numX, numY, denX, denY);
		log.info("Mapped point: " + mapped);
		return mapped;
	}

	/** Inverse map taking 0 to p1. Returns the image of centreMapped */
	private Point inverseCentre(Point p1, Point centreMapped) {
		double numX = centreMapped.getX() + p1.getX();
		double numY = centreMapped.getY() + p1.getY();
		double denX = 1 + centreMapped.getX() * p1.getX() + centreMapped.getY() * p1.getY();
		double denY = p1.getX() * centreMapped.getY() - p1.getY() * centreMapped.getX();
		Point centre = complexDivide(numX, numY, denX, denY);
		log.info("Centre: " + centre);
		return centre;
	}

	private Point findCentreForPerpBisectorWithOrigin(Point p) {
		EuclPoint eucl = new EuclPoint(p);
		double rho = eucl.magnitude();
		double d = (rho + 1) / (rho - 1);
		double dz = (d * E_HALF - 1) / (d * E_HALF + 1);
		double modifier = (dz * dz + 1) / (2 * dz * rho);

		double x = eucl.getX() * modifier;
		double y = eucl.getY() * modifier;
		log.info("Centre found at " + x + ", " + y + " with d = " + d);

		return new Point(x, y);
	}

	private Point complexDivide(double x1, double y1, double x2, double y2) {
		double x = x1 * x2 + y1 * y2;
		double y = y1 * x2 - x1 * y2;
		double div = x2 * x2 + y2 * y2;
		x = x / div;
		y = y / div;

		return new Point(x, y);
	}

	@Override
	public Line getGeodesicThrough(Point p1, Point p2) {
		Point centre = findCentre(p1, p2);

		builder.setCentre(centre);
		builder.calcRadius();
		builder.calcAngles();

		return builder.build();
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

}
