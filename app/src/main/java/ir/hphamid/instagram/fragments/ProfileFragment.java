package ir.hphamid.instagram.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ir.hphamid.instagram.R;
import ir.hphamid.instagram.data.SharedImage;
import ir.hphamid.instagram.data.User;

/**
 * Created on 1/24/17 at 2:49 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class ProfileFragment extends Fragment {
    public final static String User_Name = "user";
    class ImageItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private SharedImage data;

        public ImageItemViewHolder(View itemView) {
            super(itemView);
            findViewItems();
            setListeners();
        }

        private void findViewItems(){
            image = (ImageView) itemView.findViewById(R.id.view_item_profile_shared_image);
        }

        private void setContent(SharedImage data){
            this.data = data;
            Glide.with(ProfileFragment.this).load(data.getImageUri())
                    .placeholder(R.drawable.image_place_holder).into(image);
        }
        private void setListeners(){
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
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePic;
        private TextView profileName;
        private Button followButton;
        private User data;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            findViewItems();
        }

        private void findViewItems(){
            profilePic = (ImageView) itemView.findViewById(R.id.profile_profile_image);
            profileName = (TextView) itemView.findViewById(R.id.profile_profile_name);
            followButton = (Button) itemView.findViewById(R.id.profile_follow);
        }

        private void setContent(User data){
            this.data = data;
            Glide.with(ProfileFragment.this).load(data.getImageUri())
                    .placeholder(R.drawable.profile_place_holder).into(profilePic);
            profileName.setText(data.getFullName());
        }
    }

    class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<SharedImage> items;
        User user;

        public ProfileAdapter(User user) {
            this.user = user;
            items = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                SharedImage toAdd = new SharedImage();
                toAdd.setImageUri("file:///android_asset/" + getImageName(i));
                toAdd.setDescription("item no " + i);
                toAdd.setUser(user);
                items.add(toAdd);
            }
        }

        private String getImageName(int i){
            return "a" + (i % 30 + 1) + ".jpg";
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == 0){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_item_profile_info, parent, false);
                return new ProfileViewHolder(view);
            }else{
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_item_profile_shared_image, parent, false);
                return new ImageItemViewHolder(view);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0){
                return 0;
            }else{
                return 1;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder.getItemViewType() == 0){
                ((ProfileViewHolder)holder).setContent(user);
            }else{
                ((ImageItemViewHolder)holder).setContent(items.get(position - 1));
            }
        }

        @Override
        public int getItemCount() {
            return items.size() + 1;
        }
    }

    public GridLayoutManager layoutManager;
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View toReturn = inflater.inflate(R.layout.fragment_profile, container, false);
        if(getArguments() != null && getArguments().getSerializable(User_Name) != null &&
                getArguments().getSerializable(User_Name) instanceof User){
            user = (User) getArguments().getSerializable(User_Name);
        }
        this.recyclerView = (RecyclerView) toReturn.findViewById(R.id.profile_recycler_list_view);
        this.layoutManager = new GridLayoutManager(this.getContext(), 2);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position == 0){
                    return 2;
                }
                return 1;
            }
        });
        this.recyclerView.setLayoutManager(layoutManager);
        adapter = new ProfileAdapter(user);
        this.recyclerView.setAdapter(adapter);
        return toReturn;
    }
}
