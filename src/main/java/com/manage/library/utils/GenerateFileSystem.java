//package com.manage.library.utils;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import java.io.BufferedReader;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class GenerateFileSystem {
//
//    public static String convertFolderToJson() {
//        InputStream inputStream = GenerateFileSystem.class.getClassLoader().getResourceAsStream("subject");
//        if (inputStream == null) {
//            return "Resource folder not found.";
//        }
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            JsonObject result = new JsonObject();
//
//            // Read each line in the folder (assuming it contains file/folder names)
//            String folderName;
//
//            while ((folderName = reader.readLine()) != null) {
//                URL folderUrl = GenerateFileSystem.class.getClassLoader().getResource("subject/" + folderName);
//                try {
//                    System.err.println("NAME: " + folderUrl.toURI().toString());
//                } catch (URISyntaxException ex) {
//
//                }
//                if (folderUrl == null) {
//                    System.out.println("Folder not found: " + folderName);
//                    continue;
//                }
//
//                File subjectFolder = null;
//                try {
//                    subjectFolder = new File(folderUrl.toURI()
//                    );
//                } catch (URISyntaxException ex) {
//                    Logger.getLogger(GenerateFileSystem.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                JsonArray subjectsArray = new JsonArray();
//
//                // Check if it's a directory before processing
//                if (subjectFolder.isDirectory()) {
////                    System.out.println("Dô đây");
//                    convertAndAddFiles(subjectFolder, subjectsArray);
//                    result.add(subjectFolder.getName(), subjectsArray);
//                } else {
//                    System.out.println("Not a directory: " + folderName);
//                }
////                System.out.println("Log: " + folderName);
//            }
//            reader.close();
//
////            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            Gson gson = new GsonBuilder().setLenient().create();
//
//            return gson.toJson(result);
//        } catch (IOException e) {
//            Logger.getLogger(GenerateFileSystem.class.getName()).log(Level.SEVERE, null, e);
//        }
//        return "Not found";
//    }
//
//    private static void convertAndAddFiles(File folder, JsonArray jsonArray) {
//        if (folder.isDirectory()) {
//            for (File topicFolder : folder.listFiles()) {
//                if (topicFolder.isDirectory()) {
//                    JsonObject topicObject = new JsonObject();
//                    JsonArray filesArray = new JsonArray();
//                    convertAndAddFiles(topicFolder, filesArray);
//
//                    for (File file : topicFolder.listFiles()) {
//                        JsonObject fileObject = new JsonObject();
//
//                        if (file.isDirectory()) {
//                            // Handle nested folders if needed
//                            JsonArray nestedFilesArray = new JsonArray();
//                            convertAndAddFiles(file, nestedFilesArray);
//
//                        } else {
//                            try {
//                                String fullFileName = file.getName();
//                                String fileType = fullFileName.lastIndexOf(".") >= 1 ? (fullFileName.substring(fullFileName.lastIndexOf(".") + 1)) : "";
//                                String fileName = fullFileName.lastIndexOf(".") >= 1 ? (fullFileName.substring(0, fullFileName.lastIndexOf("."))) : "";
//                                fileObject.addProperty("name", fileName);
//                                fileObject.addProperty("type", getFileType(fileType));
//                                fileObject.addProperty("url", file.getAbsolutePath());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        if (!fileObject.isJsonNull() && fileObject.size() > 0) {
//                            filesArray.add(fileObject);
//                        }
//                    }
//
//                    topicObject.add(topicFolder.getName(), filesArray);
//                    jsonArray.add(topicObject);
//                }
//            }
//        }
//    }
//
//    private static String getFileType(String fileName) {
//        switch (fileName.toLowerCase()) {
//            case "jpg":
//            case "jpeg":
//            case "png":
//            case "gif":
//                return "Picture";
//            case "pdf":
//            case "doc":
//            case "docx":
//                return "Document";
//            case "mp4":
//            case "avi":
//            case "mkv":
//                return "Video";
//            default:
//                return "Unknown";
//        }
//    }
//}
