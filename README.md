# kicks
Kunkunshi Editor

## Build

    mvn clean package

## Run

### GUI editor

    java -jar kicks-app/target/kicks.jar
    java -jar kicks-app/target/kicks.jar [filename.kicks]

### Command line

Example: command line to create pdf file `<output directory>/<filename>.pdf`

    java -jar kicks-app/target/kicks.jar --cli --to-pdf --output-dir=<output directory> <filename>.[kicks|kicksabc]

`--cli`
: REQUIRED. Run on command line without GUI.  Must be first argument.

`--to-pdf`
: REQUIRED. Convert the document to pdf.

`--romaji-lyrics`
: OPTIONAL. Render the lyrics as romaji.

`--filename-suffix`
: OPTIONAL. Suffix to add to the output file name.
E.g. `--filename-suffix=-suffix` and the input filename is `mysong.kicks` the output file will be `mysong-suffix.pdf`

`--output-dir`
: REQUIRED. Output directory.

`--as-image`
: OPTIONAL. Create minimal area PDF image, rather than a PDF document with print media page sizes

Varargs
: Name of a single `.kicks` or `.kicksabc` file.

## `kicksabc` Text Notation

For those users who prefer to use a text editor 
rather than a GUI, [`kicksabc` text notation](doc/kicksabc.md) is available.