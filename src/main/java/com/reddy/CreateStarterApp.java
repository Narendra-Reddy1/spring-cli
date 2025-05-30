package com.reddy;

import com.reddy.utils.CliUtils;
import picocli.CommandLine;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@CommandLine.Command(name = "create-starter-app", description = "Create a spring boot starter project")
public class CreateStarterApp implements Callable<Integer> {


    @Override
    public Integer call() throws Exception {

        CliUtils.createScreen();
        String buildTool = CliUtils.selectOption("Build Tool", new String[]{"Maven", "Gradle"});
        if (buildTool == null) return 1;
        String springVersion = CliUtils.selectOption("Spring version", new String[]{"3.4.6", "3.5.0", "3.5.1 (SNAPSHOT)", "4.0.0 (SNAPSHOT)"});
        if (springVersion == null) return 1;
        String javaVersion = CliUtils.selectOption("Java version", new String[]{"17", "21", "24"});
        if (javaVersion.equals("24") && springVersion.startsWith("3.")) {
            System.out.println("Java 24 is not supported with Spring Boot 3.x. Please select Java 17 or 21.");
            javaVersion = CliUtils.selectOption("Java version", new String[]{"17", "21", "24"});
//            return 1;
        }
        String packagingType = CliUtils.selectOption("Packaging", new String[]{"jar", "war"});
        if (packagingType == null) return 1;
        String packageName = CliUtils.takeInput("Package name");
        String artifactId = CliUtils.takeInput("Artifact Id");
        String language = "java";
        CliUtils.stopScreen();

        String deps = "web,data-jpa,postgresql";
        String baseDir = artifactId;//just for readability’s sake
//        String payload = "{\n" +
//                "    \"type\":\"" + buildTool + "-project\",\n" +
//                "    \"language\": \"" + language + "\",\n" +
//                "    \"bootVersion\": \"" + springVersion + "\",\n" +
//                "    \"baseDir\": \"" + baseDir + "\",\n" +
//                "    \"groupId\": \"" + packageName + "\",\n" +
//                "    \"artifactId\": \"" + artifactId + "\",\n" +
//                "    \"name\": \"" + artifactId + "\",\n" +
//                "    \"packageName\": \"" + packageName + "\",\n" +
//                "    \"packaging\": \"" + packagingType + "\",\n" +
//                "    \"javaVersion\": " + javaVersion + "\n" +
//                "}";
        String payload = String.format("type=%s-project&language=java&bootVersion=%s&baseDir=%s&groupId=%s&artifactId=%s" + "&name=%s&packageName=%s&packaging=%s&javaVersion=%s&dependencies=%s", buildTool.toLowerCase(), springVersion, baseDir, packageName, artifactId, artifactId, packageName, packagingType, javaVersion, deps);
        downloadProject(payload, baseDir);
        return 0;
    }


    public void downloadProject(String payload, String baseDir) throws IOException {
        URL url = new URL("https://start.spring.io/starter.zip");
        System.out.println(url.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

        try (OutputStream os = conn.getOutputStream()) {
            var input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        Path zipPath = Paths.get(baseDir + ".zip");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

            try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(zipPath.toFile())) {
                in.transferTo(out);
            }
        } else {
            System.out.println("HTTP Error: " + conn.getResponseCode() + " " + conn.getResponseMessage());
            try (InputStream errorStream = conn.getErrorStream()) {
                if (errorStream != null) {
                    String errorMessage = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Error details: " + errorMessage);
                } else {
                    System.out.println("No error details available.");
                }
            }
            //UNZIP HERE
//        unZip(zipPath, Paths.get("."));
            conn.disconnect();
//        Files.delete(zipPath);

        }
    }

    public void unZip(Path zipPath, Path desDir) throws IOException {
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path newPath = desDir.resolve(entry.getName());
                if (entry.isDirectory()) Files.createDirectory(newPath);
                else {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zipIn, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipIn.closeEntry();
            }
        }
    }

}
