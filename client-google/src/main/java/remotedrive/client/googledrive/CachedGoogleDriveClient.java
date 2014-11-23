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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import remotedrive.core.File;
import remotedrive.core.exception.ClientRequestException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Specialize GoogleDriveClient to handle internal caching.
 */
public class CachedGoogleDriveClient extends GoogleDriveClient
{
    /**
     * Children cache.
     */
    private Cache<String, File[]> childrenCache;

    /**
     * File cache.
     */
    private Cache<String, File> fileCache;

    /**
     * File content cache.
     */
    private Cache<String, byte[]> contentCache;

    /**
     * Initialize a new instance Cached google drive client.
     */
    public CachedGoogleDriveClient()
    {
        this.childrenCache = CacheBuilder
            .newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
        this.fileCache = CacheBuilder
            .newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
        this.contentCache = CacheBuilder
            .newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File[] retrieveChildren(final String parentPath)
    {
        try
        {
            return childrenCache.get(parentPath, new Callable<File[]>()
            {
                @Override
                public File[] call()
                {
                    // Call the super type implementation
                    File[] children = CachedGoogleDriveClient.super.retrieveChildren(parentPath);

                    // For each child, put it in the cache
                    for(File child : children)
                    {
                        fileCache.put(child.getAbsolutePath(), child);
                    }

                    // Return the children
                    return children;
                }
            });
        }

        // Rethrow exception is occur
        catch (ExecutionException e)
        {
            throw new ClientRequestException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File retrieveDriveFile(final String filePath)
    {
        try
        {
            return fileCache.get(filePath, new Callable<File>()
            {
                @Override
                public File call()
                {
                    // Call the super type implementation
                    // TODO: Handle caching
                    return CachedGoogleDriveClient.super.retrieveDriveFile(filePath);
                }
            });
        }

        // Rethrow exception is occur
        catch (ExecutionException e)
        {
            throw new ClientRequestException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] retrieveDriveFileContent(final String filePath)
    {
        try
        {
            return contentCache.get(filePath + "-content", new Callable<byte[]>()
            {
                @Override
                public byte[] call()
                {
                    // Call the super type implementation
                    // TODO: Handle caching
                    return CachedGoogleDriveClient.super.retrieveDriveFileContent(filePath);
                }
            });
        }

        // Rethrow exception is occur
        catch (ExecutionException e)
        {
            throw new ClientRequestException(e.getMessage(), e);
        }
    }
}
