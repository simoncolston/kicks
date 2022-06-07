TODO
====

##### Actions
  - [x] **1** Glass pane
  - [x] **1** Actions - input map and action map
  - [ ] **1** Enable/disable actions depending on canvas cursor position/selection/etc.
  - [x] **1** Move 'Lyric' action to the canvas (and not use a dialog!)
  - [x] **1** Combine Title and Tuning into one dialogue box
  - [x] **1** Prompt to save if document modified - when quitting
  - [x] **1** Prompt to save if document modified - when opening document
  - [ ]   2   New document action
  - [ ]   2   Save As... action
  
##### Document
  - [x] **1** Extended notes: see `_test.xml`
  - [x] **1** Slurs and chords
  - [ ]   3   Repeats with different head styles
  - [ ]   3   Multiple pages  (Put canvas panels into vertical box layout)
  - [ ]   3   Multiple songs per document ('song' element replaces a column on the canvas)
  - [ ]   3   'new line' element moves next note to top of next column
  - [ ]   3   'new page' element moves next note to top of next page
  - [x] **1** Change the file extension to .kicks
  - [ ]   5   Compress XML?
  - [x] **1** Change 'value' attribute on 'note' element to 'string' [1,2,3] and 'placement'
  - [ ]   5   Document attributes - author, version, date/time, etc.
  - [ ]   5   Highlight
  - [ ]   5   Import/Export Paul's JSON
  - [x] **1** Use JAXB for saving/loading document to XML

#### View
  - [ ]   2   Zoom
  - [ ]   3   Show cursor coordinates

#### Help
  - [ ]   5   Add help (using JavaHelp - is that still a thing?)
  - [x] **1** About box (taking version from the manifest file)
  - [ ] **1** Keyboard shortcuts

##### Selection
  - [x] **1** Mouse to move cursor on canvas
  - [ ] **1** Selection on canvas
  
##### Edit
  - [x] **1** Delete from canvas using delete key (and remove stupid 'delete if same' logic)
  - [ ] **1** Delete from canvas (*after* selection)
  - [ ] **1** Cut/copy/paste
  - [x] **1** Undo/redo
  
##### Printing
  - [x] **1** Save as PDF (using Apache PDF implemented through Java Print API) 
  - [x] **1** **OBSOLETE** Save as PDF (using printer)
  - [x]   3   **OBSOLETE** Apache PDFBox to rotate PDF after printing due to bug in Java Print
  - [x] **1** Bundle nice font with the application
  
##### Input
  - [x] **1** Input notes using mouse
  - [x] **1** Automatically switch IME when inputting lyrics
  - [x] **1** Restrict lyric input to 2 chars
  - [x]   2   Cursor advance selection: None, half cell, full cell 
  
##### Settings
  - [x] **1** Refactor settings into Main class (includes moving UIFactory into Main)
  - [ ]   2   Settings stored using Preferences (or ~/.kicks file ?)

##### Build
  - [x] **1** Upgrade to Java 17
  - [ ] **1** Create executable
