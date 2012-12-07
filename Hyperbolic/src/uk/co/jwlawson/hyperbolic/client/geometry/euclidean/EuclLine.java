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
package uk.co.jwlawson.hyperbolic.client.geometry.euclidean;

import com.google.gwt.canvas.dom.client.Context2d;

import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;

/**
 * @author John
 * 
 */
public class EuclLine extends Line {

	private double startX, startY;
	private double endX, endY;

	protected EuclLine(double startx, double starty, double endx, double endy) {
		this.startX = startx;
		this.startY = starty;
		this.endX = endx;
		this.endY = endy;
	}

	@Override
	public void draw(Context2d context) {
		context.setLineWidth(0.5);
		context.setStrokeStyle("rgba(0,0,0,0.8)");
		context.beginPath();
		context.moveTo(startX, startY);
		context.lineTo(endX, endY);
		context.closePath();
		context.stroke();
	}

	@Override
	public boolean contains(Point p) {
		if (doubleEquals(startX, endX)) {
			// Vertical
			return (doubleEquals(startX, p.getX()));
		}
		double m = (startY - endY) / (startX - endX);
		double c = (startY - m * startX);

		return (doubleEquals(p.getY(), m * p.getX() + c));
	}

	private boolean doubleEquals(double a, double b) {
		return Math.abs(a - b) < 0.00001;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EuclLine) {
			EuclLine line = (EuclLine) obj;
			return line.contains(new Point(startX, startY)) && line.contains(new Point(endX, endY));

		} else {
			return false;
		}
	}
}
