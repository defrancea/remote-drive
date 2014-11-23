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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import remotedrive.core.Configuration;
import remotedrive.core.RemoteDriveBootstrap;
import remotedrive.gui.UIApplication;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Drive item controller.
 */
public class DriveItemController extends AbstractController implements Initializable
{
    /**
     * The configuration icon.
     */
    @FXML
    private ImageView configurationIcon;

    /**
     * The configuration name.
     */
    @FXML
    private Label configurationNameLabel;

    /**
     * The configuration mounting point.
     */
    @FXML
    private Label configurationMountingPointLabel;

    /**
     * The connect toggle.
     */
    @FXML
    private ToggleButton connectToggleButton;

    /**
     * The configuration button.
     */
    @FXML
    private Button configureButton;

    /**
     * The configuration.
     */
    private Configuration configuration;

    /**
     * Initializes a new instance of main controller.
     * @param uiApplication The gui application.
     * @param remoteDriveApplication The remote drive application.
     * @param resourceBundle The resource bundle.
     * @param configuration The configuration.
     */
    public DriveItemController(
            UIApplication uiApplication,
            RemoteDriveBootstrap remoteDriveApplication,
            ResourceBundle resourceBundle,
            Configuration configuration)
    {
        super(uiApplication, remoteDriveApplication, resourceBundle);
        this.configuration = configuration;
    }

    /**
     * Initializes the controller.
     * @param location The location.
     * @param resources The resources.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        configureButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icon/configure.png"))));
        configurationMountingPointLabel.setText(String.format("(%s:)", configuration.readString("mounting-point")));
        configurationNameLabel.setText(configuration.readString("name"));
        configurationIcon.setImage(new Image(this.remoteDriveBootstrap.lookupClientFactory(this.configuration.readString("factory-name")).getImageAsStream()));
        connectToggleButton.setSelected(configuration.readBoolean("enabled"));
        refreshToggle(true);
    }

    /**
     * Initializes the on toggle event.
     * @param event The event.
     */
    @FXML protected void onToggle(ActionEvent event) {
        refreshToggle(false);
    }

    /**
     * Implement the on configure action.
     * @param event The event.
     */
    @FXML protected void onConfigure(ActionEvent event)
    {
        // TODO: Implement it
    }

    /**
     * Refreshes the toggle component after action.
     * @param initialize True if initialization.
     */
    private void refreshToggle(boolean initialize) {
        // Change the configuration value if not initialize only.
        if(!initialize)
        {
            this.configuration.writeBoolean("enabled", connectToggleButton.isSelected());
            remoteDriveBootstrap.saveConfiguration();
        }

        // Change the text depending on the status
        if(connectToggleButton.isSelected())
        {
            connectToggleButton.setText(resourceBundle.getString("list.enabled"));
        }
        else
        {
            connectToggleButton.setText(resourceBundle.getString("list.disabled"));
        }
    }
}
