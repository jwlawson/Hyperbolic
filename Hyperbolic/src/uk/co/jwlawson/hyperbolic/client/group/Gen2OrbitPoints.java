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
import uk.co.jwlawson.hyperbolic.client.hyperbolic.HypPoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author John
 * 
 */
public class Gen2OrbitPoints implements Iterator<Point> {

	private IsomD A, B, Am, Bm;

	private List<Point> masterList;

	private List<Point> currentList;
	private List<Map> currentMapList;
	private Iterator<Point> currentIter;

	private List<Point> nextList;
	private List<Map> nextMapList;

	private int count = 0;
	private int index = 0;

	private Point current, next;
	private HypPoint hyp;

	private boolean calcMore = true;

	public Gen2OrbitPoints(IsomD A, IsomD B) {
		this.A = A;
		this.B = B;
		Am = A.getInverse();
		Bm = B.getInverse();

		masterList = new ArrayList<Point>();
		currentList = new ArrayList<Point>();
		currentMapList = new ArrayList<Gen2OrbitPoints.Map>();
		currentMapList.add(Map.Null);

		next = new HypPoint(0, 0);
		currentList.add(next);

		currentIter = currentList.iterator();
		currentIter.next();

		nextList = new ArrayList<Point>();
		nextMapList = new ArrayList<Gen2OrbitPoints.Map>();

		hyp = new HypPoint(next);
	}

	@Override
	public boolean hasNext() {
		hyp.clone(next);
		if (count > 400 || hyp.euclMag() > 0.999) {
			calcMore = false;
			return currentIter.hasNext();
		} else {
			return true;
		}
	}

	@Override
	public Point next() {

		current = new Point(next);

		if (calcMore) {
			calculateNextLevel(current);
		}
		if (currentIter.hasNext()) {
			next = currentIter.next();
			index++;
		} else {
			masterList.addAll(nextList);
			System.out.println("Adding " + nextList.size() + " points to the master");
			currentList = new ArrayList<Point>(nextList);
			currentMapList = new ArrayList<Map>(nextMapList);
			currentIter = currentList.iterator();
			nextList.clear();
			nextMapList.clear();
			next = currentIter.next();
			index = 0;
		}

		count++;
		System.out.println(count);
		return current;
	}

	private void calculateNextLevel(Point p) {

		Map map = currentMapList.get(index);

		Point q;
		if (!map.equals(Map.A)) {
			q = A.map(p);
			if (!(masterList.contains(q) || nextList.contains(q))) {
				nextList.add(A.map(p));
				nextMapList.add(Map.A);
			}
		}
		if (!map.equals(Map.B)) {
			q = B.map(p);
			if (!(masterList.contains(q) || nextList.contains(q))) {
				nextList.add(B.map(p));
				nextMapList.add(Map.B);
			}
		}

		if (!map.equals(Map.Bm)) {
			q = Bm.map(p);
			if (!(masterList.contains(q) || nextList.contains(q))) {
				nextList.add(Bm.map(p));
				nextMapList.add(Map.Bm);
			}
		}

		if (!map.equals(Map.Am)) {
			q = Am.map(p);
			if (!(masterList.contains(q) || nextList.contains(q))) {
				nextList.add(Am.map(p));
				nextMapList.add(Map.Am);
			}
		}

	}

	@Override
	public void remove() {
	}

	private enum Map {
		A, B, Am, Bm, Null;
	}
}
