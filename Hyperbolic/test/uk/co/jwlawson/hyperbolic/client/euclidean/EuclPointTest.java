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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Random;

/**
 * @author John
 * 
 */
public class EuclPointTest {

	private static final double ERROR = 0.000001;

	/**
	 * Test method for
	 * {@link uk.co.jwlawson.hyperbolic.client.euclidean.EuclPoint#distance(uk.co.jwlawson.hyperbolic.client.geometry.Point)}
	 * .
	 */
	@Test
	public void testDistance() {
		EuclPoint p1 = new EuclPoint(1, 1);
		EuclPoint p2 = new EuclPoint(2, 2);

		assertEquals(Math.sqrt(2), p1.distance(p2), ERROR);

		p1 = new EuclPoint(1, 1);
		p2 = new EuclPoint(-1, 1);

		assertEquals(2, p1.distance(p2), ERROR);

	}

	@Test
	public void testZeroDistance() {
		EuclPoint p1;
		EuclPoint p2;
		Random rand = new Random();
		p1 = new EuclPoint(rand.nextInt(), rand.nextInt());
		p2 = new EuclPoint(p1);

		assertEquals(0, p1.distance(p2), ERROR);
	}

	@Test
	public void testDistanceIsSymmetric() {
		EuclPoint p1;
		EuclPoint p2;
		Random rand = new Random();
		p1 = new EuclPoint(rand.nextInt(), rand.nextInt());
		p2 = new EuclPoint(rand.nextInt(), rand.nextInt());

		assertEquals(p2.distance(p1), p1.distance(p2), ERROR);
	}

	/**
	 * Test method for
	 * {@link uk.co.jwlawson.hyperbolic.client.euclidean.EuclPoint#magnitude()}.
	 */
	@Test
	public void testMagnitude() {
		EuclPoint p1;

		p1 = new EuclPoint(2, 0);
		assertEquals(2, p1.magnitude(), ERROR);

		p1 = new EuclPoint(0, -5);
		assertEquals(5, p1.magnitude(), ERROR);

		p1 = new EuclPoint(-3, 4);
		assertEquals(5, p1.magnitude(), ERROR);
	}

	@Test
	public void testZeroMagnitude() {
		EuclPoint p1 = new EuclPoint(0, 0);

		assertEquals(0, p1.magnitude(), ERROR);
	}
}
