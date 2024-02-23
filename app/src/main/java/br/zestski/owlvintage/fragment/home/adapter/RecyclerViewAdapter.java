package br.zestski.owlvintage.fragment.home.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import br.zestski.owlvintage.R;
import br.zestski.owlvintage.models.embed.EmbedType;
import br.zestski.owlvintage.models.status.StatusModel;

/**
 * Adapter for RecyclerView.
 *
 * @author Zestski
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.FriendsViewHolder> {

    private final List<StatusModel> statusModelList;

    public RecyclerViewAdapter(
            @NonNull List<StatusModel> statusModelList
    ) {
        this.statusModelList = statusModelList;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        var view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friends_timeline, parent, false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull FriendsViewHolder holder,
            int position
    ) {
        StatusModel statusModel = statusModelList.get(position);
        holder.bind(statusModel);
    }

    @Override
    public int getItemCount() {
        return statusModelList.size();
    }

    public void updateStatusList(
            @NonNull List<StatusModel> newStatusModelList
    ) {
        var startPosition = statusModelList.size();
        statusModelList.addAll(newStatusModelList);
        notifyItemRangeInserted(startPosition, newStatusModelList.size());
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        private final ShapeableImageView profileImageView;
        private final AppCompatTextView userTextView, userNameTextView, userMessageTextView, userTimeTextView;
        private final ShapeableImageView userEmbedImageView;

        public FriendsViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.profile_picture_friend);
            userTextView = itemView.findViewById(R.id.friend_user_name);
            userNameTextView = itemView.findViewById(R.id.friend_user_screen_name);
            userMessageTextView = itemView.findViewById(R.id.friend_user_message_text);
            userTimeTextView = itemView.findViewById(R.id.friend_user_datetime);
            userEmbedImageView = itemView.findViewById(R.id.friend_user_embed_image);
        }

        public void bind(
                @NonNull StatusModel statusModel
        ) {
            if (statusModel.getUser().getProfileImageUrl() != null && !statusModel.getUser().getProfileImageUrl().isEmpty()) {
                var profilePicture = new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource,
                            @Nullable Transition<? super Drawable> transition
                    ) {
                        profileImageView.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(
                            @Nullable Drawable placeholder
                    ) {
                    }
                };

                Glide.with(itemView.getContext())
                        .load(statusModel.getUser().getProfileImageUrl())
                        .placeholder(R.drawable.ic_profile_default)
                        .into(profilePicture);
            }

            if (statusModel.getEmbed() != null) {
                var type = statusModel.getEmbed().getEmbedType();

                if (type == EmbedType.IMAGE) {
                    var embedUrl = statusModel.getEmbed().getUrl();
                    if (embedUrl != null && !embedUrl.isEmpty()) {
                        var embedImage = new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(
                                    @NonNull Drawable resource,
                                    @Nullable Transition<? super Drawable> transition
                            ) {
                                userEmbedImageView.setImageDrawable(resource);
                                userEmbedImageView.setVisibility(View.GONE); /* [WARNING] DISABLED FEATURE DUE WIP, SET IT TO VISIBLE IF YOU WANT TO BE ABLE TO SEE IT */
                            }

                            @Override
                            public void onLoadCleared(
                                    @Nullable Drawable placeholder
                            ) {
                            }
                        };

                        var originalWidth = statusModel.getEmbed().getWidth();
                        var originalHeight = statusModel.getEmbed().getHeight();

                        int targetWidth, targetHeight;

                        if (originalWidth > originalHeight) {
                            targetWidth = 420;
                            targetHeight = (int) ((originalHeight / (float) originalWidth) * targetWidth);
                        } else {
                            targetHeight = 330;
                            targetWidth = (int) ((originalWidth / (float) originalHeight) * targetHeight);
                        }

                        Glide.with(itemView.getContext())
                                .load(embedUrl)
                                .optionalFitCenter()
                                .override(targetWidth, targetHeight)
                                .into(embedImage);

                    } else {
                        userEmbedImageView.setVisibility(View.GONE);
                    }
                } else {
                    userEmbedImageView.setVisibility(View.GONE);
                }
            } else {
                userEmbedImageView.setVisibility(View.GONE);
            }

            userTextView.setText(statusModel.getUser().getName());
            userNameTextView.setText(String.format("@%s", statusModel.getUser().getScreenName()));
            userMessageTextView.setText(statusModel.getText());
            userTimeTextView.setText(statusModel.getCreatedAt());
        }
    }

    public static class ScrollAwareFloatingActionButtonBehavior extends RecyclerView.OnScrollListener {

        private final FloatingActionButton fab;

        public ScrollAwareFloatingActionButtonBehavior(
                @NonNull FloatingActionButton fab
        ) {
            this.fab = fab;
        }

        @Override
        public void onScrolled(
                @NonNull RecyclerView recyclerView,
                int dx,
                int dy
        ) {
            super.onScrolled(recyclerView, dx, dy);

            if (dy > 0 && fab.isShown()) {
                fab.hide();
            } else if (dy < 0 && !fab.isShown()) {
                fab.show();
            }
        }

        @Override
        public void onScrollStateChanged(
                @NonNull RecyclerView recyclerView,
                int newState
        ) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    }
}