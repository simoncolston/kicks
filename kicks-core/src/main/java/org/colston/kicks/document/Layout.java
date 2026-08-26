package org.colston.kicks.document;

import jakarta.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum Layout {
    LANDSCAPE_11COL_12CELL(false, 11, 12);

    final boolean portrait;
    final int numberOfColumns;
    final int cellsPerColumn;

    Layout(boolean portrait, int numberOfColumns, int cellsPerColumn) {
        this.portrait = portrait;
        this.numberOfColumns = numberOfColumns;
        this.cellsPerColumn = cellsPerColumn;
    }

    public boolean isPortrait() {
        return portrait;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public int getCellsPerColumn() {
        return cellsPerColumn;
    }
}
