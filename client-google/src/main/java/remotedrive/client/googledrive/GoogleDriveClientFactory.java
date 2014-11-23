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

package remotedrive.client.googledrive;

import remotedrive.core.Configuration;
import remotedrive.core.spi.Client;
import remotedrive.core.spi.ClientFactory;

import java.io.InputStream;

/**
 * Implements ClientFactory for google drive.
 */
public class GoogleDriveClientFactory implements ClientFactory {

    /**
     * Gets google drive factory name.
     * @return "GoogleDrive"
     */
    @Override
    public String getName() {
        return "GoogleDrive";
    }

    /**
     * Gets google drive icon as stream.
     * @return Google drive icon as stream
     */
    @Override
    public InputStream getImageAsStream() {
        return getClass().getResourceAsStream("/img/google-drive.png");
    }

    /**
     * Creates a new instance of google drive client.
     * @param configuration The configuration.
     * @return The new instance of client.
     */
    @Override
    public Client createClient(Configuration configuration) {
        return configuration.readBoolean("caching") ?
                new CachedGoogleDriveClient() :
                new GoogleDriveClient();
    }
}
