/*
 * Copyright (c) 2014, Alain Defrance. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package remotedrive.core.spi;

import remotedrive.core.Configuration;

/**
 * Defines a file system behavior.
 * The main purpose is to provide an abstraction between the code and the FS implementation.
 * Any implementation will be loaded using ServiceLoader so as making custom packaging possible depending on the operating system.
 */
public interface FileSystemHandler
{
    /**
     * Return all possible available mounting points.
     * This is a typical usage of windows where the possible mounting points are limited letters.
     * When the system doesn't have any specific requirement for mounting point, just return null;
     * @return all possible mounting points as string.
     */
    String[] getAvailableMountingPoints();

    /**
     * Mounts a drive from a given client
     * @param client The client.
     * @param configuration The configuration.
     */
    void mount(Client client, Configuration configuration);

    /**
     * Unmounts a drive.
     */
    void umount();
}