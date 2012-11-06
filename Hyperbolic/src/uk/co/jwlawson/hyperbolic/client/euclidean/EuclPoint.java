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
import uk.co.jwlawson.hyperbolic.client.framework.Measurable;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;

/**
 * @author John
 * 
 */
public class EuclPoint extends Point implements Drawable, Measurable {

	public EuclPoint(double x, double y) {
		super(x, y);
	}

	public EuclPoint(Point p) {
		super(p.getX(), p.getY());
	}

	private double distance(double x, double y) {
		double x1 = getX();
		double y1 = getY();
		return Math.sqrt(Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2));
	}

	@Override
	public double distance(Point point) {
		return distance(point.getX(), point.getY());
	}

	@Override
	public double magnitude() {
		return distance(0, 0);
	}

	@Override
	public void draw(Context2d context) {
		context.setFillStyle("#000000");
		context.beginPath();
		context.arc(getX(), getY(), 2, 0, 2 * Math.PI);
		context.closePath();
		context.fill();
	}

}
