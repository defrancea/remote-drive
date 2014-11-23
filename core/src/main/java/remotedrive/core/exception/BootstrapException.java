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

package remotedrive.core.exception;

/**
 * Represents a bootstrap exception.
 */
public class BootstrapException extends RuntimeException
{
    /**
     * Delegates to the parent constructor.
     * @param message The message.
     */
    public BootstrapException(String message)
    {
        super(message);
    }

    /**
     * Delegates to the parent constructor.
     * @param message The message.
     * @param cause The cause.
     */
    public BootstrapException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
