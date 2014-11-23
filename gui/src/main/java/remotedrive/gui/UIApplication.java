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

package remotedrive.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import remotedrive.core.RemoteDriveBootstrap;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Graphical interface bootstrap.
 */
public class UIApplication extends Application
{
    /**
     * Main method.
     * @param args Application arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The remote drive application.
     */
    private RemoteDriveBootstrap remoteDriveApplication;

    /**
     * The resource bundle.
     */
    private ResourceBundle resourceBundle;

    /**
     * JavaFX bootstrap.
     * @param stage The stage.
     */
    @Override
    public void start(Stage stage) throws Exception
    {
        // Load cloud storage application
        String userHome = System.getProperty("user.home");
        Path path = Paths.get(String.format(Locale.US, "%s%s.remotedrive", userHome, File.separator));
        remoteDriveApplication = new RemoteDriveBootstrap(path);
        remoteDriveApplication.load();

        // Load resources
        resourceBundle = ResourceBundle.getBundle("i18n.main");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon/remote-drive.png")));
        Parent root = createLoader(getClass().getResource("/template/main.fxml")).load();

        // Initialize the scene
        Scene scene = new Scene(root, 500, 400);
        stage.setTitle(resourceBundle.getString("window.main.title"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates a new FXMLLoader from a fxml template and controller parameters
     * @param templateURL The FXML template url.
     * @param controllerParameter The controller parameters.
     * @return The FXMLLoader
     */
    public FXMLLoader createLoader(URL templateURL, Object... controllerParameter)
    {
        // Input check
        if (null == templateURL)
        {
            throw new IllegalArgumentException("The template URL cannot be null.");
        }
        if (null == controllerParameter)
        {
            throw new IllegalArgumentException("The controller parameters cannot be null.");
        }

        // Init the controller factory
        ControllerFactory controllerFactory = new ControllerFactory(
                this,
                remoteDriveApplication,
                resourceBundle,
                controllerParameter);

        // Create the new loader and return it
        FXMLLoader loader = new FXMLLoader(templateURL, resourceBundle);
        loader.setControllerFactory(controllerFactory);
        return loader;
    }
}