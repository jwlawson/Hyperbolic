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
package uk.co.jwlawson.hyperbolic.client.euclidean;

import com.google.gwt.canvas.dom.client.Context2d;

import uk.co.jwlawson.hyperbolic.client.framework.Drawable;
import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.LineFactory;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;

/**
 * @author John
 * 
 */
public class EuclLine extends Line implements Drawable {

	private double startX, startY;
	private double endX, endY;

	private EuclLine(double startx, double starty, double endx, double endy) {
		this.startX = startx;
		this.startY = starty;
		this.endX = endx;
		this.endY = endy;
	}

	@Override
	public void draw(Context2d context) {
		context.setFillStyle("#000000");
		context.beginPath();
		context.moveTo(startX, startY);
		context.lineTo(endX, endY);
		context.closePath();
		context.stroke();
	}

	public static class Factory implements LineFactory {

		private int width;
		private int height;

		private double m, c;

		public Factory(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public Line getPerpendicularBisector(Point p1, Point p2) {

			calcM(p1, p2);

			Point half = getHalfWayPoint(p1, p2);

			m = -1 / m;
			calcCthroughPoint(half);

			return getLineFromMandC();
		}

		private Point getHalfWayPoint(Point p1, Point p2) {
			return new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
		}

		@Override
		public Line getGeodesicThrough(Point p1, Point p2) {

			if (p1.getY() - p2.getY() < 0.000001) {
				// Vertical line, so handle separately
				return new EuclLine(p1.getX(), -height, p1.getX(), height);
			}
			calcM(p1, p2);
			calcCthroughPoint(p1);

			return getLineFromMandC();
		}

		private Line getLineFromMandC() {
			Point start = getStart();
			Point end = getEnd();

			Line line = new EuclLine(start.getX(), start.getY(), end.getX(), end.getY());

			return line;
		}

		private void calcM(Point p1, Point p2) {
			m = (p1.getX() - p2.getX()) / (p1.getY() - p2.getY());
		}

		private void calcCthroughPoint(Point p1) {
			c = p1.getY() - m * p1.getX();
		}

		private Point getStart() {
			double x = -width;
			double y = getY(x);

			if (y <= height && y >= -height) {
				return new Point(x, y);
			}
			x = width;
			y = getY(x);
			if (y <= height && y >= -height) {
				return new Point(x, y);
			}
			y = height;
			x = getX(y);
			if (x <= width && x >= -width) {
				return new Point(x, y);
			}
			y = -height;
			x = getX(y);
			if (x <= width && x >= -width) {
				return new Point(x, y);
			}
			return new Point(0, 0);
		}

		private Point getEnd() {

			double y = -height;
			double x = getX(y);
			if (x <= width && x >= -width) {
				return new Point(x, y);
			}
			y = height;
			x = getX(y);
			if (x <= width && x >= -width) {
				return new Point(x, y);
			}
			x = width;
			y = getY(x);
			if (y <= height && y >= -height) {
				return new Point(x, y);
			}
			x = -width;
			y = getY(x);
			if (y <= height && y >= -height) {
				return new Point(x, y);
			}
			return new Point(0, 0);
		}

		private double getX(double y) {
			return (y - c) / m;
		}

		private double getY(double x) {
			return m * x + c;
		}
	}
}
