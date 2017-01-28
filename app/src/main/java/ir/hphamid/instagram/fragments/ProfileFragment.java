package ir.hphamid.instagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ir.hphamid.instagram.HttpAddresses;
import ir.hphamid.instagram.HttpHelper;
import ir.hphamid.instagram.R;
import ir.hphamid.instagram.activities.LoginActivity;
import ir.hphamid.instagram.data.SharedImage;
import ir.hphamid.instagram.data.User;
import ir.hphamid.instagram.list.EndlessAdapter;
import ir.hphamid.instagram.reqAndres.ImageListResponse;
import ir.hphamid.instagram.reqAndres.ProfileImagesRequest;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
                    .placeholder(R.drawable.image_place_holder).centerCrop().into(image);
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

    class EndlessProfileImageListAdapter extends EndlessAdapter<RecyclerView.ViewHolder> {
        List<SharedImage> items = new ArrayList<>();
        User user;

        EndlessProfileImageListAdapter(User user){
            this.user = user;
        }

        @Override
        public int getDataItemsCount() {
            return items.size();
        }

        @Override
        public RecyclerView.ViewHolder onDataCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == 0){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_item_profile_info, parent, false);
                return new ProfileViewHolder(view);
            }else{
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_item_profile_shared_image, parent, false);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = parent.getWidth() / 2;
                Log.e("test", params.width + "  " + params.height);
                return new ImageItemViewHolder(view);
            }
        }

        @Override
        public void onDataBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder.getItemViewType() == 0){
                ((ProfileViewHolder)holder).setContent(user);
            }else{
                ((ImageItemViewHolder)holder).setContent(items.get(position - 1));
            }
        }


        @Override
        public int getDataItemViewType(int position) {
            if(position == 0){
                return 0;
            }else{
                return 1;
            }
        }

        @Override
        protected int getLoadingViewType() {
            return R.layout.view_item_loading;
        }

        @Override
        protected void onDataReset() {
            items.clear();
        }

        @Override
        protected void loadMore(int position, final LoadMoreCallback callback) {
            ProfileImagesRequest requestJson = new ProfileImagesRequest();
            requestJson.setLimit(20);
            requestJson.setSkip(position - 1);
            requestJson.setId(user.getUserId());
            RequestBody body = RequestBody.create(HttpHelper.JSON, new Gson().toJson(requestJson));
            Request request = new Request.Builder()
                    .addHeader("Authorization", HttpHelper.getInstance().getLoginHeader(getContext()))
                    .post(body)
                    .url(HttpAddresses.ProfileImages).build();
            HttpHelper.getInstance().getClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Unknown Problem", Toast.LENGTH_SHORT).show();
                            callback.done(0);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseString = response.body().string();
                    ImageListResponse res = new Gson().fromJson(responseString, ImageListResponse.class);
                    if(res.isSuccess()){
                        if(res.getImages() != null){
                            for(SharedImage image: res.getImages()){
                                items.add(image);
                            }
                            callback.done(res.getImages().size());
                        }else{
                            callback.done(0);
                        }
                    }else if(res.getCode() == 401){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Login Required", Toast.LENGTH_LONG).show();
                                Intent loginActivityIntent = new Intent(getActivity(), LoginActivity.class);
                                getActivity().finish();
                                getActivity().startActivity(loginActivityIntent);
                            }
                        });
                        callback.done(0);
                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Unknown Problem", Toast.LENGTH_SHORT).show();
                            }
                        });
                        callback.done(0);
                    }
                }
            });

        }
    }

    public GridLayoutManager layoutManager;
    public RecyclerView recyclerView;
    public EndlessProfileImageListAdapter adapter;
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
        adapter = new EndlessProfileImageListAdapter(user);
        this.recyclerView.setAdapter(adapter);
        return toReturn;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.reset();
    }
}
