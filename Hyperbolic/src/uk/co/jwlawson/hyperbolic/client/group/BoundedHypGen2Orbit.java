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

import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.geometry.hyperbolic.HypPoint;
import uk.co.jwlawson.hyperbolic.client.geometry.isometries.Isom;

/**
 * @author John
 * 
 */
public class BoundedHypGen2Orbit extends Gen2OrbitPoints {

	public BoundedHypGen2Orbit(Isom A, Isom B) {
		super(A, B);
	}

	@Override
	protected boolean calculateMore(Point p) {
		HypPoint hp = new HypPoint(p);
		return super.calculateMore(p) && (hp.euclMag() < 0.997);
	}

}
