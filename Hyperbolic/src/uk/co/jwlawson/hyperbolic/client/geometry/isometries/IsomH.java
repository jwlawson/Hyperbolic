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
 * @author Administrator
 * 
 */
public class IsomH extends Isom {

	private double a, b, c, d;

	public IsomH(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public Point map(Point p) {
		double numX = p.getX() * a + b;
		double numY = p.getY() * a;
		double denX = p.getX() * c + d;
		double denY = p.getY() * c;

		return complexDivide(numX, numY, denX, denY);
	}

	@Override
	public Isom getInverse() {
		return new IsomH(d, -b, -c, a);
	}

	public IsomD getIsomD() {
		return new IsomD(a + d, b - c, a - d, -b - c);
	}

}
