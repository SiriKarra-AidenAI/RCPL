package com.rcpl.platform.intake;

import java.util.ArrayList;
import java.util.List;

import com.rcpl.platform.intake.IntakeDtos.PdfMatchDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Component;

/**
 * Deterministic (no AI) case-insensitive substring search over a PDF's extracted text, mapping
 * each match back onto a bounding box expressed as 0..1 fractions of the page's media-box size —
 * the shape RCPL-Angular-Frontend/src/app/components/PdfHighlightViewer.html expects, so it can
 * scale a highlight box against that page's actual rendered CSS pixel size.
 *
 * <p>Best-effort by design: a page's text is assembled purely from {@link PDFTextStripper}'s
 * {@code writeString} callbacks with nothing inserted between calls (no line/word separators),
 * so an occurrence split across two source lines with no space between them can be missed —
 * an accepted limitation, not a bug, since this only backs an optional "jump to source" highlight.
 */
@Component
public class PdfTextLocator {

    /** Finds every case-insensitive occurrence of {@code query} in {@code pdfBytes}'s text. */
    public List<PdfMatchDto> locate(byte[] pdfBytes, String query) {
        List<PdfMatchDto> matches = new ArrayList<>();
        if (pdfBytes == null || pdfBytes.length == 0 || query == null || query.isBlank()) {
            return matches;
        }
        String needle = query.toLowerCase();

        try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                PDPage page = doc.getPage(i);
                PDRectangle box = page.getMediaBox();
                double pageWidth = box.getWidth();
                double pageHeight = box.getHeight();
                if (pageWidth <= 0 || pageHeight <= 0) continue;

                PageTextCollector collector = new PageTextCollector();
                collector.setStartPage(i + 1);
                collector.setEndPage(i + 1);
                collector.getText(doc);

                String pageTextLower = collector.text.toString().toLowerCase();
                List<TextPosition> positions = collector.positions;

                int from = 0;
                int idx;
                while ((idx = pageTextLower.indexOf(needle, from)) >= 0) {
                    int end = idx + needle.length();
                    addMatch(matches, i, positions, idx, end, pageWidth, pageHeight);
                    from = end;
                }
            }
        } catch (Exception e) {
            // Not a valid/parseable PDF (e.g. a .csv/.txt upload) — best-effort feature, no matches.
            return new ArrayList<>();
        }
        return matches;
    }

    /** Maps a matched [start,end) character range back onto a normalized bounding box. */
    private static void addMatch(List<PdfMatchDto> out, int pageIndex, List<TextPosition> positions,
                                  int start, int end, double pageWidth, double pageHeight) {
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        boolean any = false;
        for (int i = start; i < end && i < positions.size(); i++) {
            TextPosition tp = positions.get(i);
            if (tp == null) continue;
            double x0 = tp.getXDirAdj();
            double x1 = x0 + tp.getWidthDirAdj();
            double y1 = tp.getYDirAdj();
            double y0 = y1 - tp.getHeightDir();
            minX = Math.min(minX, x0);
            maxX = Math.max(maxX, x1);
            minY = Math.min(minY, y0);
            maxY = Math.max(maxY, y1);
            any = true;
        }
        if (!any) return;
        out.add(new PdfMatchDto(pageIndex, minX / pageWidth, minY / pageHeight,
                (maxX - minX) / pageWidth, (maxY - minY) / pageHeight));
    }

    /** Accumulates one page's text plus a parallel per-character {@link TextPosition} list. */
    private static final class PageTextCollector extends PDFTextStripper {
        private final StringBuilder text = new StringBuilder();
        private final List<TextPosition> positions = new ArrayList<>();

        PageTextCollector() throws java.io.IOException {
            super();
        }

        @Override
        protected void writeString(String string, List<TextPosition> textPositions) {
            // Deliberately not calling super — we only want the accumulated text + positions,
            // not the normal stripper output.
            text.append(string);
            positions.addAll(textPositions);
        }
    }
}
