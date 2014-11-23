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

package remotedrive.client.googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.FileList;
import remotedrive.core.Drive;
import remotedrive.core.File;
import remotedrive.core.exception.ClientAuthenticationException;
import remotedrive.core.exception.ClientRequestException;
import remotedrive.core.spi.Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Google drive implementation.
 * Google drive FS structure is represented as a graph that is incompatible with a regular hierarchical FS structure.
 * In order to provide acceptable performances, this implementation has to maintain a matching between paths and ids.
 * However it's not necessary to manage caching in this implementation and focus on naive execution where all call are
 * independent.
 */
public class GoogleDriveClient implements Client
{
    /**
     * Google drive service.
     */
    private com.google.api.services.drive.Drive driveService;

    /**
     * Google drive disk information.
     */
    private Drive drive;

    /**
     * The credentials.
     */
    private Credential credential;

    /**
     * Paths to ids index.
     * Keys: paths.
     * Values: ids.
     * Since many clients can access and modify the FS structure concurrently, it's unsafe to assume that all indexed
     * pair actually exist.
     */
    private Map<String, String> pathsToIdsIndex;

    public GoogleDriveClient()
    {
        pathsToIdsIndex = new HashMap<String, String>();
    }

    /**
     * Authenticates to Google Drive account.
     * @param username The google account email address.
     * @param password Always empty for google drive authentication
     */
    public void authenticate(String username, char[] password)
    {
        // Arguments validation
        if (null == username || username.length() == 0)
        {
            throw new IllegalArgumentException("Username has to be provided");
        }

        try
        {
            // Initialize internal state
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            // Initialize the client secrets
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                jsonFactory,
                new InputStreamReader(GoogleDriveClient.class.getResourceAsStream("/client_secrets.json")));

            // Initialize authorization flow
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                jsonFactory,
                clientSecrets,
                Arrays.asList(DriveScopes.DRIVE))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(System.getProperty("user.home"), ".store/cloud_storage")))
                .setApprovalPrompt("auto")
                .setAccessType("offline")
                .build();

            // Initialize the credentials for installed application
            credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(username);

            // Initialize the drive service
            driveService = new com.google.api.services.drive.Drive.Builder(
                httpTransport,
                jsonFactory,
                credential)
                .setApplicationName("CloudStorage")
                .build();

            // Retrieve about resource in order to initialize disk information
            com.google.api.services.drive.Drive.About.Get get = driveService.about().get();
            About about = get.execute();

            // Add root in the FS index
            pathsToIdsIndex.put("", about.getRootFolderId());

            // Initialize disk information
            drive = new Drive(about.getQuotaBytesTotal(), about.getQuotaBytesUsed());
        }
        catch (Exception e)
        {
            throw new ClientAuthenticationException(String.format("Unable to authentication as %s", username), e);
        }
    }

    /**
     * Retrieves disk information.
     * Google drive implementation initializes it at authentication.
     * Requires an authenticated client.
     * @return Disk information
     */
    public Drive retrieveDiskInformation()
    {
        // Check current state
        ensureAuthenticated();

        // Return disk information
        return drive;
    }

    /**
     * Retrieve children.
     * @param parentPath The parent file.
     * @return The children.
     */
    public File[] retrieveChildren(String parentPath)
    {
        // Check current state
        ensureAuthenticated();

        try
        {
            // Lookup the parent id form a path
            // TODO: Handle the case where the id doesn't exist in the index
            String parentId = pathsToIdsIndex.get(parentPath);
            if (null == parentId)
            {
                return null;
            }

            // Retrieve the non trashed children
            com.google.api.services.drive.Drive.Files.List list = driveService.files().list();
            list.setQ(String.format("trashed = false and '%s' in parents", parentId));

            // Execute and wrap response
            FileList fileList = list.execute();
            List<File> files = new ArrayList<File>();
            for(com.google.api.services.drive.model.File file : fileList.getItems())
            {
                String fullPath = 0 == parentPath.length() ? file.getTitle() : String.format("%s/%s", parentPath, file.getTitle());
                pathsToIdsIndex.put(fullPath, file.getId());
                files.add(new File(
                    file.getId(),
                    file.getTitle(),
                    fullPath,
                    file.getMimeType().equals("application/vnd.google-apps.folder"),
                    null != file.getFileSize() ? file.getFileSize() : 0,
                    file.getDownloadUrl(),
                    file.getCreatedDate().getValue()));
            }

            // Return the response
            return files.toArray(new File[files.size()]);
        }
        catch (IOException e)
        {
            throw new ClientRequestException("An error happened during data recuperation", e);
        }
    }

    /**
     * Retrieve a file.
     * @param filePath The absolute path.
     * @return The file.
     */
    public File retrieveDriveFile(final String filePath)
    {
        // Check current state
        ensureAuthenticated();

        try
        {
            // Lookup the file id from a path
            // TODO: Handle the case where the id doesn't exist in the index
            String fileId = pathsToIdsIndex.get(filePath);
            if (null == fileId)
            {
                return null;
            }

            // Build the request
            com.google.api.services.drive.Drive.Files.Get get = driveService.files().get(fileId);

            // Execute and wrap the request asynchronously
            com.google.api.services.drive.model.File file = get.execute();
            return new File(
                    file.getId(),
                    file.getTitle(),
                    filePath,
                    file.getMimeType().equals("application/vnd.google-apps.folder"),
                    null != file.getFileSize() ? file.getFileSize() : 0,
                    file.getDownloadUrl(),
                    file.getCreatedDate().getValue());
        }
        catch (IOException e)
        {
            throw new ClientRequestException("An error happened during data recuperation", e);
        }
    }

    @Override
    public byte[] retrieveDriveFileContent(String filePath)
    {
        // Check current state
        ensureAuthenticated();

        try
        {
            // Lookup the file id from a path
            // TODO: Handle the case where the id doesn't exist in the index
            String fileId = pathsToIdsIndex.get(filePath);
            if (null == fileId)
            {
                return null;
            }

            System.out.println("Load " + filePath);

            com.google.api.services.drive.Drive.Files.Get get = driveService.files().get(fileId);
            InputStream is = get.executeMediaAsInputStream();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[1024];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            return buffer.toByteArray();

        }
        catch (IOException e)
        {
            throw new ClientRequestException("An error happened during data recuperation", e);
        }
    }

    /**
     * Ensure that the client is authenticated.
     */
    private void ensureAuthenticated()
    {
        // Check authentication
        if (null == drive)
        {
            throw new IllegalStateException("Cannot retrieve disk information prior to authentication");
        }
    }
}