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
import uk.co.jwlawson.hyperbolic.client.geometry.isometries.Isom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author John
 * 
 */
public class Gen2OrbitPoints extends OrbitIter {

	public Gen2OrbitPoints(Isom A, Isom B) {
		Map.setIsoms(A, B);
	}

	@Override
	protected List<OrbitPoint> calculateNextLevel(OrbitPoint p) {
		Map map = (Map) p.getE();
		ArrayList<OrbitPoint> nextList = new ArrayList<OrbitPoint>();

		OrbitPoint q;
		if (map == null || !map.equals(Map.Am)) {
			q = Map.A.map(p);
			nextList.add(q);
		}
		if (map == null || !map.equals(Map.Bm)) {
			q = Map.B.map(p);
			nextList.add(q);
		}

		if (map == null || !map.equals(Map.B)) {
			q = Map.Bm.map(p);
			nextList.add(q);
		}

		if (map == null || !map.equals(Map.A)) {
			q = Map.Am.map(p);
			nextList.add(q);
		}
		return nextList;
	}

	@Override
	public void remove() {
	}

	private enum Map implements IsomEnum {
		A, B, Am, Bm;

		private Isom isom;

		private static void setIsoms(Isom a, Isom b) {
			A.isom = a;
			Am.isom = a.getInverse();
			B.isom = b;
			Bm.isom = b.getInverse();
		}

		@Override
		public OrbitPoint map(Point p) {
			OrbitPoint result = new OrbitPoint(isom.map(p));
			result.setE(this);
			return result;
		}
	}
}
