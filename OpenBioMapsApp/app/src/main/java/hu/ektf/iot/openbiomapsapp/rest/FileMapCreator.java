package hu.ektf.iot.openbiomapsapp.rest;

import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.mime.TypedFile;

/**
 * Created by szugyi on 20/11/15.
 */
public class FileMapCreator {
    public static Map<String, TypedFile> createFileMap(List<String> files){
        Map<String, TypedFile> fileMap = new HashMap<String, TypedFile>();

        for(int i = 0; i < files.size(); i++){
            String filePath = files.get(i);
            String mimeType = getMimeType(filePath);
            File file = new File(filePath);
            TypedFile typedImage = new TypedFile(mimeType, file);
            fileMap.put(String.format(BioMapsService.PARAM_FILE_ARRAY_FORMAT, i), typedImage);
        }

        return fileMap;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
