package ir.hphamid.instagram.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ir.hphamid.instagram.R;
import ir.hphamid.instagram.data.SharedImage;

/**
 * Created on 1/25/17 at 2:11 AM.
 * Project: instagram
 *
 * @author hamid
 */

public class ImageFragment extends Fragment{
    public final static String DATA_NAME = "data";

    private TextView description;
    private ImageView profilePic;
    private ImageView image;
    private TextView profileName;
    private SharedImage data;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments() != null && getArguments().getSerializable(DATA_NAME) != null &&
                getArguments().getSerializable(DATA_NAME) instanceof SharedImage){
            data = (SharedImage) getArguments().getSerializable(DATA_NAME);
        }
        View itemView = inflater.inflate(R.layout.fragment_image, container, false);
        description = (TextView) itemView.findViewById(R.id.image_view_item_description);
        profilePic = (ImageView) itemView.findViewById(R.id.image_view_item_profile_image);
        image = (ImageView) itemView.findViewById(R.id.image_view_item_image);
        profileName = (TextView) itemView.findViewById(R.id.image_view_item_profile_name);
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
        updateData();
        return itemView;
    }

    public void updateData(){
        if(data == null){
            return;
        }
        description.setText(data.getDescription());
        Glide.with(this).load(data.getImageUri())
                .placeholder(R.drawable.image_place_holder).into(image);
        if(data.getUser() != null){
            profileName.setText(data.getUser().getFullName());
            Glide.with(this).load(data.getUser().getImageUri())
                    .placeholder(R.drawable.profile_place_holder).into(profilePic);
        }
    }
}
