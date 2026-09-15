package org.colston.kicks.render;

import org.colston.kicks.document.KicksDocument;
import org.colston.printpdf.PDFBoxDocumentRenderer;
import org.colston.printpdf.PDFBoxPrintFontMap;

import java.awt.*;

public class KicksDocumentRenderer implements PDFBoxDocumentRenderer {
    private final PageRenderer pageRenderer;

    private KicksDocumentRenderer(KicksDocument kicksDocument) {
        this.pageRenderer = PageRenderer.create(kicksDocument);
    }

    public static KicksDocumentRenderer create(KicksDocument kicksDocument) {
        return new KicksDocumentRenderer(kicksDocument);
    }

    public KicksDocumentRenderer romaji(boolean romaji) {
        pageRenderer.romaji(romaji);
        return this;
    }

    public KicksDocumentRenderer includeVersion(boolean includeVersion) {
        pageRenderer.includeVersion(includeVersion);
        return this;
    }

    public KicksDocumentRenderer withNoteKanjiRenderer(NoteKanjiRenderer noteKanjiRenderer) {
        pageRenderer.withNoteKanjiRenderer(noteKanjiRenderer);
        return this;
    };

    @Override
    public float getWidth(int pageIndex) {
        return pageRenderer.getTotalCanvasWidth(pageIndex);
    }

    @Override
    public float getHeight(int pageIndex) {
        return pageRenderer.getCanvasHeight();
    }

    @Override
    public void render(Graphics2D g2, int pageIndex) {
        pageRenderer.doPaint(g2, pageIndex);
    }

    @Override
    public PDFBoxPrintFontMap getFontMap() {
        return RendererResources.createFontMap();
    }

    @Override
    public int getNumberOfPages() {
        return pageRenderer.getNumberOfPages();
    }

    @Override
    public void asImage(boolean asImage) {
        pageRenderer.useMinimumCanvas(asImage);
    }
}
