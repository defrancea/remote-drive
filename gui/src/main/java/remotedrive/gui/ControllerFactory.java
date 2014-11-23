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

import javafx.util.Callback;
import org.apache.log4j.Logger;
import remotedrive.core.RemoteDriveBootstrap;
import remotedrive.core.exception.BootstrapException;
import remotedrive.gui.controller.AbstractController;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller factory.
 * All UI Controller have to extend AbstractController in order to provide default context.
 * The created controller can also contains some extra parameters.
 * The factory have to capture the application context and inject it into the controller during its creation.
 */
public class ControllerFactory implements Callback<Class<?>, Object>
{
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(ControllerFactory.class);

    /**
     * Core bootstrap allowing the control the core features.
     */
    protected RemoteDriveBootstrap remoteDriveApplication;

    /**
     * The application resource bundle.
     */
    protected ResourceBundle resourceBundle;

    /**
     * The extra parameters.
     */
    protected Object[] extraParameters;

    /**
     * The UI application.
     */
    protected UIApplication uiApplication;

    /**
     * Initializes controller factory.
     * @param uiApplication The gui application.
     * @param remoteDriveApplication The remote drive application.
     * @param resourceBundle The resource bundle.
     * @param extraParameters The extra parameters.
     */
    public ControllerFactory(
            UIApplication uiApplication,
            RemoteDriveBootstrap remoteDriveApplication,
            ResourceBundle resourceBundle,
            Object... extraParameters)
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
        if (null == extraParameters)
        {
            throw new IllegalArgumentException("The extra parameters cannot be null.");
        }

        // Initialize
        this.uiApplication = uiApplication;
        this.remoteDriveApplication = remoteDriveApplication;
        this.resourceBundle = resourceBundle;
        this.extraParameters = extraParameters;
    }

    /**
     * Implements the callback.
     *
     * @param controllerClass The controller class.
     * @return The created controller.
     */
    @Override
    public Object call(Class<?> controllerClass)
    {
        if (null == controllerClass)
        {
            throw new IllegalArgumentException("The controller class cannot be null.");
        }
        if (controllerClass.isAssignableFrom(AbstractController.class))
        {
            throw new IllegalArgumentException(
                    String.format(
                            Locale.US,
                            "%s have to implement %s",
                            controllerClass.getName(),
                            AbstractController.class.getName()));
        }

        // Initialize both list representing the constructor parameters
        List<Class<?>> classes = new ArrayList<Class<?>>();
        List<Object> parameters = new ArrayList<Object>();

        // Add default constructor parameters types
        classes.add(UIApplication.class);
        classes.add(RemoteDriveBootstrap.class);
        classes.add(ResourceBundle.class);

        // Add default constructor parameters values
        parameters.add(uiApplication);
        parameters.add(remoteDriveApplication);
        parameters.add(resourceBundle);

        // Apply extra parameters
        for(Object extraParameter : extraParameters)
        {
            classes.add(extraParameter.getClass());
            parameters.add(extraParameter);
        }

        try
        {
            // Try to find a matching constructor and call it
            Constructor constructor = controllerClass.getConstructor(classes.toArray(new Class[classes.size()]));
            Object newInstance = constructor.newInstance(parameters.toArray(new Object[parameters.size()]));
            log.info(String.format(Locale.US, "GUI Controller created: %s", controllerClass));
            return newInstance;
        }
        catch (Exception exception)
        {
            // Build parameter signature
            StringBuilder parametersSignatureBuilder = new StringBuilder();
            for(Class clazz : classes)
            {
                // Add separator
                if(0 < parametersSignatureBuilder.length())
                    parametersSignatureBuilder.append(", ");

                // Add the class name
                parametersSignatureBuilder.append(clazz.getName());
            }

            // Build the error message
            String message = String.format(
                    Locale.US,
                    "Constructor %s(%s) not found",
                    controllerClass.getName(),
                    parametersSignatureBuilder.toString());

            // Create the exception
            BootstrapException bootstrapException = new BootstrapException(message, exception);

            // Log and throw
            log.error(bootstrapException.getMessage(), bootstrapException);
            throw bootstrapException;
        }
    }
}
