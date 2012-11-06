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
package uk.co.jwlawson.hyperbolic.client.ui;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import uk.co.jwlawson.hyperbolic.client.euclidean.EuclPoint;
import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.group.TorusOrbitPoints;

import java.util.ArrayList;

/**
 * @author John
 * 
 */
public class Tiling implements CanvasHolder {

	private static final String MOUNT_ID = "tilingcanvas";
	private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	private ArrayList<Line> mLineList;
	private ArrayList<EuclPoint> mPointList;

	private Canvas canvas;
	private Context2d context;

	// canvas size, in px
	private int height = 400;
	private int width = 400;
	private final CssColor redrawColor = CssColor.make("rgb(255,255,255)");

	public Tiling() {
		mLineList = new ArrayList<Line>();

		canvas = Canvas.createIfSupported();
		if (canvas == null) {
			RootPanel.get(MOUNT_ID).add(new Label(upgradeMessage));
			return;
		}

		initSize();

		context = canvas.getContext2d();

		initPoints();

	}

	private void initPoints() {
		mPointList = new ArrayList<EuclPoint>();
		TorusOrbitPoints orbit = new TorusOrbitPoints(width, height);
		while (orbit.hasNext()) {
			mPointList.add(orbit.next());
		}
	}

	public void initSize() {
		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);

		initPoints();
	}

	@Override
	public void doUpdate() {

		context.save();
		context.translate(width / 2, height / 2);

		context.setFillStyle(redrawColor);
		context.fillRect(-width, -height, width, height);

		for (EuclPoint point : mPointList) {
			point.draw(context);
		}
		context.restore();
	}

	@Override
	public void initHandlers() {

	}

	@Override
	public void addToPanel() {
		RootPanel panel = RootPanel.get(MOUNT_ID);
		width = panel.getOffsetWidth();
		height = width;

		initSize();
		doUpdate();

		panel.add(canvas);

	}

}
