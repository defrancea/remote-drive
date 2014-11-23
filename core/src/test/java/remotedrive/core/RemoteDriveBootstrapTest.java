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

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import remotedrive.core.impl.TestClientFactory;
import remotedrive.core.impl.TestFileSystemHandler;
import remotedrive.core.exception.BootstrapException;
import remotedrive.core.spi.ClientFactory;
import remotedrive.core.spi.FileSystemHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the application boostrap.
 */
public class RemoteDriveBootstrapTest
{
    @Rule public ExpectedException nullExtensionLoader = ExpectedException.none();
    @Rule public ExpectedException noSystemHandlerFound = ExpectedException.none();
    @Rule public ExpectedException manySystemHandlerFound = ExpectedException.none();
    @Rule public ExpectedException invalidConfiguration = ExpectedException.none();
    public ExtensionLoader loader;
    public Path configurationPath;
    public Path invalidConfigurationPath;
    public Path expectedConfigurationPath;

    @Before
    public void setup() throws URISyntaxException, IOException
    {
        loader = mock(ExtensionLoader.class);
        configurationPath = Paths.get(getClass().getResource("/test-configuration.json").toURI());
        invalidConfigurationPath = Paths.get(getClass().getResource("/invalid.json").toURI());
        expectedConfigurationPath = Paths.get(getClass().getResource("/expected.json").toURI());
    }

    @Test
    public void load_nullExtensionLoader() throws IOException
    {
        nullExtensionLoader.expect(IllegalArgumentException.class);
        nullExtensionLoader.expectMessage("The extension loader cannot be null.");

        new RemoteDriveBootstrap(null, null);
    }

    @Test
    public void load_fileSystemEmpty() throws IOException
    {
        noSystemHandlerFound.expect(BootstrapException.class);
        noSystemHandlerFound.expectMessage("Cannot initialize the application because no file system handler have been found.");

        when(loader.load(FileSystemHandler.class)).thenReturn(Collections.<FileSystemHandler>emptyIterator());
        RemoteDriveBootstrap remoteDriveBootstrap = new RemoteDriveBootstrap(loader, configurationPath);
        remoteDriveBootstrap.loadFileSystemHandler();
    }

    @Test
    public void load_fileSystemUnique() throws IOException
    {
        when(loader.load(FileSystemHandler.class)).thenReturn(Arrays.<FileSystemHandler>asList(new TestFileSystemHandler()).iterator());
        RemoteDriveBootstrap remoteDriveBootstrap = new RemoteDriveBootstrap(loader, configurationPath);
        remoteDriveBootstrap.loadFileSystemHandler();

        TestCase.assertEquals(TestFileSystemHandler.class, remoteDriveBootstrap.getFileSystemHandler().getClass());
    }

    @Test
    public void load_fileSystemMany() throws IOException
    {
        manySystemHandlerFound.expect(BootstrapException.class);
        manySystemHandlerFound.expectMessage("Only one file system handler is possible but many of them were found: remotedrive.core.impl.TestFileSystemHandler, remotedrive.core.impl.TestFileSystemHandler.");

        when(loader.load(FileSystemHandler.class)).thenReturn(Arrays.<FileSystemHandler>asList(new TestFileSystemHandler(), new TestFileSystemHandler()).iterator());
        RemoteDriveBootstrap remoteDriveBootstrap = new RemoteDriveBootstrap(loader, configurationPath);
        remoteDriveBootstrap.loadFileSystemHandler();
    }

    @Test
    public void load_clientsEmpty() throws IOException
    {
        when(loader.load(ClientFactory.class)).thenReturn(Collections.<ClientFactory>emptyIterator());
        RemoteDriveBootstrap remoteDriveBootstrap = new RemoteDriveBootstrap(loader, configurationPath);
        remoteDriveBootstrap.loadClients();

        TestCase.assertEquals(0, remoteDriveBootstrap.availableClientFactories().size());
    }

    @Test
    public void load_clientsSome() throws IOException
    {
        when(loader.load(ClientFactory.class)).thenReturn(Arrays.<ClientFactory>asList(new TestClientFactory("A"), new TestClientFactory("B")).iterator());
        RemoteDriveBootstrap remoteDriveBootstrap = new RemoteDriveBootstrap(loader, configurationPath);
        remoteDriveBootstrap.loadClients();

        TestCase.assertEquals(2, remoteDriveBootstrap.availableClientFactories().size());
        TestCase.assertEquals(true, remoteDriveBootstrap.availableClientFactories().contains("A"));
        TestCase.assertEquals(true, remoteDriveBootstrap.availableClientFactories().contains("B"));
    }

