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

import com.google.gwt.canvas.dom.client.Context2d;

import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.geometry.euclidean.EuclPoint;

import java.util.logging.Logger;

/**
 * @author John
 * 
 */
public class CircleHypLine extends HypLine {

	private double centreX = Double.MAX_VALUE, centreY = Double.MAX_VALUE;
	private double radius = Double.MAX_VALUE;
	private double startAngle = Double.MAX_VALUE, endAngle = Double.MAX_VALUE;

	private CircleHypLine() {
	}

	private CircleHypLine(CircleHypLine line) {
		this.centreX = line.centreX;
		this.centreY = line.centreY;
		this.radius = line.radius;
		this.startAngle = line.startAngle;
		this.endAngle = line.endAngle;
	}

	protected double getStartAngle() {
		return startAngle;
	}

	protected double getEndAngle() {
		return endAngle;
	}

	@Override
	public void draw(Context2d context) {
		context.setFillStyle("#000000");
		context.beginPath();
		context.arc(centreX, centreY, radius, startAngle, endAngle, true);
		context.stroke();
	}

	@Override
	public boolean contains(Point p) {
		EuclPoint e = new EuclPoint(p);

		return doubleEquals(e.distance(new Point(centreX, centreY)), 0);
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof CircleHypLine) {
			CircleHypLine line = (CircleHypLine) obj;
			result = doubleEquals(line.centreX, centreX);
			result &= doubleEquals(line.centreY, centreY);
			result &= doubleEquals(line.radius, radius);
		}
		return result;
	}

	@Override
	public String toString() {
		return "Line centred at ( " + centreX + ", " + centreY + " ) of radius " + radius;
	}

	private boolean doubleEquals(double a, double b) {
		return Math.abs(a - b) < 1E-6;
	}

	public static class Builder {

		private static final Logger log = Logger.getLogger("CircleHypLine.Builder");
		private CircleHypLine line;
		private double scale;

		public Builder() {
			line = new CircleHypLine();
		}

		public Builder setScale(double scale) {
			this.scale = scale;
			return this;
		}

		public Builder setCentre(Point centre) {
			line.centreX = centre.getX();
			line.centreY = centre.getY();

			return this;
		}

		public Builder setCentre(double x, double y) {
			line.centreX = x;
			line.centreY = y;

			return this;
		}

		public Builder calcRadius() {
			setRadius(findRadius(line.centreX, line.centreY));

			return this;
		}

		private double findRadius(double centreX, double centreY) {
			return Math.sqrt(centreX * centreX + centreY * centreY - 1);
		}

		public Builder setRadius(double radius) {
			line.radius = radius;

			return this;
		}

		public Builder calcAngles() {
			Point centre = new Point(line.centreX, line.centreY);

			Point p1 = findIdealPoint1(line.centreX, line.centreY, line.radius);
			Point p2 = findIdealPoint2(line.centreX, line.centreY, line.radius);

			double a1 = findAngleBetweenPoints(p1, centre);
			double a2 = findAngleBetweenPoints(p2, centre);
			setAngles(a1, a2);

			return this;
		}

		private Point findIdealPoint1(double centreX, double centreY, double radius) {
			double x = centreX - (centreY * radius);
			double y = centreY + (centreX * radius);
			double sq = centreX * centreX + centreY * centreY;
			x = x / sq;
			y = y / sq;

			return new Point(x, y);
		}

		private Point findIdealPoint2(double centreX, double centreY, double radius) {
			double x = centreX + (centreY * radius);
			double y = centreY - (centreX * radius);
			double sq = centreX * centreX + centreY * centreY;
			x = x / sq;
			y = y / sq;

			return new Point(x, y);
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

		public Builder setAngles(double a1, double a2) {
			a1 = normalizeAngle(a1);
			a2 = normalizeAngle(a2);

			if ((max(a1, a2) - min(a1, a2)) < Math.PI) {
				line.startAngle = max(a1, a2);
				line.endAngle = min(a1, a2);
			} else {
				line.startAngle = min(a1, a2);
				line.endAngle = max(a1, a2);
			}

			return this;
		}

		private double normalizeAngle(double a) {
			if (a > 2 * Math.PI) {
				a = a % (2 * Math.PI);
			}

			return a;
		}

		private double max(double a, double b) {
			if (a < b) {
				return b;
			} else {
				return a;
			}
		}

		private double min(double a, double b) {
			if (a > b) {
				return b;
			} else {
				return a;
			}
		}

		public CircleHypLine build() {
			if (isNotComplete()) {
				throw new IllegalArgumentException("Ensure all attributes of the line are set");
			}
			scaleLine();
			return new CircleHypLine(line);
		}

		private void scaleLine() {
			line.centreX = line.centreX * scale;
			line.centreY = line.centreY * scale;
			line.radius = line.radius * scale;
		}

		private boolean isNotComplete() {
			return line.centreX == Double.MAX_VALUE || line.centreY == Double.MAX_VALUE
					|| line.radius == Double.MAX_VALUE || line.endAngle == Double.MAX_VALUE
					|| line.startAngle == Double.MAX_VALUE;
		}
	}
}
