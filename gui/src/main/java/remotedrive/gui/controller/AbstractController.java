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

package remotedrive.gui.controller;

import remotedrive.core.RemoteDriveBootstrap;
import remotedrive.gui.UIApplication;

import java.util.ResourceBundle;

/**
 * Abstract gui controller.
 */
public abstract class AbstractController
{
    /**
     * The UI application.
     */
    protected UIApplication uiApplication;

    /**
     * Core bootstrap allowing the control the core features.
     */
    protected RemoteDriveBootstrap remoteDriveBootstrap;

    /**
     * The application resource bundle.
     */
    protected ResourceBundle resourceBundle;

    /**
     * Initializes MainController.
     * @param uiApplication The gui application.
     * @param remoteDriveApplication The remote drive application.
     * @param resourceBundle The resource bundle.
     */
    public AbstractController(
            UIApplication uiApplication,
            RemoteDriveBootstrap remoteDriveApplication,
            ResourceBundle resourceBundle)
    {
        // Input check
        if (null == uiApplication)
        {
            throw new IllegalArgumentException("The gui application cannot be null.");
        }
        if (null == remoteDriveApplication)
        {
            throw new IllegalArgumentException("The remote drive application cannot be null.");
        }
        if (null == resourceBundle)
        {
            throw new IllegalArgumentException("The resource bundle cannot be null.");
        }

        // Initialize
        this.uiApplication = uiApplication;
        this.remoteDriveBootstrap = remoteDriveApplication;
        this.resourceBundle = resourceBundle;
    }
}
