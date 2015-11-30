package hu.ektf.iot.openbiomapsapp.helper;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import hu.ektf.iot.openbiomapsapp.object.Note;
import timber.log.Timber;

/**
 * Created by Pádi on 2015. 11. 26..
 */
public class ExportHelper {
    public static File createFolderToNote(String date) throws Exception {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "openbiomaps" +
                File.separator + date);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            Timber.d("Folder successfully created.");
        } else {
            throw new Exception("FolderNotCreated");
        }
        return folder;
    }

    public static void createFileToFolder(String filename, String content, File folder) {
        try {
            File root = new File(folder.getPath());
            if (!root.exists()) {
                root.mkdirs();
            }
            File myFile = new File(root, filename);
            FileWriter writer = new FileWriter(myFile);
            writer.append(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyAttachmentsToFolder(Note note, File folder) {

        ArrayList<String> attachments = new ArrayList<>();
        attachments.addAll(note.getImagesList());
        attachments.addAll(note.getSoundsList());

        for (String file:attachments) {
            String sourcePath = file;
            File source = new File(sourcePath);
            String destinationPath = folder.getPath() +  File.separator + source.getName();
            File destination = new File(destinationPath);
            try
            {
                FileUtils.copyFile(source, destination);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            Log.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                Log.d("", "Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            Log.e("", ioe.getMessage());
        }
    }

    public static void exportNote(Note note) throws Exception {
        File folder = ExportHelper.createFolderToNote(String.valueOf(note.getDate()));
        if(note.getComment() !=null) ExportHelper.createFileToFolder("note.txt", note.getComment(), folder);
        ExportHelper.createFileToFolder("geometry.wkt", GeometryConverter.LocationToString(note.getLocation()), folder);
        ExportHelper.copyAttachmentsToFolder(note, folder);
        ExportHelper.zipFolder(folder.getPath(), Environment.getExternalStorageDirectory() + "/openbiomaps/" + note.getDate() + ".zip");
        deleteFolder(note.getDate());
    }

    public static void deleteFolder(String folderName) throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory()+"/openbiomaps/" + folderName);
        FileUtils.deleteDirectory(dir);
    }
}
