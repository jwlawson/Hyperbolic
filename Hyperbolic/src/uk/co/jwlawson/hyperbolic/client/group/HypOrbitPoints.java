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

import java.util.Iterator;

/**
 * @author John
 * 
 */
public class HypOrbitPoints implements Iterator<Point> {

	private double[] xes = new double[] { 0, 0.5, 0, -0.5, 0, 0.25, 0.8, 0.5 };
	private double[] yes = new double[] { 0, 0, 0.5, 0, -0.5, 0.25, -0.45, 0.5 };

	private int count = -1;

	@Override
	public boolean hasNext() {

		return count < xes.length - 1;
	}

	@Override
	public Point next() {
		count++;
		return new Point(xes[count], yes[count]);
	}

	@Override
	public void remove() {
	}

}
