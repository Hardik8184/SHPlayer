/*
 * This is the source code of SHPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @ RiSysNetworks 2016.
 */

package in.risysnetworks.shplayer.phonemedia;

import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.AlphabetIndexer;

/**
 * Handles comparisons in a different way because the Album, Song and Artist
 * name are stripped of some prefixes such as "a", "an", "the" and some symbols.
 *
 */
public class MusicAlphabetIndexer extends AlphabetIndexer {

	public MusicAlphabetIndexer(Cursor cursor, int sortedColumnIndex, CharSequence alphabet) {
		super(cursor, sortedColumnIndex, alphabet);
	}

	@Override
	protected int compare(String word, String letter) {
		String wordKey = MediaStore.Audio.keyFor(word);
		String letterKey = MediaStore.Audio.keyFor(letter);
		if (wordKey.startsWith(letter)) {
			return 0;
		} else {
			return wordKey.compareTo(letterKey);
		}
	}
}