    @Test
    public void init_existingConfiguration() throws IOException
    {
        when(loader.load(FileSystemHandler.class)).thenReturn(Arrays.<FileSystemHandler>asList(new TestFileSystemHandler()).iterator());
        RemoteDriveBootstrap remoteDriveBootstrap = new RemoteDriveBootstrap(loader, configurationPath);
        remoteDriveBootstrap.initConfiguration();

        TestCase.assertEquals(2, remoteDriveBootstrap.getConfigurations().size());
        TestCase.assertEquals("GoogleDrive", remoteDriveBootstrap.getConfigurations().get(0).readString("global", "client-facotry"));
        TestCase.assertEquals("GoogleDrive", remoteDriveBootstrap.getConfigurations().get(0).readString("client-facotry"));
        TestCase.assertEquals("J", remoteDriveBootstrap.getConfigurations().get(0).readString("global", "mounting-point"));
        TestCase.assertEquals("J", remoteDriveBootstrap.getConfigurations().get(0).readString("mounting-point"));
        TestCase.assertEquals("Drive name", remoteDriveBootstrap.getConfigurations().get(0).readString("global", "name"));
        TestCase.assertEquals("Drive name", remoteDriveBootstrap.getConfigurations().get(0).readString("name"));
        TestCase.assertEquals(true, remoteDriveBootstrap.getConfigurations().get(0).readBoolean("global", "caching"));
        TestCase.assertEquals(true, remoteDriveBootstrap.getConfigurations().get(0).readBoolean("caching"));
        TestCase.assertEquals(true, remoteDriveBootstrap.getConfigurations().get(0).readBoolean("global", "enabled"));
        TestCase.assertEquals(true, remoteDriveBootstrap.getConfigurations().get(0).readBoolean("enabled"));

        TestCase.assertEquals("DropBox", remoteDriveBootstrap.getConfigurations().get(1).readString("global", "client-facotry"));
        TestCase.assertEquals("DropBox", remoteDriveBootstrap.getConfigurations().get(1).readString("client-facotry"));
        TestCase.assertEquals("Q", remoteDriveBootstrap.getConfigurations().get(1).readString("global", "mounting-point"));
        TestCase.assertEquals("Q", remoteDriveBootstrap.getConfigurations().get(1).readString("mounting-point"));
        TestCase.assertEquals("Another drive", remoteDriveBootstrap.getConfigurations().get(1).readString("global", "name"));
        TestCase.assertEquals("Another drive", remoteDriveBootstrap.getConfigurations().get(1).readString("name"));
        TestCase.assertEquals(false, remoteDriveBootstrap.getConfigurations().get(1).readBoolean("global", "caching"));
        TestCase.assertEquals(false, remoteDriveBootstrap.getConfigurations().get(1).readBoolean("caching"));
        TestCase.assertEquals(false, remoteDriveBootstrap.getConfigurations().get(1).readBoolean("global", "enabled"));
        TestCase.assertEquals(false, remoteDriveBootstrap.getConfigurations().get(1).readBoolean("enabled"));
    }

    @Test
    public void init_notExistingConfiguration() throws IOException
    {
        Path tmpPath = Files.createTempFile("", "");
        Files.delete(tmpPath);

        when(loader.load(FileSystemHandler.class)).thenReturn(Arrays.<FileSystemHandler>asList(new TestFileSystemHandler()).iterator());
        RemoteDriveBootstrap remoteDriveBootstrap = new RemoteDriveBootstrap(loader, tmpPath);
        remoteDriveBootstrap.initConfiguration();

        TestCase.assertEquals("[]", new Scanner(tmpPath.toFile()).nextLine());
        TestCase.assertEquals(0, remoteDriveBootstrap.getConfigurations().size());
    }

    @Test
    public void init_invalidConfiguration() throws IOException
    {
        invalidConfiguration.expect(BootstrapException.class);
        invalidConfiguration.expectMessage("Unable to read configuration.");

        when(loader.load(FileSystemHandler.class)).thenReturn(Arrays.<FileSystemHandler>asList(new TestFileSystemHandler()).iterator());
        RemoteDriveBootstrap remoteDriveBootstrap = new RemoteDriveBootstrap(loader, invalidConfigurationPath);
        remoteDriveBootstrap.initConfiguration();
    }

    @Test
    public void load() throws IOException
    {
        when(loader.load(FileSystemHandler.class)).thenReturn(Arrays.<FileSystemHandler>asList(new TestFileSystemHandler()).iterator());
        when(loader.load(ClientFactory.class)).thenReturn(Arrays.<ClientFactory>asList(new TestClientFactory("A")).iterator());
        RemoteDriveBootstrap remoteDriveBootstrap = new RemoteDriveBootstrap(loader, configurationPath);
        remoteDriveBootstrap.load();

        TestCase.assertEquals(TestFileSystemHandler.class, remoteDriveBootstrap.getFileSystemHandler().getClass());
        TestCase.assertEquals(1, remoteDriveBootstrap.availableClientFactories().size());
        TestCase.assertEquals(true, remoteDriveBootstrap.availableClientFactories().contains("A"));
        TestCase.assertEquals(2, remoteDriveBootstrap.getConfigurations().size());
    }

    @Test
    public void saveConfiguration() throws IOException
    {
        Path tmpPath = Files.createTempFile("", "");
        Files.delete(tmpPath);

        when(loader.load(FileSystemHandler.class)).thenReturn(Arrays.<FileSystemHandler>asList(new TestFileSystemHandler()).iterator());
        when(loader.load(ClientFactory.class)).thenReturn(Arrays.<ClientFactory>asList(new TestClientFactory("A")).iterator());
        RemoteDriveBootstrap remoteDriveBootstrap = new RemoteDriveBootstrap(loader, tmpPath);
        remoteDriveBootstrap.load();

        Configuration configuration = new Configuration();
        configuration.writeString("string-default-scope", "string1");
        configuration.writeString("scope1", "string-scope", "string2");
        configuration.writeBoolean("boolean-default-scope", true);
        configuration.writeBoolean("scope2", "boolean-scope", false);
        remoteDriveBootstrap.getConfigurations().add(configuration);
        remoteDriveBootstrap.saveConfiguration();

        TestCase.assertEquals(new Scanner(expectedConfigurationPath.toFile()).nextLine(), new Scanner(tmpPath.toFile()).nextLine());
    }
}
