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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.jwlawson.hyperbolic.client.euclidean.EuclPoint;

/**
 * @author John
 * 
 */
public class TorusOrbitPoints implements Iterator<EuclPoint> {

	private static final int SIZE = 100;

	private int width, height;
	private EuclPoint current, next;

	private int depth;
	private Iterator<EuclPoint> currentDepthIter;

	public TorusOrbitPoints(int width, int height) {
		this.width = width;
		this.height = height;

		depth = 1;
		currentDepthIter = getIteratorAtDepth(depth);

		next = new EuclPoint(0, 0);
	}

	@Override
	public boolean hasNext() {
		return next.getX() < width && next.getY() < height;
	}

	@Override
	public EuclPoint next() {
		current = next;

		if (currentDepthIter.hasNext()) {
			next = currentDepthIter.next();
		} else {
			depth++;
			currentDepthIter = getIteratorAtDepth(depth);
			next = currentDepthIter.next();
		}
		return current;
	}

	/** Not used */
	@Override
	public void remove() {
	}

	private Iterator<EuclPoint> getIteratorAtDepth(int depth) {
		List<EuclPoint> list = getListAtDepth(depth);
		return list.iterator();
	}

	private List<EuclPoint> getListAtDepth(int depth) {
		ArrayList<EuclPoint> result = new ArrayList<EuclPoint>();

		for (int x = depth; x >= 0; x--) {
			int y = depth - x;
			result.add(new EuclPoint(x * SIZE, y * SIZE));
			if (x != 0) result.add(new EuclPoint(-x * SIZE, y * SIZE));
			if (y != 0) result.add(new EuclPoint(x * SIZE, -y * SIZE));
			if (x != 0 && y != 0) result.add(new EuclPoint(-x * SIZE, -y * SIZE));
		}

		return result;
	}

}
