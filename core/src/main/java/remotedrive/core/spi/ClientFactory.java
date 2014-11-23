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

import java.io.InputStream;

/**
 * Defines a client factory behavior.
 * Any remote api supported have to implement the client facotry in order to:
 * - Identify the client
 * - Load related resources
 * - Create the related client
 * All implement will be loaded using ServiceLoader in order to customize the packaging and provide plugability.
 */
public interface ClientFactory
{
    /**
     * The factory id.
     * Used to associate match configured drive to a given factory.
     * @return The factory name.
     */
    String getName();

    /**
     * The drive image as an InputStream.
     * @return The image as a stream.
     */
    InputStream getImageAsStream();

    /**
     * Create a new client from a configuration.
     * @param configuration The configuration.
     * @return The created client.
     */
    Client createClient(Configuration configuration);
}
