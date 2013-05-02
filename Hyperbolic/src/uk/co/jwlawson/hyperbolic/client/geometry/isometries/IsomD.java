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
package uk.co.jwlawson.hyperbolic.client.geometry.isometries;

import uk.co.jwlawson.hyperbolic.client.geometry.Point;

/**
 * @author John
 * 
 */
public class IsomD extends Isom {

	private Point a, b;

	public IsomD(Point a, Point b) {
		this.a = a;
		this.b = b;
	}

	public IsomD(double ax, double ay, double bx, double by) {
		a = new Point(ax, ay);
		b = new Point(bx, by);
	}

	@Override
	public Isom getInverse() {
		return new IsomD(a.getX(), -a.getY(), -b.getX(), -b.getY());
	}

	@Override
	public Point map(Point z) {
		double numX = (a.getX() * z.getX()) - (a.getY() * z.getY()) + b.getX();
		double numY = (a.getX() * z.getY()) + (a.getY() * z.getX()) + b.getY();

		double denX = (b.getX() * z.getX()) + (b.getY() * z.getY()) + a.getX();
		double denY = (b.getX() * z.getY()) - (b.getY() * z.getX()) - a.getY();

		Point result = complexDivide(numX, numY, denX, denY);
//		System.out.println("Mapping " + z + " to " + result);
		return result;
	}

	@Override
	public String toString() {
		return "IsomD a: " + a + " , b: " + b;
	}

}
