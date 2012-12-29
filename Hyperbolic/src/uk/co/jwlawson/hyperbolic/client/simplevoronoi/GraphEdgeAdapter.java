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
package uk.co.jwlawson.hyperbolic.client.simplevoronoi;

import uk.co.jwlawson.hyperbolic.client.geometry.hyperbolic.HypPoint;

/**
 * @author John
 * 
 */
public class GraphEdgeAdapter {

	private GraphEdge edge;

	public GraphEdgeAdapter(GraphEdge edge) {
		this.edge = edge;
	}

	public HypPoint getStart() {
		return new HypPoint(edge.x1, edge.y1);
	}

	public HypPoint getEnd() {
		return new HypPoint(edge.x2, edge.y2);
	}

}
