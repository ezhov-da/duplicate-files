package ru.ezhov.duplicate.files.gui.application.analyse;

import ru.ezhov.duplicate.files.gui.application.MarkToDeleteListener;
import ru.ezhov.duplicate.files.gui.application.UnmarkToDeleteListener;
import ru.ezhov.duplicate.files.gui.application.analyse.load.DuplicateAnalyseCompleteListener;
import ru.ezhov.duplicate.files.gui.application.analyse.load.LoadStampFilePanel;
import ru.ezhov.duplicate.files.gui.application.analyse.result.AnalyseResultTreeTablePanel;
import ru.ezhov.duplicate.files.gui.application.delete.UploadPreparedDeleteFileListener;
import ru.ezhov.duplicate.files.gui.application.repository.ThumbnailsRepository;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.DuplicateId;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.FilePath;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnalysePanel extends JPanel implements DuplicateAnalyseCompleteListener, UnmarkToDeleteListener, MarkToDeleteListener, UploadPreparedDeleteFileListener {

    private List<UnmarkToDeleteListener> unmarkToDeleteListeners = new ArrayList<>();
    private List<MarkToDeleteListener> markToDeleteListeners = new ArrayList<>();
    private JPanel panelMock;
    private LoadStampFilePanel loadStampFilePanel;
    private AnalyseResultTreeTablePanel analyseResultTreeTablePanel;
    private JPanel analysePanel;
    private ThumbnailsRepository thumbnailsRepository;
    private Set<FilePath> cacheFilePaths = new HashSet<>();

    public AnalysePanel(ThumbnailsRepository thumbnailsRepository) throws Exception {
        this.thumbnailsRepository = thumbnailsRepository;
        init();
    }

    private void init() throws Exception {
        this.loadStampFilePanel = new LoadStampFilePanel();
        loadStampFilePanel.addCompleteListener(this);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder("Fingerprint analysis and file selection for deletion")
        ));

        panelMock = new JPanel(new BorderLayout());
        JLabel labelMock = new JLabel("Select the fingerprint file and run duplicate analysis");
        labelMock.setHorizontalTextPosition(SwingConstants.CENTER);
        labelMock.setHorizontalAlignment(SwingConstants.CENTER);
        panelMock.add(labelMock, BorderLayout.CENTER);

        analysePanel = new JPanel(new BorderLayout());
        analysePanel.add(loadStampFilePanel, BorderLayout.NORTH);
        analysePanel.add(panelMock, BorderLayout.CENTER);
        add(analysePanel, BorderLayout.CENTER);
    }

    public void initAnalyseResultTreeTablePanel(Map<DuplicateId, List<FilePath>> duplicateIdListMap) {
        AnalyseResultTreeTablePanel newPanelAnalyseResult = new AnalyseResultTreeTablePanel(duplicateIdListMap, thumbnailsRepository);
        if (panelMock != null) {
            SwingUtilities.invokeLater(() -> {
                analysePanel.remove(panelMock);
                panelMock = null;
            });
        }
        if (analyseResultTreeTablePanel != null) {
            SwingUtilities.invokeLater(() -> {
                analysePanel.remove(analyseResultTreeTablePanel);
                analyseResultTreeTablePanel = newPanelAnalyseResult;
                analysePanel.add(analyseResultTreeTablePanel, BorderLayout.CENTER);
                initCacheUpload();
                delegateListeners();
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                analyseResultTreeTablePanel = newPanelAnalyseResult;
                analysePanel.add(analyseResultTreeTablePanel, BorderLayout.CENTER);
                initCacheUpload();
                delegateListeners();
            });
        }

        SwingUtilities.invokeLater(() -> {
            analysePanel.revalidate();
            analysePanel.repaint();
        });
    }

    @Override
    public void complete(Map<DuplicateId, List<FilePath>> result) {
        SwingUtilities.invokeLater(() -> {
            int sumSize = 0;
            Collection<List<FilePath>> values = result.values();
            for (List<FilePath> filePaths : values) {
                sumSize += filePaths.size();
            }
            if (sumSize == result.size()) {
                JOptionPane.showMessageDialog(AnalysePanel.this, "No duplicates detected", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                AnalysePanel.this.initAnalyseResultTreeTablePanel(result);
            }
        });
    }

    @Override
    public void unmark(FilePath filePath) {
        if (analyseResultTreeTablePanel != null) {
            analyseResultTreeTablePanel.unmark(filePath);
        }
    }

    @Override
    public void mark(FilePath filePath) {
        if (analyseResultTreeTablePanel != null) {
            analyseResultTreeTablePanel.mark(filePath);
        }
    }

    @Override
    public void upload(List<FilePath> filePaths) {
        cacheFilePaths.addAll(filePaths);
        if (analyseResultTreeTablePanel != null) {
            initCacheUpload();
        }
    }

    private void initCacheUpload() {
        List<FilePath> paths = new ArrayList<>(cacheFilePaths);
        analyseResultTreeTablePanel.upload(paths);
    }

    public void addMarkToDeleteListener(MarkToDeleteListener markToDeleteListener) {
        markToDeleteListeners.add(markToDeleteListener);
    }

    public void addUnmarkToDeleteListener(UnmarkToDeleteListener unmarkToDeleteListener) {
        unmarkToDeleteListeners.add(unmarkToDeleteListener);
    }

    private void delegateListeners() {
        if (analyseResultTreeTablePanel != null) {
            for (MarkToDeleteListener markToDeleteListener : markToDeleteListeners) {
                analyseResultTreeTablePanel.addMarkToDeleteListener(markToDeleteListener);
            }
            for (UnmarkToDeleteListener unmarkToDeleteListener : unmarkToDeleteListeners) {
                analyseResultTreeTablePanel.addUnmarkToDeleteListener(unmarkToDeleteListener);
            }
        }
    }
}
