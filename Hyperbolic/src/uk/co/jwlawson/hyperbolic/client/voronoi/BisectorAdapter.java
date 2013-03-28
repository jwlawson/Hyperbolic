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
package uk.co.jwlawson.hyperbolic.client.voronoi;

import uk.co.jwlawson.hyperbolic.client.geometry.Point;
import uk.co.jwlawson.voronoi.geometry.Bisector;
import uk.co.jwlawson.voronoi.geometry.Event;

/**
 * @author John
 * 
 */
public class BisectorAdapter {

	private Bisector bisector;

	public BisectorAdapter(Bisector bisector) {
		this.bisector = bisector;
	}

	public Point getStart() {
		Event evStart = bisector.getStart();

		return new Point(evStart.getX(), evStart.getY());
	}

	public Point getEnd() {
		Event evEnd = bisector.getEnd();
		return new Point(evEnd.getX(), evEnd.getY());
	}

}
