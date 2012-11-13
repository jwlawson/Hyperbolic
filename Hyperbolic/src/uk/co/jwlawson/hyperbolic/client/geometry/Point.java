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
package uk.co.jwlawson.hyperbolic.client.geometry;

import com.google.gwt.canvas.dom.client.Context2d;

import uk.co.jwlawson.hyperbolic.client.framework.Drawable;

/**
 * @author John Lawson
 * 
 */
public class Point implements Drawable {

	private static final double ERROR = Math.pow(10, -3);

	private double x;
	private double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	public boolean equals(Point p) {
		boolean result = (p.x - x < ERROR);
		result &= (p.y - y < ERROR);
		return result;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		return "( " + x + " , " + y + " )";
	}

	@Override
	public void draw(Context2d context) {
	}
}
