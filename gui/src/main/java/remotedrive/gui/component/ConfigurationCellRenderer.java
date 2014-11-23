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

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import org.apache.log4j.Logger;
import remotedrive.core.Configuration;
import remotedrive.core.RemoteDriveBootstrap;
import remotedrive.gui.UIApplication;

import java.util.ResourceBundle;

/**
 * Implements a configuration cell renderer.
 */
public class ConfigurationCellRenderer extends ListCell<Configuration>
{
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(ConfigurationCellRenderer.class);

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
     * Initializes the cell renderer.
     * @param uiApplication The gui application.
     * @param remoteDriveBootstrap The remote drive application.
     * @param resourceBundle The resource bundle.
     */
    public ConfigurationCellRenderer(
            UIApplication uiApplication,
            RemoteDriveBootstrap remoteDriveBootstrap,
            ResourceBundle resourceBundle)
    {
        this.uiApplication = uiApplication;
        this.remoteDriveBootstrap = remoteDriveBootstrap;
        this.resourceBundle = resourceBundle;
    }

    /**
     * Renders the cell.
     * @param configuration The configuration to render.
     * @param empty Is an empty cell or not.
     */
    @Override
    public void updateItem(Configuration configuration, boolean empty)
    {
        // Call the parent rendering
        super.updateItem(configuration, empty);

        // Render the cell only if it's not an empty one
        if (!empty)
        {
            // If the configuration is set and the UI component is not generated yet
            if (null != configuration && null == getGraphic())
            {
                try
                {
                    FXMLLoader loader = uiApplication.createLoader(getClass().getResource("/template/drive-item.fxml"), configuration);
                    Node graphic = loader.load();
                    setGraphic(graphic);
                }
                catch (Exception exception)
                {
                    log.error(exception.getMessage(), exception);
                }
            }

        }
        else
        {
            setGraphic(null);
        }
    }
}
