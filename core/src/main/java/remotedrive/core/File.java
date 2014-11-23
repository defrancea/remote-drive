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

/**
 * Represents a file.
 */
public class File
{
    /**
     * File id.
     */
    public String id;

    /**
     * File title.
     */
    public String title;

    /**
     * File absolute Path.
     */
    public String absolutePath;

    /**
     * File is directory.
     */
    public boolean isDirectory;

    /**
     * File size.
     */
    public long size;

    /**
     * File download url.
     */
    public String downloadUrl;

    /**
     * File creation time.
     */
    public long creationTime;

    /**
     * Initializes a file representation.
     * @param id The file id.
     * @param title The file title.
     * @param absolutePath The file absolute path.
     * @param isDirectory Is directory.
     * @param size The file size.
     * @param downloadUrl The download Url.
     * @param creationTime The creation time.
     */
    public File(
            String id,
            String title,
            String absolutePath,
            boolean isDirectory,
            long size,
            String downloadUrl,
            long creationTime)
    {
        this.id = id;
        this.title = title;
        this.absolutePath = absolutePath;
        this.isDirectory = isDirectory;
        this.size = size;
        this.downloadUrl = downloadUrl;
        this.creationTime = creationTime;
    }

    /**
     * Gets the file id.
     * @return The file id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Gets the file title.
     * @return The file title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Gets the file absolute path.
     * @return The file absolute path.
     */
    public String getAbsolutePath()
    {
        return absolutePath;
    }

    /**
     * Is the file a directory.
     * @return true if the file is a directory, false otherwise.
     */
    public boolean isDirectory()
    {
        return isDirectory;
    }

    /**
     * Gets the file size.
     * @return The file size.
     */
    public long getSize()
    {
        return size;
    }

    /**
     * Gets the file download Url.
     * @return The file Url.
     */
    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    /**
     * Gets the file creation time.
     * @return The file creation time.
     */
    public long getCreationTime() {
        return creationTime;
    }
}
