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

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import remotedrive.core.Configuration;
import remotedrive.core.RemoteDriveBootstrap;
import remotedrive.gui.UIApplication;
import remotedrive.gui.component.ConfigurationCellFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main window controller.
 */
public class MainController extends AbstractController implements Initializable
{
    /**
     * Drive list UI component.
     */
    @FXML private ListView<Configuration> configurationListView;

    /**
     * Drive list configurations.
     */
    @FXML private ObservableList<Configuration> configurations;

    /**
     * Initializes a new instance of main controller.
     * @param uiApplication The gui application.
     * @param remoteDriveApplication The remote drive application.
     * @param resourceBundle The resource bundle.
     */
    public MainController(
            UIApplication uiApplication,
            RemoteDriveBootstrap remoteDriveApplication,
            ResourceBundle resourceBundle)
    {
        super(uiApplication, remoteDriveApplication, resourceBundle);
    }

    /**
     * Initializes the window.
     * @param location The location.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurations.addAll(remoteDriveBootstrap.getConfigurations());
        configurationListView.setCellFactory(new ConfigurationCellFactory(
                uiApplication,
                remoteDriveBootstrap,
                resourceBundle));
    }

    /**
     * Handles onCreateConfiguration event.
     * @param event The event.
     */
    @FXML protected void onCreateDrive(ActionEvent event) throws IOException
    {
        // Create a modal configuration window
        Stage stage = new Stage();
        FXMLLoader loader = uiApplication.createLoader(getClass().getResource("/template/configure.fxml"), stage);
        Parent root = loader.load();
        ConfigureController controller = loader.getController();
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle(resourceBundle.getString("window.newdrive.title"));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow() );
        stage.showAndWait();

        // Handle the created configuration
        Configuration configuration = controller.getConfiguration();
        if (null != configuration)
        {
            // Add the configuration in the application
            remoteDriveBootstrap.getConfigurations().add(configuration);
            remoteDriveBootstrap.saveConfiguration();

            // Add the configuration in the UI
            configurations.add(configuration);
        }
    }

    /**
     * Handles onDeleteConfiguration event.
     * @param event The event.
     */
    @FXML protected void onDeleteDrive(ActionEvent event) {
        int removedConfigurationIndex = configurationListView.getSelectionModel().getSelectedIndex();
        remoteDriveBootstrap.getConfigurations().remove(removedConfigurationIndex);
        configurations.remove(removedConfigurationIndex);
        configurationListView.getSelectionModel().clearSelection();
        remoteDriveBootstrap.saveConfiguration();
    }
}
