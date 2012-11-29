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

/**
 * @author John
 * 
 */
public class IdealTorusOrbit extends Gen2OrbitPoints {

	public IdealTorusOrbit(double a, double b) {
		this(getIsomA(a, b), getIsomB(a, b));
	}

	private static IsomD getIsomB(double a, double b) {
		double ax2 = 1 / b + b + (b * b * b) / (a * a);
		double ay2 = b + 1 / b;

		double bx2 = -1 / b - b - (b * b * b) / (a * a);
		double by2 = b - 1 / b;

		IsomD B = new IsomD(ax2, ay2, bx2, by2);
		return B;
	}

	private static IsomD getIsomA(double a, double b) {
		double ax1 = a + b * b / a + a / (b * b);
		double ay1 = a * (1 / (b * b) - 1);

		double bx1 = a + b * b / a - a / (b * b);
		double by1 = -a - a / (b * b);

		IsomD A = new IsomD(ax1, ay1, bx1, by1);
		return A;
	}

	public IdealTorusOrbit(IsomD A, IsomD B) {
		super(A, B);
	}

}
