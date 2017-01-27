package ir.hphamid.instagram.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.hphamid.instagram.R;

/**
 * Created on 1/26/17 at 6:14 PM.
 * Project: instagram
 *
 * @author hamid
 */

public class TestImageListFragment extends Fragment {

    class ImageItemViewHolder extends RecyclerView.ViewHolder{

        public TextView textview;

        public ImageItemViewHolder(View itemView) {
            super(itemView);
            textview = (TextView) itemView.findViewById(R.id.test_text_view);
        }
    }

    class ImageItemViewHolder2 extends RecyclerView.ViewHolder{

        public TextView textview;

        public ImageItemViewHolder2(View itemView) {
            super(itemView);
            textview = (TextView) itemView.findViewById(R.id.test_text_view2);
        }
    }

    class ImageItemAdapater extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<String> items;


        ImageItemAdapater(){
            items = new ArrayList<>();
            for(int i = 0; i < 500; i++){
                items.add("item no " + i);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == 1){
                View toReturn = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_layout, parent, false);
                return new ImageItemViewHolder(toReturn);
            }else{
                View toReturn = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_layout2, parent, false);
                return new ImageItemViewHolder2(toReturn);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder.getItemViewType() == 1){
                ((ImageItemViewHolder)holder).textview.setText(items.get(position));
            }else{
                ((ImageItemViewHolder2)holder).textview.setText(items.get(position));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position % 2 == 0){
                return 2;
            }else{
                return 1;
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View toReturn = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_image_list, container, false);
        RecyclerView recyclerView = (RecyclerView) toReturn.findViewById(R.id.image_list_recycler_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new ImageItemAdapater());
        return toReturn;
    }
}
