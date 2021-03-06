package io.github.tavisco.rvglassistant.utils;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import io.github.tavisco.rvglassistant.objects.BaseItem;
import io.github.tavisco.rvglassistant.objects.CarItem;
import io.github.tavisco.rvglassistant.objects.Constants;
import io.github.tavisco.rvglassistant.objects.ItemType;
import io.github.tavisco.rvglassistant.objects.LevelItem;

/**
 * Created by Tavisco on 29/04/18.
 */
public class ItemParser {

    public static BaseItem parse(File itemFile, String basePath, ItemType itemType){
        BaseItem item = null;
        String itemPath = itemFile.getPath();
        String folderName = itemPath.substring(itemPath.lastIndexOf("/"));

        if (itemType == ItemType.CAR){
            item = new CarItem();
        } else if (itemType == ItemType.LEVEL){
            item = new LevelItem();
        } else {
            return null;
        }

        item.setType(itemType);
        item.setBasePath(basePath);
        item.setItemPath(folderName);
        String parameterFile = "";

        if (itemType == ItemType.CAR){
            parameterFile = Constants.CAR_PARAMETER_FILE_NAME;
        } else if (itemType == ItemType.LEVEL) {
            parameterFile = folderName + ".inf";
        }

        File infoFile = new File(itemPath + File.separator + parameterFile);

        if (!infoFile.isFile() || !infoFile.canRead()) {
            Log.d(Constants.TAG, "PARSER: Error reading parameter file for " + folderName
                    + ". Path:\n" + itemPath + File.separator + parameterFile);
            return null;
        }

        String wholeParamLine = "";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                try (Stream<String> lines = Files.lines(infoFile.toPath())) {
                    wholeParamLine = lines.skip(itemType.getTypeParameterNameLine()).findFirst()
                            .get();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Scanner scanner = null;
            try {
                scanner = new Scanner(infoFile).useDelimiter("\n");
                int counter = 0;
                while (scanner.hasNext() && counter <=itemType.getTypeParameterNameLine()) {
                    if (counter == itemType.getTypeParameterNameLine()){
                        wholeParamLine = scanner.next();
                    } else {
                        counter++;
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //Now I use REGEX to extract the name of the item
        Pattern p = Pattern.compile(itemType.getTypeRegex());
        Matcher m = p.matcher(wholeParamLine);
        if (m.find()) {
            item.setName(m.group(0).replace(itemType.getTypeReplacer(), ""));
        }

        return item;
    }
}
