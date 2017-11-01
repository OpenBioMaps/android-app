package hu.ektf.iot.openbiomapsapp.helper;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import hu.ektf.iot.openbiomapsapp.model.FormData;
import timber.log.Timber;

public class ExportHelper {
    public static File createFolderToNote(String date) throws Exception {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "openbiomaps" +
                File.separator + date);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            Timber.d("Folder successfully created." + folder.getName());
        } else {
            throw new Exception("FolderNotCreated" + folder.getName());
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
            Timber.d("createFileToFolder failed: " + filename);
        }
    }

    public static void copyAttachmentsToFolder(FormData formData, File folder) {
        for (String file : formData.getFiles()) {
            String sourcePath = file;
            File source = new File(sourcePath);
            String destinationPath = folder.getPath() + File.separator + source.getName();
            File destination = new File(destinationPath);
            try {
                FileUtils.copyFile(source, destination);
                Timber.d("copy attachments file copy success: " + destinationPath);
            } catch (IOException e) {
                e.printStackTrace();
                Timber.d("copy attachments failed: " + sourcePath);
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
            Timber.d("zipping folder failed:" + inputFolderPath + " to: " + outZipPath);
        }
    }

    public static void exportNote(FormData note) throws Exception {
        File folder = ExportHelper.createFolderToNote(String.valueOf(note.getDate()));
        if (note.getJson() != null)
            ExportHelper.createFileToFolder("data.json", note.getJson(), folder);
        ExportHelper.copyAttachmentsToFolder(note, folder);
        ExportHelper.zipFolder(folder.getPath(), Environment.getExternalStorageDirectory() + "/openbiomaps/" + note.getDate() + ".zip");
        deleteFolder(note.getDate().toString());
        Timber.d("exportnote finished" + note.getDate().toString());
    }

    public static void deleteFolder(String folderName) throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/openbiomaps/" + folderName);
        FileUtils.deleteDirectory(dir);
        Timber.d("deleteFolder finished" + folderName);
    }
}
