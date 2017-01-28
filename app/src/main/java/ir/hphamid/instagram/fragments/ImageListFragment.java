package ir.hphamid.instagram.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ir.hphamid.instagram.HttpAddresses;
import ir.hphamid.instagram.HttpHelper;
import ir.hphamid.instagram.LoginHelper;
import ir.hphamid.instagram.R;
import ir.hphamid.instagram.activities.HomeActivity;
import ir.hphamid.instagram.activities.LoginActivity;
import ir.hphamid.instagram.activities.SignUpActivity;
import ir.hphamid.instagram.data.SharedImage;
import ir.hphamid.instagram.data.User;
import ir.hphamid.instagram.list.EndlessAdapter;
import ir.hphamid.instagram.reqAndres.ImageListResponse;
import ir.hphamid.instagram.reqAndres.LoginResponse;
import ir.hphamid.instagram.reqAndres.PaginationRequest;
import ir.hphamid.instagram.reqAndres.SignupRequest;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        protected void onDataReset() {
            items.clear();
        }

        @Override
        protected void loadMore(int position, final LoadMoreCallback callback) {
            PaginationRequest requestJson = new PaginationRequest();
            requestJson.setLimit(20);
            requestJson.setSkip(position);
            RequestBody body = RequestBody.create(HttpHelper.JSON, new Gson().toJson(requestJson));
            Request request = new Request.Builder()
                    .addHeader("Authorization", HttpHelper.getInstance().getLoginHeader(getContext()))
                    .post(body)
                    .url(HttpAddresses.AllImages).build();
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
                    Log.e("responce", responseString);
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

    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView recyclerView;
    public EndlessImageListAdapter adapter;

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

    @Override
    public void onStart() {
        super.onStart();
        adapter.reset();
    }
}
