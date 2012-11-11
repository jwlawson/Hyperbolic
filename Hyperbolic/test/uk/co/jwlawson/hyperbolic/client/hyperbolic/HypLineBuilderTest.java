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

import org.junit.Before;
import org.junit.Test;

import uk.co.jwlawson.hyperbolic.client.hyperbolic.HypLine.Builder;

/**
 * @author John
 * 
 */
public class HypLineBuilderTest {

	private static final double ERROR = 10E-6;

	private Builder builder;

	@Before
	public void setup() {
		builder = new Builder();
	}

	@Test(expected = InstantiationError.class)
	public void testInvaldBuild() {
		builder.build();
	}

	@Test
	public void testCalcAngles() {
		builder.setCentre(1, 1);
		builder.setRadius(1);
		builder.calcAngles();
		HypLine line = builder.build();

		assertEquals(Math.PI, line.getEndAngle(), ERROR);
		assertEquals(Math.PI * 3 / 2, line.getStartAngle(), ERROR);
	}

	@Test
	public void testComplicatedCalcAngles() {
		builder.setCentre(1, 2);
		builder.calcRadius();
		builder.calcAngles();
		HypLine line = builder.build();

		assertEquals(Math.PI * 3 / 2, line.getStartAngle(), ERROR);
		assertEquals(3.7850927644, line.getEndAngle(), ERROR);
	}

	/**
	 * Test method for
	 * {@link uk.co.jwlawson.hyperbolic.client.hyperbolic.HypLine.Builder#setAngles(double, double)}
	 * .
	 */
	@Test
	public void testSetSwapAngles() {
		builder.setCentre(-1, -1);
		builder.setRadius(1);
		builder.setAngles(0, Math.PI / 2);
		HypLine line = builder.build();
		assertEquals(Math.PI / 2, line.getStartAngle(), ERROR);
		assertEquals(0, line.getEndAngle(), ERROR);
	}

	@Test
	public void testSetAngles() {
		builder.setCentre(1, 1);
		builder.setRadius(1);
		builder.setAngles(Math.PI, Math.PI * 3 / 2);
		HypLine line = builder.build();
		assertEquals(Math.PI * 3 / 2, line.getStartAngle(), ERROR);
		assertEquals(Math.PI, line.getEndAngle(), ERROR);
	}

}
