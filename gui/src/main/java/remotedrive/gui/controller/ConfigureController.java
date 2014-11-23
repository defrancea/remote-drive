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
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import remotedrive.core.Configuration;
import remotedrive.core.RemoteDriveBootstrap;
import remotedrive.gui.UIApplication;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Configure configuration controller.
 */
public class ConfigureController extends AbstractController implements Initializable
{
    /**
     * The available mounting point list.
     * TODO: Will be removed if the combo is changed into a free text or might be disabled depending on
     *       the file system handler.
     */
    @FXML
    private ObservableList<String> availableMountingPoint;

    /**
     * The list of available client type.
     */
    @FXML
    private ObservableList<String> availableClientType;

    /**
     * The drive label text field.
     */
    @FXML
    private TextField driveLabelField;

    /**
     * The mounting point combo.
     * TODO: Currently a combo because windows manages mounting point with letters.
     *       For linux implementation it will have to be a free text or automatically pick up a location.
     *       An abstraction will have to be done from the file system handler.
     */
    @FXML
    private ComboBox mountingPointCombo;

    /**
     * The client type combo.
     */
    @FXML
    private ComboBox clientTypeCombo;

    /**
     * The configuration
     */
    private Configuration configuration;

    /**
     * The stage.
     */
    private Stage stage;

    /**
     * Initializes a new instance of main controller.
     * @param uiApplication The gui application.
     * @param remoteDriveApplication The remote drive application.
     * @param resourceBundle The resource bundle.
     * @param stage The stage.
     */
    public ConfigureController(
            UIApplication uiApplication,
            RemoteDriveBootstrap remoteDriveApplication,
            ResourceBundle resourceBundle,
            Stage stage)
    {
        super(uiApplication, remoteDriveApplication, resourceBundle);
        this.stage = stage;
    }

    /**
     * Initializes the controller.
     * @param location The location.
     * @param resources The resources.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // Add some sample letter
        // TODO: Determine them from the available ones
        availableMountingPoint.add("A");
        availableMountingPoint.add("B");
        availableMountingPoint.add("C");
        availableMountingPoint.add("D");

        // Initialize available providers
        availableClientType.addAll(remoteDriveBootstrap.availableClientFactories());

        // Initialize the combo selection
        mountingPointCombo.getSelectionModel().selectFirst();
        clientTypeCombo.getSelectionModel().selectFirst();
    }

    /**
     * Implements the on save event.
     * @param event The event.
     */
    @FXML protected void onSave(ActionEvent event)
    {
        // Init the new configuration
        configuration = new Configuration();
        configuration.writeString("mounting-point", (String) mountingPointCombo.getSelectionModel().getSelectedItem());
        configuration.writeString("name", driveLabelField.getText());
        configuration.writeString("factory-name", (String) clientTypeCombo.getSelectionModel().getSelectedItem());
        configuration.writeBoolean("caching", false);
        configuration.writeBoolean("enabled", false);

        // Close the window
        stage.close();
    }

    /**
     * Implements the on cancel event
     * @param event The event.
     */
    @FXML protected void onCancel(ActionEvent event)
    {
        // TODO: Implement it
        stage.close();
    }

    /**
     * The created configuration getter.
     * @return The created configuration.
     */
    public Configuration getConfiguration() {
        return configuration;
    }
}
