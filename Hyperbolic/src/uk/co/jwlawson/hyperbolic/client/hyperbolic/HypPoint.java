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

import com.google.gwt.canvas.dom.client.Context2d;

import uk.co.jwlawson.hyperbolic.client.euclidean.EuclPoint;
import uk.co.jwlawson.hyperbolic.client.framework.Measurable;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;

import java.util.logging.Logger;

/**
 * @author John
 * 
 */
public class HypPoint extends Point implements Measurable {

	private static final Logger log = Logger.getLogger("HypPoint");
	private EuclPoint eucl;

	public static boolean isValid(Point p) {
		EuclPoint eucl = new EuclPoint(p);
		return eucl.magnitude() < 1;
	}

	public HypPoint(double x, double y) {
		super(x, y);
		eucl = new EuclPoint(this);
		checkValid();
	}

	public HypPoint(Point p) {
		super(p);
		eucl = new EuclPoint(this);
		checkValid();
	}

	private void checkValid() {
		if (eucl.magnitude() >= 1) {
			throw new IllegalArgumentException("Point must lie within the unit disc");
		}
	}

	@Override
	public double distance(Point p) {
		double x = (getX() - p.getX()) / (1 - (getX() * p.getX()));
		double y = (getY() - p.getY()) / (1 + (getY() * p.getY()));
		HypPoint point = new HypPoint(x, y);
		return point.magnitude();
	}

	@Override
	public double magnitude() {
		double mag = eucl.magnitude();
		return Math.log((1 + mag) / (1 - mag));
	}

	public double euclMag() {
		return eucl.magnitude();
	}

	@Override
	public void setX(double x) {
		eucl.setX(x);
		checkValid();
		super.setX(x);
	}

	@Override
	public void setY(double y) {
		eucl.setY(y);
		checkValid();
		super.setY(y);
	}

	@Override
	public void draw(Context2d context) {
		context.setFillStyle("#ff0000");
		context.beginPath();
		context.arc(getX(), getY(), 2.5, 0, 2 * Math.PI);
		context.closePath();
		context.fill();
	}

}
