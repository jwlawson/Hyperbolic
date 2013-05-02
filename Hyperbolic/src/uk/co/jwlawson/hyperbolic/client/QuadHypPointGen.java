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
package uk.co.jwlawson.hyperbolic.client;

import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.hyperbolic.client.group.IdealTorusOrbit;

/**
 * @author John
 * 
 */
public class QuadHypPointGen extends PointGen {

	private double y = 1;

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public void start() {
		setInitialPoint(new Point((y - 1) / (y + 1), 0));
		setOrbitIter(new IdealTorusOrbit(y));
		super.start();
	}

}
