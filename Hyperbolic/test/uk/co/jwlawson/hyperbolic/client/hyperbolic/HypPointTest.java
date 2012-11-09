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
package uk.co.jwlawson.hyperbolic.client.hyperbolic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.jwlawson.hyperbolic.client.geometry.Point;

/**
 * @author John
 * 
 */
public class HypPointTest {

	private static final double ERROR = 0.000001;

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidBoundaryConstructor() {
		HypPoint p = new HypPoint(0, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConstructor() {
		HypPoint p = new HypPoint(2, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidPointConstructor() {
		Point p = new Point(1, -1);
		HypPoint p1 = new HypPoint(p);
	}

	/**
	 * Test method for
	 * {@link uk.co.jwlawson.hyperbolic.client.hyperbolic.HypPoint#distance(uk.co.jwlawson.hyperbolic.client.geometry.Point)}
	 * .
	 */
	@Test
	public void testZeroDistance() {
		HypPoint p1 = new HypPoint(0.26, 0.75);
		HypPoint p2 = new HypPoint(p1);

		assertEquals(0, p1.distance(p2), ERROR);
	}

	@Test
	public void testSymmetricDistance() {
		HypPoint p1 = new HypPoint(0.379, 0.172);
		HypPoint p2 = new HypPoint(0.754, -0.412736);

		assertEquals(p1.distance(p2), p2.distance(p1), ERROR);
	}

	@Test
	public void testHorizontalDistance() {
		HypPoint p1 = new HypPoint(0.25, 0);
		HypPoint p2 = new HypPoint(0.75, 0);

		assertEquals(1.43508452529, p1.distance(p2), ERROR);
	}

	@Test
	public void testVerticalDistance() {
		HypPoint p1 = new HypPoint(0, 0.4);
		HypPoint p2 = new HypPoint(0, 0.8);

		assertEquals(0.62570589976, p1.distance(p2), ERROR);
	}

	@Test
	public void testDistance() {
		HypPoint p1 = new HypPoint(0.125, 0.4);
		HypPoint p2 = new HypPoint(0.875, 0.2);

		assertEquals(2.60391837123, p1.distance(p2), ERROR);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidSetterX() {
		HypPoint p = new HypPoint(0, 0);
		p.setX(2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidSetterY() {
		HypPoint p = new HypPoint(0, 0);
		p.setY(2);
	}

	/**
	 * Test method for
	 * {@link uk.co.jwlawson.hyperbolic.client.hyperbolic.HypPoint#magnitude()}.
	 */
	@Test
	public void testZeroMagnitude() {
		HypPoint p = new HypPoint(0, 0);
		assertEquals(p.magnitude(), 0, ERROR);
	}

	@Test
	public void testXMagnitude() {
		HypPoint p = new HypPoint(0.7, 0);
		assertEquals(1.7346010553, p.magnitude(), ERROR);
	}

	@Test
	public void testYMagnitude() {
		HypPoint p = new HypPoint(0, 0.9999);
		assertEquals(9.9034375512, p.magnitude(), ERROR);
	}

	@Test
	public void testMagnitude() {
		HypPoint p = new HypPoint(0.3, 0.4);
		assertEquals(1.0986122886, p.magnitude(), ERROR);
	}
}
