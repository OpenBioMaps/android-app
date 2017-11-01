package hu.ektf.iot.openbiomapsapp.upload;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.mime.TypedFile;
import timber.log.Timber;


public class FileMapCreator {
    public static Map<String, TypedFile> createFileMap(List<String>... fileLists) {
        Map<String, TypedFile> fileMap = new HashMap<String, TypedFile>();

        int count = 0;
        for (int i = 0; i < fileLists.length; i++) {
            List<String> files = fileLists[i];
            for (int j = 0; j < files.size(); j++) {
                String filePath = files.get(j);
                String mimeType = getMimeType(filePath);
                Timber.v(mimeType);
                File file = new File(filePath);
                TypedFile typedImage = new TypedFile(mimeType, file);
                fileMap.put(String.format(BioMapsService.PARAM_FILE_ARRAY_FORMAT, count), typedImage);
                count++;
            }
        }

        return fileMap;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (!TextUtils.isEmpty(extension)) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        else
        {
            int lastIndex = url.lastIndexOf(".");
            if(lastIndex>-1)
                extension = url.substring(lastIndex+1);
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
