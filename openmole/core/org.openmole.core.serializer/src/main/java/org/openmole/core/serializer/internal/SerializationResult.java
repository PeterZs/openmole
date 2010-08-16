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

package org.openmole.core.serializer.internal;

import java.io.File;
import java.util.Collection;
import org.openmole.core.serializer.ISerializationResult;

/**
 *
 * @author reuillon
 */
public class SerializationResult implements ISerializationResult {

    final Collection<Class> classesFromPlugin;
    final Collection<File> files;

    public SerializationResult(Collection<Class> classesFromPlugin, Collection<File> files) {
        this.classesFromPlugin = classesFromPlugin;
        this.files = files;
    }

    @Override
    public Collection<Class> getClassesFromPlugin() {
        return classesFromPlugin;
    }

    @Override
    public Collection<File> getFiles() {
        return files;
    }
    
    
}
