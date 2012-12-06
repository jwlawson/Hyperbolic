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
import uk.co.jwlawson.hyperbolic.client.geometry.isometries.IsomH;

/**
 * @author John
 * 
 */
public class IdealTorusOrbit extends Gen2OrbitPoints {

	public IdealTorusOrbit(double a, double b) {
		super(getIsomA(a, b), getIsomB(a, b));
	}

	private static IsomD getIsomA(double a, double t) {

		IsomD result = IsomH.getIsomD(a, -a * t, a, (1 - (a * a * t)) / a);

		System.out.println("IsomD A found: " + result);

		return result;
	}

	private static IsomD getIsomB(double a, double t) {
		double a4 = Math.pow(a, 4);
		double a2 = Math.pow(a, 2);
		double t3 = Math.pow(t, 3);
		double t2 = Math.pow(t, 2);
		double temp = a4 * t + a4 - 3 * a2 - a2
				* Math.sqrt(13 - 10 * a2 * t - 10 * a2 + 5 * a4 * t2 + 5 * a4 - 2 * a4 * t);
		System.out.println(13 - 10 * a2 * t - 10 * a2 + 5 * a4 * t2 + 5 * a4 - 2 * a4 * t);
		System.out.println(temp);
		temp = temp / (4 * (t + a4 * t + a4 * t3 - a2 * t - a2 * t2 - a4 * t2));
		System.out.println("den: " + 4 * (t + a4 * t + a4 * t3 - a2 * t - a2 * t2 - a4 * t2));
		System.out.println(temp);
		double b = Math.sqrt(temp);
		System.out.println(b);
		System.out.println(b * b);

		IsomD result = IsomH.getIsomD(b * t, -b * t, b, (1 - b * b * t) / (b * t));
		System.out.println("IsomD B found: " + result);

		return result;
	}
}
