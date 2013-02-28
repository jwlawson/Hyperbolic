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

import java.util.ArrayList;
import java.util.List;

/**
 * @author John
 * 
 */
public class TorusOrbit extends OrbitIter {

	public TorusOrbit(float x, float y) {

		Map.A.setX(x);
		Map.A.setY(y);
		Map.B.setX(1);
		Map.B.setY(0);
	}

	@Override
	protected List<OrbitPoint> calculateNextLevel(OrbitPoint p) {
		Map map = (Map) p.getE();
		List<OrbitPoint> list = new ArrayList<OrbitPoint>(4);

		OrbitPoint q;
		if (map == null || !map.equals(Map.Am)) {
			q = Map.A.map(p);
			list.add(q);
		}
		if (map == null || !map.equals(Map.Bm)) {
			q = Map.B.map(p);
			list.add(q);
		}

		if (map == null || !map.equals(Map.B)) {
			q = Map.Bm.map(p);
			list.add(q);
		}

		if (map == null || !map.equals(Map.A)) {
			q = Map.Am.map(p);
			list.add(q);
		}

		return list;
	}

	private enum Map implements IsomEnum {

		A, B, Am, Bm;

		private double x, y;

		public void setX(double x) {
			this.x = x;

			if (this == A) {
				Am.setX(-x);
			}
			if (this == B) {
				Bm.setX(-x);
			}
		}

		public void setY(double y) {
			this.y = y;

			if (this == A) {
				Am.setY(-y);
			}
			if (this == B) {
				Bm.setY(-y);
			}
		}

		@Override
		public OrbitPoint map(Point p) {
			double newX = p.getX() + x;
			double newY = p.getY() + y;
			OrbitPoint result = new OrbitPoint(newX, newY, this);
			return result;
		}
	}
}
