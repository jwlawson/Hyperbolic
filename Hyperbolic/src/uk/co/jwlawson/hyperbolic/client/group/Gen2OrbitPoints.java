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

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author John
 * 
 */
public class Gen2OrbitPoints implements Iterator<Point> {

	private IsomD A, B, Am, Bm;

	private LinkedList<Point> masterList;

	private LinkedList<Point> currentList;
	private LinkedList<Map> currentMapList;
	private Iterator<Point> currentIter;

	private LinkedList<Point> nextList;
	private LinkedList<Map> nextMapList;

	private int count = 0;
	private int index = 0;

	private Point current, next;
	private HypPoint hyp;

	public Gen2OrbitPoints(IsomD A, IsomD B) {
		this.A = A;
		this.B = B;
		Am = A.getInverse();
		Bm = B.getInverse();

		masterList = new LinkedList<Point>();
		currentList = new LinkedList<Point>();
		currentMapList = new LinkedList<Gen2OrbitPoints.Map>();
		currentMapList.add(Map.Null);

		next = new HypPoint(0, 0);
		currentList.add(next);

		currentIter = currentList.iterator();
		currentIter.next();

		nextList = new LinkedList<Point>();
		nextMapList = new LinkedList<Gen2OrbitPoints.Map>();

		hyp = new HypPoint(next);
	}

	@Override
	public boolean hasNext() {
		hyp.clone(next);
		if (count > 200 && hyp.euclMag() > 0.99) {
			return currentIter.hasNext();
		} else {
			return true;
		}
	}

	@Override
	public Point next() {

		current = new Point(next);

		calculateNextLevel(current);

		if (currentIter.hasNext()) {
			next = currentIter.next();
			index++;
		} else {
			masterList.addAll(nextList);
			currentList = new LinkedList<Point>(nextList);
			currentMapList = new LinkedList<Map>(nextMapList);
			currentIter = currentList.iterator();
			nextList.clear();
			nextMapList.clear();
			next = currentIter.next();
			index = 0;
		}

		count++;
		return current;
	}

	private void calculateNextLevel(Point p) {
		switch (currentMapList.get(index)) {
		case A:
			nextList.add(A.map(p));
			nextMapList.add(Map.A);

			nextList.add(B.map(p));
			nextMapList.add(Map.B);

			nextList.add(Bm.map(p));
			nextMapList.add(Map.Bm);

			break;

		case B:
			nextList.add(A.map(p));
			nextMapList.add(Map.A);

			nextList.add(B.map(p));
			nextMapList.add(Map.B);

			nextList.add(Bm.map(p));
			nextMapList.add(Map.Bm);
			break;

		case Am:
			nextList.add(Am.map(p));
			nextMapList.add(Map.Am);

			nextList.add(B.map(p));
			nextMapList.add(Map.B);

			nextList.add(Bm.map(p));
			nextMapList.add(Map.Bm);
			break;

		case Bm:
			nextList.add(A.map(p));
			nextMapList.add(Map.A);

			nextList.add(B.map(p));
			nextMapList.add(Map.B);

			nextList.add(Am.map(p));
			nextMapList.add(Map.Am);
			break;

		case Null:
			nextList.add(A.map(p));
			nextMapList.add(Map.A);

			nextList.add(B.map(p));
			nextMapList.add(Map.B);

			nextList.add(Bm.map(p));
			nextMapList.add(Map.Bm);

			nextList.add(Am.map(p));
			nextMapList.add(Map.Am);

			break;
		}
	}

	@Override
	public void remove() {
	}

	private enum Map {
		A, B, Am, Bm, Null;
	}
}
