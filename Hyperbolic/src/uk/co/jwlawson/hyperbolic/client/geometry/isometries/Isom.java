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
 * @author John Lawson
 * 
 */
public abstract class Isom {

	public abstract Point map(Point p);

	public abstract Isom getInverse();

	public static Point complexDivide(double x1, double y1, double x2, double y2) {
		double x = (x1 * x2) + (y1 * y2);
		double y = (y1 * x2) - (x1 * y2);
		double div = (x2 * x2) + (y2 * y2);
		x = x / div;
		y = y / div;

		return new Point(x, y);
	}
}
