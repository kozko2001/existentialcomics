package net.coscolla.comicstrip.usecases;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.coscolla.comicstrip.entities.Strip;

import rx.Observable;

public interface DetailStripUseCase {

  @NonNull
  Observable<byte[]> getImage(@NonNull  Strip strip);

  @NonNull
  Observable<Strip> getStripById(@NonNull  String id);

  @Nullable
  String getShareImagePath(@NonNull Strip strip);
}
