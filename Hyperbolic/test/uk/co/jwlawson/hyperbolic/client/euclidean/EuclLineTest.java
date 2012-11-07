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
package uk.co.jwlawson.hyperbolic.client.euclidean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.co.jwlawson.hyperbolic.client.geometry.Point;

/**
 * @author John
 * 
 */
public class EuclLineTest {

	/**
	 * Test method for
	 * {@link uk.co.jwlawson.hyperbolic.client.euclidean.EuclLine#contains(uk.co.jwlawson.hyperbolic.client.geometry.Point)}
	 * .
	 */
	@Test
	public void testContains() {
		EuclLine line = new EuclLine(0, 0, 10, 10);
		assertTrue("5,5", line.contains(new Point(5, 5)));
		assertTrue("11,11", line.contains(new Point(11, 11)));
		assertFalse("4.9,5", line.contains(new Point(4.9, 5)));
		assertFalse("3,3.01", line.contains(new Point(3, 3.01)));

	}

	@Test
	public void testVerticalLineContains() {
		EuclLine line;
		line = new EuclLine(3.2, -5, 3.2, 10);
		assertTrue(line.contains(new Point(3.2, 0)));
		assertFalse(line.contains(new Point(3.21, 0)));
	}

	@Test
	public void testStartAndEndContains() {
		EuclLine line = new EuclLine(0, 0, 10, 10);
		assertTrue("0,0", line.contains(new Point(0, 0)));
		assertTrue("10,10", line.contains(new Point(10, 10)));
	}

}
