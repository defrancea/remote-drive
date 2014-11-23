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

import remotedrive.core.Drive;
import remotedrive.core.File;

/**
 * Defines client capabilities.
 */
public interface Client {

    /**
     * Authenticates to the remote system.
     * @param username The username.
     * @param password The password.
     */
    void authenticate(String username, char[] password);

    /**
     * Retrieves disk information.
     * @return The disk information.
     */
    Drive retrieveDiskInformation();

    /**
     * Retrieves children.
     * @param parentPath The parent file.
     * @return The children.
     */
    File[] retrieveChildren(final String parentPath);

    /**
     * Retrieves file from absolute path.
     * @param filePath The absolute path.
     * @return The file.
     */
    File retrieveDriveFile(String filePath);

    /**
     * Retrieve file content from absolute path.
     * @param filePath The absolute file.
     * @return The content as stream.
     */
    byte[] retrieveDriveFileContent(String filePath);
}