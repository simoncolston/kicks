# kicks
Kunkunshi Editor

## Build

    mvn clean package

## Run

GUI editor

    java -jar target/kicks.jar
    java -jar target/kicks.jar [filename.kicks]

Command line to create pdf file `<output directory>/<filename>.pdf`

    java -jar target/kicks.jar --cli --to-pdf --output-dir=<output directory> <filename>.kicks

## `kicksabc` Text Notation

For those users who prefer to use a text editor 
rather than a GUI, [`kicksabc` text notation](doc/kicksabc.md) is available.