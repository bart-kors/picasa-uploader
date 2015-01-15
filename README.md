# picasa-uploader

## About

Java FX application to upload pictures and movies to picasa (google+)
Select the album and drag and drop the files to upload.

## How to build and run

requires the latest 1.8 java version

```
mvn package exec:java
```

To build a mac osx application first download and install [appbundler-plugin](https://github.com/federkasten/appbundler-plugin) then run:

```
mvn package appbundle:bundle
```


