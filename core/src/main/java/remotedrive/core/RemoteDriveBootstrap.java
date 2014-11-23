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

package remotedrive.core;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import remotedrive.core.exception.BootstrapException;
import remotedrive.core.spi.ClientFactory;
import remotedrive.core.spi.FileSystemHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Cloud storage application entry point.
 * Allows to load all extensions such as client factories and fs Handlers.
 */
public class RemoteDriveBootstrap
{
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(RemoteDriveBootstrap.class);

    /**
     * The loaded configurations.
     */
    private List<Configuration> configuration;

    /**
     * The loaded client factories.
     */
    private Map<String, ClientFactory> clientFactories;

    /**
     * The loaded file system handler.
     */
    private FileSystemHandler fileSystemHandler;

    /**
     * The extension loader.
     */
    private ExtensionLoader extensionLoader;

    /**
     * The configuration path.
     */
    private Path configurationPath;

    /**
     * Initializes the bootstrap with default extension loader.
     * @param configurationPath The configuration path.
     */
    public RemoteDriveBootstrap(Path configurationPath)
    {
        this(new ExtensionLoader(), configurationPath);
    }

    /**
     * Initializes an instance of the application.
     * @param extensionLoader The extension loader.
     * @param configurationPath The configuration path.
     */
    public RemoteDriveBootstrap(ExtensionLoader extensionLoader, Path configurationPath)
    {
        // Check input
        if (null == extensionLoader)
        {
            throw new IllegalArgumentException("The extension loader cannot be null.");
        }
        if (null == configurationPath)
        {
            throw new IllegalArgumentException("The configuration path cannot be null.");
        }

        // Initialize
        this.clientFactories = new HashMap<String, ClientFactory>();
        this.extensionLoader = extensionLoader;
        this.configurationPath = configurationPath;
    }

    /**
     * Gets the configurations.
     * @return The configurations.
     */
    public List<Configuration> getConfigurations()
    {
        return configuration;
    }

    /**
     * Loads the application resources that contains:
     * - The existing configuration in the environment
     * - The available file system handler
     * - The available client factories
     * @throws IOException If the configuration are not loadable.
     */
    public void load() throws IOException
    {
        // Load services
        loadFileSystemHandler();
        loadClients();

        // Init configuration
        initConfiguration();
    }

    /**
     * Saves all the configurations.
     * @throws IOException If the configurations are not writable.
     */
    public void saveConfiguration()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(configurationPath.toFile(), configuration);
            log.info(String.format(Locale.US, "Saved configuration: %s", configurationPath));
        }
        catch (IOException ioException)
        {
            // Raise error
            BootstrapException exception = new BootstrapException(ioException.getMessage());
            log.error(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * Looks up a loaded client factory in order to provide possible callbacks.
     * @param name The client factory name.
     * @return The lookup factory.
     */
    public ClientFactory lookupClientFactory(String name)
    {
        return clientFactories.getOrDefault(name, null);
    }

    /**
     * Provides all the available client factories names.
     * @return The available client factories names.
     */
    public Set<String> availableClientFactories()
    {
        return clientFactories.keySet();
    }

    /**
     * Gets the file system handler.
     * @return The file system handler.
     */
    public FileSystemHandler getFileSystemHandler()
    {
        return fileSystemHandler;
    }

    /**
     * Loads the file system handler.
     */
    void loadFileSystemHandler()
    {
        // Verify that a file system handler is available
        Iterator<FileSystemHandler> fileSystemHandlerIterator = extensionLoader.load(FileSystemHandler.class);
        if (!fileSystemHandlerIterator.hasNext())
        {
            BootstrapException exception = new BootstrapException("Cannot initialize the application because no file system handler have been found.");
            log.error(exception.getMessage(), exception);
            throw exception;
        }

        // Lookup the first file system handler
        fileSystemHandler = fileSystemHandlerIterator.next();

        // If more file system loader are available, raise an exception because of the ambiguity
        if (fileSystemHandlerIterator.hasNext())
        {
            // Build error message
            StringBuilder sb = new StringBuilder("Only one file system handler is possible but many of them were found: ");
            sb.append(fileSystemHandler.getClass().getName());
            while(fileSystemHandlerIterator.hasNext())
            {
                sb.append(", ");
                sb.append(fileSystemHandlerIterator.next().getClass().getName());
            }
            sb.append(".");

            // Raise error
            BootstrapException exception = new BootstrapException(sb.toString());
            log.error(exception.getMessage(), exception);
            throw exception;
        }

        // File handler loaded
        log.info(String.format(Locale.US, "System handler successfully loaded: %s", fileSystemHandler.getClass().getName()));
    }

    /**
     * Loads the clients.
     */
    void loadClients()
    {
        // Lookup all client factories using ServiceLoader
        Iterator<ClientFactory> clientIterator = extensionLoader.load(ClientFactory.class);
        while(clientIterator.hasNext())
        {
            ClientFactory clientFactory = clientIterator.next();
            clientFactories.put(clientFactory.getName(), clientFactory);
            log.info(String.format(Locale.US, "Client successfully loaded: %s (%s)", clientFactory.getClass().getName(), clientFactory.getName()));
        }
    }

    /**
     * Read all existing configurations.
     * If no configuration exists it initializes en empty list on the file system.
     */
    void initConfiguration()
    {
        // Create an empty configuration if not found
        try
        {
            if (!Files.exists(configurationPath))
            {
                configuration = new ArrayList<Configuration>();
                saveConfiguration();
            }

            // Load existing configuration
            else
            {
                ObjectMapper mapper = new ObjectMapper();
                JavaType type = mapper.getTypeFactory().constructParametricType(List.class, Configuration.class);
                configuration = mapper.readValue(configurationPath.toFile(), type);
                log.info(String.format(Locale.US, "Loaded configuration: %s", configurationPath));
            }
        }
        catch (Exception exception)
        {
            BootstrapException bootstrapException = new BootstrapException("Unable to read configuration.", exception);
            log.error(bootstrapException.getMessage(), exception);
            throw bootstrapException;
        }
    }
}