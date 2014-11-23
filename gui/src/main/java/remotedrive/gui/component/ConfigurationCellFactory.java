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

package remotedrive.gui.component;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import remotedrive.core.Configuration;
import remotedrive.core.RemoteDriveBootstrap;
import remotedrive.gui.UIApplication;

import java.util.ResourceBundle;

/**
 * Implements configuration cell factory.
 */
public class ConfigurationCellFactory implements Callback<ListView<Configuration>, ListCell<Configuration>>
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
     * Initializes the cell factory.
     * @param uiApplication The gui application.
     * @param remoteDriveBootstrap The remote drive application.
     * @param resourceBundle The resource bundle.
     */
    public ConfigurationCellFactory(
            UIApplication uiApplication,
            RemoteDriveBootstrap remoteDriveBootstrap,
            ResourceBundle resourceBundle)
    {
        this.uiApplication = uiApplication;
        this.remoteDriveBootstrap = remoteDriveBootstrap;
        this.resourceBundle = resourceBundle;
    }
    @Override
    public ListCell<Configuration> call(ListView<Configuration> param)
    {
        return new ConfigurationCellRenderer(uiApplication, remoteDriveBootstrap, resourceBundle);
    }
}
