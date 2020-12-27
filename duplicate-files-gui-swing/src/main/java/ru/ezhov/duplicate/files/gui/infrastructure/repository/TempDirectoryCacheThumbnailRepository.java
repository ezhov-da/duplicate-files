package ru.ezhov.duplicate.files.gui.infrastructure.repository;

import net.coobird.thumbnailator.Thumbnails;
import ru.ezhov.duplicate.files.gui.application.repository.ThumbnailsRepository;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.FilePath;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TempDirectoryCacheThumbnailRepository implements ThumbnailsRepository {
    private static final Logger LOG = Logger.getLogger(TempDirectoryCacheThumbnailRepository.class.getName());
    private static final String PATH_TO_STORE = System.getProperty("java.io.tmpdir") + File.separator + ".duplicate-thumbnail-store";
    private static final String PATH_TO_DEFAULT_IMAGE = "/default-image-150x150.jpg";
    private static final String PATH_TO_NOT_FOUND_IMAGE = "/not-found-image.png";
    private Map<String, BufferedImage> cacheStore = new HashMap<>();
    private Map<String, String> cacheStamps = new HashMap<>();
    private Set<String> errors = new HashSet<>();
    private Set<String> notFound = new HashSet<>();

    private BufferedImage defaultImage = null;
    private BufferedImage notFoundImage = null;

    public TempDirectoryCacheThumbnailRepository() {
        init();
    }

    private void init() {
        File file = new File(PATH_TO_STORE);
        file.mkdirs();
        if (file.exists()) {
            LOG.log(Level.CONFIG, "method=init stampedOn=\"thumbnail file storage created ''{0}''\"", file);
        }
    }

    @Override
    public synchronized BufferedImage by(FilePath filePath) {
        BufferedImage bufferedImage = null;
        String path = filePath.path();
        if (notFound.contains(path)) {
            LOG.log(Level.CONFIG, "method=by stampedOn=\"file ''{0}'' not found\"", path);
            try {
                bufferedImage = notFoundImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (errors.contains(path)) {
                LOG.log(Level.CONFIG, "method=by stampedOn=\"errors for file ''{0}''\"", path);
                try {
                    bufferedImage = defaultImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    File fileCheckOriginalExist = new File(path);
                    if (!fileCheckOriginalExist.exists()) {
                        LOG.log(Level.CONFIG, "method=by stampedOn=\"error creating thumbnails for ''{0}''\"", path);
                        notFound.add(path);
                        bufferedImage = notFoundImage();
                    } else {
                        if (cacheStore.containsKey(path)) {
                            LOG.log(Level.CONFIG, "method=by stampedOn=\"image for ''{0}'' retrieved from cache\"", path);
                            bufferedImage = cacheStore.get(path);
                        } else {
                            String md5StampPath;
                            if (cacheStamps.containsKey(path)) {
                                LOG.log(Level.CONFIG, "method=by stampedOn=\"fingerprint for path '' {0} '' taken from cache\"", path);
                                md5StampPath = cacheStamps.get(path);
                            } else {
                                md5StampPath = stamp(filePath);
                                LOG.log(Level.CONFIG, "method=by stampedOn=\"fingerprint '' {1} '' for path '' {0} '' created\"", new Object[]{path, md5StampPath});
                                cacheStamps.put(path, md5StampPath);
                            }
                            File stampFile = getStampTempFile(md5StampPath);
                            if (stampFile.exists()) {
                                LOG.log(Level.CONFIG, "method=by stampedOn=\"fingerprint file exists ''{0}''\"", stampFile);
                                bufferedImage = from(stampFile);
                            } else {
                                BufferedImage bufferedImageOriginal = from(fileCheckOriginalExist);
                                bufferedImage = thumbnail(bufferedImageOriginal);
                                ImageIO.write(bufferedImage, "jpg", stampFile);
                                LOG.log(Level.CONFIG, "method=by stampedOn=\"fingerprint file '' {1} '' created for ''{0}''\"", new Object[]{path, stampFile});
                                cacheStore.put(path, bufferedImage);
                            }
                        }
                    }
                } catch (Exception e) {
                    try {
                        bufferedImage = defaultImage();
                    } catch (IOException e1) {
                        //never mind
                    }
                }
            }
        }
        return bufferedImage;
    }

    private BufferedImage defaultImage() throws IOException {
        if (defaultImage == null) {
            defaultImage = thumbnail(from(PATH_TO_DEFAULT_IMAGE));
            return defaultImage;
        }

        return defaultImage;
    }

    private BufferedImage notFoundImage() throws IOException {
        if (notFoundImage == null) {
            notFoundImage = thumbnail(from(PATH_TO_NOT_FOUND_IMAGE));
            return notFoundImage;
        }

        return notFoundImage;
    }

    private BufferedImage from(File file) throws IOException {
        return ImageIO.read(file);
    }

    private BufferedImage from(String fileInResource) throws IOException {
        return ImageIO.read(TempDirectoryCacheThumbnailRepository.class.getResourceAsStream(fileInResource));
    }

    private BufferedImage thumbnail(BufferedImage originalImage) throws IOException {
        return Thumbnails.of(originalImage)
                .size(100, 100)
                .asBufferedImage();
    }

    private File getStampTempFile(String stamp) {
        return new File(PATH_TO_STORE, stamp);
    }

    private String stamp(FilePath filePath) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(filePath.path())));
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toLowerCase();
    }

}
