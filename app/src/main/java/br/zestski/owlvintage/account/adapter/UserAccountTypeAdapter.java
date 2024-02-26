package br.zestski.owlvintage.account.adapter;

import androidx.annotation.NonNull;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Objects;

import br.zestski.owlvintage.account.UserAccountImpl;
import br.zestski.owlvintage.account.interfaces.UserAccount;

public class UserAccountTypeAdapter extends TypeAdapter<UserAccount> {

    @Override
    public void write(
            @NonNull JsonWriter out,
            @NonNull UserAccount value
    ) {
        throw new UnsupportedOperationException("Writing UserAccount is not supported");
    }

    @Override
    public UserAccount read(
            @NonNull JsonReader in
    ) throws IOException {
        String username = null;
        String email = null;
        String password = null;

        in.beginObject();

        while (in.hasNext()) {
            var name = in.nextName();
            switch (name) {
                case "username" -> username = in.nextString();
                case "email" -> email = in.nextString();
                case "password" -> password = in.nextString();
                default -> in.skipValue();
            }
        }

        in.endObject();

        return new UserAccountImpl(
                Objects.requireNonNull(username),
                Objects.requireNonNull(email),
                Objects.requireNonNull(password)
        );
    }
}