package ru.ezhov.duplicate.files.gui.infrastructure.repository;

import net.coobird.thumbnailator.Thumbnails;
import ru.ezhov.duplicate.files.gui.application.repository.ThumbnailsRepository;
import ru.ezhov.duplicate.files.stamp.analyzer.model.domain.FilePath;
import ru.ezhov.duplicate.files.stamp.generator.model.service.StampGeneratorException;

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
    private Map<String, BufferedImage> cacheStore = new HashMap<>();
    private Map<String, String> cacheStamps = new HashMap<>();
    private Set<String> errors = new HashSet<>();

    private BufferedImage defaultImage = null;

    public TempDirectoryCacheThumbnailRepository() {
        init();
    }

    private void init() {
        File file = new File(PATH_TO_STORE);
        file.mkdirs();
        if (file.exists()) {
            LOG.log(Level.CONFIG, "method=init action=\"создано файловое хранилище для миниатюр ''{0}''\"", file);
        }
    }

    @Override
    public synchronized BufferedImage by(FilePath filePath) {
        BufferedImage bufferedImage = null;
        String path = filePath.path();
        if (errors.contains(path)) {
            LOG.log(Level.CONFIG, "method=by action=\"ошибки для файла ''{0}''\"", path);
            try {
                bufferedImage = defaultImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                File fileCheckOriginalExist = new File(path);
                if (!fileCheckOriginalExist.exists()) {
                    LOG.log(Level.CONFIG, "method=by action=\"ошибка создания миниатюры для ''{0}''\"", path);
                    errors.add(path);
                    bufferedImage = defaultImage();
                } else {

                    if (cacheStore.containsKey(path)) {
                        LOG.log(Level.CONFIG, "method=by action=\"изображение для ''{0}'' взято из кеша\"", path);
                        bufferedImage = cacheStore.get(path);
                    } else {

                        String md5StampPath;
                        if (cacheStamps.containsKey(path)) {
                            LOG.log(Level.CONFIG, "method=by action=\"отпечаток для пути ''{0}'' взят из кеша\"", path);
                            md5StampPath = cacheStamps.get(path);
                        } else {
                            md5StampPath = stamp(filePath);
                            LOG.log(Level.CONFIG, "method=by action=\"отпечаток ''{1}'' для пути ''{0}'' создан\"", new Object[]{path, md5StampPath});
                            cacheStamps.put(path, md5StampPath);
                        }
                        File stampFile = getStampTempFile(md5StampPath);
                        if (stampFile.exists()) {
                            LOG.log(Level.CONFIG, "method=by action=\"файл отпечатка существует ''{0}''\"", stampFile);
                            bufferedImage = from(stampFile);
                        } else {
                            File file = new File(path);
                            if (file.exists()) {
                                BufferedImage bufferedImageOriginal = from(file);
                                bufferedImage = thumbnail(bufferedImageOriginal);
                                ImageIO.write(bufferedImage, "jpg", stampFile);
                                LOG.log(Level.CONFIG, "method=by action=\"файл отпечатка ''{1}'' создан для ''{0}''\"", new Object[]{path, stampFile});
                                cacheStore.put(path, bufferedImage);
                            } else {
                                LOG.log(Level.CONFIG, "method=by action=\"ошибка создания миниатюры для ''{0}''\"", path);
                                errors.add(path);
                                bufferedImage = defaultImage();
                            }
                        }

                    }
                }
            } catch (Exception e) {
                try {
                    bufferedImage = defaultImage();
                } catch (IOException e1) {
                    //пофигу, что не смогли уменьшить
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