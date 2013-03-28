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
import java.util.LinkedList;
import java.util.List;

/**
 * @author John
 * 
 */
public abstract class OrbitIter implements Iterator<Point> {

	private List<OrbitPoint> masterList;

	private List<OrbitPoint> currentList;
	private Iterator<OrbitPoint> currentIter;

	private List<OrbitPoint> nextList;

	private int count = 0;

	private Point start = new Point(0, 0);
	private OrbitPoint current, next;

	private boolean calcMore = true;

	private double max = 1000;

	public OrbitIter() {
		masterList = new LinkedList<OrbitPoint>();
		currentList = new LinkedList<OrbitPoint>();

		next = new OrbitPoint(start);
		currentList.add(new OrbitPoint(next));

		currentIter = currentList.iterator();
		currentIter.next();

		nextList = new LinkedList<OrbitPoint>();

	}

	public void setStart(Point start) {
		this.start = start;
		next = new OrbitPoint(start);
	}

	public void setMax(double max) {
		this.max = max;
	}

	@Override
	public boolean hasNext() {
		return count < max;
	}

	@Override
	public Point next() {

		current = new OrbitPoint(next);

		if (calcMore) {
			addPoints(calculateNextLevel(current));
		}
		if (currentIter.hasNext()) {
			next = currentIter.next();
		} else {
			masterList.addAll(nextList);
			System.out.println("Adding " + nextList.size() + " points to the master");
			currentList = new LinkedList<OrbitPoint>(nextList);
			currentIter = currentList.iterator();
			nextList.clear();
			next = currentIter.next();
		}

		count++;
		if (count % 100 == 0) {
			System.out.println(count);
		}
		return current;
	}

	protected abstract List<OrbitPoint> calculateNextLevel(OrbitPoint current2);

	@Override
	public void remove() {
	}

	private void addPoints(List<OrbitPoint> list) {
		for (OrbitPoint q : list) {
			if (!(masterList.contains(q) || nextList.contains(q))) {
				nextList.add(q);
			}
		}
	}

}
