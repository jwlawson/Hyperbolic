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
package uk.co.jwlawson.hyperbolic.client.group;

import uk.co.jwlawson.hyperbolic.client.geometry.isometries.IsomD;

/**
 * @author John
 * 
 */
public class IdealTorusOrbit extends Gen2OrbitPoints {

	public IdealTorusOrbit(double a, double b) {
		this(getIsomA(a, b), getIsomB(a, b));
	}

	private static IsomD getIsomA(double a, double b) {
		double t = -(a * a) / (b * b);

		double ax2 = -a - (1 / a) + a * t;
		double ay2 = a + a * t;

		double bx2 = -a - (a * t) + (1 / a);
		double by2 = a - (a * t);

		System.out.println("Found Isom A: " + ax2 + " + i" + ay2 + ", " + bx2 + " + i" + by2);

		return new IsomD(ax2, ay2, bx2, by2);
	}

	private static IsomD getIsomB(double a, double b) {
		double t = -(a * a) / (b * b);

		double ax1 = -b * t - (1 / (b * t)) + b;
		double ay1 = b + (b * t);

		double bx1 = -b * t + 1 / (b * t) - b;
		double by1 = b - (b * t);

		System.out.println("Found Isom B: " + ax1 + " + i" + ay1 + ", " + bx1 + " + i" + by1);

		return new IsomD(ax1, ay1, bx1, by1);
	}

	public IdealTorusOrbit(IsomD A, IsomD B) {
		super(A, B);
	}

}
