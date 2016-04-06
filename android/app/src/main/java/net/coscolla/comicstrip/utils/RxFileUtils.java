package net.coscolla.comicstrip.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import timber.log.Timber;

import static rx.Observable.just;

/**
 * Simple utils class to write data and cache them to disk
 */
public class RxFileUtils {

  private final File root;

  public RxFileUtils(File root_path) {
    this.root = root_path;
  }

  /**
   * Returns an observable with the bytes[] of the file or an empty observable if the file doesn't
   * exist or some error occur.
   *
   * @param file file inside the root parameter
   * @return observable with the data inside the file
   */
  public Observable<byte[]> readFile(String file) {
    File f = getFile(file);

    return Observable.defer(() ->
        just(1)
        .flatMap(v -> {
          try {
            return just(FileUtils.readFileToByteArray(f));
          } catch (IOException e) {
            Timber.e(e, "could not read file %s", f.getAbsolutePath());
            return Observable.empty();
          }
        }));
  }

  /**
   * Writes the data bytes to a file inside the filesystem
   *
   * @param file filename to create
   * @param data bytes to store
   * @return An observable that emits true when the data is written
   */
  public Observable<Boolean> writeFile(String file, byte[] data) {
    File f = getFile(file);

    return Observable.defer(() -> Observable.fromCallable(() -> {
      FileUtils.writeByteArrayToFile(f, data);
      return true;
    }));
  }

  /**
   * Gets the file where to seek the cache
   *
   * @param file filename of the cache file
   */
  public File getFile(String file) {
    return FileUtils.getFile(root, file);
  }
}
