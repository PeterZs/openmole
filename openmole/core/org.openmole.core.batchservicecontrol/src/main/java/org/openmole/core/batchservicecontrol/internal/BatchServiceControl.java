/*
 *  Copyright (C) 2010 reuillon
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the Affero GNU General Public License as published by
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
package org.openmole.core.batchservicecontrol.internal;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.openmole.core.batchservicecontrol.IBatchServiceControl;
import org.openmole.core.batchservicecontrol.IBatchServiceController;
import org.openmole.core.model.execution.batch.IBatchServiceDescription;
import org.openmole.core.batchservicecontrol.IFailureControl;
import org.openmole.core.batchservicecontrol.IUsageControl;

public class BatchServiceControl implements IBatchServiceControl {

    Map<IBatchServiceDescription, IBatchServiceController> ressources = Collections.synchronizedMap(new TreeMap<IBatchServiceDescription, IBatchServiceController>());

    @Override
    public void registerRessouce(IBatchServiceDescription ressource, IUsageControl usageControl, IFailureControl failureControl) {
        ressources.put(ressource, new BatchServiceController(usageControl, failureControl));
    }

    @Override
    public IBatchServiceController getController(IBatchServiceDescription ressource) {
        IBatchServiceController controller = ressources.get(ressource);
        if(controller == null) return BatchServiceController.defaultController;
        else return controller;
    }

    @Override
    public boolean contains(IBatchServiceDescription ressource) {
        return ressources.containsKey(ressource);
    }
    
}
