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
import uk.co.jwlawson.hyperbolic.client.geometry.euclidean.EuclLine;
import uk.co.jwlawson.hyperbolic.client.geometry.euclidean.EuclLineFactory;
import uk.co.jwlawson.hyperbolic.client.geometry.euclidean.EuclPoint;

/**
 * @author John
 * 
 */
public class StraightHypLine extends HypLine {

	private EuclLine line;

	private StraightHypLine(EuclLine line) {
		this.line = line;
	}

	@Override
	public void draw(Context2d context) {
		line.draw(context);
	}

	@Override
	public boolean contains(Point p) {
		return line.contains(p);
	}

	public static class DiamBuilder {

		private EuclLineFactory factory;
		private double scale;

		private Point start;
		private Point end;

		public DiamBuilder() {
			factory = new EuclLineFactory(10000, 10000);
		}

		public DiamBuilder setPoint(Point p) {
			if ((new Point(0, 0)).equals(p)) {
				throw new IllegalArgumentException(
						"Point on diameter must be distinct from the origin");
			}
			EuclPoint euc = new EuclPoint(p);
			double t = 1 / euc.magnitude();

			start = new Point(t * p.getX(), t * p.getY());
			end = new Point(-t * p.getX(), -t * p.getY());

			return this;
		}

		public DiamBuilder setScale(double scale) {
			this.scale = scale;

			return this;
		}

		public DiamBuilder setPoints(Point start, Point end) {

			try {
				HypPoint p1 = new HypPoint(start);
				HypPoint p2 = new HypPoint(end);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Points must lie within the unit disk");
			}

			this.start = start;
			this.end = end;

			return this;
		}

		public StraightHypLine build() {
			if (start == null || end == null) {
				throw new IllegalArgumentException("Ensure all attributes of the line are set");
			}
			Point scaledStart = new Point(start);
			Point scaledEnd = new Point(end);
			scaledStart.scale(scale);
			scaledEnd.scale(scale);
			EuclLine line = (EuclLine) factory.getSegmentJoining(scaledStart, scaledEnd);
			return new StraightHypLine(line);
		}
	}

}
