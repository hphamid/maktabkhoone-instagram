package ir.hphamid.instagram.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ir.hphamid.instagram.R;
import ir.hphamid.instagram.data.SharedImage;
import ir.hphamid.instagram.data.User;
import ir.hphamid.instagram.list.EndlessAdapter;

/**
 * Created on 1/24/17 at 2:48 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class ImageListFragment extends Fragment {
    class ImageItemViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private ImageView profilePic;
        private ImageView image;
        private TextView profileName;
        private SharedImage data;

        public ImageItemViewHolder(View itemView) {
            super(itemView);
            findViewItems();
            setListeners();
        }

        private void findViewItems() {
            description = (TextView) itemView.findViewById(R.id.image_view_item_description);
            profilePic = (ImageView) itemView.findViewById(R.id.image_view_item_profile_image);
            image = (ImageView) itemView.findViewById(R.id.image_view_item_image);
            profileName = (TextView) itemView.findViewById(R.id.image_view_item_profile_name);
        }

        private void setListeners() {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction tranaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new ProfileFragment();
                    Bundle extra = new Bundle();
                    extra.putSerializable(ProfileFragment.User_Name, data.getUser());
                    fragment.setArguments(extra);
                    tranaction.replace(R.id.home_fragment_container, fragment);
                    tranaction.addToBackStack("profile");
                    tranaction.commit();
                }
            };
            profileName.setOnClickListener(listener);
            profilePic.setOnClickListener(listener);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction tranaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new ImageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ImageFragment.DATA_NAME, data);
                    fragment.setArguments(bundle);
                    tranaction.replace(R.id.home_fragment_container, fragment);
                    tranaction.addToBackStack("image");
                    tranaction.commit();
                }
            });
        }

        private void setContent(SharedImage data) {
            this.data = data;
            description.setText(data.getDescription());
            Glide.with(ImageListFragment.this).load(data.getImageUri())
                    .placeholder(R.drawable.image_place_holder).into(image);
            if (data.getUser() != null) {
                profileName.setText(data.getUser().getFullName());
                Glide.with(ImageListFragment.this).load(data.getUser().getImageUri())
                        .placeholder(R.drawable.profile_place_holder).into(profilePic);
            }
        }

    }

    class SimpleImageListAdapter extends RecyclerView.Adapter<ImageItemViewHolder> {
        List<SharedImage> items;

        public SimpleImageListAdapter() {
            items = new ArrayList<>();
            User user = new User();
            user.setFullName("Hamid Pourrabi");
            for (int i = 0; i < 500; i++) {
                SharedImage toAdd = new SharedImage();
                toAdd.setImageUri("file:///android_asset/" + getImageName(i));
                toAdd.setDescription("item no " + i);
                toAdd.setUser(user);
                items.add(toAdd);
            }
        }


        private String getImageName(int i) {
            return "a" + (i % 30 + 1) + ".jpg";
        }

        @Override
        public ImageItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_item_image, parent, false);
            return new ImageItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageItemViewHolder holder, int position) {
            holder.setContent(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    class EndlessImageListAdapter extends EndlessAdapter<ImageItemViewHolder>{
        List<SharedImage> items = new ArrayList<>();

        @Override
        public int getDataItemsCount() {
            return items.size();
        }

        @Override
        public ImageItemViewHolder onDataCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_item_image, parent, false);
            return new ImageItemViewHolder(view);        }

        @Override
        public void onDataBindViewHolder(ImageItemViewHolder holder, int position) {
            holder.setContent(items.get(position));
        }

        @Override
        protected int getLoadingViewType() {
            return R.layout.view_item_loading;
        }

        @Override
        protected void loadMore(int position, LoadMoreCallback callback) {
            Log.d("loadmore", position + "");
            User user = new User();
            user.setFullName("Hamid Pourrabi");
            for (int i = 0; i < 10; i++) {
                SharedImage toAdd = new SharedImage();
                toAdd.setImageUri("file:///android_asset/" + getImageName(i));
                toAdd.setDescription("item no " + (position + i + 1));
                toAdd.setUser(user);
                items.add(toAdd);
            }
            callback.done(10);
        }

        private String getImageName(int i) {
            return "a" + (i % 30 + 1) + ".jpg";
        }
    }

    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View toReturn = inflater.inflate(R.layout.fragment_image_list, container, false);
        this.recyclerView = (RecyclerView) toReturn.findViewById(R.id.image_list_recycler_list_view);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new EndlessImageListAdapter();
        this.recyclerView.setAdapter(adapter);
        return toReturn;
    }
}
