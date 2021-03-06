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
public class IdealTorusOrbit extends BoundedHypGen2Orbit {

	public IdealTorusOrbit(double y) {
		super(getIsomA(y), getIsomB(y));
	}

	private static IsomD getIsomA(double y) {
		double t = -y * y;
		double a = Math.sqrt(1 / (1 - t));

		IsomD result = IsomH.getIsomD(a, -a * t, a, (1 - (a * a * t)) / a);

		System.out.println("IsomD A found: " + result);

		return result;
	}

	private static IsomD getIsomB(double y) {
		double t = -y * y;
		double b = Math.sqrt(1 / (t * (t - 1)));

		IsomD result = IsomH.getIsomD(b * t, -b * t, b, (1 - b * b * t) / (b * t));
		System.out.println("IsomD B found: " + result);

		return result;
	}
}
