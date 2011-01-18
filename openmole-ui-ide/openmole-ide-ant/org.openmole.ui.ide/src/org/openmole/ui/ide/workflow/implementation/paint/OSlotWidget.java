/*
 *  Copyright (C) 2011 Mathieu Leclaire <mathieu.leclaire@openmole.org>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openmole.ui.ide.workflow.implementation.paint;

import java.awt.Point;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.Scene;
import org.openmole.ui.ide.commons.ApplicationCustomize;
import org.openmole.ui.ide.workflow.implementation.CapsuleViewUI;

/**
 *
 * @author Mathieu Leclaire <mathieu.leclaire@openmole.org>
 */
public class OSlotWidget extends SlotWidget {
    public OSlotWidget(Scene scene,CapsuleViewUI capsule) {
        super(scene,capsule);
        setImage(ApplicationCustomize.IMAGE_OUTPUT_SLOT);
        setDetailedView(ApplicationCustomize.TASK_CONTAINER_WIDTH);
    }

    public void setDetailedView(int w){
        setPreferredLocation(new Point(w - 6, 14 + ApplicationCustomize.TASK_TITLE_HEIGHT));
    }

}
