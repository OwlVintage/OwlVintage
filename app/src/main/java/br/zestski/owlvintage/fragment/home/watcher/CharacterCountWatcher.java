package br.zestski.owlvintage.fragment.home.watcher;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

/**
 * A TextWatcher implementation to monitor character count in a TextInputLayout.
 *
 * @author Zestski
 */
public class CharacterCountWatcher implements TextWatcher {

    private final MaterialButton button;
    private final TextInputLayout tilStatus;

    private final int minCharacterCount, maxCharacterCount;

    /**
     * Constructs a CharacterCountWatcher with the specified TextInputLayout.
     *
     * @param button            The button to enable/disable based on character count.
     * @param tilStatus         The TextInputLayout containing the text to monitor.
     * @param minCharacterCount The minimum character count allowed.
     * @param maxCharacterCount The maximum character count allowed.
     */
    public CharacterCountWatcher(
            @NonNull MaterialButton button,
            @NonNull TextInputLayout tilStatus,
            int minCharacterCount,
            int maxCharacterCount
    ) {
        this.button = button;
        this.tilStatus = tilStatus;
        this.minCharacterCount = minCharacterCount;
        this.maxCharacterCount = maxCharacterCount;
    }

    @Override
    public void beforeTextChanged(
            @NonNull CharSequence s,
            int start,
            int count,
            int after
    ) {
    }

    @Override
    public void onTextChanged(
            @NonNull CharSequence s,
            int start,
            int before,
            int count
    ) {
    }

    @Override
    public void afterTextChanged(
            @NonNull Editable s
    ) {
        final var status = Objects.requireNonNull(tilStatus.getEditText()).getText().toString().trim();

        button.setEnabled(status.length() >= minCharacterCount && status.length() <= maxCharacterCount);
    }
}