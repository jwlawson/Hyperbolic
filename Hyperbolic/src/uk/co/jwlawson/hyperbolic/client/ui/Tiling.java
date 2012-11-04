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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import uk.co.jwlawson.hyperbolic.client.geometry.Line;

import java.util.ArrayList;

/**
 * @author John
 * 
 */
public class Tiling implements CanvasHolder {

	private static final String MOUNT_ID = "tilingcanvas";
	private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	private ArrayList<Line> mLineList;

	private Canvas canvas;
	private Context2d context;

	// canvas size, in px
	private int height = 400;
	private int width = 400;

	public Tiling() {
		mLineList = new ArrayList<Line>();

		canvas = Canvas.createIfSupported();
		if (canvas == null) {
			RootPanel.get(MOUNT_ID).add(new Label(upgradeMessage));
			return;
		}

		initSize();

		context = canvas.getContext2d();

	}

	public void initSize() {
		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
	}

	@Override
	public void doUpdate() {

		context.setFillStyle("#33e5b5");
		context.beginPath();
		context.arc(0, 0, 100, 0, 2 * Math.PI);
		context.closePath();
		context.fill();

		context.beginPath();
		context.arc(width, height, 100, 0, 2 * Math.PI);
		context.closePath();
		context.fill();
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
